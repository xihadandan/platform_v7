/*
 * @(#)Mar 14, 2022 V1.0
 *
 * Copyright 2022 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.documentlink.support;

import org.apache.commons.lang.StringUtils;

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
public abstract class AbstractDocumentLinkAccessChecker implements DocumentLinkAccessChecker {

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.documentlink.support.DocumentLinkAccessChecker#getName()
     */
    @Override
    public String getName() {
        return StringUtils.uncapitalize(this.getClass().getSimpleName());
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.documentlink.support.DocumentLinkAccessChecker#check(java.lang.String, com.wellsoft.pt.basicdata.documentlink.support.DocumentLinkInfo)
     */
    @Override
    public boolean check(String dataUuid, DocumentLinkInfo documentLinkInfo) {
        return false;
    }

}
