drop table IS_EXCHANGE_SYSTEM;
-- Create table
create table IS_EXCHANGE_SYSTEM
(
  UUID                VARCHAR2(255 CHAR) not null,
  CREATE_TIME         TIMESTAMP(6),
  CREATOR             VARCHAR2(255 CHAR),
  MODIFIER            VARCHAR2(255 CHAR),
  MODIFY_TIME         TIMESTAMP(6),
  REC_VER             NUMBER(10),
  CODE                VARCHAR2(255 CHAR),
  FTP_FILE_PATH       VARCHAR2(255 CHAR),
  FTP_SERVER_URL      VARCHAR2(255 CHAR),
  FTP_USER_NAME       VARCHAR2(255 CHAR),
  FTP_USER_PASSWORD   VARCHAR2(255 CHAR),
  ID                  VARCHAR2(255 CHAR),
  NAME                VARCHAR2(255 CHAR),
  RECEIVE_URL         VARCHAR2(255 CHAR),
  REMARK              VARCHAR2(255 CHAR),
  REPLY_MSG_URL       VARCHAR2(255 CHAR),
  ROUTE_CALLBACK_URL  VARCHAR2(255 CHAR),
  SEND_CALLBACK_URL   VARCHAR2(255 CHAR),
  SERVER_IP           VARCHAR2(255 CHAR),
  SUBJECTDN           VARCHAR2(255 CHAR),
  TYPE_ID             VARCHAR2(1000 CHAR),
  UNIT_ID             VARCHAR2(255 CHAR),
  TYPE_ID1            VARCHAR2(1000 CHAR),
  CANCEL_URL          VARCHAR2(255 CHAR),
  CANCEL_CALLBACK_URL VARCHAR2(255 CHAR),
  WEB_SERVICE_URL     VARCHAR2(255 CHAR),
  IS_CALL_BACK        NUMBER(1),
  IS_SYN_BACK         VARCHAR2(255 CHAR),
  EXCHANGE_TYPE       VARCHAR2(255 CHAR),
  SEND_POSITION       VARCHAR2(255 CHAR),
  IS_ENABLE_CA        NUMBER(1)
)
;
-- Create/Recreate primary, unique and foreign key constraints 
alter table IS_EXCHANGE_SYSTEM
  add primary key (UUID)
  ;

drop table IS_EXCHANGE_DATA_TYPE;
-- Create table
create table IS_EXCHANGE_DATA_TYPE
(
  UUID                 VARCHAR2(255 CHAR) not null,
  CREATE_TIME          TIMESTAMP(6),
  CREATOR              VARCHAR2(255 CHAR),
  MODIFIER             VARCHAR2(255 CHAR),
  MODIFY_TIME          TIMESTAMP(6),
  REC_VER              NUMBER(10),
  BUSINESS_TYPE_ID     VARCHAR2(255 CHAR),
  CODE                 VARCHAR2(255 CHAR),
  FORM_ID              VARCHAR2(255 CHAR),
  ID                   VARCHAR2(255 CHAR),
  NAME                 VARCHAR2(255 CHAR),
  RETAIN               NUMBER(1),
  SHOW_TO_UNIT         NUMBER(1),
  TABLE_NAME           VARCHAR2(255 CHAR),
  TEXT                 CLOB,
  RECEIVE_LIMIT        NUMBER(10),
  REPORT_LIMIT         NUMBER(10),
  UNIT_ID              VARCHAR2(255 CHAR),
  BUSINESS_ID          VARCHAR2(255 CHAR),
  TO_SYS               NUMBER(1),
  SYNCHRONOUS          NUMBER(1),
  UNIT_SYS_SOURCE_NAME VARCHAR2(255 CHAR),
  UNIT_SYS_SOURCE_ID   VARCHAR2(255 CHAR),
  MERGE                NUMBER(1),
  IS_SYN_BACK          VARCHAR2(255 CHAR)
)
;
-- Create/Recreate primary, unique and foreign key constraints 
alter table IS_EXCHANGE_DATA_TYPE
  add primary key (UUID)
  ;
-- Create/Recreate indexes 
create index IS_EDT_ID_IDX on IS_EXCHANGE_DATA_TYPE (ID)
;

