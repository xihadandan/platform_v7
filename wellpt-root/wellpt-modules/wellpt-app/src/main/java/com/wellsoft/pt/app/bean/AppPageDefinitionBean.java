/*
 * @(#)2016-05-09 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.bean;

import com.google.common.collect.Lists;
import com.wellsoft.pt.app.entity.AppDefElementI18nEntity;
import com.wellsoft.pt.app.entity.AppPageDefinition;

import java.util.List;

/**
 * Description: 如何描述该类
 *
 * @author t
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016-05-09.1	t		2016-05-09		Create
 * </pre>
 * @date 2016-05-09
 */
public class AppPageDefinitionBean extends AppPageDefinition {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 1462783385456L;

    /**
     * 页面组件功能资源未授权集合
     * 页面组件功能资源权限只有被设置为保护的情况下（默认是不保护的），才需要判断是否有权限，所以通过未授权的逆向方式能够判断是否开启保护以及是否有权限
     */
    private List<String> unauthorizedResource = Lists.newArrayList();

    private List<AppDefElementI18nEntity> i18ns = Lists.newArrayList();

    private String system; // 归属系统


    public String getSystem() {
        return this.system;
    }

    public void setSystem(final String system) {
        this.system = system;
    }

    public List<String> getUnauthorizedResource() {
        return unauthorizedResource;
    }

    public void setUnauthorizedResource(List<String> unauthorizedResource) {
        this.unauthorizedResource = unauthorizedResource;
    }

    public List<AppDefElementI18nEntity> getI18ns() {
        return i18ns;
    }

    public void setI18ns(List<AppDefElementI18nEntity> i18ns) {
        this.i18ns = i18ns;
    }
}
