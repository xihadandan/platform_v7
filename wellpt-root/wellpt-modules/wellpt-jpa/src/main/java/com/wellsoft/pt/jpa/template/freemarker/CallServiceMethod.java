package com.wellsoft.pt.jpa.template.freemarker;

import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.context.util.date.DateUtils;
import freemarker.template.TemplateMethodModel;
import freemarker.template.TemplateModelException;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.JavaType;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Service;
import org.springframework.util.ClassUtils;
import org.springframework.web.method.HandlerMethod;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Description: freemarker支持直接调用服务返回对象
 *
 * @author chenq
 * @date 2018/10/12
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2018/10/12    chenq		2018/10/12		Create
 * </pre>
 */
public class CallServiceMethod implements TemplateMethodModel {

    private final static ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    static {
        OBJECT_MAPPER.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
        OBJECT_MAPPER.disable(
                DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES);// 反序列化不存在的属性，忽略报错
    }

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final Map<String, Method> methodCache = new ConcurrentHashMap<String, Method>();
    private final Map<String, HandlerMethod> handlerMethods = new LinkedHashMap<String, HandlerMethod>();

    private static Object getJsonValue(Object object, Class<?> parameterType) {
        if (JSONObject.NULL.equals(object)) {
            return null;
        }
        if (parameterType.isAssignableFrom(String.class)) {
            return String.valueOf(object);
        } else if (parameterType.isAssignableFrom(Boolean.class)) {
            return Boolean.valueOf(object.toString());
        } else if (parameterType.isAssignableFrom(Integer.class)) {
            return Integer.valueOf(object.toString());
        } else if (parameterType.isAssignableFrom(Long.class)) {
            return Long.valueOf(object.toString());
        } else if (parameterType.isAssignableFrom(Double.class)) {
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

    @Override
    public Object exec(List list) throws TemplateModelException {

        try {
            String serviceMethod = (String) list.get(0);
            String[] values = serviceMethod.split("\\.");
            String service = values[0];
            String methodStr = values[1];
            Object bean = getService(service);
            Method method = getMethod(bean, methodStr);
            Object[] args = getMethodArgumentValues(service, (String) list.get(1),
                    bean, method);
            return method.invoke(bean, args);

        } catch (Exception e) {
            logger.error("freemarker自定义函数callService请求异常：", e);
        }
        return null;
    }

    protected Method getMethod(Object bean, String methodName) {
        Class<? extends Object> cls = ClassUtils.getUserClass(bean.getClass());
        String key = cls.getName() + "." + methodName;
        if (methodCache.containsKey(key)) {
            return methodCache.get(key);
        }

        Method[] methods = cls.getMethods();
        for (Method method : methods) {
            if (Modifier.PUBLIC != method.getModifiers()) {
                continue;
            }
            if (method.getName().equals(methodName)) {
                methodCache.put(key, method);
                return method;
            }
        }
        throw new RuntimeException("The method of " + methodName + " is not found");
    }

    protected Object getService(String serviceName) {
        Object bean = ApplicationContextHolder.getBean(serviceName);
        Class<?> cls = ClassUtils.getUserClass(bean.getClass());
        if (cls.getAnnotation(Service.class) == null) {
            throw new RuntimeException("The bean [" + serviceName + "] is not a service");
        }
        return bean;
    }

    protected Object[] getMethodArgumentValues(String service, String jsonArgs, Object bean,
                                               Method method) throws Exception {
        JSONArray jsonArray = new JSONArray(jsonArgs);

        if (jsonArray.length() == 0) {
            return new Object[0];
        }

        HandlerMethod handlerMethod = getHandlerMethod(service,
                method.getName(), bean, method);
        MethodParameter[] parameters = handlerMethod.getMethodParameters();

        if (jsonArray.length() != parameters.length) {
            throw new RuntimeException(
                    "The length(" + jsonArray.length() + ") of json parameters is not match the length(" + parameters.length
                            + ") of the service method parameters");
        }

        Object[] args = new Object[parameters.length];
        for (int index = 0; index < parameters.length; index++) {
            Object object = jsonArray.get(index);
            if (object instanceof JSONObject) {
                args[index] = OBJECT_MAPPER.readValue(object.toString(),
                        getJavaType(parameters[index].getParameterType()));

            } else if (object instanceof JSONArray) {
                Type type = parameters[index].getGenericParameterType();
                // 判断获取的类型是否是参数类型
                if (type instanceof ParameterizedType) {
                    args[index] = OBJECT_MAPPER.readValue(object.toString(),
                            getParametricJavaType(type));
                } else {
                    args[index] = OBJECT_MAPPER.readValue(object.toString(),
                            getJavaType(parameters[index].getParameterType()));
                }
            } else {
                // Boolean、Double、Integer、Long、String
                args[index] = getJsonValue(object, parameters[index].getParameterType());
            }
        }
        return args;
    }

    private JavaType getParametricJavaType(Type type) {
        // 强制转型为带参数的泛型类型
        Type[] parameterTypes = ((ParameterizedType) type).getActualTypeArguments();
        Class<?>[] parameterClasses = new Class<?>[parameterTypes.length];
        for (int i = 0; i < parameterTypes.length; i++) {
            parameterClasses[i] = (Class<?>) parameterTypes[i];
        }
        Class<?> parametrized = null;
        if (type instanceof Class) {
            parametrized = (Class<?>) type;
        } else {
            parametrized = (Class<?>) ((ParameterizedType) type).getRawType();
        }
        return OBJECT_MAPPER.getTypeFactory().constructParametricType(parametrized,
                parameterClasses);
    }

    /**
     * @param clazz
     * @return
     */
    protected JavaType getJavaType(Class<?> clazz) {
        return this.OBJECT_MAPPER.constructType(clazz);
    }

    private HandlerMethod getHandlerMethod(String serviceName, String methodName, Object bean,
                                           Method method) {
        HandlerMethod handlerMethod = handlerMethods.get(serviceName + "." + methodName);
        if (handlerMethod == null) {
            handlerMethod = new HandlerMethod(bean, method);
            handlerMethods.put(serviceName + "." + methodName, handlerMethod);
        }
        return handlerMethod;
    }
}
