-- Alter table 
alter table WF_DEF_OPINION
  storage
  (
    next 8
  )
;
-- Add/modify columns 
alter table WF_DEF_OPINION add flow_def_id varchar2(255);
alter table WF_DEF_OPINION add task_id varchar2(255);
-- Add comments to the columns 
comment on column WF_DEF_OPINION.uuid
  is 'UUID，系统字段';
comment on column WF_DEF_OPINION.create_time
  is '创建时间';
comment on column WF_DEF_OPINION.creator
  is '创建人';
comment on column WF_DEF_OPINION.modifier
  is '修改人';
comment on column WF_DEF_OPINION.modify_time
  is '修改时间';
comment on column WF_DEF_OPINION.rec_ver
  is '版本号';
comment on column WF_DEF_OPINION.content
  is '意见内容';
comment on column WF_DEF_OPINION.code
  is '编号';
comment on column WF_DEF_OPINION.opinion_category_uuid
  is '意见分类UUID';
comment on column WF_DEF_OPINION.flow_def_id
  is '流程定义ID';
comment on column WF_DEF_OPINION.task_id
  is '环节ID';
