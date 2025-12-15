/*
 * @(#)2018年9月25日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.script.service.impl;

import com.wellsoft.pt.basicdata.script.dao.CdScriptDefinitionDao;
import com.wellsoft.pt.basicdata.script.entity.CdScriptDefinitionEntity;
import com.wellsoft.pt.basicdata.script.service.CdScriptDefinitionService;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import org.springframework.stereotype.Service;

import java.util.HashMap;
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
 * 2018年9月25日.1	zhulh		2018年9月25日		Create
 * </pre>
 * @date 2018年9月25日
 */
@Service
public class CdScriptDefinitionServiceImpl extends
        AbstractJpaServiceImpl<CdScriptDefinitionEntity, CdScriptDefinitionDao, String> implements
        CdScriptDefinitionService {

    // 根据ID获取脚本定义
    private String GET_BY_ID = "from CdScriptDefinitionEntity t where t.id = :id";
    // 根据类型获取所有脚本定义
    private String GET_ALL_BY_TYPE = "from CdScriptDefinitionEntity t where t.type = :type";

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.script.service.CdScriptDefinitionService#getById(java.lang.String)
     */
    @Override
    public CdScriptDefinitionEntity getById(String id) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("id", id);
        return this.dao.getOneByHQL(GET_BY_ID, params);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.script.service.CdScriptDefinitionService#getAllByType(java.lang.String)
     */
    @Override
    public List<CdScriptDefinitionEntity> getAllByType(String type) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("type", type);
        return this.dao.listByHQL(GET_ALL_BY_TYPE, params);
    }

}
