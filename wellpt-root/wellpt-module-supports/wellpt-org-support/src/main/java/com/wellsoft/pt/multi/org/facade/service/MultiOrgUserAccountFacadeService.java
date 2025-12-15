/*
 * @(#)2017年11月22日 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.multi.org.facade.service;

import com.wellsoft.context.jdbc.support.QueryInfo;
import com.wellsoft.context.service.BaseService;
import com.wellsoft.pt.multi.org.bean.OrgUserDto;
import com.wellsoft.pt.multi.org.bean.OrgUserTableVo;
import com.wellsoft.pt.multi.org.bean.OrgUserVo;
import com.wellsoft.pt.multi.org.dto.EncryptChangePasswordByIdDto;
import com.wellsoft.pt.multi.org.dto.NewAccountDto;
import com.wellsoft.pt.multi.org.entity.MultiOrgUserAccount;
import com.wellsoft.pt.multi.org.entity.MultiUserRelationAccountEntity;
import com.wellsoft.pt.multi.org.vo.MultiUserRelationAccountVo;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
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
public interface MultiOrgUserAccountFacadeService extends BaseService {

    // 全路径分割符
    public static String PATH_SPLIT_SYSMBOL = "/";

    public MultiOrgUserAccount getMultiOrgUserAccountByIdNumber(String idNumber);

    /**
     * 添加账号
     * 1，登录名要对大小写不敏感
     * 2,不输入密码的话， 密码默认为0
     *
     * @param vo          用户对象 包含的密码不加密
     * @param isRandomPwd 是否随机密码
     * @return
     */
    public MultiOrgUserAccount addUserAccount(OrgUserVo vo, Boolean isRandomPwd) throws UnsupportedEncodingException;

    /**
     * 修改账号信息
     *
     * @param vo
     * @return
     */
    public MultiOrgUserAccount modifyUserAccount(OrgUserVo vo);

    /**
     * 根据用户ID，更改用户密码
     *
     * @param userId
     * @param oldPassword
     * @param newPassword
     */
    boolean changePasswordById(String userId, String oldPassword, String newPassword);

    /**
     * 密码加密，根据用户ID，更改用户密码
     *
     * @param userId
     * @param oldPassword 加密的旧密码
     * @param newPassword 加密的新密码
     */
    EncryptChangePasswordByIdDto encryptChangePasswordById(String userId, String oldPassword, String newPassword);

    /**
     * 获取用户账号和INFO信息
     *
     * @param uuid
     * @return
     */
    public MultiOrgUserAccount getAccount(String uuid);

    /**
     * 禁用账号
     *
     * @param uuid
     * @return
     */
    public boolean forbidAccount(String uuid);

    /**
     * 启用账号
     *
     * @param uuid
     * @return
     */
    public boolean unforbidAccount(String uuid);

    /**
     * 锁住账号
     *
     * @param uuid
     * @return
     */
    public boolean lockAccount(String uuid);

    /**
     * 解锁账号
     *
     * @param uuid
     * @return
     */
    public boolean unlockAccount(String uuid);

    /**
     * 密码错误锁定-解锁账号
     *
     * @param uuid
     * @return
     */
    public Boolean pwdUnlockAccount(String uuid);

    /**
     * 通过登录名获取账号信息
     *
     * @param loginName
     * @param loginNameHashAlgorithmCode
     * @return 找不到数据返回null
     */
    public MultiOrgUserAccount getUserByLoginNameIgnoreCase(String loginName, String loginNameHashAlgorithmCode);

    /**
     * 通过ID获取账号信息
     *
     * @param userId
     * @return
     */
    public MultiOrgUserAccount getAccountByUserId(String userId);

    /**
     * 获取所有的账号
     *
     * @return
     */
    public List<MultiOrgUserAccount> queryAllAccountOfUnit(String systemUnitId);

    /**
     * 如何描述该方法
     *
     * @param userUuid
     */
    public void updateLastLoginTime(String userUuid);

    /**
     * 如何描述该方法
     *
     * @param unitId
     * @return
     */
    public List<MultiOrgUserAccount> queryAllAdminIdsBySystemUnitId(String unitId);

    /**
     * 如何描述该方法
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
     * 供给jqgrid分页使用
     *
     * @param queryInfo
     * @return
     */
    public List<OrgUserTableVo> query(QueryInfo queryInfo);

    /**
     * 如何描述该方法
     *
     * @param a
     */
    public void deleteAccount(MultiOrgUserAccount a);

    /**
     * 通过用户ID重置指定用户名密码
     *
     * @param userIds
     * @param password
     * @return
     */
    public List<String> resetAllUserPasswordByUserIds(Collection<String> userIds, String password);

    /**
     * 重置所有用户密码-重置为默认密码
     *
     * @param
     * @return boolean
     **/
    public boolean resetAllUserPwd();

    /**
     * 重置所有用户密码-重置为随机密码
     *
     * @param
     * @return 返回exel fileuuid
     **/
    public String resetAllUserRandomPwd(HttpServletRequest request, HttpServletResponse response);

    /**
     * 重置所有用户密码-重置为自定义密码
     *
     * @param userDefinedPwd 用户自定义密码
     * @return boolean
     **/
    public boolean resetAllUserDefinedPwd(String userDefinedPwd) throws UnsupportedEncodingException;

    /**
     * // 重置指定用户的密码-重置为默认密码
     *
     * @param userUuids
     * @return boolean
     **/
    public boolean resetUserPwd(List<String> userUuids);

    /**
     * // 重置指定用户的密码-重置为随机密码
     *
     * @param userUuids
     * @return boolean
     **/
    public String resetUserRandomPwd(List<String> userUuids);

    /**
     * // 重置指定用户的密码-重置为自定义密码
     *
     * @param userUuids
     * @param userDefinedPwd 用户自定义密码
     * @return boolean
     **/
    public boolean resetUserDefinedPwd(List<String> userUuids, String userDefinedPwd)
            throws UnsupportedEncodingException;

    /**
     * 上传exel
     *
     * @param excel
     * @return java.lang.String 返回MongoFileEntity ID
     **/
    public String uploadExcel(HSSFWorkbook excel) throws IOException;

    MultiOrgUserAccount getAccountByExtAccount(String externalLoginId, String externalActType);

    void bindAccount2Ext(String accountUuid, String username, String remoteType);

    public abstract MultiOrgUserAccount getMultiOrgUserAccountCertificateSubject(String certificateSubject);

    public abstract String getUniqueLoginNameByUserName(String userName);

    public abstract boolean unforbidAccount(Collection<String> uuids);

    public abstract List<MultiOrgUserAccount> queryUsersByUserName(String systemUnitId, String userName);

    /**
     * 根据系统单位Id
     * 统计
     * 开始时间-结束时间内
     * 创建的用户数
     *
     * @param systemUnitId
     * @param startTime
     * @param endTime
     * @return
     */
    public long countBySystemUnitId(String systemUnitId, Date startTime, Date endTime);

    HashMap<String, String> getUnforbiddenUserIdNames(List<String> userIds);

    /**
     * 保存账号表
     *
     * @param user
     * @return void
     **/
    public void save(MultiOrgUserAccount user);

    /**
     * 获取要导出的excel所有数据集合(包含密码)
     *
     * @param newAccountMap
     * @return java.util.List<java.lang.String [ ]>
     **/
    public List<String[]> getNewUserAccountDataList(Map<String, NewAccountDto> newAccountMap);

    List<MultiOrgUserAccount> getAccountByUsername(String username);

    List<MultiOrgUserAccount> getAccountsByUserIds(List<String> ids);

    int countUnforbiddenAccount(String systemUnitId);

    /**
     * 支持中文用户登录
     *
     * @param userName
     * @return
     * @author baozh
     * @date 2021/11/23 16:50
     */
    List<MultiOrgUserAccount> getUserAccountByLoginNameIgnoreCase(String userName);

    /**
     * 保存关联账户
     *
     * @param relationAccountVo
     * @return
     * @author baozh
     * @date 2022/1/12 15:32
     */
    String saveRelationAccount(MultiUserRelationAccountVo relationAccountVo, HttpServletRequest request);

    /**
     * 获取关联账户
     *
     * @param
     * @return
     * @author baozh
     * @date 2022/1/12 16:18
     */
    List<MultiUserRelationAccountEntity> getRelationAccounts();

    /**
     * 解绑
     *
     * @param
     * @return
     * @author baozh
     * @date 2022/1/13 9:58
     */
    boolean unboundAccount(String relationAccount);

    /**
     * 设置主账户
     *
     * @param
     * @return
     * @author baozh
     * @date 2022/1/13 9:58
     */
    boolean setMasterAccount(String masterAccount);

    /**
     * 校验是否关联账户
     *
     * @param account
     * @return
     * @author baozh
     * @date 2022/1/13 11:23
     */
    boolean checkRelationAccount(String account);

    List<MultiUserRelationAccountEntity> getRelationAccountsWithLoginAddr();

    void addRelationAccountCache(String userId);
}
