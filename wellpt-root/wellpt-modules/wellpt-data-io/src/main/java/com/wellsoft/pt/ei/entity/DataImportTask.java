package com.wellsoft.pt.ei.entity;

import com.wellsoft.context.jdbc.entity.IdEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.sql.Clob;
import java.util.Date;

/**
 * Description: 数据导入任务表
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
@Table(name = "DATA_IMPORT_TASK")
@DynamicUpdate
@DynamicInsert
public class DataImportTask extends IdEntity {
    // 导入数据类型
    private String dataType;
    // 数据归属单位Id
    private String importUnitId;
    // 数据归属单位名称
    private String importUnitName;
    // 导入文件名称
    private Clob importFiles;
    // 任务状态 0：取消，1：完成，2：导入中，3：异常终止
    private Integer taskStatus;
    // 任务状态中文名
    private String taskStatusCn;
    // 导入进度
    private Integer progress;
    // 数据总量
    private Integer dataTotal;
    // 操作人名称
    private String operator;
    // 导入时间
    private Date importTime;
    // 完成时间
    private Date finishTime;
    // 导入记录的uuid，用于判断任务归属
    private String recordUuid;
    // 重新导入时间，因为重新导入，日志是叠加的，详情页计算日志数量的时候，会一直增加，需要一个时间条件来使数量准确
    private Date reimportTime;

    public String getImportUnitId() {
        return importUnitId;
    }

    public void setImportUnitId(String importUnitId) {
        this.importUnitId = importUnitId;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getImportUnitName() {
        return importUnitName;
    }

    public void setImportUnitName(String importUnitName) {
        this.importUnitName = importUnitName;
    }

    public Clob getImportFiles() {
        return importFiles;
    }

    public void setImportFiles(Clob importFiles) {
        this.importFiles = importFiles;
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

    public Date getImportTime() {
        return importTime;
    }

    public void setImportTime(Date importTime) {
        this.importTime = importTime;
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

    public Date getReimportTime() {
        return reimportTime;
    }

    public void setReimportTime(Date reimportTime) {
        this.reimportTime = reimportTime;
    }
}