drop table IS_EXCHANGE_DATA_TRANSFORM;
-- Create table
create table IS_EXCHANGE_DATA_TRANSFORM
(
  UUID           VARCHAR2(255 CHAR) not null,
  CREATE_TIME    TIMESTAMP(6),
  CREATOR        VARCHAR2(255 CHAR),
  MODIFIER       VARCHAR2(255 CHAR),
  MODIFY_TIME    TIMESTAMP(6),
  REC_VER        NUMBER(10),
  CODE           VARCHAR2(255 CHAR),
  DESTINATION_ID VARCHAR2(255 CHAR),
  ID             VARCHAR2(255 CHAR),
  NAME           VARCHAR2(255 CHAR),
  SOURCE_ID      VARCHAR2(255 CHAR),
  XSL            VARCHAR2(255 CHAR),
  IS_SYN_BACK    VARCHAR2(255 CHAR)
);
-- Create/Recreate primary, unique and foreign key constraints 
alter table IS_EXCHANGE_DATA_TRANSFORM
  add primary key (UUID);
  
drop table IS_EXCHANGE_DATA_ROUTE;  
  -- Create table
create table IS_EXCHANGE_DATA_ROUTE
(
  UUID        VARCHAR2(255 CHAR) not null,
  CREATE_TIME TIMESTAMP(6),
  CREATOR     VARCHAR2(255 CHAR),
  MODIFIER    VARCHAR2(255 CHAR),
  MODIFY_TIME TIMESTAMP(6),
  REC_VER     NUMBER(10),
  DATA_ID     VARCHAR2(255 CHAR),
  MSG         VARCHAR2(255 CHAR),
  STATUS      NUMBER(1),
  UNIT_ID     VARCHAR2(255 CHAR),
  MATTER_ID   VARCHAR2(255 CHAR),
  IS_SYN_BACK VARCHAR2(255 CHAR)
);
-- Create/Recreate primary, unique and foreign key constraints 
alter table IS_EXCHANGE_DATA_ROUTE
  add primary key (UUID)
  ;
  
drop table IS_EXCHANGE_DATA_REPLY;  
-- Create table
create table IS_EXCHANGE_DATA_REPLY
(
  UUID        VARCHAR2(255 CHAR) not null,
  CREATE_TIME TIMESTAMP(6),
  CREATOR     VARCHAR2(255 CHAR),
  MODIFIER    VARCHAR2(255 CHAR),
  MODIFY_TIME TIMESTAMP(6),
  REC_VER     NUMBER(10),
  DATA_ID     VARCHAR2(255 CHAR),
  MSG         VARCHAR2(255 CHAR),
  REPLY_TIME  TIMESTAMP(6),
  STATUS      NUMBER(1),
  UNIT_ID     VARCHAR2(255 CHAR),
  MATTER_ID   VARCHAR2(255 CHAR),
  IS_SYN_BACK VARCHAR2(255 CHAR)
);
-- Create/Recreate primary, unique and foreign key constraints 
alter table IS_EXCHANGE_DATA_REPLY
  add primary key (UUID)
  ;

drop table IS_EXCHANGE_DATA_REPEST;  
-- Create table
create table IS_EXCHANGE_DATA_REPEST
(
  UUID                       VARCHAR2(255 CHAR) not null,
  CREATE_TIME                TIMESTAMP(6),
  CREATOR                    VARCHAR2(255 CHAR),
  MODIFIER                   VARCHAR2(255 CHAR),
  MODIFY_TIME                TIMESTAMP(6),
  REC_VER                    NUMBER(10),
  BATCH_ID                   VARCHAR2(255 CHAR),
  CODE                       NUMBER(10),
  DATA_ID                    VARCHAR2(255 CHAR),
  DATA_REC_VER               NUMBER(10),
  EXCHANGE_DATA_MONITOR_UUID VARCHAR2(255 CHAR),
  FROM_ID                    VARCHAR2(255 CHAR),
  INTERVAL                   NUMBER(10),
  MSG                        VARCHAR2(255 CHAR),
  OP_TIME                    TIMESTAMP(6),
  RETRANSMISSION_NUM         NUMBER(10),
  SEND_METHOD                VARCHAR2(255 CHAR),
  SEND_NUM                   NUMBER(10),
  STATUS                     VARCHAR2(255 CHAR),
  SYSTEM_UUID                VARCHAR2(255 CHAR),
  UNIT_ID                    VARCHAR2(255 CHAR),
  USER_ID                    VARCHAR2(255 CHAR),
  TENANT_ID                  VARCHAR2(255 CHAR),
  MATTER_ID                  VARCHAR2(255 CHAR),
  IS_SYN_BACK                VARCHAR2(255 CHAR)
)
;
-- Create/Recreate primary, unique and foreign key constraints 
alter table IS_EXCHANGE_DATA_REPEST
  add primary key (UUID)
  ;
