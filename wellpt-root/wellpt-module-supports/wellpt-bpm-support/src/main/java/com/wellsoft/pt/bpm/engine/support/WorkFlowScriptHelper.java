/*
 * @(#)2018年8月30日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.support;

import com.google.common.collect.Maps;
import com.wellsoft.context.enums.ModuleID;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.exception.BusinessException;
import com.wellsoft.context.exception.JsonDataException;
import com.wellsoft.context.exception.WorkFlowException;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.context.util.groovy.GroovyUtils;
import com.wellsoft.context.web.controller.ResultMessage;
import com.wellsoft.pt.basicdata.facade.service.BasicDataApiFacade;
import com.wellsoft.pt.basicdata.script.support.ScriptDefinition;
import com.wellsoft.pt.basicdata.script.support.ScriptVariableDefinition;
import com.wellsoft.pt.bpm.engine.context.event.Event;
import com.wellsoft.pt.bpm.engine.core.Script;
import com.wellsoft.pt.dyform.facade.dto.DyFormData;
import com.wellsoft.pt.dyform.facade.service.DyFormFacade;
import com.wellsoft.pt.jpa.template.TemplateEngineFactory;
import com.wellsoft.pt.log.entity.UserOperationLog;
import com.wellsoft.pt.log.service.UserOperationLogService;
import com.wellsoft.pt.security.core.userdetails.UserDetails;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import com.wellsoft.pt.workflow.enums.EnumFlowBizStateValueType;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.script.Bindings;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2018年8月30日.1	zhulh		2018年8月30日		Create
 * </pre>
 * @date 2018年8月30日
 */
public class WorkFlowScriptHelper {

    private static Logger LOG = LoggerFactory.getLogger(WorkFlowScriptHelper.class);

    //	public static final String SCRIPT_GROOVY = "groovy";

    /**
     * 执行事件脚本
     *
     * @param event
     * @param script
     */
    public static void executeEventScript(Event event, Script script) {
        executeEventScript(event, script, new HashMap<String, Object>(0));
    }

    /**
     * 执行事件脚本
     *
     * @param event
     * @param script
     */
    public static void executeEventScript(Event event, Script script,
                                          Map<String, Object> variables) {
        String type = script.getType();
        String contentType = script.getContentType();
        String content = script.getContent();
        if (StringUtils.isBlank(type) || StringUtils.isBlank(content)) {
            return;
        }

        // 获取脚本类型
        String scriptContent = content;
        Map<String, Object> scriptVariables = variables;
        if (scriptVariables == null) {
            scriptVariables = new HashMap<String, Object>(0);
        }
        ScriptDefinition scriptDefinition = getScriptDefinition(contentType, content);
        if (scriptDefinition != null) {
            scriptContent = scriptDefinition.getContent();
            scriptVariables = getScriptVariables(scriptDefinition);
        }

        if ("groovy".equalsIgnoreCase(type)) {//平台执行groovy公用工具类
            UserDetails currentUser = SpringSecurityUtils.getCurrentUser();
            DyFormFacade dyFormFacade = ApplicationContextHolder.getBean(DyFormFacade.class);
            String formUuid = event.getFormUuid();
            String dataUuid = event.getDataUuid();
            DyFormData dyFormData = event.getDyFormData();
            if (dyFormData == null) {
                dyFormData = dyFormFacade.getDyFormData(formUuid, dataUuid);
            }
            ResultMessage resultMessage = new ResultMessage();
            scriptVariables.put("applicationContext", ApplicationContextHolder.getApplicationContext());
            scriptVariables.put("currentUser", currentUser);
            scriptVariables.put("event", event);
            scriptVariables.put("flowInstUuid", event.getFlowInstUuid());
            scriptVariables.put("taskInstUuid", event.getTaskInstUuid());
            scriptVariables.put("taskData", event.getTaskData());
            scriptVariables.put("formUuid", formUuid);
            scriptVariables.put("dataUuid", dataUuid);
            scriptVariables.put("dyFormData", dyFormData);
            scriptVariables.put("dyFormFacade", dyFormFacade);
            scriptVariables.put("actionType", event.getActionType());
            scriptVariables.put("opinionText", event.getTaskOpinionText());
            scriptVariables.put("resultMessage", resultMessage);

            GroovyUtils.run(scriptContent, scriptVariables);
            log(event, script, scriptContent);
            return;
        }

        ScriptEngineManager factory = new ScriptEngineManager();
        ScriptEngine engine = factory.getEngineByName(type);
        if (engine != null) {
            executeScript(event, scriptContent, engine, scriptVariables);
            log(event, script, scriptContent);
        } else {
            String errorMsg = "无法加载类型为[" + type + "]的脚本引擎，请引入该类型脚本对JSR-223规范的实现！";
            LOG.error(errorMsg);
            throw new RuntimeException(errorMsg);
        }
        //		else {
        //			if (StringUtils.equals(SCRIPT_GROOVY, type)) {
        //				executeGroovyScript(event, content);
        //			}
        //		}
    }

