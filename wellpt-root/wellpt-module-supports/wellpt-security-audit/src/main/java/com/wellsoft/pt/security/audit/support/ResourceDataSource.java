/*
 * @(#)2015年9月24日 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.security.audit.support;

import com.wellsoft.context.component.tree.TreeNode;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2015年9月24日.1	zhulh		2015年9月24日		Create
 * </pre>
 * @date 2015年9月24日
 */
public interface ResourceDataSource {

    /**
     * @return
     */
    String getName();

    /**
     * @return
     */
    String getId();

    /**
     * 如何描述该方法
     *
     * @return
     */
    int getOrder();

    /**
     * @return
     */
    List<TreeNode> getData(Map<String, Object> params);

    /**
     * 根据系统UUID获取系统资源树
     *
     * @return
     */
    List<TreeNode> getDataByAppSystemUuid(String appSystemUuid);

    /**
     * 获取受保护的对象集合, 类型->对象集合
     *
     * @param objectIdIdentity
     * @return
     */
    Map<String, Collection<Object>> getObjectIdentities(String objectIdIdentity);

    /**
     * 获取不受权限管控的资源集合
     *
     * @return
     */
    List<String> getAnonymousResources();

}
