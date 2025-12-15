/*
 * @(#)2013-12-19 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.org.service.impl;

import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.multi.org.entity.MultiOrgUserAccount;
import com.wellsoft.pt.multi.org.service.MultiOrgUserAccountService;
import com.wellsoft.pt.org.dao.UserPropertyDao;
import com.wellsoft.pt.org.entity.UserProperty;
import com.wellsoft.pt.org.service.UserPropertyService;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-12-19.1	zhulh		2013-12-19		Create
 * </pre>
 * @date 2013-12-19
 */
@Service
public class UserPropertyServiceImpl extends
        AbstractJpaServiceImpl<UserProperty, UserPropertyDao, String> implements
        UserPropertyService {
    @Autowired
    private UserPropertyDao userPropertyDao;

    @Autowired
    private MultiOrgUserAccountService multiOrgUserAccountService;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.org.service.UserPropertyService#findByExample(com.wellsoft.pt.org.entity.UserProperty)
     */
    @Override
    public List<UserProperty> findByExample(UserProperty example) {
        return this.dao.listByEntity(example);
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.org.service.UserPropertyService#saveUserProperty(com.wellsoft.pt.org.entity.UserProperty)
     */
    @Override
    @Transactional
    public void saveUserProperty(UserProperty userProperty) {
        String userId = SpringSecurityUtils.getCurrentUserId();
        MultiOrgUserAccount user = multiOrgUserAccountService.getAccountById(userId);
        userProperty.setUserUuid(user.getUuid());
        UserProperty example = new UserProperty();
        example.setName(userProperty.getName());
        example.setUserUuid(userProperty.getUserUuid());
        UserProperty entity = userProperty;
        List<UserProperty> userProperties = this.findByExample(example);
        if (userProperties != null && userProperties.size() > 0) {
            entity = userProperties.get(0);
            entity.setValue(userProperty.getValue());
            entity.setClobValue(userProperty.getClobValue());
        }
        userPropertyDao.save(entity);
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.org.service.UserPropertyService#getUserProperty(java.lang.String)
     */
    @Override
    public UserProperty getUserProperty(String propertyName) {
        String userId = SpringSecurityUtils.getCurrentUserId();
        MultiOrgUserAccount user = multiOrgUserAccountService.getAccountById(userId);
        UserProperty example = new UserProperty();
        example.setName(propertyName);
        if (user != null) {
            example.setUserUuid(user.getUuid());
            List<UserProperty> userProperties = this.findByExample(example);
            if (CollectionUtils.isNotEmpty(userProperties)) {
                return userProperties.get(0);
            }

        }
        return null;
    }


    @Override
    public String getUserPropertyValue(String prop, String userUuid) {
        return this.dao.getUserPropertyValue(prop, userUuid);
    }

    @Override
    public String getCurrentUserPropertyValue(String prop) {
        return getCurrentUserPropertyValue(SpringSecurityUtils.getCurrentUserId(), prop);
    }

    @Override
    public String getCurrentUserPropertyValue(String userId, String prop) {
        MultiOrgUserAccount user = multiOrgUserAccountService.getAccountById(userId);
        if (user == null) {
            return StringUtils.EMPTY;
        }
        return this.dao.getUserPropertyValue(prop, user.getUuid());
    }

    @Override
    @Transactional
    public void saveUserPropertyValue(String prop, String value) {
        UserProperty userProperty = getUserProperty(prop);
        if (userProperty != null) {
            userProperty.setValue(value);
        } else {
            userProperty = new UserProperty();
            userProperty.setUserUuid(multiOrgUserAccountService.getAccountById(
                    SpringSecurityUtils.getCurrentUserId()).getUuid());
            userProperty.setName(prop);
            userProperty.setValue(value);
        }
        this.dao.save(userProperty);
    }

    @Override
    @Transactional
    public void saveUserPropertyValue(String userId, String prop, String value) {
        UserProperty userProperty = getUserProperty(prop);
        if (userProperty != null) {
            userProperty.setValue(value);
        } else {
            MultiOrgUserAccount account = multiOrgUserAccountService.getAccountById(userId);
            if (account == null) {
                return;
            }
            userProperty = new UserProperty();
            userProperty.setUserUuid(account.getUuid());
            userProperty.setName(prop);
            userProperty.setValue(value);
        }
        this.dao.save(userProperty);
    }

    @Override
    @Transactional
    public void deleteByUser(String userUuid) {
        this.dao.deleteByUser(userUuid);
    }
}
