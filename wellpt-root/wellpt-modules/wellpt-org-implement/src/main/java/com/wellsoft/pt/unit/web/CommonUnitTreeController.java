package com.wellsoft.pt.unit.web;

import com.wellsoft.context.web.controller.BaseController;
import com.wellsoft.context.web.controller.ResultMessage;
import com.wellsoft.pt.mt.mgr.TenantManagerService;
import com.wellsoft.pt.unit.service.CommonUnitService;
import com.wellsoft.pt.unit.service.CommonUnitTreeService;
import net.sf.json.JSONObject;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.dom4j.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Description: CommonUnitTreeController action
 *
 * @author liuzq
 * @date 2013-11-5
 */
@Controller
@RequestMapping("/superadmin/unit/commonUnitTree")
public class CommonUnitTreeController extends BaseController {

    @Autowired
    private TenantManagerService tenantManagerService;

    @Autowired
    private CommonUnitTreeService commonUnitTreeService;

    @Autowired
    private CommonUnitService commonUnitService;

    /**
     * 打开单位通讯录界面
     *
     * @return
     */
    @RequestMapping(value = "/config")
    public String unit(Model model) {
        model.addAttribute("tenantList", tenantManagerService.getNormalTenants());
        return forward("/superadmin/unit/common_unit_tree");
    }

    @RequestMapping(value = "/unit/tree")
    public @ResponseBody
    String getCommonUnitTreeXml(@RequestParam(value = "id") String id,
                                @RequestParam(value = "optionType") String optionType,
                                @RequestParam(value = "excludeTenantId") String excludeTenantId,
                                @RequestParam("jsonpCallback") String callback) {
        String unitxml = null;
        try {
            unitxml = commonUnitTreeService.parseCommonUnitTree(id, optionType, excludeTenantId).asXML();
        } catch (Exception e) {
            // TODO: handle exception
            logger.error(ExceptionUtils.getStackTrace(e));
        }

        return convertToJsonp(callback, unitxml);
    }

    @RequestMapping(value = "/leaf/unit/tree", produces = MediaType.APPLICATION_XML_VALUE)
    public @ResponseBody
    String leaf(@RequestParam(value = "id") String id,
                @RequestParam(value = "optionType") String optionType, @RequestParam("type") String type) {
        String unitxml = "";
        Document document = commonUnitTreeService.leafUnit(id, optionType, type);
        if (document != null) {
            unitxml = document.asXML();
        }
        return unitxml;
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
    public ResultMessage validateId(@RequestParam("uuid") String uuid, @RequestParam("unitId") String unitId) {
        ResultMessage resultMessage = new ResultMessage();
        try {
            if (commonUnitTreeService.validateId(uuid, unitId)) {
                resultMessage.setSuccess(true);
            } else {
                resultMessage.setSuccess(false);
            }
        } catch (Exception e) {
            logger.error(ExceptionUtils.getStackTrace(e));
        }
        return resultMessage;
    }

    private String convertToJsonp(String callback, String xml) {
        JSONObject xmlObjJson = new JSONObject();
        xmlObjJson.put("xml", xml);
        return callback + "(" + xmlObjJson.toString().replace("'", "\"") + ");";
    }
}
