/*
 * @(#)2015-4-7 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.exception;

import com.wellsoft.context.enums.JsonDataErrorCode;
import com.wellsoft.pt.bpm.engine.core.FlowUserSid;
import com.wellsoft.pt.bpm.engine.core.Token;
import com.wellsoft.pt.bpm.engine.element.UserUnitElement;
import com.wellsoft.pt.bpm.engine.node.Node;
import com.wellsoft.pt.bpm.engine.support.UnitUser;

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
 * 2015-4-7.1	zhulh		2015-4-7		Create
 * </pre>
 * @date 2015-4-7
 */
public class ChooseUnitUserException extends ChooseSpecificUserException {
    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = -8911683610487831298L;

    private UnitUser unitUser;

    private JsonDataErrorCode errorCode;

    /**
     * @param unitUser
     * @param taskName
     * @param taskId
     * @param userIds
     * @param submitButtonId
     * @param users
     */
    public ChooseUnitUserException(Node node, Token token, UnitUser unitUser, String taskName, String taskId, Collection<FlowUserSid> userIds,
                                   String submitButtonId, List<Map<String, String>> users, JsonDataErrorCode errorCode, List<UserUnitElement> unitElements) {
        super(node, token, taskName, taskId, userIds, submitButtonId, users, unitElements);

        this.unitUser = unitUser;
        this.errorCode = errorCode;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.exception.OnlyChooseOneUserException#getData()
     */
    @SuppressWarnings("unchecked")
    @Override
    public Object getData() {
        Map<String, Object> data = (Map<String, Object>) super.getData();
        if (data != null) {
            data.put("unitUser", unitUser);
        }
        return data;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.exception.OnlyChooseOneUserException#getErrorCode()
     */
    @Override
    public JsonDataErrorCode getErrorCode() {
        return this.errorCode;
    }
}
