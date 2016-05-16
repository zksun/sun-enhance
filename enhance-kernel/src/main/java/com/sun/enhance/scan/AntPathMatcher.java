package com.sun.enhance.scan;

import com.sun.enhance.util.Assert;
import com.sun.enhance.util.StringUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by zksun on 5/16/16.
 */
public class AntPathMatcher implements PathMatcher {

    public static final String DEFAULT_PATH_SEPARATOR = "/";
    private static final Pattern VARIABLE_PATTERN = Pattern.compile("\\{[^/]+?\\}");
    private String pathSeparator = DEFAULT_PATH_SEPARATOR;
    private boolean trimTokens = true;

    private final Map<String, AntPathStringMatcher> stringMatcherCache =
            new ConcurrentHashMap<String, AntPathStringMatcher>(256);

    public void setPathSeparator(String pathSeparator) {
        this.pathSeparator = (null != pathSeparator && !pathSeparator.equals("")) ? pathSeparator : DEFAULT_PATH_SEPARATOR;
    }

    public void setTrimTokens(boolean trimTokens) {
        this.trimTokens = trimTokens;
    }

    @Override
    public boolean isPattern(String path) {
        return path.indexOf('*') != -1 || path.indexOf('?') != -1;
    }

    @Override
    public boolean match(String pattern, String path) {
        return doMatch(pattern, path, true, null);
    }

    @Override
    public boolean matchStart(String pattern, String path) {
        return doMatch(pattern, path, false, null);
    }

    @Override
    public String extractPathWithinPattern(String pattern, String path) {
        String[] patternParts = StringUtils.tokenizeToStringArray(pattern, this.pathSeparator, this.trimTokens, true);
        String[] pathParts = StringUtils.tokenizeToStringArray(path, this.pathSeparator, this.trimTokens, true);

        StringBuilder builder = new StringBuilder();

        int puts = 0;
        for (int i = 0; i < patternParts.length; i++) {
            String patternPart = patternParts[i];
            if (patternPart.indexOf('*') > -1 || patternPart.indexOf('?') > -1 && pathParts.length >= i + 1) {
                if (puts > 0 || (i == 0 && !pattern.startsWith(this.pathSeparator))) {
                    builder.append(this.pathSeparator);
                }
                builder.append(pathParts[i]);
                puts++;
            }
        }

        for (int i = patternParts.length; i < pathParts.length; i++) {
            if (puts > 0 || i > 0) {
                builder.append(this.pathSeparator);
            }
            builder.append(pathParts[i]);
        }

        return builder.toString();
    }

    @Override
    public Map<String, String> extractUriTemplateVariables(String pattern, String path) {
        Map<String, String> variables = new LinkedHashMap<String, String>();
        boolean result = doMatch(pattern, path, true, variables);
        Assert.state(result, "Pattern \"" + pattern + "\" is not a match for \"" + path + "\"");
        return variables;
    }

    @Override
    public Comparator<String> getPatternComparator(String path) {
        return new AntPatternComparator(path);
    }

    @Override
    public String combine(String pattern1, String pattern2) {
        if (!StringUtils.hasText(pattern1) && !StringUtils.hasText(pattern2)) {
            return "";
        } else if (!StringUtils.hasText(pattern1)) {
            return pattern2;
        } else if (!StringUtils.hasText(pattern2)) {
            return pattern1;
        }

        boolean pattern1ContainsUriVar = pattern1.indexOf('{') != -1;
        if (!pattern1.equals(pattern2) && !pattern1ContainsUriVar && match(pattern1, pattern2)) {
            return pattern2;
        } else if (pattern1.endsWith("/*")) {
            if (pattern2.startsWith("/")) {
                return pattern1.substring(0, pattern1.length() - 1) + pattern2.substring(1);
            } else {
                return pattern1.substring(0, pattern1.length() - 1) + pattern2;
            }
        } else if (pattern1.endsWith("/**")) {
            if (pattern2.startsWith("/")) {
                return pattern1 + pattern2;
            } else {
                return pattern1 + "/" + pattern2;
            }
        } else {
            int dotPos1 = pattern1.indexOf('.');
            if (dotPos1 == -1 || pattern1ContainsUriVar) {
                if (pattern1.endsWith("/") || pattern2.startsWith("/")) {
                    return pattern1 + pattern2;
                } else {
                    return pattern1 + "/" + pattern2;
                }
            }

            String fileName1 = pattern1.substring(0, dotPos1);
            String extension1 = pattern1.substring(dotPos1);
            String fileName2;
            String extension2;
            int dotPos2 = pattern2.indexOf('.');
            if (dotPos2 != -1) {
                fileName2 = pattern2.substring(0, dotPos2);
                extension2 = pattern2.substring(dotPos2);
            } else {
                fileName2 = pattern2;
                extension2 = "";
            }
            String fileName = fileName1.endsWith("*") ? fileName2 : fileName1;
            String extension = extension1.startsWith("*") ? extension2 : extension1;

            return fileName + extension;
        }
    }

