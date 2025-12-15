/*
 * @(#)Mar 2, 2017 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.core.context;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * Mar 2, 2017.1	zhulh		Mar 2, 2017		Create
 * </pre>
 * @date Mar 2, 2017
 */
public class ActionContextHolder {

    private static final ThreadLocal<ActionContext> contextHolder = new ThreadLocal<ActionContext>();

    public static void clearContext() {
        contextHolder.remove();
    }

    public static ActionContext getContext() {
        return contextHolder.get();
    }

    public static void setContext(ActionContext context) {
        contextHolder.set(context);
    }

}
