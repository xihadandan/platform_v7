package com.wellsoft.context.web.controller;

import com.wellsoft.context.enums.JsonDataErrorCode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class BaseFormDataController extends AbstractJsonDataServicesController {

    /**
     * @param e
     * @param jsonDataErrorCode
     * @return
     */
    protected static ResponseEntity<ResultMessage> getFaultResultMsg(Exception e, JsonDataErrorCode jsonDataErrorCode) {
        ResultMessage resultMessage = getFaultMessage(e);
        ((FaultMessage) resultMessage).setErrorCode(jsonDataErrorCode.name());
        return new ResponseEntity<ResultMessage>(resultMessage, HttpStatus.EXPECTATION_FAILED);
    }

    @SuppressWarnings("static-method")
    protected ResponseEntity<ResultMessage> getSucessfulResultMsg(Object data) {
        ResultMessage resultMessage = new ResultMessage();
        resultMessage.setSuccess(true);
        resultMessage.setData(data);
        return new ResponseEntity<ResultMessage>(resultMessage, HttpStatus.OK);
    }

    @SuppressWarnings("static-method")
    protected ResponseEntity<ResultMessage> getFaultResultMsg(Object data, JsonDataErrorCode jsonDataErrorCode) {
        FaultMessage resultMessage = new FaultMessage();
        resultMessage.setSuccess(false);
        resultMessage.setData(data);
        resultMessage.setErrorCode(jsonDataErrorCode.toString());
        return new ResponseEntity<ResultMessage>(resultMessage, HttpStatus.EXPECTATION_FAILED);
    }

    /**
     * 异常处理
     *
     * @param e
     */
    protected ResponseEntity<ResultMessage> processException(Exception e) {
        return new ResponseEntity<ResultMessage>(AbstractJsonDataServicesController.getFaultMessage(e),
                HttpStatus.EXPECTATION_FAILED);
    }
}
