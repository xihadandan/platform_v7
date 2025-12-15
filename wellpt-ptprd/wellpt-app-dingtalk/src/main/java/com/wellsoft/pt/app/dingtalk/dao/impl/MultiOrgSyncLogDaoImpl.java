package com.wellsoft.pt.app.dingtalk.dao.impl;

import com.wellsoft.pt.app.dingtalk.dao.MultiOrgSyncLogDao;
import com.wellsoft.pt.app.dingtalk.entity.MultiOrgSyncLog;
import com.wellsoft.pt.jpa.dao.impl.AbstractJpaDaoImpl;
import org.springframework.stereotype.Repository;

/**
 * Description: 多部门人员审核dao实现类
 *
 * @author liuyz
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2021-08-16	liuyz		2021-08-16		Create
 * </pre>
 * @date 2021-08-16
 */
@Repository
@Deprecated
public class MultiOrgSyncLogDaoImpl extends AbstractJpaDaoImpl<MultiOrgSyncLog, String> implements MultiOrgSyncLogDao {
}
