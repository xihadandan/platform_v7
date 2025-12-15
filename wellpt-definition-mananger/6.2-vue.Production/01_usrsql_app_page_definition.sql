alter table app_page_definition add uni_app_definition_json clob;
comment on column APP_PAGE_DEFINITION.UNI_APP_DEFINITION_JSON is
'uni-app定义JSON信息';