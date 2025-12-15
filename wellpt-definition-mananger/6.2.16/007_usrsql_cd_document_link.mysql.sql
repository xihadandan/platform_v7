CREATE TABLE `cd_document_link` (
  `UUID` varchar(64) COLLATE utf8_bin PRIMARY KEY COMMENT 'UUID，系统字段',
  `CREATOR` varchar(64) COLLATE utf8_bin DEFAULT NULL COMMENT '创建人',
  `CREATE_TIME` datetime DEFAULT NULL COMMENT '创建时间',
  `MODIFIER` varchar(64) COLLATE utf8_bin DEFAULT NULL COMMENT '修改人',
  `MODIFY_TIME` datetime DEFAULT NULL COMMENT '修改时间',
  `REC_VER` decimal(10,0) DEFAULT NULL COMMENT '版本号',
  `BUSINESS_TYPE` varchar(100) COLLATE utf8_bin NOT NULL COMMENT '业务类型',
  `ACCESS_STRATEGY` varchar(2) COLLATE utf8_bin COMMENT '访问策略, 0不检验，1检验源数据，2检验目标数据，3任意数据，4全部',
  `SOURCE_DATA_UUID` varchar(64) COLLATE utf8_bin NOT NULL COMMENT '源数据UUID',
  `SOURCE_DATA_CHECKER` varchar(100) COLLATE utf8_bin COMMENT '源数据检验器',
  `TARGET_DATA_UUID` varchar(64) COLLATE utf8_bin NOT NULL COMMENT '目标数据UUID',
  `TARGET_DATA_CHECKER` varchar(100) COLLATE utf8_bin COMMENT '目标数据检验器',
  `TARGET_URL` varchar(200) COLLATE utf8_bin NOT NULL COMMENT '目标地址'
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin ROW_FORMAT=DYNAMIC COMMENT='文档链接关系表';
