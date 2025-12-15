/*
 * @(#)2014-3-14 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.printtemplate.support;

import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.ComThread;
import com.jacob.com.Dispatch;
import com.jacob.com.Variant;
import com.wellsoft.pt.basicdata.printtemplate.entity.PrintTemplate;
import org.apache.commons.io.IOUtils;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.usermodel.Range;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Description: 如何描述该类
 *
 * @author Administrator
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2014-3-14.1	Administrator		2014-3-14		Create
 * </pre>
 * @date 2014-3-14
 */
public class PrinttemplateUtil {
    private static Logger logger = LoggerFactory.getLogger(PrinttemplateUtil.class);

    /**
     * html转为word
     *
     * @param htmlFullPath
     * @param docFullPath
     */
    public static void htmlToWord(String htmlFullPath, String docFullPath) {
        ActiveXComponent word = getActiveXComponent(); // 启动word线程
        try {
            word.setProperty("Visible", new Variant(false));
            // Dispatch docs = word.getProperty("Documents").toDispatch();
            Dispatch docs = getWordDispatchDocument(word);
            Dispatch doc = Dispatch.invoke(docs, "Open", Dispatch.Method,
                    new Object[]{htmlFullPath, new Variant(false), new Variant(true)}, new int[1]).toDispatch();
            Dispatch.invoke(doc, "SaveAs", Dispatch.Method, new Object[]{docFullPath, new Variant(1)}, new int[1]);
            Variant f = new Variant(false);
            Dispatch.call(doc, "Close", f);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            closeActiveXComponent(word);// 关闭word线程
        }
    }

    /**
     * xml转为word
     *
     * @param htmlFullPath
     * @param docFullPath
     */
    public static void xmlToWord(String xmlFullPath, String docFullPath) {
        ActiveXComponent app = new ActiveXComponent("Word.Application"); // 启动word
        try {
            app.setProperty("Visible", new Variant(false));
            Dispatch docs = app.getProperty("Documents").toDispatch();
            Dispatch doc = Dispatch.invoke(docs, "Open", Dispatch.Method,
                    new Object[]{xmlFullPath, new Variant(false), new Variant(true)}, new int[1]).toDispatch();
            Dispatch.invoke(doc, "SaveAs", Dispatch.Method, new Object[]{docFullPath, new Variant(1)}, new int[1]);
            Variant f = new Variant(false);
            Dispatch.call(doc, "Close", f);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            app.invoke("Quit", new Variant[]{});
            ComThread.Release();
        }
    }

    /**
     * word转为html
     *
     * @param htmlFullPath
     * @param docFullPath
     */
    public static void wordToHtml(String docFullPath, String htmlFullPath) {
        ActiveXComponent word = new ActiveXComponent("Word.Application"); // 启动word
        try {
            word.setProperty("Visible", new Variant(false));
            Dispatch docs = getWordDispatchDocument(word);
            Dispatch doc = Dispatch.invoke(docs, "Open", Dispatch.Method,
                    new Object[]{docFullPath, new Variant(false), new Variant(true)}, new int[1]).toDispatch();
            Dispatch.invoke(doc, "SaveAs", Dispatch.Method, new Object[]{htmlFullPath, new Variant(8)}, new int[1]);
            Variant f = new Variant(false);
            Dispatch.call(doc, "Close", f);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            closeActiveXComponent(word);
        }
    }

    public static void main(String[] args) {
        String fileName = "D:\\a.xml";
        String content = "D:\\a.doc";
        xmlToWord(fileName, content);
    }

    /**
     * 将word文件转为xml格式
     */
    public static File wordToXml(String inputDocPath, String outPutDocPath) {
        // 初始化com的线程
        ComThread.InitSTA();
        // word运行程序对象
        ActiveXComponent word = null;
        try {
            // word运行程序对象
            word = new ActiveXComponent("Word.Application");
            // 文档对象
            Dispatch wordObject = (Dispatch) word.getObject();
            // 设置属性 Variant(true)表示word应用程序可见
            Dispatch.put((Dispatch) wordObject, "Visible", new Variant(false));
            // word所有文档
            Dispatch documents = word.getProperty("Documents").toDispatch();
            // 打开模板文档
            Dispatch document = PrinttemplateUtil.open(documents, inputDocPath);

            // 另存为指定格式的文档
            Dispatch.invoke(document, "SaveAs", Dispatch.Method, new Object[]{outPutDocPath, new Variant(19)},
                    new int[1]);
        } finally {
            if (word != null) {
                word.invoke("Quit", new Variant[0]);
            }
            // 关闭com的线程
            ComThread.Release();
        }
        return new File(outPutDocPath + ".ftl");
    }

    /**
     * 启动word线程
     *
     * @return
     */
    public static ActiveXComponent getActiveXComponent() {
        // 初始化com的线程
        ComThread.InitSTA();
        ActiveXComponent word = new ActiveXComponent("Word.Application");
        return word;
    }

    /**
     * 关闭word线程
     *
     * @return
     */
    public static void closeActiveXComponent(ActiveXComponent word) {
        word.invoke("Quit", new Variant[0]);
        // 关闭com的线程
        ComThread.Release();
    }

