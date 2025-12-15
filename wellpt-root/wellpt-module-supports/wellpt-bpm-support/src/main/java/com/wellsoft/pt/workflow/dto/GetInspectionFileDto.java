package com.wellsoft.pt.workflow.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

/**
 * Description: 手写签批附件输出对象
 *
 * @author zenghw
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	        修改人		修改日期			修改内容
 * 2021/8/25.1	    zenghw		2021/8/25		    Create
 * </pre>
 * @date 2021/8/25
 */
@ApiModel("手写签批附件输出对象")
public class GetInspectionFileDto implements Serializable {
    private static final long serialVersionUID = 7719630605874776245L;


    @ApiModelProperty("附件uuid")
    private String fileUuid;
    @ApiModelProperty("附件名称")
    private String fileName;
    @ApiModelProperty("附件格式")
    private String fileType;
    @ApiModelProperty("附件大小")
    private String filesize;
    @ApiModelProperty("创建人（上传人）")
    private String creator;
    @ApiModelProperty("上传时间")
    private String createTime;


    public String getFileUuid() {
        return this.fileUuid;
    }

    public void setFileUuid(final String fileUuid) {
        this.fileUuid = fileUuid;
    }

    public String getFileName() {
        return this.fileName;
    }

    public void setFileName(final String fileName) {
        this.fileName = fileName;
    }

    public String getFileType() {
        return this.fileType;
    }

    public void setFileType(final String fileType) {
        this.fileType = fileType;
    }

    public String getFilesize() {
        return this.filesize;
    }

    public void setFilesize(final String filesize) {
        this.filesize = filesize;
    }

    public String getCreator() {
        return this.creator;
    }

    public void setCreator(final String creator) {
        this.creator = creator;
    }

    public String getCreateTime() {
        return this.createTime;
    }

    public void setCreateTime(final String createTime) {
        this.createTime = createTime;
    }
}
