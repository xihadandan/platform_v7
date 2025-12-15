package com.wellsoft.pt.ei.entity;

import com.wellsoft.context.jdbc.entity.IdEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

/**
 * Description: 数据导入任务日志表
 *
 * @author liuyz
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2021/9/16.1	liuyz		2021/9/16		Create
 * </pre>
 * @date 2021/9/16
 */
@Entity
@Table(name = "DATA_IMPORT_TASK_LOG")
@DynamicUpdate
@DynamicInsert
public class DataImportTaskLog extends IdEntity {
    // 导入数据类型
    private String dataType;
    // 导入数据子类型
    private String dataChildType;
    // 导入数据的uuid
    private String sourceUuid;
    // 导入数据后的uuid
    private String afterImportUuid;
    // 导入数据的id
    private String sourceId;
    // 导入数据后的id
    private String afterImportId;
    // 导入时间
    private Date importTime;
    // 导入状态 0：失败，1：正常
    private Integer importStatus;
    // 异常原因
    private String errorMsg;
    // 导入任务uuid，用于判断日志归属
    private String taskUuid;
    // 是否重复 0：否，1：是
    private Integer isRepeat;
    // 源数据信息
    private String sourceData;
    // 导入状态中文名
    private String importStatusCn;

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getDataChildType() {
        return dataChildType;
    }

    public void setDataChildType(String dataChildType) {
        this.dataChildType = dataChildType;
    }

    public String getSourceUuid() {
        return sourceUuid;
    }

    public void setSourceUuid(String sourceUuid) {
        this.sourceUuid = sourceUuid;
    }

    public String getAfterImportUuid() {
        return afterImportUuid;
    }

    public void setAfterImportUuid(String afterImportUuid) {
        this.afterImportUuid = afterImportUuid;
    }

    public String getSourceId() {
        return sourceId;
    }

    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }

    public String getAfterImportId() {
        return afterImportId;
    }

    public void setAfterImportId(String afterImportId) {
        this.afterImportId = afterImportId;
    }

    public Date getImportTime() {
        return importTime;
    }

    public void setImportTime(Date importTime) {
        this.importTime = importTime;
    }

    public Integer getImportStatus() {
        return importStatus;
    }

    public void setImportStatus(Integer importStatus) {
        this.importStatus = importStatus;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public String getTaskUuid() {
        return taskUuid;
    }

    public void setTaskUuid(String taskUuid) {
        this.taskUuid = taskUuid;
    }

    public Integer getIsRepeat() {
        return isRepeat;
    }

    public void setIsRepeat(Integer isRepeat) {
        this.isRepeat = isRepeat;
    }

    public String getSourceData() {
        return sourceData;
    }

    public void setSourceData(String sourceData) {
        this.sourceData = sourceData;
    }

    public String getImportStatusCn() {
        return importStatusCn;
    }

    public void setImportStatusCn(String importStatusCn) {
        this.importStatusCn = importStatusCn;
    }
}
