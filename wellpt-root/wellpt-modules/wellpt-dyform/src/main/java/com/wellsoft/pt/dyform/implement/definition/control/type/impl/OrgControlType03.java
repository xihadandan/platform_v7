package com.wellsoft.pt.dyform.implement.definition.control.type.impl;

import com.wellsoft.pt.dyform.implement.definition.constant.DyFormConfig;
import org.springframework.stereotype.Component;

/**
 * 新的组织选择框控件类型
 *
 * @author hongjz
 */
@Component
public class OrgControlType03 extends OrgControlType {

    @Override
    public String getInputMode() {
        return DyFormConfig.INPUTMODE_ORGSELECTDEPARTMENT;
    }

}
