package com.wellsoft.pt.basicdata.printtemplate.support.utils;

import com.wellsoft.context.enums.Encoding;
import com.wellsoft.context.util.UuidUtils;
import com.wellsoft.context.web.controller.ResultMessage;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.apache.tools.ant.filters.StringInputStream;
import org.dom4j.*;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WordXmlFreeMarkerPreprocessor {
    static Logger logger = Logger.getLogger(WordXmlFreeMarkerPreprocessor.class);
    static Pattern ftlPattern = Pattern.compile("<[/]{0,1}#[^<]+>");
    static Pattern placeHolderPattern = Pattern.compile("\\$\\{[^\\$\\{\\}]+\\}");

    public static void main(String[] args) throws IOException, DocumentException {
        // File file = new File("D:\\AppData\\freemarker\\src.xml");
        File file = new File("D:\\AppData\\freemarker\\33申请材料接收凭证.xml");
        System.out.println(file.exists());
        String xml = process(new FileInputStream(file));
        System.out.println(xml);
    }

    public static String process(FileInputStream fileInputStream) throws IOException, DocumentException {
        String contentBeforeParse = IOUtils.toString(fileInputStream, Encoding.UTF8.getValue());
        return process(contentBeforeParse);
    }

    public static String process(String contentBeforeParse) throws IOException, DocumentException {
        Map<String, String> uris = new HashMap<String, String>();
        uris.put("w", "http://schemas.openxmlformats.org/wordprocessingml/2006/main");
        uris.put("pkg", "http://schemas.microsoft.com/office/2006/xmlPackage");
        SAXReader saxReader = new SAXReader();
        Document doc = null;
        try {
            doc = saxReader.read(new StringInputStream(contentBeforeParse));
        } catch (DocumentException e) {
            logger.error(e.getMessage(), e);
            throw e;
        }
        saxReader.getDocumentFactory().setXPathNamespaceURIs(uris);

        // 为TR注入来自批注的FTL标签: 获取批注
        List<WordComment> comments = getWordComments(doc);

        List<WordComment> comments2 = new ArrayList<WordComment>();
        comments2.addAll(comments);

        // 为TR注入来自批注的FTL标签: 为批注创建占位符
        Iterator<WordComment> it = comments.iterator();
        while (it.hasNext()) {
            WordComment cmtObj = it.next();
            try {
                if (!processWordComment(doc, cmtObj)) {
                    it.remove();// 处理失败，在下面的步骤中，则不需要替换占位符，所以这里直接去掉
                }
            } catch (DocumentException e) {
                logger.error(e.getMessage(), e);
                throw e;
            }
        }

        // 将freemarker被word分开的占位符变量，合并在一起
        ResultMessage msg = preprocessFreemarkerPlaceholder(doc);

        // freemarker标签处理:将已被转义的标签转换成占位符元素
        // List<WordFtl> ftls = preprocessFreemarkerFtl(doc);

        // deleteWordComment(comments2, doc);
        String xml = doc.asXML();
        System.out.println(xml);
        // 为TR注入来自批注的FTL标签: 将TR前后的占位元素替换成FTL标签
        for (WordComment cmtObj : comments) {
            String comment = cmtObj.getContent();
            int index = -1;
            if (comment.indexOf("<#break>") != -1) {
                index = comment.indexOf("<#break>");
            } else {
                index = comment.indexOf("</#");
            }
            String begin = comment.substring(0, index);
            // begin = begin.substring(0, begin.lastIndexOf(">") + 1);
            String end = comment.substring(index);
            xml = xml.replaceFirst(cmtObj.getStartElement().asXML(), begin);
            xml = xml.replaceFirst(cmtObj.getEndElement().asXML(), end);
        }
        System.out.println("==========================================");
        System.out.println("==========================================");
        System.out.println("==========================================");
        System.out.println("==========================================");
        System.out.println("==========================================");
        System.out.println(xml);
        System.out.println("==========================================");
        System.out.println("==========================================");
        System.out.println("==========================================");
        System.out.println("==========================================");
        System.out.println("==========================================");
        return xml;

    }

    private static List<WordFtl> preprocessFreemarkerFtl(Document doc) {
        List<WordFtl> ftls = new ArrayList<WordFtl>();
        List<Node> documentNodes = doc
                .selectNodes("//pkg:package/pkg:part[@pkg:name=\"/word/document.xml\"]/pkg:xmlData/w:document");
        if (CollectionUtils.isNotEmpty(documentNodes)) {
            for (Node documentNode : documentNodes) {
                List<Node> textNodes = documentNode.selectNodes("w:body//w:t");// 取body下的所有文本节点

                if (CollectionUtils.isNotEmpty(textNodes)) {
                    preprocessFreemarkerFtl(textNodes, ftls);
                }

                System.out.println("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
                System.out.println("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
            }
        }

        return ftls;
    }

    private static void preprocessFreemarkerFtl(List<Node> textNodes, List<WordFtl> ftls) {

        if (textNodes.size() > 1) {
            String content = joinAllText(textNodes);
            int freemarkerVarCount = getFreemarkerFTLCount(content);
            Node lastTextNode = textNodes.get(textNodes.size() - 1);
            List<Node> nodesRemoveLast = new ArrayList<Node>();
            nodesRemoveLast.addAll(textNodes);
            nodesRemoveLast.remove(lastTextNode);
            String lastText = lastTextNode.getText();

            String content2 = joinAllText(nodesRemoveLast);
            int freemarkerVarCount2 = getFreemarkerFTLCount(content2);
            int freemarkerVarCount3 = getFreemarkerFTLCount(lastText);

            if ((freemarkerVarCount - freemarkerVarCount2 != 0)
                    && (freemarkerVarCount - freemarkerVarCount2 != freemarkerVarCount3)) {
                // 去掉最后一个文本后，如果变量个数与预期不符:
                // 说明有变量的部分内容落到最后一个文本中，将最后一个文本节点的内容移到前一个文本节点中
                Node node = nodesRemoveLast.get(nodesRemoveLast.size() - 1);
                String text = node.getText();
                node.setText(text + lastText);
                lastTextNode.setText("");
            }

            preprocessFreemarkerFtl(nodesRemoveLast, ftls);
        }
    }

    private static int getFreemarkerFTLCount(String content) {
        content = content.replaceAll("&lt;", "<").replaceAll("&gt;", ";");
        Matcher matcher = ftlPattern.matcher(content);
        int count = 0;
        while (matcher.find()) {
            count++;
        }
        return count;
    }

    private static ResultMessage preprocessFreemarkerPlaceholder(Document doc) {
        ResultMessage msg = new ResultMessage();
        List<Node> documentNodes = doc
                .selectNodes("//pkg:package/pkg:part[@pkg:name=\"/word/document.xml\"]/pkg:xmlData/w:document");
        if (CollectionUtils.isNotEmpty(documentNodes)) {
            for (Node documentNode : documentNodes) {
                List<Node> textNodes = documentNode.selectNodes("w:body//w:t");// 取body下的所有文本节点
                if (CollectionUtils.isNotEmpty(textNodes)) {
                    msg = preprocessFreemarkerPlaceholder(textNodes);
                }
            }
        }
        return msg;
    }

    private static ResultMessage preprocessFreemarkerPlaceholder(List<Node> textNodes) {
        ResultMessage msg = new ResultMessage();
        if (textNodes.size() > 1) {
            String content = joinAllText(textNodes);
            int freemarkerVarCount = getFreemarkerPlaceHolderCount(content);
            Node lastTextNode = textNodes.get(textNodes.size() - 1);
            List<Node> nodesRemoveLast = new ArrayList<Node>();
            nodesRemoveLast.addAll(textNodes);
            nodesRemoveLast.remove(lastTextNode);
            String lastText = lastTextNode.getText();

            String content2 = joinAllText(nodesRemoveLast);
            int freemarkerVarCount2 = getFreemarkerPlaceHolderCount(content2);
            int freemarkerVarCount3 = getFreemarkerPlaceHolderCount(lastText);

            if ((freemarkerVarCount - freemarkerVarCount2 != 0)
                    && (freemarkerVarCount - freemarkerVarCount2 != freemarkerVarCount3)) {
                // 去掉最后一个文本后，如果变量个数与预期不符:
                // 说明有变量的部分内容落到最后一个文本中，将最后一个文本节点的内容移到前一个文本节点中
                Node node = nodesRemoveLast.get(nodesRemoveLast.size() - 1);
                String text = node.getText();
                node.setText(text + lastText);
                lastTextNode.setText("");
            }

            msg = preprocessFreemarkerPlaceholder(nodesRemoveLast);
        }
        return msg;
    }

    private static String joinAllText(List<Node> textNodes) {
        StringBuffer contentBuffer = new StringBuffer();
        for (int i = 0; i < textNodes.size(); i++) {
            Node textNode = textNodes.get(i);

            contentBuffer.append(textNode.getText());

        }
        return contentBuffer.toString();
    }

    private static int getFreemarkerPlaceHolderCount(String content) {
        Matcher matcher = placeHolderPattern.matcher(content);
        int count = 0;
        while (matcher.find()) {
            count++;
        }
        return count;
    }

    private static boolean processWordComment(Document doc, WordComment cmtObj) throws DocumentException {
        String commentId = cmtObj.getId();
        Node commentStartNode = doc.selectSingleNode("//w:commentRangeStart[@w:id=\"" + commentId + "\"]");
        Node commentEndNode = doc.selectSingleNode("//w:commentRangeEnd[@w:id=\"" + commentId + "\"]");
        Node commentReferenceNode = doc.selectSingleNode("//w:commentReference[@w:id=\"" + commentId + "\"]");
        if (commentStartNode == null || commentEndNode == null || commentReferenceNode == null) {// 只有批注内容，找不到批注范围
            return false;
        }
        if ("3".equals(commentId)) {
            System.out.println();
        }

        Element startTblElem = getParentElement(commentStartNode, "tbl");
        Element endTblElem = getParentElement(commentEndNode, "tbl");

        boolean result = false;
        if (startTblElem == null && endTblElem == null) {// 批注的开始和结束标签： 均不在表格中
            result = processWordComment(doc, commentStartNode, commentEndNode, cmtObj);
        } else if (startTblElem != null && endTblElem != null) {// 批注的开始和结束标签：均在表格中
            if (startTblElem == endTblElem) {// 批注的开始和结束标签：均在表格中---->在同一个表格
                Element startTdElem = getParentElement(commentStartNode, "tc");
                Element endTdElem = getParentElement(commentEndNode, "tc");
                // 开始和结束占位符
                Element commentBeginElement = DocumentHelper.createElement("comment");
                commentBeginElement.addAttribute("id", commentId + "-begin");
                Element commentEndElement = DocumentHelper.createElement("comment");
                commentEndElement.addAttribute("id", commentId + "-end");
                boolean isMarkInOneTd = (startTdElem == endTdElem); // 批注的开始和结束标签：均在表格中---->在同一个表格--->不同tc中,则值为true
                boolean isMarkAllTdContentAndOnlyOneTcInTr = false;// 如果批注包括了单位格的所有内容,且所在行只有一个单元格则值为true
                if (isMarkInOneTd) {// 批注的开始和结束标签：均在表格中---->在同一个表格--->同一个tc中
                    if (!existOtherTdIntr(startTdElem)) {// tc所在的tr，是否只含有一个tc
                        if (isMarkAllTd(startTdElem, commentStartNode, commentEndNode)) {
                            isMarkAllTdContentAndOnlyOneTcInTr = true;// 如果批注包括了单位格的所有内容,且所在行只有一个单元格
                        }
                    }
                }

                if (!isMarkInOneTd || isMarkAllTdContentAndOnlyOneTcInTr) { // 批注的开始和结束标签：均在表格中---->在同一个表格--->不同tc中
                    Element startTrElem = getParentElement(commentStartNode, "tr");
                    Element endTrElem = getParentElement(commentEndNode, "tr");

                    Element tblElem = startTblElem;
                    if (startTrElem != endTrElem) {// 批注整个表格
                        Element tblParentElement = tblElem.getParent();

                        List<Element> siblings = tblParentElement.elements();

                        int startIndex = 0;
                        for (Element sibling : siblings) {
                            if (sibling == tblElem) {
                                break;
                            } else {
                                startIndex++;
                            }
                        }
                        siblings.add(startIndex, commentBeginElement);
                        cmtObj.setStartElement(commentBeginElement);
                        siblings = tblParentElement.elements();
                        int endIndex = 0;
                        for (Element sibling : siblings) {
                            if (sibling == tblElem) {
                                break;
                            } else {
                                endIndex++;
                            }
                        }

                        siblings.add(endIndex + 1, commentEndElement);
                        cmtObj.setEndElement(commentEndElement);
                        result = true;
                    } else {

                        List<Element> children = tblElem.elements();
                        int startIndex = 0;
                        for (Element child : children) {
                            if (child == startTrElem) {
                                break;
                            } else {
                                startIndex++;
                            }
                        }

                        children.add(startIndex, commentBeginElement);
                        cmtObj.setStartElement(commentBeginElement);

                        children = tblElem.elements();
                        int endIndex = 0;
                        for (Element child : children) {
                            if (child == endTrElem) {
                                break;
                            } else {
                                endIndex++;
                            }
                        }

                        children.add(endIndex + 1, commentEndElement);

                        cmtObj.setEndElement(commentEndElement);
                        result = true;
                    }

                } else { // 批注的开始和结束标签：均在表格中---->在同一个表格--->同一tc
                    List<Element> elements = startTblElem.elements();
                    boolean isStartLevelSameAsTr = false;
                    boolean isEndLevelSameAsTr = false;

                    for (Element e : elements) {
                        if (e == commentStartNode) {
                            isStartLevelSameAsTr = true;
                        }

                        if (e == commentEndNode) {
                            isEndLevelSameAsTr = true;
                        }
                    }
                    if ((isStartLevelSameAsTr && !isStartLevelSameAsTr)
                            || (!isStartLevelSameAsTr && isStartLevelSameAsTr)) {
                        throw new RuntimeException("批注开始标签和结束标签须在同一级中");
                    } else if (isStartLevelSameAsTr && isStartLevelSameAsTr) {
                        List<Element> children = startTblElem.elements();
                        int startIndex = 0;
                        for (Element child : children) {
                            if (child == commentStartNode) {
                                break;
                            } else {
                                startIndex++;
                            }
                        }

                        children.add(startIndex, commentBeginElement);
                        cmtObj.setStartElement(commentBeginElement);

                        children = startTblElem.elements();
                        int endIndex = 0;
                        for (Element child : children) {
                            if (child == commentEndNode) {
                                break;
                            } else {
                                endIndex++;
                            }
                        }

                        children.add(endIndex + 1, commentEndElement);

                        cmtObj.setEndElement(commentEndElement);
                        result = true;

                    } else {

                        result = processWordComment(doc, commentStartNode, commentEndNode, cmtObj);
                    }

                }

            } else {// 批注的开始和结束标签：均在表格中---->但不是同一个表格
                logger.error("批注标签[id=" + cmtObj.getId() + "][content=" + cmtObj.getContent()
                        + "]异常: 同时批注了两个表格， 暂时不支持这样的freemarker标签批注 ");
                result = false;
            }
        } else {// 批注的开始和结束标签：一个在表格中，一个不在表格中, 目前这种先不处理
            logger.error("批注标签[id=" + cmtObj.getId() + "][content=" + cmtObj.getContent()
                    + "]异常: 同时批注了表格及非表格， 暂时不支持这样的freemarker标签批注 ");
            result = false;
        }

        List<Node> commentStartNodes = doc.selectNodes("//w:commentRangeStart[@w:id=\"" + commentId + "\"]");
        List<Node> commentEndNodes = doc.selectNodes("//w:commentRangeEnd[@w:id=\"" + commentId + "\"]");
        List<Node> commentReferenceNodes = doc.selectNodes("//w:commentReference[@w:id=\"" + commentId + "\"]");
        for (Node commentStartNode1 : commentStartNodes) {
            Element parent = getParentElement(commentStartNode1, "p");
            if (parent == null) {
                parent = commentStartNode1.getParent();
            }
            if (parent != null) {
                Iterator<Element> it = parent.elements().iterator();
                while (it.hasNext()) {
                    Element e = it.next();
                    if (e == commentStartNode1) {
                        it.remove();
                    }
                }
            }

        }

        for (Node commentEndNode1 : commentEndNodes) {
            Element parent = getParentElement(commentEndNode1, "p");
            if (parent == null) {
                parent = commentEndNode1.getParent();
            }
            if (parent != null) {
                Iterator<Element> it = parent.elements().iterator();
                while (it.hasNext()) {
                    Element e = it.next();
                    if (e == commentEndNode1) {
                        it.remove();
                    }
                }
            }

        }

        for (Node commentReferenceNode1 : commentReferenceNodes) {
            Element parent = getParentElement(commentReferenceNode1, "r");
            if (parent == null) {
                parent = commentReferenceNode1.getParent();
            }
            if (parent != null) {
                Iterator<Element> it = parent.elements().iterator();
                while (it.hasNext()) {
                    Element e = it.next();
                    if (e == commentReferenceNode1) {
                        it.remove();
                    }
                }
            }

        }

        return result;
        // trElem.System.out.println(tblElem.asXML());
    }

    private static boolean isMarkAllTd(Element tdElement, Node commentStartNode, Node commentEndNode) {
        // 判断所有的文本元素是否在批注的范围内, 如果都在批注的范围内，则说明批注了所有元素
        List<Element> tElements = tdElement.selectNodes(".//w:t");
        List<Element> allParentsOfStart = getAllParentUnder(tdElement, commentStartNode);
        List<Element> allParentsOfEnd = getAllParentUnder(tdElement, commentEndNode);

        boolean isNotMarkTELement = false;
        for (Element tElement : tElements) {
            boolean isStartBefore = false;
            for (Element parentOfStart : allParentsOfStart) {
                if (isABeforeB(parentOfStart, tElement)) {
                    isStartBefore = true;
                    break;
                }
            }
            boolean isEndBefore = true;
            for (Element parentOfEnd : allParentsOfEnd) {
                if (!isABeforeB(parentOfEnd, tElement)) {
                    isEndBefore = false;
                    break;
                }
            }

            if (isStartBefore && !isEndBefore) {// 文本元素在批注的范围内
                continue;
            } else {// 文本元素在批注的范围外
                isNotMarkTELement = true;
                break;
            }
        }
        return !isNotMarkTELement;
    }

    private static List<Element> getAllParentUnder(Element containerElement, Node element) {
        List<Element> parents = new ArrayList<Element>();
        parents.add((Element) element);
        Element parent = element.getParent();
        while (parent != containerElement && !parent.isRootElement()) {
            parents.add(parent);
            parent = parent.getParent();
        }

        return parents;
    }

    private static boolean isABeforeB(Node aElement, Element bElement) {

        Element parent = bElement.getParent();

        @SuppressWarnings("unchecked")
        Iterator<Element> siblingsIt = parent.elements().iterator();
        boolean isMatchB = false;
        while (siblingsIt.hasNext()) {
            Element sibling = siblingsIt.next();
            if (aElement == sibling) {
                if (isMatchB) {// 如果B已经匹配到了, 这时说明A元素在B元素之后
                    return false;
                } else {// B未匹配到，说明A元素在B元素之前
                    return true;
                }
            }
            if (sibling == bElement) {
                isMatchB = true;
            }
        }
        if (parent.isRootElement()) {// 已经遍历到顶层
            return false;
        } else {
            // 如果有执行到这里，说明没有匹配到A元素, 继续往B的上一级匹配
            return isABeforeB(aElement, parent);
        }

    }

    private static boolean existOtherTdIntr(Element tdElement) {
        Element tr = getParentElement(tdElement, "tr");
        if (tr.selectNodes("./w:tc").size() == 1) {// 表示参数指定的td元素所在行，只有一个td元素
            return false;
        } else {
            return true;
        }
    }

    private static boolean processWordComment(Document doc, Node commentStartNode, Node commentEndNode,
                                              WordComment cmtObj) {
        String commentId = cmtObj.getId();
        Element startPelem = getParentElement(commentStartNode, "p");
        Element endPelem = getParentElement(commentEndNode, "p");

        if (startPelem == endPelem && startPelem != null) {// 批注在同一段中
            List<Element> children = startPelem.elements();
            int startIndex = 0;
            for (Element child : children) {
                if (child == commentStartNode) {
                    break;
                } else {
                    startIndex++;
                }
            }
            Element commentBeginElement = DocumentHelper.createElement("comment");
            commentBeginElement.addAttribute("id", commentId + "-begin");
            children.add(startIndex + 1, commentBeginElement);
            cmtObj.setStartElement(commentBeginElement);

            Element commentEndElement = DocumentHelper.createElement("comment");
            commentEndElement.addAttribute("id", commentId + "-end");

            children = endPelem.elements();
            int endIndex = 0;
            for (Element child : children) {
                if (child == commentEndNode) {
                    break;
                } else {
                    endIndex++;
                }
            }

            children.add(endIndex, commentEndElement);
            cmtObj.setEndElement(commentEndElement);
            return true;
        } else {
            { // 批注开始标签处理
                if (startPelem == null) {
                    startPelem = getParentElement(commentStartNode, "body");// 标签处于body下面
                    List<Element> children = startPelem.elements();

                    int startNodeIndex = -1;
                    int index = -1;
                    for (Element child : children) {
                        index++;
                        if (child == commentStartNode) {
                            startNodeIndex = index;
                            break;
                        }
                    }

                    Element commentBeginElement = DocumentHelper.createElement("comment");
                    commentBeginElement.addAttribute("id", commentId + "-begin");

                    children.add(startNodeIndex + 1, commentBeginElement);

                    cmtObj.setStartElement(commentBeginElement);
                } else {
                    List<Element> children = startPelem.elements();
                    boolean existNodeAfterCommentStart = false;
                    boolean existTextNodeBeforeCommentStart = false;// 在批注开始标签之前，如果没有文本节点，直接在批注开始标签的父亲元素之前加入占位符---不然会多一个空行
                    List<Node> nodesAfterCommentStart = new ArrayList<Node>();
                    int startNodeIndex = -1;
                    int index = -1;
                    for (Element child : children) {
                        child.asXML();
                        index++;
                        if (startNodeIndex == -1 && CollectionUtils.isNotEmpty(child.selectNodes("./w:t"))) {
                            existTextNodeBeforeCommentStart = true;
                        }
                        if (child == commentStartNode) {
                            startNodeIndex = index;
                        }

                        if (startNodeIndex > -1 && index > startNodeIndex) {

                            nodesAfterCommentStart.add(child);
                            if (CollectionUtils.isNotEmpty(child.selectNodes("./w:t"))) {
                                existNodeAfterCommentStart = true;
                            }
                        }
                    }

                    Element commentBeginElement = DocumentHelper.createElement("comment");
                    commentBeginElement.addAttribute("id", commentId + "-begin");
                    List<Element> children1 = startPelem.getParent().elements();
                    int index1 = -1;
                    int startPindex = -1;
                    for (Element child : children1) {
                        index1++;
                        if (child == startPelem) {
                            startPindex = index1;
                        }
                    }
                    if (!existTextNodeBeforeCommentStart) {// 没有文本节点，直接在备注前加入占位符
                        children1.add(startPindex, commentBeginElement);
                    } else {
                        children1.add(startPindex + 1, commentBeginElement);
                    }

                    cmtObj.setStartElement(commentBeginElement);
                    if (existTextNodeBeforeCommentStart && existNodeAfterCommentStart
                            && CollectionUtils.isNotEmpty(nodesAfterCommentStart)) {

                        Element newpElement = DocumentHelper.createElement("w:p");
                        newpElement.addAttribute("w14:paraId", UuidUtils.createUuid());
                        newpElement.addAttribute("w14:textId", UuidUtils.createUuid());
                        newpElement.addAttribute("w:rsidR", startPelem.attributeValue("rsidR"));
                        newpElement.addAttribute("w:rsidRDefault", startPelem.attributeValue("rsidRDefault"));
                        newpElement.addAttribute("w:rsidP", startPelem.attributeValue("rsidP"));
                        newpElement.setContent(startPelem.elements());
                        children1.add(startPindex + 2, newpElement);
                        for (Node node : nodesAfterCommentStart) {
                            startPelem.remove(node);
                        }
                        Iterator<Element> it = newpElement.elements().iterator();
                        while (it.hasNext()) {
                            Element e = it.next();
                            if ("commentRangeStart".equals(e.getName()) && commentId.equals(e.attributeValue("id"))) {
                                break;
                            } else {
                                it.remove();
                            }
                        }
                    }
                }

            }
            {// 批注结束标签 处理
                if (endPelem == null) {
                    endPelem = getParentElement(commentEndNode, "body");// 标签处于body下面
                    List<Element> children = endPelem.elements();

                    int endNodeIndex = -1;
                    int index = -1;
                    for (Element child : children) {
                        index++;
                        if (child == commentEndNode) {
                            endNodeIndex = index;
                            break;
                        }
                    }

                    Element commentEndElement = DocumentHelper.createElement("comment");
                    commentEndElement.addAttribute("id", commentId + "-end");

                    children.add(endNodeIndex, commentEndElement);

                    cmtObj.setEndElement(commentEndElement);
                } else {
                    List<Element> children = endPelem.elements();
                    boolean existNodeBeforeCommentEnd = false;
                    boolean existTextNodeAfterCommentEnd = false;// 在批注结束标签之后，如果没有文本节点，直接在批注结束标签的父亲元素之后加入占位符---不然会多一个空行
                    List<Node> nodesBeforeCommentEnd = new ArrayList<Node>();
                    int endNodeIndex = -1;
                    int index = -1;
                    for (Element child : children) {
                        index++;
                        child.asXML();
                        if (endNodeIndex == -1) {
                            nodesBeforeCommentEnd.add(child);
                            if (CollectionUtils.isNotEmpty(child.selectNodes("./w:t"))) {
                                existNodeBeforeCommentEnd = true;
                            }
                        }
                        if (child == commentEndNode) {
                            endNodeIndex = index;

                        }
                        if (endNodeIndex != -1 && CollectionUtils.isNotEmpty(child.selectNodes("./w:t"))) {
                            existTextNodeAfterCommentEnd = true;
                        }
                    }
                    Element commentEndElement = DocumentHelper.createElement("comment");
                    commentEndElement.addAttribute("id", commentId + "-end");
                    List<Element> children1 = endPelem.getParent().elements();
                    int index1 = -1;
                    int endPindex = -1;
                    for (Element child : children1) {
                        index1++;
                        if (child == endPelem) {
                            endPindex = index1;
                        }
                    }

                    if (existNodeBeforeCommentEnd && existTextNodeAfterCommentEnd
                            && CollectionUtils.isNotEmpty(nodesBeforeCommentEnd)) {// 标签前后均有文本数据
                        children1.add(endPindex, commentEndElement);
                        cmtObj.setEndElement(commentEndElement);
                        Element newpElement = DocumentHelper.createElement("w:p");
                        newpElement.addAttribute("w14:paraId", UuidUtils.createUuid());
                        newpElement.addAttribute("w14:textId", UuidUtils.createUuid());
                        newpElement.addAttribute("w:rsidR", endPelem.attributeValue("rsidR"));
                        newpElement.addAttribute("w:rsidRDefault", endPelem.attributeValue("rsidRDefault"));
                        newpElement.addAttribute("w:rsidP", endPelem.attributeValue("rsidP"));
                        newpElement.setContent(endPelem.elements());

                        children1.add(endPindex, newpElement);

                        for (Node node : nodesBeforeCommentEnd) {
                            endPelem.remove(node);
                        }
                        Iterator<Element> it = newpElement.elements().iterator();
                        boolean isAfter = false;
                        while (it.hasNext()) {
                            Element e = it.next();
                            if ("commentRangeEnd".equals(e.getName()) && commentId.equals(e.attributeValue("id"))) {
                                isAfter = true;
                            } else {
                                if (isAfter) {
                                    it.remove();
                                }
                            }

                        }
                    } else if (existNodeBeforeCommentEnd && !existTextNodeAfterCommentEnd) {// 前面有文本，后面没文本
                        children1.add(endPindex + 1, commentEndElement);
                        cmtObj.setEndElement(commentEndElement);
                    } else {// 前面没文本
                        children1.add(endPindex, commentEndElement);
                        cmtObj.setEndElement(commentEndElement);
                    }

                }
            }

        }
        return true;

    }

    private static Element getLeafCommentElement(Element element) {
        List<Element> elements = element.elements();
        if (CollectionUtils.isEmpty(elements)) {
            return element;
        } else {
            Element child = elements.get(0);
            if (child.isTextOnly()) {
                return child;
            } else {
                return getLeafCommentElement(child);
            }
        }

    }

    private static Element getParentElement(Node node, String tagName) {

        Element parent = node.getParent();
        if (parent.isRootElement()) {
            return null;
        } else {
            String name = parent.getName();
            if (!tagName.equals(name)) {
                return getParentElement(parent, tagName);
            } else {
                return parent;
            }
        }

    }

    private static List<WordComment> getWordComments(Document doc) {
        List<WordComment> comments = new ArrayList<WordComment>();
        List<Node> commentPartNodes = doc.selectNodes("//pkg:package/pkg:part[@pkg:name=\"/word/comments.xml\"]");
        if (CollectionUtils.isNotEmpty(commentPartNodes)) {
            for (Node commentPartNode : commentPartNodes) {
                List<Node> commentNodes = commentPartNode.selectNodes("pkg:xmlData/w:comments/w:comment");
                if (CollectionUtils.isNotEmpty(commentNodes)) {
                    for (Node commentNode : commentNodes) {
                        String id = commentNode.valueOf("@w:id");
                        List<Node> textNodes = commentNode.selectNodes("w:p/w:r/w:t");
                        StringBuffer comment = new StringBuffer();
                        if (CollectionUtils.isNotEmpty(textNodes)) {
                            for (Node textNode : textNodes) {
                                String text = textNode.getText();
                                comment.append(text);
                            }
                        }
                        String commentStr = comment.toString();
                        // if
                        // (commentStr.matches("([<#]{1}[^<^>]+>)+([</#]{1}[^<^>]+>)+"))
                        // {//这里不再判断，批注全部做为指令区, 如果要加判断，注意大于号，小于号问题
                        WordComment commentObj = new WordComment();
                        commentObj.setId(id);
                        commentObj.setContent(commentStr);
                        comments.add(commentObj);
                        // }

                    }
                }
            }
        }

        return comments;
    }
}
