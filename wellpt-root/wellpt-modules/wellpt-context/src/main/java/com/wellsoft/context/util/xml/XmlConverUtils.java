package com.wellsoft.context.util.xml;

import com.sun.org.apache.xml.internal.serialize.XMLSerializer;
import org.apache.commons.io.IOUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.StringWriter;
import java.util.*;

/**
 * common xml conver utility
 *
 * @author viruscodecn@gmail.com
 * @version Framework 2010.10.26
 */
public class XmlConverUtils {
    private static Logger LOG = LoggerFactory.getLogger(XmlConverUtils.class);

    /**
     * 将MAP转化为XML<br>
     * 格式: <node><key label="key1">value1</key><br><key label="key2">value2</key><br>......<br></node>
     *
     * @param map 要转换的map
     * @return 转换后的xml
     */
    public static String maptoXml(Map map) {
        Document document = DocumentHelper.createDocument();
        Element nodeElement = document.addElement("node");
        for (Object obj : map.keySet()) {
            Element keyElement = nodeElement.addElement("key");
            keyElement.addAttribute("label", String.valueOf(obj));
            keyElement.setText(String.valueOf(map.get(obj)));
        }
        return doc2String(document);
    }

    /**
     * 将List转换为XML
     * list to xml xml <nodes><node><key label="key1">value1</key><key
     * label="key2">value2</key>......</node><node><key
     * label="key1">value1</key><key
     * label="key2">value2</key>......</node></nodes>
     *
     * @param list 欲转换的List
     * @return 转换后的xml
     */
    public static String listtoXml(List list) {
        Document document = DocumentHelper.createDocument();
        Element nodesElement = document.addElement("nodes");
        for (Object o : list) {
            Element nodeElement = nodesElement.addElement("node");
            for (Object obj : ((Map) o).keySet()) {
                Element keyElement = nodeElement.addElement("key");
                keyElement.addAttribute("label", String.valueOf(obj));
                keyElement.setText(String.valueOf(((Map) o).get(obj)));
            }
        }
        return doc2String(document);
    }

    /**
     * json to xml {"node":{"key":{"@label":"key1","#text":"value1"}}} conver
     * <o><node class="object"><key class="object"
     * label="key1">value1</key></node></o>
     *
     * @param json
     * @return
     */
    // public static String jsontoXml(String json) {
    // try {
    // XMLSerializer serializer = new XMLSerializer();
    // JSON jsonObject = JSONSerializer.toJSON(json);
    // return serializer.write(jsonObject);
    // } catch (Exception e) {
    // e.printStackTrace();
    // }
    // return null;
    // }

    /**
     * xml转化为Map(注意XML格式)
     * xml to map xml <node><key label="key1">value1</key><key
     * label="key2">value2</key>......</node>
     *
     * @param xml 欲转换xml
     * @return Map
     */
    public static Map xmltoMap(String xml) {
        try {
            Map map = new HashMap();
            Document document = DocumentHelper.parseText(xml);
            Element nodeElement = document.getRootElement();
            List node = nodeElement.elements();
            for (Iterator it = node.iterator(); it.hasNext(); ) {
                Element elm = (Element) it.next();
                map.put(elm.getName(), elm.getText());
                elm = null;
            }
            node = null;
            nodeElement = null;
            document = null;
            return map;
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
        return null;
    }

    /**
     * xml 转换为List
     * xml to list xml <nodes><node><key label="key1">value1</key><key
     * label="key2">value2</key>......</node><node><key
     * label="key1">value1</key><key
     * label="key2">value2</key>......</node></nodes>
     *
     * @param xml 欲转换List
     * @return List
     */
    public static List xmltoList(String xml) {
        try {
            List<Map> list = new ArrayList<Map>();
            Document document = DocumentHelper.parseText(xml);
            Element nodesElement = document.getRootElement();
            List nodes = nodesElement.elements();
            for (Iterator its = nodes.iterator(); its.hasNext(); ) {
                Element nodeElement = (Element) its.next();
                Map map = xmltoMap(nodeElement.asXML());
                list.add(map);
                map = null;
            }
            nodes = null;
            nodesElement = null;
            document = null;
            return list;
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
        return null;
    }

    /**
     * xml to json <node><key label="key1">value1</key></node> 转化为
     * {"key":{"@label":"key1","#text":"value1"}}
     *
     * @param xml
     * @return
     */
    // public static String xmltoJson(String xml) {
    // XMLSerializer xmlSerializer = new XMLSerializer();
    // return xmlSerializer.read(xml).toString();
    // }

    /**
     * 将XML文档转换为String
     *
     * @param document 文档
     * @return String
     */
    public static String doc2String(Document document) {
        String s = "";
        try {
            // 使用输出流来进行转化
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            // 使用UTF-8编码
            OutputFormat format = new OutputFormat("   ", true, "UTF-8");
            XMLWriter writer = new XMLWriter(out, format);
            writer.write(document);
            s = out.toString("UTF-8");
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
        }
        return s;
    }

    public static void main(String args[]) {
        XmlConverUtils util = new XmlConverUtils();
        // String xml =
        // "{/"node/":{/"key/":{/"@lable/":/"key1/",/"#text/":/"value1/"}}}";
        String xml = "<interests><interest><name>haha</name></interest><interest><name>haha1</name></interest></interests>";
        List map = util.xmltoList(xml);
        System.out.println(map);
    }

    /**
     * xml dom结构格式化、压缩
     *
     * @param document
     * @param formate
     * @return
     */
    public static String formateDocumentOutStrig(Document document, boolean formate) {
        StringWriter xmlOut = null;
        try {
            OutputFormat format = formate ? OutputFormat.createPrettyPrint() : OutputFormat.createCompactFormat(); //设置XML文档输出格式
            format.setEncoding(document.getXMLEncoding());
            xmlOut = new StringWriter();
            XMLWriter writer = new XMLWriter(xmlOut, format);
            writer.write(document);
            return xmlOut.toString();
        } catch (Exception e) {
            LOG.error("格式化输出xml文档结构异常：", e);
        } finally {
            IOUtils.closeQuietly(xmlOut);
        }
        return "";
    }

    public static String formateDocumentOutString(org.w3c.dom.Document document, boolean formate) {
        StringWriter xmlOut = null;
        try {
            com.sun.org.apache.xml.internal.serialize.OutputFormat outputFormat = new com.sun.org.apache.xml.internal.serialize.OutputFormat(
                    document);
            if (formate) {
                //格式化显示
                outputFormat.setLineWidth(65);
                outputFormat.setIndenting(true);
                outputFormat.setIndent(2);
            } else {
                //单行显示
                outputFormat.setIndenting(true);
                outputFormat.setIndent(1);
                outputFormat.setPreserveSpace(false);
                outputFormat.setLineSeparator("");
                outputFormat.setOmitComments(true);//注释删除
            }
            xmlOut = new StringWriter();
            XMLSerializer serializer = new XMLSerializer(xmlOut, outputFormat);
            serializer.serialize(document);
            return formate ? xmlOut.toString() : xmlOut.toString().replaceAll("/>\\s*<",
                    "/><").replaceAll(">\\s*<", "><");

        } catch (Exception e) {
            LOG.error("格式化输出xml文档结构异常：", e);
        }
        return "";
    }


    public static String formateXmlDocumentString(String xmlDocString, boolean formate) {
        try {
            return formateDocumentOutStrig(DocumentHelper.parseText(xmlDocString), formate);
        } catch (Exception e) {
            LOG.error("格式化xml字符串结构异常：", e);
        }
        return "";
    }
}
