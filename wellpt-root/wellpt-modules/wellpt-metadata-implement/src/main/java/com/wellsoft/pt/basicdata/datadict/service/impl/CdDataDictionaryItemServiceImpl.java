/*
 * @(#)8/9/23 V1.0
 *
 * Copyright 2023 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.datadict.service.impl;

import com.google.common.collect.Maps;
import com.wellsoft.pt.basicdata.datadict.dao.CdDataDictionaryItemDao;
import com.wellsoft.pt.basicdata.datadict.entity.CdDataDictionaryItemEntity;
import com.wellsoft.pt.basicdata.datadict.service.CdDataDictionaryItemService;
import com.wellsoft.pt.common.i18n.service.DataI18nService;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import org.apache.commons.collections.CollectionUtils;
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
public class CdDataDictionaryItemServiceImpl extends AbstractJpaServiceImpl<CdDataDictionaryItemEntity, CdDataDictionaryItemDao, Long>
        implements CdDataDictionaryItemService {


    @Autowired
    private DataI18nService dataI18nService;

    /**
     * 根据字典UUID获取字典子项数据
     *
     * @param dataDictUuid
     * @return
     */
    @Override
    public List<CdDataDictionaryItemEntity> listByDataDictUuid(Long dataDictUuid) {
        Assert.notNull(dataDictUuid, "数据字典UUID不能为空！");

        String hql = "from CdDataDictionaryItemEntity t where t.dataDictUuid = :dataDictUuid order by t.sortOrder asc";
        Map<String, Object> params = Maps.newHashMap();
        params.put("dataDictUuid", dataDictUuid);
        return this.dao.listByHQL(hql, params);
    }

    /**
     * 根据字典UUID列表删除字典子项数据
     *
     * @param dataDictUuids
     */
    @Override
    @Transactional
    public void deleteByDataDictUuids(List<Long> dataDictUuids) {
        Assert.notEmpty(dataDictUuids, "数据字典UUID列表不能为空！");

        Map<String, Object> params = Maps.newHashMap();
        params.put("dataDictUuids", dataDictUuids);

        // 删除扩展属性
        String deleteAttrHql = "delete from CdDataDictionaryItemAttributeEntity t where t.itemUuid in(select uuid from CdDataDictionaryItemEntity where dataDictUuid in(:dataDictUuids))";
        this.dao.deleteByHQL(deleteAttrHql, params);

        // 删除扩展属性
        String deleteHql = "delete from CdDataDictionaryItemEntity t where t.dataDictUuid in(:dataDictUuids)";
        this.dao.deleteByHQL(deleteHql, params);
    }

    /**
     * 根据字典值及字典数据UUID获取字典项
     *
     * @param value
     * @param dataDictUuid
     * @return
     */
    @Override
    public CdDataDictionaryItemEntity getByValueAndDataDictUuid(String value, Long dataDictUuid) {
        Assert.hasLength(value, "数据项值不能为空！");
        Assert.notNull(dataDictUuid, "数据字典UUID不能为空！");

        String hql = "from CdDataDictionaryItemEntity t where t.value = :value and t.dataDictUuid = :dataDictUuid";
        Map<String, Object> params = Maps.newHashMap();
        params.put("value", value);
        params.put("dataDictUuid", dataDictUuid);
        List<CdDataDictionaryItemEntity> entities = this.dao.listByHQL(hql, params);
        if (CollectionUtils.isNotEmpty(entities)) {
            return entities.get(0);
        }
        return null;
    }

    /**
     * 根据上级字典项UUID获取字典子项数据
     *
     * @param parentUuid
     * @return
     */
    @Override
    public List<CdDataDictionaryItemEntity> listByParentUuid(Long parentUuid) {
        Assert.notNull(parentUuid, "上级字典项UUID不能为空！");

        return this.dao.listByFieldEqValue("parentUuid", parentUuid);
    }

    /**
     * 根据UUID更新排序
     *
     * @param uuid
     * @param sortOrder
     */
    @Override
    @Transactional
    public void updateSortOrderByUuid(Long uuid, Integer sortOrder) {
        Assert.notNull(uuid, "字典项UUID不能为空！");

        Map<String, Object> params = Maps.newHashMap();
        params.put("uuid", uuid);
        params.put("sortOrder", sortOrder != null ? sortOrder : 0);
        String hql = "update CdDataDictionaryItemEntity t set t.sortOrder = :sortOrder where t.uuid = :uuid";
        this.updateByHQL(hql, params);
    }

    @Override
    public List<CdDataDictionaryItemEntity> listRootByDataDictUuid(Long dataDictUuid) {
        Assert.notNull(dataDictUuid, "数据字典UUID不能为空！");

        String hql = "from CdDataDictionaryItemEntity t where t.dataDictUuid = :dataDictUuid and t.parentUuid is null order by t.sortOrder asc";
        Map<String, Object> params = Maps.newHashMap();
        params.put("dataDictUuid", dataDictUuid);
        return this.dao.listByHQL(hql, params);
    }

    @Override
    public List<CdDataDictionaryItemEntity> listByParentUuids(List<Long> parentUuids) {
        return this.dao.listByFieldInValues("parentUuid", parentUuids);
    }

}
