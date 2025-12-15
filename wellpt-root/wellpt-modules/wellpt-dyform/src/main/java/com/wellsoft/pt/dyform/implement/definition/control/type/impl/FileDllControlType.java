package com.wellsoft.pt.dyform.implement.definition.control.type.impl;

import com.wellsoft.pt.dyform.implement.definition.constant.DyFormConfig;
import org.springframework.stereotype.Component;

/**
 * dll附件控件
 *
 * @author hongjz
 */

@Component
public class FileDllControlType extends FileListTypeControlType {

    @Override
    public String getInputMode() {
        return DyFormConfig.INPUTMODE_ACCESSORY1;
    }

}
