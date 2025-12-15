/*
 * @(#)Sep 8, 2017 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.security.core.authentication.encoding;

import org.springframework.security.authentication.encoding.BasePasswordEncoder;
import org.springframework.security.authentication.encoding.MessageDigestPasswordEncoder;
import org.springframework.security.authentication.encoding.PlaintextPasswordEncoder;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * Sep 8, 2017.1	zhulh		Sep 8, 2017		Create
 * </pre>
 * @date Sep 8, 2017
 */
public class PasswordEncoderFactory {

    public static BasePasswordEncoder getPasswordEncoder(String algorithm) {
        if (PasswordAlgorithm.Plaintext.getValue().equals(algorithm)) {
            return new PlaintextPasswordEncoder();
        }
        return new MessageDigestPasswordEncoder(algorithm);
    }

}
