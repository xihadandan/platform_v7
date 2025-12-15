/*
 * @(#)1/31/24 V1.0
 *
 * Copyright 2024 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dyform.implement.definition.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wellsoft.context.component.select2.Select2QueryData;
import com.wellsoft.context.component.select2.Select2QueryInfo;
import com.wellsoft.pt.dyform.implement.definition.dao.DyformCategoryDao;
import com.wellsoft.pt.dyform.implement.definition.entity.DyformCategoryEntity;
import com.wellsoft.pt.dyform.implement.definition.service.DyformCategoryService;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
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
 * 1/31/24.1	zhulh		1/31/24		Create
 * </pre>
 * @date 1/31/24
 */
@Service
public class DyformCategoryServiceImpl extends
        AbstractJpaServiceImpl<DyformCategoryEntity, DyformCategoryDao, Long> implements DyformCategoryService {

    /**
     * 根据模块ID获取流程分类
     *
     * @param moduleId
     * @return
     */
    @Override
    public List<DyformCategoryEntity> listByModuleId(String moduleId) {
        Assert.hasLength(moduleId, "模块ID不能为空！");

        DyformCategoryEntity entity = new DyformCategoryEntity();
        entity.setModuleId(moduleId);
        return this.dao.listByEntity(entity);
    }

    /**
     * 根据关键字、模块ID获取流程分类
     *
     * @param keyword
     * @param moduleId
     * @return
     */
    @Override
    public List<DyformCategoryEntity> listByKeywordAndModuleId(String keyword, String moduleId) {
        Assert.hasLength(keyword, "关键字不能为空！");

        Map<String, Object> params = Maps.newHashMap();
        params.put("name", keyword);
        params.put("code", keyword);
        params.put("remark", keyword);
        params.put("moduleId", moduleId);
        return this.dao.listByNameHQLQuery("dyformCategoryQuery", params);
    }

    /**
     * select2查询接口
     *
     * @param queryInfo
     * @return
     */
    @Override
    public Select2QueryData loadSelectData(Select2QueryInfo queryInfo) {
        String keyword = queryInfo.getSearchValue();
        String moduleId = queryInfo.getOtherParams("moduleId");
        List<DyformCategoryEntity> entities = null;
        if (StringUtils.isNotBlank(keyword)) {
            entities = this.listByKeywordAndModuleId(keyword, moduleId);
        } else if (StringUtils.isNotBlank(moduleId)) {
            entities = this.listByModuleId(moduleId);
        } else {
            entities = this.listAllByOrderPage(null, "code asc");
        }
        return new Select2QueryData(entities, "uuid", "name");
    }

    /**
     * 通过ID查找Text.对于远程查找分页需要实现，否则无法设置选中。
     *
     * @param queryInfo
     * @return
     */
    @Override
    public Select2QueryData loadSelectDataByIds(Select2QueryInfo queryInfo) {
        String[] ids = queryInfo.getIds();
        List<DyformCategoryEntity> entities = Lists.newArrayListWithCapacity(0);
        if (ArrayUtils.isNotEmpty(ids)) {
            Long[] uuids = new Long[ids.length];
            for (int index = 0; index < ids.length; index++) {
                uuids[index] = Long.valueOf(ids[index]);
            }
            entities = this.dao.listByFieldInValues("uuid", Lists.newArrayList(uuids));
        }
        return new Select2QueryData(entities, "uuid", "name");
    }

}
