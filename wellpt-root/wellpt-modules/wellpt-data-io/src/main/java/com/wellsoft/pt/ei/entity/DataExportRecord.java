package com.wellsoft.pt.ei.entity;

import com.wellsoft.context.jdbc.entity.IdEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

/**
 * Description: 数据导出记录表
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
@Table(name = "DATA_EXPORT_RECORD")
@DynamicUpdate
@DynamicInsert
public class DataExportRecord extends IdEntity {

    // 导出数据类型（多个,隔开）
    private String dataType;
    // 导出数据类型下的子类，用json串表示
    private String dataTypeJson;
    // 数据归属单位ID（多个,隔开）
    private String systemUnitIds;
    // 数据归属单位名称
    private String systemUnitNames;
    // 导出文件路径
    private String exportPath;
    // 导出批次数量
    private Integer batchQuantity;
    // 操作人
    private String operator;
    // 导出时间
    private Date exportTime;
    // 过程日志的中文描述
    private String processLog;

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getDataTypeJson() {
        return dataTypeJson;
    }

    public void setDataTypeJson(String dataTypeJson) {
        this.dataTypeJson = dataTypeJson;
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

    public Integer getBatchQuantity() {
        return batchQuantity;
    }

    public void setBatchQuantity(Integer batchQuantity) {
        this.batchQuantity = batchQuantity;
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

    public String getProcessLog() {
        return processLog;
    }

    public void setProcessLog(String processLog) {
        this.processLog = processLog;
    }
}
