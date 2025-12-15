create table `CD_MATERIAL_DEFINITION`
(
    `UUID`                  bigint primary key COMMENT 'UUID，系统字段',
    `CREATOR`               varchar(20) COLLATE utf8_bin   DEFAULT NULL COMMENT '创建人',
    `CREATE_TIME`           datetime                       DEFAULT NULL COMMENT '创建时间',
    `MODIFIER`              varchar(20) COLLATE utf8_bin   DEFAULT NULL COMMENT '修改人',
    `MODIFY_TIME`           datetime                       DEFAULT NULL COMMENT '修改时间',
    `REC_VER`               int                            DEFAULT NULL COMMENT '版本号',
    `NAME`                  varchar(100) COLLATE utf8_bin  DEFAULT NULL COMMENT '材料名称',
    `CODE`                  varchar(50) COLLATE utf8_bin   DEFAULT NULL COMMENT '材料编号',
    `MEDIUM_TYPE`           varchar(20) COLLATE utf8_bin   DEFAULT NULL COMMENT '材料形式：10原件、20复印件、30电子文档',
    `FORMAT`                varchar(100) COLLATE utf8_bin  DEFAULT NULL COMMENT '电子文档格式',
    `SAMPLE_REPO_FILE_UUID` varchar(200) COLLATE utf8_bin  DEFAULT NULL COMMENT '样例文件UUID',
    `DESCRIPTION`           varchar(1000) COLLATE utf8_bin DEFAULT NULL COMMENT '材料说明'
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8
  COLLATE = utf8_bin
  ROW_FORMAT = DYNAMIC COMMENT ='材料定义';


create table `CD_MATERIAL_DEFINITION_HIS`
(
    `UUID`                  bigint primary key COMMENT 'UUID，系统字段',
    `CREATOR`               varchar(20) COLLATE utf8_bin   DEFAULT NULL COMMENT '创建人',
    `CREATE_TIME`           datetime                       DEFAULT NULL COMMENT '创建时间',
    `MODIFIER`              varchar(20) COLLATE utf8_bin   DEFAULT NULL COMMENT '修改人',
    `MODIFY_TIME`           datetime                       DEFAULT NULL COMMENT '修改时间',
    `REC_VER`               int                            DEFAULT NULL COMMENT '版本号',
    `MATERIAL_DEF_UUID`     bigint                         DEFAULT NULL COMMENT '材料定义UUID',
    `NAME`                  varchar(100) COLLATE utf8_bin  DEFAULT NULL COMMENT '材料名称',
    `CODE`                  varchar(50) COLLATE utf8_bin   DEFAULT NULL COMMENT '材料编号',
    `MEDIUM_TYPE`           varchar(20) COLLATE utf8_bin   DEFAULT NULL COMMENT '材料形式：10原件、20复印件、30电子文档',
    `FORMAT`                varchar(100) COLLATE utf8_bin  DEFAULT NULL COMMENT '电子文档格式',
    `SAMPLE_REPO_FILE_UUID` varchar(200) COLLATE utf8_bin  DEFAULT NULL COMMENT '样例文件UUID',
    `DESCRIPTION`           varchar(1000) COLLATE utf8_bin DEFAULT NULL COMMENT '材料说明'
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8
  COLLATE = utf8_bin
  ROW_FORMAT = DYNAMIC COMMENT ='材料定义历史信息';


create table `CD_MATERIAL`
(
    `UUID`              bigint primary key COMMENT 'UUID，系统字段',
    `CREATOR`           varchar(20) COLLATE utf8_bin   DEFAULT NULL COMMENT '创建人',
    `CREATE_TIME`       datetime                       DEFAULT NULL COMMENT '创建时间',
    `MODIFIER`          varchar(20) COLLATE utf8_bin   DEFAULT NULL COMMENT '修改人',
    `MODIFY_TIME`       datetime                       DEFAULT NULL COMMENT '修改时间',
    `REC_VER`           int                            DEFAULT NULL COMMENT '版本号',
    `MATERIAL_DEF_UUID` bigint                         DEFAULT NULL COMMENT '材料定义UUID',
    `MATERIAL_NAME`     varchar(100) COLLATE utf8_bin  DEFAULT NULL COMMENT '材料名称',
    `MATERIAL_CODE`     varchar(50) COLLATE utf8_bin   DEFAULT NULL COMMENT '材料编号',
    `BIZ_TYPE`          varchar(20) COLLATE utf8_bin   DEFAULT NULL COMMENT '所在业务类型',
    `BIZ_ID`            varchar(50) COLLATE utf8_bin   DEFAULT NULL COMMENT '所在业务ID',
    `DATA_UUID`         varchar(50) COLLATE utf8_bin   DEFAULT NULL COMMENT '所在业务数据UUID',
    `PURPOSE`           varchar(50) COLLATE utf8_bin   DEFAULT NULL COMMENT '所在业务场景',
    `OWNER_ID`          varchar(50) COLLATE utf8_bin   DEFAULT NULL COMMENT '所有者ID',
    `VERSION`           decimal(10, 1)                 DEFAULT NULL COMMENT '版本号',
    `REPO_FILE_UUIDS`   varchar(4000) COLLATE utf8_bin DEFAULT NULL COMMENT '材料附件UUID列表，多个以分号隔开',
    `VALIDATION_FLAG`   varchar(2) COLLATE utf8_bin    DEFAULT NULL COMMENT '材料有效标识，1有效，0无效',
    `ATTRIBUTES`        longtext                       DEFAULT NULL COMMENT '材料附件扩展属性信息',
    `REMARK`            varchar(500) COLLATE utf8_bin  DEFAULT NULL COMMENT '备注'
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8
  COLLATE = utf8_bin
  ROW_FORMAT = DYNAMIC COMMENT ='材料实例化数据';


create table `CD_MATERIAL_HIS`
(
    `UUID`              bigint primary key COMMENT 'UUID，系统字段',
    `CREATOR`           varchar(20) COLLATE utf8_bin   DEFAULT NULL COMMENT '创建人',
    `CREATE_TIME`       datetime                       DEFAULT NULL COMMENT '创建时间',
    `MODIFIER`          varchar(20) COLLATE utf8_bin   DEFAULT NULL COMMENT '修改人',
    `MODIFY_TIME`       datetime                       DEFAULT NULL COMMENT '修改时间',
    `REC_VER`           int                            DEFAULT NULL COMMENT '版本号',
    `BIZ_MATERIAL_UUID` bigint                         DEFAULT NULL COMMENT '业务材料数据UUID',
    `MATERIAL_DEF_UUID` bigint                         DEFAULT NULL COMMENT '材料定义UUID',
    `MATERIAL_NAME`     varchar(100) COLLATE utf8_bin  DEFAULT NULL COMMENT '材料名称',
    `MATERIAL_CODE`     varchar(50) COLLATE utf8_bin   DEFAULT NULL COMMENT '材料编号',
    `BIZ_TYPE`          varchar(20) COLLATE utf8_bin   DEFAULT NULL COMMENT '所在业务类型',
    `BIZ_ID`            varchar(50) COLLATE utf8_bin   DEFAULT NULL COMMENT '所在业务ID',
    `DATA_UUID`         varchar(50) COLLATE utf8_bin   DEFAULT NULL COMMENT '所在业务数据UUID',
    `PURPOSE`           varchar(50) COLLATE utf8_bin   DEFAULT NULL COMMENT '所在业务场景',
    `OWNER_ID`          varchar(50) COLLATE utf8_bin   DEFAULT NULL COMMENT '所有者ID',
    `VERSION`           decimal(10, 1)                 DEFAULT NULL COMMENT '版本号',
    `REPO_FILE_UUIDS`   varchar(4000) COLLATE utf8_bin DEFAULT NULL COMMENT '材料附件UUID列表，多个以分号隔开',
    `VALIDATION_FLAG`   varchar(2) COLLATE utf8_bin    DEFAULT NULL COMMENT '材料有效标识，1有效，0无效',
    `ATTRIBUTES`        longtext                       DEFAULT NULL COMMENT '材料附件扩展属性信息',
    `REMARK`            varchar(500) COLLATE utf8_bin  DEFAULT NULL COMMENT '备注'
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8
  COLLATE = utf8_bin
  ROW_FORMAT = DYNAMIC COMMENT ='材料实例化数据历史版本';