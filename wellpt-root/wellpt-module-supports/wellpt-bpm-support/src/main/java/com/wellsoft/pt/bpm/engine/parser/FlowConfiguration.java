/*
 * @(#)4/15/24 V1.0
 *
 * Copyright 2024 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.parser;

import com.wellsoft.context.base.BaseObject;
import com.wellsoft.context.util.json.JsonUtils;
import com.wellsoft.pt.bpm.engine.core.FlowDefConstants;
import com.wellsoft.pt.bpm.engine.element.FlowElement;
import com.wellsoft.pt.bpm.engine.element.TimerElement;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.Element;

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
 * 4/15/24.1	zhulh		4/15/24		Create
 * </pre>
 * @date 4/15/24
 */
public class FlowConfiguration extends BaseObject {

    private Document document;

    private FlowElement flowElement;

    /**
     * @param content
     * @return
     */
    public static FlowConfiguration from(String content) {
        FlowConfiguration configuration = new FlowConfiguration();
        if (isXML(content)) {
            configuration.setDocument(FlowDefinitionParser.createDocument(content));
            configuration.setFlowElement(FlowDefinitionParser.parseFlow(content));
        } else {
            FlowElement flowElement = JsonUtils.json2Object(content, FlowElement.class);
            flowElement.setXmlDefinition(false);
            configuration.setFlowElement(flowElement);
        }
        return configuration;
    }

    private static boolean isXML(String content) {
        return StringUtils.startsWith(content, "<");
    }

    /**
     * @return the document
     */
    public org.dom4j.Document getDocument() {
        return document;
    }

    /**
     * @param document 要设置的document
     */
    public void setDocument(Document document) {
        this.document = document;
    }


    /**
     * @return the flowElement
     */
    public FlowElement getFlowElement() {
        return flowElement;
    }

    /**
     * @param flowElement 要设置的flowElement
     */
    public void setFlowElement(FlowElement flowElement) {
        this.flowElement = flowElement;
    }

    /**
     * @return
     */
    public String getUuid() {
        if (document != null) {
            Element rootElement = document.getRootElement();
            return rootElement.attributeValue(FlowDefConstants.FLOW_UUID);
        }

        return flowElement.getUuid();
    }

    /**
     * @param uuid
     */
    public void setUuid(String uuid) {
        if (document != null) {
            Element rootElement = document.getRootElement();
            rootElement.addAttribute(FlowDefConstants.FLOW_UUID, uuid);
        }

        flowElement.setUuid(uuid);
    }

    /**
     * @return
     */
    public String getId() {
        if (document != null) {
            Element rootElement = document.getRootElement();
            return rootElement.attributeValue(FlowDefConstants.FLOW_ID);
        }

        return flowElement.getId();
    }

    /**
     * @param code
     */
    public void setCode(String code) {
        if (document != null) {
            Element rootElement = document.getRootElement();
            rootElement.addAttribute(FlowDefConstants.FLOW_CODE, code);
        }

        flowElement.setCode(code);
    }


    public void setLastVersion(String version) {
        if (document != null) {
            Element rootElement = document.getRootElement();
            rootElement.addAttribute(FlowDefConstants.FLOW_LASTVER, version);
        }

        flowElement.setVersion(version);
    }

    /**
     * @return
     */
    public List<TimerElement> getTimers() {
        return flowElement.getTimers();
    }

    /**
     * @return
     */
    public String asXML() {
        return document != null ? document.asXML() : null;
    }

    /**
     * @return
     */
    public String asJSON() {
        if (document != null) {
            return null;//JsonUtils.object2Json(FlowDefinitionParser.parseFlow(document.asXML()));
        }
        return flowElement != null ? JsonUtils.object2Json(flowElement) : null;
    }

}
