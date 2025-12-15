-- Create table
create table APP_TIP_NO_LONGER_REMIND
(
  uuid           VARCHAR2(255 CHAR) not null,
  create_time    TIMESTAMP(6),
  creator        VARCHAR2(255 CHAR),
  modifier       VARCHAR2(255 CHAR),
  modify_time    TIMESTAMP(6),
  rec_ver        NUMBER(10),
  tip_code       VARCHAR2(255),
  user_id        VARCHAR2(255)
);

create unique index SYS_C0026759 on APP_TIP_NO_LONGER_REMIND (TIP_CODE, USER_ID)
;
-- Create/Recreate primary, unique and foreign key constraints 
alter table APP_TIP_NO_LONGER_REMIND
  add primary key (UUID) ;
