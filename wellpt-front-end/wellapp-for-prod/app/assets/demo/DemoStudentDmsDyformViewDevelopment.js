/**
 * 学生表单二开
 */
define(["jquery", "server", "commons", "constant", "appContext", "appModal", "DmsDyformDocumentView"],
	function($, server, commons, constant, appContext, appModal, DmsDyformDocumentView) {
		var StringUtils = commons.StringUtils;

		var DemoStudentDmsDyformViewDevelopment = function() {
			DmsDyformDocumentView.apply(this, arguments);
		};

		commons.inherit(DemoStudentDmsDyformViewDevelopment, DmsDyformDocumentView, {
			// 指定加载表单数据的ActionSupport的方法ID
			getLoadDataActionId: function() {
				return "btn_get_student_dyform_data";
			},
			// 准备初始化表单
			prepareInitDyform: function(dyformOptions) {
				var _self = this;
			},
			onBeforeSetData:function(){
				this.dyform.getField("t_std_sex").bind("afterSetValue", function(){
					console.log("字段t_std_sex的事件afterSetValue被触发了");
				},true);
			},
			onAfterSetData:function(){
				console.log(this.dyform.getField("test_stu_name").getValue());
			}
		});

		return DemoStudentDmsDyformViewDevelopment;
	});
