/*
 * @(#)2016年2月1日 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.common.validation.support;

import com.wellsoft.context.jdbc.entity.IdEntity;
import com.wellsoft.context.jdbc.entity.TenantEntity;
import com.wellsoft.context.validator.*;
import org.apache.commons.lang.StringUtils;
import org.hibernate.validator.constraints.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.constraints.Digits;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.lang.Number;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.Map.Entry;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016年2月1日.1	zhulh		2016年2月1日		Create
 * </pre>
 * @date 2016年2月1日
 */
public class FieldValidationUtils {

    private static Logger LOG = LoggerFactory.getLogger(FieldValidationUtils.class);

    private static Map<String, Class<?>> ruleMap = new HashMap<String, Class<?>>();

    static {
        // 必填 required -> NotBlank
        ruleMap.put("required", NotBlank.class);
        // 邮件 email -> Email
        ruleMap.put("email", Email.class);
        // URL url -> URL
        ruleMap.put("url", URL.class);
        // 日期 date -> Date
        // ruleMap.put("date", Date.class);
        // ISO日期 dateISO -> DateISO
        // ruleMap.put("dateISO", DateISO.class);
        // 数字 number -> Number
        ruleMap.put("number", Number.class);
        // 整数 digits -> Digits
        ruleMap.put("digits", Digits.class);
        // 信用卡 creditcard -> CreditCardNumber
        ruleMap.put("creditcard", CreditCardNumber.class);
        // 最小长度 minlength -> MinLength
        ruleMap.put("minlength", MinLength.class);
        // 最大长度 maxlength -> MaxLength
        ruleMap.put("maxlength", MaxLength.class);
        // 长度范围 rangelength -> Length
        ruleMap.put("rangelength", Length.class);
        // 最小值 min -> Min
        ruleMap.put("min", Min.class);
        // 最大值 max -> Max
        ruleMap.put("max", Max.class);
        // 值范围 range -> Range
        ruleMap.put("range", Range.class);
        // 值等于 equalTo -> EqualTo
        // ruleMap.put("equalTo", EqualTo.class);
        // 远程验证 remote -> RemoteUnique
        ruleMap.put("remote", RemoteUnique.class);
        // 电话号码 remote -> Telephone
        ruleMap.put("telephone", Telephone.class);
        // 手机号码 remote -> MobilePhone
        ruleMap.put("mobilePhone", MobilePhone.class);
        // 身份证号 remote -> MobilePhone
        ruleMap.put("idcardNumber", IdCardNumber.class);
    }

    public static FieldValidationMetaData getMetaData(Object object) {
        Class<?> cls = object.getClass();
        List<Field> fields = getAllDeclaredFields(cls);
        FieldValidationMetaData fieldValidationMetaData = new FieldValidationMetaData();
        List<FieldMetaData> fmds = new ArrayList<FieldMetaData>();
        fieldValidationMetaData.setFieldMetaDatas(fmds);
        for (Field field : fields) {
            String name = field.getName();
            Annotation[] annotations = field.getAnnotations();
            for (Annotation annotation : annotations) {
                if (NotNull.class.isAssignableFrom(annotation.annotationType())) {
                    fmds.add(getFieldMetaData(name, annotation, "required", NotNull.class));
                }
                for (Entry<String, Class<?>> entry : ruleMap.entrySet()) {
                    if (entry.getValue().isAssignableFrom(annotation.annotationType())) {
                        String rule = entry.getKey();
                        Class<?> ruleCls = entry.getValue();
                        fmds.add(getFieldMetaData(name, annotation, rule, ruleCls));
                    }
                }
            }
            // 字符类型的字段空默认加入长度255验证
            if (String.class.isAssignableFrom(field.getType()) && isDefaultMaxLength(annotations)) {
                FieldMetaData fieldMetaData = new FieldMetaData(name, "maxlength", "global.validator.MaxLength.message");
                fieldMetaData.setMax(255);
                fmds.add(fieldMetaData);
            }
        }
        return fieldValidationMetaData;
    }

