create table MULTI_ORG_GROUP_USER_RANGE 
(
   UUID                 VARCHAR2(100)        not null,
   CREATE_TIME          TIMESTAMP,
   CREATOR              VARCHAR2(100),
   MODIFIER             VARCHAR2(100),
   MODIFY_TIME          TIMESTAMP,
   REC_VER              NUMBER(10,2),
   GROUP_ID             VARCHAR2(100),
   USER_RANGE_DISPLAY   VARCHAR2(255),
   USER_RANGE_REAL      VARCHAR2(100),
   constraint PK_MULTI_ORG_GROUP_USER_RANGE primary key (UUID)
);

comment on table MULTI_ORG_GROUP_USER_RANGE is
'群组使用者信息';

comment on column MULTI_ORG_GROUP_USER_RANGE.UUID is
'唯一主键';

comment on column MULTI_ORG_GROUP_USER_RANGE.CREATE_TIME is
'创建时间';

comment on column MULTI_ORG_GROUP_USER_RANGE.CREATOR is
'创建人';

comment on column MULTI_ORG_GROUP_USER_RANGE.MODIFIER is
'更新人';

comment on column MULTI_ORG_GROUP_USER_RANGE.MODIFY_TIME is
'更新时间';

comment on column MULTI_ORG_GROUP_USER_RANGE.REC_VER is
'数据版本';

comment on column MULTI_ORG_GROUP_USER_RANGE.GROUP_ID is
'群组id';

comment on column MULTI_ORG_GROUP_USER_RANGE.USER_RANGE_DISPLAY is
'添加使用范围人员显示值字段';

comment on column MULTI_ORG_GROUP_USER_RANGE.USER_RANGE_REAL is
'添加使用范围人员真实值字段';