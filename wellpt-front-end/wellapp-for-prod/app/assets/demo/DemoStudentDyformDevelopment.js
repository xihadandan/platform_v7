/**
 * 学生信息表单二开
 */
define([ "jquery", "server", "commons", "constant", "DyformDevelopment",  ], function($,
		server, commons, constant, DyformDevelopment) {
	var StringUtils = commons.StringUtils;
	var Browser = commons.Browser;
	var DemoStudentDyformDevelopment = function() {
		DyformDevelopment.apply(this, arguments);
	};
	commons.inherit(DemoStudentDyformDevelopment, DyformDevelopment, {
		// 设值前
		beforeSetData : function(options) {
			var _self = this;
			var dyform = _self.getDyform();
			//dyformExplain.getField(MeetingRoomCommons.uf_oa_meeting_room.belong_obj_id).setValue(Browser.getQueryString("belongObjId"));
			console.log("beforeSetData");
		},
		onInit:function(){
      var _self = this;
      var dyform =  _self.getDyform();
			console.log("onInit");
		},

		afterSetData : function(options) {
      var _self = this;
      var dyform =  _self.getDyform();
			console.log("afterSetData");

		}
	});

	return DemoStudentDyformDevelopment;
});
