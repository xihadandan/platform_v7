/*
 * @(#)12/20/23 V1.0
 *
 * Copyright 2023 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.biz.facade.service;

import com.wellsoft.context.service.Facade;
import com.wellsoft.pt.biz.dto.BizBusinessEntityLifecycleDto;
import com.wellsoft.pt.biz.dto.BizProcessInstanceDto;
import com.wellsoft.pt.biz.dto.BizProcessItemInstanceDto;
import com.wellsoft.pt.biz.dto.BizProcessNodeInstanceDto;
import com.wellsoft.pt.biz.support.ProcessDefinitionJson;

import java.util.List;

/**
 * Description: 业务主体门面接口
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 12/20/23.1	zhulh		12/20/23		Create
 * </pre>
 * @date 12/20/23
 */
public interface BizBusinessEntityFacadeService extends Facade {

    /**
     * 根据业务主体表单定义ID及业务主体ID获取办理过的业务流程定义
     *
     * @param entityFormId
     * @param entityId
     * @return
     */
    List<ProcessDefinitionJson> listProcessDefinitionByEntityFormIdAndEntityId(String entityFormId, String entityId);

    /**
     * 根据业务主体表单定义ID及业务主体ID获取办理过的业务流程实例
     *
     * @param entityFormId
     * @param entityId
     * @return
     */
    List<BizProcessInstanceDto> listProcessInstanceByEntityFormIdAndEntityId(String entityFormId, String entityId);

    /**
     * 根据业务流程定义ID及业务主体ID获取办理过的过程节点数据
     *
     * @param processDefId
     * @param entityId
     * @return
     */
    List<BizProcessNodeInstanceDto> listProcessNodeInstanceByProcessDefIdAndEntityId(String processDefId, String entityId);

    /**
     * 根据业务流程实例UUID列表获取办理过的过程节点数据
     *
     * @param processInstUuids
     * @return
     */
    List<BizProcessNodeInstanceDto> listProcessNodeInstanceByProcessInstUuids(List<String> processInstUuids);

    /**
     * 根据过程节点实例UUID获取办理过的业务事项数据
     *
     * @param processNodeInstUuid
     * @return
     */
    List<BizProcessItemInstanceDto> listProcessItemInstanceByProcessNodeInstUuid(String processNodeInstUuid);

    /**
     * 根据业务流程实例UUID获取办理过的业务事项数据
     *
     * @param processInstUuids
     * @return
     */
    List<BizProcessItemInstanceDto> listProcessItemInstanceByProcessInstUuids(List<String> processInstUuids);

    /**
     * 根据业务主体表单定义ID及业务主体ID获取办生命周期数据
     *
     * @param entityFormId
     * @param entityId
     * @return
     */
    BizBusinessEntityLifecycleDto getLifecycleByEntityFormIdAndEntityId(String entityFormId, String entityId);
}
