alter table APP_LOGIN_PAGE_CONFIG drop column page_background_image;
alter table APP_LOGIN_PAGE_CONFIG add   page_background_image clob;
alter table APP_LOGIN_PAGE_CONFIG drop column page_logo;
alter table APP_LOGIN_PAGE_CONFIG add page_logo clob;
