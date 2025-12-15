package com.wellsoft.pt.message.service.impl;

import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.message.dao.impl.SmsLogDaoImpl;
import com.wellsoft.pt.message.entity.SmsLogEntity;
import com.wellsoft.pt.message.service.SmsLogService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2025年09月23日   chenq	 Create
 * </pre>
 */
@Service
public class SmsLogServiceImpl extends AbstractJpaServiceImpl<SmsLogEntity, SmsLogDaoImpl, Long> implements SmsLogService {
    @Override
    @Transactional
    public Long saveLog(SmsLogEntity log) {
        dao.save(log);
        return log.getUuid();
    }

    @Override
    public List<SmsLogEntity> getBySmsId(String smsId) {
        return dao.listByFieldEqValue("smsId", smsId);
    }

    @Override
    @Transactional
    public void batchSaveLog(List<SmsLogEntity> list) {
        dao.saveAll(list);
    }

    @Override
    @Transactional
    public void updateLogStatus(String smsId, SmsLogEntity.Status status) {
        List<SmsLogEntity> entity = this.getBySmsId(smsId);
        for (SmsLogEntity e : entity) {
            e.setStatus(status);
        }
        dao.saveAll(entity);
    }

    @Override
    @Transactional
    public void updateSmsRead(String smsId, boolean read) {
        List<SmsLogEntity> entity = this.getBySmsId(smsId);
        for (SmsLogEntity e : entity) {
            e.setIsRead(read);
        }
        dao.saveAll(entity);
    }

    @Override
    @Transactional
    public void updateSmsRecId(String smsId, String recId) {
        List<SmsLogEntity> entity = this.getBySmsId(smsId);
        for (SmsLogEntity e : entity) {
            e.setRecId(recId);
        }
        dao.saveAll(entity);
    }

    @Override
    @Transactional
    public void updateLogReceived(String smsId, Date receiveTime) {
        List<SmsLogEntity> entity = this.getBySmsId(smsId);
        for (SmsLogEntity e : entity) {
            e.setReceiveTime(receiveTime);
            e.setStatus(SmsLogEntity.Status.DELIVERED);
        }
        dao.saveAll(entity);
    }


}
