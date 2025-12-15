/*
 * @(#)2018年8月31日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.core;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2018年8月31日.1	zhulh		2018年8月31日		Create
 * </pre>
 * @date 2018年8月31日
 */
public interface Pointcut {

    // 创建
    public static final String CREATED = "created";

    // 启动
    public static final String STARTED = "started";

    // 执行之后
    public static final String AFTER_EXECUTED = "afterExecuted";

    // 完成
    public static final String COMPLETED = "completed";

    // 结束
    public static final String END = "end";

    // 删除
    public static final String DELETED = "deleted";

}
