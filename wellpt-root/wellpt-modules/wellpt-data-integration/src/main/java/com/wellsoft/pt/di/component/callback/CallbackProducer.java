package com.wellsoft.pt.di.component.callback;

import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.di.component.AbstractProducer;
import com.wellsoft.pt.di.entity.DiCallbackRequestEntity;
import com.wellsoft.pt.di.enums.CallbackStatusEnum;
import com.wellsoft.pt.di.service.DiCallbackRequestService;
import com.wellsoft.pt.di.util.MessageUtils;
import org.apache.camel.Exchange;

import javax.activation.DataHandler;
import java.util.Map;

/**
 * Description:
 *
 * @author chenq
 * @date 2019/7/23
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 修改后版本	    修改人		修改日期			修改内容
 * 2019/7/23    chenq		2019/7/23		Create
 * </pre>
 */
public class CallbackProducer extends AbstractProducer<CallbackEndpoint> {
    public CallbackProducer(CallbackEndpoint endpoint) {
        super(endpoint);
    }

    @Override
    protected void action(Object body, Map<String, Object> headers, Map<String, Object> properties,
                          Map<String, DataHandler> attachments) throws Exception {

        DiCallbackRequestEntity requestEntity = new DiCallbackRequestEntity();
        try {
            requestEntity.setRequestBody(MessageUtils.object2ReadableJSON(body));
        } catch (Exception e) {
            logger.error("保存异步回调请求的原始请求对象异常：", e);
        }

        requestEntity.setCallbackStatus(CallbackStatusEnum.WAIT_CALLBACK.ordinal());//等待反馈
        requestEntity.setTimeConsuming(0);
        requestEntity.setCallbackClass(super.endpoint.getCallbackClass());
        requestEntity.setRequestId(
                EXCHANGE.get().getProperty(Exchange.CORRELATION_ID, String.class));
        //保存回调实体
        ApplicationContextHolder.getBean(DiCallbackRequestService.class).save(requestEntity);
    }
}
