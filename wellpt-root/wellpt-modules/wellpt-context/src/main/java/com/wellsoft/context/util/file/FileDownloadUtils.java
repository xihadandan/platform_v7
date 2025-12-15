/*
 * @(#)2013-4-11 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.context.util.file;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.util.ClassUtils;
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler;

import javax.activation.FileTypeMap;
import javax.activation.MimetypesFileTypeMap;
import javax.mail.internet.MimeUtility;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;

/**
 * Description: 文件下载工具类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-4-11.1	zhulh		2013-4-11		Create
 * </pre>
 * @date 2013-4-11
 */
public class FileDownloadUtils {

    private static final boolean jafPresent = ClassUtils.isPresent("javax.activation.FileTypeMap",
            ResourceHttpRequestHandler.class.getClassLoader());
    private static Logger LOG = LoggerFactory.getLogger(FileDownloadUtils.class);

    /**
     * 文件下载封装方法
     *
     * @param request  web请求
     * @param response web响应
     * @param is       下载的文件文件流
     * @param filename 下载文件的名称
     */
    public static void download(HttpServletRequest request, HttpServletResponse response, InputStream is,
                                String filename) {
        MediaType mediaType = getMediaType(request == null ? null : request.getServletContext(), filename);
        download(request, response, is, filename, mediaType == null ? null : mediaType.toString());
    }

