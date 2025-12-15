package com.wellsoft.pt.dyform.implement.definition.control.enums;

import com.wellsoft.pt.dyform.implement.definition.control.bean.FieldPropertyBean;

import java.util.ArrayList;
import java.util.List;

/**
 * 表单附件字段布尔类型的配置
 *
 * @author hongjz
 */
public enum EnumFileuploadFieldProperties {
    ALLOW_DELETED("allowDelete", "删除"), ALLOW_UPLOAD("allowUpload", "上传"), ALLOW_DOWNLOAD("allowDownload", "下载");
    private String value;
    private String remark;

    private EnumFileuploadFieldProperties(String value, String remark) {
        this.value = value;
        this.remark = remark;
    }

    public static List<FieldPropertyBean> toFieldPropertyBeanList() {
        List<FieldPropertyBean> beans = new ArrayList<FieldPropertyBean>();
        for (EnumFileuploadFieldProperties config : EnumFileuploadFieldProperties.values()) {
            FieldPropertyBean bean = new FieldPropertyBean(config.getValue(), config.getRemark());
            beans.add(bean);
        }
        return beans;
    }

    public String getValue() {
        return value;
    }

    public String getRemark() {
        return remark;
    }

}
