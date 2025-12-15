/*
 * @(#)2018年1月10日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.security.audit.facade.service;

import com.wellsoft.context.service.Facade;
import com.wellsoft.pt.security.audit.bean.GrantedObjectDto;
import com.wellsoft.pt.security.audit.entity.Resource;

import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * Description: 如何描述该类
 *
 * @author chenqiong
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2018年1月10日.1	chenqiong		2018年1月10日		Create
 * </pre>
 * @date 2018年1月10日
 */
public interface SecurityAuditFacadeService extends Facade {

    List<Resource> getDynamicButtonResourcesByCode(String code);

    Resource getButtonByCode(String code);

    boolean isGranted(String code);

    Resource getResourceByCode(String authority);

    boolean hasRole(String userId, String roleAdmin);

    boolean isGranted(Object object, String functionType);

    Map<Object, Boolean> isGranted(HashSet<GrantedObjectDto> grantedObjectDtos);


}
