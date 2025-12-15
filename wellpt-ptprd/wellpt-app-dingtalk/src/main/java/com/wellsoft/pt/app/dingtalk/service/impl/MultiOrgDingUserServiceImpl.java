package com.wellsoft.pt.app.dingtalk.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wellsoft.context.enums.IdPrefix;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.http.HttpUtil;
import com.wellsoft.pt.app.dingtalk.constants.DingtalkInfo;
import com.wellsoft.pt.app.dingtalk.dao.MultiOrgDingUserDao;
import com.wellsoft.pt.app.dingtalk.entity.*;
import com.wellsoft.pt.app.dingtalk.facade.DingtalkOrgSyncApi;
import com.wellsoft.pt.app.dingtalk.service.*;
import com.wellsoft.pt.app.dingtalk.support.DingtalkConfig;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.multi.org.bean.OrgTreeNodeDto;
import com.wellsoft.pt.multi.org.bean.OrgTreeNodeVo;
import com.wellsoft.pt.multi.org.bean.OrgUserVo;
import com.wellsoft.pt.multi.org.entity.MultiOrgTreeNode;
import com.wellsoft.pt.multi.org.entity.MultiOrgUserAccount;
import com.wellsoft.pt.multi.org.facade.service.MultiOrgService;
import com.wellsoft.pt.multi.org.facade.service.MultiOrgUserAccountFacadeService;
import com.wellsoft.pt.multi.org.facade.service.OrgApiFacade;
import com.wellsoft.pt.repository.entity.mongo.file.MongoFileEntity;
import com.wellsoft.pt.repository.service.MongoFileService;
import com.wellsoft.pt.security.audit.entity.Role;
import com.wellsoft.pt.security.audit.facade.service.RoleFacadeService;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.*;

