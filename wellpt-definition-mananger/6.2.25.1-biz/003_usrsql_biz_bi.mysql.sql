-- Create table
create table `BIZ_BUSINESS_INTEGRATION`
(
    `UUID`           varchar(50) COLLATE utf8_bin primary key COMMENT 'UUID，系统字段',
    `CREATOR`        varchar(20) COLLATE utf8_bin  DEFAULT NULL COMMENT '创建人',
    `CREATE_TIME`    datetime                      DEFAULT NULL COMMENT '创建时间',
    `MODIFIER`       varchar(20) COLLATE utf8_bin  DEFAULT NULL COMMENT '修改人',
    `MODIFY_TIME`    datetime                      DEFAULT NULL COMMENT '修改时间',
    `REC_VER`        decimal(10, 0)                DEFAULT NULL COMMENT '版本号',
    `NAME`           varchar(100) COLLATE utf8_bin DEFAULT NULL COMMENT '名称',
    `TYPE`           varchar(10) COLLATE utf8_bin  DEFAULT NULL COMMENT '业务集成类型，1工作流',
    `ITEM_INST_UUID` varchar(50) COLLATE utf8_bin  DEFAULT NULL COMMENT '事项实例UUID',
    `BIZ_INST_UUID`  varchar(50) COLLATE utf8_bin  DEFAULT NULL COMMENT '业务实例UUID'
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8
  COLLATE = utf8_bin
  ROW_FORMAT = DYNAMIC COMMENT ='业务集成';


create table `BIZ_MILESTONE`
(
    `UUID`           varchar(50) COLLATE utf8_bin primary key COMMENT 'UUID，系统字段',
    `CREATOR`        varchar(20) COLLATE utf8_bin  DEFAULT NULL COMMENT '创建人',
    `CREATE_TIME`    datetime                      DEFAULT NULL COMMENT '创建时间',
    `MODIFIER`       varchar(20) COLLATE utf8_bin  DEFAULT NULL COMMENT '修改人',
    `MODIFY_TIME`    datetime                      DEFAULT NULL COMMENT '修改时间',
    `REC_VER`        decimal(10, 0)                DEFAULT NULL COMMENT '版本号',
    `NAME`           varchar(100) COLLATE utf8_bin DEFAULT NULL COMMENT '里程碑事件名称',
    `ITEM_INST_UUID` varchar(50) COLLATE utf8_bin  DEFAULT NULL COMMENT '事项实例UUID',
    `REMARK`         varchar(500) COLLATE utf8_bin DEFAULT NULL COMMENT '里程碑事件描述'
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8
  COLLATE = utf8_bin
  ROW_FORMAT = DYNAMIC COMMENT ='里程碑事件';


create table `BIZ_MILESTONE_RESULT`
(
    `UUID`            varchar(50) COLLATE utf8_bin primary key COMMENT 'UUID，系统字段',
    `CREATOR`         varchar(20) COLLATE utf8_bin   DEFAULT NULL COMMENT '创建人',
    `CREATE_TIME`     datetime                       DEFAULT NULL COMMENT '创建时间',
    `MODIFIER`        varchar(20) COLLATE utf8_bin   DEFAULT NULL COMMENT '修改人',
    `MODIFY_TIME`     datetime                       DEFAULT NULL COMMENT '修改时间',
    `REC_VER`         decimal(10, 0)                 DEFAULT NULL COMMENT '版本号',
    `MILESTONE_UUID`  varchar(50) COLLATE utf8_bin   DEFAULT NULL COMMENT '里程碑事件UUID',
    `RESULT_TYPE`     varchar(10) COLLATE utf8_bin   DEFAULT NULL COMMENT '交付物类型，1结论，2附件',
    `CONTENT`         clob                           DEFAULT NULL COMMENT '结论内容',
    `REPO_FILE_UUIDS` varchar(2000) COLLATE utf8_bin DEFAULT NULL COMMENT '附件UUID列表，多个以分号隔开'
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8
  COLLATE = utf8_bin
  ROW_FORMAT = DYNAMIC COMMENT ='里程碑事件交付物';



create table `BIZ_DEFINITION_TEMPLATE`
(
    `UUID`             varchar(50) COLLATE utf8_bin primary key COMMENT 'UUID，系统字段',
    `CREATOR`          varchar(20) COLLATE utf8_bin  DEFAULT NULL COMMENT '创建人',
    `CREATE_TIME`      datetime                      DEFAULT NULL COMMENT '创建时间',
    `MODIFIER`         varchar(20) COLLATE utf8_bin  DEFAULT NULL COMMENT '修改人',
    `MODIFY_TIME`      datetime                      DEFAULT NULL COMMENT '修改时间',
    `REC_VER`          decimal(10, 0)                DEFAULT NULL COMMENT '版本号',
    `NAME`             varchar(100) COLLATE utf8_bin DEFAULT NULL COMMENT '模板名称',
    `TYPE`             varchar(10) COLLATE utf8_bin  DEFAULT NULL COMMENT '模板类型，10业务流程表单配置模板、20过程节点表单配置模板、30事项表单配置模板、40事项集成工作流配置模板',
    `PROCESS_DEF_UUID` varchar(50) COLLATE utf8_bin  DEFAULT NULL COMMENT '业务流程定义UUID',
    `DEFINITION_JSON`  clob                          DEFAULT NULL COMMENT '定义JSON信息'
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8
  COLLATE = utf8_bin
  ROW_FORMAT = DYNAMIC COMMENT ='业务流程配置项模板';