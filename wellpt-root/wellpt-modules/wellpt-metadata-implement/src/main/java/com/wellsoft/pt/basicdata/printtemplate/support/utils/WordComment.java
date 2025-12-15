package com.wellsoft.pt.basicdata.printtemplate.support.utils;

import org.dom4j.Element;

/**
 * Description: word批注
 *
 * @author hongjz
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2018年11月26日.1	hongjz		2018年11月26日		Create
 * </pre>
 * @date 2018年11月26日
 */
public class WordComment {
    private String id;// 批注ID
    private String content;// 批注内容

    private Element startElement;

    private Element endElement;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Element getStartElement() {
        return startElement;
    }

    public void setStartElement(Element startElement) {
        this.startElement = startElement;
    }

    public Element getEndElement() {
        return endElement;
    }

    public void setEndElement(Element endElement) {
        this.endElement = endElement;
    }

}
