package com.wellsoft.pt.basicdata.cleanup;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wellsoft.context.jdbc.support.QueryItem;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

import static com.wellsoft.pt.app.cleanup.AppProductCleanupProvider.INNER_PRODUCT_ID;

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
public class BasicdataCleanupProvider extends AbstractDataCleanupDataCleanupProvider {

    @Override
    public String getType() {
        return "basicData";
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public ExpectCleanupResult cleanup(Params params) {
        List<String> clearTypes = (List<String>) params.get("clearTypes");
        Map<String, Object> queryMap = Maps.newHashMap();
        String appId = params.optString("appId");
        if (StringUtils.isNotBlank(appId)) {
            queryMap.put("appId", appId);
        } else {
            queryMap.put("excludes", INNER_PRODUCT_ID);
        }
        int count = 0;
        if (clearTypes != null) {

            if (clearTypes.contains("cd_data_dictionary")) {
                count += this.nativeDao.batchExecute("delete  from cd_data_dictionary p where "
                                + (StringUtils.isNotBlank(appId) ? "  p.module_id = :appId" :
                                "  ( p.module_id is null or ( p.module_id not like 'pt-%' and p.module_id not like 'pt_%' ) )")
                        , queryMap);
                this.nativeDao.batchExecute("delete  from cd_data_dictionary_item p where not exists ( select 1 from cd_data_dictionary d where d.uuid = p.data_dict_uuid )  "
                        , queryMap);
                this.nativeDao.batchExecute("delete  from cd_data_dict_attr p where not exists ( select 1 from cd_data_dictionary d where to_char(d.uuid) =  p.data_dict_uuid  ) "
                        , queryMap);

            }

            if (clearTypes.contains("msg_message_template")) {
                count += this.nativeDao.batchExecute("delete  from msg_message_template p where "
                                + (StringUtils.isNotBlank(appId) ? " p.module_id = :appId" :
                                " ( p.module_id is null or ( p.module_id not like 'pt-%' and p.module_id not like 'pt_%' ))  ")
                        , queryMap);
                if (StringUtils.isBlank(appId)) {
                    this.nativeDao.batchExecute("delete from msg_message_classify p where not exists ( select 1 from msg_message_template t where t.category=p.uuid) "
                            , queryMap);
                    this.nativeDao.batchExecute("delete from msg_message_template_ref p where not exists ( select 1 from msg_message_template t where t.uuid=p.ref_uuid) ", null);
                    List<String> ignores = Lists.newArrayList("MSG_MESSAGE_TEMPLATE", "MSG_MESSAGE_CLASSIFY", "MSG_MESSAGE_TEMPLATE_REF");
                    Map<String, Object> names = Maps.newHashMap();
                    names.put("tableName", "MSG_%");
                    List<QueryItem> items = nativeDao.namedQuery("queryTableNamesLike", names, QueryItem.class);
                    // 删除不存在定义的表
                    if (CollectionUtils.isNotEmpty(items)) {
                        for (QueryItem i : items) {
                            String tableName = i.getString("tableName").toUpperCase();
                            if (ignores.contains(tableName)) {
                                continue;
                            }
                            try {
                                this.nativeDao.batchExecute("delete from " + tableName, null);
                            } catch (Exception e) {
                            }
                        }
                    }

                }
            }

            if (clearTypes.contains("cd_print_template")) {

                this.nativeDao.batchExecute(
                        "delete from repo_file f where exists " +
                                " ( select 1 from cd_print_template p where p.file_uuid = f.uuid and "
                                + (StringUtils.isNotBlank(appId) ? " p.module_id = :appId )" :
                                " ( p.module_id is null or ( p.module_id not like 'pt-%' and p.module_id not like 'pt_%' )) ) ")
                        , queryMap);

                count += this.nativeDao.batchExecute("delete from cd_print_template p where "
                                + (StringUtils.isNotBlank(appId) ? " p.module_id = :appId" :
                                " ( p.module_id is null or ( p.module_id not like 'pt-%' and p.module_id not like 'pt_%' ))  ")
                        , queryMap);
                if (StringUtils.isBlank(appId)) {
                    this.nativeDao.batchExecute("delete from cd_print_contents p where not exists ( select 1 from cd_print_template t where t.uuid=p.template_uuid) "
                            , queryMap);
                    this.nativeDao.batchExecute("delete from cd_print_template_category p where not exists ( select 1 from cd_print_template t where t.category=p.uuid) "
                            , queryMap);
                    this.nativeDao.batchExecute("delete from cd_print_record  "
                            , queryMap);
                    this.nativeDao.batchExecute("delete from cd_print_template_ref p where not exists ( select 1 from cd_print_template t where t.uuid=p.ref_uuid) "
                            , queryMap);
                }

            }

            if (clearTypes.contains("cd_data_store_definition")) {
                count += this.nativeDao.batchExecute("delete  from cd_data_store_definition p where "
                                + (StringUtils.isNotBlank(appId) ? " p.module_id = :appId" :
                                " ( p.module_id is null or ( p.module_id not like 'pt-%' and p.module_id not like 'pt_%'  )) and p.name not like '平台管理%' ")
                        , queryMap);
            }

            if (clearTypes.contains("cd_serial_number")) {
                count += this.nativeDao.batchExecute("delete from cd_serial_number p where "
                        + (StringUtils.isNotBlank(appId) ? " p.module_id = :appId" :
                        " (p.module_id is null or ( p.module_id not like 'pt-%' and p.module_id not like 'pt_%' )) and not exists (" +
                                // 被使用的流水号不能删除
                                " select 1 from cd_serial_number_relation re ,dyform_form_definition f where re.sn_id = p.id and" +
                                " f.table_name =re.object_name" +
                                ")"), queryMap);
                count += this.nativeDao.batchExecute("delete from sn_serial_number_definition p where "
                        + (StringUtils.isNotBlank(appId) ? " p.module_id = :appId" :
                        " (p.module_id is null or ( p.module_id not like 'pt-%' and p.module_id not like 'pt_%' )) and not exists (" +
                                // 被使用的流水号不能删除
                                " select 1 from sn_serial_number_relation re ,dyform_form_definition f where re.sn_id = p.id and" +
                                " f.table_name =re.object_name" +
                                ")"), queryMap);

                this.nativeDao.batchExecute("delete from cd_serial_number_maintain m where not exists ( select 1 from cd_serial_number p where p.id =m.id)", null);
                this.nativeDao.batchExecute("delete from cd_serial_number_relation m where not exists ( select 1 from cd_serial_number p where p.id =m.sn_id)", null);
                this.nativeDao.batchExecute("delete from cd_serial_number_record d where not exists ( select 1 from cd_serial_number p ,cd_serial_number_relation m " +
                        " where p.id =m.sn_id and m.uuid = d.relation_uuid)", null);
                this.nativeDao.batchExecute("delete from cd_serial_number_old_data m where not exists ( select 1 from cd_serial_number p where p.id =m.sn_id)", null);
                this.nativeDao.batchExecute("delete from cd_serial_number_old_def m where not exists ( select 1 from dyform_form_definition p where p.table_name = m.table_name)", null);


                this.nativeDao.batchExecute("delete from sn_serial_number_maintain m where not exists ( select 1 from sn_serial_number_definition p where  p.uuid =m.serial_number_def_uuid )", null);
                this.nativeDao.batchExecute("delete from sn_serial_number_relation m where not exists ( select 1 from sn_serial_number_definition p where p.id =m.sn_id)", null);
                this.nativeDao.batchExecute("delete from sn_serial_number_record d where not exists ( select 1 from sn_serial_number_definition p ,sn_serial_number_relation m " +
                        " where p.id =m.sn_id and m.uuid = d.relation_uuid)", null);
                this.nativeDao.batchExecute("delete from sn_serial_number_category m where not exists ( select 1 from sn_serial_number_definition p where  p.category_uuid =m.uuid )", null);


            }


            // 删除流程产生的数据权限
            List<String> aclClass = Lists.newArrayList();
            if (clearTypes.contains("cd_serial_number")) {
                aclClass.add("com.wellsoft.pt.basicdata.serialnumber.entity.SerialNumberMaintain");
                aclClass.add("com.wellsoft.pt.basicdata.serialnumber.entity.SerialNumber");
            }
            if (clearTypes.contains("cd_print_template")) {
                aclClass.add("com.wellsoft.pt.basicdata.printtemplate.entity.PrintTemplate");
            }
            if (!aclClass.isEmpty()) {
                queryMap.put("classes", aclClass);
                List<QueryItem> queryItems = this.nativeDao.query("select uuid from acl_class where class in (:classes)", queryMap, QueryItem.class);
                if (CollectionUtils.isNotEmpty(queryItems)) {
                    List<String> objIdClasses = Lists.newArrayList();
                    for (QueryItem i : queryItems) {
                        objIdClasses.add(i.getString("uuid"));
                    }
                    queryMap.put("objectIdClass", objIdClasses);
                    this.nativeDao.batchExecute("delete from acl_object_identity where object_id_class in (:objectIdClass)", queryMap);
                    this.nativeDao.batchExecute("delete from acl_entry a where not exists (" +
                            "select 1 from acl_object_identity i where i.uuid = a.acl_object_identity) ", null);
                    this.nativeDao.batchExecute("delete from acl_sid a where not exists (" +
                            "select 1 from acl_entry i where i.sid = a.uuid) ", null);
                    this.nativeDao.batchExecute("delete from acl_sid_member a where not exists (" +
                            "select 1 from acl_sid i where i.uuid = a.owner_sid) ", null);
                }
            }


            //TODO: 计时服务、工作时间方案，目前没有在模块下，按全部删除处理
            if (clearTypes.contains("ts_timer_config")) {

                count += this.nativeDao.batchExecute("delete from ts_timer_config p where p.system is null or ( p.system not like 'pt_%' and p.system not like 'pt_%' and p.system not in (:excludes) )"
                        , queryMap);

                this.nativeDao.batchExecute("delete from ts_timer p where not exists ( select 1 from ts_timer_config c where c.uuid=p.config_uuid) "
                        , queryMap);
                this.nativeDao.batchExecute("delete from ts_timer_category p where p.system is null or ( p.system not like 'pt_%' and p.system not like 'pt_%' and p.system not in (:excludes) )"
                        , queryMap);
                this.nativeDao.batchExecute("delete from ts_timer_log p"
                        , queryMap);

            }
            if (clearTypes.contains("ts_work_time_plan")) {
                count += this.nativeDao.batchExecute("delete from ts_work_time_plan p where p.system is null or ( p.system not like 'pt_%' and p.system not like 'pt_%' and p.system not in (:excludes) )"
                        , queryMap);
                this.nativeDao.batchExecute("delete from ts_work_time_plan_his p"
                        , queryMap);
            }

            if (clearTypes.contains("ts_holiday")) {
                count += this.nativeDao.batchExecute("delete from ts_holiday p where p.system is null or ( p.system not like 'pt_%' and p.system not like 'pt_%' and p.system not in (:excludes) )"
                        , queryMap);
                this.nativeDao.batchExecute("delete from ts_holiday_instance p where not exists ( select 1 from ts_holiday h where h.uuid = p.holiday_uuid ) "
                        , queryMap);
                this.nativeDao.batchExecute("delete from ts_holiday_schedule p where not exists ( select 1 from ts_holiday h where h.uuid = p.holiday_uuid )"
                        , queryMap);
            }


        }

        return ExpectCleanupResult.total(count);
    }

    @Override
    public ExpectCleanupResult expectCleanupRows(Params params) {
        List<String> clearTypes = (List<String>) params.get("clearTypes");
        Map<String, Object> queryMap = Maps.newHashMap();
        String appId = params.optString("appId");
        if (StringUtils.isNotBlank(appId)) {
            queryMap.put("appId", appId);
        } else {
            queryMap.put("excludes", INNER_PRODUCT_ID);
        }

        int count = 0;
        if (clearTypes != null) {

            if (clearTypes.contains("cd_data_dictionary")) {
                List<QueryItem> items = this.nativeDao.query("select count(1) as total from cd_data_dictionary p where "
                                + (StringUtils.isNotBlank(appId) ? " p.module_id = :appId" :
                                " (p.module_id is null or ( p.module_id not like 'pt-%' and p.module_id not like 'pt_%' )) ")
                        , queryMap, QueryItem.class);
                count += items.get(0).getLong("total");
            }

            if (clearTypes.contains("msg_message_template")) {
                List<QueryItem> items = this.nativeDao.query("select count(1) as total from msg_message_template p where "
                                + (StringUtils.isNotBlank(appId) ? " p.module_id = :appId" :
                                " (p.module_id is null or ( p.module_id not like 'pt-%' and p.module_id not like 'pt_%' )) ")
                        , queryMap, QueryItem.class);
                count += items.get(0).getLong("total");
            }

            if (clearTypes.contains("cd_print_template")) {
                List<QueryItem> items = this.nativeDao.query("select count(1) as total from cd_print_template p where "
                                + (StringUtils.isNotBlank(appId) ? " p.module_id = :appId" :
                                " (p.module_id is null or ( p.module_id not like 'pt-%' and p.module_id not like 'pt_%' )) ")
                        , queryMap, QueryItem.class);
                count += items.get(0).getLong("total");
            }


            if (clearTypes.contains("cd_data_store_definition")) {
                List<QueryItem> items = this.nativeDao.query("select count(1) as total from cd_data_store_definition p where "
                                + (StringUtils.isNotBlank(appId) ? " p.module_id = :appId" :
                                " (p.module_id is null or (p.module_id not like 'pt-%' and p.module_id not like 'pt_%' )) and p.name not like '平台管理%' ")
                        , queryMap, QueryItem.class);
                count += items.get(0).getLong("total");
            }

            if (clearTypes.contains("cd_serial_number")) {
                List<QueryItem> items = this.nativeDao.query("select count(1) as total from cd_serial_number p where "
                                + (StringUtils.isNotBlank(appId) ? " p.module_id = :appId" :
                                " (p.module_id is null or ( p.module_id not like 'pt-%' and p.module_id not like 'pt_%' )) and not exists (" +
                                        // 被使用的流水号不能删除
                                        " select 1 from cd_serial_number_relation re ,dyform_form_definition f where re.sn_id = p.id and" +
                                        " f.table_name =re.object_name" +
                                        ") ")
                        , queryMap, QueryItem.class);
                count += items.get(0).getLong("total");

                items = this.nativeDao.query("select count(1) as total from SN_SERIAL_NUMBER_DEFINITION p where "
                                + (StringUtils.isNotBlank(appId) ? " p.module_id = :appId" :
                                " (p.module_id is null or ( p.module_id not like 'pt-%' and p.module_id not like 'pt_%' )) and not exists (" +
                                        // 被使用的流水号不能删除
                                        " select 1 from SN_SERIAL_NUMBER_RELATION re ,dyform_form_definition f where re.sn_id = p.id and" +
                                        " f.table_name =re.object_name" +
                                        ") ")
                        , queryMap, QueryItem.class);

                count += items.get(0).getLong("total");
            }


            //TODO: 计时服务、工作时间方案，目前没有在模块下，按全部删除处理
            if (clearTypes.contains("ts_timer_config")) {
                List<QueryItem> items = this.nativeDao.query("select count(1) as total from ts_timer_config p where p.system is null or ( p.system not like 'pt_%' and p.system not like 'pt_%' and p.system not in (:excludes) )"
                        , queryMap, QueryItem.class);
                count += items.get(0).getLong("total");
            }
            if (clearTypes.contains("ts_work_time_plan")) {
                List<QueryItem> items = this.nativeDao.query("select count(1) as total from ts_work_time_plan p where p.system is null or ( p.system not like 'pt_%' and p.system not like 'pt_%' and p.system not in (:excludes) )"
                        , queryMap, QueryItem.class);
                count += items.get(0).getLong("total");
            }
            if (clearTypes.contains("ts_holiday")) {
                List<QueryItem> items = this.nativeDao.query("select count(1) as total from ts_holiday p where p.system is null or ( p.system not like 'pt_%' and p.system not like 'pt_%' and p.system not in (:excludes) )"
                        , queryMap, QueryItem.class);
                count += items.get(0).getLong("total");
            }
        }

        return ExpectCleanupResult.total(count);
    }
}
