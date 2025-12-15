create table ORG_ELEMENT_I18N 
(
   UUID                 NUMBER(19)           not null,
   DATA_UUID            NUMBER(19)           not null,
   DATA_ID              VARCHAR(64),
   DATA_CODE            VARCHAR(30)          not null,
   LOCALE               VARCHAR(12),
   CONTENT              VARCHAR(300 CHAR),
   CREATE_TIME          TIMESTAMP,
   CREATOR              VARCHAR2(64),
   MODIFIER             VARCHAR2(64),
   MODIFY_TIME          TIMESTAMP,
   REC_VER              FLOAT(3)             default 1,
   SYSTEM               VARCHAR2(64),
   TENANT               VARCHAR2(64),
   constraint PK_ORG_ELEMENT_I18N primary key (UUID)
);

comment on table ORG_ELEMENT_I18N is
'组织元素国际化';

comment on column ORG_ELEMENT_I18N.DATA_UUID is
'编码';