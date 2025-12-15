package com.wellsoft.pt.common.component.jqgrid.tag;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;
import java.io.IOException;
import java.io.StringWriter;

public class TrTag extends SimpleTagSupport {

    @Override
    public void doTag() throws JspException, IOException {
        PageContext pageContext = (PageContext) this.getJspBody().getJspContext();
        HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
        JspWriter out = pageContext.getOut();
        StringWriter sw = new StringWriter();
        getJspBody().invoke(sw);
        String trInnerData = sw.getBuffer().toString().trim();
        // 这个if用来判断子标签式th还是td
        if (trInnerData.startsWith("{")) {
            trInnerData = trInnerData.replaceAll(";\\s+\\{", ";{");
            String[] ths = trInnerData.split(";");
            StringBuilder colNames = new StringBuilder("colNames:[");
            StringBuilder colModel = new StringBuilder("colModel:[");
            for (int i = 0; i < ths.length; i++) {
                String[] th = ths[i].split("_");
                colModel.append(th[0]);
                colNames.append(th[1]);
                if (i != ths.length - 1) {
                    colModel.append(",");
                } else {
                    colNames.deleteCharAt(colNames.length() - 1);
                }
            }
            colModel.append("],");
            colNames.append("],");
            out.write(colNames.toString().trim());
            out.write(colModel.toString().trim());
        } else {
            StringBuilder datas = new StringBuilder("€{");
            trInnerData = trInnerData.replaceAll(",\\s+", ",");
            datas.append(trInnerData).deleteCharAt(datas.length() - 1).append("},");
            out.write(datas.toString().trim());
        }
    }

}
