package com.wellsoft.pt.app.dingtalk.service.impl;

import com.google.common.collect.Maps;
import com.wellsoft.context.enums.IdPrefix;
import com.wellsoft.pt.app.dingtalk.constants.DingtalkInfo;
import com.wellsoft.pt.app.dingtalk.dao.MultiOrgDingDeptDao;
import com.wellsoft.pt.app.dingtalk.entity.MultiOrgDingDept;
import com.wellsoft.pt.app.dingtalk.entity.MultiOrgSyncDeptLog;
import com.wellsoft.pt.app.dingtalk.service.MultiOrgDingDeptService;
import com.wellsoft.pt.app.dingtalk.service.MultiOrgSyncDeptLogService;
import com.wellsoft.pt.app.dingtalk.support.DingtalkConfig;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.multi.org.bean.OrgTreeNodeDto;
import com.wellsoft.pt.multi.org.bean.OrgTreeNodeVo;
import com.wellsoft.pt.multi.org.bean.OrgUserTreeNodeDto;
import com.wellsoft.pt.multi.org.entity.MultiOrgTreeNode;
import com.wellsoft.pt.multi.org.facade.service.MultiOrgApiFacade;
import com.wellsoft.pt.multi.org.facade.service.MultiOrgService;
import com.wellsoft.pt.multi.org.facade.service.OrgApiFacade;
import net.sf.json.JSONObject;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * Description: 钉钉部门信息service实现类
 *
 * @author bryanlin
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2020年5月18日.1	bryanlin		2020年5月18日		Create
 *          </pre>
 * @date 2020年5月18日
 */