    /**
     * @param contentType
     * @param content
     * @return
     */
    private static ScriptDefinition getScriptDefinition(String contentType, String content) {
        if (!StringUtils.equals(contentType, "1")) {
            return null;
        }
        BasicDataApiFacade basicDataApiFacade = ApplicationContextHolder.getBean(
                BasicDataApiFacade.class);
        ScriptDefinition scriptDefinitionEntity = basicDataApiFacade.getScriptDefinitionById(
                content);
        return scriptDefinitionEntity;
    }

    /**
     * @param scriptDefinition
     * @return
     */
    private static Map<String, Object> getScriptVariables(ScriptDefinition scriptDefinition) {
        Map<String, Object> scriptVariables = new HashMap<String, Object>();
        List<ScriptVariableDefinition> variableDefinitions = scriptDefinition.getVariableDefinitions();
        for (ScriptVariableDefinition variableDefinition : variableDefinitions) {
            scriptVariables.put(variableDefinition.getName(), variableDefinition.getValue());
        }
        return scriptVariables;
    }

    /**
     * @param event
     * @param content
     * @param engine
     */
    private static void executeScript(Event event, String content, ScriptEngine engine,
                                      Map<String, Object> scriptVariables) {
        try {
            UserDetails currentUser = SpringSecurityUtils.getCurrentUser();
            DyFormFacade dyFormFacade = ApplicationContextHolder.getBean(DyFormFacade.class);
            String formUuid = event.getFormUuid();
            String dataUuid = event.getDataUuid();
            DyFormData dyFormData = event.getDyFormData();
            if (dyFormData == null) {
                dyFormData = dyFormFacade.getDyFormData(formUuid, dataUuid);
            }
            ResultMessage resultMessage = new ResultMessage();
            Bindings bindings = engine.createBindings();
            bindings.put("applicationContext", ApplicationContextHolder.getApplicationContext());
            bindings.put("currentUser", currentUser);
            bindings.put("event", event);
            bindings.put("flowInstUuid", event.getFlowInstUuid());
            bindings.put("taskInstUuid", event.getTaskInstUuid());
            bindings.put("taskData", event.getTaskData());
            bindings.put("formUuid", formUuid);
            bindings.put("dataUuid", dataUuid);
            bindings.put("dyFormData", dyFormData);
            bindings.put("dyFormFacade", dyFormFacade);
            bindings.put("actionType", event.getActionType());
            bindings.put("opinionText", event.getTaskOpinionText());
            bindings.put("resultMessage", resultMessage);
            // 外部传入的变量
            for (Entry<String, Object> entry : scriptVariables.entrySet()) {
                bindings.put(entry.getKey(), entry.getValue());
            }
            engine.eval(content, bindings);
            if (!resultMessage.isSuccess()) {
                throw new BusinessException(resultMessage.getMsg().toString());
            }
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            JsonDataException jsonDataException = getJsonDataException(e);
            if (jsonDataException != null) {
                throw jsonDataException;
            } else {
                throw new WorkFlowException(
                        "执行事件脚本出现异常: " + e.getMessage() + " <br/>脚本内容: " + content);
            }
        }
    }

    /**
     * 计算条件表达式
     *
     * @param event
     * @param scriptType
     * @param expression
     * @return
     */
    public static boolean evaluateConditionExpression(Event event, String scriptType, String expression) {
        boolean result = false;
        EnumFlowBizStateValueType stateValueType = EnumFlowBizStateValueType.getByValue(scriptType);
        if (stateValueType == null) {
            stateValueType = EnumFlowBizStateValueType.Groovy;
        }
        switch (stateValueType) {
            case Groovy:
                result = evaluateGroovyConditionExpression(event, expression);
                break;
            default:
        }
        return result;
    }

    /**
     * 计算groovy条件表达式
     *
     * @param event
     * @param expression
     * @return
     */
    private static boolean evaluateGroovyConditionExpression(Event event, String expression) {
        if (StringUtils.isNotBlank(expression)) {
            try {
                return Boolean.TRUE.equals(GroovyUtils.run(expression, getScriptVariables(event)));
            } catch (Exception e) {
                LOG.error(String.format("流程执行groovy条件表达式脚本[%s]出错！", expression), e);
                throw new RuntimeException(e);
            }
        }
        return false;
    }

