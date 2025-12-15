/**
 * 学生表单二开
 */
define(["jquery", "server", "commons", "constant", "appContext", "appModal", "DmsDyformDocumentView"],
	function($, server, commons, constant, appContext, appModal, DmsDyformDocumentView) {
		var StringUtils = commons.StringUtils;

		var StudentDmsDyformViewDevelopment = function() {
			DmsDyformDocumentView.apply(this, arguments);
		};

		commons.inherit(StudentDmsDyformViewDevelopment, DmsDyformDocumentView, {
			// 指定加载表单数据的ActionSupport的方法ID
			getLoadDataActionId: function() {
				return "btn_get_student_dyform_data";
			},
			// 准备初始化表单
			prepareInitDyform: function(dyformOptions) {
				var _self = this;
				var documentData = _self.getDocumentData();
				// 表单是否显示为文本
				if(documentData.extras) {
					dyformOptions.displayAsLabel = documentData.extras.displayAsLabel;
					dyformOptions.isOnlyEditBasic = documentData.extras.isOnlyEditBasic;
				}
			},
			onBeforeSetData:function(){
				console.log(this.dyform.getControl("student_name").getValue());
				this.dyform.getControl("student_sex").bind("afterSetValue", function(){
					console.log("字段student_sex的事件afterSetValue被触发了");
				},true);
			},
			onAfterSetData:function(){
				console.log(this.dyform.getControl("student_name").getValue());
			}
		});

		return StudentDmsDyformViewDevelopment;
	});