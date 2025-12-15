/*
 * @(#)Feb 20, 2017 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.web;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.web.controller.AbstractJsonDataServicesController;
import com.wellsoft.context.web.controller.ResultMessage;
import com.wellsoft.pt.app.design.config.BootstrapTableConfiguration;
import com.wellsoft.pt.app.support.WidgetDefinitionUtils;
import com.wellsoft.pt.dms.config.support.Configuration;
import com.wellsoft.pt.dms.config.support.ConfigurationBuilder;
import com.wellsoft.pt.dms.core.context.ActionContext;
import com.wellsoft.pt.dms.core.context.ActionContextHolder;
import com.wellsoft.pt.dms.core.context.ActionContextImpl;
import com.wellsoft.pt.dms.core.web.action.Action;
import com.wellsoft.pt.dms.core.web.action.ActionInvocation;
import com.wellsoft.pt.dms.core.web.action.ActionManager;
import com.wellsoft.pt.dms.core.web.interceptor.ActionHandlerInterceptor;
import org.apache.commons.io.Charsets;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.HandlerMethodReturnValueHandlerComposite;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestResponseBodyMethodProcessor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Description: 数据管理服务统一控制层
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * Feb 20, 2017.1	zhulh		Feb 20, 2017		Create
 * </pre>
 * @date Feb 20, 2017
 */
@Controller
@RequestMapping("/dms/data/services")
public class DmsDataServicesController extends AbstractController {

    /**
     * 如何描述UNDEFINED
     */
    private static final String UNDEFINED = "undefined";
    private static final String PARAM_LV_ID = "lv_id";
    private static final String PARAM_DMS_ID = "dms_id";
    private static final String PARAM_AC_ID = "ac_id";
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private RequestMappingHandlerAdapter handlerAdapter;

    @Autowired
    private ActionManager actionManager;

    @Autowired(required = false)
    private Map<String, ActionHandlerInterceptor> interceptorMap = new ConcurrentHashMap<String, ActionHandlerInterceptor>();

    /**
     * (non-Javadoc)
     *
     * @see org.springframework.web.servlet.mvc.AbstractController#handleRequestInternal(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        StopWatch timer = new StopWatch("DmsDataServicesController.handleRequestInternal");
        timer.start("actionContext.initialize");
        ActionContext actionContext = loadActionContext(request, response);
        timer.stop();
        ActionInvocation actionInvocation = null;
        try {
            ActionContextHolder.setContext(actionContext);

            actionInvocation = createActionInvocationHandlerMethod(request, response);

            JsonElement jsonElement = null;
            timer.start("requestInputStream2Json");
            String bodyJson = IOUtils.toString(request.getInputStream(), Charsets.UTF_8);
            if (StringUtils.isNotBlank(bodyJson)) {
                jsonElement = new Gson().fromJson(bodyJson, JsonElement.class);
            }
            timer.stop();

            // 拦截器操作前处理
            if (!applyPreHandle(request, response, actionContext, actionInvocation, jsonElement)) {
                return null;
            }

            timer.start("[" + actionContext.getActionId() + "] - actionInvocation");
            ModelAndView modelAndView = handlerAdapter.handle(request, response, actionInvocation);
            timer.stop();


            // 拦截器操作后处理
            applyPostHandle(request, response, actionContext, actionInvocation, modelAndView, jsonElement);

            if (modelAndView != null) {
                // 由前端nodeserver服务请求，转换model的属性对象为json输出
                List<HandlerMethodReturnValueHandler> returnValueHandlers = handlerAdapter.getReturnValueHandlers();
                for (HandlerMethodReturnValueHandler valueHandler : returnValueHandlers) {
                    if (valueHandler instanceof RequestResponseBodyMethodProcessor) {
                        Map<String, Object> value = Maps.newHashMap();
                        value.putAll(modelAndView.getModel());
                        value.put("viewName", modelAndView.getViewName());
                        value.remove("org.springframework.validation.BindingResult.documentData");
                        value.remove("org.springframework.validation.BindingResult.store");
                        value.remove("documentData");
                        MethodParameter methodParameter = new MethodParameter(HashMap.class.getConstructor(), -1);
                        valueHandler.handleReturnValue(value, methodParameter, new ModelAndViewContainer(), new ServletWebRequest(request, response));
                        return null;
                    }
                }
            }
            return modelAndView;
        } catch (Exception e) {
            // AJAX请求返回错误信息
            ResponseEntity<ResultMessage> responseEntity = new ResponseEntity<ResultMessage>(
                    AbstractJsonDataServicesController.getFaultMessage(e), HttpStatus.EXPECTATION_FAILED);
            ServletWebRequest servletWebRequest = new ServletWebRequest(request, response);
            MethodParameter methodParameter = null;
            if (actionInvocation != null) {
                methodParameter = actionInvocation.getReturnValueType(responseEntity);
            }
            HandlerMethodReturnValueHandlerComposite returnValueHandlerComposite = new HandlerMethodReturnValueHandlerComposite();
            returnValueHandlerComposite.addHandlers(handlerAdapter.getReturnValueHandlers());
            returnValueHandlerComposite.handleReturnValue(responseEntity, methodParameter,
                    new ModelAndViewContainer(), servletWebRequest);
        } finally {
            logger.info("DmsDataServicesController.handleRequestInternal[{}]，处理过程耗时：{}", actionContext.getActionId(), timer.prettyPrint());
            ActionContextHolder.clearContext();
        }
        return null;
    }

    /**
     * @param request
     * @param response
     * @return
     */
    private ActionContext loadActionContext(HttpServletRequest request, HttpServletResponse response) {
        String dmsId = request.getParameter(PARAM_DMS_ID);
        if (StringUtils.isBlank(dmsId) || UNDEFINED.equals(dmsId)) {
            return createActionContextFromListViewRequest(request, response);
        }
        ConfigurationBuilder builder = new ConfigurationBuilder();
        Configuration configuration = builder.buildFromWidgetDefinition(dmsId);
        ActionContext actionContext = new ActionContextImpl(request.getParameter(PARAM_AC_ID),
                getRequestParams(request), dmsId, configuration);
        return actionContext;
    }

