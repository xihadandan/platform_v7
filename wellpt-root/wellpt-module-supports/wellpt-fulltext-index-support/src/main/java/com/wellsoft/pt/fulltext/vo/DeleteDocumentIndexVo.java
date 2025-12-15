package com.wellsoft.pt.fulltext.vo;

/**
 * Description:
 * es全文检索实现
 * <pre>
 * 修改记录:
 * 修改后版本    修改人     修改日期    修改内容
 * 1.0         baozh    2021/9/14   Create
 * </pre>
 */
public class DeleteDocumentIndexVo {

    private String uuid;

    private String indexName;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
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
}
