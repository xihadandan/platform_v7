/*
 * @(#)Mar 15, 2022 V1.0
 *
 * Copyright 2022 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.file.access;

import com.wellsoft.pt.basicdata.documentlink.support.AbstractDocumentLinkAccessChecker;
import com.wellsoft.pt.basicdata.documentlink.support.DocumentLinkInfo;
import com.wellsoft.pt.dms.file.action.FileActions;
import com.wellsoft.pt.dms.file.service.DmsFileActionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Description: 文件库文档链接关系，文档可读权限检查
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * Mar 15, 2022.1	zhulh		Mar 15, 2022		Create
 * </pre>
 * @date Mar 15, 2022
 */
@Component
public class DmsFileDocumentLinkAccessChecker extends AbstractDocumentLinkAccessChecker {

    @Autowired
    private DmsFileActionService dmsFileActionService;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.documentlink.support.AbstractDocumentLinkAccessChecker#check(java.lang.String, com.wellsoft.pt.basicdata.documentlink.support.DocumentLinkInfo)
     */
    @Override
    public boolean check(String dataUuid, DocumentLinkInfo documentLinkInfo) {
        return dmsFileActionService.hasFilePermission(dataUuid, FileActions.READ_FILE);
    }

}
