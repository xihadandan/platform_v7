<#macro optionalAttributes localField>
    data-input-type="${localField.inputType}"
    <#if localField.cfgKey??> data-cfg-key="${localField.cfgKey}"</#if>
    <#if localField.dateFormat??> data-date-format="${localField.dateFormat}"</#if>
    <#if localField.defaultValue??> data-default-value="${localField.defaultValue}"</#if>
    <#if localField.validationRule??> data-validation-rule="${localField.validationRule}"</#if>
    <#if localField.constraintValue??> data-constraint-value="${localField.constraintValue}"</#if>
</#macro>
<table class="fdext namespace" id="${id}" name="${name}" data-tenant-id="${tenantId}" data-group-code="${groupCode}">
    <tbody>
    <#assign fieldRenders = render.renders>
    <#list fieldRenders as field>
        <#if field.inputType = 'text'>
            <tr class="field">
                <td class="Label">${field.name}</td>
                <td><input class="fdext-${field.inputType}" id="${name}-${field.fieldName}"
                           name="${name}-${field.fieldName}" type="text" <@optionalAttributes localField = field/>/>
                </td>
            </tr>
        <#elseif field.inputType = 'textarea'>
            <tr class="field">
                <td class="Label">${field.name}</td>
                <td><textarea class="fdext-${field.inputType}" id="${name}-${field.fieldName}"
                              name="${name}-${field.fieldName}" <@optionalAttributes localField = field/>></textarea>
                </td>
            </tr>
        <#elseif field.inputType = 'date'>
            <tr class="field">
                <td class="Label">${field.name}</td>
                <td><input class="fdext-${field.inputType} Wdate" id="${name}-${field.fieldName}"
                           name="${name}-${field.fieldName}" type="text" <@optionalAttributes localField = field/>/>
                </td>
            </tr>
        <#elseif field.inputType = 'checkbox'>
            <tr class="field">
                <td class="Label">${field.name}</td>
                <td>
                    <#assign dataItems = field.dataItems>
                    <#if (dataItems?size > 0)>
                        <#list dataItems as item>
                            <input class="fdext-${field.inputType}" id="${name}-${field.fieldName}_${item.value}"
                                   name="${name}-${field.fieldName}" type="checkbox"
                                   value="${item.value}" <#if item_index = 0><@optionalAttributes localField = field/></#if>/>
                            <label for="${name}-${field.fieldName}_${item.value}">${item.label}</label>
                        </#list>
                    <#else>
                        <span>${field.noDataShow}</span>
                    </#if>
                </td>
            </tr>
        <#elseif field.inputType = 'radio'>
            <tr class="field">
                <td class="Label">${field.name}</td>
                <td>
                    <#assign dataItems = field.dataItems>
                    <#if (dataItems?size > 0)>
                        <#list dataItems as item>
                            <input class="fdext-${field.inputType}" id="${name}-${field.fieldName}_${item.value}"
                                   name="${name}-${field.fieldName}" type="radio"
                                   value="${item.value}" <#if item_index = 0><@optionalAttributes localField = field/></#if>/>
                            <label for="${name}-${field.fieldName}_${item.value}">${item.label}</label>
                        </#list>
                    <#else>
                        <span>${field.noDataShow}</span>
                    </#if>
                </td>
            </tr>
        <#elseif field.inputType = 'select'>
            <tr class="field">
                <td class="Label">${field.name}</td>
                <td>
                    <select class="fdext-${field.inputType}" id="${name}-${field.fieldName}"
                            name="${name}-${field.fieldName}" <@optionalAttributes localField = field/>>
                        <#assign dataItems = field.dataItems>
                        <#if (dataItems?size > 0)>
                            <option class="fdext-option" id="${name}-${field.fieldName}_OPTION" value="">请选择</option>
                            <#list dataItems as item>
                                <option class="fdext-option" id="${name}-${field.fieldName}_${item.value}"
                                        value="${item.value}">${item.label}</option>
                            </#list>
                        <#else>
                            <option>${field.noDataShow}</option>
                        </#if>
                    </select>
                </td>
            </tr>
        <#else>
            <tr class="field" style="display:none;">
                <td class="Label">${field.name}</td>
                <td>
                    <input class="fdext-${field.inputType}" id="${name}-${field.fieldName}"
                           name="${name}-${field.fieldName}" type="hidden" <@optionalAttributes localField = field/>/>
                </td>
            </tr>
        </#if>
    </#list>
    <tr class="field" style="display:none;">
        <td class="Label"></td>
        <td>
            <input id="${name}-uuid" name="${name}-uuid" type="hidden"/>
            <input id="${name}-dataUuid" name="${name}-dataUuid" type="hidden"/>
            <input id="${name}-groupCode" name="${name}-groupCode" type="hidden"/>
            <input id="${name}-fieldExtDefUuid" name="${name}-fieldExtDefUuid" type="hidden"/>
        </td>
    </tr>
    </tbody>
</table>