    protected boolean doMatch(String pattern, String path, boolean fullMatch, Map<String, String> uriTemplateVariables) {
        if (path.startsWith(this.pathSeparator) != pattern.startsWith(this.pathSeparator)) {
            return false;
        }

        String[] pattDirs = tokenizePath(pattern);
        String[] pathDirs = tokenizePath(path);

        int pattIndexStart = 0;
        int pattIndexEnd = pattDirs.length - 1;

        int pathIndexStart = 0;
        int pathIndexEnd = pathDirs.length - 1;

        while (pattIndexStart <= pattIndexEnd && pathIndexStart <= pathIndexEnd) {
            String pattDir = pattDirs[pathIndexStart];
            if ("**".equals(pattDir)) {
                break;
            }
            if (!matchStrings(pattDir, pathDirs[pathIndexStart], uriTemplateVariables)) {
                return false;
            }
            pattIndexStart++;
            pathIndexStart++;
        }

        if (pathIndexStart > pathIndexEnd) {
            if (pattIndexStart > pattIndexEnd) {
                return pattern.endsWith(this.pathSeparator) ? path.endsWith(this.pathSeparator) : !path.endsWith(this.pathSeparator);
            }
            if (!fullMatch) {
                return true;
            }
            if (pattIndexStart == pattIndexEnd && pattDirs[pattIndexStart].equals('*') && path.endsWith(this.pathSeparator)) {
                return true;
            }

            for (int i = pattIndexStart; i <= pathIndexEnd; i++) {
                if (!pattDirs[i].equals("**")) {
                    return false;
                }
            }
            return true;
        } else if (pattIndexStart > pattIndexEnd) {
            return false;
        } else if (!fullMatch && "**".equals(pattDirs[pattIndexStart])) {
            return true;
        }

        while (pattIndexStart <= pattIndexEnd && pathIndexStart <= pathIndexEnd) {
            String pattDir = pattDirs[pattIndexEnd];
            if (pattDir.equals("**")) {
                break;
            }

            if (!matchStrings(pattDir, pathDirs[pathIndexEnd], uriTemplateVariables)) {
                return false;
            }

            pattIndexEnd--;
            pathIndexEnd--;
        }

        if (pathIndexStart > pathIndexEnd) {
            for (int i = pattIndexStart; i <= pattIndexEnd; i++) {
                if (!pattDirs[i].equals("**")) {
                    return false;
                }
            }
            return true;
        }

        while (pattIndexStart != pattIndexEnd && pathIndexStart != pathIndexEnd) {
            int pathIndexTmp = -1;
            for (int i = pattIndexStart + 1; i <= pattIndexEnd; i++) {
                if (pattDirs[i].equals("**")) {
                    pathIndexTmp = i;
                    break;
                }
            }
            if (pathIndexTmp == pattIndexStart + 1) {
                pathIndexStart++;
                continue;
            }

            int patLength = (pathIndexTmp - pathIndexStart - 1);
            int strLength = (pathIndexEnd - pathIndexStart + 1);
            int foundIndex = -1;

            strLoop:
            for (int i = 0; i <= strLength - patLength; i++) {
                for (int j = 0; j < patLength; j++) {
                    String subPat = pattDirs[pattIndexStart + j + 1];
                    String subStr = pathDirs[pattIndexStart + i + j];
                    if (!matchStrings(subPat, subStr, uriTemplateVariables)) {
                        continue strLoop;
                    }
                }
                foundIndex = pathIndexStart + i;
                break;
            }

            if (foundIndex == -1) {
                return false;
            }

            pattIndexStart = pathIndexTmp;
            pathIndexStart = foundIndex + patLength;
        }

        for (int i = pattIndexStart; i < pattIndexEnd; i++) {
            if (!pattDirs[i].equals("**")) {
                return false;
            }
        }

        return true;
    }

    protected String[] tokenizePath(String path) {
        return StringUtils.tokenizeToStringArray(path, this.pathSeparator, this.trimTokens, true);
    }

    private boolean matchStrings(String pattern, String str, Map<String, String> uriTemplateVariables) {
        AntPathStringMatcher matcher = this.stringMatcherCache.get(pattern);
        if (matcher == null) {
            matcher = new AntPathStringMatcher(pattern);
            this.stringMatcherCache.put(pattern, matcher);
        }
        return matcher.matchStrings(str, uriTemplateVariables);
    }

