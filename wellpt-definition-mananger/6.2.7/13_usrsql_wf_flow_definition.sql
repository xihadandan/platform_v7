alter table WF_FLOW_DEFINITION add delete_time timestamp;
alter table WF_FLOW_DEFINITION add delete_status number(10);
comment on column WF_FLOW_DEFINITION.delete_time is
'删除时间';
comment on column WF_FLOW_DEFINITION.delete_status is
'删除状态0正常，1已逻辑删除，2已逻辑删除且不可彻底删除';

-- Create table
create table WF_FLOW_DEF_CLEANUP_CONFIG
(
  uuid                     VARCHAR2(255 CHAR) not null,
  create_time              TIMESTAMP(6),
  creator                  VARCHAR2(255 CHAR),
  modifier                 VARCHAR2(255 CHAR),
  modify_time              TIMESTAMP(6),
  rec_ver                  NUMBER(10),
  system_unit_id           VARCHAR2(20 CHAR),
  enabled                  NUMBER(1),
  retention_days           NUMBER(10)
);
-- Create/Recreate primary, unique and foreign key constraints 
alter table wf_flow_def_cleanup_config
  add constraint wf_flow_def_cleanup_config primary key (UUID);
-- Add comments to the table 
comment on table WF_FLOW_DEF_CLEANUP_CONFIG
  is '流程定义定时清理配置';
-- Add comments to the columns 
comment on column WF_FLOW_DEF_CLEANUP_CONFIG.uuid
  is 'UUID，系统字段';
comment on column WF_FLOW_DEF_CLEANUP_CONFIG.create_time
  is '创建时间';
comment on column WF_FLOW_DEF_CLEANUP_CONFIG.creator
  is '创建人';
comment on column WF_FLOW_DEF_CLEANUP_CONFIG.modifier
  is '修改人';
comment on column WF_FLOW_DEF_CLEANUP_CONFIG.modify_time
  is '修改时间';
comment on column WF_FLOW_DEF_CLEANUP_CONFIG.rec_ver
  is '版本号';
comment on column WF_FLOW_DEF_CLEANUP_CONFIG.system_unit_id
  is '系统单位ID';
comment on column WF_FLOW_DEF_CLEANUP_CONFIG.enabled
  is '是否启用';
comment on column WF_FLOW_DEF_CLEANUP_CONFIG.retention_days
  is '保留天数';
  
-- Create table
create table WF_FLOW_DEFINITION_DELETE_LOG
(
  uuid                     VARCHAR2(255 CHAR) not null,
  create_time              TIMESTAMP(6),
  creator                  VARCHAR2(255 CHAR),
  modifier                 VARCHAR2(255 CHAR),
  modify_time              TIMESTAMP(6),
  rec_ver                  NUMBER(10),
  system_unit_id           VARCHAR2(20 CHAR),
  flow_def_uuid            VARCHAR2(50 CHAR),
  flow_def_name            VARCHAR2(255 CHAR),
  flow_def_id              VARCHAR2(255 CHAR),
  flow_def_version         NUMBER(4, 1),
  category_uuid            VARCHAR2(50 CHAR),
  category_name            VARCHAR2(255 CHAR),
  form_uuid                VARCHAR2(50 CHAR),
  form_name                VARCHAR2(255 CHAR),
  delete_time              TIMESTAMP(6),
  delete_type              NUMBER(10)
);
-- Create/Recreate primary, unique and foreign key constraints 
alter table wf_flow_definition_delete_log
  add constraint wf_flow_definition_delete_log primary key (UUID);
-- Add comments to the table 
comment on table WF_FLOW_DEFINITION_DELETE_LOG
  is '流程定义删除日志';
-- Add comments to the columns 
comment on column WF_FLOW_DEFINITION_DELETE_LOG.uuid
  is 'UUID，系统字段';
comment on column WF_FLOW_DEFINITION_DELETE_LOG.create_time
  is '创建时间';
comment on column WF_FLOW_DEFINITION_DELETE_LOG.creator
  is '创建人';
comment on column WF_FLOW_DEFINITION_DELETE_LOG.modifier
  is '修改人';
comment on column WF_FLOW_DEFINITION_DELETE_LOG.modify_time
  is '修改时间';
comment on column WF_FLOW_DEFINITION_DELETE_LOG.rec_ver
  is '版本号';
comment on column WF_FLOW_DEFINITION_DELETE_LOG.system_unit_id
  is '系统单位ID';
comment on column WF_FLOW_DEFINITION_DELETE_LOG.flow_def_uuid
  is '流程定义UUID';
comment on column WF_FLOW_DEFINITION_DELETE_LOG.flow_def_name
  is '流程定义名称';
comment on column WF_FLOW_DEFINITION_DELETE_LOG.flow_def_id
  is '流程定义ID';
comment on column WF_FLOW_DEFINITION_DELETE_LOG.flow_def_version
  is '流程版本';
comment on column WF_FLOW_DEFINITION_DELETE_LOG.category_uuid
  is '流程分类UUID';
comment on column WF_FLOW_DEFINITION_DELETE_LOG.category_name
  is '流程分类名称';
comment on column WF_FLOW_DEFINITION_DELETE_LOG.form_uuid
  is '对应表单UUID';
comment on column WF_FLOW_DEFINITION_DELETE_LOG.form_name
  is '对应表单名称';
comment on column WF_FLOW_DEFINITION_DELETE_LOG.delete_time
  is '删除时间';
comment on column WF_FLOW_DEFINITION_DELETE_LOG.delete_type
  is '删除类型1逻辑删除、2物理删除';
 