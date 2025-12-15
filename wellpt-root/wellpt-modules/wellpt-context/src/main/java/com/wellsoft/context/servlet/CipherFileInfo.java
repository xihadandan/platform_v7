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
public class CipherFileInfo extends BaseObject {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 5848001200111310178L;

    private String dataFileName;
    private String indexName;
    private int offset;
    private int lengh;

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
     * @return the indexName
     */
    public String getIndexName() {
        return indexName;
    }

    /**
     * @param indexName 要设置的indexName
     */
    public void setIndexName(String indexName) {
        this.indexName = indexName;
    }

    /**
     * @return the offset
     */
    public int getOffset() {
        return offset;
    }

    /**
     * @param offset 要设置的offset
     */
    public void setOffset(int offset) {
        this.offset = offset;
    }

    /**
     * @return the lengh
     */
    public int getLengh() {
        return lengh;
    }

    /**
     * @param lengh 要设置的lengh
     */
    public void setLengh(int lengh) {
        this.lengh = lengh;
    }

}