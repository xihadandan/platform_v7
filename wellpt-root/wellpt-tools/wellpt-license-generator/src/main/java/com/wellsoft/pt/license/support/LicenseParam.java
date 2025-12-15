/*
 * @(#)2019年5月24日 V1.0
 * 
 * Copyright 2019 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.license.support;

import java.util.prefs.Preferences;

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
public class LicenseParam {

	private String subject;
	private Preferences preferences;
	private KeyStoreParam keyStoreParam;

	/**
	 * @param subject
	 * @param preferences
	 * @param keyStoreParam
	 */
	public LicenseParam(String subject, Preferences preferences, KeyStoreParam keyStoreParam) {
		this.subject = subject;
		this.preferences = preferences;
		this.keyStoreParam = keyStoreParam;
	}

	/**
	 * @return the subject
	 */
	public String getSubject() {
		return subject;
	}

	/**
	 * @param subject 要设置的subject
	 */
	public void setSubject(String subject) {
		this.subject = subject;
	}

	/**
	 * @return the preferences
	 */
	public Preferences getPreferences() {
		return preferences;
	}

	/**
	 * @param preferences 要设置的preferences
	 */
	public void setPreferences(Preferences preferences) {
		this.preferences = preferences;
	}

	/**
	 * @return the keyStoreParam
	 */
	public KeyStoreParam getKeyStoreParam() {
		return keyStoreParam;
	}

	/**
	 * @param keyStoreParam 要设置的keyStoreParam
	 */
	public void setKeyStoreParam(KeyStoreParam keyStoreParam) {
		this.keyStoreParam = keyStoreParam;
	}

}