    /**
     * @param request
     * @param response
     * @return
     */
    private ActionContext createActionContextFromListViewRequest(HttpServletRequest request,
                                                                 HttpServletResponse response) {
        String listViewId = request.getParameter(PARAM_LV_ID);
        if (StringUtils.isBlank(listViewId)) {
            return createEmptyActionContext(request, response);
        }
        BootstrapTableConfiguration configuration = WidgetDefinitionUtils.getWidgetConfigurationById(
                BootstrapTableConfiguration.class, listViewId);
        String dataStoreId = configuration.getDataStoreId();
        if (StringUtils.isBlank(dataStoreId)) {
            dataStoreId = request.getParameter("dataStoreId");
        }
        ConfigurationBuilder builder = new ConfigurationBuilder();
        return new ActionContextImpl(request.getParameter(PARAM_AC_ID), getRequestParams(request), null, builder
                .setDataStoreId(dataStoreId).buildDataStoreConfiguration());
    }

    /**
     * @param request
     * @param response
     * @return
     */
    private ActionContext createEmptyActionContext(HttpServletRequest request, HttpServletResponse response) {
        ConfigurationBuilder builder = new ConfigurationBuilder();
        return new ActionContextImpl(request.getParameter(PARAM_AC_ID), getRequestParams(request), null,
                builder.buildEmptyConfiguration());
    }

    /**
     * @param request
     * @return
     */
    private Map<String, String> getRequestParams(HttpServletRequest request) {
        Map<String, String> values = Maps.newHashMap();
        Map<String, String[]> paramterMap = request.getParameterMap();
        for (Entry<String, String[]> entry : paramterMap.entrySet()) {
            values.put(entry.getKey(), StringUtils.join(entry.getValue(), Separator.SEMICOLON.getValue()));
        }
        return values;
    }

    /**
     * @param request
     * @param response
     * @return
     */
    private ActionInvocation createActionInvocationHandlerMethod(HttpServletRequest request,
                                                                 HttpServletResponse response) {
        String actionId = request.getParameter(PARAM_AC_ID);
        Action action = actionManager.getAction(actionId);
        if (action == null) {
            throw new RuntimeException("ID为[" + actionId + "]的数据管理操作不存在！");
        }

        ActionInvocation handlerMethod = new ActionInvocation(action);
        return handlerMethod;
    }

    /**
     * @param request
     * @param response
     * @param actionContext
     * @param actionInvocation
     * @return
     * @throws Exception
     */
    private boolean applyPreHandle(HttpServletRequest request, HttpServletResponse response,
                                   ActionContext actionContext, ActionInvocation actionInvocation, JsonElement jsonParams) throws Exception {
        String interceptors = actionContext.getConfiguration().getDocument(actionContext, request).getInterceptors();
        if (StringUtils.isNotBlank(interceptors)) {
            String[] handlers = StringUtils.split(interceptors, Separator.SEMICOLON.getValue());
            for (String handler : handlers) {
                if (interceptorMap.containsKey(handler)) {
                    if (!interceptorMap.get(handler).preHandle(request, response, actionContext, actionInvocation,
                            jsonParams)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * @param request
     * @param response
     * @param actionContext
     * @param actionInvocation
     * @param modelAndView
     * @throws Exception
     */
    private void applyPostHandle(HttpServletRequest request, HttpServletResponse response, ActionContext actionContext,
                                 ActionInvocation actionInvocation, ModelAndView modelAndView, JsonElement jsonParams) throws Exception {
        String interceptors = actionContext.getConfiguration().getDocument(actionContext, request).getInterceptors();
        if (StringUtils.isNotBlank(interceptors)) {
            String[] handlers = StringUtils.split(interceptors, Separator.SEMICOLON.getValue());
            for (String handler : handlers) {
                if (interceptorMap.containsKey(handler)) {
                    interceptorMap.get(handler).postHandle(request, response, actionContext, actionInvocation,
                            modelAndView, jsonParams);
                }
            }
        }
    }
}
