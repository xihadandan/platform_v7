/*
 * @(#)2017年5月2日 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.datastore.support;

import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.context.util.date.DateUtils;
import com.wellsoft.pt.jpa.criteria.DataType;
import com.wellsoft.pt.jpa.criterion.*;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.util.*;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2017年5月2日.1	zhulh		2017年5月2日		Create
 * </pre>
 * @date 2017年5月2日
 */
public class DataStoreConditionConverter {

    private static Logger LOG = LoggerFactory.getLogger(DataStoreConditionConverter.class);

    /**
     * @param criterionJson
     * @param columnMap
     * @return
     */
    public static Criterion covertCriterion(Condition criterionJson, Map<String, DataStoreColumn> columnMap) {
        try {

            String sql = criterionJson.getSql();
            String type = criterionJson.getType();
            if (StringUtils.isNotBlank(sql)) {
                if (CriterionOperator.exists.getType().equals(type)) {
                    String sl = "exists (" + criterionJson.getSql() + ")";
                    return Restrictions.sql(sl);
                }
                return Restrictions.sql(sql);
            }
            if (CriterionOperator.or.getType().equals(type)) {
                Junction junction = Restrictions.disjunction();
                List<Condition> conditions = criterionJson.getConditions();
                if (conditions != null) {
                    for (Condition condition : conditions) {
                        Criterion criterion = covertCriterion(condition, columnMap);
                        if (criterion != null) {
                            junction.add(criterion);
                        }
                    }
                }
                return junction;
            }
            if (CriterionOperator.and.getType().equals(type)) {
                Junction junction = Restrictions.conjunction();
                List<Condition> conditions = criterionJson.getConditions();
                if (conditions != null) {
                    for (Condition condition : conditions) {
                        Criterion criterion = covertCriterion(condition, columnMap);
                        if (criterion != null) {
                            junction.add(criterion);
                        }
                    }
                }
                return junction;
            }
            String columnIndex = criterionJson.getColumnIndex();
            DataStoreColumn column = columnMap.get(columnIndex);
            if (column != null) {
                // 区间
                DataType dataType = DataType.getByType(column.getDataType());
                if (CriterionOperator.bw.getType().equals(type)) {
                    List<Object> valueList = getValueArray(criterionJson);
                    if (valueList.size() != 2) {
                        return Restrictions.sql("1<>1");
                    }
                    Object value1 = valueList.get(0);
                    Object value2 = valueList.get(1);
                    Junction junction = Restrictions.conjunction();
                    if (StringUtils.isNotBlank(value1.toString()) && StringUtils.isNotBlank(value2.toString())) {
                        Object lo = convertValue(value1, dataType, DateUtils.TYPE_START);
                        Object hi = convertValue(value2, dataType, DateUtils.TYPE_END);
                        junction.add(Restrictions.between(columnIndex, lo, hi));
                    } else if (StringUtils.isNotBlank(value1.toString())) {
                        junction.add(Restrictions.ge(columnIndex, convertValue(value1, dataType, DateUtils.TYPE_START)));
                    } else if (StringUtils.isNotBlank(value2.toString())) {
                        junction.add(Restrictions.le(columnIndex, convertValue(value2, dataType, DateUtils.TYPE_END)));
                    }
                    return junction;
                }

                if (CriterionOperator.ISNULL.getType().equals(type)) {
                    return Restrictions.isNull(columnIndex);
                }
                if (CriterionOperator.ISNOTNULL.getType().equals(type)) {
                    return Restrictions.isNotNull(columnIndex);
                }

                Object value = criterionJson.getValue();

                // 自定义查询条件
                String customCriterion = criterionJson.getCustomCriterion();
                if (StringUtils.isNotBlank(customCriterion)) {
                    return createCustomCriterion(customCriterion, columnIndex, value, CriterionOperator.getOperator(type));
                }
                // in、notIn查询
                if (CriterionOperator.in.getType().equals(type) || CriterionOperator.notIn.getType().equals(type)) {
                    if (StringUtils.isBlank(value.toString()) || value.toString().equals("undefined")) {
                        return null;
                    }

                    List<Object> valueList = getValueArray(criterionJson);
                    Iterator<Object> it = valueList.iterator();
                    List<Object> values = new ArrayList<Object>();
                    while (it.hasNext()) {
                        values.add(convertValue(it.next(), dataType));
                    }
                    if (CriterionOperator.in.getType().equals(type)) {
                        return Restrictions.in(columnIndex, values);
                    } else {
                        return Restrictions.notIn(columnIndex, values);
                    }
                }

                // 日期模糊查询
                if (!dataType.equals(DataType.T)) {
                    //模糊匹配跳过空值
                    if (value == null || StringUtils.isBlank(value.toString())) {
                        return null;
                    }
                    // 日期无法做模糊查询
                    if (CriterionOperator.like.getType().equals(type)) {
                        return Restrictions.like(columnIndex, value);
                    }
                    if (CriterionOperator.nlike.getType().equals(type)) {
                        return Restrictions.nlike(columnIndex, value);
                    }
                }

                if (CriterionOperator.le.getType().equals(type)) {
                    value = convertValue(value, dataType, DateUtils.TYPE_START);
                    return Restrictions.le(columnIndex, value);
                }
                if (CriterionOperator.lt.getType().equals(type)) {
                    value = convertValue(value, dataType, DateUtils.TYPE_START);
                    return Restrictions.lt(columnIndex, value);
                }
                if (CriterionOperator.eq.getType().equals(type)) {
                    value = convertValue(value, dataType);
                    return Restrictions.eq(columnIndex, value);
                }
                if (CriterionOperator.ne.getType().equals(type)) {
                    value = convertValue(value, dataType);
                    return Restrictions.ne(columnIndex, value);
                }
                if (CriterionOperator.ge.getType().equals(type)) {
                    value = convertValue(value, dataType, DateUtils.TYPE_END);
                    return Restrictions.ge(columnIndex, value);
                }
                if (CriterionOperator.gt.getType().equals(type)) {
                    value = convertValue(value, dataType, DateUtils.TYPE_END);
                    return Restrictions.gt(columnIndex, value);
                }

                if (CriterionOperator.is.getType().equals(type)) {
                    return Restrictions.is(columnIndex, value);
                }
            }
        } catch (IllegalArgumentException e) {

        }
        return Restrictions.sql("1<>1");
    }

