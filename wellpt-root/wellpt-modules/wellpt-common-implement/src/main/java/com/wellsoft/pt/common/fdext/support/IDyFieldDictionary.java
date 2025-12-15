/*
 * @(#)2016-03-11 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.common.fdext.support;

import com.wellsoft.context.dto.DataItem;

import java.util.Collection;

/**
 * Description: 如何描述该类
 *
 * @author zhongzh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016-03-11.1	zhongzh		2016-03-11		Create
 * </pre>
 * @date 2016-03-11
 */
public interface IDyFieldDictionary {

    public final static String DEATULT_VALUE_CHECKED = "checked=\"checked\"";

    public final static String DEATULT_VALUE_SELECTED = "selected=\"selected\"";

    public Collection<DataItem> getDataItems();

}
