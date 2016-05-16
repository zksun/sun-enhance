package com.sun.enhance.io;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;

/**
 * Created by zksun on 5/16/16.
 */
public interface Resource extends InputStreamSource {

    boolean exists();

    boolean isReadable();

    URL getURL() throws IOException;

    URI getURI() throws IOException;

    File getFile() throws IOException;

    long contentLength() throws IOException;

    Resource createRelative(String relativePath) throws IOException;

    String getFilename();

    String getDescription();
}
