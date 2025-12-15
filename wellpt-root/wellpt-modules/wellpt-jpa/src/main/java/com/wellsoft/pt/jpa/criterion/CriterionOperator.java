/*
 * @(#)2016年10月28日 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.jpa.criterion;

/**
 * Description: 如何描述该类
 *
 * @author xiem
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016年10月28日.1	xiem		2016年10月28日		Create
 * </pre>
 * @date 2016年10月28日
 */
public enum CriterionOperator {
    lt("lt", "<", "小于"), le("le", "<=", "小于等于"), gt("gt", ">", "大于"), ge("ge", ">=", "大于等于"), eq("eq", "=", "等于"), ne(
            "ne", "<>", "不等于"), like("like", "like", "包含(like)"), nlike("nlike", "not like", "不包含(not like)"), bw(
            "between", "between", "区间"), and("and", "and", "与连接", CriterionOperator.JUNCTION), or("or", "or", "或连接",
            CriterionOperator.JUNCTION), in("in", "in", "In查询"), notIn("not in", "not in", "notIn查询"), ISNULL("is null", "is null", "空值查询"), ISNOTNULL(
            "is not null", "is not null", "非空查询"), is("is", "is", "Is查询"), exists("exists", "exists", "存在(exists)");
    public final static int BASE = 1;
    public final static int JUNCTION = 2;
    public final static int COMPLEX = 3;
    private String type;
    private String operator;
    private String name;
    private int model;

    private CriterionOperator(String type, String operator, String name) {
        this(type, operator, name, BASE);
    }

    private CriterionOperator(String type, String operator, String name, int model) {
        this.setOperator(operator);
        this.setName(name);
        this.setType(type);
        this.setModel(model);
    }

    /**
     * @param type
     * @return
     */
    public static String getOperator(String type) {
        for (CriterionOperator operator : CriterionOperator.values()) {
            if (operator.getType().equals(type)) {
                return operator.getOperator();
            }
        }
        return null;
    }

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type 要设置的type
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return the operator
     */
    public String getOperator() {
        return operator;
    }

    /**
     * @param operator 要设置的operator
     */
    public void setOperator(String operator) {
        this.operator = operator;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name 要设置的name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the model
     */
    public int getModel() {
        return model;
    }

    /**
     * @param model 要设置的model
     */
    public void setModel(int model) {
        this.model = model;
    }

}
