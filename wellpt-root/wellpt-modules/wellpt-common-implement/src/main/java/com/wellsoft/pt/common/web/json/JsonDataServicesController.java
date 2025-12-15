/*
 * @(#)2013-1-21 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.common.web.json;

import com.google.common.collect.Lists;
import com.wellsoft.context.exception.FieldValidationException;
import com.wellsoft.context.exception.JsonDataException;
import com.wellsoft.context.jdbc.entity.IdEntity;
import com.wellsoft.context.util.PinyinUtil;
import com.wellsoft.context.util.date.DateUtils;
import com.wellsoft.context.util.web.JsonDataServicesContextHolder;
import com.wellsoft.context.web.controller.AbstractJsonDataServicesController;
import com.wellsoft.context.web.controller.ApiResult;
import com.wellsoft.context.web.controller.ResultMessage;
import com.wellsoft.pt.jpa.datasource.SelectDatasource;
import com.wellsoft.pt.multi.org.entity.MultiOrgSystemUnit;
import com.wellsoft.pt.security.core.userdetails.UserDetails;
import com.wellsoft.pt.security.service.SecurityMetadataSourceService;
import com.wellsoft.pt.security.util.IgnoreLoginUtils;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.JavaType;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.core.Conventions;
import org.springframework.core.MethodParameter;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.util.ClassUtils;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.HandlerMethod;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Description: JsonDataServicesController.java
 *
 * @author zhulh
 * @date 2013-1-21
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-1-21.1	zhulh		2013-1-21		Create
 * </pre>
 */
@Api(tags = "JDS服务")
@Controller
@RequestMapping("/json/data/services")
public class JsonDataServicesController extends AbstractJsonDataServicesController {
    // 门面服务功能
    private static final String FUNCTION_FACEDE_SERVICE = "FacedeService";
    private final Map<String, Method[]> methodCache = new ConcurrentHashMap<String, Method[]>();
    private final Map<Method, HandlerMethod> handlerMethods = new LinkedHashMap<Method, HandlerMethod>();
    protected ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    ConfigurableListableBeanFactory configurableListableBeanFactory;
    @Autowired
    private SecurityMetadataSourceService securityMetadataSourceService;
    @Autowired
    private Validator validator;
    @Autowired
    private ConversionService conversionService;
    @Autowired
    private JsonDataServicesConfig jsonDataServicesConfig;

