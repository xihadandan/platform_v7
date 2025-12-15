/*
 * @(#)2018年12月6日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.printtemplate.support;

import java.text.DecimalFormat;

/**
 * Description: 如何描述该类
 *
 * @author zhongzh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2018年12月6日.1	zhongzh		2018年12月6日		Create
 * </pre>
 * @date 2018年12月6日
 */
public abstract class PrintUtils {

    public final static DecimalFormat versionFormat = new DecimalFormat("0.0");

    public final static String PRINT_UUID_SCHEMA = "UUID://";
}
