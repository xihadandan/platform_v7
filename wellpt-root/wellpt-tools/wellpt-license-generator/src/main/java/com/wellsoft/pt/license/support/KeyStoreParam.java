/*
 * @(#)2019年5月24日 V1.0
 * 
 * Copyright 2019 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.license.support;

/**
 * Description: 如何描述该类
 *  
 * @author zhulh
 * @date 2019年5月24日
 * @version 1.0
 * 
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2019年5月24日.1	zhulh		2019年5月24日		Create
 * </pre>
 *
 */
public class KeyStoreParam {

	private Class<?> clazz;
	private String keyStorePath;
	private String keyAlias;
	private String keyStorePassword;
	private String keyPassword;

	/**
	 * @param clazz
	 * @param keyStorePath
	 * @param keyAlias
	 * @param keyStorePassword
	 * @param keyPassword
	 */
	public KeyStoreParam(Class<?> clazz, String keyStorePath, String keyAlias, String keyStorePassword,
			String keyPassword) {
		super();
		this.clazz = clazz;
		this.keyStorePath = keyStorePath;
		this.keyAlias = keyAlias;
		this.keyStorePassword = keyStorePassword;
		this.keyPassword = keyPassword;
	}

	/**
	 * @return the clazz
	 */
	public Class<?> getClazz() {
		return clazz;
	}

	/**
	 * @param clazz 要设置的clazz
	 */
	public void setClazz(Class<?> clazz) {
		this.clazz = clazz;
	}

	/**
	 * @return the keyStorePath
	 */
	public String getKeyStorePath() {
		return keyStorePath;
	}

	/**
	 * @param keyStorePath 要设置的keyStorePath
	 */
	public void setKeyStorePath(String keyStorePath) {
		this.keyStorePath = keyStorePath;
	}

	/**
	 * @return the keyAlias
	 */
	public String getKeyAlias() {
		return keyAlias;
	}

	/**
	 * @param keyAlias 要设置的keyAlias
	 */
	public void setKeyAlias(String keyAlias) {
		this.keyAlias = keyAlias;
	}

	/**
	 * @return the keyStorePassword
	 */
	public String getKeyStorePassword() {
		return keyStorePassword;
	}

	/**
	 * @param keyStorePassword 要设置的keyStorePassword
	 */
	public void setKeyStorePassword(String keyStorePassword) {
		this.keyStorePassword = keyStorePassword;
	}

	/**
	 * @return the keyPassword
	 */
	public String getKeyPassword() {
		return keyPassword;
	}

	/**
	 * @param keyPassword 要设置的keyPassword
	 */
	public void setKeyPassword(String keyPassword) {
		this.keyPassword = keyPassword;
	}

}