-- Create/Recreate indexes 
create index IED_DEFINATION_REP_FIELD1_IDX on IS_EXCHANGE_DATA_REPEST (CREATE_TIME, STATUS)
;

drop table IS_EXCHANGE_DATA_LOG;  
-- Create table
create table IS_EXCHANGE_DATA_LOG
(
  UUID         VARCHAR2(255 CHAR) not null,
  CREATE_TIME  TIMESTAMP(6),
  CREATOR      VARCHAR2(255 CHAR),
  MODIFIER     VARCHAR2(255 CHAR),
  MODIFY_TIME  TIMESTAMP(6),
  REC_VER      NUMBER(10),
  BATCH_ID     VARCHAR2(255 CHAR),
  DATA_ID      VARCHAR2(255 CHAR),
  MSG          VARCHAR2(255 CHAR),
  NODE         NUMBER(10),
  STATUS       NUMBER(1),
  UNIT_ID      VARCHAR2(255 CHAR),
  FROM_UNIT_ID VARCHAR2(255 CHAR),
  TO_UNIT_ID   VARCHAR2(255 CHAR),
  CODE         NUMBER(1),
  MATTER_ID    VARCHAR2(255 CHAR),
  DATA_REC_VER NUMBER(1),
  IS_SYN_BACK  VARCHAR2(255 CHAR)
)
;
-- Create/Recreate primary, unique and foreign key constraints 
alter table IS_EXCHANGE_DATA_LOG
  add primary key (UUID)
  ;
-- Create/Recreate indexes 
create index IED_DEFINATION_LOG_FIELD1_IDX on IS_EXCHANGE_DATA_LOG (CREATE_TIME)
;
create index IED_DEFINATION_LOG_FIELD2_IDX on IS_EXCHANGE_DATA_LOG (DATA_ID)
;

drop table IS_EXCHANGE_DATA_FILE_UPLOAD;  
-- Create table
create table IS_EXCHANGE_DATA_FILE_UPLOAD
(
  UUID               VARCHAR2(255 CHAR) not null,
  CREATE_TIME        TIMESTAMP(6),
  CREATOR            VARCHAR2(255 CHAR),
  MODIFIER           VARCHAR2(255 CHAR),
  MODIFY_TIME        TIMESTAMP(6),
  REC_VER            NUMBER(10),
  BATCH_ID           VARCHAR2(255 CHAR),
  BUSINESS_TYPE_ID   VARCHAR2(255 CHAR),
  BUSINESS_TYPE_NAME VARCHAR2(255 CHAR),
  CERTIFICATE        VARCHAR2(2000 CHAR),
  CONTENT_TYPE       VARCHAR2(255 CHAR),
  DEPARTMENT_ID      VARCHAR2(255 CHAR),
  DEPARTMENT_NAME    VARCHAR2(255 CHAR),
  DIGEST_ALGORITHM   VARCHAR2(255 CHAR),
  DIGEST_VALUE       VARCHAR2(255 CHAR),
  FILE_SIZE          NUMBER(19) not null,
  FILE_NAME          VARCHAR2(255 CHAR),
  SIGN_UPLOAD_FILE   NUMBER(1) not null,
  SIGNATURE_VALUE    VARCHAR2(255 CHAR),
  TYPE_ID            VARCHAR2(255 CHAR),
  TYPE_NAME          VARCHAR2(255 CHAR),
  UNIT_ID            VARCHAR2(255 CHAR),
  UNIT_NAME          VARCHAR2(255 CHAR),
  UPLOAD_TIME        TIMESTAMP(6),
  USER_ID            VARCHAR2(255 CHAR),
  USER_NAME          VARCHAR2(255 CHAR),
  FILE_ID            VARCHAR2(255 CHAR),
  IS_SYN_BACK        VARCHAR2(255 CHAR)
);
-- Create/Recreate primary, unique and foreign key constraints 
alter table IS_EXCHANGE_DATA_FILE_UPLOAD
  add primary key (UUID)
