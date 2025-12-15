/*
 * @(#)3/21/25 V1.0
 *
 * Copyright 2025 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.feishu.support.response;

import com.wellsoft.pt.app.feishu.support.FeishuResponse;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * Description:
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 3/21/25.1	    zhulh		3/21/25		    Create
 * </pre>
 * @date 3/21/25
 */
public class MessageResponse extends FeishuResponse {
    @JsonProperty("message_id")
    private String messageId;

    @JsonProperty("chat_id")
    private String chatId;

    private boolean deleted;
    private boolean updated;

    /**
     * @return the messageId
     */
    public String getMessageId() {
        return messageId;
    }

    /**
     * @param messageId 要设置的messageId
     */
    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    /**
     * @return the chatId
     */
    public String getChatId() {
        return chatId;
    }

    /**
     * @param chatId 要设置的chatId
     */
    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    /**
     * @return the deleted
     */
    public boolean isDeleted() {
        return deleted;
    }

    /**
     * @param deleted 要设置的deleted
     */
    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    /**
     * @return the updated
     */
    public boolean isUpdated() {
        return updated;
    }

    /**
     * @param updated 要设置的updated
     */
    public void setUpdated(boolean updated) {
        this.updated = updated;
    }
}
