/**
 * Copyright (c) 2005-2012, www.dengwl.com
 * All rights reserved.
 *
 * @Title: ChatAppGetVersionResponse.java
 * @Package com.wellsoft.pt.api.response
 * @Description: TODO
 * @author Administrator
 * @date 2014-12-11 上午9:14:12
 * @version V1.0
 */
package com.wellsoft.pt.api.response;

import com.wellsoft.pt.api.WellptResponse;

/**
 * @author Administrator
 * @ClassName: ChatAppGetVersionResponse
 * @Description: TODO
 * @date 2014-12-11 上午9:14:12
 */
public class ChatAppGetVersionResponse extends WellptResponse {

    /**
     * @Fields serialVersionUID : TODO
     */
    private static final long serialVersionUID = 3206374455336908984L;

    /**
     * @Fields version : 版本好
     */
    private String version;
    /**
     * @Fields remark : 版本说明
     */
    private String remark;
    /**
     * @Fields fileid : 下载文件id
     */
    private String fileid;

    /**
     * @Fields downloadurl : 下载地址
     */
    private String downloadurl;

    /**
     * @return the version
     */
    public String getVersion() {
        return version;
    }

    /**
     * @param version the version to set
     */
    public void setVersion(String version) {
        this.version = version;
    }

    /**
     * @return the remark
     */
    public String getRemark() {
        return remark;
    }

    /**
     * @param remark the remark to set
     */
    public void setRemark(String remark) {
        this.remark = remark;
    }

    /**
     * @return the fileid
     */
    public String getFileid() {
        return fileid;
    }

    /**
     * @param fileid the fileid to set
     */
    public void setFileid(String fileid) {
        this.fileid = fileid;
    }

    /**
     * @return downloadurl
     */
    public String getDownloadurl() {
        return downloadurl;
    }

    /**
     * @param downloadurl the downloadurl to set
     */
    public void setDownloadurl(String downloadurl) {
        this.downloadurl = downloadurl;
    }
}
