package com.wellsoft.pt.workflow.cleanup;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.pt.basicdata.cleanup.AbstractDataCleanupDataCleanupProvider;
import com.wellsoft.pt.security.access.RequestSystemContextPathResolver;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
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
 * 2024年04月24日   chenq	 Create
 * </pre>
 */
@Service
public class FlowInstanceDataCleanupProvider extends AbstractDataCleanupDataCleanupProvider {
    @Override
    public String getType() {
        return "flowInstanceData";
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public ExpectCleanupResult cleanup(Params params) {
        List<String> flowDefUuids = null;
        String flowDefUuid = params.optString("flowDefUuid");
        if (params.get("flowDefUuids") != null) {
            flowDefUuids = (List<String>) params.get("flowDefUuids");
        } else if (StringUtils.isNotBlank(flowDefUuid)) {
            flowDefUuids = Lists.newArrayList(flowDefUuid);
        }

        long total = 0L;
        Map<String, Object> map = Maps.newHashMap();
        map.put("flowDefUuids", flowDefUuids);
        if (CollectionUtils.isNotEmpty(flowDefUuids)) {
            String system = StringUtils.defaultIfBlank(params.optString("system"), RequestSystemContextPathResolver.system());
            String underSystem = "";
            if (StringUtils.isNotBlank(system)) {
                underSystem = " and inst.system=:system and inst.tenant=:tenant ";
                map.put("system", system);
                map.put("tenant", SpringSecurityUtils.getCurrentTenantId());
            }

            nativeDao.batchExecute("delete from wf_task_sub_flow_relation  a where exists (select 1 from wf_flow_instance inst where inst.flow_def_uuid in (:flowDefUuids) and inst.uuid= a.new_flow_inst_uuid " + underSystem + ")", map);

            nativeDao.batchExecute("delete from wf_task_timer_user a where exists ( select 1 from wf_task_timer t, " +
                    " wf_flow_instance inst where inst.flow_def_uuid in (:flowDefUuids) and inst.uuid= t.flow_inst_uuid and t.uuid = a.task_timer_uuid " + underSystem + " )", map);

            List<String> taskInstRelas = Lists.newArrayList("wf_task_instance_topping", "wf_task_identity");
            for (String t : taskInstRelas) {
                nativeDao.batchExecute("delete from " + t + " a where exists (select 1 from wf_task_instance t, " +
                        " wf_flow_instance inst where inst.flow_def_uuid in (:flowDefUuids) and inst.uuid= t.flow_inst_uuid and t.uuid = a.task_inst_uuid " + underSystem + " )", map);
            }

            List<String> flowInstRelas = Lists.newArrayList("wf_flow_instance_param", "wf_task_instance", "wf_task_activity", "wf_task_branch",
                    "wf_task_delegation", "wf_task_form_attachment", "wf_task_form_attachment_log", "wf_task_form_opinion", "wf_task_form_opinion_log",
                    "wf_task_info_distribution", "wf_task_instance_todo_user", "wf_task_operation", "wf_task_sub_flow", "wf_task_sub_flow_dispatch",
                    "wf_task_timer", "wf_task_timer_log"
            );
            for (String t : flowInstRelas) {
                nativeDao.batchExecute("delete from " + t + " a where exists (select 1 from wf_flow_instance inst where inst.flow_def_uuid in (:flowDefUuids) and inst.uuid= a.flow_inst_uuid " + underSystem + " )", map);
            }

            // 删除最近意见
            nativeDao.batchExecute("delete from wf_def_opinion d where exists ( select 1 from wf_flow_definition inst where inst.id = d.flow_def_id and inst.uuid in (:flowDefUuids)  ) ", map);

            // 删除流程关联的附件
            nativeDao.batchExecute("delete from repo_file r where exists ( select 1 from repo_file_in_folder f, wf_flow_instance inst where f.file_uuid=r.uuid and  f.folder_uuid=inst.uuid and inst.flow_def_uuid in (:flowDefUuids) )", map);
            nativeDao.batchExecute("delete from repo_file_in_folder r where exists ( select 1 from  wf_flow_instance inst where r.folder_uuid = inst.uuid and inst.flow_def_uuid in (:flowDefUuids) )", map);


            // 补充删除：不存在流程定义的流程实例数据，该部分数据量不统计
            nativeDao.batchExecute("delete from repo_file r where exists ( select 1 from repo_file_in_folder f, wf_flow_instance inst where f.file_uuid=r.uuid and  f.folder_uuid=inst.uuid and not exists ( select 1 from wf_flow_definition d where d.uuid=inst.flow_def_uuid)  )", map);
            nativeDao.batchExecute("delete from repo_file_in_folder r where exists ( select 1 from  wf_flow_instance inst where r.folder_uuid = inst.uuid and not exists ( select 1 from wf_flow_definition d where d.uuid=inst.flow_def_uuid) )", map);
            nativeDao.batchExecute("delete from wf_flow_instance w where not exists ( select 1 from wf_flow_definition d where d.uuid=w.flow_def_uuid ) ", null);
            nativeDao.batchExecute("delete from wf_task_instance a where not exists (select 1 from wf_flow_instance inst  where  inst.uuid= a.flow_inst_uuid )", map);
            nativeDao.batchExecute("delete from wf_task_instance_topping a where not exists (select 1 from wf_task_instance inst  where  inst.uuid= a.task_inst_uuid )", map);
            nativeDao.batchExecute("delete from wf_task_identity a where not exists (select 1 from wf_task_instance inst  where  inst.uuid= a.task_inst_uuid )", map);

            for (String t : flowInstRelas) {
                nativeDao.batchExecute("delete from " + t + " a where not exists (select 1 from wf_flow_instance inst where inst.uuid= a.flow_inst_uuid  )", map);
            }
            nativeDao.batchExecute("delete from wf_task_sub_flow_relation a where not exists (select 1 from wf_flow_instance inst  where  inst.uuid= a.new_flow_inst_uuid )", map);
            nativeDao.batchExecute("delete from wf_task_timer_user a where not exists (select 1 from wf_task_timer inst  where  inst.uuid= a.task_timer_uuid )", map);


            // 删除流程产生的数据权限
            map.put("classes", "com.wellsoft.pt.bpm.engine.entity.FlowInstance");
            this.nativeDao.batchExecute("delete from acl_object_identity a where a.object_id_class = ( select uuid from acl_class where class=:classes )  " +
                    " and not exists ( select 1 from wf_flow_instance inst where inst.uuid = a.object_id_identity ) ", map);
            map.put("classes", "com.wellsoft.pt.bpm.engine.entity.TaskInstance");
            this.nativeDao.batchExecute("delete from acl_object_identity a where a.object_id_class = ( select uuid from acl_class where class=:classes )  " +
                    " and not exists ( select 1 from wf_task_instance w  where w.uuid = a.object_id_identity ) ", map);
            this.nativeDao.batchExecute("delete from acl_entry a where not exists (" +
                    "select 1 from acl_object_identity i where i.uuid = a.acl_object_identity) ", null);
            this.nativeDao.batchExecute("delete from acl_sid a where not exists (" +
                    "select 1 from acl_entry i where i.sid = a.uuid) ", null);
            this.nativeDao.batchExecute("delete from acl_sid_member a where not exists (" +
                    "select 1 from acl_sid i where i.uuid = a.owner_sid) ", null);

            total += nativeDao.batchExecute("delete from wf_flow_instance inst where inst.flow_def_uuid in (:flowDefUuids) " + underSystem, map);

        }
        return ExpectCleanupResult.total(total);
    }

    @Override
    public ExpectCleanupResult expectCleanupRows(Params params) {
        Map<String, Object> map = Maps.newHashMap();
        map.put("uuid", params.optString("flowDefUuid"));
        String system = StringUtils.defaultIfBlank(params.optString("system"), RequestSystemContextPathResolver.system());
        String underSystem = "";
        if (StringUtils.isNotBlank(system)) {
            underSystem = " and system=:system and tenant=:tenant ";
            map.put("system", system);
            map.put("tenant", SpringSecurityUtils.getCurrentTenantId());
        }

        // 只统计主表数据量
        List<QueryItem> items = nativeDao.query("select count(1) as total from wf_flow_instance where  flow_def_uuid =:uuid" + underSystem, map, QueryItem.class);
        return ExpectCleanupResult.total(items.get(0).getLong("total"));
    }
}
