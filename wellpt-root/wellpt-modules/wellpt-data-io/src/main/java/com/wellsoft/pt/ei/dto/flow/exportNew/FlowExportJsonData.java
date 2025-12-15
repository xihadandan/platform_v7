package com.wellsoft.pt.ei.dto.flow.exportNew;

import com.wellsoft.pt.ei.constants.DataExportConstants;

import java.io.Serializable;
import java.util.List;

public class FlowExportJsonData implements Serializable {

    private String dateType = DataExportConstants.DATA_TYPE_FLOW;

    private String dataChildType = "全部已办结流程";
    private List<FlowExportJsonItemData> flowExportJsonItemDataList;

    public String getDateType() {
        return dateType;
    }

    public void setDateType(String dateType) {
        this.dateType = dateType;
    }

    public String getDataChildType() {
        return dataChildType;
    }

    public void setDataChildType(String dataChildType) {
        this.dataChildType = dataChildType;
    }

    public List<FlowExportJsonItemData> getFlowExportJsonItemDataList() {
        return flowExportJsonItemDataList;
    }

    public void setFlowExportJsonItemDataList(List<FlowExportJsonItemData> flowExportJsonItemDataList) {
        this.flowExportJsonItemDataList = flowExportJsonItemDataList;
    }

}
