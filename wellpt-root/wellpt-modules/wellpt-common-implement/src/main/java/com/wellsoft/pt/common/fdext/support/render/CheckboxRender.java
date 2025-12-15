/*
 * @(#)2016年3月16日 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.common.fdext.support.render;

import com.wellsoft.pt.common.fdext.support.CdFieldDictionaryRender;
import com.wellsoft.pt.common.fdext.support.ICdFieldDefinition;
import com.wellsoft.pt.common.fdext.support.RenderEnum;

/**
 * Description: 如何描述该类
 *
 * @author zhongzh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016年3月16日.1	zhongzh		2016年3月16日		Create
 * </pre>
 * @date 2016年3月16日
 */
public class CheckboxRender extends CdFieldDictionaryRender {

    /**
     * 如何描述该构造方法
     *
     * @param define
     */
    public CheckboxRender(ICdFieldDefinition define) {
        super(define);
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.common.fdext.support.AbstractCdFieldRender#getTypeName()
     */
    @Override
    public String getTypeName() {
        return RenderEnum.checkbox.getName();
    }

}
