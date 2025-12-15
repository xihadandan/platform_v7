package com.wellsoft.pt.ei.dto;

import com.wellsoft.pt.ei.entity.DataImportRecord;
import com.wellsoft.pt.ei.entity.DataImportTask;
import com.wellsoft.pt.ei.entity.DataImportTaskLog;

/**
 * Description: 组织数据待处理信息类，需要处理 parentEleIdPath、parentEleNamePath、eleIdPath、管理员
 *
 * @author liuyz
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2021/9/26.1	liuyz		2021/9/26		Create
 * </pre>
 * @date 2021/9/26
 */
public class OrgPendingInfo {
    /**
     * 所属的对象id
     */
    private String id;

    private String sourceId;

    private String eleIdPath;

    /**
     * 父级节点
     */
    private String parentEleIdPath;

    private String parentEleNamePath;

    /**
     * 负责人
     */
    private String bossIdPaths;
    private String bossNames;
    /**
     * 管理员
     */
    private String managerIdPaths;
    private String managerNames;
    /**
     * 分管领导
     */
    private String branchLeaderIdPaths;
    private String branchLeaderNames;

    private DataImportRecord record;

    private DataImportTask task;

    private DataImportTaskLog log;

    private String type;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSourceId() {
        return sourceId;
    }

    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }

    public String getEleIdPath() {
        return eleIdPath;
    }

    public void setEleIdPath(String eleIdPath) {
        this.eleIdPath = eleIdPath;
    }

    public DataImportRecord getRecord() {
        return record;
    }

    public void setRecord(DataImportRecord record) {
        this.record = record;
    }

    public String getParentEleIdPath() {
        return parentEleIdPath;
    }

    public void setParentEleIdPath(String parentEleIdPath) {
        this.parentEleIdPath = parentEleIdPath;
    }

    public String getParentEleNamePath() {
        return parentEleNamePath;
    }

    public void setParentEleNamePath(String parentEleNamePath) {
        this.parentEleNamePath = parentEleNamePath;
    }

    public String getBossIdPaths() {
        return bossIdPaths;
    }

    public void setBossIdPaths(String bossIdPaths) {
        this.bossIdPaths = bossIdPaths;
    }

    public String getBossNames() {
        return bossNames;
    }

    public void setBossNames(String bossNames) {
        this.bossNames = bossNames;
    }

    public String getManagerIdPaths() {
        return managerIdPaths;
    }

    public void setManagerIdPaths(String managerIdPaths) {
        this.managerIdPaths = managerIdPaths;
    }

    public String getManagerNames() {
        return managerNames;
    }

    public void setManagerNames(String managerNames) {
        this.managerNames = managerNames;
    }

    public String getBranchLeaderIdPaths() {
        return branchLeaderIdPaths;
    }

    public void setBranchLeaderIdPaths(String branchLeaderIdPaths) {
        this.branchLeaderIdPaths = branchLeaderIdPaths;
    }

    public String getBranchLeaderNames() {
        return branchLeaderNames;
    }

    public void setBranchLeaderNames(String branchLeaderNames) {
        this.branchLeaderNames = branchLeaderNames;
    }

    public DataImportTask getTask() {
        return task;
    }

    public void setTask(DataImportTask task) {
        this.task = task;
    }

    public DataImportTaskLog getLog() {
        return log;
    }

    public void setLog(DataImportTaskLog log) {
        this.log = log;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
