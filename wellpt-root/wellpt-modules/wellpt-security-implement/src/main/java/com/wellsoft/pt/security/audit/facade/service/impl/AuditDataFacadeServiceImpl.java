package com.wellsoft.pt.security.audit.facade.service.impl;

import com.google.common.base.Throwables;
import com.wellsoft.context.util.json.JsonUtils;
import com.wellsoft.pt.rocketmq.support.MqUtils;
import com.wellsoft.pt.security.audit.dto.AuditDataLogDto;
import com.wellsoft.pt.security.audit.facade.service.AuditDataFacadeService;
import com.wellsoft.pt.security.audit.service.AuditDataLogService;
import org.apache.rocketmq.client.producer.MQProducer;
import org.apache.rocketmq.common.message.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2024年12月26日   chenq	 Create
 * </pre>
 */
@Service
public class AuditDataFacadeServiceImpl implements AuditDataFacadeService {

    @Autowired
    AuditDataLogService auditDataLogService;

    @Resource(name = "transactionMQProducer")
    private MQProducer transactionMQProducer;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void saveAuditDataLog(AuditDataLogDto dto) {
        auditDataLogService.saveAuditDataLog(dto);
//        try {
//            Message msg = new Message(MqUtils.getTopic("log"), "audit_data_log", JsonUtils
//                    .object2Gson(dto).getBytes("UTF-8"));
//            transactionMQProducer.sendMessageInTransaction(msg, null);
//        } catch (Exception e) {
//            logger.error("保存审计数据日志消息异常: {}", Throwables.getStackTraceAsString(e));
//            throw new RuntimeException(e);
//        }
    }
}
