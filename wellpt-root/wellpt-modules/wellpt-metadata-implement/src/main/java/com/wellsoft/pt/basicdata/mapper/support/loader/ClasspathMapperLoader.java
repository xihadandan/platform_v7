/*
 * @(#)2017年10月14日 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.mapper.support.loader;

import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.basicdata.mapper.MapperException;
import com.wellsoft.pt.basicdata.mapper.MapperLoader;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternUtils;

import java.io.IOException;

/**
 * Description: ClasspathMapperLoader
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
public class ClasspathMapperLoader implements MapperLoader {

    private final static ClasspathMapperLoader instance = new ClasspathMapperLoader();
    private static Logger logger = LoggerFactory.getLogger(ClasspathMapperLoader.class);

    private ClasspathMapperLoader() {

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
        try {
            ApplicationContext applicationContext = ApplicationContextHolder.getApplicationContext();
            ResourcePatternResolver resourcePatternResolver = ResourcePatternUtils
                    .getResourcePatternResolver(applicationContext);
            String pattern = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX + "/**/" + mapId;
            Resource[] resources = resourcePatternResolver.getResources(pattern);
            if (resources == null || resources.length == 0) {
                return null;// quiet
            }
            if (resources.length > 1) {
                StringBuilder sb = new StringBuilder(resources[0].getURL().toString());
                for (int i = 1; i < resources.length; i++) {
                    sb.append(resources[i].getURL().toString()).append(";");
                }
                throw new MapperException("找到多个mapId[" + mapId + "]：" + sb);// tip
            }
            Resource resource = resources[0];
            return IOUtils.toString(resource.getInputStream());
        } catch (IOException ex) {
            logger.warn(ex.getMessage(), ex);
        }
        return null;
    }
}
