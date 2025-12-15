package com.wellsoft.pt.user.service.impl;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.wellsoft.context.enums.ModuleID;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.context.util.sm.SM3Util;
import com.wellsoft.pt.cache.CacheManager;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.message.facade.service.MessageClientApiFacade;
import com.wellsoft.pt.message.support.MessageParams;
import com.wellsoft.pt.multi.org.util.PwdUtils;
import com.wellsoft.pt.org.dto.UserAcctPasswordRules;
import com.wellsoft.pt.org.entity.UserLoginLogEntity;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import com.wellsoft.pt.user.dao.UserAccountDaoImpl;
import com.wellsoft.pt.user.dao.UserAcctPasswordRuleDaoImpl;
import com.wellsoft.pt.user.dao.UserLoginLogDaoImpl;
import com.wellsoft.pt.user.entity.UserAccountEntity;
import com.wellsoft.pt.user.entity.UserAcctPasswordRuleEntity;
import com.wellsoft.pt.user.entity.UserInfoEntity;
import com.wellsoft.pt.user.enums.UserTypeEnum;
import com.wellsoft.pt.user.service.UserAccountService;
import com.wellsoft.pt.user.service.UserInfoService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

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
@Service
public class UserAccountServiceImpl extends AbstractJpaServiceImpl<UserAccountEntity, UserAccountDaoImpl, String> implements UserAccountService {

    @Resource
    UserInfoService userInfoService;

    @Resource
    UserLoginLogDaoImpl userLoginLogDao;

    @Resource
    UserAcctPasswordRuleDaoImpl userAcctPasswordRuleDao;

    @Resource
    CacheManager cacheManager;

    @Autowired
    private MessageClientApiFacade messageClientApiFacade;

    @Override
    @Transactional
    public void modifyUserPassword(String loginName, String newPassword, String oldPassword) {
        UserAccountEntity accountEntity = this.getByLoginName(loginName);
        if (accountEntity != null && ((oldPassword != null && accountEntity.getPassword().equals(
                SM3Util.encrypt(oldPassword + "{" + accountEntity.getLoginName().toLowerCase() + "}"))) || oldPassword == null)) {
            accountEntity.setPassword(PwdUtils.createSm3Password(loginName, newPassword));
            accountEntity.setPasswordModifiedByUser(SpringSecurityUtils.getCurrentLoginName().equals(accountEntity.getLoginName()));
            accountEntity.setModifyPasswordTime(new Date());
            UserAcctPasswordRules rules = getUserAcctPasswordRules();
            if (rules.enabled(UserAcctPasswordRules.RuleKey.enablePasswordTimeLimit.name())) {
                accountEntity.setPasswordExpiredTime(DateUtils.addDays(accountEntity.getModifyPasswordTime(), rules.getInt(UserAcctPasswordRules.RuleKey.passwordLimitValidDay.name())));
            }
            this.dao.save(accountEntity);
        }
    }

