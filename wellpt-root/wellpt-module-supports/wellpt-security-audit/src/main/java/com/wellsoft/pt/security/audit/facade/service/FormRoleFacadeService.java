/*
 * @(#)2020年10月21日 V1.0
 *
 * Copyright 2020 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.security.audit.facade.service;

import com.wellsoft.context.service.Facade;
import org.springframework.transaction.annotation.Transactional;

/**
 * 业务角色优化旧数据升级使用
 *
 * @author shenhb
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2020年10月21日.1	shenhb		2020年10月21日		Create
 * </pre>
 * @date 2020年10月21日
 */
public interface FormRoleFacadeService extends Facade {

    @Transactional
    void handleDyformRoleUpdate();
}
