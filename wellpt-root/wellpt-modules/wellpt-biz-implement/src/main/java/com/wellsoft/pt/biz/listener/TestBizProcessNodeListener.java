/*
 * @(#)10/25/22 V1.0
 *
 * Copyright 2022 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.biz.listener;

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
 * 10/25/22.1	zhulh		10/25/22		Create
 * </pre>
 * @date 10/25/22
 */
@Component
public class TestBizProcessNodeListener extends AbstractBizProcessNodeListener {
    
    /**
     * 监听器名称
     *
     * @return
     */
    @Override
    public String getName() {
        return "测试过程节点监听器";
    }

}
