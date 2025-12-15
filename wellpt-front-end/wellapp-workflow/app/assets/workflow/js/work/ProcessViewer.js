define([ "jquery", "server", "commons", "constant", "appContext", "appModal" ], function($, server, commons, constant,
        appContext, appModal) {
    var StringUtils = commons.StringUtils;
    // 办理过程
    var ProcessViewer = function(workView, options) {
        this.workView = workView;
        this.options = options;
        this.loaded = false;
    };
    $.extend(ProcessViewer.prototype, {
        // 初始化
        init : function() {
        },
        // 显示
        show : function() {
        },
        // 隐藏
        hide : function() {
        }
    });
    return ProcessViewer;
});