/*
 * @(#)2013-3-13 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.view.service.impl;

import com.wellsoft.pt.basicdata.view.entity.ColumnDefinitionNew;
import com.wellsoft.pt.basicdata.view.service.ColumnDefinitionNewService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Description: 如何描述该类
 *
 * @author Administrator
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-3-13.1	Administrator		2013-3-13		Create
 * </pre>
 * @date 2013-3-13
 */
@Service
@Transactional
public class ColumnDefinitionNewServiceImpl implements ColumnDefinitionNewService {

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.dyview.service.ColumnDefinitionService#getFieldById(java.lang.String)
     */
    @Override
    public ColumnDefinitionNew getFieldById(String viewUuid) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.dyview.service.ColumnDefinitionService#getAllField()
     */
    @Override
    public List<ColumnDefinitionNew> getAllField() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.dyview.service.ColumnDefinitionService#saveField(com.wellsoft.pt.basicdata.dyview.entity.ColumnDefinition)
     */
    @Override
    public void saveField(ColumnDefinitionNew entity) {
        // TODO Auto-generated method stub

    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.dyview.service.ColumnDefinitionService#deleteField(java.lang.String)
     */
    @Override
    public void deleteField(String viewUuid) {
        // TODO Auto-generated method stub

    }

}
