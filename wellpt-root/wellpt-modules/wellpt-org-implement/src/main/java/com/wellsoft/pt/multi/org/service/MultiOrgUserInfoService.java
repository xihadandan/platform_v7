/*
 * @(#)2018年4月4日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.multi.org.service;

import com.wellsoft.pt.jpa.service.JpaService;
import com.wellsoft.pt.multi.org.dao.MultiOrgUserInfoDao;
import com.wellsoft.pt.multi.org.entity.MultiOrgUserInfo;

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
public interface MultiOrgUserInfoService extends JpaService<MultiOrgUserInfo, MultiOrgUserInfoDao, String> {
    MultiOrgUserInfo getByUserId(String id);

    /**
     * 如何描述该方法
     *
     * @param id
     */
    void deleteUser(String id);

    public MultiOrgUserInfo getByIdNumber(String idNumber);

    public abstract MultiOrgUserInfo getByCertificateSubject(String certificateSubject);

    String getUserPhoto(String userId);

    List<MultiOrgUserInfo> listUserByMobilePhone(String mobilePhone);

    long countUserByMobilePhone(String mobilePhone);
}
