/*
 * @(#)2016年3月30日 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.server;

import com.wellsoft.context.util.ApplicationContextHolder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016年3月30日.1	zhulh		2016年3月30日		Create
 * </pre>
 * @date 2016年3月30日
 */
public class ComponentContainer {

    private List<Object> components = new ArrayList<Object>();

    /**
     * 如何描述该方法
     *
     * @param object
     */
    public void add(Object object) {
        DefaultListableBeanFactory fty = (DefaultListableBeanFactory) ApplicationContextHolder.getApplicationContext()
                .getAutowireCapableBeanFactory();
        components.add(object);
    }

}
