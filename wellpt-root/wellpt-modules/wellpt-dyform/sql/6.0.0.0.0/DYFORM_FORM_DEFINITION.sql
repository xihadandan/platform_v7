alter table dyform_form_definition drop column module_id;
alter table dyform_form_definition drop column module_name;
alter table dyform_form_definition drop column print_template_id;
alter table dyform_form_definition drop column print_template_name;
alter table dyform_form_definition drop column display_form_model_name;
alter table dyform_form_definition drop column display_form_model_id;
alter table dyform_form_definition drop column custom_js_module_name;
alter table dyform_form_definition drop column definition_pt_version;
alter table DYFORM_FORM_DEFINITION drop column form_display;
alter table dyform_form_definition rename column outer_id to id;
alter table dyform_form_definition rename column name to table_name;
alter table dyform_form_definition rename column display_name to name;

-- Add/modify columns 
alter table DYFORM_FORM_DEFINITION add form_type VARCHAR2(3);
alter table DYFORM_FORM_DEFINITION add p_form_uuid VARCHAR2(50);
alter table DYFORM_FORM_DEFINITION add ref_designer_form_uuid VARCHAR2(50);
-- Add comments to the columns 
comment on column DYFORM_FORM_DEFINITION.form_type
  is 'P:存储单据, V:展现单据, M: 手机单据,T:模板单据';
comment on column DYFORM_FORM_DEFINITION.p_form_uuid
  is '如果单据类型为P(存储单据)时，这个字段的值为null';
comment on column DYFORM_FORM_DEFINITION.ref_designer_form_uuid
  is '表示设计器的内容是参照哪个单据(存储单据/展现单据/模块单据)';
