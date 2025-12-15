package com.wellsoft.pt.ei.bo;

import java.util.Date;
import java.util.List;

/**
 * Description:
 * 日志记录详情
 * <pre>
 * 修改记录:
 * 修改后版本    修改人     修改日期    修改内容
 * 1.0         baozh    2021/9/22   Create
 * </pre>
 */
public class DataExportRecordInfoBo {

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

    // 过程日志
    private String processLog;

    //统计详情
    private List<DataRecordDetailBo> dataRecordDetailBos;

    //日志详情
//    private List<DataTaskLogBo> dataTaskLogBos;


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

    public List<DataRecordDetailBo> getDataRecordDetailBos() {
        return dataRecordDetailBos;
    }

    public void setDataRecordDetailBos(List<DataRecordDetailBo> dataRecordDetailBos) {
        this.dataRecordDetailBos = dataRecordDetailBos;
    }

    public String getProcessLog() {
        return processLog;
    }

    public void setProcessLog(String processLog) {
        this.processLog = processLog;
    }
/*public List<DataTaskLogBo> getDataTaskLogBos() {
        return dataTaskLogBos;
    }

    public void setDataTaskLogBos(List<DataTaskLogBo> dataTaskLogBos) {
        this.dataTaskLogBos = dataTaskLogBos;
    }*/
}
