CREATE TABLE `dyform_category`
(
    `UUID`        bigint primary key COMMENT 'UUID，系统字段',
    `CREATE_TIME` datetime                      DEFAULT NULL COMMENT '创建时间',
    `CREATOR`     varchar(50) COLLATE utf8_bin  DEFAULT NULL COMMENT '创建人',
    `MODIFIER`    varchar(50) COLLATE utf8_bin  DEFAULT NULL COMMENT '修改人',
    `MODIFY_TIME` datetime                      DEFAULT NULL COMMENT '修改时间',
    `REC_VER`     int(11)                       DEFAULT NULL COMMENT '版本号',
    `SYSTEM`      varchar(64) COLLATE utf8_bin  DEFAULT NULL COMMENT '归属系统',
    `TENANT`      varchar(64) COLLATE utf8_bin  DEFAULT NULL COMMENT '归属租户',
    `NAME`        varchar(64) COLLATE utf8_bin  DEFAULT NULL COMMENT '名称',
    `CODE`        varchar(50) COLLATE utf8_bin  DEFAULT NULL COMMENT '编号',
    `ICON`        varchar(100) COLLATE utf8_bin DEFAULT NULL COMMENT '字体图标样式类',
    `ICON_COLOR`  varchar(12) COLLATE utf8_bin  DEFAULT NULL COMMENT '图标颜色',
    `REMARK`      varchar(300) COLLATE utf8_bin DEFAULT NULL COMMENT '描述',
    `PARENT_UUID` bigint COMMENT '上级分类UUID',
    `MODULE_ID`   varchar(64) COLLATE utf8_bin  DEFAULT NULL COMMENT '模块ID',
    PRIMARY KEY (`UUID`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8
  COLLATE = utf8_bin
  ROW_FORMAT = DYNAMIC COMMENT ='表单分类';


alter table dyform_form_definition
    add category_uuid bigint COMMENT '分类UUID';