    /**
     * 文件下载封装方法
     *
     * @param request   web请求
     * @param response  web响应
     * @param inputFile 下载文件
     * @param filename  下载文件名称
     */
    public static void download(HttpServletRequest request, HttpServletResponse response, File inputFile,
                                String filename) {
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(inputFile);
            download(request, response, fis, filename);
        } catch (FileNotFoundException e) {
            LOG.error(e.getMessage(), e);
        } finally {
            if (fis != null) {
                IOUtils.closeQuietly(fis);
            }
        }
    }

    /**
     * 文件下载封装方法
     *
     * @param request     web请求
     * @param response    web响应
     * @param is          下载的文件文件流
     * @param filename    下载文件的名称
     * @param contentType contentType
     */
    public static void download(HttpServletRequest request, HttpServletResponse response, InputStream is,
                                String filename, String contentType) {
        setResponseInfo(request, response, filename, contentType);
        output(is, response);
    }

    /**
     * 文件下载封装方法
     *
     * @param request  web请求
     * @param response web响应
     * @param is       下载的文件文件流
     * @param filename 下载文件的名称
     */
    public static void download(HttpServletRequest request, HttpServletResponse response,
                                OutputStreamDownloadHanlder downloadHanlder, String filename) {
        download(request, response, downloadHanlder, filename, null);
    }

    /**
     * 文件下载封装方法
     *
     * @param request  web请求
     * @param response web响应
     * @param is       下载的文件文件流
     * @param filename 下载文件的名称
     */
    public static void download(HttpServletRequest request, HttpServletResponse response,
                                OutputStreamDownloadHanlder downloadHanlder, String filename, String contentType) {
        setResponseInfo(request, response, filename, contentType);
        output(downloadHanlder, response);
    }

    /**
     * @param request
     * @param response
     * @param filename
     * @param contentType
     */
    private static void setResponseInfo(HttpServletRequest request, HttpServletResponse response, String filename,
                                        String contentType) {
        if (StringUtils.isBlank(contentType)) {
            contentType = "application/octet-stream; charset=UTF-8";
        }
        if (StringUtils.isBlank(filename)) {
            throw new RuntimeException("Filename should not be empty.");
        }
        String filenamePart = getFilenamePart(request, filename);
        response.reset();
        // response.setCharacterEncoding(Encoding.UTF8.getValue());
        response.setContentType(contentType);
        response.setHeader("Content-Disposition", "attachment;" + filenamePart);
    }

    /**
     * Content-Disposition值可以有以下几种编码格式
     * 1、直接urlencode
     * Content-Disposition: attachment; filename="struts2.0%E4%B8%AD%E6%96%87%E6%95%99%E7%A8%8B.chm"
     * 2、Base64编码
     * Content-Disposition: attachment; filename="=?UTF8?B?c3RydXRzMi4w5Lit5paH5pWZ56iLLmNobQ==?="
     * 3、RFC2231规定的标准
     * Content-Disposition: attachment; filename*=UTF-8''%E5%9B%9E%E6%89%A7.msg
     * 4、直接ISO编码的文件名
     * Content-Disposition: attachment;filename="测试.txt"
     * <p>
     * 1、IE浏览器，采用URLEncoder编码
     * 2、Opera浏览器，采用filename*方式
     * 3、Safari浏览器，采用ISO编码的中文输出
     * 4、Chrome浏览器，采用Base64编码或ISO编码的中文输出
     * 5、FireFox浏览器，采用Base64或filename*或ISO编码的中文输出
     *
     * @param request
     * @param filename
     * @return
     */
    public static String getFilenamePart(HttpServletRequest request, String filename) {
        String filenamePart = null;
        try {
            String userAgent = request.getHeader("user-agent");
            userAgent = userAgent == null ? "" : userAgent.toLowerCase();
            if (StringUtils.isBlank(userAgent)) {
                filenamePart = "filename*=UTF-8''" + filename;
            } else if (userAgent.indexOf("msie") != -1 || userAgent.indexOf("trident") != -1) {// MSIE 10.0
                //				String namePart = filename;
                //				int lastIndex = filename.lastIndexOf(".");
                //				if (lastIndex != -1) {
                //					namePart = filename.substring(0, lastIndex);
                //				}
                //				filenamePart = "filename=" + URLEncoder.encode(namePart, "UTF-8").replaceAll("\\+", "%20");
                //				if (lastIndex != -1) {
                //					filenamePart += filename.substring(lastIndex);
                //				}
                filenamePart = "filename=\"" + URLEncoder.encode(filename, "UTF-8").replaceAll("\\+", "%20") + "\"";
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
            LOG.error(e.getMessage(), e);
            filenamePart = "filename*=UTF-8''" + filename;
        }
        return filenamePart;
    }

    /**
     * @param is
     * @param response
     */
    public static void output(InputStream is, HttpServletResponse response) {
        OutputStream os = null;
        try {
            os = response.getOutputStream();
            int len = -1;
            byte[] b = new byte[102400];
            while ((len = is.read(b)) != -1) {
                os.write(b, 0, len);
            }
            os.flush();
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            try {
                IOUtils.write(ExceptionUtils.getStackTrace(e), os);
            } catch (IOException e1) {
                LOG.error(e1.getMessage(), e1);
            }
        } finally {
            if (is != null) {
                IOUtils.closeQuietly(is);
            }
            if (os != null) {
                IOUtils.closeQuietly(os);
            }
        }
    }

    /**
     * @param downloadHanlder
     * @param response
     */
    private static void output(OutputStreamDownloadHanlder downloadHanlder, HttpServletResponse response) {
        OutputStream os = null;
        try {
            os = response.getOutputStream();
            if (downloadHanlder != null) {
                downloadHanlder.handle(os);
            }
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            try {
                IOUtils.write(ExceptionUtils.getStackTrace(e), os);
            } catch (IOException e1) {
                LOG.error(e1.getMessage(), e1);
            }
        } finally {
            if (os != null) {
                IOUtils.closeQuietly(os);
            }
        }
    }

    public static MediaType getMediaType(ServletContext servletContext, String fileName) {
        MediaType mediaType = null;
        String mimeType = servletContext == null ? null : servletContext.getMimeType(fileName);
        if (StringUtils.isNotEmpty(mimeType)) {
            mediaType = MediaType.parseMediaType(mimeType);
        }
        if (jafPresent && (mediaType == null || MediaType.APPLICATION_OCTET_STREAM.equals(mediaType))) {
            MediaType jafMediaType = ActivationMediaTypeFactory.getMediaType(fileName);
            if (jafMediaType != null && !MediaType.APPLICATION_OCTET_STREAM.equals(jafMediaType)) {
                mediaType = jafMediaType;
            }
        }
        return mediaType;
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
            return (StringUtils.isNotEmpty(mediaType) ? MediaType.parseMediaType(mediaType) : null);
        }
    }
}
