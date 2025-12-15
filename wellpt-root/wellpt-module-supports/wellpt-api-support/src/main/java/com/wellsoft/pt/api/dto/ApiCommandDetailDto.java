package com.wellsoft.pt.api.dto;

import java.io.Serializable;

/**
 * Description:
 *
 * @author chenq
 * @date 2018/8/13
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2018/8/13    chenq		2018/8/13		Create
 * </pre>
 */
public class ApiCommandDetailDto implements Serializable {

    private static final long serialVersionUID = -8861153341998937415L;
    private String requestBody;

    private String responseBody;

    private String commandUuid;

    public String getRequestBody() {
        return requestBody;
    }

    public void setRequestBody(String requestBody) {
        this.requestBody = requestBody;
    }

    public String getResponseBody() {
        return responseBody;
    }

    public void setResponseBody(String responseBody) {
        this.responseBody = responseBody;
    }

    public String getCommandUuid() {
        return commandUuid;
    }

    public void setCommandUuid(String commandUuid) {
        this.commandUuid = commandUuid;
    }
}
