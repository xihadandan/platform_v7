/*
 * @(#)2019年6月4日 V1.0
 *
 * Copyright 2019 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.manager.facade.service.impl;

import com.wellsoft.context.component.select2.Select2QueryData;
import com.wellsoft.context.component.select2.Select2QueryInfo;
import com.wellsoft.pt.app.entity.AppModule;
import com.wellsoft.pt.app.entity.AppProductIntegration;
import com.wellsoft.pt.app.facade.service.AppModuleMgr;
import com.wellsoft.pt.app.manager.dto.AppModuleDto;
import com.wellsoft.pt.app.manager.facade.service.AppModuleManager;
import com.wellsoft.pt.app.service.AppModuleService;
import com.wellsoft.pt.app.service.AppProductIntegrationService;
import com.wellsoft.pt.app.support.AppType;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2019年6月4日.1	zhulh		2019年6月4日		Create
 * </pre>
 * @date 2019年6月4日
 */
@Service
public class AppModuleManagerImpl implements AppModuleManager {

    @Autowired
    private AppProductIntegrationService appProductIntegrationService;

    @Autowired
    private AppModuleMgr appModuleMgr;

    @Autowired
    private AppModuleService appModuleService;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.manager.facade.service.AppModuleManager#saveDto(com.wellsoft.pt.app.manager.dto.AppModuleDto)
     */
    @Override
    @Transactional
    public void saveDto(AppModuleDto appModuleDto) {
        // 保存模块信息
        AppModule appModule = appModuleMgr.saveBean(appModuleDto);
        // 新增加入产品集成
        if (StringUtils.isBlank(appModuleDto.getUuid()) || StringUtils.isBlank(appModuleDto.getAppPiUuid())) {
            // 添加模块的产品集成信息，从全部模块添加的模块，归属产品UUID、产品UUID为空
            String belongToAppPiUuid = appModuleDto.getBelongToAppPiUuid();
            if (StringUtils.isBlank(belongToAppPiUuid)
                    && StringUtils.isNotBlank(appModuleDto.getBelongToAppSystemUuid())) {
                AppProductIntegration example = new AppProductIntegration();
                if (StringUtils.isNotBlank(appModuleDto.getAppProductUuid())) {
                    example.setAppProductUuid(appModuleDto.getAppProductUuid());
                }
                example.setAppSystemUuid(appModuleDto.getBelongToAppSystemUuid());
                example.setDataType(AppType.SYSTEM.toString());
                List<AppProductIntegration> appProductIntegrations = appProductIntegrationService
                        .findByExample(example);
                if (CollectionUtils.isNotEmpty(appProductIntegrations)) {
                    belongToAppPiUuid = appProductIntegrations.get(0).getUuid();
                }
            }
            if (StringUtils.isNotBlank(belongToAppPiUuid)) {
                appProductIntegrationService.addAppModule(belongToAppPiUuid, appModule);
            }
        } else {
            // 是否有变更归属系统信息
            boolean isChangeBelongToAppSystemUuid = isChangeBelongToAppSystemUuid(appModuleDto);
            // 更新归属系统信息
            if (isChangeBelongToAppSystemUuid) {
                // 归属系统变更为空，有子结点时，不可变更
                if (StringUtils.isBlank(appModuleDto.getBelongToAppSystemUuid())) {
                    if (appProductIntegrationService.hasChildren(appModuleDto.getAppPiUuid())) {
                        throw new RuntimeException("模块下有子模块或应用，归属系统不能变更为空！");
                    } else {
                        appProductIntegrationService.remove(appModuleDto.getAppPiUuid());
                    }
                } else {
                    // 获取归属系统的集成信息UUID
                    AppProductIntegration example = new AppProductIntegration();
                    example.setAppProductUuid(appModuleDto.getAppProductUuid());
                    example.setAppSystemUuid(appModuleDto.getBelongToAppSystemUuid());
                    example.setDataType(AppType.SYSTEM.toString());
                    List<AppProductIntegration> appProductIntegrations = appProductIntegrationService
                            .findByExample(example);
                    if (CollectionUtils.isNotEmpty(appProductIntegrations)) {
                        appProductIntegrationService.updateParentUuid(appModuleDto.getAppPiUuid(),
                                appProductIntegrations.get(0).getUuid());
                    }
                }
            }
        }
    }

    /**
     * @param appSystemDto
     * @return
     */
    private boolean isChangeBelongToAppSystemUuid(AppModuleDto appModuleDto) {
        String belongToAppSystemUuid = appModuleDto.getBelongToAppSystemUuid();
        String appPiUuid = appModuleDto.getAppPiUuid();
        if (StringUtils.isNotBlank(appPiUuid)) {
            String appSystemUuid = appProductIntegrationService.getAppSystemUuidByUuid(appPiUuid);
            if (StringUtils.isNotBlank(appSystemUuid) && !StringUtils.equals(appSystemUuid, belongToAppSystemUuid)) {
                return true;
            }
        }
        return false;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.context.component.select2.Select2QueryApi#loadSelectData(com.wellsoft.context.component.select2.Select2QueryInfo)
     */
    @Override
    public Select2QueryData loadSelectData(Select2QueryInfo select2QueryInfo) {
        String queryValue = select2QueryInfo.getSearchValue();
        String appPiUuid = select2QueryInfo.getOtherParams("appPiUuid", "");
        String systemUnitId = SpringSecurityUtils.getCurrentUserUnitId();
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("name", queryValue);
        values.put("id", queryValue);
        values.put("appPiUuid", appPiUuid);
        values.put("systemUnitId", systemUnitId);
        List<AppModule> list = appModuleService.listByNameSQLQueryAndPage("appModuleManagerSelect2Query", values,
                select2QueryInfo.getPagingInfo(), null);
        return new Select2QueryData(list, "id", "name", select2QueryInfo.getPagingInfo());
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.context.component.select2.Select2QueryApi#loadSelectDataByIds(com.wellsoft.context.component.select2.Select2QueryInfo)
     */
    @Override
    public Select2QueryData loadSelectDataByIds(Select2QueryInfo select2QueryInfo) {
        String[] appSysIds = select2QueryInfo.getIds();
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("appModuleIds", appSysIds);
        List<AppModule> list = appModuleService.listByNameHQLQueryAndPage("appModuleManagerSelect2IdsQuery", values,
                select2QueryInfo.getPagingInfo());
        return new Select2QueryData(list, "id", "name", select2QueryInfo.getPagingInfo());
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.manager.facade.service.AppModuleManager#remove(com.wellsoft.pt.app.manager.dto.AppModuleDto)
     */
    @Override
    public void remove(AppModuleDto appModuleDto) {
        String appPiUuid = appModuleDto.getAppPiUuid();
        // 删除产品集成
        if (StringUtils.isNotBlank(appPiUuid)) {
            if (appProductIntegrationService.hasChildren(appPiUuid)) {
                throw new RuntimeException("模块下有子模块或应用，不可删除!");
            }
            appProductIntegrationService.remove(appPiUuid);
        }
        // 删除模块
        appModuleMgr.remove(appModuleDto.getUuid());
    }

}
