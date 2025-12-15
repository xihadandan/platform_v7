package com.wellsoft.pt.basicdata.printtemplate.entity;

import com.wellsoft.context.jdbc.entity.IdEntity;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.sql.Clob;

@Entity
@Table(name = "cd_print_contents")
@DynamicUpdate
@DynamicInsert
public class PrintContents extends IdEntity {
    private static final long serialVersionUID = 1L;

    // 语言
    private String lang;
    // 模版文件的Html
    @JsonIgnore
    private Clob content;
    // 备注
    private String remark;
    // 模版文件的uuid
    private String fileUuid;
    // 表单UUID
    private String formUuid;

    private Integer sortOrder;

    // 资源编号
    private String resourceCode;

    private PrintTemplate printTemplate;

    /**
     * @return the lang
     */
    public String getLang() {
        return lang;
    }

    /**
     * @param lang 要设置的lang
     */
    public void setLang(String lang) {
        this.lang = lang;
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

    /**
     * @return the fileUuid
     */
    public String getFileUuid() {
        return fileUuid;
    }

    /**
     * @param fileUuid 要设置的fileUuid
     */
    public void setFileUuid(String fileUuid) {
        this.fileUuid = fileUuid;
    }

    /**
     * @return the formUuid
     */
    public String getFormUuid() {
        return formUuid;
    }

    /**
     * @param formUuid 要设置的formUuid
     */
    public void setFormUuid(String formUuid) {
        this.formUuid = formUuid;
    }

    /**
     * @return the content
     */
    public Clob getContent() {
        return content;
    }

    /**
     * @param content 要设置的content
     */
    public void setContent(Clob content) {
        this.content = content;
    }

    /**
     * @return the resourceCode
     */
    public String getResourceCode() {
        return resourceCode;
    }

    /**
     * @param resourceCode 要设置的resourceCode
     */
    public void setResourceCode(String resourceCode) {
        this.resourceCode = resourceCode;
    }

    /**
     * @return the printTemplate
     */
    @ManyToOne(targetEntity = PrintTemplate.class)
    @JoinColumn(name = "TEMPLATE_UUID")
    public PrintTemplate getPrintTemplate() {
        return printTemplate;
    }

    /**
     * @param printTemplate 要设置的printTemplate
     */
    public void setPrintTemplate(PrintTemplate printTemplate) {
        this.printTemplate = printTemplate;
    }

    /**
     * @return the sortOrder
     */
    public Integer getSortOrder() {
        return sortOrder;
    }

    /**
     * @param sortOrder 要设置的sortOrder
     */
    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }
}
