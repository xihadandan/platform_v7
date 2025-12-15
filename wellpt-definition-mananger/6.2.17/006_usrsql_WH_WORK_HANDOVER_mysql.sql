create table WH_WORK_HANDOVER 
(
   UUID                 varchar(255)   not null comment '唯一主键',
   CREATE_TIME          datetime comment '创建时间(操作时间)',
   CREATOR              varchar(255) comment '创建人',
   MODIFIER             varchar(255) comment '更新人',
   MODIFY_TIME          datetime comment '更新时间',
   REC_VER              int comment '数据版本',
   HANDOVER_PERSON_ID   varchar(128) comment '交接人ID 单选',
   HANDOVER_PERSON_NAME varchar(128) comment '交接人名称 单选',
   RECEIVER_ID          varchar(128) comment '交接人ID 单选',
   RECEIVER_NAME        varchar(128) comment '交接人名称 单选',
   HANDOVER_WORK_TYPE   varchar(20) comment 'HandoverWorkTypeEnum  流程： flow; ',
   HANDOVER_WORK_TYPE_NAME varchar(20) comment '工作类型显示值',
   HANDOVER_CONTENTS_ID longtext ,
   HANDOVER_CONTENTS_NAME longtext ,
   HANDOVER_WORK_TIME_SETTING int comment 'HandoverworktimesettingEnum 1系统空闲时执行 ;2立即执行',
   NOTICE_HANDOVER_PERSON_FLAG int comment 'NoticeHandoverPersonFlagEnum 0:不通知；1通知',
   WORK_HANDOVER_STATUS int comment 'WorkHandoverStatusEnum 1未执行；2执行中；3已完成；',
   SYSTEM_UNIT_ID       varchar(255) comment '系统单位id',
   HANDOVER_WORK_TIME   datetime comment '任务实际开始执行的时间',
  primary key (uuid)
);