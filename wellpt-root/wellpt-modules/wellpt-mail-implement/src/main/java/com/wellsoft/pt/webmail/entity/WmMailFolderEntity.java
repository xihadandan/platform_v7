/*
 * @(#)2016-06-03 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.webmail.entity;

import com.wellsoft.context.jdbc.entity.IdEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Description: 邮件文件夹
 *
 * @author chenqiong
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2018年3月1日.1	chenqiong		2018年3月1日		Create
 * </pre>
 * @date 2018年3月1日
 */
@Entity
@Table(name = "WM_MAIL_FOLDER")
@DynamicUpdate
@DynamicInsert
public class WmMailFolderEntity extends IdEntity {

    public static final String FOLDER_CODE_PREFIX = "FOLDER";
    private static final long serialVersionUID = -877822085718230943L;
    /**
     * 用户id
     */
    private String userId;

    /**
     * 系统单位Id
     */
    private String systemUnitId;

    /**
     * 文件夹名称
     */
    private String folderName;

    /**
     * 文件夹编码 用于标注邮件归属
     */
    private String folderCode;

    /**
     * 排序号
     */
    private Integer seq;

    /**
     * 获取 用户id
     *
     * @return the userId
     */
    public String getUserId() {
        return userId;
    }

    /**
     * 设置 用户id
     *
     * @param userId 要设置的userId
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * 获取 系统单位Id
     *
     * @return the systemUnitId
     */
    public String getSystemUnitId() {
        return systemUnitId;
    }

    /**
     * 设置 系统单位Id
     *
     * @param systemUnitId 要设置的systemUnitId
     */
    public void setSystemUnitId(String systemUnitId) {
        this.systemUnitId = systemUnitId;
    }

    /**
     * 获取 文件夹名称
     *
     * @return the folderName
     */
    public String getFolderName() {
        return folderName;
    }

    /**
     * 设置 文件夹名称
     *
     * @param folderName 要设置的folderName
     */
    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    /**
     * 获取 排序号
     *
     * @return the seq
     */
    public Integer getSeq() {
        return seq;
    }

    /**
     * 设置 排序号
     *
     * @param seq 要设置的seq
     */
    public void setSeq(Integer seq) {
        this.seq = seq;
    }

    /**
     * 获取 文件夹编码 用于标注邮件归属
     *
     * @return the folderCode
     */
    public String getFolderCode() {
        return folderCode;
    }

    /**
     * 设置 文件夹编码 用于标注邮件归属
     *
     * @param folderCode 要设置的folderCode
     */
    public void setFolderCode(String folderCode) {
        this.folderCode = folderCode;
    }

}
