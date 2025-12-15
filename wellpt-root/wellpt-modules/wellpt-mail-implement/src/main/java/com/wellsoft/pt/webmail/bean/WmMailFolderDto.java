/*
 * @(#)2018年2月27日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.webmail.bean;

import java.io.Serializable;

/**
 * Description: 邮件文件夹DTO
 *
 * @author chenqiong
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2018年2月27日.1	chenqiong		2018年2月27日		Create
 * </pre>
 * @date 2018年2月27日
 */
public class WmMailFolderDto implements Serializable {

    private static final long serialVersionUID = 5657118712517581887L;

    private String folderName;

    private String uuid;

    private Integer seq;

    private String folderCode;

    /**
     * @return the folderName
     */
    public String getFolderName() {
        return folderName;
    }

    /**
     * @param folderName 要设置的folderName
     */
    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

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
     * @return the seq
     */
    public Integer getSeq() {
        return seq;
    }

    /**
     * @param seq 要设置的seq
     */
    public void setSeq(Integer seq) {
        this.seq = seq;
    }

    /**
     * @return the folderCode
     */
    public String getFolderCode() {
        return folderCode;
    }

    /**
     * @param folderCode 要设置的folderCode
     */
    public void setFolderCode(String folderCode) {
        this.folderCode = folderCode;
    }

}
