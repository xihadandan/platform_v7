/*
 * @(#)2016年6月16日 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.webmail.facade.service;

import com.wellsoft.context.service.BaseService;

/**
 * Description: 邮箱升级服务
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016年6月16日.1	zhulh		2016年6月16日		Create
 * </pre>
 * @date 2016年6月16日
 */
public interface WmMailboxUpgradeService extends BaseService {

    void upgradeOutbox();

}
