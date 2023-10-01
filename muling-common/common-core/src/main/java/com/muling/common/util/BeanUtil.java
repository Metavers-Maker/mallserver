package com.muling.common.util;

import jodd.bean.BeanCopy;

import java.util.ArrayList;
import java.util.List;

public class BeanUtil {

    public static final jodd.bean.BeanUtil pojo = jodd.bean.BeanUtil.pojo;

    public static final jodd.bean.BeanUtil declared = jodd.bean.BeanUtil.declared;

    public static final jodd.bean.BeanUtil silent = jodd.bean.BeanUtil.silent;

    /**
     * bean copy
     *
     * @param src
     * @param targetClass
     */
    public static <T> T copy(Object src, Class<T> targetClass, String... excludes) {

        return copy(src, targetClass, false, excludes);
    }

    public static <T> List<T> copy(List src, Class<T> targetClass, String... excludes) {

        return copy(src, targetClass, false, excludes);
    }

    public static <T> T copy(Object src, T target, String... excludes) {

        return copy(src, target, false, excludes);
    }

    /**
     * bean copy
     *
     * @param src
     * @param targetClass
     */
    public static <T> T copy(Object src, Class<T> targetClass, boolean ignoreNullValues, String... excludes) {
        if (src == null) {
            return null;
        }
        T target = newInstance(targetClass);
        BeanCopy.from(src).to(target).ignoreNulls(ignoreNullValues).exclude(excludes).copy();
        return target;
    }

    public static <T> List<T> copy(List src, Class<T> targetClass, boolean ignoreNullValues, String... excludes) {
        if (src == null) {
            return null;
        }
        List<T> results = new ArrayList<>();
        for (Object each : src) {
            results.add(copy(each, targetClass, ignoreNullValues, excludes));
        }
        return results;
    }

    public static <T> T copy(Object src, T target, boolean ignoreNullValues, String... excludes) {

        if (src == null) {
            return target;
        }

        BeanCopy.from(src).to(target).ignoreNulls(ignoreNullValues).exclude(excludes).copy();

        return target;
    }


    private static <T> T newInstance(Class<T> clazz) {
        try {
            return ReflectUtil.newInstance(clazz);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}
