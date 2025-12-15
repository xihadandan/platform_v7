/*
 * @(#)2014-10-22 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.message.provider;


/**
 * Description: 如何描述该类
 *
 * @author tony
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2014-10-22.1	tony		2014-10-22		Create
 * </pre>
 * @date 2014-10-22
 */
public interface MessageEventSourceProvider {


    /**
     * 获取模块ID
     *
     * @return
     */
    String getMessageType();

    /**
     * 获取模块ID
     *
     * @return
     */
    String getModuleId();

    /**
     * 获取模块的名字
     *
     * @return
     */
    String getModuleName();


}
