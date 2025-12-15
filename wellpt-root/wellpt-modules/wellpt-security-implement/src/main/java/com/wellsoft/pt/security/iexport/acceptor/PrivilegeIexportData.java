/*
 * @(#)2015-6-16 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
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
import com.wellsoft.pt.basicdata.iexport.acceptor.IexportData;
import com.wellsoft.pt.basicdata.iexport.suport.IexportDataProviderFactory;
import com.wellsoft.pt.basicdata.iexport.suport.IexportDataResultSetUtils;
import com.wellsoft.pt.basicdata.iexport.suport.IexportType;
import com.wellsoft.pt.security.audit.entity.Privilege;
import com.wellsoft.pt.security.audit.entity.PrivilegeResource;
import com.wellsoft.pt.security.audit.entity.Resource;
import com.wellsoft.pt.security.audit.service.PrivilegeResourceService;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2015-6-16.1	zhulh		2015-6-16		Create
 * </pre>
 * @date 2015-6-16
 */
public class PrivilegeIexportData extends IexportData {
    public Privilege privilege;

    public PrivilegeIexportData(Privilege privilege) {
        this.privilege = privilege;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.iexport.acceptor.IexportData#getUuid()
     */
    @Override
    public String getUuid() {
        return privilege.getUuid();
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.iexport.acceptor.Entry#getName()
     */
    @Override
    public String getName() {
        return "权限：" + privilege.getName();
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.iexport.acceptor.Entry#getType()
     */
    @Override
    public String getType() {
        return IexportType.Privilege;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.iexport.acceptor.IexportData#getInputStream()
     */
    @Override
    public InputStream getInputStream() throws IOException {
        return IexportDataResultSetUtils.iexportDataResultSet2InputStream(this, privilege);
    }

    @Override
    public Integer getRecVer() {
        // TODO Auto-generated method stub
        return privilege.getRecVer();
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.iexport.acceptor.IexportData#getDependencies()
     */
    @Override
    public List<IexportData> getDependencies() {
        List<IexportData> dependencies = new ArrayList<IexportData>();
        Set<Resource> resources = privilege.getResources();
        for (Resource resource : resources) {
            IexportData dependency = IexportDataProviderFactory.getIexportData(IexportType.ResourceParent,
                    resource.getUuid());
            dependencies.add(dependency);
        }
        PrivilegeResourceService privilegeResourceService = ApplicationContextHolder.getBean(PrivilegeResourceService.class);
        List<PrivilegeResource> list = privilegeResourceService.getByPrivilegeUuid(privilege.getUuid());
        for (PrivilegeResource privilegeResource : list) {
            String uuid = privilegeResource.getResourceUuid();
            if (uuid.startsWith(AppConstants.PAGE_PREFIX) || uuid.startsWith(AppConstants.PAGEREF_PREFIX)) {
                AppPageDefinitionService appPageDefinitionService = ApplicationContextHolder.getBean(AppPageDefinitionService.class);
                uuid = uuid.substring(uuid.lastIndexOf(Separator.UNDERLINE.getValue()) + 1);
                AppPageDefinition appPageDefinition = appPageDefinitionService.get(uuid);
                if (appPageDefinition == null) {
                    continue;
                }
            } else if (uuid.startsWith(AppConstants.FUNCTIONREF_OF_PAGE_PREFIX)) {
                AppFunctionService appFunctionService = ApplicationContextHolder.getBean(AppFunctionService.class);
                uuid = uuid.substring(uuid.lastIndexOf(Separator.UNDERLINE.getValue()) + 1);
                AppFunction appFunction = appFunctionService.get(uuid);
                if (appFunction == null) {
                    continue;
                }
            } else {
                AppProductIntegrationService appProductIntegrationService = ApplicationContextHolder.getBean(AppProductIntegrationService.class);
                uuid = privilegeResource.getResourceUuid();
                AppProductIntegration appProductIntegration = appProductIntegrationService.get(uuid);
                if (appProductIntegration == null) {
                    continue;
                }
            }
            IexportData dependency = IexportDataProviderFactory.getIexportData(IexportType.PrivilegeResource, privilegeResource.getUuid());
            dependencies.add(dependency);
        }
        return dependencies;
    }

}
