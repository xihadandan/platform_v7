package com.wellsoft.pt.security.audit.bean;

import com.google.common.collect.Lists;

import java.io.Serializable;
import java.util.List;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2020年08月20日   chenq	 Create
 * </pre>
 */
public class RoleDto implements Serializable {
    private String uuid;
    private String id;
    private String code;
    private String name;
    private String remark;
    private Integer recVer;
    private String appId;
    private String system;
    private String tenant;
    private Integer systemDef = 0;

    private List<RoleDto> nestedRoles = Lists.newArrayList();

    private List<PrivilegeDto> privileges = Lists.newArrayList();


    public RoleDto() {
    }

    public RoleDto(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public RoleDto(String uuid, String id, String name) {
        this.uuid = uuid;
        this.id = id;
        this.name = name;
    }

    public RoleDto(String uuid, String id, String name, String remark, Integer recVer, String appId) {
        this.uuid = uuid;
        this.id = id;
        this.name = name;
        this.remark = remark;
        this.recVer = recVer;
        this.appId = appId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Integer getRecVer() {
        return recVer;
    }

    public void setRecVer(Integer recVer) {
        this.recVer = recVer;
    }

    public List<RoleDto> getNestedRoles() {
        return nestedRoles;
    }

    public void setNestedRoles(List<RoleDto> nestedRoles) {
        this.nestedRoles = nestedRoles;
    }

    public List<PrivilegeDto> getPrivileges() {
        return privileges;
    }

    public void setPrivileges(List<PrivilegeDto> privileges) {
        this.privileges = privileges;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getSystem() {
        return system;
    }

    public void setSystem(String system) {
        this.system = system;
    }

    public String getTenant() {
        return tenant;
    }

    public void setTenant(String tenant) {
        this.tenant = tenant;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Integer getSystemDef() {
        return systemDef;
    }

    public void setSystemDef(Integer systemDef) {
        this.systemDef = systemDef;
    }
}
