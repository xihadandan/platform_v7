/*
 * @(#)2014-8-17 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.api.domain;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2014-8-17.1	zhulh		2014-8-17		Create
 * </pre>
 * @date 2014-8-17
 */
public class FlowDefinitionDetail extends FlowDefinition {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = -3755480082013780329L;

    private String xml;

    /**
     * @return the xml
     */
    public String getXml() {
        return xml;
    }

    /**
     * @param xml 要设置的xml
     */
    public void setXml(String xml) {
        this.xml = xml;
    }

}
