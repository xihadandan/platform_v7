
-- Create table
create table MULTI_ORG_DING_ROLE
(
  uuid           VARCHAR2(255) not null,
  create_time    TIMESTAMP(6),
  creator        VARCHAR2(255),
  modifier       VARCHAR2(255),
  modify_time    TIMESTAMP(6),
  rec_ver        NUMBER(10,2),
  role_id        VARCHAR2(64),
  ding_role_id   VARCHAR2(64),
  ding_role_name VARCHAR2(512),
  system_unit_id VARCHAR2(64)
)
;
-- Add comments to the columns 
comment on column MULTI_ORG_DING_ROLE.uuid
  is '唯一主键';
comment on column MULTI_ORG_DING_ROLE.create_time
  is '创建时间';
comment on column MULTI_ORG_DING_ROLE.creator
  is '创建人';
comment on column MULTI_ORG_DING_ROLE.modifier
  is '更新人';
comment on column MULTI_ORG_DING_ROLE.modify_time
  is '更新时间';
comment on column MULTI_ORG_DING_ROLE.rec_ver
  is '数据版本';
comment on column MULTI_ORG_DING_ROLE.role_id
  is '平台角色ID';
comment on column MULTI_ORG_DING_ROLE.ding_role_id
  is '钉钉角色ID';
comment on column MULTI_ORG_DING_ROLE.ding_role_name
  is '钉钉角色名称';
comment on column MULTI_ORG_DING_ROLE.system_unit_id
  is '系统单位ID';
-- Create/Recreate primary, unique and foreign key constraints 
alter table MULTI_ORG_DING_ROLE
  add constraint MULTI_ORG_DING_ROLE primary key (UUID);
  
  
  
  
  

-- Add/modify columns 
alter table MULTI_ORG_DING_ROLE add ding_group_id VARCHAR2(64);
-- Add comments to the columns 
comment on column MULTI_ORG_DING_ROLE.ding_group_id
  is '钉钉组';
  
  
  -- Add/modify columns 
alter table MULTI_ORG_DING_ROLE add ding_group_name VARCHAR2(512);
-- Add comments to the columns 
comment on column MULTI_ORG_DING_ROLE.ding_group_name
  is '钉钉组名称';
  
  
-- Add/modify columns 
alter table PT_T_EVENT_CALL_BACK add ding_label_id VARCHAR2(512);
-- Add comments to the columns 
comment on column PT_T_EVENT_CALL_BACK.ding_label_id
  is '角色发生变更的deptId列表';
  
  
  
  
  
  
-- Drop primary, unique and foreign key constraints 
alter table AUDIT_ROLE
  drop constraint UK_8RY1RAJCN8M5P4GET4STEGV3I cascade;
  
-- Drop indexes 
drop index UK_8RY1RAJCN8M5P4GET4STEGV3I;  