/*
 * @(#)2013-10-12 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.log.support;

import com.wellsoft.context.config.Config;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.util.date.DateUtils;
import com.wellsoft.pt.security.util.TenantContextHolder;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.spi.LoggingEvent;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-10-12.1	zhulh		2013-10-12		Create
 * </pre>
 * @date 2013-10-12
 */
public class SystemLogAppender extends AppenderSkeleton {
    private static final int QUEUE_SIZE = 30;
    private static final LinkedBlockingQueue<LogRecord> queue = new LinkedBlockingQueue<LogRecord>(QUEUE_SIZE);
    private long lineNumber = 0;

    public synchronized static List<LogRecord> getLogRecords(long fromLineNumber) {
        List<LogRecord> logRecords = new ArrayList<LogRecord>();
        Iterator<LogRecord> it = queue.iterator();
        while (it.hasNext()) {
            LogRecord logRecord = it.next();
            if (logRecord.lineNumber >= fromLineNumber) {
                logRecords.add(logRecord);
            }
        }
        return logRecords;
    }

    /**
     * (non-Javadoc)
     *
     * @see org.apache.log4j.Appender#close()
     */
    @Override
    public void close() {
    }

    /**
     * (non-Javadoc)
     *
     * @see org.apache.log4j.Appender#requiresLayout()
     */
    @Override
    public boolean requiresLayout() {
        return false;
    }

    /**
     * (non-Javadoc)
     *
     * @see org.apache.log4j.AppenderSkeleton#append(org.apache.log4j.spi.LoggingEvent)
     */
    @Override
    protected synchronized void append(LoggingEvent event) {
        String tenantId = TenantContextHolder.getTenantId();
        if (tenantId == null || tenantId.equals(Config.COMMON_TENANT)) {
            return;
        }
        String msg = event.getLevel().toString() + Separator.COLON.getValue() + Separator.SPACE.getValue()
                + DateUtils.formatDateTime(new Date(event.getTimeStamp())) + Separator.SPACE.getValue()
                + event.getMessage();
        LogRecord logRecord = new LogRecord();
        logRecord.lineNumber = lineNumber++;
        logRecord.content = msg;
        if (queue.size() == QUEUE_SIZE) {
            queue.poll();
        }
        queue.offer(logRecord);
    }
}
