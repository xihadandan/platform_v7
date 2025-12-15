/**
 * 车辆管理-用车申请表单二开
 */
define(["jquery", "server", "commons", "constant", "DyformDevelopment"], function ($, server, commons, constant, DyformDevelopment) {
    var StringUtils = commons.StringUtils;
    var MyCalendarDyformDevelopment = function () {
        DyformDevelopment.apply(this, arguments);
    };
    commons.inherit(MyCalendarDyformDevelopment, DyformDevelopment, {
        // 设值前
        beforeSetData: function (options) {
            //var isDefault = DyformFacade.getControlValue("isDefault");
        },


        afterSetData: function (options) {
            var _self = this;
            var dyformExplain = _self.getDyform();
            var $dyform = dyformExplain.$dyform;
            var isDefault = DyformFacade.getControlValue("is_default");
            if ("1" == isDefault) {
                DyformFacade.setControlLabel(["calendar_name"]);
            }
        }
    });

    return MyCalendarDyformDevelopment;
});