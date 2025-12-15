/*
 * @(#)2021年1月15日 V1.0
 *
 * Copyright 2021 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.log.support;

import com.google.common.collect.Lists;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.context.util.json.JsonUtils;
import com.wellsoft.pt.log.LogEvent;
import com.wellsoft.pt.log.entity.BusinessDetailsLog;
import com.wellsoft.pt.log.entity.BusinessOperationLog;
import com.wellsoft.pt.log.facade.service.LogFacadeService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import javax.transaction.Status;
import java.util.List;

/**
 * Description: 上下文日志工具
 *
 * @author zhongzh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2021年1月15日.1	zhongzh		2021年1月15日		Create
 * </pre>
 * @date 2021年1月15日
 */
public class ContextLogs {

    private static final Logger logger = LoggerFactory.getLogger(ContextLogs.class);

    private static final ThreadLocal<LogEvent> logEvent = new ThreadLocal<LogEvent>();

    private static final ThreadLocal<List<BusinessDetailsLog>> detailsLogs = new ThreadLocal<List<BusinessDetailsLog>>();

    /**
     * 添加日志详情
     *
     * @param detailsLogJson
     */
    public static BusinessDetailsLog addDetailsLog(String detailsLogJson) {
        if (StringUtils.isBlank(detailsLogJson)) {
            return null;
        }
        return addDetailsLog(JsonUtils.json2Object(detailsLogJson, BusinessDetailsLog.class));
    }

    /**
     * 添加日志详情
     *
     * @param detailsLogDto
     */
    public static BusinessDetailsLog addDetailsLog(BusinessDetailsLog detailsLogDto) {
        if (null == detailsLogDto) {
            return null;
        }
        List<BusinessDetailsLog> detailLogs = getDetailLogs();
        if (null == detailLogs) {
            detailLogs = Lists.newArrayList();
        }
        detailLogs.add(detailsLogDto);
        detailsLogs.set(detailLogs);
        return detailsLogDto;
    }

    public static List<BusinessDetailsLog> getDetailLogs() {
        return detailsLogs.get();
    }

    public static void removeDetailLogs() {
        detailsLogs.remove();
    }

    /**
     * @return
     */
    public static LogEvent getLogEvent() {
        return logEvent.get();
    }

    /**
     * 已经去除分布式事务了，不用加入事务
     *
     * @param event
     */
    public static void sendLogEvent(LogEvent event) {
        Assert.notNull(event, "event is not null");
        try {
            // 设置模块名称
            BusinessOperationLog source = event.getSource();
            if (null == source.getModuleName() && StringUtils.isNoneBlank(source.getModuleId())) {
                LogFacadeService logFacadeService = ApplicationContextHolder.getBean(LogFacadeService.class);
                source.setModuleName(logFacadeService.getModuleNameById(source.getModuleId()));
            }
            logEvent.set(event);
            sendLogEventInternal(Status.STATUS_COMMITTED);
        } catch (IllegalStateException ex) {
            logger.warn(ex.getMessage(), ex);
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }

    //已经没有分布式事务了,先去掉
	/*public static void sendLogEvent(LogEvent event) {
		Assert.notNull(event, "event is not null");
		try {
			// 设置模块名称
			BusinessOperationLog source = event.getSource();
			if (null == source.getModuleName() && StringUtils.isNoneBlank(source.getModuleId())) {
				LogFacadeService logFacadeService = ApplicationContextHolder.getBean(LogFacadeService.class);
				source.setModuleName(logFacadeService.getModuleNameById(source.getModuleId()));
			}
			TransactionManager transactionManager = ApplicationContextHolder.getBean(TransactionManager.class);
			Transaction transaction = transactionManager.getTransaction();
			if (null == transaction || !(Status.STATUS_ACTIVE == transaction.getStatus())) {
				// 没有事务，直接发送
				logEvent.set(event);
				sendLogEventInternal(Status.STATUS_COMMITTED);
				return;
			}
			LogEvent oEvent = getLogEvent();
			if (null == oEvent) {
				// 已经注册过，不再注册
				transaction.registerSynchronization(new Synchronization() {

					@Override
					public void beforeCompletion() {

					}

					@Override
					public void afterCompletion(int status) {
						sendLogEventInternal(status);
					}
				});
			}
			// 覆盖
			logEvent.set(event);
		} catch (IllegalStateException ex) {
			logger.warn(ex.getMessage(), ex);
			throw new RuntimeException(ex.getMessage(), ex);
		} catch (RollbackException ex) {
			logger.warn(ex.getMessage(), ex);
			throw new RuntimeException(ex.getMessage(), ex);
		} catch (SystemException ex) {
			logger.warn(ex.getMessage(), ex);
			throw new RuntimeException(ex.getMessage(), ex);
		}
	}*/

    private static void sendLogEventInternal(int status) {
        LogEvent event = getLogEvent();
        if (Status.STATUS_COMMITTED == status && null != event) {
            // 带入日志详情列表
            event.setDetails(getDetailLogs());
            // 事务提交成功，发送MQ处理日志
            LogFacadeService logFacadeService = ApplicationContextHolder.getBean(LogFacadeService.class);
            logFacadeService.sendLogEventOutTransation(event);
        }
        // 清理日志上下文
        logEvent.remove();
        removeDetailLogs();
    }

}
