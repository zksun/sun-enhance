package com.sun.enhance.io;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by zksun on 5/16/16.
 */
public interface InputStreamSource {
    InputStream getInputStream() throws IOException;
}
