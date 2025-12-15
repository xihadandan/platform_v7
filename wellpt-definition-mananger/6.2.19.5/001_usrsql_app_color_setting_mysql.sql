-- ----------------------------
-- Table structure for APP_COLOR_SETTING
-- ----------------------------
CREATE TABLE `APP_COLOR_SETTING`  (
  `UUID` varchar(36) NOT NULL comment '主键uuid',
  `CREATOR` varchar(255) comment '创建人',
  create_time datetime  comment '创建时间',
  `MODIFIER` varchar(255) comment '更新人',
  `MODIFY_TIME` datetime comment '更新时间',
  rec_ver int  comment '数据版本',
  system_unit_id varchar(64) comment '系统单位Id',
  `ID` varchar(255)  NULL DEFAULT NULL COMMENT 'id',
  `NAME` varchar(255)  NULL DEFAULT NULL COMMENT '名称',
  `COLOR` varchar(10)  NULL DEFAULT NULL COMMENT '颜色',
  `MODULE_CODE` varchar(40)  NULL DEFAULT NULL COMMENT '模块编码',
  `TYPE` varchar(40)  NULL DEFAULT NULL COMMENT '分类/类型',
  PRIMARY KEY (`UUID`)
) comment='应用颜色配置表';