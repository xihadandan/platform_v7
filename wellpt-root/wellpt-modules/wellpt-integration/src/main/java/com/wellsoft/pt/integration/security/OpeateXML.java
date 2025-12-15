package com.wellsoft.pt.integration.security;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.io.IOException;
import java.util.Date;
import java.util.Iterator;

public class OpeateXML {

    //创建XML文档
    public static Document createDocument() {
        Document document = DocumentHelper.createDocument();
        Element root = document.addElement("<item>");

        Element fieldElement = root.addElement("field").addAttribute("name", "uuid").addAttribute("title", "索引");
        Element valueElement = fieldElement.addElement("value").addText("123");

        return document;
    }

    //read Docment内容
    public static void read(Document document) throws DocumentException {
        //获得根节点
        Element root = document.getRootElement();
        // 从XML的根结点开始遍历
        for (Iterator i = root.elementIterator(); i.hasNext(); ) {
            Element element = (Element) i.next();
            System.out.println(element.getName() + ":" + element.getText());
        }
    }

    //将document转化为String
    public static String XmlToString(Document document) {
        return document.asXML();
    }

    //将String转化为document
    public static Document StringToDocument(String text) {
        try {
            return DocumentHelper.parseText(text);
        } catch (DocumentException e) {
            // TODO Auto-generated catch block
            System.out.println(e.getMessage());
            return null;
        }
    }

    /**
     * 时间date向后推n秒
     */
    public static Date getPreTime(Integer n) {
        Date date = new Date();
        try {
            long Time = (date.getTime() / 1000) + n;
            date.setTime(Time * 1000);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return date;
    }

    public static void main(String[] args) throws IOException, DocumentException {
        //		String str = "<?xml version='1.0' encoding='utf-16'?> " + "<CurrentWeather>"
        //				+ "<Location>Shanghai / Hongqiao, China (ZSSS) 31-10N 121-26E 3M</Location>"
        //				+ "<Time>Aug 22, 2007 - 09:00 PM EDT / 2007.08.23 0100 UTC</Time>"
        //				+ "<Wind> from the ESE (110 degrees) at 7 MPH (6 KT) (direction variable):0</Wind>"
        //				+ "<Visibility> 4 mile(s):0</Visibility>" + "<SkyConditions> mostly clear</SkyConditions>"
        //				+ "<Temperature> 87 F (31 C)</Temperature>" + "<DewPoint> 78 F (26 C)</DewPoint>"
        //				+ "<RelativeHumidity> 74%</RelativeHumidity>" + "<Pressure> 29.77 in. Hg (1008 hPa)</Pressure>"
        //				+ "<Status>Success</Status>" + "</CurrentWeather>";
        //		read(StringToDocument(str));
        getPreTime(1000);
    }

}