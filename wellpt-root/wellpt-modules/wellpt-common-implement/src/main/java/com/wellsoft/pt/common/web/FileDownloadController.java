/*
 * @(#)2 Dec 2016 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.common.web;

import com.wellsoft.context.exception.JsonDataException;
import com.wellsoft.context.util.file.FileDownloadUtils;
import com.wellsoft.context.util.json.JsonUtils;
import com.wellsoft.context.util.web.JsonDataServicesContextHolder;
import com.wellsoft.pt.common.web.json.JsonData;
import com.wellsoft.pt.common.web.json.JsonDataServicesController;
import com.wellsoft.pt.security.util.IgnoreLoginUtils;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.json.JSONArray;
import org.json.JSONException;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.activation.DataSource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.Method;

/**
 * Description: 如何描述该类
 *
 * @author Xiem
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2 Dec 2016.1	Xiem		2 Dec 2016		Create
 * </pre>
 * @date 2 Dec 2016
 */
@Api(tags = "文件下载服务")
@Controller
@RequestMapping("/file/download/services")
public class FileDownloadController extends JsonDataServicesController {
    /**
     * 跳转到数据库定义界面
     *
     * @return
     * @throws IOException
     * @throws JSONException
     * @throws IllegalArgumentException
     * @throws com.fr.json.JSONException
     */
    @ApiOperation(value = "文件下载接口", notes = "文件下载接口")
    @RequestMapping(value = "", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public void download(@RequestParam(value = "jsonData") String jsonDataStr,
                         @RequestParam(value = "args") String argsStr, HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        JsonData jsonData = JsonUtils.json2Object(jsonDataStr, JsonData.class);
        jsonData.setArgs(argsStr);
        String tenantId = jsonData.getTenantId();
        String userId = jsonData.getUserId();
        String currentTenantId = SpringSecurityUtils.getCurrentTenantId();
        try {
            JsonDataServicesContextHolder.setRequestResponse(request, response);
            if (StringUtils.isNotBlank(tenantId) && StringUtils.isNotBlank(currentTenantId)
                    && !StringUtils.equals(tenantId, currentTenantId)) {
                IgnoreLoginUtils.login(tenantId, userId);
            }

            authenticate(jsonData);

            checkAndPrepare(jsonData);

            String json = jsonData.getArgs();
            JSONArray jsonArray = new JSONArray(json);

            Object bean = getService(jsonData.getServiceName());
            Method method = getMethod(jsonData, jsonArray, bean);
            if (!method.getReturnType().isAssignableFrom(DataSource.class)) {
                throw new RuntimeException("函数[" + jsonData.getMethodName() + "] 的返回类型 ["
                        + method.getReturnType().getName() + "] 不是 ExportDataSource类型");
            }
            Object[] args = getMethodArgumentValues(jsonData, jsonArray, bean, method);
            DataSource returnValue = (DataSource) method.invoke(bean, args);

            FileDownloadUtils.download(request, response, returnValue.getInputStream(), returnValue.getName(),
                    returnValue.getContentType());
        } catch (Exception e) {
            if (!(e instanceof JsonDataException)) {
                StringWriter writer = new StringWriter();
                try {
                    objectMapper.writeValue(writer, jsonData);
                } catch (JsonGenerationException e1) {
                    logger.error(ExceptionUtils.getStackTrace(e1));
                } catch (JsonMappingException e1) {
                    logger.error(ExceptionUtils.getStackTrace(e1));
                } catch (IOException e1) {
                    logger.error(ExceptionUtils.getStackTrace(e1));
                }
                logger.error("JDS调用异常: " + writer.toString());
                logger.error(ExceptionUtils.getStackTrace(e));
                throw new RuntimeException(e);
            }
        } finally {
            JsonDataServicesContextHolder.remove();
            if (StringUtils.isNotBlank(tenantId) && StringUtils.isNotBlank(currentTenantId)
                    && !StringUtils.equals(tenantId, currentTenantId)) {
                IgnoreLoginUtils.logout();
            }
        }
    }
}
