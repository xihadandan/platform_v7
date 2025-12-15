package com.wellsoft.oauth2.web.controller;

import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.reflect.TypeToken;
import com.sun.xml.internal.messaging.saaj.packaging.mime.internet.MimeUtility;
import com.wellsoft.oauth2.web.filter.RequestContext;
import com.wellsoft.oauth2.web.search.DynamicSpecifications;
import com.wellsoft.oauth2.web.search.WebSearchFilter;
import com.wellsoft.oauth2.web.support.BasicResponse;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Description:
 *
 * @author chenq
 * @date 2019/9/18
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2019/9/18    chenq		2019/9/18		Create
 * </pre>
 */
public abstract class AbstractController<T> {

    public final static String STATUS_CODE = "statusCode";
    public final static String ERROR_MSG = "errMsg";
    protected Class<T> entityClass;
    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    public AbstractController() {
        super();
        try {
            TypeToken<?> genericTypeToken = TypeToken.of(getClass());
            this.entityClass = (Class<T>) genericTypeToken.resolveType(
                    AbstractController.class.getTypeParameters()[0])
                    .getRawType();
        } catch (Exception e) {
            throw Throwables.propagate(e);
        }
    }

    public AbstractController(Class<T> entityClass) {
        super();
        this.entityClass = entityClass;
    }

    public static String getServerPath(HttpServletRequest request) {
        return request.getScheme() + "://" + request.getServerName()
                + (request.getServerPort() == 80 ? "" : (":" + request.getServerPort()))
                + ("/".equalsIgnoreCase(request.getContextPath()) ? "" : request.getContextPath());
    }

    @RequestMapping
    public String index(Model model, HttpServletRequest request, HttpServletResponse response) {
        throw new UnsupportedOperationException("not yet implement");
    }

    @RequestMapping(value = "/new")
    public String _new(Model model, T entity, HttpServletRequest request,
                       HttpServletResponse response) {
        throw new UnsupportedOperationException("not yet implement");
    }

    @RequestMapping(value = "/{uuid}")
    public String show(Model model, @PathVariable Long uuid, HttpServletRequest request,
                       HttpServletResponse response) {
        throw new UnsupportedOperationException("not yet implement");
    }

    @RequestMapping(value = "/{uuid}/edit")
    public String edit(Model model, @PathVariable Long uuid, HttpServletRequest request,
                       HttpServletResponse response) {
        throw new UnsupportedOperationException("not yet implement");
    }

