package com.wellsoft.pt.ei.entity;

import com.wellsoft.context.jdbc.entity.IdEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

/**
 * Description: 数据导出任务日志表
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
@Table(name = "DATA_EXPORT_TASK_LOG")
@DynamicUpdate
@DynamicInsert
public class DataExportTaskLog extends IdEntity {
    // 数据类型
    private String dataType;
    // 数据子类
    private String dataChildType;
    // 导出数据的uuid
    private String dataUuid;
    // 导出时间
    private Date exportTime;
    // 导出状态 0：失败  1：正常
    private Integer exportStatus;
    // 异常原因
    private String errorMsg;
    // 导出任务的uuid，用于判断日志归属
    private String taskUuid;
    // 导出状态中文名
    private String exportStatusCn;

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

    public String getDataUuid() {
        return dataUuid;
    }

    public void setDataUuid(String dataUuid) {
        this.dataUuid = dataUuid;
    }

    public Date getExportTime() {
        return exportTime;
    }

    public void setExportTime(Date exportTime) {
        this.exportTime = exportTime;
    }

    public Integer getExportStatus() {
        return exportStatus;
    }

    public void setExportStatus(Integer exportStatus) {
        this.exportStatus = exportStatus;
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

    public String getExportStatusCn() {
        return exportStatusCn;
    }

    public void setExportStatusCn(String exportStatusCn) {
        this.exportStatusCn = exportStatusCn;
    }
}
