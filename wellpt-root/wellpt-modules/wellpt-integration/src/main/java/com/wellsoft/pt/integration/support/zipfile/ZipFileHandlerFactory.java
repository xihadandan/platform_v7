/*
 * @(#)2013-12-26 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.integration.support.zipfile;

import com.wellsoft.context.util.ApplicationContextHolder;
import org.apache.commons.lang.StringUtils;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;

import java.util.Enumeration;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-12-26.1	zhulh		2013-12-26		Create
 * </pre>
 * @date 2013-12-26
 */
public class ZipFileHandlerFactory {

    public static ZipFileHandler getZipFileHandler(ZipFile zipFile) {
        Enumeration<ZipEntry> e = zipFile.getEntries();
        while (e.hasMoreElements()) {
            ZipEntry entry = e.nextElement();
            if (entry.isDirectory()) {
                continue;
            }

            String name = entry.getName();
            String extension = StringUtils.substring(name, name.lastIndexOf(".") + 1);
            if ("xls".equalsIgnoreCase(extension) || "xlsx".equalsIgnoreCase(extension)) {
                return ApplicationContextHolder.getBean(ExcelZipFileHanlder.class);
            }
        }

        return null;
    }

}
