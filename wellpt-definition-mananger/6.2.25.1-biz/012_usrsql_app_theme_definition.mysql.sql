-- Create table
create table `APP_THEME_DEFINITION`
(
    `UUID`                 varchar(50) COLLATE utf8_bin primary key COMMENT 'UUID，系统字段',
    `CREATOR`              varchar(20) COLLATE utf8_bin  DEFAULT NULL COMMENT '创建人',
    `CREATE_TIME`          datetime                      DEFAULT NULL COMMENT '创建时间',
    `MODIFIER`             varchar(20) COLLATE utf8_bin  DEFAULT NULL COMMENT '修改人',
    `MODIFY_TIME`          datetime                      DEFAULT NULL COMMENT '修改时间',
    `REC_VER`              decimal(10, 0)                DEFAULT NULL COMMENT '版本号',
    `SYSTEM_UNIT_ID`       varchar(12) COLLATE utf8_bin  DEFAULT NULL COMMENT '系统单位ID',
    `NAME`                 varchar(100) COLLATE utf8_bin DEFAULT NULL COMMENT '名称',
    `ID`                   varchar(50) COLLATE utf8_bin  DEFAULT NULL COMMENT 'ID',
    `CODE`                 varchar(20) COLLATE utf8_bin  DEFAULT NULL COMMENT '编号',
    `SORT_ORDER`           decimal(10, 0)                DEFAULT NULL COMMENT '排序号',
    `APPLY_TO`             varchar(50) COLLATE utf8_bin  DEFAULT NULL COMMENT '应用于',
    `ENABLED`              tinyint(1)                    DEFAULT NULL COMMENT '是否启用',
    `DEFINITION_JSON_UUID` varchar(50) COLLATE utf8_bin  DEFAULT NULL COMMENT '主题定义JSON UUID',
    `REMARK`               varchar(200) COLLATE utf8_bin DEFAULT NULL COMMENT '备注'
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8
  COLLATE = utf8_bin
  ROW_FORMAT = DYNAMIC COMMENT ='主题定义';


create table `APP_THEME_DEFINITION_JSON`
(
    `UUID`            varchar(50) COLLATE utf8_bin primary key COMMENT 'UUID，系统字段',
    `CREATOR`         varchar(20) COLLATE utf8_bin DEFAULT NULL COMMENT '创建人',
    `CREATE_TIME`     datetime                     DEFAULT NULL COMMENT '创建时间',
    `MODIFIER`        varchar(20) COLLATE utf8_bin DEFAULT NULL COMMENT '修改人',
    `MODIFY_TIME`     datetime                     DEFAULT NULL COMMENT '修改时间',
    `REC_VER`         decimal(10, 0)               DEFAULT NULL COMMENT '版本号',
    `SYSTEM_UNIT_ID`  varchar(12) COLLATE utf8_bin DEFAULT NULL COMMENT '系统单位ID',
    `DEFINITION_JSON` longtext COLLATE utf8_bin    DEFAULT NULL COMMENT '主题定义JSON UUID'
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8
  COLLATE = utf8_bin
  ROW_FORMAT = DYNAMIC COMMENT ='主题定义JSON';


create table `APP_THEME_DEFINITION_HIS`
(
    `UUID`            varchar(50) COLLATE utf8_bin primary key COMMENT 'UUID，系统字段',
    `CREATOR`         varchar(20) COLLATE utf8_bin  DEFAULT NULL COMMENT '创建人',
    `CREATE_TIME`     datetime                      DEFAULT NULL COMMENT '创建时间',
    `MODIFIER`        varchar(20) COLLATE utf8_bin  DEFAULT NULL COMMENT '修改人',
    `MODIFY_TIME`     datetime                      DEFAULT NULL COMMENT '修改时间',
    `REC_VER`         decimal(10, 0)                DEFAULT NULL COMMENT '版本号',
    `SYSTEM_UNIT_ID`  varchar(12) COLLATE utf8_bin  DEFAULT NULL COMMENT '系统单位ID',
    `THEME_DEF_UUID`  varchar(50) COLLATE utf8_bin  DEFAULT NULL COMMENT '主题定义UUID',
    `NAME`            varchar(100) COLLATE utf8_bin DEFAULT NULL COMMENT '名称',
    `ID`              varchar(50) COLLATE utf8_bin  DEFAULT NULL COMMENT 'ID',
    `CODE`            varchar(20) COLLATE utf8_bin  DEFAULT NULL COMMENT '编号',
    `SORT_ORDER`      decimal(10, 0)                DEFAULT NULL COMMENT '排序号',
    `APPLY_TO`        varchar(50) COLLATE utf8_bin  DEFAULT NULL COMMENT '应用于',
    `ENABLED`         tinyint(1)                    DEFAULT NULL COMMENT '是否启用',
    `DEFINITION_JSON` longtext COLLATE utf8_bin     DEFAULT NULL COMMENT '主题定义JSON',
    `REMARK`          varchar(200) COLLATE utf8_bin DEFAULT NULL COMMENT '备注'
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8
  COLLATE = utf8_bin
  ROW_FORMAT = DYNAMIC COMMENT ='主题定义历史记录';