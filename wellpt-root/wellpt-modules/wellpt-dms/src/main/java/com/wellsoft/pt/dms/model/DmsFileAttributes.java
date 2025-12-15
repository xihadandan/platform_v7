/*
 * @(#)Jan 4, 2018 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.model;

import com.wellsoft.context.base.BaseObject;

import java.util.Date;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * Jan 4, 2018.1	zhulh		Jan 4, 2018		Create
 * </pre>
 * @date Jan 4, 2018
 */
public class DmsFileAttributes extends BaseObject {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 7747919819633028430L;

    // 文件名
    private String name;
    // 文件类型
    private String contentType;
    // 文件类型名称
    private String contentTypeName;
    // 文件位置
    private String location;
    // 文件大小
    private Long size;
    // 文件大小字符串
    private String sizeString;
    // 创建时间
    private Date createTime;
    // 修改时间
    private Date modifyTime;
    // 夹配置
    private DmsFolderConfiguration folderConfiguration;

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
     * @return the contentTypeName
     */
    public String getContentTypeName() {
        return contentTypeName;
    }

    /**
     * @param contentTypeName 要设置的contentTypeName
     */
    public void setContentTypeName(String contentTypeName) {
        this.contentTypeName = contentTypeName;
    }

    /**
     * @return the location
     */
    public String getLocation() {
        return location;
    }

    /**
     * @param location 要设置的location
     */
    public void setLocation(String location) {
        this.location = location;
    }

    /**
     * @return the size
     */
    public Long getSize() {
        return size;
    }

    /**
     * @param size 要设置的size
     */
    public void setSize(Long size) {
        this.size = size;
    }

    /**
     * @return the sizeString
     */
    public String getSizeString() {
        return sizeString;
    }

    /**
     * @param sizeString 要设置的sizeString
     */
    public void setSizeString(String sizeString) {
        this.sizeString = sizeString;
    }

    /**
     * @return the createTime
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * @param createTime 要设置的createTime
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * @return the modifyTime
     */
    public Date getModifyTime() {
        return modifyTime;
    }

    /**
     * @param modifyTime 要设置的modifyTime
     */
    public void setModifyTime(Date modifyTime) {
        this.modifyTime = modifyTime;
    }

    /**
     * @return the folderConfiguration
     */
    public DmsFolderConfiguration getFolderConfiguration() {
        return folderConfiguration;
    }

    /**
     * @param folderConfiguration 要设置的folderConfiguration
     */
    public void setFolderConfiguration(DmsFolderConfiguration folderConfiguration) {
        this.folderConfiguration = folderConfiguration;
    }

}
