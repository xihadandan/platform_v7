create table WF_TASK_INSTANCE_TOPPING
(
  uuid                    VARCHAR2(255 CHAR) not null,
  create_time             TIMESTAMP(6),
  creator                 VARCHAR2(255 CHAR),
  modifier                VARCHAR2(255 CHAR),
  modify_time             TIMESTAMP(6),
  rec_ver                 NUMBER(10),
  user_id                 VARCHAR2(255 CHAR),
  task_inst_uuid          VARCHAR2(255 CHAR),
  is_topping              NUMBER(1) default 1
);

alter table WF_TASK_INSTANCE_TOPPING
  add primary key (UUID);