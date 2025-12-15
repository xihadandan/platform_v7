package com.wellsoft.pt.repository.entity;

import com.wellsoft.context.jdbc.entity.TenantEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Description:
 * 金格file转换实体类
 * <pre>
 * 修改记录:
 * 修改后版本    修改人     修改日期    修改内容
 * 1.0         baozh    2021/11/8   Create
 * </pre>
 */
@ApiModel("金格文件转换实体")
@Entity
@Table(name = "king_grid_file_convert")
public class KingGridFileEntity extends TenantEntity {

    @ApiModelProperty("文件名称")
    private String fileName;

    @ApiModelProperty("转换类型")
    private ConvertType convertType;

    @ApiModelProperty("来源文件路径")
    private String sourceFilePath;

    @ApiModelProperty("目标文件路径或者预览地址")
    private String targetFilePath;

    @ApiModelProperty("转换状态")
    private ConvertStatus status;

    @ApiModelProperty("文件大小")
    private Long fileSize;

    @ApiModelProperty("错误信息")
    private String errorMsg;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public ConvertType getConvertType() {
        return convertType;
    }

    public void setConvertType(ConvertType convertType) {
        this.convertType = convertType;
    }

    public String getSourceFilePath() {
        return sourceFilePath;
    }

    public void setSourceFilePath(String sourceFilePath) {
        this.sourceFilePath = sourceFilePath;
    }

    public String getTargetFilePath() {
        return targetFilePath;
    }

    public void setTargetFilePath(String targetFilePath) {
        this.targetFilePath = targetFilePath;
    }

    public ConvertStatus getStatus() {
        return status;
    }

    public void setStatus(ConvertStatus status) {
        this.status = status;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }
}
