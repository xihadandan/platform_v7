/*
 * @(#)2015年9月24日 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.security.audit.support;

import com.wellsoft.context.component.tree.TreeNode;

import java.util.*;

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
public abstract class AbstractResourceDataSource implements ResourceDataSource {

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.audit.support.ResourceDataSource#getName()
     */
    @Override
    public String getName() {
        return this.getClass().getCanonicalName();
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.audit.support.ResourceDataSource#getId()
     */
    @Override
    public String getId() {
        return this.getClass().getSimpleName();
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.audit.support.ResourceDataSource#getOrder()
     */
    @Override
    public int getOrder() {
        return 0;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.audit.support.ResourceDataSource#getDataByAppSystemUuid(java.lang.String)
     */
    @Override
    public List<TreeNode> getDataByAppSystemUuid(String appSystemUuid) {
        return Collections.emptyList();
    }

    /**
     * 获取受保护的对象集合
     *
     * @param objectIdIdentity
     * @return
     */
    public Map<String, Collection<Object>> getObjectIdentities(String objectIdIdentity) {
        Map<String, Collection<Object>> objectIdentityMap = new HashMap<String, Collection<Object>>();
        List<Object> objectIdentities = new ArrayList<Object>();
        objectIdentities.add(objectIdIdentity);
        objectIdentityMap.put(getId(), objectIdentities);
        return objectIdentityMap;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.audit.support.ResourceDataSource#getAnonymousResources()
     */
    @Override
    public List<String> getAnonymousResources() {
        return Collections.emptyList();
    }

}
