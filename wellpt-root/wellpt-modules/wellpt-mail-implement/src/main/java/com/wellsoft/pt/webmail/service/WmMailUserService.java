/*
 * @(#)2016-06-03 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.webmail.service;

import com.wellsoft.pt.jpa.service.JpaService;
import com.wellsoft.pt.multi.org.entity.MultiOrgUserAccount;
import com.wellsoft.pt.webmail.bean.WmMailUserDto;
import com.wellsoft.pt.webmail.dao.WmMailUserDao;
import com.wellsoft.pt.webmail.entity.WmMailConfigEntity;
import com.wellsoft.pt.webmail.entity.WmMailUserEntity;

import java.util.List;
import java.util.Set;

/**
 * Description: 邮件用户服务
 *
 * @author t
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016-06-03.1	t		2016-06-03		Create
 * </pre>
 * @date 2016-06-03
 */
public interface WmMailUserService extends JpaService<WmMailUserEntity, WmMailUserDao, String> {


    List<WmMailUserEntity> querByUserIds(Set<String> userIdset);

    /**
     * 根据实例查询列表
     *
     * @param example
     * @return
     */
    List<WmMailUserEntity> findByExample(WmMailUserEntity example);

    /**
     * 如何描述该方法
     *
     * @param orgId
     * @return
     */
    List<WmMailUserEntity> getMailAddressByOrgIds(List<String> orgIds);


    /**
     * 获取邮件账号
     *
     * @param mailAddress
     * @return
     */
    WmMailUserEntity getByMailAddress(String mailAddress);


    /**
     * 根据用户ID添加用户邮箱账号，返回邮箱地址
     *
     * @param userId
     * @param password
     * @return
     */
    String addMailUser(String userId, String password);

    /**
     * 根据用户ID更改用户邮箱密码
     *
     * @param userId
     * @param password
     */
    void alterMailUserPassword(String userId, String password);

    /**
     * 根据用户ID删除邮箱账号
     *
     * @param userId
     */
    void deleteMailUser(String userId);

    void deleteMailUserByUuids(List<String> uuid);

    /**
     * 保存更新用户的邮箱账号
     *
     * @param user
     * @param password
     * @return
     */
    WmMailUserEntity saveOrUpdateMailUser(WmMailConfigEntity wmMailConfig, MultiOrgUserAccount user,
                                          String password);


    WmMailUserEntity saveMailUser(WmMailUserDto userDto);

    /**
     * 获取外部邮件账号
     *
     * @param mailAddress
     * @param currentUserId
     * @return
     */
    WmMailUserEntity getOuterMailUser(String mailAddress, String currentUserId);

    /**
     * 获取内部邮件账号
     *
     * @param userId
     * @return
     */
    WmMailUserEntity getInnerMailUser(String userId);

    /**
     * 获取指定用户ID的所有外部邮箱账号
     *
     * @param currentUserId
     * @return
     */
    List<WmMailUserEntity> listOuterMailUser(String currentUserId);

    /**
     * 是否内部邮件用户
     *
     * @param mailAddress
     * @return
     */
    boolean isInnerMailUserAddress(String mailAddress);

    /**
     * 更新用户使用容量空间
     *
     * @param userId
     * @param increasement
     * @return
     */
    int updateMailUserAccountUseCapacity(String userId, Long increasement, boolean limit);

    List<WmMailUserEntity> getMailUser(String userId, String fromMailAddress);

    List<WmMailUserEntity> listInnerMailUsers();

    void updateMailUserMid(String userId, String mid);

    int updateMailUserPassword(String userId, String password);
}
