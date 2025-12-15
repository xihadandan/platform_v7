/*
 * @(#)Feb 1, 2018 V1.0
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
 * Feb 1, 2018.1	zhulh		Feb 1, 2018		Create
 * </pre>
 * @date Feb 1, 2018
 */
@Component
public class PhysicalDeleteFolderAction extends AbstractFileAction {

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.file.action.FileAction#getId()
     */
    @Override
    public String getId() {
        return FileActions.PHYSICAL_DELETE_FOLDER;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.file.action.FileAction#getName()
     */
    @Override
    public String getName() {
        return "彻底删除夹(有子夹、文件一起删除)";
    }

    /**
     * (non-Javadoc)
     *
     * @see org.springframework.core.Ordered#getOrder()
     */
    @Override
    public int getOrder() {
        return 132;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.file.action.AbstractFileAction#configurable()
     */
    @Override
    public boolean configurable() {
        return false;
    }

}
