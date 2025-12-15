/*
 * @(#)2021年10月26日 V1.0
 *
 * Copyright 2021 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.repository.support;

import com.wellsoft.pt.repository.entity.LogicFileInfo;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2021年10月26日.1	zhulh		2021年10月26日		Create
 * </pre>
 * @date 2021年10月26日
 */
public class LogicFileInfoQueryItem extends LogicFileInfo {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = -3593722235535443203L;

    // 夹UUID
    private String folderUuid;

    /**
     * @return the folderUuid
     */
    public String getFolderUuid() {
        return folderUuid;
    }

    /**
     * @param folderUuid 要设置的folderUuid
     */
    public void setFolderUuid(String folderUuid) {
        this.folderUuid = folderUuid;
    }

}
