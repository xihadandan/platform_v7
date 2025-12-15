package com.wellsoft.pt.dyform.cleanup;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.pt.basicdata.cleanup.AbstractDataCleanupDataCleanupProvider;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
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
 * 2024年06月04日   chenq	 Create
 * </pre>
 */
@Service
public class DyformDefinitionCleanupProvider extends AbstractDataCleanupDataCleanupProvider {
    @Override
    public String getType() {
        return "formDefinition";
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public ExpectCleanupResult cleanup(Params params) {
        String appId = params.optString("appId");
        Map<String, Object> queryMap = Maps.newHashMap();
        Set<String> tables = Sets.newHashSet();
        int count = 0;
        if (StringUtils.isNotBlank(appId)) {
            // 删除指定模块下的表单: 清理时候顺便把不存在模块的表单一起清理掉（但不计入清理总数）
            queryMap.put("appId", appId);
            nativeDao.batchExecute("delete from dyform_form_definition_log a where exists ( select 1 from dyform_form_definition d where ( d.module_id=:appId  or not exists ( select 1 from app_module a where a.id=d.module_id ) )   and d.uuid=a.form_uuid )", queryMap);
            nativeDao.batchExecute("delete from app_function a where exists (select 1 from dyform_form_resource r , " +
                    " dyform_form_definition d where ( d.module_id=:appId  or not exists ( select 1 from app_module a where a.id=d.module_id ) )  and d.uuid=r.form_uuid and  r.app_function_uuid = a.uuid ) ", queryMap);
            nativeDao.batchExecute("delete from dyform_form_resource a where exists ( select 1 from dyform_form_definition d where ( d.module_id=:appId  or not exists ( select 1 from app_module a where a.id=d.module_id ) )  and d.uuid=a.form_uuid )", queryMap);
            nativeDao.batchExecute("delete from app_widget_definition a " +
                    "where exists ( select 1 from dyform_form_definition d where a.app_page_uuid = d.uuid and ( d.module_id=:appId  or not exists ( select 1 from app_module a where a.id=d.module_id ) ) )", queryMap);
            nativeDao.batchExecute("delete from app_data_def_ref_resource a " +
                    "where exists ( select 1 from dyform_form_definition d where a.data_def_uuid = d.uuid and ( d.module_id=:appId  or not exists ( select 1 from app_module a where a.id=d.module_id ) )  )", queryMap);
            List<QueryItem> tableNames = nativeDao.query("select d.table_name as t from dyform_form_definition d where ( d.module_id=:appId  or not exists ( select 1 from app_module a where a.id=d.module_id ) )  ", queryMap, QueryItem.class);
            Set<String> dmIds = Sets.newHashSet();
            for (QueryItem i : tableNames) {
                if (StringUtils.isNotBlank(i.getString("t"))) {
                    tables.add(i.getString("t").toUpperCase());
                    tables.add(i.getString("t").toUpperCase() + "_RL");
                    tables.add(i.getString("t").toUpperCase() + "_DL");
                    tables.add(i.getString("t").toUpperCase() + "_VN");
                    dmIds.add(i.getString("t").substring(3).toUpperCase() + "_RL");
                    dmIds.add(i.getString("t").substring(3).toUpperCase() + "_DL");
                    dmIds.add(i.getString("t").substring(3).toUpperCase() + "_VN");
                }
            }
            queryMap.put("dataModelIds", dmIds);
            if (CollectionUtils.isNotEmpty(dmIds)) {
                nativeDao.batchExecute("delete from data_model where id in (:dataModelIds) ", queryMap);
                nativeDao.batchExecute("delete from data_model_detail where id in (:dataModelIds)  ", queryMap);
            }


            count = nativeDao.batchExecute("delete from dyform_form_definition where module_id=:appId", queryMap);
            nativeDao.batchExecute("delete from dyform_form_definition d where not exists ( select 1 from app_module a where a.id=d.module_id )", queryMap);

        } else {

            nativeDao.batchExecute("delete from dyform_category d where not exists ( select 1 from dyform_form_definition f where ( f.module_id like 'pt-%' and f.module_id like 'pt_%' ) and f.category_uuid=d.uuid )", queryMap);
            nativeDao.batchExecute("delete from dyform_file_list_button_config", queryMap);
            nativeDao.batchExecute("delete from dyform_file_list_source_config", queryMap);
            nativeDao.batchExecute("delete from dyform_form_definition_log", queryMap);
            nativeDao.batchExecute("delete from app_function a where exists (select 1 from dyform_form_resource r where r.app_function_uuid = a.uuid and exists " +
                    " (select 1 from dyform_form_definition f where f.uuid=r.form_uuid and f.module_id not like 'pt-%' and f.module_id not like 'pt_%'  ) ) ", queryMap);
            nativeDao.batchExecute("delete from dyform_form_resource r where" +
                    " exists  (select 1 from dyform_form_definition f where f.uuid=r.form_uuid and f.module_id not like 'pt-%' and f.module_id not like 'pt_%'  ) ", queryMap);
            nativeDao.batchExecute("delete from app_widget_definition a " +
                    "where exists ( select 1 from dyform_form_definition d where a.app_page_uuid = d.uuid and  d.module_id not like 'pt-%' and d.module_id not like 'pt_%' )", queryMap);
            nativeDao.batchExecute("delete from app_data_def_ref_resource a " +
                    "where exists ( select 1 from dyform_form_definition d where a.data_def_uuid = d.uuid and  d.module_id not like 'pt-%' and d.module_id not like 'pt_%' )", queryMap);

            List<QueryItem> allUfTables = nativeDao.query("select d.table_name as t from dyform_form_definition d where d.module_id not like 'pt-%' and d.module_id not like 'pt_%' ", null, QueryItem.class);
            List<String> dyformTables = Lists.newArrayList();
            for (QueryItem i : allUfTables) {
                if (StringUtils.isNotBlank(i.getString("t"))) {
                    tables.add(i.getString("t").toUpperCase());
                    dyformTables.add(i.getString("t").toUpperCase());
                    tables.add(i.getString("t").toUpperCase() + "_RL");
                    tables.add(i.getString("t").toUpperCase() + "_DL");
                    tables.add(i.getString("t").toUpperCase() + "_VN");
                }
            }

            count = nativeDao.batchExecute("delete from dyform_form_definition p where p.module_id not like 'pt-%' and p.module_id not like 'pt_%' ", queryMap);
            List<String> clearTypes = (List<String>) params.get("clearTypes");
            if (clearTypes != null && clearTypes.contains("dropDataModel")) {
                // 删除数据模型视图
                nativeDao.batchExecute("delete from data_model p where p.module not like 'pt-%' and p.module not like 'pt_%' ", null);
                nativeDao.batchExecute("delete from data_model_detail p where not exists ( select 1 from data_model m where m.uuid = p.data_model_uuid ) ", null);

//                List<QueryItem> dmItems = nativeDao.query("select id from data_model where type = 0 ", null, QueryItem.class);
//                if (CollectionUtils.isNotEmpty(dmItems)) {
//                    for (QueryItem item : dmItems) {
//                        Map<String, Object> q = Maps.newHashMap();
//                        q.put("id", item.getString("id"));
//                        q.put("tableName", "UF_" + item.getString("id").toUpperCase());
//                        // 删除未被引用的数据模型
//                        nativeDao.batchExecute("delete from data_model_detail d where d.id=:id  and not exists (" +
//                                " select 1 from dyform_form_definition f where f.form_type='V' and f.table_name =:tableName" +
//                                ") ", q);
//                        int i = nativeDao.batchExecute("delete from data_model d where d.id=:id and d.type=0 and not exists (" +
//                                " select 1 from dyform_form_definition f where f.form_type='V' and f.table_name =:tableName" +
//                                ") ", q);
//                        if (i > 0) {
//                            q.put("id", Lists.newArrayList(item.getString("id") + "_VN",
//                                    item.getString("id") + "_RL", item.getString("id") + "_DL"));
//                            nativeDao.batchExecute("delete from data_model d where d.type=1 and id in (:id) ", q);
//                            nativeDao.batchExecute("delete from data_model_detail d where id in (:id) ", q);
//                        } else {
//                            // 不存在数据模型，或者数据模型被使用
//                        }
//
//                    }
//                }
            }
            if (CollectionUtils.isNotEmpty(dyformTables)) {
                queryMap.put("table", dyformTables);
                // 删除表单关联使用的流水号数据:
                nativeDao.batchExecute("delete from cd_serial_number_relation where upper(object_name) in (:table) ", queryMap);
                nativeDao.batchExecute("delete from cd_serial_number_record d where not exists ( select 1 from cd_serial_number_relation r where r.uuid=d.relation_uuid)", queryMap);
                nativeDao.batchExecute("delete from cd_serial_number_old_data where upper(object_name) in (:table) ", queryMap);
                nativeDao.batchExecute("delete from cd_serial_number_old_def where  upper(table_name) in (:table) ", queryMap);
            }


        }


//        queryTableNamesLike
        Map<String, Object> names = Maps.newHashMap();
        names.put("tableName", "UF_%");
        List<QueryItem> items = nativeDao.namedQuery("queryTableNamesLike", names, QueryItem.class);
        // 删除不存在定义的表
        List<String> dropTables = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(items)) {
            for (QueryItem i : items) {
                String tableName = i.getString("tableName").toUpperCase();
                Map<String, Object> queryParams = Maps.newHashMap();
                queryParams.put("tableName", tableName);
                queryParams.put("id", tableName.substring(3));
                List<QueryItem> result = nativeDao.query("select 1 from dyform_form_definition d where upper(d.table_name) =:tableName", queryParams, QueryItem.class);
                if (CollectionUtils.isNotEmpty(result)) {
                    continue;
                } else {
                    // 判断是否有数据模型
                    result = nativeDao.query("select 1 from data_model d where id =:id", queryParams, QueryItem.class);
                    if (CollectionUtils.isEmpty(result)) {
                        // 删除表
                        dropTables.add(tableName);
                    }
                }

            }
        }
        tables.addAll(dropTables);
        // 删除表
        if (!tables.isEmpty()) {
            for (String table : tables) {
                try {
                    nativeDao.batchExecute("drop table " + table, null);
                } catch (Exception e) {
//                    logger.warn("drop table 错误", e);
                }
            }
        }

        return ExpectCleanupResult.total(count);
    }

    @Override
    public ExpectCleanupResult expectCleanupRows(Params params) {
        String appId = params.optString("appId");
        Map<String, Object> queryMap = Maps.newHashMap();
        if (StringUtils.isNotBlank(appId)) {
            // 删除指定模块下的表单
            queryMap.put("appId", appId);
        }
        int count = 0;
        List<QueryItem> items = this.nativeDao.query("select count(1) as total from dyform_form_definition p where 1=1"
                        + (queryMap.containsKey("appId") ? " and p.module_id = :appId"
                        : " and  ( p.module_id not like 'pt-%' and p.module_id not like 'pt_%' ) ")
                , queryMap, QueryItem.class);
        count += items.get(0).getLong("total");
        List<String> clearTypes = (List<String>) params.get("clearTypes");
        if (clearTypes != null && clearTypes.contains("dropDataModel")) {
            items = this.nativeDao.query("select count(1) as total from data_model p where p.type=0 and "
                            + (queryMap.containsKey("appId") ? " p.module = :appId" :
                            " ( p.module not like 'pt-%' and p.module not like 'pt_%' )  ")
                    , queryMap, QueryItem.class);
            count += items.get(0).getLong("total");
        }
        return ExpectCleanupResult.total(count);
    }
}
