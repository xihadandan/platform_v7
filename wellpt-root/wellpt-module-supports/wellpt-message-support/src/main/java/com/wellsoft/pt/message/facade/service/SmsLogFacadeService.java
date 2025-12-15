package com.wellsoft.pt.message.facade.service;

import com.wellsoft.pt.message.entity.SmsLogEntity;

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
public interface SmsLogFacadeService {

    Long saveLog(SmsLogEntity log);

    List<SmsLogEntity> getBySmsId(String smsId);

    void batchSaveLog(List<SmsLogEntity> list);

    void updateLogStatus(String smsId, SmsLogEntity.Status status);

    void updateSmsRead(String smsId, boolean read);

    void updateSmsRecId(String smsId, String recId);

    void updateLogReceived(String smsId, Date receiveTime);
}
