/*
 * @(#)2016年3月25日 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.web;

import com.wellsoft.pt.app.support.AppCacheUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.util.ClassUtils;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler;

import javax.activation.FileTypeMap;
import javax.activation.MimetypesFileTypeMap;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016年3月25日.1	zhulh		2016年3月25日		Create
 * </pre>
 * @date 2016年3月25日
 */
public class WebResourcesFacadeController extends AbstractController implements InitializingBean {

    private static final boolean jafPresent = ClassUtils.isPresent("javax.activation.FileTypeMap",
            ResourceHttpRequestHandler.class.getClassLoader());

    private List<Resource> locations;

    /**
     * (non-Javadoc)
     *
     * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        locations = new ArrayList<Resource>();
        Resource location = new ClassPathResource("");
        locations.add(location);
    }

    /**
     * (non-Javadoc)
     *
     * @see org.springframework.web.servlet.mvc.AbstractController#handleRequestInternal(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        checkAndPrepare(request, response, true);

        // check whether a matching resource exists
        Resource resource = getResource(request);
        if (resource == null) {
            logger.debug("No matching resource found - returning 404");
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return null;
        }

        // check the resource's media type
        MediaType mediaType = getMediaType(resource);
        if (mediaType != null) {
            if (logger.isDebugEnabled()) {
                logger.debug("Determined media type '" + mediaType + "' for " + resource);
            }
        } else {
            if (logger.isDebugEnabled()) {
                logger.debug("No media type found for " + resource + " - not sending a content-type header");
            }
        }

        // header phase
        if (new ServletWebRequest(request, response).checkNotModified(resource.lastModified())) {
            logger.debug("Resource not modified - returning 304");
            return null;
        }
        setHeaders(response, resource, mediaType);

        // content phase
        if (METHOD_HEAD.equals(request.getMethod())) {
            logger.trace("HEAD request - skipping content");
            return null;
        }
        writeContent(response, resource);
        return null;
    }

    protected Resource getResource(HttpServletRequest request) {
        String path = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
        if (path == null) {
            throw new IllegalStateException("Required request attribute '"
                    + HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE + "' is not set");
        }

        if (!StringUtils.hasText(path) || isInvalidPath(path)) {
            if (logger.isDebugEnabled()) {
                logger.debug("Ignoring invalid resource path [" + path + "]");
            }
            return null;
        }

        String realPath = AppCacheUtils.getRealPath(path);
        if (StringUtils.hasLength(realPath)) {
            path = realPath.substring("/web/res/".length());
        }

        String[] pathComponents = StringUtils.split(path, "/");
        if (pathComponents.length > 0) {
            String packagePath = AppCacheUtils.getPackagePath(pathComponents[0]);
            path = path.replaceFirst(pathComponents[0], packagePath);
        }

        for (Resource location : this.locations) {
            try {
                if (logger.isDebugEnabled()) {
                    logger.debug("Trying relative path [" + path + "] against base location: " + location);
                }
                Resource resource = location.createRelative(path);
                if (resource.exists() && resource.isReadable()) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("Found matching resource: " + resource);
                    }
                    return resource;
                } else if (logger.isTraceEnabled()) {
                    logger.trace("Relative resource doesn't exist or isn't readable: " + resource);
                }
            } catch (IOException ex) {
                logger.debug("Failed to create relative resource - trying next resource location", ex);
            }
        }
        return null;
    }

    protected boolean isInvalidPath(String path) {
        return (path.contains("WEB-INF") || path.contains("META-INF") || StringUtils.cleanPath(path).startsWith(".."));
    }

    protected MediaType getMediaType(Resource resource) {
        MediaType mediaType = null;
        String mimeType = getServletContext().getMimeType(resource.getFilename());
        if (StringUtils.hasText(mimeType)) {
            mediaType = MediaType.parseMediaType(mimeType);
        }
        if (jafPresent && (mediaType == null || MediaType.APPLICATION_OCTET_STREAM.equals(mediaType))) {
            MediaType jafMediaType = ActivationMediaTypeFactory.getMediaType(resource.getFilename());
            if (jafMediaType != null && !MediaType.APPLICATION_OCTET_STREAM.equals(jafMediaType)) {
                mediaType = jafMediaType;
            }
        }
        return mediaType;
    }

    protected void setHeaders(HttpServletResponse response, Resource resource, MediaType mediaType) throws IOException {
        long length = resource.contentLength();
        if (length > Integer.MAX_VALUE) {
            throw new IOException("Resource content too long (beyond Integer.MAX_VALUE): " + resource);
        }
        response.setContentLength((int) length);

        if (mediaType != null) {
            response.setContentType(mediaType.toString());
        }
    }

    protected void writeContent(HttpServletResponse response, Resource resource) throws IOException {
        FileCopyUtils.copy(resource.getInputStream(), response.getOutputStream());
    }

    /**
     * Inner class to avoid hard-coded JAF dependency.
     */
    private static class ActivationMediaTypeFactory {

        private static final FileTypeMap fileTypeMap;

        static {
            fileTypeMap = loadFileTypeMapFromContextSupportModule();
        }

        private static FileTypeMap loadFileTypeMapFromContextSupportModule() {
            // see if we can find the extended mime.types from the
            // context-support module
            Resource mappingLocation = new ClassPathResource("org/springframework/mail/javamail/mime.types");
            if (mappingLocation.exists()) {
                InputStream inputStream = null;
                try {
                    inputStream = mappingLocation.getInputStream();
                    return new MimetypesFileTypeMap(inputStream);
                } catch (IOException ex) {
                    // ignore
                } finally {
                    if (inputStream != null) {
                        try {
                            inputStream.close();
                        } catch (IOException ex) {
                            // ignore
                        }
                    }
                }
            }
            return FileTypeMap.getDefaultFileTypeMap();
        }

        public static MediaType getMediaType(String filename) {
            String mediaType = fileTypeMap.getContentType(filename);
            return (StringUtils.hasText(mediaType) ? MediaType.parseMediaType(mediaType) : null);
        }
    }

}
