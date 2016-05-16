package com.sun.enhance;

import java.io.IOException;

/**
 * Created by zksun on 5/16/16.
 */
public class NestedIOException extends IOException {

    public NestedIOException(String msg) {
        super(msg);
    }

    public NestedIOException(String message, Throwable cause) {
        super(message);
        initCause(cause);
    }

    @Override
    public String getMessage() {
        Throwable cause = getCause();
        if (null != cause) {
            StringBuilder stringBuilder = new StringBuilder();
            String msg;
            if (null != (msg = getMessage())) {
                stringBuilder.append(msg).append("; ");
            }
            return stringBuilder.append("nested exception is ").append(cause).toString();
        } else {
            return getMessage();
        }
    }
}
