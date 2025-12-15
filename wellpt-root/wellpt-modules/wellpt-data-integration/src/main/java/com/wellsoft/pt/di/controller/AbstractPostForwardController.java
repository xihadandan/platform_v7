package com.wellsoft.pt.di.controller;

import com.google.common.base.Charsets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.wellsoft.context.exception.JsonDataException;
import com.wellsoft.context.web.controller.ApiResult;
import com.wellsoft.context.web.controller.FaultMessage;
import com.wellsoft.pt.security.access.intercept.provider.SecurityMetadataSourceProviderFactory;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.net.util.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.FilterInvocation;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.DefaultMultipartHttpServletRequest;
import org.springframework.web.servlet.HandlerAdapter;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.Serializable;
import java.net.URLDecoder;
import java.util.*;

import static org.apache.commons.fileupload.FileUploadBase.MULTIPART;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2021年10月28日   chenq	 Create
 * </pre>
 */
public class AbstractPostForwardController {

    protected Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    protected List<HandlerMapping> handlerMappings;
    @Autowired
    protected List<HandlerAdapter> handlerAdapters;
    @Autowired
    protected AccessDecisionManager accessDecisionManager;
    @Autowired
    protected SecurityMetadataSourceProviderFactory securityMetadataProviderFactory;

    protected Gson gson = new GsonBuilder().create();

    protected void forward(PostParameter postParameter, HttpServletRequest request, HttpServletResponse response) {
        try {
            // 不支持websocket
            if (postParameter.getRequestUrl().indexOf("/wellSocket") != -1) {
                return;
            }
            // 获取报文
            HttpServletRequest mockRequest = null;
            logger.debug("{}", postParameter);
            //构造request
            HttpMethod httpMethod = HttpMethod.resolve(postParameter.getMethod().toString());
            String uri = URLDecoder.decode(postParameter.getRequestUrl(), Charsets.UTF_8.name());
            MockHttpServletRequestBuilder builder = null;
            try {
                MediaType.parseMediaType(postParameter.getContentType());
            } catch (Exception e) {
                logger.warn("url: {} , 不识别的contentType类型: {}", postParameter.getRequestUrl(), postParameter.getContentType());
                postParameter.setContentType(MediaType.APPLICATION_JSON_VALUE);
            }
            if (HttpMethod.GET == httpMethod) {
                builder = MockMvcRequestBuilders.get(uri).contentType(postParameter.getContentType());
            } else if (HttpMethod.POST == httpMethod) {
                builder = MockMvcRequestBuilders.post(uri).contentType(postParameter.getContentType());
                if (StringUtils.isNotBlank(postParameter.getContent())) {
                    builder.content(postParameter.getContent());
                }
            } else if (HttpMethod.PUT == httpMethod) {
                builder = MockMvcRequestBuilders.put(uri).contentType(postParameter.getContentType());
                if (StringUtils.isNotBlank(postParameter.getContent())) {
                    builder.content(postParameter.getContent());
                }

            } else if (HttpMethod.DELETE == httpMethod) {
                builder = MockMvcRequestBuilders.delete(uri).contentType(postParameter.getContentType());
            }
            mockRequest = builder.buildRequest(request.getServletContext());
            authenticate(mockRequest, response);
            if (HttpMethod.POST == httpMethod && postParameter.getContentType().startsWith(MULTIPART)) {
                Map<String, Base64File> fileMap = postParameter.getBase64files();
                if (MapUtils.isNotEmpty(fileMap)) {
                    MultiValueMap<String, MultipartFile> multipartFiles = new LinkedMultiValueMap<String, MultipartFile>();
                    Map<String, String[]> multipartParameters = new HashMap<String, String[]>();
                    Map<String, String> multipartParameterContentTypes = new HashMap<String, String>();
                    Set<String> fieldKeys = fileMap.keySet();
                    for (String field : fieldKeys) {
                        setFile(field, fileMap.get(field), multipartFiles, multipartParameters, multipartParameterContentTypes);
                    }
                    mockRequest = new DefaultMultipartHttpServletRequest(mockRequest, multipartFiles, multipartParameters, multipartParameterContentTypes);
                }
            }


            HandlerExecutionChain mappedHandler = null;
            for (HandlerMapping hm : this.handlerMappings) {
                mappedHandler = hm.getHandler(mockRequest);
                if (mappedHandler != null) {
                    break;
                }
            }
            if (mappedHandler != null) {
                logger.debug("post-conversion -> mapping : {}", mappedHandler.toString());
                handlerAdapter(mappedHandler.getHandler()).handle(mockRequest, response, mappedHandler.getHandler());
            }

        } catch (Exception e) {
            logger.error("post转换异常：", e);
            responseException(response, e);
        }
    }

