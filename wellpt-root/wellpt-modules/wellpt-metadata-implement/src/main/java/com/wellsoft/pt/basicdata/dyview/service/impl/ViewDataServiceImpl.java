/*
 * @(#)2013-3-19 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.dyview.service.impl;

import com.wellsoft.pt.basicdata.dyview.service.ViewDataService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Description: 如何描述该类
 *
 * @author wubin
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-3-19.1	wubin		2013-3-19		Create
 * </pre>
 * @date 2013-3-19
 */
@Service
@Transactional
public class ViewDataServiceImpl implements ViewDataService {

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.dyview.service.ViewDataService#getViewById()
     */
    @Override
    public List getViewById() {
        // TODO Auto-generated method stub
        return null;
    }

}
