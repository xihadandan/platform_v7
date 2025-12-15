 
/*==============================================================*/
/* Table: AUDIT_DATA_LOG                                        */
/*==============================================================*/
create table AUDIT_DATA_LOG 
(
   UUID                 NUMBER(19)           not null,
   NAME                 VARCHAR2(120 CHAR)   not null,
   DATA_UUID            VARCHAR(240 CHAR)    not null,
   TABLE_NAME           VARCHAR(40 CHAR),
   MODIFIER_NAME        VARCHAR(120 CHAR),
   DATA_VER             FLOAT(3),
   CATEGORY             VARCHAR(120 CHAR),
   OPERATION            VARCHAR(120 CHAR),
   PARENT_UUID          VARCHAR(64 CHAR),
   IP                   VARCHAR(64),
   REMARK               VARCHAR2(300 char),
   CREATE_TIME          TIMESTAMP,
   CREATOR              VARCHAR2(64),
   MODIFIER             VARCHAR2(64),
   MODIFY_TIME          TIMESTAMP,
   SYSTEM               VARCHAR2(64),
   TENANT               VARCHAR2(64),
   REC_VER              FLOAT(3)             default 1,
   constraint PK_AUDIT_DATA_LOG primary key (UUID)
);

comment on table AUDIT_DATA_LOG is
'数据审计日志';

comment on column AUDIT_DATA_LOG.NAME is
'数据名';

comment on column AUDIT_DATA_LOG.DATA_UUID is
'数据UUID';

comment on column AUDIT_DATA_LOG.TABLE_NAME is
'修改表';

comment on column AUDIT_DATA_LOG.MODIFIER_NAME is
'修改者名字';

comment on column AUDIT_DATA_LOG.DATA_VER is
'数据版本';

comment on column AUDIT_DATA_LOG.OPERATION is
'操作描述';

comment on column AUDIT_DATA_LOG.REMARK is
'REMARK';



 
/*==============================================================*/
/* Table: AUDIT_DATA_ITEM_LOG                                   */
/*==============================================================*/
create table AUDIT_DATA_ITEM_LOG 
(
   UUID                 NUMBER(19)           not null,
   AUDIT_UUID           NUMBER(19)           not null,
   DATA_ITEM_NAME       VARCHAR(120 CHAR)    not null,
   DATA_ITEM_CODE       VARCHAR(64 CHAR),
   DATA_TYPE            VARCHAR(32),
   OLD_VALUE            CLOB,
   NEW_VALUE            CLOB,
   CREATE_TIME          TIMESTAMP,
   CREATOR              VARCHAR2(64),
   MODIFIER             VARCHAR2(64),
   MODIFY_TIME          TIMESTAMP,
   SYSTEM               VARCHAR2(64),
   TENANT               VARCHAR2(64),
   REC_VER              FLOAT(3)             default 1,
   constraint PK_AUDIT_DATA_ITEM_LOG primary key (UUID)
);

comment on table AUDIT_DATA_ITEM_LOG is
'数据审计数据项详情';
