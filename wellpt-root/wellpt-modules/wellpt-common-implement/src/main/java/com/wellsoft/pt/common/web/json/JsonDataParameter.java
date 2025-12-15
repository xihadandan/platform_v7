/*
 * @(#)2019年6月12日 V1.0
 *
 * Copyright 2019 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.common.web.json;

import com.wellsoft.context.util.date.DateUtils;
import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.core.MethodParameter;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2019年6月12日.1	zhulh		2019年6月12日		Create
 * </pre>
 * @date 2019年6月12日
 */
public class JsonDataParameter {

    private int index;

    private Object jsonObject;

    private String argType;

    private boolean isArray;

    private boolean isPrimitive;

    /**
     * @param index
     * @param jsonObject
     * @param argType
     */
    public JsonDataParameter(int index, Object jsonObject, String argType) {
        super();
        this.index = index;
        this.jsonObject = jsonObject;
        this.argType = argType;
        parseJsonObject(jsonObject);
    }

    /**
     * @param object
     */
    private void parseJsonObject(Object object) {
        if (object instanceof JSONObject) {
        } else if (object instanceof JSONArray) {
            isArray = true;
        } else {
            isPrimitive = true;
        }
    }

    /**
     * @return the index
     */
    public int getIndex() {
        return index;
    }

    /**
     * @param index 要设置的index
     */
    public void setIndex(int index) {
        this.index = index;
    }

    /**
     * @return the jsonObject
     */
    public Object getJsonObject() {
        return jsonObject;
    }

    /**
     * @param jsonObject 要设置的jsonObject
     */
    public void setJsonObject(Object jsonObject) {
        this.jsonObject = jsonObject;
    }

    /**
     * @return the argType
     */
    public String getArgType() {
        return argType;
    }

    /**
     * @param argType 要设置的argType
     */
    public void setArgType(String argType) {
        this.argType = argType;
    }

    /**
     * @param methodParameter
     * @return
     */
    public boolean isMatches(MethodParameter methodParameter) {
        if (isArray) {
            return isMatchArray(methodParameter);
        } else if (isPrimitive) {
            return isMatchPrimitive(methodParameter);
        }
        return isMatchObjectClass(methodParameter);
    }

    /**
     * @param methodParameter
     * @return
     */
    private boolean isMatchArray(MethodParameter methodParameter) {
        Class<?> parameterType = methodParameter.getParameterType();
        return (parameterType.isArray() || (Collection.class.isAssignableFrom(parameterType)))
                && isMatchArgType(this.argType, methodParameter);
    }

    /**
     * @param methodParameter
     * @return
     */
    private boolean isMatchPrimitive(MethodParameter methodParameter) {
        Class<?> parameterType = methodParameter.getParameterType();
        return canConvertPrimitive(this.jsonObject, parameterType) && isMatchArgType(this.argType, methodParameter);
    }

    /**
     * @param methodParameter
     * @return
     */
    private boolean isMatchObjectClass(MethodParameter methodParameter) {
        Class<?> parameterType = methodParameter.getParameterType();
        return !(parameterType.isAnnotation() || parameterType.isAnonymousClass() || parameterType.isArray()
                || parameterType.isEnum() || parameterType.isPrimitive() || parameterType.isSynthetic())
                && !Collection.class.isAssignableFrom(parameterType)
                && !isPrimitiveWrapperType(parameterType)
                && isMatchArgType(this.argType, methodParameter);
    }

    /**
     * @param parameterType
     * @return
     */
    private boolean isPrimitiveWrapperType(Class<?> parameterType) {
        return Boolean.class.equals(parameterType) || Character.class.equals(parameterType)
                || Byte.class.equals(parameterType) || Short.class.equals(parameterType)
                || Integer.class.equals(parameterType) || Long.class.equals(parameterType)
                || Float.class.equals(parameterType) || Double.class.equals(parameterType);
    }

    /**
     * @param parameterType
     * @return
     */
    private boolean canConvertPrimitive(Object object, Class<?> parameterType) {
        boolean canConvert = true;
        try {
            if (JSONObject.NULL.equals(object)) {
                return !parameterType.isPrimitive();
            }
            String parameterTypeName = parameterType.getName();
            if (object instanceof String && (parameterType.isPrimitive() || isPrimitiveWrapperType(parameterType))) {
                canConvert = false;
            } else if (parameterType.isAssignableFrom(String.class)) {
                if (!(object instanceof String)) {
                    canConvert = false;
                }
            } else if (parameterType.isAssignableFrom(Boolean.class)
                    || StringUtils.equals(parameterTypeName, "boolean")) {
                Boolean.valueOf(object.toString());
            } else if (parameterType.isAssignableFrom(Character.class) || StringUtils.equals(parameterTypeName, "char")) {
                if (object.toString().length() == 1) {
                    Character.valueOf(object.toString().charAt(0));
                } else {
                    canConvert = false;
                }
            } else if (parameterType.isAssignableFrom(Byte.class) || StringUtils.equals(parameterTypeName, "byte")) {
                Byte.valueOf(object.toString());
            } else if (parameterType.isAssignableFrom(Short.class) || StringUtils.equals(parameterTypeName, "short")) {
                Byte.valueOf(object.toString());
            } else if (parameterType.isAssignableFrom(Integer.class) || StringUtils.equals(parameterTypeName, "int")) {
                Integer.valueOf(object.toString());
            } else if (parameterType.isAssignableFrom(Long.class) || StringUtils.equals(parameterTypeName, "long")) {
                Long.valueOf(object.toString());
            } else if (parameterType.isAssignableFrom(Float.class) || StringUtils.equals(parameterTypeName, "float")) {
                Float.valueOf(object.toString());
            } else if (parameterType.isAssignableFrom(Double.class) || StringUtils.equals(parameterTypeName, "double")) {
                Double.valueOf(object.toString());
            } else if (parameterType.isAssignableFrom(Date.class)) {
                DateUtils.parse(object.toString());
            } else if (parameterType.isEnum()) {
            } else {
                canConvert = false;
            }
        } catch (Exception e) {
            canConvert = false;
        }
        return canConvert;
    }

