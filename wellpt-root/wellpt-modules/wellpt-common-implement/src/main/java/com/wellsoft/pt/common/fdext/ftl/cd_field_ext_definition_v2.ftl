<table id="" class="fdext">
    <tbody>
    <#list fieldDefines as field>
        <tr class="field">
            <td width="110">${field.label}</td>
            <td>${field.render}</td>
        </tr>
    </#list>
    </tbody>
</table>
