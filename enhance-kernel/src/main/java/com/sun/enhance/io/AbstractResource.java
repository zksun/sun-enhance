package com.sun.enhance.io;

import com.sun.enhance.NestedIOException;
import com.sun.enhance.util.Assert;
import com.sun.enhance.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * Created by zksun on 5/16/16.
 */
public abstract class AbstractResource implements Resource {
    @Override
    public boolean exists() {
        // Try file existence: can we find the file in the file system?
        try {
            return getFile().exists();
        } catch (IOException ex) {
            // Fall back to stream existence: can we open the stream?
            try {
                InputStream is = getInputStream();
                is.close();
                return true;
            } catch (Throwable isEx) {
                return false;
            }
        }
    }

    @Override
    public boolean isReadable() {
        return true;
    }

    public boolean isOpen() {
        return false;
    }

    @Override
    public URL getURL() throws IOException {
        throw new FileNotFoundException(getDescription() + " cannot be resolved to URL");
    }

    @Override
    public URI getURI() throws IOException {
        URL url = getURL();

        try {
            return ResourceUtils.toURI(url);
        } catch (URISyntaxException e) {
            throw new NestedIOException("Invalid URI [" + url + "]", e);
        }
    }

    @Override
    public File getFile() throws IOException {
        throw new FileNotFoundException(getDescription() + " cannot be resolved to absolute file path");
    }

    @Override
    public long contentLength() throws IOException {
        InputStream inputStream = this.getInputStream();
        Assert.state(null != inputStream, "resource input stream must not be null");

        long size = 0;
        byte[] buf = new byte[255];
        int read;
        try {
            while ((read = inputStream.read(buf)) != -1) {
                size += read;
            }
        } finally {
            try {
                inputStream.close();
            } catch (IOException ex) {
                //ignore
            }
        }
        return size;
    }

    public long lastModified() throws IOException {
        long lastModified = getFileForLastModifiedCheck().lastModified();
        if (lastModified == 0L) {
            throw new FileNotFoundException(getDescription() +
                    " cannot be resolved in the file system for resolving its last-modified timestamp");
        }
        return lastModified;
    }

    protected File getFileForLastModifiedCheck() throws IOException {
        return getFile();
    }

    @Override
    public Resource createRelative(String relativePath) throws IOException {
        throw new FileNotFoundException("Cannot create a relative resource for " + getDescription());
    }

    @Override
    public String getFilename() {
        return null;
    }

    @Override
    public String toString() {
        return getDescription();
    }

    @Override
    public boolean equals(Object obj) {
        return (obj == this || (obj instanceof Resource && ((Resource) obj).getDescription().equals(getDescription())));
    }

    @Override
    public int hashCode() {
        return getDescription().hashCode();
    }
}
