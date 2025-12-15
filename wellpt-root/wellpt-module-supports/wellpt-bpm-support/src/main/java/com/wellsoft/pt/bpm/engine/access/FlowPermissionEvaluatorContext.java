/*
 * @(#)2021年7月27日 V1.0
 *
 * Copyright 2021 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.access;

import org.apache.commons.lang3.BooleanUtils;
import org.springframework.core.NamedThreadLocal;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2021年7月27日.1	zhulh		2021年7月27日		Create
 * </pre>
 * @date 2021年7月27日
 */
public class FlowPermissionEvaluatorContext {

    private static final ThreadLocal<Boolean> enableContextHolder = new NamedThreadLocal<Boolean>(
            "onlyUseAccessPermissionProvider");

    private static final ThreadLocal<Boolean> onlyUseAccessPermissionProviderContextHolder = new NamedThreadLocal<Boolean>(
            "onlyUseAccessPermissionProvider");

    /**
     * @return
     */
    public static boolean isEnableContextHolder() {
        Boolean enable = enableContextHolder.get();
        return BooleanUtils.isTrue(enable);
    }

    /**
     * @param onlyUseAccessPermissionProvider
     */
    public static void setEnableContextHolder(boolean enable) {
        enableContextHolder.set(enable);
    }

    /**
     * @return
     */
    public static boolean getOnlyUseAccessPermissionProvider() {
        Boolean onlyUseAccessPermissionProvider = onlyUseAccessPermissionProviderContextHolder.get();
        return BooleanUtils.isTrue(onlyUseAccessPermissionProvider);
    }

    /**
     * @param onlyUseAccessPermissionProvider
     */
    public static void setOnlyUseAccessPermissionProvider(boolean onlyUseAccessPermissionProvider) {
        onlyUseAccessPermissionProviderContextHolder.set(onlyUseAccessPermissionProvider);
    }

    /**
     *
     */
    public static void reset() {
        enableContextHolder.remove();
        onlyUseAccessPermissionProviderContextHolder.remove();
    }

}
