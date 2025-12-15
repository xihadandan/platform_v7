-- Add/modify columns 
alter table WF_FLOW_DEFINITION add remark VARCHAR2(4000);
-- Add comments to the columns 
comment on column WF_FLOW_DEFINITION.remark
  is '流程备注';