    /**
     * @param argType
     * @param parameterType
     * @return
     */
    private boolean isMatchArgType(String argType, MethodParameter methodParameter) {
        if (StringUtils.isBlank(argType)) {
            return true;
        }
        // 判断是否泛型参数
        if (isGenericArgType(argType)) {
            return isMatchGenericArgType(argType, methodParameter);
        } else if (isArrayArgType(argType)) {
            // 判断是否数组参数
            return isMatchArrayArgType(argType, methodParameter);
        }
        Class<?> parameterType = methodParameter.getParameterType();
        String parameterName = parameterType.getName();
        if (isMatchName(argType, parameterName)) {
            return true;
        }
        return false;
    }

    /**
     * @param argType
     * @return
     */
    private boolean isArrayArgType(String argType) {
        return StringUtils.endsWith(argType, "]");
    }

    /**
     * @param argType
     * @return
     */
    private boolean isGenericArgType(String argType) {
        return StringUtils.contains(argType, "<");
    }

    /**
     * @param argType
     * @param methodParameter
     * @return
     */
    private boolean isMatchArrayArgType(String argType, MethodParameter methodParameter) {
        Type type = methodParameter.getGenericParameterType();
        String parameterName = type.toString();
        return isMatchName(argType, parameterName);
    }

    /**
     * @param argType
     * @param methodParameter
     * @return
     */
    private boolean isMatchGenericArgType(String argType, MethodParameter methodParameter) {
        Type type = methodParameter.getGenericParameterType();
        String parameterName = type.toString();
        if (type instanceof ParameterizedType) {
            StringBuilder sb = new StringBuilder();
            extractShortParameterName((ParameterizedType) type, sb);
            parameterName = sb.toString();
        }
        String argRawTypeName = getRawTypeName(argType);
        String[] argComponentTypeNames = getComponentTypeNames(argType);
        String parameterRawTypeName = getRawTypeName(parameterName);
        String[] parameterComponentTypeNames = getComponentTypeNames(parameterName);
        if (isMatchName(argRawTypeName, parameterRawTypeName)
                && isMatchNames(argComponentTypeNames, parameterComponentTypeNames)) {
            return true;
        }
        return false;
    }

    /**
     * @param type
     * @param sb
     * @return
     */
    private String extractShortParameterName(ParameterizedType type, StringBuilder sb) {
        String typeName = type.getRawType().toString();
        Type[] componentTypes = type.getActualTypeArguments();
        sb.append(StringUtils.substringAfterLast(typeName, "."));
        Iterator<Type> it = Arrays.asList(componentTypes).iterator();
        sb.append("<");
        while (it.hasNext()) {
            Type componentType = it.next();
            if (componentType instanceof ParameterizedType) {
                extractShortParameterName((ParameterizedType) componentType, sb);
            } else if (componentType instanceof WildcardType) {
                sb.append(componentType.toString());
            } else {
                sb.append(StringUtils.substringAfterLast(componentType.toString(), "."));
            }
            if (it.hasNext()) {
                sb.append(",");
            }
        }
        sb.append(">");
        return sb.toString();
    }

    /**
     * @param argType
     * @return
     */
    private String getRawTypeName(String typeName) {
        return StringUtils.substringBefore(typeName, "<");
    }

    /**
     * @param argType
     * @return
     */
    private String[] getComponentTypeNames(String typeName) {
        String[] componentTypeNames = StringUtils.substringBefore(StringUtils.substringAfter(typeName, "<"), ">")
                .split(",");
        for (int i = 0; i < componentTypeNames.length; i++) {
            componentTypeNames[i] = StringUtils.trim(componentTypeNames[i]);
        }
        return componentTypeNames;
    }

    /**
     * @param argType
     * @param parameterName
     * @return
     */
    private boolean isMatchName(String argType, String parameterName) {
        String type = StringUtils.replace(argType, "[]", ";");
        if (StringUtils.contains(type, ".")) {
            return StringUtils.equals(type, parameterName) || StringUtils.endsWith(parameterName, type);
        }
        return StringUtils.equals(type, parameterName) || StringUtils.endsWith(parameterName, "." + type);
    }

    /**
     * @param argComponentTypeNames
     * @param parameterComponentTypeNames
     * @return
     */
    private boolean isMatchNames(String[] argComponentTypeNames, String[] parameterComponentTypeNames) {
        for (int i = 0; i < argComponentTypeNames.length; i++) {
            if (!isMatchName(argComponentTypeNames[i], parameterComponentTypeNames[i])) {
                return false;
            }
        }
        return true;
    }

}
