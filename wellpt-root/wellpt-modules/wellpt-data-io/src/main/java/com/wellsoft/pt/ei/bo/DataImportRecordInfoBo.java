package com.wellsoft.pt.ei.bo;

import java.util.Date;
import java.util.List;

/**
 * Description:
 * es全文检索实现
 * <pre>
 * 修改记录:
 * 修改后版本    修改人     修改日期    修改内容
 * 1.0         baozh    2021/9/23   Create
 * </pre>
 */
public class DataImportRecordInfoBo {

    // 数据类型
    private String dataType;
    // 数据类型子类的json串
    private String dataTypeJson;
    // 源数据uuid字段名
    private String sourceUuid;
    // 数据归属单位名称
    private String importUnitName;
    // 组织版本ID
    private String versionId;
    // 组织版本名称
    private String versionName;
    // 设置登录密码
    private String settingPwd;
    // 导入文件路径
    private String importPath;
    // 导入文件名称
    private String importFiles;
    // 重复策略 1：替换，2：跳过
    private Integer repeatStrategy;
    // 异常策略 1：终止，2：跳过
    private Integer errorStrategy;
    // 处理批号
    private String batchNo;
    // 处理结果 0：取消，1：完成，2：异常终止
    private Integer importStatus;
    // 操作人名称
    private String operator;
    // 导入时间
    private Date importTime;
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

    public String getSourceUuid() {
        return sourceUuid;
    }

    public void setSourceUuid(String sourceUuid) {
        this.sourceUuid = sourceUuid;
    }

    public String getImportUnitName() {
        return importUnitName;
    }

    public void setImportUnitName(String importUnitName) {
        this.importUnitName = importUnitName;
    }

    public String getVersionId() {
        return versionId;
    }

    public void setVersionId(String versionId) {
        this.versionId = versionId;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public String getSettingPwd() {
        return settingPwd;
    }

    public void setSettingPwd(String settingPwd) {
        this.settingPwd = settingPwd;
    }

    public String getImportPath() {
        return importPath;
    }

    public void setImportPath(String importPath) {
        this.importPath = importPath;
    }

    public String getImportFiles() {
        return importFiles;
    }

    public void setImportFiles(String importFiles) {
        this.importFiles = importFiles;
    }

    public Integer getRepeatStrategy() {
        return repeatStrategy;
    }

    public void setRepeatStrategy(Integer repeatStrategy) {
        this.repeatStrategy = repeatStrategy;
    }

    public Integer getErrorStrategy() {
        return errorStrategy;
    }

    public void setErrorStrategy(Integer errorStrategy) {
        this.errorStrategy = errorStrategy;
    }

    public String getBatchNo() {
        return batchNo;
    }

    public void setBatchNo(String batchNo) {
        this.batchNo = batchNo;
    }

    public Integer getImportStatus() {
        return importStatus;
    }

    public void setImportStatus(Integer importStatus) {
        this.importStatus = importStatus;
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
