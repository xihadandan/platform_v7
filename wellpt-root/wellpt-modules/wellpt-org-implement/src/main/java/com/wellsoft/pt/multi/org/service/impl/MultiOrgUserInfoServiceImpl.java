/*
 * @(#)2018年4月4日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.multi.org.service.impl;

import com.google.common.collect.Maps;
import com.wellsoft.context.util.PinyinUtil;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.multi.org.dao.MultiOrgUserInfoDao;
import com.wellsoft.pt.multi.org.entity.MultiOrgUserAccount;
import com.wellsoft.pt.multi.org.entity.MultiOrgUserInfo;
import com.wellsoft.pt.multi.org.service.MultiOrgUserAccountService;
import com.wellsoft.pt.multi.org.service.MultiOrgUserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;

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
@Service
public class MultiOrgUserInfoServiceImpl extends AbstractJpaServiceImpl<MultiOrgUserInfo, MultiOrgUserInfoDao, String>
        implements MultiOrgUserInfoService {

    @Autowired
    private MultiOrgUserAccountService multiOrgUserAccountService;

    /**
     * 初始化用户简拼
     */
//    @PostConstruct
//    public void initUserNameJp() {
//        List<MultiOrgUserAccount> userAccountList = multiOrgUserAccountService.listByHQL("from MultiOrgUserAccount where userNameJp is null", null);
//        for (MultiOrgUserAccount userAccount : userAccountList) {
//            userAccount.setUserNameJp(PinyinUtil.getPinYinHeadChar(userAccount.getUserName()));
//            multiOrgUserAccountService.update(userAccount);
//        }
//    }

    @Override
    public MultiOrgUserInfo getByUserId(String id) {
        MultiOrgUserInfo q = new MultiOrgUserInfo();
        q.setUserId(id);
        List<MultiOrgUserInfo> objs = this.dao.listByEntity(q);
        if (CollectionUtils.isEmpty(objs)) {
            return null;
        }
        return objs.get(0);
    }

    @Override
    public MultiOrgUserInfo getByIdNumber(String idNumber) {
        MultiOrgUserInfo q = new MultiOrgUserInfo();
        q.setIdNumber(idNumber);
        List<MultiOrgUserInfo> objs = this.dao.listByEntity(q);
        if (CollectionUtils.isEmpty(objs)) {
            return null;
        }
        return objs.get(0);
    }

    @Override
    public MultiOrgUserInfo getByCertificateSubject(String certificateSubject) {
        MultiOrgUserInfo q = new MultiOrgUserInfo();
        q.setCertificateSubject(certificateSubject);
        List<MultiOrgUserInfo> objs = this.dao.listByEntity(q);
        if (CollectionUtils.isEmpty(objs)) {
            return null;
        }
        return objs.get(0);
    }

    @Override
    public String getUserPhoto(String userId) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("id", userId);
        return this.dao.getCharSequenceByHQL("select i.photoUuid from MultiOrgUserInfo i where i.userId=:id", params);
    }

    @Override
    public List<MultiOrgUserInfo> listUserByMobilePhone(String mobilePhone) {
        Assert.hasText(mobilePhone, "手机号码不能为空！");

        MultiOrgUserInfo entity = new MultiOrgUserInfo();
        entity.setMobilePhone(mobilePhone);
        return this.dao.listByEntity(entity);
    }

    @Override
    public long countUserByMobilePhone(String mobilePhone) {
        Assert.hasText(mobilePhone, "手机号码不能为空！");

        MultiOrgUserInfo entity = new MultiOrgUserInfo();
        entity.setMobilePhone(mobilePhone);
        return this.dao.countByEntity(entity);
    }

    @Override
    public void deleteUser(String id) {
        MultiOrgUserInfo obj = this.getByUserId(id);
        if (obj != null) {
            this.delete(obj);
        }
    }
}
