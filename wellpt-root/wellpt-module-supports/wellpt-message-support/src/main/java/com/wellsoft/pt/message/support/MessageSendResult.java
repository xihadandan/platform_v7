/*
 * @(#)8/22/25 V1.0
 *
 * Copyright 2025 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.message.support;

/**
 * Description: 0：成功，1：失败，2：发送中，3：取消，4：超时，5：失败重试，6：部分成功，7：未知
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 8/22/25.1	    zhulh		8/22/25		    Create
 * </pre>
 * @date 8/22/25
 */
public interface MessageSendResult {
    public static final int SUCCESS = 0;
    public static final int FAILED = 1;
    public static final int SENDING = 2;
    public static final int CANCELED = 3;
    public static final int TIMEOUT = 4;
    public static final int RETRY_FAILED = 5;
    public static final int SUCCESS_PART = 6;
    public static final int UNKNOWN = 7;
}
