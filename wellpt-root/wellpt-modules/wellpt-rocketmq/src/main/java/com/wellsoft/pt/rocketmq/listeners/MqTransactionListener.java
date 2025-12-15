package com.wellsoft.pt.rocketmq.listeners;

import com.wellsoft.pt.rocketmq.service.MqTransactionService;
import org.apache.commons.lang.StringUtils;
import org.apache.rocketmq.client.producer.LocalTransactionState;
import org.apache.rocketmq.client.producer.TransactionListener;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.IllegalTransactionStateException;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2021年01月12日   chenq	 Create
 * </pre>
 */
public class MqTransactionListener implements TransactionListener {

    MqTransactionService mqTransactionService;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    public MqTransactionListener(MqTransactionService mqTransactionService) {
        this.mqTransactionService = mqTransactionService;
    }

    @Override
    public LocalTransactionState executeLocalTransaction(Message msg, Object arg) {

        try {
            String uuid = mqTransactionService.saveTransactionId(msg.getTransactionId());
            if (StringUtils.isBlank(uuid)) {
                return LocalTransactionState.ROLLBACK_MESSAGE;
            }
            logger.info("保存事务消息=[{}]", msg.getTransactionId());
        } catch (IllegalTransactionStateException e) {
            logger.error("事务消息必须嵌套在事务里面：" + e.getMessage());
            return LocalTransactionState.ROLLBACK_MESSAGE;
        }
        return LocalTransactionState.UNKNOW;
    }

    @Override
    public LocalTransactionState checkLocalTransaction(MessageExt msg) {
        int row = mqTransactionService.deleteById(msg.getTransactionId());
        logger.info("删除消息事务=[{}] ，影响条数=[{}]", msg.getTransactionId(), row);
        return row > 0 ? LocalTransactionState.COMMIT_MESSAGE : LocalTransactionState.UNKNOW;
    }


}