    /**
     * 获取脚本变量
     *
     * @param event
     * @return
     */
    public static Map<String, Object> getScriptVariables(Event event) {
        // 表单数据
        DyFormData dyFormData = event.getDyFormData();
        if (dyFormData == null) {
            dyFormData = ApplicationContextHolder.getBean(DyFormFacade.class).getDyFormData(event.getFormUuid(), event.getDataUuid());
        }
        Map<String, Object> formData = Maps.newHashMap();
        Map<String, List<Map<String, Object>>> formDatas = dyFormData.getFormDatas();
        if (MapUtils.isNotEmpty(formDatas)) {
            for (Map.Entry<String, List<Map<String, Object>>> entry : formDatas.entrySet()) {
                formData.put(ApplicationContextHolder.getBean(DyFormFacade.class).getFormIdByFormUuid(entry.getKey()), entry.getValue());
            }
        }
        formData.put(dyFormData.getFormId(), dyFormData.getFormDataOfMainform());

        Map<String, Object> values = Maps.newHashMap();
        values.put("event", event);
        values.put("dyFormData", dyFormData);
        values.put("formData", formData);

        // 当前用户相关变量
        values.putAll(TemplateEngineFactory.getExplainRootModel());
        return values;
    }

    /**
     * @param event
     * @param script
     */
    private static void log(Event event, Script script, String scriptContent) {
        UserOperationLogService userOperationLogService = ApplicationContextHolder
                .getBean(UserOperationLogService.class);
        // 用户操作日志
        UserOperationLog log = new UserOperationLog();
        log.setModuleId(ModuleID.WORKFLOW.getValue());
        log.setModuleName("工作流程");
        log.setContent(event.getTaskInstUuid());
        log.setOperation("执行事件脚本");
        log.setUserName(SpringSecurityUtils.getCurrentUserName());
        StringBuilder details = new StringBuilder();
        details.append("流程实例: " + event.getFlowInstUuid());
        details.append("环节实例: " + event.getTaskInstUuid());
        details.append("事件脚本: " + script.getPointcut() + ", ");
        details.append(script.getType() + ", " + script.getContentType() + ", " + Separator.LINE);
        if (StringUtils.equals(script.getContent(), scriptContent)) {
            details.append(script.getContent());
        } else {
            details.append(script.getContent() + ":" + scriptContent);
        }
        log.setDetails(details.toString());
        userOperationLogService.save(log);
    }

    //	/**
    //	 * @param event
    //	 * @param content
    //	 */
    //	private static void executeGroovyScript(Event event, String content) {
    //		try {
    //			// 从Java代码中调用Groovy语句
    //			UserDetails currentUser = SpringSecurityUtils.getCurrentUser();
    //			Binding binding = new Binding();
    //			binding.setVariable("applicationContext", ApplicationContextHolder.getApplicationContext());
    //			binding.setVariable("currentUser", currentUser);
    //			binding.setVariable("event", event);
    //			binding.setVariable("taskData", event.getTaskData());
    //			binding.setVariable("dyFormData", event.getDyFormData());
    //			binding.setVariable("dyFormFacade", ApplicationContextHolder.getBean(DyFormFacade.class));
    //			binding.setVariable("formUuid", event.getFormUuid());
    //			binding.setVariable("dataUuid", event.getDataUuid());
    //			binding.setVariable("actionType", event.getActionType());
    //			binding.setVariable("taskInstUuid", event.getTaskInstUuid());
    //			binding.setVariable("flowInstUuid", event.getFlowInstUuid());
    //			binding.setVariable("opinionText", event.getTaskOpinionText());
    //			GroovyShell shell = new GroovyShell(binding);
    //			shell.evaluate(content);
    //		} catch (Exception e) {
    //			LOG.error(e.getMessage(), e);
    //			throw new WorkFlowException("执行事件脚步出现异常: " + e.getMessage());
    //		}
    //	}

    /**
     * @param e
     * @return
     */
    private static JsonDataException getJsonDataException(Exception e) {
        InvocationTargetException tmp = null;
        Throwable target = e;
        Throwable cause = target.getCause();
        while (target != null) {
            if (target instanceof JsonDataException) {
                return (JsonDataException) target;
            }
            if (cause instanceof InvocationTargetException) {
                tmp = (InvocationTargetException) cause;
                target = tmp.getTargetException();
                cause = target.getCause();
            } else {
                cause = target;
                target = target.getCause();
            }
            if (target == null) {
                target = cause;
                break;
            }
        }
        return null;
    }

}
