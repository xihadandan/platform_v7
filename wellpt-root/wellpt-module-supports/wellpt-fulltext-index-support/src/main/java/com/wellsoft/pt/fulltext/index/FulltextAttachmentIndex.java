package com.wellsoft.pt.fulltext.index;

import com.wellsoft.pt.fulltext.annotation.Pipeline;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.io.Serializable;

/**
 * Description:
 * es全文检索实现
 * <pre>
 * 修改记录:
 * 修改后版本    修改人     修改日期    修改内容
 * 1.0         baozh    2021/9/13   Create
 * </pre>
 */
@Document(indexName = "attachment_document")
public class FulltextAttachmentIndex extends FulltextDocumentIndex implements Serializable {

    @Field(type = FieldType.Keyword, index = false, store = true)
    private String contentType;

    @Pipeline
    @Field(name = "attachment.content", store = true, type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_smart")
    private String content;

    /**
     * @return the contentType
     */
    public String getContentType() {
        return contentType;
    }

    /**
     * @param contentType 要设置的contentType
     */
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    /**
     * @return the content
     */
    @Override
    public String getContent() {
        return content;
    }

    /**
     * @param content 要设置的content
     */
    @Override
    public void setContent(String content) {
        this.content = content;
    }
    
}
