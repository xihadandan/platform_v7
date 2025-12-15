/*
 * @(#)2014-6-28 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.repository.jobs;

import com.mongodb.gridfs.GridFSDBFile;
import com.wellsoft.context.config.Config;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.context.util.ThreadUtils;
import com.wellsoft.pt.mt.entity.Tenant;
import com.wellsoft.pt.mt.facade.service.TenantFacadeService;
import com.wellsoft.pt.repository.entity.LogicFileInfo;
import com.wellsoft.pt.repository.service.GarbageCollectorService;
import com.wellsoft.pt.repository.service.MongoFileService;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Description: 垃圾文件回收任务
 *
 * @author hunt
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2014-6-28.1	hunt		2014-6-28		Create
 * </pre>
 * @date 2014-6-28
 */
public class GarbageCollectorStartUp {

    private static final int maxResults = 100;
    private static final String garbagetime = StringUtils.isEmpty(Config.getValue("mongodb.server.gc.garbagetime")) ? "4"
            : Config.getValue("mongodb.server.gc.garbagetime");
    private static final boolean purgeHistoryFiles = "true".equals(Config.getValue("mongodb.server.gc.historyfile"));
    private static Logger logger = LoggerFactory.getLogger(GarbageCollectorStartUp.class);
    private static boolean locked;
    private static int firstResult = 0;

    // private static final MongoFileService mongoService = (MongoFileService)
    // ApplicationContextHolder.getBean("mongoFileService");

    // private static final TenantService tenantService = (TenantService)
    // ApplicationContextHolder.getBean("tenantService");

    // private static final GarbageCollectorService garbageCollectorService =
    // (GarbageCollectorService)
    // ApplicationContextHolder.getBean("garbageCollectorService");

    /**
     * 如何描述该方法
     */
    public static void doExecute() {
        if (locked) {
            return;
        }
        try {
            locked = true;
            logger.info("mongo garbage collect startup");
            GarbageCollectorStartUp.startup();
            logger.info("mongo garbage collect over");
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            locked = false;
        }
    }

    private static void startup() {
        logger.info("------------>clean the file in repo_file but not in repo_file_in_folder  ");
        TenantFacadeService tenantService = ApplicationContextHolder.getBean(TenantFacadeService.class);
        List<Tenant> tenants = tenantService.getActiveTenants();
        for (Tenant tenant : tenants) {
            String tenantId = tenant.getId();
            GarbageCollectorStartUp.cleanGarbageKindOne(tenantId);// clean the
            // file in
            // repo_file
            // but not
            // in
            // repo_file_in_folder
        }
        logger.info("the file in repo_file but not in repo_file_in_folder has been cleaned");

        logger.info("------------>clean the file in mongo but not in repo_file");
        GarbageCollectorService gcService = ApplicationContextHolder.getBean(GarbageCollectorService.class);
        for (Tenant tenant : tenants) {
            String tenantId = tenant.getId();
            // cleanGarbageKindTwo(tenantId);//clean the file in mongo but not
            // in repo_file
            gcService.syncRepoFileMirror(tenantId);
            gcService.cleanGridFS(tenantId);
        }

        logger.info("the file in mongo but not in repo_file has been cleaned");

    }

