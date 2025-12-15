package com.wellsoft.pt.ei.dto.flow.ipt;

import org.apache.commons.math3.util.Pair;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class ImportFlowData implements Serializable {

    private String flowInstUuid;

    private String flowDefUuid;

    private String formUuid;

    private String formDefUuid;

    private Map<String, List<Map<String, Object>>> formDatas;

    private Map<String, Object> flowInstanceMap;

    private List<Map<String, Object>> taskInstanceMapList;

    private List<Map<String, Object>> taskOperationMapList;

    private List<Map<String, Object>> taskActivityMapList;

    // uuid   fieldName filePath
    private Map<String, List<Pair<String, String>>> fileFieldMap;

    public Map<String, List<Map<String, Object>>> getFormDatas() {
        return formDatas;
    }

    public void setFormDatas(Map<String, List<Map<String, Object>>> formDatas) {
        this.formDatas = formDatas;
    }

    public Map<String, Object> getFlowInstanceMap() {
        return flowInstanceMap;
    }

    public void setFlowInstanceMap(Map<String, Object> flowInstanceMap) {
        this.flowInstanceMap = flowInstanceMap;
    }

    public List<Map<String, Object>> getTaskInstanceMapList() {
        return taskInstanceMapList;
    }

    public void setTaskInstanceMapList(List<Map<String, Object>> taskInstanceMapList) {
        this.taskInstanceMapList = taskInstanceMapList;
    }

    public List<Map<String, Object>> getTaskOperationMapList() {
        return taskOperationMapList;
    }

    public void setTaskOperationMapList(List<Map<String, Object>> taskOperationMapList) {
        this.taskOperationMapList = taskOperationMapList;
    }


    public List<Map<String, Object>> getTaskActivityMapList() {
        return taskActivityMapList;
    }

    public void setTaskActivityMapList(List<Map<String, Object>> taskActivityMapList) {
        this.taskActivityMapList = taskActivityMapList;
    }

    public Map<String, List<Pair<String, String>>> getFileFieldMap() {
        return fileFieldMap;
    }

    public void setFileFieldMap(Map<String, List<Pair<String, String>>> fileFieldMap) {
        this.fileFieldMap = fileFieldMap;
    }
}
