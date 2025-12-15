/*
 * @(#)2013-4-11 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.exception;

import com.wellsoft.context.jdbc.support.QueryItem;

import java.util.Collection;

/**
 * Description: 没有指定打印模板异常类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-4-11.1	zhulh		2013-4-11		Create
 * </pre>
 * @date 2013-4-11
 */
public class PrintTemplateNotAssignedException extends WorkFlowException {
    private static final long serialVersionUID = 5590955869389056226L;

    private Collection<QueryItem> items;

    public PrintTemplateNotAssignedException(Collection<QueryItem> items) {
        this.items = items;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.exception.WorkFlowException#getData()
     */
    @Override
    public Object getData() {
        return this.items;
    }

}
