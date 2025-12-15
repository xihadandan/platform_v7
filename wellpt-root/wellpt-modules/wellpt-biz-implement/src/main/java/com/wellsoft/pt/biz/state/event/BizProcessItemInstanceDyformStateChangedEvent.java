/*
 * @(#)12/7/23 V1.0
 *
 * Copyright 2023 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.biz.state.event;

import com.wellsoft.pt.dyform.facade.dto.DyFormData;
import org.springframework.context.ApplicationEvent;

/**
 * Description: 业务事项办件表单数据变更事件
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 12/7/23.1	zhulh		12/7/23		Create
 * </pre>
 * @date 12/7/23
 */
public class BizProcessItemInstanceDyformStateChangedEvent extends ApplicationEvent {
    private static final long serialVersionUID = 73564031353952800L;

    // 业务流程实例表单数据
    private DyFormData processItemInstanceDyformData;
    // 状态名称字段
    private String stateNameField;
    // 状态值
    private String stateNameValue;
    // 状态代码字段
    private String stateCodeField;
    // 代码值
    private String stateCodeValue;

    /**
     * Create a new ApplicationEvent.
     *
     * @param source the object on which the event initially occurred (never {@code null})
     */
    public BizProcessItemInstanceDyformStateChangedEvent(Object source, DyFormData processItemInstanceDyformData,
                                                         String stateNameField, String stateNameValue,
                                                         String stateCodeField, String stateCodeValue) {
        super(source);
        this.processItemInstanceDyformData = processItemInstanceDyformData;
        this.stateNameField = stateNameField;
        this.stateNameValue = stateNameValue;
        this.stateCodeField = stateCodeField;
        this.stateCodeValue = stateCodeValue;
    }

    /**
     * @return the processItemInstanceDyformData
     */
    public DyFormData getProcessItemInstanceDyformData() {
        return processItemInstanceDyformData;
    }

    /**
     * @return the stateNameField
     */
    public String getStateNameField() {
        return stateNameField;
    }

    /**
     * @return the stateNameValue
     */
    public String getStateNameValue() {
        return stateNameValue;
    }

    /**
     * @return the stateCodeField
     */
    public String getStateCodeField() {
        return stateCodeField;
    }

    /**
     * @return the stateCodeValue
     */
    public String getStateCodeValue() {
        return stateCodeValue;
    }
}
