/*
 * @(#)6/20/25 V1.0
 *
 * Copyright 2025 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.file.index.rebuild;

import com.google.common.collect.Maps;
import com.wellsoft.pt.dms.entity.DmsFileEntity;
import com.wellsoft.pt.dms.entity.DmsFolderEntity;
import com.wellsoft.pt.dms.file.service.DmsFileDoucmentIndexService;
import com.wellsoft.pt.dms.service.DmsFileService;
import com.wellsoft.pt.dms.service.DmsFolderService;
import com.wellsoft.pt.fulltext.support.FulltextRebuildIndexTask;
import com.wellsoft.pt.fulltext.support.FulltextSetting;
import com.wellsoft.pt.security.access.RequestSystemContextPathResolver;
import com.wellsoft.pt.security.core.userdetails.UserDetails;
import com.wellsoft.pt.security.util.IgnoreLoginUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Description:
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 6/20/25.1	    zhulh		6/20/25		    Create
 * </pre>
 * @date 6/20/25
 */
@Service
public class FulltextDmsFileRebuildIndexTaskImpl implements FulltextRebuildIndexTask {
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private DmsFileDoucmentIndexService dmsFileDoucmentIndexService;

    @Autowired
    private DmsFileService dmsFileService;

    @Autowired
    private DmsFolderService dmsFolderService;

    @Override
    public long indexCount(FulltextSetting.RebuildRule rebuildRule, FulltextSetting fulltextSetting) {
        String system = fulltextSetting.getSystem();
        return dmsFileDoucmentIndexService.countByFieldEq("system", system);
    }

    @Override
    public void rebuildIndex(FulltextSetting.RebuildRule rebuildRule, FulltextSetting fulltextSetting) {
        String system = fulltextSetting.getSystem();
        dmsFileDoucmentIndexService.deleteBySystem(system);
        List<DmsFolderEntity> libraries = dmsFolderService.listLibraryBySystem(system);
        Map<String, UserDetails> userDetailsMap = Maps.newHashMap();
        for (DmsFolderEntity library : libraries) {
            if (!dmsFileDoucmentIndexService.isEnableFulltextIndex(library.getUuid())) {
                continue;
            }
            List<String> folderUuids = dmsFolderService.listFolderUuidByLibraryUuid(library.getUuid());
            folderUuids.forEach(folderUuid -> rebuildIndex(folderUuid, rebuildRule, fulltextSetting, userDetailsMap));
        }
    }

    @Override
    public void buildIncrementIndex(Date fromTime, FulltextSetting fulltextSetting) {
        String system = fulltextSetting.getSystem();
        dmsFileDoucmentIndexService.deleteBySystem(system);
        List<DmsFolderEntity> libraries = dmsFolderService.listLibraryBySystem(system);
        Map<String, UserDetails> userDetailsMap = Maps.newHashMap();
        for (DmsFolderEntity library : libraries) {
            if (!dmsFileDoucmentIndexService.isEnableFulltextIndex(library.getUuid())) {
                continue;
            }
            List<String> folderUuids = dmsFolderService.listFolderUuidByLibraryUuid(library.getUuid());
            folderUuids.forEach(folderUuid -> buildIncrementIndex(folderUuid, fromTime, fulltextSetting, userDetailsMap));
        }
    }

    private void buildIncrementIndex(String folderUuid, Date fromTime, FulltextSetting fulltextSetting, Map<String, UserDetails> userDetailsMap) {
        List<DmsFileEntity> dmsFileEntities = dmsFileService.listByFolderUuidAndGTEModifyTime(folderUuid, fromTime);
        dmsFileEntities.forEach(dmsFileEntity -> {
            try {
                RequestSystemContextPathResolver.setSystem(fulltextSetting.getSystem());
                UserDetails userDetails = userDetailsMap.get(dmsFileEntity.getModifier());
                if (userDetails == null) {
                    IgnoreLoginUtils.login(fulltextSetting.getTenant(), dmsFileEntity.getModifier());
                    userDetails = IgnoreLoginUtils.getUserDetails();
                    userDetailsMap.put(dmsFileEntity.getModifier(), userDetails);
                } else {
                    IgnoreLoginUtils.login(userDetails);
                }
                dmsFileDoucmentIndexService.index(dmsFileEntity, false);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            } finally {
                IgnoreLoginUtils.logout();
                RequestSystemContextPathResolver.clear();
            }
        });
    }

    private void rebuildIndex(String folderUuid, FulltextSetting.RebuildRule rebuildRule, FulltextSetting fulltextSetting, Map<String, UserDetails> userDetailsMap) {
        List<DmsFileEntity> dmsFileEntities = dmsFileService.listByFolderUuid(folderUuid);
        dmsFileEntities.forEach(dmsFileEntity -> {
            try {
                UserDetails userDetails = userDetailsMap.get(dmsFileEntity.getModifier());
                if (userDetails == null) {
                    IgnoreLoginUtils.login(fulltextSetting.getTenant(), dmsFileEntity.getModifier());
                    userDetails = IgnoreLoginUtils.getUserDetails();
                    userDetailsMap.put(dmsFileEntity.getModifier(), userDetails);
                } else {
                    IgnoreLoginUtils.login(userDetails);
                }
                dmsFileDoucmentIndexService.index(dmsFileEntity, false);
            } catch (Exception e) {
                logger.error("文件索引重建失败，文件UUID：" + dmsFileEntity.getUuid(), e);
            } finally {
                IgnoreLoginUtils.logout();
            }
        });
    }

    @Override
    public int getOrder() {
        return 20;
    }
}
