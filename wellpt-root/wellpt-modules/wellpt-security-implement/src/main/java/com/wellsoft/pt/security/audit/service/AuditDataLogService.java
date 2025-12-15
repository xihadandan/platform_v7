package com.wellsoft.pt.security.audit.service;

import com.wellsoft.pt.jpa.service.JpaService;
import com.wellsoft.pt.security.audit.dao.AuditDataLogDao;
import com.wellsoft.pt.security.audit.dto.AuditDataLogDto;
import com.wellsoft.pt.security.audit.entity.AuditDataLogEntity;

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
public interface AuditDataLogService extends JpaService<AuditDataLogEntity, AuditDataLogDao, Long> {

    void saveAuditDataLog(AuditDataLogDto logDto);
}
