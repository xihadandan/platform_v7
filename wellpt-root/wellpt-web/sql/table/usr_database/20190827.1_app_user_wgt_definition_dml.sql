-- Create table
create table APP_USER_WIDGET_DEFINITION
(
  uuid            VARCHAR2(64 CHAR) primary key,
  create_time     TIMESTAMP(6),
  creator         VARCHAR2(255 CHAR),
  modifier        VARCHAR2(255 CHAR),
  modify_time     TIMESTAMP(6),
  rec_ver         NUMBER(10),
  user_id         VARCHAR2(64) not null,
  widget_id       VARCHAR2(255 CHAR),
  definition_json CLOB
);

comment on table APP_USER_WIDGET_DEFINITION
  is '用户组件的定制化配置';
