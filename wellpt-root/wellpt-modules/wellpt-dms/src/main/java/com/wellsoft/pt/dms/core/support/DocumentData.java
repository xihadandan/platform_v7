/*
 * @(#)May 23, 2017 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.core.support;

import com.wellsoft.pt.dms.core.web.ActionProxy;

import java.util.List;
import java.util.Map;

/**
 * Description: 文档数据
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * May 23, 2017.1	zhulh		May 23, 2017		Create
 * </pre>
 * @date May 23, 2017
 */
public interface DocumentData {
    List<ActionProxy> getActions();

    void setActions(List<ActionProxy> actions);

    // 获取额外信息
    Map<String, Object> getExtras();

    // 设置额外信息
    void setExtras(Map<String, Object> extras);

    Object getData();
}
