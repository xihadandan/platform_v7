/*
 * @(#)2019年6月4日 V1.0
 *
 * Copyright 2019 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.manager.facade.service.impl;

import com.wellsoft.context.component.select2.Select2QueryData;
import com.wellsoft.context.component.select2.Select2QueryInfo;
import com.wellsoft.pt.app.entity.AppProductIntegration;
import com.wellsoft.pt.app.entity.AppSystem;
import com.wellsoft.pt.app.facade.service.AppProductIntegrationMgr;
import com.wellsoft.pt.app.facade.service.AppSystemMgr;
import com.wellsoft.pt.app.manager.dto.AppSystemDto;
import com.wellsoft.pt.app.manager.facade.service.AppSystemManager;
import com.wellsoft.pt.app.service.AppPageDefinitionService;
import com.wellsoft.pt.app.service.AppProductIntegrationService;
import com.wellsoft.pt.app.service.AppSystemService;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
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
public class AppSystemManagerImpl implements AppSystemManager {

    @Autowired
    private AppSystemMgr appSystemMgr;

    @Autowired
    private AppSystemService appSystemService;

    @Autowired
    private AppProductIntegrationService appProductIntegrationService;

    @Autowired
    private AppPageDefinitionService appPageDefinitionService;

    @Autowired
    private AppProductIntegrationMgr appProductIntegrationMgr;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.manager.facade.service.AppSystemManager#saveDto(com.wellsoft.pt.app.manager.dto.AppSystemDto)
     */
    @Override
    @Transactional
    public void saveDto(AppSystemDto appSystemDto) {
        // 是否有变更产品信息
        boolean isChangeAppProductUuid = isChangeAppProductUuid(appSystemDto);
        // 保存系统信息
        AppSystem appSystem = appSystemMgr.saveBean(appSystemDto);
        // 更新归属产品信息
        if (isChangeAppProductUuid) {
            // 归属产品变更为空，有子结点时，不可变更
            if (StringUtils.isBlank(appSystemDto.getAppProductUuid())) {
                if (appProductIntegrationService.hasChildren(appSystemDto.getAppPiUuid())) {
                    throw new RuntimeException("系统下有模块，归属产品不能变更为空！");
                } else {
                    appProductIntegrationService.remove(appSystemDto.getAppPiUuid());
                }
            } else {
                appProductIntegrationService.updateAppProductUuid(appSystemDto.getAppPiUuid(),
                        appSystemDto.getAppProductUuid());
            }
        }
        // 添加系统的产品集成信息
        String appProductUuid = appSystemDto.getAppProductUuid();
        if (StringUtils.isNotBlank(appProductUuid)
                && (StringUtils.isBlank(appSystemDto.getUuid()) || StringUtils.isBlank(appSystemDto.getAppPiUuid()))) {
            appProductIntegrationService.createAppSystem(appProductUuid, appSystem);
        }
    }

    /**
     * @param appSystemDto
     * @return
     */
    private boolean isChangeAppProductUuid(AppSystemDto appSystemDto) {
        String appProductUuid = appSystemDto.getAppProductUuid();
        String appPiUuid = appSystemDto.getAppPiUuid();
        // appPiUuid为空表示未加入产品集成，不存在产品变更
        if (StringUtils.isNotBlank(appPiUuid)) {
            AppProductIntegration appProductIntegration = appProductIntegrationService.get(appPiUuid);
            if (!StringUtils.equals(appProductUuid, appProductIntegration.getAppProductUuid())) {
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
        String appProductUuid = select2QueryInfo.getOtherParams("appProductUuid", "");
        String systemUnitId = SpringSecurityUtils.getCurrentUserUnitId();
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("name", queryValue);
        values.put("id", queryValue);
        values.put("appProductUuid", appProductUuid);
        values.put("systemUnitId", systemUnitId);
        List<AppSystem> list = appSystemService.listByNameSQLQueryAndPage("appSystemManagerSelect2Query", values,
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
        values.put("appSysIds", appSysIds);
        List<AppSystem> list = appSystemService.listByNameHQLQueryAndPage("appSystemManagerSelect2IdsQuery", values,
                select2QueryInfo.getPagingInfo());
        return new Select2QueryData(list, "id", "name", select2QueryInfo.getPagingInfo());
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.manager.facade.service.AppSystemManager#remove(com.wellsoft.pt.app.manager.dto.AppSystemDto)
     */
    @Override
    @Transactional
    public void remove(AppSystemDto appSystemDto) {
        String appPiUuid = appSystemDto.getAppPiUuid();
        // 删除产品集成
        if (StringUtils.isNotBlank(appPiUuid)) {
            StringBuilder errorSb = new StringBuilder();
            if (appProductIntegrationService.hasChildren(appPiUuid)) {
                errorSb.append("模块，");
            }
            List<String> appPageUuidList = appPageDefinitionService.getAppPageDefinitionUuidsByAppPiUuid(appPiUuid);
            if (appPageUuidList.size() > 0) {
                errorSb.append("工作台，");
            }
            if (errorSb.length() > 0) {
                throw new RuntimeException("系统下有:" + errorSb.append("不可删除！").toString());
            }
            appProductIntegrationService.remove(appPiUuid);
        }
        // 删除系统
        appSystemMgr.remove(appSystemDto.getUuid());
    }

}
