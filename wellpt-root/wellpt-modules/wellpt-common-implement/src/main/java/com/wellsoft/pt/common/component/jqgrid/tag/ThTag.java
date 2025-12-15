package com.wellsoft.pt.common.component.jqgrid.tag;

import org.apache.commons.lang.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;
import java.io.IOException;
import java.io.StringWriter;

public class ThTag extends SimpleTagSupport {
    private String align;
    private String cellattr;
    private String classes;
    private String datefmt;
    private String defval;
    private String editable;
    private String editoptions;
    private String editrules;
    private String edittype;
    private String firstsortorder;
    private String fixed;
    private String formoptions;
    private String formatoptions;
    private String formatter;
    private String frozen;
    private String hidedlg;
    private String hidden;
    private String index;
    private String jsonmap;
    private String key;
    private String label;
    private String name;
    private String resizable;
    private String search;
    private String searchoptions;
    private String sortable;
    private String sorttype;
    private String stype;
    private String surl;
    private String template;
    private String title;
    private String width;
    private String xmlmap;
    private String unformat;
    private String viewable;

    public void setAlign(String align) {
        this.align = align;
    }

    public void setCellattr(String cellattr) {
        this.cellattr = cellattr;
    }

    public void setClasses(String classes) {
        this.classes = classes;
    }

    public void setDatefmt(String datefmt) {
        this.datefmt = datefmt;
    }

    public void setDefval(String defval) {
        this.defval = defval;
    }

    public void setEditable(String editable) {
        this.editable = editable;
    }

    public void setEditoptions(String editoptions) {
        this.editoptions = editoptions;
    }

    public void setEditrules(String editrules) {
        this.editrules = editrules;
    }

    public void setEdittype(String edittype) {
        this.edittype = edittype;
    }

    public void setFirstsortorder(String firstsortorder) {
        this.firstsortorder = firstsortorder;
    }

    public void setFixed(String fixed) {
        this.fixed = fixed;
    }

    public void setFormoptions(String formoptions) {
        this.formoptions = formoptions;
    }

    public void setFormatoptions(String formatoptions) {
        this.formatoptions = formatoptions;
    }

    public void setFormatter(String formatter) {
        this.formatter = formatter;
    }

    public void setFrozen(String frozen) {
        this.frozen = frozen;
    }

    public void setHidedlg(String hidedlg) {
        this.hidedlg = hidedlg;
    }

    public void setHidden(String hidden) {
        this.hidden = hidden;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public void setJsonmap(String jsonmap) {
        this.jsonmap = jsonmap;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setResizable(String resizable) {
        this.resizable = resizable;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public void setSearchoptions(String searchoptions) {
        this.searchoptions = searchoptions;
    }

    public void setSortable(String sortable) {
        this.sortable = sortable;
    }

    public void setSorttype(String sorttype) {
        this.sorttype = sorttype;
    }

    public void setStype(String stype) {
        this.stype = stype;
    }

    public void setSurl(String surl) {
        this.surl = surl;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setWidth(String width) {
        this.width = width;
    }

    public void setXmlmap(String xmlmap) {
        this.xmlmap = xmlmap;
    }

    public void setUnformat(String unformat) {
        this.unformat = unformat;
    }

    public void setViewable(String viewable) {
        this.viewable = viewable;
    }

    @Override
    public void doTag() throws JspException, IOException {
        PageContext pageContext = (PageContext) this.getJspBody()
                .getJspContext();
        HttpServletRequest request = (HttpServletRequest) pageContext
                .getRequest();
        JspWriter out = pageContext.getOut();
        StringWriter sw = new StringWriter();
        getJspBody().invoke(sw);
        String thInnerText = sw.getBuffer().toString();
        thInnerText = StringUtils.replace(thInnerText, "\"", "\\\"");
        String jsonProperties = TagTools.KeyValuePair2Json("align:"
                        + this.align + ":false",
                "cellattr:" + this.cellattr + ":false", "classes:"
                        + this.classes + ":false", "datefmt:" + this.datefmt
                        + ":false", "defval:" + this.defval + ":false",
                "editable:" + this.editable + ":false", "editoptions:"
                        + this.editoptions + ":false", "editrules:"
                        + this.editrules + ":false", "edittype:"
                        + this.edittype + ":false", "firstsortorder:"
                        + this.firstsortorder + ":false", "fixed:" + this.fixed
                        + ":false", "formoptions:" + this.formoptions
                        + ":false", "formatoptions:" + this.formatoptions
                        + ":false", "formatter:" + this.formatter + ":false",
                "frozen:" + this.frozen + ":true", "hidedlg:" + this.hidedlg
                        + ":false", "hidden:" + this.hidden + ":false",
                "index:" + this.index + ":false", "jsonmap:" + this.jsonmap
                        + ":false", "key:" + this.key + ":false", "label:"
                        + this.label + ":false",
                "name:" + this.name + ":false", "resizable:" + this.resizable
                        + ":false", "search:" + this.search + ":false",
                "searchoptions:" + this.searchoptions + ":false", "sortable:"
                        + this.sortable + ":false", "sorttype:" + this.sorttype
                        + ":false", "stype:" + this.stype + ":false", "surl:"
                        + this.surl + ":false", "template:" + this.template
                        + ":false", "title:" + this.title + ":false", "width:"
                        + this.width + ":false", "xmlmap:" + this.xmlmap
                        + ":false", "unformat:" + this.unformat + ":false",
                "viewable:" + this.viewable + ":false");
        // jsonProperties是colModel部分,thInnerText.trim()则是colName部分
        String s = "{" + jsonProperties + "}_" + "\"" + thInnerText.trim()
                + "\",;";
        out.write(s);
    }
}