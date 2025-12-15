/*
 * @(#)2020年8月28日 V1.0
 *
 * Copyright 2020 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.multi.org.support;

/**
 * Description: 如何描述该类
 *
 * @author zhongzh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2020年8月28日.1	zhongzh		2020年8月28日		Create
 * </pre>
 * @date 2020年8月28日
 */
public enum ViewStyle {

    tree("组织树"), list("人员列表");

    private String name;

    /**
     * @param name
     */
    private ViewStyle(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

}
