/*
 * @(#)2020年7月31日 V1.0
 *
 * Copyright 2020 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.printtemplate.support.utils;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hwpf.HWPFDocumentCore;
import org.apache.poi.hwpf.converter.AbstractWordUtils;
import org.apache.poi.hwpf.converter.WordToFoConverter;
import org.apache.tools.ant.filters.StringInputStream;
import org.dom4j.*;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Description: 如何描述该类
 *
 * @author zhongzh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2020年7月31日.1	zhongzh		2020年7月31日		Create
 * </pre>
 * @date 2020年7月31日
 */
public class PoiMain {
    public static void main(String[] args) throws Exception {
        InputStream is = new FileInputStream("F:\\DCA\\作业\\达梦DAC的培训感悟.doc");
        HWPFDocumentCore wordDocument = null;
        // wordDocument = new HWPFDocument(is);
        wordDocument = AbstractWordUtils.loadDoc(is);

        WordToFoConverter converter = new WordToFoConverter(DocumentBuilderFactory.newInstance().newDocumentBuilder()
                .newDocument());
        //对HWPFDocument进行转换
        converter.processDocument(wordDocument);
        Writer writer = new FileWriter(new File("F:\\TEMP\\converter.xml"));
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.ENCODING, "utf-8");
        //是否添加空格
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.transform(new DOMSource(converter.getDocument()), new StreamResult(writer));

        Map<String, String> uris = new HashMap<String, String>();
        uris.put("w", "http://schemas.openxmlformats.org/wordprocessingml/2006/main");
        uris.put("r", "http://schemas.openxmlformats.org/officeDocument/2006/relationships");
        uris.put("rr", "http://schemas.openxmlformats.org/package/2006/relationships");
        uris.put("a", "http://schemas.openxmlformats.org/drawingml/2006/main");
        uris.put("pkg", "http://schemas.microsoft.com/office/2006/xmlPackage");
        InputStream xml = new FileInputStream("F:\\TEMP\\定稿输出(新）.xml");
        InputStream xml2 = new FileInputStream("F:\\TEMP\\达梦DAC的培训感悟-wps.xml");
        SAXReader saxReader = new SAXReader();
        Document doc = null;
        doc = saxReader.read(new StringInputStream(IOUtils.toString(xml)));
        saxReader.getDocumentFactory().setXPathNamespaceURIs(uris);
        List<Element> list = doc
                .selectNodes("/pkg:package/pkg:part[@pkg:name=\"/word/document.xml\"]/pkg:xmlData/w:document/w:body/w:*");
        // System.out.println(list);

        Document doc2 = null;
        doc2 = saxReader.read(new StringInputStream(IOUtils.toString(xml2)));
        saxReader.getDocumentFactory().setXPathNamespaceURIs(uris);
        List<Element> list2 = doc2
                .selectNodes("/pkg:package/pkg:part[@pkg:name=\"/word/document.xml\"]/pkg:xmlData/w:document/w:body/w:*");
        // System.out.println(list2);
        List<Element> list3 = doc
                .selectNodes("/pkg:package/pkg:part[@pkg:name=\"/word/document.xml\"]/pkg:xmlData/w:document/w:body/w:p/w:r/w:t[contains(text(),'${dytable.MainBody}')]");
        // System.out.println(list3);
        // 处理图片
        List<Element> imageList = list2.get(0).getParent().selectNodes("//a:blip[@r:embed]");
        System.out.println(imageList);

        Element Relationships = (Element) doc
                .selectSingleNode("/pkg:package/pkg:part[@pkg:name=\"/word/_rels/document.xml.rels\"]/pkg:xmlData/rr:Relationships");
        Element pkg = (Element) doc.selectSingleNode("/pkg:package");
        int relSize = Relationships.elements().size() + 1;
        int partSize = pkg.elements().size() + 1;

        for (Element image : imageList) {
            QName qName = new QName("embed", new Namespace("r",
                    "http://schemas.openxmlformats.org/officeDocument/2006/relationships"));
            Attribute attribute = image.attribute(qName);
            String embId = attribute.getValue();
            String newId = "rId" + (relSize++);
            attribute.setValue(newId);
            Element imageRel = (Element) doc2
                    .selectSingleNode("/pkg:package/pkg:part[@pkg:name=\"/word/_rels/document.xml.rels\"]/pkg:xmlData/rr:Relationships/rr:Relationship[@Id=\""
                            + embId + "\"]");
            attribute = imageRel.attribute("Target");
            String target = attribute.getValue();
            Element imageDate = (Element) doc2.selectSingleNode("/pkg:package/pkg:part[@pkg:name=\"/word/" + target
                    + "\"]");
            // System.out.println(imageDate);
            imageRel = (Element) imageRel.clone();
            imageRel.setParent(null);
            String ext = target.substring(target.lastIndexOf("."));
            String newName = "image" + (partSize++);
            setAttribute(imageRel, "Id", newId);
            setAttribute(imageRel, "Target", "media/" + newName + ext);
            Relationships.add(imageRel);
            imageDate = (Element) imageDate.clone();
            setAttribute(imageDate, "pkg:name", "/word/media/" + newName + ext);
            pkg.add(imageDate);
        }

        Element body = list3.get(0).getParent().getParent();
        // System.out.println(body);
        int idx = list.indexOf(body);
        list.remove(idx);
        for (Element node : list2) {
            list.add(idx++, node);
        }
        Element element = body.getParent();
        element.clearContent();
        for (Element node : list) {
            Node nodeClone2 = (Node) node.clone();
            nodeClone2.setParent(null);
            element.add(nodeClone2);
        }
        XMLWriter xw = new XMLWriter(new FileOutputStream("F:\\TEMP\\merger.xml"), OutputFormat.createPrettyPrint());
        xw.write(doc);
    }

    public static void setAttribute(Element element, String name, String value) {
        List<Attribute> attributes = element.attributes();
        for (Attribute attribute : attributes) {
            if (StringUtils.endsWith(name, attribute.getName())) {
                attribute.setValue(value);
                return;
            }
        }
        element.addAttribute(name, value);
    }

}
