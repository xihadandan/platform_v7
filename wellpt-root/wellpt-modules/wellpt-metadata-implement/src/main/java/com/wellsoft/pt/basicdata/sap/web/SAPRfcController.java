package com.wellsoft.pt.basicdata.sap.web;

import com.wellsoft.context.web.controller.BaseController;
import com.wellsoft.context.web.controller.ResultMessage;
import com.wellsoft.pt.basicdata.sap.service.SAPRfcService;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/basicdata/sap")
public class SAPRfcController extends BaseController {

    @Autowired
    public SAPRfcService saprfcservice;

    @RequestMapping(value = "/executeRFC", method = RequestMethod.POST)
    @ResponseBody
    public ResultMessage executeRFC(@RequestParam("saps") String saps,
                                    @RequestParam("functionName") String functionname, @RequestParam("jsonParams") String jsonparams,
                                    HttpServletResponse response) throws Exception {
        ResultMessage resultMessage = new ResultMessage();
        try {
            String result = saprfcservice.RFC(saps, functionname, jsonparams, 1, -1, null).getRfcJson();
            resultMessage.setSuccess(true);
            resultMessage.setData(result);
        } catch (Exception e) {
            logger.error(ExceptionUtils.getStackTrace(e));
            resultMessage.setMsg(new StringBuilder(e.getMessage()));
            resultMessage.setSuccess(false);
        }
        return resultMessage;
    }
}