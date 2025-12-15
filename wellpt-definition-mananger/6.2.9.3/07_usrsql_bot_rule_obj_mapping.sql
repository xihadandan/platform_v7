alter table BOT_RULE_OBJ_MAPPING add seq number(3) default 1;
comment on column BOT_RULE_OBJ_MAPPING.seq
  is '排序';
