/*
 * @(#)2013-12-25 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.common.web.json;

import com.wellsoft.context.enums.JsonDataErrorCode;
import com.wellsoft.context.exception.JsonDataException;
import com.wellsoft.pt.security.access.CustomLogoutFilter;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-12-25.1	zhulh		2013-12-25		Create
 * </pre>
 * @date 2013-12-25
 */
public class SessionExpiredException extends JsonDataException {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 3234875516131756459L;

    /**
     * @param string
     */
    public SessionExpiredException(String message) {
        super(message);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.core.web.json.JsonDataException#getData()
     */
    @Override
    public Object getData() {
        return CustomLogoutFilter.DEFAULT_LOGOUT_URL;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.core.web.json.JsonDataException#getErrorCode()
     */
    @Override
    public JsonDataErrorCode getErrorCode() {
        return JsonDataErrorCode.SessionExpired;
    }

}
