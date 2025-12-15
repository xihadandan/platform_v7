/*
 * @(#)8/22/24 V1.0
 *
 * Copyright 2024 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bot.service;

import com.wellsoft.pt.bot.support.BotResult;
import com.wellsoft.pt.bot.support.DyFormBoter;

/**
 * Description: 表单从表数据单据转换复制、同步接口
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 8/22/24.1	    zhulh		8/22/24		    Create
 * </pre>
 * @date 8/22/24
 */
public interface BotDyformDataAfterBotService {

    /**
     * @param subformId
     * @param dyFormBoter
     * @param result
     */
    void copySubformData(String subformId, DyFormBoter dyFormBoter, BotResult result);

    /**
     * @param subformId
     * @param rowKeyField
     * @param dyFormBoter
     * @param result
     */
    void syncSubformData(String subformId, String rowKeyField, DyFormBoter dyFormBoter, BotResult result);

}
