CREATE TABLE `sn_serial_number_category`
(
    `UUID`           varchar(50) COLLATE utf8_bin NOT NULL COMMENT 'UUID，系统字段',
    `CREATE_TIME`    datetime                      DEFAULT NULL COMMENT '创建时间',
    `CREATOR`        varchar(50) COLLATE utf8_bin  DEFAULT NULL COMMENT '创建人',
    `MODIFIER`       varchar(50) COLLATE utf8_bin  DEFAULT NULL COMMENT '修改人',
    `MODIFY_TIME`    datetime                      DEFAULT NULL COMMENT '修改时间',
    `REC_VER`        int(11)                       DEFAULT NULL COMMENT '版本号',
    `SYSTEM_UNIT_ID` varchar(12) COLLATE utf8_bin  DEFAULT NULL COMMENT '归属系统单位',
    `NAME`           varchar(200) COLLATE utf8_bin DEFAULT NULL COMMENT '名称',
    `CODE`           varchar(50) COLLATE utf8_bin  DEFAULT NULL COMMENT '编号',
    `ICON`           varchar(120) COLLATE utf8_bin DEFAULT NULL COMMENT '字体图标样式类',
    `ICON_COLOR`     varchar(12) COLLATE utf8_bin  DEFAULT NULL COMMENT '图标颜色',
    `REMARK`         varchar(300) COLLATE utf8_bin DEFAULT NULL COMMENT '描述',
    PRIMARY KEY (`UUID`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8
  COLLATE = utf8_bin
  ROW_FORMAT = DYNAMIC COMMENT ='流水号分类';



CREATE TABLE `sn_serial_number_definition`
(
    `UUID`                 varchar(50) COLLATE utf8_bin NOT NULL COMMENT 'UUID，系统字段',
    `CREATE_TIME`          datetime                      DEFAULT NULL COMMENT '创建时间',
    `CREATOR`              varchar(50) COLLATE utf8_bin  DEFAULT NULL COMMENT '创建人',
    `MODIFIER`             varchar(50) COLLATE utf8_bin  DEFAULT NULL COMMENT '修改人',
    `MODIFY_TIME`          datetime                      DEFAULT NULL COMMENT '修改时间',
    `REC_VER`              int(11)                       DEFAULT NULL COMMENT '版本号',
    `SYSTEM_UNIT_ID`       varchar(12) COLLATE utf8_bin  DEFAULT NULL COMMENT '归属系统单位',
    `NAME`                 varchar(200) COLLATE utf8_bin DEFAULT NULL COMMENT '名称',
    `ID`                   varchar(50) COLLATE utf8_bin  DEFAULT NULL COMMENT 'ID',
    `CODE`                 varchar(50) COLLATE utf8_bin  DEFAULT NULL COMMENT '编号',
    `CATEGORY_UUID`        varchar(50) COLLATE utf8_bin  DEFAULT NULL COMMENT '分类UUID',
    `MODULE_ID`            varchar(50) COLLATE utf8_bin  DEFAULT NULL COMMENT '模块ID',
    `PREFIX`               varchar(50) COLLATE utf8_bin  DEFAULT NULL COMMENT '前缀',
    `INITIAL_VALUE`        varchar(10) COLLATE utf8_bin  DEFAULT NULL COMMENT '指针初始值',
    `INCREMENTAL`          int                           DEFAULT NULL COMMENT '指针增量',
    `DEFAULT_DIGITS`       int                           DEFAULT NULL COMMENT '指针默认位数',
    `SUFFIX`               varchar(50) COLLATE utf8_bin  DEFAULT NULL COMMENT '后缀',
    `ENABLE_POINTER_RESET` boolean                       DEFAULT NULL COMMENT '是否启用指针自动重置',
    `POINTER_RESET_TYPE`   varchar(10) COLLATE utf8_bin  DEFAULT NULL COMMENT '1按周期重置、2按变量重置',
    `POINTER_RESET_RULE`   varchar(100) COLLATE utf8_bin DEFAULT NULL COMMENT '指针重置规则',
    `NEXT_YEAR_START_DATE` varchar(10) COLLATE utf8_bin  DEFAULT NULL COMMENT '下一年新年度开始日期，默认值为 01-01',
    `OWNER_IDS`            varchar(500) COLLATE utf8_bin DEFAULT NULL COMMENT '使用人ID，多个以分号隔开',
    `OWNER_NAMES`          varchar(500) COLLATE utf8_bin DEFAULT NULL COMMENT '使用人名称，多个以分号隔开',
    `REMARK`               varchar(300) COLLATE utf8_bin DEFAULT NULL COMMENT '描述',
    PRIMARY KEY (`UUID`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8
  COLLATE = utf8_bin
  ROW_FORMAT = DYNAMIC COMMENT ='流水号定义';



CREATE TABLE `sn_serial_number_maintain`
(
    `UUID`                     varchar(50) COLLATE utf8_bin NOT NULL COMMENT 'UUID，系统字段',
    `CREATE_TIME`              datetime                      DEFAULT NULL COMMENT '创建时间',
    `CREATOR`                  varchar(50) COLLATE utf8_bin  DEFAULT NULL COMMENT '创建人',
    `MODIFIER`                 varchar(50) COLLATE utf8_bin  DEFAULT NULL COMMENT '修改人',
    `MODIFY_TIME`              datetime                      DEFAULT NULL COMMENT '修改时间',
    `REC_VER`                  int(11)                       DEFAULT NULL COMMENT '版本号',
    `SYSTEM_UNIT_ID`           varchar(12) COLLATE utf8_bin  DEFAULT NULL COMMENT '归属系统单位',
    `SERIAL_NUMBER_DEF_UUID`   varchar(50) COLLATE utf8_bin  DEFAULT NULL COMMENT '流水号定义UUID',
    `INITIAL_VALUE`            varchar(10) COLLATE utf8_bin  DEFAULT NULL COMMENT '指针初始值',
    `POINTER`                  varchar(100) COLLATE utf8_bin DEFAULT NULL COMMENT '指针',
    `POINTER_RESET_TYPE`       varchar(10) COLLATE utf8_bin  DEFAULT NULL COMMENT '1按周期重置、2按变量重置',
    `POINTER_RESET_RULE`       varchar(100) COLLATE utf8_bin DEFAULT NULL COMMENT '指针重置规则',
    `POINTER_RESET_RULE_VALUE` varchar(100) COLLATE utf8_bin DEFAULT NULL COMMENT '指针重置规则值',
    PRIMARY KEY (`UUID`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8
  COLLATE = utf8_bin
  ROW_FORMAT = DYNAMIC COMMENT ='流水号维护';



CREATE TABLE `sn_serial_number_relation`
(
    `UUID`           varchar(50) COLLATE utf8_bin  NOT NULL COMMENT 'UUID，系统字段',
    `CREATE_TIME`    datetime                     DEFAULT NULL COMMENT '创建时间',
    `CREATOR`        varchar(50) COLLATE utf8_bin DEFAULT NULL COMMENT '创建人',
    `MODIFIER`       varchar(50) COLLATE utf8_bin DEFAULT NULL COMMENT '修改人',
    `MODIFY_TIME`    datetime                     DEFAULT NULL COMMENT '修改时间',
    `REC_VER`        int(11)                      DEFAULT NULL COMMENT '版本号',
    `SYSTEM_UNIT_ID` varchar(12) COLLATE utf8_bin DEFAULT NULL COMMENT '归属系统单位',
    `SN_ID`          varchar(50) COLLATE utf8_bin  NOT NULL COMMENT '流水号定义ID',
    `OBJECT_TYPE`    int(11)                       NOT NULL COMMENT '对象类型：1：数据库表',
    `OBJECT_NAME`    varchar(255) COLLATE utf8_bin NOT NULL COMMENT '对象名',
    `FIELD_NAME`     varchar(255) COLLATE utf8_bin NOT NULL COMMENT '字段名',
    PRIMARY KEY (`UUID`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8
  COLLATE = utf8_bin
  ROW_FORMAT = DYNAMIC COMMENT ='流水号关联表字段记录';



CREATE TABLE `sn_serial_number_record`
(
    `UUID`           varchar(50) COLLATE utf8_bin  NOT NULL COMMENT 'UUID，系统字段',
    `CREATE_TIME`    datetime                      DEFAULT NULL COMMENT '创建时间',
    `CREATOR`        varchar(50) COLLATE utf8_bin  DEFAULT NULL COMMENT '创建人',
    `MODIFIER`       varchar(50) COLLATE utf8_bin  DEFAULT NULL COMMENT '修改人',
    `MODIFY_TIME`    datetime                      DEFAULT NULL COMMENT '修改时间',
    `REC_VER`        int(11)                       DEFAULT NULL COMMENT '版本号',
    `SYSTEM_UNIT_ID` varchar(12) COLLATE utf8_bin  DEFAULT NULL COMMENT '归属系统单位',
    `RELATION_UUID`  varchar(50) COLLATE utf8_bin  NOT NULL COMMENT '流水号关联表字段记录UUID',
    `MAINTAIN_UUID`  varchar(50) COLLATE utf8_bin  NOT NULL COMMENT '流水号关联表字段记录UUID',
    `PREFIX`         varchar(100) COLLATE utf8_bin DEFAULT NULL COMMENT '前缀',
    `POINTER`        int(16)                       NOT NULL COMMENT '指针',
    `SUFFIX`         varchar(100) COLLATE utf8_bin DEFAULT NULL COMMENT '后缀',
    `SERIAL_NO`      varchar(255) COLLATE utf8_bin NOT NULL COMMENT '流水号',
    PRIMARY KEY (`UUID`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8
  COLLATE = utf8_bin
  ROW_FORMAT = DYNAMIC COMMENT ='流水号记录';