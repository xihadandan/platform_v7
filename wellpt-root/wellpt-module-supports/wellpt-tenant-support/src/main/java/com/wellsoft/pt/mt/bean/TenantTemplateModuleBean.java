package com.wellsoft.pt.mt.bean;

import com.wellsoft.pt.mt.entity.TenantTemplateModule;

public class TenantTemplateModuleBean extends TenantTemplateModule {
    private String moduleName;

    private String delRepoFileUuids;

    private String delRepoFileNames;

    public String getDelRepoFileUuids() {
        return delRepoFileUuids;
    }

    public void setDelRepoFileUuids(String delRepoFileUuids) {
        this.delRepoFileUuids = delRepoFileUuids;
    }

    public String getDelRepoFileNames() {
        return delRepoFileNames;
    }

    public void setDelRepoFileNames(String delRepoFileNames) {
        this.delRepoFileNames = delRepoFileNames;
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }
}
