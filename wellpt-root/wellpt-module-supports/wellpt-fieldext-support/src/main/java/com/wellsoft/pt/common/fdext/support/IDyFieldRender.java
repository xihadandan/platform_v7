/*
 * @(#)2016年3月21日 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.common.fdext.support;

import java.util.List;

/**
 * Description: 如何描述该类
 *
 * @author zhongzh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016年3月21日.1	zhongzh		2016年3月21日		Create
 * </pre>
 * @date 2016年3月21日
 */
public interface IDyFieldRender {

    public List<ICdFieldRender> getRenders();

    /**
     * 是否在后台（freemarker）渲染数据
     *
     * @return
     */
    public boolean isRenderData();

    /**
     * 获取渲染的数据
     *
     * @return
     */
    public <T> T getData();

    /**
     * 在data中获取字段的值
     *
     * @param fieldName
     * @param defaultValue
     * @return
     */
    public Object getValue(String fieldName, String defaultValue);
}
