package com.wellsoft.context.config;

import com.wellsoft.context.exception.WellException;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author lilin
 * @ClassName: Environment
 * @Description: TODO(这里用一句话描述这个类的作用)
 */
public final class Environment {
    public static final String PARIMARY_KEY_CACHE_NUM = "com.well.oa.core.workflow.core.key.cache.num";

    public static final String IS_USE_WEBSERVICE = "com.well.oa.core.workflow.use.webservice";

    public static final String IS_USE_TRANSACTION = "com.well.oa.core.workflow.use.transaction";

    public static final String RELA_DATA_MANAGER_TYPE = "com.well.oa.core.workflow.relaData.type";

    private static final Properties GLOBAL_PROPERTIES;

    private static final Logger log = LoggerFactory.getLogger(Environment.class);

    static {
        GLOBAL_PROPERTIES = new Properties();

        try {
            InputStream stream = ConfigHelper.getResourceAsStream("/system.properties");
            try {
                GLOBAL_PROPERTIES.load(stream);
            } catch (Exception e) {
                log.error("problem loading properties from hibernate.properties");
                log.error(ExceptionUtils.getStackTrace(e));
            } finally {
                try {
                    stream.close();
                } catch (IOException ioe) {
                    log.error("could not close stream on hibernate.properties", ioe);
                    log.error(ExceptionUtils.getStackTrace(ioe));
                }
            }
        } catch (WellException he) {
            log.error("starflow.properties not found");
            log.error(ExceptionUtils.getStackTrace(he));
        }

        try {
            GLOBAL_PROPERTIES.putAll(System.getProperties());
        } catch (SecurityException se) {
            log.error("could not copy system properties, system properties will be ignored");
            log.error(ExceptionUtils.getStackTrace(se));
        }

    }

    /**
     * Disallow instantiation
     */
    private Environment() {
        throw new UnsupportedOperationException();
    }

    /**
     * Return <tt>System</tt> properties, extended by any properties specified
     * in <tt>hibernate.properties</tt>.
     *
     * @return Properties
     */
    public static Properties getProperties() {
        Properties copy = new Properties();
        copy.putAll(GLOBAL_PROPERTIES);
        return copy;
    }
}
