define(["constant", "commons", "appContext", "appModal"], function (constant, commons, appContext, appModal) {
    // 公告二开片段测试
    return {
        // 获取加载数据的操作ID
        getLoadDataActionId: function () {
            return "bnt_notice_dyform_get_data";
        },
        init: function (options) {
            appModal.info("初始化流程二开WorkViewTest2.js");
            var _self = this;
            _self._superApply(arguments);
        },
        // 重写提交方法
        submit: function () {
            var _self = this;
            var workData = _self.getWorkData();
            // 调用父类提交方法
            _self._superApply(arguments);
        }
    };
});