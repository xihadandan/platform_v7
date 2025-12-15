/*
 * @(#)Dec 29, 2017 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.file.action;

import org.springframework.stereotype.Component;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * Dec 29, 2017.1	zhulh		Dec 29, 2017		Create
 * </pre>
 * @date Dec 29, 2017
 */
@Component
public class MoveFolderAction extends AbstractFileAction {

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.core.file.action.FileAction#getId()
     */
    @Override
    public String getId() {
        return FileActions.MOVE_FOLDER;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.core.file.action.FileAction#getName()
     */
    @Override
    public String getName() {
        return "移动夹";
    }

    /**
     * (non-Javadoc)
     *
     * @see org.springframework.core.Ordered#getOrder()
     */
    @Override
    public int getOrder() {
        return 122;
    }

}
