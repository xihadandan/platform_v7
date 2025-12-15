/*
 * @(#)2019年11月7日 V1.0
 *
 * Copyright 2019 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.jpa.log4tx;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Description: Utils
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
public abstract class TxLogUtils {

    public static final String nl = System.getProperty("line.separator");
    public static final int QUEUE_SIZE = 1024;
    private static final Logger logger = LoggerFactory.getLogger(TxLogUtils.class);
    private static final ThreadLocal<InvocationNode> invocationNodes = new ThreadLocal<InvocationNode>();

    private static final ThreadLocal<LinkedBlockingQueue<TxLogItem>> threadLocalTx = new ThreadLocal<LinkedBlockingQueue<TxLogItem>>();

    /**
     * log4tx内部调用
     */
    public static void createLog() {
        createLog(true);
    }

    /**
     * log4tx内部调用
     *
     * @param clear
     */
    public static void createLog(boolean clear) {
        LinkedBlockingQueue<TxLogItem> queue = getLog();
        if (queue == null) {
            threadLocalTx.set(new LinkedBlockingQueue<TxLogItem>(QUEUE_SIZE));
        } else if (clear) {
            queue.clear();
        }
    }

    /**
     * log4tx内部调用
     */
    public static void removeLog() {
        threadLocalTx.remove();
    }

    /**
     * log4tx内部调用
     */
    public static LinkedBlockingQueue<TxLogItem> getLog() {
        return threadLocalTx.get();
    }

    /**
     * log4tx内部调用
     */
    public static void clearLog() {
        LinkedBlockingQueue<TxLogItem> queue = getLog();
        if (queue != null && queue.isEmpty() == false) {
            queue.clear();
        }
    }

    /**
     * 外部调用，用于添加而外调试日志
     *
     * @param txLogItem
     * @return 队列满时，移除的头部
     */
    public static TxLogItem appendJtaLog(TxLogItem txLogItem) {
        TxLogItem removeItem = null;
        LinkedBlockingQueue<TxLogItem> queue = getLog();
        if (queue.size() >= QUEUE_SIZE) {
            removeItem = queue.poll();
        }
        queue.offer(txLogItem);
        return removeItem;
    }

    public static void printJtaStack() {
        StringWriter writer = new StringWriter();
        printJtaStack(writer);
        System.out.print(writer);
    }

    public static void printJtaStack(Writer writer) {
        printJtaStack(TxLogLevel.INFO, writer);
    }

    public static void printJtaStack(TxLogLevel level, Writer writer) {
        LinkedBlockingQueue<TxLogItem> queue = getLog();
        if (queue != null && queue.isEmpty() == false) {
            try {
                for (TxLogItem logRecord : queue) {
                    if (logRecord.level.compareTo(level) > 0) {
                        continue;
                    }
                    writer.append(logRecord.content);// .append(nl);
                }
            } catch (IOException ex) {
                logger.warn(ex.getMessage(), ex);
            }
        }
    }

    public static InvocationNode getInvocationNode() {
        return invocationNodes.get();
    }

    public static void setInvocationNode(InvocationNode invocationNode) {
        invocationNodes.set(invocationNode);
    }

    public static void removeInvocationNode() {
        invocationNodes.remove();
    }

    public static String printSpringTxStack() {
        return printSpringTxStack(getInvocationNode());
    }

    public static String printSpringTxStack(InvocationNode invocationNode) {
        String ret = null;
        if (invocationNode != null) {
            // 从根节点开始输出
            InvocationNode parent = invocationNode;
            do {
                parent.setOpen(true);// 展开当前节点
                if (parent.getParent() == null) {
                    break;
                } else {
                    parent = parent.getParent();
                }
            } while (true);
            logger.info(ret = parent.toString());
        }
        return ret;
    }

    public static void main(String[] args) {
        System.out.println(TxLogLevel.DEBUG.compareTo(TxLogLevel.INFO));
    }
}