;

drop table IS_EXCHANGE_DATA_CALLBACK;  
-- Create table
create table IS_EXCHANGE_DATA_CALLBACK
(
  UUID        VARCHAR2(255 CHAR) not null,
  CREATE_TIME TIMESTAMP(6),
  CREATOR     VARCHAR2(255 CHAR),
  MODIFIER    VARCHAR2(255 CHAR),
  MODIFY_TIME TIMESTAMP(6),
  REC_VER     NUMBER(10),
  DATA_ID     VARCHAR2(255 CHAR),
  MSG         VARCHAR2(255 CHAR),
  STATUS      NUMBER(1),
  UNIT_ID     VARCHAR2(255 CHAR),
  IS_SYN_BACK VARCHAR2(255 CHAR)
)
;
-- Create/Recreate primary, unique and foreign key constraints 
alter table IS_EXCHANGE_DATA_CALLBACK
  add primary key (UUID)
  ;
  drop table IS_DX_EXCHANGE_DATA;  
drop table IS_DX_EXCHANGE_BATCH;  

-- Create table
create table IS_DX_EXCHANGE_BATCH
(
  UUID            VARCHAR2(255 CHAR) not null,
  CREATE_TIME     TIMESTAMP(6),
  CREATOR         VARCHAR2(255 CHAR),
  MODIFIER        VARCHAR2(255 CHAR),
  MODIFY_TIME     TIMESTAMP(6),
  REC_VER         NUMBER(10),
  ARROW           NUMBER(10),
  BCC             VARCHAR2(1000 CHAR),
  CC              VARCHAR2(1000 CHAR),
  FROM_ID         VARCHAR2(255 CHAR),
  FROM_USER       VARCHAR2(255 CHAR),
  ID              VARCHAR2(255 CHAR),
  PARAMS          VARCHAR2(255 CHAR),
  TO_ID           VARCHAR2(2500 CHAR),
  TYPE_ID         VARCHAR2(255 CHAR),
  SOURCE_BATCH_ID VARCHAR2(255 CHAR),
  IS_SYN_BACK     VARCHAR2(255 CHAR)
)
;
-- Create/Recreate primary, unique and foreign key constraints 
alter table IS_DX_EXCHANGE_BATCH
  add primary key (UUID)
  ;
-- Create/Recreate indexes 
create index INDEX_EB_01 on IS_DX_EXCHANGE_BATCH (ID)
;


-- Create table
create table IS_DX_EXCHANGE_DATA
(
  UUID             VARCHAR2(255 CHAR) not null,
  CREATE_TIME      TIMESTAMP(6),
  CREATOR          VARCHAR2(255 CHAR),
  MODIFIER         VARCHAR2(255 CHAR),
  MODIFY_TIME      TIMESTAMP(6),
  REC_VER          NUMBER(10),
  DATA_ID          VARCHAR2(255 CHAR),
  DATA_REC_VER     NUMBER(10),
  FORM_DATA_UUID   VARCHAR2(3999 CHAR),
  FORM_UUID        VARCHAR2(255 CHAR),
  PARAMS           VARCHAR2(255 CHAR),
  SIGN_DIGEST      VARCHAR2(255 CHAR),
  SOURCE_DATA_UUID VARCHAR2(255 CHAR),
  TEXT             CLOB,
  VALID_DATA       VARCHAR2(255 CHAR),
  VALID_MSG        VARCHAR2(255 CHAR),
  BATCH_UUID       VARCHAR2(255 CHAR),
  IS_SYN_BACK      VARCHAR2(255 CHAR)
)
;
-- Create/Recreate primary, unique and foreign key constraints 
alter table IS_DX_EXCHANGE_DATA
  add primary key (UUID)
  ;
