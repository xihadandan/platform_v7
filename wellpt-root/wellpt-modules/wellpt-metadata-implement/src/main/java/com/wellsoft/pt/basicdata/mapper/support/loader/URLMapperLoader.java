/*
 * @(#)2017年10月14日 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.mapper.support.loader;

import com.wellsoft.pt.basicdata.mapper.MapperLoader;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;

/**
 * Description: FileMapperLoader
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
public class URLMapperLoader implements MapperLoader {

    private final static URLMapperLoader instance = new URLMapperLoader();
    private static Logger logger = LoggerFactory.getLogger(URLMapperLoader.class);

    private URLMapperLoader() {

    }

    public static final MapperLoader getInstance() {
        return instance;
    }

    public static void main(String[] args) {
        System.out.println(getInstance().find("file:C:\\config.ini"));
        System.out.println("---------------------------------->>>>>>");
        System.out.println(getInstance().find("http://java.sun.com/index.html"));
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
            return IOUtils.toString(new URL(mapId));
        } catch (IOException ex) {
            logger.warn(ex.getMessage(), ex);
        }
        return null;
    }
}
