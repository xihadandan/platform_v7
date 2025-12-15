/*
 * @(#)2019年5月29日 V1.0
 *
 * Copyright 2019 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.manager.facade.service.impl;

import com.google.common.collect.Lists;
import com.wellsoft.pt.app.entity.AppProduct;
import com.wellsoft.pt.app.entity.AppProductIntegration;
import com.wellsoft.pt.app.manager.dto.AppBreadcrumbsDataItemDto;
import com.wellsoft.pt.app.manager.dto.AppBreadcrumbsParamDto;
import com.wellsoft.pt.app.manager.facade.service.AppBreadcrumbsFacadeService;
import com.wellsoft.pt.app.service.AppProductIntegrationService;
import com.wellsoft.pt.app.service.AppProductService;
import com.wellsoft.pt.app.support.AppType;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2019年5月29日.1	zhulh		2019年5月29日		Create
 * </pre>
 * @date 2019年5月29日
 */
@Service
public class AppBreadcrumbsFacadeServiceImpl implements AppBreadcrumbsFacadeService {

    @Autowired
    private AppProductService appProductService;

    @Autowired
    private AppProductIntegrationService appProductIntegrationService;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.manager.facade.service.AppBreadcrumbsFacadeService#getAppBreadcrumbsDataList(com.wellsoft.pt.app.manager.dto.AppBreadcrumbsParamDto)
     */
    @Override
    public List<AppBreadcrumbsDataItemDto> getAppBreadcrumbsDataList(AppBreadcrumbsParamDto appBreadcrumbsParamDto) {
        List<AppBreadcrumbsDataItemDto> appBreadcrumbsDataItemDtos = Lists.newArrayList();
        String appPiUuid = appBreadcrumbsParamDto.getAppPiUuid();
        String appProductUuid = appBreadcrumbsParamDto.getAppProductUuid();
        if (StringUtils.isBlank(appProductUuid) || appProductUuid.equals("null")) {
            return appBreadcrumbsDataItemDtos;
        }
        // 添加产品导航数据
        addAppProductBreadcrumbs(appProductUuid, appBreadcrumbsDataItemDtos);
        // 产品集成UUID
        if (StringUtils.isNotBlank(appPiUuid)) {
            // 获取自身及所有上级的产品集成信息
            List<AppProductIntegration> appProductIntegrations = appProductIntegrationService
                    .getSelfWithParentsByUuid(appPiUuid);
            for (AppProductIntegration appProductIntegration : appProductIntegrations) {
                addAppProductIntegrationBreadcrumbs(appProductIntegration, appBreadcrumbsDataItemDtos);
            }
        }
        return appBreadcrumbsDataItemDtos;
    }

    /**
     * @param appProductIntegration
     * @param appBreadcrumbsDataItemDtos
     */
    private void addAppProductIntegrationBreadcrumbs(AppProductIntegration appProductIntegration,
                                                     List<AppBreadcrumbsDataItemDto> appBreadcrumbsDataItemDtos) {
        AppBreadcrumbsDataItemDto dataItemDto = new AppBreadcrumbsDataItemDto();
        dataItemDto.setNavId(UUID.randomUUID().toString());
        dataItemDto.setAppProductUuid(appProductIntegration.getAppProductUuid());
        dataItemDto.setAppSystemUuid(appProductIntegration.getAppSystemUuid());
        dataItemDto.setAppPiUuid(appProductIntegration.getUuid());
        dataItemDto.setParentAppPiUuid(appProductIntegration.getParentUuid());
        dataItemDto.setAppType(Integer.valueOf(appProductIntegration.getDataType()));
        dataItemDto.setLabel(appProductIntegration.getDataName());
        dataItemDto.setValue(appProductIntegration.getDataId());
        appBreadcrumbsDataItemDtos.add(dataItemDto);
    }

    /**
     * @param appProductUuid
     * @param appBreadcrumbsDataItemDtos
     */
    private void addAppProductBreadcrumbs(String appProductUuid,
                                          List<AppBreadcrumbsDataItemDto> appBreadcrumbsDataItemDtos) {
        AppProduct appProduct = appProductService.get(appProductUuid);
        if (appProduct == null) {
            return;
        }
        AppBreadcrumbsDataItemDto dataItemDto = new AppBreadcrumbsDataItemDto();
        dataItemDto.setNavId(UUID.randomUUID().toString());
        dataItemDto.setAppType(AppType.PRODUCT);
        dataItemDto.setAppProductUuid(appProduct.getUuid());
        dataItemDto.setLabel(appProduct.getName());
        dataItemDto.setValue(appProduct.getId());
        appBreadcrumbsDataItemDtos.add(dataItemDto);
    }

}