/**
 * Description: 钉钉用戶信息service实现类
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
public class MultiOrgDingUserServiceImpl extends AbstractJpaServiceImpl<MultiOrgDingUser, MultiOrgDingUserDao, String>
        implements MultiOrgDingUserService {

    @Autowired
    private OrgApiFacade orgApiFacade;

    @Autowired
    private DingtalkConfig dingtalkConfig;

    @Autowired
    private MultiOrgService multiOrgService;

    @Autowired
    private RoleFacadeService roleFacadeService;

    @Autowired
    private DingtalkOrgSyncApi dingtalkOrgSyncApi;

    @Autowired
    private MultiOrgDingRoleService multiOrgDingRoleService;

    @Autowired
    private MultiOrgDingDeptService multiOrgDingDeptService;

    @Autowired
    private MultiOrgUserAccountFacadeService multiOrgUserAccountService;

    @Autowired
    private MultiOrgSyncUserLogService multiOrgSyncUserLogService;

    @Autowired
    private MultiOrgSyncUserWorkLogService multiOrgSyncUserWorkLogService;

    @Autowired
    private MultiDeptUserAuditService multiDeptUserAuditService;

    @Autowired
    private MongoFileService mongoFileService;

    /**
     * 添加角色
     *
     * @param orgUserVo
     * @param roles
     * @return
     */
    private void addRolesFromDingtalk(OrgUserVo orgUserVo, JSONArray roles) {
        if (CollectionUtils.isEmpty(roles)) {
            return;
        }
        String sep = Separator.SEMICOLON.getValue();
        List<String> roleUuids = Lists.newArrayList();
        if (StringUtils.isNotBlank(orgUserVo.getRoleUuids())) {
            // 保留旧(平台)的角色信息,钉钉角色由钉钉管理
            String[] roleUuids2 = StringUtils.split(orgUserVo.getRoleUuids(), sep);
            for (String roleUuid : roleUuids2) {
                Role role = roleFacadeService.get(roleUuid);
                if (null != role && null == multiOrgDingRoleService.getByRoleId(role.getId())) {
                    roleUuids.add(roleUuid);
                }
            }
        }
        for (int i = 0; i < roles.size(); i++) {
            Role roleEntity = null;
            JSONObject role = roles.getJSONObject(i);
            String roleId = role.getString("id");
            String roleName = role.getString("name");
            MultiOrgDingRole multiOrgDingRole = multiOrgDingRoleService.getByDingRoleId(roleId);
            if (null != multiOrgDingRole && StringUtils.isNotBlank(multiOrgDingRole.getRoleId())) {
                roleEntity = roleFacadeService.getRoleById(multiOrgDingRole.getRoleId());
            }
            roleEntity = roleEntity == null ? roleFacadeService.getByName(roleName) : roleEntity;
            if (null == roleEntity) {
                roleEntity = new Role();
                roleEntity.setName(roleName);
                roleEntity.setCode(roleId);
                roleEntity.setId("ROLE_DINGTALK_" + roleId);
                roleEntity.setSystemUnitId(dingtalkConfig.getSystemUnitId());
                roleFacadeService.save(roleEntity);
            }
            if (false == roleUuids.contains(roleEntity.getUuid())) {
                roleUuids.add(roleEntity.getUuid());
            }
        }
        orgUserVo.setRoleUuids(StringUtils.join(roleUuids, sep));
    }

    /**
     * 添加职位
     *
     * @param departments
     * @param position
     * @return
     */
    private void addJobFromDingtalk(OrgUserVo orgUserVo, JSONArray departments, String position) {
        if (CollectionUtils.isEmpty(departments) || StringUtils.isBlank(position)) {
            return;
        }
        List<String> jobIds = Lists.newArrayList(), jobNames = Lists.newArrayList(), jobIdPaths = Lists.newArrayList();
        int deptLength = departments.size();
        for (int i = 0; i < deptLength; i++) {
            String deptId = departments.optString(i), eleId;
            MultiOrgDingDept multiOrgDingDept = multiOrgDingDeptService.getByDingDeptId(deptId);
            if (multiOrgDingDept == null || StringUtils.isBlank(eleId = multiOrgDingDept.getEleId())) {
                continue;// 部门未同步，忽略
            }
            OrgTreeNodeDto orgTreeNodeDto = multiOrgDingDeptService.getNodeDtoByEleId(eleId,
                    multiOrgDingDept.getOrgVersionId());
            if (orgTreeNodeDto == null) {
                continue;// 部门未挂载到组织树中
            }
            OrgTreeNodeDto mainJobNode = null;
            List<OrgTreeNodeDto> list = orgApiFacade.queryAllNodeOfOrgVersionByEleIdPath(
                    multiOrgDingDept.getOrgVersionId(), orgTreeNodeDto.getEleIdPath(), IdPrefix.JOB.getValue());
            // 过滤部门下一级职位
            for (OrgTreeNodeDto jobNode : list) {
                if (StringUtils.equals(jobNode.getSystemUnitId(), dingtalkConfig.getSystemUnitId())
                        && StringUtils.equals(jobNode.getParentIdPath(), orgTreeNodeDto.getEleIdPath())
                        && StringUtils.equals(position, jobNode.getName())) {
                    mainJobNode = jobNode;
                    break;
                }
            }
            if (mainJobNode == null) {
                // 构造职位VO
                OrgTreeNodeVo nodevo = new OrgTreeNodeVo();
                nodevo.setName(position);
                nodevo.setShortName(position);
                nodevo.setCode("JOB_DINGTALK_" + RandomStringUtils.randomNumeric(3));
                nodevo.setType(IdPrefix.JOB.getValue()); // 类型
                nodevo.setParentEleIdPath(orgTreeNodeDto.getEleIdPath());
                nodevo.setParentEleNamePath(orgTreeNodeDto.getEleNamePath());
                nodevo.setOrgVersionId(dingtalkConfig.getOrgVersionId());
                nodevo.setSystemUnitId(dingtalkConfig.getSystemUnitId());
                MultiOrgTreeNode node = multiOrgService.addOrgChildNode(nodevo);
                jobIds.add(dingtalkConfig.getOrgVersionId() + MultiOrgService.PATH_SPLIT_SYSMBOL + node.getEleId());
                jobNames.add(position);
                jobIdPaths.add(node.getEleIdPath());
                //jobNamePaths.add(orgTreeNodeDto.getEleNamePath() + MultiOrgService.PATH_SPLIT_SYSMBOL + position);
            } else {
                jobIds.add(mainJobNode.getOrgVersionId() + MultiOrgService.PATH_SPLIT_SYSMBOL + mainJobNode.getEleId());
                jobNames.add(mainJobNode.getName());
                jobIdPaths.add(mainJobNode.getEleIdPath());
                //jobNamePaths.add(mainJobNode.getEleNamePath());
            }
        }
        if (false == jobIds.isEmpty()) {
            orgUserVo.setMainJobId(jobIds.remove(0));
            orgUserVo.setMainJobName(jobNames.remove(0));
            orgUserVo.setMainJobIdPath(jobIdPaths.remove(0));
            //orgUserVo.setMainJobNamePath(jobNamePaths.remove(0));
        }
        if (false == jobIds.isEmpty()) {
            orgUserVo.setOtherJobIds(StringUtils.join(jobIds, ";"));
            orgUserVo.setOtherJobNames(StringUtils.join(jobNames, ";"));
            orgUserVo.setOtherJobIdPaths(StringUtils.join(jobIdPaths, ";"));
            //orgUserVo.setOtherJobNamePaths(StringUtils.join(jobNamePaths, ";"));
        }
    }

    /**
     * 添加职位
     *
     * @param jobIds
     * @param jobNames
     * @param jobIdPaths
     * @return
     */
    private void addJobFromDingtalk(OrgUserVo orgUserVo, List<String> jobIds, List<String> jobNames, List<String> jobIdPaths) {

        if (false == jobIds.isEmpty()) {
            orgUserVo.setMainJobId(jobIds.remove(0));
            orgUserVo.setMainJobName(jobNames.remove(0));
            orgUserVo.setMainJobIdPath(jobIdPaths.remove(0));
            //orgUserVo.setMainJobNamePath(jobNamePaths.remove(0));
        }
        if (false == jobIds.isEmpty()) {
            orgUserVo.setOtherJobIds(StringUtils.join(jobIds, ";"));
            orgUserVo.setOtherJobNames(StringUtils.join(jobNames, ";"));
            orgUserVo.setOtherJobIdPaths(StringUtils.join(jobIdPaths, ";"));
            //orgUserVo.setOtherJobNamePaths(StringUtils.join(jobNamePaths, ";"));
        }
    }

    /**
     * 添加部门
     *
     * @param orgUserVo
     * @param departments
     */
    private void addDeptFromDingtalk(OrgUserVo orgUserVo, JSONArray departments) {
        if (CollectionUtils.isEmpty(departments)) {
            return;
        }
        int deptLength = departments.size();
        for (int i = 0; i < deptLength; i++) {
            String deptId = departments.optString(i), eleId;
            MultiOrgDingDept multiOrgDingDept = multiOrgDingDeptService.getByDingDeptId(deptId);
            if (multiOrgDingDept == null || StringUtils.isBlank(eleId = multiOrgDingDept.getEleId())) {
                continue;
            }
            OrgTreeNodeDto orgTreeNodeDto = multiOrgDingDeptService.getNodeDtoByEleId(eleId,
                    multiOrgDingDept.getOrgVersionId());
            if (orgTreeNodeDto != null) {
                orgUserVo.setMainDepartmentIdPath(orgTreeNodeDto.getEleIdPath());
                orgUserVo.setMainDepartmentNamePath(orgTreeNodeDto.getEleNamePath());
                return;// 任意一个已同步的部门
            }
        }
    }

    /**
     * 根据名称和部门匹配平台用户
     *
     * @param name
     * @param departments
     * @return
     */
    private OrgUserVo matchPtUser(String name, JSONArray departments) {
        OrgUserVo orgUserVo = null;
        List<MultiOrgUserAccount> unlinkAccounts = Lists.newArrayList();
        List<MultiOrgUserAccount> accounts = multiOrgUserAccountService.queryUsersByUserName(
                dingtalkConfig.getSystemUnitId(), name);
        for (MultiOrgUserAccount account : accounts) {
            boolean isExists = getByPtUserId(account.getId()) != null;
            if (isExists) {
                // 去掉已经关联的用户
                continue;
            }
            List<String> deptIds = Lists.newArrayList();
            if (false == CollectionUtils.isEmpty(departments)) {
                // 过滤部门
                for (int i = 0; i < departments.size(); i++) {
                    String deptId = departments.optString(i);
                    MultiOrgDingDept multiOrgDingDept = multiOrgDingDeptService.getByDingDeptId(deptId);
                    if (null == multiOrgDingDept || StringUtils.isBlank(multiOrgDingDept.getEleId())) {
                        continue;
                    }
                    deptIds.add(multiOrgDingDept.getEleId());
                }
            }
            OrgUserVo orgUserVo2 = multiOrgService.getUser(account.getUuid());
            if (false == CollectionUtils.isEmpty(deptIds) && StringUtils.isNotBlank(orgUserVo2.getMainDepartmentId())) {
                for (String deptId : deptIds) {
                    if (StringUtils.endsWith(orgUserVo2.getMainDepartmentId(), deptId)) {
                        unlinkAccounts.add(account);
                        break;// 按部门过滤（部门要先同步）
                    }
                }
            } else {
                // 未配置部门
                unlinkAccounts.add(account);
            }
        }
        if (unlinkAccounts.size() == 1) {
            orgUserVo = multiOrgService.getUser(unlinkAccounts.get(0).getUuid());
        } else if (unlinkAccounts.size() > 1) {
            throw new RuntimeException("系统存在多个未关联的重名用户[" + name + "]");
        }
        return orgUserVo;
    }

    /**
     * 根据名称和部门匹配平台用户
     *
     * @param name
     * @param departments
     * @return
     */
    private List<MultiOrgUserAccount> matchPtUserAccount(String name, JSONArray departments) {
        OrgUserVo orgUserVo = null;
        List<MultiOrgUserAccount> unlinkAccounts = Lists.newArrayList();
        List<MultiOrgUserAccount> accounts = multiOrgUserAccountService.queryUsersByUserName(
                dingtalkConfig.getSystemUnitId(), name);
        for (MultiOrgUserAccount account : accounts) {
            boolean isExists = getByPtUserId(account.getId()) != null;
            if (isExists) {
                // 去掉已经关联的用户
                continue;
            }
            List<String> deptIds = Lists.newArrayList();
            if (false == CollectionUtils.isEmpty(departments)) {
                // 过滤部门
                for (int i = 0; i < departments.size(); i++) {
                    String deptId = departments.optString(i);
                    MultiOrgDingDept multiOrgDingDept = multiOrgDingDeptService.getByDingDeptId(deptId);
                    if (null == multiOrgDingDept || StringUtils.isBlank(multiOrgDingDept.getEleId())) {
                        continue;
                    }
                    deptIds.add(multiOrgDingDept.getEleId());
                }
            }
            OrgUserVo orgUserVo2 = multiOrgService.getUser(account.getUuid());
            if (false == CollectionUtils.isEmpty(deptIds) && StringUtils.isNotBlank(orgUserVo2.getMainDepartmentId())) {
                for (String deptId : deptIds) {
                    if (StringUtils.endsWith(orgUserVo2.getMainDepartmentId(), deptId)) {
                        unlinkAccounts.add(account);
                        break;// 按部门过滤（部门要先同步）
                    }
                }
            } else {
                // 未配置部门
                unlinkAccounts.add(account);
            }
        }
        return unlinkAccounts;
    }

    @Override
    @Transactional
    public void saveAndUpdateUserFromDingtalk(JSONObject userobj) {
        if (userobj != null) {
            String unionid = userobj.getString("unionid");
            String userId = userobj.getString("userid");
            String orderInDepts = userobj.optString("orderInDepts");
            String openId = userobj.optString("openId");
            String mobile = userobj.optString("mobile");
            boolean active = userobj.optBoolean("active");
            String avatar = userobj.optString("avatar");
            boolean isAdmin = userobj.optBoolean("isAdmin");
            String tags = userobj.optString("tags");
            boolean isHide = userobj.optBoolean("isHide");
            String isLeaderInDepts = userobj.optString("isLeaderInDepts");
            boolean isBoss = userobj.optBoolean("isBoss");
            boolean isSenior = userobj.optBoolean("isSenior");
            String name = userobj.optString("name");
            String stateCode = userobj.optString("stateCode");
            String email = userobj.optString("email");
            // 角色
            JSONArray roles = userobj.optJSONArray("roles");
            // 部门
            JSONArray departments = userobj.optJSONArray("department");
            // 职位
            String position = userobj.optString("position");
            MultiOrgDingUser userInfo = getUser(unionid, userId);
            OrgUserVo orgUserVo = null;
            MultiOrgUserAccount multiOrgUserAccount = null;
            if (userInfo != null && StringUtils.isNotBlank(userInfo.getUserId())) {
                String ptUserId = userInfo.getUserId();
                if ((multiOrgUserAccount = multiOrgUserAccountService.getAccountByUserId(ptUserId)) != null) {
                    orgUserVo = multiOrgService.getUser(multiOrgUserAccount.getUuid());
                }
            } else {
                orgUserVo = matchPtUser(name, departments);
            }
            if (orgUserVo == null) {
                orgUserVo = new OrgUserVo();
                // 名称再加已存在的排序+1，例如已存在两个linyu，linyu2，则后续为拼音重复时，为linyu3
                String loginName = multiOrgUserAccountService.getUniqueLoginNameByUserName(name);// PinyinUtil.getPinYin(name);
                orgUserVo.setLoginName(loginName); // 平台登录名称使用手机号，保证唯一性
                orgUserVo.setCode(userId); // userid为钉钉返回的用户标识，这边作为code写入，后续平台可自行修改
                orgUserVo.setType(0);
                orgUserVo.setPassword("0");
                orgUserVo.setSystemUnitId(dingtalkConfig.getSystemUnitId());
                orgUserVo.setIsLocked(0);
                orgUserVo.setIsForbidden(0);
            }
            orgUserVo.setMainEmail(email);
            orgUserVo.setMobilePhone(mobile);
            orgUserVo.setUserName(name);
            // 角色
            addRolesFromDingtalk(orgUserVo, roles);
            // 部门
            addDeptFromDingtalk(orgUserVo, departments);
            // 职位
            addJobFromDingtalk(orgUserVo, departments, position);
            if (StringUtils.isBlank(orgUserVo.getUuid())) {
                try {
                    orgUserVo = multiOrgService.addUser(orgUserVo);
                } catch (UnsupportedEncodingException ex) {
                    logger.warn(ex.getMessage(), ex);
                }
            } else {
                orgUserVo.setPassword(null);// 不修改密码
                multiOrgService.modifyUser(orgUserVo);
            }
            if (userInfo == null) {
                userInfo = new MultiOrgDingUser();
                userInfo.setUnionId(unionid);
                userInfo.setDing_userId(userId);
                userInfo.setUserId(orgUserVo.getId());
                userInfo.setSystemUnitId(dingtalkConfig.getSystemUnitId());
            }
            userInfo.setName(name);
            userInfo.setOrderInDepts(orderInDepts);
            userInfo.setOpenId(openId);
            userInfo.setRoles(CollectionUtils.isEmpty(roles) ? null : roles.toString());
            userInfo.setMobile(mobile);
            userInfo.setPtIsActive(0);// 默认待确认
            userInfo.setActive(active ? 1 : 0);
            userInfo.setIsAdmin(isAdmin ? 1 : 0);
            userInfo.setIsHide(isHide ? 1 : 0);
            userInfo.setIsLeaderInDepts(isLeaderInDepts);
            userInfo.setIsBoss(isBoss ? 1 : 0);
            userInfo.setIsSenior(isSenior ? 1 : 0);
            userInfo.setStateCode(stateCode);
            userInfo.setDepartment(CollectionUtils.isEmpty(departments) ? null : departments.toString());
            userInfo.setEmail(email);
            userInfo.setPosition(position);
            userInfo.setAvatar(avatar);
            userInfo.setTags(tags);
            dao.save(userInfo);
        }
    }

    @Override
    @Transactional
    public void saveAndUpdateUserFromDingtalk(JSONObject userobj, String logId, boolean syncUserWork) {
        if (userobj != null) {
            String unionid = userobj.getString("unionid");
            String userId = userobj.getString("userid");
            String orderInDepts = userobj.optString("orderInDepts");
            String openId = userobj.optString("openId");
            boolean active = userobj.optBoolean("active");
            boolean isAdmin = userobj.optBoolean("isAdmin");
            String tags = userobj.optString("tags");
            boolean isHide = userobj.optBoolean("isHide");
            String isLeaderInDepts = userobj.optString("isLeaderInDepts");
            boolean isBoss = userobj.optBoolean("isBoss");
            boolean isSenior = userobj.optBoolean("isSenior");
            // 姓名
            String name = userobj.optString("name");
            String stateCode = userobj.optString("stateCode");

            // 部门
            JSONArray departments = userobj.optJSONArray("department");
            // 职位
            String position = userobj.optString("position");

            MultiOrgDingUser userInfo = getUser(unionid, userId);
            OrgUserVo orgUserVo = null;
            MultiOrgUserAccount multiOrgUserAccount = null;
            if (userInfo != null && StringUtils.isNotBlank(userInfo.getUserId())) {
                String ptUserId = userInfo.getUserId();
                if ((multiOrgUserAccount = multiOrgUserAccountService.getAccountByUserId(ptUserId)) != null) {
                    orgUserVo = multiOrgService.getUser(multiOrgUserAccount.getUuid());
                }
            } else {
                List<MultiOrgUserAccount> unlinkAccounts = matchPtUserAccount(name, departments);
                if (unlinkAccounts.size() == 1) {
                    orgUserVo = multiOrgService.getUser(unlinkAccounts.get(0).getUuid());
                } else if (unlinkAccounts.size() > 1) {
                    userInfo = new MultiOrgDingUser();
                    userInfo.setDing_userId(userId);
                    userInfo.setName(name);
                    saveMultiOrgSyncUserLog(logId, userInfo, DingtalkInfo.SYNC_OPERATION_ADD, DingtalkInfo.SYNC_STATUS_ERROR, DingtalkInfo.SYNC_USER_ERROR_DUPLICATE_NAME);
                    return;
                }
            }
            String operationName = DingtalkInfo.SYNC_OPERATION_MOD;
            if (orgUserVo == null) {
                orgUserVo = new OrgUserVo();
                // 名称再加已存在的排序+1，例如已存在两个linyu，linyu2，则后续为拼音重复时，为linyu3
                String loginName = multiOrgUserAccountService.getUniqueLoginNameByUserName(name);// PinyinUtil.getPinYin(name);
                loginName = handleLoginName(loginName);
                orgUserVo.setLoginName(loginName); // 平台登录名称使用手机号，保证唯一性
                orgUserVo.setCode(userId); // userid为钉钉返回的用户标识，这边作为code写入，后续平台可自行修改
                orgUserVo.setType(0);
                orgUserVo.setPassword("0");
                orgUserVo.setSystemUnitId(dingtalkConfig.getSystemUnitId());
                orgUserVo.setIsLocked(0);
                orgUserVo.setIsForbidden(0);
                operationName = DingtalkInfo.SYNC_OPERATION_ADD;
            }

            // 证件号码
            String idNumber = "";
            String idnumberSyncSwitch = dingtalkConfig.getIdnumberSyncSwitch();
            if ("1".equals(idnumberSyncSwitch)) {
                JSONObject json = userobj.optJSONObject("extattr");
                if (null != json && !json.isEmpty()) {
                    idNumber = json.optString("身份证号");
                }
            }
            userobj.put("idNumber", idNumber);

            orgUserVo.setUserName(name);
            orgUserVo = syncUserField(userobj, orgUserVo);
            // 部门
            addDeptFromDingtalk(orgUserVo, departments);

            MultiDeptUserAudit audit = null;
            // 职位
            if (syncUserWork) {
                audit = syncWorkinfo(orgUserVo, userobj, position, logId);
            }

            if (StringUtils.isBlank(orgUserVo.getUuid())) {
                try {
                    orgUserVo = multiOrgService.addUser(orgUserVo);
                } catch (UnsupportedEncodingException ex) {
                    saveMultiOrgSyncUserLog(logId, userInfo, operationName, DingtalkInfo.SYNC_STATUS_ERROR, DingtalkInfo.SYNC_USER_ERROR_SYS_ERROR);
                    logger.error(ex.getMessage(), ex);
                    return;
                }
            } else {
                orgUserVo.setPassword(null);// 不修改密码
                multiOrgService.modifyUser(orgUserVo);
            }

            if (null != audit) {
                audit.setUserId(orgUserVo.getId());
                multiDeptUserAuditService.save(audit);
            }

            if (userInfo == null) {
                userInfo = new MultiOrgDingUser();
                userInfo.setUnionId(unionid);
                userInfo.setDing_userId(userId);
                userInfo.setUserId(orgUserVo.getId());
                userInfo.setSystemUnitId(dingtalkConfig.getSystemUnitId());
            }
            userInfo.setName(name);
            userInfo.setOrderInDepts(orderInDepts);
            userInfo.setOpenId(openId);

            userInfo.setActive(active ? 1 : 0);
            userInfo.setIsAdmin(isAdmin ? 1 : 0);
            userInfo.setIsHide(isHide ? 1 : 0);
            userInfo.setIsLeaderInDepts(isLeaderInDepts);
            userInfo.setIsBoss(isBoss ? 1 : 0);
            userInfo.setIsSenior(isSenior ? 1 : 0);
            userInfo.setStateCode(stateCode);
            userInfo.setDepartment(CollectionUtils.isEmpty(departments) ? null : departments.toString());
            userInfo.setPosition(position);
            userInfo.setTags(tags);
            syncUserField(userobj, userInfo);
            dao.save(userInfo);
            saveMultiOrgSyncUserLog(logId, userInfo, operationName, DingtalkInfo.SYNC_STATUS_SUCCESS, StringUtils.EMPTY);
        }
    }

    private String handleLoginName(String loginName) {
        loginName = loginName.replaceAll(" ", "");
        loginName = loginName.replaceAll("（", "_").replaceAll("）", "");
        loginName = loginName.replaceAll("\\(", "_").replaceAll("\\)", "");
        loginName = loginName.replaceAll("\\{", "_").replaceAll("\\}", "");
        loginName = loginName.replaceAll("[^(a-z0-9A-Z_)]", "_");
        return loginName;
    }

    private void saveMultiOrgSyncUserLog(String logId, MultiOrgDingUser multiOrgDingUser, String operationName, Integer syncStatus, String remark) {
        MultiOrgSyncUserLog log = new MultiOrgSyncUserLog();
        log.setLogId(logId);

        if (null != multiOrgDingUser) {
            log.setUserId(multiOrgDingUser.getDing_userId());
            log.setName(multiOrgDingUser.getName());
            log.setMobile(multiOrgDingUser.getMobile());
        }

        log.setOperationName(operationName);
        log.setSyncStatus(syncStatus);
        log.setRemark(remark);

        if (null != multiOrgDingUser && StringUtils.isNotBlank(multiOrgDingUser.getDepartment())) {
            if (multiOrgDingUser.getDepartment().contains(Separator.COMMA.getValue())) {
                log.setIsMultiDepts(1);
            }
        }

        multiOrgSyncUserLogService.save(log);
    }

    private void saveMultiOrgSyncUserWorkLog(String logId, String jobId, Integer syncStatus, String remark, MultiOrgDingDept multiOrgDingDept, JSONObject userobj) {
        MultiOrgSyncUserWorkLog log = new MultiOrgSyncUserWorkLog();
        log.setLogId(logId);
        log.setUserId(userobj.optString("userid"));
        log.setName(userobj.optString("name"));
        log.setMobile(userobj.optString("mobile"));

        if (null != multiOrgDingDept) {
            if (StringUtils.isNotBlank(multiOrgDingDept.getId())) {
                log.setDeptId(multiOrgDingDept.getId());
            }
            if (StringUtils.isNotBlank(multiOrgDingDept.getName())) {
                log.setDeptName(multiOrgDingDept.getName());
            }
        }

        log.setJobId(jobId);
        log.setJobName(userobj.optString("position"));
        log.setSyncStatus(syncStatus);
        log.setRemark(remark);
        multiOrgSyncUserWorkLogService.save(log);
    }

    private MultiDeptUserAudit syncWorkinfo(OrgUserVo orgUserVo, JSONObject userobj, String position, String logId) {
        JSONArray departments = userobj.optJSONArray("department");
        if (CollectionUtils.isEmpty(departments) || StringUtils.isBlank(position)) {
            return null;
        }
        // 人员和职位的关系同步开关
        String workinfoSyncSwitch = dingtalkConfig.getWorkinfoSyncSwitch();
        if (DingtalkInfo.SYNC_SWITCH_OPEN.equals(workinfoSyncSwitch)) {
            List<String> jobIds = Lists.newArrayList(), jobNames = Lists.newArrayList(), jobIdPaths = Lists.newArrayList(), deptIds = Lists.newArrayList(), deptNames = Lists.newArrayList();
            int deptLength = departments.size();

            /**
             * 同步人员和职位关系的时候，
             * 单部门：直接同步，职位作为主职，生成人员和职位关系同步的日志；
             * 多部门：不同步人员和职位关系，生成多部门人员审核记录，审核后，同时创建职位节点；在组织同步日志、业务事件同步日志中，添加是否多部门人员的标记。
             */
            if (1 == deptLength) {
                String deptId = departments.optString(0), eleId;
                MultiOrgDingDept multiOrgDingDept = multiOrgDingDeptService.getByDingDeptId(deptId);
                if (multiOrgDingDept == null || StringUtils.isBlank(eleId = multiOrgDingDept.getEleId())) {
                    multiOrgDingDept = multiOrgDingDeptService.getDao().getOneByFieldEq("id", deptId);
                    saveMultiOrgSyncUserWorkLog(logId, StringUtils.EMPTY, DingtalkInfo.SYNC_STATUS_ERROR, DingtalkInfo.SYNC_USER_WORK_ERROR_DEPT_NO_EXISTS, multiOrgDingDept, userobj);
                    return null;// 部门未同步，忽略
                }
                OrgTreeNodeDto orgTreeNodeDto = multiOrgDingDeptService.getNodeDtoByEleId(eleId,
                        multiOrgDingDept.getOrgVersionId());
                if (orgTreeNodeDto == null) {
                    saveMultiOrgSyncUserWorkLog(logId, StringUtils.EMPTY, DingtalkInfo.SYNC_STATUS_ERROR, DingtalkInfo.SYNC_USER_WORK_ERROR_DEPT_NO_EXISTS, multiOrgDingDept, userobj);
                    return null;// 部门未挂载到组织树中
                }

                deptIds.add(multiOrgDingDept.getEleId());
                deptNames.add(multiOrgDingDept.getName());

                OrgTreeNodeDto mainJobNode = null;
                List<OrgTreeNodeDto> list = orgApiFacade.queryAllNodeOfOrgVersionByEleIdPath(
                        multiOrgDingDept.getOrgVersionId(), orgTreeNodeDto.getEleIdPath(), IdPrefix.JOB.getValue());
                // 过滤部门下一级职位
                for (OrgTreeNodeDto jobNode : list) {
                    if (StringUtils.equals(jobNode.getSystemUnitId(), dingtalkConfig.getSystemUnitId())
                            && StringUtils.equals(jobNode.getParentIdPath(), orgTreeNodeDto.getEleIdPath())
                            && StringUtils.equals(position, jobNode.getName())) {
                        mainJobNode = jobNode;
                        break;
                    }
                }
                if (mainJobNode == null) {
                    // 构造职位VO
                    OrgTreeNodeVo nodevo = new OrgTreeNodeVo();
                    nodevo.setName(position);
                    nodevo.setShortName(position);
                    nodevo.setCode("JOB_DINGTALK_" + RandomStringUtils.randomNumeric(3));
                    nodevo.setType(IdPrefix.JOB.getValue()); // 类型
                    nodevo.setParentEleIdPath(orgTreeNodeDto.getEleIdPath());
                    nodevo.setParentEleNamePath(orgTreeNodeDto.getEleNamePath());
                    nodevo.setOrgVersionId(dingtalkConfig.getOrgVersionId());
                    nodevo.setSystemUnitId(dingtalkConfig.getSystemUnitId());
                    MultiOrgTreeNode node = multiOrgService.addOrgChildNode(nodevo);
                    jobIds.add(dingtalkConfig.getOrgVersionId() + MultiOrgService.PATH_SPLIT_SYSMBOL + node.getEleId());
                    jobNames.add(position);
                    jobIdPaths.add(node.getEleIdPath());
                    //jobNamePaths.add(orgTreeNodeDto.getEleNamePath() + MultiOrgService.PATH_SPLIT_SYSMBOL + position);
                    saveMultiOrgSyncUserWorkLog(logId, node.getEleId(), DingtalkInfo.SYNC_STATUS_SUCCESS, StringUtils.EMPTY, multiOrgDingDept, userobj);
                } else {
                    jobIds.add(mainJobNode.getOrgVersionId() + MultiOrgService.PATH_SPLIT_SYSMBOL + mainJobNode.getEleId());
                    jobNames.add(mainJobNode.getName());
                    jobIdPaths.add(mainJobNode.getEleIdPath());
                    //jobNamePaths.add(mainJobNode.getEleNamePath());
                    saveMultiOrgSyncUserWorkLog(logId, mainJobNode.getEleId(), DingtalkInfo.SYNC_STATUS_SUCCESS, StringUtils.EMPTY, multiOrgDingDept, userobj);
                }
                // 职位
                addJobFromDingtalk(orgUserVo, jobIds, jobNames, jobIdPaths);
            } else {
                for (int i = 0; i < deptLength; i++) {
                    String deptId = departments.optString(i), eleId;
                    MultiOrgDingDept multiOrgDingDept = multiOrgDingDeptService.getByDingDeptId(deptId);
                    if (multiOrgDingDept == null || StringUtils.isBlank(eleId = multiOrgDingDept.getEleId())) {
                        continue;// 部门未同步，忽略
                    }
                    OrgTreeNodeDto orgTreeNodeDto = multiOrgDingDeptService.getNodeDtoByEleId(eleId,
                            multiOrgDingDept.getOrgVersionId());
                    if (orgTreeNodeDto == null) {
                        continue;// 部门未挂载到组织树中
                    }
                    deptIds.add(multiOrgDingDept.getEleId());
                    deptNames.add(multiOrgDingDept.getName());
                }

                // 多部门人员审核  一个人只允许存在一条 未审核的多部门人员审核数据，存在更新，不存在新增
                String dingUserId = userobj.optString("userid");
                String unionId = userobj.optString("unionid");
                MultiDeptUserAudit audit = new MultiDeptUserAudit();
//				audit.setUserId(orgUserVo.getId());
                audit.setDingUserId(dingUserId);
                audit.setUnionId(unionId);
                audit.setAuditStatus(DingtalkInfo.MULTI_DEPTS_USER_WORK_UNAUDIT);
                audit.setSystemUnitId(dingtalkConfig.getSystemUnitId());
                audit = multiDeptUserAuditService.getByEntity(audit);

                if (null == audit) {
                    audit = new MultiDeptUserAudit();
                    audit.setUserId(orgUserVo.getId());
                    audit.setDingUserId(dingUserId);
                    audit.setUnionId(unionId);
                    audit.setAuditStatus(DingtalkInfo.MULTI_DEPTS_USER_WORK_UNAUDIT);
                }

                audit.setName(userobj.optString("name"));
                audit.setLoginName(orgUserVo.getLoginName());
                audit.setEmployeeNumber(orgUserVo.getEmployeeNumber());
                audit.setDeptIds(StringUtils.join(deptIds, Separator.COMMA.getValue()));
                audit.setDeptNames(StringUtils.join(deptNames, Separator.COMMA.getValue()));
                audit.setJobName(position);
                audit.setBeforeAuditMainJob(orgUserVo.getMainJobNamePath());
                audit.setSyncTime(new Date());
                audit.setSystemUnitId(dingtalkConfig.getSystemUnitId());

                return audit;
            }
        }
        return null;
    }

    /**
     * 设置人员的同步字段
     *
     * @return
     */
    private OrgUserVo syncUserField(JSONObject userobj, OrgUserVo orgUserVo) {
        // 头像
        String avatar = userobj.optString("avatar");
        String avatarSyncSwitch = dingtalkConfig.getAvatarSyncSwitch();
        if ("1".equals(avatarSyncSwitch)) {
            if (StringUtils.isNotBlank(avatar)) {
                InputStream inputStream = HttpUtil.getInputStream(avatar);
                MongoFileEntity mongoFileEntity = mongoFileService.saveFile(avatar.substring(avatar.lastIndexOf("/") + 1, avatar.length()), inputStream);
                orgUserVo.setPhotoUuid(mongoFileEntity.getFileID());
            }
        }

        // 手机
        String mobile = userobj.optString("mobile");
        String mobileSyncSwitch = dingtalkConfig.getMobileSyncSwitch();
        if ("1".equals(mobileSyncSwitch)) {
            orgUserVo.setMobilePhone(mobile);
        }

        // 分机号
        String telephone = userobj.optString("tel");
        String telephoneSyncSwitch = dingtalkConfig.getTelephoneSyncSwitch();
        if ("1".equals(telephoneSyncSwitch)) {
            orgUserVo.setOfficePhone(telephone);
        }

        // 邮箱
        String email = userobj.optString("email");
        String emailSyncSwitch = dingtalkConfig.getEmailSyncSwitch();
        if ("1".equals(emailSyncSwitch)) {
            orgUserVo.setMainEmail(email);
        }

        // 工号
        String jobnumber = userobj.optString("jobnumber");
        String jobnoSyncSwitch = dingtalkConfig.getJobnoSyncSwitch();
        if ("1".equals(jobnoSyncSwitch)) {
            orgUserVo.setEmployeeNumber(jobnumber);
        }

        // 证件号码
        String idnumber = userobj.optString("idNumber");
        String idnumberSyncSwitch = dingtalkConfig.getIdnumberSyncSwitch();
        if ("1".equals(idnumberSyncSwitch)) {
            orgUserVo.setIdNumber(idnumber);
        }

        // 备注
        String remark = userobj.optString("remark");
        String remarkSyncSwitch = dingtalkConfig.getRemarkSyncSwitch();
        if ("1".equals(remarkSyncSwitch)) {
            orgUserVo.setRemark(remark);
        }

        // 如果OA用户禁用，则同步后启用用户
        orgUserVo.setIsForbidden(0);
        return orgUserVo;
    }

    /**
     * 设置人员的同步字段
     *
     * @param userobj
     * @param userInfo
     * @return
     */
    private MultiOrgDingUser syncUserField(JSONObject userobj, MultiOrgDingUser userInfo) {
        // 头像
        String avatar = userobj.optString("avatar");
        String avatarSyncSwitch = dingtalkConfig.getAvatarSyncSwitch();
        if ("1".equals(avatarSyncSwitch)) {
            userInfo.setAvatar(avatar);
        }

        // 手机
        String mobile = userobj.optString("mobile");
        String mobileSyncSwitch = dingtalkConfig.getMobileSyncSwitch();
        if ("1".equals(mobileSyncSwitch)) {
            userInfo.setMobile(mobile);
        }

        // 分机号
        String telephone = userobj.optString("tel");
        String telephoneSyncSwitch = dingtalkConfig.getTelephoneSyncSwitch();
        if ("1".equals(telephoneSyncSwitch)) {
            userInfo.setOfficePhone(telephone);
        }

        // 邮箱
        String email = userobj.optString("email");
        String emailSyncSwitch = dingtalkConfig.getEmailSyncSwitch();
        if ("1".equals(emailSyncSwitch)) {
            userInfo.setEmail(email);
        }

        // 工号
        String jobnumber = userobj.optString("jobnumber");
        String jobnoSyncSwitch = dingtalkConfig.getJobnoSyncSwitch();
        if ("1".equals(jobnoSyncSwitch)) {
            userInfo.setEmployeeNumber(jobnumber);
        }

        // 证件号码
        String idnumber = userobj.optString("idNumber");
        String idnumberSyncSwitch = dingtalkConfig.getIdnumberSyncSwitch();
        if ("1".equals(idnumberSyncSwitch)) {
            userInfo.setIdNumber(idnumber);
        }

        // 备注
        String remark = userobj.optString("remark");
        String remarkSyncSwitch = dingtalkConfig.getRemarkSyncSwitch();
        if ("1".equals(remarkSyncSwitch)) {
            userInfo.setRemark(remark);
        }

        return userInfo;
    }

    @Override
    @Transactional
    public void deleteUserFromDingtalk(String userId) {
        MultiOrgDingUser user = getUser(null, userId);
        if (user != null) {
            // multiOrgService.deleteUsers(new String[] { user.getUserId() });
            MultiOrgUserAccount userAccount = multiOrgUserAccountService.getAccountByUserId(user.getUserId());
            if (userAccount != null) {
                multiOrgUserAccountService.forbidAccount(userAccount.getUuid());
            }
            // user.setActive(0);
            // save(user);
            dao.delete(user.getUuid());
        }
    }

    @Override
    public MultiOrgDingUser getUser(String unionId, String dingUserId) {
        String hql;
        Map<String, Object> params = Maps.newHashMap();
        if (StringUtils.isNotBlank(unionId)) {
            hql = "from MultiOrgDingUser t where t.unionId = :unionId";
            params.put("unionId", unionId);
        } else {
            hql = "from MultiOrgDingUser t where t.ding_userId = :dingUserId";
            params.put("dingUserId", dingUserId);
        }
        hql += " and exists (select 1 from MultiOrgUserAccount tt where tt.id = t.userId and tt.systemUnitId = :systemUnitId)";
        params.put("systemUnitId", dingtalkConfig.getSystemUnitId());
        List<MultiOrgDingUser> list = dao.listByHQL(hql, params);
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        return list.get(0);
    }

    @Override
    public MultiOrgDingUser getByUnionId(String unionId) {
        return getUser(unionId, null);
    }

    @Override
    public MultiOrgDingUser getByDingUserId(String dingUserId) {
        return getUser(null, dingUserId);
    }

    @Override
    public MultiOrgDingUser getByPtUserId(String ptUserId) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("userId", ptUserId);
        List<MultiOrgDingUser> lists = listByHQL("from MultiOrgDingUser t where t.userId = :userId", params);
        if (lists != null && false == lists.isEmpty()) {
            return lists.get(0);
        }
        return null;
    }

    @Override
    public List<String> getDingIdsByPtIds(Collection<String> userIds) {
        List<String> dingUserIds = Lists.newArrayList();
        Map<String, Object> params = Maps.newHashMap();
        params.put("userIds", userIds);
        String hql = "select t.ding_userId from MultiOrgDingUser t where t.userId in :userIds";
        List<MultiOrgDingUser> lists = listByHQL(hql, params);
        for (MultiOrgDingUser user : lists) {
            dingUserIds.add(user.getDing_userId());
        }
        return dingUserIds;
    }

    @Override
    @Transactional
    public void confirmUsers(Collection<String> uuids) {
        if (CollectionUtils.isEmpty(uuids)) {
            return;
        }
        for (String uuid : uuids) {
            MultiOrgDingUser entity = getOne(uuid);
            entity.setPtIsActive(1);
            save(entity);
        }
    }

    @Override
    public List<String> getDingIdsByDeptId(String deptId) {
        List<String> dingUserIds = Lists.newArrayList();
        Map<String, Object> params = Maps.newHashMap();
        params.put("systemUnitId", dingtalkConfig.getSystemUnitId());
        params.put("deptId", deptId);
        String hql = "select t.ding_userId from MultiOrgDingUser t where systemUnitId = :systemUnitId and (department like '[' || :deptId || ',%' or department like '%,' || :deptId || ',%' or department like '%,' || :deptId || ']' or department = '[' || :deptId || ']') ";
		/*List<MultiOrgDingUser> lists = listByHQL(hql, params);
		for (MultiOrgDingUser user : lists) {
			dingUserIds.add(user.getDing_userId());
		}*/
        return dao.listCharSequenceByHQL(hql, params);
    }

    @Override
    public void saveAndUpdateUserWork(JSONObject userobj, String logId, String deptId, String position) {
        if (userobj != null) {
            String unionid = userobj.optString("unionid");
            String userId = userobj.optString("userid");

            MultiOrgDingUser userInfo = getUser(unionid, userId);
            OrgUserVo orgUserVo = null;
            MultiOrgUserAccount multiOrgUserAccount = null;
            if (userInfo != null && StringUtils.isNotBlank(userInfo.getUserId())) {
                String ptUserId = userInfo.getUserId();
                if ((multiOrgUserAccount = multiOrgUserAccountService.getAccountByUserId(ptUserId)) != null) {
                    orgUserVo = multiOrgService.getUser(multiOrgUserAccount.getUuid());
                }
            }

            String jobId, jobName, jobIdPath, eleId;
            MultiOrgDingDept multiOrgDingDept = multiOrgDingDeptService.getByDingDeptId(deptId);
            if (multiOrgDingDept == null || StringUtils.isBlank(eleId = multiOrgDingDept.getEleId())) {
                saveMultiOrgSyncUserWorkLog(logId, StringUtils.EMPTY, DingtalkInfo.SYNC_STATUS_ERROR, DingtalkInfo.SYNC_USER_WORK_ERROR_DEPT_NO_EXISTS, multiOrgDingDept, userobj);
                return;// 部门未同步，忽略
            }
            OrgTreeNodeDto orgTreeNodeDto = multiOrgDingDeptService.getNodeDtoByEleId(eleId, multiOrgDingDept.getOrgVersionId());
            OrgTreeNodeDto mainJobNode = null;
            List<OrgTreeNodeDto> list = orgApiFacade.queryAllNodeOfOrgVersionByEleIdPath(multiOrgDingDept.getOrgVersionId(), orgTreeNodeDto.getEleIdPath(), IdPrefix.JOB.getValue());
            // 过滤部门下一级职位
            for (OrgTreeNodeDto jobNode : list) {
                if (StringUtils.equals(jobNode.getSystemUnitId(), dingtalkConfig.getSystemUnitId())
                        && StringUtils.equals(jobNode.getParentIdPath(), orgTreeNodeDto.getEleIdPath())
                        && StringUtils.equals(position, jobNode.getName())) {
                    mainJobNode = jobNode;
                    break;
                }
            }

            if (mainJobNode == null) {
                // 构造职位VO
                OrgTreeNodeVo nodevo = new OrgTreeNodeVo();
                nodevo.setName(position);
                nodevo.setShortName(position);
                nodevo.setCode("JOB_DINGTALK_" + RandomStringUtils.randomNumeric(3));
                nodevo.setType(IdPrefix.JOB.getValue()); // 类型
                nodevo.setParentEleIdPath(orgTreeNodeDto.getEleIdPath());
                nodevo.setParentEleNamePath(orgTreeNodeDto.getEleNamePath());
                nodevo.setOrgVersionId(dingtalkConfig.getOrgVersionId());
                nodevo.setSystemUnitId(dingtalkConfig.getSystemUnitId());
                MultiOrgTreeNode node = multiOrgService.addOrgChildNode(nodevo);
                jobId = dingtalkConfig.getOrgVersionId() + MultiOrgService.PATH_SPLIT_SYSMBOL + node.getEleId();
                jobName = position;
                jobIdPath = node.getEleIdPath();
                saveMultiOrgSyncUserWorkLog(logId, node.getEleId(), DingtalkInfo.SYNC_STATUS_SUCCESS, StringUtils.EMPTY, multiOrgDingDept, userobj);
            } else {
                jobId = mainJobNode.getOrgVersionId() + MultiOrgService.PATH_SPLIT_SYSMBOL + mainJobNode.getEleId();
                jobName = mainJobNode.getName();
                jobIdPath = mainJobNode.getEleIdPath();
                saveMultiOrgSyncUserWorkLog(logId, mainJobNode.getEleId(), DingtalkInfo.SYNC_STATUS_SUCCESS, StringUtils.EMPTY, multiOrgDingDept, userobj);
            }

            // 职位处理
//			addJobFromDingtalk(orgUserVo, jobIds, jobNames, jobIdPaths);
            if (StringUtils.isNotBlank(orgUserVo.getOtherJobIds())) {
                if (!Arrays.asList(orgUserVo.getOtherJobIds().split(Separator.SEMICOLON.getValue())).contains(jobId)) {
                    orgUserVo.setOtherJobIds(orgUserVo.getOtherJobIds() + Separator.SEMICOLON.getValue() + jobId);
                }
            } else {
                orgUserVo.setOtherJobIds(jobId);
            }

            if (StringUtils.isNotBlank(orgUserVo.getOtherJobNames())) {
                if (!Arrays.asList(orgUserVo.getOtherJobNames().split(Separator.SEMICOLON.getValue())).contains(jobName)) {
                    orgUserVo.setOtherJobNames(orgUserVo.getOtherJobNames() + Separator.SEMICOLON.getValue() + jobName);
                }
            } else {
                orgUserVo.setOtherJobNames(jobName);
            }

            if (StringUtils.isNotBlank(orgUserVo.getOtherJobIdPaths())) {
                if (!Arrays.asList(orgUserVo.getOtherJobIdPaths().split(Separator.SEMICOLON.getValue())).contains(jobIdPath)) {
                    orgUserVo.setOtherJobIdPaths(orgUserVo.getOtherJobIdPaths() + Separator.SEMICOLON.getValue() + jobIdPath);
                }
            } else {
                orgUserVo.setOtherJobIdPaths(jobIdPath);
            }

            orgUserVo.setPassword(null);// 不修改密码
            multiOrgService.modifyUser(orgUserVo);
        }
    }

}
