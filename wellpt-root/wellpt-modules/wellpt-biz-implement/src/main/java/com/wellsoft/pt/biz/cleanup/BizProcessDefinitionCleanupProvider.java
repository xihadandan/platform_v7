/*
 * @(#)3/4/25 V1.0
 *
 * Copyright 2025 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.biz.cleanup;

import com.wellsoft.pt.basicdata.cleanup.AbstractDataCleanupDataCleanupProvider;
import com.wellsoft.pt.biz.entity.BizItemDefinitionEntity;
import com.wellsoft.pt.biz.entity.BizProcessDefinitionEntity;
import com.wellsoft.pt.biz.entity.BizProcessInstanceEntity;
import com.wellsoft.pt.biz.entity.BizTagEntity;
import com.wellsoft.pt.biz.service.BizItemDefinitionService;
import com.wellsoft.pt.biz.service.BizProcessDefinitionService;
import com.wellsoft.pt.biz.service.BizProcessInstanceService;
import com.wellsoft.pt.biz.service.BizTagService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Description:
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 3/4/25.1	    zhulh		3/4/25		    Create
 * </pre>
 * @date 3/4/25
 */
@Service
public class BizProcessDefinitionCleanupProvider extends AbstractDataCleanupDataCleanupProvider {

    @Autowired
    private BizProcessDefinitionService processDefinitionService;

    @Autowired
    private BizProcessInstanceService processInstanceService;

    @Autowired
    private BizItemDefinitionService itemDefinitionService;

    @Autowired
    private BizTagService tagService;

    @Override
    public String getType() {
        return "pt-biz-process";
    }

    @Override
    public ExpectCleanupResult cleanup(Params params) {
        List<String> clearTypes = (List<String>) params.get("clearTypes");
        if (CollectionUtils.isEmpty(clearTypes)) {
            return ExpectCleanupResult.total(0);
        }

        long total = 0;
        for (String clearType : clearTypes) {
            switch (clearType) {
                case "bizProcessDefinition":
                    total += deleteTableData("BIZ_PROCESS_DEFINITION");
                    deleteTableData("BIZ_PROCESS_ASSEMBLE", "BIZ_PROCESS_DEFINITION_HIS", "BIZ_DEFINITION_TEMPLATE",
                            "BIZ_PROCESS_NODE_DEFINITION", "BIZ_PROCESS_ITEM_DEFINITION");
                    break;
                case "bizProcessInstance":
                    total += deleteTableData("BIZ_PROCESS_INSTANCE");
                    deleteTableData("BIZ_PROCESS_NODE_INSTANCE", "BIZ_PROCESS_ITEM_INSTANCE", "BIZ_PROCESS_ITEM_OPERATION",
                            "BIZ_PROCESS_ITEM_INST_DISPENSE", "BIZ_NEW_ITEM_RELATION", "BIZ_MILESTONE", "BIZ_MILESTONE_RESULT",
                            "BIZ_BUSINESS_INTEGRATION", "BIZ_PROCESS_ENTITY_TIMER", "BIZ_FORM_STATE_HISTORY");
                    break;
                case "bizItemDefinitin":
                    total += deleteTableData("BIZ_ITEM_DEFINITION");
                    break;
                case "bizTag":
                    total += deleteTableData("BIZ_TAG");
                    break;
            }
        }

        return ExpectCleanupResult.total(total);
    }

    private int deleteTableData(String... tableNames) {
        int deleteCount = 0;
        String deleteSqlTpl = "delete from %s where 1 = 1";
        for (String tableName : tableNames) {
            String deleteSql = String.format(deleteSqlTpl, tableName);
            deleteCount += this.nativeDao.batchExecute(deleteSql, null);
        }
        return deleteCount;
    }

    @Override
    public ExpectCleanupResult expectCleanupRows(Params params) {
        List<String> clearTypes = (List<String>) params.get("clearTypes");
        if (CollectionUtils.isEmpty(clearTypes)) {
            return ExpectCleanupResult.total(0);
        }

        long total = 0;
        for (String clearType : clearTypes) {
            switch (clearType) {
                case "bizProcessDefinition":
                    total += processDefinitionService.countByEntity(new BizProcessDefinitionEntity());
                    break;
                case "bizProcessInstance":
                    total += processInstanceService.countByEntity(new BizProcessInstanceEntity());
                    break;
                case "bizItemDefinitin":
                    total += itemDefinitionService.countByEntity(new BizItemDefinitionEntity());
                    break;
                case "bizTag":
                    total += tagService.countByEntity(new BizTagEntity());
                    break;
            }
        }
        return ExpectCleanupResult.total(total);
    }

}
