-- Create table
create table BUSINESS_APPLICATION
(
  business_category_uuid VARCHAR2(255),
  uuid                   VARCHAR2(255) not null,
  create_time            TIMESTAMP(6),
  creator                VARCHAR2(255),
  modifier               VARCHAR2(255),
  modify_time            TIMESTAMP(6),
  rec_ver                VARCHAR2(255)
);
-- Create/Recreate primary, unique and foreign key constraints 
alter table BUSINESS_APPLICATION
  add constraint BUSINESS_APPLICATION_UUID primary key (UUID);
 
-- Create table
create table BUSINESS_APPLICATION_CONFIG
(
  form_uuid                 VARCHAR2(255),
  rule_uuid                 VARCHAR2(255),
  business_application_uuid VARCHAR2(255),
  uuid                      VARCHAR2(255) not null,
  create_time               TIMESTAMP(6),
  creator                   VARCHAR2(255),
  modifier                  VARCHAR2(255),
  modify_time               TIMESTAMP(6),
  rec_ver                   VARCHAR2(255),
  dict_code                 VARCHAR2(255),
  dict_type                 VARCHAR2(255)
);
-- Add comments to the columns 
comment on column BUSINESS_APPLICATION_CONFIG.form_uuid
  is '应用子表单';
comment on column BUSINESS_APPLICATION_CONFIG.rule_uuid
  is '规则id';
comment on column BUSINESS_APPLICATION_CONFIG.dict_code
  is '应用于(字典表_code)';
comment on column BUSINESS_APPLICATION_CONFIG.dict_type
  is '应用于(字典表_父节点_type)';
-- Create/Recreate primary, unique and foreign key constraints 
alter table BUSINESS_APPLICATION_CONFIG
  add constraint BUSINESS_CONFIG_UUID primary key (UUID);



-- Create table
create table BUSINESS_CATEGORY
(
  name              VARCHAR2(256),
  id                VARCHAR2(32),
  code              VARCHAR2(32),
  manage_dept       VARCHAR2(64),
  manage_dept_value VARCHAR2(256),
  manage_user       VARCHAR2(64),
  manage_user_value VARCHAR2(256),
  uuid              VARCHAR2(255) not null,
  create_time       TIMESTAMP(6),
  creator           VARCHAR2(255),
  modifier          VARCHAR2(255),
  modify_time       TIMESTAMP(6),
  rec_ver           VARCHAR2(255),
  system_unit_id    VARCHAR2(12)
);
-- Add comments to the table 
comment on table BUSINESS_CATEGORY
  is '业务类别分类表';
-- Add comments to the columns 
comment on column BUSINESS_CATEGORY.name
  is '名称';
comment on column BUSINESS_CATEGORY.id
  is 'id';
comment on column BUSINESS_CATEGORY.code
  is '编号';
comment on column BUSINESS_CATEGORY.manage_dept
  is '管理单位key值';
comment on column BUSINESS_CATEGORY.manage_dept_value
  is '管理单位value值';
comment on column BUSINESS_CATEGORY.manage_user
  is '管理员key值';
comment on column BUSINESS_CATEGORY.manage_user_value
  is '管理员value值';
comment on column BUSINESS_CATEGORY.system_unit_id
  is '归属系统单位';
-- Create/Recreate primary, unique and foreign key constraints 
alter table BUSINESS_CATEGORY
  add constraint PK_BUSINESS_CATEGORY primary key (UUID);
-- Create table
create table BUSINESS_CATEGORY_ORG
(
  name                   VARCHAR2(255),
  parent_uuid            VARCHAR2(255),
  dept                   VARCHAR2(255),
  dept_value             VARCHAR2(256),
  unit                   VARCHAR2(255),
  unit_value             VARCHAR2(256),
  code                   VARCHAR2(32),
  type                   VARCHAR2(1),
  business_category_uuid VARCHAR2(255),
  uuid                   VARCHAR2(255) not null,
  create_time            TIMESTAMP(6),
  creator                VARCHAR2(255),
  modifier               VARCHAR2(255),
  modify_time            TIMESTAMP(6),
  rec_ver                VARCHAR2(255)
);
-- Add comments to the table 
comment on table BUSINESS_CATEGORY_ORG
  is '分类管理组织';
