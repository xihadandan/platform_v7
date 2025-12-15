-- Create table
create table `WF_TASK_SUB_FLOW_DISPATCH`
(
  `UUID` varchar(50) COLLATE utf8_bin primary key COMMENT 'UUID，系统字段',
  `CREATE_TIME` datetime DEFAULT NULL COMMENT '创建时间',
  `CREATOR` varchar(50) COLLATE utf8_bin DEFAULT NULL COMMENT '创建人',
  `MODIFIER` varchar(50) COLLATE utf8_bin DEFAULT NULL COMMENT '修改人',
  `MODIFY_TIME` datetime DEFAULT NULL COMMENT '修改时间',
  `REC_VER` decimal(10,0) DEFAULT NULL COMMENT '版本号',
  `PARENT_TASK_INST_UUID`  varchar(50) COLLATE utf8_bin DEFAULT NULL COMMENT '父环节实例UUID',
  `PARENT_FLOW_INST_UUID`  varchar(50) COLLATE utf8_bin DEFAULT NULL COMMENT '父流程实例UUID',
  `FLOW_INST_UUID`         varchar(50) COLLATE utf8_bin DEFAULT NULL COMMENT '子流程实例UUID',
  `TASK_SUB_FLOW_UUID`     varchar(50) COLLATE utf8_bin DEFAULT NULL COMMENT '子流程UUID',
  `NEW_FLOW_ID`            varchar(50) COLLATE utf8_bin DEFAULT NULL COMMENT '子流程ID',
  `TASK_USERS`             varchar(4000) COLLATE utf8_bin DEFAULT NULL COMMENT '办理人',
  `LOG_ADD_SUBFLOW`        decimal(1,0) DEFAULT NULL COMMENT '记录添加子流程日志',
  `COMPLETION_STATE`       decimal(10,0) DEFAULT 0 COMMENT '分发状态 0分发中、1分发成功、2分发失败',
  `RESULT_MSG`             varchar(500) COLLATE utf8_bin DEFAULT NULL COMMENT '分发结果信息'
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin ROW_FORMAT=DYNAMIC COMMENT='子流程分发';
