/*
 * @(#)Dec 6, 2017 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.ui.client.dispatcher;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * Dec 6, 2017.1	zhulh		Dec 6, 2017		Create
 * </pre>
 * @date Dec 6, 2017
 */
public interface AppFunctionDispatcherHandlerScriptMapping {

    /**
     * 返回处理的功能类型
     *
     * @return
     */
    String getAppFunctionType();

    /**
     * 处理处理的脚本函数
     *
     * @return
     */
    String getHandlerFunction();

}
