package com.wellsoft.context.servlet;

import com.wellsoft.context.base.BaseObject;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2020年06月29日   chenq	 Create
 * </pre>
 */
public class SecretKeyInfo extends BaseObject {
    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = -3917929083398494868L;

    private String dataFileName;

    private byte[] encoded;

    /**
     * @return the dataFileName
     */
    public String getDataFileName() {
        return dataFileName;
    }

    /**
     * @param dataFileName 要设置的dataFileName
     */
    public void setDataFileName(String dataFileName) {
        this.dataFileName = dataFileName;
    }

    /**
     * @return the encoded
     */
    public byte[] getEncoded() {
        return encoded;
    }

    /**
     * @param encoded 要设置的encoded
     */
    public void setEncoded(byte[] encoded) {
        this.encoded = encoded;
    }

}

