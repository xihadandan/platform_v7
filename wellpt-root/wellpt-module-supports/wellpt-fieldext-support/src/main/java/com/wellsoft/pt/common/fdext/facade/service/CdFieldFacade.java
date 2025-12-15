/*
 * @(#)2016-03-11 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.common.fdext.facade.service;

import com.wellsoft.context.service.BaseService;
import com.wellsoft.pt.common.fdext.bean.CdFieldExtensionValue;
import com.wellsoft.pt.common.fdext.support.DyFieldRender;
import com.wellsoft.pt.common.fdext.support.ICdFieldRender;

import java.util.List;

/**
 * Description: 如何描述该类
 *
 * @author zhongzh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016-03-11.1	zhongzh		2016-03-11		Create
 * </pre>
 * @date 2016-03-11
 */
public interface CdFieldFacade extends BaseService {

    DyFieldRender getDyFieldRender(String tenantId, String groupCode, String dataUuid);

    DyFieldRender getDyFieldRender(String tenantId, String groupCode);

    List<ICdFieldRender> getFields(String tenantId, String groupCode);

    CdFieldExtensionValue getData(String dataUuid, String groupType);

    void saveData(String dataUuid, String groupType, CdFieldExtensionValue bean);

}