-- Add comments to the columns 
comment on column BUSINESS_CATEGORY_ORG.name
  is '名称';
comment on column BUSINESS_CATEGORY_ORG.parent_uuid
  is '父节点';
comment on column BUSINESS_CATEGORY_ORG.dept
  is '部门key';
comment on column BUSINESS_CATEGORY_ORG.dept_value
  is '部门value';
comment on column BUSINESS_CATEGORY_ORG.unit
  is '单位key';
comment on column BUSINESS_CATEGORY_ORG.unit_value
  is '单位value';
comment on column BUSINESS_CATEGORY_ORG.code
  is '编号';
comment on column BUSINESS_CATEGORY_ORG.type
  is '类型(1单位，2分类)';
-- Create/Recreate primary, unique and foreign key constraints 
alter table BUSINESS_CATEGORY_ORG
  add constraint PK_BUSINESS_CATEGORY_ORG primary key (UUID);
  
-- Create table
create table BUSINESS_ROLE
(
  name                   VARCHAR2(255),
  business_category_uuid VARCHAR2(255),
  users                  VARCHAR2(255),
  uuid                   VARCHAR2(255) not null,
  create_time            TIMESTAMP(6),
  creator                VARCHAR2(255),
  modifier               VARCHAR2(255),
  modify_time            TIMESTAMP(6),
  rec_ver                VARCHAR2(255),
  users_value            VARCHAR2(255)
);
-- Add comments to the table 
comment on table BUSINESS_ROLE
  is '业务角色';
-- Add comments to the columns 
comment on column BUSINESS_ROLE.name
  is '名称';
comment on column BUSINESS_ROLE.users
  is '成员(多选)';
-- Create/Recreate primary, unique and foreign key constraints 
alter table BUSINESS_ROLE
  add constraint PK_BUSINESS_ROLE primary key (UUID);

-- Create table
create table BUSINESS_ROLE_ORG_USER
(
  business_category_org_uuid VARCHAR2(255),
  business_role_uuid         VARCHAR2(255),
  users                      VARCHAR2(255),
  users_value                VARCHAR2(2000),
  create_time                TIMESTAMP(6),
  creator                    VARCHAR2(255),
  modifier                   VARCHAR2(255),
  modify_time                TIMESTAMP(6),
  rec_ver                    VARCHAR2(255),
  uuid                       VARCHAR2(255) not null
);
-- Add comments to the table 
comment on table BUSINESS_ROLE_ORG_USER
  is '角色和单位对应的人员';
-- Add comments to the columns 
comment on column BUSINESS_ROLE_ORG_USER.business_category_org_uuid
  is '部门id';
comment on column BUSINESS_ROLE_ORG_USER.business_role_uuid
  is '角色id';
comment on column BUSINESS_ROLE_ORG_USER.users
  is '成员(多选)';
-- Create/Recreate primary, unique and foreign key constraints 
alter table BUSINESS_ROLE_ORG_USER
  add constraint BUSINESS_ROLE_ORG_USER_KEY primary key (UUID);

insert into CD_DATA_DICT (UUID, CREATE_TIME, CREATOR, MODIFIER, MODIFY_TIME, REC_VER, CODE, NAME, TYPE, PARENT_UUID, SOURCE_UUID, SOURCE_TYPE, EDITABLE, DELETABLE, CHILD_EDITABLE)
values ('c973d316-3bbc-47cd-bb60-c41ac828dbd4', '18-3月 -19 03.00.39.690000 下午', 'U0000000000', 'U0000000000', '18-3月 -19 03.00.39.690000 下午', 4, '003011', '表单应用配置', 'BASIC_DATA_FORM_APP', 'fdf8472f-eced-4594-b829-9f890a41f5cc', null, null, 1, 1, 1);


