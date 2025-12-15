package com.wellsoft.pt.basicdata.printtemplate.support.utils;

import org.dom4j.Node;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
public class WordTextNode {
    private Node textNode;
    private String content;

    public static void main(String[] args) {
        // TODO Auto-generated method stub
        String str = "Hello,World! in Java. hello world in batch ,hello world in groovy";
        Pattern pattern = Pattern.compile("(lo)");
        Matcher matcher = pattern.matcher(str);

        while (matcher.find()) {
            int i = matcher.groupCount();
            System.out.println("---->" + i);
            System.out.println("Group 0:" + matcher.group(0));// 得到第0组——整个匹配
            System.out.println("Group 1:" + matcher.group(1));// 得到第一组匹配——与(or)匹配的
            // System.out.println("Group 2:" + matcher.group(2));//
            // 得到第二组匹配——与(ld!)匹配的，组也就是子表达式
            System.out.println("Start 0:" + matcher.start(0) + " End 0:" + matcher.end(0));// 总匹配的索引
            System.out.println("Start 1:" + matcher.start(1) + " End 1:" + matcher.end(1));// 第一组匹配的索引
            // System.out.println("Start 2:" + matcher.start(2) + " End 2:" +
            // matcher.end(2));// 第二组匹配的索引
            // System.out.println(str.substring(matcher.end(1)));//
            // 从总匹配开始索引到第1组匹配的结束索引之间子串——Wor

            str = "abcdfdfsabcdfdsf abc fdfdabcabc";
            // 1. 用这则表达式处理, 不过好像一点都不省事..
            Pattern p = Pattern.compile("abc", Pattern.CASE_INSENSITIVE);
            Matcher m = p.matcher(str);
            int count = 0;
            while (m.find()) {
                count++;
            }
            System.out.println("ABC的个数 : " + count);

            Pattern ftlPattern = Pattern.compile("<#[^<]+>");

            System.out.println(ftlPattern.matcher("bbbbbbbbbbbb<#abc>aa>aaaaaaaaaa").find());
            System.out.println(ftlPattern.matcher("&lt;#c&gt;&gt;").find());
            System.out.println(ftlPattern.matcher("&lt;#&gt;c&gt;").find());

        }
    }

    public Node getTextNode() {
        return textNode;
    }

    public void setTextNode(Node textNode) {
        this.textNode = textNode;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
