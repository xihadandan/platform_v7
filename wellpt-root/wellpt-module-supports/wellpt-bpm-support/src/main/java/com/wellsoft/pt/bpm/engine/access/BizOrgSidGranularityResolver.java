/*
 * @(#)12/13/24 V1.0
 *
 * Copyright 2024 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.access;

import com.wellsoft.pt.bpm.engine.core.FlowUserSid;
import com.wellsoft.pt.bpm.engine.core.Token;
import com.wellsoft.pt.bpm.engine.node.Node;

import java.util.List;

/**
 * Description:
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 12/13/24.1	    zhulh		12/13/24		    Create
 * </pre>
 * @date 12/13/24
 */
public interface BizOrgSidGranularityResolver {
    /**
     * @param node
     * @param token
     * @param rawUsers
     * @param granularity
     * @param bizOrgId
     * @return
     */
    List<FlowUserSid> resolve(Node node, Token token, List<String> rawUsers, String granularity, String bizOrgId);
}
