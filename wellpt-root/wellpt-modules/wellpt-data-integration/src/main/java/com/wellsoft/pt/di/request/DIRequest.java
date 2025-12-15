package com.wellsoft.pt.di.request;

import com.wellsoft.pt.integration.request.Request;

import java.io.Serializable;

/**
 * Description: 数据交换请求体
 *
 * @author chenq
 * @date 2019/8/13
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2019/8/13    chenq		2019/8/13		Create
 * </pre>
 */
public class DIRequest<BODY extends Serializable> extends Request {

    private static final long serialVersionUID = 9062623448961549933L;
    /**
     * 头部信息
     */
    protected RequestHeader header = new RequestHeader();
    /**
     * 消息体
     */
    protected BODY body;

    public DIRequest() {
    }

    public DIRequest(BODY body) {
        this.body = body;
    }

    public DIRequest(RequestHeader header, BODY body) {
        this.header = header;
        this.body = body;
    }

    public RequestHeader getHeader() {
        return header;
    }

    public void setHeader(RequestHeader header) {
        this.header = header;
    }

    public BODY getBody() {
        return body;
    }

    public void setBody(BODY body) {
        this.body = body;
    }
}
