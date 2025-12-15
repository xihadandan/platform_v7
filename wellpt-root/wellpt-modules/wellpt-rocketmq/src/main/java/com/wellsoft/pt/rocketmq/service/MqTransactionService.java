package com.wellsoft.pt.rocketmq.service;

import com.wellsoft.pt.jpa.service.JpaService;
import com.wellsoft.pt.rocketmq.dao.impl.MqTransactionDaoImpl;
import com.wellsoft.pt.rocketmq.entity.MqTransactionEntity;

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
public interface MqTransactionService extends JpaService<MqTransactionEntity, MqTransactionDaoImpl, String> {

    boolean exist(String id);

    String saveTransactionId(String transactionId);

    int deleteById(String id);


}
