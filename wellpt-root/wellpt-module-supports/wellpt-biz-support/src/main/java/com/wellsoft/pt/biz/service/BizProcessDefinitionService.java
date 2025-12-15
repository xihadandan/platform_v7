/*
 * @(#)9/27/22 V1.0
 *
 * Copyright 2022 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.biz.service;

import com.wellsoft.pt.biz.dao.BizProcessDefinitionDao;
import com.wellsoft.pt.biz.entity.BizProcessDefinitionEntity;
import com.wellsoft.pt.biz.query.BizProcessDefinitionQueryItem;
import com.wellsoft.pt.biz.support.ProcessDefinitionJson;
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
 * 9/27/22.1	zhulh		9/27/22		Create
 * </pre>
 * @date 9/27/22
 */
public interface BizProcessDefinitionService extends JpaService<BizProcessDefinitionEntity, BizProcessDefinitionDao, String> {

    /**
     * 根据ID获取业务流程定义
     *
     * @param id
     * @return
     */
    BizProcessDefinitionEntity getById(String id);

    /**
     * 保存业务流程定义
     *
     * @param entity
     */
    void saveDefinition(BizProcessDefinitionEntity entity);

    /**
     * 根据ID获取数量
     *
     * @param id
     * @return
     */
    Long countById(String id);

    /**
     * @param entity
     * @return
     */
    long countByEntity(BizProcessDefinitionEntity entity);

    /**
     * 根据UUID获取最大版本号
     *
     * @param uuid
     * @return
     */
    Double getMaxVersionByUuid(String uuid);

    /**
     * 根据ID判断业务流程定义是否存在
     *
     * @param id
     * @return
     */
    boolean isExistsById(String id);

    /**
     * 根据业务ID列表获取业务流程定义数量
     *
     * @param businessIds
     * @return
     */
    Long countByBusinessIds(List<String> businessIds);

    /**
     * 根据UUID获取业务流程定义JSON
     *
     * @param uuid
     * @return
     */
    ProcessDefinitionJson getProcessDefinitionJsonByUuid(String uuid);

    /**
     * 根据ID获取业务流程定义JSON
     *
     * @param id
     * @return
     */
    ProcessDefinitionJson getProcessDefinitionJsonById(String id);

    /**
     * 根据业务流程配置项模板UUID列表判断是否被使用
     *
     * @param templateUuids
     * @return
     */
    boolean isUsedDefinitionTemplateByTemplateUuids(List<String> templateUuids);

    /**
     * 根据流程定义UUID列表删除流程定义
     *
     * @param uuids
     */
    void deleteAllByUuids(List<String> uuids);

    /**
     * @param businessId
     * @param excludeDefId
     * @return
     */
    List<String> listUuidByBusinessId(String businessId, String excludeDefId);

    /**
     * @param refId
     * @param templateType
     * @param processDefUuid
     * @return
     */
    List<BizProcessDefinitionQueryItem> listOfRefTemplate(String refId, String templateType, String processDefUuid);

}
