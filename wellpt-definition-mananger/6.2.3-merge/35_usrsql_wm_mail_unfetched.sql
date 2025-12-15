create table WM_MAIL_UNFETCHED
(
  uuid           VARCHAR2(255) primary key,
  create_time    TIMESTAMP(6),
  creator        VARCHAR2(255),
  modify_time    TIMESTAMP(6),
  modifier       VARCHAR2(255),
  rec_ver        NUMBER(10) not null,
  user_id        VARCHAR2(255) not null,
  fail_count     NUMBER(10) default 0,
  mail_uuid      VARCHAR2(255) not null,
  remark         VARCHAR2(2000 CHAR),
  system_unit_id VARCHAR2(64)
);

create index idx_wm_mailunfetched_u_m on WM_MAIL_UNFETCHED (MAIL_UUID, USER_ID);