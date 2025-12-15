/*
 * @(#)3/29/24 V1.0
 *
 * Copyright 2024 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.license.web;

import com.wellsoft.pt.license.generator.LicenseGenerator;
import com.wellsoft.pt.license.support.LicenseContent;
import com.wellsoft.pt.license.support.LicenseContentBuilder;
import com.wellsoft.pt.license.support.properties.LicenseProperties;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 3/29/24.1	zhulh		3/29/24		Create
 * </pre>
 * @date 3/29/24
 */
@Controller
@RequestMapping("/license/manager")
@EnableConfigurationProperties({LicenseProperties.class})
@ConditionalOnProperty(prefix = "license.manager", name = "enabled", havingValue = "true", matchIfMissing = false)
public class LicenseManagerController {
    private LicenseProperties licenseProperties;

    public LicenseManagerController(LicenseProperties licenseProperties) {
        this.licenseProperties = licenseProperties;
    }

    /**
     * 生成证书
     *
     * @return
     */
    @RequestMapping(value = "/generate", method = {RequestMethod.GET, RequestMethod.POST})
    public void generate(@RequestParam(name = "subject", required = true) String subject, HttpServletRequest request,
                         HttpServletResponse response) throws Exception {
        LicenseGenerator licenseGenerator = new LicenseGenerator();
        Calendar calendar = Calendar.getInstance();
        Map<String, Object> extra = new HashMap<>();
        Enumeration<String> paramterNames = request.getParameterNames();
        while (paramterNames.hasMoreElements()) {
            String paramterName = paramterNames.nextElement();
            if (!StringUtils.equals("subject", paramterName)) {
                extra.put(paramterName, request.getParameter(paramterName));
            }
        }
        LicenseContent content =
                LicenseContentBuilder.create().subject(subject).issued(calendar.getTime()).extra(extra).build();
        InputStream input = licenseGenerator.generate(content);
        String contentType = "application/octet-stream; charset=UTF-8";
        response.setContentType(contentType);
        response.setHeader("Content-Disposition", "attachment;filename=license.lic");
        OutputStream output = response.getOutputStream();
        IOUtils.copyLarge(input, output);
        IOUtils.closeQuietly(input);
        IOUtils.closeQuietly(output);
    }

    /**
     * 生成证书开发版本证书
     *
     * @return
     */
    @RequestMapping(value = "/generate/dev", method = {RequestMethod.GET, RequestMethod.POST})
    public void generateDev(@RequestParam(name = "subject", required = true) String subject,
                            HttpServletRequest request, HttpServletResponse response) throws Exception {
        LicenseGenerator licenseGenerator = new LicenseGenerator();
        Calendar calendar = Calendar.getInstance();
        Map<String, Object> extra = new HashMap<>();
        Enumeration<String> paramterNames = request.getParameterNames();
        while (paramterNames.hasMoreElements()) {
            String paramterName = paramterNames.nextElement();
            if (!StringUtils.equals("subject", paramterName)) {
                extra.put(paramterName, request.getParameter(paramterName));
            }
        }
        extra.put("profile", licenseProperties.getDevProfile());
        extra.put("serviceUrl", licenseProperties.getServiceUrl());
        LicenseContent content =
                LicenseContentBuilder.create().subject(subject).issued(calendar.getTime()).consumerAmount(5).extra(extra).build();
        InputStream input = licenseGenerator.generate(content);
        String contentType = "application/octet-stream; charset=UTF-8";
        response.setContentType(contentType);
        response.setHeader("Content-Disposition", "attachment;filename=license.lic");
        OutputStream output = response.getOutputStream();
        IOUtils.copyLarge(input, output);
        IOUtils.closeQuietly(input);
        IOUtils.closeQuietly(output);
    }

}
