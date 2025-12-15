/*
 * @(#)2013-1-27 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.org.facade.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Function;
import com.google.common.collect.*;
import com.wellsoft.context.component.tree.OrgNode;
import com.wellsoft.context.enums.IdPrefix;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.context.service.AbstractApiFacade;
import com.wellsoft.pt.basicdata.business.dto.BusinessCategoryOrgDto;
import com.wellsoft.pt.basicdata.business.entity.BusinessCategoryOrgEntity;
import com.wellsoft.pt.basicdata.business.facade.service.BusinessFacadeService;
import com.wellsoft.pt.dyform.facade.service.DyFormFacade;
import com.wellsoft.pt.jpa.util.BeanUtils;
import com.wellsoft.pt.jpa.util.HqlUtils;
import com.wellsoft.pt.multi.group.entity.MultiGroup;
import com.wellsoft.pt.multi.group.entity.MultiGroupTreeNode;
import com.wellsoft.pt.multi.group.service.MultiGroupService;
import com.wellsoft.pt.multi.group.service.MultiGroupTreeNodeService;
import com.wellsoft.pt.multi.org.bean.*;
import com.wellsoft.pt.multi.org.constant.UnitParamConstant;
import com.wellsoft.pt.multi.org.dto.CurrentUnitJobGradeDto;
import com.wellsoft.pt.multi.org.dto.DutySeqAndjobRankDto;
import com.wellsoft.pt.multi.org.dto.GetCurrentUnitUserListByUserNameKeyDto;
import com.wellsoft.pt.multi.org.dto.OrgDutySeqTreeDto;
import com.wellsoft.pt.multi.org.entity.*;
import com.wellsoft.pt.multi.org.facade.service.*;
import com.wellsoft.pt.multi.org.service.*;
import com.wellsoft.pt.multi.org.vo.JobRankLevelVo;
import com.wellsoft.pt.org.entity.DutyAgent;
import com.wellsoft.pt.org.facade.service.OrgFacadeService;
import com.wellsoft.pt.org.service.DutyAgentService;
import com.wellsoft.pt.org.service.JobService;
import com.wellsoft.pt.org.service.UserPropertyService;
import com.wellsoft.pt.security.audit.entity.NestedRole;
import com.wellsoft.pt.security.audit.entity.Role;
import com.wellsoft.pt.security.audit.facade.service.NestedRoleFacadeService;
import com.wellsoft.pt.security.audit.facade.service.RoleFacadeService;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import com.wellsoft.pt.user.entity.UserInfoEntity;
import com.wellsoft.pt.user.facade.service.UserInfoFacadeService;
import com.wellsoft.pt.user.service.UserAccountService;
import com.wellsoft.pt.user.service.UserInfoService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.collections4.SetUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.CharUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Description: 组织机构对外统一提供的接口
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-1-27.1	zhulh		2013-1-27		Create
 * </pre>
 * @date 2013-1-27
 */
@Service
public class OrgApiFacadeImpl extends AbstractApiFacade implements OrgApiFacade {
    @Autowired
    UserPropertyService userPropertyService;
    @Autowired
    private DutyAgentService dutyAgentService;
    @Autowired
    private JobService jobService;
    @Autowired
    private MultiOrgUserWorkInfoService multiOrgUserWorkInfoService;
    @Autowired
    private DyFormFacade dyFormFacade;
    @Autowired
    private MultiOrgTreeNodeService multiOrgTreeNodeService;
    @Autowired
    private MultiOrgElementAttrService multiOrgElementAttrService;
    @Autowired
    private MultiOrgElementService multiOrgElementService;
    @Autowired
    private BusinessFacadeService businessFacadeService;
    @Autowired
    private MultiOrgOptionService multiOrgOptionService;
    @Autowired
    private MultiOrgUserTreeNodeService multiOrgUserTreeNodeService;
    @Autowired
    private RoleFacadeService roleFacadeService;
    @Autowired
    private NestedRoleFacadeService nestedRoleFacadeService;
    @Autowired
    private MultiOrgElementRoleService multiOrgElementRoleService;
    @Autowired
    private MultiOrgGroupRoleService multiOrgGroupRoleService;
    @Autowired
    private MultiOrgGroupMemberService multiOrgGroupMemberService;
    @Autowired
    private MultiOrgUserRoleService multiOrgUserRoleService;
    @Autowired
    private MultiOrgJobDutyService multiOrgJobDutyService;
    @Autowired
    private UserInfoService userInfoService;
    @Autowired
    private MultiJobRankLevelService multiJobRankLevelService;
    @Autowired
    private UnitParamService unitParamService;
    @Autowired
    private OrgJobGradeService orgJobGradeService;
    @Autowired
    private OrgDutySeqService orgDutySeqService;
    @Autowired
    private MultiOrgJobRankService multiOrgJobRankService;
    @Autowired
    private MultiOrgUserAccountService multiOrgUserAccountService;
    @Autowired
    private MultiOrgTreeDialogService multiOrgTreeDialogService;
    @Autowired
    private UserAccountService userAccountService;
    /**
     * 新代码
     */
    @Autowired
    private MultiOrgService multiOrgService;
    @Autowired
    private MultiOrgUserService multiOrgUserService;
    @Autowired
    private MultiOrgGroupFacade multiOrgGroupFacade;
    @Autowired
    private MultiOrgTreeDialogService orgTreeDialogService;
    @Autowired
    private MultiOrgVersionFacade multiOrgVersionService;
    @Autowired
    private MultiOrgSystemUnitService multiOrgSystemUnitService;
    @Autowired
    private MultiGroupService multiGroupService;
    @Autowired
    private MultiGroupTreeNodeService multiGroupTreeNodeService;
    @Autowired
    private OrgFacadeService orgFacadeService;
    @Autowired
    private UserInfoFacadeService userInfoFacadeService;

    public static Set<String> getUserOrgElementIds(OrgUserVo user, MultiOrgService multiOrgService) {
        Set<String> orgIdSet = Sets.newHashSet();
        if (user != null) {
            List<OrgUserJobDto> jobList = user.getJobList();
            if (jobList != null) {
                for (OrgUserJobDto jobDto : jobList) {
                    String jobIdPath = jobDto.getOrgTreeNodeDto().getEleIdPath();
                    String[] ids = jobIdPath.split(MultiOrgService.PATH_SPLIT_SYSMBOL);
                    if (ids != null) {
                        orgIdSet.addAll(Arrays.asList(ids));
                    }
                    // 获取职位对应的职务ID
                    String jobId = jobDto.getOrgTreeNodeDto().getEleId();
                    MultiOrgJobDuty duty = multiOrgService.getJobDutyByJobId(jobId);
                    if (duty != null) {
                        orgIdSet.add(duty.getDutyId());
                    }
                }
            }
        }
        return orgIdSet;
    }

    // 判断是否多组织的元素节点
    public static boolean isMultiOrgEleNode(String id) {
        return MultiOrgElement.isValidElementId(id);
    }

    /**
     * 获取委托人ID在指定业务类型下业务内容的职务代理人ID列表
     *
     * @param userId       委托人ID
     * @param businessType 业务类型
     * @param content      业务内容
     * @param root
     * @return 职务代理人ID列表
     */
    @Override
    public List<String> getDutyAgentIds(String userId, String businessType, String content, Map<Object, Object> root) {
        return dutyAgentService.getDutyAgentIds(userId, businessType, content, root);
    }

    /**
     * 根据委托人ID列表、业务类型获取当前有效的职务代理配置信息
     *
     * @param userId
     * @param businessType
     * @param content
     * @param root
     * @return
     */
    @Override
    public List<DutyAgent> getDutyAgents(List<String> consignorIds, String businessType) {
        return dutyAgentService.getDutyAgents(consignorIds, businessType);
    }

    /**
     * 对用户根据职级高低进行排序，返回排序后的用户ID列表
     *
     * @param userIds
     * @return
     */
    @Override
    public com.wellsoft.pt.org.support.UsersGrade orderedUserIdsByJobGrade(Collection<String> userIds) {
        return jobService.orderedUserIdsByJobGrade(userIds);
    }

    // 根据用户ID获取用户账号信息
    @Override
    public MultiOrgUserAccount getAccountByUserId(String userId) {
        return this.multiOrgUserService.getAccountByUserId(userId);
    }

    // 根据账号UUID,获取账号信息
    @Override
    public MultiOrgUserAccount getAccountByUuid(String userUuid) {
        return this.multiOrgUserService.getAccountByUuid(userUuid);
    }

    // 根据用户登录名获取用户账号信息
    @Override
    public MultiOrgUserAccount getAccountByLoginName(String loginName) {
        return this.multiOrgUserService.getUserAccountByLoginName(loginName);
    }