    /**
     * @param customCriterion
     * @param columnIndex
     * @param value
     * @param type
     * @return
     */
    private static Criterion createCustomCriterion(String customCriterion, String columnIndex, Object value, String op) {
        AbstractCustomCriterion criterion = ApplicationContextHolder.getBean(customCriterion,
                AbstractCustomCriterion.class);
        Object[] initargs = new Object[]{columnIndex, value, op};
        try {
            Class<?> criterionClass = criterion.getClass();
            return (Criterion) (criterionClass.getConstructor(String.class, Object.class, String.class)
                    .newInstance(initargs));
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
        return null;
    }

    /**
     * 如何描述该方法
     *
     * @param criterionJson
     * @return
     */
    private static List<Object> getValueArray(Condition criterionJson) {
        Object valueObject = criterionJson.getValue();
        List<Object> valueList = new ArrayList<Object>();
        if (valueList != null && valueList.getClass().isArray()) {
            Object[] valueArray = (Object[]) valueObject;
            for (Object object : valueArray) {
                valueList.add(object);
            }
        } else if (valueObject instanceof Collection) {
            Collection<?> valueCollection = (Collection<?>) valueObject;
            for (Object object : valueCollection) {
                valueList.add(object);
            }
        } else if (valueObject instanceof String) {
            valueList.addAll(Arrays.asList(valueObject.toString().split(";")));
        } else {
            valueList.add(valueObject);
        }
        return valueList;
    }

    /**
     * @param value
     * @param dataType
     * @return
     */
    private static Object convertValue(Object value, DataType dataType, int type) {
        Object result = null;
        if (dataType.equals(DataType.T) && value != null) {
            try {
                result = DateUtils.parse((String) value, type);
            } catch (ParseException e) {
                result = convertValue(value, dataType);
            }
        } else {
            result = convertValue(value, dataType);
        }
        return result;
    }

    /**
     * @param value
     * @param dataType
     * @return
     */
    private static Object convertValue(Object value, DataType dataType) {
        if (value == null) {
            return value;
        }
        if (value instanceof String && value != null) {
            try {
                switch (dataType) {
                    case I:
                        return Integer.parseInt(value.toString());
                    case D:
                        return Double.parseDouble(value.toString());
                    case F:
                        return Float.parseFloat(value.toString());
                }
            } catch (Exception e) {
                LOG.error("数值转换异常: ", e);
                throw new IllegalArgumentException("无法识别的数字查询");
            }

        }

        if (dataType.equals(DataType.T)) {
            try {
                return DateUtils.parse(value.toString());
            } catch (ParseException e) {
                LOG.error("日期转换异常: ", e);
                throw new IllegalArgumentException("无法识别格式的日期查询");
            }
        }
        return value;
    }
}
