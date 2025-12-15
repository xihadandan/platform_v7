/*
 * @(#)2016年3月23日 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.common.wopi.support;

import org.codehaus.jackson.annotate.JsonProperty;

import java.io.Serializable;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016年3月23日.1	zhulh		2016年3月23日		Create
 * </pre>
 * @date 2016年3月23日
 */
public class FileInfo implements Serializable {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 193601233251543286L;

    @JsonProperty(value = "BaseFileName")
    private String baseFileName;

    @JsonProperty(value = "OwnerId")
    private String ownerId;

    @JsonProperty(value = "Size")
    private long size;

    @JsonProperty(value = "UserId")
    private String userId;

    @JsonProperty(value = "SHA256")
    private String sha256;

    @JsonProperty(value = "Version")
    private String version;

    /**
     * @return the baseFileName
     */
    public String getBaseFileName() {
        return baseFileName;
    }

    /**
     * @param baseFileName 要设置的baseFileName
     */
    public void setBaseFileName(String baseFileName) {
        this.baseFileName = baseFileName;
    }

    /**
     * @return the ownerId
     */
    public String getOwnerId() {
        return ownerId;
    }

    /**
     * @param ownerId 要设置的ownerId
     */
    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    /**
     * @return the size
     */
    public long getSize() {
        return size;
    }

    /**
     * @param size 要设置的size
     */
    public void setSize(long size) {
        this.size = size;
    }

    /**
     * @return the userId
     */
    public String getUserId() {
        return userId;
    }

    /**
     * @param userId 要设置的userId
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * @return the sha256
     */
    public String getSha256() {
        return sha256;
    }

    /**
     * @param sha256 要设置的sha256
     */
    public void setSha256(String sha256) {
        this.sha256 = sha256;
    }

    /**
     * @return the version
     */
    public String getVersion() {
        return version;
    }

    /**
     * @param version 要设置的version
     */
    public void setVersion(String version) {
        this.version = version;
    }

}
