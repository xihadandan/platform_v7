${message}
<form id="fdext" class="fdext">
    <table>
        <tbody>
        <#list fieldDefines as field>
            <#if field.inputType = 'text'>
                <tr class="field">
                    <td width="110">${field.label}</td>
                    <td>${field.render}</td>
                </tr>
            <#elseif field.inputType = 'textarea'>
                <tr class="field">
                    <td width="110">${field.name}</td>
                    <td><input class="editableClass Wdate" name="${field.fieldName}" type="text"
                               value="${field.defaultValue}"/></td>
                </tr>
            <#elseif field.inputType = 'date'>
                <tr class="field">
                    <td width="110">${field.name}</td>
                    <td><input class="editableClass Wdate" name="${field.fieldName}" type="text"
                               value="${field.defaultValue}"/></td>
                </tr>
            <#elseif field.inputType = 'checkbox'>
                <tr class="field">
                    <td width="110">${field.name}</td>
                    <td><input class="editableClass" name="${field.fieldName}" type="checkbox" value="222"/>
                        <input class="editableClass" name="${field.fieldName}" type="checkbox" value="111"/></td>
                </tr>
            <#elseif field.inputType = 'radio'>
                <tr class="field">
                    <td width="110">${field.name}</td>
                    <td><input class="editableClass" name="${field.fieldName}" type="radio" value="222"/>
                        <input class="editableClass" name="${field.fieldName}" type="radio" value="111"/></td>
                </tr>
            <#elseif field.inputType = 'select'>
                <tr class="field">
                    <td width="110">${field.name}</td>
                    <td><select class="editableClass" name="${field.fieldName}" value="222" style="width:98%;"></select>
                    </td>
                </tr>
            </#if>
        </#list>
        </tbody>
    </table>
</form>