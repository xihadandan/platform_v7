/*
\ * @(#)2013-4-15 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.org.service;

import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.context.jdbc.support.QueryInfo;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.pt.org.bean.DutyAgentBean;
import com.wellsoft.pt.org.entity.DutyAgent;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Description: 职务代理人服务接口
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-4-15.1	zhulh		2013-4-15		Create
 * </pre>
 * @date 2013-4-15
 */
public interface DutyAgentService {
    /**
     * 分页查询
     *
     * @param queryInfo
     * @return
     */
    List<QueryItem> query(QueryInfo queryInfo);

    /**
     * 获取职务代理人PO类
     *
     * @param uuid
     * @return
     */
    DutyAgentBean getBean(String uuid);

    /**
     * 保存职务代理人PO类
     *
     * @param bean
     */
    void saveBean(DutyAgentBean bean);

    /**
     * 委托生效
     *
     * @param uuid
     */
    void active(String uuid);

    /**
     * 委托终止
     *
     * @param dutyAgentUuid
     */
    void deactive(String uuid);

    /**
     * 拒绝委托，委托终止
     *
     * @param uuid
     */
    void refuse(String uuid);

    /**
     * 根据UUID删除职务代理人
     *
     * @param uuid
     */
    void remove(String uuid);

    /**
     * 根据UUID，批量删除职务代理人
     *
     * @param uuid
     */
    void removeAll(Collection<String> uuids);

    /**
     * 获取委托人在指定业务类型下业务内容的职务代理人
     *
     * @param consignor
     * @param businessType
     * @param content
     * @param root
     * @return
     */
    List<String> getDutyAgentIds(String consignor, String businessType, String content, Map<Object, Object> root);

    /**
     * 获取委托人在指定业务类型下业务内容的职务代理人
     *
     * @param consignor
     * @param businessType
     * @param content
     * @param root
     * @return
     */
    List<DutyAgent> getDutyAgents(List<String> consignorIds, String businessType);

    /**
     * 从指定类型开始异步加载树形结点，维护时不需要考虑ACL权限
     *
     * @param id
     * @return
     */
    List<TreeNode> getContentAsTreeAsync(String uuid, String businessType);

    /**
     * 根据指定类型及值，获取相应的显示值
     *
     * @param id
     * @return
     */
    QueryItem getKeyValuePair(String businessType, String value);

    /**
     * 获取所有动态表单定义基本信息
     *
     * @return
     */
    List<QueryItem> getAllDyFormDefinitionBasicInfo();

    List<String> getLimitDyformUuids(String limitFlowDefId);

    /**
     * 获取所有动态表单定义基本信息
     *
     * @return
     */
    List<QueryItem> getDyFormFieldDefinition(String formUuid);

    /**
     * 根据委托人及业务类型获取该委托人当前有效的业务代理人列表
     *
     * @param consignor
     * @param businessType
     * @return
     */
    public List<DutyAgent> getActiveDutyAgents(String consignor, String businessType);

    /**
     * 根据委托人ID列表及业务类型获取该委托人当前有效的业务代理人列表
     *
     * @param consignorIds
     * @param businessType
     * @return
     */
    public List<DutyAgent> getActiveDutyAgents(List<String> consignorIds, String businessType);

}
