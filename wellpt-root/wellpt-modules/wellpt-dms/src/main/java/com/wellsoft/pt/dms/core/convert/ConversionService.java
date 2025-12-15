/*
 * @(#)Feb 19, 2017 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.core.convert;

import com.wellsoft.pt.dms.core.convert.converter.MapToBaseObjectConverter;
import org.springframework.format.support.DefaultFormattingConversionService;
import org.springframework.stereotype.Component;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * Feb 19, 2017.1	zhulh		Feb 19, 2017		Create
 * </pre>
 * @date Feb 19, 2017
 */
@Component
public class ConversionService extends DefaultFormattingConversionService {

    /**
     *
     */
    public ConversionService() {
        super();

        addConverter(new MapToBaseObjectConverter(this));
    }

}
