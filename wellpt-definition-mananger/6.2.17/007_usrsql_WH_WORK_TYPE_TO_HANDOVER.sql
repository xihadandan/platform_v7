create table WH_WORK_TYPE_TO_HANDOVER 
(
   UUID                 VARCHAR2(255 BYTE)   not null,
   CREATE_TIME          TIMESTAMP,
   CREATOR              VARCHAR2(255 BYTE),
   MODIFIER             VARCHAR2(255 BYTE),
   MODIFY_TIME          TIMESTAMP,
   REC_VER              NUMBER(10,2),
   WH_WORK_HANDOVER_UUID VARCHAR2(255 BYTE),
   HANDOVER_CONTENT_TYPE VARCHAR2(20),
   HANDOVER_CONTENT_TYPE_NAME VARCHAR2(20),
   COMPLETED_FLOW_FLAG  NUMBER(1),
   constraint PK_WH_WORK_TYPE_TO_HANDOVER primary key (UUID)
);

comment on column WH_WORK_TYPE_TO_HANDOVER.UUID is
'唯一主键';

comment on column WH_WORK_TYPE_TO_HANDOVER.CREATE_TIME is
'创建时间';

comment on column WH_WORK_TYPE_TO_HANDOVER.CREATOR is
'创建人';

comment on column WH_WORK_TYPE_TO_HANDOVER.MODIFIER is
'更新人';

comment on column WH_WORK_TYPE_TO_HANDOVER.MODIFY_TIME is
'更新时间';

comment on column WH_WORK_TYPE_TO_HANDOVER.REC_VER is
'数据版本';

comment on column WH_WORK_TYPE_TO_HANDOVER.HANDOVER_CONTENT_TYPE is
'交接内容类型 HandoverContentTypeEnum 待办流程:todo
	// ;查阅流程:consult;监控流程:monitor;已办流程:done;督办流程:supervise;';

comment on column WH_WORK_TYPE_TO_HANDOVER.HANDOVER_CONTENT_TYPE_NAME is
'交接内容类型显示值';

comment on column WH_WORK_TYPE_TO_HANDOVER.COMPLETED_FLOW_FLAG is
'是否含已办结流程 0代表不含 1代表含';