package com.wellsoft.context.config;

import com.google.common.base.Throwables;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import java.io.File;
import java.io.InputStream;
import java.util.*;

/**
 * @author lilin
 * @ClassName: Config
 * @Description: 整个系统的配置文件处理类
 */
public class Config {
    public static final String CLASS_DIR = getClassDir();
    public static final String APP_DIR = getAppDir();
    public static final String TMP_DIR = getTempDir();
    public static final String NULL_DEVICE = getNullDevice();
    public static final boolean IN_SERVER = inServer();
    public static final String SYSTEM_CONFIG = "system.properties";
    public static final String SYSTEM_JDBC_CONFIG = "system-jdbc.properties";
    public static final String CUSTOM_CONFIG_PATTERN = "system-*.properties";
    public static final String CUSTOM_CONFIG_DIR_IN_JAR = "config";
    public static final String REPOSITORY_CONFIG = "repository.xml";
    public static final String CUSTOMNODE_CONFIG = "customnode.cnd";
    public static final String APP_DATA_DIR;
    // 上传文件目录
    // public static final String PROPERTY_UPLOAD_DIR = "upload.dir";
    public static final String UPLOAD_DIR;
    public static final String TRUE = "true";
    public static final String FALSE = "false";
    public static final boolean MULTI_TENANCY;
    public static final String KEY_MULTI_TENANCY_STRATEGY = "multi.tenancy.strategy";
    public static final String MULTI_TENANCY_STRATEGY_SESSION_FACTORY = "SessionFactory";
    // 公共租户
    public static final String KEY_COMMON_TENANT = "multi.tenancy.common.datasource";
    public static final String COMMON_TENANT;
    // 默认租户
    public static final String KEY_DEFAULT_TENANT = "multi.tenancy.tenant.default";
    public static final String DEFAULT_TENANT;
    public static final String DEFAULT_SESSION_FACTORY_BEAN_NAME = "sessionFactory";
    public static final String KEY_COMMON_SESSION_FACTORY_BEAN_NAME = "multi.tenancy.common.session_factory.bean_name";
    public static final String COMMON_SESSION_FACTORY_BEAN_NAME;
    public static final String KEY_TENANT_SESSION_FACTORY_BEAN_NAME = "multi.tenancy.tenant.session_factory.bean_name";
    // SQLServer2008数据库初始库脚本目录
    public static final String SQL_SERVER_2008_INITIAL_SCRIPT_DIR;
    // SQLServer2008数据库生成存入的租户库数据目录
    public static final String KEY_SQL_SERVER_2008_TENANT_DATA_DIR = "database.SQLServer2008.tenant_data_dir";
    public static final String SQL_SERVER_2008_TENANT_DATA_DIR;
    // 附加数据库执行的osql bat命令文件目录
    public static final String KEY_SQL_SERVER_2008_OSQL_BAT_FILE = "database.SQLServer2008.osql_bat_file";
    public static final String SQL_SERVER_2008_OSQL_BAT_FILE;
    public static final String TENANT_SESSION_FACTORY_BEAN_NAME;
    public static final String KEY_JCR_REP_HOME = "jcr.rep.home";
    public static final String JCR_REP_HOME;
    // 平台基本包路径
    public static final String KEY_BASE_PACKAGES = "base.package";
    public static final String BASE_PACKAGES;
    // 应用版本
    public static final String KEY_APP_VERSION = "app.version";
    public static final String APP_VERSION;
    // 应用环境(dev开发、test测试、uat预生产、prd生产)
    public static final String KEY_APP_ENV = "spring.profiles.active";
    //public static final String APP_ENV = getAppEnv();
    public static final String ENV_DEV = "dev";
    public static final String ENV_TEST = "test";
    public static final String ENV_UAT = "uat";
    public static final String ENV_PRD = "prd";
    //获取数据库类型
    public static final String DATABASE_TYP_PROPERTIES = "database.type";
    public static final String DATABASE_TYPE;
    public static final String SESSION_TIMEOUT = "session.timeout";
    public static final String FILE_MAX_UPLOAD_SIZE = "file.max.upload.size";
    public static final String CAS_ENABLE = "security.cas.enable";
    public static final String SESSION_ID_KEY = "session.id.key";
    // 应用数据统一目录，若没有设置使用应用目录+AppData作为默认的应用数据统一目录
    private static final String KEY_APP_DATA_DIR = "app.data.dir";
    // 是否运行在多租户环境下
    private static final String KEY_MULTI_TENANCY = "multi.tenancy.enable";
    private static final Logger logger = LoggerFactory.getLogger(Config.class);
    // Default directories
    public static final String HOME_DIR = getHomeDir();
    private static final TreeMap<String, String> values = new TreeMap<String, String>();
    private static final String SNF_DCID = "snowflake.datacenter.id"; // 雪花算法的数据中心ID
    private static final String SNF_MID = "snowflake.machine.id"; // 雪花算法的机器ID

