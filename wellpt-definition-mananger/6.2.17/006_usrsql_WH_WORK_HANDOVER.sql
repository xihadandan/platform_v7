create table WH_WORK_HANDOVER 
(
   UUID                 VARCHAR2(255 BYTE)   not null,
   CREATE_TIME          TIMESTAMP,
   CREATOR              VARCHAR2(255 BYTE),
   MODIFIER             VARCHAR2(255 BYTE),
   MODIFY_TIME          TIMESTAMP,
   REC_VER              NUMBER(10,2),
   HANDOVER_PERSON_ID   VARCHAR2(128),
   HANDOVER_PERSON_NAME VARCHAR2(128),
   RECEIVER_ID          VARCHAR2(128),
   RECEIVER_NAME        VARCHAR2(128),
   HANDOVER_WORK_TYPE   VARCHAR2(20),
   HANDOVER_WORK_TYPE_NAME VARCHAR2(20),
   HANDOVER_CONTENTS_ID CLOB,
   HANDOVER_CONTENTS_NAME CLOB,
   HANDOVER_WORK_TIME_SETTING NUMBER(1),
   NOTICE_HANDOVER_PERSON_FLAG NUMBER(1),
   WORK_HANDOVER_STATUS NUMBER(1),
   SYSTEM_UNIT_ID       VARCHAR2(255 BYTE),
   HANDOVER_WORK_TIME   TIMESTAMP,
   constraint PK_WH_WORK_HANDOVER primary key (UUID)
);

comment on column WH_WORK_HANDOVER.UUID is
'唯一主键';

comment on column WH_WORK_HANDOVER.CREATE_TIME is
'创建时间(操作时间)';

comment on column WH_WORK_HANDOVER.CREATOR is
'创建人';

comment on column WH_WORK_HANDOVER.MODIFIER is
'更新人';

comment on column WH_WORK_HANDOVER.MODIFY_TIME is
'更新时间';

comment on column WH_WORK_HANDOVER.REC_VER is
'数据版本';

comment on column WH_WORK_HANDOVER.HANDOVER_PERSON_ID is
'交接人ID 单选';

comment on column WH_WORK_HANDOVER.HANDOVER_PERSON_NAME is
'交接人名称 单选';

comment on column WH_WORK_HANDOVER.RECEIVER_ID is
'接收人ID单选';

comment on column WH_WORK_HANDOVER.RECEIVER_NAME is
'接收人名称单选';

comment on column WH_WORK_HANDOVER.HANDOVER_WORK_TYPE is
'HandoverWorkTypeEnum  流程： flow; ';

comment on column WH_WORK_HANDOVER.HANDOVER_WORK_TYPE_NAME is
'工作类型显示值';

comment on column WH_WORK_HANDOVER.HANDOVER_WORK_TIME_SETTING is
'HandoverworktimesettingEnum 1系统空闲时执行 ;2立即执行';

comment on column WH_WORK_HANDOVER.NOTICE_HANDOVER_PERSON_FLAG is
'NoticeHandoverPersonFlagEnum 0:不通知；1通知';

comment on column WH_WORK_HANDOVER.WORK_HANDOVER_STATUS is
'WorkHandoverStatusEnum 1未执行；2执行中；3已完成；';

comment on column WH_WORK_HANDOVER.SYSTEM_UNIT_ID is
'系统单位id';

comment on column WH_WORK_HANDOVER.HANDOVER_WORK_TIME is
'任务实际开始执行的时间';
