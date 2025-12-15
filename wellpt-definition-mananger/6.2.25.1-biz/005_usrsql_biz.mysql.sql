create table `BIZ_PROCESS_NODE_DEFINITION`
(
    `UUID`             varchar(50) COLLATE utf8_bin primary key COMMENT 'UUID，系统字段',
    `CREATOR`          varchar(20) COLLATE utf8_bin   DEFAULT NULL COMMENT '创建人',
    `CREATE_TIME`      datetime                       DEFAULT NULL COMMENT '创建时间',
    `MODIFIER`         varchar(20) COLLATE utf8_bin   DEFAULT NULL COMMENT '修改人',
    `MODIFY_TIME`      datetime                       DEFAULT NULL COMMENT '修改时间',
    `REC_VER`          decimal(10, 0)                 DEFAULT NULL COMMENT '版本号',
    `NAME`             varchar(100) COLLATE utf8_bin  DEFAULT NULL COMMENT '过程节点名称',
    `ID`               varchar(50) COLLATE utf8_bin   DEFAULT NULL COMMENT '过程节点ID',
    `CODE`             varchar(50) COLLATE utf8_bin   DEFAULT NULL COMMENT '过程节点编号',
    `EXT_ATTRS_JSON`   varchar(4000) COLLATE utf8_bin DEFAULT NULL COMMENT '扩展定义JSON信息',
    `SORT_ORDER`       decimal(10, 0)                 DEFAULT NULL COMMENT '排序号',
    `PARENT_ID`        varchar(50) COLLATE utf8_bin   DEFAULT NULL COMMENT '上级过程节点ID',
    `PROCESS_DEF_UUID` varchar(50) COLLATE utf8_bin   DEFAULT NULL COMMENT '过程定义UUID'
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8
  COLLATE = utf8_bin
  ROW_FORMAT = DYNAMIC COMMENT ='业务流程过程节点定义基本信息';



create table `BIZ_PROCESS_ITEM_DEFINITION`
(
    `UUID`             varchar(50) COLLATE utf8_bin primary key COMMENT 'UUID，系统字段',
    `CREATOR`          varchar(20) COLLATE utf8_bin  DEFAULT NULL COMMENT '创建人',
    `CREATE_TIME`      datetime                      DEFAULT NULL COMMENT '创建时间',
    `MODIFIER`         varchar(20) COLLATE utf8_bin  DEFAULT NULL COMMENT '修改人',
    `MODIFY_TIME`      datetime                      DEFAULT NULL COMMENT '修改时间',
    `REC_VER`          decimal(10, 0)                DEFAULT NULL COMMENT '版本号',
    `ITEM_DEF_NAME`    varchar(100) COLLATE utf8_bin DEFAULT NULL COMMENT '事项定义名称',
    `ITEM_DEF_ID`      varchar(50) COLLATE utf8_bin  DEFAULT NULL COMMENT '事项定义ID',
    `ITEM_NAME`        varchar(512) COLLATE utf8_bin DEFAULT NULL COMMENT '事项名称',
    `ITEM_CODE`        varchar(50) COLLATE utf8_bin  DEFAULT NULL COMMENT '事项编码',
    `ITEM_ID`          varchar(50) COLLATE utf8_bin  DEFAULT NULL COMMENT '事项ID，业务流程事项配置自动生成的ID',
    `ITEM_TYPE`        varchar(10) COLLATE utf8_bin  DEFAULT NULL COMMENT '事项类型',
    `SORT_ORDER`       decimal(10, 0)                DEFAULT NULL COMMENT '排序号',
    `PROCESS_NODE_ID`  varchar(50) COLLATE utf8_bin  DEFAULT NULL COMMENT '过程节点ID',
    `PROCESS_DEF_UUID` varchar(50) COLLATE utf8_bin  DEFAULT NULL COMMENT '过程定义UUID'
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8
  COLLATE = utf8_bin
  ROW_FORMAT = DYNAMIC COMMENT ='业务流程事项定义基本信息';
