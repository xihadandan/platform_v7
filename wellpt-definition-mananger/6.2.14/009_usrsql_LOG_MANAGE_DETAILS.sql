create table LOG_MANAGE_DETAILS 
(
   UUID                 VARCHAR2(255 CHAR)   not null,
   CREATE_TIME          TIMESTAMP(6),
   CREATOR              VARCHAR2(255 CHAR),
   MODIFIER             VARCHAR2(255 CHAR),
   MODIFY_TIME          TIMESTAMP(6),
   REC_VER              NUMBER(10),
   LOG_ID               VARCHAR2(255 CHAR),
   ATTR_ID              VARCHAR2(255 CHAR),
   ATTR_NAME            VARCHAR2(1024 CHAR),
   ATTR_TYPE            VARCHAR2(255 CHAR),
   DATA_SHOW_TYPE       VARCHAR2(10 CHAR),
   BEFORE_VALUE         VARCHAR2(255 CHAR),
   AFTER_VALUE          VARCHAR2(255 CHAR),
   constraint PK_LOG_MANAGE_DETAILS primary key (UUID)
);

comment on table LOG_MANAGE_DETAILS is
'管理操作日志详情';

comment on column LOG_MANAGE_DETAILS.CREATE_TIME is
'操作时间';

comment on column LOG_MANAGE_DETAILS.LOG_ID is
'明细日志id 对应管理操作日志的uuid';

comment on column LOG_MANAGE_DETAILS.ATTR_ID is
'属性id';

comment on column LOG_MANAGE_DETAILS.ATTR_NAME is
'属性名称';

comment on column LOG_MANAGE_DETAILS.ATTR_TYPE is
'属性类型';

comment on column LOG_MANAGE_DETAILS.DATA_SHOW_TYPE is
'前端数据展示类型：eg:文本，图片';

comment on column LOG_MANAGE_DETAILS.BEFORE_VALUE is
'操作前的数据值';

comment on column LOG_MANAGE_DETAILS.AFTER_VALUE is
'操作后的数据值';