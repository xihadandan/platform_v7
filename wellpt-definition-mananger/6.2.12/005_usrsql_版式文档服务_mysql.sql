/* 签章服务配置 */
CREATE TABLE `LAYOUT_DOCUMENT_SERVICE_CONF`
(
  `UUID` VARCHAR(64) NOT NULL,
  `CREATE_TIME`            TIMESTAMP COMMENT  '创建时间',
  `CREATOR`                VARCHAR(255) COMMENT  '创建人',
  `MODIFIER`               VARCHAR(255) COMMENT '更新人',
  `MODIFY_TIME`            TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `REC_VER`              DECIMAL(10,0) COMMENT  '版本号',
  `SYSTEM_UNIT_ID`         VARCHAR(64) COMMENT '系统单位ID',
  `SERVER_NAME`         VARCHAR(64) COMMENT '服务名称',
  `SERVER_UNIQUE_CODE`         VARCHAR(64) COMMENT '服务唯一标识符',
  `CODE`         VARCHAR(64) COMMENT '编号',
  `SERVER_URL`         VARCHAR(1000) COMMENT '服务地址',
  `FILE_EXTENSIONS`         VARCHAR(64) COMMENT '支持的文件扩展名',
  `STATUS`         VARCHAR(10) COMMENT '状态',
  PRIMARY KEY (`UUID`)
) COMMENT = '签章服务配置';


INSERT INTO DYFORM_FILE_LIST_BUTTON_CONFIG(UUID, CREATE_TIME, CREATOR, MODIFIER, MODIFY_TIME, REC_VER, BUTTON_NAME, CODE, BTN_LIB, EVENT_MANGER, FILE_EXTENSIONS, DEFAULT_FLAG, ORDER_INDEX, BTN_TYPE, BTN_SHOW_TYPE) VALUES ('0580cd7b-878e-4445-8c45-8cc551eeda18', str_to_date('2021-11-24 11:26:25','%Y-%m-%d %T'), 'U0000000059', NULL, NULL, '9', '查阅', 'look_up_btn', '{"btnSize":"","iconInfo":null,"btnInfo":{"type":"line","type_name":"线框按钮","class":"w-line-btn","status":[{"class":"","text":"普通状态"},{"class":"hover","text":"鼠标移入状态"},{"class":"active","text":"点击状态"},{"class":"w-disable-btn","text":"禁用状态"}]},"btnColor":"w-btn-primary"}', '{}', 'ofd', '0', '9', '1', 'show');

INSERT INTO DYFORM_FILE_LIST_BUTTON_CONFIG(UUID, CREATE_TIME, CREATOR, MODIFIER, MODIFY_TIME, REC_VER, BUTTON_NAME, CODE, BTN_LIB, EVENT_MANGER, FILE_EXTENSIONS, DEFAULT_FLAG, ORDER_INDEX, BTN_TYPE, BTN_SHOW_TYPE) VALUES ('03bf3d3a-ae1b-417b-a808-e41dfde18535', str_to_date('2021-11-24 11:26:25','%Y-%m-%d %T'), 'U0000000059', NULL, NULL, '8', '盖章', 'seal_file_btn', '{"btnSize":"","iconInfo":null,"btnInfo":{"type":"line","type_name":"线框按钮","class":"w-line-btn","status":[{"class":"","text":"普通状态"},{"class":"hover","text":"鼠标移入状态"},{"class":"active","text":"点击状态"},{"class":"w-disable-btn","text":"禁用状态"}]},"btnColor":"w-btn-primary"}', '{}', 'ofd;pdf', '0', '10', '1', 'edit');
