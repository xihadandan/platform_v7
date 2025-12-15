/*
 * @(#)2020年3月17日 V1.0
 *
 * Copyright 2020 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.repository.support;

import com.wellsoft.context.config.SystemParamsUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * Description: 如何描述该类
 *
 * @author zhongzh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2020年3月17日.1	zhongzh		2020年3月17日		Create
 * </pre>
 * @date 2020年3月17日
 */
public abstract class RepoUtils {

    public static final String[] IMAGE_EXTS = "png,bmp,jpg,tiff,gif,pcx,tga,exif,fpx,svg,psd,cdr,pcd,dxf,ufo,eps,ai,raw"
            .split(",");

    public static boolean isOfdOrPdf(String fileName) {
        if (StringUtils.isBlank(fileName)) {
            return false;
        }
        fileName = fileName.toLowerCase();
        return fileName.endsWith(".ofd") || fileName.endsWith(".pdf");
    }

    public static String getOfdFileId(String fileId) {
        // converted
        return fileId + "-cvted";
    }

    public static boolean isWord(String fileName) {
        if (StringUtils.isBlank(fileName)) {
            return false;
        }
        fileName = fileName.toLowerCase();
        return fileName.endsWith(".docx") || fileName.endsWith(".doc") || fileName.endsWith(".wps")
                || fileName.endsWith(".wpsx") || fileName.endsWith(".odt");
    }

    public static boolean isPpt(String fileName) {
        if (StringUtils.isBlank(fileName)) {
            return false;
        }
        fileName = fileName.toLowerCase();
        return fileName.endsWith(".pptx") || fileName.endsWith(".ppt") || fileName.endsWith(".dps")
                || fileName.endsWith(".dpsx") || fileName.endsWith(".odp");
    }

    public static boolean isExcel(String fileName) {
        if (StringUtils.isBlank(fileName)) {
            return false;
        }
        fileName = fileName.toLowerCase();
        return fileName.endsWith(".xlsx") || fileName.endsWith(".xls") || fileName.endsWith(".et")
                || fileName.endsWith(".etx") || fileName.endsWith(".ods");
    }

    public static boolean isOffice(String fileName) {
        return isWord(fileName) || isPpt(fileName) || isExcel(fileName) || isOfdOrPdf(fileName);
    }

    public static boolean isImage(String fileName) {
        if (StringUtils.isBlank(fileName)) {
            return false;
        }
        fileName = fileName.toLowerCase();
        for (String ext : IMAGE_EXTS) {
            if (fileName.endsWith(ext)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isOfdConvertMethod() {
        return StringUtils.equals(SystemParamsUtils.getValue("word2pdf.convert.method", "ofd"), "ofd");
//		return false;
    }

}
