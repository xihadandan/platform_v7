/*
 * @(#)2021-01-22 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.imgcategory.service.impl;

import com.google.common.collect.Maps;
import com.wellsoft.pt.app.entity.AppDefElementI18nEntity;
import com.wellsoft.pt.app.service.AppDefElementI18nService;
import com.wellsoft.pt.basicdata.iexport.suport.IexportType;
import com.wellsoft.pt.basicdata.imgcategory.dao.CdImgCategoryDao;
import com.wellsoft.pt.basicdata.imgcategory.entity.CdImgCategoryEntity;
import com.wellsoft.pt.basicdata.imgcategory.service.CdImgCategoryService;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.jpa.util.BeanUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Description: 数据库表CD_IMG_CATEGORY的service服务接口实现类
 *
 * @author zhongzh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2021-01-22.1	zhongzh		2021-01-22		Create
 * </pre>
 * @date 2021-01-22
 */
@Service
public class CdImgCategoryServiceImpl extends AbstractJpaServiceImpl<CdImgCategoryEntity, CdImgCategoryDao, String>
        implements CdImgCategoryService {

    @Autowired
    private AppDefElementI18nService appDefElementI18nService;

    @Override
    @Transactional
    public CdImgCategoryEntity saveBean(CdImgCategoryEntity bean) {
        CdImgCategoryEntity entity = null;
        if (StringUtils.isNotBlank(bean.getUuid())) {
            entity = dao.getOne(bean.getUuid());
        }
        if (null == entity) {
            entity = bean;
        } else {
            BeanUtils.copyProperties(bean, entity);
        }
        dao.save(entity);

        if (CollectionUtils.isNotEmpty(bean.getI18ns())) {
            appDefElementI18nService.deleteAllI18n(null, entity.getUuid(), null, IexportType.CdImgCategory);
            for (AppDefElementI18nEntity i : bean.getI18ns()) {
                i.setDefId(entity.getUuid());
                i.setApplyTo(IexportType.CdImgCategory);
                i.setVersion(new BigDecimal(1.0));
            }
            appDefElementI18nService.saveAll(bean.getI18ns());
        }

        return entity;
    }

    @Override
    public List<CdImgCategoryEntity> queryAllCatetory(String systemUnitId) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("systemUnitId", systemUnitId);
        String hql = "from CdImgCategoryEntity t where (t.systemUnitId = 'S0000000000' or t.systemUnitId = :systemUnitId) order by t.code desc";
        return dao.listByHQL(hql, params);
    }
}
