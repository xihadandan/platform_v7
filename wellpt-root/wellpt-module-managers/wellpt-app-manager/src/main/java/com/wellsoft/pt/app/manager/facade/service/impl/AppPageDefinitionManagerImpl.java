/*
 * @(#)2019年6月6日 V1.0
 *
 * Copyright 2019 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.manager.facade.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wellsoft.context.component.tree.OrgNode;
import com.wellsoft.context.dto.DataItem;
import com.wellsoft.context.jdbc.entity.IdEntity;
import com.wellsoft.pt.app.bean.AppPageDefinitionBean;
import com.wellsoft.pt.app.design.container.AbstractContainer;
import com.wellsoft.pt.app.entity.AppPageDefinition;
import com.wellsoft.pt.app.entity.AppPageDefinitionRefEntity;
import com.wellsoft.pt.app.entity.AppPageResourceEntity;
import com.wellsoft.pt.app.entity.AppProductIntegration;
import com.wellsoft.pt.app.facade.service.AppPageDefinitionMgr;
import com.wellsoft.pt.app.manager.dto.AppPageDefinitionDto;
import com.wellsoft.pt.app.manager.dto.AppPageResourceDto;
import com.wellsoft.pt.app.manager.facade.service.AppPageDefinitionManager;
import com.wellsoft.pt.app.service.*;
import com.wellsoft.pt.app.support.AppConstants;
import com.wellsoft.pt.app.support.AppType;
import com.wellsoft.pt.jpa.util.BeanUtils;
import com.wellsoft.pt.security.audit.facade.service.PrivilegeFacadeService;
import com.wellsoft.pt.security.audit.facade.service.RoleFacadeService;
import com.wellsoft.pt.security.facade.service.SecurityApiFacade;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.OrderComparator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
 * 2019年6月6日.1	zhulh		2019年6月6日		Create
 * </pre>
 * @date 2019年6月6日
 */
@Service
public class AppPageDefinitionManagerImpl implements AppPageDefinitionManager {

    @Autowired
    private AppPageDefinitionService appPageDefinitionService;

    @Autowired
    private AppPageDefinitionMgr appPageDefinitionMgr;

    @Autowired(required = false)
    private List<AbstractContainer> pageContainers;

    @Autowired
    private AppProductIntegrationService appProductIntegrationService;

    @Autowired
    private AppPageResourceService appPageResourceService;

    @Autowired
    private AppWidgetDefinitionService appWidgetDefinitionService;

    @Autowired
    private AppPageDefinitionRefService appPageDefinitionRefService;

    @Autowired
    private SecurityApiFacade securityApiFacade;

    @Autowired
    private PrivilegeFacadeService privilegeFacadeService;

    @Autowired
    private RoleFacadeService roleFacadeService;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.manager.facade.service.AppPageDefinitionManager#saveDto(com.wellsoft.pt.app.manager.dto.AppPageDefinitionDto)
     */
    @Override
    @Transactional
    public void saveDto(AppPageDefinitionDto appPageDefinitionDto) {
        if (StringUtils.isBlank(appPageDefinitionDto.getAppPiUuid())) {
            throw new RuntimeException("页面归属不能为空！");
        }
        if (appPageDefinitionDto.getIsDefault() == null) {
            appPageDefinitionDto.setIsDefault(Boolean.FALSE);
        }
        AppProductIntegration appProductIntegration = appProductIntegrationService.get(appPageDefinitionDto.getAppPiUuid());
        if (appProductIntegration == null || !appProductIntegration.getDataType().equals(AppType.SYSTEM.toString())) {
            //非系统下工作台页面 默认null
            appPageDefinitionDto.setIsDefault(null);
        }

        if (BooleanUtils.isTrue(appPageDefinitionDto.getIsDefault())) {
            //默认的，则重置其他未非默认的
            appPageDefinitionService.updateUnDefaultByAppPiUuid(
                    appPageDefinitionDto.getAppPiUuid());
        }
        AppPageDefinition appPageDefinition = appPageDefinitionMgr.saveBean(appPageDefinitionDto);
        //appPageResourceService.removeByAppPageUuid(appPageDefinition.getUuid());
        List<AppPageResourceDto> pageResourceDtos = appPageDefinitionDto.getPageResources();

        List<AppPageResourceEntity> changeResources = Lists.newArrayList();
        Map<String, Boolean> uuidMap = Maps.newHashMap();
        for (AppPageResourceDto appPageResourceDto : pageResourceDtos) {
            AppPageResourceEntity entity = new AppPageResourceEntity();
            if (StringUtils.isNotBlank(appPageResourceDto.getUuid())) {
                //更新资源权限保护配置
                entity = appPageResourceService.getOne(appPageResourceDto.getUuid());
                if (!entity.getIsProtected().equals(appPageResourceDto.getIsProtected())) {
                    entity.setIsProtected(appPageResourceDto.getIsProtected());
                    appPageResourceService.update(entity);
                    changeResources.add(entity);
                }
            } else {
                //新增的资源
                BeanUtils.copyProperties(appPageResourceDto, entity, IdEntity.BASE_FIELDS);
                String uuid = DigestUtils.md5Hex(
                        appPageDefinition.getUuid() + entity.getAppFunctionUuid());
                entity.setUuid(uuid);
                entity.setAppPageUuid(appPageDefinition.getUuid());
                entity.setAppPiUuid(appPageDefinition.getAppPiUuid());
                appPageResourceService.save(entity);
                changeResources.add(entity);
            }
            uuidMap.put(entity.getUuid(), true);

        }
        List<AppPageResourceEntity> existPageResourceEntities = appPageResourceService
                .listByAppPageUuid(appPageDefinition.getUuid());
        List<AppPageResourceEntity> deletes = Lists.newArrayList();
        for (AppPageResourceEntity exist : existPageResourceEntities) {//删除不存在的资源
            if (!uuidMap.containsKey(exist.getUuid())) {
                deletes.add(exist);
            }
        }
        appPageResourceService.deleteByEntities(deletes);
        appPageResourceService.flushSession();
        appPageResourceService.clearSession();
        // 增加不受权限控制的资源
        for (AppPageResourceEntity appPageResourceEntity : changeResources) {
            String pageResourceId = getPageResourceId(appPageResourceEntity);
            if (!Boolean.TRUE.equals(appPageResourceEntity.getIsProtected())) {
                securityApiFacade.addAnonymousResource(pageResourceId);
            } else {
                securityApiFacade.removeAnonymousResource(pageResourceId);
            }
        }
//        if(appProductIntegration!=null && appProductIntegration.getDataType().equals(AppType.SYSTEM.toString())){
//            //生成默认工作台权限角色等
//            privilegeFacadeService.saveAppPageDef(appPageDefinitionDto.getIsDefault(), appPageDefinition.getUuid(), appPageDefinitionDto.getAppPiUuid());
//        }
    }


