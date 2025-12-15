/*
 * @(#)2012-10-19 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.workflow.web;

import com.wellsoft.context.enums.Encoding;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.exception.FieldValidationException;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.context.web.controller.BaseController;
import com.wellsoft.context.web.controller.ResultMessage;
import com.wellsoft.pt.bpm.engine.org.service.WorkflowOrgService;
import com.wellsoft.pt.bpm.engine.parser.FlowDictionary;
import com.wellsoft.pt.bpm.engine.service.FlowManagementService;
import com.wellsoft.pt.bpm.engine.support.ManagementType;
import com.wellsoft.pt.bpm.engine.support.WorkFlowSettings;
import com.wellsoft.pt.workflow.service.FlowSchemeService;
import com.wellsoft.pt.workflow.service.WfFlowSettingService;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.ConstraintViolationException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Description: 工作流基础数据加载控制器
 * 1、基础数据加载		/workflow/scheme/diction/xml
 * 2、流程定义加载		/workflow/scheme/flow/xml
 * 3、保存流程定义		/workflow/scheme/save
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 2012-10-19.1	zhulh		2012-10-19		Create
 * </pre>
 * @date 2012-10-19
 */
@Controller
@RequestMapping({"/workflow/scheme", "/api/workflow/scheme"})
public class FlowSchemeController extends BaseController {
    @Autowired
    private FlowSchemeService flowSchemeService;

    @Autowired
    private FlowManagementService flowManagementService;

    @Autowired
    private WfFlowSettingService flowSettingService;

    /**
     * 返回验证错误信息
     *
     * @param e
     * @return
     */
    @SuppressWarnings("unchecked")
    private static ResponseEntity<String> extractValidationErrorMsg(ConstraintViolationException e) {
        List<Map<String, Object>> errorData = (List<Map<String, Object>>) FieldValidationException.extractData(e);
        StringBuilder sb = new StringBuilder();
        Iterator<Map<String, Object>> it = errorData.iterator();
        while (it.hasNext()) {
            Map<String, Object> error = it.next();
            String field = (String) error.get("field");
            if ("name".equals(field)) {
                sb.append("名称");
            }
            if ("id".equals(field)) {
                sb.append("ID");
            }
            if ("code".equals(field)) {
                sb.append("编号");
            }
            sb.append(error.get("msg"));
            if (it.hasNext()) {
                sb.append(Separator.SEMICOLON.getValue());
            }
        }
        return new ResponseEntity<String>(StringUtils.replace(sb.toString(), "{max}", "255"),
                HttpStatus.EXPECTATION_FAILED);
    }

    /**
     * 基础数据加载
     * 原请求flow_scheme!dictionXml.action
     *
     * @return
     */
    @RequestMapping(value = "/diction/xml", produces = MediaType.APPLICATION_XML_VALUE)
    public @ResponseBody
    String dictionXml(@RequestParam(value = "id", required = false) String id,
                      @RequestParam(value = "moduleId", required = false) String moduleId) {
        String dictionxml = flowSchemeService.getDictionXml(id, moduleId);
        return dictionxml;
    }

    /**
     * 流程定义UUID加载
     * 原请求flow_scheme!flowXml.action
     *
     * @return
     */
    @RequestMapping(value = "/flow/xml", produces = MediaType.APPLICATION_XML_VALUE)
    public @ResponseBody
    String flowXml(@RequestParam("id") String id) {
        String xml = null;
        try {
            xml = flowSchemeService.getFlowXml(id);
        } catch (Exception e) {
            logger.error(ExceptionUtils.getStackTrace(e));
            xml = e.getMessage();
        }
        return xml;
        // return flowDefineService.get(id).getContent();
        // return
        // "<flow name=\"sf\" id=\"sf\" sn=\"\"
        // version=\"1.0\"><property><categorySN>01</categorySN><formID>One</formID><equalFlow><name></name><id></id></equalFlow><bakUsers/><creators/><users/><monitors/><admins/><fileRecipients/><msgRecipients/><isFree>0</isFree><isActive>1</isActive><dueTime></dueTime><timeUnit>3</timeUnit><isSendFile>0</isSendFile><isSendMsg>0</isSendMsg><fileTemplate></fileTemplate><printTemplate></printTemplate></property><timers/><tasks><task
        // name=\"环节1\" id=\"T677\"
        // type=\"1\"><X>280</X><Y>100</Y><conditionName></conditionName><conditionBody></conditionBody><conditionX></conditionX><conditionY></conditionY><conditionLine></conditionLine></task><task
        // name=\"环节2\" id=\"T037\"
        // type=\"1\"><X>520</X><Y>100</Y><conditionName></conditionName><conditionBody></conditionBody><conditionX></conditionX><conditionY></conditionY><conditionLine></conditionLine></task></tasks><directions><direction
        // name=\"送环节1\" id=\"D442\" type=\"1\" fromID=\"&lt;StartFlow&gt;\"
        // toID=\"T677\"><terminalName>开始</terminalName><terminalType>BEGIN</terminalType><terminalX>200</terminalX><terminalY>100</terminalY><terminalBody></terminalBody><lineLabel>201;80</lineLabel><line>BEELINE</line><isShowName>1</isShowName></direction><direction
        // name=\"送环节2\" id=\"D705\" type=\"1\" fromID=\"T677\"
        // toID=\"T037\"><terminalName></terminalName><terminalType></terminalType><terminalX></terminalX><terminalY></terminalY><terminalBody></terminalBody><lineLabel>371;80</lineLabel><line>BEELINE</line><isShowName>1</isShowName></direction><direction
        // name=\"送结束\" id=\"D495\" type=\"1\" fromID=\"T037\"
        // toID=\"&lt;EndFlow&gt;\"><terminalName>结束</terminalName><terminalType>END</terminalType><terminalX>800</terminalX><terminalY>140</terminalY><terminalBody></terminalBody><lineLabel>647;101</lineLabel><line>BEELINE</line><isShowName>1</isShowName></direction></directions><labels/><deletes/></flow>";
    }

