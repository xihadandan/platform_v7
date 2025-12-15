/*
 * @(#)2016年10月25日 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.jpa.criterion;

import java.util.Collection;
import java.util.Map;

/**
 * Description: 如何描述该类
 *
 * @author xiem
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016年10月25日.1	xiem		2016年10月25日		Create
 * </pre>
 * @date 2016年10月25日
 */
public class Restrictions {

    /**
     * 生成like查询条件(匹配方法MatchMode.ANYWHERE)
     *
     * @param columnIndex 列索引
     * @param value       值
     * @return
     */
    public static Criterion like(String columnIndex, Object value) {
        if (value == null) {
            throw new IllegalArgumentException("不能用空值进行模糊查询!");
            // throw new IllegalArgumentException("Comparison value passed to ilike cannot be null");
        }
        return like(columnIndex, value.toString(), MatchMode.ANYWHERE);
    }

    /**
     * 生成not like查询条件(匹配方法MatchMode.ANYWHERE)
     *
     * @param columnIndex 列索引
     * @param value       值
     * @return
     */
    public static Criterion nlike(String columnIndex, Object value) {
        return nlike(columnIndex, value.toString(), MatchMode.ANYWHERE);
    }

    /**
     * 生成like查询条件(根据MatchMode指定匹配方法)
     *
     * @param columnIndex 列索引
     * @param value       值
     * @param matchMode   匹配模式
     * @return
     */
    public static Criterion like(String columnIndex, String value, MatchMode matchMode) {
        if (value == null) {
            throw new IllegalArgumentException("不能用空值进行模糊查询!");
            // throw new IllegalArgumentException("Comparison value passed to ilike cannot be null");
        }
        return new LikeExpression(columnIndex, value, matchMode);
    }

    /**
     * 生成not like查询条件(根据MatchMode指定匹配方法)
     *
     * @param columnIndex 列索引
     * @param value       值
     * @param matchMode   匹配模式
     * @return
     */
    public static Criterion nlike(String columnIndex, String value, MatchMode matchMode) {
        if (value == null) {
            throw new IllegalArgumentException("不能用空值进行模糊查询!");
            // throw new IllegalArgumentException("Comparison value passed to ilike cannot be null");
        }
        return new NotLikeExpression(columnIndex, value, matchMode);
    }

    /**
     * 生成like查询条件(根据MatchMode指定匹配方法)
     *
     * @param columnIndex 列索引
     * @param value       值
     * @param matchMode   匹配模式
     * @param ignoreCase  是否忽略大小写
     * @return
     */
    public static Criterion like(String columnIndex, String value, MatchMode matchMode, boolean ignoreCase) {
        if (value == null) {
            throw new IllegalArgumentException("不能用空值进行模糊查询!");
            // throw new IllegalArgumentException("Comparison value passed to ilike cannot be null");
        }
        return new LikeExpression(columnIndex, value, matchMode, ignoreCase);
    }

    /**
     * 生成not like查询条件(根据MatchMode指定匹配方法)
     *
     * @param columnIndex 列索引
     * @param value       值
     * @param matchMode   匹配模式
     * @param ignoreCase  是否忽略大小写
     * @return
     */
    public static Criterion nlike(String columnIndex, String value, MatchMode matchMode, boolean ignoreCase) {
        if (value == null) {
            throw new IllegalArgumentException("不能用空值进行模糊查询!");
            // throw new IllegalArgumentException("Comparison value passed to ilike cannot be null");
        }
        return new NotLikeExpression(columnIndex, value, matchMode, ignoreCase);
    }

    /**
     * 给查询条件包装not 前缀
     *
     * @param expression 被包装查询条件
     * @return
     */
    public static Criterion not(Criterion expression) {
        return new NotExpression(expression);
    }

    /**
     * 指定列大于某值查询条件
     *
     * @param columnIndex 列索引
     * @param value       值
     * @return
     */
    public static SimpleExpression gt(String columnIndex, Object value) {
        return new SimpleExpression(columnIndex, value, ">");
    }

    /**
     * 指定列小于某值查询条件
     *
     * @param columnIndex 列索引
     * @param value       值
     * @return
     */
    public static SimpleExpression lt(String columnIndex, Object value) {
        return new SimpleExpression(columnIndex, value, "<");
    }

    /**
     * 指定列小于等于某值查询条件
     *
     * @param columnIndex 列索引
     * @param value       值
     * @return
     */
    public static SimpleExpression le(String columnIndex, Object value) {
        return new SimpleExpression(columnIndex, value, "<=");
    }

    /**
     * 指定列大于等于某值查询条件
     *
     * @param columnIndex 列索引
     * @param value       值
     * @return
     */
    public static SimpleExpression ge(String columnIndex, Object value) {
        return new SimpleExpression(columnIndex, value, ">=");
    }

    /**
     * 指定列等于某值查询条件
     *
     * @param columnIndex 列索引
     * @param value       值
     * @return
     */
    public static SimpleExpression eq(String columnIndex, Object value) {
        return new SimpleExpression(columnIndex, value, "=");
    }

    /**
     * @param columnIndex
     * @param value
     * @return
     */
    public static SimpleExpression ne(String columnIndex, Object value) {
        return new SimpleExpression(columnIndex, value, "<>");
    }

    /**
     * 判断某列值为空查询条件
     *
     * @param columnIndex 列索引
     * @return
     */
    public static Criterion isNull(String columnIndex) {
        return new NullExpression(columnIndex);
    }

    /**
     * 判断某列值不为空查询条件
     *
     * @param columnIndex 列索引
     * @return
     */
    public static Criterion isNotNull(String columnIndex) {
        return new NotNullExpression(columnIndex);
    }