alter table IS_DX_EXCHANGE_DATA
  add constraint FK_9QPFDIFWL4FDTIW5V1WE7IDW7 foreign key (BATCH_UUID)
  references IS_DX_EXCHANGE_BATCH (UUID)
  disable;
-- Create/Recreate indexes 
create index INDEX_ED_01 on IS_DX_EXCHANGE_DATA (BATCH_UUID)
;
create index INDEX_ED_02 on IS_DX_EXCHANGE_DATA (DATA_ID, DATA_REC_VER)
;

drop table IS_DX_EXCHANGE_ROUTE;
-- Create table
create table IS_DX_EXCHANGE_ROUTE
(
  UUID               VARCHAR2(255 CHAR) not null,
  CREATE_TIME        TIMESTAMP(6),
  CREATOR            VARCHAR2(255 CHAR),
  MODIFIER           VARCHAR2(255 CHAR),
  MODIFY_TIME        TIMESTAMP(6),
  REC_VER            NUMBER(10),
  INTERVAL           NUMBER(10),
  RECEIVE_STATUS     VARCHAR2(255 CHAR),
  RECEIVE_TIME       TIMESTAMP(6),
  RETRANSMISSION_NUM NUMBER(10),
  ROUTE_MSG          VARCHAR2(255 CHAR),
  SEND_NUM           NUMBER(10),
  SYSTEM_ID          VARCHAR2(255 CHAR),
  TYPE               VARCHAR2(255 CHAR),
  UNIT_ID            VARCHAR2(255 CHAR),
  DATA_UUID          VARCHAR2(255 CHAR),
  TEMP               VARCHAR2(255 CHAR),
  IS_SYN_BACK        VARCHAR2(255 CHAR)
)
;
-- Create/Recreate primary, unique and foreign key constraints 
alter table IS_DX_EXCHANGE_ROUTE
  add primary key (UUID)
  ;
  
drop table IS_EXCHANGE_ROUTE;
-- Create table
create table IS_EXCHANGE_ROUTE
(
  UUID               VARCHAR2(255 CHAR) not null,
  CREATE_TIME        TIMESTAMP(6),
  CREATOR            VARCHAR2(255 CHAR),
  MODIFIER           VARCHAR2(255 CHAR),
  MODIFY_TIME        TIMESTAMP(6),
  REC_VER            NUMBER(10),
  CODE               VARCHAR2(255 CHAR),
  FROM_TYPE_ID       VARCHAR2(255 CHAR),
  ID                 VARCHAR2(255 CHAR),
  INTERVAL           NUMBER(10),
  NAME               VARCHAR2(255 CHAR),
  RESTRAIN           VARCHAR2(255 CHAR),
  RETRANSMISSION_NUM NUMBER(10),
  TO_ID              VARCHAR2(255 CHAR),
  TRANSFORM_ID       VARCHAR2(255 CHAR),
  TO_FIELD           VARCHAR2(255 CHAR),
  TO_TYPE            VARCHAR2(255 CHAR),
  TO_EMPTY           VARCHAR2(255 CHAR),
  IS_SYN_BACK        VARCHAR2(255 CHAR)
);
-- Create/Recreate primary, unique and foreign key constraints 
alter table IS_EXCHANGE_ROUTE
  add primary key (UUID)
  ;
  drop table IS_EXCHANGE_DATA_MONITOR;
drop table IS_EXCHANGE_SEND_MONITOR;
drop table IS_EXCHANGE_DATA;  
drop table IS_EXCHANGE_DATA_BATCH;
-- Create table
create table IS_EXCHANGE_DATA_BATCH
(
  UUID           VARCHAR2(255 CHAR) not null,
  CREATE_TIME    TIMESTAMP(6),
  CREATOR        VARCHAR2(255 CHAR),
  MODIFIER       VARCHAR2(255 CHAR),
  MODIFY_TIME    TIMESTAMP(6),
  REC_VER        NUMBER(10),
  BCC            VARCHAR2(1000 CHAR),
  CC             VARCHAR2(1000 CHAR),
  FROM_ID        VARCHAR2(255 CHAR),
  ID             VARCHAR2(255 CHAR),
  OPERATE_SOURCE NUMBER(10),
  TO_ID          VARCHAR2(2500 CHAR),
  TYPE_ID        VARCHAR2(255 CHAR),
  RECEIVE_LIMIT  TIMESTAMP(6),
  FROM_USER      VARCHAR2(255 CHAR),
  IS_SYN_BACK    VARCHAR2(255 CHAR)
)
;
-- Create/Recreate primary, unique and foreign key constraints 
alter table IS_EXCHANGE_DATA_BATCH
  add primary key (UUID)
  ;
  

