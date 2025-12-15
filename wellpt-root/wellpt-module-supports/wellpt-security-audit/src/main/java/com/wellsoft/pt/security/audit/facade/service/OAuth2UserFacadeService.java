package com.wellsoft.pt.security.audit.facade.service;

import com.wellsoft.context.service.BaseService;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2020年08月10日   chenq	 Create
 * </pre>
 */
public interface OAuth2UserFacadeService extends BaseService {

    boolean existUser(String loginName);

    void addUser(String userjson);

    void deleteUser(String loginName);

    void modifyPassword(String loginName, String newPassword, String oldPassword);

    void expiredUser(String loginName);
}
