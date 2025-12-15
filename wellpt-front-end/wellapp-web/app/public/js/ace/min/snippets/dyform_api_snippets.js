/**
 * 表单的api相关snippet
 */

ace.define("ace/snippets/dyform_api_snippets", ["require", "exports", "module"], function (require, exports, module) {
    "use strict";

    exports.snippetText = "# \n\
# 表格组件api \n\
snippet DyformFacade.setBlockShow : 设置表单区块显示\n\
	DyformFacade.setBlockShow(${1:区块编码} , ${2:表单ID(可选)})\n\
snippet DyformFacade.setBlockHide : 设置表单区块隐藏\n\
	DyformFacade.setBlockHide(${1:区块编码} , ${2:表单ID(可选)})\n\
snippet DyformFacade.setControlShowByFieldName : 显示控件\n\
	DyformFacade.setControlShowByFieldName(${1:[字段编码数组]} , ${2:数据UUID(可选，操作从表) , ${3:表单ID(可选)})\n\
snippet DyformFacade.setControlCellShow : 显示字段控件的单元格(包括控件对应的标签单元格)\n\
	DyformFacade.setControlCellShow(${1:[字段编码数组]} , ${2表单ID(可选)})\n\
snippet DyformFacade.isUniqueForFields : 表单组合字段唯一性校验 \n\
	DyformFacade.isUniqueForFields(${1:数据UUID(可选)} , ${2:表单ID} , ${3:{'field1':'value1','field2':'value2'}} , ${4:过滤类型 D 部门/C 当前用户/O 组织单位/A 全部})\n\
snippet DyformFacade.addFileIcon : 给控件加图标 \n\
	DyformFacade.addFileIcon(${1:字段编码} , ${2:数据UUID})\n\
snippet DyformFacade.getControlValue : 获取控件值\n\
	DyformFacade.getControlValue(${1:字段编码} , ${2:数据UUID} , ${3:表单ID} , ${4:成功回调函数} , ${5:失败回调函数})\n\
snippet DyformFacade.getControlType : 获取主表控件类型 \n\
	DyformFacade.getControlType(${1:字段编码} , ${2:表单ID})\n\
snippet DyformFacade.isControlEmpty : 判断表单控件值是否为空\n\
	DyformFacade.isControlEmpty(${1:字段编码} , ${2:数据UUID(可选)} , ${3:表单ID(可选)})\n\
snippet DyformFacade.isInitCompleted : 判断控件是否已初始化完成\n\
	DyformFacade.isInitCompleted(${1:字段编码} , ${2:数据UUID(可选)} , ${3:表单ID(可选)})\n\
snippet DyformFacade.setControlNorequire : 取消表单控件必填\n\
	DyformFacade.setControlNorequire(${1:字段编码} , ${2:数据UUID(可选)} , ${3:表单ID(可选)})\n\
snippet DyformFacade.isGranted : 判断当前用户是否有指定类型的功能权限\n\
	DyformFacade.isGranted(${1:产品集成树相关树节点路径} , ${2:功能类型})\n\
snippet DyformFacade.hasRole : 判断当前用户是否有指定类型的功能权限\n\
	DyformFacade.hasRole(${1:角色ID})\n\
snippet DyformFacade.getDateByDateDaysParam : 返回指定日期多少天前后的日期\n\
	DyformFacade.getDateByDateDaysParam(${1:指定日期} , ${2:天数} , ${3:计算方式 before/after})\n\
snippet DyformFacade.getDateByDateWeeksParam : 返回指定日期多少周前后的日期\n\
	DyformFacade.getDateByDateWeeksParam(${1:指定日期} , ${2:周数} , ${3:计算方式 before/after})\n\
snippet DyformFacade.getDateByDateMonthsParam : 返回指定日期多少个月前后的日期\n\
	DyformFacade.getDateByDateMonthsParam(${1:指定日期} , ${2:月数} , ${3:计算方式 before/after})\n\
snippet DyformFacade.getDateByDateYearsParam : 返回指定日期多少年前后的日期\n\
	DyformFacade.getDateByDateYearsParam(${1:指定日期} , ${2:年数} , ${3:计算方式 before/after})\n\
snippet DyformFacade.getDateStrByDateAndFormat : 格式化日期输出字符串\n\
	DyformFacade.getDateStrByDateAndFormat(${1:指定日期} , ${2:日期格式})\n\
snippet DyformFacade.getSpecificDateByDateFormat : 格式化日期输出日期\n\
	DyformFacade.getSpecificDateByDateFormat(${1:指定日期} , ${2:日期格式})\n\
snippet DyformFacade.getSpecificDateByDatewkhrParam : 返回指定日期多少个工作日前后的日期\n\
	DyformFacade.getSpecificDateByDatewkhrParam(${1:指定日期} , ${2:工作日天数} , ${3:计算方式 before/after})\n\
snippet DyformFacade.getWeekDayByDate : 返回指定日期对应的星期几\n\
	DyformFacade.getWeekDayByDate(${1:指定日期})\n\
snippet DyformFacade.setMinDate : 日期控件设置最小时间值\n\
	DyformFacade.setMinDate(${1:字段编码} , ${2:最小时间字符串} , ${3:数据UUID(可选)})\n\
snippet DyformFacade.setMaxDate : 日期控件设置最大时间值\n\
	DyformFacade.setMaxDate(${1:字段编码} , ${2:最大时间字符串} , ${3:数据UUID(可选)})\n\
snippet DyformFacade.setControlRequire : 设置控件为必填\n\
	DyformFacade.setControlRequire(${1:字段编码} , ${2:数据UUID(可选)} , ${3:表单ID(可选)})\n\
snippet DyformFacade.setControlLabel : 设置控件为显示文本\n\
	DyformFacade.setControlLabel(${1:字段编码} , ${2:数据UUID(可选)} , ${3:表单ID(可选)})\n\
snippet DyformFacade.setControlEditable : 设置控件为可编辑\n\
	DyformFacade.setControlEditable(${1:字段编码} , ${2:数据UUID(可选)} , ${3:表单ID(可选)})\n\
snippet DyformFacade.setControlValue : 设置控件值\n\
	DyformFacade.setControlValue(${1:字段编码} , ${2:值对象} , ${3:数据UUID(可选)} , ${4:表单ID(可选)})\n\
snippet DyformFacade.setSelectOption : 设置下拉控件的选项\n\
	DyformFacade.setSelectOption(${1:字段编码数组} , ${2:选项数组} , ${3:数据UUID(可选)} , ${4:表单ID(可选)})\n\
snippet DyformFacade.setControlRowShow : 显示指定控件的行\n\
	DyformFacade.setControlRowShow(${1:字段编码数组} , ${2:表单ID(可选)})\n\
snippet DyformFacade.setControlHideByFieldName : 隐藏指定的表单控件\n\
	DyformFacade.setControlHideByFieldName(${1:字段编码数组} , ${2:数据UUID(可选)} , ${3:表单ID(可选)})\n\
snippet DyformFacade.setControlCellHide : 隐藏控件的单元格\n\
	DyformFacade.setControlCellHide(${1:字段编码数组} , ${2:表单ID(可选)})\n\
snippet DyformFacade.setControlRowHide : 隐藏控件对应的行\n\
	DyformFacade.setControlRowHide(${1:字段编码数组} , ${2:表单ID(可选)})\n\
snippet DyformFacade.isControlExist : 控件是否存在，返回true或者false\n\
	DyformFacade.isControlExist(${1:字段编码} , ${2:表单ID(可选)})\n\
snippet pinYinHeadChar : 输出中文的首字母\n\
	pinYinHeadChar(${1:中文字符})\n\
snippet pinYin : 输出中文的全拼\n\
	pinYin(${1:中文字符})\n\
snippet getCurrentUserId : 获取当前登录用户的ID\n\
	SpringSecurityUtils.getCurrentUserId()\n\
	#其他常用的业务逻辑代码\n\
snippet _@setPinyin : 当前字段的拼音设置到其他字段上(afterSetValue事件使用)\n\
	if(\\$this.value!=''){\n\
	     DyformFacade.setControlValue('${1:拼音保存字段}' , pinYin(\\$this.value));\n\
	}\n\
snippet _@noRoleAuthHide : 当用户没有角色权限时候隐藏该控件(beforeInit事件使用)\n\
	if(!DyformFacade.hasRole('${1:角色编码}')){\n\
	     columnProperty.showType=dyshowType.hide;\n\
	}\n\
snippet _@hideOrShowBlock : 隐藏或者显示区块(表单初始化前事件使用)\n\
	formDefinition.blocks.${1:区块编码}.hide=${2:是否显示，是 true 否 false};\n\
snippet _@refreshTreeControl : 树形控件数据重新按条件加载并刷新\n\
	var treeControl=DyformFacade.getControl('${1:树形控件字段编码}');\n\
	treeControl.setDefaultCondition(\"${2:默认查询条件}\");\n\
	treeControl.refresh();\n\
snippet _@refreshComboSelectControl : 下拉控件数据仓库重新按条件加载并刷新\n\
	var selectControl=DyformFacade.getControl('${1:下拉控件字段编码}');\n\
	selectControl.addDsConditions(${2:条件数组});\n\
	selectControl.refresh();\n\
snippet _@closeAndRefreshParent : 关闭表单页面，并提示消息与刷新父窗口\n\
	appModal.alert({message:'${1:提示消息内容}',type:'info',resultCode:0});//提示消息\n\
	appContext.getWindowManager().closeAndRefreshParent();\n\
snippet _@generateSerailNumber : 生成流水号\n\
	DyformFacade.getControl('${1:流水号字段}')[0].generateSerialNumber('${2:流水号ID}',${3:是否占用 true或者false},${4:附加流水号渲染参数键值对{key1:value1}});\
	";

    exports.scope = "dyform_api_snippets";


});
(function () {
    ace.require(["ace/snippets/dyform_api_snippets"], function (m) {
        if (typeof module == "object" && typeof exports == "object" && module) {
            module.exports = m;
        }
    });
})();
