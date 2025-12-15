/*
 * @(#)5/22/25 V1.0
 *
 * Copyright 2025 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.weixin.utils;

import org.apache.commons.collections.CollectionUtils;
import org.dom4j.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

/**
 * Description:
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 5/22/25.1	    zhulh		5/22/25		    Create
 * </pre>
 * @date 5/22/25
 */
public class WeixinXmlUtils {
    public static Document strToDocument(String xml) {
        try {
            //加上xml标签是为了获取最外层的标签，如果不需要可以去掉
            return DocumentHelper.parseText(xml);
        } catch (DocumentException e) {
            return null;
        }
    }

    public static JSONObject documentToJSONObject(String xml) {
        return elementToJSONObject(strToDocument(xml).getRootElement());
    }

    private static JSONObject elementToJSONObject(Element node) {
        JSONObject result = new JSONObject();
        // 当前节点的名称、文本内容和属性
        List<Attribute> listAttr = node.attributes();// 当前节点的所有属性的list
        for (Attribute attr : listAttr) {// 遍历当前节点的所有属性
            result.put(attr.getName(), attr.getValue());
        }
        // 递归遍历当前节点所有的子节点
        List<Element> elements = node.elements();// 所有一级子节点的list
        if (CollectionUtils.isNotEmpty(elements)) {
            for (Element element : elements) {
                if (CollectionUtils.isEmpty(element.attributes()) && CollectionUtils.isEmpty(element.elements())) {
                    result.put(element.getName(), element.getTextTrim());
                } else {
                    List<Element> children = element.elements();
                    if (CollectionUtils.isEmpty(children)) {
                        result.put(element.getName(), new JSONArray());
                    }
                    ((JSONArray) result.get(element.getName())).put(elementToJSONObject(element));
                }
            }
        }
        return result;
    }

}
