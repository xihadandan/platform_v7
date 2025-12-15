package com.wellsoft.pt.dyform.implement.definition.control.type.impl;

import com.wellsoft.pt.dyform.implement.definition.constant.DyFormConfig;
import com.wellsoft.pt.dyform.implement.definition.constant.DyFormConfig.EnumInputModeType;
import com.wellsoft.pt.dyform.implement.definition.control.bean.FieldPropertyBean;
import com.wellsoft.pt.dyform.implement.definition.control.type.AbstractControlType;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 新的组织选择框控件类型
 *
 * @author hongjz
 */
@Component
public class OrgControlType extends AbstractControlType {

    @Override
    public String getInputMode() {
        return DyFormConfig.INPUTMODE_ORGSELECT2;
    }

    @Override
    public boolean isValueAsMap() {
        return true;
    }

    @Override
    public EnumInputModeType getInputModeType() {
        return EnumInputModeType.TEXT;
    }

    @Override
    protected void customedFieldPropertys(List<FieldPropertyBean> configs) {
        configs.add(new FieldPropertyBean("multiple", "多选"));
    }
}
