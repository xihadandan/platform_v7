/*
 * @(#)6/18/25 V1.0
 *
 * Copyright 2025 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.fulltext.listener;

import com.wellsoft.pt.dyform.facade.dto.DyFormFormDefinition;
import com.wellsoft.pt.dyform.facade.service.DyFormFacade;
import com.wellsoft.pt.dyform.implement.definition.event.FormDataDeleteEvent;
import com.wellsoft.pt.fulltext.facade.service.FulltextSettingFacadeService;
import com.wellsoft.pt.fulltext.index.FormDataDocumentIndex;
import com.wellsoft.pt.fulltext.service.FulltextDocumentIndexService;
import com.wellsoft.pt.fulltext.support.FulltextSetting;
import com.wellsoft.pt.jpa.event.WellptTransactionalEventListener;
import org.apache.commons.lang.BooleanUtils;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.annotations.Document;
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
 * 6/18/25.1	    zhulh		6/18/25		    Create
 * </pre>
 * @date 6/18/25
 */
@Component
public class FulltextFormDataDeletedListener extends WellptTransactionalEventListener<FormDataDeleteEvent> {

    @Autowired(required = false)
    private RestHighLevelClient restHighLevelClient;

    @Autowired
    private DyFormFacade dyFormFacade;

    @Autowired
    private FulltextSettingFacadeService fulltextSettingFacadeService;

    @Autowired
    private FulltextDocumentIndexService fulltextDocumentIndexService;

    @Override
    public void onApplicationEvent(FormDataDeleteEvent event) {
        if (restHighLevelClient == null) {
            return;
        }

        String formUuid = event.getFromUuid();
        DyFormFormDefinition dyFormFormDefinition = dyFormFacade.getFormDefinition(formUuid);
        FulltextSetting fulltextSetting = fulltextSettingFacadeService.getSettingBySystem(dyFormFormDefinition.getSystem());
        if (BooleanUtils.isNotTrue(fulltextSetting.getEnabled())) {
            return;
        }

        String indexName = FormDataDocumentIndex.class.getAnnotation(Document.class).indexName();
        fulltextDocumentIndexService.deleteByFieldEq(indexName, "uuid", event.getDataUuid());
    }

}