-- Create table
create table IS_EXCHANGE_DATA
(
  UUID                VARCHAR2(255 CHAR) not null,
  CREATE_TIME         TIMESTAMP(6),
  CREATOR             VARCHAR2(255 CHAR),
  MODIFIER            VARCHAR2(255 CHAR),
  MODIFY_TIME         TIMESTAMP(6),
  REC_VER             NUMBER(10),
  CORRELATION_DATA_ID VARCHAR2(255 CHAR),
  CORRELATION_RECVER  NUMBER(10),
  DATA_ID             VARCHAR2(255 CHAR),
  FORM_DATA_ID        VARCHAR2(255 CHAR),
  RESERVED_NUMBER1    VARCHAR2(255 CHAR),
  RESERVED_NUMBER2    VARCHAR2(255 CHAR),
  RESERVED_TEXT1      VARCHAR2(255 CHAR),
  RESERVED_TEXT2      VARCHAR2(255 CHAR),
  RESERVED_TEXT3      VARCHAR2(255 CHAR),
  SIGN_DIGEST         VARCHAR2(255 CHAR),
  TEXT                CLOB,
  BATCH_ID            VARCHAR2(255 CHAR),
  UPLOAD_LIMIT_NUM    NUMBER(10),
  NEWEST_DATA         VARCHAR2(255 CHAR),
  DATA_REC_VER        NUMBER(10),
  EXAMINE_FAIL_MSG    VARCHAR2(255 CHAR),
  VALID_DATA          VARCHAR2(255 CHAR),
  RELEASE_TIME        TIMESTAMP(6),
  MATTER_ID           VARCHAR2(255 CHAR),
  FORM_ID             VARCHAR2(255 CHAR),
  RESERVED_TEXT4      VARCHAR2(255 CHAR),
  RESERVED_TEXT5      VARCHAR2(255 CHAR),
  RESERVED_TEXT6      VARCHAR2(255 CHAR),
  DRAFTER             VARCHAR2(255 CHAR),
  DRAFT_TIME          TIMESTAMP(6),
  RELEASER            VARCHAR2(255 CHAR),
  CONTENT_STATUS      VARCHAR2(255 CHAR),
  HISTORY_DATA        NUMBER(1),
  IS_SYN_BACK         VARCHAR2(255 CHAR)
)
;
-- Create/Recreate primary, unique and foreign key constraints 
alter table IS_EXCHANGE_DATA
  add primary key (UUID)
  ;
alter table IS_EXCHANGE_DATA
  add constraint FK60505F1B15DB0FD foreign key (BATCH_ID)
  references IS_EXCHANGE_DATA_BATCH (UUID);
-- Create/Recreate indexes 
create index IED_DEFINATION_BATCH2_IDX on IS_EXCHANGE_DATA (RELEASE_TIME, VALID_DATA, NEWEST_DATA, BATCH_ID)
;
create index IED_DEFINATION_BATCH_IDX on IS_EXCHANGE_DATA (VALID_DATA, UPLOAD_LIMIT_NUM, BATCH_ID, NEWEST_DATA, DATA_ID)
;
create index IED_DEFINATION_FIELD1_IDX on IS_EXCHANGE_DATA (BATCH_ID)
;
create index IED_DEFINATION_FIELD2_IDX on IS_EXCHANGE_DATA (RESERVED_NUMBER2)
;