    static {
        load();
        APP_DATA_DIR = getAppDataDir();
        UPLOAD_DIR = getUploadDir();
        MULTI_TENANCY = isMultiTenancy();
        COMMON_TENANT = getCommonTenant();
        DEFAULT_TENANT = getDefaultTenant();
        COMMON_SESSION_FACTORY_BEAN_NAME = getCommonSessionFactoryBeanName();
        SQL_SERVER_2008_INITIAL_SCRIPT_DIR = getSQLServer2008InitialScriptDir();
        SQL_SERVER_2008_TENANT_DATA_DIR = getSQLServer2008TenantDataDir();
        SQL_SERVER_2008_OSQL_BAT_FILE = getSQLServer2008OsqlBatFile();
        TENANT_SESSION_FACTORY_BEAN_NAME = getTenantSessionFactoryBeanName();
        JCR_REP_HOME = getJcrRepHome();
        BASE_PACKAGES = getBasePackage();
        APP_VERSION = getAppVersion();
        DATABASE_TYPE = getDatabaseType();
    }


    public static long getFileMaxUploadSize() {
        String fileMaxUploadSize = values.get(FILE_MAX_UPLOAD_SIZE);
        if (StringUtils.isBlank(fileMaxUploadSize)) {
            fileMaxUploadSize = "2048";
        }
        long maxUploadSize = Long.parseLong(fileMaxUploadSize) * 1024 * 1024;
        return maxUploadSize;
    }

    private static String getDatabaseType() {
        return values.get(DATABASE_TYP_PROPERTIES) == null ? "" : values.get(DATABASE_TYP_PROPERTIES);
    }

    /**
     * @return
     */
    private static String getBasePackage() {
        return values.get(KEY_BASE_PACKAGES) == null ? "" : values.get(KEY_BASE_PACKAGES);
    }

    /**
     * @return
     */
    private static String getAppVersion() {
        return values.get(KEY_APP_VERSION) == null ? "" : values.get(KEY_APP_VERSION);
    }

    /**
     * 返回session超时时间（单位：秒），无配置则默认4小时
     *
     * @return
     */
    public static int getSessionTimeout() {
        return values.get(SESSION_TIMEOUT) == null ? 14400 : Integer.parseInt(values.get(SESSION_TIMEOUT));
    }

    public static boolean getCasEnable() {
        return "1".equals(values.get(CAS_ENABLE));
    }

    public static boolean isBackendServer() {
        return "backend_server".equals(values.get("wellpt.application.type"));
    }

    public static boolean getSecurityJwtEnable() {
        return "1".equals(values.get("security.jwt.enable"));
    }

    /**
     * @return
     */
    public static String getAppEnv() {
        String appEnv = getValue(KEY_APP_ENV);
        if (StringUtils.isBlank(appEnv)) {
            appEnv = System.getProperty(KEY_APP_ENV);
        }
        if (StringUtils.isBlank(appEnv)) {
            appEnv = ENV_PRD;
        }
        return appEnv;
    }

    /**
     * @return
     */
    private static String getAppDataDir() {
        String appDataDir = values.get(KEY_APP_DATA_DIR);
        if (StringUtils.isBlank(appDataDir)) {
            appDataDir = APP_DIR + "/" + "AppData";
        }
        if (appDataDir.endsWith("/") || appDataDir.endsWith("\\")) {
            appDataDir = appDataDir.substring(0, appDataDir.length() - 1);
        }
        return appDataDir;
    }

    public static long getSnfDcid() {
        if (values.containsKey(SNF_DCID) && NumberUtils.isDigits(values.get(SNF_DCID))) {
            return Long.parseLong(values.get(SNF_DCID));
        }
        return 0L;
    }

    public static long getSnfMid() {
        if (values.containsKey(SNF_MID) && NumberUtils.isDigits(values.get(SNF_MID))) {
            return Long.parseLong(values.get(SNF_MID));
        }
        return 0L;
    }

    /**
     * @return
     */
    private static String getUploadDir() {
        return getAppDataDir() + "/" + "upload";
    }

    private static boolean isMultiTenancy() {
        String multiTenancy = values.get(KEY_MULTI_TENANCY);
        return Boolean.valueOf((multiTenancy != null) && multiTenancy.equalsIgnoreCase("true") ? values.get(
                KEY_MULTI_TENANCY).toLowerCase() : "false");
    }

    /**
     * @return
     */
    private static String getSQLServer2008InitialScriptDir() {
        return APP_DATA_DIR + "/script";
    }

    /**
     * @return
     */
    private static String getSQLServer2008TenantDataDir() {
        return values.get(KEY_SQL_SERVER_2008_TENANT_DATA_DIR);
    }

    /**
     * @return
     */
    private static String getSQLServer2008OsqlBatFile() {
        return values.get(KEY_SQL_SERVER_2008_OSQL_BAT_FILE);
    }

    /**
     * @return
     */
    private static String getJcrRepHome() {
        String repHome = values.get(KEY_JCR_REP_HOME);
        return repHome != null ? repHome : APP_DIR + File.separator + "jcr";
    }

    /**
     * @return
     */
    private static String getCommonTenant() {
        String commonTenant = values.get(KEY_COMMON_TENANT);
        return commonTenant == null ? "" : commonTenant;
    }

