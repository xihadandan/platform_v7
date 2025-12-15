package com.wellsoft.pt.mt.bean;

import com.wellsoft.context.annotation.UnCloneable;
import com.wellsoft.pt.mt.entity.TenantTemplate;

import java.util.Set;

public class TenantTemplateBean extends TenantTemplate {

    private String delDdlFileNames;
    // DDL_FILE_UUIDS
    private String delDdlFileUuids;
    // DML_FILE_NAMES
    private String delDmlFileNames;
    // DML_FILE_UUIDS
    private String delDmlFileUuids;

    @UnCloneable
    private Set<TenantTemplateModuleBean> tenantTemplateModuleBeans;

    public Set<TenantTemplateModuleBean> getTenantTemplateModuleBeans() {
        return tenantTemplateModuleBeans;
    }

    public void setTenantTemplateModuleBeans(Set<TenantTemplateModuleBean> tenantTemplateModuleBeans) {
        this.tenantTemplateModuleBeans = tenantTemplateModuleBeans;
    }

    public String getDelDdlFileNames() {
        return delDdlFileNames;
    }

    public void setDelDdlFileNames(String delDdlFileNames) {
        this.delDdlFileNames = delDdlFileNames;
    }

    public String getDelDdlFileUuids() {
        return delDdlFileUuids;
    }

    public void setDelDdlFileUuids(String delDdlFileUuids) {
        this.delDdlFileUuids = delDdlFileUuids;
    }

    public String getDelDmlFileNames() {
        return delDmlFileNames;
    }

    public void setDelDmlFileNames(String delDmlFileNames) {
        this.delDmlFileNames = delDmlFileNames;
    }

    public String getDelDmlFileUuids() {
        return delDmlFileUuids;
    }

    public void setDelDmlFileUuids(String delDmlFileUuids) {
        this.delDmlFileUuids = delDmlFileUuids;
    }
}
