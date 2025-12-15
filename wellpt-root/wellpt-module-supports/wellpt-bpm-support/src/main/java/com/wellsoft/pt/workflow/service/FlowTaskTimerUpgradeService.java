/*
 * @(#)2021年6月8日 V1.0
 *
 * Copyright 2021 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.workflow.service;

import java.util.List;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2021年6月8日.1	zhulh		2021年6月8日		Create
 * </pre>
 * @date 2021年6月8日
 */
public interface FlowTaskTimerUpgradeService {

    /**
     * 计时器实例数据升级到平台6.2.7版本
     *
     * @return
     */
    List<String> upgrade2v6_2_7(String flowDefUuid);

}
