/*
 * @(#)2019年11月7日 V1.0
 *
 * Copyright 2019 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.jpa.log4tx.support;

import com.wellsoft.context.enums.Separator;
import com.wellsoft.pt.jpa.log4tx.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Description: slf4j的日志实现
 * log4j.logger.log4tx.jta=ERROR
 * log4j.logger.log4tx.jta=INFO
 * log4j.logger.log4tx.jta=DEBUG
 * log4j.logger.log4tx.jta=OFF
 *
 * @author zhongzh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2019年11月7日.1	zhongzh		2019年11月7日		Create
 * </pre>
 * @date 2019年11月7日
 */
@Component
public class Slf4jTxLog implements TxLog {

    private final Logger jtaLogger = LoggerFactory.getLogger("log4tx.jta");

    private final Logger springLogger = LoggerFactory.getLogger("log4tx.spring");

    // 打印线程堆方法
    private List<String> debugMethods = new ArrayList<String>();

    // 线程堆前置
    private List<String> stackPrefixs = new ArrayList<String>();
    private List<String> ignorePrefixs = new ArrayList<String>();

    public Slf4jTxLog() {
        String debugMethodsStr = System.getProperty("log4tx.debugMethods");
        if (StringUtils.isNotBlank(debugMethodsStr)) {
            debugMethods.addAll(Arrays.asList(debugMethodsStr.split(Separator.COMMA.getValue())));
        } else {
            debugMethods.add("begin");// 记录事务的开始点，方便查找只读事务和事务超时时间设置
            debugMethods.add("rollback");// 记录异常点
            debugMethods.add("setRollbackOnly");// 记录异常点
            debugMethods.add("setTransactionTimeout");
        }

        String stackPrefixsStr = System.getProperty("log4tx.stackPrefixs");
        if (StringUtils.isNotBlank(stackPrefixsStr)) {
            stackPrefixs.addAll(Arrays.asList(stackPrefixsStr.split(Separator.COMMA.getValue())));
        } else {
            stackPrefixs.add("com.wellsoft.");
            // stackPrefixs.add("bitronix.tm.");
            // stackPrefixs.add("org.springframework.");
        }
        String ignorePrefixsStr = System.getProperty("log4tx.ignorePrefixs");
        if (StringUtils.isNotBlank(ignorePrefixsStr)) {
            ignorePrefixs.addAll(Arrays.asList(ignorePrefixsStr.split(Separator.COMMA.getValue())));
        } else {
            ignorePrefixs.add("net.sf.log4jdbc");
            ignorePrefixs.add("com.wellsoft.pt.jpa.log4tx");
        }
    }

    public static boolean startWiths(List<String> prefixs, String prefix) {
        for (String localPrefix : prefixs) {
            if (localPrefix.startsWith(prefix) || prefix.startsWith(localPrefix)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isJtaLoggingEnabled() {
        return this.jtaLogger.isErrorEnabled();
    }

    @Override
    public boolean isSpringLoggingEnabled() {
        return this.springLogger.isErrorEnabled();
    }

    @Override
    public void infoReturn(TxSpy spy, String methodCall, String returnMsg) {
        appendLog(TxLogLevel.INFO, spy, methodCall, returnMsg);
    }

    @Override
    public void errorReturn(TxSpy spy, String methodCall, String returnMsg) {
        appendLog(TxLogLevel.ERROR, spy, methodCall, returnMsg);
    }

    @Override
    public void debugReturn(TxSpy spy, String methodCall, String returnMsg) {
        appendLog(TxLogLevel.DEBUG, spy, methodCall, returnMsg);
    }

    protected void appendLog(TxLogLevel level, TxSpy spy, String methodCall, String returnMsg) {
        if (StringUtils.equals(methodCall, "getStatus")) {
            // 忽略getStatus
            return;
        }
        boolean debugEnabled = jtaLogger.isDebugEnabled();
        String classType = spy.getClassType();
        String header = classType + "." + methodCall + " returned [" + (returnMsg == null ? "void" : returnMsg) + "]";
        boolean traceFromApplication = debugEnabled || startWiths(debugMethods, methodCall);
        if (traceFromApplication) {
            header += "\n  " + getDebugInfo();
        }
        TxLogItem logRecord = new TxLogItem();
        logRecord.timestamp = System.currentTimeMillis(); // org.apache.log4j.Level
        logRecord.content = header;
        logRecord.level = level;
        TxLogItem removeItem = TxLogUtils.appendJtaLog(logRecord);
        if (removeItem != null && (debugEnabled || jtaLogger.isInfoEnabled())) {
            if (removeItem.level == TxLogLevel.DEBUG && debugEnabled) {
                jtaLogger.debug(logRecord.content);
            } else {
                jtaLogger.info(logRecord.content);
            }
        }
    }

    @Override
    public String logStackTrace() {
        String result = null;
        StringWriter writer = new StringWriter();
        // writer.append(Log4JdbcFactory.nl);
        if (jtaLogger.isDebugEnabled()) {
            TxLogUtils.printJtaStack(TxLogLevel.DEBUG, writer);
            jtaLogger.debug(result = writer.toString());
        } else if (jtaLogger.isInfoEnabled()) {
            TxLogUtils.printJtaStack(TxLogLevel.INFO, writer);
            jtaLogger.info(result = writer.toString());
        }
        // writer.close();
        return result;
    }

    public String getDebugInfo() {
        Throwable t = new Throwable();
        t.fillInStackTrace();
        StackTraceElement[] stackTrace = t.getStackTrace();
        if (stackTrace != null) {
            StringBuffer dump = new StringBuffer();
            boolean first = true;
            for (int i = 0; i < stackTrace.length; i++) {
                String className = stackTrace[i].getClassName();
                if (startWiths(ignorePrefixs, className)) {
                    // 忽略log4tx框架
                    continue;
                } else if (stackPrefixs.isEmpty() == false && startWiths(stackPrefixs, className) == false) {
                    // 如果有前缀，只打印前缀，其他忽略
                    continue;
                }
                if (first) {
                    first = false;
                } else {
                    dump.append("  ");
                }
                dump.append("at ");
                dump.append(stackTrace[i]);
                dump.append(TxLogUtils.nl);
            }
            return dump.toString();
        }
        return null;
    }
}
