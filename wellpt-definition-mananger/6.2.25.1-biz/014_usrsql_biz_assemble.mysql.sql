CREATE TABLE `biz_process_assemble`
(
    `UUID`             bigint primary key COMMENT 'UUID，系统字段',
    `CREATE_TIME`      datetime                     DEFAULT NULL COMMENT '创建时间',
    `CREATOR`          varchar(50) COLLATE utf8_bin DEFAULT NULL COMMENT '创建人',
    `MODIFIER`         varchar(50) COLLATE utf8_bin DEFAULT NULL COMMENT '修改人',
    `MODIFY_TIME`      datetime                     DEFAULT NULL COMMENT '修改时间',
    `REC_VER`          int(11)                      DEFAULT NULL COMMENT '版本号',
    `SYSTEM`           varchar(64) COLLATE utf8_bin DEFAULT NULL COMMENT '归属系统',
    `TENANT`           varchar(64) COLLATE utf8_bin DEFAULT NULL COMMENT '归属租户',
    `PROCESS_DEF_UUID` varchar(50) COLLATE utf8_bin DEFAULT NULL COMMENT '业务流程定义UUID',
    `DEFINITION_JSON`  longtext COLLATE utf8_bin    DEFAULT NULL COMMENT '组装定义JSON信息',
    PRIMARY KEY (`UUID`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8
  COLLATE = utf8_bin
  ROW_FORMAT = DYNAMIC COMMENT ='业务流程组装信息表';