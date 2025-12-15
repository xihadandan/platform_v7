/*
 * @(#)2019年5月13日 V1.0
 *
 * Copyright 2019 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.license.generator;

import com.wellsoft.pt.license.support.*;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Properties;
import java.util.UUID;
import java.util.prefs.Preferences;

/**
 * Description: 许可证生成器
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2019年5月13日.1	zhulh		2019年5月13日		Create
 * </pre>
 * @date 2019年5月13日
 */
public class LicenseGenerator {

    // 项目的唯一识别码，例如机器码，从平台的系统设置->许可管理页面中获取
    private String subject;

    // 证书生成配置信息
    // 密钥库路径
    private String privateKeyStorePath;
    // 密钥库密码
    private String privateKeyStorePassword;
    // 私钥别名
    private String privateKeyAlias;
    // 私钥密码
    private String privateKeyPassword;
    // 生成的许可证路径
    private String licensePath;

    // 证书内容
    private String issuedTime;
    private String notBefore;
    private String notAfter;
    private String consumerType;
    private int consumerAmount;
    private String info;
    private String character;
    // 开发版本dev、授权版本prd
    private String profile;

    /**
     *
     */
    public LicenseGenerator() {
        initConfigFileInfo("/license-generator.properties");
    }

    /**
     * @param subject
     * @param configFilePath
     */
    public LicenseGenerator(String subject, String configFilePath) {
        this.subject = subject;
        initConfigFileInfo(configFilePath);
    }

    /**
     * @param configFilePath
     */
    public void initConfigFileInfo(String configFilePath) {
        // 获取参数
        Properties prop = new Properties();
        InputStream in = getClass().getResourceAsStream(configFilePath);
        try {
            prop.load(in);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        // 证书生成配置信息
        privateKeyStorePath = prop.getProperty("private.key.store.path");
        privateKeyStorePassword = prop.getProperty("private.key.store.password");
        privateKeyAlias = prop.getProperty("private.key.alias");
        privateKeyPassword = prop.getProperty("private.key.password");
        licensePath = prop.getProperty("license.path");

        // 证书内容
        issuedTime = prop.getProperty("license.issuedTime");
        notBefore = prop.getProperty("license.notBefore");
        notAfter = prop.getProperty("license.notAfter");
        consumerType = prop.getProperty("license.consumerType");
        consumerAmount = Integer.valueOf(prop.getProperty("license.consumerAmount"));
        info = prop.getProperty("license.info");
        character = prop.getProperty("license.character", "");//国产化特性
        profile = prop.getProperty("license.profile", "dev");//开发版本
    }

    /**
     * 初始化证书的相关参数
     *
     * @return
     */
    private LicenseParam initLicenseParams() {
        Class<LicenseGenerator> clazz = LicenseGenerator.class;
        // 证书安装存储位置
        Preferences preferences = Preferences.userNodeForPackage(clazz);
        // 设置对证书内容加密的对称密码
        // CipherParam cipherParam = new DefaultCipherParam(privateKeyStorePassword);
        // 密钥库参数
        KeyStoreParam privateStoreParam = new KeyStoreParam(clazz, privateKeyStorePath, privateKeyAlias,
                privateKeyStorePassword, privateKeyPassword);
        // 返回生成证书时需要的参数
        LicenseParam licenseParam = new LicenseParam(subject, preferences, privateStoreParam);
        return licenseParam;
    }

    /**
     * 通过外部配置文件构建证书的的相关信息
     *
     * @return
     * @throws ParseException
     */
    private LicenseContent buildLicenseContent() throws ParseException {
        LicenseContent content = new LicenseContent();

        content.setConsumerAmount(consumerAmount);
        content.setConsumerType(consumerType);
        content.setIssued(parseDate(issuedTime));
        content.setNotBefore(parseDate(notBefore));
        content.setNotAfter(parseDate(notAfter));
        content.setSerialNumber(UUID.randomUUID().toString());
        content.setInfo(info);
        HashMap<String, Object> extra = new HashMap<>();
        extra.put("character", character);
        extra.put("profile", profile);
        content.setExtra(extra);
        return content;
    }

    /**
     * @param dateString
     * @return
     */
    private Date parseDate(String dateString) {
        SimpleDateFormat formate = new SimpleDateFormat("yyyy-MM-dd");
        try {
            if (dateString != null && !"".equals(dateString.trim())) {
                return formate.parse(dateString);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 生成许可证，在许可证发布者端执行
     *
     * @throws Exception
     */
    public File create() throws Exception {
        LicenseManager licenseManager = new DefaultLicenseManager(initLicenseParams());
        LicenseContent content = buildLicenseContent();
        File licenseFile = new File(licensePath);
        licenseManager.store(content, licenseFile);
        System.out.println("许可证生成成功：" + licenseFile.getAbsolutePath());
        return licenseFile;
    }

    /**
     * 生成许可证
     *
     * @param content
     * @return
     */
    public InputStream generate(LicenseContent content) {
        LicenseManager licenseManager = new DefaultLicenseManager(initLicenseParams());
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        licenseManager.store(content, output);
        System.out.println("许可证生成成功！");
        InputStream intput = new ByteArrayInputStream(output.toByteArray());
        IOUtils.closeQuietly(output);
        return intput;
    }

    public LicenseManager getLicenseManager() {
        return new DefaultLicenseManager(initLicenseParams());
    }
}
