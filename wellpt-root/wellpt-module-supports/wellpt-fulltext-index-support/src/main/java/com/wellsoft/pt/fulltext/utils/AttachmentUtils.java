/*
 * @(#)8/1/25 V1.0
 *
 * Copyright 2025 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.fulltext.utils;

import com.google.common.collect.Lists;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.context.util.json.JsonUtils;
import com.wellsoft.pt.fulltext.support.Attachment;
import com.wellsoft.pt.repository.entity.LogicFileInfo;
import com.wellsoft.pt.repository.entity.mongo.file.MongoFileEntity;
import com.wellsoft.pt.repository.service.MongoFileService;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Description:
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 8/1/25.1	    zhulh		8/1/25		    Create
 * </pre>
 * @date 8/1/25
 */
public class AttachmentUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(AttachmentUtils.class);

    private static List<String> WORD_EXTS = Lists.newArrayList("doc", "docx");
    private static List<String> WPS_EXTS = Lists.newArrayList("wps", "wpt");
    private static List<String> TEXT_BASE_EXTS = Lists.newArrayList("txt", "text", "log", "wps", "wpt", "rtf", "dot", "dotm", "dotx", "dot",
            "doc", "docm", "docx", "ppt", "pptm", "pptx", "csv", "xls", "xlsb", "xlsm", "xlsx", "pdf", "ssp");

    /**
     * @param mongoFileEntity
     * @return
     */
    public static String getFileContent(MongoFileEntity mongoFileEntity, String contentType) {
        try {
            if (supportsExtractFileContent(mongoFileEntity, contentType)) {
                // 大于30M的world文档，手动提取
                if (isWord(mongoFileEntity) && mongoFileEntity.getLength() > 1024 * 1024 * 30) {
                    return Base64.getEncoder().encodeToString(extractWordContent(mongoFileEntity));
                } else if (isWps(mongoFileEntity)) {
                    return Base64.getEncoder().encodeToString(extractWordContent(mongoFileEntity));
                }
                return Base64.getEncoder().encodeToString(IOUtils.toByteArray(mongoFileEntity.getInputstream()));
            }
            return Base64.getEncoder().encodeToString(Objects.toString(mongoFileEntity.getFileName(), StringUtils.EMPTY).getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return Base64.getEncoder().encodeToString(Objects.toString(mongoFileEntity.getFileName(), StringUtils.EMPTY).getBytes(StandardCharsets.UTF_8));
    }

    public static String getFileContent(String data) {
        if (StringUtils.isBlank(data)) {
            return Base64.getEncoder().encodeToString(StringUtils.EMPTY.getBytes(StandardCharsets.UTF_8));
        } else {
            return Base64.getEncoder().encodeToString(data.getBytes(StandardCharsets.UTF_8));
        }
    }

    /**
     * @param mongoFileEntity
     * @return
     */
    private static boolean isWord(MongoFileEntity mongoFileEntity) {
        String filename = mongoFileEntity.getFileName();
        return WORD_EXTS.contains(Objects.toString(FilenameUtils.getExtension(filename), StringUtils.EMPTY).toLowerCase());
    }

    /**
     * @param mongoFileEntity
     * @return
     */
    private static boolean isWps(MongoFileEntity mongoFileEntity) {
        String filename = mongoFileEntity.getFileName();
        return WPS_EXTS.contains(Objects.toString(FilenameUtils.getExtension(filename), StringUtils.EMPTY).toLowerCase());
    }

    /**
     * @param mongoFileEntity
     * @return
     */
    private static boolean supportsExtractFileContent(MongoFileEntity mongoFileEntity, String contentType) {
        String mongoFileContentTpe = mongoFileEntity.getContentType();
        if (StringUtils.startsWithIgnoreCase(contentType, "image/") ||
                StringUtils.startsWithIgnoreCase(contentType, "video/") ||
                StringUtils.startsWithIgnoreCase(mongoFileContentTpe, "image/") ||
                StringUtils.startsWithIgnoreCase(mongoFileContentTpe, "video/")) {
            return false;
        }
        if (StringUtils.startsWithIgnoreCase(contentType, "text/")
                || StringUtils.startsWithIgnoreCase(mongoFileContentTpe, "text/")) {
            return true;
        }

        String filename = mongoFileEntity.getFileName();
        return TEXT_BASE_EXTS.contains(Objects.toString(FilenameUtils.getExtension(filename), StringUtils.EMPTY).toLowerCase());
    }

    /**
     * 提取word文件内容
     *
     * @param mongoFileEntity
     * @return
     */
    private static byte[] extractWordContent(MongoFileEntity mongoFileEntity) {
        String content = null;
        try {
            String filename = mongoFileEntity.getFileName();
            String extension = FilenameUtils.getExtension(filename);
            if (StringUtils.equalsIgnoreCase("docx", extension)) {
                XWPFDocument docx = new XWPFDocument(mongoFileEntity.getInputstream());
                XWPFWordExtractor extractor = new XWPFWordExtractor(docx);
                content = extractor.getText();
                extractor.close();
            }
            if (StringUtils.equalsIgnoreCase("wpt", extension)) {
                HWPFDocument doc = new HWPFDocument(mongoFileEntity.getInputstream());
                WordExtractor extractor = new WordExtractor(doc);
                content = extractor.getText();
                extractor.close();
            } else {
                WordExtractor wordExtractor = new WordExtractor(mongoFileEntity.getInputstream());
                content = wordExtractor.getText();
                wordExtractor.close();
            }
            if (StringUtils.isEmpty(content)) {
                content = StringUtils.EMPTY;
            }
            return content.getBytes(StandardCharsets.UTF_8);
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
            content = mongoFileEntity.getFileName();
        }
        return content.getBytes(StandardCharsets.UTF_8);
    }

    public static String getEmptyFileContent() {
        return Base64.getEncoder().encodeToString(StringUtils.EMPTY.getBytes(StandardCharsets.UTF_8));
    }

    public static List<Attachment> logicFileInfos2Attachments(List<LogicFileInfo> fileInfos, boolean indexAttachment) {
        MongoFileService mongoFileService = ApplicationContextHolder.getBean(MongoFileService.class);
        List<Attachment> attachments = Lists.newArrayList();
        fileInfos.forEach(fileInfo -> {
            Attachment attachment = new Attachment();
            attachment.putAll(JsonUtils.gson2Object(JsonUtils.object2Json(fileInfo), Map.class));
            // attachment.setFileName(fileInfo.getFileName());

            if (indexAttachment) {
                MongoFileEntity mongoFileEntity = mongoFileService.getFile(fileInfo.getFileID());
                if (mongoFileEntity != null) {
                    attachment.setData(getFileContent(mongoFileEntity, fileInfo.getContentType()));
                } else {
                    attachment.setData(getEmptyFileContent());
                }
            } else {
                attachment.setData(getEmptyFileContent());
            }

            attachments.add(attachment);
        });
        return attachments;
    }
}
