package com.itisacat.rpcdemo.restclient.utils;

import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ClassUtils {
    private static final Map<Class<?>, Class<?>> primitiveMap = new HashMap<>(9);

    static {
        primitiveMap.put(String.class, String.class);
        primitiveMap.put(Boolean.class, boolean.class);
        primitiveMap.put(Byte.class, byte.class);
        primitiveMap.put(Character.class, char.class);
        primitiveMap.put(Double.class, double.class);
        primitiveMap.put(Float.class, float.class);
        primitiveMap.put(Integer.class, int.class);
        primitiveMap.put(Long.class, long.class);
        primitiveMap.put(Short.class, short.class);
        primitiveMap.put(Date.class, Date.class);
        primitiveMap.put(BigDecimal.class, BigDecimal.class);
    }

    /**
     * @param clazz
     * @return boolean
     * @description 判断基本类型
     * @see String#TYPE
     * @see Boolean#TYPE
     * @see Character#TYPE
     * @see Byte#TYPE
     * @see Short#TYPE
     * @see Integer#TYPE
     * @see Long#TYPE
     * @see Float#TYPE
     * @see Double#TYPE
     * @see Boolean#TYPE
     * @see char#TYPE
     * @see byte#TYPE
     * @see short#TYPE
     * @see int#TYPE
     * @see long#TYPE
     * @see float#TYPE
     * @see double#TYPE
     */
    public static boolean isPrimitive(Class<?> clazz) {
        if (primitiveMap.containsKey(clazz)) {
            return true;
        }
        return clazz.isPrimitive();
    }

    /**
     * @description 获取方法返回值类型
     * @param tartget
     * @param fieldName
     * @return
     * @return Class<?>
     */

    /**
     * @param tartget
     * @param fieldName
     * @return Class<?>
     * @description 获取方法返回值类型
     */
    public static Class<?> getElementType(Class<?> tartget, String fieldName) {
        Class<?> elementTypeClass = null;
        try {
            Type type = getElementTypeCase(tartget, fieldName); //tartget.getDeclaredField(fieldName).getGenericType();
            ParameterizedType t = (ParameterizedType) type;
            String classStr = t.getActualTypeArguments()[0].toString().replace("class ", "");
            elementTypeClass = Thread.currentThread().getContextClassLoader().loadClass(classStr);
        } catch (ClassNotFoundException | NoSuchFieldException | SecurityException e) {
            throw new RuntimeException("get fieldName[" + fieldName + "] error", e);
        }
        return elementTypeClass;
    }


    public static Type getElementTypeCase(Class<?> target, String fieldName) throws NoSuchFieldException {

        Type fieldType = getElementFieldTypeCase(target, fieldName);
        if (fieldType == null) {
            fieldType = getSupperClassFieldsTypeCase(target, fieldName);
        }
        if (fieldType == null)
            throw new NoSuchFieldException("get fieldName[" + fieldName + "] error target class [" + target.getCanonicalName() + "]");
        return fieldType;
    }


    public static Type getSupperClassFieldsTypeCase(Class<?> target, String fieldName) {

        for (Class<?> tg : target.getClasses()) {
            Type fieldType = getElementFieldTypeCase(tg, fieldName);
            if (fieldType != null)
                return fieldType;
        }
        Field field = ReflectionUtils.findField(target, fieldName);
        return field != null ? field.getGenericType() : null;
    }

    public static Type getElementFieldTypeCase(Class<?> target, String fieldName) {

        for (Field f : target.getDeclaredFields()) {
            if (f.getName().toLowerCase().compareTo(fieldName.toLowerCase()) == 0)
                return f.getGenericType();
        }
        return null;

    }
}
