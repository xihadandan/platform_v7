/*
 * @(#)2018年9月25日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.script.facade.service;

import com.wellsoft.context.component.select2.Select2QueryApi;
import com.wellsoft.context.service.BaseService;
import com.wellsoft.pt.basicdata.script.dto.CdScriptDefinitionDto;
import com.wellsoft.pt.basicdata.script.support.ScriptDefinition;

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
public interface CdScriptDefinitionFacadeService extends BaseService, Select2QueryApi {

    CdScriptDefinitionDto get(String uuid);

    void save(CdScriptDefinitionDto cdScriptDefinitionDto);

    void removeAll(List<String> uuids);

    /**
     * 根据脚本定义ID获取脚本
     *
     * @param scriptDefinitionId
     * @return
     */
    ScriptDefinition getScriptDefinitionById(String scriptDefinitionId);

}
