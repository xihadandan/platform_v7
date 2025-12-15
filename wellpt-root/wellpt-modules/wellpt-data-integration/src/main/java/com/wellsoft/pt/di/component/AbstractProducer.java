package com.wellsoft.pt.di.component;

import com.google.common.base.Stopwatch;
import com.wellsoft.pt.security.util.IgnoreLoginUtils;
import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.activation.DataHandler;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;

/**
 * Description:数据交换-生产者定义
 *
 * @author chenq
 * @date 2019/7/11
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2019/7/11    chenq		2019/7/11		Create
 * </pre>
 */
public abstract class AbstractProducer<ENDPOINT extends Endpoint>
        extends DefaultProducer {
    protected Logger logger = LoggerFactory.getLogger(this.getClass());
    protected ENDPOINT endpoint;
    protected ThreadLocal<Exchange> EXCHANGE = new ThreadLocal<>();
    protected ThreadLocal<Exchange> COPY_EXCHANGE = new ThreadLocal<>();
    Class<ENDPOINT> endpointClass;

    public AbstractProducer(ENDPOINT endpoint) {
        super(endpoint);
        this.endpoint = endpoint;
        try {

            Type type = this.getClass().getGenericSuperclass();
            if (type instanceof ParameterizedType) {
                Type[] paramterTypes = ((ParameterizedType) type).getActualTypeArguments();
                endpointClass = (Class<ENDPOINT>) paramterTypes[0];
            }

        } catch (Exception e) {
        }
    }


    @Override
    public void process(Exchange exchange) throws Exception {
        log.debug("endpointUri = [{}] ", endpoint.getEndpointUri());
        Stopwatch timer = Stopwatch.createStarted();
        try {
            IgnoreLoginUtils.loginSuperadmin();
            COPY_EXCHANGE.set(exchange.copy(true));
            EXCHANGE.set(exchange);
            //处理消息内容
            action(exchange.getIn().getBody(), exchange.getIn().getHeaders(),
                    exchange.getProperties(), exchange.getIn().getAttachments());
        } catch (UnsupportedOperationException ue) {
            exchange.getOut().setFault(true);
            exchange.getOut().setBody(ue.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            IgnoreLoginUtils.logout();
            COPY_EXCHANGE.remove();
            EXCHANGE.remove();
            log.info("数据交换-消费[{}]处理结束，耗时：{}", this.getClass().getSimpleName(), timer.stop());
        }
    }


    protected void propagateActionMessage(Object result) {
        EXCHANGE.get().getIn().setBody(result);
    }


    /**
     * 传播新的消息内容
     *
     * @param body
     * @param headers
     * @param properties
     * @param attachments
     */
    protected void propagateNewMessage(Object body, Map<String, Object> headers,
                                       Map<String, Object> properties,
                                       Map<String, DataHandler> attachments) {
        EXCHANGE.get().getOut().setBody(body);
        EXCHANGE.get().getOut().getHeaders().putAll(headers);
        EXCHANGE.get().getOut().getAttachments().putAll(attachments);
        EXCHANGE.get().getProperties().putAll(properties);
    }


    /**
     * 传播原始的消息内容
     * <p>
     * 消息传播可以按照原始消息传播，也可以在传播已经在本次action方法加工过的消息内容
     */
    protected void propagate() {
        EXCHANGE.get().setOut(COPY_EXCHANGE.get().getIn());
        Map<String, Object> properties = COPY_EXCHANGE.get().getProperties();
        EXCHANGE.get().getProperties().putAll(properties);
    }

    /**
     * 停止向下级传播
     */
    protected void stopPropagate() {
        EXCHANGE.get().getOut().setFault(true);
        EXCHANGE.get().getOut().setBody("停止传播");
    }


    /**
     * @param body        消息内容
     * @param headers     消息头部信息
     * @param properties  消息属性信息
     * @param attachments 消息携带的附件信息
     */
    protected abstract void action(Object body, Map<String, Object> headers,
                                   Map<String, Object> properties,
                                   Map<String, DataHandler> attachments) throws Exception;
}
