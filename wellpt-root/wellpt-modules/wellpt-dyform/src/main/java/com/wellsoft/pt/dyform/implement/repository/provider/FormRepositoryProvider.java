/*
 * @(#)2019年8月20日 V1.0
 *
 * Copyright 2019 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dyform.implement.repository.provider;

import com.wellsoft.pt.dyform.implement.repository.FormRepository;
import com.wellsoft.pt.dyform.implement.repository.FormRepositoryContext;


/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2019年8月20日.1	zhulh		2019年8月20日		Create
 * </pre>
 * @date 2019年8月20日
 */
public interface FormRepositoryProvider {

    FormRepository provide(FormRepositoryContext formRepositoryContext);

}
