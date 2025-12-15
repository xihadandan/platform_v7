/*
 * @(#)2017年11月22日 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.multi.org.facade.service;

import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.context.service.BaseService;
import com.wellsoft.pt.multi.org.bean.*;
import com.wellsoft.pt.multi.org.entity.*;

import java.io.UnsupportedEncodingException;
import java.util.*;

/**
 * Description: 如何描述该类
 *
 * @author zyguo
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2017年11月22日.1	zyguo		2017年11月22日		Create
 * </pre>
 * @date 2017年11月22日
 */
public interface MultiOrgUserService extends BaseService {

    // 全路径分割符
    public static String PATH_SPLIT_SYSMBOL = "/";

    /**
     * 添加账号
     *
     * @param vo 包含的密码不加密
     * @return
     */
    public OrgUserVo addUser(OrgUserVo vo) throws UnsupportedEncodingException;

    /**
     * 修改账号信息
     *
     * @param vo
     * @return
     */
    public OrgUserVo modifyUser(OrgUserVo vo);

    /**
     * 获取用户账号和INFO信息
     *
     * @param uuid
     * @return
     */
    public OrgUserVo getUser(String uuid);

    /**
     * 添加账号
     *
     * @param vo
     * @return
     */
    public OrgUserVo addUnitAdmin(OrgUserVo vo) throws UnsupportedEncodingException;

    /**
     * 修改账号信息
     *
     * @param vo
     * @return
     */
    public OrgUserVo modifyUnitAdmin(OrgUserVo vo);

    /**
     * 如何描述该方法
     *
     * @param userUuid
     * @return
     */
    List<OrgUserJobDto> queryUserJob(String userUuid);

    /**
     * 如何描述该方法
     *
     * @param userId
     * @return
     */
    ArrayList<OrgUserJobDto> queryUserJobByUserId(String userId);

    /**
     * 获取组织树下面指定节点位置下面的用户
     *
     * @param eleId
     * @param orgVersionId
     * @return
     */
    List<OrgUserTreeNodeDto> queryUserByOrgTreeNode(String eleId, String orgVersionId);

    /**
     * 通过登录名获取用户信息
     *
     * @param loginName
     * @param loginNameHashAlgorithmCode
     * @return
     */
    public OrgUserVo getUserByLoginNameIgnoreCase(String loginName, String loginNameHashAlgorithmCode);

    /**
     * 通过ID获取用户信息
     *
     * @param userId
     * @return
     */
    public OrgUserVo getUserById(String userId);

    /**
     * 通过ID获取用户信息
     *
     * @param userId
     * @param includeJobInfo
     * @param includeWorkInfo
     * @param includeRoleInfo
     * @return
     */
    public OrgUserVo getUserById(String userId, boolean includeJobInfo, boolean includeWorkInfo,
                                 boolean includeRoleInfo);

    /**
     * 批量按用户ID获取用户
     *
     * @param ids
     * @return
     */
    public List<OrgUserVo> queryUserListByIds(List<String> ids);

    /**
     * 用户模块处理组织升级版本事件
     *
     * @param oldOrg
     * @param newOrg
     */
    public boolean dealOrgUpgradeEvent(MultiOrgVersion oldOrgVersion, MultiOrgVersion newOrgVersion);

    /**
     * 获取指定角色的用户列表
     *
     * @param roleUuid
     * @return
     */
    public List<MultiOrgUserRole> queryUserListByRole(String roleUuid);

    /**
     * 处理角色删除事件
     *
     * @param roleUuid
     */
    public boolean dealRoleRemoveEvent(String roleUuid);

    /**
     * 获取用户对应的角色的权限列表，以角色树形态展示
     *
     * @param uuid
     * @return
     */
    TreeNode getUserPrivilegeResultTree(String uuid);

    /**
     * 获取指定用户的角色列表
     *
     * @param userId
     * @return
     */
    List<MultiOrgUserRole> queryRoleListOfUser(String userId);

    /**
     * 通过用户ID，获取账号
     *
     * @param userId
     */
    public MultiOrgUserAccount getAccountByUserId(String userId);

    /**
     * 通过UUID，获取账号
     *
     * @param userUuid
     */
    public MultiOrgUserAccount getAccountByUuid(String userUuid);

    /**
     * 通过登录名获取账号信息
     *
     * @param loginName
     * @return
     */
    public MultiOrgUserAccount getUserAccountByLoginName(String loginName);

    /**
     * 获取一家单位的所有的账号
     *
     * @param systemUnitId
     * @return
     */
    public List<MultiOrgUserAccount> queryAllAccountOfUnit(String systemUnitId);

