/*
 * @(#)2017-02-14 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.core.web.action;

import com.wellsoft.pt.dms.core.web.ActionProxy;

import java.util.Collection;

/**
 * Description: 操作管理
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2017-02-14.1	zhulh		2017-02-14		Create
 * </pre>
 * @date 2017-02-14
 */
public interface ActionManager {

    void registerAction(String actionId, ActionProxy action);

    ActionProxy getAction(String actionId);

    Collection<ActionProxy> getAllActions();

}
