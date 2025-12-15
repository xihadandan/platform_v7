/*
 * @(#)Apr 26, 2018 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.task.support;

/**
 * Description: 任务触发类型
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * Apr 26, 2018.1	zhulh		Apr 26, 2018		Create
 * </pre>
 * @date Apr 26, 2018
 */
public interface JobFiredType {

    public static final Integer START = 1;

    public static final Integer PAUSE = 2;

    public static final Integer RESUME = 3;

    public static final Integer RESTART = 4;

    public static final Integer STOP = 5;

    public static final Integer DELETE = 6;

}