    /**
     * String、Boolean、Integer、Long、Double
     *
     * @param object
     * @param parameterType
     * @return
     */
    private static Object getJsonValue(Object object, Class<?> parameterType) {
        if (JSONObject.NULL.equals(object)) {
            return null;
        }
        String parameterTypeName = parameterType.getName();
        if (parameterType.isAssignableFrom(String.class)) {
            return String.valueOf(object);
        } else if (parameterType.isAssignableFrom(Boolean.class) || StringUtils.equals(
                parameterTypeName, "boolean")) {
            return Boolean.valueOf(object.toString());
        } else if (parameterType.isAssignableFrom(Character.class) || StringUtils.equals(
                parameterTypeName, "char")) {
            if (object.toString().length() == 1) {
                return Character.valueOf(object.toString().charAt(0));
            } else {
                throw new RuntimeException("character parameter error");
            }
        } else if (parameterType.isAssignableFrom(Byte.class) || StringUtils.equals(
                parameterTypeName, "byte")) {
            return Byte.valueOf(object.toString());
        } else if (parameterType.isAssignableFrom(Short.class) || StringUtils.equals(
                parameterTypeName, "short")) {
            return Short.valueOf(object.toString());
        } else if (parameterType.isAssignableFrom(Integer.class) || StringUtils.equals(
                parameterTypeName, "int")) {
            return Integer.valueOf(object.toString());
        } else if (parameterType.isAssignableFrom(Long.class) || StringUtils.equals(
                parameterTypeName, "long")) {
            return Long.valueOf(object.toString());
        } else if (parameterType.isAssignableFrom(Float.class) || StringUtils.equals(
                parameterTypeName, "float")) {
            return Float.valueOf(object.toString());
        } else if (parameterType.isAssignableFrom(Double.class) || StringUtils.equals(
                parameterTypeName, "double")) {
            return Double.valueOf(object.toString());
        } else if (parameterType.isAssignableFrom(Date.class)) {
            try {
                return DateUtils.parse(object.toString());
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        } else if (parameterType.isEnum()) {
            Object[] enumConstants = parameterType.getEnumConstants();
            for (Object enumCont : enumConstants) {
                if (enumCont.toString().equals(object.toString())) {
                    return enumCont;
                }
            }
        }
        return object;
    }

    /**
     * 构造json对象映射
     */
    @PostConstruct
    public void setObjectMapper() {
        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
        objectMapper.disable(
                DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES);// 反序列化不存在的属性，忽略报错
        objectMapper.enable(
                DeserializationConfig.Feature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);//允许复合对象类型传空字符串时候转为空对象
    }

    @SelectDatasource
    @ApiOperation(value = "JDS服务接口", notes = "JDS服务接口")
    @RequestMapping(value = "", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResultMessage> service(HttpServletRequest request,
                                                 HttpServletResponse response,
                                                 @RequestBody JsonData jsonData) {
        ResultMessage msg = new ResultMessage();
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

            String json = jsonData.getArgs();
            JSONArray jsonArray = StringUtils.isNotBlank(json) ? new JSONArray(json) : new JSONArray();

            Object bean = getService(jsonData.getServiceName());
            Method method = getMethod(jsonData, jsonArray, bean);
            Object[] args = getMethodArgumentValues(jsonData, jsonArray, bean, method);

            Object returnValue = method.invoke(bean, args);

            if (returnValue instanceof ResultMessage) {
                msg = (ResultMessage) returnValue;
            } else {
                msg.setData(returnValue);
            }
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
                // logger.error(ExceptionUtils.getStackTrace(e));
            }

            return processException(e);// new ResponseEntity<ResultMessage>(msg,
            // HttpStatus.EXPECTATION_FAILED);
        } finally {
            JsonDataServicesContextHolder.remove();
            if (StringUtils.isNotBlank(tenantId) && StringUtils.isNotBlank(currentTenantId)
                    && !StringUtils.equals(tenantId, currentTenantId)) {
                IgnoreLoginUtils.logout();
            }
        }
        return new ResponseEntity<ResultMessage>(msg, HttpStatus.OK);
    }

    /**
     * 异常处理
     *
     * @param e
     */
    private ResponseEntity<ResultMessage> processException(Exception e) {
        return new ResponseEntity<ResultMessage>(getFaultMessage(e), HttpStatus.OK);
    }

    private boolean isLoginedAuthenticateService(JsonData jsonData) {
        return SpringSecurityUtils.getAuthentication().isAuthenticated() &&
                jsonDataServicesConfig.getLoginedAuthenticatedServices().contains(jsonData.getServiceName() + "." + jsonData.getMethodName());
    }

    /**
     * Session 登录超时检查
     *
     * @param jsonData
     */
    protected void checkAndPrepare(JsonData jsonData) {
        UserDetails userDetails = SpringSecurityUtils.getCurrentUser();
        if (userDetails == null) {
            String service = jsonData.getServiceName() + "." + jsonData.getMethodName();
            if (!jsonDataServicesConfig.getAnonymousServices().contains(service)) {
                throw new SessionExpiredException("登录超时，请重新登录!");
            }
        }
    }

