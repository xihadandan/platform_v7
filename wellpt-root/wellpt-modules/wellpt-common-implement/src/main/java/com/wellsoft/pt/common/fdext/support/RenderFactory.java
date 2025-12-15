/*
 * @(#)2016年3月17日 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.common.fdext.support;

import com.wellsoft.pt.common.fdext.entity.CdFieldExtDefinition;
import org.apache.commons.lang3.StringUtils;

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
public class RenderFactory {

    private RenderFactory() {

    }

    public static ICdFieldRender createRender(CdFieldExtDefinition define) {
        if (define == null || StringUtils.isBlank(define.getInputType())) {
            throw new IllegalArgumentException("CdFieldExtDefinition[define] is require");
        }
        return RenderEnum.valueOf(define.getInputType()).getReader(define);
    }
}
