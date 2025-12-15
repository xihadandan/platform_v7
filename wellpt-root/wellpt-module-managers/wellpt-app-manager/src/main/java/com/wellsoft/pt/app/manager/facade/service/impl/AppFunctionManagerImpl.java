/*
 * @(#)2019年6月11日 V1.0
 *
 * Copyright 2019 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.manager.facade.service.impl;

import com.wellsoft.pt.app.entity.AppProductIntegration;
import com.wellsoft.pt.app.manager.dto.AppFunctionDto;
import com.wellsoft.pt.app.manager.facade.service.AppFunctionManager;
import com.wellsoft.pt.app.service.AppProductIntegrationService;
import com.wellsoft.pt.app.support.AppCacheUtils;
import com.wellsoft.pt.app.support.AppType;
import com.wellsoft.pt.security.facade.service.SecurityApiFacade;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2019年6月11日.1	zhulh		2019年6月11日		Create
 * </pre>
 * @date 2019年6月11日
 */
@Service
public class AppFunctionManagerImpl implements AppFunctionManager {

    @Autowired
    private AppProductIntegrationService appProductIntegrationService;

    @Autowired
    private SecurityApiFacade securityApiFacade;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.manager.facade.service.AppFunctionManager#addPiFunction(java.lang.String, com.wellsoft.pt.app.manager.dto.AppFunctionDto)
     */
    @Override
    @Transactional
    public void addPiFunction(String piUuid, AppFunctionDto appFunctionDto) {
        boolean isProtected = Boolean.TRUE.equals(appFunctionDto.getIsProtected());
        AppProductIntegration appProductIntegration = appProductIntegrationService.addAppFunction(piUuid,
                appFunctionDto, isProtected);
        AppCacheUtils.clear();
        // 增加不受权限控制的资源
        if (!isProtected && appProductIntegration != null) {
            securityApiFacade.addAnonymousResource(appProductIntegration.getUuid());
        }
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.manager.facade.service.AppFunctionManager#removePiFunction(java.lang.String)
     */
    @Override
    public void removePiFunction(String piUuid) {
        AppProductIntegration appProductIntegration = appProductIntegrationService.get(piUuid);
        if (!StringUtils.equals(AppType.FUNCTION.toString(), appProductIntegration.getDataType())) {
            throw new RuntimeException("数据错误，不能删除功能！");
        }
        appProductIntegrationService.remove(piUuid);
        AppCacheUtils.clear();
        // 删除权限控制的资源
        securityApiFacade.removeResourceAttribute(piUuid);
    }

    @Override
    @Transactional
    public void updatePiFunction(String piUuid, boolean flg) {
        AppProductIntegration appProductIntegration = appProductIntegrationService.get(piUuid);
        if (appProductIntegration == null) {
            return;
        }
        appProductIntegration.setIsProtected(flg);
        appProductIntegrationService.update(appProductIntegration);
        if (!flg) {
            securityApiFacade.addAnonymousResource(piUuid);
        }
    }
}
