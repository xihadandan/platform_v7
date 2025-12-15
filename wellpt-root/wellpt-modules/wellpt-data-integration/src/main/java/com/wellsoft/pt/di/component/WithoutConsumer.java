package com.wellsoft.pt.di.component;

import org.apache.camel.Endpoint;
import org.apache.camel.Processor;
import org.apache.camel.impl.DefaultConsumer;

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
public class WithoutConsumer extends DefaultConsumer {
    public WithoutConsumer(Endpoint endpoint, Processor processor) {
        super(endpoint, processor);
    }


    @Override
    public void start() throws Exception {
        throw new UnsupportedOperationException("不支持的消费者");
    }

    @Override
    protected void doStart() throws Exception {
        throw new UnsupportedOperationException("不支持的消费者");
    }
}