    public static boolean isDefaultMaxLength(Annotation[] annotations) {
        return (annotations.length == 0 || !contains(annotations, Length.class)) && !contains(annotations, MaxLength.class);
    }

    /**
     * @param name
     * @param annotation
     * @param rule
     * @return
     */
    private static FieldMetaData getFieldMetaData(String name, Annotation annotation, String rule, Class<?> ruleCls) {
        String message = "";
        try {
            Method method = annotation.annotationType().getMethod("message");
            message = (String) method.invoke(annotation);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }

        FieldMetaData fmd = new FieldMetaData(name, rule, message);
        fmd.setRuleValue(true);

        if (Length.class.isAssignableFrom(ruleCls)) {
            Length len = (Length) annotation;
            fmd.setMax(len.max());
            fmd.setMin(len.min());
        }
        if (MaxLength.class.isAssignableFrom(ruleCls)) {
            MaxLength len = (MaxLength) annotation;
            fmd.setMax(len.max());
        }

        return fmd;
    }

    /**
     * 如何描述该方法
     *
     * @param annotations
     * @param class1
     * @return
     */
    private static boolean contains(Annotation[] annotations, Class<?> lengthCls) {
        for (Annotation annotation : annotations) {
            if (lengthCls.isAssignableFrom(annotation.annotationType())) {
                return true;
            }
        }
        return false;
    }

    public static Map<String, Object> getJSMetaData(Object object) {
        Map<String, Object> map = new HashMap<String, Object>();
        FieldValidationMetaData fieldValidationMetaData = getMetaData(object);
        Map<String, Map<String, Object>> rules = new HashMap<String, Map<String, Object>>();
        Map<String, Map<String, Object>> messages = new HashMap<String, Map<String, Object>>();
        map.put("rules", rules);
        map.put("messages", messages);

        List<FieldMetaData> fmds = fieldValidationMetaData.getFieldMetaDatas();
        for (FieldMetaData fmd : fmds) {
            // 设置规则
            String name = fmd.getName();
            String rule = fmd.getRule();
            if (!rules.containsKey(name)) {
                rules.put(name, new HashMap<String, Object>());
            }
            Map<String, Object> ruleMap = rules.get(name);
            ruleMap.put(rule, fmd.getRuleValue());
            if (fmd.getMax() != 0) {
                if ("rangelength".equals(rule) || "range".equals(rule)) {
                    int[] range = new int[2];
                    range[0] = fmd.getMin();
                    range[1] = fmd.getMax();
                    ruleMap.put(rule, range);
                } else {
                    ruleMap.put(rule, fmd.getMax());
                }
            }
            // 远程唯一检验
            if ("remote".equals(rule)) {
                String type = getType(object);
                ruleMap.put(rule, type);
            }

            // 设置消息
            if (!messages.containsKey(name)) {
                messages.put(name, new HashMap<String, Object>());
            }
            Map<String, Object> messageMap = messages.get(name);
            messageMap.put(rule, fmd.getJsMessage());
        }

        return map;
    }

    /**
     * @param object
     * @return
     */
    private static String getType(Object object) {
        Class<?> tmp = object.getClass();
        while (!(tmp.getSuperclass().equals(IdEntity.class) || tmp.getSuperclass().equals(TenantEntity.class))) {
            if (tmp.getSuperclass().equals(Object.class)) {
                break;
            }
            tmp = tmp.getSuperclass();
        }
        return StringUtils.uncapitalize(tmp.getSimpleName());
    }

    /**
     * @param cls
     * @return
     */
    private static List<Field> getAllDeclaredFields(Class<?> cls) {
        List<Field> fields = new ArrayList<Field>();
        fields.addAll(Arrays.asList(cls.getDeclaredFields()));
        Class<?> superclass = cls.getSuperclass();
        if (!superclass.isAssignableFrom(Object.class)) {
            fields.addAll(Arrays.asList(superclass.getDeclaredFields()));
            superclass = superclass.getSuperclass();
        }
        return fields;
    }
}
