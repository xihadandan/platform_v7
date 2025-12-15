package com.wellsoft.pt.log.dto;

import com.wellsoft.pt.log.entity.LogManageDetailsEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * Description:
 *
 * @author zenghw
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	        修改人		修改日期			修改内容
 * 2021/6/28.1	    zenghw		2021/6/28		    Create
 * </pre>
 * @date 2021/6/28
 */
public class SaveLogManageOperationDto {

    // 报文操作前的数据值（不涉及搜索）
    private String beforeMessageValue;
    // 数据名称简介
    private String dataNameInfo;
    // 数据id
    private String dataId;
    // 模块名称
    private String moduleName;
    // 操作前的数据名称
    private String beforeDataName;
    // 操作后的数据名称
    private String afterDataName;
    // 报文操作后的数据值（不涉及搜索）
    private String afterMessageValue;
    // 操作类型
    private String operation;
    // 操作类型ID
    private String operationId;
    // 模块id
    private String moduleId;
    // 数据类型：流程分类，流程定义
    private String dataType;

    // 数据类型：1:流程分类，2:流程定义
    private String dataTypeId;

    /**
     * 管理日志详情列表
     **/
    private List<LogManageDetailsEntity> logManageDetailsEntity = new ArrayList<>();

    public List<LogManageDetailsEntity> getLogManageDetailsEntity() {
        return logManageDetailsEntity;
    }

    public void setLogManageDetailsEntity(List<LogManageDetailsEntity> logManageDetailsEntity) {
        this.logManageDetailsEntity = logManageDetailsEntity;
    }

    public String getOperationId() {
        return operationId;
    }

    public void setOperationId(String operationId) {
        this.operationId = operationId;
    }

    public String getBeforeMessageValue() {
        return beforeMessageValue;
    }

    public void setBeforeMessageValue(String beforeMessageValue) {
        this.beforeMessageValue = beforeMessageValue;
    }

    public String getDataNameInfo() {
        return dataNameInfo;
    }

    public void setDataNameInfo(String dataNameInfo) {
        this.dataNameInfo = dataNameInfo;
    }

    public String getDataId() {
        return dataId;
    }

    public void setDataId(String dataId) {
        this.dataId = dataId;
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public String getBeforeDataName() {
        return beforeDataName;
    }

    public void setBeforeDataName(String beforeDataName) {
        this.beforeDataName = beforeDataName;
    }

    public String getAfterDataName() {
        return afterDataName;
    }

    public void setAfterDataName(String afterDataName) {
        this.afterDataName = afterDataName;
    }

    public String getAfterMessageValue() {
        return afterMessageValue;
    }

    public void setAfterMessageValue(String afterMessageValue) {
        this.afterMessageValue = afterMessageValue;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String getModuleId() {
        return moduleId;
    }

    public void setModuleId(String moduleId) {
        this.moduleId = moduleId;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getDataTypeId() {
        return dataTypeId;
    }

    public void setDataTypeId(String dataTypeId) {
        this.dataTypeId = dataTypeId;
    }
}
