/*
 * @(#)2016年3月30日 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.server;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016年3月30日.1	zhulh		2016年3月30日		Create
 * </pre>
 * @date 2016年3月30日
 */
public abstract class AbstractModule implements Module {

    private ComponentContainer container = new ComponentContainer();

    public final void configure(ComponentContainer container) {
        this.container = container;
        configure();
    }

    protected abstract void configure();

    protected final void add(Object... objects) {
        for (Object object : objects) {
            if (object != null) {
                container.add(object);
            }
        }
    }

}
