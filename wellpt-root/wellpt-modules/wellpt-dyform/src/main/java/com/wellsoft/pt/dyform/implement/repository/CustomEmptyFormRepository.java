/*
 * @(#)2019年8月29日 V1.0
 *
 * Copyright 2019 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dyform.implement.repository;

import org.springframework.stereotype.Component;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2019年8月29日.1	zhulh		2019年8月29日		Create
 * </pre>
 * @date 2019年8月29日
 */
@Component
public class CustomEmptyFormRepository extends CustomFormRepository {

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dyform.implement.repository.CustomFormRepository#getName()
     */
    @Override
    public String getName() {
        return "空表单存储服务";
    }

}
