package com.wellsoft.pt.di.component;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.impl.ScheduledPollConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.activation.DataHandler;
import java.util.Map;

/**
 * Description: 数据交换-轮询消费者定义
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
public abstract class AbstractScheduledPollConsumer<ENDPOINT extends AbstractEndpoint>
        extends ScheduledPollConsumer {

    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    protected ENDPOINT endpoint;

    protected ThreadLocal<Object> BODY = new ThreadLocal<>();

    protected ThreadLocal<Exchange> EXCHANGE = new ThreadLocal<Exchange>();

    public AbstractScheduledPollConsumer(ENDPOINT endpoint, Processor processor) {
        super(endpoint, processor);
        this.endpoint = endpoint;
        super.setDelay(endpoint.getDelay());
    }

    /**
     * 设置消息体内容
     *
     * @return
     */
    protected abstract <BODY> BODY body();

    /**
     * 设置头部信息
     *
     * @return
     */
    protected abstract Map<String, Object> headers();

    /**
     * 设置属性
     *
     * @return
     */
    public abstract Map<String, Object> properties();

    @Override
    protected int poll() throws Exception {
        Exchange exchange = endpoint.createExchange();
        EXCHANGE.set(exchange);
        //设置消息体
        Object body = body();
        if (body == null) {
            logger.warn("数据交换消费类[{}]，暂无数据处理", this.getClass().getSimpleName());
            return 0;
        }
        BODY.set(body);
        exchange.getIn().setBody(body);
        //设置消息头部
        Map headers = headers();
        if (headers != null) {
            exchange.getIn().setHeaders(headers());
        }
        //设置消息属性
        Map<String, Object> properties = properties();
        if (properties != null) {
            exchange.getProperties().putAll(properties());
        }

        //设置附件
        Map<String, DataHandler> attachments = attachments();
        if (attachments != null) {
            exchange.getIn().setAttachments(attachments());
        }
        try {
            //传递消息
            getProcessor().process(exchange);
            return 1;
        } finally {
            BODY.remove();
            EXCHANGE.remove();
            if (exchange.getException() != null) {
                getExceptionHandler().handleException("传递消息错误", exchange,
                        exchange.getException());
            }
        }
    }


    protected void setProperty(String key, Object value) {
        EXCHANGE.get().setProperty(key, value);
    }

    /**
     * 设置附件信息
     *
     * @return
     */
    protected abstract Map<String, DataHandler> attachments();


}
