/*
 * @(#)Jan 22, 2017 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.context;

import com.wellsoft.context.config.Config;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.app.context.support.OrderedProperties;
import com.wellsoft.pt.app.support.AppCacheUtils;
import com.wellsoft.pt.app.support.AppConstants;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternUtils;
import org.springframework.util.ResourceUtils;
import org.springframework.util.StringUtils;

import java.io.File;
import java.util.Enumeration;
import java.util.Properties;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * Jan 22, 2017.1	zhulh		Jan 22, 2017		Create
 * </pre>
 * @date Jan 22, 2017
 */
public class AppContextPropertiesConfigurationSupport {

    private static final String MIN_EXTENSION = "min";

    private AppContextConfigurer appContextConfigurer;
    private String propertiesFile;
    private Properties properties;
    private boolean isJarPropertiesFile;

    /**
     * @param appContextConfigurer
     * @param propertiesFile
     * @param orderedProperties
     */
    public AppContextPropertiesConfigurationSupport(AppContextConfigurer appContextConfigurer, String propertiesFile,
                                                    OrderedProperties orderedProperties) {
        this.appContextConfigurer = appContextConfigurer;
        this.propertiesFile = propertiesFile;
        this.properties = orderedProperties;
    }

    /**
     * @return the appContextConfigurer
     */
    public AppContextConfigurer getAppContextConfigurer() {
        return appContextConfigurer;
    }

    /**
     * @return the propertiesFile
     */
    public String getPropertiesFile() {
        return propertiesFile;
    }

    /**
     * @return the properties
     */
    public Properties getProperties() {
        return properties;
    }

    /**
     * @return the isJarPropertiesFile
     */
    public boolean isJarPropertiesFile() {
        return isJarPropertiesFile;
    }

    /**
     * @param isJarPropertiesFile 要设置的isJarPropertiesFile
     */
    public void setJarPropertiesFile(boolean isJarPropertiesFile) {
        this.isJarPropertiesFile = isJarPropertiesFile;
    }

    /**
     * @return
     */
    public Enumeration<?> keys() {
        return properties.keys();
    }

    /**
     * @param key
     * @return
     */
    public String getProperty(String key) {
        return properties.getProperty(key);
    }

    /**
     * @param string
     * @return
     */
    public String createRelative(String relativePath, String extension) {
        if (StringUtils.isEmpty(relativePath)) {
            return relativePath;
        }
        if (relativePath.startsWith("/")) {
            return getFileUglifyPath(relativePath, extension);
        }
        String pathToUse = getPropertiesFileRelative();
        return StringUtils.applyRelativePath(pathToUse, getPackageUglifyPath(relativePath, extension));
    }

    /**
     * 根据当前环境(开发、测试、预生产、生产)，返回文件系统中是否压缩的路径
     *
     * @param relativePath
     * @param extension
     * @return
     */
    private String getFileUglifyPath(String relativePath, String extension) {
        // 开发环境直接返回
        if (Config.getAppEnv().equalsIgnoreCase(Config.ENV_DEV)) {
            return relativePath;
        }
        if (relativePath.endsWith(MIN_EXTENSION)) {
            return relativePath;
        }

        String separator = FilenameUtils.EXTENSION_SEPARATOR_STR;
        // 文件系统中的资源目录
        String fileRelativePath = relativePath + separator + MIN_EXTENSION + separator + extension;
        File minFile = new File(Config.APP_DIR, fileRelativePath);
        if (minFile.exists()) {
            return relativePath + separator + MIN_EXTENSION;
        }
        // jar包中的资源目录
        fileRelativePath = "/META-INF/resources" + fileRelativePath;
        ApplicationContext applicationContext = ApplicationContextHolder.getApplicationContext();
        ResourcePatternResolver resourcePatternResolver = ResourcePatternUtils
                .getResourcePatternResolver(applicationContext);
        Resource resource = resourcePatternResolver.getResource(ResourceUtils.CLASSPATH_URL_PREFIX + fileRelativePath);
        if (resource != null && resource.exists()) {
            return relativePath + separator + MIN_EXTENSION;
        }

        return relativePath;
    }

    /**
     * 根据当前环境(开发、测试、预生产、生产)，返回jar包中是否压缩的路径
     *
     * @param relativePath
     * @param extension
     * @return
     */
    private String getPackageUglifyPath(String relativePath, String extension) {
        // 开发环境直接返回
        if (Config.getAppEnv().equalsIgnoreCase(Config.ENV_DEV)) {
            return relativePath;
        }
        String packageName = getAppContextConfigurer().getClass().getPackage().getName();
        String packagePath = StringUtils.replace(packageName, ".", "/");
        String propertiesFileRelative = StringUtils.applyRelativePath(packagePath + "/", propertiesFile);
        String relativeFileRelative = StringUtils.applyRelativePath(propertiesFileRelative, relativePath);
        String separator = FilenameUtils.EXTENSION_SEPARATOR_STR;
        String jarFilePath = relativeFileRelative + separator + MIN_EXTENSION + separator + extension;
        ApplicationContext applicationContext = ApplicationContextHolder.getApplicationContext();
        ResourcePatternResolver resourcePatternResolver = ResourcePatternUtils
                .getResourcePatternResolver(applicationContext);
        Resource resource = resourcePatternResolver.getResource(ResourceUtils.CLASSPATH_URL_PREFIX + jarFilePath);
        if (resource != null && resource.exists()) {
            return relativePath + separator + MIN_EXTENSION;
        }
        return relativePath;
    }

    /**
     * @return
     */
    private String getPropertiesFileRelative() {
        String packageName = getAppContextConfigurer().getClass().getPackage().getName();
        String packagePath = StringUtils.replace(packageName, ".", "/");
        String packagePathMd5 = DigestUtils.md5Hex(packagePath);
        AppCacheUtils.addPackagePath(packagePathMd5, packagePath);
        String webJarsPackagePath = StringUtils
                .applyRelativePath(AppConstants.WEB_RES_PATH + "/", packagePathMd5 + "/");
        String propertiesFileRelative = StringUtils.applyRelativePath(webJarsPackagePath, propertiesFile);
        return propertiesFileRelative;
    }

}
