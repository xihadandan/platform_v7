alter table WF_FLOW_SCHEMA_LOG add creator VARCHAR2(50 CHAR);
alter table WF_FLOW_SCHEMA_LOG add modifier VARCHAR2(50 CHAR);
alter table WF_FLOW_SCHEMA_LOG add modify_time TIMESTAMP(6);
alter table WF_FLOW_SCHEMA_LOG add rec_ver NUMBER(10);
comment on column WF_FLOW_SCHEMA_LOG.creator
  is '创建人';
comment on column WF_FLOW_SCHEMA_LOG.modifier
  is '修改人';
comment on column WF_FLOW_SCHEMA_LOG.modify_time
  is '修改时间';
comment on column WF_FLOW_SCHEMA_LOG.rec_ver
  is '版本号';