create table WH_FLOW_DATAS_RECORD 
(
   UUID                 VARCHAR2(255 BYTE)   not null,
   CREATE_TIME          TIMESTAMP,
   CREATOR              VARCHAR2(255 BYTE),
   MODIFIER             VARCHAR2(255 BYTE),
   MODIFY_TIME          TIMESTAMP,
   REC_VER              NUMBER(10,2),
   WH_WORK_HANDOVER_UUID VARCHAR2(255 BYTE),
   TODO_COUNT           NUMBER(10),
   DONE_COUNT           NUMBER(10),
   MONITOR_COUNT        NUMBER(10),
   SUPERVISE_COUNT      NUMBER(10),
   CONSULT_COUNT        NUMBER(10),
   constraint PK_WH_FLOW_DATAS_RECORD primary key (UUID)
);

comment on column WH_FLOW_DATAS_RECORD.UUID is
'唯一主键';

comment on column WH_FLOW_DATAS_RECORD.CREATE_TIME is
'创建时间(操作时间)';

comment on column WH_FLOW_DATAS_RECORD.CREATOR is
'创建人';

comment on column WH_FLOW_DATAS_RECORD.MODIFIER is
'更新人';

comment on column WH_FLOW_DATAS_RECORD.MODIFY_TIME is
'更新时间';

comment on column WH_FLOW_DATAS_RECORD.REC_VER is
'数据版本';

comment on column WH_FLOW_DATAS_RECORD.TODO_COUNT is
'待办流程数量';

comment on column WH_FLOW_DATAS_RECORD.DONE_COUNT is
'已办流程数量';

comment on column WH_FLOW_DATAS_RECORD.MONITOR_COUNT is
'监控流程数量';

comment on column WH_FLOW_DATAS_RECORD.SUPERVISE_COUNT is
'督办流程数量';

comment on column WH_FLOW_DATAS_RECORD.CONSULT_COUNT is
'查阅流程数量';