-- 增加组组织选项"全部单位"
INSERT INTO MULTI_ORG_OPTION(`UUID`, `CREATE_TIME`, `CREATOR`, `MODIFIER`, `MODIFY_TIME`, `REC_VER`, `CODE`, `ID`, `NAME`, `REMARK`, `IS_SHOW`, `SYSTEM_UNIT_ID`, `IS_ENABLE`)
VALUES ('53023edd-a654-47c5-b017-6e8f36cc4966', STR_TO_DATE('2022-03-01 13:38:37','%Y-%m-%d %H:%i:%s'), 'U0000000000', 'U0000000000', STR_TO_DATE('2022-03-01 13:38:37','%Y-%m-%d %H:%i:%s'), '0', '010', 'AllUnits', '全部单位', '全部单位 展现默认且启用的组织版本，如果是集团单位，展示各成员单位的默认且启用的组织版本', '1', 'S0000000000', '1');