    private static void cleanGarbageKindTwo(String tenantId) {
        MongoFileService mongoService = ApplicationContextHolder.getBean(MongoFileService.class);
        List<GridFSDBFile> fsdbFiles = mongoService.findProtoFilesWithTenantId(tenantId, garbagetime);
        if (fsdbFiles == null || fsdbFiles.size() == 0) {
            return;
        }

        long uselesscount = 0l;
        long usefulcount = 0l;
        long uselessSize = 0l;
        long usefulSize = 0l;
        long totalcount = 0;
        List<String> physicalFileIds = new ArrayList<String>();
        Map<String, GridFSDBFile> physicalInfos = new HashMap<String, GridFSDBFile>();
        for (GridFSDBFile fsdbFile : fsdbFiles) {
            String physicalFileId = fsdbFile.getId().toString();
            physicalFileIds.add(physicalFileId);
            physicalInfos.put(physicalFileId, fsdbFile);
            totalcount++;
            if (physicalFileIds.size() <= 60 && totalcount != fsdbFiles.size()) {
                continue;
            }

            List<LogicFileInfo> logicFileInfos = mongoService.getFilesByPhysicalFileId(physicalFileIds);
            for (String physicalFileIdx : physicalFileIds) {
                fsdbFile = physicalInfos.get(physicalFileIdx);
                boolean isUsed = isUsedOfPhysicalFile(physicalFileIdx, logicFileInfos);
                if (isUsed) {
                    usefulcount++;
                    usefulSize += fsdbFile.getLength();
                    logger.info("used file:" + physicalFileIdx + "(" + fsdbFile.getFilename() + ") fulcount: "
                            + usefulcount + "space:" + usefulSize + "  total:" + totalcount);
                } else {
                    mongoService.destroyProtoFile(fsdbFile.getId().toString());
                    uselesscount++;
                    uselessSize += fsdbFile.getLength();
                    logger.info("------->find a useless file:" + physicalFileIdx + "(" + fsdbFile.getFilename()
                            + ") lesscount:" + uselesscount + " space:" + uselessSize + "  total:" + totalcount);
                }
            }

            physicalFileIds.clear();
            physicalInfos.clear();
            ThreadUtils.sleep(100);
        }
        logger.info("no useless file any more ^-^ it's cleaned count:" + uselesscount + "  size:" + uselessSize);

    }

    private static void cleanGarbageKindOne(String tenantId) {
        MongoFileService mongoService = ApplicationContextHolder.getBean(MongoFileService.class);
        List<QueryItem> fileItems = mongoService.getFilesByPage(tenantId, firstResult, maxResults, garbagetime);
        GarbageCollectorStartUp.doProcess(tenantId, fileItems, mongoService);

        // 重置状态
        firstResult = 0;
    }

    private static boolean isUsedOfPhysicalFile(String physicalFileId, List<LogicFileInfo> logicFileInfos) {
        if (logicFileInfos == null || logicFileInfos.size() == 0) {
            return false;
        }
        for (LogicFileInfo lfile : logicFileInfos) {
            if (physicalFileId.equals(lfile.getPhysicalFileId())) {
                return true;
            }
        }
        return false;
    }

    private static void doProcess(String tenantId, List<QueryItem> fileItems, MongoFileService mongoService) {
        // 删除repo_file_in_folder找不到关系的repo_file
        GarbageCollectorStartUp.delFiles(fileItems, mongoService);
        List<QueryItem> fileItemsNew = mongoService.getFilesByPage(tenantId, firstResult, maxResults, garbagetime);
        if (fileItemsNew != null) {
            if (fileItemsNew.size() == maxResults) {
                ThreadUtils.sleep(100);
                GarbageCollectorStartUp.doProcess(tenantId, fileItemsNew, mongoService);
            } else {
                GarbageCollectorStartUp.delFiles(fileItemsNew, mongoService);
            }
        }
    }

    /**
     * 删除repo_file_in_folder找不到关系的repo_file
     *
     * @param fileItems
     */
    private static void delFiles(List<QueryItem> fileItems, MongoFileService mongoService) {
        for (QueryItem item : fileItems) {
            String fileID = item.getString("uuid");
            if (mongoService.isFileInFolder(fileID)) {
                // 文件如果在文件夹中则不处理
                firstResult++;
                continue;
            } else if (isHistoryFileInFolder(fileID, mongoService)) {
                // 不清理历史
                if (false == purgeHistoryFiles) {
                    firstResult++;
                    continue;
                }
            }
            logger.info("mongo garbage collect destroy file fileId: " + fileID);
            mongoService.destroyFile(fileID);
        }
    }

    private static boolean isHistoryFileInFolder(String fileID, MongoFileService mongoService) {
        List<LogicFileInfo> history = mongoService.queryFileHistory(fileID);
        if (false == CollectionUtils.isEmpty(history)) {
            for (LogicFileInfo fileInfo : history) {
                if (mongoService.isFileInFolder(fileInfo.getUuid())) {
                    return true;
                }
            }
        }
        return false;
    }

}
