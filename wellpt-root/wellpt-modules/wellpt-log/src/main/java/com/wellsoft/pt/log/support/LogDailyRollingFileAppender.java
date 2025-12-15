package com.wellsoft.pt.log.support;

import org.apache.log4j.DailyRollingFileAppender;
import org.apache.log4j.spi.LoggingEvent;

/**
 * Description: 如何描述该类
 *
 * @author lmw
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2015-6-30	lmw		2015-6-30		Create
 * </pre>
 * @date 2015-6-30
 */
public class LogDailyRollingFileAppender extends DailyRollingFileAppender {

    @Override
    protected void subAppend(LoggingEvent event) {
        if (this.threshold.toInt() == event.getLevel().toInt()) {
            super.subAppend(event);
        }
    }
}
