<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/pt/common/taglibs.jsp"%>
<form:form id="form_${tableName}" commandName="${entityLowFisrt}Bean">
    <input type="hidden" id="uuid" name="uuid"/>
    <input type="hidden" id="recVer" name="recVer"/>
    <table width="100%">
        <tbody>
        <#list propertyWithoutDefaults as property>
            <#if property_index = 0></#if>
            <#if !property_has_next></#if>
            <tr align="center">
                <td>${property.remark} <font color="red">*</font></td>
                <td><input type="text" id="${property.name}" name="${property.name}"/></td>
            </tr>
        </#list>
        </tbody>
    </table>
</form:form>