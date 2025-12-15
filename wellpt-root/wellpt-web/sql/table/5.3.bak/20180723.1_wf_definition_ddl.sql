
-- Add/modify columns 
alter table WF_FLOW_DEFINITION add apply_id VARCHAR2(255 CHAR);
-- Add comments to the columns 
comment on column WF_FLOW_DEFINITION.apply_id
  is '流程应用ID';
