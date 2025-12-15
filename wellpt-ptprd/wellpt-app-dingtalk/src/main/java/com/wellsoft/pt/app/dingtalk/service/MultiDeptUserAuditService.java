package com.wellsoft.pt.app.dingtalk.service;

import com.wellsoft.pt.app.dingtalk.dao.MultiDeptUserAuditDao;
import com.wellsoft.pt.app.dingtalk.entity.MultiDeptUserAudit;
import com.wellsoft.pt.app.dingtalk.vo.MultiDeptUserAuditVo;
import com.wellsoft.pt.jpa.service.JpaService;

import java.util.List;

/**
 * Description: 多部门人员审核service
 *
 * @author liuyz
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2021年8月12日.1	liuyz		2021年8月12日  	Create
 *          </pre>
 * @date 2021年8月12日
 */
@Deprecated
public interface MultiDeptUserAuditService extends JpaService<MultiDeptUserAudit, MultiDeptUserAuditDao, String> {

    /**
     * 根据多部门人员审核的uuid获取记录，用于审核和查看详细
     *
     * @param uuid
     * @return
     */
    public MultiDeptUserAudit getByUuid(String uuid);

    /**
     * 获取多部门人员审核列表
     *
     * @return
     */
    public List<MultiDeptUserAudit> query(MultiDeptUserAudit multiDeptUserAudit);

    /**
     * 多部门人员审核
     *
     * @param multiDeptUserAuditVo
     */
    public void auditRecord(MultiDeptUserAuditVo multiDeptUserAuditVo);

    public MultiDeptUserAudit getByEntity(MultiDeptUserAudit multiDeptUserAudit);

    /**
     * 获取多部门人员审核详情
     *
     * @param uuid
     * @return
     */
    public MultiDeptUserAuditVo getMultiDeptUserAuditDetail(String uuid);

    /**
     * 获取钉钉职位的下拉列表
     *
     * @return
     */
    public List<String> getDingJobList();

}
