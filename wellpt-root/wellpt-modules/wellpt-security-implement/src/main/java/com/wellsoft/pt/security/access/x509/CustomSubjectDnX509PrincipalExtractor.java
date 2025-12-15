/*
 * @(#)2013-12-4 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.security.access.x509;

import com.wellsoft.context.enums.LoginType;
import com.wellsoft.pt.security.util.TenantContextHolder;
import org.springframework.security.web.authentication.preauth.x509.SubjectDnX509PrincipalExtractor;

import java.security.cert.X509Certificate;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-12-4.1	zhulh		2013-12-4		Create
 * </pre>
 * @date 2013-12-4
 */
public class CustomSubjectDnX509PrincipalExtractor extends SubjectDnX509PrincipalExtractor {

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see org.springframework.security.web.authentication.preauth.x509.SubjectDnX509PrincipalExtractor#extractPrincipal(java.security.cert.X509Certificate)
     */
    @Override
    public Object extractPrincipal(X509Certificate clientCert) {
        TenantContextHolder.setLoginType(LoginType.X509Certificate);
        return super.extractPrincipal(clientCert);
    }

}
