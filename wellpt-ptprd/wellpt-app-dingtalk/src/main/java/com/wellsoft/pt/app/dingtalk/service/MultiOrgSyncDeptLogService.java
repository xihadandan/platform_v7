package com.wellsoft.pt.app.dingtalk.service;

import com.wellsoft.pt.app.dingtalk.dao.MultiOrgSyncDeptLogDao;
import com.wellsoft.pt.app.dingtalk.entity.MultiOrgSyncDeptLog;
import com.wellsoft.pt.jpa.service.JpaService;

/**
 * Description: 组织同步部门同步日志Service
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
public interface MultiOrgSyncDeptLogService extends JpaService<MultiOrgSyncDeptLog, MultiOrgSyncDeptLogDao, String> {

    /**
     * 判断 是否存在 同步异常的数据
     *
     * @param logId
     * @return
     */
    public boolean isExistsErrorData(String logId);
}
