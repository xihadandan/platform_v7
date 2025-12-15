package com.wellsoft.pt.workflow.cleanup;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.pt.basicdata.cleanup.AbstractDataCleanupDataCleanupProvider;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

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
public class FlowDefinitionCleanupProvider extends AbstractDataCleanupDataCleanupProvider {
    @Override
    public String getType() {
        return "flowDefinition";
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public ExpectCleanupResult cleanup(Params params) {
        String appId = params.optString("appId");
        Map<String, Object> queryMap = Maps.newHashMap();
        if (StringUtils.isNotBlank(appId)) {
            // 删除指定模块下的
            queryMap.put("appId", appId);

        }
        List<QueryItem> items = this.nativeDao.query("select p.uuid as uuid ,p.flow_schema_uuid as uuid2 ,p.module_id as module from wf_flow_definition p where   "
                        + (queryMap.containsKey("appId") ? " p.module_id = :appId or p.module_id is null " : " p.module_id is null or ( p.module_id not like 'pt-%' and p.module_id not like 'pt_%' )  ")
                , queryMap, QueryItem.class);
        int count = 0, moduleNullCount = 0;
        if (CollectionUtils.isNotEmpty(items)) {
            List<String> flowDefUuids = Lists.newArrayList();
            Params p = new Params();
            for (QueryItem item : items) {
                flowDefUuids.add(item.getString("uuid"));
                if (StringUtils.isBlank(item.getString("module"))) {
                    moduleNullCount++;
                }
                if (flowDefUuids.size() == 200) {
                    p.put("flowDefUuids", flowDefUuids);
                    this.cleanupByTypeParams("flowInstanceData", p);
                    count += this.nativeDao.batchExecute("delete from wf_flow_definition where uuid in (:flowDefUuids)", p);
                    flowDefUuids.clear();
                }
            }
            if (!flowDefUuids.isEmpty()) {
                p.put("flowDefUuids", flowDefUuids);
                this.cleanupByTypeParams("flowInstanceData", p);
                count += this.nativeDao.batchExecute("delete from wf_flow_definition where uuid in (:flowDefUuids)", p);
            }
        }

        if (StringUtils.isNotBlank(appId)) {
            // 顺便清理掉无归属模块的流程，但是不计入数据
            count -= moduleNullCount;

            // 补充删除：不存在流程定义的流程实例数据，该部分数据量不统计
            nativeDao.batchExecute("delete from repo_file r where exists ( select 1 from repo_file_in_folder f, wf_flow_instance inst where f.file_uuid=r.uuid and  f.folder_uuid=inst.uuid and not exists ( select 1 from wf_flow_definition d where d.uuid=inst.flow_def_uuid)  )", null);
            nativeDao.batchExecute("delete from repo_file_in_folder r where exists ( select 1 from  wf_flow_instance inst where r.folder_uuid = inst.uuid and not exists ( select 1 from wf_flow_definition d where d.uuid=inst.flow_def_uuid) )", null);
            nativeDao.batchExecute("delete from wf_flow_instance w where not exists ( select 1 from wf_flow_definition d where d.uuid=w.flow_def_uuid ) ", null);
            nativeDao.batchExecute("delete from wf_task_instance a where not exists (select 1 from wf_flow_instance inst  where  inst.uuid= a.flow_inst_uuid )", null);
            nativeDao.batchExecute("delete from wf_task_instance_topping a where not exists (select 1 from wf_task_instance inst  where  inst.uuid= a.task_inst_uuid )", null);
            nativeDao.batchExecute("delete from wf_task_identity a where not exists (select 1 from wf_task_instance inst  where  inst.uuid= a.task_inst_uuid )", null);

            List<String> flowInstRelas = Lists.newArrayList("wf_flow_instance_param", "wf_task_instance", "wf_task_activity", "wf_task_branch",
                    "wf_task_delegation", "wf_task_form_attachment", "wf_task_form_attachment_log", "wf_task_form_opinion", "wf_task_form_opinion_log",
                    "wf_task_info_distribution", "wf_task_instance_todo_user", "wf_task_operation", "wf_task_sub_flow", "wf_task_sub_flow_dispatch",
                    "wf_task_timer", "wf_task_timer_log"
            );
            for (String t : flowInstRelas) {
                nativeDao.batchExecute("delete from " + t + " a where not exists (select 1 from wf_flow_instance inst where inst.uuid= a.flow_inst_uuid  )", null);
            }
            nativeDao.batchExecute("delete from wf_task_sub_flow_relation a where not exists (select 1 from wf_flow_instance inst  where  inst.uuid= a.new_flow_inst_uuid )", null);
            nativeDao.batchExecute("delete from wf_task_timer_user a where not exists (select 1 from wf_task_timer inst  where  inst.uuid= a.task_timer_uuid )", null);
        }


        // 删除流程产生的数据权限
        List<String> aclClass = Lists.newArrayList("com.wellsoft.pt.bpm.engine.entity.TaskInstance"
                , "com.wellsoft.pt.bpm.engine.entity.FlowDefinition", "com.wellsoft.pt.workflow.entity.FlowOpinionCategory",
                "com.wellsoft.pt.bpm.engine.entity.FlowInstance");
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


        this.nativeDao.batchExecute("delete from wf_flow_schema s where not exists (select 1 from wf_flow_definition d where s.uuid = d.flow_schema_uuid) ", null);
        this.nativeDao.batchExecute("delete from wf_flow_schema_log l where not exists ( select 1 from wf_flow_schema s where s.uuid=l.parent_flow_schemauuid ) ", null);

        return ExpectCleanupResult.total(count);
    }

    @Override
    public ExpectCleanupResult expectCleanupRows(Params params) {
        String appId = params.optString("appId");
        Map<String, Object> queryMap = Maps.newHashMap();
        if (StringUtils.isNotBlank(appId)) {
            // 删除指定模块下的
            queryMap.put("appId", appId);
        }
        List<QueryItem> items = this.nativeDao.query("select count(1) as total from wf_flow_definition p where  "
                        + (queryMap.containsKey("appId") ? " p.module_id = :appId" : "  p.module_id is null or ( p.module_id not like 'pt-%' and p.module_id not like 'pt_%' ) ")
                , queryMap, QueryItem.class);
        long total = items.get(0).getLong("total");
        // TODO: 是否包括实例数量??
//        items = nativeDao.query("select count(1) as total from wf_flow_instance w where" +
//                        " exists ( select 1 from wf_flow_definition p where w.flow_def_uuid = p.uuid and " +
//                        (queryMap.containsKey("appId") ? " p.module_id = :appId" : " ( p.module not like 'pt-%' and p.module not like 'pt_%' ) )"),
//                queryMap, QueryItem.class);
//        total += items.get(0).getLong("total");

        return ExpectCleanupResult.total(total);
    }
}
