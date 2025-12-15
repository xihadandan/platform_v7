/*
 * @(#)Feb 1, 2018 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.support;

import com.google.common.collect.Lists;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.dms.entity.DmsFileEntity;
import com.wellsoft.pt.dms.enums.FileTypeEnum;
import com.wellsoft.pt.dms.file.registry.DmsFileTypeRegistry;
import com.wellsoft.pt.dms.model.DmsFile;
import com.wellsoft.pt.jpa.criteria.CriteriaMetadata;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * Feb 1, 2018.1	zhulh		Feb 1, 2018		Create
 * </pre>
 * @date Feb 1, 2018
 */
public class FileHelper {

    public static char[] INVALID_FILE_NAME_CHARS = new char[]{'\\', '/', ':', '*', '?', '"', '<', '>', '|'};
    public static String TIP_INVALID_FILE_NAME = "文件名不能包含下列任何字符：<br/>\\/:*?\"<>|";
    public static String TIP_INVALID_FOLDER_NAME = TIP_INVALID_FILE_NAME;
    private static DecimalFormat FILE_SIZE_DECIMAL_DF = new DecimalFormat("0.0");
    private static DecimalFormat FILE_SIZE_DF = new DecimalFormat("0.#");
    private static String FILE_NAME_PATTERN = "(?!((^(con)$)|^(con)//..*|(^(prn)$)|^(prn)//..*|(^(aux)$)|^(aux)//..*|(^(nul)$)|^(nul)//..*|(^(com)[1-9]$)|^(com)[1-9]//..*|(^(lpt)[1-9]$)|^(lpt)[1-9]//..*)|^//s+|.*//s$)(^[^/////////://*//?//\\\"//<//>//|]{1,255}$)";

    /**
     * @param dmsFile
     * @return
     */
    public static boolean isValidFileName(String fileName) {
        if (StringUtils.isBlank(fileName)) {
            return false;
        }
        if (StringUtils.containsAny(fileName, INVALID_FILE_NAME_CHARS)) {
            return false;
        }
        Pattern pattern = Pattern.compile(FILE_NAME_PATTERN);
        Matcher matcher = pattern.matcher(fileName.toLowerCase());
        return matcher.find();
    }

    /**
     * @param dmsFile
     * @return
     */
    public static boolean isFolder(DmsFile dmsFile) {
        return StringUtils.equals(FileMediaType.APPLICATION_FOLDER, dmsFile.getContentType());
    }

    /**
     * @param fileEntity
     */
    public static boolean isDyform(DmsFileEntity fileEntity) {
        return FileTypeEnum.DYFORM.getType().equals(fileEntity.getContentType());
    }

    /**
     * @param dmsFile
     * @return
     */
    public static boolean isDyform(DmsFile dmsFile) {
        return FileTypeEnum.DYFORM.getType().equals(dmsFile.getContentType());
    }

    /**
     * 文件大小，转化为KB、M、G的展现形式
     *
     * @param size
     * @param contentType
     * @return
     */
    public static String getFileSizeAsString(Object size, String contentType) {
        // 文件夹返回空
        if (FileMediaType.APPLICATION_FOLDER.equals(contentType)) {
            return StringUtils.EMPTY;
        }

        String fileSizeString = StringUtils.EMPTY;
        long fileSize = 0;
        if (size instanceof Long) {
            fileSize = ((Long) size).longValue();
        } else if (size instanceof BigDecimal) {
            fileSize = ((BigDecimal) size).longValue();
        } else if (size != null) {
            try {
                fileSize = Long.valueOf(size.toString());
            } catch (Exception e) {
            }
        }

        if (fileSize == 0) {
            fileSizeString = "0" + " KB";
        } else if (fileSize < 1024) {
            fileSizeString = "1" + " KB";
        } else if (fileSize < 1024 * 1024) {
            fileSizeString = fileSize / 1024 + " KB";
        } else if (fileSize < 1024 * 1024 * 1024) {
            String decimalDf = getDecimalFormat(fileSize, 1024 * 1024);
            fileSizeString = FILE_SIZE_DF.format((fileSize / (1024 * 1024) + Double.valueOf(decimalDf))) + " M";
        } else {
            String decimalDf = getDecimalFormat(fileSize, 1024 * 1024 * 1024);
            fileSizeString = FILE_SIZE_DF.format((fileSize / (1024 * 1024 * 1024) + Double.valueOf(decimalDf))) + " G";
        }
        return fileSizeString;
    }

    /**
     * @param fileSize
     * @return
     */
    private static String getDecimalFormat(long fileSize, int divisor) {
        double decimal = fileSize % (divisor) / (divisor * 1d);
        return FILE_SIZE_DECIMAL_DF.format(decimal);
    }

