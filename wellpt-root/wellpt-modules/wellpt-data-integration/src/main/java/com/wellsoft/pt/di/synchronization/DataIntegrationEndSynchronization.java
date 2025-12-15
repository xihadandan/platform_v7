package com.wellsoft.pt.di.synchronization;

import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.di.constant.DiConstant;
import com.wellsoft.pt.di.service.DiDataInterationLogService;
import org.apache.camel.Exchange;
import org.apache.camel.spi.Synchronization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * Description: 数据交换接收的同步器：更新数据交换日志
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
public class DataIntegrationEndSynchronization implements Synchronization {

    private Logger logger = LoggerFactory.getLogger(this.getClass());


    @Override
    public void onComplete(Exchange exchange) {
        try {
            DiDataInterationLogService logService = ApplicationContextHolder.getBean(
                    DiDataInterationLogService.class);
            Date beginDate = exchange.getProperty(DiConstant.DI_CONSUMER_DATA_BEGIN_TIME,
                    Date.class);
            Date endDate = exchange.getProperty(DiConstant.DI_CONSUMER_DATA_END_TIME, Date.class);
            Integer pageIndex = exchange.getProperty(DiConstant.DI_CONSUMER_DATA_PAGE_INDEX,
                    Integer.class);
            Integer pageLimit = exchange.getProperty(DiConstant.DI_CONSUMER_DATA_PAGE_LIMIT,
                    Integer.class);
            Integer totalPage = exchange.getProperty(DiConstant.DI_CONSUMER_DATA_TOTAL_PAGE,
                    Integer.class);
            logService.saveEndLog(exchange.getExchangeId(), beginDate, endDate, pageIndex,
                    totalPage, pageLimit);
        } catch (Exception e) {
            logger.error("数据交换结束交换失败", exchange.getFromRouteId(), e);
        }
    }

    @Override
    public void onFailure(Exchange exchange) {

    }
}
