create table WH_FLOW_DATAS_RECORD 
(
   UUID                 varchar(255)   not null comment '唯一主键',
   CREATE_TIME          datetime comment '创建时间(操作时间)',
   CREATOR              varchar(255) comment '创建人',
   MODIFIER             varchar(255) comment '更新人',
   MODIFY_TIME          datetime comment '更新时间',
   REC_VER              int comment '数据版本',
   WH_WORK_HANDOVER_UUID varchar(255),
   TODO_COUNT           int comment '待办流程数量',
   DONE_COUNT          int comment '待办流程数量',
   MONITOR_COUNT        int comment '待办流程数量',
   SUPERVISE_COUNT      int comment '待办流程数量',
   CONSULT_COUNT        int comment '待办流程数量',
   primary key (UUID)
);