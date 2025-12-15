/*
 * @(#)2019年6月6日 V1.0
 *
 * Copyright 2019 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.manager.facade.service.impl;

import com.wellsoft.pt.app.entity.AppApplication;
import com.wellsoft.pt.app.facade.service.AppApplicationMgr;
import com.wellsoft.pt.app.manager.dto.AppApplicationDto;
import com.wellsoft.pt.app.manager.facade.service.AppApplicationManager;
import com.wellsoft.pt.app.service.AppProductIntegrationService;
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
 * 2019年6月6日.1	zhulh		2019年6月6日		Create
 * </pre>
 * @date 2019年6月6日
 */
@Service
public class AppApplicationManagerImpl implements AppApplicationManager {

    @Autowired
    private AppApplicationMgr appApplicationMgr;

    @Autowired
    private AppProductIntegrationService appProductIntegrationService;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.manager.facade.service.AppApplicationManager#saveDto(com.wellsoft.pt.app.manager.dto.AppApplicationDto)
     */
    @Override
    @Transactional
    public void saveDto(AppApplicationDto appApplicationDto) {
        // 保存应用信息
        AppApplication appApplication = appApplicationMgr.saveBean(appApplicationDto);
        // 添加模块的产品集成信息
        if (StringUtils.isBlank(appApplicationDto.getUuid()) || StringUtils.isBlank(appApplicationDto.getAppPiUuid())) {
            String belongToAppPiUuid = appApplicationDto.getBelongToAppPiUuid();
            if (StringUtils.isNotBlank(belongToAppPiUuid)) {
                appProductIntegrationService.addAppApplication(belongToAppPiUuid, appApplication);
            }
        } else {
            // 是否有变更归属模块信息
            boolean isChangeBelongToAppModulePiUuid = isChangeBelongToAppModulePiUuid(appApplicationDto);
            // 更新归属模块信息
            if (isChangeBelongToAppModulePiUuid) {
                // 归属系统变更为空，有子结点时，不可变更
                if (StringUtils.isBlank(appApplicationDto.getBelongToAppPiUuid())) {
                    if (appProductIntegrationService.hasChildren(appApplicationDto.getAppPiUuid())) {
                        throw new RuntimeException("应用下有功能，归属模块不能变更为空！");
                    } else {
                        appProductIntegrationService.remove(appApplicationDto.getAppPiUuid());
                    }
                } else {
                    appProductIntegrationService.updateParentUuid(appApplicationDto.getAppPiUuid(),
                            appApplicationDto.getBelongToAppPiUuid());
                }
            }
        }
    }

    /**
     * @param appApplicationDto
     * @return
     */
    private boolean isChangeBelongToAppModulePiUuid(AppApplicationDto appApplicationDto) {
        String belongToAppPiUuid = appApplicationDto.getBelongToAppPiUuid();
        String parentAppPiUuid = appApplicationDto.getParentAppPiUuid();
        if (StringUtils.isNotBlank(appApplicationDto.getUuid())
                && !StringUtils.equals(parentAppPiUuid, belongToAppPiUuid)) {
            return true;
        }
        return false;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.manager.facade.service.AppApplicationManager#remove(com.wellsoft.pt.app.manager.dto.AppApplicationDto)
     */
    @Override
    public void remove(AppApplicationDto appApplicationDto) {
        String appPiUuid = appApplicationDto.getAppPiUuid();
        // 删除产品集成
        if (StringUtils.isNotBlank(appPiUuid)) {
            if (appProductIntegrationService.hasChildren(appPiUuid)) {
                throw new RuntimeException("应用有功能，不可删除!");
            }
            appProductIntegrationService.remove(appPiUuid);
        }
        // 删除应用
        appApplicationMgr.remove(appApplicationDto.getUuid());
    }

}
