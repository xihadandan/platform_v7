/*
 * @(#)2014-5-27 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.exception;

import com.wellsoft.context.enums.JsonDataErrorCode;
import com.wellsoft.pt.bpm.engine.core.FlowUserSid;
import com.wellsoft.pt.bpm.engine.core.Token;
import com.wellsoft.pt.bpm.engine.element.UserUnitElement;
import com.wellsoft.pt.bpm.engine.node.Node;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Description: 选择具体办理人
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
public class ChooseSpecificUserException extends OnlyChooseOneUserException {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = -1582724212187384404L;

    /**
     * @param taskId
     * @param userIds
     * @param submitButtonId
     */
    public ChooseSpecificUserException(Node node, Token token, String taskName, String taskId,
                                       Collection<FlowUserSid> userIds, String submitButtonId,
                                       List<Map<String, String>> users, List<UserUnitElement> unitElements) {
        super(node, token, taskName, taskId, userIds, submitButtonId, users, unitElements);
    }

    ///**
    // * @param taskId
    // * @param userIds
    // * @param submitButtonId
    // */
    //public ChooseSpecificUserException(Node node, Token token, String taskName, String taskId,
    //                                   List<String> userIds, String submitButtonId,
    //                                   List<Map<String, String>> users, List<UnitElement> unitElements) {
    //	super(node,token,taskName, taskId, userIds, submitButtonId, users,unitElements);
    //}


    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.exception.OnlyChooseOneUserException#getErrorCode()
     */
    @Override
    public JsonDataErrorCode getErrorCode() {
        return JsonDataErrorCode.ChooseSpecificUser;
    }

}
