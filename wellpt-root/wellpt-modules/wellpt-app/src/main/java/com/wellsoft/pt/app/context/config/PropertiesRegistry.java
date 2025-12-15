/*
 * @(#)2017-01-20 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.context.config;

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
 * 2017-01-20.1	zhulh		2017-01-20		Create
 * </pre>
 * @date 2017-01-20
 */
public class PropertiesRegistry {
    private List<String> propertiesFiles = new ArrayList<String>();

    /**
     * @return the propertiesFiles
     */
    public List<String> getPropertiesFiles() {
        return propertiesFiles;
    }

    /**
     * @param propertiesFiles 要设置的propertiesFiles
     */
    public void setPropertiesFiles(List<String> propertiesFiles) {
        this.propertiesFiles = propertiesFiles;
    }

    /**
     * @param properties
     */
    public void registerProperties(String properties) {
        this.propertiesFiles.add(properties);
    }
}
