package com.wellsoft.pt.app.cleanup;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.pt.app.service.AppModuleService;
import com.wellsoft.pt.basicdata.cleanup.AbstractDataCleanupDataCleanupProvider;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2024年04月23日   chenq	 Create
 * </pre>
 */
@Service
public class AppModuleRuntimeDataCleanupProvider extends AbstractDataCleanupDataCleanupProvider {

    @Autowired
    AppModuleService appModuleService;

    @Override
    public String getType() {
        return "appModuleRuntimeData";
    }

    @Override
    @Transactional(readOnly = true)
    public ExpectCleanupResult cleanup(Params params) {
        String moduleId = params.optString("id");
        List<String> clearTypes = (List<String>) params.get("clearTypes");
        if (StringUtils.isBlank(moduleId)) {
            return new ExpectCleanupResult(0L);
        }
        Map<String, Object> sqlParams = Maps.newHashMap();
        // 表单数据
        sqlParams.put("moduleId", moduleId);
        List<QueryItem> tableItems = appModuleService.listQueryItemBySQL("select table_name , uuid  from dyform_form_definition where module_id  = :moduleId  ", sqlParams, null);
        Set<String> tableNames = Sets.newHashSet();
        Set<String> formUuids = Sets.newHashSet();
        if (CollectionUtils.isNotEmpty(tableItems)) {
            for (QueryItem item : tableItems) {
                tableNames.add(item.getString("tableName"));
                formUuids.add(item.getString("uuid"));
            }

            // 删除各个表单的数据量
            long total = 0;
            if (CollectionUtils.isNotEmpty(tableNames)) {
                if (clearTypes.contains("dyformData") || clearTypes.contains("dyformFixedData")) {
                    for (String t : tableNames) {
                        Params tempParams = new Params();
                        tempParams.putAll(params);
                        tempParams.put("table", t);
                        total += this.cleanupByTypeParams("dyformData", tempParams).getTotal();
                    }
                }


            }

            if (clearTypes.contains("flowInstanceData")) {
                List<QueryItem> flowUuidItems = appModuleService.listQueryItemBySQL("select distinct uuid as uuid from wf_flow_definition  where module_id = :moduleId  ", sqlParams, null);
                for (QueryItem i : flowUuidItems) {
                    Params tempParams = new Params();
                    tempParams.putAll(params);
                    tempParams.put("flowDefUuid", i.getString("uuid"));
                    total += this.cleanupByTypeParams("flowInstanceData", tempParams).getTotal();
                }

            }
            return new ExpectCleanupResult(total);
        }


        return null;
    }

    @Override
    public ExpectCleanupResult expectCleanupRows(Params params) {
        String moduleId = params.optString("id");
        List<String> clearTypes = (List<String>) params.get("clearTypes");
        if (StringUtils.isBlank(moduleId)) {
            return new ExpectCleanupResult(0L);
        }
        Map<String, Object> sqlParams = Maps.newHashMap();
        StringBuilder sql = new StringBuilder("");
        // 表单数据
        sqlParams.put("moduleId", moduleId);
        List<QueryItem> tableItems = appModuleService.listQueryItemBySQL("select table_name , uuid  from dyform_form_definition where module_id  = :moduleId  ", sqlParams, null);
        Set<String> tableNames = Sets.newHashSet();
        Set<String> formUuids = Sets.newHashSet();
        if (CollectionUtils.isNotEmpty(tableItems)) {
            for (QueryItem item : tableItems) {
                tableNames.add(item.getString("tableName"));
                formUuids.add(item.getString("uuid"));
            }

            // 统计各个表单的数据量
            long total = 0;
            if (CollectionUtils.isNotEmpty(tableNames)) {
                if (clearTypes.contains("dyformData") || clearTypes.contains("dyformFixedData")) {
                    for (String t : tableNames) {
                        Params tempParams = new Params();
                        tempParams.putAll(params);
                        tempParams.put("table", t);
                        total += this.expectCleanupRowsByTypeParams("dyformData", tempParams).getTotal();
                    }
                }

            }

            if (clearTypes.contains("flowInstanceData")) {
                List<QueryItem> flowUuidItems = appModuleService.listQueryItemBySQL("select distinct uuid as uuid from wf_flow_definition  where module_id = :moduleId  ", sqlParams, null);
                for (QueryItem i : flowUuidItems) {
                    Params tempParams = new Params();
                    tempParams.put("flowDefUuid", i.getString("uuid"));
                    total += this.expectCleanupRowsByTypeParams("flowInstanceData", tempParams).getTotal();
                }

            }
            return new ExpectCleanupResult(total);
        }


        return null;
    }


}
