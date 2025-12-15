/*
 * @(#)5/22/25 V1.0
 *
 * Copyright 2025 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.weixin.web;

import com.wellsoft.context.web.controller.BaseController;
import com.wellsoft.context.web.controller.IgnoreResultAdvice;
import com.wellsoft.pt.app.weixin.facade.service.WeixinConfigFacadeService;
import com.wellsoft.pt.app.weixin.facade.service.WeixinOrgSyncFacadeService;
import com.wellsoft.pt.app.weixin.service.WeixinDeptService;
import com.wellsoft.pt.app.weixin.support.WeixinEventHoler;
import com.wellsoft.pt.app.weixin.support.aes.WXBizJsonMsgCrypt;
import com.wellsoft.pt.app.weixin.utils.WeixinApiUtils;
import com.wellsoft.pt.app.weixin.utils.WeixinEventUtils;
import com.wellsoft.pt.app.weixin.utils.WeixinXmlUtils;
import com.wellsoft.pt.app.weixin.vo.WeixinConfigVo;
import com.wellsoft.pt.app.weixin.vo.WeixinDepartmentVo;
import com.wellsoft.pt.security.access.RequestSystemContextPathResolver;
import com.wellsoft.pt.security.util.IgnoreLoginUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.BooleanUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Description:
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 5/22/25.1	    zhulh		5/22/25		    Create
 * </pre>
 * @date 5/22/25
 */
@Api(tags = "微信回调")
@RestController
@RequestMapping("/api/weixin/callback")
public class ApiWeixinCallbackController extends BaseController {

//    @Autowired
//    private ScheduledExecutorService scheduledExecutorService;

    @Autowired
    private WeixinConfigFacadeService weixinConfigFacadeService;

    @Autowired
    private WeixinOrgSyncFacadeService weixinOrgSyncFacadeService;

    @Autowired
    private WeixinDeptService weixinDeptService;

    @IgnoreResultAdvice
    @ApiOperation(value = "微信通讯录回调", notes = "微信通讯录回调")
    @GetMapping(value = "/org/{systemId}")
    public ResponseEntity<String> verifyUrl(@PathVariable(name = "systemId") String systemId,
                                            @RequestParam(name = "msg_signature") String msgSignature,
                                            @RequestParam(name = "timestamp") String timestamp,
                                            @RequestParam(name = "nonce") String nonce,
                                            @RequestParam(name = "echostr") String echostr) {
        WeixinConfigVo weixinConfigVo = weixinConfigFacadeService.getVoBySystem(systemId);
        WeixinConfigVo.WeixinOrgSyncOption orgSyncOption = weixinConfigVo.getConfiguration().getOrgSyncOption();
        String sToken = orgSyncOption.getEventToken(); // "38FpWBe";
        String sEncodingAESKey = orgSyncOption.getEventAesKey();// "yMWBAgexI3ymifEGKL0Mcb6bNzjaXJNa5ZcszoLdIVf";
        String sCorpID = weixinConfigVo.getCorpId();// "wwdbeb091f2c5110cb";
        String sEchoStr = null;
        try {
            WXBizJsonMsgCrypt wxcpt = new WXBizJsonMsgCrypt(sToken, sEncodingAESKey, sCorpID);
            sEchoStr = wxcpt.VerifyURL(msgSignature, timestamp, nonce, echostr);
            logger.info("verifyurl echostr: " + sEchoStr);
            // 验证URL成功，将sEchoStr返回
            // HttpUtils.SetResponse(sEchoStr);
        } catch (Exception e) {
            // 验证URL失败，错误原因请查看异常
            logger.error(e.getMessage(), e);
        }
        return new ResponseEntity(sEchoStr, HttpStatus.OK);
    }

    @IgnoreResultAdvice
    @ApiOperation(value = "微信通讯录回调", notes = "微信通讯录回调")
    @PostMapping(value = "/org/{systemId}")
    public ResponseEntity<String> callback(@PathVariable(name = "systemId") String systemId,
                                           @RequestParam(name = "msg_signature") String msgSignature,
                                           @RequestParam(name = "timestamp") String timestamp,
                                           @RequestParam(name = "nonce") String nonce, @RequestBody String requestBody) {
        WeixinConfigVo weixinConfigVo = weixinConfigFacadeService.getVoBySystem(systemId);
        if (BooleanUtils.isNotTrue(weixinConfigVo.getEnabled())) {
            logger.error("微信接入配置不存在或未启用");
            return new ResponseEntity(HttpStatus.OK);
        }

        WeixinConfigVo.WeixinOrgSyncOption orgSyncOption = weixinConfigVo.getConfiguration().getOrgSyncOption();
        String sToken = orgSyncOption.getEventToken(); // "38FpWBe";
        String sEncodingAESKey = orgSyncOption.getEventAesKey();// "yMWBAgexI3ymifEGKL0Mcb6bNzjaXJNa5ZcszoLdIVf";
        String sCorpID = weixinConfigVo.getCorpId();// "wwdbeb091f2c5110cb";
        try {
            WXBizJsonMsgCrypt wxcpt = new WXBizJsonMsgCrypt(sToken, sEncodingAESKey, sCorpID);
            String xmlContent = wxcpt.DecryptMsg(msgSignature, timestamp, nonce, requestBody);
            JSONObject jsonObject = WeixinXmlUtils.documentToJSONObject(xmlContent);
            // scheduledExecutorService.execute(() -> handleWeixinOrgEvent(systemId, jsonObject, weixinConfigVo));
            handleWeixinOrgEvent(systemId, jsonObject, weixinConfigVo);
        } catch (Exception e) {
            // 验证URL失败，错误原因请查看异常
            logger.error(e.getMessage(), e);
        }
        return new ResponseEntity(HttpStatus.OK);
    }

