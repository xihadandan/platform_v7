/*
 * @(#)2022-01-12 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.multi.org.service;


import com.wellsoft.pt.jpa.service.JpaService;
import com.wellsoft.pt.multi.org.dao.MultiUserRelationAccountDao;
import com.wellsoft.pt.multi.org.entity.MultiUserRelationAccountEntity;

/**
 * Description: 数据库表MULTI_USER_RELATION_ACCOUNT的service服务接口
 *
 * @author baozh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2022-01-12.1	baozh		2022-01-12		Create
 * </pre>
 * @date 2022-01-12
 */
public interface MultiUserRelationAccountService extends JpaService<MultiUserRelationAccountEntity, MultiUserRelationAccountDao, String> {

}