    protected HandlerAdapter handlerAdapter(Object handler) throws ServletException {
        for (HandlerAdapter adapter : this.handlerAdapters) {
            if (adapter.supports(handler)) {
                return adapter;
            }
        }
        throw new ServletException("无法适配请求处理适配器[" + handler + "]");
    }


    protected void authenticate(HttpServletRequest mockRequest, HttpServletResponse response) throws AccessDeniedException {
        FilterInvocation invocation = new FilterInvocation(mockRequest, response, new MockFilterChain());
        Authentication authentication = SpringSecurityUtils.getAuthentication();
        Collection<ConfigAttribute> configAttributes = securityMetadataProviderFactory.getSecurityMetadataSourceProvider().getRequestAttributes(invocation);
        if ("anonymousUser".equals(authentication.getPrincipal())) {//匿名请求不允许访问有权限的地址
            for (ConfigAttribute attribute : configAttributes) {
                if (attribute.toString().equalsIgnoreCase("authenticated")) {
                    throw new AccessDeniedException("Access is denied");
                }
            }
        }
        accessDecisionManager.decide(authentication, invocation, configAttributes);
    }

    protected void responseException(HttpServletResponse response, Exception e) {
        try {
            ApiResult result = ApiResult.fail(e.getMessage());
            if (e instanceof JsonDataException) {
                FaultMessage msg = new FaultMessage();
                msg.setData(((JsonDataException) e).getData());
                msg.setErrorCode(((JsonDataException) e).getErrorCode().name());
                result = ApiResult.fail(msg);
            }
            if (result != null) {
                response.getOutputStream().write(gson.toJson(result).getBytes());
                response.getOutputStream().flush();
                response.getOutputStream().close();
            }

        } catch (Exception ex) {
            logger.error("服务类：{} ", this.getClass().getName(), ex);
        }
    }


    private void setFile(String field, Base64File base64File, MultiValueMap<String, MultipartFile> multipartFiles,
                         Map<String, String[]> multipartParameters, Map<String, String> multipartParameterContentTypes) throws Exception {
        // 解析base64附件
        byte[] b = Base64.decodeBase64(base64File.getBase64());
        MultipartFile file = new MockMultipartFile(base64File.getFilename(), new ByteArrayInputStream(b));
        multipartFiles.add(field, file);
        multipartParameters.put(field, new String[]{b.toString()});
        multipartParameterContentTypes.put(field,
                base64File.getBase64().substring(base64File.getBase64().indexOf(":") + 1, base64File.getBase64().indexOf(";base64,"))); // 从base64截类型
    }

    public static class Base64File implements Serializable {
        private String base64;
        private String filename;

        public String getBase64() {
            return base64;
        }

        public void setBase64(String base64) {
            this.base64 = base64;
        }

        public String getFilename() {
            return filename;
        }

        public void setFilename(String filename) {
            this.filename = filename;
        }
    }

    public static class PostParameter implements Serializable {
        private static final long serialVersionUID = -7517563860167512587L;

        private HttpMethod method;

        private String content;

        private Map<String, Base64File> base64files;

        private String requestUrl;

        private String contentType;

        public HttpMethod getMethod() {
            return method;
        }

        public void setMethod(HttpMethod method) {
            this.method = method;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getRequestUrl() {
            return requestUrl;
        }

        public void setRequestUrl(String requestUrl) {
            this.requestUrl = requestUrl;
        }

        public String getContentType() {
            return contentType;
        }

        public void setContentType(String contentType) {
            this.contentType = contentType;
        }

        public Map<String, Base64File> getBase64files() {
            return base64files;
        }

        public void setBase64files(Map<String, Base64File> base64files) {
            this.base64files = base64files;
        }

        @Override
        public String toString() {
            return "PostParameter{" +
                    "method=" + method +
                    ", content='" + content + '\'' +
                    ", requestUrl='" + requestUrl + '\'' +
                    ", contentType='" + contentType + '\'' +
                    '}';
        }
    }
}
