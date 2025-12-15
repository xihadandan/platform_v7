/*
 * @(#)Mar 14, 2022 V1.0
 *
 * Copyright 2022 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.documentlink.support;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * Mar 14, 2022.1	zhulh		Mar 14, 2022		Create
 * </pre>
 * @date Mar 14, 2022
 */
public interface DocumentLinkAccessChecker {

    /**
     * 检验器名称
     *
     * @return
     */
    String getName();

    /**
     * 检验数据的可访问性
     *
     * @param dataUuid
     * @param documentLinkInfo
     * @return
     */
    boolean check(String dataUuid, DocumentLinkInfo documentLinkInfo);

}
