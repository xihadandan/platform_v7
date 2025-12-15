/*
 * @(#)11/23/23 V1.0
 *
 * Copyright 2023 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.biz.support;

import com.wellsoft.context.base.BaseObject;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.util.List;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 11/23/23.1	zhulh		11/23/23		Create
 * </pre>
 * @date 11/23/23
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class StateDefinition extends BaseObject {
    private static final long serialVersionUID = -2823979622204142796L;

    // ID，自动生成
    private String id;
    // 状态类型名称
    private String stateTypeName;
    // 状态名称字段
    private String stateNameField;
    // 状态代码字段
    private String stateCodeField;
    // 状态配置列表
    private List<StateConfig> stateConfigs;

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id 要设置的id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return the stateTypeName
     */
    public String getStateTypeName() {
        return stateTypeName;
    }

    /**
     * @param stateTypeName 要设置的stateTypeName
     */
    public void setStateTypeName(String stateTypeName) {
        this.stateTypeName = stateTypeName;
    }

    /**
     * @return the stateNameField
     */
    public String getStateNameField() {
        return stateNameField;
    }

    /**
     * @param stateNameField 要设置的stateNameField
     */
    public void setStateNameField(String stateNameField) {
        this.stateNameField = stateNameField;
    }

    /**
     * @return the stateCodeField
     */
    public String getStateCodeField() {
        return stateCodeField;
    }

    /**
     * @param stateCodeField 要设置的stateCodeField
     */
    public void setStateCodeField(String stateCodeField) {
        this.stateCodeField = stateCodeField;
    }

    /**
     * @return the stateConfigs
     */
    public List<StateConfig> getStateConfigs() {
        return stateConfigs;
    }

    /**
     * @param stateConfigs 要设置的stateConfigs
     */
    public void setStateConfigs(List<StateConfig> stateConfigs) {
        this.stateConfigs = stateConfigs;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class StateConfig extends BaseObject {
        private static final long serialVersionUID = -6150555107174243279L;

        // ID，自动生成
        private String id;
        // 状态名称脚本类型，1固定值，2freemarker表达式，3groovy脚本
        private String stateNameScriptType;
        // 状态名称值
        private String stateNameValue;
        // 状态代码脚本类型，1固定值，2freemarker表达式，3groovy脚本
        private String stateCodeScriptType;
        // 状态名称值
        private String stateCodeValue;
        // 状态触发类型
        private String triggerType;
        // 业务过程状态
        private String processState;
        // 过程节点名称
        private String processNodeName;
        // 过程节点编码
        private String processNodeCode;
        // 过程节点状态
        private String processNodeState;
        // 事项名称
        private String itemName;
        // 事项编码
        private String itemCode;
        // 事项状态
        private String itemState;
        // 事项事件ID
        private String itemEventId;
        // 状态计时事件ID
        private String timerEventId;

        // 冗余字段
        // 状态名称字段
        private String stateNameField;
        // 状态代码字段
        private String stateCodeField;

        /**
         * @return the id
         */
        public String getId() {
            return id;
        }

        /**
         * @param id 要设置的id
         */
        public void setId(String id) {
            this.id = id;
        }

        /**
         * @return the stateNameScriptType
         */
        public String getStateNameScriptType() {
            return stateNameScriptType;
        }

        /**
         * @param stateNameScriptType 要设置的stateNameScriptType
         */
        public void setStateNameScriptType(String stateNameScriptType) {
            this.stateNameScriptType = stateNameScriptType;
        }

        /**
         * @return the stateNameValue
         */
        public String getStateNameValue() {
            return stateNameValue;
        }

        /**
         * @param stateNameValue 要设置的stateNameValue
         */
        public void setStateNameValue(String stateNameValue) {
            this.stateNameValue = stateNameValue;
        }

        /**
         * @return the stateCodeScriptType
         */
        public String getStateCodeScriptType() {
            return stateCodeScriptType;
        }

        /**
         * @param stateCodeScriptType 要设置的stateCodeScriptType
         */
        public void setStateCodeScriptType(String stateCodeScriptType) {
            this.stateCodeScriptType = stateCodeScriptType;
        }

        /**
         * @return the stateCodeValue
         */
        public String getStateCodeValue() {
            return stateCodeValue;
        }

        /**
         * @param stateCodeValue 要设置的stateCodeValue
         */
        public void setStateCodeValue(String stateCodeValue) {
            this.stateCodeValue = stateCodeValue;
        }

        /**
         * @return the triggerType
         */
        public String getTriggerType() {
            return triggerType;
        }

        /**
         * @param triggerType 要设置的triggerType
         */
        public void setTriggerType(String triggerType) {
            this.triggerType = triggerType;
        }

        /**
         * @return the processState
         */
        public String getProcessState() {
            return processState;
        }

        /**
         * @param processState 要设置的processState
         */
        public void setProcessState(String processState) {
            this.processState = processState;
        }

        /**
         * @return the processNodeName
         */
        public String getProcessNodeName() {
            return processNodeName;
        }

        /**
         * @param processNodeName 要设置的processNodeName
         */
        public void setProcessNodeName(String processNodeName) {
            this.processNodeName = processNodeName;
        }

        /**
         * @return the processNodeCode
         */
        public String getProcessNodeCode() {
            return processNodeCode;
        }

        /**
         * @param processNodeCode 要设置的processNodeCode
         */
        public void setProcessNodeCode(String processNodeCode) {
            this.processNodeCode = processNodeCode;
        }

        /**
         * @return the processNodeState
         */
        public String getProcessNodeState() {
            return processNodeState;
        }

        /**
         * @param processNodeState 要设置的processNodeState
         */
        public void setProcessNodeState(String processNodeState) {
            this.processNodeState = processNodeState;
        }

        /**
         * @return the itemName
         */
        public String getItemName() {
            return itemName;
        }

        /**
         * @param itemName 要设置的itemName
         */
        public void setItemName(String itemName) {
            this.itemName = itemName;
        }

        /**
         * @return the itemCode
         */
        public String getItemCode() {
            return itemCode;
        }

        /**
         * @param itemCode 要设置的itemCode
         */
        public void setItemCode(String itemCode) {
            this.itemCode = itemCode;
        }

        /**
         * @return the itemState
         */
        public String getItemState() {
            return itemState;
        }

        /**
         * @param itemState 要设置的itemState
         */
        public void setItemState(String itemState) {
            this.itemState = itemState;
        }

        /**
         * @return the itemEventId
         */
        public String getItemEventId() {
            return itemEventId;
        }

        /**
         * @param itemEventId 要设置的itemEventId
         */
        public void setItemEventId(String itemEventId) {
            this.itemEventId = itemEventId;
        }

        /**
         * @return the timerEventId
         */
        public String getTimerEventId() {
            return timerEventId;
        }

        /**
         * @param timerEventId 要设置的timerEventId
         */
        public void setTimerEventId(String timerEventId) {
            this.timerEventId = timerEventId;
        }

        /**
         * @return the stateNameField
         */
        public String getStateNameField() {
            return stateNameField;
        }

        /**
         * @param stateNameField 要设置的stateNameField
         */
        public void setStateNameField(String stateNameField) {
            this.stateNameField = stateNameField;
        }

        /**
         * @return the stateCodeField
         */
        public String getStateCodeField() {
            return stateCodeField;
        }

        /**
         * @param stateCodeField 要设置的stateCodeField
         */
        public void setStateCodeField(String stateCodeField) {
            this.stateCodeField = stateCodeField;
        }
    }

}
