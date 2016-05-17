package com.sun.enhance.internal;

import com.sun.enhance.util.ClassUtils;
import com.sun.enhance.logging.Logger;
import com.sun.enhance.logging.LoggerFactory;
import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.Buffer;
import java.nio.ByteBuffer;

/**
 * Created by zksun on 5/12/16.
 */
public final class PlatformSupportInner {

    private static final Logger logger = LoggerFactory.getLogger(PlatformSupportInner.class);

    private static final Unsafe UNSAFE;

    static {
        boolean directBufferFreeable = false;

        try {
            Class<?> cls = ClassUtils.loadClass("sun.nio.ch.DirectBuffer", false, ClassUtils.getClassLoader(PlatformSupportInner.class));
            Method method = cls.getMethod("cleaner");
            if ("sun.misc.Cleaner".equals(method.getReturnType().getName())) {
                directBufferFreeable = true;
            }
        } catch (Throwable throwable) {
            //ignore
        }

        logger.debug("sun.nio.ch.DirectBuffer.cleaner():{}", directBufferFreeable ? "available" : "unavailable");

        Field addressField;

        try {
            addressField = Buffer.class.getDeclaredField("address");
            addressField.setAccessible(true);
            if (addressField.getLong(ByteBuffer.allocate(1)) != 0) {
                addressField = null;
            }
        } catch (Throwable throwable) {
            addressField = null;
        }

        logger.debug("java.nio.Buffer.address: {}", null != addressField ? "available" : "unavailable");

        Unsafe unsafe;
        if (null != addressField && directBufferFreeable) {
            Field unsafeField = null;

            try {
                unsafeField = Unsafe.class.getDeclaredField("theUnsafe");
                unsafeField.setAccessible(true);
                unsafe = (Unsafe) unsafeField.get(null);
                logger.debug("sun.misc.Unsafe.theUnsafe: {}", null != unsafe ? "available" : "unavailable");

                try {
                    unsafe.getClass().getDeclaredMethod("copyMemory", new Class[]{Object.class, long.class, Object.class, long.class, long.class});
                    logger.debug("sun.misc.Unsafe.copyMemory: available");
                } catch (NoSuchMethodException e) {
                    logger.debug("sun.misc.Unsafe.copyMemory: unavailable");
                    throw e;
                } catch (SecurityException e) {
                    logger.debug("sun.misc.Unsafe.copyMemory: unavailable");
                    throw e;
                }

            } catch (Throwable throwable) {
                unsafe = null;
            }
        } else {
            unsafe = null;
        }

        UNSAFE = unsafe;

    }

    static void throwException(Throwable t) {
        UNSAFE.throwException(t);
    }

    static boolean hasUnsafe() {
        return null != UNSAFE;
    }
}
