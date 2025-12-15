package com.wellsoft.pt.di.component.callback;

import com.wellsoft.pt.di.component.AbstractEndpoint;
import com.wellsoft.pt.di.component.WithoutConsumer;

/**
 * Description:
 *
 * @author chenq
 * @date 2019/7/23
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2019/7/23    chenq		2019/7/23		Create
 * </pre>
 */
public class CallbackEndpoint extends
        AbstractEndpoint<CallbackDIComponent, CallbackProducer, WithoutConsumer> {

    private String callbackClass;


    @Override
    public String endpointPrefix() {
        return "callback";
    }

    @Override
    public String endpointName() {
        return "回调函数端点";
    }

    @Override
    public boolean isExpose() {
        return false;
    }

    public String getCallbackClass() {
        return callbackClass;
    }

    public void setCallbackClass(String callbackClass) {
        this.callbackClass = callbackClass;
    }
}
