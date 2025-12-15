/*
 * @(#)21 Dec 2016 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.mobilelist.facade.service.impl;

import com.wellsoft.context.component.select2.Select2DataBean;
import com.wellsoft.context.component.select2.Select2QueryData;
import com.wellsoft.context.component.select2.Select2QueryInfo;
import com.wellsoft.pt.basicdata.mobilelist.facade.service.MobileListService;
import com.wellsoft.pt.basicdata.mobilelist.support.MobileListTemplate;
import com.wellsoft.pt.jpa.service.impl.BaseServiceImpl;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Description: 如何描述该类
 *
 * @author Xiem
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 21 Dec 2016.1	Xiem		21 Dec 2016		Create
 * </pre>
 * @date 21 Dec 2016
 */
@Service
@Transactional
public class MobileListServiceImpl extends BaseServiceImpl implements MobileListService, ApplicationContextAware {

    private ApplicationContext applicationContext;

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.mobilelist.facade.service.MobileListService#getMobileListTemplate(com.wellsoft.pt.common.component.select2.Select2QueryInfo)
     */
    @Override
    public Select2QueryData getMobileListTemplate(Select2QueryInfo queryInfo) {
        String[] beanNames = applicationContext.getBeanNamesForType(MobileListTemplate.class);
        Select2QueryData result = new Select2QueryData();
        for (String beanName : beanNames) {
            result.addResultData(new Select2DataBean(beanName, ((MobileListTemplate) applicationContext
                    .getBean(beanName)).getName()));
        }
        return result;
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.mobilelist.facade.service.MobileListService#getMobileListTemplateByBeanName(java.lang.String)
     */
    @Override
    public MobileListTemplate getMobileListTemplateByBeanName(String beanName) {
        return (MobileListTemplate) applicationContext.getBean(beanName);
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see org.springframework.context.ApplicationContextAware#setApplicationContext(org.springframework.context.ApplicationContext)
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

}
