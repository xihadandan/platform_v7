package com.wellsoft.pt.security.audit.bean;

import java.io.Serializable;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2023年07月14日   chenq	 Create
 * </pre>
 */
public class ResourceDto implements Serializable {
    private String uuid;
    private String code;
    private String remark;
    private String applyTo;
    private String url;
    private String name;
    private String moduleId;

    public ResourceDto() {
    }

    public ResourceDto(String uuid, String code, String name, String url, String applyTo, String remark) {
        this.uuid = uuid;
        this.code = code;
        this.remark = remark;
        this.applyTo = applyTo;
        this.url = url;
        this.name = name;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getApplyTo() {
        return applyTo;
    }

    public void setApplyTo(String applyTo) {
        this.applyTo = applyTo;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getModuleId() {
        return moduleId;
    }

    public void setModuleId(String moduleId) {
        this.moduleId = moduleId;
    }
}
