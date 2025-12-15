/*
 * @(#)2013-12-3 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.security.service.impl;

import com.wellsoft.context.config.Config;
import com.wellsoft.context.util.sm.SM3Util;
import com.wellsoft.pt.multi.org.entity.MultiOrgUserAccount;
import com.wellsoft.pt.multi.org.facade.service.MultiOrgUserAccountFacadeService;
import com.wellsoft.pt.org.service.UserService;
import com.wellsoft.pt.security.core.userdetails.UserDetailsServiceProvider;
import com.wellsoft.pt.security.service.CertificateLoginService;
import fjca.FJCAApps;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.encoding.ShaPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-12-3.1	zhulh		2013-12-3		Create
 * </pre>
 * @date 2013-12-3
 */
@Service
@Transactional(readOnly = true)
public class CertificateLoginServiceImpl implements CertificateLoginService {

    // private static final String CERT_TYPE_PERSONAL = "personal";
    //
    // private static final String CERT_TYPE_ENTERPRISE = "enterprise";

    protected Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    MultiOrgUserAccountFacadeService multiOrgUserAccountFacadeService;
    @Autowired
    private UserService userService;
    private ShaPasswordEncoder shaPasswordEncoder = new ShaPasswordEncoder();

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.service.CertificateLoginService#check(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public Map<String, String> getLoginName(String tenant, String textCert, String textOriginData, String textSignData,
                                            String idNumber, String certType) {
        logger.info("getLoginName : tenant[" + tenant + "], idNumber[" + idNumber + "], certType[" + certType + "]");

        FJCAApps ca = new fjca.FJCAApps();
        // 社保4000
        FJCAApps.setErrorBase(4000);
        FJCAApps.setServerURL(Config.getValue(UserDetailsServiceProvider.KEY_FJCA_SERVER_URL));

        Map<String, String> queryItem = new HashMap<String, String>();
        String subjectDN = ca.getSubject(textCert);
        if (StringUtils.isBlank(subjectDN)) {

            queryItem.put("error", "证书错误");
            return queryItem;

            // 抛出证书错误
            // throw new RuntimeException("抛出证书错误");
        }

        MultiOrgUserAccount user = null;
        // 证书登录时，使用证书主体 + 身份证号或组织机构代码去验证用户登录，如果后台配置没有设置证书主体则只使用身份证号或组织机构验证
        // 使用证书主体 + 身份证号或组织机构代码去验证用户登录

        if (StringUtils.isNotBlank(idNumber)) {

            user = multiOrgUserAccountFacadeService.getMultiOrgUserAccountByIdNumber(idNumber);

            if (user == null) {
                if ("personal".equals(certType)) {
                    queryItem.put("error", "个人证书身份证号[" + idNumber + "]或证书主体[" + subjectDN + "]配置错误!");
                    return queryItem;
                    // throw new RuntimeException("个人证书身份证号[" + idNumber +
                    // "]或证书主体[" + subjectDN + "]配置错误!");
                } else {
                    queryItem.put("error", "企业证书组织机构代码[" + idNumber + "]或证书主体[" + subjectDN + "]配置错误!");
                    return queryItem;
                    // throw new RuntimeException("企业证书组织机构代码[" + idNumber +
                    // "]或证书主体[" + subjectDN + "]配置错误!");
                }
            }
        } else {
            // 证书取证书主体验证
            // user = userService.getBySubjectDN(subjectDN);
            queryItem.put("error", "个人证书身份证号或企业证书组织机构代码不能为空!");
            return queryItem;
            // throw new RuntimeException("个人证书身份证号或企业证书组织机构代码不能为空!");
        }

        String loginName = null;
        String password = null;
        loginName = user.getLoginNameLowerCase();

        // password = shaPasswordEncoder.encodePassword(loginName,
        // textOriginData);
        // password = Md5PasswordEncoderUtils.encodePassword(password,
        // loginName);

        password = SM3Util.encrypt(textOriginData + "{" + loginName + "}");

        queryItem.put("loginName", loginName);
        queryItem.put("password", password);

        return queryItem;
    }

    @Override
    public Map<String, String> getLoginNameByKey(String tenant, String certificateSubject) {
        Map<String, String> queryItem = new HashMap<String, String>();
        MultiOrgUserAccount user = null;
        if (StringUtils.isNotBlank(certificateSubject)) {
            user = multiOrgUserAccountFacadeService.getMultiOrgUserAccountCertificateSubject(certificateSubject);
            if (user == null) {
                queryItem.put("error", "电子钥匙盘[" + certificateSubject + "]未绑定用户");
            } else {
                queryItem.put("loginName", user.getLoginNameLowerCase());
            }
        } else {
            queryItem.put("error", "电子钥匙盘序列号不能为空");
        }
        return queryItem;
    }

}
