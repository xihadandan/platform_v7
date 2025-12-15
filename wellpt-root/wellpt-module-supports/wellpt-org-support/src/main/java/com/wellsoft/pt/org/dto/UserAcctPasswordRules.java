package com.wellsoft.pt.org.dto;

import com.google.common.collect.Maps;
import com.wellsoft.pt.user.entity.UserAcctPasswordRuleEntity;
import org.apache.commons.collections.CollectionUtils;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2025年04月15日   chenq	 Create
 * </pre>
 */
public class UserAcctPasswordRules implements Serializable {


    public static enum RuleKey {
        charTypeNumRequire, mustContainsUpperLowerCaseChar, minLength, maxLength, enablePasswordTimeLimit, passwordLimitValidDay, notifyDayBeforeExpired,
        lockIfInputError, lockIfInputErrorNum, lockMinuteIfInputError, closeLockRuleToReleaseAcctLocked, loginCheckPasswordPattern,
        forceModifySysInitPassword, sysInitPasswordSource;
    }

    private Map<String, Object> ruleMap = Maps.newHashMap();

    public static UserAcctPasswordRules init(List<UserAcctPasswordRuleEntity> list) {
        UserAcctPasswordRules rules = new UserAcctPasswordRules();
        if (CollectionUtils.isNotEmpty(list)) {
            for (UserAcctPasswordRuleEntity entity : list) {
                rules.ruleMap.put(entity.getAttrKey(), entity.getAttrVal());
            }
        }
        return rules;
    }

    public boolean enabled(String key) {
        if (this.ruleMap.containsKey(key)) {
            String v = this.ruleMap.get(key).toString();
            return v.equalsIgnoreCase("true") || v.equals("1");
        }
        return false;
    }

    public boolean enabled(RuleKey key) {
        if (this.ruleMap.containsKey(key.name())) {
            String v = this.ruleMap.get(key.name()).toString();
            return v.equalsIgnoreCase("true") || v.equals("1");
        }
        return false;
    }

    public Integer getInt(RuleKey key) {
        if (this.ruleMap.containsKey(key.name())) {
            return Integer.parseInt(this.ruleMap.get(key.name()).toString());
        }
        return null;
    }

    public Integer getInt(String key) {
        if (this.ruleMap.containsKey(key)) {
            return Integer.parseInt(this.ruleMap.get(key).toString());
        }
        return null;
    }


    public String getString(String key) {
        if (this.ruleMap.containsKey(key)) {
            return this.ruleMap.get(key).toString();
        }
        return null;
    }

    public String getString(RuleKey key) {
        if (this.ruleMap.containsKey(key.name())) {
            return this.ruleMap.get(key.name()).toString();
        }
        return null;
    }


    /**
     * 增强版密码验证方法
     *
     * @param password         要验证的密码
     * @param minRequirements  至少需要满足的条件数量（1=至少一种，2=至少两种，3=必须全部）
     * @param requireBothCases 是否强制要求同时包含大小写字母
     * @return 是否满足要求
     */
    public static boolean isValidPasswordFlexible(String password, int minRequirements, boolean requireBothCases) {
        if (password == null || password.isEmpty() || minRequirements < 1 || minRequirements > 3) {
            return false;
        }

        // 检查各种字符类型
        boolean hasLowerCase = password.matches(".*[a-z].*");
        boolean hasUpperCase = password.matches(".*[A-Z].*");
        boolean hasDigit = password.matches(".*\\d.*");
        boolean hasSpecial = password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*");

        // 处理字母要求
        boolean hasLetter = false;
        if (hasLowerCase || hasUpperCase) { // 存在字母的情况下
            if (requireBothCases) {
                hasLetter = hasLowerCase && hasUpperCase; // 必须同时有大小写
            } else {
                hasLetter = hasLowerCase || hasUpperCase; // 有任意一种即可
            }
        }

        // 计算满足的条件数量（字母算作一个条件）
        int metConditions = 0;
        if (hasLetter) metConditions++;
        if (hasDigit) metConditions++;
        if (hasSpecial) metConditions++;

        // 检查是否满足最小要求数量
        return metConditions >= minRequirements;
    }

    public Map<String, Object> getRuleMap() {
        return ruleMap;
    }
}
