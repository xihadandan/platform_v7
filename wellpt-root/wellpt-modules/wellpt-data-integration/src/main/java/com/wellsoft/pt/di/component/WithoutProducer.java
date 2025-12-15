package com.wellsoft.pt.di.component;

import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultProducer;

/**
 * Description:
 *
 * @author chenq
 * @date 2019/7/16
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2019/7/16    chenq		2019/7/16		Create
 * </pre>
 */
public class WithoutProducer extends DefaultProducer {


    public WithoutProducer(Endpoint endpoint) {
        super(endpoint);
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        exchange.getUnitOfWork().done(exchange);
    }
}
