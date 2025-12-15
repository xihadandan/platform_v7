package com.wellsoft.pt.ei.dto.flow.exportNew;

import com.wellsoft.pt.ei.annotate.FieldType;
import com.wellsoft.pt.ei.constants.ExportFieldTypeEnum;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class FlowExportJsonItemData implements Serializable {

    // UUID
    @FieldType(desc = "UUID，系统字段", type = ExportFieldTypeEnum.STRING)
    protected String uuid;

    @FieldType(desc = "流程实例", isGroup = true)
    private FlowInstanceExportData flowInstance;

    @FieldType(desc = "流程表单数据")
    private Map<String, Object> dyformData;

    @FieldType(desc = "流程表单从表数据")
    private List<Map<String, Object>> subFormDataList;


    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public FlowInstanceExportData getFlowInstance() {
        return flowInstance;
    }

    public void setFlowInstance(FlowInstanceExportData flowInstance) {
        this.flowInstance = flowInstance;
    }

    public Map<String, Object> getDyformData() {
        return dyformData;
    }

    public void setDyformData(Map<String, Object> dyformData) {
        this.dyformData = dyformData;
    }

    public List<Map<String, Object>> getSubFormDataList() {
        return subFormDataList;
    }

    public void setSubFormDataList(List<Map<String, Object>> subFormDataList) {
        this.subFormDataList = subFormDataList;
    }
}
