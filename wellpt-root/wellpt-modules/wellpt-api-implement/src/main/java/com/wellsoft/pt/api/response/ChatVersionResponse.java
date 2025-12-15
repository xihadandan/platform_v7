package com.wellsoft.pt.api.response;

import com.wellsoft.pt.api.WellptResponse;

/**
 * 返回最新的应用信息
 *
 * @author lumw
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2015-3-12.1	lumw		2015-3-12		Create
 * </pre>
 * @date 2015-3-12
 */
public class ChatVersionResponse extends WellptResponse {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 1353221782517127236L;

    private Object chatVersion;

    public static long getSerialversionuid() {
        return serialVersionUID;
    }

    public Object getChatVersion() {
        return chatVersion;
    }

    public void setChatVersion(Object chatVersion) {
        this.chatVersion = chatVersion;
    }
}
