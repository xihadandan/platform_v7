/*
 * @(#)2014-4-25 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.support;

import com.wellsoft.pt.bpm.engine.element.UserUnitElement;

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
 * 2014-4-25.1	zhulh		2014-4-25		Create
 * </pre>
 * @date 2014-4-25
 */
public class MessageTemplate {
    public static final String SEND_MSG = "1";

    // 类型
    private String type;
    // 类型名称
    private String typeName;
    // 模板ID
    private String id;
    // 模板名称
    private String name;
    // 约束条件
    private String condition;

    /*add by huanglinchuan 2014.10.21 begin*/
    private String isSendMsg;

    private List<UserUnitElement> msgRecipients;// 分发人员

    //额外的分发对象
    private List<UserUnitElement> extraMsgRecipients;

    public String getIsSendMsg() {
        return isSendMsg;
    }

    public void setIsSendMsg(String isSendMsg) {
        this.isSendMsg = isSendMsg;
    }

    public List<UserUnitElement> getExtraMsgRecipients() {
        return extraMsgRecipients;
    }

    public void setExtraMsgRecipients(List<UserUnitElement> extraMsgRecipients) {
        this.extraMsgRecipients = extraMsgRecipients;
    }

    /*add by huanglinchuan 2014.10.21 end*/

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

    public List<UserUnitElement> getMsgRecipients() {
        return msgRecipients;
    }

    public void setMsgRecipients(List<UserUnitElement> msgRecipients) {
        this.msgRecipients = msgRecipients;
    }
}
