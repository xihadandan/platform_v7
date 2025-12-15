/*
 * @(#)2016年6月23日 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.facade.service;

import com.wellsoft.context.component.select2.Select2QueryApi;
import com.wellsoft.context.service.BaseService;
import com.wellsoft.pt.app.js.JavaScriptModule;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016年6月23日.1	zhulh		2016年6月23日		Create
 * </pre>
 * @date 2016年6月23日
 */
public interface AppJavaScriptModuleMgr extends BaseService, Select2QueryApi {

    JavaScriptModule getById(String id);

}
