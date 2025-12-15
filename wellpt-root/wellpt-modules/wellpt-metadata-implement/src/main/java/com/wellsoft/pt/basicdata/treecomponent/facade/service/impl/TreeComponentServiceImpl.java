/*
 * @(#)27 Feb 2017 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.treecomponent.facade.service.impl;

import com.wellsoft.context.component.select2.Select2DataBean;
import com.wellsoft.context.component.select2.Select2QueryData;
import com.wellsoft.context.component.select2.Select2QueryInfo;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.basicdata.treecomponent.facade.service.TreeComponentService;
import com.wellsoft.pt.basicdata.treecomponent.facade.support.TreeComponentAsyncDataProvider;
import com.wellsoft.pt.basicdata.treecomponent.facade.support.TreeComponentDataProvider;
import com.wellsoft.pt.basicdata.treecomponent.facade.support.TreeType;
import com.wellsoft.pt.jpa.service.impl.BaseServiceImpl;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Description: 如何描述该类
 *
 * @author Xiem
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 27 Feb 2017.1	Xiem		27 Feb 2017		Create
 * </pre>
 * @date 27 Feb 2017
 */
@Service
public class TreeComponentServiceImpl extends BaseServiceImpl implements TreeComponentService {

    @Autowired(required = false)
    private List<TreeComponentDataProvider> treeComponentDataProviders;

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.treecomponent.facade.service.TreeComponentService#loadTreeComponent(com.wellsoft.pt.common.component.select2.Select2QueryInfo)
     */
    @Override
    public Select2QueryData loadTreeComponent(Select2QueryInfo query) {
        Select2QueryData result = new Select2QueryData();
        String superClass = query.getRequest().getParameter("superComponentClass");
        for (TreeComponentDataProvider treeComponentDataProvider : treeComponentDataProviders) {
            try {
                if (StringUtils.isNotBlank(
                        superClass) && !Class.forName(superClass).isAssignableFrom(
                        treeComponentDataProvider.getClass()
                )) {
                    continue;
                }
            } catch (Exception e) {
                logger.error("获取树形组件异常：", e);
            }
            result.addResultData(new Select2DataBean(treeComponentDataProvider.getClass().getName(),
                    treeComponentDataProvider.getName()));
        }
        return result;
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.treecomponent.facade.service.TreeComponentService#getTreeTypes(java.lang.String)
     */
    @Override
    public List<TreeType> getTreeTypes(String dataProviderClz) {
        try {
            return ((TreeComponentDataProvider) ApplicationContextHolder.getBean(
                    Class.forName(dataProviderClz)))
                    .getNodeTypes();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return new ArrayList<TreeType>();
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.treecomponent.facade.service.TreeComponentService#isAsync(java.lang.String)
     */
    @Override
    public boolean isAsync(String dataProviderClz) {
        try {
            Object provider = ApplicationContextHolder.getBean(Class.forName(dataProviderClz));
            return provider instanceof TreeComponentAsyncDataProvider;
        } catch (ClassNotFoundException e) {
            logger.error(e.getMessage(), e);
        }
        return false;
    }

    @Override
    public String getFilterHint(String dataProviderClz) {
        try {
            return ((TreeComponentDataProvider) ApplicationContextHolder.getBean(
                    Class.forName(dataProviderClz)))
                    .getFilterHint();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

}