    /**
     * 获取某个组织版本的所有用户
     *
     * @param orgVersionId
     * @return
     */
    public List<OrgUserTreeNodeDto> queryUserByOrgVersion(String orgVersionId);

    /**
     * 获取用户指定版本的的职位列表
     *
     * @param userId
     * @param orgVersionId
     * @return
     */
    public List<OrgUserJobDto> queryUserJobByOrgVersionId(String userId, String orgVersionId);

    /**
     * 如何描述该方法
     *
     * @param oldEleId
     * @param newEleId
     * @param orgVersionId
     */
    public void dealElementIdChangeEvent(String oldEleId, String newEleId, String orgVersionId);

    /**
     * 更新账号的最后登录时间
     *
     * @param userUuid
     */
    public void updateLastLoginTime(String userUuid);

    /**
     * 获取指定单位的所有管理员
     *
     * @param unitId
     * @return
     */
    public List<MultiOrgUserAccount> queryAllAdminIdsBySystemUnitId(String unitId);

    /**
     * 模糊搜索用户
     *
     * @param name
     * @return
     */
    public List<String> queryUserIdsLikeName(String name);

    /**
     * 如何描述该方法
     *
     * @param userIds
     * @return
     */
    public List<OrgUserDto> queryUserDtoListByIds(List<String> userIds);

    /**
     * 获取一个单位管理员
     *
     * @param unitId
     * @return
     */
    public OrgUserVo getUnitAdmin(String unitId);

    /**
     * 重新计算指定单位的所有用户的工作职位信息
     *
     * @param unitId
     */
    public void recomputeUserWorkInfoByUnit(String unitId);

    public void recomputeUserWorkInfoByEleId(String orgVersionId, String eleId);

    /**
     * 获取用户所有的权限，包括组织节点，表单控件等其他地方继承过来的角色权限
     *
     * @param uuid
     * @return
     */
    public TreeNode getUserAllPrivilegeResultTree(String uuid);

    /**
     * 如何描述该方法
     *
     * @param a
     */
    void deleteUser(MultiOrgUserAccount a);

    /**
     * 如何描述该方法
     *
     * @param id
     * @param roleUuid
     */
    public void addRoleListOfUser(String userId, String roleUuid);

    int countUserByJob(Map<String, Object> params);

    String getUserPhoto(String userId);

    UserNode queryUserNode(String userId);

    public Map<String, UserJob> gerUserJob(String userId);

    public List<MultiOrgElementRole> getElementRoles(String eleId);

    public MultiOrgUserWorkInfo getMultiOrgUserWorkInfo(String userId);

    public MultiOrgUserWorkInfo saveMultiOrgUserWorkInfo(MultiOrgUserWorkInfo multiOrgUserWorkInfo);

    OrgUserVo getSimpleUserInfoById(String userId);

    void recomputeUserWorkInfoByVersions(String fromVersionId, String toVersionId);

    HashMap<String, String> getUnforbiddenUserIdNames(List<String> userIds);

    /**
     * 重置密码错误次数
     *
     * @param userUuid
     * @return void
     **/
    public void resetPwdErrorNumber(String userUuid);

    List<MultiOrgUserAccount> getAccountByUsername(String username);

    List<MultiOrgUserAccount> getAccountsByUserIds(List<String> ids);

    Map<String, OrgUserVo> getUserAccoutVoWithMainJobByIds(List<String> userids);

    Map<String, OrgUserVo> getUserAccoutVoWithAllJobByIds(List<String> userids);

    void updateIdNumber(String idNumber, String userId);

    MultiOrgUserInfo getUserByIdNumber(String idNumber);

    /**
     * 校验用户重复登录名
     *
     * @param vo
     * @return
     * @author baozh
     * @date 2021/11/24 14:11
     */
    List<String> checkOnly(OrgUserVo vo);

    OrgUserVo getLoginUserInfo(String loginName, String loginNameHashAlgorithmCode);

    List<MultiOrgGroupMember> queryGroupListByMemberId(String memberId);

    Set<String> queryGroupIdsByMemeberId(Set<String> memberIds);

    List<MultiOrgUserAccount> getUserAccountByLoginNameIgnoreCase(String loginName);

    /**
     * 登录用户业务事务处理
     * 1、更新用户的最后登录时间
     * 2、	重置密码错误次数
     *
     * @param userUuid
     * @return void
     **/
    public void loginUserBusinessTransaction(String userUuid);

    /**
     * 根据手机号码，列出对应的用户信息
     *
     * @param mobilePhone
     * @return
     */
    List<MultiOrgUserInfo> listUserByMobilePhone(String mobilePhone);

    /**
     * 根据手机号码，获取对应的用户数量
     *
     * @param mobilePhone
     * @return
     */
    long countUserByMobilePhone(String mobilePhone);
}
