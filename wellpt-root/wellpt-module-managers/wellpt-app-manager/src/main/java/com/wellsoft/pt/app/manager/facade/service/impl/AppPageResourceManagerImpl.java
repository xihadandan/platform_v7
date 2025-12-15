/*
 * @(#)2019年6月11日 V1.0
 *
 * Copyright 2019 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.manager.facade.service.impl;

import com.google.common.collect.Lists;
import com.wellsoft.pt.app.manager.dto.AppPageResourceDto;
import com.wellsoft.pt.app.manager.facade.service.AppPageResourceManager;
import com.wellsoft.pt.app.service.AppPageResourceService;
import com.wellsoft.pt.app.support.AppConstants;
import com.wellsoft.pt.security.facade.service.SecurityApiFacade;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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
public class AppPageResourceManagerImpl implements AppPageResourceManager {

    @Autowired
    private AppPageResourceService appPageResourceService;

    @Autowired
    private SecurityApiFacade securityApiFacade;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.manager.facade.service.AppPageResourceManager#remove(java.util.List)
     */
    @Override
    public void remove(List<AppPageResourceDto> appPageResourceDtos) {
        List<String> uuids = Lists.newArrayList();
        for (AppPageResourceDto appPageResourceDto : appPageResourceDtos) {
            if (StringUtils.isNotBlank(appPageResourceDto.getUuid())) {
                uuids.add(appPageResourceDto.getUuid());
            }
            // 删除权限控制的资源
            String pageResourceId = getPageResourceId(appPageResourceDto);
            securityApiFacade.removeResourceAttribute(pageResourceId);
        }
        appPageResourceService.deleteByUuids(uuids);
    }

    /**
     * @param appPageResourceDto
     * @return
     */
    private String getPageResourceId(AppPageResourceDto appPageResourceDto) {
        return AppConstants.FUNCTIONREF_OF_PAGE_PREFIX + "_" + appPageResourceDto.getAppPiUuid() + "_"
                + appPageResourceDto.getAppPageUuid() + "_" + appPageResourceDto.getAppFunctionUuid();
    }

}
