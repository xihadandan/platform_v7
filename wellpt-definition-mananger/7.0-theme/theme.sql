create table THEME_SPECIFICATION 
(
   UUID                 NUMBER(19)           not null,
   VERSION              NUMBER(6,2),
   ENABLED              NUMBER(1)            default 0,
   REMARK               CLOB,
   SOURCE_UUID          NUMBER(19),
   DEF_JSON             CLOB,
   CREATE_TIME          TIMESTAMP,
   CREATOR              VARCHAR2(64),
   MODIFIER             VARCHAR2(64),
   MODIFY_TIME          TIMESTAMP,
   REC_VER              FLOAT(3)             default 1,
   constraint PK_THEME_SPECIFICATION primary key (UUID)
);

comment on table THEME_SPECIFICATION is
'主题规范';



create table THEME_PACK 
(
   UUID                 NUMBER(19)           not null,
   NAME                 VARCHAR(64),
   REMARK               VARCHAR(300),
   SPECIFY_UUID         NUMBER(19),
   LOGO                 CLOB,
   THUMBNAIL            CLOB,
   TYPE                 NUMBER(1)            default 0,
   STATUS               NUMBER(1)            default 0 not null,
   DEF_JSON             CLOB,
   CREATE_TIME          TIMESTAMP,
   CREATOR              VARCHAR2(64),
   MODIFIER             VARCHAR2(64),
   MODIFY_TIME          TIMESTAMP,
   REC_VER              FLOAT(3)             default 1,
   constraint PK_THEME_PACK primary key (UUID)
);

comment on table THEME_PACK is
'主题包';

comment on column THEME_PACK.TYPE is
'0 web端主题 1 手机端主题';

comment on column THEME_PACK.STATUS is
'0 未发布 1 已发布';



create table THEME_PACK_TAG 
(
   UUID                 NUMBER(19)           not null,
   PACK_UUID            NUMBER(19)           not null,
   TAG_UUID             NUMBER(19)           not null,
   CREATE_TIME          TIMESTAMP,
   CREATOR              VARCHAR2(64),
   MODIFIER             VARCHAR2(64),
   MODIFY_TIME          TIMESTAMP,
   REC_VER              FLOAT(3)             default 1,
   constraint PK_THEME_PACK_TAG primary key (UUID)
);

comment on table THEME_PACK_TAG is
'主题包标签';



create table THEME_TAG 
(
   UUID                 NUMBER(19)           not null,
   NAME                 VARCHAR(64)          not null,
   CREATE_TIME          TIMESTAMP,
   CREATOR              VARCHAR2(64),
   MODIFIER             VARCHAR2(64),
   MODIFY_TIME          TIMESTAMP,
   REC_VER              FLOAT(3)             default 1,
   constraint PK_THEME_TAG primary key (UUID)
);

comment on table THEME_TAG is
'主题包标签';