    /**
     * @param jsonData
     */
    protected void authenticate(JsonData jsonData) {
        // 允许匿名服务方法请求
        String key = jsonData.getServiceName() + "." + jsonData.getMethodName();
        if (jsonDataServicesConfig.getAnonymousServices().contains(key)) {
            return;
        }

        //FIXME: 7.0版接口访问粗粒度管理，仅对需要进行权限控制的接口进行角色权限判断（待规划设计），其他都默认为登录可访问
        if (SpringSecurityUtils.getAuthentication().isAuthenticated()) {
            return;
        }

        String unitId = SpringSecurityUtils.getCurrentUserUnitId();

        /**
         * 不需要权限控制的情况
         * 1. 非互联网用户：超管单位/请求参数authenticate=fasle
         * 2. 互联网用户：如果有包含角色 ROLE_TRUSTED_INTERNET_JDS 角色，则允许访问
         */
        if (((MultiOrgSystemUnit.PT_ID.equals(unitId) || !jsonData.getAuthenticate()) && !SpringSecurityUtils.isInternetLoginUser())
                || isLoginedAuthenticateService(jsonData)
                || (SpringSecurityUtils.isInternetLoginUser() && SpringSecurityUtils.hasAnyRole("ROLE_TRUSTED_INTERNET_JDS"))
        ) {
            return;
        }

        Collection<ConfigAttribute> methodAttributes = securityMetadataSourceService.getAttributes(
                key,
                FUNCTION_FACEDE_SERVICE);
        if (methodAttributes == null || methodAttributes.isEmpty()) {
            throw new RuntimeException("无权限调用门面服务 [" + key + "]");
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        for (ConfigAttribute attribute : methodAttributes) {
            // Attempt to find a matching granted authority
            for (GrantedAuthority authority : authorities) {
                if (attribute.getAttribute().equals(authority.getAuthority())) {
                    return;
                }
            }
        }
        if (SpringSecurityUtils.isAnonymousUser()) {
            throw new SessionExpiredException("登录超时，请重新登录!");
        }
        throw new RuntimeException("无权限调用门面服务 [" + key + "]");
    }

    protected Object[] getMethodArgumentValues(JsonData jsonData, JSONArray jsonArray, Object bean,
                                               Method method)
            throws Exception {
        int argsLength = jsonArray.length();
        if (argsLength == 0) {
            return new Object[0];
        }

        HandlerMethod handlerMethod = getHandlerMethod(jsonData.getServiceName(),
                jsonData.getMethodName(), bean,
                method);
        MethodParameter[] parameters = handlerMethod.getMethodParameters();

        if (jsonArray.length() != parameters.length) {
            throw new RuntimeException("The length(" + jsonArray.length()
                    + ") of json parameters is not match the length(" + parameters.length
                    + ") of the service method parameters");
        }
        Object[] args = new Object[parameters.length];
        for (int index = 0; index < parameters.length; index++) {
            Object object = jsonArray.get(index);
            if (object instanceof JSONObject) {
                args[index] = objectMapper.readValue(object.toString(),
                        getJavaType(parameters[index].getParameterType()));
                if (jsonData.getValidate()) {
                    validate(parameters[index], args[index]);
                }
            } else if (object instanceof JSONArray) {
                Type type = parameters[index].getGenericParameterType();
                // 判断获取的类型是否是参数类型
                if (type instanceof ParameterizedType) {
                    args[index] = objectMapper.readValue(object.toString(),
                            getParametricJavaType(type));
                } else {
                    args[index] = objectMapper.readValue(object.toString(),
                            getJavaType(parameters[index].getParameterType()));
                }
            } else {
                // Boolean、Double、Integer、Long、String
                args[index] = getJsonValue(object, parameters[index].getParameterType());
            }
        }
        return args;
    }

    private void validate(MethodParameter parameter, Object object) throws Exception {
        if (object instanceof IdEntity) {
            String name = Conventions.getVariableNameForParameter(parameter);
            BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(object, name,
                    true, 256);
            bindingResult.initConversion(conversionService);
            validator.validate(object, bindingResult);
            if (bindingResult.hasErrors()) {
                throw new FieldValidationException(parameter, bindingResult);
            }
        }
    }

    private HandlerMethod getHandlerMethod(String serviceName, String methodName, Object bean,
                                           Method method) {
        HandlerMethod handlerMethod = handlerMethods.get(method);
        if (handlerMethod == null) {
            handlerMethod = new HandlerMethod(bean, method);
            handlerMethods.put(method, handlerMethod);
        }
        return handlerMethod;
    }

    protected Method getMethod(JsonData jsonData, JSONArray jsonArray, Object bean) {
        String methodName = jsonData.getMethodName();
        int argsLength = jsonArray.length();
        Class<? extends Object> cls = ClassUtils.getUserClass(bean.getClass());
        String methodKey = cls.getName() + "." + methodName + argsLength;
        if (methodCache.containsKey(methodKey)) {
            Method[] candidateMethods = methodCache.get(methodKey);
            return determineTargetMethod(jsonData, jsonArray, bean, candidateMethods);
        }

        // 根据方法名称、参数个数获取候选的方法
        Method[] candidateMethods = getCandidateMethods(cls, methodName,
                argsLength);//cls.getMethods();
        if (candidateMethods.length == 0) {
            throw new RuntimeException("The method of " + methodName + " is not found");
        }
        methodCache.put(methodKey, candidateMethods);
        return determineTargetMethod(jsonData, jsonArray, bean, candidateMethods);
    }

    /**
     * @param jsonData
     * @param jsonArray
     * @param candidateMethods
     * @return
     */
    private Method determineTargetMethod(JsonData jsonData, JSONArray jsonArray, Object bean,
                                         Method[] candidateMethods) {
        if (candidateMethods.length == 1) {
            return candidateMethods[0];
        }
        List<String> argTypes = jsonData.getArgTypes();
        if (CollectionUtils.isNotEmpty(argTypes)) {
            if (argTypes.size() != jsonArray.length()) {
                throw new RuntimeException("参数类型个数与参数数据个数不匹配！");
            }
        }
        JsonDataMethod jsonDataMethod = new JsonDataMethod(jsonData, jsonArray);
        for (Method method : candidateMethods) {
            if (jsonDataMethod.matches(new HandlerMethod(bean, method))) {
                return method;
            }
        }
        throw new RuntimeException(
                "无法确定调用的服务方法[" + jsonData.getServiceName() + "." + jsonData.getMethodName()
                        + "]，请指定正确的方法参数类型！");
    }

    /**
     * 根据方法名称、参数个数获取候选的方法
     *
     * @param cls
     * @param methodName
     * @param argsLength
     * @return
     */
    private Method[] getCandidateMethods(Class<? extends Object> cls, String methodName,
                                         int argsLength) {
        Method[] methods = cls.getMethods();
        List<Method> returnMethods = Lists.newArrayList();
        for (Method method : methods) {
            if (!Modifier.isPublic(method.getModifiers())) {
                continue;
            }
            if (method.getName().equals(
                    methodName) && method.getParameterTypes().length == argsLength) {
                returnMethods.add(method);
            }
        }
        return returnMethods.toArray(new Method[0]);
    }

    protected Object getService(String serviceName) {
        Object bean = configurableListableBeanFactory.getBean(serviceName);
        Class<?> cls = ClassUtils.getUserClass(bean.getClass());
        if (cls.getAnnotation(Service.class) == null) {
            throw new RuntimeException("The bean [" + serviceName + "] is not a service");
        }
        return bean;
    }

    /**
     * @param clazz
     * @return
     */
    protected JavaType getJavaType(Class<?> clazz) {
        return this.objectMapper.constructType(clazz);
    }

    /**
     * @param type
     * @return
     */
    private JavaType getParametricJavaType(Type type) {
        // 强制转型为带参数的泛型类型
        Type[] parameterTypes = ((ParameterizedType) type).getActualTypeArguments();
        JavaType[] parameterClasses = new JavaType[parameterTypes.length];
        for (int i = 0; i < parameterTypes.length; i++) {
            parameterClasses[i] = objectMapper.getTypeFactory().constructType(parameterTypes[i]);
        }
        Class<?> parametrized = null;
        if (type instanceof Class) {
            parametrized = (Class<?>) type;
        } else {
            parametrized = (Class<?>) ((ParameterizedType) type).getRawType();
        }
        return objectMapper.getTypeFactory().constructParametricType(parametrized,
                parameterClasses);
    }

    @ApiOperation(value = "拼音全拼")
    @RequestMapping(value = "/pinYin", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ApiResult<String> pinYin(@ApiParam("字符串") @RequestParam("chString") String chString, HttpServletResponse response,
                                    HttpServletRequest request) throws IOException {
        return ApiResult.success(PinyinUtil.getPinYin(chString));
    }

    @ApiOperation(value = "拼音简拼")
    @RequestMapping(value = "/pinYinHeadChar", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ApiResult<String> pinYinHeadChar(@ApiParam("字符串") @RequestParam("chString") String chString,
                                            HttpServletResponse response,
                                            HttpServletRequest request) throws IOException {
        return ApiResult.success(PinyinUtil.getPinYinHeadChar(chString));
    }
}
