/*
 * @(#)2012-12-25 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.org.unit.web;

import com.wellsoft.pt.org.unit.service.UnitTreeService;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Iterator;

import static com.wellsoft.pt.org.unit.service.impl.UnitTreeServiceImpl.*;

/**
 * Description: 如何描述该类
 *
 * @author Xiem
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 6 Feb 2017.1	Xiem		6 Feb 2017		Create
 * </pre>
 * @date 6 Feb 2017
 */
@Controller
@RequestMapping("/org/unit/tree/json")
public class UnitTreeJsonController extends UnitTreeController {
    @Autowired
    private UnitTreeService unitTreeService;

    @RequestMapping(value = "/search")
    public @ResponseBody
    JSONArray search(@RequestParam("optionType") String optionType,
                     @RequestParam("all") String all, @RequestParam("login") String login,
                     @RequestParam("searchValue") String searchValue,
                     @RequestParam(value = "filterCondition", required = false) String filterCondition,
                     HttpServletRequest request) {
        Document unitxml = searchXml(optionType, all, login, searchValue, filterCondition, request);
        Element root = unitxml.getRootElement();
        return element2Json(root.elementIterator());
    }

    private JSONArray element2Json(Iterator<?> elementIt) {
        JSONArray result = new JSONArray();
        while (elementIt.hasNext()) {
            Element element = (Element) elementIt.next();
            JSONObject json = new JSONObject();
            Iterator<?> it = element.attributeIterator();
            while (it.hasNext()) {
                Attribute attribute = (Attribute) it.next();
                json.put(attribute.getName(), attribute.getValue());
            }
            String isLeaf = json.optString(ATTRIBUTE_ISLEAF, TRUE);
            if (isLeaf.equals(FALSE)) {
                json.put("children", element2Json(element.elementIterator()));
            }
            result.add(json);
        }
        return result;
    }

    /**
     * 返回所有群组类型的xml
     *
     * @param id
     * @param version
     * @return
     */
    @RequestMapping(value = "/type")
    public @ResponseBody
    JSONArray typeJson(HttpServletRequest request) {
        Element root = type(request).getRootElement();
        Iterator<?> elementIt = root.elementIterator();
        JSONArray result = new JSONArray();
        while (elementIt.hasNext()) {
            JSONObject json = new JSONObject();
            Element element = (Element) elementIt.next();
            json.put(ATTRIBUTE_ID, element.attribute(ATTRIBUTE_ID).getValue());
            json.put(ATTRIBUTE_NAME, element.getText());
            result.add(json);
        }
        return result;
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
    @RequestMapping(value = "/unit")
    public @ResponseBody
    JSONArray json(@RequestParam("optionType") String optionType, @RequestParam("all") String all,
                   @RequestParam("login") String login, @RequestParam("filterCondition") String filterCondition,
                   HttpServletRequest request) {
        Document unitxml = xml(optionType, all, login, filterCondition, request);
        Element root = unitxml.getRootElement();
        return element2Json(root.elementIterator());
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
    @RequestMapping(value = "/toggle/unit")
    public @ResponseBody
    JSONArray toggleJson(@RequestParam("optionType") String optionType,
                         @RequestParam("id") String id, @RequestParam("all") String all, @RequestParam("login") String login,
                         HttpServletRequest request) {
        Document unitxml = toggle(optionType, id, all, login, request);
        Element root = unitxml.getRootElement();
        return element2Json(root.elementIterator());
    }
}
