/*
 * @(#)2015-5-28 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.notice.support;

import com.wellsoft.context.config.Config;

/**
 * Description: 如何描述该类
 *
 * @author Administrator
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2015-5-28.1	Administrator		2015-5-28		Create
 * </pre>
 * @date 2015-5-28
 */
public class MtNoticeConfig {
    private static final String GLOBAL_NOTICE_ENABLE = "true";
    private static final String KEY_GLOBAL_NOTICE_ENABLE = "global.notice.enable";

    /**
     * @return
     */
    public static boolean isEnable() {
        return GLOBAL_NOTICE_ENABLE.equalsIgnoreCase(Config.getValue(KEY_GLOBAL_NOTICE_ENABLE));
    }
}
