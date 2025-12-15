// 1、客户端版本检测提示更新
require(["jquery", "constant", "commons"], function () {
    var jQuery = require("jquery");
    var constant = require("constant");
    var commons = require("commons");
    var env = WebApp.environment || {};
    var lastestVersion = env.app_version;
    var storage = commons.StorageUtils.getStorage(3);
    var localVersion = storage.getItem("app_version");

    function showMask(message, container, timer, hasMask) {
        if ($("#appModalLoading").size() > 0) {
            return;
        }
        // 提示文本
        if (commons.StringUtils.isBlank(message)) {
            message = "数据加载中...";
        }
        // 容器
        if (container == null) {
            container = "body";
        }
        // timer
        if (timer == null) {
            timer = 20000;
        }
        if (hasMask == null) {
            hasMask = true;
        }

        // 容器定位
        var $box = $(container);

        // 遮罩层
        var overlay = '<div class="widget-box-overlay" id="appModalLoading"><div class="loading-overlay">'
            + '<div id="loadingGif" class="loading-overlay-gif"></div>' +
            '<div class="rectangle-bounce">' +
            '<div class="rect1"></div>' +
            '<div class="rect2"></div>' +
            '<div class="rect3"></div>' +
            '<div class="rect4"></div>' +
            '<div class="rect5"></div>' +
            '</div>' +
            '<div class="loading-text">' + message
            + '</div></div></div>';
        $box.append(overlay);
        if (!hasMask) {
            $box.find('.widget-box-overlay').css({
                background: "initial"
            }).find(".loading-text").css({
                color: "#999"
            })
        }
        // 自定义事件移除遮罩层
        $box.one('reloaded.mask.widget', function () {
            $box.find('.widget-box-overlay').remove();
            if (!hasMask) {
                $box.find('.widget-box-overlay').css({
                    background: "initial"
                })
            }
        });
        // // 超时移除遮罩层
        var maskTimeout = setTimeout(function () {
            $box.trigger('reloaded.mask.widget');
        }, timer);
        $box.data('maskTimeout', maskTimeout);
    };

    function hideMask(container) {
        // 容器
        if (container == null) {
            container = "body";
        }

        var $box = $(container);
        $box.trigger('reloaded.mask.widget');
        window.clearTimeout($box.data('maskTimeout'));
    }


    if (localVersion != lastestVersion) {
        var msg = '系统登录中，请稍候...';
        try {
            if (window.parent.$.ligerui.controls.Tab) {
                //在旧版的管理后台iframe框架内打开的页面
                msg = '';
            }
        } catch (e) {
        }
        showMask(msg);
        jQuery(document).one(constant.WIDGET_EVENT.PageContainerCreationComplete, function () {
            hideMask();
        });
        // 存储版本
        storage.setItem("app_version", lastestVersion);
    }
});