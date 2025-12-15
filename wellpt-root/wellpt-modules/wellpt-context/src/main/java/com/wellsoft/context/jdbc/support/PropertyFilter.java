package com.wellsoft.context.jdbc.support;

import com.google.common.collect.Lists;
import com.wellsoft.context.util.reflection.ConvertUtils;
import com.wellsoft.context.util.web.ServletUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * 与具体ORM实现无关的属性过滤条件封装类, 主要记录页面中简单的搜索过滤条件.
 *
 * @author lilin
 */
public class PropertyFilter {
    /**
     * 多个属性间OR关系的分隔符.
     */
    public static final String OR_SEPARATOR = "_OR_";
    private static Logger LOG = LoggerFactory.getLogger(PropertyFilter.class);
    private boolean queryOr = false;
    private MatchType matchType = null;
    private Object matchValue = null;
    private Class<?> propertyClass = null;
    private String[] propertyNames = null;

    public PropertyFilter() {
    }
    /**
     * @param filterName 比较属性字符串,含待比较的比较类型、属性值类型及属性列表.
     *                   eg. LIKES_NAME_OR_LOGIN_NAME
     * @param value      待比较的值.
     */
    public PropertyFilter(final String filterName, final String value) {

        String firstPart = StringUtils.substringBefore(filterName, "_");
        String matchTypeCode = StringUtils.substring(firstPart, 0, firstPart.length() - 1);
        String propertyTypeCode = StringUtils.substring(firstPart, firstPart.length() - 1, firstPart.length());

        try {
            matchType = Enum.valueOf(MatchType.class, matchTypeCode);
        } catch (RuntimeException e) {
            LOG.error(ExceptionUtils.getStackTrace(e));
            throw new IllegalArgumentException("filter名称" + filterName + "没有按规则编写,无法得到属性比较类型.", e);
        }

        try {
            propertyClass = Enum.valueOf(PropertyType.class, propertyTypeCode).getValue();
        } catch (RuntimeException e) {
            LOG.error(ExceptionUtils.getStackTrace(e));
            throw new IllegalArgumentException("filter名称" + filterName + "没有按规则编写,无法得到属性值类型.", e);
        }

        String propertyNameStr = StringUtils.substringAfter(filterName, "_");
        Assert.isTrue(StringUtils.isNotBlank(propertyNameStr), "filter名称" + filterName + "没有按规则编写,无法得到属性名称.");
        propertyNames = StringUtils.splitByWholeSeparator(propertyNameStr, PropertyFilter.OR_SEPARATOR);
        if (matchType == MatchType.IN && StringUtils.isNotBlank(value)) {
            //集合类型
            String[] splits = value.split(";");
            List<Object> values = Lists.newArrayListWithCapacity(splits.length);
            for (String s : splits) {
                values.add(ConvertUtils.convertStringToObject(s, propertyClass));
            }
            this.matchValue = values;
            return;
        }
        this.matchValue = ConvertUtils.convertStringToObject(value, propertyClass);
    }

    /**
     * 从HttpRequest中创建PropertyFilter列表, 默认Filter属性名前缀为filter.
     *
     * @see #buildFromHttpRequest(HttpServletRequest, String)
     */
    public static List<PropertyFilter> buildFromHttpRequest(final HttpServletRequest request) {
        return buildFromHttpRequest(request, "filter");
    }

    /**
     * 从HttpRequest中创建PropertyFilter列表
     * PropertyFilter命名规则为Filter属性前缀_比较类型属性类型_属性名.
     * <p>
     * eg.
     * filter_EQS_name
     * filter_LIKES_name_OR_email
     */
    public static List<PropertyFilter> buildFromHttpRequest(final HttpServletRequest request, final String filterPrefix) {
        List<PropertyFilter> filterList = new ArrayList<PropertyFilter>();

        // 从request中获取含属性前缀名的参数,构造去除前缀名后的参数Map.
        Map<String, Object> filterParamMap = ServletUtils.getParametersStartingWith(request, filterPrefix + "_");

        // 分析参数Map,构造PropertyFilter列表
        for (Map.Entry<String, Object> entry : filterParamMap.entrySet()) {
            String filterName = entry.getKey();
            Object value = entry.getValue();
            if (value instanceof String[]) {
                String[] valueArr = (String[]) entry.getValue();
                for (String v : valueArr) {
                    // 如果value值为空,则忽略此filter.
                    if (StringUtils.isNotBlank(v)) {
                        try {
                            filterList.add(new PropertyFilter(filterName, v));
                        } catch (Exception e) {
                            LOG.error(ExceptionUtils.getStackTrace(e));
                        }
                    }
                }
            } else {
                try {
                    if (StringUtils.isNotBlank((String) value)) {
                        filterList.add(new PropertyFilter(filterName, (String) value));
                    }
                } catch (Exception e) {
                    LOG.error(ExceptionUtils.getStackTrace(e));
                }
            }

        }

        return filterList;
    }

    /**
     * @param request
     * @param queryPrefix
     * @param queryOr
     * @return
     */
    public static List<PropertyFilter> buildFromHttpRequest(HttpServletRequest request, String queryPrefix,
                                                            boolean queryOr) {
        List<PropertyFilter> propertyFilters = buildFromHttpRequest(request, queryPrefix);
        if (queryOr) {
            for (PropertyFilter propertyFilter : propertyFilters) {
                propertyFilter.setQueryOr(queryOr);
            }
        }
        return propertyFilters;
    }

    /**
     * 如何描述该方法
     *
     * @param propertyFilters
     * @return
     */
    public static Map<String, Object> convertToMap(List<PropertyFilter> propertyFilters) {
        Map<String, Object> values = new HashMap<String, Object>();
        for (PropertyFilter propertyFilter : propertyFilters) {
            if (!propertyFilter.hasMultiProperties()) {
                values.put(propertyFilter.getPropertyName(), propertyFilter.getMatchValue());
            } else {
                for (String propertyName : propertyFilter.getPropertyNames()) {
                    values.put(propertyName, propertyFilter.getMatchValue());
                }
            }
        }
        return values;
    }

    /**
     * @return the queryOr
     */
    public boolean isQueryOr() {
        return queryOr;
    }

    /**
     * @param queryOr
     */
    private void setQueryOr(boolean queryOr) {
        this.queryOr = queryOr;
    }

    /**
     * 获取比较值的类型.
     */
    public Class<?> getPropertyClass() {
        return propertyClass;
    }

    /**
     * 获取比较方式.
     */
    public MatchType getMatchType() {
        return matchType;
    }

    /**
     * 获取比较值.
     */
    public Object getMatchValue() {
        return matchValue;
    }

    /**
     * 获取比较属性名称列表.
     */
    public String[] getPropertyNames() {
        return propertyNames;
    }

    /**
     * 获取唯一的比较属性名称.
     */
    public String getPropertyName() {
        Assert.isTrue(propertyNames.length == 1, "There are not only one property in this filter.");
        return propertyNames[0];
    }

    /**
     * 是否比较多个属性.
     */
    public boolean hasMultiProperties() {
        return (propertyNames.length > 1);
    }

    /**
     * 属性比较类型.
     */
    public enum MatchType {
        EQ, LIKE, LT, GT, LE, GE, IN, NOT_IN;
    }

    /**
     * 属性数据类型.
     */
    public enum PropertyType {
        S(String.class), I(Integer.class), L(Long.class), N(Double.class), D(Date.class), B(Boolean.class);

        private Class<?> clazz;

        private PropertyType(Class<?> clazz) {
            this.clazz = clazz;
        }

        public Class<?> getValue() {
            return clazz;
        }
    }

}