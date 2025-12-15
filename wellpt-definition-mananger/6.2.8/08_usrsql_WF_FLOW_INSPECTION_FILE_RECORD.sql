create table WF_FLOW_INSPECTION_FILE_RECORD 
(
   UUID                 VARCHAR2(255 char)   not null,
   CREATE_TIME          TIMESTAMP,
   CREATOR              VARCHAR2(255 char),
   MODIFIER             VARCHAR2(255 char),
   MODIFY_TIME          TIMESTAMP,
   REC_VER              NUMBER(10),
   FLOW_INST_UUID       VARCHAR2(64),
   FILE_UUID            VARCHAR2(64),
   INSPECTION_FILE_UUID VARCHAR2(64),
   INSPECTION_LOG       CLOB
);

comment on column WF_FLOW_INSPECTION_FILE_RECORD.UUID is
'UUID，系统字段';

comment on column WF_FLOW_INSPECTION_FILE_RECORD.CREATE_TIME is
'创建时间';

comment on column WF_FLOW_INSPECTION_FILE_RECORD.CREATOR is
'创建人';

comment on column WF_FLOW_INSPECTION_FILE_RECORD.MODIFIER is
'修改人';

comment on column WF_FLOW_INSPECTION_FILE_RECORD.MODIFY_TIME is
'修改时间';

comment on column WF_FLOW_INSPECTION_FILE_RECORD.REC_VER is
'版本号';

comment on column WF_FLOW_INSPECTION_FILE_RECORD.FLOW_INST_UUID is
'流程实例UUID';

comment on column WF_FLOW_INSPECTION_FILE_RECORD.FILE_UUID is
'原附件fileUuid';

comment on column WF_FLOW_INSPECTION_FILE_RECORD.INSPECTION_FILE_UUID is
'手写签批文件uuid';

comment on column WF_FLOW_INSPECTION_FILE_RECORD.INSPECTION_LOG is
'签批日志（json字符串结构）';