/*
 * @(#)2013-4-15 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.org.service;

import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.context.jdbc.support.QueryInfo;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.pt.org.bean.DutyBean;
import com.wellsoft.pt.org.entity.Duty;
import com.wellsoft.pt.org.entity.User;

import java.util.*;

/**
 * Description: 职务服务接口
 *
 * @author zhengky
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	       修改人		修改日期	      修改内容
 * 2014-8-22.1  zhengky	2014-8-22	  Create
 * </pre>
 * @date 2014-8-22
 */
public interface DutyService {

    /**
     * 根据UUID获取
     *
     * @param uuid
     * @return
     */
    Duty getByUuid(String uuid);

    /**
     * 根据ID获取
     *
     * @param id
     * @return
     */
    Duty getById(String id);

    /**
     * 分页查询
     *
     * @param queryInfo
     * @return
     */
    List<QueryItem> query(QueryInfo queryInfo);

    /**
     * 如何描述该方法
     *
     * @param uuid
     * @return
     */
    DutyBean getBean(String uuid);

    /**
     * 如何描述该方法
     *
     * @param bean
     */
    void saveBean(DutyBean bean);

    /**
     * 如何描述该方法
     *
     * @param uuid
     */
    void remove(String uuid);

    /**
     * 如何描述该方法
     *
     * @param uuids
     */
    void removeAll(Collection<String> uuids);

    /**
     * 将职务数据列表保存到数据字典表中(Excel导入)
     *
     * @param list
     */
    HashMap<String, Object> saveDutyFromList(List list);

    /**
     * 构建职务树形下拉选择框
     */
    List<TreeNode> createDutyTreeSelect(String conditon);

    /**
     * 通过职务ID获得下面所属用户
     *
     * @param dutyId
     * @return
     */
    List<User> getUsersByDutyId(String dutyId);

    /**
     * 根据职务UUID加载角色树，自动选择已有角色
     *
     * @param uuid
     * @return
     */
    TreeNode getRoleTree(String uuid);

    /**
     * s
     * 根据UUID加载职务角色嵌套树, 包含角色嵌套及权限
     *
     * @param uuid
     * @return
     */
    TreeNode getDutyRoleNestedRoleTree(String uuid);

    List<Duty> getAll();

    void save(Duty duty);

    Set<Duty> getDutyByRoleUuid(String roleUuid);

    List<Duty> getAllDutys();

    List<Duty> namedQuery(String string, Map<String, Object> values, Class<Duty> class1, PagingInfo pagingInfo);

}
