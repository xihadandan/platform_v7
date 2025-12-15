/*
 * @(#)Feb 11, 2018 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.support.renderer;

import com.wellsoft.pt.basicdata.datastore.support.renderer.AbstractCustomDataStoreRenderer;
import com.wellsoft.pt.dms.support.FileHelper;
import org.springframework.stereotype.Component;

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
 * Feb 11, 2018.1	zhulh		Feb 11, 2018		Create
 * </pre>
 * @date Feb 11, 2018
 */
@Component
public class FileTypeDataStoreRenderer extends AbstractCustomDataStoreRenderer {

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.datastore.support.renderer.DataStoreRenderer#getType()
     */
    @Override
    public String getType() {
        return "fileTypeRenderer";
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.datastore.support.renderer.DataStoreRenderer#getName()
     */
    @Override
    public String getName() {
        return "数据管理_文件库_文件类型渲染器";
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.datastore.support.renderer.AbstractCustomDataStoreRenderer#doRenderData(java.lang.String, java.lang.Object, java.util.Map, java.lang.String)
     */
    @Override
    public Object doRenderData(String columnIndex, Object value, Map<String, Object> rowData, String exParams) {
        String filename = (String) rowData.get("name");
        String contentType = (String) value;
        return FileHelper.getFileTypeAsString(filename, contentType);
    }

}
