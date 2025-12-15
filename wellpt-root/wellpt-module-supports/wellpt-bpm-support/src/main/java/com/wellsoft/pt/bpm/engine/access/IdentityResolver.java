/*
 * @(#)2012-11-21 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.access;

import com.wellsoft.pt.bpm.engine.core.FlowUserSid;
import com.wellsoft.pt.bpm.engine.core.Token;
import com.wellsoft.pt.bpm.engine.element.UserUnitElement;
import com.wellsoft.pt.bpm.engine.enums.ParticipantType;
import com.wellsoft.pt.bpm.engine.node.Node;

import java.util.List;

/**
 * Description: 默认的工作流参与者解析接口
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2012-11-21.1	zhulh		2012-11-21		Create
 * </pre>
 * @date 2012-11-21
 */
public interface IdentityResolver {

    /**
     * 解析出指定节点的用户标识
     *
     * @param node
     * @param token
     * @param raws
     * @param participantType
     * @param sidGranularity
     * @return
     */
    List<FlowUserSid> resolve(Node node, Token token, List<String> raws, ParticipantType participantType,
                              String sidGranularity);

    /**
     * 解析出指定节点的用户标识
     *
     * @param node
     * @param token
     * @param userUnitElement
     * @param participantType
     * @param sidGranularity
     * @return
     */
    List<FlowUserSid> resolve(Node node, Token token, UserUnitElement userUnitElement, ParticipantType participantType,
                              String sidGranularity);

}
