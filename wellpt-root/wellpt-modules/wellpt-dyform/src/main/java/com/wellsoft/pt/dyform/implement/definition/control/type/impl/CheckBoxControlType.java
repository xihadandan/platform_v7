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
public class CheckBoxControlType extends AbstractControlType {

    @Override
    public String getInputMode() {
        return DyFormConfig.INPUTMODE_CHECKBOX;
    }

    @Override
    public boolean isValueAsMap() {
        return true;
    }

    @Override
    public EnumInputModeType getInputModeType() {
        return EnumInputModeType.TEXT;
    }

}
