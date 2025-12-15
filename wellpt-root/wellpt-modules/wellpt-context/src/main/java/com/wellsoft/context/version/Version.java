/*
 * @(#)2016-09-09 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.context.version;

import com.wellsoft.context.config.SystemParamsUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Properties;

/**
 * Description: 版本类，GNU 风格版本号
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016-09-09.1	zhulh		2016-09-09		Create
 * </pre>
 * @date 2016-09-09
 */
public final class Version {
    static Logger logger = Logger.getLogger(Version.class);
    private static String version;
    private static String runtimeVersion;
    private static String name;

    // private static final String VERSION_BASE =
    // "/com/wellsoft/context/version/";
    private static String fullVersion;

    static {

        loadProperties();
    }

    private Version() {
        // utility class - never constructed
    }

    private static InputStream getResourceAsStream(String resource) {
        ClassLoader cl = Version.class.getClassLoader();
        InputStream ins = cl.getResourceAsStream(resource);
        if (ins == null && resource.startsWith("/")) {
            ins = cl.getResourceAsStream(resource.substring(1));
        }
        return ins;
    }

    private static synchronized void loadProperties() {
        if (version == null) {
            Properties p = new Properties();
            InputStream ins = null;
            try {
                ins = getResourceAsStream("/system-about-pt.properties");
                if (ins == null) {
                    String errorMsg = "无法找到平台版本信息配置文件[system-about-pt.properties], 该文件的说明可通过wiki查看【http://wiki.well-soft.com:81/pages/viewpage.action?pageId=2719787】";
                    Exception e = new FileNotFoundException(errorMsg);
                    logger.error(e.getMessage(), e);
                    return;
                }
                p.load(ins);
                ins.close();
            } catch (IOException ex) {
                logger.error(ex.getMessage(), ex);
            } finally {
                if (ins != null) {
                    IOUtils.closeQuietly(ins);
                }
            }

            version = p.getProperty("pt.version", "5.3");
            String buildNumberFormat = SystemParamsUtils.getValue("pt.version.build.number.format");
            if (StringUtils.isBlank(buildNumberFormat)) {
                buildNumberFormat = p.getProperty("pt.version.build.number.format", "yyMMdd");
            }
            runtimeVersion = version
                    + "."
                    + com.wellsoft.context.util.date.DateUtils.format(Calendar.getInstance().getTime(),
                    buildNumberFormat);
            name = p.getProperty("pt.name", "Well-Soft Platform");
            fullVersion = name + " " + version;
        }
    }

    public static String getCurrentVersion() {
        loadProperties();
        return version;
    }

    public static String getRuntimeVersion() {
        loadProperties();
        return runtimeVersion;
    }

    public static String getName() {
        loadProperties();
        return name;
    }

    public static String getCompleteVersionString() {
        loadProperties();
        return fullVersion;
    }

}
