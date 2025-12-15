package com.wellsoft.pt.repository.support.enums;

public enum EnumReplicaType {
    SWF("SWF", "swf", "flash"), PDF("PDF", "pdf", "");

    private String type;//存放到数据的类型
    private String extName;//文件扩展名
    private String remark;//备注

    private EnumReplicaType(String type, String extName, String remark) {
        this.type = type;
        this.extName = type;
        this.remark = remark;
    }

    public String getType() {
        return type;
    }

    public String getExtName() {
        return extName;
    }

    public String getRemark() {
        return remark;
    }


}