    /**
     * @return
     */
    private static String getDefaultTenant() {
        String defaultTenant = values.get(KEY_DEFAULT_TENANT);
        return defaultTenant == null ? "" : defaultTenant;
    }

    public static String getSessionId() {
        return values.get(SESSION_ID_KEY);
    }

    /**
     * @return
     */
    private static String getCommonSessionFactoryBeanName() {
        boolean multiTenancy = isMultiTenancy();
        if (multiTenancy) {
            return values.get(KEY_COMMON_SESSION_FACTORY_BEAN_NAME) == null ? getTenantSessionFactoryBeanName()
                    : values.get(KEY_COMMON_SESSION_FACTORY_BEAN_NAME);
        }
        // 单租户下，返回租户的sesssionFactory beanName
        return getTenantSessionFactoryBeanName();
    }

    /**
     * @return
     */
    private static String getTenantSessionFactoryBeanName() {
        return values.get(KEY_TENANT_SESSION_FACTORY_BEAN_NAME) == null ? DEFAULT_SESSION_FACTORY_BEAN_NAME : values
                .get(KEY_TENANT_SESSION_FACTORY_BEAN_NAME);
    }

    /**
     * Guess the application server home directory
     */
    private static String getHomeDir() {
        // Try JBoss
        String dir = System.getProperty("jboss.home.dir");
        if (dir != null) {
            logger.info("Using JBoss: " + dir);
            return dir;
        }

        // Try Tomcat
        dir = System.getProperty("catalina.home");
        if (dir != null) {
            logger.info("Using Tomcat: " + dir);
            return dir;
        }

        // Otherwise GWT hosted mode
        dir = System.getProperty("user.dir") + "/src/test/resources";
        logger.info("Using default dir: " + dir);
        return dir;
    }

    private static String getClassDir() {
        String path = Thread.currentThread().getContextClassLoader().getResource("").getPath();
        // 去掉最后一个"/"
        path = path.substring(0, path.length() - 1);
        return path;
    }

    private static String getAppDir() {
        String path = Thread.currentThread().getContextClassLoader().getResource("").getPath();
        if (path.indexOf("/WEB-INF/classes") >= 0) {
            path = path.substring(0, path.indexOf("/WEB-INF/classes"));
        }
        return path;
    }

    /**
     * Guess the system wide temporary directory
     */
    private static String getTempDir() {
        String dir = System.getProperty("java.io.tmpdir");
        if (dir != null) {
            return dir;
        } else {
            return "";
        }
    }

    /**
     * Guess the system null device
     */
    private static String getNullDevice() {
        String os = System.getProperty("os.name").toLowerCase();

        if (os.contains("linux") || os.contains("mac os")) {
            return "/dev/null";
        } else if (os.contains("windows")) {
            return "NUL:";
        } else {
            return null;
        }
    }

    /**
     * Test if is running in application server
     */
    private static boolean inServer() {
        return System.getProperty("jboss.home.dir") != null || System.getProperty("catalina.home") != null;
    }

    // @PostConstruct
    private static void load() {
        loadDefaultConfiguration();
    }

    /**
     *
     */
    private static void loadDefaultConfiguration() {
        logger.info("开始读取properties配置文件");
        Properties config = new Properties();
        //web工程编译目录下资源配置文件
        String webContextSystemProperties = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX + "system*.properties";
        //jar包内的资源配置文件
        String jarContextSystemProperties = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX
                + Config.CUSTOM_CONFIG_DIR_IN_JAR + "/system*.properties";
        String[] resourcePaths = new String[]{webContextSystemProperties, jarContextSystemProperties};
        ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
        Map<String, Boolean> filter = Maps.newHashMap();
        Set<String> filters = Sets.newHashSet();
        try {
            for (String resourcePath : resourcePaths) {
                Resource[] resources = resourcePatternResolver.getResources(resourcePath);
                if (ArrayUtils.isNotEmpty(resources)) {
                    for (Resource res : resources) {
                        InputStream in = res.getInputStream();
                        if (!filters.add(res.getFilename())) {
                            continue;
                        }
                        logger.info("读取properties配置文件：[{}]", res.getURL());
                        config.load(in);
                        IOUtils.closeQuietly(in);
                        Enumeration<?> keys = config.keys();
                        while (keys.hasMoreElements()) {
                            String key = keys.nextElement().toString();
                            values.put(key, config.getProperty(key));
                        }

                    }
                }
            }

        } catch (Exception e) {
            logger.error("读取配置文件异常：{}", Throwables.getStackTraceAsString(e));
            throw new RuntimeException(e);
        } finally {
            logger.info("结束读取properties配置文件");
        }
    }

    /**
     *
     */
    public static void loadCustomConfiguration() {
        loadDefaultConfiguration();
    }

    public static String getValue(String key) {
        return values.get(key);
    }

    public static void setValue(String key, String value) {
        values.put(key, value);
    }

    public static String getValue(String key, String defaultValue) {
        String value = values.get(key);
        return value != null ? value : defaultValue;
    }

    public static Set<String> keySet() {
        return values.keySet();
    }


}
