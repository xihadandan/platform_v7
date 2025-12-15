/*
 * @(#)2019年8月16日 V1.0
 *
 * Copyright 2019 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dyform.implement.definition.validator;

import java.util.Map;

/**
 * Description: 如何描述该类
 *
 * @author zhongzh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2019年8月16日.1	zhongzh		2019年8月16日		Create
 * </pre>
 * @date 2019年8月16日
 */
public interface ValidatorInit {

    public Map<String, Object> init(Object obj);

}
