/*
 * @(#)2015-6-16 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.mt.web;

import com.wellsoft.context.component.jqgrid.JqGridQueryController;
import com.wellsoft.context.component.jqgrid.JqGridQueryData;
import com.wellsoft.context.component.jqgrid.JqGridQueryInfo;
import com.wellsoft.context.jdbc.support.QueryData;
import com.wellsoft.context.jdbc.support.QueryInfo;
import com.wellsoft.pt.mt.service.TenantTemplateService;
import com.wellsoft.pt.repository.entity.mongo.file.MongoFileEntity;
import com.wellsoft.pt.repository.service.MongoFileService;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Description: 如何描述该类
 *
 * @author linz
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016年1月26日.1	linz		2016年1月26日		Create
 * </pre>
 * @date 2016年1月26日
 */
@Controller
@RequestMapping("/superadmin/tenantTemplate")
public class TenantTemplateController extends JqGridQueryController {
    @Autowired
    private TenantTemplateService tenantTemplateService;
    @Autowired
    private MongoFileService mongoFileService;

    @RequestMapping(value = "/list")
    public String dependency(HttpServletRequest request, HttpServletResponse response) throws IOException {
        return forward("/superadmin/tenant/tenant_template");
    }

    @RequestMapping(value = "/dataGrid", method = RequestMethod.POST)
    @ResponseBody
    public JqGridQueryData dataGrid(@RequestParam(value = "searchValue") String queryValue, HttpServletRequest request,
                                    JqGridQueryInfo jqGridQueryInfo) {
        QueryInfo queryInfo = buildQueryInfo(jqGridQueryInfo, request);
        QueryData queryData = tenantTemplateService.queryDataGrid(queryValue, jqGridQueryInfo, queryInfo);
        return convertToJqGridQueryData(queryData);
    }

    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    @ResponseBody
    public void upload(@RequestParam(value = "fileUpload") MultipartFile upload, HttpServletResponse response)
            throws IOException {
        String fileId = "";
        try {
            MongoFileEntity fileEntity = mongoFileService.saveFile(upload.getOriginalFilename(),
                    upload.getInputStream());
            fileId = fileEntity.getId();
        } catch (IOException e) {
            logger.error(ExceptionUtils.getStackTrace(e));
        }
        response.setContentType(MediaType.TEXT_HTML_VALUE);
        response.getWriter().write(fileId);
        response.getWriter().flush();
        response.getWriter().close();
    }
}
