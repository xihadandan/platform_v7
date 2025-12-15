package com.wellsoft.pt.fulltext.index;

import com.wellsoft.pt.fulltext.annotation.Pipeline;
import com.wellsoft.pt.fulltext.support.Attachment;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.TermVector;

import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Description:
 * es全文检索实现
 * <pre>
 * 修改记录:
 * 修改后版本    修改人     修改日期    修改内容
 * 1.0         baozh    2021/9/13   Create
 * </pre>
 */
public class FulltextDocumentIndex {

    @Id
    @Field(type = FieldType.Keyword, index = false, store = true)
    protected String uuid;

    // 标题
    @Field(type = FieldType.Text, store = true, analyzer = "ik_max_word", searchAnalyzer = "ik_smart", termVector = TermVector.with_positions_offsets)
    protected String title;

    // 摘要
    @Field(type = FieldType.Text, store = true, analyzer = "ik_max_word", searchAnalyzer = "ik_smart", termVector = TermVector.with_positions_offsets)
    protected String content;

    // 备注
    @Field(type = FieldType.Text, store = true)
    protected String remark;

    // 创建人
    @Field(type = FieldType.Text, store = true, analyzer = "ik_max_word", searchAnalyzer = "ik_smart", termVector = TermVector.with_positions_offsets)
    protected String creator;

    // 创建人ID
    @Field(type = FieldType.Keyword, store = true, index = false)
    protected String creatorId;

    // 创建时间
    @Field(type = FieldType.Date, store = true)
    protected Date createTime;

    // 修改人
    @Field(type = FieldType.Text, store = true, analyzer = "ik_max_word", searchAnalyzer = "ik_smart", termVector = TermVector.with_positions_offsets)
    protected String modifier;

    // 修改时间
    @Field(type = FieldType.Date, store = true)
    protected Date modifyTime;


    // 链接
    @Field(type = FieldType.Keyword, store = true, index = false)
    protected String url;

    // 附件名称
    @Field(type = FieldType.Text, store = true, analyzer = "ik_max_word", searchAnalyzer = "ik_smart", termVector = TermVector.with_positions_offsets)
    protected String fileNames;

//    @Field(type = FieldType.Keyword, store = true, index = false)
//    private String fileInfos;

    @Pipeline(value = "attachments")
    @Field(name = "attachments.attachment.content", store = true, type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_smart")
    protected List<Attachment> attachments;

    // 是否删除
    @Field(type = FieldType.Byte, store = true, index = false)
    protected Integer isDelete;

    // 系统ID
    @Field(type = FieldType.Keyword, store = true, index = false)
    protected String system;

    @Field(type = FieldType.Keyword, store = true, index = false)
    @NotBlank(message = "分类编码不能为空")
    protected Set<String> categoryCodes;

    @Field(type = FieldType.Keyword, store = true, index = false)
    protected Set<String> readers;

    @Field(type = FieldType.Integer, store = true, index = false)
    protected Integer indexOrder;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

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

    /**
     * @return the remark
     */
    public String getRemark() {
        return remark;
    }

    /**
     * @param remark 要设置的remark
     */
    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    /**
     * @return the creatorId
     */
    public String getCreatorId() {
        return creatorId;
    }

    /**
     * @param creatorId 要设置的creatorId
     */
    public void setCreatorId(String creatorId) {
        this.creatorId = creatorId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getModifier() {
        return modifier;
    }

    public void setModifier(String modifier) {
        this.modifier = modifier;
    }

    public Date getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(Date modifyTime) {
        this.modifyTime = modifyTime == null ? new Date() : modifyTime;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getFileNames() {
        return fileNames;
    }

    public void setFileNames(String fileNames) {
        this.fileNames = fileNames;
    }

//    /**
//     * @return the fileInfos
//     */
//    public String getFileInfos() {
//        return fileInfos;
//    }
//
//    /**
//     * @param fileInfos 要设置的fileInfos
//     */
//    public void setFileInfos(String fileInfos) {
//        this.fileInfos = fileInfos;
//    }

    /**
     * @return the attachments
     */
    public List<Attachment> getAttachments() {
        return attachments;
    }

    /**
     * @param attachments 要设置的attachments
     */
    public void setAttachments(List<Attachment> attachments) {
        this.attachments = attachments;
    }

    public Integer getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(Integer isDelete) {
        this.isDelete = isDelete;
    }

    /**
     * @return the system
     */
    public String getSystem() {
        return system;
    }

    /**
     * @param system 要设置的system
     */
    public void setSystem(String system) {
        this.system = system;
    }

    /**
     * @return the categoryCodes
     */
    public Set<String> getCategoryCodes() {
        return categoryCodes;
    }

    /**
     * @param categoryCodes 要设置的categoryCodes
     */
    public void setCategoryCodes(Set<String> categoryCodes) {
        this.categoryCodes = categoryCodes;
    }

    /**
     * @return the readers
     */
    public Set<String> getReaders() {
        return readers;
    }

    /**
     * @param readers 要设置的readers
     */
    public void setReaders(Set<String> readers) {
        this.readers = readers;
    }

    /**
     * @return the indexOrder
     */
    public Integer getIndexOrder() {
        return indexOrder;
    }

    /**
     * @param indexOrder 要设置的indexOrder
     */
    public void setIndexOrder(Integer indexOrder) {
        this.indexOrder = indexOrder;
    }
}
