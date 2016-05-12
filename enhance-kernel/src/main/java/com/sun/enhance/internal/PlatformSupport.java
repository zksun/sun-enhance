package com.sun.enhance.internal;

import com.sun.enhance.logging.Logger;
import com.sun.enhance.logging.LoggerFactory;

import java.util.regex.Pattern;

/**
 * Created by zksun on 5/12/16.
 */
public final class PlatformSupport {

    private final static Logger logger = LoggerFactory.getLogger(PlatformSupport.class);

    private static final Pattern MAX_DIRECT_MEMORY_SIZE_ARG_PATTERN = Pattern.compile(
            "\\s*-XX:MaxDirectMemorySize\\s*=\\s*([0-9]+)\\s*([kKmMgG]?)\\s*$");

    private static final boolean HAS_UNSAFE = checkUnsafe();

    private static boolean checkUnsafe() {
        try {
            boolean hasUnsafe = PlatformSupportInner.hasUnsafe();
            logger.debug("sun.misc.Unsafe: {}", hasUnsafe ? "available" : "unavailable");
            return hasUnsafe;
        } catch (Throwable t) {
            return false;
        }
    }

    public static boolean hasUnsafe() {
        return HAS_UNSAFE;
    }

    private static <E extends Throwable> void doThrowException(Throwable t) throws E {
        if (hasUnsafe()) {
            PlatformSupportInner.throwException(t);
        } else {
            PlatformSupport.<RuntimeException>doThrowException(t);
        }
    }


}
