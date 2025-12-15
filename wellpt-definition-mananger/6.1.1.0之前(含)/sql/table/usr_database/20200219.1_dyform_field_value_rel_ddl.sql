-- Create table
create table DYFORM_FIELD_VALUE_REL
(
  uuid        VARCHAR2(255 CHAR),
  creator     VARCHAR2(255 CHAR),
  create_time TIMESTAMP(6),
  modifier    VARCHAR2(255 CHAR),
  modify_time TIMESTAMP(6),
  rec_ver     NUMBER(10),
  form_id     VARCHAR2(64 CHAR),
  field_value VARCHAR2(255 CHAR),
  field_text  VARCHAR2(255 CHAR),
  field_code  VARCHAR2(255 CHAR),
  data_uuid   VARCHAR2(64 CHAR)
);
-- Add comments to the columns 
comment on column DYFORM_FIELD_VALUE_REL.form_id
  is '表单id';
comment on column DYFORM_FIELD_VALUE_REL.field_value
  is '表单字段值';
comment on column DYFORM_FIELD_VALUE_REL.field_text
  is '表单字段显示值';
comment on column DYFORM_FIELD_VALUE_REL.field_code
  is '表单字段编码';
comment on column DYFORM_FIELD_VALUE_REL.data_uuid
  is '数据uuid';
-- Create/Recreate indexes 
create index DYFORM_F_V_REL_DD_FE_FD on DYFORM_FIELD_VALUE_REL (DATA_UUID, FIELD_CODE, FORM_ID);

-- Create/Recreate primary, unique and foreign key constraints 
alter table DYFORM_FIELD_VALUE_REL
  add constraint DYFORM_FIELD_VALUE_REL_PRI primary key (UUID)


