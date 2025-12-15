/*
 * @(#)2014-4-24 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.element;

import com.wellsoft.pt.bpm.engine.constant.FlowConstant;
import com.wellsoft.pt.rule.engine.suport.Operator;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.io.Serializable;
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
 * 2014-4-24.1	zhulh		2014-4-24		Create
 * </pre>
 * @date 2014-4-24
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class MessageTemplateElement implements Serializable {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = -2764868684584735211L;

    // 类型
    private String type;
    // 类型名称
    private String typeName;
    // 模板ID
    private String id;
    // 模板名称
    private String name;

    /*add by huanglinchuan 2014.10.21 begin*/
    //消息分发设置，不同的消息类型，要增加控制开关，除了默认的分发对象外海可以指定额外的分发对象，指定消息模板，也需要支持指定环节进行发送功能
    //是否消息分发，默认分发
    private String isSendMsg;
    // 环节约束条件
    private String condition;
    //额外的分发对象
    private String extraMsgRecipients;
    private String extraMsgRecipientUserIds;
    //额外的自定义分发对象
    private String extraMsgCustomRecipients;
    private String extraMsgCustomRecipientUserIds;
    /*add by huanglinchuan 2014.10.21 end*/
    private List<UserUnitElement> copyMsgRecipients;  //  抄送人员

    //启用条件
    private String conditionEnable;
    // 条件的连接符
    private String condExpressionSignal;
    //分发人员
    private List<MessageDistributerElement> distributerElements;
    //分发节点
    private List<MessageDistributionElement> distributionElements;
    private List<ConditionElement> conditionElements;


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
     * @return the typeName
     */
    public String getTypeName() {
        return typeName;
    }

    /**
     * @param typeName 要设置的typeName
     */
    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

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

    public String getIsSendMsg() {
        return isSendMsg;
    }

    public void setIsSendMsg(String isSendMsg) {
        this.isSendMsg = isSendMsg;
    }

    /**
     * @return the condition
     */
    public String getCondition() {
        return condition;
    }

    /**
     * @param condition 要设置的condition
     */
    public void setCondition(String condition) {
        this.condition = condition;
    }

    public String getExtraMsgRecipients() {
        return extraMsgRecipients;
    }

    public void setExtraMsgRecipients(String extraMsgRecipients) {
        this.extraMsgRecipients = extraMsgRecipients;
    }

    public String getExtraMsgRecipientUserIds() {
        return extraMsgRecipientUserIds;
    }

    public void setExtraMsgRecipientUserIds(String extraMsgRecipientUserIds) {
        this.extraMsgRecipientUserIds = extraMsgRecipientUserIds;
    }

    /**
     * @return the extraMsgCustomRecipients
     */
    public String getExtraMsgCustomRecipients() {
        return extraMsgCustomRecipients;
    }

    /**
     * @param extraMsgCustomRecipients 要设置的extraMsgCustomRecipients
     */
    public void setExtraMsgCustomRecipients(String extraMsgCustomRecipients) {
        this.extraMsgCustomRecipients = extraMsgCustomRecipients;
    }

    /**
     * @return the extraMsgCustomRecipientUserIds
     */
    public String getExtraMsgCustomRecipientUserIds() {
        return extraMsgCustomRecipientUserIds;
    }

    /**
     * @param extraMsgCustomRecipientUserIds 要设置的extraMsgCustomRecipientUserIds
     */
    public void setExtraMsgCustomRecipientUserIds(String extraMsgCustomRecipientUserIds) {
        this.extraMsgCustomRecipientUserIds = extraMsgCustomRecipientUserIds;
    }


    public List<MessageDistributerElement> getDistributerElements() {
        return distributerElements;
    }

    public void setDistributerElements(List<MessageDistributerElement> distributerElements) {
        this.distributerElements = distributerElements;
    }

    public List<MessageDistributionElement> getDistributionElements() {
        return distributionElements;
    }

    public void setDistributionElements(List<MessageDistributionElement> distributionElements) {
        this.distributionElements = distributionElements;
    }

    public String getConditionEnable() {
        return conditionEnable;
    }

    public void setConditionEnable(String conditionEnable) {
        this.conditionEnable = conditionEnable;
    }

    public String getCondExpressionSignal() {
        return condExpressionSignal;
    }

    public void setCondExpressionSignal(String condExpressionSignal) {
        this.condExpressionSignal = condExpressionSignal;
    }

    public List<ConditionElement> getConditionElements() {
        return conditionElements;
    }

    public void setConditionElements(List<ConditionElement> conditionElements) {
        this.conditionElements = conditionElements;
    }

    @JsonIgnore
    public String getConditionElementString() {
        StringBuilder conditionBuilder = new StringBuilder("");
        // 分发节点
        if (CollectionUtils.isNotEmpty(this.distributionElements)) {
            for (int i = 0, size = this.distributionElements.size(); i < size; i++) {
                MessageDistributionElement distributionElement = this.distributionElements.get(i);
                String type = distributionElement.getType();
                String value = distributionElement.getValue();
                if ("0".equals(type) || "all".equals(type)) {
                    continue;
                }
                if (StringUtils.isBlank(type) || StringUtils.isBlank(distributionElement.getValue())) {
                    continue;
                }
                boolean notlast = i != (size - 1);
                String[] parts = value.split(",|;");
                if ("1".equals(type) || "task".equals(type)) {
                    for (int j = 0, jsize = parts.length; j < jsize; j++) {
                        //指定环节
                        conditionBuilder.append("${taskId}=='" + parts[j] + "'" + ((j != (jsize - 1)) ? " || " : ""));
                    }
                    if (notlast) { // 连接下一个分发节点的判断
                        conditionBuilder.append(" || ");
                    }

                } else if ("2".equals(type) || "direction".equals(type)) {
                    //指定流向
                    for (int j = 0, jsize = parts.length; j < jsize; j++) {
                        conditionBuilder.append("${directionId}=='" + parts[j] + "'" + ((j != (jsize - 1)) ? " || " : ""));
                    }
                    if (notlast) { // 连接下一个分发节点的判断
                        conditionBuilder.append(" || ");
                    }

                } else if ("3".equals(type) || "jumptask".equals(type)) {
                    if (parts.length != 2) {
                        continue;
                    }
                    //环节跳转
                    String toTaskId = parts[1];
                    if (StringUtils.equals(FlowConstant.END_FLOW_ID, toTaskId)) {
                        toTaskId = FlowConstant.END_FLOW;
                    }
                    conditionBuilder.append("(${fromTaskId}=='" + parts[0] + "' && ${toTaskId}=='" + toTaskId + "'" + (notlast ? ") || " : ")"));
                }
            }
            if (conditionBuilder.length() > 0) {
                conditionBuilder.insert(0, "(");
                conditionBuilder.append(")");
                // 以上结果类似： ( ${taskId}=='1' || ${directionId}=='2' || (${formTaskId}=='3' && ${toTaskId}=='4') )
            }
        }

        // 分发条件
        if ("0".equals(this.conditionEnable)) {
            return conditionBuilder.toString();
        }

        if (CollectionUtils.isNotEmpty(this.conditionElements)) {
            StringBuilder fieldCondtionBuilder = new StringBuilder("");
            String signal = "and".equals(this.condExpressionSignal) ? "&&" : "||";
            for (int i = 0, size = this.conditionElements.size(); i < size; i++) {
                ConditionElement conditionElement = this.conditionElements.get(i);
                String ctype = conditionElement.getType();
                String code = conditionElement.getCode();
                String symbols = Operator.getName(conditionElement.getSymbols());
                if (symbols == null) {
                    continue;
                }
                String value = conditionElement.getValue();
                if ("0".equals(ctype) || "formField".equals(ctype)) {// 表单字段值
                    String stringFieldCondition = "${dyform." + code + "} " + symbols + " '" + value + "'";
                    String numberFieldCondition = "${dyform." + code + "} " + symbols + " " + value + "";
                    fieldCondtionBuilder.append("(" + stringFieldCondition + " || " + numberFieldCondition + ")" + ((i != size - 1) ? " " + signal + " " : ""));
                } else if ("1".equals(type) || "userComment".equals(ctype)) {// 用户意见内容
                    String stringFieldCondition = "${dyform.opinionText} " + symbols + " '" + value + "'";
                    fieldCondtionBuilder.append("(" + stringFieldCondition + ")" + ((i != size - 1) ? " " + signal + " " : ""));
                }
            }
            if (conditionBuilder.length() > 0) {
                conditionBuilder.append(" && (");
                fieldCondtionBuilder.append(")");
                conditionBuilder.append(fieldCondtionBuilder.toString());
            } else {
                conditionBuilder.append(fieldCondtionBuilder.toString());
            }
        }
        return conditionBuilder.toString();
    }

    public List<UserUnitElement> getCopyMsgRecipients() {
        return copyMsgRecipients;
    }

    public void setCopyMsgRecipients(List<UserUnitElement> copyMsgRecipients) {
        this.copyMsgRecipients = copyMsgRecipients;
    }
}
