package com.wellsoft.pt.ei.dto.flow.exportNew;


import com.wellsoft.pt.ei.annotate.FieldType;
import com.wellsoft.pt.ei.constants.ExportFieldTypeEnum;

import java.io.Serializable;
import java.util.Date;

public class FlowInstanceExportData implements Serializable {

//    // UUID
//    @FieldType(desc = "UUID，系统字段", type = ExportFieldTypeEnum.STRING)
//    protected String uuid;

    /**
     * 标题
     */
    @FieldType(desc = "标题", type = ExportFieldTypeEnum.STRING)
    private String flow_title;

    // 创建人
    @FieldType(desc = "创建人", type = ExportFieldTypeEnum.STRING)
    private String flow_creator_name;

    // 创建时间
    @FieldType(desc = "创建时间", type = ExportFieldTypeEnum.DATE)
    private Date flow_create_time;

    // 已办人
    @FieldType(desc = "已办人", type = ExportFieldTypeEnum.STRING)
    private String flow_done_user_name;

//    --------------------

//    public String getUuid() {
//        return uuid;
//    }
//
//    public void setUuid(String uuid) {
//        this.uuid = uuid;
//    }

    public String getFlow_title() {
        return flow_title;
    }

    public void setFlow_title(String flow_title) {
        this.flow_title = flow_title;
    }

    public String getFlow_creator_name() {
        return flow_creator_name;
    }

    public void setFlow_creator_name(String flow_creator_name) {
        this.flow_creator_name = flow_creator_name;
    }

    public Date getFlow_create_time() {
        return flow_create_time;
    }

    public void setFlow_create_time(Date flow_create_time) {
        this.flow_create_time = flow_create_time;
    }

    public String getFlow_done_user_name() {
        return flow_done_user_name;
    }

    public void setFlow_done_user_name(String flow_done_user_name) {
        this.flow_done_user_name = flow_done_user_name;
    }
}
