/*
 * @(#)2013-3-17 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.service;

import com.wellsoft.context.component.select2.Select2QueryData;
import com.wellsoft.context.component.select2.Select2QueryInfo;
import com.wellsoft.context.jdbc.support.Page;
import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.pt.bpm.engine.dao.FlowDefinitionDao;
import com.wellsoft.pt.bpm.engine.delegate.FlowDelegate;
import com.wellsoft.pt.bpm.engine.element.FlowElement;
import com.wellsoft.pt.bpm.engine.entity.FlowDefinition;
import com.wellsoft.pt.bpm.engine.entity.FlowSchema;
import com.wellsoft.pt.bpm.engine.parser.FlowConfiguration;
import com.wellsoft.pt.jpa.service.JpaService;
import com.wellsoft.pt.log.enums.LogManageOperationEnum;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-3-17.1	zhulh		2013-3-17		Create
 * </pre>
 * @date 2013-3-17
 */
public interface FlowDefinitionService extends JpaService<FlowDefinition, FlowDefinitionDao, String> {

    /**
     * 通过UUID或者是流程分类uuid集合 获取流程定义集合
     *
     * @param uuids ID或者是流程分类uuid集合
     * @return java.util.List<com.wellsoft.pt.bpm.engine.entity.FlowDefinition>
     **/
    public List<FlowDefinition> getListByIdsOrCategoryUuids(List<String> uuids);

    /**
     * 检查流程定义XML内容是否可更新
     *
     * @param xml
     * @return
     */
    boolean checkFlowXmlForUpdate(String xml);

    /**
     * 解析与保存流程定义
     *
     * @param xml
     * @param pbNew
     * @param isCopy 是否复制操作
     * @return
     */
    FlowConfiguration parseAndSave(String xml, Boolean pbNew, Boolean isCopy);

    /**
     * 解析与保存流程定义
     *
     * @param xml
     * @param pbNew
     * @return
     */
    FlowDefinition parseAndSaveForUpdate(String xml);

    /**
     * 更新流程定义的流程分类编号
     *
     * @param oldCategoryCode
     * @param newCategoryCode
     */
    void updateCategory(String oldCategoryCode, String newCategoryCode);

    /**
     * 根据流程定义UUID，判断该定义是不是最高版本的流程定义
     *
     * @param uuid
     * @return
     */
    boolean isMaxVersion(String uuid);

    /**
     * 如何描述该方法
     *
     * @param example
     * @return
     */
    Long countByEntity(FlowDefinition example);

    void getLatest(Page<FlowDefinition> dataPage);

    Long countByCategory(String category);

    Long countById(String id);

    List<FlowDefinition> getByCategory(String categorySN);

    List<FlowDefinition> getByFormUuid(String formUuid);

    FlowDefinition getById(String id);

    /**
     * 通过id集合获取流程定义集合
     * 没找到数据，返回Null
     *
     * @param ids
     **/
    List<FlowDefinition> getByIds(Set<String> ids);

    Double getLatestVersionById(String id);

    /**
     * 如何描述该方法
     *
     * @param string
     * @param values
     * @param class1
     * @param pagingInfo
     * @return
     */
    List<QueryItem> flowDefinitionTreeQuery(String string, Map<String, Object> values, PagingInfo pagingInfo);

    /**
     * 如何描述该方法
     *
     * @return
     */
    List<String> getAllUuids();

    /**
     * 获取最新更新的指定数量流程定义UUID
     *
     * @param count
     * @return
     */
    List<String> getLatestUpdatedUuids(int count);

    /**
     * 如何描述该方法
     *
     * @param example
     * @return
     */
    List<FlowDefinition> findByExample(FlowDefinition example);

    /**
     * 如何描述该方法
     *
     * @param flowDefId
     * @return
     */
    List<String> getAllUuidsById(String flowDefId);

    /**
     * @param taskInstUuid
     * @return
     */
    String getFlowNameByTaskInstUuid(String taskInstUuid);

    Select2QueryData loadSelectDataWorkflowDefinition(Select2QueryInfo select2QueryInfo);

    /**
     * 根据系统单位Id统计数量
     *
     * @param systemUnitId
     * @return
     */
    long countBySystemUnitId(String systemUnitId);

    void saveAcl(FlowDefinition flowDefinition, FlowElement flowElement);

    /**
     * 根据流程定义UUID获取流程定义解析委派类
     *
     * @param flowDefUuid
     * @return
     */
    FlowDelegate getFlowDelegate(String flowDefUuid);

    /**
     * 保存流程分类的管理操作日志
     *
     * @param newFlowDefinition      新的的流程定义数据
     * @param oldFlowDefinition      旧的流程定义数据 新增时为null
     * @param logManageOperationEnum 操作类型
     * @return void
     **/
    public void saveLogManageOperation(FlowDefinition newFlowDefinition, FlowDefinition oldFlowDefinition,
                                       LogManageOperationEnum logManageOperationEnum);

    /**
     * 保存流程分类的管理操作日志
     *
     * @param newFlowDefinition      新的的流程定义数据
     * @param oldFlowDefinition      旧的流程定义数据 新增时为null
     * @param oldflowSchema          旧的流程定义规划对象
     * @param logManageOperationEnum 操作类型
     * @return void
     **/
    public void saveLogManageOperation(FlowDefinition newFlowDefinition, FlowDefinition oldFlowDefinition,
                                       FlowSchema oldflowSchema, LogManageOperationEnum logManageOperationEnum);

    FlowDefinition getByFlowSchemaUuid(String flowSchemaUuid);

    List<String> listFormUuidByCategoryUuidsAndFlowDefIds(List<String> categoryUuids, List<String> flowDefIds);
}
