package com.wellsoft.pt.ei.dto.flow.ipt;

import java.util.List;

public class ImportFlowAddData {

    private List<String> donePermissionUserIdList;

    private String flowInstanceUuid;

    private List<String> taskInstanceUuidList;

    private List<String> taskOperationUuidList;

    private List<String> taskActivityUuidList;

    private String lastTaskInstanceUuid;

    private String formUuid;

    private String dataUuid;


    public List<String> getDonePermissionUserIdList() {
        return donePermissionUserIdList;
    }

    public void setDonePermissionUserIdList(List<String> donePermissionUserIdList) {
        this.donePermissionUserIdList = donePermissionUserIdList;
    }

    public String getFlowInstanceUuid() {
        return flowInstanceUuid;
    }

    public void setFlowInstanceUuid(String flowInstanceUuid) {
        this.flowInstanceUuid = flowInstanceUuid;
    }

    public List<String> getTaskInstanceUuidList() {
        return taskInstanceUuidList;
    }

    public void setTaskInstanceUuidList(List<String> taskInstanceUuidList) {
        this.taskInstanceUuidList = taskInstanceUuidList;
    }

    public List<String> getTaskOperationUuidList() {
        return taskOperationUuidList;
    }

    public void setTaskOperationUuidList(List<String> taskOperationUuidList) {
        this.taskOperationUuidList = taskOperationUuidList;
    }

    public List<String> getTaskActivityUuidList() {
        return taskActivityUuidList;
    }

    public void setTaskActivityUuidList(List<String> taskActivityUuidList) {
        this.taskActivityUuidList = taskActivityUuidList;
    }

    public String getLastTaskInstanceUuid() {
        return lastTaskInstanceUuid;
    }

    public void setLastTaskInstanceUuid(String lastTaskInstanceUuid) {
        this.lastTaskInstanceUuid = lastTaskInstanceUuid;
    }

    public String getFormUuid() {
        return formUuid;
    }

    public void setFormUuid(String formUuid) {
        this.formUuid = formUuid;
    }

    public String getDataUuid() {
        return dataUuid;
    }

    public void setDataUuid(String dataUuid) {
        this.dataUuid = dataUuid;
    }
}
