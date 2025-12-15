select
<#list columns as column>
    t.${column.name} as ${column.name}<#if column_has_next>,</#if>
</#list>
from ${tableName} t where t.${pkName} = :${pkName}
