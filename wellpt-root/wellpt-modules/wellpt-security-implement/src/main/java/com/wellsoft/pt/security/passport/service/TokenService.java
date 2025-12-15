package com.wellsoft.pt.security.passport.service;

import org.springframework.security.access.AccessDeniedException;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2021年11月08日   chenq	 Create
 * </pre>
 */
public interface TokenService {

    void verifyToken(String token, String appId) throws AccessDeniedException;
}
