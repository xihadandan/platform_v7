update ${tableName} t set
<#list columns as column>
    t.${column.name} = :${column.name}<#if column_has_next>,</#if>
</#list>
where
<#list pkNames as pkName>
    t.${pkName} = :${pkName}<#if pkName_has_next> and </#if>
</#list>