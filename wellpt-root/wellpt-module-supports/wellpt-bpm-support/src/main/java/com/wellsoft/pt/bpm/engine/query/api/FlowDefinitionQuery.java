/*
 * @(#)Mar 29, 2018 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.query.api;

import com.wellsoft.pt.jpa.query.Query;
import org.springframework.security.acls.model.Permission;

import java.util.Collection;

/**
 * Description: 流程定义查询接口
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * Mar 29, 2018.1	zhulh		Mar 29, 2018		Create
 * </pre>
 * @date Mar 29, 2018
 */
public interface FlowDefinitionQuery extends Query<FlowDefinitionQuery, FlowDefinitionQueryItem> {

    /**
     * UUID等于查询
     *
     * @param uuid
     * @return
     */
    FlowDefinitionQuery uuid(String uuid);

    /**
     * UUID in查询
     *
     * @param uuids
     * @return
     */
    FlowDefinitionQuery uuids(Collection<String> uuids);

    /**
     * ID等于查询
     *
     * @param id
     * @return
     */
    FlowDefinitionQuery id(String id);

    /**
     * ID in查询
     *
     * @param ids
     * @return
     */
    FlowDefinitionQuery ids(Collection<String> ids);

    /**
     * 名称等于查询
     *
     * @param name
     * @return
     */
    FlowDefinitionQuery name(String name);

    /**
     * 分类等于查询
     *
     * @param category
     * @return
     */
    FlowDefinitionQuery category(String category);

    /**
     * 系统单位ID等于查询
     *
     * @param systemUnitId
     * @return
     */
    FlowDefinitionQuery systemUnitId(String systemUnitId);

    /**
     * 系统单位ID in查询
     *
     * @param systemUnitIds
     * @return
     */
    FlowDefinitionQuery systemUnitIds(Collection<String> systemUnitIds);

    /**
     * ID like查询
     *
     * @param id
     * @return
     */
    FlowDefinitionQuery idLike(String id);

    /**
     * 名称like查询
     *
     * @param name
     * @return
     */
    FlowDefinitionQuery nameLike(String name);

    /**
     * 分类like查询
     *
     * @param category
     * @return
     */
    FlowDefinitionQuery categoryLike(String category);

    /**
     * 是否启用等于查询
     *
     * @param enabled
     * @return
     */
    FlowDefinitionQuery enabled(boolean enabled);

    /**
     * 是否移动端展示等于查询
     *
     * @param mobileShow
     * @return
     */
    FlowDefinitionQuery mobileShow(boolean mobileShow);

    /**
     * 根据用户ID及权限查询
     *
     * @param userId
     * @param permissions
     * @return
     */
    FlowDefinitionQuery permission(String userId, Collection<Permission> permissions);

    /**
     * 版本号去重
     *
     * @return
     */
    FlowDefinitionQuery distinctVersion();

    /**
     * 按编号升序
     *
     * @return
     */
    FlowDefinitionQuery orderByCodeAsc();

    /**
     * 按编号降序
     *
     * @return
     */
    FlowDefinitionQuery orderByCodeDesc();

    /**
     * 按分类升序
     *
     * @return
     */
    FlowDefinitionQuery orderByCategoryAsc();

    /**
     * 按分类降序
     *
     * @return
     */
    FlowDefinitionQuery orderByCategoryDesc();

    /**
     * 使用该方法不能再调用其他orderByXXX方法
     *
     * @param orderBy
     * @return
     */
    FlowDefinitionQuery order(String orderBy);

    /**
     * 使用该方法其他orderBy无效
     *
     * @return
     */
    FlowDefinitionQuery recentUse();

    FlowDefinitionQuery moduleId(String moduleId);

    /**
     * 归属系统
     *
     * @param system
     */
    FlowDefinitionQuery system(String system);
}
