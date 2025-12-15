package com.wellsoft.context.web.controller;

import com.wellsoft.context.config.Config;
import com.wellsoft.context.enums.ApiCodeEnum;
import com.wellsoft.context.exception.BusinessException;
import com.wellsoft.context.exception.JsonDataException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.support.MethodArgumentNotValidException;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

/**
 * 统一数据输出
 */
@RestControllerAdvice(basePackages = "com.wellsoft")
@ResponseStatus
public class ApiResultExceptionAdvice {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * 业务异常处理
     *
     * @param e BusinessException
     * @return ApiResult
     */
    @ExceptionHandler(value = BusinessException.class)
    public ApiResult handlerBusinessException(BusinessException e) {
        logger.error(e.getMessage(), e);
        return ApiResult.fail(e.getMessage());
    }

    @ExceptionHandler(value = IllegalArgumentException.class)
    public ApiResult handlerIllegalArgumentException(IllegalArgumentException e) {
        logger.error(e.getMessage(), e);
        return ApiResult.build(ApiCodeEnum.ARGUMENTS_ERROR.getCode(), e.getMessage(), null);
    }

    @ExceptionHandler(value = JsonDataException.class)
    public ApiResult handlerJsonDataException(JsonDataException e) {
        logger.error(e.getMessage(), e);
        FaultMessage msg = new FaultMessage();
        msg.setData(e.getData());
        msg.setErrorCode(e.getErrorCode().name());
        return ApiResult.fail(msg);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiResult handlerMethodArgumentNotValidExceptionn(MethodArgumentNotValidException e) {
        String message = "";
        for (ObjectError allError : e.getBindingResult().getAllErrors()) {
            message += allError.getDefaultMessage();
        }
        return ApiResult.build(ApiCodeEnum.ARGUMENTS_ERROR.getCode(), message, null);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ApiResult handlerConstraintViolationException(ConstraintViolationException e) {
        String message = "";
        for (ConstraintViolation constraintViolation : e.getConstraintViolations()) {
            message += constraintViolation.getMessage();
        }
        return ApiResult.build(ApiCodeEnum.ARGUMENTS_ERROR.getCode(), message, null);
    }

    @ExceptionHandler(BindException.class)
    public ApiResult handlerBindException(BindException e) {
        String message = "";
        for (ObjectError allError : e.getBindingResult().getAllErrors()) {
            message += allError.getDefaultMessage();
        }
        return ApiResult.build(ApiCodeEnum.ARGUMENTS_ERROR.getCode(), message, null);
    }

    @ExceptionHandler(value = Exception.class)
    public ApiResult handlerGlobeException(Exception e) {
        logger.error(e.getMessage(), e);
        if (Config.ENV_PRD.equalsIgnoreCase(Config.getAppEnv())) {
            return ApiResult.fail(ApiCodeEnum.API_SYSTEM_ERROR.getDescription());
        }
        return ApiResult.fail(e.getMessage() == null ? e.toString() : e.getMessage());
    }

}

