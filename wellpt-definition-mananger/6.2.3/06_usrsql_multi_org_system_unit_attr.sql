create table MULTI_ORG_SYSTEM_UNIT_ATTR
(
  uuid          VARCHAR2(255 CHAR) primary key,
  create_time   TIMESTAMP(6),
  creator       VARCHAR2(255 CHAR),
  modifier      VARCHAR2(255 CHAR),
  modify_time   TIMESTAMP(6),
  rec_ver       NUMBER(10),
  attr_code          VARCHAR2(255 CHAR) not null,
  attr_value            VARCHAR2(4000 CHAR),
  remark        VARCHAR2(255 char),
  system_unit_id varchar2(64 char)
)
-- Add/modify columns 
alter table MULTI_ORG_SYSTEM_UNIT_ATTR add attr_name varchar2(4000 char);
