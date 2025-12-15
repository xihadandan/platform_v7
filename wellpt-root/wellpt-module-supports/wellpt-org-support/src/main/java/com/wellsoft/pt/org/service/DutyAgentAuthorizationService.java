/*
 * @(#)2015-4-3 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.org.service;

import com.wellsoft.context.service.BaseService;
import com.wellsoft.pt.org.entity.DutyAgentAuthorization;

import java.util.Collection;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2015-4-3.1	zhulh		2015-4-3		Create
 * </pre>
 * @date 2015-4-3
 */
public interface DutyAgentAuthorizationService extends BaseService {

    DutyAgentAuthorization get(String uuid);

    void save(DutyAgentAuthorization dutyAgentAuthorization);

    void remove(String uuid);

    void removeAll(Collection<String> uuids);

}
