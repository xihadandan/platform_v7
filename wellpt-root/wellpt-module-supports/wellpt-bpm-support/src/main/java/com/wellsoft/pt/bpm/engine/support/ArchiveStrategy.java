/*
 * @(#)2019年1月18日 V1.0
 *
 * Copyright 2019 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.support;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2019年1月18日.1	zhulh		2019年1月18日		Create
 * </pre>
 * @date 2019年1月18日
 */
public interface ArchiveStrategy {

    // 新增
    public static final String ADD = "1";
    // 替换
    public static final String REPLACE = "2";
    // 忽略
    public static final String IGNORE = "3";

}
