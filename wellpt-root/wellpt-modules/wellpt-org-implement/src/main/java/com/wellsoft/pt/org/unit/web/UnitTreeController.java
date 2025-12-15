/*
 * @(#)2012-12-25 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.org.unit.web;

import com.wellsoft.context.web.controller.BaseController;
import com.wellsoft.pt.org.unit.service.UnitTreeService;
import com.wellsoft.pt.org.unit.support.OrgExtendParamsContext;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.dom4j.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2012-12-25.1	zhulh		2012-12-25		Create
 * </pre>
 * @date 2012-12-25
 */
@Controller
@RequestMapping("/org/unit/tree")
public class UnitTreeController extends BaseController {
    protected Logger logger = LoggerFactory.getLogger(UnitTreeController.class);

    @Autowired
    private UnitTreeService unitTreeService;

    protected Document searchXml(String optionType, String all, String login, String searchValue,
                                 String filterCondition, HttpServletRequest request) {
        OrgExtendParamsContext.set(request.getParameter("extendParams"));
        String displayListString = request.getParameter("displayList");
        HashMap<String, String> displayMap = new HashMap<String, String>();
        String displayList[] = null;
        String searchValue1 = "";
        try {
            if (!StringUtils.isEmpty(displayListString)) {
                displayList = displayListString.split(";");
                for (String key : displayList)
                    displayMap.put(key, key);
            }
            searchValue1 = java.net.URLDecoder.decode(searchValue, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            logger.error(ExceptionUtils.getFullStackTrace(e));
        }
        try {
            if (StringUtils.isBlank(searchValue1)) {
                return unitTreeService.parserUnit(optionType, all, login, filterCondition, displayMap);
            } else {
                return unitTreeService.searchXml(optionType, all, login, searchValue1, filterCondition, displayMap);
            }
        } finally {
            OrgExtendParamsContext.remove();
        }
    }

    @RequestMapping(value = "/search/xml")
    public @ResponseBody
    String searchXml(@RequestParam("optionType") String optionType,
                     @RequestParam("all") String all, @RequestParam("login") String login,
                     @RequestParam("searchValue") String searchValue, @RequestParam("jsonpCallback") String callback,
                     @RequestParam(value = "filterCondition", required = false) String filterCondition,
                     HttpServletRequest request) {

        String unitxml = searchXml(optionType, all, login, searchValue, filterCondition, request).asXML();

        return convertToJsonp(callback, unitxml);
    }

    protected Document type(HttpServletRequest request) {
        //获取参数
        OrgExtendParamsContext.set(request.getParameter("extendParams"));
        String isShowGroup = request.getParameter("isShowGroup");
        String psType = request.getParameter("psType");
        try {
            return unitTreeService.parserType(isShowGroup, psType);
        } finally {
            OrgExtendParamsContext.remove();
        }
    }

    /**
     * 返回所有群组类型的xml
     *
     * @param id
     * @param version
     * @return
     */
    @RequestMapping(value = "/type/xml")
    public @ResponseBody
    String type(@RequestParam("jsonpCallback") String callback, HttpServletRequest request) {
        return convertToJsonp(callback, type(request).asXML());
    }

    protected Document xml(String optionType, String all, String login, String filterCondition,
                           HttpServletRequest request) {
        //update
        OrgExtendParamsContext.set(request.getParameter("extendParams"));
        String displayListString = request.getParameter("displayList");
        try {
            String displayList[] = null;
            HashMap<String, String> displayMap = new HashMap<String, String>();
            if (!StringUtils.isEmpty(displayListString)) {
                displayList = displayListString.split(";");
                for (String key : displayList)
                    displayMap.put(key, key);
            }
            return unitTreeService.parserUnit(optionType, all, login, filterCondition, displayMap);
        } finally {
            OrgExtendParamsContext.remove();
        }
    }

    /**
     * 返回组织结构树的xml
     *
     * @param idXml
     * @param type
     * @param all
     * @param login
     * @return
     */
    @RequestMapping(value = "/unit/xml")
    public @ResponseBody
    String xml(@RequestParam("optionType") String optionType, @RequestParam("all") String all,
               @RequestParam("login") String login, @RequestParam("jsonpCallback") String callback,
               @RequestParam("filterCondition") String filterCondition, HttpServletRequest request) {
        String unitxml = xml(optionType, all, login, filterCondition, request).asXML();
        return convertToJsonp(callback, unitxml);
    }

    protected Document toggle(String optionType, String id, String all, String login, HttpServletRequest request) {
        // 获取参数
        // String id = request.getParameter("id");
        OrgExtendParamsContext.set(request.getParameter("extendParams"));
        String displayListString = request.getParameter("displayList");
        try {
            String displayList[] = null;
            HashMap<String, String> displayMap = new HashMap<String, String>();
            if (!StringUtils.isEmpty(displayListString)) {
                displayList = displayListString.split(";");
                for (String key : displayList)
                    displayMap.put(key, key);
            }
            return unitTreeService.toggleUnit(optionType, id, all, login, displayMap);
        } finally {
            OrgExtendParamsContext.remove();
        }
    }

    /**
     * 展开组织单元
     *
     * @param optionType
     * @param id
     * @param all
     * @param login
     * @return
     */
    @RequestMapping(value = "/toggle/unit/xml")
    public @ResponseBody
    String toggle(@RequestParam("optionType") String optionType, @RequestParam("id") String id,
                  @RequestParam("all") String all, @RequestParam("login") String login,
                  @RequestParam("jsonpCallback") String callback, HttpServletRequest request) {
        String unitxml = toggle(optionType, id, all, login, request).asXML();
        return convertToJsonp(callback, unitxml);
    }

    /**
     * 获取组织单元根结点
     *
     * @param idXml
     * @param type
     * @param all
     * @param login
     * @return
     */
    @RequestMapping(value = "/leaf/unit/xml")
    public @ResponseBody
    String leaf(@RequestParam("optionType") String optionType, @RequestParam("id") String id,
                @RequestParam("type") String type, @RequestParam("login") String login,
                @RequestParam("jsonpCallback") String callback, HttpServletRequest request) {
        // 获取参数
        // String id = request.getParameter("id");
        String unitxml = "";
        OrgExtendParamsContext.set(request.getParameter("extendParams"));
        String displayListString = request.getParameter("displayList");
        try {
            String displayList[] = null;
            HashMap<String, String> displayMap = new HashMap<String, String>();
            if (!StringUtils.isEmpty(displayListString)) {
                displayList = displayListString.split(";");
                for (String key : displayList)
                    displayMap.put(key, key);
            }
            Document document = unitTreeService.leafUnit(optionType, id, type, login, displayMap);
            if (document != null) {
                unitxml = document.asXML();
            }
            return convertToJsonp(callback, unitxml);
        } finally {
            OrgExtendParamsContext.remove();
        }
    }

    /**
     * 将xml转成jsonp格式
     *
     * @param callback
     * @param xml
     * @return
     */
    private String convertToJsonp(String callback, String xml) {
        JSONObject xmlObjJson = new JSONObject();
        xmlObjJson.put("xml", xml);
        return callback + "(" + xmlObjJson.toString().replace("'", "\"") + ");";
    }
}