    /**
     * 基础数据加载
     *
     * @return
     */
    @RequestMapping(value = "/diction/json")
    public @ResponseBody
    FlowDictionary dictionJson(@RequestParam(value = "uuid", required = false) String uuid,
                               @RequestParam(value = "moduleId", required = false) String moduleId) {
        FlowDictionary flowDictionary = flowSchemeService.getFlowDictionary(uuid, moduleId);
        return flowDictionary;
    }

    /**
     * 流程定义UUID加载
     *
     * @return
     */
    @RequestMapping(value = "/flow/json", produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    String flowJson(@RequestParam("uuid") String uuid) {
        String json = null;
        try {
            json = flowSchemeService.getFlowJson(uuid);
        } catch (Exception e) {
            logger.error(ExceptionUtils.getStackTrace(e));
            json = e.getMessage();
        }
        return json;
    }

    /**
     * 流程定义ID加载
     * 原请求flow_scheme!flowXml.action
     *
     * @return
     */
    @RequestMapping(value = "/flow/xml/id", produces = MediaType.APPLICATION_XML_VALUE)
    public @ResponseBody
    String flowXmlById(@RequestParam("id") String id) {
        String xml = null;
        try {
            xml = flowSchemeService.getFlowXmlById(id);
        } catch (Exception e) {
            logger.error(ExceptionUtils.getStackTrace(e));
            xml = e.getMessage();
        }
        return xml;
        // return flowDefineService.get(id).getContent();
        // return
        // "<flow name=\"sf\" id=\"sf\" sn=\"\"
        // version=\"1.0\"><property><categorySN>01</categorySN><formID>One</formID><equalFlow><name></name><id></id></equalFlow><bakUsers/><creators/><users/><monitors/><admins/><fileRecipients/><msgRecipients/><isFree>0</isFree><isActive>1</isActive><dueTime></dueTime><timeUnit>3</timeUnit><isSendFile>0</isSendFile><isSendMsg>0</isSendMsg><fileTemplate></fileTemplate><printTemplate></printTemplate></property><timers/><tasks><task
        // name=\"环节1\" id=\"T677\"
        // type=\"1\"><X>280</X><Y>100</Y><conditionName></conditionName><conditionBody></conditionBody><conditionX></conditionX><conditionY></conditionY><conditionLine></conditionLine></task><task
        // name=\"环节2\" id=\"T037\"
        // type=\"1\"><X>520</X><Y>100</Y><conditionName></conditionName><conditionBody></conditionBody><conditionX></conditionX><conditionY></conditionY><conditionLine></conditionLine></task></tasks><directions><direction
        // name=\"送环节1\" id=\"D442\" type=\"1\" fromID=\"&lt;StartFlow&gt;\"
        // toID=\"T677\"><terminalName>开始</terminalName><terminalType>BEGIN</terminalType><terminalX>200</terminalX><terminalY>100</terminalY><terminalBody></terminalBody><lineLabel>201;80</lineLabel><line>BEELINE</line><isShowName>1</isShowName></direction><direction
        // name=\"送环节2\" id=\"D705\" type=\"1\" fromID=\"T677\"
        // toID=\"T037\"><terminalName></terminalName><terminalType></terminalType><terminalX></terminalX><terminalY></terminalY><terminalBody></terminalBody><lineLabel>371;80</lineLabel><line>BEELINE</line><isShowName>1</isShowName></direction><direction
        // name=\"送结束\" id=\"D495\" type=\"1\" fromID=\"T037\"
        // toID=\"&lt;EndFlow&gt;\"><terminalName>结束</terminalName><terminalType>END</terminalType><terminalX>800</terminalX><terminalY>140</terminalY><terminalBody></terminalBody><lineLabel>647;101</lineLabel><line>BEELINE</line><isShowName>1</isShowName></direction></directions><labels/><deletes/></flow>";
    }

    /**
     * 验证流程定义
     *
     * @return
     */
    @RequestMapping(value = "/checkFlowXmlForUpdate", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResultMessage checkFlowXmlForUpdate(@RequestBody String xml) {
        ResultMessage resultMsg = new ResultMessage();
        boolean result = false;
        try {
            result = flowSchemeService.checkFlowXmlForUpdate(xml);
            resultMsg.setSuccess(result);
        } catch (Exception e) {
            resultMsg.clear();
            resultMsg.addMessage(e.getMessage());
        }
        resultMsg.setSuccess(result);
        resultMsg.setData(result);
        return resultMsg;
    }

    /**
     * 保存流程定义
     * 原请求flow_scheme!save.action
     *
     * @return
     */
    @RequestMapping(value = "/save", method = RequestMethod.POST, produces = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<String> save(@RequestBody String xml, @RequestParam("pbNew") Boolean pbNew) {
        String returnXml = decodeBase64IfRequired(xml);
        try {
            returnXml = flowSchemeService.saveFlowXml(returnXml, pbNew, false);
        } catch (Exception e) {
            if (e instanceof ConstraintViolationException) {
                return extractValidationErrorMsg((ConstraintViolationException) e);
            }
            logger.error(ExceptionUtils.getStackTrace(e));
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.EXPECTATION_FAILED);
        }
        // System.out.println(returnXml);
        return new ResponseEntity<String>(returnXml, HttpStatus.OK);
    }

    /**
     * 保存流程定义
     * 原请求flow_scheme!save.action
     *
     * @return
     */
    @RequestMapping(value = "/json/save", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> jsonSave(@RequestBody String json, @RequestParam("pbNew") Boolean pbNew) {
        String returnJson = decodeBase64IfRequired(json);
        try {
            returnJson = flowSchemeService.saveFlowJson(returnJson, pbNew, false);
        } catch (Exception e) {
            if (e instanceof ConstraintViolationException) {
                return extractValidationErrorMsg((ConstraintViolationException) e);
            }
            logger.error(ExceptionUtils.getStackTrace(e));
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.EXPECTATION_FAILED);
        }
        // System.out.println(returnXml);
        return new ResponseEntity<String>(returnJson, HttpStatus.OK);
    }

    private String decodeBase64IfRequired(String json) {
        String returnJson = json;
        try {
            WorkFlowSettings workFlowSettings = flowSettingService.getWorkFlowSettings();
            if (StringUtils.equals("base64", workFlowSettings.getSaveEncoder())) {
                returnJson = new String(Base64.getDecoder().decode(json), StandardCharsets.UTF_8);
                if (containsEncodedParts(returnJson)) {
                    return URLDecoder.decode(returnJson, Encoding.UTF8.getValue());
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return returnJson;
    }

    private boolean containsEncodedParts(String base64Json) {
        return StringUtils.contains(base64Json, "%");
    }

    /**
     * 获取选择流程的XML内容
     *
     * @return
     */
    @RequestMapping(value = "/flow/tree/xml", produces = MediaType.APPLICATION_XML_VALUE)
    public @ResponseBody
    String flowTreeXml() {
        return flowSchemeService.getFlowTreeXml();
    }

    @RequestMapping(value = "/update/batch")
    public void update() {
        List<String> flowUuids = flowSchemeService.getAllUuids();
        for (String flowUuid : flowUuids) {
            String xml = null;
            try {
                xml = flowSchemeService.getFlowXml(flowUuid);

                flowSchemeService.saveFlowXml(xml, false, false);
            } catch (Exception e) {
                logger.error(ExceptionUtils.getStackTrace(e));
                xml = e.getMessage();
            }
        }
    }

    @RequestMapping(value = "/update/ldx")
    public void title() {
        List<String> flowUuids = flowSchemeService.getAllUuids();
        for (String flowUuid : flowUuids) {
            String xml = null;
            try {
                xml = flowSchemeService.getFlowXml(flowUuid);
                flowSchemeService.saveFlowXml(xml, false, false);
            } catch (Exception e) {
                logger.error(ExceptionUtils.getStackTrace(e));
                xml = e.getMessage();
            }
        }
    }

    @RequestMapping(value = "/update/viewer")
    public void viewer() {
        // 管理员用户ID
//        OrgApiFacade orgApiFacade = ApplicationContextHolder.getBean(OrgApiFacade.class);
        WorkflowOrgService workflowOrgService = ApplicationContextHolder.getBean(WorkflowOrgService.class);
//        List<String> allAdminIds = orgApiFacade.queryAllAdminIdsByUnitId(SpringSecurityUtils.getCurrentUserUnitId());
        List<String> allAdminIds = workflowOrgService.listCurrentTenantAdminIds();
        List<String> flowDefUuids = flowSchemeService.getAllUuids();
        for (String flowDefUuid : flowDefUuids) {
            flowManagementService.remove(ManagementType.READ, allAdminIds, flowDefUuid);
            // flowManagementService.create(ManagementType.READ, allAdminIds,
            // flowDefUuid);
            flowManagementService.upgrade(flowDefUuid);
        }
    }

    @RequestMapping(value = "/fieldState")
    @ResponseBody
    public Map<String, Map<String, String>> formFieldIsApply(@RequestParam("formUuid") String formUuid,
                                                             @RequestParam("fields") String fields) {
        return flowSchemeService.getElementApplyFormField(formUuid, fields);
    }
}
