/*
 * @(#)2019年12月13日 V1.0
 *
 * Copyright 2019 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.repository.websocket;

/**
 * Description:
 *
 * @author zhongzh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2019年12月13日.1	zhongzh		2019年12月13日		Create
 * </pre>
 * @date 2019年12月13日
 */
public abstract class WebSocketUtils {

    public static final int RESULT_MESSAGE_CODE_DATA = 0;

    public static final int RESULT_MESSAGE_CODE_CTRL = RESULT_MESSAGE_CODE_DATA + 100;

    public static final int RESULT_MESSAGE_CODE_OPEN = RESULT_MESSAGE_CODE_CTRL + 1;

    public static final int RESULT_MESSAGE_CODE_CLOSE = RESULT_MESSAGE_CODE_OPEN + 1;

    public static final int RESULT_MESSAGE_CODE_NOTICE = RESULT_MESSAGE_CODE_CLOSE + 1;

}
