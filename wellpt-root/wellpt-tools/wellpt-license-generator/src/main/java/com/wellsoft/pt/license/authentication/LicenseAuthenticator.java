/*
 * @(#)2019年5月13日 V1.0
 * 
 * Copyright 2019 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.license.authentication;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.prefs.Preferences;

import com.wellsoft.pt.license.support.DefaultLicenseManager;
import com.wellsoft.pt.license.support.KeyStoreParam;
import com.wellsoft.pt.license.support.LicenseManager;
import com.wellsoft.pt.license.support.LicenseParam;

/**
 * Description: 许可证安装验证器
 *  
 * @author zhulh
 * @date 2019年5月13日
 * @version 1.0
 * 
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2019年5月13日.1	zhulh		2019年5月13日		Create
 * </pre>
 *
 */
public class LicenseAuthenticator {

	// 项目的唯一识别码，例如机器码，从平台的系统设置->许可管理页面中获取
	private String subject;

	// 公钥密钥库路径
	private String publicKeyStorePath;
	// 公钥密钥库密码
	private String keyStorePwd;
	// 公钥证书别名
	private String publicKeyAlias;

	/**
	 * @param subject
	 * @param configFilePath
	 */
	public LicenseAuthenticator(String subject, String configFilePath) {
		this.subject = subject;
		initConfigFileInfo(configFilePath);
	}

	/**
	 * 通过外部配置文件获取配置信息
	 * 
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

		publicKeyStorePath = prop.getProperty("public.key.store.path");
		publicKeyAlias = prop.getProperty("public.key.alias");
		keyStorePwd = prop.getProperty("public.key.store.password");
	}

	/**
	 * 初始化证书的相关参数
	 * 
	 * @return
	 */
	private LicenseParam initLicenseParams() {
		Class<LicenseAuthenticator> clazz = LicenseAuthenticator.class;
		Preferences pre = Preferences.userNodeForPackage(clazz);
		KeyStoreParam pubStoreParam = new KeyStoreParam(clazz, publicKeyStorePath, publicKeyAlias, keyStorePwd, null);
		LicenseParam licenseParam = new LicenseParam(subject, pre, pubStoreParam);
		return licenseParam;
	}

	/**
	 * 安装许可证
	 * 
	 * @param licenseFile
	 * @return
	 */
	public boolean install(File licenseFile) {
		boolean result = false;
		try {
			LicenseManager licenseManager = new DefaultLicenseManager(initLicenseParams());
			licenseManager.install(licenseFile);
			result = true;
			System.out.println("许可证安装成功!");
		} catch (Exception e) {
			System.out.println("许可证安装失败!");
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 验证许可证
	 * 
	 * @return
	 */
	public boolean authenticate() {
		boolean result = false;
		LicenseManager licenseManager = new DefaultLicenseManager(initLicenseParams());
		try {
			licenseManager.verify();
			result = true;
			System.out.println("许可证验证成功!");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

}
