/*
 * @(#)2014-1-12 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.common.web;

import com.wellsoft.context.util.file.FileDownloadUtils;
import com.wellsoft.context.web.controller.BaseController;
import com.wellsoft.pt.mt.entity.Tenant;
import com.wellsoft.pt.mt.facade.service.TenantFacadeService;
import com.wellsoft.pt.repository.entity.mongo.file.MongoFileEntity;
import com.wellsoft.pt.repository.service.MongoFileService;
import com.wellsoft.pt.security.util.IgnoreLoginUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.support.ServletContextResource;

import javax.annotation.security.PermitAll;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.Writer;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2014-1-12.1	zhulh		2014-1-12		Create
 * </pre>
 * @date 2014-1-12
 */
@Controller
@RequestMapping("/resfacade/**")
public class ResourceDownloadFacadeController extends BaseController {
    private static String FILE_NAME_PATTERN = "(?!((^(con)$)|^(con)//..*|(^(prn)$)|^(prn)//..*|(^(aux)$)|^(aux)//..*|(^(nul)$)|^(nul)//..*|(^(com)[1-9]$)|^(com)[1-9]//..*|(^(lpt)[1-9]$)|^(lpt)[1-9]//..*)|^//s+|.*//s$)(^[^/////////://*//?//\"//<//>//|]{1,255}$)";
    @Autowired
    private MongoFileService fileService;
    @Autowired
    private TenantFacadeService tenantService;

    /**
     * @param fileName
     * @return
     */
    public static boolean isValidFileName(String fileName) {
        if (StringUtils.isBlank(fileName)) {
            return false;
        }
        Pattern pattern = Pattern.compile(FILE_NAME_PATTERN);
        Matcher matcher = pattern.matcher(fileName.toLowerCase());
        return matcher.find();
    }

    /**
     * web项目/resources/pt/目录下文件下载
     *
     * @param request
     * @param response
     * @param filename
     */
    @RequestMapping("")
    public void download(HttpServletRequest request, HttpServletResponse response,
                         @RequestParam("filename") String filename) {
        String downloadFilename = filename;
        if (!isValidFileName(downloadFilename)) {
            downloadFilename = UUID.randomUUID().toString();
        }
        String ctx = request.getContextPath();
        String requestURI = request.getRequestURI();
        String basePath = requestURI.replace("/resfacade/", "/resources/pt/") + "/{0}";
        if (!"/".equals(ctx)) {
            basePath = basePath.substring(ctx.length());
        }
        download(request, response, basePath, downloadFilename);
    }

    /**
     * web项目/resources/pt/cadriver/目录下文件下载
     *
     * @param request
     * @param response
     * @param filename
     */
    @RequestMapping("/share/cadriver")
    @PermitAll
    public void shareCadriver(HttpServletRequest request, HttpServletResponse response,
                              @RequestParam("filename") String filename) {
        String downloadFilename = filename;
        if (!isValidFileName(downloadFilename)) {
            downloadFilename = UUID.randomUUID().toString();
        }
        String ctx = request.getContextPath();
        String requestURI = request.getRequestURI();
        String basePath = requestURI.replace("/resfacade/share", "/resources/pt/") + "/{0}";
        if (!"/".equals(ctx)) {
            basePath = basePath.substring(ctx.length());
        }
        download(request, response, basePath, downloadFilename);
    }

    /**
     * web项目/resources/pt/share/目录下是否存在共享文件
     *
     * @param request
     * @param response
     * @param filename
     * @throws IOException
     */
    @RequestMapping("/exists/share/")
    public void exists(HttpServletRequest request, HttpServletResponse response,
                       @RequestParam("filename") String filename) throws IOException {
        String downloadFilename = filename;
        if (!isValidFileName(downloadFilename)) {
            downloadFilename = UUID.randomUUID().toString();
        }
        String ctx = request.getContextPath();
        String requestURI = request.getRequestURI();
        String basePath = requestURI.replace("/resfacade/exists/share", "/resources/pt/share/") + "/{0}";
        if (!"/".equals(ctx)) {
            basePath = basePath.substring(ctx.length());
        }
        ServletContextResource servletContextResource = new ServletContextResource(request.getServletContext(),
                basePath);
        Resource resource = servletContextResource.createRelative(downloadFilename);
        Writer writer = response.getWriter();
        writer.write(resource.exists() + StringUtils.EMPTY);
        writer.flush();
        writer.close();
    }

    /**
     * web项目/resources/pt/share/目录下文件下载
     *
     * @param request
     * @param response
     * @param filename
     */
    @RequestMapping("/share")
    @PermitAll
    public void share(HttpServletRequest request, HttpServletResponse response,
                      @RequestParam("filename") String filename) {
        String downloadFilename = filename;
        if (!isValidFileName(downloadFilename)) {
            downloadFilename = UUID.randomUUID().toString();
        }
        String ctx = request.getContextPath();
        String requestURI = request.getRequestURI();
        String basePath = requestURI.replace("/resfacade/share", "/resources/pt/share/") + "/{0}";
        if (!"/".equals(ctx)) {
            basePath = basePath.substring(ctx.length());
        }
        download(request, response, basePath, downloadFilename);
    }

    /**
     * 如何描述该方法
     *
     * @param request
     * @param response
     * @param filename
     * @param basePath
     */
    private void download(HttpServletRequest request, HttpServletResponse response, String basePath, String filename) {
        ServletContextResource servletContextResource = new ServletContextResource(request.getServletContext(),
                basePath);
        Resource resource = servletContextResource.createRelative(filename);
        // if (!resource.exists()) {
        // ClassPathResource classPathResource = new
        // ClassPathResource("/META-INF/resources" + basePath);
        // resource = classPathResource.createRelative(filename);
        // }
        try {
            FileDownloadUtils.download(request, response, resource.getInputStream(), resource.getFilename());
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }

    /**
     * mongo DB文件下载
     *
     * @param request
     * @param response
     * @param tenant
     * @param fileId
     */
    @RequestMapping("/{tenant:[a-zA-Z_0-9]+}/share/file/{fileId:[a-zA-Z_0-9]+}")
    @PermitAll
    public void shareFile(HttpServletRequest request, HttpServletResponse response,
                          @PathVariable("tenant") String tenant, @PathVariable("fileId") String fileId) {
        String fileName = null;
        try {
            prepare(tenant);

            MongoFileEntity mongoFileEntity = fileService.getFile(fileId);
            fileName = mongoFileEntity.getFileName();
            FileDownloadUtils.download(request, response, mongoFileEntity.getInputstream(), fileName);
        } catch (Exception e) {
            logger.error(ExceptionUtils.getStackTrace(e));
            FileDownloadUtils
                    .download(request, response, new ByteArrayInputStream(e.getMessage().getBytes()), fileName);
        } finally {
            IgnoreLoginUtils.logout();
        }
    }

    private void prepare(String tenant) throws Exception {
        Tenant tenantAccount = tenantService.getByAccount(tenant);
        IgnoreLoginUtils.login(tenantAccount.getId(), tenantAccount.getId());
    }
}
