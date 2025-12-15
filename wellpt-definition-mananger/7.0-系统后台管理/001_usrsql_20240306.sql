1 alter table APP_LOGIN_SECURITY_CONFIG add SYSTEM varchar2(64);
alter table APP_LOGIN_SECURITY_CONFIG add TENANT varchar2(64);


 alter table SYS_IP_CONFIG add SYSTEM varchar2(64);
alter table SYS_IP_CONFIG add TENANT varchar2(64);


 alter table APP_PAGE_DEFINITION add ENABLED number(1) default 1;
 comment on column APP_PAGE_DEFINITION.ENABLED
  is '是否启用';


alter table AUDIT_ROLE add TENANT varchar2(64);
alter table AUDIT_PRIVILEGE add TENANT varchar2(64);
alter table AUDIT_ROLE add SYSTEM varchar2(64);
alter table AUDIT_PRIVILEGE add SYSTEM varchar2(64);
 
 
alter table APP_SYSTEM_PAGE_SETTING add USER_THEME_DEFINABLE number(1) default 1;
alter table APP_SYSTEM_PAGE_SETTING add USER_LAYOUT_DEFINABLE number(1) default 1;
comment on column APP_SYSTEM_PAGE_SETTING.USER_THEME_DEFINABLE
  is '用户可自定义主题';
comment on column APP_SYSTEM_PAGE_SETTING.USER_LAYOUT_DEFINABLE
  is '用户可自定义导航布局';

 
alter table THEME_PACK add THEME_COLORS varchar2(300);
alter table THEME_PACK add FONT_SIZES varchar2(120);
alter table THEME_PACK add DEFAULT_THEME_COLOR varchar2(60);
ALTER TABLE THEME_PACK DROP COLUMN LOGO;
ALTER TABLE THEME_PACK DROP COLUMN THUMBNAIL;
ALTER TABLE THEME_PACK ADD LOGO VARCHAR2(120);
ALTER TABLE THEME_PACK ADD THUMBNAIL VARCHAR2(120);

create index SYS_APP_WGT_DEF_APPID_WTYPE on APP_WIDGET_DEFINITION (wtype, app_id, main);
 
ALTER TABLE APP_MODULE ADD SYSTEM VARCHAR2(120);
ALTER TABLE APP_MODULE ADD TENANT VARCHAR2(120);
ALTER TABLE APP_MODULE MODIFY SYSTEM_UNIT_ID NULL;



ALTER TABLE ORG_ELEMENT ADD SOURCE_ID VARCHAR2(32);
COMMENT ON COLUMN ORG_ELEMENT.SOURCE_ID
  IS '来源ID,单元实例的创建来源';



alter table APP_WIDGET_DEFINITION add app_page_id varchar2(120);
alter table APP_WIDGET_DEFINITION add version number(4,1) default 1.0;
comment on column APP_WIDGET_DEFINITION.app_page_id
  is '定义ID';
comment on column APP_WIDGET_DEFINITION.version
  is '版本号';

update app_widget_definition a set a.version = (select to_number(p.version) from app_page_definition p where p.uuid = a.app_page_uuid);
update app_widget_definition a set a.app_page_id = (select p.id from app_page_definition p where p.uuid = a.app_page_uuid);

-- Create/Recreate indexes 
CREATE INDEX SYS_APP_WGT_DEF_ID_APP_PAGE ON APP_WIDGET_DEFINITION (ID, APP_PAGE_ID, APP_PAGE_UUID);



COMMENT ON COLUMN USER_ACCOUNT.TYPE
  IS '1 平台管理员 2 租户管理员  3 开发运维 4 业务用户';
ALTER TABLE USER_ACCOUNT MODIFY TYPE DEFAULT 4;
