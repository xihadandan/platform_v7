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
public class LicenseCertificate {

	// AES加密密钥
	private byte[] keyEncoded;

	// AES加密的数据
	private byte[] cipherData;

	// 证书签名算法base64数据
	private byte[] signatureAlgorithmData;

	// 签名数据
	private byte[] signedData;

	/**
	 * @return the keyEncoded
	 */
	public byte[] getKeyEncoded() {
		return keyEncoded;
	}

	/**
	 * @param keyEncoded 要设置的keyEncoded
	 */
	public void setKeyEncoded(byte[] keyEncoded) {
		this.keyEncoded = keyEncoded;
	}

	/**
	 * @return the cipherData
	 */
	public byte[] getCipherData() {
		return cipherData;
	}

	/**
	 * @param cipherData 要设置的cipherData
	 */
	public void setCipherData(byte[] cipherData) {
		this.cipherData = cipherData;
	}

	/**
	 * @return the signatureAlgorithmData
	 */
	public byte[] getSignatureAlgorithmData() {
		return signatureAlgorithmData;
	}

	/**
	 * @param signatureAlgorithmData 要设置的signatureAlgorithmData
	 */
	public void setSignatureAlgorithmData(byte[] signatureAlgorithmData) {
		this.signatureAlgorithmData = signatureAlgorithmData;
	}

	/**
	 * @return the signedData
	 */
	public byte[] getSignedData() {
		return signedData;
	}

	/**
	 * @param signedData 要设置的signedData
	 */
	public void setSignedData(byte[] signedData) {
		this.signedData = signedData;
	}

}
