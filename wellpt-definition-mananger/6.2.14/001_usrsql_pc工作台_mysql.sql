ALTER TABLE APP_PAGE_DEFINITION ADD IS_PC CHAR(1) COMMENT  'pc端状态：1为启用；0为禁用';

update APP_PAGE_DEFINITION set is_pc = 2 where wtype = 'wMobilePage';
update APP_PAGE_DEFINITION set is_pc = 1 where wtype = 'wPage';

