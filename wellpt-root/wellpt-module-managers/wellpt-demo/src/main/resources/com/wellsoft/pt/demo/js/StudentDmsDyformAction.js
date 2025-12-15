/**
 * 学生操作按钮二开
 */
define("StudentDmsDyformAction", ["jquery", "commons", "constant", "server", "appContext", "appModal", "DmsDyformActionBase"
], function ($, commons, constant, server, appContext, appModal, DmsDyformActionBase) {
    var StringUtils = commons.StringUtils;

    var StudentDmsDyformAction = function () {
        DmsDyformActionBase.apply(this, arguments);
    }

    var layoutId = "BASIC_INFO"; // 页签控件ID

    commons.inherit(StudentDmsDyformAction, DmsDyformActionBase, {
        // 保存
        btn_demo_save_student_info: function (options) {
            var _self = this;


            var validatePass = true;

            var dyformExplain = options.ui.dyform;//表单解析对象，封装了表单操作

            _self.setExtra("param1", "测试");//通过_self.setExtra传自定义参数给action

            appModal.info("该学生的姓名为:" + dyformExplain.getControl("student_name").getValue(), function () {
                //提交给对应的action
                _self.dmsDataServices.performed(options);
            });
        }
    });

    return StudentDmsDyformAction;
});