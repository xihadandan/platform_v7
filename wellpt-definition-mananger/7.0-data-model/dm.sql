create table DATA_MODEL 
(
   UUID                 NUMBER(19)           not null,
   NAME                 VARCHAR2(64)         not null,
   ID                   VARCHAR2(27)         not null,
   TYPE                 NUMBER(1),
   REMARK               VARCHAR2(120),
   CREATE_TIME          TIMESTAMP,
   CREATOR              VARCHAR2(64),
   MODIFIER             VARCHAR2(64),
   MODIFY_TIME          TIMESTAMP,
   MODULE               VARCHAR2(64),
   SYSTEM               VARCHAR2(64),
   TENANT               VARCHAR2(64),
   REC_VER              FLOAT(3)             default 1,
   constraint PK_DATA_MODEL primary key (UUID)
);

comment on column DATA_MODEL.TYPE is
'0 : TABLE 1 : VIEW 2 : RELATION';

/*==============================================================*/
/* Index: "data_model_uniq_id"                                  */
/*==============================================================*/
create unique index "data_model_uniq_id" on DATA_MODEL (
   ID ASC
);



create table DATA_MODEL_DETAIL 
(
   UUID                 NUMBER(19)           not null,
   DATA_MODEL_UUID      NUMBER(19)           not null,
   ID                   VARCHAR(27)          not null,
   MODEL_JSON           CLOB,
   COLUMN_JSON          CLOB,
   RULE_JSON            CLOB,
   INDEX_JSON           CLOB,
   SQL                  CLOB,
   SQL_PARAMETER        CLOB,
   CREATE_TIME          TIMESTAMP,
   CREATOR              VARCHAR2(64),
   MODIFIER             VARCHAR2(64),
   MODIFY_TIME          TIMESTAMP,
   SYSTEM               VARCHAR2(64),
   TENANT               VARCHAR2(64),
   REC_VER              FLOAT(3)             default 1
);



create table DATA_MARK_TYPE 
(
   UUID                 NUMBER(19)           not null,
   DATA_UUID            VARCHAR2(64)         not null,
   TYPE                 NUMBER(1),
   CREATE_TIME          TIMESTAMP,
   CREATOR              VARCHAR2(64),
   MODIFIER             VARCHAR2(64),
   MODIFY_TIME          TIMESTAMP,
   MODULE               VARCHAR2(64),
   SYSTEM               VARCHAR2(64),
   TENANT               VARCHAR2(64),
   REC_VER              FLOAT(3)             default 1,
   constraint PK_DATA_MARK_TYPE primary key (UUID)
);

comment on table DATA_MARK_TYPE is
'数据类型标记';

comment on column DATA_MARK_TYPE.TYPE is
'0 : 固定数据 1 :样例数据';
