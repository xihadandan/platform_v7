/*
 * @(#)2013-5-2 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.jpa.template;

import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.jdbc.entity.IdEntity;
import com.wellsoft.pt.jpa.template.freemarker.FreeMarkerTemplateEngine;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanWrapperImpl;

import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.net.URL;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-5-2.1	zhulh		2013-5-2		Create
 * </pre>
 * @date 2013-5-2
 */
public abstract class AbstractTemplateEngine implements TemplateEngine {

    // ${variable}变量查找正则表达式
    private static final String REGEX = "\\$\\{.*?(?=\\})";
    private static final String[] NUMBERS = {"零", "壹", "贰", "叁", "肆", "伍", "陆", "柒", "捌", "玖"};
    protected Logger logger = LoggerFactory.getLogger(getClass());

    public static synchronized String toChinese(String str) {
        StringBuilder sb = new StringBuilder();
        sb.append(getSplitDateStr(str, 0));
        return sb.toString();
    }

    public static String getSplitDateStr(String str, int unit) {
        // unit是单位 0=年 1=月 2日
        String[] DateStr = str.split("-");
        if (unit > DateStr.length) {
            unit = 0;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < DateStr[unit].length(); i++) {

            if ((unit == 1 || unit == 2) && Integer.valueOf(DateStr[unit]) > 9) {
                sb.append(convertNum(DateStr[unit].substring(0, 1))).append("拾")
                        .append(convertNum(DateStr[unit].substring(1, 2)));
                break;
            } else {
                sb.append(convertNum(DateStr[unit].substring(i, i + 1)));
            }
        }
        if (unit == 1 || unit == 2) {
            return sb.toString().replaceAll("^壹", "").replace("零", "");
        }
        return sb.toString();
    }

    private static String convertNum(String str) {
        return NUMBERS[Integer.valueOf(str)];
    }

