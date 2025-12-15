create table WH_WORK_HANDOVER_ITEM 
(
   UUID                 varchar(255)   not null comment '唯一主键',
   CREATE_TIME          datetime comment '创建时间(操作时间)',
   CREATOR              varchar(255) comment '创建人',
   MODIFIER             varchar(255) comment '更新人',
   MODIFY_TIME          datetime comment '更新时间',
   REC_VER              int comment '数据版本',
   WH_WORK_HANDOVER_UUID varchar(255),
   DATA_UUID            varchar(255) comment '工作类型是流程的话，值为taskInstUuid',
   HANDOVER_ITEM_STATUS int comment 'HandoverItemStatusEnum 0待交接；1交接成功；2交接失败',
   HANDOVER_ITEM_TYPE   varchar(10) comment 'HandoverItemTypeEnum  待办：TODO；已办：DONE;监控：MONITOR;督办：SUPERVISE；查阅：CONSULT',
   primary key (UUID)
);