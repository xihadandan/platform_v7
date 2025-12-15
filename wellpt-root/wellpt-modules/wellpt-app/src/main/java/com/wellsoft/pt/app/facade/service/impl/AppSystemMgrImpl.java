/*
 * @(#)2016-05-09 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.facade.service.impl;

import com.google.common.collect.Lists;
import com.wellsoft.context.component.select2.Select2QueryData;
import com.wellsoft.context.component.select2.Select2QueryInfo;
import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.jdbc.entity.ConfigurableIdEntity;
import com.wellsoft.context.service.CommonValidateService;
import com.wellsoft.pt.app.bean.AppSystemBean;
import com.wellsoft.pt.app.entity.AppSystem;
import com.wellsoft.pt.app.facade.service.AppProductIntegrationMgr;
import com.wellsoft.pt.app.facade.service.AppSystemMgr;
import com.wellsoft.pt.app.security.AppProductIntegrationResourceDataSource;
import com.wellsoft.pt.app.service.AppProductIntegrationService;
import com.wellsoft.pt.app.service.AppSystemService;
import com.wellsoft.pt.app.support.AppCacheUtils;
import com.wellsoft.pt.app.support.AppType;
import com.wellsoft.pt.jpa.util.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;

/**
 * Description: 系统管理
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016-05-09.1	zhulh		2016-05-09		Create
 * </pre>
 * @date 2016-05-09
 */
@Service
public class AppSystemMgrImpl implements AppSystemMgr {

    @Autowired
    private AppSystemService appSystemService;

    @Autowired
    private AppProductIntegrationService appProductIntegrationService;

    @Autowired
    private CommonValidateService commonValidateService;

    @Resource
    private AppProductIntegrationResourceDataSource appProductIntegrationResourceDataSource;

    @Resource
    private AppProductIntegrationMgr appProductIntegrationMgr;

    @Override
    public AppSystemBean getBean(String uuid) {
        AppSystem entity = appSystemService.get(uuid);
        AppSystemBean bean = new AppSystemBean();
        BeanUtils.copyProperties(entity, bean);
        return bean;
    }

    @Override
    @Transactional
    public AppSystem saveBean(AppSystemBean bean) {
        String uuid = bean.getUuid();
        AppSystem entity = new AppSystem();
        if (StringUtils.isNotBlank(uuid)) {
            entity = appSystemService.get(uuid);
            // ID非空唯一性判断
            if (!commonValidateService.checkUnique(bean.getUuid(),
                    StringUtils.uncapitalize(AppSystem.class.getSimpleName()),
                    ConfigurableIdEntity.ID, bean.getId())) {
                throw new RuntimeException("已经存在ID为[" + bean.getId() + "]的系统!");
            }
        } else {
            // ID非空唯一性判断
            if (commonValidateService.checkExists(
                    StringUtils.uncapitalize(AppSystem.class.getSimpleName()),
                    ConfigurableIdEntity.ID, bean.getId())) {
                throw new RuntimeException("已经存在ID为[" + bean.getId() + "]的系统!");
            }
        }
        BeanUtils.copyProperties(bean, entity);
        appSystemService.save(entity);

        // 更新集成信息名称
        appProductIntegrationService.updatPiDataName(entity);

        AppCacheUtils.clear();

        return entity;
    }

    @Override
    public void remove(String uuid) {
        if (appProductIntegrationService.hasChildrenByDataUuid(uuid)) {
            throw new RuntimeException("系统下有模块，不可删除!");
        }
        appProductIntegrationService.removeByDataUuidAndType(uuid, AppType.SYSTEM.toString());
        appSystemService.remove(uuid);
    }

    @Override
    public void removeAll(Collection<String> uuids) {
        for (String uuid : uuids) {
            if (appProductIntegrationService.hasChildrenByDataUuid(uuid)) {
                throw new RuntimeException("系统下有模块，不可删除!");
            }
            appProductIntegrationService.removeByDataUuidAndType(uuid, AppType.SYSTEM.toString());
        }
        appSystemService.removeAllByPk(uuids);
    }

    @Override
    public List<TreeNode> getTreeByAppSystemId(String id) {
        AppSystem appSystem = appSystemService.getById(id);
        return appProductIntegrationResourceDataSource.getDataByAppSystemUuid(appSystem.getUuid());
    }

    @Override
    public List<TreeNode> getTreeByAppSystemUuid(String uuid) {
        return appProductIntegrationResourceDataSource.getDataByAppSystemUuid(uuid);
    }

    @Override
    public List<TreeNode> getTreeBySystemUnitId(String systemUnitId) {
        List<AppSystem> appSystemList = appSystemService.listBySystemUnitId(systemUnitId);
        List<TreeNode> systemNodes = Lists.newArrayList();
        for (AppSystem sys : appSystemList) {
            systemNodes.addAll(this.getTreeByAppSystemUuid(sys.getUuid()));
        }
        return systemNodes;
    }


    @Override
    public List<TreeNode> getSystemModuleAppTreeBySystemUnitId(String systemUnitId) {
        List<AppSystem> appSystemList = appSystemService.listBySystemUnitId(systemUnitId);
        List<TreeNode> systemNodes = Lists.newArrayList();
        for (AppSystem sys : appSystemList) {
            systemNodes.addAll(appProductIntegrationMgr.getSysModuleAppTreeByAppSystemUuid(sys.getUuid()));
        }
        return systemNodes;
    }

    @Override
    public Select2QueryData loadSelectData(Select2QueryInfo select2QueryInfo) {
        String queryValue = select2QueryInfo.getSearchValue();
        String excludeDataUuids = select2QueryInfo.getOtherParams("excludeDataUuids", "-1");
        String systemUnitId = select2QueryInfo.getOtherParams("systemUnitId", "");
        List<String> excludeUuids = Arrays.asList(
                StringUtils.split(excludeDataUuids, Separator.SEMICOLON.getValue()));
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("name", queryValue);
        values.put("id", queryValue);
        values.put("excludeUuids", excludeUuids);
        values.put("systemUnitId", systemUnitId);
        List<AppSystem> list = appSystemService.listByNameHQLQueryAndPage("appSystemSelect2Query",
                values,
                select2QueryInfo.getPagingInfo());
        return new Select2QueryData(list, "id", "name", select2QueryInfo.getPagingInfo());
    }

    @Override
    public Select2QueryData loadSelectDataByIds(Select2QueryInfo select2QueryInfo) {
        String[] appSysIds = select2QueryInfo.getIds();
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("appSysIds", appSysIds);
        List<AppSystem> list = appSystemService.listByNameHQLQueryAndPage(
                "appSystemSelect2IdsQuery", values,
                select2QueryInfo.getPagingInfo());
        return new Select2QueryData(list, "id", "name", select2QueryInfo.getPagingInfo());
    }

}