    private static class AntPatternComparator implements Comparator<String> {

        private final String path;

        private AntPatternComparator(String path) {
            this.path = path;
        }

        @Override
        public int compare(String pattern1, String pattern2) {
            if (null == pattern1 && null == pattern2) {
                return 0;
            } else if (null == pattern1) {
                return 1;
            } else if (null == pattern2) {
                return -1;
            }
            boolean pattern1EqualsPath = pattern1.equals(path);
            boolean pattern2EquealPath = pattern2.equals(path);
            if (pattern1EqualsPath && pattern2EquealPath) {
                return 0;
            } else if (pattern1EqualsPath) {
                return -1;
            } else if (pattern2EquealPath) {
                return 1;
            }
            int wildCardCount1 = getWildCardCount(pattern1);
            int wildCardCount2 = getWildCardCount(pattern2);

            int bracketCount1 = StringUtils.countOccurrencesOf(pattern1, "{");
            int bracketCount2 = StringUtils.countOccurrencesOf(pattern2, "{");

            int totalCount1 = wildCardCount1 + bracketCount1;
            int totalCount2 = wildCardCount2 + bracketCount2;

            if (totalCount1 != totalCount2) {
                return totalCount1 - totalCount2;
            }

            int pattern1Length = getPatternLength(pattern1);
            int pattern2Length = getPatternLength(pattern2);

            if (pattern1Length != pattern2Length) {
                return pattern2Length - pattern1Length;
            }

            if (wildCardCount1 < wildCardCount2) {
                return -1;
            } else if (wildCardCount2 < wildCardCount1) {
                return 1;
            }

            if (bracketCount1 < bracketCount2) {
                return -1;
            }

            return 0;
        }

        private int getWildCardCount(String pattern) {
            if (pattern.endsWith(".*")) {
                pattern = pattern.substring(0, pattern.length() - 2);
            }
            return StringUtils.countOccurrencesOf(pattern, "*");
        }

        private int getPatternLength(String pattern) {
            Matcher m = VARIABLE_PATTERN.matcher(pattern);
            return m.replaceAll("#").length();
        }


    }

    private static class AntPathStringMatcher {
        private static final Pattern GLOB_PATTERN = Pattern.compile("\\?|\\*|\\{((?:\\{[^/]+?\\}|[^/{}]|\\\\[{}])+?)\\}");

        private static final String DEFAULT_VARIABLE_PATTERN = "(.*)";

        private final Pattern pattern;

        private final List<String> variableNames = new LinkedList<String>();

        public AntPathStringMatcher(String pattern) {
            StringBuilder patternBuilder = new StringBuilder();
            Matcher m = GLOB_PATTERN.matcher(pattern);
            int end = 0;
            while (m.find()) {
                patternBuilder.append(quote(pattern, end, m.start()));
                String match = m.group();
                if ("?".equals(match)) {
                    patternBuilder.append('.');
                } else if ("*".equals(match)) {
                    patternBuilder.append(".*");
                } else if (match.startsWith("{") && match.endsWith("}")) {
                    int colonIdx = match.indexOf(':');
                    if (colonIdx == -1) {
                        patternBuilder.append(DEFAULT_VARIABLE_PATTERN);
                        this.variableNames.add(m.group(1));
                    } else {
                        String variablePattern = match.substring(colonIdx + 1, match.length() - 1);
                        patternBuilder.append('(');
                        patternBuilder.append(variablePattern);
                        patternBuilder.append(')');
                        String variableName = match.substring(1, colonIdx);
                        this.variableNames.add(variableName);
                    }
                }
                end = m.end();
            }
            patternBuilder.append(quote(pattern, end, pattern.length()));
            this.pattern = Pattern.compile(patternBuilder.toString());
        }


        private String quote(String s, int start, int end) {
            if (start == end) {
                return "";
            }
            return Pattern.quote(s.substring(start, end));
        }

        public boolean matchStrings(String str, Map<String, String> uriTemplateVariables) {
            Matcher matcher = this.pattern.matcher(str);
            if (matcher.matches()) {
                if (uriTemplateVariables != null) {
                    Assert.isTrue(this.variableNames.size() == matcher.groupCount(),
                            "The number of capturing groups in the pattern segment " + this.pattern +
                                    " does not match the number of URI template variables it defines, which can occur if " +
                                    " capturing groups are used in a URI template regex. Use non-capturing groups instead.");
                    for (int i = 1; i <= matcher.groupCount(); i++) {
                        String name = this.variableNames.get(i - 1);
                        String value = matcher.group(i);
                        uriTemplateVariables.put(name, value);
                    }
                }
                return true;
            } else {
                return false;
            }
        }

    }
}
