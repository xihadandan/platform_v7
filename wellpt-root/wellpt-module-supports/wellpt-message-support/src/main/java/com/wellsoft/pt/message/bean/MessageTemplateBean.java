/*
 * @(#)2013-1-25 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.message.bean;

import com.wellsoft.pt.message.entity.MessageTemplate;
import com.wellsoft.pt.message.entity.WebServiceParm;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Description: 消息模板VO类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-1-25.1	zhulh		2013-1-25		Create
 * </pre>
 * @date 2013-1-25
 */
public class MessageTemplateBean extends MessageTemplate {

    private static final long serialVersionUID = -8835215992953826397L;

    private List<String> sendWays = new ArrayList<String>();

    private Set<WebServiceParm> changedChildren = new HashSet<WebServiceParm>();
    private Set<WebServiceParm> deletedChildren = new HashSet<WebServiceParm>();

    // 产品集成信息UUID
    private String piUuid;
    // 产品集成信息名称
    // @NotBlank
    private String piName;

    /**
     * @return the sendWays
     */
    public List<String> getSendWays() {
        return sendWays;
    }

    /**
     * @param sendWays 要设置的sendWays
     */
    public void setSendWays(List<String> sendWays) {
        this.sendWays = sendWays;
    }

    public Set<WebServiceParm> getChangedChildren() {
        return changedChildren;
    }

    public void setChangedChildren(Set<WebServiceParm> changedChildren) {
        this.changedChildren = changedChildren;
    }

    public Set<WebServiceParm> getDeletedChildren() {
        return deletedChildren;
    }

    public void setDeletedChildren(Set<WebServiceParm> deletedChildren) {
        this.deletedChildren = deletedChildren;
    }

    /**
     * @return the piUuid
     */
    public String getPiUuid() {
        return piUuid;
    }

    /**
     * @param piUuid 要设置的piUuid
     */
    public void setPiUuid(String piUuid) {
        this.piUuid = piUuid;
    }

    /**
     * @return the piName
     */
    public String getPiName() {
        return piName;
    }

    /**
     * @param piName 要设置的piName
     */
    public void setPiName(String piName) {
        this.piName = piName;
    }

}
