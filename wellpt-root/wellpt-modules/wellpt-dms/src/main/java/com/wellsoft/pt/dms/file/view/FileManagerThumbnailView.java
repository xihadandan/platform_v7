/*
 * @(#)Jan 17, 2018 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.file.view;

import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * Jan 17, 2018.1	zhulh		Jan 17, 2018		Create
 * </pre>
 * @date Jan 17, 2018
 */
@Component
public class FileManagerThumbnailView implements FileDataView {

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.file.view.FileDataView#getName()
     */
    @Override
    public String getName() {
        return "文件管理缩略视图";
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.file.view.FileDataView#getType()
     */
    @Override
    public String getType() {
        return "fileManagerThumbnailView";
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.file.view.FileDataView#render(java.util.Map, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public void render(Map<String, ?> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
    }

    /**
     * (non-Javadoc)
     *
     * @see org.springframework.core.Ordered#getOrder()
     */
    @Override
    public int getOrder() {
        return 10;
    }

}
