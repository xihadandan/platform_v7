package com.wellsoft.pt.app.dingtalk.service.impl;

import com.wellsoft.pt.app.dingtalk.constants.DingtalkInfo;
import com.wellsoft.pt.app.dingtalk.dao.MultiOrgSyncDeptLogDao;
import com.wellsoft.pt.app.dingtalk.entity.MultiOrgSyncDeptLog;
import com.wellsoft.pt.app.dingtalk.service.MultiOrgSyncDeptLogService;
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
public class MultiOrgSyncDeptLogServiceImpl extends AbstractJpaServiceImpl<MultiOrgSyncDeptLog, MultiOrgSyncDeptLogDao, String> implements MultiOrgSyncDeptLogService {


    @Override
    public boolean isExistsErrorData(String logId) {
        MultiOrgSyncDeptLog log = new MultiOrgSyncDeptLog();
        log.setLogId(logId);
        log.setSyncStatus(DingtalkInfo.SYNC_STATUS_ERROR);
        return this.dao.countByEntity(log) > 0;
    }
}
