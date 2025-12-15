package com.wellsoft.context.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.DefaultPropertiesPersister;
import org.springframework.util.PropertiesPersister;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

/**
 * Description: 属性工具类
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2015年12月24日.1	Asus		2015年12月24日		Create
 * </pre>
 */
public class PropertiesUtils {

    private static final String DEFAULT_ENCODING = "UTF-8";

    private static Logger logger = LoggerFactory.getLogger(PropertiesUtils.class);

    private static PropertiesPersister propertiesPersister = new DefaultPropertiesPersister();
    private static ResourceLoader resourceLoader = new DefaultResourceLoader();

    /**
     * 载入多个properties文件, 相同的属性在最后载入的文件中的值将会覆盖之前的载入.
     * 文件路径使用Spring Resource格式, 文件编码使用UTF-8.
     *
     * @param resourcesPaths 资源文件路径
     * @return 资源对象
     * @throws IOException
     * @see org.springframework.beans.factory.config.PropertyPlaceholderConfigurer
     */
    public static Properties loadProperties(String... resourcesPaths) throws IOException {
        Properties props = new Properties();

        for (String location : resourcesPaths) {

            logger.debug("Loading properties file from:" + location);

            InputStream is = null;
            try {
                Resource resource = resourceLoader.getResource(location);
                is = resource.getInputStream();
                propertiesPersister.load(props, new InputStreamReader(is, DEFAULT_ENCODING));
            } catch (IOException ex) {
                logger.info("Could not load properties from classpath:" + location + ": " + ex.getMessage(), ex);
            } finally {
                if (is != null) {
                    is.close();
                }
            }
        }

        return props;
    }

}
