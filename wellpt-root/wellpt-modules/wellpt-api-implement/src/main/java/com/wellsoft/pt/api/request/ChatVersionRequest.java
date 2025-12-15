package com.wellsoft.pt.api.request;

import com.wellsoft.pt.api.WellptRequest;
import com.wellsoft.pt.api.internal.suport.ApiServiceName;
import com.wellsoft.pt.api.response.ChatVersionResponse;

/**
 * Description: 如何描述该类
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
public class ChatVersionRequest extends WellptRequest<ChatVersionResponse> {
    // 手机应用类型
    private String type;

    @Override
    public String getApiServiceName() {
        return ApiServiceName.CHATVERSION;
    }

    @Override
    public Class<ChatVersionResponse> getResponseClass() {
        return ChatVersionResponse.class;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
