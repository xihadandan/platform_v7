create table LOG_MANAGE_OPERATION 
(
   UUID                 VARCHAR2(255 CHAR)   not null,
   CREATE_TIME          TIMESTAMP(6),
   CREATOR              VARCHAR2(255 CHAR),
   MODIFIER             VARCHAR2(255 CHAR),
   MODIFY_TIME          TIMESTAMP(6),
   REC_VER              NUMBER(10),
   SYSTEM_UNIT_ID       VARCHAR2(12),
   MODULE_ID            VARCHAR2(255 CHAR),
   MODULE_NAME          VARCHAR2(255 CHAR),
   DATA_TYPE_ID         VARCHAR2(2),
   DATA_TYPE            VARCHAR2(255 CHAR),
   DATA_ID              VARCHAR2(255 CHAR),
   BEFORE_DATA_NAME     VARCHAR2(1024 CHAR),
   AFTER_DATA_NAME      VARCHAR2(1024 CHAR),
   DATA_NAME_INFO       VARCHAR2(1024 CHAR),
   OPERATION            VARCHAR2(255 CHAR),
   USER_ID              VARCHAR2(64 CHAR),
   USER_NAME            VARCHAR2(64 CHAR),
   DATA_PARSE_TYPE      VARCHAR2(10),
   BEFORE_MESSAGE_VALUE CLOB,
   AFTER_MESSAGE_VALUE  CLOB,
    OPERATION_ID   VARCHAR2(20),
   constraint PK_LOG_MANAGE_OPERATION primary key (UUID)
);
comment on table LOG_MANAGE_OPERATION is
'管理操作日志';
comment on column LOG_MANAGE_OPERATION.SYSTEM_UNIT_ID is
'归属系统单位';

comment on column LOG_MANAGE_OPERATION.MODULE_ID is
'模块id';

comment on column LOG_MANAGE_OPERATION.MODULE_NAME is
'模块名称';

comment on column LOG_MANAGE_OPERATION.DATA_TYPE is
'数据类型：流程分类，流程定义';

comment on column LOG_MANAGE_OPERATION.DATA_ID is
'数据id';

comment on column LOG_MANAGE_OPERATION.BEFORE_DATA_NAME is
'操作前的数据名称';

comment on column LOG_MANAGE_OPERATION.AFTER_DATA_NAME is
'操作后的数据名称';

comment on column LOG_MANAGE_OPERATION.DATA_NAME_INFO is
'数据名称简介';

comment on column LOG_MANAGE_OPERATION.OPERATION is
'操作类型';

comment on column LOG_MANAGE_OPERATION.USER_ID is
'操作人id';

comment on column LOG_MANAGE_OPERATION.USER_NAME is
'操作人';

comment on column LOG_MANAGE_OPERATION.DATA_PARSE_TYPE is
'数据解析类型：entity;xml;json等';

comment on column LOG_MANAGE_OPERATION.BEFORE_MESSAGE_VALUE is
'报文操作前的数据值（不涉及搜索）';

comment on column LOG_MANAGE_OPERATION.AFTER_MESSAGE_VALUE is
'报文操作后的数据值（不涉及搜索）';