    /**
     * @param columnIndex
     * @param value
     * @return
     */
    public static Criterion is(String columnIndex, Object value) {
        return new IsExpression(columnIndex, value.toString());
    }

    /**
     * value为空时，某列值为空，否则等于该值
     *
     * @param columnIndex 列索引
     * @param value       值
     * @return
     */
    public static Criterion eqOrIsNull(String columnIndex, Object value) {
        return value == null ? isNull(columnIndex) : eq(columnIndex, value);
    }

    /**
     * 列的值等于另一列的值
     *
     * @param columnIndex      列索引
     * @param otherColumnIndex 另一列索引
     * @return
     */
    public static PropertyExpression eqProperty(String columnIndex, String otherColumnIndex) {
        return new PropertyExpression(columnIndex, otherColumnIndex, "=");
    }

    /**
     * 列的值不等于另一列的值
     *
     * @param columnIndex      列索引
     * @param otherColumnIndex 另一列索引
     * @return
     */
    public static PropertyExpression neProperty(String columnIndex, String otherColumnIndex) {
        return new PropertyExpression(columnIndex, otherColumnIndex, "<>");
    }

    /**
     * 列的值小于另一列的值
     *
     * @param columnIndex      列索引
     * @param otherColumnIndex 另一列索引
     * @return
     */
    public static PropertyExpression ltProperty(String columnIndex, String otherColumnIndex) {
        return new PropertyExpression(columnIndex, otherColumnIndex, "<");
    }

    /**
     * 列的值小于等于另一列的值
     *
     * @param columnIndex      列索引
     * @param otherColumnIndex 另一列索引
     * @return
     */
    public static PropertyExpression leProperty(String columnIndex, String otherColumnIndex) {
        return new PropertyExpression(columnIndex, otherColumnIndex, "<=");
    }

    /**
     * 列的值大于另一列的值
     *
     * @param columnIndex      列索引
     * @param otherColumnIndex 另一列索引
     * @return
     */
    public static PropertyExpression gtProperty(String columnIndex, String otherColumnIndex) {
        return new PropertyExpression(columnIndex, otherColumnIndex, ">");
    }

    /**
     * 列的值大于等于另一列的值
     *
     * @param columnIndex      列索引
     * @param otherColumnIndex 另一列索引
     * @return
     */
    public static PropertyExpression geProperty(String columnIndex, String otherColumnIndex) {
        return new PropertyExpression(columnIndex, otherColumnIndex, ">=");
    }

    /**
     * 生成AND关联表达式
     *
     * @param predicates 查询条件
     * @return
     */
    public static Conjunction and(Criterion... predicates) {
        return conjunction(predicates);
    }

    /**
     * 生成OR关联表达式
     *
     * @param predicates 查询条件
     * @return
     */
    public static Disjunction or(Criterion... predicates) {
        return disjunction(predicates);
    }

    /**
     * 生产And关联表达式
     *
     * @param conditions 查询条件
     * @return
     */
    public static Conjunction conjunction(Criterion... conditions) {
        return new Conjunction(conditions);
    }

    /**
     * 生产OR关联表达式
     *
     * @param conditions 查询条件
     * @return
     */
    public static Disjunction disjunction(Criterion... conditions) {
        return new Disjunction(conditions);
    }

    /**
     * 生产And关联表达式
     *
     * @return
     */
    public static Conjunction conjunction() {
        return new Conjunction();
    }

    /**
     * 生产OR关联表达式
     *
     * @param conditions 查询条件
     * @return
     */
    public static Disjunction disjunction() {
        return new Disjunction();
    }

    /**
     * 映射中的Key对应的列得值与值一一相等
     *
     * @param columnIndexValues 列索引 值 的键值对
     * @return
     */
    public static Criterion allEq(Map<String, ?> columnIndexValues) {
        final Conjunction conj = conjunction();

        for (Map.Entry<String, ?> entry : columnIndexValues.entrySet()) {
            conj.add(eq(entry.getKey(), entry.getValue()));
        }
        return conj;
    }

    /**
     * 列对应的值在两值之间
     *
     * @param columnIndex 列索引
     * @param lo          开始值
     * @param hi          结束值
     * @return
     */
    public static Criterion between(String columnIndex, Object lo, Object hi) {
        return new BetweenExpression(columnIndex, lo, hi);
    }

    /**
     * 生成in查询条件
     *
     * @param columnIndex 列索引
     * @param values      值
     * @return
     */
    public static Criterion in(String columnIndex, Object[] values) {
        return new InExpression(columnIndex, values);
    }

    /**
     * 生成in查询条件
     *
     * @param columnIndex 列索引
     * @param values      值
     * @return
     */
    public static Criterion in(String columnIndex, Collection<?> values) {
        return new InExpression(columnIndex, values.toArray());
    }

    /**
     * 生成not in查询条件
     *
     * @param columnIndex 列索引
     * @param values      值
     * @return
     */
    public static Criterion notIn(String columnIndex, Object[] values) {
        return not(in(columnIndex, values));
    }

    /**
     * 生成not in查询条件
     *
     * @param columnIndex 列索引
     * @param values      值
     * @return
     */
    public static Criterion notIn(String columnIndex, Collection<?> values) {
        return not(in(columnIndex, values));
    }

    /**
     * 生成not in查询条件
     *
     * @param columnIndex 列索引
     * @param values      值
     * @return
     */
    public static Criterion sql(String sql) {
        return new SqlExpression(sql);
    }

    /**
     * 生成not in查询条件
     *
     * @param columnIndex 列索引
     * @param values      值
     * @return
     */
    public static Criterion sql(String sql, Map<String, Object> params) {
        return new SqlExpression(sql, params);
    }

}
