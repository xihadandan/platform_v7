/*
 * @(#)2013-1-27 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.multi.org.facade.service;

import com.wellsoft.context.service.BaseService;
import com.wellsoft.context.util.groovy.GroovyUseable;
import com.wellsoft.pt.multi.org.bean.*;
import com.wellsoft.pt.multi.org.dto.CurrentUnitJobGradeDto;
import com.wellsoft.pt.multi.org.dto.DutySeqAndjobRankDto;
import com.wellsoft.pt.multi.org.dto.GetCurrentUnitUserListByUserNameKeyDto;
import com.wellsoft.pt.multi.org.dto.OrgDutySeqTreeDto;
import com.wellsoft.pt.multi.org.entity.*;
import com.wellsoft.pt.multi.org.vo.JobRankLevelVo;
import com.wellsoft.pt.org.entity.DutyAgent;
import com.wellsoft.pt.org.support.UsersGrade;

import java.util.*;

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
@GroovyUseable
public interface OrgApiFacade extends BaseService {

    // 批量通过ID获取对应的组织元素
    List<MultiOrgElement> queryOrgElementListByIds(Collection<String> eleIds);

    // 通过ID,获取对应的组织元素
    MultiOrgElement getOrgElementById(String eleId);

    // 通过orgId, 批量获取对应的用户
    HashMap<String, String> getUsersByOrgIds(List<String> orgIds);

    HashMap<String, String> getInternetUsersByLoginNames(List<String> loginNames);

    // 通过orgId, 批量获取对应的用户
    HashMap<String, String> getUsersByOrgIds(String orgIds);

    // 获取指定节点当前启用版本下的用户列表
    Set<String> queryUserIdListByOrgId(String eleId, boolean isInMyUnit);

    // 通过组织元素ID,获取该节点下的所有用户
    Set<MultiOrgUserAccount> queryUserListByOrgId(String eleId, boolean isInMyUnit);

    // 通过组织元素ID,获取该节点下的所有用户ID
    Set<String> queryUserIdListByOrgId(String orgVersionId, String eleId, boolean isInMyUnit);

    // 通过组织元素ID,获取该节点下的所有职位ID和名称
    HashMap<String, String> getJobsByOrgIds(String orgIds);

    HashMap<String, String> getJobsByOrgIds(List<String> orgIds);

    // 通过组织元素ID,获取该节点下的所有用户
    Set<MultiOrgUserAccount> queryUserListByOrgId(String orgVersionId, String eleId, boolean isInMyUnit);

    // 通过群组ID,获取对应的用户
    HashMap<String, String> queryAllUsesByGroupId(String groupId);

    // 通过群组ID,获取对应的群组完整信息，包括成员信息
    OrgGroupVo getGroupVoById(String groupId);

    // 通过群组ID,获取对应的群组基本信息
    MultiOrgGroup getGroupById(String groupId);

    // 需要改成获取某一个单位内的所有账号
    List<MultiOrgUserAccount> queryAllAccountOfUnit(String systemUnitId);

    // 通过用户ID批量获取对应的账号信息
    List<MultiOrgUserAccount> queryUserAccountListByIds(Collection<String> userIds);

    // 返回一个平台管理员账号
    OrgUserVo getPTAdmin();

    // 获取单位管理员，如果有多个，则返回第一个
    OrgUserVo getUnitAdmin(String unitId);

    // 通过ID,获取一个用户的完整信息
    OrgUserVo getUserVoById(String userId);

    // 通过ID,获取一个用户的信息
    OrgUserVo getUserVoById(String userId, boolean includeJobInfo, boolean includeWorkInfo, boolean includeRoleInfo);

    // 通过登录名，获取账号信息
    MultiOrgUserAccount getAccountByLoginName(String loginName);

    // 通过UUID,获取对应的账号信息
    MultiOrgUserAccount getAccountByUuid(String userUuid);

    // 通过ID,获取对应的账号信息
    MultiOrgUserAccount getAccountByUserId(String userId);

    @Deprecated
    UsersGrade orderedUserIdsByJobGrade(Collection<String> userIds);

    // 获取职位对应的职务信息
    MultiOrgDuty getDutyById(String id);

    // 批量获取用户
    List<OrgUserDto> queryUserDtoListByIds(List<String> userIds);

    @Deprecated
    List<DutyAgent> getDutyAgents(List<String> consignorIds, String businessType);

    @Deprecated
    List<String> getDutyAgentIds(String userId, String businessType, String content, Map<Object, Object> root);

    // 获取一个用户的所有相关组织ID,包含组织，群组，职务 三方面信息
    Set<String> getUserOrgIds(String userId);

    Set<String> getUserOrgIds(OrgUserVo userVo);

    // 判断用户是否是memberOf中的成员
    boolean isMemberOf(String userId, String memberOf);

    boolean isMemberOf(String userId, String[] memberOf, String jobId);

    // 获取一个系统单位的所有管理员账号ID
    List<String> queryAllAdminIdsByUnitId(String systemUnitId);

    // 通过ID获取对应的组织元素，用户，职务的名称
    String getNameByOrgEleId(String eleId);

    // 通过orgId 批量获取对应的组织名称
    HashMap<String, String> getNameByOrgEleIds(List<String> orgIds);

    // 通过用户ID,获取用户名称
    String getUserNameById(String userId);

    // 通过职位全路径，获取部门全路径，不包含单位
    String getDepartmentNamePathByJobIdPath(String mainJobIdPath, boolean isNeedUnit);

    // 获取用户的直属上级领导
    Set<String> queryUserDirectLeaderList(String userId);

    // 返回用户所有对应的上级领导账号ID,isAll代表是否所有上级节点
    Set<String> queryAllUserSuperiorLeaderList(String userId, String orgVersionId, boolean isAll);

    // 返回用户主职对应的上级领导账号ID,isAll代表是否所有上级节点
    Set<String> queryUserSuperiorLeaderList(String userId, String orgVersionId, boolean isAll);

    // 返回指定职位的上级领导账号ID, isAll代表是否所有上级节点
    Set<String> queryUserSuperiorLeaderListByJobIdPath(String jobIdPath, boolean isAll);

    // 用户所有对应的的部门领导账号ID
    Set<String> queryAllUserDepartmentLeaderList(String userId, String orgVersionId);

    // 用户主职对应的的部门领导账号ID
    Set<String> queryUserDepartmentLeaderList(String userId, String orgVersionId);

    // 返回指定职位的部门领导账号ID
    Set<String> queryUserDepartmentLeaderListByJobIdPath(String jobIdPath);

    // 获取指定节点对应的领导账号ID
    Set<String> queryBossLeaderUserListByNode(String eleId, String orgVersionId);

    // 返回用户所有对应的分管领导账号ID
    Set<String> queryAllUserBranchLeaderList(String userId, String orgVersionId);

    // 返回用户主职对应的分管领导账号ID
    Set<String> queryUserBranchLeaderList(String userId, String orgVersionId);

    // 返回指定职位的分管领导账号ID
    Set<String> queryUserBranchLeaderListByJobIdPath(String jobIdPath);

    // 获取指定节点对应的分管领导账号ID
    Set<String> queryBranchLeaderUserListByNode(String eleId, String orgVersionId);

    // 获取用户对应的部门下属账号ID
    Set<String> queryBossUnderlingUserList(String userId, String orgVersionId);

    // 获取用户对应的分管下属账号ID
    Set<String> queryBranchUnderlingUserList(String userId, String orgVersionId);

    // 获取指定职位对应的部门下属账号ID
    Set<String> queryBossUnderlingUserListByJobIdPath(String jobIdPath);

    // 获取指定职位对应的分管下属账号ID
    Set<String> queryBranchUnderlingUserListByJobIdPath(String jobIdPath);

    // 通过停用的组织版本ID，换取当前正在启用的版本
    MultiOrgVersion getCurrentActiveVersionByOrgVersionId(String orgVersionId);

    // 返回用户对应的部门ID
    Set<String> queryDepartmentIdListByUserId(String userId, String orgVersionId);

    /**
     * //返回用户的主职部门ID
     * 不存在返回null
     *
     * @param userId
     * @param orgVersionId
     * @return java.lang.String
     **/
    String queryMainDepartmentIdListByUserId(String userId, String orgVersionId);

    // 返回用户对应的部门用户
    Set<String> queryDepartmentUserIdsByUserId(String userId, String orgVersionId);

    // 返回用户对应的主职部门用户
    Set<String> queryMainDepartmentUserIdsByUserId(String userId, String orgVersionId);

    // 返回用户对应的上级部门ID
    Set<String> queryParentDepartmentIdsByUserId(String userId, String orgVersionId);

    // 返回用户对应的上级部门ID（指定职位范围内的）
    Set<String> queryParentDepartmentIdsByUserId(String userId, String orgVersionId, String jobId);

    // 返回用户对应的根部门ID,离的最远的部门节点
    Set<String> queryRootDepartmentIdsByUserId(String userId, String orgVersionId);

    // 返回用户主职对应的根部门ID,离的最远的部门节点
    Set<String> queryMainRootDepartmentIdsByUserId(String userId, String orgVersionId);

    // 返回用户对应的根部门ID,离的最远的部门节点（指定职位范围内的）
    Set<String> queryRootDepartmentIdsByUserId(String userId, String orgVersionId, String jobId);

    // 返回用户对应的业务单位ID,离的最近的业务单位节点（指定职位范围内的）
    Set<String> queryBizUnitIdsByUserId(String priorUserId, String orgVersionId, String jobId);

    // 返回用户对应的业务单位ID,离的最近的业务单位节点
    Set<String> queryBizUnitIdsByUserId(String priorUserId, String orgVersionId);

    // 返回用户主职对应的业务单位ID,离的最近的业务单位节点
    Set<String> queryMainBizUnitIdsByUserId(String priorUserId, String orgVersionId);

    // 返回用户对应的根部门的所有用户ID
    List<String> queryRootDepartmentUserIdsByUserId(String userId, String orgVersionId);

    // 返回用户主职对应的根部门的所有用户ID
    List<String> queryMainRootDepartmentUserIdsByUserId(String userId, String orgVersionId);

    // 返回用户对应的业务单位的所有用户ID
    List<String> queryBizUnitUserIdsByUserId(String userId, String orgVersionId);

    // 返回用户主职对应的业务单位的所有用户ID
    List<String> queryMainBizUnitUserIdsByUserId(String userId, String orgVersionId);

    // 通过名字，模糊匹配用户
    List<String> getOrgIdsLikeName(String rawName);

    // 获取用户的完整的角色列表，包括职务，职位，群组所带的角色
    Map<String, Set<String>> queryAllRoleListByUser(String userId);

    Map<String, Set<String>> queryAllRoleListByUser(OrgUserVo user);

    List<UserRoleInfoDto> queryAllUserRoleInfoDtoList(String userId);

    List<UserRoleInfoDto> queryAllUserRoleInfoDtoList(String userId, String[] roleUuids);

    List<UserRoleInfoDto> queryAllUserRoleInfoDtoList(OrgUserVo user, String[] roleUuids);

    // 获取归属单位
    MultiOrgSystemUnit getSystemUnitById(String unitId);

    // 获取组织元素正在使用的版本
    String getCurrentVersionByEleId(String eleId);

    // 通过用户ID,获取该用户对应的单位正在使用的组织版本列表
    List<MultiOrgVersion> getCurrentOrgVersionByUserId(String userId);

    List<MultiOrgVersion> queryCurrentActiveVersionListOfSystemUnit(String unitId);

    // 通过组织ID,获取对应的组织版本ID
    Set<String> getCurrentOrgVersionByOrgId(Collection<String> orgIds);

    // 修改用户信息
    OrgUserVo modifyUserVo(OrgUserVo userVo);

    // 获取用户所属的部门id和职位id
    MultiOrgUserWorkInfo getUserWorkInfoByUserId(String userId);

    // 获取所有的系统单位
    List<MultiOrgSystemUnit> queryAllSystemUnitList();

    // 通过id全路径，获取对应的所有节点的信息
    Map<String, MultiOrgElement> queryElementMapByEleIdPath(String idPath);

    // 通过职位全路径，获取离的最近的业务单位的名称
    String getBusinessUnitNameByJobIdPath(String jobIdPath);

    MultiOrgElement getBusinessUnitByJobIdPath(String jobIdPath);

    // 通过元素ID,获取该元素在当前使用的版本的组织树节点
    OrgTreeNodeDto getNodeOfCurrentVerisonByEleId(String eleId);

    // 通过全路径名称，换取全路径ID
    String getEleIdPathByEleNamePath(String namePath, String orgVersionId);

    // 通过ID全路径，换取全路径名称
    String getEleNamePathByEleIdPath(String idPath);

    // 通过节点ID和版本ID,换取全路径ID
    String getEleIdPathByEleId(String eleId, String orgVersionId);

    // 获取指定节点路径下的所有子节点，包含自己
    List<OrgTreeNodeDto> queryAllNodeOfOrgVersionByEleIdPath(String verId, String eleIdPath);

    List<OrgTreeNodeDto> queryAllNodeOfOrgVersionByEleIdPath(String verId, String eleIdPath, String eleType);

    /**
     * 通过元素ID,获取该元素在当前使用的版本的所有上级直接组织树节点ID
     *
     * @param ownerIds
     * @param eleTypes
     * @return
     */
    List<String> getSuperiorOrgIdsByOrgEleIdAndEleTypes(List<String> eleIds, String... eleTypes);

    /**
     * 通过元素ID,获取该元素在当前使用的版本的所有同级组织树节点ID
     *
     * @param ownerIds
     * @param eleTypes
     * @return
     */
    List<String> getSiblingOrgIdsByOrgEleIdAndEleTypes(List<String> eleIds, String... eleTypes);

    /**
     * 通过元素ID,获取该元素在当前使用的版本的所有下级组织树节点ID
     *
     * @param ownerIds
     * @param eleTypes
     * @return
     */
    List<String> getSubordinateOrgIdsByOrgEleIdAndEleTypes(List<String> eleIds, String... eleTypes);

    String getMultiOrgElementAttrValue(String attrCode, String elementUuid);

    String getMultiOrgElementAttrValueById(String attrCode, String elementId);

    MultiOrgSystemUnit getSystemUnitByCode(String unitCode);

    /**
     * 获取当前用户的扩展属性
     *
     * @param propName
     * @return
     */
    String getCurrentUserProperty(String propName);

    String getUserProperty(String propName, String userUuid);

    void saveUserProperty(String prop, String value);

    public abstract void saveUserProperty(String userId, String prop, String value);

    public abstract String getCurrentUserProperty(String userId, String propName);

    List<MultiOrgOption> getOrgOptionsByIds(String[] ids);

    /**
     * 获取用户所有职位的部门领导
     *
     * @param creator
     * @return
     */
    Set<String> queryUserAllJobDepartmentLeaderList(String userId);

    /**
     * 获取用户的主职位的部门领导
     *
     * @param creator
     * @return
     */
    Set<String> queryUserMainJobDepartmentLeaderList(String userId);

    /**
     * 获取指定用户职位的部门领导
     *
     * @param userId
     * @param jobId
     * @return
     */
    Set<String> queryUserJobDepartmentLeaderList(String userId, String jobId);

    /**
     * 获取指定用户的所有职位的分管领导
     *
     * @param userId
     * @return
     */
    Set<String> queryUserAllJobBranchLeaderList(String userId);

    /**
     * 获取指定用户的主职的分管领导
     *
     * @param userId
     * @return
     */
    Set<String> queryUserMainJobBranchLeaderList(String userId);

    /**
     * 获取指定用户职位的分管领导
     *
     * @param userId
     * @param jobId
     * @return
     */
    Set<String> queryUserJobBranchLeaderList(String userId, String jobId);

    /**
     * 获取用户所有职位对应的所有上级领导
     *
     * @param userId
     * @return
     */
    Set<String> queryUserAllJobSuperiorLeaderList(String userId);

    /**
     * 获取用户主职对应的所有上级领导
     *
     * @param userId
     * @return
     */
    Set<String> queryUserMainJobSuperiorLeaderList(String userId);

    /**
     * 获取用户职位对应的所有上级领导
     *
     * @param userId
     * @param jobId
     * @return
     */
    Set<String> queryUserJobSuperiorLeaderList(String userId, String jobId);

    /**
     * 获取用户的所有所属部门的所有人员ID
     *
     * @param userId
     * @return
     */
    Set<String> queryAllJobDepartmentUserListByUserId(String userId);

    /**
     * 获取用户的所有所属部门ID集合
     *
     * @param userId
     * @return
     */
    Set<String> queryAllJobDepartmentIdListByUserId(String userId);

    /**
     * 获取用户的主职所在的部门下的所有人员ID
     *
     * @param userId
     * @return
     */
    Set<String> queryMainJobDepartmentUserListByUserId(String userId);

    /**
     * 获取用户的主职所在的部门ID集合
     *
     * @param userId
     * @return
     */
    Set<String> queryMainJobDepartmentIdListByUserId(String userId);

    /**
     * 获取用户的指定职位所在的部门下的所有人员ID
     *
     * @param userId
     * @param jobId
     * @return
     */
    Set<String> queryJobDepartmentUserListByUserId(String userId, String jobId);

    /**
     * 获取用户的指定职位所在的部门ID集合
     *
     * @param userId
     * @param jobId
     * @return
     */
    Set<String> queryJobDepartmentIdListByUserId(String userId, String jobId);

    /**
     * 获取用户所有职位的上一级部门下的所有人员ID
     *
     * @param userId
     * @return
     */
    Set<String> queryAllJobParentDepartmentUserIdsByUserId(String userId);

    /**
     * 获取用户所有职位的上一级部门ID 集合
     *
     * @param userId
     * @return
     */
    Set<String> queryAllJobParentDepartmentIdsByUserId(String userId);

    /**
     * 获取用户主职的上一级部门下的所有人员ID
     *
     * @param userId
     * @return
     */
    Set<String> queryMainJobParentDepartmentUserIdsByUserId(String userId);

    /**
     * 获取用户主职的上一级部门ID集合
     *
     * @param userId
     * @return
     */
    Set<String> queryMainJobParentDepartmentIdsByUserId(String userId);

    /**
     * 获取用户职位对应的上一级部门下的所有人员ID
     *
     * @param userId
     * @param jobId
     * @return
     */
    Set<String> queryJobParentDepartmentUserIdsByUserId(String userId, String jobId);

    /**
     * 获取用户职位对应的上一级部门ID集合
     *
     * @param userId
     * @param jobId
     * @return
     */
    Set<String> queryJobParentDepartmentIdsByUserId(String userId, String jobId);

    /**
     * 获取用户所有职位的根级部门下的所有人员ID
     *
     * @param userId
     * @return
     */
    Set<String> queryAllJobRootDepartmentUserIdsByUserId(String userId);

    /**
     * 获取用户所有职位的根级部门ID集合
     *
     * @param userId
     * @return
     */
    Set<String> queryAllJobRootDepartmentIdsByUserId(String userId);

    /**
     * 获取用户主职的根级部门下的所有人员ID
     *
     * @param userId
     * @return
     */
    Set<String> queryMainJobRootDepartmentUserIdsByUserId(String userId);

    /**
     * 获取用户主职的根级部门ID 集合
     *
     * @param userId
     * @return
     */
    Set<String> queryMainJobRootDepartmentIdsByUserId(String userId);

    /**
     * 获取用户职位对应的根级部门下的所有人员ID
     *
     * @param userId
     * @param jobId
     * @return
     */
    Set<String> queryJobRootDepartmentUserIdsByUserId(String userId, String jobId);

    /**
     * 获取用户职位对应的根级部门ID集合
     *
     * @param userId
     * @param jobId
     * @return
     */
    Set<String> queryJobRootDepartmentIdsByUserId(String userId, String jobId);

    /**
     * 获取用户所有职位对应业务单位下的所有人员ID
     *
     * @param userId
     * @return
     */
    Set<String> queryAllJobBizUnitUserIdsByUserId(String userId);

    /**
     * 获取用户主职所在的业务单位下的所有人员ID
     *
     * @param userId
     * @return
     */
    Set<String> queryMainJobBizUnitUserIdsByUserId(String userId);

    /**
     * 获取用户职位所在的业务单位下的所有人员ID
     *
     * @param userId
     * @param jobId
     * @return
     */
    Set<String> queryJobBizUnitUserIdsByUserId(String userId, String jobId);

    List<OrgElementVo> getUserJobElementByUserId(String userId);

    /**
     * 获取某个组织节点下具有某个角色的所有人员接口
     * 返回信息太少 请用新的接口getOrgUserDtosByEleIdRid
     */
    @Deprecated
    List<SimpleUser> getSimpleUsersByEleIdRid(String eleId, String roleId);

    /**
     * 获取某个组织节点下具有某个角色的所有人员接口
     *
     * @param eleId  组织节点
     * @param roleId 角色id
     * @return 完整的用户信息列表
     **/
    List<OrgUserDto> getOrgUserDtosByEleIdRid(String eleId, String roleId);

    boolean isMemberOfMainJob(String userId, String join);

    boolean isMemberOfSelectedJob(String userId, String jobId, String join);

    boolean isMemberOfAllJob(String userId, String join);

    boolean isMemberOfGroup(String userId, Set<String> groupIds);

    boolean isMemberOfSelectedJobDuty(String userId, Set<String> dutyIds, String jobId);

    boolean isMemberOfMainJobDuty(String userId, Set<String> dutyIds);

    boolean isMemberOfAllJobDuty(String userId, Set<String> dutyIds);

    boolean isMemberOfExternal(String userId, Set<String> externalIds);

    String getUserMainJobId(String userId);

    /**
     * 对节点全路径集合进行组织排序
     *
     * @param eleIdPaths 节点全路径集合
     * @return key:eleIdPath value 排序位置序号
     */
    Map<String, Integer> getOrgEleOrderByEleIdPaths(List<String> eleIdPaths);

    List<MultiOrgUserAccount> getAccountsByUserIds(List<String> ids);

    List<MultiOrgElement> getOrgElementsByIds(List<String> ids);

    List<MultiOrgGroup> getGroupsByIds(List<String> ids);

    List<MultiOrgDuty> getDutysByIds(List<String> ids);

    Map<String, OrgUserVo> getUserAccoutVoWithMainJobByIds(List<String> userids);

    Map<String, OrgUserVo> getUserAccoutVoWithAllJobByIds(List<String> userids);

    Map<String, String> getNamesByOrgEleIds(Set<String> ids);

    /**
     * 返回用户的所有职位，包含主职和其他职位
     * key为userId
     *
     * @param userids
     **/
    Map<String, List<OrgUserJobDto>> getAllUserJobIdsIgnoreVersion(List<String> userids);

    /**
     * 返回用户工作信息列表
     *
     * @param userids
     * @return java.util.List<com.wellsoft.pt.multi.org.entity.MultiOrgUserWorkInfo>
     **/
    public List<MultiOrgUserWorkInfo> getUserWorkInfosByUserIds(List<String> userids);

    /**
     * 获取指定节点的一级分管领导职位节点
     *
     * @param eleId
     * @param orgVersionId
     * @return
     */
    public List<OrgTreeNodeDto> queryBranchLeaderNodeListByNode(String eleId, String orgVersionId);

    /**
     * 获取指定节点的负责人领导节点(部门领导)
     *
     * @param eleId
     * @param orgVersionId
     * @return
     */
    public List<OrgTreeNodeDto> queryBossLeaderNodeListByNode(String eleId, String orgVersionId);

    /**
     * 通过节点ID集合获取对应节点对象，包含节点的完整路径
     *
     * @param nodeIds
     * @return java.util.List<com.wellsoft.pt.multi.org.entity.MultiOrgTreeNode>
     **/
    public List<MultiOrgTreeNode> getOrgTreeNodeListByNodeIds(Set<String> nodeIds);

    /**
     * 根据userId获取职位职档信息
     *
     * @param userId
     * @return
     * @author baozh
     * @date 2021/10/27 14:18
     */
    public List<JobRankLevelVo> queryJobRankLevelListByUserId(String userId);

    /**
     * 查询当前用户单位职等排序
     *
     * @param
     **/
    public String getCurrentUnitJobGradeOrder();

    /**
     * 查询当前用户单位查询职等列表
     *
     * @param
     **/
    public List<CurrentUnitJobGradeDto> getCurrentUnitJobGradeList();

    /**
     * 查询当前用户单位查询职级列表
     *
     * @param
     **/
    public List<OrgDutySeqTreeDto> queryJobRankTree();

    /**
     * 获取职务序列编号和职级ID对应关系列表
     *
     * @param jobRankIds
     * @return java.util.List<com.wellsoft.pt.multi.org.dto.DutySeqAndjobRankDto>
     **/
    List<DutySeqAndjobRankDto> getDutySeqAndjobRankListByRankIds(List<String> jobRankIds);

    /**
     * 根据职级获取职级信息
     *
     * @param jobRankIds
     * @return
     */
    List<MultiOrgJobRank> getMultiOrgJobRankByJobRank(List<String> jobRankIds);

    /**
     * 根据职级UUID获取职级信息
     *
     * @param uuid 所属序列的职级列表
     * @return
     */
    public MultiOrgJobRank getMultiOrgJobRankDetailByUuid(String uuid);

    /**
     * 根据职级ID获取职级信息
     *
     * @param id 所属序列的职级列表
     * @return
     */
    public MultiOrgJobRank getMultiOrgJobRankDetailById(String id);

    /**
     * 所属序列的职级列表
     *
     * @param uuid
     * @return
     */
    public List<MultiOrgJobRank> getBelongSeqMultiOrgJobRankListByUuid(String uuid);

    Map<String, Set<String>> queryAllRoleListByUserByJobs(OrgUserVo user);

    /**
     * 通过用户名称模糊匹配获取当前用户单位的用户列表
     *
     * @param userNameKey 用户名称模糊匹配
     * @return java.util.List<getUserListByUserNameKeyDto>
     **/
    List<GetCurrentUnitUserListByUserNameKeyDto> getCurrentUnitUserListByUserNameKey(String userNameKey);

    /**
     * 获取用户所有职位对应直属人员ID
     *
     * @param priorUserId   前办理人Id
     * @param priorUserJobs 前办理人所有岗位
     * @return
     */
    Set<String> querySubordinateUserIdsByUserIdOfAllJob(String priorUserId, Map<String, List<OrgUserJobDto>> priorUserIds, List<OrgUserJobDto> priorUserJobs, boolean isFilter);

    /**
     * 获取用户指定职位对应直属人员ID
     *
     * @param priorUserId 前办理人Id
     * @param jobId       指定职位ID
     * @return
     * @author baozh
     * @date 2022/1/27 9:52
     */
    Set<String> querySubordinateUserIdsByUserIdOfJobId(String priorUserId, Map<String, List<OrgUserJobDto>> priorUserIds, String jobId, boolean isFilter);

    /**
     * 获取用户所有职位对应下属全部人员ID
     *
     * @param priorUserId
     * @return
     */
    Set<String> queryAllSubordinateUserIdsByUserIdOfAllJob(String priorUserId, Map<String, List<OrgUserJobDto>> priorUserIds, List<OrgUserJobDto> priorUserJobs, boolean isFilter);

    /**
     * 获取用户指定职位对应下属全部人员ID
     *
     * @param priorUserId
     * @param jobId
     * @return
     */
    Set<String> queryAllSubordinateUserIdsByUserIdOfJobId(String priorUserId, Map<String, List<OrgUserJobDto>> priorUserIds, String jobId, boolean isFilter);

    /**
     * 判断用户是否在组织节点+群组节点中
     *
     * @param userId
     * @param eleIdSet
     * @return
     */
    boolean isExistUserByEleIds(String userId, Set<String> eleIdSet);

}