-- Create table
create table IS_EXCHANGE_SEND_MONITOR
(
  UUID           VARCHAR2(255 CHAR) not null,
  CREATE_TIME    TIMESTAMP(6),
  CREATOR        VARCHAR2(255 CHAR),
  MODIFIER       VARCHAR2(255 CHAR),
  MODIFY_TIME    TIMESTAMP(6),
  REC_VER        NUMBER(10),
  FROM_ID        VARCHAR2(255 CHAR),
  LIMIT_TIME     TIMESTAMP(6),
  SEND_LIMIT_NUM NUMBER(10),
  SEND_MSG       VARCHAR2(255 CHAR),
  SEND_NODE      VARCHAR2(255 CHAR),
  SEND_TIME      TIMESTAMP(6),
  SEND_TYPE      VARCHAR2(255 CHAR),
  SEND_USER      VARCHAR2(255 CHAR),
  DATA_UUID      VARCHAR2(255 CHAR),
  IS_SYN_BACK    VARCHAR2(255 CHAR)
)
;
-- Create/Recreate primary, unique and foreign key constraints 
alter table IS_EXCHANGE_SEND_MONITOR
  add primary key (UUID)
  ;
alter table IS_EXCHANGE_SEND_MONITOR
  add constraint FK_JIADUG73QLIF7FW67EKKDIS9P foreign key (DATA_UUID)
  references IS_EXCHANGE_DATA (UUID);
-- Create/Recreate indexes 
create index IESM_DEFINATION_BATCH_IDX on IS_EXCHANGE_SEND_MONITOR (FROM_ID, DATA_UUID, SEND_NODE, CREATOR, SEND_TIME, MODIFY_TIME)
;


-- Create table
create table IS_EXCHANGE_DATA_MONITOR
(
  UUID               VARCHAR2(255 CHAR) not null,
  CREATE_TIME        TIMESTAMP(6),
  CREATOR            VARCHAR2(255 CHAR),
  MODIFIER           VARCHAR2(255 CHAR),
  MODIFY_TIME        TIMESTAMP(6),
  REC_VER            NUMBER(10),
  FORM_DATA_UUID     VARCHAR2(255 CHAR),
  RECEIVE_STATUS     VARCHAR2(255 CHAR),
  RECEIVE_TIME       TIMESTAMP(6),
  REPLY_STATUS       VARCHAR2(255 CHAR),
  REPLY_TIME         TIMESTAMP(6),
  UNIT_ID            VARCHAR2(255 CHAR),
  XML                CLOB,
  DATA_ID            VARCHAR2(255 CHAR),
  CANCEL_MSG         VARCHAR2(255 CHAR),
  CANCEL_STATUS      VARCHAR2(255 CHAR),
  CANCEL_TIME        TIMESTAMP(6),
  FORM_ID            VARCHAR2(255 CHAR),
  REPLY_MSG          VARCHAR2(255 CHAR),
  ROUTE_MSG          VARCHAR2(255 CHAR),
  SYSTEM_ID          VARCHAR2(255 CHAR),
  REPLY_LIMIT_NUM    NUMBER(10),
  REPLY_USER         VARCHAR2(255 CHAR),
  CANCEL_USER        VARCHAR2(255 CHAR),
  RECEIVE_NODE       VARCHAR2(255 CHAR),
  RETRANSMISSION_NUM NUMBER(10),
  INTERVAL           NUMBER(10),
  SEND_NUM           NUMBER(10),
  CANCEL_REQUEST     VARCHAR2(255 CHAR),
  MATTER             VARCHAR2(255 CHAR),
  CERTIFICATE_STATUS NUMBER(10),
  SEND_ID            VARCHAR2(255 CHAR),
  MATTER_ID          VARCHAR2(255 CHAR),
  UNIT_TYPE          VARCHAR2(255 CHAR),
  IS_SYN_BACK        VARCHAR2(255 CHAR)
)
;
-- Create/Recreate primary, unique and foreign key constraints 
alter table IS_EXCHANGE_DATA_MONITOR
  add primary key (UUID)
  ;
alter table IS_EXCHANGE_DATA_MONITOR
  add constraint FKF1DCE90C40F77E4D foreign key (DATA_ID)
  references IS_EXCHANGE_DATA (UUID);
-- Create/Recreate indexes 
create index IEDM_DEFINATION_BATCH_IDX on IS_EXCHANGE_DATA_MONITOR (UNIT_ID, RECEIVE_NODE, CREATE_TIME, SEND_ID, REPLY_LIMIT_NUM, RECEIVE_TIME, FORM_ID, MATTER_ID)
;

