/*
 * @(#)2016年8月13日 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.context;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016年8月13日.1	zhulh		2016年8月13日		Create
 * </pre>
 * @date 2016年8月13日
 */
public class AppContextHolderStrategy {
    private static final ThreadLocal<AppContext> contextHolder = new ThreadLocal<AppContext>();

    public void clearContext() {
        contextHolder.remove();
    }

    public AppContext getContext() {
        AppContext ctx = contextHolder.get();

        if (ctx == null) {
            ctx = createEmptyContext();
            contextHolder.set(ctx);
        }

        return ctx;
    }

    public void setContext(AppContext context) {
        contextHolder.set(context);
    }

    public AppContext createEmptyContext() {
        return new AppContextCurrentUserProxyImpl();
    }
}
