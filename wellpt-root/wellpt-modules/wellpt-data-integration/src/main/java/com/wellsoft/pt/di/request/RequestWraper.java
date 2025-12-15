package com.wellsoft.pt.di.request;

import com.wellsoft.pt.integration.request.Request;

/**
 * Description:
 *
 * @author chenq
 * @date 2019/8/14
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2019/8/14    chenq		2019/8/14		Create
 * </pre>
 */
public class RequestWraper<R extends Request> {

    private String id;//数据交换ID

    private R request;//数据交换请求

    public RequestWraper() {
    }

    public RequestWraper(String id, R request) {
        this.id = id;
        this.request = request;
    }

    public R getRequest() {
        return request;
    }

    public void setRequest(R request) {
        this.request = request;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}
