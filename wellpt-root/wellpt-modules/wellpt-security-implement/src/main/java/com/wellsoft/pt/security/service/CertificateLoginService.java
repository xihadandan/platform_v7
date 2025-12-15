/*
 * @(#)2013-12-3 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.security.service;

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
public interface CertificateLoginService {
    Map<String, String> getLoginName(String tenant, String textCert, String textOriginData, String textSignData,
                                     String idNumber, String certType);

    public abstract Map<String, String> getLoginNameByKey(String tenant, String snKey);
}
