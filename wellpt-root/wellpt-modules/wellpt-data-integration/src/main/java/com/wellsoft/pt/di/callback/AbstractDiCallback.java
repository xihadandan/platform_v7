package com.wellsoft.pt.di.callback;

import com.google.common.base.Stopwatch;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.activation.DataHandler;

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
public abstract class AbstractDiCallback
        implements Processor {
    protected static final ThreadLocal<Exchange> EXCHANGE_LOCAL = new ThreadLocal<>();
    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void process(Exchange exchange) {
        Stopwatch timer = Stopwatch.createStarted();
        try {
            EXCHANGE_LOCAL.set(exchange);
            callback(exchange.getIn().getBody(), null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            EXCHANGE_LOCAL.remove();
            logger.info("处理数据交换回调函数[{}]结束，耗时：{}", this.getClass(), timer.stop());
        }
    }


    protected <P> P getProperty(String key, Class<P> clazz) {
        return EXCHANGE_LOCAL.get().getProperty(key, clazz);
    }


    protected DataHandler getAttachment(String id) {
        return EXCHANGE_LOCAL.get().getIn().getAttachment(id);
    }

    protected <H> H getHeader(String key, Class<H> clazz) {
        return EXCHANGE_LOCAL.get().getIn().getHeader(key, clazz);
    }


    /**
     * 回调执行方法
     *
     * @param responseObj 返回的内容
     * @param request     原始的请求内容
     */
    public abstract void callback(Object responseObj, Object request);

    public abstract String name();


    public boolean isExpose() {
        return true;
    }
}
