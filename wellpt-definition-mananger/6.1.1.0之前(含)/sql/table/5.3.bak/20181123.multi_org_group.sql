--添加使用范围人员显示值字段
alter table multi_org_group ADD user_range_display VARCHAR2(255);
--添加使用范围人员真实值字段
alter table multi_org_group ADD user_range_real VARCHAR2(255);