/*
 * @(#)Jan 3, 2018 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.model;

import com.wellsoft.context.base.BaseObject;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * Jan 3, 2018.1	zhulh		Jan 3, 2018		Create
 * </pre>
 * @date Jan 3, 2018
 */
public class DmsFolder extends BaseObject {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = -3264724902993415971L;

    // 夹UUID
    private String uuid;
    // 夹名称
    private String name;
    // 类型
    private String contentType;
    // 夹备注
    private String remark;
    // 是否父结点
    private boolean isParent;
    // 文件夹所在夹UUID
    private String folderUuid;

    /**
     * @return the uuid
     */
    public String getUuid() {
        return uuid;
    }

    /**
     * @param uuid 要设置的uuid
     */
    public void setUuid(String uuid) {
        this.uuid = uuid;
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
     * @return the contentType
     */
    public String getContentType() {
        return contentType;
    }

    /**
     * @param contentType 要设置的contentType
     */
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    /**
     * @return the remark
     */
    public String getRemark() {
        return remark;
    }

    /**
     * @param remark 要设置的remark
     */
    public void setRemark(String remark) {
        this.remark = remark;
    }

    /**
     * @return the isParent
     */
    public boolean isParent() {
        return isParent;
    }

    /**
     * @param isParent 要设置的isParent
     */
    public void setParent(boolean isParent) {
        this.isParent = isParent;
    }

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

}
