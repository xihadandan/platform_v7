package com.wellsoft.pt.app.dto;

import com.wellsoft.pt.app.entity.AppProdAnonUrlEntity;
import com.wellsoft.pt.app.entity.AppProdModuleEntity;
import com.wellsoft.pt.app.entity.AppProdVersionSettingEntity;
import com.wellsoft.pt.app.entity.AppProductVersionLogEntity;

import java.io.Serializable;
import java.util.List;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2023年07月31日   chenq	 Create
 * </pre>
 */
public class AppProdVersionDto implements Serializable {

    private Long uuid;

    private String version;

    private String versionId;

    private AppProductVersionLogEntity log;

    private AppProductDto product;

    private AppProdVersionSettingEntity setting;

    private List<AppProdAnonUrlEntity> anonUrls;

    private List<AppProdModuleEntity> modules;

    public Long getUuid() {
        return uuid;
    }

    public void setUuid(Long uuid) {
        this.uuid = uuid;
    }

    public AppProductVersionLogEntity getLog() {
        return log;
    }

    public void setLog(AppProductVersionLogEntity log) {
        this.log = log;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getVersionId() {
        return versionId;
    }

    public void setVersionId(String versionId) {
        this.versionId = versionId;
    }

    public AppProdVersionSettingEntity getSetting() {
        return setting;
    }

    public void setSetting(AppProdVersionSettingEntity setting) {
        this.setting = setting;
    }

    public List<AppProdAnonUrlEntity> getAnonUrls() {
        return anonUrls;
    }

    public void setAnonUrls(List<AppProdAnonUrlEntity> anonUrls) {
        this.anonUrls = anonUrls;
    }


    public List<AppProdModuleEntity> getModules() {
        return modules;
    }

    public void setModules(List<AppProdModuleEntity> modules) {
        this.modules = modules;
    }

    public AppProductDto getProduct() {
        return product;
    }

    public void setProduct(AppProductDto product) {
        this.product = product;
    }
}
