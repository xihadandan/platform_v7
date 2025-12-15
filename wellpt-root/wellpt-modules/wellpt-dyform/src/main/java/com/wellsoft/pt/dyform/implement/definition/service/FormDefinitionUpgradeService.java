/*
 * @(#)2021年3月1日 V1.0
 *
 * Copyright 2021 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dyform.implement.definition.service;

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
 * 2021年3月1日.1	zhulh		2021年3月1日		Create
 * </pre>
 * @date 2021年3月1日
 */
public interface FormDefinitionUpgradeService {

    List<String> upgrade2v6_2_3(String formUuid);

    List<String> upgrade_v6_2_3_repair_json(String formUuid);

    List<String> upgrade2v6_2_5(String formUuid);

    List<String> upgrade_v6_2_5_repair_readonly_style(String formUuid);
}
