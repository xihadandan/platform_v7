-- Add/modify columns
alter table wf_flow_instance add(
    view_main_flow_json VARCHAR2(255)
);

comment on column wf_flow_instance.view_main_flow_json is '查看主流程配置';



