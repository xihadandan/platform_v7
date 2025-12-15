/*
 * @(#)Feb 8, 2018 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.enums;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * Feb 8, 2018.1	zhulh		Feb 8, 2018		Create
 * </pre>
 * @date Feb 8, 2018
 */
public enum FileTypeEnum {
    FOLDER("application/folder", "文件夹"), // 文件夹
    FILE("application/file", "文件"), // 文件
    DYFORM("application/dyform", "表单文件"), // 动态表单
    EXCEL("ods,xls,xlsb,xlsm,xlsx", "Microsoft Office Excel 工作表"), // EXCEL
    PPT("odp,pot,potm,potx,pps,ppsm,ppsx,ppt,pptm,pptx", "Microsoft Office PowerPoint 演示文稿"), // PPT
    DOC("doc,docm,docx,dot,dotm,dotx,odt", "Microsoft Office Word 文档"), // DOC
    PDF("pdf", "Foxit Reader PDF Document"), // PDF
    RAR("rar", "WinRAR 压缩文件"), // RAR
    ZIP("zip", "WinRAR ZIP 压缩文件"), // ZIP
    GIF("gif", "GIF 图像"), // GIF
    PNG("png", "PNG 图像"), // PNG
    JPEG("jpg,jpeg", "JPEG 图像"), // BMP
    TIF("tif", "TIF 图像"), // TIF
    BMP("bmp", "BMP 图像"), // BMP
    EXE("exe", "应用程序"), // EXE
    TXT("txt,text,log", "文本文档"), // TXT
    MMAP("mmap", "Mindjet Document"), // MMAP
    UNKNOW("unknow", "未知文件");

    // 成员变量
    private String type;
    private String name;

    // 构造方法
    private FileTypeEnum(String type, String name) {
        this.type = type;
        this.name = name;
    }

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type 要设置的type
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name 要设置的name
     */
    public void setName(String name) {
        this.name = name;
    }

}
