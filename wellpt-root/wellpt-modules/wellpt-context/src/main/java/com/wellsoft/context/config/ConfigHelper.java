package com.wellsoft.context.config;

import com.wellsoft.context.exception.WellException;
import org.hibernate.HibernateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

/**
 * Description: 如何描述该类
 *
 * @author ll
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 2012-10-19.1	ll		2012-10-19		Create
 * </pre>
 * @date 2012-10-19
 */
public final class ConfigHelper {
    private static final Logger log = LoggerFactory.getLogger(ConfigHelper.class);

    private ConfigHelper() {
    }

    /**
     * Try to locate a local URL representing the incoming path.  The first attempt
     * assumes that the incoming path is an actual URL string (file://, etc).  If this
     * does not work, then the next attempts try to locate this UURL as a java system
     * resource.
     *
     * @param path The path representing the config location.
     * @return An appropriate URL or null.
     */
    public static final URL locateConfig(final String path) {
        try {
            return new URL(path);
        } catch (MalformedURLException e) {
            return findAsResource(path);
        }
    }

    /**
     * 如何描述该方法
     *
     * @param path
     * @return
     */
    public static final URL findAsResource(final String path) {
        URL url = null;

        // First, try to locate this resource through the current
        // context classloader.
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        if (contextClassLoader != null) {
            url = contextClassLoader.getResource(path);
        }
        if (url != null)
            return url;

        // Next, try to locate this resource through this class's classloader
        url = ConfigHelper.class.getClassLoader().getResource(path);
        if (url != null)
            return url;

        // Next, try to locate this resource through the system classloader
        url = ClassLoader.getSystemClassLoader().getResource(path);

        // Anywhere else we should look?
        return url;
    }

    public static final InputStream getConfigStream(final String path) throws WellException {
        final URL url = ConfigHelper.locateConfig(path);

        if (url == null) {
            String msg = "Unable to locate config file: " + path;
            log.error(msg);
            throw new WellException(msg);
        }

        try {
            return url.openStream();
        } catch (IOException e) {
            throw new WellException("Unable to open config file: " + path, e);
        }
    }

    /**
     * Open an Reader to the URL represented by the incoming path.  First makes a call
     * to {@link #locateConfig(java.lang.String)} in order to find an appropriate URL.
     * {@link java.net.URL#openStream()} is then called to obtain a stream, which is then
     * wrapped in a Reader.
     *
     * @param path The path representing the config location.
     * @return An input stream to the requested config resource.
     * @throws HibernateException Unable to open reader to that resource.
     */
    public static final Reader getConfigStreamReader(final String path) throws WellException {
        return new InputStreamReader(getConfigStream(path));
    }

    /**
     * @param path
     * @return
     * @throws WellException
     * @Title: getConfigProperties
     * @Description: TODO(这里用一句话描述这个方法的作用)
     */
    public static final Properties getConfigProperties(String path) throws WellException {
        try {
            Properties properties = new Properties();
            properties.load(getConfigStream(path));
            return properties;
        } catch (IOException e) {
            throw new WellException("Unable to load properties from specified config file: " + path, e);
        }
    }

    public static InputStream getResourceAsStream(String resource) throws WellException {
        String stripped = resource.startsWith("/") ? resource.substring(1) : resource;

        InputStream stream = null;
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        if (classLoader != null) {
            stream = classLoader.getResourceAsStream(stripped);
        }
        if (stream == null) {
            stream = Environment.class.getResourceAsStream(resource);
        }
        if (stream == null) {
            stream = Environment.class.getClassLoader().getResourceAsStream(stripped);
        }
        if (stream == null) {
            throw new WellException(resource + " not found");
        }
        return stream;
    }

    public static InputStream getUserResourceAsStream(String resource) throws WellException {
        boolean hasLeadingSlash = resource.startsWith("/");
        String stripped = hasLeadingSlash ? resource.substring(1) : resource;

        InputStream stream = null;

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        if (classLoader != null) {
            stream = classLoader.getResourceAsStream(resource);
            if (stream == null && hasLeadingSlash) {
                stream = classLoader.getResourceAsStream(stripped);
            }
        }

        if (stream == null) {
            stream = Environment.class.getClassLoader().getResourceAsStream(resource);
        }
        if (stream == null && hasLeadingSlash) {
            stream = Environment.class.getClassLoader().getResourceAsStream(stripped);
        }

        if (stream == null) {
            throw new WellException(resource + " not found");
        }

        return stream;
    }

}
