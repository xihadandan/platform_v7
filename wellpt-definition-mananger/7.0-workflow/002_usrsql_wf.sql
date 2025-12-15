alter table wf_task_sub_flow_dispatch
    add system varchar2(64);
alter table wf_task_sub_flow_dispatch
    add tenant varchar2(64);

alter table wf_flow_schema
    add definition_json clob;
comment
on column wf_flow_schema.definition_json is '流程定义JSON信息';

alter table wf_flow_schema_log
    add definition_json clob;
comment
on column wf_flow_schema_log.definition_json is '流程定义JSON信息';