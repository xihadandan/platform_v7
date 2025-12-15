/*
 * @(#)8/9/23 V1.0
 *
 * Copyright 2023 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.datadict.service.impl;

import com.google.common.collect.Maps;
import com.wellsoft.pt.basicdata.datadict.dao.CdDataDictionaryCategoryDao;
import com.wellsoft.pt.basicdata.datadict.entity.CdDataDictionaryCategoryEntity;
import com.wellsoft.pt.basicdata.datadict.enums.EnumDictionaryCategoryType;
import com.wellsoft.pt.basicdata.datadict.service.CdDataDictionaryCategoryService;
import com.wellsoft.pt.basicdata.datadict.service.CdDataDictionaryService;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

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
 * 8/9/23.1	zhulh		8/9/23		Create
 * </pre>
 * @date 8/9/23
 */
@Service
public class CdDataDictionaryCategoryServiceImpl extends AbstractJpaServiceImpl<CdDataDictionaryCategoryEntity, CdDataDictionaryCategoryDao, Long>
        implements CdDataDictionaryCategoryService {
    @Autowired
    private CdDataDictionaryService dataDictionaryService;

    /**
     * 根据模块ID获取字典分类
     *
     * @param moduleId
     * @return
     */
    @Override
    public List<CdDataDictionaryCategoryEntity> listByModuleId(String moduleId) {
        Assert.hasLength(moduleId, "模块ID不能为空");

        String hql = "from CdDataDictionaryCategoryEntity t where t.moduleId = :moduleId order by t.sortOrder asc";
        Map<String, Object> params = Maps.newHashMap();
        params.put("moduleId", moduleId);
        return this.listByHQL(hql, params);
    }

    /**
     * 根据字典分类UUID删除字典分类
     *
     * @param uuid
     */
    @Override
    @Transactional
    public void deleteByUuid(Long uuid) {
        // bug#67112 产品需求不用检骓
//        if (dataDictionaryService.countByCategoryUuid(uuid) > 0) {
//            throw new BusinessException("字典分类被引用，不能删除！");
//        }

        this.dao.delete(uuid);
    }

    /**
     * 获取模块ID为空的字典分类
     *
     * @return
     */
    @Override
    public List<CdDataDictionaryCategoryEntity> listWithoutModuleId() {
        String hql = "from CdDataDictionaryCategoryEntity t where t.moduleId = '' or t.moduleId is null order by t.modifyTime desc";
        return this.dao.listByHQL(hql, null);
    }

    /**
     * 获取所有字典分类
     *
     * @return
     */
    @Override
    public List<CdDataDictionaryCategoryEntity> listAllBySortOrderAsc() {
        String hql = "from CdDataDictionaryCategoryEntity t order by t.sortOrder asc";
        return this.dao.listByHQL(hql, null);
    }

    /**
     * 根据字典分类类型获取字典分类
     *
     * @param type
     * @return
     */
    @Override
    public List<CdDataDictionaryCategoryEntity> listByType(EnumDictionaryCategoryType type) {
        Assert.notNull(type, "字典分类类型不能为空！");

        String hql = "from CdDataDictionaryCategoryEntity t where t.type = :type order by t.sortOrder asc";
        Map<String, Object> params = Maps.newHashMap();
        params.put("type", EnumDictionaryCategoryType.getTypeCode(type));
        return this.dao.listByHQL(hql, params);
    }

    /**
     * 按类型升序获取所有字典分类
     *
     * @return
     */
    @Override
    public List<CdDataDictionaryCategoryEntity> listAllByTypeAsc() {
        String hql = "from CdDataDictionaryCategoryEntity t order by t.type, t.sortOrder";
        return this.dao.listByHQL(hql, null);
    }

}
