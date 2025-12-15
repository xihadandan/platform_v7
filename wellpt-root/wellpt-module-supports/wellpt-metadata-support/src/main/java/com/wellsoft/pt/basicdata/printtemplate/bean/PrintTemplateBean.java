package com.wellsoft.pt.basicdata.printtemplate.bean;

import com.wellsoft.pt.basicdata.printtemplate.entity.PrintTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * Description: 打印模板VO类
 *
 * @author zhouyq
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-3-21.1	zhouyq		2013-3-21		Create
 * </pre>
 * @date 2013-3-21
 */
public class PrintTemplateBean extends PrintTemplate {
    private static final long serialVersionUID = 1L;

    /**
     * 打印模板使用人，从组织机构中选择直接作为ACL中的SID
     */
    private String ownerNames;

    private String ownerIds;

    private String fileName;

    private List<PrintContentsBean> printContents = new ArrayList<PrintContentsBean>();
    private List<String> printContentsDel = new ArrayList<String>(0);

    /* 打印模版分类名称 */
    private String categoryName;

    /**
     * @return the ownerNames
     */
    public String getOwnerNames() {
        return ownerNames;
    }

    /**
     * @param ownerNames 要设置的ownerNames
     */
    public void setOwnerNames(String ownerNames) {
        this.ownerNames = ownerNames;
    }

    /**
     * @return the ownerIds
     */
    public String getOwnerIds() {
        return ownerIds;
    }

    /**
     * @param ownerIds 要设置的ownerIds
     */
    public void setOwnerIds(String ownerIds) {
        this.ownerIds = ownerIds;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    /**
     * @return the printContentsDel
     */
    public List<String> getPrintContentsDel() {
        return printContentsDel;
    }

    /**
     * @param printContentsDel 要设置的printContentsDel
     */
    public void setPrintContentsDel(List<String> printContentsDel) {
        this.printContentsDel = printContentsDel;
    }

    /**
     * @return the printContents
     */
    public List<PrintContentsBean> getPrintContents() {
        return printContents;
    }

    /**
     * @param printContents 要设置的printContents
     */
    public void setPrintContents(List<PrintContentsBean> printContents) {
        this.printContents = printContents;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
}
