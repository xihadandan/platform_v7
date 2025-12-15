create table MQ_TRANSACTION
(
  uuid        VARCHAR2(64) primary key,
  create_time TIMESTAMP(6),
  creator     VARCHAR2(255 CHAR),
  modifier    VARCHAR2(255 CHAR),
  modify_time TIMESTAMP(6),
  rec_ver     NUMBER(10),
  id          VARCHAR2(64),
  status      NUMBER(1)
);

alter table MQ_TRANSACTION
add constraint UNIQ_MQ_TRANSACTION_ID unique(id);

 