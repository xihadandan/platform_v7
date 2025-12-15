insert into ${tableName}
(
<#list columns as col>
    ${col} <#sep>,
</#list>
)
values
(
:${col} <#sep>,
)