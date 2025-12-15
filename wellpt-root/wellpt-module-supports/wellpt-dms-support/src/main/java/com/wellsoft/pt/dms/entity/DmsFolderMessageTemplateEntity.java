/*
 * @(#)Dec 15, 2017 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.entity;

import com.wellsoft.context.jdbc.entity.IdEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * Dec 15, 2017.1	zhulh		Dec 15, 2017		Create
 * </pre>
 * @date Dec 15, 2017
 */
@Entity
@Table(name = "DMS_FOLDER_MESSAGE_TEMPLATE")
@DynamicUpdate
@DynamicInsert
public class DmsFolderMessageTemplateEntity extends IdEntity {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = -753013550006398682L;

    // 夹UUID
    private String folderUuid;
    // 消息模板ID
    private String templateId;
    // 发送消息对应的操作ID
    private String actionId;
    // 消息通知对象
    private String noticeObjects;

    /**
     * @return the folderUuid
     */
    public String getFolderUuid() {
        return folderUuid;
    }

    /**
     * @param folderUuid 要设置的folderUuid
     */
    public void setFolderUuid(String folderUuid) {
        this.folderUuid = folderUuid;
    }

    /**
     * @return the templateId
     */
    public String getTemplateId() {
        return templateId;
    }

    /**
     * @param templateId 要设置的templateId
     */
    public void setTemplateId(String templateId) {
        this.templateId = templateId;
    }

    /**
     * @return the actionId
     */
    public String getActionId() {
        return actionId;
    }

    /**
     * @param actionId 要设置的actionId
     */
    public void setActionId(String actionId) {
        this.actionId = actionId;
    }

    /**
     * @return the noticeObjects
     */
    public String getNoticeObjects() {
        return noticeObjects;
    }

    /**
     * @param noticeObjects 要设置的noticeObjects
     */
    public void setNoticeObjects(String noticeObjects) {
        this.noticeObjects = noticeObjects;
    }

}
