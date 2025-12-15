 
/*==============================================================*/
/* Table: API_LINK                                              */
/*==============================================================*/
create table API_LINK 
(
   UUID                 NUMBER(19)           not null,
   ID                   VARCHAR2(120 CHAR),
   NAME                 VARCHAR2(120 CHAR)   not null,
   REMARK               VARCHAR2(300 char),
   TEST_ENDPOINT        VARCHAR2(120 CHAR),
   DEV_ENDPOINT         VARCHAR2(120 CHAR),
   STAG_ENDPOINT        VARCHAR2(120 CHAR),
   ENDPOINT             VARCHAR2(120 CHAR),
   PROTOCOL             VARCHAR2(120 CHAR),
   ICON                 VARCHAR2(120 CHAR),
   AUTH_CONFIG          CLOB,
   FAULT_TOLERANCE_CONFIG CLOB,
   CREATE_TIME          TIMESTAMP,
   CREATOR              VARCHAR2(64),
   MODIFIER             VARCHAR2(64),
   MODIFY_TIME          TIMESTAMP,
   SYSTEM               VARCHAR2(64),
   TENANT               VARCHAR2(64),
   REC_VER              FLOAT(3)             default 1,
   constraint PK_API_LINK primary key (UUID)
);

comment on table API_LINK is
'API连接';

comment on column API_LINK.NAME is
'名称';

comment on column API_LINK.REMARK is
'描述';

comment on column API_LINK.TEST_ENDPOINT is
'测试环境端点';

comment on column API_LINK.DEV_ENDPOINT is
'开发环境端点';

comment on column API_LINK.STAG_ENDPOINT is
'预生产环境端点';

comment on column API_LINK.ENDPOINT is
'生产环境端点';

comment on column API_LINK.PROTOCOL is
'协议';

comment on column API_LINK.ICON is
'图标';

comment on column API_LINK.AUTH_CONFIG is
'授权配置';

comment on column API_LINK.FAULT_TOLERANCE_CONFIG is
'容错策略配置';


/*==============================================================*/
/* Table: API_OPERATION                                         */
/*==============================================================*/
create table API_OPERATION 
(
   UUID                 NUMBER(19)           not null,
   API_LINK_UUID        NUMBER(19)           not null,
   CODE                 VARCHAR2(120 CHAR),
   NAME                 VARCHAR2(120 CHAR)   not null,
   REMARK               VARCHAR2(300 char),
   PATH                 VARCHAR2(120 CHAR)   not null,
   METHOD               VARCHAR2(12 CHAR),
   RES_FORMAT_TYPE      VARCHAR2(32 CHAR),
   REQ_FORMAT_TYPE      VARCHAR2(32 CHAR),
   TIMEOUT              NUMBER(12),
   CREATE_TIME          TIMESTAMP,
   CREATOR              VARCHAR2(64),
   MODIFIER             VARCHAR2(64),
   MODIFY_TIME          TIMESTAMP,
   SYSTEM               VARCHAR2(64),
   TENANT               VARCHAR2(64),
   REC_VER              FLOAT(3)             default 1,
   constraint PK_API_OPERATION primary key (UUID)
);

comment on table API_OPERATION is
'API操作';

comment on column API_OPERATION.NAME is
'名称';

comment on column API_OPERATION.REMARK is
'描述';

comment on column API_OPERATION.PATH is
'路径';

comment on column API_OPERATION.METHOD is
'方法类型';

comment on column API_OPERATION.RES_FORMAT_TYPE is
'请求数据格式';

comment on column API_OPERATION.REQ_FORMAT_TYPE is
'请求数据格式';

comment on column API_OPERATION.TIMEOUT is
'超时时间(ms)';




/*==============================================================*/
/* Table: API_OPERATION_PARAM                                   */
/*==============================================================*/
create table API_OPERATION_PARAM 
(
   UUID                 NUMBER(19)           not null,
   OPERATION_UUID       NUMBER(19)           not null,
   NAME                 varchar2(64 char)    not null,
   REMARK               varchar2( 200 char),
   PARAM_TYPE           VARCHAR2(12 CHAR),
   DATA_TYPE            VARCHAR2(12 CHAR),
   DEFAULT_VALUE        VARCHAR2(300 CHAR),
   EXAMPLE_VALUE        VARCHAR2(300 CHAR),
   IS_REQUIRED          NUMBER(1)            default 0,
   CREATE_TIME          TIMESTAMP,
   CREATOR              VARCHAR2(64),
   MODIFIER             VARCHAR2(64),
   MODIFY_TIME          TIMESTAMP,
   REC_VER              FLOAT(3)             default 1,
   SYSTEM               VARCHAR2(64),
   TENANT               VARCHAR2(64),
   constraint PK_API_OPERATION_PARAM primary key (UUID)
);

comment on table API_OPERATION_PARAM is
'API操作参数';


create table API_OPERATION_BODY_SCHEMA 
(
   UUID                 NUMBER(19)           not null,
   OPERATION_UUID       NUMBER(19)           not null,
   APPLY_TO             VARCHAR2(12 CHAR),
   SCHEMA_CONFIG        CLOB                 not null,
   SCHEMA_DEFINITION    CLOB                 not null,
   EXAMPLE_BODY         CLOB,
   CREATE_TIME          TIMESTAMP,
   CREATOR              VARCHAR2(64),
   MODIFIER             VARCHAR2(64),
   MODIFY_TIME          TIMESTAMP,
   REC_VER              FLOAT(3)             default 1,
   SYSTEM               VARCHAR2(64),
   TENANT               VARCHAR2(64),
   constraint PK_API_BODY_SCHEMA primary key (UUID)
);

comment on table API_OPERATION_BODY_SCHEMA is
'API操作BODY结构';

 
/*==============================================================*/
/* Table: API_INVOKE_LOG                                        */
/*==============================================================*/
create table API_INVOKE_LOG 
(
   UUID                 NUMBER(19)           not null,
   API_LINK_UUID        NUMBER(19)           not null,
   API_OPERATION_UUID   NUMBER(19)           not null,
   INVOKE_URL           VARCHAR(600 CHAR)    not null,
   PROTOCOL             VARCHAR(12),
   PATH                 VARCHAR(120 CHAR),
   REQ_METHOD           VARCHAR(12),
   REQ_HEADERS          CLOB,
   REQ_QUERY_PARAMS     CLOB,
   REQ_BODY             CLOB,
   REQ_TIME             TIMESTAMP            not null,
   RES_TIME             TIMESTAMP,
   LATENCY              NUMBER(12),
   RES_STATUS           NUMBER(3),
   RES_BODY             CLOB,
   RES_HEADERS          CLOB,
   ERROR_MESSAGE        CLOB,
   CREATE_TIME          TIMESTAMP,
   CREATOR              VARCHAR2(64),
   MODIFIER             VARCHAR2(64),
   MODIFY_TIME          TIMESTAMP,
   SYSTEM               VARCHAR2(64),
   TENANT               VARCHAR2(64),
   REC_VER              FLOAT(3)             default 1,
   constraint PK_API_INVOKE_LOG primary key (UUID)
);

comment on table API_INVOKE_LOG is
'API调用日志';
