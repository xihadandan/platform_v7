/*
 * @(#)2016年6月21日 V1.0
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
 * 2016年6月21日.1	zhulh		2016年6月21日		Create
 * </pre>
 * @date 2016年6月21日
 */
public abstract class AbstractAppContext implements AppContext {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = -8230594133512905335L;

    @Override
    public void debug() {
        AppContextHolder.getContext().debug();
    }

    @Override
    public boolean isDebug() {
        return AppContextHolder.getContext().isDebug();
    }
}
