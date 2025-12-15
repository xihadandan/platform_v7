package com.wellsoft.pt.unit.web;

import com.wellsoft.context.web.controller.BaseController;
import com.wellsoft.context.web.controller.ResultMessage;
import com.wellsoft.pt.unit.service.BusinessTypeService;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Description: BusinessTypeController action
 *
 * @author liuzq
 * @date 2013-11-5
 */
@Controller
@RequestMapping("/superadmin/unit/businessType")
public class BusinessTypeController extends BaseController {

    @Autowired
    private BusinessTypeService businessTypeService;

    /**
     * 打开单位通讯录界面
     *
     * @return
     */
    @RequestMapping(value = "/config")
    public String unit(Model model) {
        return forward("/superadmin/unit/business_type");
    }

    /**
     * 验证ID是否重复
     *
     * @param id
     * @param uuid
     * @return
     */
    @RequestMapping(value = "/validateId", method = RequestMethod.GET)
    @ResponseBody
    public ResultMessage validateId(@RequestParam("id") String id, @RequestParam("uuid") String uuid) {
        ResultMessage resultMessage = new ResultMessage();
        try {
            if (businessTypeService.validateId(id, uuid)) {
                resultMessage.setSuccess(true);
            } else {
                resultMessage.setSuccess(false);
            }
        } catch (Exception e) {
            logger.error(ExceptionUtils.getStackTrace(e));
        }
        return resultMessage;
    }

    /**
     * 保存用户ID到业务类别里
     *
     * @param id
     * @param uuid
     * @return
     */
    @RequestMapping(value = "/saveUnitManagerToBusinessType", method = RequestMethod.POST)
    @ResponseBody
    public ResultMessage saveUnitManagerToBusinessType(@RequestParam("businessTypeUuid") String businessTypeUuid,
                                                       @RequestParam("unitManagerUserId") String unitManagerUserId,
                                                       @RequestParam("unitManagerUserName") String unitManagerUserName) {
        ResultMessage resultMessage = new ResultMessage();
        try {
            businessTypeService.saveUnitManagerToBusinessType(businessTypeUuid, unitManagerUserId, unitManagerUserName);
            resultMessage.setSuccess(true);
        } catch (Exception e) {
            resultMessage.setSuccess(false);
            logger.error(ExceptionUtils.getStackTrace(e));
        }
        return resultMessage;
    }

    /**
     * 保存用户ID到业务单位树中
     *
     * @param id
     * @param uuid
     * @return
     */
    @RequestMapping(value = "/saveUserIdToBusinessUnitTree", method = RequestMethod.POST)
    @ResponseBody
    public ResultMessage saveUserIdToBusinessUnitTree(@RequestParam("uuid") String uuid,
                                                      @RequestParam("userId") String userId, @RequestParam("type") String type) {
        ResultMessage resultMessage = new ResultMessage();
        try {
            //businessUnitService.saveUserIdToBusinessUnitTree(uuid, userId, type);
            resultMessage.setSuccess(true);
        } catch (Exception e) {
            resultMessage.setSuccess(false);
            logger.error(ExceptionUtils.getStackTrace(e));
        }
        return resultMessage;
    }
}
