/*
 * @(#)6/5/24 V1.0
 *
 * Copyright 2024 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.fulltext.support;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 6/5/24.1	zhulh		6/5/24		Create
 * </pre>
 * @date 6/5/24
 */
@FunctionalInterface
public interface DataFiller<T> {

    void filling(T t);

}
