package com.sun.enhance.util;

import java.util.Collection;

/**
 * Created by zksun on 5/12/16.
 */
public final class CollectionUtils {
    public static boolean isEmpty(Collection<?> collection) {
        if (null == collection) return false;
        return collection.isEmpty();
    }

    public static boolean isNotEmpty(Collection<?> collection) {
        if (null == collection) {
            throw new NullPointerException("collection");
        }
        return !isEmpty(collection);
    }
}
