create table WH_WORK_SETTINGS 
(
   UUID                 VARCHAR2(255 BYTE)   not null,
   CREATE_TIME          TIMESTAMP,
   CREATOR              VARCHAR2(255 BYTE),
   MODIFIER             VARCHAR2(255 BYTE),
   MODIFY_TIME          TIMESTAMP,
   REC_VER              NUMBER(10,2),
   SYSTEM_UNIT_ID       VARCHAR2(255 BYTE),
   WORK_TIME            VARCHAR2(5),
   constraint PK_WH_WORK_SETTINGS primary key (UUID)
);

comment on column WH_WORK_SETTINGS.UUID is
'唯一主键';

comment on column WH_WORK_SETTINGS.CREATE_TIME is
'创建时间';

comment on column WH_WORK_SETTINGS.CREATOR is
'创建人';

comment on column WH_WORK_SETTINGS.MODIFIER is
'更新人';

comment on column WH_WORK_SETTINGS.MODIFY_TIME is
'更新时间';

comment on column WH_WORK_SETTINGS.REC_VER is
'数据版本';

comment on column WH_WORK_SETTINGS.SYSTEM_UNIT_ID is
'系统单位id';

comment on column WH_WORK_SETTINGS.WORK_TIME is
'格式 ：01:00';
