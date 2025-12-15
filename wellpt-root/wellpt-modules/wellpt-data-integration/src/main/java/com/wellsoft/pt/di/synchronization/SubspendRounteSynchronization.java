package com.wellsoft.pt.di.synchronization;

import org.apache.camel.Exchange;
import org.apache.camel.spi.Synchronization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * Description: 挂起路由同步器
 *
 * @author chenq
 * @date 2019/7/24
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2019/7/24    chenq		2019/7/24		Create
 * </pre>
 */
public class SubspendRounteSynchronization implements Synchronization {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private Long timeout = 1L;

    private TimeUnit timeUnit = TimeUnit.MINUTES;

    public SubspendRounteSynchronization() {
    }

    public SubspendRounteSynchronization(Long timeout, TimeUnit timeUnit) {
        this.timeout = timeout;
        this.timeUnit = timeUnit;
    }

    @Override
    public void onComplete(Exchange exchange) {
        try {
            exchange.getContext().suspendRoute(exchange.getFromRouteId(), timeout,
                    timeUnit);
        } catch (Exception e) {
            logger.error("数据交换挂起路由[{}]失败", exchange.getFromRouteId(), e);
        }
    }

    @Override
    public void onFailure(Exchange exchange) {
        try {
            exchange.getContext().suspendRoute(exchange.getFromRouteId(), timeout,
                    timeUnit);
        } catch (Exception e) {
            logger.error("数据交换挂起路由[{}]失败", exchange.getFromRouteId(), e);
        }
    }
}
