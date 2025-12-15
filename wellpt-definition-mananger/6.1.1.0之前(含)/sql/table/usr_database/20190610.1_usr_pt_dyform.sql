-- Create table
create table DYFORM_COMMON_FIELD_REF
(
  uuid              VARCHAR2(50) not null,
  create_time       TIMESTAMP(6),
  creator           VARCHAR2(50),
  modify_time       TIMESTAMP(6),
  rec_ver           NUMBER(10),
  modifier          VARCHAR2(50),
  form_uuid         VARCHAR2(50),
  common_field_uuid VARCHAR2(50)
);
-- Create/Recreate primary, unique and foreign key constraints 
alter table DYFORM_COMMON_FIELD_REF
  add constraint PK_FORM_COMMON_FIELD primary key (UUID)
  using index ;



-- Create table
create table DYFORM_COMMON_FIELD_DEFINITION
(
  uuid              VARCHAR2(50) not null,
  create_time       TIMESTAMP(6),
  creator           VARCHAR2(50),
  modify_time       TIMESTAMP(6),
  rec_ver           NUMBER(10),
  scope             NUMBER(10),
  module_id         VARCHAR2(255),
  display_name      VARCHAR2(255),
  name              VARCHAR2(30),
  definition_json   CLOB,
  control_type      VARCHAR2(50),
  category_uuid     VARCHAR2(50),
  modifier          VARCHAR2(50),
  notes             VARCHAR2(500),
  module_name       VARCHAR2(50),
  control_type_name VARCHAR2(50),
  category_name     VARCHAR2(50)
);
-- Create/Recreate primary, unique and foreign key constraints 
alter table DYFORM_COMMON_FIELD_DEFINITION
  add constraint PK_FIELD_DEFINITION primary key (UUID)
  using index ;


-- Create table
create table DYFORM_COMMON_FIELD_CATEGORY
(
  uuid          VARCHAR2(50) not null,
  create_time   TIMESTAMP(6),
  creator       VARCHAR2(50),
  modify_time   TIMESTAMP(6),
  rec_ver       NUMBER(10),
  modifier      VARCHAR2(50),
  scope         NUMBER(1),
  module_id     VARCHAR2(250),
  category_name VARCHAR2(250)
);
-- Create/Recreate primary, unique and foreign key constraints 
alter table DYFORM_COMMON_FIELD_CATEGORY
  add constraint PK_D_C_F_CATEGORY primary key (UUID)
  using index 
;
