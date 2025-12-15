create table WH_WORK_TYPE_TO_HANDOVER 
(
   UUID                 varchar(255)   not null comment '唯一主键',
   CREATE_TIME          datetime comment '创建时间(操作时间)',
   CREATOR              varchar(255) comment '创建人',
   MODIFIER             varchar(255) comment '更新人',
   MODIFY_TIME          datetime comment '更新时间',
   REC_VER              int comment '数据版本',
   WH_WORK_HANDOVER_UUID varchar(255) ,
   HANDOVER_CONTENT_TYPE varchar(20) comment '交接内容类型 HandoverContentTypeEnum 待办流程:todo;查阅流程:consult;监控流程:monitor;已办流程:done;督办流程:supervise;',
   HANDOVER_CONTENT_TYPE_NAME varchar(20) comment '交接内容类型显示值',
   COMPLETED_FLOW_FLAG  int comment '是否含已办结流程 0代表不含 1代表含',
    primary key (UUID)
);