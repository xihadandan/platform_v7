package com.wellsoft.pt.app.dingtalk.service;

import com.wellsoft.pt.app.dingtalk.dao.MultiOrgSyncUserWorkLogDao;
import com.wellsoft.pt.app.dingtalk.entity.MultiOrgSyncUserWorkLog;
import com.wellsoft.pt.jpa.service.JpaService;

/**
 * Description: 组织同步人员职位关系同步日志Service
 *
 * @author liuyz
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2021年8月16日.1	liuyz		2021年8月16日		Create
 * </pre>
 * @date 2021年8月16日
 */
@Deprecated
public interface MultiOrgSyncUserWorkLogService extends JpaService<MultiOrgSyncUserWorkLog, MultiOrgSyncUserWorkLogDao, String> {

    public boolean isExistsErrorData(String logId);

}
