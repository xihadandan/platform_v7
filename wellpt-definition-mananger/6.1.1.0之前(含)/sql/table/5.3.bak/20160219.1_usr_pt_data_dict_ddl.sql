alter table cd_data_dict  add  EDITABLE       NUMBER(1) default 1 not null;
alter table cd_data_dict  add  DELETABLE      NUMBER(1) default 1 not null;
alter table cd_data_dict  add  CHILD_EDITABLE NUMBER(1) default 1 not null;
comment on column cd_data_dict.EDITABLE is '可编辑';
comment on column cd_data_dict.DELETABLE is '可删除';
comment on column cd_data_dict.CHILD_EDITABLE is '子项可编辑';