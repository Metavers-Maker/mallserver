package com.muling.common.util;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

public class ReflectUtil extends jodd.util.ClassUtil {

    /**
     * 获得所有字段
     *
     * @param clazz
     * @return
     */
    public static Field[] getAllFields(Class clazz) {
        return getAllFields(clazz, Object.class);
    }

    /**
     * 获得所有字段
     *
     * @param clazz
     * @param limit
     * @return
     */
    public static Field[] getAllFields(Class clazz, Class limit) {
        Package topPackage = clazz.getPackage();
        List<Field> fieldList = new ArrayList<Field>();
        int topPackageHash = topPackage == null ? 0 : topPackage.hashCode();
        boolean top = true;
        do {
            if (clazz == null) {
                break;
            }
            Field[] declaredFields = clazz.getDeclaredFields();
            for (Field field : declaredFields) {
                if (top == true) {                // add all top declared fields
                    fieldList.add(field);
                    continue;
                }
                int modifier = field.getModifiers();
                if (Modifier.isPrivate(modifier) == true) {
                    addFieldIfNotExist(fieldList, field);
                    continue;
                }
                if (Modifier.isPublic(modifier) == true) {
                    addFieldIfNotExist(fieldList, field);            // add super public methods
                    continue;
                }
                if (Modifier.isProtected(modifier) == true) {
                    addFieldIfNotExist(fieldList, field);            // add super protected methods
                    continue;
                }
                // add super default methods from the same package
                Package pckg = field.getDeclaringClass().getPackage();
                int pckgHash = pckg == null ? 0 : pckg.hashCode();
                if (pckgHash == topPackageHash) {
                    addFieldIfNotExist(fieldList, field);
                }
            }
            top = false;
        } while ((clazz = clazz.getSuperclass()) != limit);

        Field[] fields = new Field[fieldList.size()];
        for (int i = 0; i < fields.length; i++) {
            fields[i] = fieldList.get(i);
        }
        return fields;
    }

    private static void addFieldIfNotExist(List<Field> allFields, Field newField) {
        for (Field f : allFields) {
            if (ReflectUtil.compareSignatures(f, newField) == true) {
                return;
            }
        }
        allFields.add(newField);
    }

}
