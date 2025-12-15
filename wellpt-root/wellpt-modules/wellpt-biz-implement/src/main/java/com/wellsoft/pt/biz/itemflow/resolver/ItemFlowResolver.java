/*
 * @(#)12/12/23 V1.0
 *
 * Copyright 2023 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.biz.itemflow.resolver;

import com.wellsoft.pt.biz.listener.event.ProcessItemEvent;
import com.wellsoft.pt.biz.support.ItemFlowDefinition;

import java.util.List;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 12/12/23.1	zhulh		12/12/23		Create
 * </pre>
 * @date 12/12/23
 */
public interface ItemFlowResolver {

    /**
     * 解析下一事项
     *
     * @param event
     * @param itemFlowDefinition
     */
    List<ItemFlowDefinition.NextItemInfo> resolveNextItem(ProcessItemEvent event, ItemFlowDefinition itemFlowDefinition);

}
