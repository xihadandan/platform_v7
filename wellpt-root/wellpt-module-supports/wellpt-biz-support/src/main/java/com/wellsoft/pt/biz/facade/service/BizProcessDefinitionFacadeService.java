/*
 * @(#)9/28/22 V1.0
 *
 * Copyright 2022 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.biz.facade.service;

import com.wellsoft.context.component.select2.Select2QueryData;
import com.wellsoft.context.component.select2.Select2QueryInfo;
import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.context.service.Facade;
import com.wellsoft.pt.biz.dto.BizProcessDefinitionDto;
import com.wellsoft.pt.biz.dto.BizProcessNodeDto;
import com.wellsoft.pt.biz.query.BizDefinitionTemplateQueryItem;
import com.wellsoft.pt.biz.query.BizProcessDefinitionQueryItem;
import com.wellsoft.pt.biz.support.ProcessDefinitionJson;

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
 * 9/28/22.1	zhulh		9/28/22		Create
 * </pre>
 * @date 9/28/22
 */
public interface BizProcessDefinitionFacadeService extends Facade {
    /**
     * 获取业务流程定义
     *
     * @param uuid
     * @return
     */
    BizProcessDefinitionDto getDto(String uuid);


    /**
     * 根据ID获取业务流程定义
     *
     * @param id
     * @return
     */
    BizProcessDefinitionDto getDtoById(String id);

    /**
     * 保存业务流程定义
     *
     * @param dto
     */
    String saveDto(BizProcessDefinitionDto dto);

    /**
     * 根据业务流程定义UUID列表删除业务流程定义
     *
     * @param uuids
     */
    void deleteAll(List<String> uuids);

    /**
     * 获取表单定义下拉数据
     *
     * @param queryInfo
     * @return
     */
    Select2QueryData listDyFormDefinitionSelectData(Select2QueryInfo queryInfo);

    /**
     * 根据表单ID获取表单定义下拉数据
     *
     * @param queryInfo
     * @return
     */
    Select2QueryData getDyFormDefinitionSelectDataByUuids(Select2QueryInfo queryInfo);

    /**
     * 获取业务流程监听器下拉数据
     *
     * @param queryInfo
     * @return
     */
    Select2QueryData listBizProcessListenerSelectData(Select2QueryInfo queryInfo);

    /**
     * 获取过程节点监听器下拉数据
     *
     * @param queryInfo
     * @return
     */
    Select2QueryData listBizProcessNodeListenerSelectData(Select2QueryInfo queryInfo);

    /**
     * 获取业务事项监听器下拉数据
     *
     * @param queryInfo
     * @return
     */
    Select2QueryData listBizProcessItemListenerSelectData(Select2QueryInfo queryInfo);

    /**
     * 保存业务流程定义JSON信息
     *
     * @param processDefinitionJson
     * @return
     */
    String saveProcessDefinitionJson(ProcessDefinitionJson processDefinitionJson);

    /**
     * 保存业务流程定义JSON信息为新版本
     *
     * @param processDefinitionJson
     * @return
     */
    String saveProcessDefinitionJsonAsNewVersion(ProcessDefinitionJson processDefinitionJson);

    /**
     * 复制业务流程定义
     *
     * @param uuid
     * @param newName
     * @return
     */
    String copy(String uuid, String newName, String newId);

    /**
     * 根据业务流程定义UUID获取过程结点信息
     *
     * @param uuid
     * @return
     */
    List<BizProcessNodeDto> listProcessNodeItemByUuid(String uuid);

    /**
     * 根据业务流程定义ID获取过程结点信息
     *
     * @param id
     * @return
     */
    List<BizProcessNodeDto> listProcessNodeItemById(String id);

    /**
     * 获取业务流程定义模板树
     *
     * @param businessId
     * @param excludeDefId
     * @return
     */
    List<TreeNode> getTemplateTree(String businessId, String excludeDefId);

    /**
     * 根据业务流程定义UUID获取定义的模板信息
     *
     * @param uuid
     * @return
     */
    List<BizDefinitionTemplateQueryItem> listNodeAndItemTemplateItemByUuid(String uuid);

    /**
     * 获取引用指定模板信息的流程定义
     *
     * @param refId
     * @param templateType
     * @param processDefUuid
     * @return
     */
    List<BizProcessDefinitionQueryItem> listOfRefTemplate(String refId, String templateType, String processDefUuid);
}