    public static boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        if (!isNum.matches()) {
            return false;
        }
        return true;
    }

    /**
     * 将${variable}的变量形式转化为${variable!}
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.core.template.TemplateEngine#decorateSource(java.lang.String)
     */
    @Override
    public String decorateSource(String source) {
        String input = source;
        Pattern pattern = Pattern.compile(REGEX);
        Matcher matcher = pattern.matcher(input);
        while (matcher.find()) {
            String group = matcher.group();
            input = input.replace(group + "}", group + "!}");
        }
        return input;
    }

    /**
     * 合并实体集合、动态表单及默认常量数据
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.core.template.TemplateEngine#mergeDataAsMap(java.util.Collection, java.util.Map, boolean, boolean)
     */
    @Override
    public <ENTITY extends IdEntity> Map<Object, Object> mergeDataAsMap(Collection<ENTITY> entities, Map dytableMap,
                                                                        Map<String, Object> extraData, boolean addDefaultConstant, boolean replaceExistKey) {
        Map<Object, Object> root = new HashMap<Object, Object>();
        // 添加默认常量
        if (addDefaultConstant) {
            addDefaultConstant(root);
        }

        // 实体解析
        if (entities != null) {
            for (IdEntity idEntity : entities) {
                // 传入${类.属性}时解析
                root.put(StringUtils.lowerCase(idEntity.getClass().getSimpleName()), idEntity);

                // 只传入${属性}时解析
                BeanWrapperImpl wrapper = new BeanWrapperImpl(idEntity);
                PropertyDescriptor[] descriptors = wrapper.getPropertyDescriptors();
                for (PropertyDescriptor descriptor : descriptors) {
                    String propertyName = descriptor.getName();
                    // 多个实体是否替换存在的属性变量
                    if (replaceExistKey) {
                        root.put(propertyName, wrapper.getPropertyValue(propertyName));
                    } else {
                        if (!root.containsKey(propertyName)) {
                            root.put(propertyName, wrapper.getPropertyValue(propertyName));
                        }
                    }
                }
            }
        }

        // 动态表单解析
        if (dytableMap != null) {
            root.put("dytable", dytableMap);
            root.put("dyform", dytableMap);
        }

        // 额外数据
        if (extraData != null) {
            root.putAll(extraData);
        }

        return root;
    }

    /**
     * @param root
     */
    @Override
    public void addDefaultConstant(Map<Object, Object> root) {
        Map<String, Object> defaultConstantMap = new HashMap<String, Object>();
        this.addDefaultConstant(defaultConstantMap, Calendar.getInstance());
        root.putAll(defaultConstantMap);
    }

    @Override
    public void addDefaultConstant(Map<String, Object> root, Calendar calendar) {
        int year = calendar.get(Calendar.YEAR); // 获取年份
        int month = calendar.get(Calendar.MONTH) + 1; // 获取月份
        int week = calendar.get(Calendar.WEEK_OF_YEAR);// 获取周
        int day = calendar.get(Calendar.DATE); // 获取日
        int hour = calendar.get(Calendar.HOUR); // 小时
        int minute = calendar.get(Calendar.MINUTE); // 分
        int second = calendar.get(Calendar.SECOND); // 秒

        root.put("年", String.valueOf(year));
        root.put("月", String.valueOf(month));
        root.put("周", String.valueOf(week));
        root.put("日", String.valueOf(day));
        root.put("时", String.valueOf(hour));
        root.put("分", String.valueOf(minute));
        root.put("秒", String.valueOf(second));
        root.put("简年", String.valueOf(year).substring(2));
        root.put("大写年", toChinese(String.valueOf(year)));
        root.put("大写月", toChinese(String.valueOf(month)));
        root.put("大写日", toChinese(String.valueOf(day)));

        root.put("YEAR", String.valueOf(year));
        root.put("MONTH", String.valueOf(month));
        root.put("WEEK", String.valueOf(week));
        root.put("DAY", String.valueOf(day));
        root.put("HOUR", String.valueOf(hour));
        root.put("MINUTE", String.valueOf(minute));
        root.put("SECOND", String.valueOf(second));
        root.put("SHORTYEAR", String.valueOf(year).substring(2));
    }

    @Override
    public void addDefaultConstantFillFormat(Map<Object, Object> root) {
        Map<String, Object> defaultConstantMap = new HashMap<String, Object>();
        this.addDefaultConstantFillFormat(defaultConstantMap, Calendar.getInstance());
        root.putAll(defaultConstantMap);
    }

    @Override
    public void addDefaultConstantFillFormat(Map<String, Object> root, Calendar calendar) {
        int year = calendar.get(Calendar.YEAR); // 获取年份
        int month = calendar.get(Calendar.MONTH) + 1; // 获取月份
        int week = calendar.get(Calendar.WEEK_OF_YEAR);// 获取周
        int day = calendar.get(Calendar.DATE); // 获取日
        int hour = calendar.get(Calendar.HOUR); // 小时
        int minute = calendar.get(Calendar.MINUTE); // 分
        int second = calendar.get(Calendar.SECOND); // 秒

        root.put("年", String.format("%04d", year));
        root.put("月", String.format("%02d", month));
        root.put("周", String.format("%02d", week));
        root.put("日", String.format("%02d", day));
        root.put("时", String.format("%02d", hour));
        root.put("分", String.format("%02d", minute));
        root.put("秒", String.format("%02d", second));
        root.put("简年", String.valueOf(year).substring(2));
        root.put("大写年", toChinese(String.valueOf(year)));
        root.put("大写月", toChinese(String.valueOf(month)));
        root.put("大写日", toChinese(String.valueOf(day)));

        root.put("YEAR", String.format("%04d", year));
        root.put("MONTH", String.format("%02d", month));
        root.put("WEEK", String.format("%02d", week));
        root.put("DAY", String.format("%02d", day));
        root.put("HOUR", String.format("%02d", hour));
        root.put("MINUTE", String.format("%02d", minute));
        root.put("SECOND", String.format("%02d", second));
        root.put("SHORTYEAR", String.valueOf(year).substring(2));
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.core.template.TemplateEngine#getTemplateAsString(java.lang.Class, java.lang.String)
     */
    @Override
    public String getTemplateAsString(Class<?> packageCls, String templateName) {
        String packagePath = StringUtils.replace(packageCls.getPackage().getName(), Separator.DOT.getValue(),
                Separator.SLASH.getValue());
        String path = Separator.SLASH.getValue() + packagePath + Separator.SLASH.getValue() + templateName;
        URL ftlRes = FreeMarkerTemplateEngine.class.getResource(path);
        try {
            return IOUtils.toString(ftlRes.openStream());
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        return StringUtils.EMPTY;
    }

}
