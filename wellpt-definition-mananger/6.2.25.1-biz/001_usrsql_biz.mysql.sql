-- Create table
create table `BIZ_CATEGORY`
(
    `UUID`        varchar(50) COLLATE utf8_bin primary key COMMENT 'UUID，系统字段',
    `CREATOR`     varchar(20) COLLATE utf8_bin  DEFAULT NULL COMMENT '创建人',
    `CREATE_TIME` datetime                      DEFAULT NULL COMMENT '创建时间',
    `MODIFIER`    varchar(20) COLLATE utf8_bin  DEFAULT NULL COMMENT '修改人',
    `MODIFY_TIME` datetime                      DEFAULT NULL COMMENT '修改时间',
    `REC_VER`     decimal(10, 0)                DEFAULT NULL COMMENT '版本号',
    `NAME`        varchar(100) COLLATE utf8_bin DEFAULT NULL COMMENT '名称',
    `ID`          varchar(50) COLLATE utf8_bin  DEFAULT NULL COMMENT 'ID',
    `CODE`        varchar(20) COLLATE utf8_bin  DEFAULT NULL COMMENT '编号',
    `PARENT_UUID` varchar(50) COLLATE utf8_bin  DEFAULT NULL COMMENT '上级分类UUID',
    `REMARK`      varchar(200) COLLATE utf8_bin DEFAULT NULL COMMENT '备注'
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8
  COLLATE = utf8_bin
  ROW_FORMAT = DYNAMIC COMMENT ='业务流程分类';



create table `BIZ_BUSINESS`
(
    `UUID`          varchar(50) COLLATE utf8_bin primary key COMMENT 'UUID，系统字段',
    `CREATOR`       varchar(20) COLLATE utf8_bin  DEFAULT NULL COMMENT '创建人',
    `CREATE_TIME`   datetime                      DEFAULT NULL COMMENT '创建时间',
    `MODIFIER`      varchar(20) COLLATE utf8_bin  DEFAULT NULL COMMENT '修改人',
    `MODIFY_TIME`   datetime                      DEFAULT NULL COMMENT '修改时间',
    `REC_VER`       decimal(10, 0)                DEFAULT NULL COMMENT '版本号',
    `NAME`          varchar(100) COLLATE utf8_bin DEFAULT NULL COMMENT '名称',
    `ID`            varchar(50) COLLATE utf8_bin  DEFAULT NULL COMMENT 'ID',
    `CODE`          varchar(20) COLLATE utf8_bin  DEFAULT NULL COMMENT '编号',
    `CATEGORY_UUID` varchar(50) COLLATE utf8_bin  DEFAULT NULL COMMENT '业务分类UUID',
    `REMARK`        varchar(200) COLLATE utf8_bin DEFAULT NULL COMMENT '备注'
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8
  COLLATE = utf8_bin
  ROW_FORMAT = DYNAMIC COMMENT ='业务';



create table `BIZ_PROCESS_DEFINITION`
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
    `VERSION`         decimal(10, 1)                DEFAULT NULL COMMENT '版本',
    `ENABLED`         tinyint(1) DEFAULT NULL COMMENT '是否启用',
    `BUSINESS_ID`     varchar(50) COLLATE utf8_bin  DEFAULT NULL COMMENT '业务ID',
    `TAG_ID`          varchar(100) COLLATE utf8_bin DEFAULT NULL COMMENT '业务标签ID',
    `DEFINITION_JSON` longtext COLLATE utf8_bin     DEFAULT NULL COMMENT '定义JSON信息',
    `REMARK`          varchar(200) COLLATE utf8_bin DEFAULT NULL COMMENT '备注'
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8
  COLLATE = utf8_bin
  ROW_FORMAT = DYNAMIC COMMENT ='业务流程定义';



create table `BIZ_PROCESS_DEFINITION_HIS`
(
    `UUID`             varchar(50) COLLATE utf8_bin primary key COMMENT 'UUID，系统字段',
    `CREATOR`          varchar(20) COLLATE utf8_bin  DEFAULT NULL COMMENT '创建人',
    `CREATE_TIME`      datetime                      DEFAULT NULL COMMENT '创建时间',
    `MODIFIER`         varchar(20) COLLATE utf8_bin  DEFAULT NULL COMMENT '修改人',
    `MODIFY_TIME`      datetime                      DEFAULT NULL COMMENT '修改时间',
    `REC_VER`          decimal(10, 0)                DEFAULT NULL COMMENT '版本号',
    `PROCESS_DEF_UUID` varchar(50) COLLATE utf8_bin  DEFAULT NULL COMMENT '过程定义UUID',
    `NAME`             varchar(100) COLLATE utf8_bin DEFAULT NULL COMMENT '名称',
    `ID`               varchar(50) COLLATE utf8_bin  DEFAULT NULL COMMENT 'ID',
    `CODE`             varchar(20) COLLATE utf8_bin  DEFAULT NULL COMMENT '编号',
    `VERSION`          decimal(10, 1)                DEFAULT NULL COMMENT '版本',
    `ENABLED`          tinyint(1) DEFAULT NULL COMMENT '是否启用',
    `BUSINESS_ID`      varchar(50) COLLATE utf8_bin  DEFAULT NULL COMMENT '业务ID',
    `TAG_ID`           varchar(100) COLLATE utf8_bin DEFAULT NULL COMMENT '业务标签ID',
    `DEFINITION_JSON`  longtext COLLATE utf8_bin     DEFAULT NULL COMMENT '定义JSON信息',
    `REMARK`           varchar(200) COLLATE utf8_bin DEFAULT NULL COMMENT '备注'
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8
  COLLATE = utf8_bin
  ROW_FORMAT = DYNAMIC COMMENT ='业务流程定义历史版本';



create table `BIZ_TAG`
(
    `UUID`        varchar(50) COLLATE utf8_bin primary key COMMENT 'UUID，系统字段',
    `CREATOR`     varchar(20) COLLATE utf8_bin  DEFAULT NULL COMMENT '创建人',
    `CREATE_TIME` datetime                      DEFAULT NULL COMMENT '创建时间',
    `MODIFIER`    varchar(20) COLLATE utf8_bin  DEFAULT NULL COMMENT '修改人',
    `MODIFY_TIME` datetime                      DEFAULT NULL COMMENT '修改时间',
    `REC_VER`     decimal(10, 0)                DEFAULT NULL COMMENT '版本号',
    `NAME`        varchar(100) COLLATE utf8_bin DEFAULT NULL COMMENT '名称',
    `ID`          varchar(50) COLLATE utf8_bin  DEFAULT NULL COMMENT 'ID',
    `CODE`        varchar(20) COLLATE utf8_bin  DEFAULT NULL COMMENT '编号',
    `SCOPE`       varchar(100) COLLATE utf8_bin DEFAULT NULL COMMENT '使用范围，由数据字典配置，例如全部、业务流程、过程节点、事项、事项工作日、事项材料、其他',
    `REMARK`      varchar(200) COLLATE utf8_bin DEFAULT NULL COMMENT '备注'
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8
  COLLATE = utf8_bin
  ROW_FORMAT = DYNAMIC COMMENT ='业务标签';



create table `BIZ_PROCESS_INSTANCE`
(
    `UUID`             varchar(50) COLLATE utf8_bin primary key COMMENT 'UUID，系统字段',
    `CREATOR`          varchar(20) COLLATE utf8_bin  DEFAULT NULL COMMENT '创建人',
    `CREATE_TIME`      datetime                      DEFAULT NULL COMMENT '创建时间',
    `MODIFIER`         varchar(20) COLLATE utf8_bin  DEFAULT NULL COMMENT '修改人',
    `MODIFY_TIME`      datetime                      DEFAULT NULL COMMENT '修改时间',
    `REC_VER`          decimal(10, 0)                DEFAULT NULL COMMENT '版本号',
    `NAME`             varchar(100) COLLATE utf8_bin DEFAULT NULL COMMENT '业务流程名称',
    `ID`               varchar(50) COLLATE utf8_bin  DEFAULT NULL COMMENT '业务流程ID',
    `TITLE`            varchar(200) COLLATE utf8_bin DEFAULT NULL COMMENT '标题',
    `ENTITY_NAME`      varchar(150) COLLATE utf8_bin DEFAULT NULL COMMENT '业务主体名称',
    `ENTITY_ID`        varchar(100) COLLATE utf8_bin DEFAULT NULL COMMENT '业务主体ID',
    `FORM_UUID`        varchar(50) COLLATE utf8_bin  DEFAULT NULL COMMENT '表单定义UUID',
    `DATA_UUID`        varchar(50) COLLATE utf8_bin  DEFAULT NULL COMMENT '表单数据UUID',
    `START_TIME`       datetime                      DEFAULT NULL COMMENT '开始时间',
    `END_TIME`         datetime                      DEFAULT NULL COMMENT '结束时间',
    `STATE`            varchar(10) COLLATE utf8_bin  DEFAULT NULL COMMENT '当前状态，10运行中，20暂停，30已结束',
    `PROCESS_DEF_UUID` varchar(50) COLLATE utf8_bin  DEFAULT NULL COMMENT '业务流程定义UUID'
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8
  COLLATE = utf8_bin
  ROW_FORMAT = DYNAMIC COMMENT ='业务流程实例';



create table `BIZ_PROCESS_NODE_INSTANCE`
(
    `UUID`                  varchar(50) COLLATE utf8_bin primary key COMMENT 'UUID，系统字段',
    `CREATOR`               varchar(20) COLLATE utf8_bin  DEFAULT NULL COMMENT '创建人',
    `CREATE_TIME`           datetime                      DEFAULT NULL COMMENT '创建时间',
    `MODIFIER`              varchar(20) COLLATE utf8_bin  DEFAULT NULL COMMENT '修改人',
    `MODIFY_TIME`           datetime                      DEFAULT NULL COMMENT '修改时间',
    `REC_VER`               decimal(10, 0)                DEFAULT NULL COMMENT '版本号',
    `NAME`                  varchar(100) COLLATE utf8_bin DEFAULT NULL COMMENT '过程节点名称',
    `ID`                    varchar(50) COLLATE utf8_bin  DEFAULT NULL COMMENT '过程节点ID',
    `TITLE`                 varchar(200) COLLATE utf8_bin DEFAULT NULL COMMENT '标题',
    `ENTITY_NAME`           varchar(150) COLLATE utf8_bin DEFAULT NULL COMMENT '业务主体名称',
    `ENTITY_ID`             varchar(100) COLLATE utf8_bin DEFAULT NULL COMMENT '业务主体ID',
    `FORM_UUID`             varchar(50) COLLATE utf8_bin  DEFAULT NULL COMMENT '表单定义UUID',
    `DATA_UUID`             varchar(50) COLLATE utf8_bin  DEFAULT NULL COMMENT '表单数据UUID',
    `START_TIME`            datetime                      DEFAULT NULL COMMENT '开始时间',
    `END_TIME`              datetime                      DEFAULT NULL COMMENT '结束时间',
    `TIME_LIMIT_TYPE`       decimal(10, 0)                DEFAULT NULL COMMENT '时限类型1、工作日，2、自然日',
    `TIME_LIMIT`            decimal(10, 0)                DEFAULT NULL COMMENT '时限标线',
    `TOTAL_TIME`            decimal(10, 1)                DEFAULT NULL COMMENT '总用时',
    `IS_MILESTONE`          tinyint(1) DEFAULT NULL COMMENT '是否里程碑',
    `STATE`                 varchar(10) COLLATE utf8_bin  DEFAULT NULL COMMENT '当前状态，10运行中，20暂停，30已结束',
    `PARENT_NODE_INST_UUID` varchar(50) COLLATE utf8_bin  DEFAULT NULL COMMENT '上级过程节点实例UUID',
    `PROCESS_INST_UUID`     varchar(50) COLLATE utf8_bin  DEFAULT NULL COMMENT '业务流程实例UUID',
    `PROCESS_DEF_UUID`      varchar(50) COLLATE utf8_bin  DEFAULT NULL COMMENT '业务流程定义UUID'
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8
  COLLATE = utf8_bin
  ROW_FORMAT = DYNAMIC COMMENT ='业务流程过程节点实例';



create table `BIZ_PROCESS_ITEM_INSTANCE`
(
    `UUID`                   varchar(50) COLLATE utf8_bin primary key COMMENT 'UUID，系统字段',
    `CREATOR`                varchar(20) COLLATE utf8_bin   DEFAULT NULL COMMENT '创建人',
    `CREATE_TIME`            datetime                       DEFAULT NULL COMMENT '创建时间',
    `MODIFIER`               varchar(20) COLLATE utf8_bin   DEFAULT NULL COMMENT '修改人',
    `MODIFY_TIME`            datetime                       DEFAULT NULL COMMENT '修改时间',
    `REC_VER`                decimal(10, 0)                 DEFAULT NULL COMMENT '版本号',
    `ITEM_DEF_NAME`          varchar(100) COLLATE utf8_bin  DEFAULT NULL COMMENT '事项定义名称',
    `ITEM_DEF_ID`            varchar(50) COLLATE utf8_bin   DEFAULT NULL COMMENT '事项定义ID',
    `ITEM_NAME`              varchar(2000) COLLATE utf8_bin DEFAULT NULL COMMENT '事项名称',
    `ITEM_CODE`              varchar(2000) COLLATE utf8_bin DEFAULT NULL COMMENT '事项编码',
    `ITEM_ID`                varchar(2000) COLLATE utf8_bin DEFAULT NULL COMMENT '事项ID，业务流程事项配置自动生成的ID',
    `ITEM_TYPE`              varchar(10) COLLATE utf8_bin   DEFAULT NULL COMMENT '事项类型，10单个事项，20串联事项，30并联事项',
    `TITLE`                  varchar(200) COLLATE utf8_bin  DEFAULT NULL COMMENT '标题',
    `ENTITY_NAME`            varchar(150) COLLATE utf8_bin  DEFAULT NULL COMMENT '业务主体名称',
    `ENTITY_ID`              varchar(100) COLLATE utf8_bin  DEFAULT NULL COMMENT '业务主体ID',
    `FORM_UUID`              varchar(50) COLLATE utf8_bin   DEFAULT NULL COMMENT '表单定义UUID',
    `DATA_UUID`              varchar(50) COLLATE utf8_bin   DEFAULT NULL COMMENT '表单数据UUID',
    `START_TIME`             datetime                       DEFAULT NULL COMMENT '开始时间',
    `END_TIME`               datetime                       DEFAULT NULL COMMENT '结束时间',
    `TIME_LIMIT_TYPE`        decimal(10, 0)                 DEFAULT NULL COMMENT '时限类型1、工作日，2、自然日',
    `TIME_LIMIT`             decimal(10, 0)                 DEFAULT NULL COMMENT '时限',
    `TOTAL_TIME`             decimal(10, 1)                 DEFAULT NULL COMMENT '总用时',
    `IS_MILESTONE`           tinyint(1) DEFAULT NULL COMMENT '是否里程碑',
    `IS_DISPENSE_ITEM`       tinyint(1) DEFAULT NULL COMMENT '是否分发事项',
    `STATE`                  varchar(10) COLLATE utf8_bin   DEFAULT NULL COMMENT '当前状态，10运行中，20暂停，30已结束',
    `TIMER_UUID`             varchar(50) COLLATE utf8_bin   DEFAULT NULL COMMENT '计时器UUID',
    `TIMER_STATE`            decimal(1)                     DEFAULT NULL COMMENT '计时器状态，0未启动、1已启动、2暂停、3结束',
    `TIMING_STATE`           decimal(1)                     DEFAULT NULL COMMENT '计时状态， 0正常、1预警、2到期、3逾期',
    `DUE_TIME`               datetime                       DEFAULT NULL COMMENT '到期时间',
    `PARENT_ITEM_INST_UUID`  varchar(50) COLLATE utf8_bin   DEFAULT NULL COMMENT '上级事项实例UUID',
    `BELONG_ITEM_INST_UUID`  varchar(50) COLLATE utf8_bin   DEFAULT NULL COMMENT '归属事项实例UUID，事项拆分时归属的事项实例',
    `ITEM_DEF_UUID`          varchar(50) COLLATE utf8_bin   DEFAULT NULL COMMENT '事项定义UUID',
    `PROCESS_NODE_INST_UUID` varchar(50) COLLATE utf8_bin   DEFAULT NULL COMMENT '过程节点实例UUID',
    `PROCESS_INST_UUID`      varchar(50) COLLATE utf8_bin   DEFAULT NULL COMMENT '业务流程实例UUID',
    `PROCESS_DEF_UUID`       varchar(50) COLLATE utf8_bin   DEFAULT NULL COMMENT '业务流程定义UUID',
    `PROCESS_DEF_ID`         varchar(50) COLLATE utf8_bin   DEFAULT NULL COMMENT '业务流程定义ID'
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8
  COLLATE = utf8_bin
  ROW_FORMAT = DYNAMIC COMMENT ='业务流程事项实例';


create table `BIZ_PROCESS_ITEM_INST_DISPENSE`
(
    `UUID`                  varchar(50) COLLATE utf8_bin primary key COMMENT 'UUID，系统字段',
    `CREATOR`               varchar(20) COLLATE utf8_bin   DEFAULT NULL COMMENT '创建人',
    `CREATE_TIME`           datetime                       DEFAULT NULL COMMENT '创建时间',
    `MODIFIER`              varchar(20) COLLATE utf8_bin   DEFAULT NULL COMMENT '修改人',
    `MODIFY_TIME`           datetime                       DEFAULT NULL COMMENT '修改时间',
    `REC_VER`               decimal(10, 0)                 DEFAULT NULL COMMENT '版本号',
    `PARENT_ITEM_INST_UUID` varchar(100) COLLATE utf8_bin  DEFAULT NULL COMMENT '上级事项实例UUID',
    `ITEM_INST_UUID`        varchar(50) COLLATE utf8_bin   DEFAULT NULL COMMENT '当前事项实例UUID',
    `ITEM_NAME`             varchar(50) COLLATE utf8_bin   DEFAULT NULL COMMENT '事项名称',
    `ITEM_CODE`             varchar(2000) COLLATE utf8_bin DEFAULT NULL COMMENT '事项编码',
    `SORT_ORDER`            varchar(2000) COLLATE utf8_bin DEFAULT NULL COMMENT '排序号',
    `COMPLETION_STATE`      varchar(2000) COLLATE utf8_bin DEFAULT NULL COMMENT '完成状态，0未分发，1已分发'
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8
  COLLATE = utf8_bin
  ROW_FORMAT = DYNAMIC COMMENT ='业务流程事项实例分发';



create table `BIZ_PROCESS_ITEM_OPERATION`
(
    `UUID`                   varchar(50) COLLATE utf8_bin primary key COMMENT 'UUID，系统字段',
    `CREATOR`                varchar(20) COLLATE utf8_bin   DEFAULT NULL COMMENT '创建人',
    `CREATE_TIME`            datetime                       DEFAULT NULL COMMENT '创建时间',
    `MODIFIER`               varchar(20) COLLATE utf8_bin   DEFAULT NULL COMMENT '修改人',
    `MODIFY_TIME`            datetime                       DEFAULT NULL COMMENT '修改时间',
    `REC_VER`                decimal(10, 0)                 DEFAULT NULL COMMENT '版本号',
    `OPERATOR_NAME`          varchar(100) COLLATE utf8_bin  DEFAULT NULL COMMENT '操作人名称',
    `OPERATOR_ID`            varchar(20) COLLATE utf8_bin   DEFAULT NULL COMMENT '操作人ID',
    `OPERATE_TYPE`           varchar(50) COLLATE utf8_bin   DEFAULT NULL COMMENT '操作类型',
    `OPERATE_TIME`           datetime                       DEFAULT NULL COMMENT '操作时间',
    `OPINION_TEXT`           varchar(2000) COLLATE utf8_bin DEFAULT NULL COMMENT '办理意见',
    `REPO_FILE_UUIDS`        varchar(2000) COLLATE utf8_bin DEFAULT NULL COMMENT '办理附件UUID列表，多个以分号隔开',
    `ITEM_INST_UUID`         varchar(50) COLLATE utf8_bin   DEFAULT NULL COMMENT '事项实例UUID',
    `PROCESS_NODE_INST_UUID` varchar(50) COLLATE utf8_bin   DEFAULT NULL COMMENT '过程节点实例UUID',
    `PROCESS_INST_UUID`      varchar(50) COLLATE utf8_bin   DEFAULT NULL COMMENT '业务流程实例UUID'
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8
  COLLATE = utf8_bin
  ROW_FORMAT = DYNAMIC COMMENT ='业务流程事项实例操作表';



create table `BIZ_ITEM_DEFINITION`
(
    `UUID`                    varchar(50) COLLATE utf8_bin primary key COMMENT 'UUID，系统字段',
    `CREATOR`                 varchar(20) COLLATE utf8_bin  DEFAULT NULL COMMENT '创建人',
    `CREATE_TIME`             datetime                      DEFAULT NULL COMMENT '创建时间',
    `MODIFIER`                varchar(20) COLLATE utf8_bin  DEFAULT NULL COMMENT '修改人',
    `MODIFY_TIME`             datetime                      DEFAULT NULL COMMENT '修改时间',
    `REC_VER`                 decimal(10, 0)                DEFAULT NULL COMMENT '版本号',
    `NAME`                    varchar(100) COLLATE utf8_bin DEFAULT NULL COMMENT '名称',
    `ID`                      varchar(50) COLLATE utf8_bin  DEFAULT NULL COMMENT 'ID',
    `CODE`                    varchar(50) COLLATE utf8_bin  DEFAULT NULL COMMENT '编号',
    `TYPE`                    varchar(10) COLLATE utf8_bin  DEFAULT NULL COMMENT '事项类型，10单个事项，20串联事项，30并联事项',
    `BUSINESS_ID`             varchar(50) COLLATE utf8_bin  DEFAULT NULL COMMENT '业务ID',
    `FORM_ID`                 varchar(50) COLLATE utf8_bin  DEFAULT NULL COMMENT '使用定义单ID',
    `ITEM_NAME_FIELD`         varchar(30) COLLATE utf8_bin  DEFAULT NULL COMMENT '事项名称字段',
    `ITEM_CODE_FIELD`         varchar(30) COLLATE utf8_bin  DEFAULT NULL COMMENT '事项编码字段',
    `TIME_LIMIT_SUBFORM_ID`   varchar(30) COLLATE utf8_bin  DEFAULT NULL COMMENT '时限从表ID',
    `TIME_LIMIT_FIELD`        varchar(30) COLLATE utf8_bin  DEFAULT NULL COMMENT '时限从表的时限字段',
    `MATERIAL_SUBFORM_ID`     varchar(30) COLLATE utf8_bin  DEFAULT NULL COMMENT '材料从表ID',
    `MATERIAL_NAME_FIELD`     varchar(30) COLLATE utf8_bin  DEFAULT NULL COMMENT '材料从表的材料名称字段',
    `MATERIAL_CODE_FIELD`     varchar(30) COLLATE utf8_bin  DEFAULT NULL COMMENT '材料从表的材料编码字段',
    `MATERIAL_REQUIRED_FIELD` VARCHAR(30) COLLATE utf8_bin  DEFAULT NULL COMMENT '材料从表的材料是否必填字段',
    `INCLUDE_ITEM_SUBFORM_ID` varchar(30) COLLATE utf8_bin  DEFAULT NULL COMMENT '包含事项从表ID',
    `INCLUDE_ITEM_NAME_FIELD` varchar(30) COLLATE utf8_bin  DEFAULT NULL COMMENT '包含事项从表的事项名称字段',
    `INCLUDE_ITEM_CODE_FIELD` varchar(30) COLLATE utf8_bin  DEFAULT NULL COMMENT '包含事项从表的事项编码字段',
    `FRONT_ITEM_CODE_FIELD`   varchar(30) COLLATE utf8_bin  DEFAULT NULL COMMENT '包含事项从表的前置事项编码',
    `MUTEX_ITEM_SUBFORM_ID`   varchar(30) COLLATE utf8_bin  DEFAULT NULL COMMENT '互斥事项从表ID',
    `MUTEX_ITEM_CODE_FIELD`   varchar(30) COLLATE utf8_bin  DEFAULT NULL COMMENT '互斥事项从表的事项编码字段，字段值多个以分号隔开',
    `RELATE_ITEM_SUBFORM_ID`  varchar(30) COLLATE utf8_bin  DEFAULT NULL COMMENT '关联事项从表ID',
    `RELATE_ITEM_CODE_FIELD`  varchar(30) COLLATE utf8_bin  DEFAULT NULL COMMENT '关联事项从表的事项编码字段，字段值多个以分号隔开',
    `REMARK`                  varchar(200) COLLATE utf8_bin DEFAULT NULL COMMENT '备注'
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8
  COLLATE = utf8_bin
  ROW_FORMAT = DYNAMIC COMMENT ='业务事项定义';


-- Create table
create table `BIZ_NEW_ITEM_RELATION`
(
    `UUID`                  varchar(50) COLLATE utf8_bin primary key COMMENT 'UUID，系统字段',
    `CREATOR`               varchar(20) COLLATE utf8_bin  DEFAULT NULL COMMENT '创建人',
    `CREATE_TIME`           datetime                      DEFAULT NULL COMMENT '创建时间',
    `MODIFIER`              varchar(20) COLLATE utf8_bin  DEFAULT NULL COMMENT '修改人',
    `MODIFY_TIME`           datetime                      DEFAULT NULL COMMENT '修改时间',
    `REC_VER`               decimal(10, 0)                DEFAULT NULL COMMENT '版本号',
    `SOURCE_ITEM_INST_UUID` varchar(50) COLLATE utf8_bin  DEFAULT NULL COMMENT '源事项实例UUID',
    `SOURCE_BIZ_INST_UUID`  varchar(50) COLLATE utf8_bin  DEFAULT NULL COMMENT '源业务实例UUID',
    `ITEM_INST_UUID`        varchar(50) COLLATE utf8_bin  DEFAULT NULL COMMENT '发起的事项实例UUID',
    `EXTRA_DATA`            VARCHAR(500) COLLATE utf8_bin DEFAULT NULL COMMENT '附加数据'
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8
  COLLATE = utf8_bin
  ROW_FORMAT = DYNAMIC COMMENT ='业务事项发起新业务事项关系';
