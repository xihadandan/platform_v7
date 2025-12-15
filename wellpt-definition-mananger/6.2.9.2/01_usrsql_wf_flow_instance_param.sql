alter table wf_flow_instance_param add value_tmp clob;
update wf_flow_instance_param set value_tmp = value;
commit;
alter table wf_flow_instance_param rename column value to value_bak;
alter table wf_flow_instance_param rename column value_tmp to value;