-- Add/modify columns 
alter table DYFORM_FORM_DEFINITION add MODULE_ID varchar2(120 CHAR);
-- Add comments to the columns 
comment on column DYFORM_FORM_DEFINITION.MODULE_ID
  is '所属模块';

  -- Add/modify columns 
alter table WF_FLOW_DEFINITION add MODULE_ID varchar2(120 CHAR);
-- Add comments to the columns 
comment on column WF_FLOW_DEFINITION.MODULE_ID
  is '所属模块';
  
  -- Create table
create table DYFORM_FORM_DEFINITION_REF
(
  uuid           VARCHAR2(50),
  create_time    TIMESTAMP(6),
  creator        VARCHAR2(50),
  modify_time    TIMESTAMP(6),
  rec_ver        NUMBER(10),
  ref_uuid       VARCHAR2(50),
  module_id      VARCHAR2(100 CHAR),
  system_unit_id VARCHAR2(50),
  modifier       VARCHAR2(50)
);
-- Add comments to the table 
comment on table DYFORM_FORM_DEFINITION_REF
  is '表单引用关系';
-- Add comments to the columns 
comment on column DYFORM_FORM_DEFINITION_REF.ref_uuid
  is '引用的表单UUID';
comment on column DYFORM_FORM_DEFINITION_REF.module_id
  is '所属模块ID';

  
  
  -- Create table
create table WF_FLOW_DEFINITION_REF
(
  uuid           VARCHAR2(50),
  create_time    TIMESTAMP(6),
  creator        VARCHAR2(50),
  modify_time    TIMESTAMP(6),
  rec_ver        NUMBER(10),
  ref_uuid       VARCHAR2(50),
  module_id      VARCHAR2(100 CHAR),
  system_unit_id VARCHAR2(50),
  modifier       VARCHAR2(50)
);

-- Add comments to the table 
comment on table WF_FLOW_DEFINITION_REF
  is '流程定义引用';
-- Add comments to the columns 
comment on column WF_FLOW_DEFINITION_REF.ref_uuid
  is '被引用的流程定义UUID';
comment on column WF_FLOW_DEFINITION_REF.module_id
  is '模块ID';

  
  -- Create table
create table CD_DATA_STORE_DEFINITION_REF
(
  uuid           VARCHAR2(50),
  create_time    TIMESTAMP(6),
  creator        VARCHAR2(50),
  modify_time    TIMESTAMP(6),
  rec_ver        NUMBER(10),
  ref_uuid       VARCHAR2(50),
  module_id      VARCHAR2(100 CHAR),
  system_unit_id VARCHAR2(50),
  modifier       VARCHAR2(50)
);

comment on table CD_DATA_STORE_DEFINITION_REF
  is '数据仓库引用';
  
  -- Add/modify columns 
alter table CD_DATA_STORE_DEFINITION add module_id varchar2(120 CHAR);
-- Add comments to the columns 
comment on column CD_DATA_STORE_DEFINITION.module_id
  is '模块ID';

  
  
  -- Add/modify columns 
alter table CD_DATA_DICT add module_id varchar2(120 char);
-- Add comments to the columns 
comment on column CD_DATA_DICT.module_id
  is '模块ID';

  
-- Add/modify columns 
alter table CD_DATA_DICT add seq number(10) default 1;
-- Add comments to the columns 
comment on column CD_DATA_DICT.seq
  is '序号';

-- Create table
create table CD_DATA_DICT_REF
(
  uuid           VARCHAR2(50),
  create_time    TIMESTAMP(6),
  creator        VARCHAR2(50),
  modify_time    TIMESTAMP(6),
  rec_ver        NUMBER(10),
  ref_uuid       VARCHAR2(50),
  parent_uuid    VARCHAR2(50),
  module_id      VARCHAR2(100 CHAR),
  system_unit_id VARCHAR2(50),
  modifier       VARCHAR2(50)
);

