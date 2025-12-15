/*
 * @(#)2016-05-09 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.facade.service.impl;

import com.wellsoft.context.component.select2.Select2QueryData;
import com.wellsoft.context.component.select2.Select2QueryInfo;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.jdbc.entity.ConfigurableIdEntity;
import com.wellsoft.context.service.CommonValidateService;
import com.wellsoft.pt.app.bean.AppApplicationBean;
import com.wellsoft.pt.app.entity.AppApplication;
import com.wellsoft.pt.app.facade.service.AppApplicationMgr;
import com.wellsoft.pt.app.service.AppApplicationService;
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
 * Description: 应用管理
 *
 * @author t
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016-05-09.1	t		2016-05-09		Create
 * </pre>
 * @date 2016-05-09
 */
@Service
public class AppApplicationMgrImpl implements AppApplicationMgr {

    @Autowired
    private AppApplicationService appApplicationService;

    @Autowired
    private AppProductIntegrationService appProductIntegrationService;

    @Autowired
    private CommonValidateService commonValidateService;

    @Override
    public AppApplicationBean getBean(String uuid) {
        AppApplication entity = appApplicationService.get(uuid);
        AppApplicationBean bean = new AppApplicationBean();
        BeanUtils.copyProperties(entity, bean);
        return bean;
    }

    @Override
    @Transactional
    public AppApplication saveBean(AppApplicationBean bean) {
        String uuid = bean.getUuid();
        AppApplication entity = new AppApplication();
        if (StringUtils.isNotBlank(uuid)) {
            entity = appApplicationService.get(uuid);
            // ID非空唯一性判断
            if (!commonValidateService.checkUnique(bean.getUuid(),
                    StringUtils.uncapitalize(AppApplication.class.getSimpleName()), ConfigurableIdEntity.ID,
                    bean.getId())) {
                throw new RuntimeException("已经存在ID为[" + bean.getId() + "]的应用!");
            }
        } else {
            // ID非空唯一性判断
            if (commonValidateService.checkExists(StringUtils.uncapitalize(AppApplication.class.getSimpleName()),
                    ConfigurableIdEntity.ID, bean.getId())) {
                throw new RuntimeException("已经存在ID为[" + bean.getId() + "]的应用!");
            }
        }
        BeanUtils.copyProperties(bean, entity);
        appApplicationService.save(entity);

        // 更新集成信息名称
        appProductIntegrationService.updatPiDataName(entity);

        AppCacheUtils.clear();

        return entity;
    }

    @Override
    public void remove(String uuid) {
        if (appProductIntegrationService.hasChildrenByDataUuid(uuid)) {
            throw new RuntimeException("应用下有功能，不可删除!");
        }
        appProductIntegrationService.removeByDataUuidAndType(uuid, AppType.APPLICATION.toString());
        appApplicationService.remove(uuid);
    }

    @Override
    public void removeAll(Collection<String> uuids) {
        for (String uuid : uuids) {
            if (appProductIntegrationService.count(uuid, AppType.APPLICATION) > 0) {
                throw new RuntimeException("应用已集成到产品，不可删除!");
            }
            appProductIntegrationService.removeByDataUuidAndType(uuid, AppType.APPLICATION.toString());
        }
        appApplicationService.removeAllByPk(uuids);
    }

    @Override
    public Select2QueryData loadSelectData(Select2QueryInfo select2QueryInfo) {
        String queryValue = select2QueryInfo.getSearchValue();
        String excludeDataUuids = select2QueryInfo.getOtherParams("excludeDataUuids", "-1");
        String systemUnitId = select2QueryInfo.getOtherParams("systemUnitId", "");
        List<String> excludeUuids = Arrays.asList(StringUtils.split(excludeDataUuids, Separator.SEMICOLON.getValue()));
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("name", queryValue);
        values.put("id", queryValue);
        values.put("excludeUuids", excludeUuids);
        values.put("systemUnitId", systemUnitId);
        List<AppApplication> list = appApplicationService.listByNameHQLQueryAndPage("appApplicationSelect2Query",
                values, select2QueryInfo.getPagingInfo());
        return new Select2QueryData(list, "id", "name", select2QueryInfo.getPagingInfo());
    }

    @Override
    public Select2QueryData loadSelectDataByIds(Select2QueryInfo select2QueryInfo) {
        String[] appAppIds = select2QueryInfo.getIds();
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("appAppIds", appAppIds);
        List<AppApplication> list = appApplicationService.listByNameHQLQueryAndPage("appApplicationSelect2IdsQuery",
                values, select2QueryInfo.getPagingInfo());
        return new Select2QueryData(list, "id", "name", select2QueryInfo.getPagingInfo());
    }
}