    @RequestMapping(method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity create(Model model, RedirectAttributes redirectAttributes,
                          @RequestBody @Validated T entity,
                          BindingResult errors,
                          HttpServletRequest request, HttpServletResponse response) {
        throw new UnsupportedOperationException("not yet implement");
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity update(RedirectAttributes model, @RequestBody @Validated T entity,
                          BindingResult errors,
                          HttpServletRequest request, HttpServletResponse response) {
        throw new UnsupportedOperationException("not yet implement");
    }

    @RequestMapping(value = "/{uuid}", method = RequestMethod.DELETE)
    public @ResponseBody
    ResponseEntity delete(RedirectAttributes model, @PathVariable Long uuid, T entity,
                          HttpServletRequest request,
                          HttpServletResponse response) {
        throw new UnsupportedOperationException("not yet implement");
    }

    @RequestMapping(value = "/pageData", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody
    DataTable.PageJsonInfo pageData(Model model, DataTable dataTable, HttpServletRequest request,
                                    HttpServletResponse response) {
        throw new UnsupportedOperationException("not yet implement");
    }

    protected abstract void pageCallback(Page page);

    protected Specification<T> filter(Model model, HttpServletRequest request) {
        return DynamicSpecifications.bySearchFilter(request, entityClass, toFilter(request));
    }

    protected List<WebSearchFilter> toFilter(HttpServletRequest request) {
        return Lists.newArrayList();
    }

    @RequestMapping("/excel/upload")
    public @ResponseBody
    ResponseEntity<BasicResponse> excelUpload(@RequestParam MultipartFile file) {
        throw new UnsupportedOperationException("not yet implement");
    }

    @GetMapping("/template/excel/download")
    public void download(HttpServletResponse response, HttpServletRequest request,
                         @RequestParam String filename) throws IOException {
        response.setContentType("application/vnd.ms-excel");
        response.setCharacterEncoding("utf-8");
        String filenamePart = null;
        try {
            String userAgent = request.getHeader("user-agent");
            userAgent = userAgent == null ? "" : userAgent.toLowerCase();
            if (StringUtils.isBlank(userAgent)) {
                filenamePart = "filename*=UTF-8''" + filename;
            } else if (userAgent.indexOf("msie") != -1 || userAgent.indexOf(
                    "trident") != -1) {// MSIE 10.0
                filenamePart = "filename=\"" + URLEncoder.encode(filename, "UTF-8").replaceAll(
                        "\\+", "%20") + "\"";
            } else if (userAgent.indexOf("chrome") != -1) {// Chrome
                filenamePart = "filename=" + MimeUtility.encodeText(filename, "UTF-8", "B");
            } else if (userAgent.indexOf("firefox") != -1) {// Firefox
                filenamePart = "filename=" + MimeUtility.encodeText(filename, "UTF-8", "B");
                // outputFileName = MimeUtility.encodeText(filename, "UTF-8", "B");
            } else if (userAgent.indexOf("opera") != -1) {// Opera
                filenamePart = "filename=" + filename;
            } else if (userAgent.indexOf("safari") != -1) {// Safari
                filenamePart = "filename=" + new String(filename.getBytes("UTF-8"), "ISO8859-1");
            } else {
                filenamePart = "filename=" + MimeUtility.encodeText(filename, "UTF-8", "B");
            }
        } catch (Exception e) {
            filenamePart = "filename*=UTF-8''" + filename;
        }
        response.setHeader("Content-Disposition", "attachment;" + filenamePart);
        String path = request.getServletContext().getRealPath("/");
        File file = new File(path + "/files/template/" + filename);
        if (file.exists()) {
            response.getOutputStream().write(IOUtils.toByteArray(new FileInputStream(file)));
        }

    }

    @RequestMapping(method = RequestMethod.DELETE)
    public @ResponseBody
    ResponseEntity batchDelete(RedirectAttributes model, @RequestParam("items[]") Long[] items,
                               HttpServletRequest request, HttpServletResponse response) {
        throw new UnsupportedOperationException("not yet implement");
    }

    protected ServletContext servletContext() {
        return RequestContext.get().context();
    }

    /**
     * Gets the servlet context.
     *
     * @return the servlet context
     */
    protected ServletContext getServletContext() {
        return servletContext();
    }

    protected HttpServletRequest request() {
        RequestContext context = RequestContext.get();
        return RequestContext.get().request();
    }

    protected HttpServletRequest getRequest() {
        return request();
    }

    protected HttpServletResponse response() {
        return RequestContext.get().response();
    }

    protected HttpServletResponse getResponse() {
        return response();
    }

    protected HttpSession session() {
        return request().getSession();
    }

    @SuppressWarnings("unchecked")
    public Map<String, String[]> getAsMap() {
        return ImmutableMap.copyOf(request().getParameterMap());
    }

    public String getAsString(String setting) {
        String[] o = get(setting);
        return ((o != null) && (o.length >= 1)) ? o[0] : null;
    }

    public String getAsString(String setting, String defaultValue) {
        String retVal = getAsString(setting);
        return retVal == null ? defaultValue : retVal;
    }

    public String[] get(String setting) {
        String[] retVal = bulidRequestParams().get(setting);
        if (retVal != null) {
            return retVal;
        }
        return bulidRequestParams().get(setting.toLowerCase());
    }

    public String[] get(String setting, String[] defaultValue) {
        String[] retVal = bulidRequestParams().get(setting);
        return retVal == null ? defaultValue : retVal;
    }

    public Float getAsFloat(String setting, Float defaultValue) {
        String sValue = getAsString(setting);
        if (sValue == null) {
            return defaultValue;
        }
        try {
            return Float.parseFloat(sValue);
        } catch (NumberFormatException e) {
            throw Throwables.propagate(e);
        }
    }

    public Double getAsDouble(String setting, Double defaultValue) {
        String sValue = getAsString(setting);
        if (sValue == null) {
            return defaultValue;
        }
        try {
            return Double.parseDouble(sValue);
        } catch (NumberFormatException e) {
            throw Throwables.propagate(e);
        }
    }

    public Integer getAsInt(String setting, Integer defaultValue) {
        String sValue = getAsString(setting);
        if (sValue == null) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(sValue);
        } catch (NumberFormatException e) {
            throw Throwables.propagate(e);
        }
    }

    public Long getAsLong(String setting, Long defaultValue) {
        String sValue = getAsString(setting);
        if (sValue == null) {
            return defaultValue;
        }
        try {
            return Long.parseLong(sValue);
        } catch (NumberFormatException e) {
            throw Throwables.propagate(e);
        }
    }

    public Boolean getAsBoolean(String setting, Boolean defaultValue) {
        String value = getAsString(setting, defaultValue.toString());
        return Boolean.valueOf(value);
    }

    protected ImmutableMap<String, String[]> bulidRequestParams() {
        return ImmutableMap.copyOf(getParametersStartingWith(getRequest(), null));

    }

    protected Map<String, String[]> getParametersStartingWith(ServletRequest request,
                                                              String prefix) {
        Assert.notNull(request, "Request must not be null");
        Enumeration paramNames = request.getParameterNames();
        Map<String, String[]> params = new TreeMap<String, String[]>();
        if (prefix == null) {
            prefix = "";
        }
        while ((paramNames != null) && paramNames.hasMoreElements()) {
            String paramName = (String) paramNames.nextElement();
            if ("".equals(prefix) || paramName.startsWith(prefix)) {
                String unprefixed = paramName.substring(prefix.length());
                String[] values = request.getParameterValues(paramName);
                if ((values == null) || (values.length == 0)) {
                    // Do nothing, no values found at all.
                } else {
                    params.put(unprefixed, values);
                }
            }
        }
        return params;
    }
}
