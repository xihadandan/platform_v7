alter table wf_flow_delegation_settings
    add condition_json clob;
alter table wf_flow_delegation_settings
    add allow_secondary_delegation number(1);
comment
    on column wf_flow_delegation_settings.condition_json is '委托条件JSON信息';
comment
    on column wf_flow_delegation_settings.allow_secondary_delegation is '是否允许二次委托';