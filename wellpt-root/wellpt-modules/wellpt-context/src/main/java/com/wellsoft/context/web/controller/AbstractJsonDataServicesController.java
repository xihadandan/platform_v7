/*
 * @(#)2013-12-10 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.context.web.controller;

import com.wellsoft.context.enums.JsonDataErrorCode;
import com.wellsoft.context.exception.FieldValidationException;
import com.wellsoft.context.exception.JsonDataException;
import com.wellsoft.context.exception.WorkFlowException;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.hibernate.StaleObjectStateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import javax.validation.ConstraintViolationException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-12-10.1	zhulh		2013-12-10		Create
 * </pre>
 * @date 2013-12-10
 */
public class AbstractJsonDataServicesController extends BaseController {
    private static Logger log = LoggerFactory.getLogger(AbstractJsonDataServicesController.class);

    /**
     * 异常处理
     *
     * @param e
     */
    public static ResultMessage getFaultMessage(Exception e) {
        // log.error(ExceptionUtils.getStackTrace(e));

        FaultMessage msg = new FaultMessage();
        msg.clear();
        msg.setSuccess(false);
        msg.setErrorCode(JsonDataErrorCode.BusinessException.name());
        // 实体验证异常
        if (e instanceof MethodArgumentNotValidException) {
            List<ObjectError> errors = ((MethodArgumentNotValidException) e).getBindingResult().getAllErrors();
            for (ObjectError error : errors) {
                msg.addMessage(error.getDefaultMessage());
            }
            // 非流程日志记录
            logError(e);
        } else if (e instanceof InvocationTargetException) {
            // 服务层返回的异常方法
            addITEMessage(msg, (InvocationTargetException) e);
        } else if (e.getCause() instanceof InvocationTargetException) {
            // 服务层返回的异常方法
            addITEMessage(msg, (InvocationTargetException) e.getCause());
        } else if (e instanceof JsonDataException) {
            msg.addMessage(e.getMessage());
            msg.setData(((JsonDataException) e).getData());
            msg.setErrorCode(((JsonDataException) e).getErrorCode().name());
            // 非流程日志记录
            logError(e);
        } else {
            msg.addMessage(e.getMessage());
            // 非流程日志记录
            logError(e);
        }

        return msg;
    }

    /**
     * 如何描述该方法
     *
     * @param msg
     * @param e
     */
    private static void addITEMessage(FaultMessage msg, InvocationTargetException e) {
        InvocationTargetException tmp = e;
        Throwable target = tmp.getTargetException();
        Throwable cause = target.getCause();
        while (target != null) {
            if (target instanceof JsonDataException || target instanceof ConstraintViolationException) {
                break;
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

        msg.addMessage(target.getMessage());

        // 非流程日志记录
        logError(target);

        if (target instanceof JsonDataException) {
            msg.clear();
            msg.addMessage(ExceptionUtils.getStackTrace(target));
            msg.setData(((JsonDataException) target).getData());
            msg.setErrorCode(((JsonDataException) target).getErrorCode().name());
        } else if (target instanceof ConstraintViolationException) {
            // FieldValidationException fve = convert2JsonData(cve);
            msg.clear();
            msg.addMessage(ExceptionUtils.getStackTrace(target));
            ConstraintViolationException cve = (ConstraintViolationException) target;
            msg.setData(FieldValidationException.extractData(cve));
            msg.setErrorCode(JsonDataErrorCode.FieldValidation.name());
        } else if (target instanceof StaleObjectStateException) {
            msg.setData("系统忙，请稍候再试!");
            msg.setErrorCode(JsonDataErrorCode.StaleObjectState.name());
        } else if (target.getMessage() == null) {
            // 打印成string输出到FaultMessage去
            msg.setMsg(new StringBuilder());
            msg.addMessage(ExceptionUtils.getStackTrace(e));
            log.error(msg.getMsg().toString());
        }
    }

    /**
     * 非流程日志记录
     *
     * @param e
     */
    private static void logError(Throwable e) {
        if (!(e instanceof WorkFlowException)) {
            log.error(ExceptionUtils.getStackTrace(e));
        }
    }
}