@Service
@Deprecated
public class MultiOrgDingDeptServiceImpl extends AbstractJpaServiceImpl<MultiOrgDingDept, MultiOrgDingDeptDao, String>
        implements MultiOrgDingDeptService {

    @Autowired
    private OrgApiFacade orgApiFacade;

    @Autowired
    private DingtalkConfig dingtalkConfig;

    @Autowired
    private MultiOrgService multiOrgService;

    @Autowired
    private MultiOrgApiFacade multiOrgApiFacade;

    @Autowired
    private MultiOrgSyncDeptLogService multiOrgSyncDeptLogService;

    @Override
    @Transactional
    public void saveAndUpdateDeptFromDingtalk(JSONObject deptObj, int order, boolean includeChildNode) {
        if (deptObj != null) {
            String deptId = deptObj.optString("id");
            String parentid = deptObj.optString("parentid");
            String deptExt = deptObj.optString("ext");
            String deptName = deptObj.getString("name"); // 部门名称
            String deptOrder = deptObj.optString("order", order + ""); // 部门排序
            boolean createDeptGroup = deptObj.optBoolean("createDeptGroup");
            boolean autoAddUser = deptObj.optBoolean("autoAddUser");
            boolean isRoot = deptObj.optBoolean("isRoot");
            String parentEleIdPath = null, parentEleNamePath = null;
            if (StringUtils.isBlank(parentid) || StringUtils.equals("1", deptId) || isRoot) {// 不为空，且不为钉钉根节点
                parentEleIdPath = dingtalkConfig.getOrgVersionId();//dingtalkConfig.getOrgVersionId();// + "/" + DingtalkInfo.SELF_BUSINESS_UNIT_ID; // 默认
                parentEleNamePath = dingtalkConfig.getOrgVersionName();// + "/" + deptName; // 默认
            } else {
                // 父节点必须存在
                MultiOrgDingDept multiParentOrgDingDept = getByDingDeptId(parentid);
                OrgTreeNodeDto orgTreeNodeDto = getNodeDtoByEleId(multiParentOrgDingDept.getEleId(),
                        multiParentOrgDingDept.getOrgVersionId());
                parentEleIdPath = orgTreeNodeDto.getEleIdPath();
                parentEleNamePath = orgTreeNodeDto.getEleNamePath();
            }
            // 新增钉钉部门信息
            MultiOrgDingDept multiOrgDingDept = getByDingDeptId(deptId);
            OrgTreeNodeVo orgNodeVo = null;
            if (null != multiOrgDingDept && StringUtils.isNotBlank(multiOrgDingDept.getEleId())) {
                MultiOrgTreeNode multiOrgTreeNode = getNodeDtoByEleId(multiOrgDingDept.getEleId(),
                        multiOrgDingDept.getOrgVersionId());
                orgNodeVo = multiOrgService.getOrgNodeByTreeUuid(multiOrgTreeNode.getUuid());
            } else {
                // 挂在业务单位或部门
                List<OrgTreeNodeDto> list = orgApiFacade.queryAllNodeOfOrgVersionByEleIdPath(
                        dingtalkConfig.getOrgVersionId(), parentEleIdPath, IdPrefix.BUSINESS_UNIT.getValue());
                list.addAll(orgApiFacade.queryAllNodeOfOrgVersionByEleIdPath(dingtalkConfig.getOrgVersionId(),
                        parentEleIdPath, IdPrefix.DEPARTMENT.getValue()));
                // 过滤部门下一级部门
                for (OrgTreeNodeDto deptNode : list) {
                    if (StringUtils.endsWith(deptNode.getParentIdPath(), parentEleIdPath)
                            && StringUtils.equals(deptName, deptNode.getName())) {
                        if (null != getByPtDeptId(deptNode.getEleId())) {
                            // 忽略已关联部门
                            continue;
                        }
                        orgNodeVo = multiOrgService.getOrgNodeByTreeUuid(deptNode.getUuid());
                        break;
                    }
                }
            }
            if (orgNodeVo == null) {
                // 构造部门VO
                orgNodeVo = new OrgTreeNodeVo();
                orgNodeVo.setType(IdPrefix.DEPARTMENT.getValue()); // 类型
                orgNodeVo.setSystemUnitId(dingtalkConfig.getSystemUnitId());
            }
            String oldEleIdPath = orgNodeVo.getEleIdPath();
            String oldOrgVersionId = orgNodeVo.getOrgVersionId();
            orgNodeVo.setName(deptName);
            orgNodeVo.setShortName(deptName);
            orgNodeVo.setCode(deptOrder);
            orgNodeVo.setParentEleIdPath(parentEleIdPath);
            orgNodeVo.setParentEleNamePath(parentEleNamePath);
            orgNodeVo.setOrgVersionId(dingtalkConfig.getOrgVersionId());
            String eleId = orgNodeVo.getEleId();
            if (StringUtils.isBlank(orgNodeVo.getUuid())) {
                MultiOrgTreeNode orgTreeNode = multiOrgService.addOrgChildNode(orgNodeVo);
                eleId = orgTreeNode.getEleId();
            } else {
                multiOrgService.modifyOrgChildNode(orgNodeVo, false, includeChildNode);
                List<OrgTreeNodeDto> list = orgApiFacade.queryAllNodeOfOrgVersionByEleIdPath(
                        oldOrgVersionId, oldEleIdPath, IdPrefix.JOB.getValue());
                // 过滤部门下一级职位(同步职位)
                for (OrgTreeNodeDto jobNode : list) {
                    if (StringUtils.equals(jobNode.getSystemUnitId(), dingtalkConfig.getSystemUnitId())
                            && StringUtils.equals(jobNode.getParentIdPath(), oldEleIdPath)) {
                        OrgTreeNodeVo orgJobNodeVo = multiOrgService.getOrgNodeByTreeUuid(jobNode.getUuid());
                        orgJobNodeVo.setParentEleIdPath(orgNodeVo.getEleIdPath());
                        orgJobNodeVo.setParentEleNamePath(orgNodeVo.getEleNamePath());
                        orgJobNodeVo.setOrgVersionId(dingtalkConfig.getOrgVersionId());
                        multiOrgService.modifyOrgChildNode(orgJobNodeVo, false, includeChildNode);
                    }
                }
            }
            if (multiOrgDingDept == null) {
                multiOrgDingDept = new MultiOrgDingDept();
                // 新增钉钉部门信息
                multiOrgDingDept.setId(deptId);
                multiOrgDingDept.setEleId(eleId);
                multiOrgDingDept.setSystemUnitId(dingtalkConfig.getSystemUnitId());
            }
            multiOrgDingDept.setName(deptName);
            multiOrgDingDept.setParentId(parentid);
            // 钉钉接入改造：只同步部门的名称、父级ID
            multiOrgDingDept.setExt(deptExt);
            multiOrgDingDept.setCreateDeptGroup(createDeptGroup ? 1 : 0);
            multiOrgDingDept.setAutoAddUser(autoAddUser ? 1 : 0);
            multiOrgDingDept.setOrgVersionId(dingtalkConfig.getOrgVersionId());
            dao.save(multiOrgDingDept);
        }
    }

    @Override
    @Transactional
    public void saveAndUpdateDeptFromDingtalk(JSONObject deptObj, int order, boolean includeChildNode, String logId) {
        if (deptObj != null) {
            String deptId = deptObj.optString("id");
            String parentid = deptObj.optString("parentid");
            String deptName = deptObj.optString("name"); // 部门名称
            String deptOrder = deptObj.optString("order", order + ""); // 部门排序
            boolean isRoot = deptObj.optBoolean("isRoot");
            String parentEleIdPath = null, parentEleNamePath = null;
            if (StringUtils.isBlank(parentid) || StringUtils.equals("1", deptId) || isRoot) {// 不为空，且不为钉钉根节点
                parentEleIdPath = dingtalkConfig.getOrgVersionId();//dingtalkConfig.getOrgVersionId();// + "/" + DingtalkInfo.SELF_BUSINESS_UNIT_ID; // 默认
                parentEleNamePath = dingtalkConfig.getOrgVersionName();// + "/" + deptName; // 默认
            } else {
                // 父节点必须存在
                MultiOrgDingDept multiParentOrgDingDept = getByDingDeptId(parentid);
                OrgTreeNodeDto orgTreeNodeDto = getNodeDtoByEleId(multiParentOrgDingDept.getEleId(),
                        multiParentOrgDingDept.getOrgVersionId());
                parentEleIdPath = orgTreeNodeDto.getEleIdPath();
                parentEleNamePath = orgTreeNodeDto.getEleNamePath();
            }
            // 新增钉钉部门信息
            MultiOrgDingDept multiOrgDingDept = getByDingDeptId(deptId);
            OrgTreeNodeVo orgNodeVo = null;
            if (null != multiOrgDingDept && StringUtils.isNotBlank(multiOrgDingDept.getEleId())) {
                MultiOrgTreeNode multiOrgTreeNode = getNodeDtoByEleId(multiOrgDingDept.getEleId(),
                        multiOrgDingDept.getOrgVersionId());
                if (null != multiOrgTreeNode) {
                    orgNodeVo = multiOrgService.getOrgNodeByTreeUuid(multiOrgTreeNode.getUuid());
                }
            } else {
                // 挂在业务单位或部门
                List<OrgTreeNodeDto> list = orgApiFacade.queryAllNodeOfOrgVersionByEleIdPath(
                        dingtalkConfig.getOrgVersionId(), parentEleIdPath, IdPrefix.BUSINESS_UNIT.getValue());
                list.addAll(orgApiFacade.queryAllNodeOfOrgVersionByEleIdPath(dingtalkConfig.getOrgVersionId(),
                        parentEleIdPath, IdPrefix.DEPARTMENT.getValue()));
                // 过滤部门下一级部门
                for (OrgTreeNodeDto deptNode : list) {
                    if (StringUtils.endsWith(deptNode.getParentIdPath(), parentEleIdPath)
                            && StringUtils.equals(deptName, deptNode.getName())) {
                        if (null != getByPtDeptId(deptNode.getEleId())) {
                            // 忽略已关联部门
                            continue;
                        }
                        orgNodeVo = multiOrgService.getOrgNodeByTreeUuid(deptNode.getUuid());
                        break;
                    }
                }
            }
            if (orgNodeVo == null) {
                // 构造部门VO
                orgNodeVo = new OrgTreeNodeVo();
                orgNodeVo.setType(IdPrefix.DEPARTMENT.getValue()); // 类型
                orgNodeVo.setSystemUnitId(dingtalkConfig.getSystemUnitId());
            }
            String oldEleIdPath = orgNodeVo.getEleIdPath();
            String oldOrgVersionId = orgNodeVo.getOrgVersionId();
            orgNodeVo.setName(deptName);
            orgNodeVo.setShortName(deptName);
            orgNodeVo.setCode(deptOrder);
            orgNodeVo.setParentEleIdPath(parentEleIdPath);
            orgNodeVo.setParentEleNamePath(parentEleNamePath);
            orgNodeVo.setOrgVersionId(dingtalkConfig.getOrgVersionId());
            String eleId = orgNodeVo.getEleId();
            if (StringUtils.isBlank(orgNodeVo.getUuid())) {
                MultiOrgTreeNode orgTreeNode = multiOrgService.addOrgChildNode(orgNodeVo);
                eleId = orgTreeNode.getEleId();
            } else {
                multiOrgService.modifyOrgChildNode(orgNodeVo, false, includeChildNode);
                List<OrgTreeNodeDto> list = orgApiFacade.queryAllNodeOfOrgVersionByEleIdPath(
                        oldOrgVersionId, oldEleIdPath, IdPrefix.JOB.getValue());
                // 过滤部门下一级职位(同步职位)
                for (OrgTreeNodeDto jobNode : list) {
                    if (StringUtils.equals(jobNode.getSystemUnitId(), dingtalkConfig.getSystemUnitId())
                            && StringUtils.equals(jobNode.getParentIdPath(), oldEleIdPath)) {
                        OrgTreeNodeVo orgJobNodeVo = multiOrgService.getOrgNodeByTreeUuid(jobNode.getUuid());
                        orgJobNodeVo.setParentEleIdPath(orgNodeVo.getEleIdPath());
                        orgJobNodeVo.setParentEleNamePath(orgNodeVo.getEleNamePath());
                        orgJobNodeVo.setOrgVersionId(dingtalkConfig.getOrgVersionId());
                        multiOrgService.modifyOrgChildNode(orgJobNodeVo, false, includeChildNode);
                    }
                }
            }
            String operationName = DingtalkInfo.SYNC_OPERATION_MOD;
            if (multiOrgDingDept == null) {
                multiOrgDingDept = new MultiOrgDingDept();
                // 新增钉钉部门信息
                multiOrgDingDept.setId(deptId);
                multiOrgDingDept.setEleId(eleId);
                operationName = DingtalkInfo.SYNC_OPERATION_ADD;
            }
            multiOrgDingDept.setName(deptName);
            multiOrgDingDept.setParentId(parentid);
            multiOrgDingDept.setSystemUnitId(dingtalkConfig.getSystemUnitId());
            multiOrgDingDept.setOrgVersionId(dingtalkConfig.getOrgVersionId());
            dao.save(multiOrgDingDept);

            // 组织同步，添加组织同步日志；业务事件同步，添加业务事件同步日志
            saveMultiOrgSyncDeptLog(logId, operationName, multiOrgDingDept, DingtalkInfo.SYNC_STATUS_SUCCESS, StringUtils.EMPTY);
        }
    }

    /**
     * 保存组织同步-部门同步的详细日志
     *
     * @param logId            同步批次ID
     * @param operationName    同步操作
     * @param multiOrgDingDept 钉钉部门信息
     * @param syncStatus       同步状态
     * @param remark           同步状态为异常时的异常信息
     */
    private void saveMultiOrgSyncDeptLog(String logId, String operationName, MultiOrgDingDept multiOrgDingDept, Integer syncStatus, String remark) {
        MultiOrgSyncDeptLog log = new MultiOrgSyncDeptLog();
        log.setLogId(logId);
        log.setDeptId(multiOrgDingDept.getId());
        log.setDeptName(multiOrgDingDept.getName());
        log.setOperationName(operationName);
        log.setDeptParentId(multiOrgDingDept.getParentId());

        if (StringUtils.isNotBlank(multiOrgDingDept.getParentId())) {
            MultiOrgDingDept parent = this.getByDingDeptId(multiOrgDingDept.getParentId());
            log.setDeptParentName(parent.getName());
        }

        log.setSyncStatus(syncStatus);
        log.setRemark(remark);

        multiOrgSyncDeptLogService.save(log);
    }

    @Override
    @Transactional
    public void deleteDeptFromDingtalk(String deptId) {
        MultiOrgDingDept dingDept = getByDingDeptId(deptId); // 获取当前节点信息
        if (dingDept != null) {
            MultiOrgTreeNode multiOrgTreeNode = getNodeDtoByEleId(dingDept.getEleId(), dingDept.getOrgVersionId());
            if (null != multiOrgTreeNode && StringUtils.isNotBlank(multiOrgTreeNode.getUuid())) {
                List<OrgTreeNodeDto> list = orgApiFacade.queryAllNodeOfOrgVersionByEleIdPath(
                        dingDept.getOrgVersionId(), multiOrgTreeNode.getEleIdPath());
                for (OrgTreeNodeDto treeNode : list) {
                    if (StringUtils.equals(multiOrgTreeNode.getUuid(), treeNode.getUuid())) {
                        continue;// 忽略本节点
                    }
                    // 递归删除子节点
                    List<OrgUserTreeNodeDto> userNodes = multiOrgApiFacade.queryUserByOrgTreeNode(treeNode.getEleId(),
                            dingDept.getOrgVersionId());
                    if (CollectionUtils.isNotEmpty(userNodes)) {
//						throw new RuntimeException("部门下存在人员");
                        continue;
                    }
                    multiOrgService.deleteOrgChildNode(treeNode.getUuid());
                }
                dao.flushSession();
                multiOrgService.deleteOrgChildNode(multiOrgTreeNode.getUuid());
            }
            dao.delete(dingDept);
        }
    }

    @Override
    @Transactional
    public void deleteDeptFromDingtalk(String deptId, String logId) {
        MultiOrgDingDept dingDept = getByDingDeptId(deptId); // 获取当前节点信息

        if (dingDept != null) {
            MultiOrgTreeNode multiOrgTreeNode = getNodeDtoByEleId(dingDept.getEleId(), dingDept.getOrgVersionId());
            if (null != multiOrgTreeNode && StringUtils.isNotBlank(multiOrgTreeNode.getUuid())) {
                List<OrgTreeNodeDto> list = orgApiFacade.queryAllNodeOfOrgVersionByEleIdPath(
                        dingDept.getOrgVersionId(), multiOrgTreeNode.getEleIdPath());
                for (OrgTreeNodeDto treeNode : list) {
                    if (StringUtils.equals(multiOrgTreeNode.getUuid(), treeNode.getUuid())) {
                        continue;// 忽略本节点
                    }
                    // 递归删除子节点
                    List<OrgUserTreeNodeDto> userNodes = multiOrgApiFacade.queryUserByOrgTreeNode(treeNode.getEleId(),
                            dingDept.getOrgVersionId());
                    if (CollectionUtils.isNotEmpty(userNodes)) {
//						throw new RuntimeException("部门下存在人员");
                        saveMultiOrgSyncDeptLog(logId, DingtalkInfo.SYNC_OPERATION_DEL, dingDept, DingtalkInfo.SYNC_STATUS_ERROR, DingtalkInfo.SYNC_DEPT_ERROR_DEPT_EXISTS_USER);
                        // 部门下有人，删不了，直接return
                        return;
                    }
                    multiOrgService.deleteOrgChildNode(treeNode.getUuid());
                }
                dao.flushSession();
                multiOrgService.deleteOrgChildNode(multiOrgTreeNode.getUuid());
            }
            dao.delete(dingDept);
            MultiOrgSyncDeptLog log = new MultiOrgSyncDeptLog();
            log.setLogId(logId);
            log.setSyncStatus(DingtalkInfo.SYNC_STATUS_ERROR);
            List<MultiOrgSyncDeptLog> logs = multiOrgSyncDeptLogService.listByEntity(log);

            if (CollectionUtils.isNotEmpty(logs)) {
                saveMultiOrgSyncDeptLog(logId, DingtalkInfo.SYNC_OPERATION_DEL, dingDept, DingtalkInfo.SYNC_STATUS_ERROR, StringUtils.EMPTY);
            } else {
                saveMultiOrgSyncDeptLog(logId, DingtalkInfo.SYNC_OPERATION_DEL, dingDept, DingtalkInfo.SYNC_STATUS_SUCCESS, StringUtils.EMPTY);
            }
        }
    }

    @Override
    public OrgTreeNodeDto getNodeDtoByEleId(String eleId, String orgVersionId) {
        OrgTreeNodeDto orgTreeNodeDto = multiOrgService.getNodeByEleIdAndOrgVersion(eleId,
                dingtalkConfig.getOrgVersionId());
        if (null == orgTreeNodeDto && StringUtils.isNotBlank(orgVersionId)) {
            orgTreeNodeDto = multiOrgService.getNodeByEleIdAndOrgVersion(eleId, orgVersionId);
        }
        return orgTreeNodeDto;
    }

    @Override
    public MultiOrgDingDept getByPtDeptId(String ptDeptId) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("ptDeptId", ptDeptId);
        // params.put("orgVersionId", dingtalkConfig.getOrgVersionId());
        // and t.orgVersionId = :orgVersionId
        List<MultiOrgDingDept> list = listByHQL("from MultiOrgDingDept t where t.eleId = :ptDeptId", params);
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        return list.get(0);
    }

    @Override
    public MultiOrgDingDept getByDingDeptId(String dingDeptId) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("dingDeptId", dingDeptId);
        params.put("systemUnitId", dingtalkConfig.getSystemUnitId());
        params.put("orgVersionId", dingtalkConfig.getOrgVersionId());
        String hql = "from MultiOrgDingDept t where t.id = :dingDeptId and t.orgVersionId = :orgVersionId and exists (select 1 from MultiOrgElement tt where tt.id = t.eleId and tt.systemUnitId = :systemUnitId)";
        List<MultiOrgDingDept> list = listByHQL(hql, params);
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        return list.get(0);
    }

    @Override
    public List<MultiOrgDingDept> getDingDepts(String ptDeptId) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("orgVersionId", dingtalkConfig.getOrgVersionId());
        params.put("systemUnitId", dingtalkConfig.getSystemUnitId());
        String hql = "from MultiOrgDingDept t where t.orgVersionId = :orgVersionId and t.systemUnitId = :systemUnitId and id is not null ";

        if (StringUtils.isNotBlank(ptDeptId)) {
            params.put("ptDeptId", ptDeptId);
            hql += " and parentId = :ptDeptId ";
        } else {
            hql += " and parentId is null ";
        }

        return listByHQL(hql, params);
    }

    @Override
    public List<MultiOrgDingDept> getAllDingDepts() {
        MultiOrgDingDept dept = new MultiOrgDingDept();
        dept.setSystemUnitId(dingtalkConfig.getSystemUnitId());
        dept.setOrgVersionId(dingtalkConfig.getOrgVersionId());
        return dao.listByEntity(dept);
    }
}
