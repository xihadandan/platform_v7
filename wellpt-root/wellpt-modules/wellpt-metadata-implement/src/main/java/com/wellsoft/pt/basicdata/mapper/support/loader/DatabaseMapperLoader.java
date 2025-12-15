/*
 * @(#)2017年10月14日 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.mapper.support.loader;

import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.basicdata.mapper.MapperLoader;
import com.wellsoft.pt.basicdata.mapper.service.MapperService;

/**
 * Description: DatabaseMapperLoader
 * 使用：
 * 建议：
 *
 * @author zhongzh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2017年10月14日.1	zhongzh		2017年10月14日		Create
 * </pre>
 * @date 2017年10月14日
 */
public class DatabaseMapperLoader implements MapperLoader {

    private final static DatabaseMapperLoader instance = new DatabaseMapperLoader();

    private DatabaseMapperLoader() {

    }

    public static final MapperLoader getInstance() {
        return instance;
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.mapper.DefaultMapperBuilder#build(java.lang.String)
     */
    @Override
    public String find(String mapId) {
        MapperService mapperService = ApplicationContextHolder.getBean(MapperService.class);
        return mapperService.find(mapId);
    }
}
