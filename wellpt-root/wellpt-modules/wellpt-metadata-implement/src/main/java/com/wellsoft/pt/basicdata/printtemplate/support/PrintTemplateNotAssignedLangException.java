/*
 * @(#)2018年12月6日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.printtemplate.support;

import com.wellsoft.context.enums.JsonDataErrorCode;
import com.wellsoft.context.exception.JsonDataException;

/**
 * Description: 如何描述该类
 *
 * @author zhongzh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2018年12月6日.1	zhongzh		2018年12月6日		Create
 * </pre>
 * @date 2018年12月6日
 */
public class PrintTemplateNotAssignedLangException extends JsonDataException {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    private Object data;

    /**
     * 如何描述该构造方法
     *
     * @param message
     * @param data
     */
    public PrintTemplateNotAssignedLangException(String message, Object data) {
        super(message);
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.core.web.json.JsonDataException#getData()
     */
    @Override
    public Object getData() {
        return data;
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.core.web.json.JsonDataException#getErrorCode()
     */
    @Override
    public JsonDataErrorCode getErrorCode() {
        return JsonDataErrorCode.PrintReasonNotAssigned;
    }

}