    /**
     * 获取word的Dispatch对象
     */
    public static Dispatch getWordDispatchDocument(ActiveXComponent word) {
        // 文档对象
        Dispatch wordObject = (Dispatch) word.getObject();
        // 设置属性 Variant(true)表示word应用程序可见
        Dispatch.put((Dispatch) wordObject, "Visible", new Variant(false));
        // word dispatch对象
        Dispatch dispatchDocument = word.getProperty("Documents").toDispatch();
        return dispatchDocument;
    }

    /**
     * 打开文件
     *
     * @param documents    word的Dispath对象
     * @param inputDocPath 需打开的word文件的完整路径
     * @return
     */
    public static Dispatch open(Dispatch doc, String inputDocPath) {
        // 打开word
        Dispatch document = Dispatch.call(doc, "Open", inputDocPath).toDispatch();
        return document;
    }

    /**
     * 关闭文件
     *
     * @param doc
     */
    public static void close(Dispatch doc) {
        Dispatch.call(doc, "Close", new Variant(true));
    }

    /**
     * 文件保存或另存为
     *
     * @param savePath 一定要记得加上扩展名 .doc 保存或另存为路径
     */
    public static void save(ActiveXComponent word, String savePath) {
        Dispatch.call(Dispatch.call(word, "WordBasic").getDispatch(), "FileSaveAs", savePath);
    }

    /**
     * 设置页面方向和页边距
     *
     * @param orientation  可取值0或1，分别代表横向和纵向
     * @param pageSize     A3是8，A4是9，A5是11等等
     * @param leftMargin   左边距的值
     * @param rightMargin  右边距的值
     * @param topMargin    上边距的值
     * @param buttomMargin 下边距的值
     */
    public static void setPageSetup(Dispatch doc, int orientation, int pageSize, int leftMargin, int rightMargin,
                                    int topMargin, int buttomMargin) {
        Dispatch pageSetup = Dispatch.get(doc, "PageSetup").toDispatch();
        Dispatch.put(pageSetup, "Orientation", orientation);
        Dispatch.put(pageSetup, "PaperSize", new Integer(pageSize));
        Dispatch.put(pageSetup, "LeftMargin", leftMargin);
        Dispatch.put(pageSetup, "RightMargin", rightMargin);
        Dispatch.put(pageSetup, "TopMargin", topMargin);
        Dispatch.put(pageSetup, "BottomMargin", buttomMargin);
    }

    /**
     * 插入空行
     *
     * @param selection
     */
    public static void insertBlank(Dispatch selection) {
        Dispatch.call(selection, "TypeParagraph");// 插入空行
    }

    /**
     * 插入分页符
     *
     * @param selection
     */
    public static void PageBreak(Dispatch selection) {
        Dispatch.call(selection, "InsertBreak", new Variant(7));
    }

    /**
     * 按下Ctrl + End键
     */
    public static void goToEnd(Dispatch selection) {
        Dispatch.call(selection, "EndKey", "6");
    }

    /**
     * 执行某条宏指令
     *
     * @param cmd
     */
    public static void cmd(String cmd, Dispatch selection) {
        Dispatch.call(selection, cmd);
    }

    /**
     * 在当前光标处做粘贴
     */
    public static void paste(Dispatch selection) {
        Dispatch.call(selection, "Paste");
    }

    /**
     * 把选定的内容或插入点向上移动
     *
     * @param pos 移动的距离
     */

    public static void moveUp(int pos, Dispatch selection, ActiveXComponent word) {
        if (selection == null)
            selection = Dispatch.get(word, "Selection").toDispatch();
        for (int i = 0; i < pos; i++)
            Dispatch.call(selection, "MoveUp");
    }

    /**
     * 把选定的内容或者插入点向下移动
     *
     * @param pos 移动的距离
     */

    public static void moveDown(Dispatch selection, ActiveXComponent word) {
        if (selection == null)
            selection = Dispatch.get(word, "Selection").toDispatch();
        // for (int i = 0; i < pos; i++)
        Dispatch.call(selection, "MoveDown");
    }

    /**
     * 从选定内容或插入点开始查找文本
     *
     * @param toFindText 要查找的文本
     * @return boolean true-查找到并选中该文本，false-未查找到文本
     */

    public static boolean find(String toFindText, Dispatch selection, ActiveXComponent word) {
        if (toFindText == null || toFindText.equals(""))
            return false;
        // 从selection所在位置开始查询
        Dispatch find = ActiveXComponent.call(selection, "Find").toDispatch();
        // 设置要查找的内容
        Dispatch.put(find, "Text", toFindText);
        // 向前查找
        Dispatch.put(find, "Forward", "True");
        // 设置格式
        Dispatch.put(find, "Format", "True");
        // 大小写匹配
        Dispatch.put(find, "MatchCase", "True");
        // 全字匹配
        Dispatch.put(find, "MatchWholeWord", "True");
        // 查找并选中
        return Dispatch.call(find, "Execute").getBoolean();
    }

