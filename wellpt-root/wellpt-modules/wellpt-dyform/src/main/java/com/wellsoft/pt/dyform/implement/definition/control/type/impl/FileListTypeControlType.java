package com.wellsoft.pt.dyform.implement.definition.control.type.impl;

import com.wellsoft.pt.dyform.implement.definition.constant.DyFormConfig;
import com.wellsoft.pt.dyform.implement.definition.constant.DyFormConfig.EnumInputModeType;
import com.wellsoft.pt.dyform.implement.definition.control.bean.FieldPropertyBean;
import com.wellsoft.pt.dyform.implement.definition.control.enums.EnumFileuploadFieldProperties;
import com.wellsoft.pt.dyform.implement.definition.control.type.AbstractControlType;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 列表式附件控件
 *
 * @author hongjz
 */

@Component
public class FileListTypeControlType extends AbstractControlType {

    @Override
    public String getInputMode() {
        return DyFormConfig.INPUTMODE_ACCESSORY3;
    }

    @Override
    public boolean isValueAsMap() {
        return false;
    }

    @Override
    public EnumInputModeType getInputModeType() {
        return EnumInputModeType.ATTACH;
    }

    @Override
    protected void customedFieldPropertys(List<FieldPropertyBean> configs) {
        //configs.add(new BooleanConfigBean("downloadable", "下载"));
        //configs.add(new BooleanConfigBean("deletable", "删除"));
        //configs.add(new BooleanConfigBean("added", "上传"));
        configs.addAll(EnumFileuploadFieldProperties.toFieldPropertyBeanList());
    }
}
