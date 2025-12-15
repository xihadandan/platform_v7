/**
 * Copyright (c) 2005-2012, www.dengwl.com
 * All rights reserved.
 *
 * @Title: ChatAppGetVersionRequest.java
 * @Package com.wellsoft.pt.api.request
 * @Description: TODO
 * @author Administrator
 * @date 2014-12-11 上午9:16:50
 * @version V1.0
 */
package com.wellsoft.pt.api.request;

import com.wellsoft.pt.api.WellptRequest;
import com.wellsoft.pt.api.internal.suport.ApiServiceName;
import com.wellsoft.pt.api.response.FileUploadResponse;
import org.codehaus.jackson.annotate.JsonIgnore;

import java.io.InputStream;

/**
 * @author Administrator
 * @ClassName: FileUploadApiRequest
 * @Description: TODO
 * @date 2015年6月24日08:49:59
 */
public class FileUploadRequest extends WellptRequest<FileUploadResponse> {

    private String fileName;

    private String folderID;

    @JsonIgnore
    private InputStream inputStream;

    /**
     * 用途
     */
    private String purpose;

    /**
     *
     */
    private String contentType;

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public String getFolderID() {
        return folderID;
    }

    public void setFolderID(String folderID) {
        this.folderID = folderID;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public Class<FileUploadResponse> getResponseClass() {
        return FileUploadResponse.class;
    }

    @Override
    public String getApiServiceName() {
        // TODO Auto-generated method stub
        return ApiServiceName.FILEUPLOAD;
    }

}