    @Override
    public void saveEleIds(String appPiUuid, String uuid, List<String> eleIds) {
        boolean isRef = this.isRef(appPiUuid, uuid);
        privilegeFacadeService.saveAppPageEleIds(isRef, appPiUuid, uuid, eleIds);
    }

    @Override
    public List<OrgNode> getEleIds(String appPiUuid, String uuid) {
        boolean isRef = this.isRef(appPiUuid, uuid);
        return privilegeFacadeService.getEleIds(isRef, appPiUuid, uuid);
    }

    /**
     * 是否引用工作台
     *
     * @param appPiUuid
     * @param uuid
     * @return
     */
    private boolean isRef(String appPiUuid, String uuid) {
        AppPageDefinitionDto appPageDefinition = this.getDto(uuid);
        if (appPageDefinition == null) {
            throw new RuntimeException("工作台不存在！");
        }
        if (appPageDefinition.getAppPiUuid().equals(appPiUuid)) {
            return false;
        }
        return true;
    }

    /**
     * @param entity
     * @return
     */
    private String getPageResourceId(AppPageResourceEntity entity) {
        return AppConstants.FUNCTIONREF_OF_PAGE_PREFIX + "_" + entity.getAppPiUuid() + "_" + entity.getAppPageUuid()
                + "_" + entity.getAppFunctionUuid();
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.manager.facade.service.AppPageDefinitionManager#saveNewVersion(com.wellsoft.pt.app.manager.dto.AppPageDefinitionDto)
     */
    @Override
    public void saveNewVersion(AppPageDefinitionDto appPageDefinitionDto) {
        AppPageDefinition appPageDefinition = appPageDefinitionMgr.saveNewVersion(
                appPageDefinitionDto);
        List<AppPageResourceDto> pageResourceDtos = appPageDefinitionDto.getPageResources();
        for (AppPageResourceDto appPageResourceDto : pageResourceDtos) {
            AppPageResourceEntity entity = new AppPageResourceEntity();
            BeanUtils.copyProperties(appPageResourceDto, entity, IdEntity.BASE_FIELDS);
            String uuid = DigestUtils.md5Hex(
                    appPageDefinition.getUuid() + entity.getAppFunctionUuid());
            entity.setUuid(uuid);
            entity.setAppPageUuid(appPageDefinition.getUuid());
            entity.setAppPiUuid(appPageDefinition.getAppPiUuid());
            appPageResourceService.save(entity);
        }
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.manager.facade.service.AppPageDefinitionManager#getDto(java.lang.String)
     */
    @Override
    public AppPageDefinitionDto getDto(String uuid) {
        AppPageDefinitionDto appPageDefinitionDto = new AppPageDefinitionDto();
        AppPageDefinitionBean appPageDefinitionBean = appPageDefinitionMgr.getBean(uuid);
        BeanUtils.copyProperties(appPageDefinitionBean, appPageDefinitionDto);
        return appPageDefinitionDto;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.manager.facade.service.AppPageDefinitionManager#getPageDesignerList()
     */
    @Override
    public List<DataItem> getPageDesignerList() {
        List<DataItem> pageDesigners = Lists.newArrayList();
        if (CollectionUtils.isEmpty(pageContainers)) {
            return pageDesigners;
        }

        OrderComparator.sort(pageContainers);
        for (AbstractContainer pageContainer : pageContainers) {
            if (pageContainer.getCategory() != null) {
                continue;
            }
            DataItem dataItem = new DataItem();
            dataItem.setLabel(pageContainer.getName());
            dataItem.setValue(pageContainer.getType());
            pageDesigners.add(dataItem);
        }
        return pageDesigners;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.manager.facade.service.AppPageDefinitionManager#copyPageDefinition(java.lang.String, java.lang.String)
     */
    @Override
    @Transactional
    public void copyPageDefinition(String appPiUuid, String copyPageUuid, String copyPageId) {
        try {
            String newPageUuid = appPageDefinitionMgr.copyPageDefinition(copyPageUuid, null, copyPageId);
            AppPageDefinition sourceAppPageDefinition = appPageDefinitionService.get(copyPageUuid);
            AppPageDefinition newAppPageDefinition = appPageDefinitionService.get(newPageUuid);
            newAppPageDefinition.setName(sourceAppPageDefinition.getName() + "(复制)");
            newAppPageDefinition.setTitle(sourceAppPageDefinition.getTitle());
            newAppPageDefinition.setCode(sourceAppPageDefinition.getCode());
            newAppPageDefinition.setAppPiUuid(appPiUuid);
            appPageDefinitionService.save(newAppPageDefinition);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.manager.facade.service.AppPageDefinitionManager#refPageDefinition(java.lang.String, java.lang.String)
     */
    @Override
    public void refPageDefinition(String appPiUuid, String refPageUuid) {
        AppPageDefinitionRefEntity refEntity = new AppPageDefinitionRefEntity();
        refEntity.setAppPiUuid(appPiUuid);
        refEntity.setRefUuid(refPageUuid);
        appPageDefinitionRefService.save(refEntity);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.manager.facade.service.AppPageDefinitionManager#getRefPageInfo(java.lang.String)
     */
    @Override
    public Map<String, Object> getRefPageInfo(String refPageUuid) {
        Map<String, Object> values = Maps.newHashMap();
        AppPageDefinition appPageDefinition = appPageDefinitionService.get(refPageUuid);
        String appPiUuid = appPageDefinition.getAppPiUuid();
        if (StringUtils.isBlank(appPiUuid)) {
            return values;
        }
        // 获取自身及所有上级的产品集成信息
        List<AppProductIntegration> appProductIntegrations = appProductIntegrationService
                .getSelfWithParentsByUuid(appPiUuid);
        List<String> pathNames = Lists.newArrayList();
        for (AppProductIntegration appProductIntegration : appProductIntegrations) {
            pathNames.add(appProductIntegration.getDataName());
        }
        values.put("piPathName", StringUtils.join(pathNames, "/"));
        return values;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.manager.facade.service.AppPageDefinitionManager#remove(java.lang.String)
     */
    @Override
    @Transactional
    public void remove(String uuid) {
        // 页面被引用不可删除
        if (appPageDefinitionRefService.isReferenced(uuid)) {
            throw new RuntimeException("页面被引用，不可删除！");
        }

        AppPageDefinitionBean appPageDefinitionBean = appPageDefinitionMgr.getBean(uuid);
        String appPiUuid = appPageDefinitionBean.getAppPiUuid();
        boolean flg = false;
        if (StringUtils.isNotBlank(appPiUuid)) {
            AppProductIntegration appProductIntegration = appProductIntegrationService.get(
                    appPiUuid);
            if (appProductIntegration.getDataType().equals(AppType.SYSTEM.toString())) {
                flg = true;
            }
            // 重置产品集成信息的页面信息
            if (StringUtils.equals(uuid, appProductIntegration.getAppPageUuid())) {
                appProductIntegrationService.resetAppPageDefinition(appPiUuid);
            }
        }
        // 删除页面资源
        appPageResourceService.removeByAppPageUuid(uuid);
        // 删除页面组件
        appWidgetDefinitionService.removeByAppPageUuid(uuid);
        // 删除页面
        appPageDefinitionMgr.remove(uuid);

        appPageDefinitionService.flushSession();

//        if (flg) {
//            roleFacadeService.workbenchByAppPiuuid(appPiUuid);
//        }
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.manager.facade.service.AppPageDefinitionManager#cancelRef(java.lang.String)
     */
    @Override
    @Transactional
    public void cancelRef(String pageUuid, String appPiUuid) {
        appPageDefinitionRefService.remove(pageUuid, appPiUuid);
    }

}