    // 获取用户完整信息
    @Override
    public OrgUserVo getUserVoById(String userId) {
        return this.multiOrgUserService.getUserById(userId);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.multi.org.facade.service.OrgApiFacade#getUserVoById(java.lang.String, boolean, boolean, boolean)
     */
    @Override
    public OrgUserVo getUserVoById(String userId, boolean includeJobInfo, boolean includeWorkInfo,
                                   boolean includeRoleInfo) {
        return this.multiOrgUserService.getUserById(userId, includeJobInfo, includeWorkInfo, includeRoleInfo);
    }

    // 获取平台管理员
    @Override
    public OrgUserVo getPTAdmin() {
        String adminUserId = MultiOrgUserAccount.PT_ACCOUNT_ID;
        OrgUserVo vo = new OrgUserVo();
        vo.setId(adminUserId);
        vo.setUserName("平台管理员");
        vo.setLoginName("admin");
        vo.setPassword("0");
        vo.setSystemUnitId(MultiOrgSystemUnit.PT_ID);
        MultiOrgUserAccount orgUserAccount = this.multiOrgUserAccountService.getAccountById(adminUserId);
        vo.setUuid(orgUserAccount.getUuid());
        return vo;
    }

    // 获取指定系统单位的所有管理员
    @Override
    public List<String> queryAllAdminIdsByUnitId(String unitId) {
        List<MultiOrgUserAccount> list = this.multiOrgUserService.queryAllAdminIdsBySystemUnitId(unitId);
        List<String> ids = new ArrayList<String>();
        if (!CollectionUtils.isEmpty(list)) {
            for (MultiOrgUserAccount a : list) {
                ids.add(a.getId());
            }
        }
        return ids;
    }

    // 批量获取用户账号列表
    @Override
    public List<MultiOrgUserAccount> queryUserAccountListByIds(Collection<String> userIds) {
        List<MultiOrgUserAccount> list = new ArrayList<MultiOrgUserAccount>();
        if (!CollectionUtils.isEmpty(userIds)) {
            for (String id : userIds) {
                MultiOrgUserAccount a = this.getAccountByUserId(id);
                if (a != null) {
                    list.add(a);
                }
            }
        }
        return list;
    }

    // 获取所有的账号
    @Override
    public List<MultiOrgUserAccount> queryAllAccountOfUnit(String systemUnitId) {
        return this.multiOrgUserService.queryAllAccountOfUnit(systemUnitId);
    }

    // 获取用户的直属上级领导
    @Override
    public Set<String> queryUserDirectLeaderList(String userId) {
        Set<String> directLeaderIdSet = new HashSet<>();
        MultiOrgUserWorkInfo userWorkInfo = multiOrgUserWorkInfoService.getUserWorkInfo(userId);
        if (userWorkInfo != null && userWorkInfo.getDirectLeaderIds() != null) {
            String directLeaderIds = userWorkInfo.getDirectLeaderIds();
            String[] directLeaderIdSplit = directLeaderIds.split(";");
            for (String directLeaderId : directLeaderIdSplit) {
                String[] split = directLeaderId.split(MultiOrgService.PATH_SPLIT_SYSMBOL);
                // String versionId = split[0];
                String directLeaderOnlyId;
                if (split.length > 1) {
                    directLeaderOnlyId = split[1];
                } else {
                    directLeaderOnlyId = split[0];
                }

                if (directLeaderOnlyId.startsWith(IdPrefix.JOB.getValue())) {
                    List<MultiOrgUserWorkInfo> multiOrgUserWorkInfoList = multiOrgUserWorkInfoService
                            .getUserWorkInfosByJobId(directLeaderOnlyId);
                    for (MultiOrgUserWorkInfo multiOrgUserWorkInfo : multiOrgUserWorkInfoList) {
                        MultiOrgUserAccount multiOrgUserAccount = getAccountByUserId(multiOrgUserWorkInfo.getUserId());
                        if (multiOrgUserAccount.getIsForbidden() == 0) {
                            directLeaderIdSet.add(multiOrgUserWorkInfo.getUserId());
                        }
                    }
                } else if (directLeaderOnlyId.startsWith(IdPrefix.USER.getValue())) {
                    MultiOrgUserAccount multiOrgUserAccount = getAccountByUserId(directLeaderOnlyId);
                    if (multiOrgUserAccount.getIsForbidden() == 0) {
                        directLeaderIdSet.add(directLeaderOnlyId);
                    }
                }
            }
        }

        // 不包含自己
        if (!CollectionUtils.isEmpty(directLeaderIdSet)) {
            directLeaderIdSet.remove(userId);
        }
        return directLeaderIdSet;
    }

    // 获取用户的上级领导,以主职为准, isAll代表是否是所有上级领导
    @Override
    public Set<String> queryAllUserSuperiorLeaderList(String userId, String orgVersionId, boolean isAll) {
        List<OrgUserJobDto> jobs = this.multiOrgUserService.queryUserJobByOrgVersionId(userId, orgVersionId);
        if (CollectionUtils.isEmpty(jobs)) {
            return Sets.newHashSet();
        }
        Set<String> leaders = Sets.newHashSet();
        for (OrgUserJobDto job : jobs) {
            String jobIdPath = job.getOrgTreeNodeDto().getEleIdPath();
            leaders.addAll(this.queryUserSuperiorLeaderListByJobIdPath(jobIdPath, isAll));
        }
        // 不包含自己
        if (!CollectionUtils.isEmpty(leaders)) {
            leaders.remove(userId);
        }
        return leaders;
    }

    // 获取用户的上级领导,以主职为准, isAll代表是否是所有上级领导
    @Override
    public Set<String> queryUserSuperiorLeaderList(String userId, String orgVersionId, boolean isAll) {
        List<OrgUserJobDto> jobs = this.multiOrgUserService.queryUserJobByOrgVersionId(userId, orgVersionId);
        if (CollectionUtils.isEmpty(jobs)) {
            return Sets.newHashSet();
        }
        String jobIdPath = jobs.get(0).getOrgTreeNodeDto().getEleIdPath();
        Set<String> leaders = this.queryUserSuperiorLeaderListByJobIdPath(jobIdPath, isAll);
        // 不包含自己
        if (!CollectionUtils.isEmpty(leaders)) {
            leaders.remove(userId);
        }
        return leaders;
    }

    // 获取用户的上级领导,
    // 依次往上查找，找到第一个有设置负责人的节点，并且该负责人有对应的具体用户，然后才返回
    @Override
    public Set<String> queryUserSuperiorLeaderListByJobIdPath(String jobIdPath, boolean isAll) {
        if (StringUtils.isBlank(jobIdPath)) {
            return Sets.newHashSet();
        }
        Set<String> allLeaders = new HashSet<String>();
        String[] path = jobIdPath.split(MultiOrgService.PATH_SPLIT_SYSMBOL);
        String orgVersionId = path[0];
        // 依次往上查找，找到第一个有设置负责人的节点，然后算出对应的领导就返回
        // 最后一个节点是职位，所以 i = path.length - 2
        for (int i = path.length - 2; i >= 0; i--) {
            String id = path[i];
            Set<String> leaders = this.queryBossLeaderUserListByNode(id, orgVersionId);
            // 如果节点有设置负责人，但是该负责人对应的职位下面没有用户的话，则需要继续查找上一节节点，如果有找到对应的用户，则返回
            if (CollectionUtils.isEmpty(leaders)) {
                continue;
            } else {
                allLeaders.addAll(leaders);
                // 如果不是需要全部，则break跳出循环
                if (!isAll) {
                    break;
                }
            }

        }
        return allLeaders;
    }

    // 获取用户所有对应的部门领导
    @Override
    public Set<String> queryAllUserDepartmentLeaderList(String userId, String orgVersionId) {
        List<OrgUserJobDto> jobs = this.multiOrgUserService.queryUserJobByOrgVersionId(userId, orgVersionId);
        if (CollectionUtils.isEmpty(jobs)) {
            return Sets.newHashSet();
        }
        Set<String> leaders = Sets.newHashSet();
        for (OrgUserJobDto job : jobs) {
            String jobIdPath = job.getOrgTreeNodeDto().getEleIdPath();
            leaders.addAll(this.queryUserDepartmentLeaderListByJobIdPath(jobIdPath));
        }

        // 不能包含自己
        if (!CollectionUtils.isEmpty(leaders)) {
            leaders.remove(userId);
        }
        return leaders;
    }

    // 获取用户主职对应的部门领导
    @Override
    public Set<String> queryUserDepartmentLeaderList(String userId, String orgVersionId) {
        List<OrgUserJobDto> jobs = this.multiOrgUserService.queryUserJobByOrgVersionId(userId, orgVersionId);
        if (CollectionUtils.isEmpty(jobs)) {
            return Sets.newHashSet();
        }
        String jobIdPath = jobs.get(0).getOrgTreeNodeDto().getEleIdPath();
        // 不能包含自己
        Set<String> leaders = this.queryUserDepartmentLeaderListByJobIdPath(jobIdPath);
        if (!CollectionUtils.isEmpty(leaders)) {
            leaders.remove(userId);
        }
        return leaders;
    }

    // 获取指定职位对应的部门领导
    @Override
    public Set<String> queryUserDepartmentLeaderListByJobIdPath(String jobIdPath) {
        if (StringUtils.isBlank(jobIdPath)) {
            return Sets.newHashSet();
        }
        String[] path = jobIdPath.split(MultiOrgService.PATH_SPLIT_SYSMBOL);
        String orgVersionId = path[0];
        // 获取离的最近的部门ID
        String depId = MultiOrgTreeNode.getNearestEleIdByType(jobIdPath, IdPrefix.DEPARTMENT.getValue());
        if (StringUtils.isBlank(depId)) {
            return Sets.newHashSet();
        }
        return this.queryBossLeaderUserListByNode(depId, orgVersionId);
    }

    // 获取指定节点的负责人对应的用户账号
    @Override
    public Set<String> queryBossLeaderUserListByNode(String eleId, String orgVersionId) {
        return this.queryLeaderUserListByNodeAndType(eleId, orgVersionId, "boss");
    }

    // 获取用户主职对应的分管领导
    @Override
    public Set<String> queryAllUserBranchLeaderList(String userId, String orgVersionId) {
        List<OrgUserJobDto> jobs = this.multiOrgUserService.queryUserJobByOrgVersionId(userId, orgVersionId);
        if (CollectionUtils.isEmpty(jobs)) {
            return Sets.newHashSet();
        }
        Set<String> leaders = Sets.newHashSet();
        for (String leader : leaders) {
            String jobIdPath = jobs.get(0).getOrgTreeNodeDto().getEleIdPath();
            leaders.addAll(this.queryUserBranchLeaderListByJobIdPath(jobIdPath));
        }
        if (!CollectionUtils.isEmpty(leaders)) {
            leaders.remove(userId);
        }
        return leaders;
    }

    // 获取用户主职对应的分管领导
    @Override
    public Set<String> queryUserBranchLeaderList(String userId, String orgVersionId) {
        List<OrgUserJobDto> jobs = this.multiOrgUserService.queryUserJobByOrgVersionId(userId, orgVersionId);
        if (CollectionUtils.isEmpty(jobs)) {
            return Sets.newHashSet();
        }
        String jobIdPath = jobs.get(0).getOrgTreeNodeDto().getEleIdPath();
        Set<String> leaders = this.queryUserBranchLeaderListByJobIdPath(jobIdPath);
        if (!CollectionUtils.isEmpty(leaders)) {
            leaders.remove(userId);
        }
        return leaders;
    }

    // 获取指定职位对应的分管领导
    @Override
    public Set<String> queryUserBranchLeaderListByJobIdPath(String jobIdPath) {
        if (StringUtils.isBlank(jobIdPath)) {
            return Sets.newHashSet();
        }
        String[] path = jobIdPath.split(MultiOrgService.PATH_SPLIT_SYSMBOL);
        String orgVersionId = path[0];
        // 依次往上查找，找到第一个分管部门节点的职位，然后算出对应的用户就可以
        for (int i = path.length - 1; i >= 0; i--) {
            String id = path[i];
            Set<String> leaders = this.queryBranchLeaderUserListByNode(id, orgVersionId);
            // 如果节点有设置负责人，但是该负责人对应的职位下面没有用户的话，则需要继续查找上一节节点，如果有找到对应的用户，则返回
            if (CollectionUtils.isEmpty(leaders)) {
                continue;
            } else {
                return leaders;
            }

        }
        return Sets.newHashSet();
    }

    // 获取指定节点的分管领导对应的用户账号
    @Override
    public Set<String> queryBranchLeaderUserListByNode(String eleId, String orgVersionId) {
        return this.queryLeaderUserListByNodeAndType(eleId, orgVersionId, "branch");
    }

    private Set<String> queryLeaderUserListByNodeAndType(String eleId, String orgVersionId, String type) {
        // 找到领导对应的职位
        List<OrgTreeNodeDto> leaderNodes = null;
        if (type.equals("boss")) {
            leaderNodes = this.multiOrgService.queryBossLeaderNodeListByNode(eleId, orgVersionId);
        } else if (type.equals("branch")) {
            leaderNodes = this.multiOrgService.queryBranchLeaderNodeListByNode(eleId, orgVersionId);
        }
        // 通过对应的职位，找到对应的用户
        List<OrgUserTreeNodeDto> leaders = new ArrayList<OrgUserTreeNodeDto>();
        for (OrgTreeNodeDto orgTreeNodeDto : leaderNodes) {
            List<OrgUserTreeNodeDto> users = this.multiOrgUserService.queryUserByOrgTreeNode(orgTreeNodeDto.getEleId(),
                    orgVersionId);
            if (!CollectionUtils.isEmpty(users)) {
                leaders.addAll(users);
            }
        }
        Set<String> users = new HashSet<String>();
        if (!CollectionUtils.isEmpty(leaders)) {
            for (OrgUserTreeNodeDto userDto : leaders) {
                users.add(userDto.getUserId());
            }
        }
        if (type.equals("branch")) {
            users.addAll(this.multiOrgService.queryBranchLeaderUserIdListByNode(eleId, orgVersionId));
        }
        return users;
    }

    // 根据群组ID,获取群组信息
    @Override
    public MultiOrgGroup getGroupById(String groupId) {
        return this.multiOrgGroupFacade.getGroupById(groupId);
    }

    // 根据群组ID,获取群组完整信息，包括成员列表和角色关系
    @Override
    public OrgGroupVo getGroupVoById(String groupId) {
        return this.multiOrgGroupFacade.getGroupVoById(groupId);
    }

    @Override
    public HashMap<String, String> getInternetUsersByLoginNames(List<String> loginNames) {
        HashMap<String, String> recipients_all_map = new HashMap<>();

        if (CollectionUtils.isNotEmpty(loginNames)) {
            List<UserInfoEntity> userInfoEntities = userInfoService.listByLoginNames(loginNames);
            for (UserInfoEntity userInfoEntity : userInfoEntities) {
                recipients_all_map.put(userInfoEntity.getLoginName(), userInfoEntity.getUserName());
            }
        }

        return recipients_all_map;
    }

    /**
     * 通过组织相关ids获得对应的用户
     *
     * @param orgIds 组织ID集合
     * @return 组织下用户
     */
    @Override
    public HashMap<String, String> getUsersByOrgIds(String orgIds) {
        if (StringUtils.isEmpty(orgIds)) {
            return new LinkedHashMap<String, String>(0);
        }
        String[] orgidls = orgIds.split(";");
        return getUsersByOrgIds(orgidls);
    }

    // 同上
    @Override
    public HashMap<String, String> getUsersByOrgIds(List<String> orgIds) {
        if (orgIds == null) {
            return new HashMap<String, String>();
        }
        String[] orgidls = orgIds.toArray(new String[0]);
        return getUsersByOrgIds(orgidls);
    }

    private HashMap<String, String> getUsersByOrgIds(String[] orgIds) {
        List<String> userIds = new ArrayList<String>();

        HashMap<String, String> userMaps = new LinkedHashMap<String, String>();
        // 筛选出互联网用户
        Map<String, String> internetUserNames = userInfoService.getInternetUserNamesByLoginNames(orgIds);
        for (String orgId : orgIds) {
            if (internetUserNames.containsKey(orgId)) {
                // 互联网用户
                userMaps.put(orgId, internetUserNames.get(orgId));
            } else if (orgId.startsWith(IdPrefix.ORG_VERSION.getValue())) { // 6.0的新格式
                String orgVersionId = orgId;
                String eleId = orgId;
                if (orgId.indexOf(MultiOrgService.PATH_SPLIT_SYSMBOL) > -1) {
                    String[] idAndVersion = orgId.split(MultiOrgService.PATH_SPLIT_SYSMBOL);
                    orgVersionId = idAndVersion[0];
                    eleId = idAndVersion[1];
                }
                Set<String> users = this.queryUserIdListByOrgId(orgVersionId, eleId, false);
                userIds.addAll(users);
            } else if (orgId.startsWith(IdPrefix.USER.getValue())) {
                userIds.add(orgId);
            } else if (isMultiOrgEleNode(orgId)) { // 兼容老版本，没有版本ID,则取当前启用版本
                Set<String> users = this.queryUserIdListByOrgId(orgId, false);
                userIds.addAll(users);
            } else if (orgId.startsWith(IdPrefix.DUTY.getValue())) { // 职务
                List<MultiOrgVersion> vers = this
                        .queryCurrentActiveVersionListOfSystemUnit(SpringSecurityUtils.getCurrentUserUnitId());
                if (vers != null) {
                    for (MultiOrgVersion ver : vers) {
                        List<OrgJobDutyDto> jobs = this.multiOrgService.queryJobListByDutyAndVersionId(ver.getId(),
                                orgId);
                        if (!CollectionUtils.isEmpty(jobs)) {
                            for (OrgJobDutyDto jobDto : jobs) {
                                Set<String> set = this.queryUserIdListByOrgId(ver.getId(), jobDto.getEleId(), false);
                                if (set != null) {
                                    userIds.addAll(set);
                                }
                            }
                        }
                    }
                }
            } else if (orgId.startsWith(IdPrefix.GROUP.getValue())) { // 群组
                HashMap<String, String> users = this.queryAllUsesByGroupId(orgId);
                // 直接加到usermaps里面，就不需要绕到userIds中，重新getAccountByUserId
                if (users != null) {
                    userMaps.putAll(users);
                }
            } else if (IdPrefix.startsWithExternal(orgId)) {
                userIds.addAll(businessFacadeService.getUserByOrgUuidAndRoleUuid(null, orgId, orgId));
            } else {
                // bug#56881，错误格式的数据返回自身
                if (userAccountService.existAccountByLoginName(orgId)) {
                    userMaps.put(orgId, orgId);
                }
                logger.error("getUsersByOrgIds, 数据格式不对,orgId=" + orgId);
                // throw new RuntimeException("getUsersByOrgIds, 数据格式不对,orgId=" + orgId);
            }
        }
        if (CollectionUtils.isNotEmpty(userIds)) {
            Map<String, String> userMapByUserIds = this.multiOrgUserService.getUnforbiddenUserIdNames(userIds);
            // 按userIds的顺序放入userMaps
            if (MapUtils.isNotEmpty(userMapByUserIds)) {
                for (String userId : userIds) {
                    if (userMapByUserIds.containsKey(userId)) {
                        userMaps.put(userId, userMapByUserIds.get(userId));
                    }
                }
            }
            // if (userMapByUserIds != null && userMapByUserIds.size() > 0) {
            // userMaps.putAll(userMapByUserIds);
            // }
        }

        return userMaps;

    }

    // 通过群组的ID获取该群组下的所有用户ID
    @Override
    public HashMap<String, String> queryAllUsesByGroupId(String groupId) {
        OrgGroupVo g = this.multiOrgGroupFacade.getGroupVoById(groupId);
        if (g != null && StringUtils.isNotBlank(g.getMemberIdPaths())) {
            return this.getUsersByOrgIds(g.getMemberIdPaths());
        }
        return Maps.newHashMap();
    }

    // 获取指定组织节点下的用户列表,
    @Override
    public Set<MultiOrgUserAccount> queryUserListByOrgId(String orgVersionId, String eleId, boolean isInMyUnit) {
        Set<String> set = this.queryUserIdListByOrgId(orgVersionId, eleId, isInMyUnit);
        Set<MultiOrgUserAccount> userSet = new HashSet<MultiOrgUserAccount>();
        if (!CollectionUtils.isEmpty(set)) {
            List<MultiOrgUserAccount> list = this.queryUserAccountListByIds(set);
            userSet.addAll(list);
        }
        return userSet;
    }

    // 获取指定节点当前启用版本下的用户列表
    @Override
    public Set<String> queryUserIdListByOrgId(String eleId, boolean isInMyUnit) {
        OrgTreeNodeDto currVerNode = this.multiOrgService.getNodeOfCurrentVerisonByEleId(eleId);
        if (currVerNode != null) {
            return this.queryUserIdListByOrgId(currVerNode.getOrgVersionId(), eleId, isInMyUnit);
        }
        return Sets.newHashSet();
    }

    // 通过eleId, 获取当前正在使用的版本ID
    @Override
    public String getCurrentVersionByEleId(String eleId) {
        if (isMultiOrgEleNode(eleId)) {
            OrgTreeNodeDto currVerNode = this.multiOrgService.getNodeOfCurrentVerisonByEleId(eleId);
            if (currVerNode != null) {
                return currVerNode.getOrgVersionId();
            }
            return null;
        } else {
            throw new RuntimeException("getCurrentVersionByEleId, 数据格式不对,eleId=" + eleId);
        }
    }

    // 获取指定组织节点下的用户列表,
    @Override
    public Set<MultiOrgUserAccount> queryUserListByOrgId(String eleId, boolean isInMyUnit) {
        Set<String> set = this.queryUserIdListByOrgId(eleId, isInMyUnit);
        Set<MultiOrgUserAccount> userSet = new HashSet<MultiOrgUserAccount>();
        if (!CollectionUtils.isEmpty(set)) {
            List<MultiOrgUserAccount> list = this.queryUserAccountListByIds(set);
            userSet.addAll(list);
        }
        return userSet;
    }

    // TODO 需要完善群组的计算， 并且需要做一下缓存处理，从而提高查询效率
    // 获取指定组织节点下的用户列表,
    @Override
    public Set<String> queryUserIdListByOrgId(String orgVersionId, String orgId, boolean isInMyUnit) {
        Set<String> userSet = new HashSet<String>();
        if (orgId.startsWith(IdPrefix.USER.getValue())) {// 用户
            userSet.add(orgId);
        } else if (orgId.startsWith(IdPrefix.ORG_VERSION.getValue())) { // 组织版本元素节点
            List<String> userIds = this.multiOrgUserTreeNodeService.listUserIdsByVersionId(orgId);
            if (CollectionUtils.isNotEmpty(userIds)) {
                userSet.addAll(userIds);
            }
        } else if (isMultiOrgEleNode(orgId)) { // 组织元素节点
            List<String> userIds = this.multiOrgUserTreeNodeService.queryUserIdsByLikeElementId(orgId, orgVersionId);
            if (CollectionUtils.isNotEmpty(userIds)) {
                userSet.addAll(userIds);
            }
        } else if (orgId.startsWith(IdPrefix.DUTY.getValue())) {// 职务
            List<OrgJobDutyDto> jobs = this.multiOrgService.queryJobListByDutyAndVersionId(orgVersionId, orgId);
            if (!CollectionUtils.isEmpty(jobs)) {
                for (OrgJobDutyDto jobDto : jobs) {
                    Set<String> set = this.queryUserIdListByOrgId(orgVersionId, jobDto.getEleId(), isInMyUnit);
                    if (set != null) {
                        userSet.addAll(set);
                    }
                }
            }
        } else if (orgId.startsWith(IdPrefix.GROUP.getValue())) { // 群组
            HashMap<String, String> users = this.queryAllUsesByGroupId(orgId);
            if (users != null) {
                userSet.addAll(users.keySet());
            }
        } else if (IdPrefix.startsWithExternal(orgId)) {
            userSet.addAll(businessFacadeService.getUserByOrgUuidAndRoleUuid(null, orgId, orgId));
        } else {
            throw new RuntimeException("无效的eleId[" + orgId + "]!");
        }
        return userSet;
    }

    // 通过组织元素ID,获取元素信息
    @Override
    public MultiOrgElement getOrgElementById(String eleId) {
        return this.multiOrgService.getOrgElementById(eleId);
    }

    // 批量获取组织元素信息
    @Override
    public List<MultiOrgElement> queryOrgElementListByIds(Collection<String> eleIds) {
        return this.multiOrgService.queryOrgElementListByIds(eleIds);
    }

    // 根据职务id获得职务
    @Override
    public MultiOrgDuty getDutyById(String id) {
        return multiOrgService.getDutyById(id);
    }

    public BusinessCategoryOrgDto getBusinessById(String id) {
        return businessFacadeService.getBusinessById(id);
    }

    // 批量通过ID, 获取对应的名称
    @Override
    public HashMap<String, String> getNameByOrgEleIds(List<String> orgIds) {
        HashMap<String, String> map = new HashMap<String, String>();
        for (String orgId : orgIds) {
            String name = this.getNameByOrgEleId(orgId);
            map.put(orgId, name);
        }
        return map;
    }

    // 通过ID,获取对应的额名称
    @Override
    public String getNameByOrgEleId(String eleId) {
        UserInfoEntity userInfoEntity = userInfoService.getByLoginName(eleId);
        if (userInfoEntity != null) {
            return userInfoEntity.getUserName();
        }

        if (StringUtils.isBlank(eleId)) {
            throw new RuntimeException("组织ID为空!");
        } else if (eleId.startsWith(IdPrefix.ORG_VERSION.getValue())
                && eleId.contains(MultiOrgService.PATH_SPLIT_SYSMBOL)) {
            String[] eleIds = eleId.split(MultiOrgService.PATH_SPLIT_SYSMBOL);
            eleId = eleIds[eleIds.length - 1];
        }
        if (eleId.startsWith(IdPrefix.MULTI_GROUP.getValue())) {
            MultiGroup a = this.multiGroupService.getById(eleId);
            Assert.notNull(a, "集团不存在:" + eleId);
            return a.getName();
        } else if (eleId.startsWith(IdPrefix.MULTI_GROUP_CATEGORY.getValue())) {
            MultiGroupTreeNode a = this.multiGroupTreeNodeService.getById(eleId);
            Assert.notNull(a, "集团节点分类不存在:" + eleId);
            return a.getName();
            // 收件人是职务
        } else if (eleId.startsWith(IdPrefix.DUTY.getValue())) {
            MultiOrgDuty d = this.getDutyById(eleId);
            Assert.notNull(d, "职务不存在:" + eleId);
            return d.getName();
        } else if (eleId.startsWith(IdPrefix.SYSTEM_UNIT.getValue())) {
            MultiOrgSystemUnit a = this.getSystemUnitById(eleId);
            Assert.notNull(a, "系统单位不存在:" + eleId);
            return a.getName();
        } else if (eleId.startsWith(IdPrefix.USER.getValue())) {
            MultiOrgUserAccount a = this.multiOrgUserService.getAccountByUserId(eleId);
            Assert.notNull(a, "用户不存在:" + eleId);
            return a.getUserName();
            // 收件人是群组
        } else if (eleId.startsWith(IdPrefix.GROUP.getValue())) {
            MultiOrgGroup g = this.getGroupById(eleId);
            Assert.notNull(g, "群组不存在:" + eleId);
            return g.getName();
        } else if (IdPrefix.startsWithExternal(eleId)) {
            BusinessCategoryOrgDto d = this.getBusinessById(eleId);
            Assert.notNull(d, "外部单位/分类不存在:" + eleId);
            return d.getName();
        } else if (eleId.startsWith(IdPrefix.ROLE.getValue())) {
            Role role = roleFacadeService.getRoleById(eleId);
            Assert.notNull(role, "角色不存在:" + eleId);
            return role.getName();
        } else {
            // 收件人是组织元素
            MultiOrgElement ele = this.getOrgElementById(eleId);
            if (ele != null) {
                return ele.getName();
            } else {
                throw new RuntimeException("无效的eleId[" + eleId + "]!");
            }
        }
    }

    // 通过用户名称，获取用户名
    @Override
    public String getUserNameById(String userId) {
        MultiOrgUserAccount a = this.getAccountByUserId(userId);
        if (a != null) {
            return a.getUserName();
        }
        return null;
    }

    // 通过职位ID全路径，换算对应的职位名称全路径
    @Override
    public String getDepartmentNamePathByJobIdPath(String mainJobIdPath, boolean isNeedUnitName) {
        return multiOrgService.getDepartmentNamePathByJobIdPath(mainJobIdPath, isNeedUnitName);
    }

    // 通过职位ID全路径，换算对应的职位名称全路径
    @Override
    public String getBusinessUnitNameByJobIdPath(String jobIdPath) {
        String unitId = MultiOrgTreeNode.getNearestEleIdByType(jobIdPath, IdPrefix.BUSINESS_UNIT.getValue());
        MultiOrgElement ele = this.getOrgElementById(unitId);
        return ele == null ? null : ele.getName();
    }

    @Override
    public MultiOrgElement getBusinessUnitByJobIdPath(String jobIdPath) {
        String unitId = MultiOrgTreeNode.getNearestEleIdByType(jobIdPath, IdPrefix.BUSINESS_UNIT.getValue());
        MultiOrgElement ele = this.getOrgElementById(unitId);
        return ele;
    }

    // 通过停用的版本，获取当前正在启用的版本
    @Override
    public MultiOrgVersion getCurrentActiveVersionByOrgVersionId(String orgVersionId) {
        return this.multiOrgVersionService.getCurrentActiveVersionByOrgVersionId(orgVersionId);
    }

    // 返回用户主职部门ID
    @Override
    public String queryMainDepartmentIdListByUserId(String userId, String orgVersionId) {
        List<OrgUserJobDto> jobs = this.multiOrgUserService.queryUserJobByOrgVersionId(userId, orgVersionId);
        if (CollectionUtils.isEmpty(jobs)) {
            return null;
        }
        String depId = null;
        for (OrgUserJobDto orgUserJobDto : jobs) {
            if (orgUserJobDto.getIsMain() == 1) {
                if (orgUserJobDto.getOrgTreeNodeDto() != null
                        && StringUtils.isNotBlank(orgUserJobDto.getOrgTreeNodeDto().getEleIdPath())) {
                    String jobIdPath = orgUserJobDto.getOrgTreeNodeDto().getEleIdPath();
                    depId = MultiOrgTreeNode.getNearestEleIdByType(jobIdPath, IdPrefix.DEPARTMENT.getValue());
                    break;
                }
            }
        }
        return depId;
    }

    // 返回用户对应的部门ID
    @Override
    public Set<String> queryDepartmentIdListByUserId(String userId, String orgVersionId) {
        List<OrgUserJobDto> jobs = this.multiOrgUserService.queryUserJobByOrgVersionId(userId, orgVersionId);
        if (CollectionUtils.isEmpty(jobs)) {
            return Sets.newHashSet();
        }
        Set<String> set = new HashSet<String>();
        for (OrgUserJobDto orgUserJobDto : jobs) {
            if (orgUserJobDto.getOrgTreeNodeDto() != null
                    && StringUtils.isNotBlank(orgUserJobDto.getOrgTreeNodeDto().getEleIdPath())) {
                String jobIdPath = orgUserJobDto.getOrgTreeNodeDto().getEleIdPath();
                String depId = MultiOrgTreeNode.getNearestEleIdByType(jobIdPath, IdPrefix.DEPARTMENT.getValue());
                if (StringUtils.isNotBlank(depId)) {
                    set.add(depId);
                }
            }
        }
        return set;
    }

    // 根据用户ID及组织版本ID获取用户上级的部门ID
    @Override
    public Set<String> queryParentDepartmentIdsByUserId(String userId, String orgVersionId, String jobId) {
        List<OrgUserJobDto> jobs = this.multiOrgUserService.queryUserJobByOrgVersionId(userId, orgVersionId);
        if (CollectionUtils.isEmpty(jobs)) {
            return Sets.newHashSet();
        }
        Set<String> set = new HashSet<String>();

        for (OrgUserJobDto orgUserJobDto : jobs) {
            if (orgUserJobDto.getOrgTreeNodeDto() != null
                    && StringUtils.isNotBlank(orgUserJobDto.getOrgTreeNodeDto().getEleIdPath())
                    && orgUserJobDto.getOrgTreeNodeDto().getEleIdPath().indexOf(jobId) > -1) {
                String jobIdPath = orgUserJobDto.getOrgTreeNodeDto().getEleIdPath();
                String[] idPath = jobIdPath.split(MultiOrgService.PATH_SPLIT_SYSMBOL);
                int pos = 0;
                for (int i = idPath.length - 2; i >= 0; i--) {
                    String id = idPath[i];
                    // 部门节点或业务单位节点都算
                    if (id.startsWith(IdPrefix.DEPARTMENT.getValue())
                            || id.startsWith(IdPrefix.BUSINESS_UNIT.getValue())) {
                        pos++;
                    }
                    if (pos == 2) {
                        set.add(id);
                        break;
                    }
                }
            }
        }
        return set;
    }

    // 根据用户ID及组织版本ID获取用户上级的部门ID
    @Override
    public Set<String> queryParentDepartmentIdsByUserId(String userId, String orgVersionId) {
        List<OrgUserJobDto> jobs = this.multiOrgUserService.queryUserJobByOrgVersionId(userId, orgVersionId);
        if (CollectionUtils.isEmpty(jobs)) {
            return Sets.newHashSet();
        }
        Set<String> set = new HashSet<String>();

        for (OrgUserJobDto orgUserJobDto : jobs) {
            if (orgUserJobDto.getOrgTreeNodeDto() != null
                    && StringUtils.isNotBlank(orgUserJobDto.getOrgTreeNodeDto().getEleIdPath())) {
                String jobIdPath = orgUserJobDto.getOrgTreeNodeDto().getEleIdPath();
                String[] idPath = jobIdPath.split(MultiOrgService.PATH_SPLIT_SYSMBOL);
                int pos = 0;
                for (int i = idPath.length - 2; i >= 0; i--) {
                    String id = idPath[i];
                    // 部门节点或业务单位节点都算
                    if (id.startsWith(IdPrefix.DEPARTMENT.getValue())
                            || id.startsWith(IdPrefix.BUSINESS_UNIT.getValue())) {
                        pos++;
                    }
                    if (pos == 2) {
                        set.add(id);
                        break;
                    }
                }
            }
        }
        return set;
    }

    @Override
    public Set<String> queryRootDepartmentIdsByUserId(String userId, String orgVersionId) {
        List<OrgUserJobDto> jobs = this.multiOrgUserService.queryUserJobByOrgVersionId(userId, orgVersionId);
        if (CollectionUtils.isEmpty(jobs)) {
            return Sets.newHashSet();
        }
        Set<String> set = new HashSet<String>();
        for (OrgUserJobDto orgUserJobDto : jobs) {
            if (orgUserJobDto.getOrgTreeNodeDto() != null
                    && StringUtils.isNotBlank(orgUserJobDto.getOrgTreeNodeDto().getEleIdPath())) {
                String jobIdPath = orgUserJobDto.getOrgTreeNodeDto().getEleIdPath();
                String[] idPath = jobIdPath.split(MultiOrgService.PATH_SPLIT_SYSMBOL);
                for (String id : idPath) {
                    if (id.startsWith(IdPrefix.DEPARTMENT.getValue())) {
                        set.add(id);
                        break;
                    }
                }
            }
        }
        return set;
    }

    @Override
    public Set<String> queryMainRootDepartmentIdsByUserId(String userId, String orgVersionId) {
        List<OrgUserJobDto> jobs = this.multiOrgUserService.queryUserJobByOrgVersionId(userId, orgVersionId);
        if (CollectionUtils.isEmpty(jobs)) {
            return Sets.newHashSet();
        }
        Set<String> set = new HashSet<String>();
        for (OrgUserJobDto orgUserJobDto : jobs) {
            if (orgUserJobDto.getIsMain() == 1) {
                if (orgUserJobDto.getOrgTreeNodeDto() != null
                        && StringUtils.isNotBlank(orgUserJobDto.getOrgTreeNodeDto().getEleIdPath())) {
                    String jobIdPath = orgUserJobDto.getOrgTreeNodeDto().getEleIdPath();
                    String[] idPath = jobIdPath.split(MultiOrgService.PATH_SPLIT_SYSMBOL);
                    for (String id : idPath) {
                        if (id.startsWith(IdPrefix.DEPARTMENT.getValue())) {
                            set.add(id);
                            break;
                        }
                    }
                }
            }

        }
        return set;
    }

    @Override
    public Set<String> queryRootDepartmentIdsByUserId(String userId, String orgVersionId, String jobId) {
        List<OrgUserJobDto> jobs = this.multiOrgUserService.queryUserJobByOrgVersionId(userId, orgVersionId);
        if (CollectionUtils.isEmpty(jobs)) {
            return Sets.newHashSet();
        }
        Set<String> set = new HashSet<String>();
        for (OrgUserJobDto orgUserJobDto : jobs) {
            if (orgUserJobDto.getOrgTreeNodeDto() != null
                    && StringUtils.isNotBlank(orgUserJobDto.getOrgTreeNodeDto().getEleIdPath())
                    && orgUserJobDto.getOrgTreeNodeDto().getEleIdPath().indexOf(jobId) > -1) {
                String jobIdPath = orgUserJobDto.getOrgTreeNodeDto().getEleIdPath();
                String[] idPath = jobIdPath.split(MultiOrgService.PATH_SPLIT_SYSMBOL);
                for (String id : idPath) {
                    if (id.startsWith(IdPrefix.DEPARTMENT.getValue())) {
                        set.add(id);
                        break;
                    }
                }
            }
        }
        return set;
    }

    @Override
    public Set<String> queryBizUnitIdsByUserId(String userId, String orgVersionId, String jobId) {
        List<OrgUserJobDto> jobs = this.multiOrgUserService.queryUserJobByOrgVersionId(userId, orgVersionId);
        if (CollectionUtils.isEmpty(jobs)) {
            return Sets.newHashSet();
        }
        Set<String> set = new HashSet<String>();
        for (OrgUserJobDto orgUserJobDto : jobs) {
            if (orgUserJobDto.getOrgTreeNodeDto() != null
                    && StringUtils.isNotBlank(orgUserJobDto.getOrgTreeNodeDto().getEleIdPath())
                    && orgUserJobDto.getOrgTreeNodeDto().getEleIdPath().indexOf(jobId) > -1) {
                String jobIdPath = orgUserJobDto.getOrgTreeNodeDto().getEleIdPath();
                String depId = MultiOrgTreeNode.getNearestEleIdByType(jobIdPath, IdPrefix.BUSINESS_UNIT.getValue());
                if (StringUtils.isNotBlank(depId)) {
                    set.add(depId);
                }
            }
        }
        return set;
    }

    @Override
    public Set<String> queryBizUnitIdsByUserId(String userId, String orgVersionId) {
        List<OrgUserJobDto> jobs = this.multiOrgUserService.queryUserJobByOrgVersionId(userId, orgVersionId);
        if (CollectionUtils.isEmpty(jobs)) {
            return Sets.newHashSet();
        }
        Set<String> set = new HashSet<String>();
        for (OrgUserJobDto orgUserJobDto : jobs) {
            if (orgUserJobDto.getOrgTreeNodeDto() != null
                    && StringUtils.isNotBlank(orgUserJobDto.getOrgTreeNodeDto().getEleIdPath())) {
                String jobIdPath = orgUserJobDto.getOrgTreeNodeDto().getEleIdPath();
                String depId = MultiOrgTreeNode.getNearestEleIdByType(jobIdPath, IdPrefix.BUSINESS_UNIT.getValue());
                if (StringUtils.isNotBlank(depId)) {
                    set.add(depId);
                }
            }

        }
        return set;
    }

    @Override
    public Set<String> queryMainBizUnitIdsByUserId(String userId, String orgVersionId) {
        List<OrgUserJobDto> jobs = this.multiOrgUserService.queryUserJobByOrgVersionId(userId, orgVersionId);
        if (CollectionUtils.isEmpty(jobs)) {
            return Sets.newHashSet();
        }
        Set<String> set = new HashSet<String>();
        for (OrgUserJobDto orgUserJobDto : jobs) {
            if (orgUserJobDto.getIsMain() == 1) {
                if (orgUserJobDto.getOrgTreeNodeDto() != null
                        && StringUtils.isNotBlank(orgUserJobDto.getOrgTreeNodeDto().getEleIdPath())) {
                    String jobIdPath = orgUserJobDto.getOrgTreeNodeDto().getEleIdPath();
                    String depId = MultiOrgTreeNode.getNearestEleIdByType(jobIdPath, IdPrefix.BUSINESS_UNIT.getValue());
                    if (StringUtils.isNotBlank(depId)) {
                        set.add(depId);
                    }
                }
            }

        }
        return set;
    }

    // 获取用户所在部门的所有用户
    @Override
    public Set<String> queryDepartmentUserIdsByUserId(String userId, String orgVersionId) {
        Set<String> userIds = new HashSet<String>();
        Set<String> departmentIds = this.queryDepartmentIdListByUserId(userId, orgVersionId);
        if (departmentIds != null) {
            for (String deptId : departmentIds) {
                userIds.addAll(this.queryUserIdListByOrgId(orgVersionId, deptId, false));
            }
        }
        return userIds;
    }

    // 获取用户主职所在部门的所有用户
    @Override
    public Set<String> queryMainDepartmentUserIdsByUserId(String userId, String orgVersionId) {
        Set<String> userIds = new HashSet<String>();
        String departmentId = this.queryMainDepartmentIdListByUserId(userId, orgVersionId);
        if (departmentId != null) {
            userIds.addAll(this.queryUserIdListByOrgId(orgVersionId, departmentId, false));
        }
        return userIds;
    }

    // 返回用户的根部门用户
    @Override
    public List<String> queryRootDepartmentUserIdsByUserId(String userId, String orgVersionId) {
        Set<String> deptIds = this.queryRootDepartmentIdsByUserId(userId, orgVersionId);
        List<String> userList = Lists.newArrayList();
        if (deptIds != null) {
            for (String deptId : deptIds) {
                Set<String> users = this.queryUserIdListByOrgId(orgVersionId, deptId, false);
                if (users != null) {
                    userList.addAll(users);
                }
            }
        }
        return userList;
    }

    // 返回用户主职的根部门用户
    @Override
    public List<String> queryMainRootDepartmentUserIdsByUserId(String userId, String orgVersionId) {
        Set<String> deptIds = this.queryMainRootDepartmentIdsByUserId(userId, orgVersionId);
        List<String> userList = Lists.newArrayList();
        if (deptIds != null) {
            for (String deptId : deptIds) {
                Set<String> users = this.queryUserIdListByOrgId(orgVersionId, deptId, false);
                if (users != null) {
                    userList.addAll(users);
                }
            }
        }
        return userList;
    }

    // 返回用户的业务单位用户
    @Override
    public List<String> queryBizUnitUserIdsByUserId(String userId, String orgVersionId) {
        Set<String> ids = this.queryBizUnitIdsByUserId(userId, orgVersionId);
        List<String> userList = Lists.newArrayList();
        if (ids != null) {
            for (String eleId : ids) {
                Set<String> users = this.queryUserIdListByOrgId(orgVersionId, eleId, false);
                if (users != null) {
                    userList.addAll(users);
                }
            }
        }
        return userList;
    }

    // 返回用户主职的业务单位用户
    @Override
    public List<String> queryMainBizUnitUserIdsByUserId(String userId, String orgVersionId) {
        Set<String> ids = this.queryMainBizUnitIdsByUserId(userId, orgVersionId);
        List<String> userList = Lists.newArrayList();
        if (ids != null) {
            for (String eleId : ids) {
                Set<String> users = this.queryUserIdListByOrgId(orgVersionId, eleId, false);
                if (users != null) {
                    userList.addAll(users);
                }
            }
        }
        return userList;
    }

    // 通过名字，模糊搜索用户
    @Override
    public List<String> getOrgIdsLikeName(String name) {
        return this.multiOrgUserService.queryUserIdsLikeName(name);
    }

    // 判定用户ID是否属于所选的部门/群组ID/职位/职务，多个部门/群组用;分割
    @Override
    public boolean isMemberOf(String userId, String memberOf) {
        if (StringUtils.isBlank(memberOf)) {
            return false;
        }
        if (memberOf.contains(userId)) {
            return true;
        }
        String[] ids = StringUtils.split(memberOf, Separator.SEMICOLON.getValue());
        for (String id : ids) {
            // isMemberOfGroup 是群组时
            if (id.startsWith("G")) {
                Set<String> groupIds = Sets.newHashSet();
                groupIds.add(id);
                return this.isMemberOfGroup(userId, groupIds);
            } else {
                Set<String> users = this.queryUserIdListByOrgId(id, true);
                if (users != null && users.contains(userId)) {
                    return true;
                }
            }

        }
        return false;
    }

    @Override
    public boolean isMemberOf(String userId, String[] memberOf, String jobId) {
        if (ArrayUtils.contains(memberOf, userId)) {
            return true;
        }
        Set<String> groupIds = Sets.newHashSet();
        Set<String> dutyIds = Sets.newHashSet();
        Set<String> externalIds = Sets.newHashSet();
        Set<String> jobRelaIds = Sets.newHashSet();
        for (String r : memberOf) {
            if (StringUtils.isBlank(r) || !CharUtils.isAsciiAlpha(r.charAt(0))) {
                continue;
            }
            if (r.startsWith(IdPrefix.GROUP.getValue())) {
                groupIds.add(r);
            } else if (r.startsWith(IdPrefix.DUTY.getValue())) {
                dutyIds.add(r);
            } else if (IdPrefix.startsWithExternal(r)) {
                externalIds.add(r);
            } else {
                jobRelaIds.add(r);
            }
        }
        if (CollectionUtils.isNotEmpty(jobRelaIds)) {
            // 要根据流程的职位选择判断
            for (String j : jobRelaIds) {
                if (jobId != null && this.isMemberOfSelectedJob(userId, jobId, j)) {
                    return true;
                } else if (this.isMemberOfAllJob(userId, j)) {
                    return true;
                }
            }
        }
        if (CollectionUtils.isNotEmpty(dutyIds)) {
            if (jobId != null && this.isMemberOfSelectedJobDuty(userId, dutyIds, jobId)) {
                return true;
            } else if (this.isMemberOfAllJobDuty(userId, dutyIds)) {
                return true;
            }
        }
        // 判断是否是群组成员
        if (CollectionUtils.isNotEmpty(groupIds) && this.isMemberOfGroup(userId, groupIds)) {
            return true;
        }
        // 判断是否是业务通讯录成员
        if (CollectionUtils.isNotEmpty(externalIds) && this.isMemberOfExternal(userId, externalIds)) {
            return true;
        }
        return false;
    }

    // 获得用户相关的组织信息ID
    @Override
    public Set<String> getUserOrgIds(String userId) {
        // 找到用户的当前职位，然后通过职位ID全路径PATH，就可以知道该用户对应的所有组织元素节点
        OrgUserVo user = this.getUserVoById(userId);
        return this.getUserOrgIds(user);
    }

    @Override
    public Set<String> getUserOrgIds(OrgUserVo user) {
        Set<String> orgIdSet = Sets.newHashSet();
        Set<String> eleIds = getUserOrgElementIds(user, this.multiOrgService);
        if (CollectionUtils.isNotEmpty(eleIds)) {
            orgIdSet.addAll(eleIds);
        }

        // 添加上群组的信息
        if (CollectionUtils.isNotEmpty(eleIds)) {
            Set<String> memberIds = Sets.newHashSet(eleIds);
            // 需要把用户自己算进去
            memberIds.add(user.getId());
            for (String memberId : memberIds) {
                List<MultiOrgGroupMember> groups = this.multiOrgGroupFacade.queryGroupListByMemberId(memberId);
                if (CollectionUtils.isNotEmpty(groups)) {
                    for (MultiOrgGroupMember gm : groups) {
                        orgIdSet.add(gm.getGroupId());
                    }
                }
            }
        }

        return orgIdSet;
    }

    // 根据用户ID列表获取相应的手机号码，用于发送短信
    @Override
    public List<OrgUserDto> queryUserDtoListByIds(List<String> userIds) {
        return this.multiOrgUserService.queryUserDtoListByIds(userIds);
    }

    // 获取用户的完整角色，包括职位，部门，群组，职务所带的角色
    @Override
    public Map<String, Set<String>> queryAllRoleListByUser(String userId) {
        OrgUserVo userVo = this.getUserVoById(userId);
        if (userVo != null) {
            return this.queryAllRoleListByUser(userVo);
        }
        return null;
    }

    private void addRoleToMap(Map<String, Set<String>> map, String roleUuid, String from) {
        if (map.get(roleUuid) == null) {
            map.put(roleUuid, new HashSet<String>());
        }
        map.get(roleUuid).add(from);
    }

    // 获取用户的完整角色，包括职位，部门，群组，职务所带的角色
    @Override
    public Map<String, Set<String>> queryAllRoleListByUser(OrgUserVo user) {
        Map<String, Set<String>> role2from = Maps.newHashMap();
        if (user == null) {
            return role2from;
        }
        // 先获取用户自身的角色
        String userRoles = user.getRoleUuids();
        if (StringUtils.isNotBlank(userRoles)) {
            String[] roles = userRoles.split(";");
            for (String roleUuid : roles) {
                this.addRoleToMap(role2from, roleUuid, "账号");
            }
        }

        // 获取跟用户相关的所有组织节点, 包含了群组的信息，然后依次获取该节点对应的的角色信息
        Set<String> userOrgIds = this.getUserOrgIds(user);
        for (String eleId : userOrgIds) {
            if (eleId.startsWith(IdPrefix.GROUP.getValue())) {
                MultiOrgGroup group = this.getGroupById(eleId);
                // 只有公共群组才有角色信息
                if (group != null && group.getType() == MultiOrgGroup.TYPE_PUBLIC_GROUP) {
                    List<MultiOrgGroupRole> roleList = this.multiOrgGroupFacade.queryRoleListOfGroup(eleId);
                    if (CollectionUtils.isNotEmpty(roleList)) {
                        for (MultiOrgGroupRole gRole : roleList) {
                            String from = group == null ? eleId : group.getName();
                            this.addRoleToMap(role2from, gRole.getRoleUuid(), "群组:" + from);
                        }
                    }
                }
            } else if (eleId.startsWith(IdPrefix.DUTY.getValue())) {
                // 职务没有角色，直接跳过
                continue;
            } else {
                List<MultiOrgElementRole> roleList = this.multiOrgService.queryRoleListOfElement(eleId);
                if (CollectionUtils.isNotEmpty(roleList)) {
                    for (MultiOrgElementRole eleRole : roleList) {
                        MultiOrgElement ele = this.getOrgElementById(eleId);
                        String from = ele == null ? eleId : ele.getName();
                        this.addRoleToMap(role2from, eleRole.getRoleUuid(), "组织:" + from);
                    }
                }

            }

        }

        return role2from;
    }

    @Override
    @Transactional
    public List<UserRoleInfoDto> queryAllUserRoleInfoDtoList(String userId) {
        OrgUserVo userVo = this.getUserVoById(userId);
        if (userVo != null) {
            return this.queryAllUserRoleInfoDtoList(userVo, null);
        }
        return null;
    }

    @Override
    @Transactional
    public List<UserRoleInfoDto> queryAllUserRoleInfoDtoList(String userId, String[] roleUuids) {

        if (StringUtils.isBlank(userId)) {
            return this.queryAllUserRoleInfoDtoList((OrgUserVo) null, roleUuids);
        }
        OrgUserVo userVo = this.getUserVoById(userId);
        if (userVo != null) {
            return this.queryAllUserRoleInfoDtoList(userVo, roleUuids);
        }
        return null;
    }

    @Override
    @Transactional
    public List<UserRoleInfoDto> queryAllUserRoleInfoDtoList(OrgUserVo user, String[] roleUuids) {

        List<UserRoleInfoDto> userRoleInfoDtoList = new ArrayList<>();
        String[] roleUuidArr = new String[]{};
        // 先获取用户自身的角色
        if (roleUuids != null) {
            roleUuidArr = roleUuids;
        } else {
            if (user != null) {
                String userRoles = user.getRoleUuids();
                if (StringUtils.isNotBlank(userRoles)) {
                    roleUuidArr = userRoles.split(";");
                }
            }
        }
        for (String roleUuid : roleUuidArr) {
            userRoleInfoDtoList.add(new UserRoleInfoDto(roleUuid, "", "", "", "用户", ""));
        }

        if (user != null) {
            // 获取跟用户相关的所有组织节点, 包含了群组的信息，然后依次获取该节点对应的的角色信息
            Set<String> userOrgIds = this.getUserOrgIds(user);
            for (String eleId : userOrgIds) {
                if (eleId.startsWith(IdPrefix.GROUP.getValue())) {
                    MultiOrgGroup group = this.getGroupById(eleId);
                    // 只有公共群组才有角色信息
                    if (group != null && group.getType() == MultiOrgGroup.TYPE_PUBLIC_GROUP) {
                        List<MultiOrgGroupRole> roleList = this.multiOrgGroupFacade.queryRoleListOfGroup(eleId);
                        if (CollectionUtils.isNotEmpty(roleList)) {
                            for (MultiOrgGroupRole gRole : roleList) {
                                userRoleInfoDtoList.add(
                                        new UserRoleInfoDto(gRole.getRoleUuid(), "", "", "", "群组", group.getName()));
                            }
                        }
                    }
                } else if (eleId.startsWith(IdPrefix.DUTY.getValue())) {
                    // 职务没有角色，直接跳过
                    continue;
                } else {
                    List<MultiOrgElementRole> roleList = this.multiOrgService.queryRoleListOfElement(eleId);
                    if (CollectionUtils.isNotEmpty(roleList)) {
                        for (MultiOrgElementRole eleRole : roleList) {
                            MultiOrgElement ele = this.getOrgElementById(eleId);
                            if (ele != null) {
                                List<MultiOrgTreeNode> multiOrgTreeNodeList = multiOrgTreeNodeService
                                        .queryNodeByEleId(ele.getId());
                                String orgName = "";
                                if (CollectionUtils.isNotEmpty(multiOrgTreeNodeList)) {
                                    orgName = this
                                            .getEleNamePathByEleIdPath(multiOrgTreeNodeList.get(0).getEleIdPath());
                                }
                                userRoleInfoDtoList
                                        .add(new UserRoleInfoDto(eleRole.getRoleUuid(), "", "", "", "组织", orgName));
                            }
                        }
                    }

                }
            }
        }

        List<String> roleUuidList = new ArrayList<>();
        for (UserRoleInfoDto userRoleInfoDto : userRoleInfoDtoList) {
            roleUuidList.add(userRoleInfoDto.getRoleUuid());
        }

        // 父角色
        List<Role> roles = roleFacadeService.getByUuids(roleUuidList);
        for (Role role : roles) {
            obtainNestedRole(userRoleInfoDtoList, role);
        }

        roleUuidList = new ArrayList<>();
        for (UserRoleInfoDto userRoleInfoDto : userRoleInfoDtoList) {
            roleUuidList.add(userRoleInfoDto.getRoleUuid());
        }
        roles = roleFacadeService.getByUuids(roleUuidList);
        Map<String, Role> roleMap = new HashMap<>();
        for (Role role : roles) {
            roleMap.put(role.getUuid(), role);
        }

        // 过滤重复，设置roleId、roleCode、roleName
        List<String> uniqueStringList = new ArrayList<>();
        for (Iterator<UserRoleInfoDto> iterator = userRoleInfoDtoList.iterator(); iterator.hasNext(); ) {
            UserRoleInfoDto userRoleInfoDto = iterator.next();
            String uniqueString = userRoleInfoDto.getRoleUuid() + userRoleInfoDto.getCalculatePath();
            if (uniqueStringList.contains(uniqueString)) {
                iterator.remove();
                continue;
            }
            uniqueStringList.add(uniqueString);

            Role role = roleMap.get(userRoleInfoDto.getRoleUuid());
            if (role == null || role.getSystemDef() == 1) {
                iterator.remove();
                continue;
            }
            userRoleInfoDto.setRoleId(role.getId());
            userRoleInfoDto.setRoleCode(role.getCode());
            userRoleInfoDto.setRoleName(role.getName());
        }

        return userRoleInfoDtoList;
    }

    private void obtainNestedRole(List<UserRoleInfoDto> userRoleInfoDtoList, Role role) {
        for (NestedRole nestedRole : role.getNestedRoles()) {
            Role childRole = roleFacadeService.get(nestedRole.getRoleUuid());
            if (childRole.getSystemDef() == 0) {
                userRoleInfoDtoList
                        .add(new UserRoleInfoDto(nestedRole.getRoleUuid(), "", "", "", "父角色", role.getName()));
            }
            obtainNestedRole(userRoleInfoDtoList, childRole);
        }
    }

    @Override
    public MultiOrgSystemUnit getSystemUnitById(String unitId) {
        return this.multiOrgService.getSystemUnitById(unitId);
    }

    @Override
    public OrgUserVo modifyUserVo(OrgUserVo userVo) {
        return this.multiOrgUserService.modifyUser(userVo);
    }

    @Override
    public MultiOrgUserWorkInfo getUserWorkInfoByUserId(String userId) {
        MultiOrgUserWorkInfo entity = new MultiOrgUserWorkInfo();
        entity.setUserId(userId);
        return multiOrgUserWorkInfoService.getUserWorkInfo(userId);
    }

    // 获取单位管理员
    @Override
    public OrgUserVo getUnitAdmin(String unitId) {
        return this.multiOrgUserService.getUnitAdmin(unitId);
    }

    // 获取用户对应的归属单位正在启用的组织版本
    @Override
    public List<MultiOrgVersion> getCurrentOrgVersionByUserId(String userId) {
        MultiOrgUserAccount a = this.getAccountByUserId(userId);
        return this.multiOrgVersionService.queryCurrentActiveVersionListOfSystemUnit(a.getSystemUnitId());
    }

    @Override
    public Set<String> getCurrentOrgVersionByOrgId(Collection<String> orgIds) {
        Set<String> set = Sets.newHashSet();
        if (!CollectionUtils.isEmpty(orgIds)) {
            for (String orgId : orgIds) {
                if (isMultiOrgEleNode(orgId)) { // 组织元素
                    String verId = this.getCurrentVersionByEleId(orgId);
                    set.add(verId);
                } else if (orgId.startsWith(IdPrefix.USER.getValue())) { // 用户
                    List<MultiOrgVersion> list = this.getCurrentOrgVersionByUserId(orgId);
                    if (!CollectionUtils.isEmpty(list)) {
                        for (MultiOrgVersion v : list) {
                            set.add(v.getId());
                        }
                    }
                } else if (orgId.startsWith(IdPrefix.DUTY.getValue())) { // 职务
                    List<String> vs = this.getCurrentOrgVersionByDutyId(orgId);
                    if (vs != null) {
                        set.addAll(vs);
                    }
                }
            }
        }
        return set;
    }

    // 通过职务ID,获取该职务对应的正在使用的组织版本ID
    private List<String> getCurrentOrgVersionByDutyId(String dutyId) {
        List<OrgTreeNodeDto> currVerNodes = this.multiOrgService.queryJobNodeListOfCurrentVerisonByDutyId(dutyId);
        List<String> vs = Lists.newArrayList();
        if (currVerNodes != null) {
            for (OrgTreeNodeDto dto : currVerNodes) {
                vs.add(dto.getOrgVersionId());
            }
        }
        return vs;
    }

    @Override
    public List<MultiOrgVersion> queryCurrentActiveVersionListOfSystemUnit(String unitId) {
        return this.multiOrgVersionService.queryCurrentActiveVersionListOfSystemUnit(unitId);
    }

    @Override
    public List<MultiOrgSystemUnit> queryAllSystemUnitList() {
        return multiOrgService.queryAllSystemUnitList();
    }

    // 获取用户的部门下属
    @Override
    public Set<String> queryBossUnderlingUserList(String userId, String orgVersionId) {
        return this.queryUnderlingUserListByType(userId, orgVersionId, "boss");
    }

    // 获取分管下属
    @Override
    public Set<String> queryBranchUnderlingUserList(String userId, String orgVersionId) {
        Set<String> users = this.queryUnderlingUserListByType(userId, orgVersionId, "branch");
        // 分管领导可以设置用户（非职位ID）的情况
        users.addAll(this.queryUnderlingUserListByJobIdPathAndType(userId, orgVersionId, "branch"));
        return users;
    }

    // 获取用户的部门下属或分管下属
    private Set<String> queryUnderlingUserListByType(String userId, String orgVersionId, String type) {
        Set<String> users = Sets.newHashSet();
        List<OrgUserJobDto> jobs = this.multiOrgUserService.queryUserJobByOrgVersionId(userId, orgVersionId);
        if (!CollectionUtils.isEmpty(jobs)) {
            for (OrgUserJobDto jobDto : jobs) {
                String jobId = jobDto.getOrgTreeNodeDto().getEleId();
                String versionId = jobDto.getOrgTreeNodeDto().getOrgVersionId();
                Set<String> u = this.queryUnderlingUserListByJobIdPathAndType(jobId, versionId, type);
                if (u != null) {
                    users.addAll(u);
                }
            }
        }
        return users;
    }

    private Set<String> queryUnderlingUserListByJobIdPathAndType(String jobId, String versionId, String type) {
        Set<String> users = Sets.newHashSet();
        List<OrgTreeNodeDto> underlingOrgIds = null;
        if ("branch".equals(type)) {
            underlingOrgIds = this.multiOrgService.queryBranchUnderlingNodeListByJobId(jobId, versionId);
        } else if ("boss".equals(type)) {
            // 获取我负责的下属节点
            underlingOrgIds = this.multiOrgService.queryBossUnderlingNodeListByJobId(jobId, versionId);
        }
        if (!CollectionUtils.isEmpty(underlingOrgIds)) {
            for (OrgTreeNodeDto nodeDto : underlingOrgIds) {
                Set<String> u = this.queryUserIdListByOrgId(nodeDto.getOrgVersionId(), nodeDto.getEleId(), true);
                if (u != null) {
                    users.addAll(u);
                }
            }
        }
        return users;

    }

    @Override
    public Set<String> queryBossUnderlingUserListByJobIdPath(String jobIdPath) {
        String[] ids = jobIdPath.split(MultiOrgService.PATH_SPLIT_SYSMBOL);
        String versionId = ids[0];
        String jobId = ids[ids.length - 1];
        return this.queryUnderlingUserListByJobIdPathAndType(jobId, versionId, "boss");
    }

    @Override
    public Set<String> queryBranchUnderlingUserListByJobIdPath(String jobIdPath) {
        String[] ids = jobIdPath.split(MultiOrgService.PATH_SPLIT_SYSMBOL);
        String versionId = ids[0];
        String jobId = ids[ids.length - 1];
        return this.queryUnderlingUserListByJobIdPathAndType(jobId, versionId, "branch");
    }

    /**
     * 通过组织相关ids获得对应的用户
     *
     * @param orgIds 组织ID集合
     * @return 组织下用户
     */
    @Override
    public HashMap<String, String> getJobsByOrgIds(String orgIds) {
        if (StringUtils.isEmpty(orgIds)) {
            return new LinkedHashMap<String, String>(0);
        }
        String[] orgidls = orgIds.split(";");
        return getJobsByOrgIds(orgidls);
    }

    // 同上
    @Override
    public HashMap<String, String> getJobsByOrgIds(List<String> orgIds) {
        if (orgIds == null) {
            return new HashMap<String, String>();
        }
        String[] orgidls = orgIds.toArray(new String[0]);
        return getJobsByOrgIds(orgidls);
    }

    private HashMap<String, String> getJobsByOrgIds(String[] orgIds) {
        HashMap<String, String> jobMaps = new LinkedHashMap<String, String>();
        for (String orgId : orgIds) {
            if (orgId.startsWith(IdPrefix.ORG_VERSION.getValue())) { // 6.0的新格式
                String[] idAndVersion = orgId.split(MultiOrgService.PATH_SPLIT_SYSMBOL);
                String orgVersionId = idAndVersion[0];
                String eleId = idAndVersion[1];
                HashMap<String, String> map = this.queryJobIdListByOrgId(orgVersionId, eleId);
                jobMaps.putAll(map);
            } else if (orgId.startsWith(IdPrefix.USER.getValue())) {
                OrgUserVo user = this.getUserVoById(orgId);
                if (!CollectionUtils.isEmpty(user.getJobList())) {
                    for (OrgUserJobDto job : user.getJobList()) {
                        jobMaps.put(job.getOrgTreeNodeDto().getEleId(), job.getOrgTreeNodeDto().getEleName());
                    }
                }
            } else if (isMultiOrgEleNode(orgId)) { // 兼容老版本，没有版本ID,则取当前启用版本
                HashMap<String, String> map = this.queryJobIdListByOrgId(orgId);
                jobMaps.putAll(map);
            } else if (orgId.startsWith(IdPrefix.DUTY.getValue())) {// 职务
                List<MultiOrgVersion> vers = this
                        .queryCurrentActiveVersionListOfSystemUnit(SpringSecurityUtils.getCurrentUserUnitId());
                if (vers != null) {
                    for (MultiOrgVersion ver : vers) {
                        List<OrgJobDutyDto> list = this.multiOrgService.queryJobListByDutyAndVersionId(ver.getId(),
                                orgId);
                        if (list != null) {
                            for (OrgJobDutyDto dto : list) {
                                jobMaps.put(dto.getEleId(), dto.getEleName());
                            }
                        }
                    }
                }
            } else {
                // bug#56881，错误格式的数据返回自身
                if (userAccountService.existAccountByLoginName(orgId)) {
                    jobMaps.put(orgId, orgId);
                }
                logger.error("getJobsByOrgIds, 数据格式不对,orgId=" + orgId);
                // throw new RuntimeException("getJobsByOrgIds, 数据格式不对,orgId=" + orgId);
            }
        }
        return jobMaps;
    }

    // 获取指定节点当前启用版本下的用户列表
    public HashMap<String, String> queryJobIdListByOrgId(String eleId) {
        OrgTreeNodeDto currVerNode = this.multiOrgService.getNodeOfCurrentVerisonByEleId(eleId);
        if (currVerNode != null) {
            return this.queryJobIdListByOrgId(currVerNode.getOrgVersionId(), eleId);
        }
        return Maps.newHashMap();
    }

    public HashMap<String, String> queryJobIdListByOrgId(String orgVersionId, String orgId) {
        HashMap<String, String> jobMap = Maps.newHashMap();
        if (orgId.startsWith(IdPrefix.USER.getValue())) {// 用户
            OrgUserVo user = this.getUserVoById(orgId);
            if (!CollectionUtils.isEmpty(user.getJobList())) {
                for (OrgUserJobDto job : user.getJobList()) {
                    jobMap.put(job.getOrgTreeNodeDto().getEleId(), job.getOrgTreeNodeDto().getEleName());
                }
            }
        } else if (isMultiOrgEleNode(orgId)) { // 组织元素节点
            List<OrgTreeNodeDto> jobList = this.multiOrgService.queryJobListByEleIdAndVersionId(orgVersionId, orgId);
            if (!CollectionUtils.isEmpty(jobList)) {
                for (OrgTreeNodeDto dto : jobList) {
                    jobMap.put(dto.getEleId(), dto.getName());
                }
            }
        } else if (orgId.startsWith(IdPrefix.DUTY.getValue())) {// 职务
            List<OrgJobDutyDto> jobs = this.multiOrgService.queryJobListByDutyAndVersionId(orgVersionId, orgId);
            if (!CollectionUtils.isEmpty(jobs)) {
                for (OrgJobDutyDto jobDto : jobs) {
                    jobMap.put(jobDto.getEleId(), jobDto.getEleName());
                }
            }
        } else if (orgId.startsWith(IdPrefix.GROUP.getValue())) { // 群组

        } else {
            throw new RuntimeException("无效的eleId[" + orgId + "]!");
        }
        return jobMap;
    }

    @Override
    public Map<String, MultiOrgElement> queryElementMapByEleIdPath(String idPath) {
        return this.multiOrgService.queryElementMapByEleIdPath(idPath);
    }

    @Override
    public OrgTreeNodeDto getNodeOfCurrentVerisonByEleId(String eleId) {
        return this.multiOrgService.getNodeOfCurrentVerisonByEleId(eleId);
    }

    @Override
    public String getEleIdPathByEleNamePath(String namePath, String orgVersionId) {
        OrgTreeNode treeNode = this.multiOrgService.getOrgAsTreeByVersionId(orgVersionId);
        if (treeNode != null) {
            Map<String, OrgTreeNode> map = treeNode.toMapByNamePath();
            OrgTreeNode node = map.get(namePath);
            if (node != null) {
                return node.getPath();
            }
        }
        return null;
    }

    @Override
    public String getEleIdPathByEleId(String eleId, String orgVersionId) {
        OrgTreeNodeDto dto = this.multiOrgTreeNodeService.getNodeByEleIdAndOrgVersion(eleId, orgVersionId);
        return dto.getEleIdPath();
    }

    @Override
    public List<OrgTreeNodeDto> queryAllNodeOfOrgVersionByEleIdPath(String verId, String eleIdPath) {
        return this.multiOrgTreeNodeService.queryAllNodeOfOrgVersionByEleIdPath(verId, eleIdPath);
    }

    @Override
    public List<OrgTreeNodeDto> queryAllNodeOfOrgVersionByEleIdPath(String verId, String eleIdPath, String eleType) {
        return this.multiOrgTreeNodeService.queryAllNodeOfOrgVersionByEleIdPath(verId, eleIdPath, eleType);
    }

    @Override
    public String getEleNamePathByEleIdPath(String idPath) {
        List<String> names = Lists.newArrayList();
        if (StringUtils.isNotBlank(idPath)) {
            String[] ids = idPath.split(MultiOrgService.PATH_SPLIT_SYSMBOL);
            for (String id : ids) {
                MultiOrgElement e = this.getOrgElementById(id);
                if (e != null) {
                    names.add(e.getName());
                }
            }
        }
        return StringUtils.join(names, "/");
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.multi.org.facade.service.OrgApiFacade#getSuperiorOrgIdsByOrgEleIdAndEleTypes(java.util.List, java.lang.String[])
     */
    @Override
    public List<String> getSuperiorOrgIdsByOrgEleIdAndEleTypes(List<String> eleIds, String... eleTypes) {
        List<String> superiorOrgIds = Lists.newArrayList();
        for (String eleId : eleIds) {
            // 用户结点没有对应的上级结点
            if (StringUtils.startsWith(eleId, IdPrefix.USER.getValue())) {
                continue;
            }
            // 获取结点路径
            String orgVersionId = getCurrentVersionByEleId(eleId);
            String eleIdPath = getEleIdPathByEleId(eleId, orgVersionId);
            // 取结点路径中同类型的结点
            String eleType = StringUtils.substring(eleId, 0, 1);
            List<String> eleTypeList = Lists.newArrayList(eleTypes);
            if (CollectionUtils.isEmpty(eleTypeList)) {
                eleTypeList.add(eleType);
            }
            List<String> idLists = Lists.newArrayList(StringUtils.split(eleIdPath, "/"));
            idLists.remove(eleId);
            for (String parentId : idLists) {
                for (String includeEleType : eleTypeList) {
                    if (StringUtils.startsWith(parentId, includeEleType)) {
                        superiorOrgIds.add(parentId);
                    }
                }
            }
        }
        return superiorOrgIds;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.multi.org.facade.service.OrgApiFacade#getSiblingOrgIdsByOrgEleIdAndEleTypes(java.util.List, java.lang.String[])
     */
    @Override
    public List<String> getSiblingOrgIdsByOrgEleIdAndEleTypes(List<String> eleIds, String... eleTypes) {
        List<String> siblingOrgIds = Lists.newArrayList();
        for (String eleId : eleIds) {
            // 用户结点处理
            if (StringUtils.startsWith(eleId, IdPrefix.USER.getValue())) {
                OrgUserVo user = this.getUserVoById(eleId);
                List<OrgUserJobDto> jobDtos = user.getJobList();
                for (OrgUserJobDto orgUserJobDto : jobDtos) {
                    OrgTreeNodeDto jobTreeNodeDto = orgUserJobDto.getOrgTreeNodeDto();
                    List<MultiOrgUserWorkInfo> workInfos = multiOrgUserWorkInfoService
                            .getUserWorkInfosByJobId(jobTreeNodeDto.getEleId());
                    for (MultiOrgUserWorkInfo multiOrgUserWorkInfo : workInfos) {
                        if (!StringUtils.equals(eleId, multiOrgUserWorkInfo.getUserId())) {
                            siblingOrgIds.add(multiOrgUserWorkInfo.getUserId());
                        }
                    }
                }
            } else {
                // 获取结点路径
                String orgVersionId = getCurrentVersionByEleId(eleId);
                String eleIdPath = getEleIdPathByEleId(eleId, orgVersionId);
                // 获取上级结点路径
                String parentEleIdPath = getParentEleIdPath(eleIdPath);
                String eleType = StringUtils.substring(eleId, 0, 1);
                List<String> eleTypeList = Lists.newArrayList(eleTypes);
                if (CollectionUtils.isEmpty(eleTypeList)) {
                    eleTypeList.add(eleType);
                }
                List<OrgTreeNodeDto> orgTreeNodeDtos = multiOrgTreeNodeService
                        .queryAllNodeOfOrgVersionByEleIdPathAndEleTypes(orgVersionId, parentEleIdPath,
                                eleTypeList.toArray(new String[0]));
                // 取结点路径中同类型的结点
                for (OrgTreeNodeDto orgTreeNodeDto : orgTreeNodeDtos) {
                    if (StringUtils.equals(parentEleIdPath, orgTreeNodeDto.getParentIdPath())
                            && !StringUtils.equals(eleId, orgTreeNodeDto.getEleId())) {
                        siblingOrgIds.add(orgTreeNodeDto.getEleId());
                    }
                }
            }
        }
        return siblingOrgIds;
    }

    /**
     * @param eleIdPath
     * @return
     */
    private String getParentEleIdPath(String eleIdPath) {
        String[] eleIdPaths = StringUtils.split(eleIdPath, "/");
        if (ArrayUtils.isEmpty(eleIdPaths)) {
            return StringUtils.EMPTY;
        }
        Object[] parentEleIdPaths = ArrayUtils.subarray(eleIdPaths, 0, eleIdPaths.length - 1);
        return StringUtils.join(parentEleIdPaths, "/");
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.multi.org.facade.service.OrgApiFacade#getSubordinateOrgIdsByOrgEleIdAndEleTypes(java.util.List, java.lang.String[])
     */
    @Override
    public List<String> getSubordinateOrgIdsByOrgEleIdAndEleTypes(List<String> eleIds, String... eleTypes) {
        List<String> subordinateOrgIds = Lists.newArrayList();
        for (String eleId : eleIds) {
            // 用户结点没有对应的下级结点
            if (StringUtils.startsWith(eleId, IdPrefix.USER.getValue())) {
                continue;
            }
            // 获取结点路径
            String orgVersionId = getCurrentVersionByEleId(eleId);
            String eleIdPath = getEleIdPathByEleId(eleId, orgVersionId);
            String eleType = StringUtils.substring(eleId, 0, 1);
            List<String> eleTypeList = Lists.newArrayList(eleTypes);
            if (CollectionUtils.isEmpty(eleTypeList)) {
                eleTypeList.add(eleType);
            }
            List<OrgTreeNodeDto> orgTreeNodeDtos = multiOrgTreeNodeService
                    .queryAllNodeOfOrgVersionByEleIdPathAndEleTypes(orgVersionId, eleIdPath,
                            eleTypeList.toArray(new String[0]));
            // 取结点路径中同类型的结点
            for (OrgTreeNodeDto orgTreeNodeDto : orgTreeNodeDtos) {
                if (!StringUtils.equals(eleId, orgTreeNodeDto.getEleId())) {
                    subordinateOrgIds.add(orgTreeNodeDto.getEleId());
                }
            }
        }
        return subordinateOrgIds;
    }

    @Override
    public String getMultiOrgElementAttrValue(String attrCode, String elementUuid) {
        MultiOrgElementAttrEntity attr = multiOrgElementAttrService.getByAttrCodeAndElementUuid(attrCode, elementUuid);
        return attr != null ? attr.getAttrValue() : null;
    }

    @Override
    public String getMultiOrgElementAttrValueById(String attrCode, String elementId) {
        if (elementId.startsWith(IdPrefix.SYSTEM_UNIT.getValue())) {
            MultiOrgSystemUnit unit = multiOrgService.getSystemUnitById(elementId);
            if (unit != null) {
                return this.getMultiOrgElementAttrValue(attrCode, unit.getUuid());
            }
        } else {
            String version = null;
            String id = elementId;
            if (elementId.startsWith(IdPrefix.ORG_VERSION.getValue())) {// 指定了版本号
                String[] idAndVersion = elementId.split(MultiOrgService.PATH_SPLIT_SYSMBOL);
                version = idAndVersion[0];
                id = idAndVersion[1];

            } else {// 未指定版本号，则获取当前生效的组织版本
                List<MultiOrgVersion> versions = this.multiOrgVersionService
                        .queryCurrentActiveVersionListOfSystemUnit(SpringSecurityUtils.getCurrentUserUnitId());
                if (CollectionUtils.isNotEmpty(versions)) {
                    version = versions.get(0).getVersion();
                }
            }

            if (id != null && version != null) {
                OrgTreeNodeDto nodeDto = this.multiOrgService.getNodeByEleIdAndOrgVersion(id, version);
                if (nodeDto != null) {
                    return this.getMultiOrgElementAttrValue(attrCode, nodeDto.getEleUuid());
                }
            }
        }
        return null;
    }

    @Override
    public MultiOrgSystemUnit getSystemUnitByCode(String unitCode) {
        return multiOrgSystemUnitService.getByCode(unitCode);
    }

    @Override
    public String getCurrentUserProperty(String propName) {
        return userPropertyService.getCurrentUserPropertyValue(propName);
    }

    @Override
    public String getUserProperty(String propName, String userUuid) {
        return userPropertyService.getUserPropertyValue(propName, userUuid);
    }

    @Override
    public void saveUserProperty(String prop, String value) {
        this.userPropertyService.saveUserPropertyValue(prop, value);
    }

    @Override
    public String getCurrentUserProperty(String userId, String propName) {
        return userPropertyService.getCurrentUserPropertyValue(userId, propName);
    }

    @Override
    public List<MultiOrgOption> getOrgOptionsByIds(String[] ids) {
        return multiOrgOptionService.getOrgOptionsByIds(ids);
    }

    private Set<String> queryUserDepartmentLeaderByJob(String userId, Boolean onlyMainJob, String jobId) {
        String eleIdPaths = this.getUserWorkJobEleIdPaths(userId);
        if (StringUtils.isBlank(eleIdPaths)) {
            return Collections.emptySet();
        }
        String[] paths = eleIdPaths.split(Separator.SEMICOLON.getValue());
        Set<String> departmentLeaders = Sets.newLinkedHashSet();
        for (String p : paths) {
            if (onlyMainJob == null && jobId == null) {
                departmentLeaders.addAll(this.queryUserDepartmentLeaderListByJobIdPath(p));
            } else if (jobId != null) {
                if (p.indexOf(jobId) != -1) {
                    departmentLeaders.addAll(this.queryUserDepartmentLeaderListByJobIdPath(p));
                    break;
                }
            } else if (BooleanUtils.isTrue(onlyMainJob)) {
                String[] slashs = p.split(Separator.SLASH.getValue());
                String vid = slashs[0];
                MultiOrgUserTreeNode treeNode = this.multiOrgUserTreeNodeService.queryUserJobByOrgVersionEleId(userId,
                        vid, slashs[slashs.length - 1]);
                if (treeNode == null) {
                    logger.error("无法查询到用户职位关联组织树信息，用户ID={}", userId);
                    break;
                }
                if (1 == treeNode.getIsMain()) {
                    // 主职
                    departmentLeaders.addAll(this.queryUserDepartmentLeaderListByJobIdPath(p));
                    break;
                }
            }
        }
        departmentLeaders.remove(userId);
        return departmentLeaders;
    }

    @Override
    public Set<String> queryUserAllJobDepartmentLeaderList(String userId) {
        return this.queryUserDepartmentLeaderByJob(userId, null, null);
    }

    @Override
    public Set<String> queryUserMainJobDepartmentLeaderList(String userId) {
        return this.queryUserDepartmentLeaderByJob(userId, true, null);
    }

    @Override
    public Set<String> queryUserJobDepartmentLeaderList(String userId, String jobId) {
        return this.queryUserDepartmentLeaderByJob(userId, true, jobId);
    }

    @Override
    public Set<String> queryUserAllJobBranchLeaderList(String userId) {
        return this.queryUserBranchLeaders(userId, null, null);
    }

    private String getUserWorkJobEleIdPaths(String userId) {
        MultiOrgUserWorkInfo workInfo = multiOrgUserWorkInfoService.getUserWorkInfo(userId);
        if (workInfo == null) {
            return null;
        }
        String eleIdPaths = workInfo.getEleIdPaths();
        if (StringUtils.isBlank(eleIdPaths)) {
            return null;
        }
        return eleIdPaths;
    }

    private Set<String> queryUserBranchLeaders(String userId, Boolean onlyMainJob, String jobId) {
        String eleIdPaths = this.getUserWorkJobEleIdPaths(userId);
        if (StringUtils.isBlank(eleIdPaths)) {
            return Collections.emptySet();
        }
        String[] paths = eleIdPaths.split(Separator.SEMICOLON.getValue());
        Set<String> leaders = Sets.newLinkedHashSet();
        for (String p : paths) {
            if (onlyMainJob == null && jobId == null) {
                leaders.addAll(this.queryUserBranchLeaderListByJobIdPath(p));
            } else if (jobId != null) {
                if (p.indexOf(jobId) != -1) {
                    leaders.addAll(this.queryUserBranchLeaderListByJobIdPath(p));
                    break;
                }
            } else if (BooleanUtils.isTrue(onlyMainJob)) {
                String[] slashs = p.split(Separator.SLASH.getValue());
                String vid = slashs[0];
                MultiOrgUserTreeNode treeNode = this.multiOrgUserTreeNodeService.queryUserJobByOrgVersionEleId(userId,
                        vid, slashs[slashs.length - 1]);
                if (treeNode == null) {
                    logger.error("无法查询到用户职位关联组织树信息，用户ID={}", userId);
                    continue;
                }
                if (1 == treeNode.getIsMain()) {
                    // 主职
                    leaders.addAll(this.queryUserBranchLeaderListByJobIdPath(p));
                    break;
                }
            }
        }
        leaders.remove(userId);
        return leaders;
    }

    @Override
    public Set<String> queryUserMainJobBranchLeaderList(String userId) {
        return this.queryUserBranchLeaders(userId, true, null);
    }

    @Override
    public Set<String> queryUserJobBranchLeaderList(String userId, String jobId) {
        return this.queryUserBranchLeaders(userId, false, jobId);
    }

    private Set<String> queryUserSuperiorLeader(String userId, Boolean onlyMainJob, String jobId) {
        String eleIdPaths = this.getUserWorkJobEleIdPaths(userId);
        if (StringUtils.isBlank(eleIdPaths)) {
            return Collections.emptySet();
        }
        String[] paths = eleIdPaths.split(Separator.SEMICOLON.getValue());
        Set<String> leaders = Sets.newLinkedHashSet();
        for (String p : paths) {
            if (onlyMainJob == null && jobId == null) {
                leaders.addAll(this.queryUserSuperiorLeaderListByJobIdPath(p, false));
            } else if (jobId != null) {
                if (p.indexOf(jobId) != -1) {
                    leaders.addAll(this.queryUserSuperiorLeaderListByJobIdPath(p, false));
                    break;
                }
            } else if (BooleanUtils.isTrue(onlyMainJob)) {
                String[] slashs = p.split(Separator.SLASH.getValue());
                String vid = slashs[0];
                MultiOrgUserTreeNode treeNode = this.multiOrgUserTreeNodeService.queryUserJobByOrgVersionEleId(userId,
                        vid, slashs[slashs.length - 1]);
                if (treeNode == null) {
                    logger.error("无法查询到用户职位关联组织树信息，用户ID={}", userId);
                    continue;
                }
                if (1 == treeNode.getIsMain()) {
                    // 主职
                    leaders.addAll(this.queryUserSuperiorLeaderListByJobIdPath(p, false));
                    break;
                }
            }
        }
        leaders.remove(userId);
        return leaders;
    }

    @Override
    public Set<String> queryUserAllJobSuperiorLeaderList(String userId) {
        return this.queryUserSuperiorLeader(userId, null, null);
    }

    @Override
    public Set<String> queryUserMainJobSuperiorLeaderList(String userId) {
        return this.queryUserSuperiorLeader(userId, true, null);
    }

    @Override
    public Set<String> queryUserJobSuperiorLeaderList(String userId, String jobId) {
        return this.queryUserSuperiorLeader(userId, null, jobId);
    }

    @Override
    public Set<String> queryJobDepartmentUserListByUserId(String userId, String jobId) {
        return queryJobDepartmentUserIdsByUserId(userId, null, jobId, 0);
    }

    @Override
    public Set<String> queryJobDepartmentIdListByUserId(String userId, String jobId) {
        return queryJobDepartmentIdsByUserId(userId, null, jobId, 0);
    }

    @Override
    public Set<String> queryJobParentDepartmentUserIdsByUserId(String userId, String jobId) {
        return queryJobDepartmentUserIdsByUserId(userId, null, jobId, -1);
    }

    @Override
    public Set<String> queryJobParentDepartmentIdsByUserId(String userId, String jobId) {
        return queryJobDepartmentIdsByUserId(userId, null, jobId, -1);
    }

    private Set<String> queryJobDepartmentIdsByUserId(String userId, Boolean onlyMainJob, String jobId, Integer level) {
        String eleIdPaths = this.getUserWorkJobEleIdPaths(userId);
        if (StringUtils.isBlank(eleIdPaths)) {
            return Collections.emptySet();
        }
        String[] paths = eleIdPaths.split(Separator.SEMICOLON.getValue());
        Set<String> userIds = Sets.newLinkedHashSet();
        Set<String> deptIds = Sets.newLinkedHashSet();
        Map<String, String> deptVersions = Maps.newHashMap();
        for (String p : paths) {
            String[] parts = p.split(Separator.SLASH.getValue());
            String v = parts[0];
            String depId = null;
            if (level == -9) {// 获取的是根级部门
                for (int i = 0; i < parts.length; i++) { // 获取上一级的部门
                    if (parts[i].startsWith(IdPrefix.DEPARTMENT.getValue())) {
                        depId = parts[i];
                        break;
                    }
                }
            } else if (level == -1) { // 获取上一级的部门
                int pos = 0;
                for (int i = parts.length - 2; i >= 0; i--) { // 获取上一级的部门
                    if (parts[i].startsWith(IdPrefix.DEPARTMENT.getValue())
                            || parts[i].startsWith(IdPrefix.BUSINESS_UNIT.getValue())) {
                        pos++;
                    }
                    if (pos == 2) {
                        depId = parts[i];
                        break;
                    }
                }
            } else if (level == 0) { // 获取当前的部门
                depId = MultiOrgTreeNode.getNearestEleIdByType(p, IdPrefix.DEPARTMENT.getValue());
            }
            if (StringUtils.isNotBlank(depId)) {
                deptVersions.put(depId, v);
                if (onlyMainJob == null && jobId == null) {
                    deptIds.add(depId);
                } else if (jobId != null) {
                    if (p.indexOf(jobId) != -1) {
                        deptIds.add(depId);
                        break;
                    }
                } else if (BooleanUtils.isTrue(onlyMainJob)) {
                    String[] slashs = p.split(Separator.SLASH.getValue());
                    String vid = slashs[0];
                    MultiOrgUserTreeNode treeNode = this.multiOrgUserTreeNodeService
                            .queryUserJobByOrgVersionEleId(userId, vid, slashs[slashs.length - 1]);
                    if (treeNode == null) {
                        logger.error("无法查询到用户职位关联组织树信息，用户ID={}", userId);
                        continue;
                    }
                    if (1 == treeNode.getIsMain()) {
                        // 主职
                        deptIds.add(depId);
                        break;
                    }
                }
            }

        }

        return deptIds;
    }

    private Set<String> queryJobDepartmentUserIdsByUserId(String userId, Boolean onlyMainJob, String jobId,
                                                          Integer level) {
        String eleIdPaths = this.getUserWorkJobEleIdPaths(userId);
        if (StringUtils.isBlank(eleIdPaths)) {
            return Collections.emptySet();
        }
        String[] paths = eleIdPaths.split(Separator.SEMICOLON.getValue());
        Set<String> userIds = Sets.newLinkedHashSet();
        Set<String> deptIds = Sets.newLinkedHashSet();
        Map<String, String> deptVersions = Maps.newHashMap();
        for (String p : paths) {
            String[] parts = p.split(Separator.SLASH.getValue());
            String v = parts[0];
            String depId = null;
            if (level == -9) {// 获取的是根级部门
                for (int i = 0; i < parts.length; i++) { // 获取上一级的部门
                    if (parts[i].startsWith(IdPrefix.DEPARTMENT.getValue())) {
                        depId = parts[i];
                        break;
                    }
                }
            } else if (level == -1) { // 获取上一级的部门
                int pos = 0;
                for (int i = parts.length - 2; i >= 0; i--) { // 获取上一级的部门
                    if (parts[i].startsWith(IdPrefix.DEPARTMENT.getValue())
                            || parts[i].startsWith(IdPrefix.BUSINESS_UNIT.getValue())) {
                        pos++;
                    }
                    if (pos == 2) {
                        depId = parts[i];
                        break;
                    }
                }
            } else if (level == 0) { // 获取当前的部门
                depId = MultiOrgTreeNode.getNearestEleIdByType(p, IdPrefix.DEPARTMENT.getValue());
            }
            if (StringUtils.isNotBlank(depId)) {
                deptVersions.put(depId, v);
                if (onlyMainJob == null && jobId == null) {
                    deptIds.add(depId);
                } else if (jobId != null) {
                    if (p.indexOf(jobId) != -1) {
                        deptIds.add(depId);
                        break;
                    }
                } else if (BooleanUtils.isTrue(onlyMainJob)) {
                    String[] slashs = p.split(Separator.SLASH.getValue());
                    String vid = slashs[0];
                    MultiOrgUserTreeNode treeNode = this.multiOrgUserTreeNodeService
                            .queryUserJobByOrgVersionEleId(userId, vid, slashs[slashs.length - 1]);
                    if (treeNode == null) {
                        logger.error("无法查询到用户职位关联组织树信息，用户ID={}", userId);
                        continue;
                    }
                    if (1 == treeNode.getIsMain()) {
                        // 主职
                        deptIds.add(depId);
                        break;
                    }
                }
            }

        }

        for (String deptId : deptIds) {
            userIds.addAll(queryUserIdListByOrgId(deptVersions.get(deptId), deptId, false));
        }
        return userIds;
    }

    @Override
    public Set<String> queryAllJobRootDepartmentUserIdsByUserId(String userId) {
        return queryJobDepartmentUserIdsByUserId(userId, null, null, -9);
    }

    @Override
    public Set<String> queryAllJobRootDepartmentIdsByUserId(String userId) {
        return queryJobDepartmentIdsByUserId(userId, null, null, -9);
    }

    @Override
    public Set<String> queryMainJobRootDepartmentUserIdsByUserId(String userId) {
        return queryJobDepartmentUserIdsByUserId(userId, true, null, -9);
    }

    @Override
    public Set<String> queryMainJobRootDepartmentIdsByUserId(String userId) {
        return queryJobDepartmentIdsByUserId(userId, true, null, -9);
    }

    @Override
    public Set<String> queryJobRootDepartmentUserIdsByUserId(String userId, String jobId) {
        return queryJobDepartmentUserIdsByUserId(userId, null, jobId, -9);
    }

    @Override
    public Set<String> queryJobRootDepartmentIdsByUserId(String userId, String jobId) {
        return queryJobDepartmentIdsByUserId(userId, null, jobId, -9);
    }

    private Set<String> queryJobBizUnitUserIds(String userId, Boolean onlyMainJob, String jobId) {
        String eleIdPaths = this.getUserWorkJobEleIdPaths(userId);
        if (StringUtils.isBlank(eleIdPaths)) {
            return Collections.emptySet();
        }
        String[] paths = eleIdPaths.split(Separator.SEMICOLON.getValue());
        Set<String> userIds = Sets.newLinkedHashSet();
        Set<String> bids = Sets.newLinkedHashSet();
        Map<String, String> deptVersions = Maps.newHashMap();
        for (String p : paths) {
            String[] parts = p.split(Separator.SLASH.getValue());
            String v = parts[0];
            String bid = MultiOrgTreeNode.getNearestEleIdByType(p, IdPrefix.BUSINESS_UNIT.getValue());
            if (StringUtils.isNotBlank(bid)) {
                deptVersions.put(bid, v);
                if (onlyMainJob == null && jobId == null) {
                    bids.add(bid);
                } else if (jobId != null) {
                    if (p.indexOf(jobId) != -1) {
                        bids.add(bid);
                        break;
                    }
                } else if (BooleanUtils.isTrue(onlyMainJob)) {
                    String[] slashs = p.split(Separator.SLASH.getValue());
                    String vid = slashs[0];
                    MultiOrgUserTreeNode treeNode = this.multiOrgUserTreeNodeService
                            .queryUserJobByOrgVersionEleId(userId, vid, slashs[slashs.length - 1]);
                    if (treeNode == null) {
                        logger.error("无法查询到用户职位关联组织树信息，用户ID={}", userId);
                        continue;
                    }
                    if (1 == treeNode.getIsMain()) {
                        // 主职
                        bids.add(bid);
                        break;
                    }
                }
            }

        }
        for (String id : bids) {
            userIds.addAll(queryUserIdListByOrgId(deptVersions.get(id), id, false));
        }
        return userIds;
    }

    @Override
    public Set<String> queryAllJobBizUnitUserIdsByUserId(String userId) {
        return this.queryJobBizUnitUserIds(userId, null, null);
    }

    @Override
    public Set<String> queryMainJobBizUnitUserIdsByUserId(String userId) {
        return this.queryJobBizUnitUserIds(userId, true, null);
    }

    @Override
    public Set<String> queryJobBizUnitUserIdsByUserId(String userId, String jobId) {
        return this.queryJobBizUnitUserIds(userId, null, jobId);
    }

    @Override
    public List<OrgElementVo> getUserJobElementByUserId(String userId) {
        String eleIdPaths = this.getUserWorkJobEleIdPaths(userId);
        String[] eleIdPathArrs = null;
        if (StringUtils.isNotBlank(eleIdPaths)) {
            eleIdPathArrs = eleIdPaths.split(Separator.SEMICOLON.getValue());
        }
        List<OrgElementVo> elementVos = Lists.newArrayList();
        if (eleIdPathArrs == null) {
            return elementVos;
        }
        Map<String, MultiOrgVersion> versionMap = Maps.newHashMap();
        for (String eleIdPath : eleIdPathArrs) {
            String[] eleIds = eleIdPath.split(Separator.SLASH.getValue());
            MultiOrgUserTreeNode jobNode = this.multiOrgUserTreeNodeService.queryUserJobByOrgVersionEleId(userId,
                    eleIds[0], eleIds[eleIds.length - 1]);
            if (jobNode != null) {
                OrgElementVo vo = new OrgElementVo();
                vo.setId(jobNode.getEleId());
                MultiOrgElement jobElement = this.multiOrgElementService.getById(jobNode.getEleId());
                if (jobElement != null) {
                    vo.setName(jobElement.getName());
                } else {
                    continue;
                }
                if (jobNode.getIsMain() == 1 && elementVos.size() > 0) {
                    elementVos.add(0, vo);
                } else {
                    elementVos.add(vo);
                }
                // 查询部门
                String depId = MultiOrgTreeNode.getNearestEleIdByType(eleIdPath, IdPrefix.DEPARTMENT.getValue());
                MultiOrgElement deptElement = this.multiOrgElementService.getById(depId);
                OrgElementVo parent = new OrgElementVo();
                if (deptElement != null) {
                    parent.setId(deptElement.getId());
                    parent.setName(deptElement.getName());
                    vo.setParent(parent);
                }
                if (!versionMap.containsKey(jobNode.getOrgVersionId())) {
                    versionMap.put(jobNode.getOrgVersionId(),
                            this.multiOrgVersionService.getVersionById(jobNode.getOrgVersionId()));
                }
                if (versionMap.get(jobNode.getOrgVersionId()) != null) {
                    OrgElementVo versionNode = null;
                    if (parent.getId() != null) {
                        versionNode = new OrgElementVo();
                        parent.setParent(versionNode);
                    } else {
                        versionNode = parent;
                        vo.setParent(versionNode);
                    }
                    versionNode.setId(jobNode.getOrgVersionId());
                    versionNode.setName(versionMap.get(jobNode.getOrgVersionId()).getName());
                }

            }
        }

        return elementVos;
    }

    @Override
    public Set<String> queryMainJobParentDepartmentUserIdsByUserId(String userId) {
        return queryJobDepartmentUserIdsByUserId(userId, true, null, -1);
    }

    @Override
    public Set<String> queryMainJobParentDepartmentIdsByUserId(String userId) {
        return queryJobDepartmentIdsByUserId(userId, true, null, -1);
    }

    @Override
    public Set<String> queryAllJobParentDepartmentUserIdsByUserId(String userId) {
        return queryJobDepartmentUserIdsByUserId(userId, null, null, -1);
    }

    @Override
    public Set<String> queryAllJobParentDepartmentIdsByUserId(String userId) {
        return queryJobDepartmentIdsByUserId(userId, null, null, -1);
    }

    @Override
    public Set<String> queryAllJobDepartmentUserListByUserId(String userId) {
        return queryJobDepartmentUserIdsByUserId(userId, null, null, 0);
    }

    @Override
    public Set<String> queryAllJobDepartmentIdListByUserId(String userId) {
        return queryJobDepartmentIdsByUserId(userId, null, null, 0);
    }

    @Override
    public Set<String> queryMainJobDepartmentUserListByUserId(String userId) {
        return queryJobDepartmentUserIdsByUserId(userId, true, null, 0);
    }

    @Override
    public Set<String> queryMainJobDepartmentIdListByUserId(String userId) {
        return queryJobDepartmentIdsByUserId(userId, true, null, 0);
    }

    @Override
    public void saveUserProperty(String userId, String prop, String value) {
        this.userPropertyService.saveUserPropertyValue(userId, prop, value);
    }

    /**
     * 获取某个组织节点下具有某个角色的所有人员接口
     * 1，群组 有该角色 群组包含 节点 人
     * 2，节点下 部分节点 有该角色
     * 3，用户 有该角色
     *
     * @param eleId
     * @param roleId
     * @return
     */
    @Override
    @Transactional(readOnly = true)
    public List<SimpleUser> getSimpleUsersByEleIdRid(String eleId, String roleId) {
        List<SimpleUser> simpleUsers = new ArrayList<>();
        // 组织版本节点不处理
        if (eleId.startsWith(IdPrefix.ORG_VERSION.getValue())) {
            return simpleUsers;
        }
        Role role = roleFacadeService.getRoleById(roleId);
        if (role == null) {
            return simpleUsers;
        }

        // 启用的组织节点
        HashMap<String, Object> query = new HashMap<String, Object>();
        query.put("eleId", eleId);
        List<MultiOrgTreeNode> orgTreeNodeList = multiOrgTreeNodeService.listByHQL(
                "select a from MultiOrgTreeNode a,MultiOrgVersion b where a.orgVersionId = b.id and b.status = 1 and a.eleId = :eleId ",
                query);
        if (orgTreeNodeList.size() == 0) {
            return simpleUsers;
        }

        // 所有包含roleId的 roleUuid
        List<String> roleUuidList = nestedRoleFacadeService.getAllParentRoleUuidsByRoleUuid(role.getUuid());
        Set<String> roleUuidSet = new HashSet<>(roleUuidList);
        roleUuidSet.add(role.getUuid());

        // 包含角色的 节点id+用户Id
        Set<String> eleIdSet = new HashSet<>();

        // 查询包含角色的节点
        StringBuilder hql = new StringBuilder("from MultiOrgElementRole where ");
        HqlUtils.appendSql("roleUuid", query, hql, Sets.<Serializable>newHashSet(roleUuidSet));
        List<MultiOrgElementRole> elementRoleList = multiOrgElementRoleService.listByHQL(hql.toString(), query);
        for (MultiOrgElementRole orgElementRole : elementRoleList) {
            eleIdSet.add(orgElementRole.getEleId());
        }

        // eleId(节点id) 本身就包含该角色 查询该eleId下所有用户即可
        if (eleIdSet.contains(eleId)) {
            return this.getSimpleUserList(eleId, orgTreeNodeList);
        }

        // 查询角色包含的群组
        hql = new StringBuilder("select distinct groupId from MultiOrgGroupRole where ");
        query = new HashMap<>();
        HqlUtils.appendSql("roleUuid", query, hql, Sets.<Serializable>newHashSet(roleUuidSet));
        List<String> groupIdList = multiOrgGroupRoleService.getDao().listCharSequenceByHQL(hql.toString(), query);
        if (groupIdList.size() > 0) {
            // 群组包含的 节点id 用户id
            hql = new StringBuilder("select distinct memberObjId from MultiOrgGroupMember where ");
            query = new HashMap<>();
            HqlUtils.appendSql("groupId", query, hql, Sets.<Serializable>newHashSet(groupIdList));
            List<String> objIdList = multiOrgGroupMemberService.getDao().listCharSequenceByHQL(hql.toString(), query);
            eleIdSet.addAll(objIdList);
        }

        // eleId 本身就包含该角色 查询该eleId下所有用户即可
        if (eleIdSet.contains(eleId)) {
            return this.getSimpleUserList(eleId, orgTreeNodeList);
        }

        // 处理节点 包含角色的 子节点路径 TreeMap减少循环判断
        // key:eleIdPath, val:orgVersionId
        TreeMap<String, String> eleIdPathTree = new TreeMap();
        for (MultiOrgTreeNode treeNode : orgTreeNodeList) {
            HashMap<String, Object> params = new HashMap<String, Object>();
            params.put("orgVersionId", treeNode.getOrgVersionId());
            params.put("eleIdPath", treeNode.getEleIdPath() + MultiOrgService.PATH_SPLIT_SYSMBOL + "%");
            // eleIdPath 排序 方便下面循环处理
            List<MultiOrgTreeNode> childrenList = multiOrgTreeNodeService.listByHQL(
                    "from MultiOrgTreeNode b where b.orgVersionId=:orgVersionId and b.eleIdPath like :eleIdPath order by eleIdPath ",
                    params);
            for (MultiOrgTreeNode multiOrgTreeNode : childrenList) {
                // 该节点 存在 角色相关节点里
                if (eleIdSet.contains(multiOrgTreeNode.getEleId())) {
                    boolean flg = true;
                    for (String eleIdPath : eleIdPathTree.keySet()) {
                        // 包含eleIdPath 说明是子节点 不添加
                        if (multiOrgTreeNode.getEleIdPath().startsWith(eleIdPath)) {
                            flg = false;
                            break;
                        }
                    }
                    if (flg) {
                        // 只添加 父节点
                        eleIdPathTree.put(multiOrgTreeNode.getEleIdPath(), multiOrgTreeNode.getOrgVersionId());
                    }
                }
            }
        }
        // 查询所有eleIdPathTree下的用户数据
        Set<SimpleUser> simpleUserSet = new HashSet<>();
        for (String eleIdPath : eleIdPathTree.keySet()) {
            String eleIdp = eleIdPath.substring(eleIdPath.lastIndexOf(MultiOrgService.PATH_SPLIT_SYSMBOL) + 1);
            List<SimpleUser> simpleUserList = multiOrgUserTreeNodeService.querSimpleUsers(eleIdp,
                    eleIdPathTree.get(eleIdPath));
            simpleUserSet.addAll(simpleUserList);
        }

        // 处理用户角色数据
        hql = new StringBuilder("select distinct userId from MultiOrgUserRole where ");
        query = new HashMap<>();
        HqlUtils.appendSql("roleUuid", query, hql, Sets.<Serializable>newHashSet(roleUuidSet));
        List<String> userIdList = multiOrgUserRoleService.getDao().listCharSequenceByHQL(hql.toString(), query);
        // 包含角色的userId
        Set<String> useIdSet = new HashSet<>(userIdList);
        for (String eId : eleIdSet) {
            if (eId.startsWith(IdPrefix.USER.getValue())) {
                useIdSet.add(eId);
            }
        }
        // 排除已查询出节点下的用户Id
        for (SimpleUser simpleUser : simpleUserSet) {
            if (useIdSet.contains(simpleUser.getId())) {
                useIdSet.remove(simpleUser.getId());
            }
        }

        if (useIdSet.size() == 0) {
            simpleUsers.addAll(simpleUserSet);
            return simpleUsers;
        }
        // 根据useIdSet 查询出是eleId下的用户
        for (MultiOrgTreeNode treeNode : orgTreeNodeList) {
            StringBuilder hqlSb = new StringBuilder(
                    "select distinct a.id as id,a.userName as name,a.code as code from MultiOrgUserAccount a,MultiOrgUserTreeNode b,MultiOrgTreeNode c ");
            hqlSb.append(
                    "where a.id = b.userId and b.eleId = c.eleId and b.orgVersionId = c.orgVersionId and a.isForbidden = 0 ");
            hqlSb.append("and b.orgVersionId = :orgVersionId  and c.eleIdPath like :eleIdPath and ");
            HashMap<String, Object> parms = new HashMap<String, Object>();
            parms.put("orgVersionId", treeNode.getOrgVersionId());
            parms.put("eleIdPath", "%" + treeNode.getEleId() + "%");
            HqlUtils.appendSql("a.id", parms, hqlSb, Sets.<Serializable>newHashSet(useIdSet));
            hqlSb.append(" order by a.code");
            List<SimpleUser> simpleUserList = multiOrgTreeNodeService.listItemHqlQuery(hqlSb.toString(),
                    SimpleUser.class, parms);
            simpleUserSet.addAll(simpleUserList);
        }
        simpleUsers.addAll(simpleUserSet);
        return simpleUsers;
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrgUserDto> getOrgUserDtosByEleIdRid(String eleId, String roleId) {
        List<OrgUserDto> orgUserDtos = new ArrayList<>();
        // 组织版本节点不处理
        if (eleId.startsWith(IdPrefix.ORG_VERSION.getValue())) {
            return orgUserDtos;
        }
        Role role = roleFacadeService.getRoleById(roleId);
        if (role == null) {
            return orgUserDtos;
        }
        // 启用的组织节点
        HashMap<String, Object> query = new HashMap<String, Object>();
        query.put("eleId", eleId);
        List<MultiOrgTreeNode> orgTreeNodeList = multiOrgTreeNodeService.listByHQL(
                "select a from MultiOrgTreeNode a,MultiOrgVersion b where a.orgVersionId = b.id and b.status = 1 and a.eleId = :eleId ",
                query);
        if (orgTreeNodeList.size() == 0) {
            return orgUserDtos;
        }
        // 所有包含roleId的 roleUuid
        List<String> roleUuidList = nestedRoleFacadeService.getAllParentRoleUuidsByRoleUuid(role.getUuid());
        Set<String> roleUuidSet = new HashSet<>(roleUuidList);
        roleUuidSet.add(role.getUuid());

        // 包含角色的 节点id+用户Id
        Set<String> eleIdSet = new HashSet<>();

        // 查询包含角色的节点
        StringBuilder hql = new StringBuilder("from MultiOrgElementRole where ");
        HqlUtils.appendSql("roleUuid", query, hql, Sets.<Serializable>newHashSet(roleUuidSet));
        List<MultiOrgElementRole> elementRoleList = multiOrgElementRoleService.listByHQL(hql.toString(), query);
        for (MultiOrgElementRole orgElementRole : elementRoleList) {
            eleIdSet.add(orgElementRole.getEleId());
        }

        // eleId(节点id) 本身就包含该角色 查询该eleId下所有用户即可
        if (eleIdSet.contains(eleId)) {
            return this.getOrgUserList(eleId, orgTreeNodeList);
        }

        // 查询角色包含的群组
        hql = new StringBuilder("select distinct groupId from MultiOrgGroupRole where ");
        query = new HashMap<>();
        HqlUtils.appendSql("roleUuid", query, hql, Sets.<Serializable>newHashSet(roleUuidSet));
        List<String> groupIdList = multiOrgGroupRoleService.getDao().listCharSequenceByHQL(hql.toString(), query);
        if (groupIdList.size() > 0) {
            // 群组包含的 节点id 用户id
            hql = new StringBuilder("select distinct memberObjId from MultiOrgGroupMember where ");
            query = new HashMap<>();
            HqlUtils.appendSql("groupId", query, hql, Sets.<Serializable>newHashSet(groupIdList));
            List<String> objIdList = multiOrgGroupMemberService.getDao().listCharSequenceByHQL(hql.toString(), query);
            eleIdSet.addAll(objIdList);
        }

        // eleId 本身就包含该角色 查询该eleId下所有用户即可
        if (eleIdSet.contains(eleId)) {
            return this.getOrgUserList(eleId, orgTreeNodeList);
        }

        // 处理节点 包含角色的 子节点路径 TreeMap减少循环判断
        // key:eleIdPath, val:orgVersionId
        TreeMap<String, String> eleIdPathTree = new TreeMap();
        for (MultiOrgTreeNode treeNode : orgTreeNodeList) {
            HashMap<String, Object> params = new HashMap<String, Object>();
            params.put("orgVersionId", treeNode.getOrgVersionId());
            params.put("eleIdPath", treeNode.getEleIdPath() + MultiOrgService.PATH_SPLIT_SYSMBOL + "%");
            // eleIdPath 排序 方便下面循环处理
            List<MultiOrgTreeNode> childrenList = multiOrgTreeNodeService.listByHQL(
                    "from MultiOrgTreeNode b where b.orgVersionId=:orgVersionId and b.eleIdPath like :eleIdPath order by eleIdPath ",
                    params);
            for (MultiOrgTreeNode multiOrgTreeNode : childrenList) {
                // 该节点 存在 角色相关节点里
                if (eleIdSet.contains(multiOrgTreeNode.getEleId())) {
                    boolean flg = true;
                    for (String eleIdPath : eleIdPathTree.keySet()) {
                        // 包含eleIdPath 说明是子节点 不添加
                        if (multiOrgTreeNode.getEleIdPath().startsWith(eleIdPath)) {
                            flg = false;
                            break;
                        }
                    }
                    if (flg) {
                        // 只添加 父节点
                        eleIdPathTree.put(multiOrgTreeNode.getEleIdPath(), multiOrgTreeNode.getOrgVersionId());
                    }
                }
            }
        }
        // 查询所有eleIdPathTree下的用户数据
        Set<OrgUserDto> orgUserSet = new HashSet<>();
        for (String eleIdPath : eleIdPathTree.keySet()) {
            String eleIdp = eleIdPath.substring(eleIdPath.lastIndexOf(MultiOrgService.PATH_SPLIT_SYSMBOL) + 1);
            List<OrgUserDto> orgUserList = multiOrgUserTreeNodeService.querOrgUsers(eleIdp,
                    eleIdPathTree.get(eleIdPath));
            orgUserSet.addAll(orgUserList);
        }
        // 处理用户角色数据
        hql = new StringBuilder("select distinct userId from MultiOrgUserRole where ");
        query = new HashMap<>();
        HqlUtils.appendSql("roleUuid", query, hql, Sets.<Serializable>newHashSet(roleUuidSet));
        List<String> userIdList = multiOrgUserRoleService.getDao().listCharSequenceByHQL(hql.toString(), query);
        // 包含角色的userId
        Set<String> useIdSet = new HashSet<>(userIdList);
        for (String eId : eleIdSet) {
            if (eId.startsWith(IdPrefix.USER.getValue())) {
                useIdSet.add(eId);
            }
        }
        // 排除已查询出节点下的用户Id
        for (OrgUserDto orgUser : orgUserSet) {
            if (useIdSet.contains(orgUser.getId())) {
                useIdSet.remove(orgUser.getId());
            }
        }
        if (useIdSet.size() == 0) {
            orgUserDtos.addAll(orgUserSet);
            return orgUserDtos;
        }

        // 根据useIdSet 查询出是eleId下的用户
        // for (MultiOrgTreeNode treeNode : orgTreeNodeList) {
        //
        // StringBuilder hqlSb = new StringBuilder("SELECT moua,moui ");
        // hqlSb.append(" FROM ( ");
        // hqlSb.append(
        // "select distinct a.id as accountId,a from MultiOrgUserAccount
        // a,MultiOrgUserTreeNode b,MultiOrgTreeNode c ");
        // hqlSb.append(
        // "where a.id = b.userId and b.eleId = c.eleId and b.orgVersionId =
        // c.orgVersionId and a.isForbidden = 0 ");
        // hqlSb.append("and b.orgVersionId = :orgVersionId and c.eleIdPath like
        // :eleIdPath and ");
        // HashMap<String, Object> parms = new HashMap<String, Object>();
        // parms.put("orgVersionId", treeNode.getOrgVersionId());
        // parms.put("eleIdPath", "%" + treeNode.getEleId() + "%");
        // HqlUtils.appendSql("a.id", parms, hqlSb, useIdSet);
        // hqlSb.append(" )moua");
        // hqlSb.append(" LEFT JOIN MultiOrgUserInfo moui ON moua.id = moui.userId");
        // hqlSb.append(" order by moua.code");
        // List<OrgUserDto> simpleUserList =
        // multiOrgTreeNodeService.listItemHqlQuery(hqlSb.toString(),
        // OrgUserDto.class, parms);
        // //// QueryItem implements BaseQueryItem
        // // List<OrgUserDto> simpleUserList =
        // //// multiOrgTreeNodeService.listItemByNameSQLQuery(
        // // hqlSb.toString(), OrgUserDto.class, parms, new PagingInfo(1,
        // // Integer.MAX_VALUE));
        // orgUserSet.addAll(simpleUserList);
        // }

        for (MultiOrgTreeNode treeNode : orgTreeNodeList) {
            // todo-zhw 需要优化，调整sql
            StringBuilder hqlSb = new StringBuilder(
                    "select moua.login_name,moua.login_name_lower_case,moua.user_name,moua.code, ");
            hqlSb.append("moua.id,moua.type,moua.last_login_time,moua.system_unit_id,moua.remark,moua.is_expired, ");
            hqlSb.append("moua.is_locked,moua.is_forbidden,moua.user_name_py,moua.user_name_jp, ");
            hqlSb.append("moui.* ");
            hqlSb.append(" FROM ( ");
            hqlSb.append(
                    "select distinct a.id as account_id,a.* from multi_org_user_account a,multi_org_user_tree_node b,multi_org_tree_node c ");
            hqlSb.append(
                    "where a.id = b.user_id and b.ele_id = c.ele_id and b.org_version_id = c.org_version_id and a.is_forbidden = 0 ");
            hqlSb.append("and b.org_version_id = :org_version_id  and c.ele_id_path like :ele_id_path and ");
            HashMap<String, Object> parms = new HashMap<String, Object>();
            parms.put("org_version_id", treeNode.getOrgVersionId());
            parms.put("ele_id_path", "%" + treeNode.getEleId() + "%");
            HqlUtils.appendSql("a.id", parms, hqlSb, Sets.<Serializable>newHashSet(useIdSet));
            hqlSb.append(" )moua");
            hqlSb.append(" left join multi_org_user_info moui on moua.id = moui.user_id ");
            hqlSb.append(" order by moua.code");
            List<QueryItem> simpleUserList = multiOrgTreeNodeService.listQueryItemBySQL(hqlSb.toString(), parms, null);
            List<OrgUserDto> orgUserDtoArrayList = new ArrayList<>();
            for (QueryItem queryItem : simpleUserList) {
                OrgUserDto orgUserDto = JSONObject.parseObject(JSONObject.toJSONString(queryItem), OrgUserDto.class);
                orgUserDtoArrayList.add(orgUserDto);
            }
            orgUserSet.addAll(orgUserDtoArrayList);
        }
        orgUserDtos.addAll(orgUserSet);
        return orgUserDtos;
    }

    @Override
    public boolean isMemberOfMainJob(String userId, String memberOf) {
        if (StringUtils.isBlank(memberOf)) {
            return false;
        }
        if (memberOf.contains(userId)) {
            return true;
        }
        String[] ids = StringUtils.split(memberOf, Separator.SEMICOLON.getValue());
        // 查询用户的主职位，以及主职位的部门
        String jobPaths = getUserWorkJobEleIdPaths(userId);
        if (StringUtils.isNotBlank(jobPaths)) {
            for (String p : jobPaths.split(Separator.SEMICOLON.getValue())) {
                String[] slashs = p.split(Separator.SLASH.getValue());
                String vid = slashs[0];
                MultiOrgUserTreeNode treeNode = this.multiOrgUserTreeNodeService.queryUserJobByOrgVersionEleId(userId,
                        vid, slashs[slashs.length - 1]);
                if (treeNode == null) {
                    logger.error("无法查询到用户职位关联组织树信息，用户ID={}", userId);
                    continue;
                }
                if (1 == treeNode.getIsMain()) {
                    // 主职
                    for (String id : ids) {
                        if (p.indexOf(id) != -1) {
                            return true;
                        }
                    }
                    return false;
                }
            }
        }
        return false;
    }

    @Override
    public boolean isMemberOfSelectedJob(String userId, String jobId, String memberOf) {
        if (StringUtils.isBlank(memberOf)) {
            return false;
        }
        if (memberOf.contains(userId)) {
            return true;
        }
        String[] ids = StringUtils.split(memberOf, Separator.SEMICOLON.getValue());
        // 查询用户的主职位，以及主职位的部门
        String jobPaths = getUserWorkJobEleIdPaths(userId);
        if (StringUtils.isNotBlank(jobPaths)) {
            String[] jobs = jobPaths.split(Separator.SEMICOLON.getValue());
            for (String j : jobs) {
                if (j.indexOf(jobId) != -1) {
                    for (String id : ids) {
                        if (j.indexOf(id) != -1) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    @Override
    public boolean isMemberOfAllJob(String userId, String memberOf) {
        if (StringUtils.isBlank(memberOf)) {
            return false;
        }
        if (memberOf.contains(userId)) {
            return true;
        }
        String[] ids = StringUtils.split(memberOf, Separator.SEMICOLON.getValue());
        // 查询用户的主职位，以及主职位的部门
        String jobPaths = getUserWorkJobEleIdPaths(userId);
        String[] jobs = StringUtils.isNotBlank(jobPaths) ? jobPaths.split(Separator.SEMICOLON.getValue())
                : new String[0];
        for (String j : jobs) {
            for (String id : ids) {
                if (j.indexOf(id) != -1) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean isMemberOfGroup(String userId, Set<String> groupIds) {
        return multiOrgGroupMemberService.isMemberOfGroup(userId, groupIds);
    }

    @Override
    public boolean isMemberOfSelectedJobDuty(String userId, Set<String> dutyIds, String jobId) {
        Set<String> jobIds = Sets.newHashSet(jobId);
        return multiOrgJobDutyService.isMemberOfSelectedJobDuty(userId, dutyIds, jobIds);
    }

    @Override
    public boolean isMemberOfAllJobDuty(String userId, Set<String> dutyIds) {
        return multiOrgJobDutyService.isMemberOfAllJobDuty(userId, dutyIds);
    }

    @Override
    public boolean isMemberOfExternal(String userId, Set<String> externalIds) {
        return businessFacadeService.isMemberOf(userId, externalIds);
    }

    @Override
    public String getUserMainJobId(String userId) {
        String jobPaths = getUserWorkJobEleIdPaths(userId);
        if (StringUtils.isNotBlank(jobPaths)) {
            for (String p : jobPaths.split(Separator.SEMICOLON.getValue())) {
                String[] slashs = p.split(Separator.SLASH.getValue());
                String vid = slashs[0];
                MultiOrgUserTreeNode treeNode = this.multiOrgUserTreeNodeService.queryUserJobByOrgVersionEleId(userId,
                        vid, slashs[slashs.length - 1]);
                if (treeNode == null) {
                    logger.error("无法查询到用户职位关联组织树信息，用户ID={}", userId);
                    continue;
                }
                if (1 == treeNode.getIsMain()) {
                    return slashs[slashs.length - 1];
                }
            }
        }
        return null;
    }

    @Override
    public boolean isMemberOfMainJobDuty(String userId, Set<String> dutyIds) {
        return multiOrgJobDutyService.isMemberOfMainJobDuty(userId, dutyIds);
    }

    private List<SimpleUser> getSimpleUserList(String eleId, List<MultiOrgTreeNode> orgTreeNodeList) {
        List<SimpleUser> simpleUsers = new ArrayList<>();
        for (MultiOrgTreeNode treeNode : orgTreeNodeList) {
            List<SimpleUser> simpleUserList = multiOrgUserTreeNodeService.querSimpleUsers(eleId,
                    treeNode.getOrgVersionId());
            simpleUsers.addAll(simpleUserList);
        }
        return simpleUsers;
    }

    /**
     * 获取该节点（节点包含该角色）下的所有用户
     *
     * @param eleId           组织节点ID
     * @param orgTreeNodeList 组织树节点列表
     * @return java.util.List<com.wellsoft.pt.multi.org.bean.OrgUserDto>
     **/
    private List<OrgUserDto> getOrgUserList(String eleId, List<MultiOrgTreeNode> orgTreeNodeList) {
        List<OrgUserDto> orgUsers = new ArrayList<>();
        for (MultiOrgTreeNode treeNode : orgTreeNodeList) {
            List<OrgUserDto> orgUserList = multiOrgUserTreeNodeService.querOrgUsers(eleId, treeNode.getOrgVersionId());
            orgUsers.addAll(orgUserList);
        }
        return orgUsers;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.multi.org.facade.service.OrgApiFacade#getOrgEleOrderByEleIdPaths(java.util.List)
     */
    @Override
    public Map<String, Integer> getOrgEleOrderByEleIdPaths(List<String> eleIdPaths) {
        return multiOrgService.getOrgEleOrderByEleIdPaths(eleIdPaths);
    }

    @Override
    public List<MultiOrgUserAccount> getAccountsByUserIds(List<String> ids) {
        return multiOrgUserService.getAccountsByUserIds(ids);
    }

    @Override
    public List<MultiOrgElement> getOrgElementsByIds(List<String> ids) {
        return multiOrgElementService.getOrgElementsByIds(ids);
    }

    @Override
    public List<MultiOrgGroup> getGroupsByIds(List<String> ids) {
        return multiOrgGroupFacade.getGroupsByIds(ids);
    }

    @Override
    public List<MultiOrgDuty> getDutysByIds(List<String> ids) {
        return multiOrgService.getDutysByIds(ids);
    }

    @Override
    public Map<String, OrgUserVo> getUserAccoutVoWithMainJobByIds(List<String> userids) {
        return this.multiOrgUserService.getUserAccoutVoWithMainJobByIds(userids);
    }

    @Override
    public Map<String, OrgUserVo> getUserAccoutVoWithAllJobByIds(List<String> userids) {
        if (CollectionUtils.isEmpty(userids)) {
            return Maps.newHashMap();
        }
        return this.multiOrgUserService.getUserAccoutVoWithAllJobByIds(userids);
    }

    @Override
    public Map<String, String> getNamesByOrgEleIds(Set<String> ids) {
        Map<String, String> result = Maps.newHashMap();
        if (CollectionUtils.isEmpty(ids)) {
            return result;
        }
        Map<String, String> userinfoMap = userInfoService.getUserNamesByLoginNames(ids.toArray(new String[]{}));
        result.putAll(userinfoMap);
        userinfoMap.keySet();
        ids = SetUtils.difference(ids, userinfoMap.keySet()).toSet();

        // 1. 分组用户数据，批量查询
        ImmutableListMultimap<String, String> map = Multimaps.index(ids, new Function<String, String>() {
            @Nullable
            @Override
            public String apply(@Nullable String sid) {
                if (sid.startsWith(IdPrefix.DUTY.getValue())) {
                    return IdPrefix.DUTY.getValue();
                } else if (sid.startsWith(IdPrefix.USER.getValue())) {
                    return IdPrefix.USER.getValue();
                } else if (sid.startsWith(IdPrefix.DEPARTMENT.getValue())) {
                    return IdPrefix.DEPARTMENT.getValue();
                } else if (sid.startsWith(IdPrefix.GROUP.getValue())) {
                    return IdPrefix.GROUP.getValue();
                } else if (sid.startsWith(IdPrefix.ROLE.getValue())) {
                    return IdPrefix.ROLE.getValue();
                } else if (sid.startsWith(IdPrefix.JOB.getValue())) {
                    return IdPrefix.JOB.getValue();
                } else if (IdPrefix.startsWithExternal(sid)) {
                    return "External";
                }
                return "element";
            }
        });

        Map<String, Collection<String>> groupMap = map.asMap();
        Set<String> keys = groupMap.keySet();
        for (String sid : keys) {
            List<String> sids = Lists.newArrayList(groupMap.get(sid));
            if (sid.equals(IdPrefix.USER.getValue())) {
                List<MultiOrgUserAccount> accounts = this.getAccountsByUserIds(sids);
                if (accounts != null) {
                    for (MultiOrgUserAccount account : accounts) {
                        result.put(account.getId(), account.getUserName());
                    }
                }
            } else if (sid.equals(IdPrefix.ROLE.getValue())) {
                List<Role> roles = roleFacadeService.getRolesByIds(sids);
                if (roles != null) {
                    for (Role d : roles) {
                        result.put(d.getId(), d.getName());
                    }
                }
            } else if (sid.equals(IdPrefix.DUTY.getValue())) {
                List<MultiOrgDuty> dutys = this.getDutysByIds(sids);
                if (dutys != null) {
                    for (MultiOrgDuty d : dutys) {
                        result.put(d.getId(), d.getName());
                    }
                }
            } else if (sid.equals(IdPrefix.DEPARTMENT.getValue()) || sid.equals(IdPrefix.JOB.getValue())
                    || sid.equals("element")) {
                List<MultiOrgElement> elements = this.getOrgElementsByIds(sids);
                if (elements != null) {
                    for (MultiOrgElement element : elements) {
                        result.put(element.getId(), element.getName());
                    }
                }
            } else if (sid.equals(IdPrefix.GROUP.getValue())) {
                List<MultiOrgGroup> multiOrgGroups = this.getGroupsByIds(sids);
                if (multiOrgGroups != null) {
                    for (MultiOrgGroup element : multiOrgGroups) {
                        result.put(element.getId(), element.getName());
                    }
                }
            } else if (sid.equals("External")) {
                List<BusinessCategoryOrgEntity> businessCategoryOrgEntities = businessFacadeService
                        .getBusinessByIds(sids);
                if (businessCategoryOrgEntities != null) {
                    for (BusinessCategoryOrgEntity entity : businessCategoryOrgEntities) {
                        result.put(entity.getId(), entity.getName());
                    }
                }
            }
        }
        return result;
    }

    @Override
    public Map<String, List<OrgUserJobDto>> getAllUserJobIdsIgnoreVersion(List<String> userids) {
        return multiOrgUserTreeNodeService.getAllUserJobIdsIgnoreVersion(userids);
    }

    @Override
    public List<MultiOrgUserWorkInfo> getUserWorkInfosByUserIds(List<String> userids) {
        return multiOrgUserWorkInfoService.getUserWorkInfosByUserIds(userids);
    }

    @Override
    public List<OrgTreeNodeDto> queryBranchLeaderNodeListByNode(String eleId, String orgVersionId) {
        return multiOrgService.queryBranchLeaderNodeListByNode(eleId, orgVersionId);
    }

    @Override
    public List<OrgTreeNodeDto> queryBossLeaderNodeListByNode(String eleId, String orgVersionId) {
        return multiOrgService.queryBossLeaderNodeListByNode(eleId, orgVersionId);
    }

    @Override
    public List<MultiOrgTreeNode> getOrgTreeNodeListByNodeIds(Set<String> nodeIds) {
        HashMap<String, Object> query = new HashMap<>();
        StringBuilder hqlSb = new StringBuilder("select b from MultiOrgVersion a,MultiOrgTreeNode b where "
                + "a.id = b.orgVersionId and a.status=1 and ");
        HqlUtils.appendSql("b.eleId", query, hqlSb, Sets.<Serializable>newHashSet(nodeIds));
        List<MultiOrgTreeNode> multiOrgTreeNodeList = multiOrgTreeNodeService.listByHQL(hqlSb.toString(), query);
        return multiOrgTreeNodeList;
    }

    @Override
    public List<JobRankLevelVo> queryJobRankLevelListByUserId(String userId) {
        return multiJobRankLevelService.queryListByUserId(userId);
    }

    @Override
    public String getCurrentUnitJobGradeOrder() {
        return unitParamService.getValue(UnitParamConstant.JOB_GRADE_ORDER);
    }

    @Override
    public List<CurrentUnitJobGradeDto> getCurrentUnitJobGradeList() {
        List<OrgJobGradeEntity> orgJobGradeEntityList = orgJobGradeService.jobGradeList();
        if (orgJobGradeEntityList != null && orgJobGradeEntityList.size() > 0) {
            return BeanUtils.copyCollection(orgJobGradeEntityList, CurrentUnitJobGradeDto.class);
        }
        return Lists.newArrayList();
    }

    @Override
    public List<OrgDutySeqTreeDto> queryJobRankTree() {

        return orgDutySeqService.queryJobRankTree(null);
    }

    @Override
    public List<DutySeqAndjobRankDto> getDutySeqAndjobRankListByRankIds(List<String> jobRankIds) {
        return orgDutySeqService.getDutySeqAndjobRankListByRankIds(jobRankIds);
    }

    @Override
    public List<MultiOrgJobRank> getMultiOrgJobRankByJobRank(List<String> jobRankIds) {
        String[] strings = new String[jobRankIds.size()];
        return multiOrgJobRankService.getMultiOrgJobRankByJobRankId(jobRankIds.toArray(strings));
    }

    @Override
    public MultiOrgJobRank getMultiOrgJobRankDetailByUuid(String uuid) {
        return multiOrgJobRankService.getOne(uuid);
    }

    @Override
    public MultiOrgJobRank getMultiOrgJobRankDetailById(String id) {
        return multiOrgJobRankService.getById(id);
    }

    @Override
    public List<MultiOrgJobRank> getBelongSeqMultiOrgJobRankListByUuid(String uuid) {
        MultiOrgJobRank multiOrgJobRank = null;
        if (StringUtils.startsWith(uuid, IdPrefix.RANK.getValue())) {
            multiOrgJobRank = multiOrgJobRankService.getById(uuid);
        } else {
            multiOrgJobRank = multiOrgJobRankService.getOne(uuid);
        }
        if (multiOrgJobRank == null) {
            return Lists.newArrayList();
        }
        return multiOrgJobRankService.getMultiOrgJobRankDetailByDutySeqUuid(multiOrgJobRank.getDutySeqUuid());
    }

    @Override
    public Map<String, Set<String>> queryAllRoleListByUserByJobs(OrgUserVo user) {
        Map<String, Set<String>> userRoles = Maps.newHashMap();
        // 兼容获取新版组织用户的角色
        Set<String> roleUuids = orgFacadeService.getOrgUserRoleUuidsByUserId(user.getId());
        List<String> user7Roles = userInfoFacadeService.getUserRolesByUserId(user.getId());
        if (CollectionUtils.isNotEmpty(user7Roles)) {
            for (String u : user7Roles) {
                this.addRoleToMap(userRoles, u, "新版组织用户账号");
            }
        }
        if (CollectionUtils.isNotEmpty(roleUuids)) {
            for (String u : roleUuids) {
                this.addRoleToMap(userRoles, u, "新版组织元素");
            }
        }

        // 先获取用户自身的角色
        String userRoleUuids = user.getRoleUuids();
        if (StringUtils.isNotBlank(userRoleUuids)) {
            String[] roles = userRoleUuids.split(";");
            for (String roleUuid : roles) {
                this.addRoleToMap(userRoles, roleUuid, "账号");
            }
        }

        // 获取跟用户相关的所有组织节点, 包含了群组的信息，然后依次获取该节点对应的的角色信息

        String jobIds = StringUtils.join(new String[]{StringUtils.defaultIfBlank(user.getMainJobIdPath(), ""),
                StringUtils.defaultIfBlank(user.getOtherJobIdPaths(), "")}, ";");
        if (StringUtils.isNotBlank(jobIds)) {
            String[] jobIdPaths = jobIds.split(";");
            Set<String> userOrgIds = Sets.newHashSet();
            for (String j : jobIdPaths) {
                if (StringUtils.isNotBlank(j)) {
                    userOrgIds.addAll(Arrays.asList(j.split(MultiOrgService.PATH_SPLIT_SYSMBOL)));
                }
            }

            // 添加上群组的角色信息
            if (org.apache.commons.collections.CollectionUtils.isNotEmpty(userOrgIds)) {
                Set<String> memberIds = Sets.newHashSet(userOrgIds);
                // 需要把用户自己算进去
                memberIds.add(user.getId());
                Set<String> groupIds = multiOrgUserService.queryGroupIdsByMemeberId(memberIds);
                if (CollectionUtils.isNotEmpty(groupIds)) {
                    // 查询公共群组的角色信息
                    List<QueryItem> roleList = this.multiOrgGroupFacade.queryRoleListOfGroupIds(groupIds);
                    if (CollectionUtils.isNotEmpty(roleList)) {
                        for (QueryItem gRole : roleList) {
                            this.addRoleToMap(userRoles, gRole.getString("roleuuid"), "群组:" + gRole.getString("name"));
                        }
                    }
                }
            }

            // 查询组织元素关联的角色信息
            List<QueryItem> roleList = this.multiOrgService.queryRoleListOfElementIds(userOrgIds);
            if (org.apache.commons.collections.CollectionUtils.isNotEmpty(roleList)) {
                for (QueryItem eleRole : roleList) {
                    this.addRoleToMap(userRoles, eleRole.getString("roleuuid"), "组织:" + eleRole.getString("name"));
                }
            }

        }
        return userRoles;
    }

    @Override
    public List<GetCurrentUnitUserListByUserNameKeyDto> getCurrentUnitUserListByUserNameKey(String userNameKey) {

        if (StringUtils.isBlank(userNameKey) || StringUtils.isBlank(userNameKey.trim())) {
            return new ArrayList<>();
        }
        // 模糊匹配对应的数据 获取正常启用的用户
        List<MultiOrgUserAccount> multiOrgUserAccounts = multiOrgUserAccountService
                .getMultiOrgUserAccountListByUserNameKey(userNameKey, SpringSecurityUtils.getCurrentUserUnitId());
        List<String> userIds = Lists.newArrayList();
        for (MultiOrgUserAccount multiOrgUserAccount : multiOrgUserAccounts) {
            userIds.add(multiOrgUserAccount.getId());
        }

        List<MultiOrgUserWorkInfo> multiOrgUserWorkInfos = multiOrgUserWorkInfoService
                .getUserWorkInfoByUserIds(userIds);

        // KEY:userid value:MULTI_ORG_USER_WORK_INFO里的job_ids 第一个值
        Map<String, String> userFirstJobIdMap = getUserFirstJobIdMap(multiOrgUserWorkInfos);

        List<String> nodeIdList = getNodeIdList(userFirstJobIdMap);
        // 获取全路径名称 KEY:nodeId
        Map<String, OrgNode> OrgNodeMap = multiOrgTreeDialogService.smartName(0, nodeIdList, null);

        return toGetCurrentUnitUserListByUserNameKeyDto(multiOrgUserAccounts, userFirstJobIdMap, OrgNodeMap);
    }

    /**
     * 获取节点ID集合
     *
     * @param userFirstJobIdMap 用户对应的第一个职位集合
     * @return java.util.List<java.lang.String>
     **/
    private List<String> getNodeIdList(Map<String, String> userFirstJobIdMap) {
        List<String> nodeIds = Lists.newArrayList();
        for (String key : userFirstJobIdMap.keySet()) {
            nodeIds.add(userFirstJobIdMap.get(key));
        }
        return nodeIds;
    }

    /**
     * 获取用户对应的第一个职位集合
     * // KEY:userid value:MULTI_ORG_USER_WORK_INFO里的job_ids 第一个值
     *
     * @param multiOrgUserWorkInfos 用户工作信息集合
     * @return java.util.Map<java.lang.String, java.lang.String>
     **/
    private Map<String, String> getUserFirstJobIdMap(List<MultiOrgUserWorkInfo> multiOrgUserWorkInfos) {
        Map<String, String> userFirstJobIdMap = Maps.newHashMap();
        for (MultiOrgUserWorkInfo multiOrgUserWorkInfo : multiOrgUserWorkInfos) {
            String jobIdsStr = multiOrgUserWorkInfo.getJobIds();
            if (StringUtils.isBlank(jobIdsStr)) {
                continue;
            }
            String[] JobIds = jobIdsStr.split(";");
            if (JobIds.length > 0) {
                userFirstJobIdMap.put(multiOrgUserWorkInfo.getUserId(), JobIds[0]);
            }
        }
        return userFirstJobIdMap;
    }

    /**
     * 封装处理-获取模糊搜索后要返回的对象列表
     *
     * @param multiOrgUserAccounts 模糊匹配对应的用户数据
     * @param userFirstJobIdMap    用户对应的第一个职位集合
     * @param OrgNodeMap           职位ID对应的节点对象集合
     **/
    private List<GetCurrentUnitUserListByUserNameKeyDto> toGetCurrentUnitUserListByUserNameKeyDto(
            List<MultiOrgUserAccount> multiOrgUserAccounts, Map<String, String> userFirstJobIdMap,
            Map<String, OrgNode> OrgNodeMap) {
        List<GetCurrentUnitUserListByUserNameKeyDto> userListByUserNameKeyDtos = Lists.newArrayList();
        for (MultiOrgUserAccount multiOrgUserAccount : multiOrgUserAccounts) {
            GetCurrentUnitUserListByUserNameKeyDto dto = new GetCurrentUnitUserListByUserNameKeyDto();
            dto.setUserId(multiOrgUserAccount.getId());
            dto.setUserName(multiOrgUserAccount.getUserName());

            String firstJobId = userFirstJobIdMap.get(multiOrgUserAccount.getId());

            if (StringUtils.isNotBlank(firstJobId)) {
                OrgNode jobOrgNode = OrgNodeMap.get(firstJobId);
                if (jobOrgNode != null) {
                    dto.setJobPathName(jobOrgNode.getNamePath());
                }
            }

            if (StringUtils.isBlank(dto.getJobPathName())) {
                dto.setJobPathName("");
            }
            userListByUserNameKeyDtos.add(dto);

        }
        return userListByUserNameKeyDtos;
    }

    /**
     * 获取用户所有职位对应直属人员ID
     * 1、组织用户-->直属领导人员
     * 2、部门负责人，部门下所有人员
     * 3、分管领导下部门负责人
     *
     * @param priorUserId  前办理人ID
     * @param priorUserIds 所有职位
     * @return
     * @author baozh
     * @date 2022/1/27 10:10
     */
    @Override
    public Set<String> querySubordinateUserIdsByUserIdOfAllJob(String priorUserId,
                                                               Map<String, List<OrgUserJobDto>> priorUserIds, List<OrgUserJobDto> priorUserJobs, boolean isFilter) {

        List<String> leaderIds = new ArrayList<>();
        leaderIds.add(priorUserId);
        for (OrgUserJobDto priorUserJob : priorUserJobs) {
            leaderIds.add(priorUserJob.getOrgTreeNodeDto().getEleId());
        }
        Set<String> subordinateUserIds = querySubordinateUserIds(leaderIds, priorUserId);
        if (!isFilter) {
            setAllJobPriorUserIdsBySubordinateUserIds(priorUserId, priorUserIds, subordinateUserIds, priorUserJobs,
                    false);
        }
        return subordinateUserIds;
    }

    @Override
    public Set<String> querySubordinateUserIdsByUserIdOfJobId(String priorUserId,
                                                              Map<String, List<OrgUserJobDto>> priorUserIds, String jobId, boolean isFilter) {
        List<String> leaderIds = new ArrayList<>();
        leaderIds.add(priorUserId);
        leaderIds.add(jobId);
        Set<String> subordinateUserIds = querySubordinateUserIds(leaderIds, priorUserId);
        if (!isFilter) {
            setUserSelectJobPriorUserIdsBySubordinateUserIds(priorUserId, priorUserIds, subordinateUserIds, jobId);
        }
        return subordinateUserIds;
    }

    /**
     * 获取用户所有职位对应直属人员ID
     * 1、组织用户-->直属领导人员
     * 2、部门负责人，部门下所有人员
     * 3、分管领导下部门负责人
     *
     * @param leaderIds   前办理人ID或者工作ID集合
     * @param priorUserId 前办理人ID
     * @return
     * @author baozh
     * @date 2022/1/27 10:10
     */
    private Set<String> querySubordinateUserIds(List<String> leaderIds, String priorUserId) {
        Set<String> resultUserIds = new HashSet<>();
        // 1、组织用户-->直属领导人员
        List<MultiOrgUserWorkInfo> list = multiOrgUserWorkInfoService.getUserWorkInfoByLeaderIds(leaderIds);
        for (MultiOrgUserWorkInfo userWorkInfo : list) {
            resultUserIds.add(userWorkInfo.getUserId());
        }
        // 获取在使用的版本
        List<MultiOrgVersion> orgVersions = getCurrentOrgVersionByUserId(priorUserId);
        // List<String> removeUserIds = new ArrayList<>();
        for (String leaderId : leaderIds) {
            for (MultiOrgVersion orgVersion : orgVersions) {
                // 2、部门负责人，部门下所有人员
                // resultUserIds.addAll(queryBossUnderlingUserList(leaderId,
                // orgVersion.getVersion()));
                Set<String> bossIds = queryUnderlingUserListByJobIdPathAndType(leaderId, orgVersion.getRootVersionId(),
                        "boss");
                if (bossIds != null && !bossIds.isEmpty()) {
                    // 去除部门负责人
                    // removeUserIds.addAll();
                    bossIds.removeAll(multiOrgUserTreeNodeService.queryUserIdsByLikeElementId(leaderId,
                            orgVersion.getRootVersionId()));
                }
                resultUserIds.addAll(bossIds);
                // 3、分管领导下部门负责人
                resultUserIds.addAll(queryBossUserListByBranch(leaderId, orgVersion.getRootVersionId()));
            }
        }
        // resultUserIds.removeAll(removeUserIds);
        return resultUserIds;
    }

    private void setAllJobPriorUserIdsBySubordinateUserIds(String priorUserId,
                                                           Map<String, List<OrgUserJobDto>> priorUserIds, Set<String> subordinateUserIds,
                                                           List<OrgUserJobDto> priorUserJobs, Boolean isAll) {
        if (subordinateUserIds.isEmpty()) {
            return;
        }
        Set<String> jobIdSet = new HashSet<>();
        for (OrgUserJobDto priorUserJob : priorUserJobs) {
            List<String> jobIds = Lists.newArrayList();
            jobIds.add(priorUserJob.getOrgTreeNodeDto().getEleId());
            Set<String> versionIds = getCurrentOrgVersionByOrgId(jobIds);
            if (versionIds.size() > 0) {
                for (String versionId : versionIds) {
                    List<OrgTreeNodeDto> underlingOrgIds = multiOrgService
                            .queryBossUnderlingNodeListByJobId(priorUserJob.getOrgTreeNodeDto().getEleId(), versionId);
                    List<OrgTreeNodeDto> branchUnderlingOrgIds = multiOrgService.queryBranchUnderlingNodeListByJobId(
                            priorUserJob.getOrgTreeNodeDto().getEleId(), versionId);
                    if (isAll) {
                        underlingOrgIds.addAll(branchUnderlingOrgIds);
                    }
                    // 找负责人
                    if (!branchUnderlingOrgIds.isEmpty()) {
                        for (OrgTreeNodeDto branchUnderlingOrgTreeNodeDto : branchUnderlingOrgIds) {
                            List<String> nodeIds = multiOrgService.queryBossListByEleId(
                                    branchUnderlingOrgTreeNodeDto.getEleId(),
                                    branchUnderlingOrgTreeNodeDto.getOrgVersionId());
                            jobIdSet.addAll(nodeIds);

                        }
                    }

                    for (OrgTreeNodeDto dto : underlingOrgIds) {
                        List<OrgTreeNodeDto> nodeDtos = multiOrgTreeNodeService
                                .queryAllNodeOfOrgVersionByEleIdPath(versionId, dto.getEleIdPath());
                        jobIdSet.addAll(nodeDtos.stream().filter(
                                        nodeDto -> !nodeDto.getEleId().equals(priorUserJob.getOrgTreeNodeDto().getEleId()))
                                .map(OrgTreeNodeDto::getEleId).collect(Collectors.toSet()));
                    }
                }
            }
        }

        // 查询人员所属岗位
        Map<String, List<OrgUserJobDto>> allUserJobIdMap = getAllUserJobIdsIgnoreVersion(
                new ArrayList<>(subordinateUserIds));
        for (Map.Entry<String, List<OrgUserJobDto>> userJobIdEntry : allUserJobIdMap.entrySet()) {
            for (OrgUserJobDto jobDto : userJobIdEntry.getValue()) {
                if (jobIdSet.contains(jobDto.getOrgTreeNodeDto().getEleId())) {
                    if (!priorUserIds.containsKey(userJobIdEntry.getKey())) {
                        priorUserIds.put(userJobIdEntry.getKey(), new ArrayList<>());
                    }
                    OrgUserJobDto userJobDto = new OrgUserJobDto();
                    userJobDto.setUserId(userJobIdEntry.getKey());
                    userJobDto.setOrgTreeNodeDto(jobDto.getOrgTreeNodeDto());
                    priorUserIds.get(userJobIdEntry.getKey()).add(userJobDto);
                }
            }
        }
    }

    private void setUserSelectJobPriorUserIdsBySubordinateUserIds(String priorUserId,
                                                                  Map<String, List<OrgUserJobDto>> priorUserIds, Set<String> subordinateUserIds, String jobId) {
        if (subordinateUserIds.isEmpty()) {
            return;
        }
        List<String> jobIds = Lists.newArrayList();
        jobIds.add(jobId);
        Set<String> versionIds = getCurrentOrgVersionByOrgId(jobIds);
        Map<String, OrgTreeNodeDto> jobDtoMap = new HashMap<>();
        if (versionIds.size() > 0) {
            for (String versionId : versionIds) {
                List<OrgTreeNodeDto> underlingOrgIds = multiOrgService.queryBossUnderlingNodeListByJobId(jobId,
                        versionId);
                List<OrgTreeNodeDto> branchUnderlingOrgIds = multiOrgService.queryBranchUnderlingNodeListByJobId(jobId,
                        versionId);
                underlingOrgIds.addAll(branchUnderlingOrgIds);
                for (OrgTreeNodeDto dto : underlingOrgIds) {
                    List<OrgTreeNodeDto> nodeDtos = multiOrgTreeNodeService
                            .queryAllNodeOfOrgVersionByEleIdPath(versionId, dto.getEleIdPath());
                    jobDtoMap.putAll(nodeDtos.stream().filter(nodeDto -> !nodeDto.getEleId().equals(jobId))
                            .collect(Collectors.toMap(OrgTreeNodeDto::getEleId, t -> t)));
                }
            }
        }
        // 查询人员所属岗位
        Map<String, List<OrgUserJobDto>> allUserJobIdMap = getAllUserJobIdsIgnoreVersion(
                new ArrayList<>(subordinateUserIds));
        for (Map.Entry<String, List<OrgUserJobDto>> userJobIdEntry : allUserJobIdMap.entrySet()) {
            for (OrgUserJobDto jobDto : userJobIdEntry.getValue()) {
                if (jobDtoMap.containsKey(jobDto.getOrgTreeNodeDto().getEleId())) {
                    if (!priorUserIds.containsKey(userJobIdEntry.getKey())) {
                        priorUserIds.put(userJobIdEntry.getKey(), new ArrayList<>());
                    }
                    OrgUserJobDto userJobDto = new OrgUserJobDto();
                    userJobDto.setUserId(userJobIdEntry.getKey());
                    userJobDto.setOrgTreeNodeDto(jobDto.getOrgTreeNodeDto());
                    priorUserIds.get(userJobIdEntry.getKey()).add(userJobDto);
                }
            }
        }
    }

    private Set<String> queryBossUserListByBranch(String leaderId, String versionId) {
        List<OrgTreeNodeDto> nodeDtos = multiOrgService.queryBranchUnderlingNodeListByJobId(leaderId, versionId);
        Set<String> resultUserIds = new HashSet<>();
        for (OrgTreeNodeDto nodeDto : nodeDtos) {
            List<String> bossJobIds = multiOrgService.queryBossListByEleId(nodeDto.getEleId(),
                    nodeDto.getOrgVersionId());
            for (String jobId : bossJobIds) {
                resultUserIds.addAll(queryUserIdListByOrgId(nodeDto.getOrgVersionId(), jobId, true));
            }
        }
        return resultUserIds;
    }

    @Override
    public Set<String> queryAllSubordinateUserIdsByUserIdOfAllJob(String priorUserId,
                                                                  Map<String, List<OrgUserJobDto>> priorUserIds, List<OrgUserJobDto> priorUserJobs, boolean isFilter) {
        List<String> leaderIds = new ArrayList<>();
        leaderIds.add(priorUserId);
        for (OrgUserJobDto priorUserJob : priorUserJobs) {
            leaderIds.add(priorUserJob.getOrgTreeNodeDto().getEleId());
        }
        Set<String> subordinateUserIds = queryAllSubordinateUserIds(leaderIds, priorUserId);
        if (!isFilter) {
            setAllJobPriorUserIdsBySubordinateUserIds(priorUserId, priorUserIds, subordinateUserIds, priorUserJobs,
                    true);
        }
        return subordinateUserIds;
    }

    @Override
    public Set<String> queryAllSubordinateUserIdsByUserIdOfJobId(String priorUserId,
                                                                 Map<String, List<OrgUserJobDto>> priorUserIds, String jobId, boolean isFilter) {
        List<String> leaderIds = new ArrayList<>();
        leaderIds.add(priorUserId);
        leaderIds.add(jobId);
        Set<String> subordinateUserIds = queryAllSubordinateUserIds(leaderIds, priorUserId);
        if (!isFilter) {
            setUserSelectJobPriorUserIdsBySubordinateUserIds(priorUserId, priorUserIds, subordinateUserIds, jobId);
        }
        return subordinateUserIds;
    }

    /**
     * 获取用户所有职位对应直属人员ID
     * 1、组织用户-->直属领导人员
     * 2、部门负责人，部门下所有人员
     * 3、分管领导下部门负责人
     *
     * @param leaderIds   前办理人ID或者工作ID集合
     * @param priorUserId 前办理人ID
     * @return
     * @author baozh
     * @date 2022/1/27 10:10
     */
    private Set<String> queryAllSubordinateUserIds(List<String> leaderIds, String priorUserId) {
        Set<String> resultUserIds = new HashSet<>();
        // 1、组织用户-->直属领导人员
        List<MultiOrgUserWorkInfo> list = multiOrgUserWorkInfoService.getUserWorkInfoByLeaderIds(leaderIds);
        for (MultiOrgUserWorkInfo userWorkInfo : list) {
            resultUserIds.add(userWorkInfo.getUserId());
        }
        // 直属领导下全部人员
        if (!resultUserIds.isEmpty()) {
            for (String resultUserId : resultUserIds) {
                List<String> userids = Lists.newArrayList();
                userids.add(resultUserId);
                Map<String, List<OrgUserJobDto>> priorUserJobsMap = getAllUserJobIdsIgnoreVersion(userids);
                if (priorUserJobsMap.containsKey(priorUserId)) {
                    List<OrgUserJobDto> priorUserJobs = priorUserJobsMap.get(priorUserId);
                    for (OrgUserJobDto priorUserJob : priorUserJobs) {
                        leaderIds.add(priorUserJob.getOrgTreeNodeDto().getEleId());
                    }
                }
            }
        }
        // 获取在使用的版本
        List<MultiOrgVersion> orgVersions = getCurrentOrgVersionByUserId(priorUserId);
        // List<String> removeUserIds = new ArrayList<>();
        for (String leaderId : leaderIds) {
            for (MultiOrgVersion orgVersion : orgVersions) {
                // 2、部门负责人，部门下所有人员
                Set<String> bossIds = queryUnderlingUserListByJobIdPathAndType(leaderId, orgVersion.getRootVersionId(),
                        "boss");
                if (bossIds != null && !bossIds.isEmpty()) {
                    bossIds.removeAll(multiOrgUserTreeNodeService.queryUserIdsByLikeElementId(leaderId,
                            orgVersion.getRootVersionId()));
                }
                resultUserIds.addAll(bossIds);
                // 3、分管领导下所有人员
                Set<String> branchIds = queryUnderlingUserListByJobIdPathAndType(leaderId,
                        orgVersion.getRootVersionId(), "branch");
                if (branchIds != null && !branchIds.isEmpty()) {
                    branchIds.removeAll(multiOrgUserTreeNodeService.queryUserIdsByLikeElementId(leaderId,
                            orgVersion.getRootVersionId()));
                }
                resultUserIds.addAll(branchIds);
                // removeUserIds.addAll(multiOrgUserTreeNodeService.queryUserIdsByLikeElementId(leaderId,
                // orgVersion.getRootVersionId()));
            }
        }
        // resultUserIds.removeAll(removeUserIds);
        return resultUserIds;
    }

    @Override
    public boolean isExistUserByEleIds(String userId, Set<String> eleIdSet) {
        if (CollectionUtils.isEmpty(eleIdSet) || StringUtils.isBlank(userId)) {
            return false;
        }
        Set<String> groupIdSet = new HashSet<>();
        Set<String> eleSet = new HashSet<>();
        for (String eleId : eleIdSet) {
            if (eleId.startsWith(IdPrefix.GROUP.getValue())) {
                groupIdSet.add(eleId);
            } else {
                eleSet.add(eleId);
            }
        }
        if (CollectionUtils.isNotEmpty(groupIdSet)) {
            List<MultiOrgGroupMember> groupMemberList = multiOrgGroupMemberService.queryMemberListOfIds(groupIdSet, true);
            for (MultiOrgGroupMember multiOrgGroupMember : groupMemberList) {
                if (multiOrgGroupMember.getMemberObjType().equals(IdPrefix.GROUP.getValue())) {
                    continue;
                }
                if (multiOrgGroupMember.getMemberObjType().equals(IdPrefix.USER.getValue())) {
                    if (multiOrgGroupMember.getMemberObjId().equals(userId)) {
                        return true;
                    }
                } else {
                    eleSet.add(multiOrgGroupMember.getMemberObjId());
                }
            }
        }
        if (CollectionUtils.isEmpty(eleSet)) {
            return false;
        }
        MultiOrgUserWorkInfo workInfo = multiOrgUserWorkInfoService.getUserWorkInfo(userId);
        if (workInfo == null || StringUtils.isBlank(workInfo.getEleIdPaths())) {
            return false;
        }

        for (String eleId : eleSet) {
            if (workInfo.getEleIdPaths().indexOf(eleId) > -1) {
                return true;
            }
        }
        return false;
    }

}
