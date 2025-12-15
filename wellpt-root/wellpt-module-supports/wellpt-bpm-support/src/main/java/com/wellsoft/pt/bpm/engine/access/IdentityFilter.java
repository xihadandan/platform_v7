/*
 * @(#)2014-5-27 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.access;

import com.wellsoft.pt.bpm.engine.core.FlowUserSid;
import com.wellsoft.pt.bpm.engine.core.Token;
import com.wellsoft.pt.bpm.engine.element.UserUnitElement;
import com.wellsoft.pt.bpm.engine.node.Node;

import java.util.List;
import java.util.Set;

/**
 * Description: 人员过滤接口
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2014-5-27.1	zhulh		2014-5-27		Create
 * </pre>
 * @date 2014-5-27
 */
public interface IdentityFilter {
    Set<FlowUserSid> doFilter(List<FlowUserSid> userIds, Node node, Token token, List<String> raws);

    Set<FlowUserSid> doFilter(List<FlowUserSid> userIds, Node node, Token token, UserUnitElement userUnitElement);
}
