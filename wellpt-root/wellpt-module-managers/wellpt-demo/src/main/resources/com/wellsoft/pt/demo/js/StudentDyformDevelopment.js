/**
 * 学生信息表单二开
 */
define(["jquery", "server", "commons", "constant", "DyformDevelopment",], function ($,
                                                                                    server, commons, constant, DyformDevelopment) {
    var StringUtils = commons.StringUtils;
    var Browser = commons.Browser;
    var StudentDyformDevelopment = function () {
        DyformDevelopment.apply(this, arguments);
    };
    commons.inherit(StudentDyformDevelopment, DyformDevelopment, {
        // 设值前
        beforeSetData: function (options) {
            var _self = this;
            var dyformExplain = _self.getDyform();
            //dyformExplain.getField(MeetingRoomCommons.uf_oa_meeting_room.belong_obj_id).setValue(Browser.getQueryString("belongObjId"));
            console.log("beforeSetData");
        },
        onInit: function () {
            console.log("onInit");
        },

        afterSetData: function (options) {
            console.log("afterSetData");
        }
    });

    return StudentDyformDevelopment;
});