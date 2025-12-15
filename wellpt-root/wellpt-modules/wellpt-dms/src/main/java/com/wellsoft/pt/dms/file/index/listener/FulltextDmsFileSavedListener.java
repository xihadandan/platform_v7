/*
 * @(#)6/23/25 V1.0
 *
 * Copyright 2025 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.file.index.listener;

import com.wellsoft.pt.dms.entity.DmsFolderEntity;
import com.wellsoft.pt.dms.event.DmsFileSavedEvent;
import com.wellsoft.pt.dms.file.service.DmsFileDoucmentIndexService;
import com.wellsoft.pt.dms.service.DmsFolderService;
import com.wellsoft.pt.dyform.facade.dto.DyFormData;
import com.wellsoft.pt.fulltext.facade.service.FulltextSettingFacadeService;
import com.wellsoft.pt.fulltext.support.FulltextSetting;
import com.wellsoft.pt.jpa.event.WellptTransactionalEventListener;
import com.wellsoft.pt.security.access.RequestSystemContextPathResolver;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Description:
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 6/23/25.1	    zhulh		6/23/25		    Create
 * </pre>
 * @date 6/23/25
 */
@Component
public class FulltextDmsFileSavedListener extends WellptTransactionalEventListener<DmsFileSavedEvent> {

    @Autowired
    private FulltextSettingFacadeService fulltextSettingFacadeService;

    @Autowired
    private DmsFileDoucmentIndexService dmsFileDoucmentIndexService;

    @Autowired
    private DmsFolderService dmsFolderService;

    @Autowired(required = false)
    private RestHighLevelClient restHighLevelClient;

    @Override
    public void onApplicationEvent(DmsFileSavedEvent event) {
        if (restHighLevelClient == null) {
            return;
        }

        try {
            DmsFolderEntity dmsFolderEntity = dmsFolderService.getByFileUuid(event.getDmsFileEntity().getUuid());
            if (dmsFolderEntity == null) {
                dmsFolderEntity = dmsFolderService.get(event.getDmsFileEntity().getFolderUuid());
            }
            if (dmsFolderEntity == null) {
                return;
            }

            FulltextSetting fulltextSetting = fulltextSettingFacadeService.getSettingBySystem(dmsFolderEntity.getSystem());
            if (BooleanUtils.isNotTrue(fulltextSetting.getEnabled()) || StringUtils.equals(fulltextSetting.getUpdateMode(), FulltextSetting.UPDATE_MODE_REGULAR)) {
                return;
            }

            RequestSystemContextPathResolver.setSystem(fulltextSetting.getSystem());
            DyFormData dyFormData = (DyFormData) event.getDyFormData();
            String fileId = event.getFileID();
            if (StringUtils.isNotBlank(fileId)) {
                dmsFileDoucmentIndexService.index(event.getDmsFileEntity(), fileId);
            } else if (dyFormData != null) {
                dmsFileDoucmentIndexService.index(event.getDmsFileEntity(), dyFormData);
            } else {
                dmsFileDoucmentIndexService.index(event.getDmsFileEntity());
            }
        } catch (Exception e) {
            logger.error("FulltextDmsFileSavedListener error", e);
        } finally {
            RequestSystemContextPathResolver.clear();
        }
    }

}
