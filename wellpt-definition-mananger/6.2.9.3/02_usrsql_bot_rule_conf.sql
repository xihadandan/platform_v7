alter table BOT_RULE_CONF add auto_map_same_column number(1) default 0;
comment on column BOT_RULE_CONF.auto_map_same_column
  is '自动映射同名字段';

create table BOT_RULE_OBJ_MAPPING_IGNORE
(
  uuid                    VARCHAR2(64) primary key ,
  create_time             TIMESTAMP(6),
  creator                 VARCHAR2(255),
  modify_time             TIMESTAMP(6),
  modifier                VARCHAR2(255),
  rec_ver                 NUMBER(10) not null,
  rule_conf_uuid          VARCHAR2(64) not null,
  source_obj_id           VARCHAR2(64),
  source_obj_field        VARCHAR2(32),
   target_obj_field        VARCHAR2(32),
  target_obj_id varchar2(64),
  system_unit_id          VARCHAR2(32) 
);
