/*
 * @(#)2018年9月11日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.access;

import com.wellsoft.pt.bpm.engine.core.FlowUserSid;
import com.wellsoft.pt.bpm.engine.core.Token;
import com.wellsoft.pt.bpm.engine.node.Node;

import java.util.Collection;
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
 * 2018年9月11日.1	zhulh		2018年9月11日		Create
 * </pre>
 * @date 2018年9月11日
 */
public interface SidGranularityResolver {

    /**
     * 解析组织ID
     *
     * @param node
     * @param token
     * @param rawUsers
     * @return
     */
    List<FlowUserSid> resolve(Node node, Token token, Collection<String> rawUsers);

    /**
     * 解析组织ID
     *
     * @param node
     * @param token
     * @param rawUsers
     * @param granularity
     * @return
     */
    List<FlowUserSid> resolve(Node node, Token token, Collection<String> rawUsers, String granularity);

    /**
     * 解析组织ID
     *
     * @param node
     * @param token
     * @param rawUsers
     * @param granularity
     * @return
     */
    List<FlowUserSid> resolve(Node node, Token token, Collection<String> rawUsers, String granularity, String orgId);

    /**
     * 解析组织ID
     *
     * @param node
     * @param token
     * @param ids
     * @return
     */
    List<FlowUserSid> resolveWithSid(Node node, Token token, Collection<FlowUserSid> rawSids);

    /**
     * 解析组织ID
     *
     * @param node
     * @param token
     * @param ids
     * @return
     */
    List<FlowUserSid> resolveWithSid(Node node, Token token, Collection<FlowUserSid> rawSids, String granularity);

    /**
     * 解析成用户ID
     *
     * @param sids
     * @return
     */
    Collection<String> resolveAsUserIds(Collection<FlowUserSid> sids);

}
