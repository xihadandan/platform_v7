package com.wellsoft.pt.ei.entity;

import com.wellsoft.context.jdbc.entity.IdEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

/**
 * Description: 数据导出任务表
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
@Table(name = "DATA_EXPORT_TASK")
@DynamicUpdate
@DynamicInsert
public class DataExportTask extends IdEntity {
    // 数据类型
    private String dataType;
    // 数据归属单位id，多个逗号隔开
    private String systemUnitIds;
    // 数据归属单位名称，多个逗号隔开
    private String systemUnitNames;
    // 导出路径
    private String exportPath;
    // 任务状态：0：取消，1：完成，2：导出中，3：异常终止
    private Integer taskStatus;
    // 任务状态中文
    private String taskStatusCn;
    // 导出进度
    private Integer progress;
    // 数据总量
    private Integer dataTotal;
    // 操作人名称
    private String operator;
    // 导出时间
    private Date exportTime;
    // 完成时间
    private Date finishTime;
    // 导出记录uuid，用于判断任务归属
    private String recordUuid;
    // 重新导出时间，因为重新导出，日志是叠加的，详情页计算日志数量的时候，会一直增加，需要一个时间条件来使数量准确
    private Date reexportTime;

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getSystemUnitIds() {
        return systemUnitIds;
    }

    public void setSystemUnitIds(String systemUnitIds) {
        this.systemUnitIds = systemUnitIds;
    }

    public String getSystemUnitNames() {
        return systemUnitNames;
    }

    public void setSystemUnitNames(String systemUnitNames) {
        this.systemUnitNames = systemUnitNames;
    }

    public String getExportPath() {
        return exportPath;
    }

    public void setExportPath(String exportPath) {
        this.exportPath = exportPath;
    }

    public Integer getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(Integer taskStatus) {
        this.taskStatus = taskStatus;
    }

    public Integer getProgress() {
        return progress;
    }

    public void setProgress(Integer progress) {
        this.progress = progress;
    }

    public Integer getDataTotal() {
        return dataTotal;
    }

    public void setDataTotal(Integer dataTotal) {
        this.dataTotal = dataTotal;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public Date getExportTime() {
        return exportTime;
    }

    public void setExportTime(Date exportTime) {
        this.exportTime = exportTime;
    }

    public Date getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(Date finishTime) {
        this.finishTime = finishTime;
    }

    public String getRecordUuid() {
        return recordUuid;
    }

    public void setRecordUuid(String recordUuid) {
        this.recordUuid = recordUuid;
    }

    public String getTaskStatusCn() {
        return taskStatusCn;
    }

    public void setTaskStatusCn(String taskStatusCn) {
        this.taskStatusCn = taskStatusCn;
    }

    public Date getReexportTime() {
        return reexportTime;
    }

    public void setReexportTime(Date reexportTime) {
        this.reexportTime = reexportTime;
    }
}
