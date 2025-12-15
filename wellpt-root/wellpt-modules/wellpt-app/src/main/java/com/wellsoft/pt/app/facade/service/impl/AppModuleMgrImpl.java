/*
 * @(#)2016-05-09 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.facade.service.impl;

import com.google.common.collect.Lists;
import com.wellsoft.context.component.jqgrid.JqGridQueryInfo;
import com.wellsoft.context.component.jqgrid.JqTreeGridNode;
import com.wellsoft.context.component.select2.Select2QueryData;
import com.wellsoft.context.component.select2.Select2QueryInfo;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.jdbc.entity.ConfigurableIdEntity;
import com.wellsoft.context.jdbc.support.PropertyFilter;
import com.wellsoft.context.jdbc.support.QueryData;
import com.wellsoft.context.jdbc.support.QueryInfo;
import com.wellsoft.context.service.CommonValidateService;
import com.wellsoft.pt.app.bean.AppModuleBean;
import com.wellsoft.pt.app.bean.AppSystemBean;
import com.wellsoft.pt.app.entity.AppModule;
import com.wellsoft.pt.app.entity.AppProductIntegration;
import com.wellsoft.pt.app.entity.AppSystem;
import com.wellsoft.pt.app.facade.service.AppModuleMgr;
import com.wellsoft.pt.app.service.AppModuleService;
import com.wellsoft.pt.app.service.AppProductIntegrationService;
import com.wellsoft.pt.app.service.AppSystemService;
import com.wellsoft.pt.app.support.AppCacheUtils;
import com.wellsoft.pt.app.support.AppType;
import com.wellsoft.pt.jpa.util.BeanUtils;
import com.wellsoft.pt.security.access.RequestSystemContextPathResolver;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Description: 模块管理
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
public class AppModuleMgrImpl implements AppModuleMgr {

    @Autowired
    private AppModuleService appModuleService;

    @Autowired
    private AppProductIntegrationService appProductIntegrationService;

    @Autowired
    private AppSystemService appSystemService;

    @Autowired
    private CommonValidateService commonValidateService;

    @Override
    public AppModuleBean getBean(String uuid) {
        AppModule entity = appModuleService.get(uuid);
        AppModuleBean bean = new AppModuleBean();
        BeanUtils.copyProperties(entity, bean);
        return bean;
    }

    @Override
    public AppModuleBean getModuleDetail(String id) {
        List<AppModule> appModules = appModuleService.getByIds(new String[]{id});
        if (appModules.isEmpty()) {
            return null;
        }
        AppModuleBean bean = new AppModuleBean();
        BeanUtils.copyProperties(appModules.get(0), bean);
        List<AppProductIntegration> appProductIntegrations = appProductIntegrationService.getByDataUuidAndType(bean.getUuid(),
                AppType.MODULE.toString());
        if (CollectionUtils.isNotEmpty(appProductIntegrations)) { //获取所属系统
            AppSystem appSystem = appSystemService.get(appProductIntegrations.get(0).getAppSystemUuid());
            AppSystemBean appSystemBean = new AppSystemBean();
            org.springframework.beans.BeanUtils.copyProperties(appSystem, appSystemBean);
            bean.setAppSystemBean(appSystemBean);
        }

        return bean;
    }

    @Override
    public String getModuleNameById(String id) {
        if (StringUtils.isBlank(id)) {
            return null;
        }
        return appModuleService.getModuleNameById(id);
    }

    @Override
    @Transactional
    public AppModule saveBean(AppModuleBean bean) {
        String uuid = bean.getUuid();
        AppModule entity = new AppModule();
        if (StringUtils.isNotBlank(uuid)) {
            entity = appModuleService.get(uuid);
            // ID非空唯一性判断
            if (!commonValidateService.checkUnique(bean.getUuid(),
                    StringUtils.uncapitalize(AppModule.class.getSimpleName()), ConfigurableIdEntity.ID, bean.getId())) {
                throw new RuntimeException("已经存在ID为[" + bean.getId() + "]的模块!");
            }
        } else {
            // ID非空唯一性判断
            if (commonValidateService.checkExists(StringUtils.uncapitalize(AppModule.class.getSimpleName()),
                    ConfigurableIdEntity.ID, bean.getId())) {
                throw new RuntimeException("已经存在ID为[" + bean.getId() + "]的模块!");
            }
        }
        BeanUtils.copyProperties(bean, entity);
        appModuleService.save(entity);

        // 更新集成信息名称
        appProductIntegrationService.updatPiDataName(entity);

        AppCacheUtils.clear();
        return entity;
    }

    @Override
    public void remove(String uuid) {
        if (appProductIntegrationService.hasChildrenByDataUuid(uuid)) {
            throw new RuntimeException("模块下有子模块或应用，不可删除!");
        }
        appProductIntegrationService.removeByDataUuidAndType(uuid, AppType.MODULE.toString());
        appModuleService.remove(uuid);
    }

    @Override
    public void removeAll(Collection<String> uuids) {
        for (String uuid : uuids) {
            if (appProductIntegrationService.hasChildrenByDataUuid(uuid)) {
                throw new RuntimeException("模块下有子模块或应用，不可删除!");
            }
            appProductIntegrationService.removeByDataUuidAndType(uuid, AppType.MODULE.toString());
        }
        appModuleService.removeAllByPk(uuids);
    }

    @Override
    public QueryData getTreeList(JqGridQueryInfo jqGridQueryInfo, QueryInfo queryInfo) {
        // 设置查询字段条件
        Map<String, Object> values = PropertyFilter.convertToMap(queryInfo.getPropertyFilters());

        // 查询父节点为null的部门
        List<AppModule> results = null;
        if (StringUtils.isBlank(jqGridQueryInfo.getNodeid())) {
            if (values.isEmpty()) {
                results = appModuleService.listByNameHQLQueryAndPage("topAppModuleTreeQuery", values,
                        queryInfo.getPagingInfo());
            } else {
                results = appModuleService.listByNameHQLQueryAndPage("appModuleTreeQuery", values,
                        queryInfo.getPagingInfo());
            }
        } else {
            values.put("id", jqGridQueryInfo.getNodeid());
            results = appModuleService.listByNameHQLQueryAndPage("appModuleTreeByIdQuery", values,
                    queryInfo.getPagingInfo());
        }

        List<JqTreeGridNode> retResults = new ArrayList<JqTreeGridNode>();

        int level = jqGridQueryInfo.getN_level() == null ? 0 : jqGridQueryInfo.getN_level() + 1;
        String parentId = jqGridQueryInfo.getNodeid() == null ? "null" : jqGridQueryInfo.getNodeid();
        for (int index = 0; index < results.size(); index++) {
            AppModule appModule = results.get(index);
            JqTreeGridNode node = new JqTreeGridNode();
            node.setId(appModule.getUuid());// ID
            List<Object> cell = node.getCell();
            cell.add(appModule.getUuid()); // UUID
            cell.add(appModule.getName());// Name
            cell.add(appModule.getId());// ID
            cell.add(appModule.getCode());// Code
            cell.add(appModule.getJsModule());// JsModule
            cell.add(appModule.getEnabled());// Enabled
            cell.add(appModule.getRemark());// Remark
            // level field
            cell.add(level);
            // parent id field
            cell.add(parentId);
            // leaf field
            AppModule example = new AppModule();
            example.setParentUuid(appModule.getUuid());
            Long count = appModuleService.countByParentUuid(appModule.getUuid());
            cell.add(Integer.valueOf(0).equals(count.intValue()));
            // expanded field 第一个节点展开
            if ("null".equals(parentId)) {
                cell.add(true);
            } else {
                cell.add(false);
            }

            retResults.add(node);
        }
        QueryData queryData = new QueryData();
        queryData.setDataList(retResults);
        queryData.setPagingInfo(queryInfo.getPagingInfo());
        return queryData;
    }

    @Override
    public Select2QueryData loadSelectData(Select2QueryInfo select2QueryInfo) {
        String queryValue = select2QueryInfo.getSearchValue();
        String excludeDataUuids = select2QueryInfo.getOtherParams("excludeDataUuids");
        String excludeIds = select2QueryInfo.getOtherParams("excludeIds");
        String systemUnitId = select2QueryInfo.getOtherParams("systemUnitId", "");
        String idProperty = select2QueryInfo.getOtherParams("idProperty", "id");
        String includeSuperAdmin = select2QueryInfo.getOtherParams("includeSuperAdmin", "false");//是否包含超管的模块
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("name", queryValue);
        values.put("id", queryValue);
        if (StringUtils.isNotBlank(excludeDataUuids)) {
            values.put("excludeUuids",
                    Arrays.asList(StringUtils.split(excludeDataUuids, Separator.SEMICOLON.getValue())));
        }
        if (StringUtils.isNotBlank(excludeIds)) {
            values.put("excludeIds", Arrays.asList(StringUtils.split(excludeIds, Separator.SEMICOLON.getValue())));
        }

        if (StringUtils.isNotBlank(RequestSystemContextPathResolver.system())) {
            values.put("systemIds", Lists.newArrayList(RequestSystemContextPathResolver.system()));
            values.put("tenantId", SpringSecurityUtils.getCurrentTenantId());
        }
        List<AppModule> list = appModuleService.listByNameHQLQueryAndPage("appModuleSelect2Query", values,
                select2QueryInfo.getPagingInfo());
        return new Select2QueryData(list, idProperty, "name", select2QueryInfo.getPagingInfo());
    }

    @Override
    public Select2QueryData loadSelectDataByIds(Select2QueryInfo select2QueryInfo) {
        String[] appModuleIds = select2QueryInfo.getIds();
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("appModuleIds", appModuleIds);
        List<AppModule> list = appModuleService.listByNameHQLQueryAndPage("appModuleSelect2IdsQuery", values,
                select2QueryInfo.getPagingInfo());
        return new Select2QueryData(list, "id", "name", select2QueryInfo.getPagingInfo());
    }

}
