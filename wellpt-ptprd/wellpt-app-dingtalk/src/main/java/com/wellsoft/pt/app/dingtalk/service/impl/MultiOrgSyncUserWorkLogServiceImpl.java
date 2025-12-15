package com.wellsoft.pt.app.dingtalk.service.impl;

import com.wellsoft.pt.app.dingtalk.constants.DingtalkInfo;
import com.wellsoft.pt.app.dingtalk.dao.MultiOrgSyncUserWorkLogDao;
import com.wellsoft.pt.app.dingtalk.entity.MultiOrgSyncUserWorkLog;
import com.wellsoft.pt.app.dingtalk.service.MultiOrgSyncUserWorkLogService;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import org.springframework.stereotype.Service;

/**
 * Description: 如何描述该类
 *
 * @author liuyz
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2021年08月16日.1	liuyz		2021年08月16日  	Create
 *          </pre>
 * @date 2021年08月16日
 */
@Service
@Deprecated
public class MultiOrgSyncUserWorkLogServiceImpl extends AbstractJpaServiceImpl<MultiOrgSyncUserWorkLog, MultiOrgSyncUserWorkLogDao, String> implements MultiOrgSyncUserWorkLogService {

    @Override
    public boolean isExistsErrorData(String logId) {
        MultiOrgSyncUserWorkLog multiOrgSyncUserWorkLog = new MultiOrgSyncUserWorkLog();
        multiOrgSyncUserWorkLog.setLogId(logId);
        multiOrgSyncUserWorkLog.setSyncStatus(DingtalkInfo.SYNC_STATUS_ERROR);
        return dao.countByEntity(multiOrgSyncUserWorkLog) > 0;
    }
}
