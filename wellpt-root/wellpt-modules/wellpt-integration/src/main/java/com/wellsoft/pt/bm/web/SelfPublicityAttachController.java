/*
 * @(#)2013-12-9 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bm.web;

import com.wellsoft.context.config.Config;
import com.wellsoft.context.util.file.FileDownloadUtils;
import com.wellsoft.context.util.web.ServletUtils;
import com.wellsoft.context.web.controller.AbstractJsonDataServicesController;
import com.wellsoft.context.web.controller.ResultMessage;
import com.wellsoft.pt.bm.service.SelfPublicityAsyncService;
import com.wellsoft.pt.mt.entity.Tenant;
import com.wellsoft.pt.mt.facade.service.TenantFacadeService;
import com.wellsoft.pt.repository.entity.mongo.file.MongoFileEntity;
import com.wellsoft.pt.repository.service.MongoFileService;
import com.wellsoft.pt.security.util.IgnoreLoginUtils;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.*;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-12-9.1	zhulh		2013-12-9		Create
 * </pre>
 * @date 2013-12-9
 */
@Controller
@RequestMapping("/self/publicity/attach")
public class SelfPublicityAttachController extends AbstractJsonDataServicesController {

    private static final String KEY_SELF_PUBLICITY_ATTACH_ALLOWED_DOWNLOAD_IP = "self.publicity.attach.allowed.download.ip";
    private static final String allowedDownloadIp = Config.getValue(KEY_SELF_PUBLICITY_ATTACH_ALLOWED_DOWNLOAD_IP);
    ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private MongoFileService fileService;

    @Autowired
    private TenantFacadeService tenantService;

    @Autowired
    private SelfPublicityAsyncService selfPublicityAsyncService;

    @RequestMapping("/{tenant}/download/{uuid:.+}")
    public void download(@PathVariable(value = "tenant") String tenant, @PathVariable(value = "uuid") String uuid,
                         @RequestParam(value = "fileName") String fileName, HttpServletRequest request, HttpServletResponse response) {
        try {
            prepare(tenant, request);

            // Enumeration<String> e = request.getHeaderNames();
            // while (e.hasMoreElements()) {
            // String name = e.nextElement();
            // System.out.println(name + "\t" + request.getHeader(name));
            // }

            InputStream fileInputStream = fileService.getFile(uuid).getInputstream();
            // FileDownloadUtils.download(fileInputStream, response, fileName);
            FileDownloadUtils.download(request, response, fileInputStream, fileName);
        } catch (Exception e) {
            logger.info(e.getMessage());
            FileDownloadUtils
                    .download(request, response, new ByteArrayInputStream(e.getMessage().getBytes()), fileName);
        } finally {
            IgnoreLoginUtils.logout();
        }
    }

    /**
     * 商事登记获取文件列表
     *
     * @param nodeUuid
     * @return
     */
    @RequestMapping(value = "/{tenant}/async")
    @ResponseBody
    public ResultMessage async(@PathVariable(value = "tenant") String tenant, @RequestBody String requestUuid,
                               HttpServletRequest request) {
        ResultMessage msg = new ResultMessage();
        try {
            prepare(tenant, request);

            JSONObject jsonObject = new JSONObject(requestUuid);
            String uuid = jsonObject.getString("uuid");
            selfPublicityAsyncService.asyncAttach(uuid);
            msg.setData(uuid);
        } catch (Exception e) {
            logger.info(e.getMessage());
            return getFaultMessage(e);
        } finally {
            IgnoreLoginUtils.logout();
        }
        return msg;
    }

    /**
     * 商事登记获取文件列表
     *
     * @param nodeUuid
     * @return
     */
    @RequestMapping(value = "/{tenant}/list")
    @ResponseBody
    public ResultMessage getFiles(@PathVariable(value = "tenant") String tenant, @RequestBody String requestUuids,
                                  HttpServletRequest request) {
        ResultMessage msg = new ResultMessage();
        try {
            prepare(tenant, request);

            JSONObject jsonObject = new JSONObject(requestUuids);
            JSONArray a = jsonObject.getJSONArray("uuids");
            String[] uuids = objectMapper.readValue(a.toString(), String[].class);
            Map<String, List<Map<String, Object>>> data = new HashMap<String, List<Map<String, Object>>>();
            for (String nodeUuid : uuids) {
                List<MongoFileEntity> fileEntities = fileService.getFilesFromFolder(nodeUuid, nodeUuid);

                List<Map<String, Object>> files = new ArrayList<Map<String, Object>>();
                for (MongoFileEntity fileEntity : fileEntities) {
                    Map<String, Object> fileMap = new HashMap<String, Object>();
                    fileMap.put("fileName", fileEntity.getFileName());
                    fileMap.put("size", fileEntity.getLength());

                    files.add(fileMap);
                }

                data.put(nodeUuid, files);
            }
            msg.setData(data);
        } catch (Exception e) {
            logger.info(e.getMessage());
            return getFaultMessage(e);
        } finally {
            IgnoreLoginUtils.logout();
        }
        return msg;
    }

    /**
     * 如何描述该方法
     *
     * @throws Exception
     */
    private void prepare(String tenant, HttpServletRequest request) throws Exception {
        checkedRemoteIp(request);

        Tenant tenantAccount = tenantService.getByAccount(tenant);
        IgnoreLoginUtils.login(tenantAccount.getId(), tenantAccount.getId());
    }

    /**
     * @param request
     */
    private void checkedRemoteIp(HttpServletRequest request) {
        if (allowedDownloadIp != null) {
            String requestIp = ServletUtils.getRemoteAddr(request);
            List<String> allowedDownloadIps = Arrays.asList(StringUtils.split(allowedDownloadIp, ", "));
            if (!allowedDownloadIps.isEmpty() && !allowedDownloadIps.contains(requestIp)) {
                throw new RuntimeException("不允许从ip地址[" + requestIp + "]下载附件!");
            }
        }
    }
}
