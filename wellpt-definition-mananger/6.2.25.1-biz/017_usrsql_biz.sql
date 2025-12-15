alter table BIZ_DEFINITION_TEMPLATE
    add ITEM_ID varchar2(50 char);
alter table BIZ_DEFINITION_TEMPLATE
    add NODE_ID varchar2(50 char);

COMMENT
    ON COLUMN BIZ_DEFINITION_TEMPLATE.TYPE IS '模板类型，10业务流程表单配置模板、20过程节点表单配置模板、30事项表单配置模板、40事项集成工作流配置模板、50事项配置模板、60阶段配置模板';
COMMENT
    ON COLUMN BIZ_DEFINITION_TEMPLATE.ITEM_ID IS '事项ID，模板类型为50时有值';
COMMENT
    ON COLUMN BIZ_DEFINITION_TEMPLATE.NODE_ID IS '阶段ID，模板类型为60时有值';
