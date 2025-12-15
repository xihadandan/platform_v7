package org.bouncycastle2.pqc.crypto.xmss;

/**
 * Interface for XMSS objects that need to be storeable as a byte array.
 *
 */
public interface XMSSStoreableObjectInterface {

	/**
	 * Create byte representation of object.
	 *
	 * @return Byte representation of object.
	 */
	public byte[] toByteArray();
}
