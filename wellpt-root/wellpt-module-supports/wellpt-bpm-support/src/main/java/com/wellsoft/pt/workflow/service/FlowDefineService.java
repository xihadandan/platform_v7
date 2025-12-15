/*
 * @(#)2012-10-19 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.workflow.service;

import com.wellsoft.context.component.jqgrid.JqGridQueryInfo;
import com.wellsoft.context.jdbc.support.QueryData;
import com.wellsoft.context.jdbc.support.QueryInfo;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.context.service.BaseService;
import com.wellsoft.pt.bpm.engine.entity.FlowDefinition;
import com.wellsoft.pt.bpm.engine.query.api.FlowDefinitionQueryItem;

import java.util.Collection;
import java.util.List;

/**
 * Description: 工作流程定义服务类，处理界面上定义的流程，前台提交为xml，进行解析，保存到数据库中
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 2012-10-19.1	zhulh		2012-10-19		Create
 * </pre>
 * @date 2012-10-19
 */
public interface FlowDefineService extends BaseService {
    /**
     * 分页查询
     *
     * @param queryInfo
     * @return
     */
    List<QueryItem> query(QueryInfo queryInfo);

    /**
     * 获取JqGrid树形列表数据
     *
     * @param jqGridQueryInfo
     * @param queryInfo
     * @return
     */
    QueryData getForPageAsTree(JqGridQueryInfo jqGridQueryInfo, QueryInfo queryInfo);

    /**
     * 根据流程分类编号，获取该分类下所有最高版本的流程定义
     *
     * @param catagoryCode 流程分类
     * @return
     */
    List<QueryItem> getMaxVerFlowDefByCatagoryCode(String categoryCode);

    /**
     * 获取指定的流程定义
     *
     * @param uuid
     */
    FlowDefinition get(String uuid);

    /**
     * 如何描述该方法
     *
     * @param flowDefinition
     */
    void save(FlowDefinition flowDefinition);

    /**
     * 删除流程定义
     *
     * @param uuid
     */
    void remove(String uuid);

    /**
     * 删除流程定义
     *
     * @param uuids
     */
    void removeAll(Collection<String> uuids);

    String getTaskMultiUserSubmitType(String flowDefinitionUuid, String taskId);

    void updateFlowDefCategory(String uuid, String categoryUuid);

    List<FlowDefinitionQueryItem> queryAllModuleFlowDefs(String moduleId);

    List<FlowDefinitionQueryItem> queryAllModuleFlowDefs(List<String> moduleId);
}
