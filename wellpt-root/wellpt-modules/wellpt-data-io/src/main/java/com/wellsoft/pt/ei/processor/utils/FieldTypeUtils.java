package com.wellsoft.pt.ei.processor.utils;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.util.reflection.ReflectionUtils;
import com.wellsoft.pt.ei.annotate.FieldType;
import com.wellsoft.pt.ei.constants.ExportFieldTypeEnum;
import org.apache.commons.lang.StringUtils;
import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @Auther: yt
 * @Date: 2021/9/28 14:05
 * @Description:
 */
public class FieldTypeUtils {


    public static String toJson(Map<String, List<FieldDesc>> fieldDescMap, Class clazz) throws IllegalAccessException, InstantiationException {
        List<FieldDesc> descList = toFieldDescList(fieldDescMap, clazz);
        String jsonString = JSON.toJSONString(descList);
        return jsonString;
    }

    public static String descObject2Json(Object o) {
        String jsonString = JSON.toJSONString(o);
        return jsonString;
    }

    public static List<FieldDesc> toFieldDescList(Map<String, List<FieldDesc>> fieldDescMap, Class clazz) throws InstantiationException, IllegalAccessException {
        List<FieldDesc> descList = new ArrayList<>();
        Set<Field> fieldSet = ReflectionUtils.getFieldAll(clazz);
        for (Field field : fieldSet) {
            if (!field.isAnnotationPresent(FieldType.class)) {
                continue;
            }
            FieldDesc fieldDesc = convert(fieldDescMap, field);
            descList.add(fieldDesc);
        }
        return descList;
    }

    private static FieldDesc convert(Map<String, List<FieldDesc>> fieldDescMap, Field field) throws IllegalAccessException, InstantiationException {
        String fieldName = field.getName();
        FieldType fieldType = field.getAnnotation(FieldType.class);
        FieldDesc fieldDesc = new FieldDesc();
        fieldDesc.setFieldName(fieldName);
        fieldDesc.setDesc(fieldType.desc());
        fieldDesc.setDisplayValue(fieldType.displayValue());
        fieldDesc.setDictValue(fieldType.dictValue());
        if (fieldDescMap != null && fieldDescMap.containsKey(fieldName)) {
            fieldDesc.setType("Object");
            fieldDesc.setFields(fieldDescMap.get(fieldName));
            return fieldDesc;
        }
        if (fieldType.isGroup()) {
            Class fieldClass = null;
            if (field.getType().getName().equals("java.util.List")) {
                Type type = field.getGenericType(); //取得field的type
                ParameterizedTypeImpl parameterizedType = (ParameterizedTypeImpl) type; //强转成具体的实现类
                Type[] genericTypes = parameterizedType.getActualTypeArguments(); //取得包含的泛型类型
                fieldClass = ((Class<?>) genericTypes[0]).newInstance().getClass();
                fieldDesc.setType("List<Object>");
            } else {
                fieldClass = field.getType();
                fieldDesc.setType("Object");
            }
            List<FieldDesc> fieldDescList = toFieldDescList(fieldDescMap, fieldClass);
            fieldDesc.setFields(fieldDescList);
        } else {
            if (!fieldType.type().equals(ExportFieldTypeEnum.FILE) && field.getType().getName().equals("java.util.List")) {
                fieldDesc.setType("List<" + fieldType.type().getValue() + ">");
            } else {
                fieldDesc.setType(fieldType.type().getValue());
            }
        }

        return fieldDesc;
    }


    public static List<String> convertList(String str) {
        if (StringUtils.isBlank(str)) {
            return null;
        }
        List<String> strList = Lists.newArrayList();
        if (str.indexOf(Separator.SEMICOLON.getValue()) > -1) {
            strList = Lists.newArrayList(StringUtils.split(str, Separator.SEMICOLON.getValue()));
        } else if (str.indexOf(Separator.COMMA.getValue()) > -1) {
            strList = Lists.newArrayList(StringUtils.split(str, Separator.COMMA.getValue()));
        } else {
            strList.add(str);
        }
        return strList;
    }

    public static Map<String, List<String>> getAttachFileMap(String uuid, Object obj) throws Exception {
        RequiredFile requiredFile = getRequiredFile(uuid, obj);
        return requiredFile.getAttachFileMap();
    }

    public static RequiredFile getRequiredFile(String uuid, Object obj) throws Exception {
        RequiredFile requiredFile = new RequiredFile();
        convertRequiredFile(uuid, obj, requiredFile);
        return requiredFile;
    }


    public static void convertRequiredFile(String uuid, Object obj, RequiredFile requiredFile) throws Exception {
        Set<Field> fieldSet = ReflectionUtils.getFieldAll(obj.getClass());
        for (Field field : fieldSet) {
            if (!field.isAnnotationPresent(FieldType.class)) {
                continue;
            }
            FieldType fieldType = field.getAnnotation(FieldType.class);
            if (fieldType.isGroup()) {
                if (field.getType().getName().equals("java.util.List")) {
                    List cList = (List) field.get(obj);
                    for (Object cObj : cList) {
                        convertRequiredFile(uuid, cObj, requiredFile);
                    }
                } else {
                    Object cObj = field.get(obj);
                    convertRequiredFile(uuid, cObj, requiredFile);
                }
            }
            Object object = field.get(obj);
            if (fieldType.required()) {
                if (object == null) {
                    requiredFile.getRequiredNo().add(fieldType);
                }
                if (fieldType.type().equals(ExportFieldTypeEnum.STRING)) {
                    if (obj.toString().equals("")) {
                        requiredFile.getRequiredNo().add(fieldType);
                    }
                }
            }
            if (fieldType.type().equals(ExportFieldTypeEnum.FILE)) {
                if (object != null) {
                    List<String> fileList = (List<String>) object;
                    if (fileList.size() > 0) {
                        requiredFile.getAttachFileMap().put(uuid + Separator.UNDERLINE.getValue() + field.getName().toLowerCase(), fileList);
                    }
                }
            }

        }
    }
}
