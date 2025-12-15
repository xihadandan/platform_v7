/*
 * @(#)2021年5月27日 V1.0
 *
 * Copyright 2021 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.timer.support;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2021年5月27日.1	zhulh		2021年5月27日		Create
 * </pre>
 * @date 2021年5月27日
 */
public interface TsWorkTimePlanUsedChecker {

    boolean isWorkTimePlanUsed(String workTimePlanUuid);

}
