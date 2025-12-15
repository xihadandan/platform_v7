/*
 * @(#)2021年1月30日 V1.0
 *
 * Copyright 2021 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dyform.implement.definition.event;

import com.wellsoft.context.event.WellptEvent;
import com.wellsoft.pt.dyform.implement.definition.util.dyform.FormDefinitionHandler;

import java.util.List;

/**
 * Description: 如何描述该类
 *
 * @author zhongzh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2021年1月30日.1	zhongzh		2021年1月30日		Create
 * </pre>
 * @date 2021年1月30日
 */
public class FormFieldsRemovedEvent extends WellptEvent {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    private FormDefinitionHandler handler;

    private List<String> removedFieldNames;

    /**
     * @param source
     * @param handler
     * @param removedFieldNames
     */
    public FormFieldsRemovedEvent(Object source, FormDefinitionHandler handler, List<String> removedFieldNames) {
        super(source);
        this.handler = handler;
        this.removedFieldNames = removedFieldNames;
    }

    public FormDefinitionHandler getHandler() {
        return handler;
    }

    public List<String> getRemovedFieldNames() {
        return removedFieldNames;
    }

}
