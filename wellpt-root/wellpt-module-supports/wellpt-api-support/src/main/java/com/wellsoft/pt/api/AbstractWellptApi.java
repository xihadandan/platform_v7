package com.wellsoft.pt.api;

import com.google.common.base.Throwables;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.wellsoft.context.enums.ApiCodeEnum;
import com.wellsoft.pt.api.annotation.AsyncMethod;
import com.wellsoft.pt.api.request.ApiRequest;
import com.wellsoft.pt.api.request.AsyncRequestWrapper;
import com.wellsoft.pt.api.response.ApiResponse;
import com.wellsoft.pt.api.support.ApiContextHolder;
import com.wellsoft.pt.api.support.TokenClaims;
import org.apache.commons.io.Charsets;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.task.TaskExecutor;
import org.springframework.http.HttpMethod;
import org.springframework.util.ClassUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;

/**
 * Description:
 *
 * @author chenq
 * @date 2018/8/8
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2018/8/8    chenq		2018/8/8		Create
 * </pre>
 */
public abstract class AbstractWellptApi implements WellptApi {

    protected final static Logger LOGGER = LoggerFactory.getLogger(AbstractWellptApi.class);

    private final Map<String, Method> CLASS_METHOD = Maps.newConcurrentMap();

    @Resource(name = "apiExecutor")
    private TaskExecutor taskExecutor;


    private Gson gson;

    public AbstractWellptApi() {
        try {
            gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
        } catch (Exception var3) {
            LOGGER.error("WellptApi 实例化异常：", var3);
            throw Throwables.propagate(var3);
        }
    }

    protected ApiRequest convert2ApiRequest(String str,
                                            Class<? extends ApiRequest> clazz) {
        return gson.fromJson(str, clazz);
    }


    public ApiResponse handleRequest(final HttpServletRequest httpServletRequest,
                                     HttpServletResponse httpServletResponse,
                                     final String methodName) throws Exception {

        Method method = getMethod(methodName, this, null);
        if (method == null) {
            return ApiResponse.build().code(ApiCodeEnum.API_NOT_FOUND);
        }
        Class requestParamClazz = method.getParameterTypes()[0];
        final String jsonString = IOUtils.toString(httpServletRequest.getInputStream(),
                Charsets.UTF_8);


        if (httpServletRequest.getMethod().equalsIgnoreCase(HttpMethod.POST.toString())) {
            if (httpServletRequest instanceof MultipartHttpServletRequest) {//文件流请求
                Map<String, MultipartFile> fileMap = ((MultipartHttpServletRequest) httpServletRequest).getFileMap();
                return (ApiResponse) method.invoke(this, httpServletRequest.getParameterMap(),
                        fileMap);

            } else {

                ApiResponse res = (ApiResponse) method.invoke(this,
                        convert2ApiRequest(jsonString, requestParamClazz));
                if (res.isSuccess()) {
                    //是否异步的方法
                    if (method.isAnnotationPresent(AsyncMethod.class)) {
                        final TokenClaims claims = new TokenClaims();
                        claims.putAll(
                                (TokenClaims) ApiContextHolder.getContext().get().get(
                                        ApiContextHolder.TOKEN_CLAIMS));
                        taskExecutor.execute(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    //处理异步的方法
                                    Method asyncMethod = getMethod(methodName + "Async",
                                            AbstractWellptApi.this,
                                            new Class[]{AsyncRequestWrapper.class});
                                    AsyncRequestWrapper wrapper = new AsyncRequestWrapper();
                                    ParameterizedType parameterizedType = (ParameterizedType) (asyncMethod.getGenericParameterTypes()[0]);
                                    Type[] params = parameterizedType.getActualTypeArguments();
                                    wrapper.setRequestBody(
                                            convert2ApiRequest(jsonString, (Class) params[0]));
                                    wrapper.setRequester(
                                            new AsyncRequestWrapper.Requester(
                                                    claims.getSystemCode(),
                                                    claims.getSysteName(), claims.getUnit()));
                                    asyncMethod.invoke(AbstractWellptApi.this,
                                            wrapper);
                                } catch (Exception e) {
                                    LOGGER.error("异步执行服务异常：", e);
                                }

                            }
                        });
                    }
                }
                return res;
            }

        }

        return (ApiResponse) method.invoke(this, httpServletRequest.getParameterMap(), null);

    }

    private Method getMethod(String methodName, WellptApi api, Class[] paramTypes) {
        String key = api.getClass().getCanonicalName() + "." + methodName;
        if (!CLASS_METHOD.containsKey(this.getClass())) {
            CLASS_METHOD.put(key,
                    ClassUtils.getMethod(api.getClass(), methodName, paramTypes));
        }
        Method method = CLASS_METHOD.get(key);
        return method;
    }


    public ApiResponse postMultipartFile(Map<String, String[]> parameters,
                                         Map<String, MultipartFile> files) {
        return ApiResponse.build();
    }

    public ApiResponse post(ApiRequest request) {
        return ApiResponse.build();
    }

    public ApiResponse get(Map<String, String[]> parameters) {
        return ApiResponse.build();
    }

}
