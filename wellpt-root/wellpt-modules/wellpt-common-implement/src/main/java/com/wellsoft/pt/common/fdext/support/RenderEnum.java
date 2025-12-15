/*
 * @(#)2016年3月17日 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.common.fdext.support;

import com.wellsoft.pt.common.fdext.entity.CdFieldExtDefinition;
import com.wellsoft.pt.common.fdext.support.render.*;

import java.lang.reflect.Constructor;

/**
 * Description: 如何描述该类
 *
 * @author zhongzh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016年3月17日.1	zhongzh		2016年3月17日		Create
 * </pre>
 * @date 2016年3月17日
 */
public enum RenderEnum {
    label("标签", LabelRender.class), text("文本", TextRender.class), textarea("多行文本", TextareaRender.class), radio("单选框",
            RadioRender.class), checkbox("单选框", CheckboxRender.class), select("单选框", SelectRender.class), date("日期",
            DateRender.class), button("按钮", ButtonRender.class);

    private String name;
    // private Class<? extends IDyFieldRender> render;
    private Constructor<? extends ICdFieldRender> renderConstructor;
    // 构造方法
    private RenderEnum(String name, Class<? extends ICdFieldRender> clazz) {
        this.name = name;
        // this.render = value;
        try {
            this.renderConstructor = clazz.getConstructor(ICdFieldDefinition.class);
        } catch (Exception cause) {
            throw new RuntimeException("Render[" + this.name + "] error Init", cause);
        }
    }

    public String getName() {
        return name;
    }

    public ICdFieldRender getReader(CdFieldExtDefinition define) {
        if (define == null) {
            throw new IllegalArgumentException("CdFieldExtDefinition[define] is require");
        }
        ICdFieldRender irender;
        try {
            irender = renderConstructor.newInstance(define);
            // irender.setTypeName(name);
        } catch (Exception cause) {
            String inputType = define.getInputType(), fieldName = define.getFieldName();
            String message = "fieldName[" + fieldName + "]inputType[" + inputType + "] error createRender";
            throw new RuntimeException(message, cause);
        }
        return irender;
    }
}
