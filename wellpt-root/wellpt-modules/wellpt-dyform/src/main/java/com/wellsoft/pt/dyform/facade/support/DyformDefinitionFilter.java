/*
 * @(#)2021年1月30日 V1.0
 *
 * Copyright 2021 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dyform.facade.support;

import com.wellsoft.pt.dyform.facade.dto.DyFormFormDefinition;

/**
 * Description: 如何描述该类
 *
 * @author zhongzh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2021年1月30日.1	zhongzh		2021年1月30日		Create
 * </pre>
 * @date 2021年1月30日
 */
public interface DyformDefinitionFilter {

    boolean accept(DyFormFormDefinition pathname);

}
