/*
 * @(#)2019-03-01 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.business.service;


import com.wellsoft.pt.basicdata.business.dao.BusinessRoleOrgUserDao;
import com.wellsoft.pt.basicdata.business.dto.BusinessRoleOrgUserDto;
import com.wellsoft.pt.basicdata.business.entity.BusinessRoleOrgUserEntity;
import com.wellsoft.pt.jpa.service.JpaService;

import java.util.List;
import java.util.Set;

/**
 * Description: 数据库表BUSINESS_ROLE_ORG_USER的service服务接口
 *
 * @author leo
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2019-03-01.1	leo		2019-03-01		Create
 * </pre>
 * @date 2019-03-01
 */
public interface BusinessRoleOrgUserService extends JpaService<BusinessRoleOrgUserEntity, BusinessRoleOrgUserDao, String> {

    public void batchSave(String categoryUuid, List<String> roleUuidList, List<String> usersList, List<String> usersValueList);

    public List<BusinessRoleOrgUserDto> findByOrgUuid(String orgUuid);

    public List<BusinessRoleOrgUserEntity> findByOrgUuidAndRoleUuid(String categoryUuid, String orgUuid, String roleUuid);

    boolean isMemberOf(String userId, Set<String> orgIds);
}
