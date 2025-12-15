package com.wellsoft.pt.fulltext.index;

/**
 * es6 升级 es7
 *
 * @author baozh
 * import com.wellsoft.pt.fulltext.annotation.Keyword;
 * import com.wellsoft.pt.fulltext.annotation.FilterTime;
 * import org.springframework.data.annotation.Id;
 * import org.springframework.data.elasticsearch.annotations.Field;
 * import org.springframework.data.elasticsearch.annotations.FieldIndex;
 * import org.springframework.data.elasticsearch.annotations.FieldType;
 * import java.util.Date;
 */

import java.io.Serializable;


/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2020年09月08日   chenq	 Create
 * </pre>
 */
public class BaseDocumentIndex extends FulltextDocumentIndex implements Serializable {
    private static final long serialVersionUID = 4959483435535632043L;

    /**
     * es6 升级  es7
     *
     * @author baozh
     @Id
     @Field(type = FieldType.String, index = FieldIndex.not_analyzed)
     protected String uuid;

     // 标题
     @Keyword
     @Field(type = FieldType.String, analyzer = "ik_max_word", searchAnalyzer = "ik_smart")
     protected String title;

     // 文档内容
     @Keyword
     @Field(type = FieldType.String, analyzer = "ik_max_word", searchAnalyzer = "ik_smart")
     protected String content;

     // 文档地址
     @Field(type = FieldType.String, index = FieldIndex.not_analyzed)
     protected String url;


     @Field(type = FieldType.String, analyzer = "ik_max_word", searchAnalyzer = "ik_smart")
     protected String creator;

     @FilterTime
     @Field(type = FieldType.Date)
     protected Date createTime;

     @FilterTime
     @Field(type = FieldType.Date)
     protected Date modifyTime;

     @Field(type = FieldType.String, analyzer = "ik_max_word", searchAnalyzer = "ik_smart")
     protected String modifier;


     public String getTitle() {
     return title;
     }

     public void setTitle(String title) {
     this.title = title;
     }

     public String getContent() {
     return content;
     }

     public void setContent(String content) {
     this.content = content;
     }

     public String getUrl() {
     return url;
     }

     public void setUrl(String url) {
     this.url = url;
     }

     public String getCreator() {
     return creator;
     }

     public void setCreator(String creator) {
     this.creator = creator;
     }

     public Date getCreateTime() {
     return createTime;
     }

     public void setCreateTime(Date createTime) {
     this.createTime = createTime;
     }

     public Date getModifyTime() {
     return modifyTime;
     }

     public void setModifyTime(Date modifyTime) {
     this.modifyTime = modifyTime;
     }

     public String getModifier() {
     return modifier;
     }

     public void setModifier(String modifier) {
     this.modifier = modifier;
     }

     public String getUuid() {
     return uuid;
     }

     public void setUuid(String uuid) {
     this.uuid = uuid;
     }**/
}
