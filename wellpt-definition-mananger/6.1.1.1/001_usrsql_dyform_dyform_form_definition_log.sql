-- Create table
create table DYFORM_FORM_DEFINITION_LOG
(
  uuid                   VARCHAR2(50),
  create_time            TIMESTAMP(6),
  creator                VARCHAR2(50),
  modifier               VARCHAR2(50),
  modify_time            TIMESTAMP(6),
  rec_ver                NUMBER(10),
  code                   VARCHAR2(255 CHAR),
  form_uuid           VARCHAR2(255 CHAR),
  operate_ip       VARCHAR2(255 CHAR),
    definition_json        CLOB
);
-- Add comments to the columns 
comment on column DYFORM_FORM_DEFINITION_LOG.form_uuid
  is '归属表单UUID';
comment on column DYFORM_FORM_DEFINITION_LOG.operate_ip
  is '操作IP';
comment on column DYFORM_FORM_DEFINITION_LOG.definition_json
  is '定义JSON';
-- Create/Recreate indexes 
create unique index DYFORM_FORM_DEFINITION_LOG on DYFORM_FORM_DEFINITION_LOG (UUID);