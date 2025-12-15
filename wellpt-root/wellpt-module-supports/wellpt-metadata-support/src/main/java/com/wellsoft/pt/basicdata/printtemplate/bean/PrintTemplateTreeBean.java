package com.wellsoft.pt.basicdata.printtemplate.bean;

import com.wellsoft.pt.basicdata.printtemplate.entity.PrintTemplate;
import com.wellsoft.pt.basicdata.printtemplate.entity.PrintTemplateCategory;

public class PrintTemplateTreeBean {

    private String uuid;

    private String parentUuid;

    private PrintTemplateCategory printTemplateCategory;

    private PrintTemplate printTemplate;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getParentUuid() {
        return parentUuid;
    }

    public void setParentUuid(String parentUuid) {
        this.parentUuid = parentUuid;
    }

    public PrintTemplateCategory getPrintTemplateCategory() {
        return printTemplateCategory;
    }

    public void setPrintTemplateCategory(PrintTemplateCategory printTemplateCategory) {
        this.printTemplateCategory = printTemplateCategory;
    }

    public PrintTemplate getPrintTemplate() {
        return printTemplate;
    }

    public void setPrintTemplate(PrintTemplate printTemplate) {
        this.printTemplate = printTemplate;
    }
}
