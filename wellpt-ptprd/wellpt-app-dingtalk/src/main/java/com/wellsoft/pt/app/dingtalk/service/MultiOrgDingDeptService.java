package com.wellsoft.pt.app.dingtalk.service;

import com.wellsoft.pt.app.dingtalk.dao.MultiOrgDingDeptDao;
import com.wellsoft.pt.app.dingtalk.entity.MultiOrgDingDept;
import com.wellsoft.pt.jpa.service.JpaService;
import com.wellsoft.pt.multi.org.bean.OrgTreeNodeDto;
import net.sf.json.JSONObject;

import java.util.List;

/**
 * Description: 钉钉部门信息service
 *
 * @author bryanlin
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2020年5月18日.1	bryanlin		2020年5月18日  	Create
 *          </pre>
 * @date 2020年5月18日
 */
@Deprecated
public interface MultiOrgDingDeptService extends JpaService<MultiOrgDingDept, MultiOrgDingDeptDao, String> {

    /**
     * 同步钉钉部门
     *
     * @param deptObj
     * @param order
     * @param includeChildNode
     * @param logId
     */
    public void saveAndUpdateDeptFromDingtalk(JSONObject deptObj, int order, boolean includeChildNode, String logId);

    /**
     * 同步钉钉部门
     *
     * @param deptObj
     * @param order
     * @param includeChildNode
     */
    public void saveAndUpdateDeptFromDingtalk(JSONObject deptObj, int order, boolean includeChildNode);

    /**
     * 删除钉钉部门
     *
     * @param deptId
     */
    public void deleteDeptFromDingtalk(String deptId);

    /**
     * 删除钉钉部门
     *
     * @param deptId
     * @param logId  同步的批次ID
     */
    public void deleteDeptFromDingtalk(String deptId, String logId);

    public MultiOrgDingDept getByDingDeptId(String dingDeptId);

    public MultiOrgDingDept getByPtDeptId(String ptDeptId);

    public OrgTreeNodeDto getNodeDtoByEleId(String eleId, String orgVersionId);

    /**
     * 根据组织版本ID、系统单位ID、父ID获取钉钉部门列表
     *
     * @param ptDeptId
     * @return
     */
    public List<MultiOrgDingDept> getDingDepts(String ptDeptId);

    /**
     * 根据组织版本ID、系统单位ID 获取钉钉部门列表
     *
     * @return
     */
    public List<MultiOrgDingDept> getAllDingDepts();
}
