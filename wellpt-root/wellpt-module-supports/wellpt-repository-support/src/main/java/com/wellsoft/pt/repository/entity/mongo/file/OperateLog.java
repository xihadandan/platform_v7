package com.wellsoft.pt.repository.entity.mongo.file;


public class OperateLog {
    private String operate_type;
    private String operator;
    private String operator_time;
    private String folder_id;

    public String getOperate_type() {
        return operate_type;
    }

    public void setOperate_type(String operate_type) {
        this.operate_type = operate_type;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getOperator_time() {
        return operator_time;
    }

    public void setOperator_time(String operator_time) {
        this.operator_time = operator_time;
    }

    public String getFolder_id() {
        return folder_id;
    }

    public void setFolder_id(String folder_id) {
        this.folder_id = folder_id;
    }


}
