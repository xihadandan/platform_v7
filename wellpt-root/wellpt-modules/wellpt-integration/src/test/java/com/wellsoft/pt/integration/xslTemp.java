/*
 * @(#)2013-11-11 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.integration;

import org.apache.tools.ant.filters.StringInputStream;

import javax.xml.transform.*;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.IOException;
import java.io.StringWriter;

/**
 * Description: 如何描述该类
 *
 * @author Administrator
 * @version 1.0
 * <p>
 * "<pre>"+
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-11-29.1	Administrator		2013-11-29		Create
 * ""</pre>"+
 * @date 2013-11-29
 */
public class xslTemp {

    /**
     * 如何描述该方法
     *
     * @param args
     */
    public static void main(String[] args) throws TransformerException, IOException {
        String xmlStr = "<?xml version='1.0' encoding='UTF-8'?>"
                + "<?xml-stylesheet type='text/xsl' href='Quick.xsl'?>"
                + "<quicks>"
                + "<cate>"
                + "<id>Q_1</id>"
                + "<name><![CDATA[便捷操作]]></name>"
                + "<sub>"
                + "<name><![CDATA[写邮件]]></name>"
                + "<js><![CDATA[alert(1);OpenDocument(top.gaDivDns['Mail']+top.gaInxVar['MailURL']+'/Memo?openform','');;return false;]]></js>"
                + "</sub>"
                + "<sub>"
                + "<name><![CDATA[起草工作]]></name>"
                + "<js><![CDATA[alert(1);bNewWork();;return false;]]></js>"
                + "</sub>"
                + "<sub>"
                + "<name><![CDATA[工作交办]]></name>"
                + "<js><![CDATA[alert(1);bNewWork('','RC_GZJB');;return false;]]></js>"
                + "</sub>"
                + "<sub>"
                + "<name><![CDATA[发公告]]></name>"
                + "<js><![CDATA[alert(1);if(bCheckInfoRight('zBulletin.nsf')){OpenDocument(gsOAURL+'zBulletin.nsf/frmInfo?openform');}else{bNewWork('','XZ_DZGG');};return false;]]></js>"
                + "</sub>" + "</cate>" + "</quicks>";
        String xslStr = "<?xml version='1.0' encoding='UTF-8'?>"
                + "<xsl:stylesheet xmlns:xsl='http://www.w3.org/1999/XSL/Transform' version='1.0'>"
                + "<xsl:variable name='TotalCate' select='count(/quicks/cate)'/>"
                + "<xsl:variable name='Percent' select='25'/>" + "<xsl:template match='/'>"
                + "<xsl:for-each select='/quicks/cate'>" + "<span>"
                + "<xsl:attribute name='style'>float:left;width:<xsl:value-of select='$Percent'/>%;</xsl:attribute>"
                + "<table>" + "<tr>" + "<th>" + "<xsl:value-of select='name'/>" + "</th>" + "</tr>"
                + "<xsl:for-each select='sub'>" + "<tr>" + "<td>" + "<a href='#'>"
                + "<xsl:attribute name='onclick'><xsl:value-of select='js'/></xsl:attribute>"
                + "<xsl:value-of select='name'/>" + "</a>" + "</td>" + "</tr>" + "</xsl:for-each>" + "</table>"
                + "</span>" + "</xsl:for-each>" + "</xsl:template>" + "</xsl:stylesheet>";
        StringInputStream xmlStream = new StringInputStream(xmlStr);
        StringInputStream xslStream = new StringInputStream(xslStr);
        Source source = new StreamSource(xmlStream);
        Source xsl = new StreamSource(xslStream);
        StringWriter writer = new StringWriter();
        Result result = new StreamResult(writer);
        TransformerFactory factory = TransformerFactory.newInstance();
        Transformer transformer = factory.newTransformer(xsl);
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.transform(source, result);
        System.out.println(writer.toString());
    }
}
