-- Add/modify columns 
alter table WF_FLOW_DEFINITION add is_mobile_show number(1) default 0;
-- Add comments to the columns 
comment on column WF_FLOW_DEFINITION.is_mobile_show
  is '是否手机端展示';