    @Override
    public UserAccountEntity getByLoginName(String loginName) {
        return this.dao.getOneByFieldEq("loginName", loginName);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.user.service.UserAccountService#existAccountByLoginName(java.lang.String)
     */
    @Override
    public boolean existAccountByLoginName(String loginName) {
        UserAccountEntity entity = new UserAccountEntity();
        entity.setLoginName(loginName);
        return this.dao.countByEntity(entity) > 0;
    }

    @Override
    @Transactional
    public UserAccountEntity addAccount(String loginName, String password) {
        return this.addAccount(loginName, password, UserTypeEnum.INDIVIDUAL);
    }

    @Override
    @Transactional
    public UserAccountEntity addAccount(String loginName, String password, UserTypeEnum type) {
        UserAccountEntity accountEntity = new UserAccountEntity();
        accountEntity.setLoginName(loginName);
        accountEntity.setPassword(password);
        this.dao.save(accountEntity);
        return accountEntity;
    }

    @Override
    public UserAccountEntity getByExtLoginInfo(String extLoginName, String extLoginType) {
        UserAccountEntity accountEntity = new UserAccountEntity();
        accountEntity.setExtLoginName(extLoginName);
        accountEntity.setExtLoginType(extLoginType);
        List<UserAccountEntity> userAccountEntities = this.listByEntity(accountEntity);
        return CollectionUtils.isNotEmpty(userAccountEntities) ? userAccountEntities.get(0) : null;
    }

    @Override
    @Transactional
    public void expiredUser(String loginName) {
        UserAccountEntity userAccountEntity = getByLoginName(loginName);
        if (userAccountEntity != null) {
            userAccountEntity.setIsAccountNonExpired(false);
            this.dao.save(userAccountEntity);
        }
    }

    @Override
    @Transactional
    public void lockAccountByUserUuid(String userUuid, boolean locked) {
        UserInfoEntity userInfoEntity = userInfoService.getOne(userUuid);
        if (userInfoEntity != null) {
            UserAccountEntity accountEntity = getByLoginName(userInfoEntity.getLoginName());
            if (accountEntity != null) {
                accountEntity.setIsAccountNonLocked(!locked);
                if (locked) {
                    accountEntity.setLockTime(new Date());
                    accountEntity.setLockCause(UserAccountEntity.LockCause.MANAGER_LOCK);
                } else {
                    accountEntity.setLockTime(null);
                }
                save(accountEntity);
            }
        }
    }

    @Override
    public boolean existAccountByLoginName(String loginName, UserAccountEntity.Type type) {
        UserAccountEntity entity = new UserAccountEntity();
        entity.setLoginName(loginName);
        entity.setType(type);
        return this.dao.countByEntity(entity) > 0;
    }

    @Override
    @Transactional
    public void enableUserByUserUuid(String userUuid, boolean enable) {
        UserInfoEntity userInfoEntity = userInfoService.getOne(userUuid);
        if (userInfoEntity != null) {
            UserAccountEntity accountEntity = getByLoginName(userInfoEntity.getLoginName());
            if (accountEntity != null) {
                accountEntity.setIsEnabled(enable);
                save(accountEntity);
            }
        }
    }

    @Override
    @Transactional
    public void saveUserLoginLog(UserLoginLogEntity log) {
        userLoginLogDao.save(log);
    }

    @Override
    @Transactional
    public void saveUserAcctPasswordRule(List<UserAcctPasswordRuleEntity> rules) {
        String cacheKey = "defaultAcctPasswordRule";
        int deleteRow = userAcctPasswordRuleDao.deleteByHQL("delete from UserAcctPasswordRuleEntity", null);
        userAcctPasswordRuleDao.saveAll(rules);
        if (deleteRow > 0) {
            boolean closeUserPwdInputLock = false;
            boolean releaseAcctLocked = false;
            for (UserAcctPasswordRuleEntity r : rules) {
                if (r.getAttrKey().equalsIgnoreCase(UserAcctPasswordRules.RuleKey.closeLockRuleToReleaseAcctLocked.name()) && BooleanUtils.toBoolean(r.getAttrVal())) {
                    releaseAcctLocked = true;
                }
                if (r.getAttrKey().equalsIgnoreCase(UserAcctPasswordRules.RuleKey.lockIfInputError.name()) && !BooleanUtils.toBoolean(r.getAttrVal())) {
                    closeUserPwdInputLock = true;
                }
            }
            if (closeUserPwdInputLock && releaseAcctLocked) {
                Map<String, Object> params = Maps.newHashMap();
                params.put("lockCause", UserAccountEntity.LockCause.PASSWORD_ERROR);
                this.dao.updateByHQL("update UserAccountEntity set passwordErrorNum = 0 ,lockTime = null" +
                        " , isAccountNonLocked=true , lockCause = null ,unlockTime = null" +
                        " where isAccountNonLocked = false and  lockCause=:lockCause ", params);

            }
        }
        Cache cache = cacheManager.getCache(ModuleID.SECURITY);
        cache.evict(cacheKey);
    }

    @Override
    public UserAcctPasswordRules getUserAcctPasswordRules() {
        Cache cache = cacheManager.getCache(ModuleID.SECURITY);
        String cacheKey = "defaultAcctPasswordRule";
        Cache.ValueWrapper valueWrapper = cache.get(cacheKey);
        if (valueWrapper != null) {
            return (UserAcctPasswordRules) valueWrapper.get();
        } else {
            List<UserAcctPasswordRuleEntity> list = userAcctPasswordRuleDao.listAllByOrderPage(null, null);
            UserAcctPasswordRules rules = UserAcctPasswordRules.init(list);
            cache.put(cacheKey, rules);
            return rules;
        }
    }

    @Override
    @Transactional
    public int updateAcctPasswordErrorNum(String loginName) {
        UserAccountEntity accountEntity = getByLoginName(loginName);
        if (accountEntity != null) {
            accountEntity.setPasswordErrorNum(accountEntity.getPasswordErrorNum() + 1);
            UserAcctPasswordRules rules = getUserAcctPasswordRules();
            if (rules.enabled(UserAcctPasswordRules.RuleKey.lockIfInputError) && accountEntity.getPasswordErrorNum() >= rules.getInt(UserAcctPasswordRules.RuleKey.lockIfInputErrorNum)) {
                accountEntity.setLockTime(new Date());
                accountEntity.setIsAccountNonLocked(false);
                accountEntity.setLockCause(UserAccountEntity.LockCause.PASSWORD_ERROR);
                accountEntity.setUnlockTime(DateUtils.addMinutes(accountEntity.getLockTime(), rules.getInt(UserAcctPasswordRules.RuleKey.lockMinuteIfInputError)));
            }
            save(accountEntity);
            return accountEntity.getPasswordErrorNum();
        }
        return 0;
    }

    @Override
    @Transactional
    public void unlockUserAccountHaveReachedUnlockTime() {
        Map<String, Object> params = Maps.newHashMap();
        params.put("now", new Date());
        params.put("nonLocked", true);
        params.put("lockCause", UserAccountEntity.LockCause.PASSWORD_ERROR);
        this.dao.updateByHQL("update UserAccountEntity set passwordErrorNum = 0 ,lockTime = null" +
                " , isAccountNonLocked=:nonLocked , lockCause = null ,unlockTime = null" +
                " where unlockTime <= :now and lockCause=:lockCause ", params);
    }

    @Override
    @Transactional
    public void notifyUserAccountPasswordAreAboutToExpired() {
        UserAcctPasswordRules rules = getUserAcctPasswordRules();
        if (rules.enabled(UserAcctPasswordRules.RuleKey.enablePasswordTimeLimit)) {
            int beforeDay = rules.getInt(UserAcctPasswordRules.RuleKey.notifyDayBeforeExpired);
            Date from = DateUtils.addDays(DateUtils.truncate(new Date(), Calendar.DATE), beforeDay);
            Date to = DateUtils.addDays(from, 1);
            Map<String, Object> params = Maps.newHashMap();
            params.put("from", from);
            params.put("to", to);
            StringBuilder sql = new StringBuilder("select u.user_id ,u.user_name from user_account a ,user_info u" +
                    " where a.password_expired_time between :from and :to and a.uuid= u.account_uuid");
            List<QueryItem> userItems = dao.listQueryItemBySQL(sql.toString(), params, null);
            for (QueryItem item : userItems) {
                MessageParams messageParams = new MessageParams();
                messageParams.setTemplateId("MSG_PWD_DUE_MESSAGE");
                messageParams.setRecipientIds(Sets.newHashSet(item.getString("userId")));
                messageParams.setRecipientNames(Sets.newHashSet(item.getString("userName")));
                Map<String, Object> extraDataMap = Maps.newHashMap();
                extraDataMap.put("advancePwdDay", beforeDay);
                extraDataMap.put("提醒的提前天数", beforeDay);
                messageParams.setExtraData(extraDataMap);
                messageClientApiFacade.sendByParams(messageParams);
            }
        }
    }

}
