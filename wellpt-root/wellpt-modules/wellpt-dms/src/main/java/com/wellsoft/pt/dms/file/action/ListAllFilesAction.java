/*
 * @(#)Jan 4, 2018 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
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
 * Jan 4, 2018.1	zhulh		Jan 4, 2018		Create
 * </pre>
 * @date Jan 4, 2018
 */
@Component
public class ListAllFilesAction extends AbstractFileAction {

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.file.action.FileAction#getId()
     */
    @Override
    public String getId() {
        return FileActions.LIST_ALL_FILES;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.file.action.FileAction#getName()
     */
    @Override
    public String getName() {
        return "列出当前夹下的文件(包含子夹)";
    }

    /**
     * (non-Javadoc)
     *
     * @see org.springframework.core.Ordered#getOrder()
     */
    @Override
    public int getOrder() {
        return 114;
    }

}
