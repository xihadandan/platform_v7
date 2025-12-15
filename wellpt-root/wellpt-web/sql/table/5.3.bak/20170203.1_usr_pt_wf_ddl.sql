-- Alter table 
alter table WF_DEF_OPINION_CATEGORY
  storage
  (
    next 8
  )
;
-- Add comments to the columns 
comment on column WF_DEF_OPINION_CATEGORY.uuid
  is 'UUID，系统字段';
comment on column WF_DEF_OPINION_CATEGORY.create_time
  is '创建时间';
comment on column WF_DEF_OPINION_CATEGORY.creator
  is '创建人';
comment on column WF_DEF_OPINION_CATEGORY.modifier
  is '修改人';
comment on column WF_DEF_OPINION_CATEGORY.modify_time
  is '修改时间';
comment on column WF_DEF_OPINION_CATEGORY.rec_ver
  is '版本号';
comment on column WF_DEF_OPINION_CATEGORY.code
  is '编号';
comment on column WF_DEF_OPINION_CATEGORY.name
  is '分类名称';
comment on column WF_DEF_OPINION_CATEGORY.id
  is 'ID';
