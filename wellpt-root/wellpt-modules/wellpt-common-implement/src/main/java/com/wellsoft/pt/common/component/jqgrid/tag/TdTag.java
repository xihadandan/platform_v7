package com.wellsoft.pt.common.component.jqgrid.tag;

import org.apache.commons.lang.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;
import java.io.IOException;
import java.io.StringWriter;

public class TdTag extends SimpleTagSupport {

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
        String tdInnerText = sw.getBuffer().toString().trim();
        tdInnerText = StringUtils.replace(tdInnerText, "\"", "\\\"");// 解决双引号问题
        tdInnerText = tdInnerText.replaceAll("\\s{1,}", " ");
        // 下面的变量s为mydata数组的每一个元素的一个键值对(也就是一个单元格的数据.
        String s = this.name + ":\"" + tdInnerText + "\",";
        out.write(s);
    }
}