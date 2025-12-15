-- 如果表存在，则执行删除操作，表不存在，drop不需要执行
drop table USER_EXTRA_INFO ;

create table USER_EXTRA_INFO 
(
   UUID                 VARCHAR2(255)        not null,
   CREATE_TIME          TIMESTAMP,
   CREATOR              VARCHAR2(255),
   MODIFIER             VARCHAR2(255 BYTE),
   MODIFY_TIME          TIMESTAMP,
   REC_VER              NUMBER(10,2),
   ACCOUNT_UUID         VARCHAR2(64),
   KEY_NAME             varchar2(255),
   DATA_TYPE            varchar2(20),
   VALUE_STRING         varchar2(1333),
   VALUE_DATE           timestamp,
   VALUE_INT            number(10)
);

comment on column USER_EXTRA_INFO.UUID is
'唯一主键';

comment on column USER_EXTRA_INFO.CREATE_TIME is
'创建时间';

comment on column USER_EXTRA_INFO.CREATOR is
'创建人';

comment on column USER_EXTRA_INFO.MODIFIER is
'更新人';

comment on column USER_EXTRA_INFO.MODIFY_TIME is
'更新时间';

comment on column USER_EXTRA_INFO.REC_VER is
'数据版本';

comment on column USER_EXTRA_INFO.KEY_NAME is
'GaspAffairsProcessExtendConstant.KEY_NAME';

comment on column USER_EXTRA_INFO.DATA_TYPE is
'静态变量，枚举类：EnumDataType
string date int';