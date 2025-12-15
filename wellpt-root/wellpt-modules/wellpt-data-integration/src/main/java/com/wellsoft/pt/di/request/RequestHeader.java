package com.wellsoft.pt.di.request;

import java.util.HashMap;

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
public class RequestHeader extends HashMap<String, Object> {

    public final static String REQUEST_ID = "requestId";

    public final static String FROM = "from";

    public final static String TO = "to";

    public final static String CC = "cc";

    public final static String BCC = "bcc";


    public RequestHeader requestId(String requestId) {
        this.put(REQUEST_ID, requestId);
        return this;
    }


    public RequestHeader type(String typeId) {
        this.put("typeId", typeId);
        return this;
    }

    public RequestHeader batchId(String batchId) {
        this.put("batchId", batchId);
        return this;
    }


    public RequestHeader bcc(String bcc) {
        this.put("bcc", bcc);
        return this;
    }

    public RequestHeader cc(String cc) {
        this.put("cc", cc);
        return this;
    }


    public RequestHeader to(String to) {
        this.put("to", to);
        return this;
    }

    public RequestHeader from(String from) {
        this.put("from", from);
        return this;
    }

    public String getRequestId() {
        return (String) this.get(REQUEST_ID);
    }


    public String getFrom() {
        return (String) this.get("from");
    }


}
