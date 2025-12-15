alter table dyform_form_definition add definition_vjson clob;
comment on column dyform_form_definition.definition_vjson is
'vue定义JSON信息';