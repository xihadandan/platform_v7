/*
 * @(#)Jun 24, 2017 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.datastore.support.renderer;

import com.wellsoft.pt.basicdata.datadict.service.DataDictionaryService;
import com.wellsoft.pt.basicdata.datastore.support.RendererParam;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
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
 * Jun 24, 2017.1	zhulh		Jun 24, 2017		Create
 * </pre>
 * @date Jun 24, 2017
 */
@Component
public class DataDictionaryDataStoreRenderer extends AbstractSelectiveDatasDataStoreRenderer {
    // 数据字典UUID的KEY
    private static final String KEY_DATA_DIC = "dataDic";

    @Autowired
    private DataDictionaryService dataDictionaryService;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.datastore.support.renderer.DataStoreRenderer#getType()
     */
    @Override
    public String getType() {
        return "dataDictionaryRenderer";
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.datastore.support.renderer.DataStoreRenderer#getName()
     */
    @Override
    public String getName() {
        return "数据字典渲染器";
    }

    @Override
    public String getConfigKey(RendererParam param) {
        String dataDict = param.getString(KEY_DATA_DIC);
        if (StringUtils.isNotBlank(dataDict)) {
            return dataDict;
        }
        return StringUtils.EMPTY;
    }

}
