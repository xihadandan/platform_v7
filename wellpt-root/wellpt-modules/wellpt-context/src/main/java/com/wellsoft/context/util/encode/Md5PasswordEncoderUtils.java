/*
 * @(#)2013-11-19 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.context.util.encode;

import org.springframework.security.authentication.encoding.Md5PasswordEncoder;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-11-19.1	zhulh		2013-11-19		Create
 * </pre>
 * @date 2013-11-19
 */
public class Md5PasswordEncoderUtils {
    private static Md5PasswordEncoder passwordEncoder = new Md5PasswordEncoder();

    /**
     * 对密码进行MD5编码
     *
     * @param rawPass 明文密码
     * @param salt    密钥（盐值）
     * @return 编码后字符串
     * @see Md5PasswordEncoder#encodePassword(String, Object)
     */
    public synchronized static String encodePassword(String rawPass, Object salt) {
        return passwordEncoder.encodePassword(rawPass, salt);
    }

    /**
     * 验证密码
     *
     * @param encPass MD5加密后密码
     * @param rawPass 明文密码
     * @param salt    密钥（盐值）
     * @return true-验证成功,false-验证失败
     */
    public synchronized static boolean isPasswordValid(String encPass, String rawPass, Object salt) {
        return passwordEncoder.isPasswordValid(encPass, rawPass, salt);
    }
}
