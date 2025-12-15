/*
 * @(#)2018年9月25日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.script.service;

import com.wellsoft.pt.basicdata.script.dao.CdScriptDefinitionDao;
import com.wellsoft.pt.basicdata.script.entity.CdScriptDefinitionEntity;
import com.wellsoft.pt.jpa.service.JpaService;

import java.util.List;

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
public interface CdScriptDefinitionService extends JpaService<CdScriptDefinitionEntity, CdScriptDefinitionDao, String> {

    /**
     * @param id
     * @return
     */
    CdScriptDefinitionEntity getById(String id);

    /**
     * 如何描述该方法
     *
     * @param type
     * @return
     */
    List<CdScriptDefinitionEntity> getAllByType(String type);

}
