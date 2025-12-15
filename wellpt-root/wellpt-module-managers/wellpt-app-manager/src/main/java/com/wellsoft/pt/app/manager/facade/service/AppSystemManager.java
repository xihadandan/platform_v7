/*
 * @(#)2019年6月4日 V1.0
 *
 * Copyright 2019 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.manager.facade.service;

import com.wellsoft.context.component.select2.Select2QueryApi;
import com.wellsoft.context.service.BaseService;
import com.wellsoft.pt.app.manager.dto.AppSystemDto;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2019年6月4日.1	zhulh		2019年6月4日		Create
 * </pre>
 * @date 2019年6月4日
 */
public interface AppSystemManager extends BaseService, Select2QueryApi {

    void saveDto(AppSystemDto appSystemDto);

    void remove(AppSystemDto appSystemDto);

}
