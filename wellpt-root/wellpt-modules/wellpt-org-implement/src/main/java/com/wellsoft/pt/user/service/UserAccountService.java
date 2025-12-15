package com.wellsoft.pt.user.service;

import com.wellsoft.pt.jpa.service.JpaService;
import com.wellsoft.pt.org.dto.UserAcctPasswordRules;
import com.wellsoft.pt.org.entity.UserLoginLogEntity;
import com.wellsoft.pt.user.dao.UserAccountDaoImpl;
import com.wellsoft.pt.user.entity.UserAccountEntity;
import com.wellsoft.pt.user.entity.UserAcctPasswordRuleEntity;
import com.wellsoft.pt.user.enums.UserTypeEnum;

import java.util.List;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2020年08月10日   chenq	 Create
 * </pre>
 */
public interface UserAccountService extends JpaService<UserAccountEntity, UserAccountDaoImpl, String> {


    void modifyUserPassword(String loginName, String newPassword, String oldPassword);

    UserAccountEntity getByLoginName(String loginName);

    boolean existAccountByLoginName(String loginName);

    UserAccountEntity addAccount(String loginName, String password);

    UserAccountEntity addAccount(String loginName, String password, UserTypeEnum type);

    UserAccountEntity getByExtLoginInfo(String extLoginName, String extLoginType);

    void expiredUser(String loginName);

    void lockAccountByUserUuid(String userUuid, boolean locked);

    boolean existAccountByLoginName(String loginName, UserAccountEntity.Type type);

    void enableUserByUserUuid(String userUuid, boolean enable);

    void saveUserLoginLog(UserLoginLogEntity log);

    void saveUserAcctPasswordRule(List<UserAcctPasswordRuleEntity> rules);

    UserAcctPasswordRules getUserAcctPasswordRules();


    int updateAcctPasswordErrorNum(String loginName);

    void unlockUserAccountHaveReachedUnlockTime();

    void notifyUserAccountPasswordAreAboutToExpired();
}
