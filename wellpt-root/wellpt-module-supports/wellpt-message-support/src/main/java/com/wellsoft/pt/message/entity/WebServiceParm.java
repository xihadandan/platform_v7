/*
 * @(#)2012-11-14 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.message.entity;

import com.wellsoft.context.annotation.UnCloneable;
import com.wellsoft.context.jdbc.entity.TenantEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * Description: 消息-webservice 参数
 *
 * @author tony
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 2014-10-20.1	tony		2014-10-20		Create
 * </pre>
 * @date 2014-10-20
 */
@Entity
@Table(name = "msg_webservice_parm")
@DynamicUpdate
@DynamicInsert
public class WebServiceParm extends TenantEntity {

    private static final long serialVersionUID = 3865909809415107871L;
    // jqGrid的行标识
    private String id;
    /**
     * 参数名称
     */
    private String parmName;

    /**
     * 参数值
     */
    private String parmValue;

    /**
     * 参数说明
     */
    private String parmdesc;

    /**
     * 所属消息
     */
    @UnCloneable
    private MessageTemplate messageTemplate;

    public String getParmName() {
        return parmName;
    }

    public void setParmName(String parmName) {
        this.parmName = parmName;
    }

    public String getParmValue() {
        return parmValue;
    }

    public void setParmValue(String parmValue) {
        this.parmValue = parmValue;
    }

    public String getParmdesc() {
        return parmdesc;
    }

    public void setParmdesc(String parmdesc) {
        this.parmdesc = parmdesc;
    }

    @ManyToOne
    @JoinColumn(name = "message_template_uuid")
    public MessageTemplate getMessageTemplate() {
        return messageTemplate;
    }

    public void setMessageTemplate(MessageTemplate messageTemplate) {
        this.messageTemplate = messageTemplate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        WebServiceParm other = (WebServiceParm) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }

}