    /**
     * POI读取word文本内容
     *
     * @return
     */
    public static String getWordText(File tempFile) {
        // POI读取word模板
        String text = "";
        FileInputStream in = null;
        try {
            in = new FileInputStream(tempFile);
            HWPFDocument hdt = new HWPFDocument(in);
            // 读取word文本内容
            //			  Fields fields = hdt.getFields();
            //			  Iterator<Field> it = fields.getFields(FieldsDocumentPart.MAIN) .iterator();
            //			  while(it.hasNext()) {
            //				   System.out.println(it.next().getType());
            //			  }
            Range range = hdt.getRange();
            text = range.text();
            logger.info("word文本内容：" + text);
        } catch (FileNotFoundException e1) {
            logger.error(e1.getMessage(), e1);
        } catch (IOException e1) {
            logger.error(e1.getMessage(), e1);
        } finally {
            IOUtils.closeQuietly(in);
        }
        return text;
    }

    /**
     * 选定内容
     *
     * @param word
     * @return
     */
    public static Dispatch select(ActiveXComponent word) {
        return word.getProperty("Selection").toDispatch();
    }

    /**
     * 把插入点移动到文件首位置
     *
     * @param selection
     */
    public static void moveStart(Dispatch selection) {
        Dispatch.call(selection, "HomeKey", new Variant(6));
    }

    /**
     * 从选定内容或插入点开始查找文本
     *
     * @param selection  选定内容
     * @param toFindText 要查找的文本
     * @return true：查找到并选中该文本；false：未查找到文本。
     */

    public static boolean find(Dispatch selection, String toFindText) {
        // 从selection所在位置开始查询
        Dispatch find = Dispatch.call(selection, "Find").toDispatch();
        // 设置要查找的内容
        Dispatch.put(find, "Text", toFindText);
        // 向前查找
        Dispatch.put(find, "Forward", "True");
        // 设置格式
        Dispatch.put(find, "format", "True");
        // 大小写匹配
        Dispatch.put(find, "MatchCase", "True");
        // 全字匹配
        Dispatch.put(find, "MatchWholeWord", "True");
        // 查找并选中
        return Dispatch.call(find, "Execute").getBoolean();
    }

    /**
     * 把选定内容替换为设定文本
     *
     * @param selection
     * @param newText
     */
    public static void replace(Dispatch selection, String newText) {
        Dispatch.put(selection, "Text", newText);
    }

    /**
     * 全局替换
     *
     * @param selection
     * @param oldText
     * @param replaceObj
     */
    public static void replaceAll(Dispatch selection, String oldText, Object replaceObj) {
        //		moveStart(selection);
        String newText = (String) replaceObj;
        while (find(selection, oldText)) {
            replace(selection, newText);
            Dispatch.call(selection, "MoveRight");
        }
    }

    /**
     * 全局替换
     *
     * @param selection
     * @param oldText
     * @param replaceObj
     */
    public static void replaceTextAll(Dispatch selection, String oldText, Object replaceObj) {
        moveStart(selection);
        String newText = (String) replaceObj;
        while (find(selection, oldText)) {
            replace(selection, newText);
            Dispatch.call(selection, "MoveRight");
        }
    }

    public static void replaceFile(Dispatch selection, String psKey, String psFileName) {
        moveStart(selection);
        while (find(selection, psKey)) {
            // Dispatch.invoke(selection, "InsertFile", Dispatch.Method, new Object[] { psFileName, new Variant(""), new Variant(false),new Variant(false),new Variant(false) }, new int[1]).toDispatch();
            Dispatch.call(selection, "InsertFile", psFileName, new Variant(""), new Variant(false), new Variant(false), new Variant(
                    false));
        }
    }

    /**
     * 打印
     *
     * @param document
     */
    public static void print(Dispatch document) {
        Dispatch.call(document, "PrintOut");
    }

    /**
     * 保存文件
     *
     * @param word
     * @param outputPath
     */
    public static void save(ActiveXComponent word, String outputPath, PrintTemplate printTemplate, Dispatch document) {
        Dispatch.call(Dispatch.call(word, "WordBasic").getDispatch(), "FileSaveAs", outputPath);
        if (printTemplate.getIsReadOnly()) {
            new File(outputPath).setWritable(false);// 将输出文档设为只读
        }
    }

    /**
     * 获取随机数(年月日时分秒+五位随机数)
     *
     * @return
     */
    public static String getFileName() {
        Date date = new Date();
        SimpleDateFormat sf = new SimpleDateFormat("yyyyMMddHHmmss");
        int temp = (int) (Math.random() * 100000);
        while (temp / 10000 < 1) {
            temp = (int) (Math.random() * 100000);
        }
        return sf.format(date) + temp;
    }

    public static void initSTA() {
        try {
            // 初始化com的线程
            ComThread.InitSTA();
        } catch (NoClassDefFoundError e) { // extends Error
            logger.info("ComThread初始化失败,请检测jacob-xxx.dll是否在系统PATH目录下", e);
            throw new RuntimeException("ComThread初始化失败,请检测jacob-xxx.dll是否在系统PATH目录下", e);
        }
    }

}
