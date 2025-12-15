/*
 * @(#)2019年12月12日 V1.0
 *
 * Copyright 2019 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.dp.owner;

import org.springframework.core.Ordered;

import java.util.List;

/**
 * Description: 当前用户数据所有者提供者
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2019年12月12日.1	zhulh		2019年12月12日		Create
 * </pre>
 * @date 2019年12月12日
 */
public interface DataOwnerProvider extends Ordered {

    /**
     * 提供者名称
     *
     * @return
     */
    String getName();

    /**
     * 提供者类型
     *
     * @return
     */
    String getType();

    /**
     * 返回所有者ID
     *
     * @return
     */
    List<String> getOwnerIds();

}
