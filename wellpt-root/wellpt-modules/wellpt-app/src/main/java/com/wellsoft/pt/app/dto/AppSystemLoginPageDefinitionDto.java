package com.wellsoft.pt.app.dto;

import com.wellsoft.pt.app.entity.AppSystemLoginPolicyEntity;

import java.io.Serializable;
import java.util.List;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2024年04月07日   chenq	 Create
 * </pre>
 */
public class AppSystemLoginPageDefinitionDto implements Serializable {

    private String defJson;
    private String name;
    private String title;
    private String remark;

    private List<AppSystemLoginPolicyEntity> loginPolicy;

    public String getDefJson() {
        return defJson;
    }

    public void setDefJson(String defJson) {
        this.defJson = defJson;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public List<AppSystemLoginPolicyEntity> getLoginPolicy() {
        return loginPolicy;
    }

    public void setLoginPolicy(List<AppSystemLoginPolicyEntity> loginPolicy) {
        this.loginPolicy = loginPolicy;
    }
}
