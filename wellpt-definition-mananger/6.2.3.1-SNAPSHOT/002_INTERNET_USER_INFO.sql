-- 如果表存在，则执行删除操作，表不存在，drop不需要执行
drop table INTERNET_USER_INFO ;

create table INTERNET_USER_INFO 
(
   UUID                 VARCHAR2(255)        not null,
   CREATE_TIME          TIMESTAMP,
   CREATOR              VARCHAR2(255),
   MODIFIER             VARCHAR2(255),
   MODIFY_TIME          TIMESTAMP,
   REC_VER              NUMBER(10,2),
   ACCOUNT_UUID         VARCHAR2(64),
   TYPE                 NUMBER(1),
   LICENSE_NUMBER       VARCHAR2(255),
   LICENSE_TYPE         VARCHAR2(64),
   UNIT_NAME            VARCHAR2(255)
);

comment on column INTERNET_USER_INFO.UUID is
'唯一主键';

comment on column INTERNET_USER_INFO.CREATE_TIME is
'创建时间';

comment on column INTERNET_USER_INFO.CREATOR is
'创建人';

comment on column INTERNET_USER_INFO.MODIFIER is
'更新人';

comment on column INTERNET_USER_INFO.MODIFY_TIME is
'更新时间';

comment on column INTERNET_USER_INFO.REC_VER is
'数据版本';

comment on column INTERNET_USER_INFO.TYPE is
'//用户类型：个人/法人
0 个人
1法人
';

comment on column INTERNET_USER_INFO.LICENSE_NUMBER is
'证照号码由业务决定，自己存自已取';

comment on column INTERNET_USER_INFO.LICENSE_TYPE is
'证照类型由业务决定，自己存自已取';