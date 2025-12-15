package com.wellsoft.pt.di.processor;

import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.di.service.DiDataInterationLogService;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;

/**
 * Description:
 *
 * @author chenq
 * @date 2019/8/17
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2019/8/17    chenq		2019/8/17		Create
 * </pre>
 */
public class ExceptionOccuredProcessor implements Processor {
    @Override
    public void process(Exchange exchange) throws Exception {
        String exchangeId = exchange.getExchangeId();
        DiDataInterationLogService logService = ApplicationContextHolder.getBean(
                DiDataInterationLogService.class);
        logService.saveException(exchange.getException(), exchangeId);
    }
}
