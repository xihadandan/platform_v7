/*
 * @(#)2016年2月1日 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.common.validation.support;

import com.wellsoft.context.util.i18n.MsgUtils;

import java.io.Serializable;

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
public class FieldMetaData implements Serializable {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 2140697212798020482L;

    private String name;

    private String rule;

    private String message;

    private Object ruleValue;

    private int min;

    private int max;

    /**
     *
     */
    public FieldMetaData() {
    }

    /**
     * @param name
     * @param rule
     * @param message
     */
    public FieldMetaData(String name, String rule, String message) {
        super();
        this.name = name;
        this.rule = rule;
        this.message = message;
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
     * @return the rule
     */
    public String getRule() {
        return rule;
    }

    /**
     * @param rule 要设置的rule
     */
    public void setRule(String rule) {
        this.rule = rule;
    }

    /**
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * @param message 要设置的message
     */
    public void setMessage(String message) {
        this.message = message;
    }

    public String getJsMessage() {
        String msg = message;
        if (msg.startsWith("{")) {
            msg = msg.substring(1);
        }
        if (msg.endsWith("}")) {
            msg = msg.substring(0, msg.length() - 1);
        }
        msg = MsgUtils.getMessage(msg);
        if (msg.indexOf("{min}") != -1 && msg.indexOf("{max}") != -1) {
            msg = msg.replace("{min}", "{0}");
            msg = msg.replace("{max}", "{1}");
        } else {
            msg = msg.replace("{min}", "{0}");
            msg = msg.replace("{max}", "{0}");
        }
        return msg;
    }

    /**
     * @return the ruleValue
     */
    public Object getRuleValue() {
        return ruleValue;
    }

    /**
     * @param ruleValue 要设置的ruleValue
     */
    public void setRuleValue(Object ruleValue) {
        this.ruleValue = ruleValue;
    }

    /**
     * @return the min
     */
    public int getMin() {
        return min;
    }

    /**
     * @param min 要设置的min
     */
    public void setMin(int min) {
        this.min = min;
    }

    /**
     * @return the max
     */
    public int getMax() {
        return max;
    }

    /**
     * @param max 要设置的max
     */
    public void setMax(int max) {
        this.max = max;
    }

}
