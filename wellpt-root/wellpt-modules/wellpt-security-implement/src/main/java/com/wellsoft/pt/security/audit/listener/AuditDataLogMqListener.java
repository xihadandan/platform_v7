package com.wellsoft.pt.security.audit.listener;

import com.wellsoft.pt.rocketmq.annotation.RocketMqListener;
import com.wellsoft.pt.security.audit.dto.AuditDataLogDto;
import com.wellsoft.pt.security.audit.service.AuditDataLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2025年05月08日   chenq	 Create
 * </pre>
 */
@Component
public class AuditDataLogMqListener {

    @Autowired
    AuditDataLogService auditDataLogService;

    @RocketMqListener(requireNew = true, body = AuditDataLogDto.class, tags = "audit_data_log", topic = "log")
    public void saveLog(AuditDataLogDto dto) {
        auditDataLogService.saveAuditDataLog(dto);
    }
}
