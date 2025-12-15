package com.wellsoft.pt.api.request;

/**
 * Description:
 *
 * @author chenq
 * @date 2018/8/8
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2018/8/8    chenq		2018/8/8		Create
 * </pre>
 */
public class DemoApiRequest extends ApiRequest {
    private static final long serialVersionUID = -5571449544969684239L;

    private String uuid;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
