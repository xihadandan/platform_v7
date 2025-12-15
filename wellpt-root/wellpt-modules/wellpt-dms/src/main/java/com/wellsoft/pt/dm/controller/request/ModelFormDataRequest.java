package com.wellsoft.pt.dm.controller.request;

import com.wellsoft.pt.dm.enums.AllowAccessType;

import java.util.List;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2023年06月12日   Qiong	 Create
 * </pre>
 */
public class ModelFormDataRequest {

    private String formDataJson;

    private List<AllowAccessType> allowAccessTypes;


    public String getFormDataJson() {
        return formDataJson;
    }

    public void setFormDataJson(String formDataJson) {
        this.formDataJson = formDataJson;
    }

    public List<AllowAccessType> getAllowAccessTypes() {
        return allowAccessTypes;
    }

    public void setAllowAccessTypes(List<AllowAccessType> allowAccessTypes) {
        this.allowAccessTypes = allowAccessTypes;
    }
}
