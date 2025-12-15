package com.wellsoft.pt.app.dingtalk.service;

import com.wellsoft.pt.app.dingtalk.dao.MultiDeptUserAuditDetailDao;
import com.wellsoft.pt.app.dingtalk.entity.MultiDeptUserAuditDetail;
import com.wellsoft.pt.jpa.service.JpaService;

/**
 * Description: 多部门人员审核详情service
 *
 * @author liuyz
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2021/11/15.1	liuyz		2021/11/15		Create
 * </pre>
 * @date 2021/11/15
 */
@Deprecated
public interface MultiDeptUserAuditDetailService extends JpaService<MultiDeptUserAuditDetail, MultiDeptUserAuditDetailDao, String> {
}
