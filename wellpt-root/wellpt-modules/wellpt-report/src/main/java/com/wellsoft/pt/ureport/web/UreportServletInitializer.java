package com.wellsoft.pt.ureport.web;

import com.bstek.ureport.console.UReportServlet;
import org.springframework.web.WebApplicationInitializer;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;

/**
 * Description: ureport2的报表注册servlet
 *
 * @author chenq
 * @date 2019/5/9
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2019/5/9    chenq		2019/5/9		Create
 * </pre>
 */
public class UreportServletInitializer implements WebApplicationInitializer {
    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        ServletRegistration.Dynamic ureportServlet = servletContext.addServlet("ureportServlet",
                new UReportServlet());
        ureportServlet.setLoadOnStartup(Integer.MAX_VALUE);
        ureportServlet.addMapping("/ureport/*");
    }
}
