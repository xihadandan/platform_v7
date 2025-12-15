package com.wellsoft.pt.rocketmq.service.impl;

import com.google.common.collect.Maps;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.rocketmq.dao.impl.MqTransactionDaoImpl;
import com.wellsoft.pt.rocketmq.entity.MqTransactionEntity;
import com.wellsoft.pt.rocketmq.service.MqTransactionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

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
@Service
public class MqTransactionServiceImpl extends AbstractJpaServiceImpl<MqTransactionEntity, MqTransactionDaoImpl, String> implements MqTransactionService {

    @Override
    public boolean exist(String id) {
        return this.dao.getOneByFieldEq("id", id) != null;
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public String saveTransactionId(String transactionId) {
        MqTransactionEntity entity = new MqTransactionEntity();
        entity.setId(transactionId);
        this.dao.save(entity);
        return entity.getUuid();
    }


    @Override
    @Transactional
    public int deleteById(String id) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("id", id);
        return this.dao.deleteBySQL("delete from mq_transaction where id=:id", params);
    }
}
