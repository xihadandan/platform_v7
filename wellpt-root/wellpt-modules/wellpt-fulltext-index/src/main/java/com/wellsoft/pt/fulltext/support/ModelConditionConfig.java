/*
 * @(#)6/23/25 V1.0
 *
 * Copyright 2025 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.fulltext.support;

import com.wellsoft.context.base.BaseObject;
import io.swagger.annotations.ApiModelProperty;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.util.List;

/**
 * Description:
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 6/23/25.1	    zhulh		6/23/25		    Create
 * </pre>
 * @date 6/23/25
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ModelConditionConfig extends BaseObject {

    public static final String MATCH_ALL = "all";

    @ApiModelProperty("匹配方式，all满足全部条件、any满足任一条件")
    private String match;

    @ApiModelProperty("条件列表")
    private List<FormFieldCondition> conditions;

    /**
     * @return the match
     */
    public String getMatch() {
        return match;
    }

    /**
     * @param match 要设置的match
     */
    public void setMatch(String match) {
        this.match = match;
    }

    /**
     * @return the conditions
     */
    public List<FormFieldCondition> getConditions() {
        return conditions;
    }

    /**
     * @param conditions 要设置的conditions
     */
    public void setConditions(List<FormFieldCondition> conditions) {
        this.conditions = conditions;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class FormFieldCondition extends BaseObject {

        @ApiModelProperty("条件代码，字段名或其他定义的名称")
        private String code;

        @ApiModelProperty("操作符")
        private String operator;

        @ApiModelProperty("条件值")
        private String value;

        /**
         * @return the code
         */
        public String getCode() {
            return code;
        }

        /**
         * @param code 要设置的code
         */
        public void setCode(String code) {
            this.code = code;
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
         * @return the value
         */
        public String getValue() {
            return value;
        }

        /**
         * @param value 要设置的value
         */
        public void setValue(String value) {
            this.value = value;
        }
    }
}
