package com.wellsoft.pt.security.iexport.acceptor;

import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.app.entity.AppFunction;
import com.wellsoft.pt.app.entity.AppPageDefinition;
import com.wellsoft.pt.app.entity.AppProductIntegration;
import com.wellsoft.pt.app.service.AppFunctionService;
import com.wellsoft.pt.app.service.AppPageDefinitionService;
import com.wellsoft.pt.app.service.AppProductIntegrationService;
import com.wellsoft.pt.app.support.AppConstants;
import com.wellsoft.pt.app.support.AppType;
import com.wellsoft.pt.basicdata.iexport.acceptor.IexportData;
import com.wellsoft.pt.basicdata.iexport.suport.IexportDataResultSetUtils;
import com.wellsoft.pt.basicdata.iexport.suport.IexportType;
import com.wellsoft.pt.security.audit.entity.PrivilegeResource;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @author yt
 * @title: PrivilegeResourceIexportData
 * @date 2020/9/17 15:20
 */
public class PrivilegeResourceIexportData extends IexportData {
    public PrivilegeResource privilegeResource;

    public PrivilegeResourceIexportData(PrivilegeResource privilegeResource) {
        this.privilegeResource = privilegeResource;
    }


    @Override
    public String getUuid() {
        return privilegeResource.getUuid();
    }

    @Override
    public String getName() {
        String uuid = this.privilegeResource.getResourceUuid();
        String name = IexportType.ErrorData + "引用资源不存在：" + uuid;
        if (uuid.startsWith(AppConstants.PAGE_PREFIX) || uuid.startsWith(AppConstants.PAGEREF_PREFIX)) {
            AppPageDefinitionService appPageDefinitionService = ApplicationContextHolder.getBean(AppPageDefinitionService.class);
            uuid = uuid.substring(uuid.lastIndexOf(Separator.UNDERLINE.getValue()) + 1);
            AppPageDefinition appPageDefinition = appPageDefinitionService.get(uuid);
            if (appPageDefinition != null) {
                name = "引用资源：页面-" + appPageDefinition.getName();
            }
        } else if (uuid.startsWith(AppConstants.FUNCTIONREF_OF_PAGE_PREFIX)) {
            AppFunctionService appFunctionService = ApplicationContextHolder.getBean(AppFunctionService.class);
            uuid = uuid.substring(uuid.lastIndexOf(Separator.UNDERLINE.getValue()) + 1);
            AppFunction appFunction = appFunctionService.get(uuid);
            if (appFunction != null) {
                name = "引用资源：功能-" + appFunction.getName();
            }
        } else {
            AppProductIntegrationService appProductIntegrationService = ApplicationContextHolder.getBean(AppProductIntegrationService.class);
            uuid = privilegeResource.getResourceUuid();
            AppProductIntegration appProductIntegration = appProductIntegrationService.get(uuid);
            if (appProductIntegration != null) {
                name = "引用资源：" + AppType.getName(Integer.valueOf(appProductIntegration.getDataType())) + "-" + appProductIntegration.getDataName();
            }
        }
        return name;
    }

    @Override
    public Integer getRecVer() {
        return privilegeResource.getRecVer();
    }

    @Override
    public String getType() {
        return IexportType.PrivilegeResource;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.iexport.acceptor.IexportData#getInputStream()
     */
    @Override
    public InputStream getInputStream() throws IOException {
        return IexportDataResultSetUtils.iexportDataResultSet2InputStream(this, privilegeResource);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.iexport.acceptor.IexportData#getDependencies()
     */
    @Override
    public List<IexportData> getDependencies() {
        List<IexportData> dependencies = new ArrayList<IexportData>();
        return dependencies;
    }

}
