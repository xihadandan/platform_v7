
-- create table
create table cd_serial_number_relation (
  uuid varchar(36) not null comment '主键uuid',
  create_time datetime  comment '创建时间',
  creator varchar(255)  comment '创建人',
  modifier varchar(255)  comment '更新人',
  modify_time datetime  comment '更新时间',
  rec_ver int  comment '数据版本',
  system_unit_id varchar(64) comment '系统单位Id',

  sn_id varchar(255) not null comment '流水号定义Id',
  object_type int not null comment '对象类型：1：数据库表',
  object_name varchar(255) not null comment '对象名',
  field_name varchar(255) not null comment '字段名',
  primary key (uuid),
  unique key index_c_s_n_r_sn_id (sn_id,object_type,object_name,field_name)
) comment='流水号关联表字段记录';

-- create table
create table cd_serial_number_record (
  uuid varchar(36) not null comment '主键uuid',
  create_time datetime comment '创建时间',
  creator varchar(255) comment '创建人',
  modifier varchar(255)  comment '更新人',
  modify_time datetime  comment '更新时间',
  rec_ver int  comment '数据版本',
  system_unit_id varchar(64) comment '系统单位Id',

  relation_uuid  varchar(36) not null comment '流水号关联表字段记录UUID',
  data_uuid  varchar(36) not null comment '使用流水号的数据UUID',
  key_part varchar(255) not null comment '关键部分',
  head_part varchar(255) comment '头部',
  last_part varchar(255) comment '尾部',
  pointer int not null comment '指针',
  serial_no varchar(255) not null comment '流水号',
  primary key (uuid),
  unique key index_c_s_n_re_relation_uuid (relation_uuid,key_part,pointer),
  key index_c_s_n_re_data_uuid (data_uuid),
  key index_c_s_n_re_serial_no (serial_no)
) comment='流水号记录';




-- Create table
create table cd_serial_number_old_def
(
  uuid varchar(36) not null comment '主键uuid',
  create_time datetime comment '创建时间',
  creator varchar(255) comment '创建人',
  modifier varchar(255)  comment '更新人',
  modify_time datetime  comment '更新时间',
  rec_ver int  comment '数据版本',
  system_unit_id varchar(64) comment '系统单位Id',

  definition_type   int not null comment '定义类型：1，表单，2，流程',
  definition_uuid   varchar(36) not null comment '定义uuid',
  definition_name   varchar(255) not null comment '定义名称',
  table_name        varchar(255) comment '数据库表名称',
  data_state        int default 0 not null comment '数据处理状态：0：待处理，1：有流水号字段，2：无流水号字段，3，数据已处理',
  sn_data           text comment '流水号字段相关信息json',
  primary key (uuid),
  unique key index_c_s_n_o_d_def_unique (definition_type,definition_uuid),
  key index_c_s_n_o_d_def_data_state (data_state)
)comment='流水号定义关联记录处理';

-- Create table
create table cd_serial_number_old_data
(
  uuid varchar(36) not null comment '主键uuid',
  create_time datetime comment '创建时间',
  creator varchar(255) comment '创建人',
  modifier varchar(255)  comment '更新人',
  modify_time datetime  comment '更新时间',
  rec_ver int  comment '数据版本',
  system_unit_id varchar(64) comment '系统单位Id',

  sn_type           varchar(255) comment '流水号分类',
  sn_id             varchar(255) comment '流水号定义ID',
  serial_no         varchar(255) not null comment '流水号',
  object_name       varchar(255) not null comment '表名',
  field_name        varchar(255) not null comment '字段名',
  data_uuid         varchar(36) not null comment '数据UUID',
  data_state        int default 0 not null comment '数据状态：1：已占用，2：匹配有重复（重复记录只记录一条RECORD_DATA_UUID）3：不匹配',
  maintain_uuid     varchar(36) comment '流水号维护记录uuid',
  record_data_uuid  varchar(36) comment '已记录数据UUID',
  primary key (uuid),
  key index_c_s_n_o_d_d_s_id (sn_id),
  key index_c_s_n_o_d_d_o_name (object_name),
  key index_c_s_n_o_d_d_d_uuid (data_uuid)
)comment='流水号旧数据记录表';

-- 初始化数据
-- 表单
insert into cd_serial_number_old_def (uuid,create_time,creator,rec_ver,definition_type,definition_uuid,definition_name,table_name,data_state)
select uuid(),now(),'admin',1,1,uuid,name,table_name,0 from dyform_form_definition where form_type='p' or form_type='P';
-- 流程
insert into cd_serial_number_old_def (uuid,create_time,creator,rec_ver,definition_type,definition_uuid,definition_name,table_name,data_state)
select uuid(),now(),'admin',1,2,uuid,name,'wf_task_instance',0 from wf_flow_definition;







