/*
 * @(#)2018年4月4日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.multi.org.service;

import com.wellsoft.context.jdbc.support.QueryInfo;
import com.wellsoft.pt.jpa.service.JpaService;
import com.wellsoft.pt.multi.org.bean.OrgUserDto;
import com.wellsoft.pt.multi.org.bean.OrgUserTableVo;
import com.wellsoft.pt.multi.org.dao.MultiOrgUserAccountDao;
import com.wellsoft.pt.multi.org.entity.MultiOrgUserAccount;
import com.wellsoft.pt.multi.org.query.MultiOrgUserAccountPwdCreateTimeQueryItem;
import com.wellsoft.pt.security.config.entity.MultiUserLoginSettingsEntity;

import java.util.Date;
import java.util.List;

/**
 * Description: 如何描述该类
 *
 * @author chenqiong
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2018年4月4日.1	chenqiong		2018年4月4日		Create
 * </pre>
 * @date 2018年4月4日
 */
public interface MultiOrgUserAccountService extends JpaService<MultiOrgUserAccount, MultiOrgUserAccountDao, String> {
    /**
     * 通过单位ID和名字获取账号信息
     *
     * @param loginName
     * @return
     */
    MultiOrgUserAccount getAccountByLoignName(String loginName);

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
     * @param name
     * @return
     */
    List<String> queryUserIdsLikeName(String name);

    /**
     * 如何描述该方法
     *
     * @param userIds
     * @return
     */
    List<OrgUserDto> queryUserDtoListByIds(List<String> userIds);

    List<MultiOrgUserAccount> queryAllAccountOfUnit(String systemUnitId);

    /**
     * 如何描述该方法
     *
     * @param userId
     * @return
     */
    MultiOrgUserAccount getAccountById(String userId);

    /**
     * 如何描述该方法
     *
     * @param q
     * @return
     */
    List<MultiOrgUserAccount> queryAllAdminIdsBySystemUnitId(String unitId);

    /**
     * 如何描述该方法
     *
     * @param q
     * @return
     */
    List<MultiOrgUserAccount> findByExample(MultiOrgUserAccount q);

    public abstract String getUniqueLoginNameByUserName(String userName);

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

    /**
     * ，初始化账号表的PWD_CREATE_TIME
     *
     * @param
     * @return void
     **/
    public void initPwdCreateTime();

    /**
     * 查找所有用户账号的密码创建时间（重置时间）
     **/
    public List<MultiOrgUserAccountPwdCreateTimeQueryItem> getAllAccountPwdCreateTime();

    /**
     * // 查找密码被锁定的账号 解锁时间小于等于当前时间时
     *
     * @param
     * @return java.util.List<com.wellsoft.pt.multi.org.entity.MultiOrgUserAccount>
     **/
    public List<MultiOrgUserAccount> getUnlockAccounts();

    /**
     * 查找密码被锁定的账号
     *
     * @param
     * @return java.util.List<com.wellsoft.pt.multi.org.entity.MultiOrgUserAccount>
     **/
    public List<MultiOrgUserAccount> getCoerceUnlockAccounts();

    /**
     * 通过登录名获取多组织账号集合
     *
     * @param loginNames
     * @return void
     **/
    public List<MultiOrgUserAccount> getUserAccountListByloginNames(List<String> loginNames);

    List<MultiOrgUserAccount> getAccountByUsername(String username);

    List<MultiOrgUserAccount> getAccountsByUserIds(List<String> ids);

    int countUnforbiddenAccount(String systemUnitId);

    long countBySystemUnitIds(List<String> systemUnitIds);

    List<MultiOrgUserAccount> getUserAccountByLoginNameIgnoreCase(String userName,
                                                                  MultiUserLoginSettingsEntity loginSetting);

    /**
     * 模糊匹配对应的数据 获取正常启用的用户
     *
     * @param userNameKey 用户名称 模糊匹配
     * @param userUnitId  指定单位
     * @return
     **/
    public List<MultiOrgUserAccount> getMultiOrgUserAccountListByUserNameKey(String userNameKey, String userUnitId);
}
