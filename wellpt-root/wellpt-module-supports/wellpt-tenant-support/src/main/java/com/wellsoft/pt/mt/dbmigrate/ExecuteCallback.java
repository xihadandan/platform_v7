/*
 * @(#)2016年3月9日 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.mt.dbmigrate;

import com.wellsoft.pt.mt.entity.Tenant;

/**
 * Description: 如何描述该类
 *
 * @author zhongzh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016年3月9日.1	zhongzh		2016年3月9日		Create
 * </pre>
 * @date 2016年3月9日
 */
public interface ExecuteCallback {

    public void onExecuting(IMigrateModule module, Tenant tenant);

    public void onExecuted(IMigrateModule module, Tenant tenant, Integer status, String message);
}
