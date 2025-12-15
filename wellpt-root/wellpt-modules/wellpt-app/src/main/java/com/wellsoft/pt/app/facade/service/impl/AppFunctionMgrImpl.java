/*
 * @(#)2016-07-26 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.facade.service.impl;

import com.wellsoft.context.component.select2.Select2QueryData;
import com.wellsoft.context.component.select2.Select2QueryInfo;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.pt.app.bean.AppFunctionBean;
import com.wellsoft.pt.app.entity.AppFunction;
import com.wellsoft.pt.app.facade.service.AppFunctionMgr;
import com.wellsoft.pt.app.service.AppFunctionService;
import com.wellsoft.pt.app.service.AppProductIntegrationService;
import com.wellsoft.pt.app.support.AppCacheUtils;
import com.wellsoft.pt.app.support.AppType;
import com.wellsoft.pt.jpa.util.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016-07-26.1	zhulh		2016-07-26		Create
 * </pre>
 * @date 2016-07-26
 */
@Service
public class AppFunctionMgrImpl implements AppFunctionMgr {

    @Autowired
    private AppFunctionService appFunctionService;

    @Autowired
    private AppProductIntegrationService appProductIntegrationService;

    @Override
    public AppFunctionBean getBean(String uuid) {
        AppFunction entity = appFunctionService.get(uuid);
        AppFunctionBean bean = new AppFunctionBean();
        BeanUtils.copyProperties(entity, bean);
        return bean;
    }

    @Override
    public void saveBean(AppFunctionBean bean) {
        AppFunction entity = new AppFunction();
        if (StringUtils.isNotBlank(bean.getUuid())) {
            entity = appFunctionService.get(bean.getUuid());
        }
        BeanUtils.copyProperties(bean, entity);
        appFunctionService.save(entity);
        AppCacheUtils.clear();
    }

    @Override
    public void remove(String uuid) {
        if (appProductIntegrationService.count(uuid, AppType.FUNCTION) > 0) {
            throw new RuntimeException("功能已集成到产品，不可删除!");
        }
        appFunctionService.remove(uuid);
    }

    @Override
    @Transactional
    public void forceRemove(String uuid) {
        appProductIntegrationService.removeByDataUuidAndType(uuid, AppType.FUNCTION.toString());
        if (appFunctionService.getOne(uuid) != null) {
            appFunctionService.remove(uuid);
        }
    }

    @Override
    public void removeAll(Collection<String> uuids) {
        for (String uuid : uuids) {
            if (appProductIntegrationService.count(uuid, AppType.FUNCTION) > 0) {
                throw new RuntimeException("功能已集成到产品，不可删除!");
            }
        }
        appFunctionService.removeAllByPk(uuids);
    }

    @Override
    public Select2QueryData loadSelectData(Select2QueryInfo select2QueryInfo) {
        String queryValue = select2QueryInfo.getSearchValue();
        String functionType = select2QueryInfo.getOtherParams("functionType", "");
        String excludeDataUuids = select2QueryInfo.getOtherParams("excludeDataUuids", "-1");
        List<String> excludeUuids = Arrays.asList(StringUtils.split(excludeDataUuids, Separator.SEMICOLON.getValue()));
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("name", queryValue);
//        values.put("id", queryValue);
        values.put("functionType", functionType);
        values.put("excludeUuids", excludeUuids);
        List<AppFunction> list = appFunctionService.listByNameHQLQueryAndPage("appFunctionSelect2Query", values,
                select2QueryInfo.getPagingInfo());
        return new Select2QueryData(list, "uuid", "name", select2QueryInfo.getPagingInfo());
    }

    @Override
    public Select2QueryData loadSelectDataByIds(Select2QueryInfo select2QueryInfo) {
        String[] appFuncIds = select2QueryInfo.getIds();
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("appFuncIds", appFuncIds);
        List<AppFunction> list = appFunctionService.listByNameHQLQueryAndPage("appFunctionSelect2IdsQuery", values,
                select2QueryInfo.getPagingInfo());
        return new Select2QueryData(list, "uuid", "name", select2QueryInfo.getPagingInfo());
    }

    @Override
    public Select2QueryData loadSelectDataForUuid(Select2QueryInfo select2QueryInfo) {
        String queryValue = select2QueryInfo.getSearchValue();
        String excludeDataUuids = select2QueryInfo.getOtherParams("excludeDataUuids", "-1");
        List<String> excludeUuids = Arrays.asList(StringUtils.split(excludeDataUuids, Separator.SEMICOLON.getValue()));
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("name", queryValue);
        values.put("id", queryValue);
        values.put("excludeUuids", excludeUuids);
        List<AppFunction> list = appFunctionService.listByNameHQLQueryAndPage("appFunctionSelect2Query", values,
                select2QueryInfo.getPagingInfo());
        return new Select2QueryData(list, "uuid", "name", select2QueryInfo.getPagingInfo());
    }

    @Override
    public Select2QueryData loadSelectDataByUuids(Select2QueryInfo select2QueryInfo) {
        String[] appFuncUuids = select2QueryInfo.getIds();
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("appFuncUuids", appFuncUuids);
        List<AppFunction> list = appFunctionService.listByNameHQLQueryAndPage("appFunctionSelect2UuidsQuery", values,
                select2QueryInfo.getPagingInfo());
        return new Select2QueryData(list, "uuid", "name", select2QueryInfo.getPagingInfo());
    }

}