    /**
     * @param filename
     * @param contentType
     * @return
     */
    public static String getFileTypeAsString(String filename, String contentType) {
        if (FileTypeEnum.FOLDER.getType().equals(contentType)) {
            return FileTypeEnum.FOLDER.getName();
        }
        if (FileTypeEnum.DYFORM.getType().equals(contentType)) {
            return FileTypeEnum.DYFORM.getName();
        }
        if (StringUtils.isBlank(filename)) {
            return FileTypeEnum.FILE.getName();
        }
        String extension = FilenameUtils.getExtension(filename);
        if (StringUtils.isBlank(filename)) {
            return FileTypeEnum.FILE.getName();
        }

        DmsFileTypeRegistry dmsFileTypeRegistry = ApplicationContextHolder.getBean(DmsFileTypeRegistry.class);
        String typeName = dmsFileTypeRegistry.getName(contentType);
        if (StringUtils.isNotBlank(typeName)) {
            return typeName;
        }
        return StringUtils.upperCase(extension) + " " + FileTypeEnum.FILE.getName();
    }

    /**
     * @return
     */
    public static CriteriaMetadata getCriteriaMetadata() {
        CriteriaMetadata criteriaMetadata = CriteriaMetadata.createMetadata();
        criteriaMetadata.add("uuid", "t.uuid", "UUID", String.class);
        criteriaMetadata.add("name", "t.name", "名称", String.class);
        criteriaMetadata.add("sourcePath", "t.folder_uuid", "源路径", String.class);
        criteriaMetadata.add("deletedTime", "r.create_time", "删除时间", Date.class);
        criteriaMetadata.add("deleteUserId", "r.user_id", "删除人ID", String.class);
        criteriaMetadata.add("deleteUserName", "ru.user_name", "删除人", String.class);
        criteriaMetadata.add("code", "t.code", "编号", String.class);
        criteriaMetadata.add("contentType", "t.content_type", "类型", String.class);
        criteriaMetadata.add("fileSize", "t.file_size", "大小", Long.class);
        criteriaMetadata.add("status", "t.status", "状态", String.class);
        criteriaMetadata.add("dataDefUuid", "t.data_def_uuid", "数据定义UUID", String.class);
        criteriaMetadata.add("dataUuid", "t.data_uuid", "数据UUID", String.class);
        criteriaMetadata.add("folderUuid", "t.folder_uuid", "所在夹UUID", String.class);
        criteriaMetadata.add("creator", "t.creator", "创建人ID", String.class);
        criteriaMetadata.add("creatorName", "t.creator_name", "创建人", String.class);
        criteriaMetadata.add("createTime", "t.create_time", "创建时间", Date.class);
        criteriaMetadata.add("modifier", "t.modifier", "修改人ID", String.class);
        criteriaMetadata.add("modifyTime", "t.modify_time", "修改时间", Date.class);
        return criteriaMetadata;
    }

    /**
     * @param fileName
     * @return
     */
    public static String getInValidFileNameTip(String fileName) {
        if (StringUtils.containsAny(fileName, INVALID_FILE_NAME_CHARS)) {
            return TIP_INVALID_FILE_NAME;
        }
        return "无效的文件名称，请输入可在本地操作系统上创建的文件名称！";
    }

    /**
     * @param folderName
     * @return
     */
    public static String getInValidFolderNameTip(String folderName) {
        if (StringUtils.containsAny(folderName, INVALID_FILE_NAME_CHARS)) {
            return TIP_INVALID_FOLDER_NAME;
        }
        return "无效的文件夹名称，请输入可在本地操作系统上创建的文件夹名称！";
    }

    /**
     * @param folderUuids
     * @param values
     * @return
     */
    public static String generateQueryFolderUuidSql(List<String> folderUuids, Map<String, Object> values) {
        return generateQueryFolderUuidSql(folderUuids, values, "query");
    }

    /**
     * @param folderUuids
     * @param values
     * @param paramPrefix
     * @return
     */
    public static String generateQueryFolderUuidSql(List<String> folderUuids, Map<String, Object> values, String paramPrefix) {
        if (CollectionUtils.isEmpty(folderUuids)) {
            return StringUtils.EMPTY;
        }
        List<String> sqls = Lists.newArrayList();
        Set<String> tmpFolderUuids = new HashSet<String>();
        int num = 0;
        for (String folderUuid : folderUuids) {
            num++;
            tmpFolderUuids.add(folderUuid);
            if (num % 1000 == 0 || num == folderUuids.size()) {
                String paramName = paramPrefix + "_queryFolderUuids_" + num;
                sqls.add("select f.uuid as folder_uuid from dms_folder f where f.uuid in(:" + paramName + ")");
                values.put(paramName, Lists.<String>newArrayList(tmpFolderUuids));
                tmpFolderUuids.clear();
            }
        }
        return StringUtils.join(sqls, " union all ");
    }

}
