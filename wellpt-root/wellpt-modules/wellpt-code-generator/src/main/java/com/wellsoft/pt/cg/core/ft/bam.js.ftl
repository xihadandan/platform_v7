/***
* 如何描述该JS
*
* @author ${author}
* @date ${createDate}
*/
${r'$'}(function() {
// 表单选择器
var form_selector = "#${tableName}_form";
// ${entity}的VO类
var bean = {
uuid : null, // UUID
recVer : null, // 版本号
<#list propertyWithoutDefaults as property>
    ${property.name} : null<#if property_has_next>,</#if> // ${property.remark}
</#list>
};
var validator = $.common.validation.validate(form_selector, "${entityLowFisrt}");
${r'$'}("#list").jqGrid(${r'$'}.extend(${r'$'}.common.jqGrid.settings, {
url : ctx + '/common/jqgrid/query?queryType=${entityLowFisrt}',
colNames : ${colNames},
colModel : ${colModel},// 行选择事件
sortname : "",
onSelectRow : function(id) {
var rowData = ${r'$'}(this).getRowData(id);
get${entity}(rowData.uuid);
}
}));

// 根据UUID获取组织选择项
function get${entity}(uuid) {
var ${entityLowFisrt} = {};
${entityLowFisrt}.uuid = uuid;
JDS.call({
service : "${mgr}.getBean",
data : ${entityLowFisrt}.uuid,
success : function(result) {
bean = result.data;
${r'$'}("#btn_del").show();
${r'$'}(form_selector).json2form(bean);
validator.form();
}
});
}

// 新增操作
${r'$'}("#btn_add").click(function() {
${r'$'}(form_selector).clearForm(true);
${r'$'}("#btn_del").hide();
});

// 保存用户信息
${r'$'}("#btn_save").click(function() {
if (!validator.form()) {
return false;
}
${r'$'}(form_selector).form2json(bean);
JDS.call({
service : "${mgr}.saveBean",
data : bean,
success : function(result) {
alert("保存成功！");
// 删除成功刷新列表
${r'$'}("#list").trigger("reloadGrid");
}
});
});

// 删除操作
${r'$'}("#btn_del").click(function() {
if (bean.uuid == "" || bean.uuid == null) {
alert("请选择记录！");
return true;
}
var name = bean.name;
if (confirm("确定要删除选择项[" + name + "]吗？")) {
JDS.call({
service : "${mgr}.remove",
data : bean.uuid,
success : function(result) {
alert("删除成功!");
${r'$'}(form_selector).clearForm(true);
// 删除成功刷新列表
${r'$'}("#list").trigger("reloadGrid");
}
});
}
});
// 批量删除操作
${r'$'}("#btn_del_all").click(function() {
var rowids = ${r'$'}("#list").jqGrid('getGridParam', 'selarrrow');
if (rowids.length == 0) {
alert("请选择记录！");
return true;
}
if (confirm("确定要删除所选记录吗？")) {
JDS.call({
service : "${mgr}.removeAll",
data : [ rowids ],
success : function(result) {
alert("删除成功！");
${r'$'}(form_selector).clearForm(true);
// 删除成功刷新列表
$("#list").trigger("reloadGrid");
}
});
}
});

// 列表查询
${r'$'}("#query_${tableName}").keypress(function(e) {
if (e.keyCode == 13) {
$("#btn_query").trigger("click");
}
});

// JQuery UI按钮
$("input[type=submit], a, button", $(".btn-group")).button();
// JQuery UI页签
$(".tabs").tabs();
// $("li>a[href!='#tabs-0']", ".tabs").parent().hide();

${r'$'}("#btn_query").click(function(e) {
var queryValue = ${r'$'}("#query_${tableName}").val();
var postData = {
"queryPrefix" : "query",
"queryOr" : true,
"query_LIKES" : queryValue
};
// if (queryValue == "是") {
//    postData["query_EQB_show"] = true;
// } else if (queryValue == "否") {
//    postData["query_EQB_show"] = false;
// }
${r'$'}("#list").jqGrid("setGridParam", {
postData : null
});
${r'$'}("#list").jqGrid("setGridParam", {
postData : postData,
page : 1
}).trigger("reloadGrid");
});

// 界面布局
Layout.layout();
});