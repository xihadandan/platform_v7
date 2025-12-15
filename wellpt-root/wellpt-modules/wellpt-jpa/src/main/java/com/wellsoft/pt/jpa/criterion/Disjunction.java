/*
 * @(#)2016年10月25日 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.jpa.criterion;

/**
 * Description: 如何描述该类
 *
 * @author xiem
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016年10月25日.1	xiem		2016年10月25日		Create
 * </pre>
 * @date 2016年10月25日
 */
public class Disjunction extends Junction {

    /**
     * 如何描述该构造方法
     *
     * @param nature
     */
    protected Disjunction() {
        super(Nature.OR);
    }

    protected Disjunction(Criterion... criterion) {
        super(Nature.OR, criterion);
    }
}
