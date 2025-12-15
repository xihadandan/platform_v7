/*
 * @(#)4/27/23 V1.0
 *
 * Copyright 2023 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.material.service.impl;

import com.google.common.collect.Maps;
import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.pt.basicdata.material.dao.CdMaterialDefinitionDao;
import com.wellsoft.pt.basicdata.material.entity.CdMaterialDefinitionEntity;
import com.wellsoft.pt.basicdata.material.service.CdMaterialDefinitionService;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.security.access.RequestSystemContextPathResolver;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
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
 * 4/27/23.1	zhulh		4/27/23		Create
 * </pre>
 * @date 4/27/23
 */
@Service
public class CdMaterialDefinitionServiceImpl extends AbstractJpaServiceImpl<CdMaterialDefinitionEntity, CdMaterialDefinitionDao, Long>
        implements CdMaterialDefinitionService {
    /**
     * 根据材料编码获取材料
     *
     * @param code
     * @return
     */
    @Override
    public CdMaterialDefinitionEntity getByCode(String code) {
        Assert.hasLength(code, "材料编码不能为空！");

        CdMaterialDefinitionEntity entity = new CdMaterialDefinitionEntity();
        entity.setCode(code);
        List<CdMaterialDefinitionEntity> entities = this.dao.listByEntity(entity);
        if (CollectionUtils.isNotEmpty(entities)) {
            return entities.get(0);
        }
        return null;
    }

    /**
     * 根据材料编码判断材料是否存在
     *
     * @param code
     * @return
     */
    @Override
    public boolean existsByCode(String code) {
        Assert.hasLength(code, "材料编码不能为空！");

        CdMaterialDefinitionEntity entity = new CdMaterialDefinitionEntity();
        entity.setCode(code);
        return this.dao.countByEntity(entity) > 0;
    }

    /**
     * 根据材料UUID、编码判断材料是否唯一
     *
     * @param uuid
     * @param code
     * @return
     */
    @Override
    public boolean isUnique(Long uuid, String code) {
        Assert.hasLength(code, "材料编码不能为空！");

        Map<String, Object> values = Maps.newHashMap();
        values.put("uuid", uuid);
        values.put("code", code);
        String hql = "from CdMaterialDefinitionEntity t where t.uuid != :uuid and t.code = :code";
        return this.dao.countByHQL(hql, values) == 0;
    }

    /**
     * 根据名称查询材料
     *
     * @param name
     * @param pagingInfo
     * @return
     */
    @Override
    public List<CdMaterialDefinitionEntity> queryByName(String name, PagingInfo pagingInfo) {
        Map<String, Object> values = Maps.newHashMap();
        values.put("name", name);
        return this.dao.listByNameHQLQuery("listMaterialDefinitionQuery", values, pagingInfo);
    }

    @Override
    public List<String> listFormatByCodes(List<String> codes) {
        Assert.notEmpty(codes, "材料编码列表不能为空！");

        String hql = "select t.format as format from CdMaterialDefinitionEntity t where t.code in(:codes)";
        Map<String, Object> values = Maps.newHashMap();
        values.put("codes", codes);
        String system = RequestSystemContextPathResolver.system();
        if (StringUtils.isNotBlank(system)) {
            hql += " and t.system = :system";
            values.put("system", system);
        }
        List<String> formats = this.dao.listCharSequenceByHQL(hql, values);
        return formats;
    }

}
