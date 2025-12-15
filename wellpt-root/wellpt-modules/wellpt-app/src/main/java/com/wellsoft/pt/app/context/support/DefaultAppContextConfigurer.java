/*
 * @(#)Jan 19, 2017 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.context.support;

import com.wellsoft.pt.app.context.AppContextConfigurerAdapter;
import com.wellsoft.pt.app.context.config.PropertiesRegistry;

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
 * 2017.1	zhulh		Jan 19, 2017		Create
 * </pre>
 * @date Jan 19, 2017
 */
public class DefaultAppContextConfigurer extends AppContextConfigurerAdapter {
    private List<String> locations;

    /**
     *
     */
    public DefaultAppContextConfigurer() {
    }

    /**
     * @return the locations
     */
    public List<String> getLocations() {
        return locations;
    }

    /**
     * @param locations 要设置的locations
     */
    public void setLocations(List<String> locations) {
        this.locations = locations;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.context.AppContextConfigurerAdapter#addProperties(com.wellsoft.pt.app.context.config.PropertiesRegistry)
     */
    @Override
    public void addProperties(PropertiesRegistry propertiesRegistry) {
        if (this.locations == null) {
        }
        for (String location : locations) {
            propertiesRegistry.registerProperties(location);
        }
    }

}
