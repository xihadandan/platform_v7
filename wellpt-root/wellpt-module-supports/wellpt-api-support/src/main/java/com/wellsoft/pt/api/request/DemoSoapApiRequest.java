package com.wellsoft.pt.api.request;

/**
 * Description:
 *
 * @author chenq
 * @date 2018/11/8
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2018/11/8    chenq		2018/11/8		Create
 * </pre>
 */
public class DemoSoapApiRequest extends SoapApiRequest {
    private String name;

    public DemoSoapApiRequest(Operation operation) {
        super(operation);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
