insert into ${tableName} (
<#list columns as column>
    ${column.name}<#if column_has_next>,</#if>
</#list>
)
values (
<#list columns as column>
    :${column.name}<#if column_has_next>,</#if>
</#list>
)