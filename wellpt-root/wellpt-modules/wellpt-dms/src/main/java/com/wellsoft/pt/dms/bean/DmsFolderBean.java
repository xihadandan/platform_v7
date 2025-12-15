/*
 * @(#)2017-12-19 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.bean;

import com.wellsoft.pt.dms.entity.DmsFolderEntity;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2017-12-19.1	zhulh		2017-12-19		Create
 * </pre>
 * @date 2017-12-19
 */
public class DmsFolderBean extends DmsFolderEntity {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 1513679281314L;

    private DmsFolderConfigurationBean configuration = new DmsFolderConfigurationBean();

    /**
     * @return the configuration
     */
    public DmsFolderConfigurationBean getConfiguration() {
        return configuration;
    }

    /**
     * @param configuration 要设置的configuration
     */
    public void setConfiguration(DmsFolderConfigurationBean configuration) {
        this.configuration = configuration;
    }

}
