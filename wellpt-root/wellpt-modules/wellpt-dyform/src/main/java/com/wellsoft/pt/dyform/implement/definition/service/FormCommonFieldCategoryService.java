/*
 * @(#)2019年5月22日 V1.0
 *
 * Copyright 2019 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dyform.implement.definition.service;

import com.wellsoft.context.component.select2.Select2QueryApi;
import com.wellsoft.context.component.select2.Select2UpdateApi;
import org.springframework.transaction.annotation.Transactional;

/**
 * Description: 如何描述该类
 *
 * @author zhongzh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2019年5月22日.1	zhongzh		2019年5月22日		Create
 * </pre>
 * @date 2019年5月22日
 */
public interface FormCommonFieldCategoryService extends Select2UpdateApi, Select2QueryApi {

    public abstract Boolean isInRef(String id);

    @Transactional
    void moveFieldCategoryAfterOther(String uuid, String afterUuid);
}
