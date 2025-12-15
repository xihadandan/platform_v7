-- Create table
create table LOG_BUSINESS_OPERATION
(
  uuid           VARCHAR2(255 CHAR) not null,
  create_time    TIMESTAMP(6),
  creator        VARCHAR2(255 CHAR),
  modifier       VARCHAR2(255 CHAR),
  modify_time    TIMESTAMP(6),
  rec_ver        NUMBER(10),
  module_id      VARCHAR2(255 CHAR),
  module_name    VARCHAR2(255 CHAR),
  data_def_type  VARCHAR2(255 CHAR),
  data_def_id    VARCHAR2(255 CHAR),
  data_def_name  VARCHAR2(1024 CHAR),
  data_id        VARCHAR2(255 CHAR),
  data_name      VARCHAR2(1024 CHAR),
  operation      VARCHAR2(255 CHAR),
  user_id        VARCHAR2(64 CHAR),
  user_name      VARCHAR2(64 CHAR),
  system_unit_id VARCHAR2(12)
);
-- Add comments to the columns 
comment on column LOG_BUSINESS_OPERATION.create_time
  is '操作时间';
comment on column LOG_BUSINESS_OPERATION.module_id
  is '模块id';
comment on column LOG_BUSINESS_OPERATION.module_name
  is '模块名称';
comment on column LOG_BUSINESS_OPERATION.data_def_type
  is '数据类型';
comment on column LOG_BUSINESS_OPERATION.data_def_id
  is '数据定义id';
comment on column LOG_BUSINESS_OPERATION.data_def_name
  is '数据定义名称';
comment on column LOG_BUSINESS_OPERATION.data_id
  is '数据id';
comment on column LOG_BUSINESS_OPERATION.data_name
  is '数据名称';
comment on column LOG_BUSINESS_OPERATION.operation
  is '操作类型';
comment on column LOG_BUSINESS_OPERATION.user_id
  is '操作人id';
comment on column LOG_BUSINESS_OPERATION.user_name
  is '操作人';
comment on column LOG_BUSINESS_OPERATION.system_unit_id
  is '归属系统单位';
-- Create/Recreate primary, unique and foreign key constraints 
alter table LOG_BUSINESS_OPERATION
  add constraint LOG_BUSINESS_OPERATION_PK primary key (UUID)
  using index;
  
  
-- Create table
create table LOG_BUSINESS_DETAILS
(
  uuid           VARCHAR2(255 CHAR) not null,
  create_time    TIMESTAMP(6),
  creator        VARCHAR2(255 CHAR),
  modifier       VARCHAR2(255 CHAR),
  modify_time    TIMESTAMP(6),
  rec_ver        NUMBER(10),
  log_id         VARCHAR2(255 CHAR) not null,
  data_def_type  VARCHAR2(255 CHAR),
  data_def_id    VARCHAR2(255 CHAR),
  data_def_name  VARCHAR2(1024 CHAR),
  data_id        VARCHAR2(255 CHAR),
  data_name      VARCHAR2(1024 CHAR),
  parent_data_id VARCHAR2(255 CHAR),
  attr_id        VARCHAR2(255 CHAR),
  attr_name      VARCHAR2(1024 CHAR),
  attr_type      VARCHAR2(255 CHAR),
  operation      VARCHAR2(255 CHAR),
  before_value   CLOB,
  after_value    CLOB,
  system_unit_id VARCHAR2(12)
);
-- Add comments to the columns 
comment on column LOG_BUSINESS_DETAILS.create_time
  is '操作时间';
comment on column LOG_BUSINESS_DETAILS.log_id
  is '明细日志id';
comment on column LOG_BUSINESS_DETAILS.data_def_type
  is '数据类型';
comment on column LOG_BUSINESS_DETAILS.data_def_id
  is '数据定义id';
comment on column LOG_BUSINESS_DETAILS.data_def_name
  is '数据定义名称';
comment on column LOG_BUSINESS_DETAILS.data_id
  is '数据id';
comment on column LOG_BUSINESS_DETAILS.data_name
  is '数据名称';
comment on column LOG_BUSINESS_DETAILS.parent_data_id
  is '父级数据id';
comment on column LOG_BUSINESS_DETAILS.attr_id
  is '属性id';
comment on column LOG_BUSINESS_DETAILS.attr_name
  is '属性名称';
comment on column LOG_BUSINESS_DETAILS.attr_type
  is '属性类型';
comment on column LOG_BUSINESS_DETAILS.operation
  is '操作类型';
comment on column LOG_BUSINESS_DETAILS.before_value
  is '操作前的数据值';
comment on column LOG_BUSINESS_DETAILS.after_value
  is '操作后的数据值';
comment on column LOG_BUSINESS_DETAILS.system_unit_id
  is '归属系统单位';
-- Create/Recreate primary, unique and foreign key constraints 
alter table LOG_BUSINESS_DETAILS
  add constraint LOG_BUSINESS_DETAILS_PK primary key (UUID)
  using index;

