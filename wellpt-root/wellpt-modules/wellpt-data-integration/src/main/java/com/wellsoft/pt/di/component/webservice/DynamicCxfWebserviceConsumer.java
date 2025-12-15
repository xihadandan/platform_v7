package com.wellsoft.pt.di.component.webservice;

import com.wellsoft.pt.di.component.AbstractScheduledPollConsumer;
import org.apache.camel.Processor;

import javax.activation.DataHandler;
import java.util.Map;

/**
 * Description:
 *
 * @author chenq
 * @date 2019/8/15
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2019/8/15    chenq		2019/8/15		Create
 * </pre>
 */
public class DynamicCxfWebserviceConsumer extends
        AbstractScheduledPollConsumer<DynamicCxfWebserviceEndpoint> {


    public DynamicCxfWebserviceConsumer(
            DynamicCxfWebserviceEndpoint endpoint, Processor processor) {
        super(endpoint, processor);
    }

    @Override
    protected Object body() {
        return null;
    }

    @Override
    protected Map<String, Object> headers() {
        return null;
    }

    @Override
    public Map<String, Object> properties() {
        return null;
    }

    @Override
    protected Map<String, DataHandler> attachments() {
        return null;
    }
}
