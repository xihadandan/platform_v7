create index wf_task_act_taskinstuuid_idx on wf_task_activity (task_inst_uuid);
create index wf_task_act_flowinstuuid_idx on wf_task_activity (flow_inst_uuid);

create index wf_task_ope_taskinstuuid_idx on wf_task_operation (task_inst_uuid);
create index wf_task_ope_flowinstuuid_idx on wf_task_operation (flow_inst_uuid);
