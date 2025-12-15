/*
 * @(#)3/25/25 V1.0
 *
 * Copyright 2025 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.feishu.support.response;

import com.wellsoft.pt.app.feishu.model.BotInfo;
import com.wellsoft.pt.app.feishu.support.FeishuResponse;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

/**
 * Description:
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 3/25/25.1	    zhulh		3/25/25		    Create
 * </pre>
 * @date 3/25/25
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class BotInfoResponse extends FeishuResponse {
    private BotInfo bot;

    /**
     * @return the bot
     */
    public BotInfo getBot() {
        return bot;
    }

    /**
     * @param bot 要设置的bot
     */
    public void setBot(BotInfo bot) {
        this.bot = bot;
    }
}
