package com.wellsoft.pt.config;

import com.alibaba.fastjson.JSON;
import com.wellsoft.context.web.controller.ApiResult;
import com.wellsoft.context.web.controller.IgnoreResultAdvice;
import com.wellsoft.context.web.controller.ResultMessage;
import com.wellsoft.pt.api.response.ApiResponse;
import io.swagger.annotations.Api;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * 统一数据输出
 */
@RestControllerAdvice(basePackages = "com.wellsoft")
public class ApiResultDataAdvice implements ResponseBodyAdvice {

    @Override
    public boolean supports(MethodParameter methodParameter, Class aClass) {
        // 如果被IgnoreResponseAdvice标识就不拦截
        if (methodParameter.getDeclaringClass().isAnnotationPresent(
                IgnoreResultAdvice.class
        )) {
            return false;
        }
        //方法上被标注，也不拦截
        if (methodParameter.getMethod().isAnnotationPresent(
                IgnoreResultAdvice.class
        )) {
            return false;
        }

        //只处理有@Api注解的Controller类
        return methodParameter.getDeclaringClass().isAnnotationPresent(
                Api.class
        );
    }

    @Override
    public Object beforeBodyWrite(Object o, MethodParameter methodParameter, MediaType mediaType, Class aClass, ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse) {
        ApiResult<Object> apiResult = ApiResult.success();
        if (null == o) {
            return apiResult;
        } else if (o instanceof ApiResult
                || o instanceof ApiResponse
                || o instanceof ResponseEntity
                || o instanceof ResultMessage
        ) {
            return o;
        } else if (o instanceof String) {
            apiResult.setData(o);
            return JSON.toJSONString(apiResult);
        } else {
            apiResult.setData(o);
        }
        return apiResult;
    }
}

