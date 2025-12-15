/*
 * @(#)Dec 15, 2017 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.entity;

import com.wellsoft.context.jdbc.entity.IdEntity;
import com.wellsoft.pt.jpa.service.UUIDGeneratorIndicate;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Description: 文件
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
@Table(name = "DMS_FILE")
@DynamicUpdate
@DynamicInsert
@ApiModel(value = "DmsFileEntity", description = "归档数据")
public class DmsFileEntity extends IdEntity implements UUIDGeneratorIndicate {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 3337915094863017664L;

    // 文件名称
    @ApiModelProperty("文件名称")
    private String fileName;
    // 文件类型
    @ApiModelProperty("文件类型")
    private String contentType;
    // 文件大小
    @ApiModelProperty("文件大小")
    private Long fileSize;
    // 数据定义UUID
    @ApiModelProperty("数据定义UUID（表单定义uuid/流程定义uuid）")
    private String dataDefUuid;
    // 数据UUID
    @ApiModelProperty("数据UUID（表单数据uuid/流程实例uuid）")
    private String dataUuid;
    // 文件状态，0删除，1正常，2草稿
    @ApiModelProperty("文件状态，0删除，1正常，2草稿")
    private String status;
    // 文件所在夹UUID
    @ApiModelProperty("文件所在夹UUID")
    private String folderUuid;
    // 库UUID
    @ApiModelProperty("文件所在库UUID")
    private String libraryUuid;

    /**
     * @return the fileName
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * @param fileName 要设置的fileName
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
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
     * @return the fileSize
     */
    public Long getFileSize() {
        return fileSize;
    }

    /**
     * @param fileSize 要设置的fileSize
     */
    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    /**
     * @return the dataDefUuid
     */
    public String getDataDefUuid() {
        return dataDefUuid;
    }

    /**
     * @param dataDefUuid 要设置的dataDefUuid
     */
    public void setDataDefUuid(String dataDefUuid) {
        this.dataDefUuid = dataDefUuid;
    }

    /**
     * @return the dataUuid
     */
    public String getDataUuid() {
        return dataUuid;
    }

    /**
     * @param dataUuid 要设置的dataUuid
     */
    public void setDataUuid(String dataUuid) {
        this.dataUuid = dataUuid;
    }

    /**
     * @return the status
     */
    public String getStatus() {
        return status;
    }

    /**
     * @param status 要设置的status
     */
    public void setStatus(String status) {
        this.status = status;
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

    /**
     * @return the libraryUuid
     */
    public String getLibraryUuid() {
        return libraryUuid;
    }

    /**
     * @param libraryUuid 要设置的libraryUuid
     */
    public void setLibraryUuid(String libraryUuid) {
        this.libraryUuid = libraryUuid;
    }
}
