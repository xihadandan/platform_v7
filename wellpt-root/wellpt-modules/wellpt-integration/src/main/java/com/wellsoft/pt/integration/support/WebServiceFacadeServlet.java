package com.wellsoft.pt.integration.support;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WebServiceFacadeServlet extends HttpServlet {
    private static final long serialVersionUID = -3097547347152862878L;

    private Pattern tenantPattern = Pattern.compile("/webservices/(.*?(?=/))", Pattern.CASE_INSENSITIVE);

    /**
     * The doGet method of the servlet. <br>
     * <p>
     * This method is called when a form has its tag value method equals to get.
     *
     * @param request  the request send by the client to the server
     * @param response the response send by the server to the client
     * @throws ServletException if an error occurred
     * @throws IOException      if an error occurred
     */
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    /**
     * The doPost method of the servlet. <br>
     * <p>
     * This method is called when a form has its tag value method equals to
     * post.
     *
     * @param request  the request send by the client to the server
     * @param response the response send by the server to the client
     * @throws ServletException if an error occurred
     * @throws IOException      if an error occurred
     */
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String forwardUrl = request.getRequestURI().replace("wsfacade", "webservices")
                .replace(request.getContextPath(), "");
        Matcher matcher = tenantPattern.matcher(forwardUrl);
        if (matcher.find()) {
            String tenantAccount = matcher.group(1);
            request.setAttribute("tenantId", tenantAccount);
            forwardUrl = forwardUrl.replace("/" + tenantAccount, "");
        }
        request.getRequestDispatcher(forwardUrl).forward(request, response);
    }
}
