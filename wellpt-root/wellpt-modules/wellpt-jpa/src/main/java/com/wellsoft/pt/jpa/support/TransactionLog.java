package com.wellsoft.pt.jpa.support;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * Description: 事务日记
 *
 * @author xujm
 * @version 1.0
 * @date @2015-5-5
 */
@Aspect
public class TransactionLog implements Ordered {
    /**
     * 事务超长日记
     */
    private static final Logger transactionLogger = LoggerFactory.getLogger("TransactionLogFile");

    /**
     * 普通日记
     */
    private static final Logger logger = LoggerFactory.getLogger(TransactionLog.class);

    private static final DateFormat dateFormate = new SimpleDateFormat("yyyy年MM月dd日 HH时mm分ss秒 E");

    /**
     * 事务最长时间10s
     */
    private static final float MAX_TRANSACTION_TIME = 10f;

    /**
     * 当事务时间超过10s时打印调用者信息
     *
     * @param transactionTime
     */
    private static void logStackTracInfo(float transactionTime, ProceedingJoinPoint pjp, UUID uuid) {
        if (transactionTime > MAX_TRANSACTION_TIME) {
            transactionLogger.error("***********事务时间过长" + uuid.toString() + " 类：" + pjp.getTarget().getClass() + "，方法："
                    + pjp.getSignature().getName() + "，花费时间：" + transactionTime + "***********");
            StackTraceElement stack[] = Thread.currentThread().getStackTrace();
            for (StackTraceElement ste : stack) {
                transactionLogger.error("事务时间过长  called by " + ste.getClassName() + "." + ste.getMethodName() + "/"
                        + ste.getFileName());
            }
            transactionLogger.error("***********事务时间过长" + uuid.toString() + "end***********");
        }
    }

    @Around("@annotation(org.springframework.transaction.annotation.Transactional)")
    public Object around(ProceedingJoinPoint pjp) throws Throwable {
        UUID uuid = UUID.randomUUID();
        Date startDate = new Date();
        try {
            logger.info("开启事务" + uuid.toString() + " 类：" + pjp.getTarget().getClass() + "，方法："
                    + pjp.getSignature().getName() + "，时间：" + dateFormate.format(startDate));

            Object retVal = pjp.proceed();

            Date endDate = new Date();

            float transactionTime = (endDate.getTime() - startDate.getTime()) / 1000f;
            logStackTracInfo(transactionTime, pjp, uuid);
            logger.info("事务关闭" + uuid.toString() + " 类：" + pjp.getTarget().getClass() + "，方法："
                    + pjp.getSignature().getName() + "，时间：" + dateFormate.format(endDate) + ",花费时间：" + transactionTime
                    + "s");
            return retVal;
        } catch (Exception e) {
            Date endDate = new Date();
            float transactionTime = (endDate.getTime() - startDate.getTime()) / 1000f;
            logger.info("事务异常" + uuid.toString() + " 类：" + pjp.getTarget().getClass() + "，方法："
                    + pjp.getSignature().getName() + "，时间：" + dateFormate.format(endDate) + ",花费时间：" + transactionTime
                    + "s");
            logStackTracInfo(transactionTime, pjp, uuid);
            throw e;
        }
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
