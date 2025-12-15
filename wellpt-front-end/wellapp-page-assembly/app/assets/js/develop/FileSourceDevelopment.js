define(["jquery", "constant", "commons", "server", "appContext"], function ($, commons, constant, server, appContext) {
    var FileSourceDevelopment = function (wellFileUpload) {
        this.wellFileUpload = wellFileUpload;
    };
    // 接口方法
    $.extend(FileSourceDevelopment.prototype, {
        // 返回上传组件对象
        getWellFileUpload: function () {
            return this.wellFileUpload;
        },

        getDialogParams: function () {
            return {};
        },
        getDialogContentHtml: function () {
            return '<div></div>';//自定义弹窗内容
        },
        excJs: function ($dialogContent) {
            //绑定html页面元素事件
        },
        /**
         * {fileId:'',fileName:'',fileSize:0}
         * @returns {Array}
         */
        getFiles: function () {
            return [];
        },

        //是否覆盖
        getIsCover: function () {
            return false;
        },

        _clickEvent: function () {
            var _self = this;
            return function () {
                var params = _self.getDialogParams();
                var defaultParams = {
                    title: "文件选择",
                    bgiframe: true,
                    autoOpen: true,
                    resizable: true,
                    stack: true,
                    width: 980,
                    height: 620,
                    modal: true,
                    overlay: {
                        background: '#000',
                        opacity: 0.5
                    }
                };
                params = $.extend(defaultParams, params);

                var targetWindow = params.targetWindow ? params.targetWindow : (window.top.appModal ? window.top : window);

                var fileDialog = targetWindow.$(_self.getDialogContentHtml()).dialog($.extend(params, {
                    buttons: {
                        "确定": function () {
                            var files = _self.getFiles();
                            if (false === $.isArray(files)) {
                                alert('返回值需为数组');
                                return;
                            }
                            var msg = _self.wellFileUpload.addFilesByFiles(files, _self.fileSource.icon, _self.getIsCover());
                            if (msg) {
                                appModal.info(msg);
                            } else {
                                fileDialog.dialog("close");
                            }

                        },
                        "取消": function () {
                            fileDialog.dialog("close");
                        }
                    },
                    open: function (e) {
                        _self.excJs($(e.target));
                    },
                    close: function () {
                        fileDialog.remove();
                        return true;
                    }

                }));
                fileDialog.parent().find('.ui-dialog-buttonset').css('text-align', 'right');
                fileDialog.parent().find('.ui-dialog-buttonset button').css('float', 'none');

                _self.fileDialog = fileDialog;
            }
        },
        // // 组件准备
        // prepare: function () {
        // },
        // // 组件创建
        // create: function () {
        // },
        // 组件初始化
        init: function () {
        },
        _setInitData: function (fileSource) {
            this.fileSource = fileSource;
        },
        // 调用了未知的二开方法回调
        onUnkonwnDevelopmentMethodInvoked: function (method, methodArgs) {
        }
    });
    return FileSourceDevelopment;
});