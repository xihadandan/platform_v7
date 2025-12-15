 
create table BOT_RULE_CONF
(
  uuid                VARCHAR2(64) not null,
  create_time         TIMESTAMP(6),
  creator             VARCHAR2(255),
  modify_time         TIMESTAMP(6),
  modifier            VARCHAR2(255),
  rec_ver             NUMBER(10) not null,
  id                  VARCHAR2(64),
  rule_name           VARCHAR2(120),
  transfer_type       NUMBER(1),
  target_obj_id       VARCHAR2(64),
  target_obj_name     VARCHAR2(120),
  is_persist          NUMBER(1),
  script_before_trans CLOB,
  script_after_trans  CLOB,
  system_unit_id      VARCHAR2(32)
)
tablespace USERS
  pctfree 10
  initrans 1
  maxtrans 255
  storage
  (
    initial 64K
    next 1M
    minextents 1
    maxextents unlimited
  );
comment on table BOT_RULE_CONF
  is '单据转换规则配置';
comment on column BOT_RULE_CONF.id
  is '规则id';
comment on column BOT_RULE_CONF.rule_name
  is '规则名称';
comment on column BOT_RULE_CONF.transfer_type
  is '转换类型：0 单据转单据 1 报文转单据';
comment on column BOT_RULE_CONF.target_obj_id
  is '目标单据id';
comment on column BOT_RULE_CONF.target_obj_name
  is '目标单据名称';
comment on column BOT_RULE_CONF.is_persist
  is '是否持久化转换单据';
comment on column BOT_RULE_CONF.script_before_trans
  is '转换前脚本';
comment on column BOT_RULE_CONF.script_after_trans
  is '转换后脚本';
alter table BOT_RULE_CONF
  add primary key (UUID)
  using index 
  tablespace USERS
  pctfree 10
  initrans 2
  maxtrans 255
  storage
  (
    initial 64K
    next 1M
    minextents 1
    maxextents unlimited
  );

 
create table BOT_RULE_OBJ_MAPPING
(
  uuid                    VARCHAR2(64) not null,
  create_time             TIMESTAMP(6),
  creator                 VARCHAR2(255),
  modify_time             TIMESTAMP(6),
  modifier                VARCHAR2(255),
  rec_ver                 NUMBER(10) not null,
  rule_conf_uuid          VARCHAR2(64) not null,
  source_obj_id           VARCHAR2(64),
  source_obj_field        VARCHAR2(32),
  source_obj_field_name   VARCHAR2(64),
  target_obj_field        VARCHAR2(32),
  render_value_type       NUMBER(1),
  render_value_expression CLOB,
  is_reverse_write        NUMBER(1) default 0,
  system_unit_id          VARCHAR2(32),
  target_obj_field_name   VARCHAR2(64)
)
tablespace USERS
  pctfree 10
  initrans 1
  maxtrans 255
  storage
  (
    initial 64K
    next 1M
    minextents 1
    maxextents unlimited
  );
comment on table BOT_RULE_OBJ_MAPPING
  is '单据转换规则字段映射';
comment on column BOT_RULE_OBJ_MAPPING.rule_conf_uuid
  is '规则配置UUID';
comment on column BOT_RULE_OBJ_MAPPING.source_obj_id
  is '源单据ID';
comment on column BOT_RULE_OBJ_MAPPING.source_obj_field
  is '源单据字段';
comment on column BOT_RULE_OBJ_MAPPING.source_obj_field_name
  is '源单据字段名称';
comment on column BOT_RULE_OBJ_MAPPING.target_obj_field
  is '目标单据字段';
comment on column BOT_RULE_OBJ_MAPPING.render_value_type
  is '值计算方式';
comment on column BOT_RULE_OBJ_MAPPING.render_value_expression
  is '值计算表达式';
comment on column BOT_RULE_OBJ_MAPPING.is_reverse_write
  is '是否反写映射';
comment on column BOT_RULE_OBJ_MAPPING.target_obj_field_name
  is '目标单据字段名称';
alter table BOT_RULE_OBJ_MAPPING
  add primary key (UUID)
  using index 
  tablespace USERS
  pctfree 10
  initrans 2
  maxtrans 255
  storage
  (
    initial 64K
    next 1M
    minextents 1
    maxextents unlimited
  );

 
create table BOT_RULE_OBJ_RELA
(
  uuid           VARCHAR2(64) not null,
  create_time    TIMESTAMP(6),
  creator        VARCHAR2(255),
  modify_time    TIMESTAMP(6),
  modifier       VARCHAR2(255),
  rec_ver        NUMBER(10) not null,
  rule_conf_uuid VARCHAR2(64) not null,
  rela_obj_id    VARCHAR2(64) not null,
  rela_obj_name  VARCHAR2(64),
  system_unit_id VARCHAR2(32)
)
tablespace USERS
  pctfree 10
  initrans 1
  maxtrans 255
  storage
  (
    initial 64K
    next 1M
    minextents 1
    maxextents unlimited
  );
comment on table BOT_RULE_OBJ_RELA
  is '单据转换对象关系';
comment on column BOT_RULE_OBJ_RELA.rule_conf_uuid
  is '单据转换规则UUID';
comment on column BOT_RULE_OBJ_RELA.rela_obj_id
  is '关系单据ID';
comment on column BOT_RULE_OBJ_RELA.rela_obj_name
  is '关系单据名称';
alter table BOT_RULE_OBJ_RELA
  add primary key (UUID)
  using index 
  tablespace USERS
  pctfree 10
  initrans 2
  maxtrans 255
  storage
  (
    initial 64K
    next 1M
    minextents 1
    maxextents unlimited
  );
 
create table BOT_RULE_OBJ_RELA_MAPPING
(
  uuid                  VARCHAR2(64) not null,
  create_time           TIMESTAMP(6),
  creator               VARCHAR2(255),
  modify_time           TIMESTAMP(6),
  modifier              VARCHAR2(255),
  rec_ver               NUMBER(10) not null,
  rule_obj_rela_uuid    VARCHAR2(64) not null,
  source_obj_id         VARCHAR2(64),
  source_obj_field      VARCHAR2(32),
  source_obj_field_name VARCHAR2(64),
  rela_obj_field        VARCHAR2(32),
  rela_obj_field_name   VARCHAR2(64),
  system_unit_id        VARCHAR2(32)
)
tablespace USERS
  pctfree 10
  initrans 1
  maxtrans 255
  storage
  (
    initial 64K
    next 1M
    minextents 1
    maxextents unlimited
  );
comment on table BOT_RULE_OBJ_RELA_MAPPING
  is '单据转换对象关系字段映射';
comment on column BOT_RULE_OBJ_RELA_MAPPING.rule_obj_rela_uuid
  is '规则关联关系UUID';
comment on column BOT_RULE_OBJ_RELA_MAPPING.source_obj_id
  is '源单据ID';
comment on column BOT_RULE_OBJ_RELA_MAPPING.source_obj_field
  is '源单据字段';
comment on column BOT_RULE_OBJ_RELA_MAPPING.rela_obj_field
  is '关联关系字段';
alter table BOT_RULE_OBJ_RELA_MAPPING
  add primary key (UUID)
  using index 
  tablespace USERS
  pctfree 10
  initrans 2
  maxtrans 255
  storage
  (
    initial 64K
    next 1M
    minextents 1
    maxextents unlimited
  );

 