-- Add comments to the table 
comment on table CD_DATA_DICT_REF
  is '数据字典引用';
-- Add comments to the columns 
comment on column CD_DATA_DICT_REF.ref_uuid
  is '引用的数据字典UUID';
comment on column CD_DATA_DICT_REF.parent_uuid
  is '父级数据字典UUID';
comment on column CD_DATA_DICT_REF.module_id
  is '模块ID';

  
  
  -- Add/modify columns 
alter table MSG_MESSAGE_TEMPLATE add module_id varchar2(120 char);
-- Add comments to the columns 
comment on column MSG_MESSAGE_TEMPLATE.module_id
  is '模块ID';

  
  
  -- Create table
create table MSG_MESSAGE_TEMPLATE_REF
(
  uuid           VARCHAR2(50),
  create_time    TIMESTAMP(6),
  creator        VARCHAR2(50),
  modify_time    TIMESTAMP(6),
  rec_ver        NUMBER(10),
  ref_uuid       VARCHAR2(50),
  module_id      VARCHAR2(100 CHAR),
  system_unit_id VARCHAR2(50),
  modifier       VARCHAR2(50)
);

-- Add comments to the table 
comment on table MSG_MESSAGE_TEMPLATE_REF
  is '消息格式引用';
  
  -- Add/modify columns 
alter table CD_PRINT_TEMPLATE add module_id varchar2(120 CHAR);
-- Add comments to the columns 
comment on column CD_PRINT_TEMPLATE.module_id
  is '模块ID';

  
  -- Create table
create table CD_PRINT_TEMPLATE_REF
(
  uuid           VARCHAR2(50),
  create_time    TIMESTAMP(6),
  creator        VARCHAR2(50),
  modify_time    TIMESTAMP(6),
  rec_ver        NUMBER(10),
  ref_uuid       VARCHAR2(50),
  module_id      VARCHAR2(100 CHAR),
  system_unit_id VARCHAR2(50),
  modifier       VARCHAR2(50)
);

-- Add comments to the table 
comment on table CD_PRINT_TEMPLATE_REF
  is '引用打印模板';

  
  -- Add/modify columns 
alter table CD_SERIAL_NUMBER add module_id varchar2(120 char);
-- Add comments to the columns 
comment on column CD_SERIAL_NUMBER.module_id
  is '模块id';

  -- Add/modify columns 
alter table CD_SERIAL_NUMBER_MAINTAIN add module_id varchar2(120 char);
-- Add comments to the columns 
comment on column CD_SERIAL_NUMBER_MAINTAIN.module_id
  is '模块ID';

  
  -- Add/modify columns 
alter table CD_EXCEL_IMPORT_RULE add module_id varchar2(120 char);
-- Add comments to the columns 
comment on column CD_EXCEL_IMPORT_RULE.module_id
  is '模块ID';
-- Add/modify columns 
alter table CD_SYSTEM_TABLE_ENTITY add module_id varchar2(120 char);
-- Add comments to the columns 
comment on column CD_SYSTEM_TABLE_ENTITY.module_id
  is '模块ID';

-- Add/modify columns 
alter table TASK_JOB_DETAILS add module_id varchar2(120 char);
-- Add comments to the columns 
comment on column TASK_JOB_DETAILS.module_id
  is '模块ID';

 
 -- Add/modify columns 
alter table AUDIT_RESOURCE add module_id varchar2(120 char);
-- Add comments to the columns 
comment on column AUDIT_RESOURCE.module_id
  is '模块ID';

  
  
  -- Add/modify columns 
alter table AUDIT_PRIVILEGE add app_id varchar2(120 char);
-- Add comments to the columns 
comment on column AUDIT_PRIVILEGE.app_id
  is '归属的系统/模块/应用ID';

  
    -- Add/modify columns 
alter table AUDIT_ROLE add app_id varchar2(120 char);
-- Add comments to the columns 
comment on column AUDIT_ROLE.app_id
  is '归属的系统/模块/应用ID';
  
  
