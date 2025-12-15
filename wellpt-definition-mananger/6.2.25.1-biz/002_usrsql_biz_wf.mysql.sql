-- Create table
create table `WF_FLOW_BUSINESS_DEFINITION`
(
    `UUID`            varchar(50) COLLATE utf8_bin primary key COMMENT 'UUID，系统字段',
    `CREATOR`         varchar(20) COLLATE utf8_bin  DEFAULT NULL COMMENT '创建人',
    `CREATE_TIME`     datetime                      DEFAULT NULL COMMENT '创建时间',
    `MODIFIER`        varchar(20) COLLATE utf8_bin  DEFAULT NULL COMMENT '修改人',
    `MODIFY_TIME`     datetime                      DEFAULT NULL COMMENT '修改时间',
    `REC_VER`         decimal(10, 0)                DEFAULT NULL COMMENT '版本号',
    `NAME`            varchar(100) COLLATE utf8_bin DEFAULT NULL COMMENT '名称',
    `ID`              varchar(50) COLLATE utf8_bin  DEFAULT NULL COMMENT 'ID',
    `CODE`            varchar(20) COLLATE utf8_bin  DEFAULT NULL COMMENT '编号',
    `CATEGORY_UUID`   varchar(50) COLLATE utf8_bin  DEFAULT NULL COMMENT '流程分类UUID',
    `FLOW_DEF_UUID`   varchar(50) COLLATE utf8_bin  DEFAULT NULL COMMENT '流程定义UUID',
    `FLOW_DEF_ID`     varchar(100) COLLATE utf8_bin DEFAULT NULL COMMENT '流程定义ID',
    `DEFINITION_JSON` CLOB                          DEFAULT NULL COMMENT '定义JSON信息',
    `LISTENER`        varchar(100) COLLATE utf8_bin DEFAULT NULL COMMENT '状态变更监听器',
    `REMARK`          varchar(200) COLLATE utf8_bin DEFAULT NULL COMMENT '备注'
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8
  COLLATE = utf8_bin
  ROW_FORMAT = DYNAMIC COMMENT ='流程业务定义';
