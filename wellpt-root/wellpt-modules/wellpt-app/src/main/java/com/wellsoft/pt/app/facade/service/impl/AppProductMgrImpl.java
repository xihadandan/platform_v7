/*
 * @(#)2016-07-24 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.facade.service.impl;

import com.wellsoft.context.component.select2.Select2QueryData;
import com.wellsoft.context.component.select2.Select2QueryInfo;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.jdbc.entity.ConfigurableIdEntity;
import com.wellsoft.context.service.CommonValidateService;
import com.wellsoft.pt.app.bean.AppProductBean;
import com.wellsoft.pt.app.entity.AppProduct;
import com.wellsoft.pt.app.facade.service.AppProductIntegrationMgr;
import com.wellsoft.pt.app.facade.service.AppProductMgr;
import com.wellsoft.pt.app.service.AppProductService;
import com.wellsoft.pt.jpa.util.BeanUtils;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Description: 产品管理
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016-07-24.1	zhulh		2016-07-24		Create
 * </pre>
 * @date 2016-07-24
 */
@Service
public class AppProductMgrImpl implements AppProductMgr {

    @Autowired
    private AppProductService appProductService;

    @Autowired
    private AppProductIntegrationMgr appProductIntegrationMgr;

    @Autowired
    private CommonValidateService commonValidateService;

    @Override
    public AppProductBean getBean(String uuid) {
        AppProduct entity = appProductService.get(uuid);
        AppProductBean bean = new AppProductBean();
        BeanUtils.copyProperties(entity, bean);
        return bean;
    }

    @Override
    @Transactional
    public void saveBean(AppProductBean bean) {
        String uuid = bean.getUuid();
        AppProduct entity = new AppProduct();
        if (StringUtils.isNotBlank(uuid)) {
            entity = appProductService.get(uuid);
            // ID非空唯一性判断
            if (!commonValidateService.checkUnique(bean.getUuid(),
                    StringUtils.uncapitalize(AppProduct.class.getSimpleName()), ConfigurableIdEntity.ID, bean.getId())) {
                throw new RuntimeException("已经存在ID为[" + bean.getId() + "]的产品!");
            }
        } else {
            // ID非空唯一性判断
            if (commonValidateService.checkExists(StringUtils.uncapitalize(AppProduct.class.getSimpleName()),
                    ConfigurableIdEntity.ID, bean.getId())) {
                throw new RuntimeException("已经存在ID为[" + bean.getId() + "]的产品!");
            }
        }
        // 乐观锁判断
        if (entity.getRecVer() != null && !entity.getRecVer().equals(bean.getRecVer())) {
            throw new RuntimeException("数据已过时，请重新加载再更改保存或保存局部数据!");
        }
        BeanUtils.copyProperties(bean, entity);
        appProductService.save(entity);
        appProductIntegrationMgr.savePiTreeNode(bean.getPiTreeNode());
    }

    @Override
    public void update(String uuid) {
        // 更新乐观锁等系统及字段
        AppProduct appProduct = appProductService.get(uuid);
        if (appProduct != null) {
            appProductService.save(appProduct);
        }
    }

    @Override
    @Transactional
    public void remove(String uuid) {
        appProductService.remove(uuid);
        long piCount = appProductIntegrationMgr.countByAppProductUuid(uuid);
        if (piCount > 0) {
            throw new RuntimeException("产品存在集成信息，不能删除产品！");
        }
        // 删除集成信息
        // appProductIntegrationMgr.removeByAppProductUuid(uuid);
    }

    @Override
    public void removeAll(Collection<String> uuids) {
        appProductService.removeAllByPk(uuids);
    }

    @Override
    public Select2QueryData loadSelectData(Select2QueryInfo select2QueryInfo) {
        String queryValue = select2QueryInfo.getSearchValue();
        String excludeDataUuids = select2QueryInfo.getOtherParams("excludeDataUuids", "-1");
        String systemUnitId = SpringSecurityUtils.getCurrentUserUnitId();
        List<String> excludeUuids = Arrays.asList(StringUtils.split(excludeDataUuids, Separator.SEMICOLON.getValue()));
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("name", queryValue);
        values.put("id", queryValue);
        values.put("excludeUuids", excludeUuids);
        values.put("systemUnitId", systemUnitId);
        List<AppProduct> list = appProductService.listByNameHQLQueryAndPage("appProductSelect2Query", values,
                select2QueryInfo.getPagingInfo());
        return new Select2QueryData(list, "id", "name", select2QueryInfo.getPagingInfo());
    }

    @Override
    public Select2QueryData loadSelectDataByIds(Select2QueryInfo select2QueryInfo) {
        String[] appSysIds = select2QueryInfo.getIds();
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("appPrdIds", appSysIds);
        List<AppProduct> list = appProductService.listByNameHQLQueryAndPage("appProductSelect2IdsQuery", values,
                select2QueryInfo.getPagingInfo());
        return new Select2QueryData(list, "id", "name", select2QueryInfo.getPagingInfo());
    }

}
