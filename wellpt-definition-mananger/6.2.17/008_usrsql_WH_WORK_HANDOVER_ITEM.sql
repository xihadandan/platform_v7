create table WH_WORK_HANDOVER_ITEM 
(
   UUID                 VARCHAR2(255 BYTE)   not null,
   CREATE_TIME          TIMESTAMP,
   CREATOR              VARCHAR2(255 BYTE),
   MODIFIER             VARCHAR2(255 BYTE),
   MODIFY_TIME          TIMESTAMP,
   REC_VER              NUMBER(10,2),
   WH_WORK_HANDOVER_UUID VARCHAR2(255 BYTE),
   DATA_UUID            VARCHAR2(255 BYTE),
   HANDOVER_ITEM_STATUS NUMBER(1),
   HANDOVER_ITEM_TYPE   CHAR(10),
   constraint PK_WH_WORK_HANDOVER_ITEM primary key (UUID)
);

comment on column WH_WORK_HANDOVER_ITEM.UUID is
'唯一主键';

comment on column WH_WORK_HANDOVER_ITEM.CREATE_TIME is
'创建时间(操作时间)';

comment on column WH_WORK_HANDOVER_ITEM.CREATOR is
'创建人';

comment on column WH_WORK_HANDOVER_ITEM.MODIFIER is
'更新人';

comment on column WH_WORK_HANDOVER_ITEM.MODIFY_TIME is
'更新时间';

comment on column WH_WORK_HANDOVER_ITEM.REC_VER is
'数据版本';

comment on column WH_WORK_HANDOVER_ITEM.DATA_UUID is
'工作类型是流程的话，值为taskInstUuid';

comment on column WH_WORK_HANDOVER_ITEM.HANDOVER_ITEM_STATUS is
'HandoverItemStatusEnum 0待交接；1交接成功；2交接失败';

comment on column WH_WORK_HANDOVER_ITEM.HANDOVER_ITEM_TYPE is
'HandoverItemTypeEnum  待办：TODO；已办：DONE;监控：MONITOR;督办：SUPERVISE；查阅：CONSULT
';