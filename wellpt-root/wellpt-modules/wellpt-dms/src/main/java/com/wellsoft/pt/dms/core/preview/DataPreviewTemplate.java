/*
 * @(#)Feb 15, 2017 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.core.preview;

import org.springframework.core.Ordered;

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
public interface DataPreviewTemplate extends Ordered {

    /**
     * 数据预览名称
     *
     * @return
     */
    public String getName();

    /**
     * 数据预览类型
     *
     * @return
     */
    public String getType();

}
