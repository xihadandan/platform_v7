package com.wellsoft.distributedlog.log4j.appender;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wellsoft.distributedlog.utils.GsonUtils;
import com.yomahub.tlog.context.TLogContext;
import com.yomahub.tlog.utils.LocalhostUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.spi.LoggingEvent;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2021年07月01日   chenq	 Create
 * </pre>
 */
public abstract class AbstractLogQueueAppender extends AppenderSkeleton {

    protected Integer queueSize = 1000;

    protected volatile LinkedBlockingQueue<String> queue = null;

    protected AtomicLong pushTime = new AtomicLong(0);

    protected String ip;

    protected String appName;

    protected Integer pushIntervalTimeMillis = 3000; // 默认推送间隔3秒

    ScheduledExecutorService scheduledExecutorService;

    public AbstractLogQueueAppender() {
        super();
        this.ip = LocalhostUtil.getHostIp();
        scheduledExecutorService = Executors
                .newSingleThreadScheduledExecutor();
        pushTime.set(System.currentTimeMillis());
        // 间隔推送日志
        scheduledExecutorService.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                if ((System.currentTimeMillis() - pushTime.get()) > pushIntervalTimeMillis && queue.size() > 0) {
                    startPushQueue();
                }
            }
        }, 10000, pushIntervalTimeMillis, TimeUnit.MILLISECONDS);
    }

    private void startPushQueue() {
        pushTime.set(System.currentTimeMillis());
        List<String> logs = Lists.newArrayListWithCapacity(queueSize);
        queue.drainTo(logs, queueSize);
        subAppend(logs);
    }

    @Override
    protected void append(LoggingEvent event) {
        if (queue == null) {
            synchronized (this) {
                if (queue == null)
                    queue = new LinkedBlockingQueue<>(queueSize + 100); // 冗余
            }
        }
        queue.add(GsonUtils.toJson(convert2Map(event)));
        if (((System.currentTimeMillis() - pushTime.get()) > this.pushIntervalTimeMillis || queueSize <= queue.size())
                && queue.size() > 0) {
            startPushQueue();
        }

    }

    protected abstract void subAppend(List<String> logs);

    public Map<String, Object> convert2Map(LoggingEvent event) {
        Map<String, Object> logMap = Maps.newHashMap();
        logMap.put("content", event.getMessage());
        logMap.put("logLevel", event.getLevel().toString());
        logMap.put("className", event.getLocationInformation().getClassName());
        if (StringUtils.isNotBlank(TLogContext.getTraceId())) {
            logMap.put("traceId", TLogContext.getTraceId());
        }
        if (StringUtils.isNotBlank(TLogContext.getSpanId())) {
            logMap.put("spanId", TLogContext.getSpanId());
        }
        if (StringUtils.isNotBlank(TLogContext.getPreIp())) {
            logMap.put("preIp", TLogContext.getPreIp());
        }
        if (StringUtils.isNotBlank(TLogContext.getPreIvkApp())) {
            logMap.put("preApp", TLogContext.getPreIvkApp());
        }
        if (StringUtils.isNotBlank(appName)) {
            logMap.put("app", appName);
        }
        if (StringUtils.isNotBlank(ip)) {
            logMap.put("ip", ip);
        }
        logMap.put("logTime", new Date(event.getTimeStamp()));
        return logMap;
    }

    @Override
    public void close() {

    }

    @Override
    public boolean requiresLayout() {
        return true;
    }
}
