package com.wellsoft.pt.dyform.implement.definition.control.type.impl;

import com.wellsoft.pt.dyform.implement.definition.constant.DyFormConfig;
import com.wellsoft.pt.dyform.implement.definition.constant.DyFormConfig.EnumInputModeType;
import com.wellsoft.pt.dyform.implement.definition.control.type.AbstractControlType;
import org.springframework.stereotype.Component;

/**
 * 文本控件
 *
 * @author hongjz
 */

@Component
public class NumberControlType extends AbstractControlType {

    @Override
    public String getInputMode() {
        return DyFormConfig.INPUTMODE_NUMBER;
    }

    @Override
    public boolean isValueAsMap() {
        return false;
    }

    @Override
    public EnumInputModeType getInputModeType() {
        return EnumInputModeType.NUMBER;
    }

}
