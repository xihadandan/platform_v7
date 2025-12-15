package com.wellsoft.pt.message.facade.service.impl;

import com.wellsoft.pt.message.entity.SmsLogEntity;
import com.wellsoft.pt.message.facade.service.SmsLogFacadeService;
import com.wellsoft.pt.message.service.SmsLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
public class SmsLogFacadeServiceImpl implements SmsLogFacadeService {

    @Autowired
    SmsLogService smsLogService;

    @Override
    public Long saveLog(SmsLogEntity log) {
        return smsLogService.saveLog(log);
    }

    @Override
    public List<SmsLogEntity> getBySmsId(String smsId) {
        return smsLogService.getBySmsId(smsId);
    }

    @Override
    public void batchSaveLog(List<SmsLogEntity> list) {
        smsLogService.batchSaveLog(list);
    }

    @Override
    public void updateLogStatus(String smsId, SmsLogEntity.Status status) {
        smsLogService.updateLogStatus(smsId, status);
    }

    @Override
    public void updateSmsRead(String smsId, boolean read) {
        smsLogService.updateSmsRead(smsId, read);
    }

    @Override
    public void updateSmsRecId(String smsId, String recId) {
        smsLogService.updateSmsRecId(smsId, recId);
    }

    @Override
    public void updateLogReceived(String smsId, Date receiveTime) {
        smsLogService.updateLogReceived(smsId, receiveTime);
    }

}
