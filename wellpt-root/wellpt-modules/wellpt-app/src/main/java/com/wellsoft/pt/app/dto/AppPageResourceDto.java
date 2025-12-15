package com.wellsoft.pt.app.dto;

import com.wellsoft.pt.app.entity.AppFunction;

import java.io.Serializable;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2023年07月12日   chenq	 Create
 * </pre>
 */
public class AppPageResourceDto implements Serializable {
    private static final long serialVersionUID = -7884075440671737824L;
    private String uuid;
    private String id;
    private String appPageUuid;
    private String configType;
    private String appFunctionUuid;
    private Boolean isProtected;

    private AppFunction appFunction;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getAppPageUuid() {
        return appPageUuid;
    }

    public void setAppPageUuid(String appPageUuid) {
        this.appPageUuid = appPageUuid;
    }

    public String getConfigType() {
        return configType;
    }

    public void setConfigType(String configType) {
        this.configType = configType;
    }

    public String getAppFunctionUuid() {
        return appFunctionUuid;
    }

    public void setAppFunctionUuid(String appFunctionUuid) {
        this.appFunctionUuid = appFunctionUuid;
    }

    public Boolean getIsProtected() {
        return isProtected;
    }

    public void setIsProtected(Boolean aProtected) {
        isProtected = aProtected;
    }

    public AppFunction getAppFunction() {
        return appFunction;
    }

    public void setAppFunction(AppFunction appFunction) {
        this.appFunction = appFunction;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
