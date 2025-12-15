/*
 * @(#)2015年8月20日 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.cg.core.source;

import com.wellsoft.pt.dyform.facade.dto.DyFormFormDefinition;

import java.util.LinkedList;
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
 * 2015年8月20日.1	zhulh		2015年8月20日		Create
 * </pre>
 * @date 2015年8月20日
 */
public class DyformSource implements Source {
    private List<DyFormFormDefinition> dyforms = new LinkedList<DyFormFormDefinition>();

    public void add(DyFormFormDefinition dyFormDefinition) {
        dyforms.add(dyFormDefinition);
    }

    public List<DyFormFormDefinition> getDyforms() {
        return dyforms;
    }
}
