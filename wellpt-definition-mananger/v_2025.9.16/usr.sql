

update app_code_i18n set apply_to ='pt-app-workflow' where apply_to='pt-workflow';
update app_code_i18n set apply_to ='pt-usr-mgr' where apply_to='pt-org';
update app_code_i18n set apply_to ='pt-dyform-mgr' where apply_to='pt-dyform';


ALTER TABLE APP_SYSTEM_INFO ADD DEFAULT_LOCALE VARCHAR2(64) DEFAULT 'zh_CN';
ALTER TABLE APP_SYSTEM_INFO ADD ENABLE_LOCALE NUMBER(1) DEFAULT 1;
COMMENT ON COLUMN APP_SYSTEM_INFO.DEFAULT_LOCALE
  IS '系统默认语言';
COMMENT ON COLUMN APP_SYSTEM_INFO.ENABLE_LOCALE
  IS '是否启用国际化';

