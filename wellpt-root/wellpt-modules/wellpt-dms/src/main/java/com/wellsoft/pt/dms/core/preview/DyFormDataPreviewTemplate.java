/*
 * @(#)Feb 15, 2017 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.core.preview;

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
 * Feb 15, 2017.1	zhulh		Feb 15, 2017		Create
 * </pre>
 * @date Feb 15, 2017
 */
@Component
public class DyFormDataPreviewTemplate implements DataPreviewTemplate {

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.core.preview.DataPreviewTemplate#getName()
     */
    @Override
    public String getName() {
        return "表单数据预览";
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.core.preview.DataPreviewTemplate#getType()
     */
    @Override
    public String getType() {
        return "dyFormDataPreview";
    }

    /**
     * (non-Javadoc)
     *
     * @see org.springframework.core.Ordered#getOrder()
     */
    @Override
    public int getOrder() {
        return 0;
    }

}