    /**
     * @param systemId
     * @param jsonObject
     * @param weixinConfigVo
     */
    private void handleWeixinOrgEvent(String systemId, JSONObject jsonObject, WeixinConfigVo weixinConfigVo) {
        if (!jsonObject.has("ChangeType")) {
            logger.error("微信变更类型不存在");
            return;
        }

        String eventId = jsonObject.getString("CreateTime") + "_" + jsonObject.getString("ChangeType");
        if (WeixinEventUtils.existsEvent(eventId)) {
            return;
        }

        try {
            WeixinEventUtils.addEvent(eventId);
            RequestSystemContextPathResolver.setSystem(weixinConfigVo.getSystem());
            IgnoreLoginUtils.login(weixinConfigVo.getTenant(), weixinConfigVo.getCreator());

            WeixinEventHoler.create(jsonObject, weixinConfigVo);
            // 事件类型
            String changeType = jsonObject.getString("ChangeType");
            if (!isRegistryEvent(weixinConfigVo, changeType)) {
                WeixinEventHoler.error("应用配置没有监听事件");
                return;
            }

            // 处理事件
            switch (changeType) {
                // 新增成员事件
                case "create_user":
                    handleUserCreated(jsonObject, weixinConfigVo);
                    break;
                // 更新成员事件
                case "update_user":
                    handleUserUpdated(jsonObject, weixinConfigVo);
                    break;
                // 删除成员事件
                case "delete_user":
                    handleUserDeleted(jsonObject, weixinConfigVo);
                    break;
                // 新增部门事件
                case "create_party":
                    handleDepartmentCreated(jsonObject, weixinConfigVo);
                    break;
                // 更新部门事件
                case "update_party":
                    handleDepartmentUpdated(jsonObject, weixinConfigVo);
                    break;
                // 删除部门事件
                case "delete_party":
                    handleDepartmentDeleted(jsonObject, weixinConfigVo);
                    break;
            }
        } catch (Exception e) {
            WeixinEventUtils.removeEvent(eventId);
            WeixinEventHoler.error(e.getMessage());
            logger.error(e.getMessage(), e);
        } finally {
            WeixinEventHoler.commit();
            IgnoreLoginUtils.logout();
            RequestSystemContextPathResolver.clear();
        }
    }

    private boolean isRegistryEvent(WeixinConfigVo weixinConfigVo, String eventType) {
        List<String> orgSyncEvents = weixinConfigVo.getConfiguration().getOrgSyncEvents();
        return CollectionUtils.isNotEmpty(orgSyncEvents) && orgSyncEvents.contains(eventType);
    }

    private void handleUserCreated(JSONObject jsonObject, WeixinConfigVo weixinConfigVo) {
        String userId = jsonObject.getString("UserID");
        WeixinDepartmentVo.User user = WeixinApiUtils.getUserByUserId(userId, weixinConfigVo);
        weixinOrgSyncFacadeService.createUser(user, weixinConfigVo);
    }

    private void handleUserUpdated(JSONObject jsonObject, WeixinConfigVo weixinConfigVo) {
        String userId = jsonObject.getString("UserID");
        WeixinDepartmentVo.User user = WeixinApiUtils.getUserByUserId(userId, weixinConfigVo);
        weixinOrgSyncFacadeService.updateUser(user, weixinConfigVo);
    }

    private void handleUserDeleted(JSONObject jsonObject, WeixinConfigVo weixinConfigVo) {
        String userId = jsonObject.getString("UserID");
        weixinOrgSyncFacadeService.deleteUser(userId, weixinConfigVo);
    }

    private void handleDepartmentCreated(JSONObject jsonObject, WeixinConfigVo weixinConfigVo) {
        String deptId = jsonObject.getString("Id");
        WeixinDepartmentVo.Department department = WeixinApiUtils.getDepartmentById(deptId, weixinConfigVo);
        if (department.getParentId() > 1) {
            createParentDepartmentIfAbsent(department.getParentId(), weixinConfigVo);
        }
        weixinOrgSyncFacadeService.createDepartment(department, weixinConfigVo);
    }

    /**
     * @param parentId
     * @param weixinConfigVo
     */
    private void createParentDepartmentIfAbsent(Long parentId, WeixinConfigVo weixinConfigVo) {
        boolean existsDept = weixinDeptService.existsByIdAndConfigUuid(parentId, weixinConfigVo.getUuid());
        if (BooleanUtils.isNotTrue(existsDept)) {
            WeixinDepartmentVo.Department department = WeixinApiUtils.getDepartmentById(parentId.toString(), weixinConfigVo);
            if (department.getParentId() > 1) {
                createParentDepartmentIfAbsent(department.getParentId(), weixinConfigVo);
            }
            weixinOrgSyncFacadeService.createDepartment(department, weixinConfigVo);
        }
    }

    private void handleDepartmentUpdated(JSONObject jsonObject, WeixinConfigVo weixinConfigVo) {
        String deptId = jsonObject.getString("Id");
        WeixinDepartmentVo.Department department = WeixinApiUtils.getDepartmentById(deptId, weixinConfigVo);
        weixinOrgSyncFacadeService.updateDepartment(department, weixinConfigVo);
    }

    private void handleDepartmentDeleted(JSONObject jsonObject, WeixinConfigVo weixinConfigVo) {
        String deptId = jsonObject.getString("Id");
        weixinOrgSyncFacadeService.deleteDepartment(Long.valueOf(deptId), weixinConfigVo);
    }


}
