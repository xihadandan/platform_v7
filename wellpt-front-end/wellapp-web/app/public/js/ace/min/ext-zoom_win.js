/**
 * 全屏代码编辑框
 */
ace.define("ace/ext/zoom_win", ["require", "exports", "module", "ace/editor", "ace/config", "ace/snippets"], function (require, exports, module) {

    var dom = require("ace/lib/dom");
    var lang = require("ace/lib/lang");

    var Editor = require("ace/editor").Editor;
    var winMaxCss = "fa fa-window-maximize";
    var winRestoreCss = "fa fa-window-restore"

    var Zoom = function (editor) {
        this.$editor = editor;
        this.$editorContainer = $(editor.container);
        //this.snippetManager = require("ace/snippets").snippetManager;
    };

    (function () {
        this.createZoomIcon = function () {
            var _this = this;
            var $editorContainer = this.$editorContainer;
            var $span = $("<span>", {
                "class": "ace_bigger " + winMaxCss,
                "style": "position: absolute; z-index: 100001; cursor: pointer; right: 16px;top:5px;display:none;",
                "title": "全屏( ctrl + ~ )",
                "aria-hidden": true
            });
            $editorContainer.append($span);

            $editorContainer.on('mouseover', function (e) {
                if ($(e.currentTarget).is('.ace_autocomplete')) {
                    return true;
                }
                $(this).find('.ace_bigger').show();
            });

            $editorContainer.on('mouseout', function () {
                $(this).find('.ace_bigger').hide();
            });

            $editorContainer.on('click', ".ace_bigger", function () {
                var $aceContainer = $editorContainer;
                if ($(this).hasClass(winRestoreCss)) {
                    $(this).removeClass(winRestoreCss).addClass(winMaxCss).attr('title', '全屏( ctrl + ~ )');
                    if (window.frameElement) {
                        $(window.frameElement).css({
                            'position': 'relative',
                            'z-index': 0,
                            'top': 0,
                            'left': 0,
                            'width': '100%',
                            'height': $(window.frameElement).data('css').height,
                        });

                        //在jquery-ui的弹窗内
                        if ($(window.frameElement).parents('.ui-dialog').length > 0) {
                            var $uiDialog = $(window.frameElement).parents('.ui-dialog');
                            $(window.frameElement).parents('.ui-dialog-content').css({
                                'overflow-x': 'hidden',
                                'overflow-y': 'auto'
                            });
                            $uiDialog.css('overflow', 'hidden');
                        }
                    }
                    $aceContainer.data('$parent').append($aceContainer);
                    $aceContainer.css($aceContainer.data('css'));

                    if (window.frameElement) {
                        $aceContainer.css('width', '100%');
                        $aceContainer.css('height', $(window.frameElement).height());
                    }

                    $(".bootbox").show();


                } else {
                    var widthFixed = 0;
                    var heightFixed = 0;
                    var leftFixed = parseInt($("body").css('margin-left'));//cms模块有偏移量
                    $(this).removeClass(winMaxCss).addClass(winRestoreCss).attr('title', '恢复窗口( ctrl + ~ )');
                    $aceContainer.data('css', {
                        top: 0,
                        'z-index': $aceContainer.css('z-index'),
                        left: '0',
                        width: $aceContainer.width(),
                        height: $aceContainer.height(),
                        position: $aceContainer.css('position')
                    });
                    $aceContainer.data('$parent', $aceContainer.parent());

                    if (window.frameElement) {
                        //当前处于iframe内，调整iframe的样式
                        $(window.frameElement).data('css', {
                            width: $(window.frameElement).outerWidth(),
                            height: $(window.frameElement).outerHeight(),
                        });
                        widthFixed = 2;
                        heightFixed = 2;
                        leftFixed = 0;

                        $(window.frameElement).css({
                            'position': 'fixed',
                            'top': 0,
                            'left': 0,
                            'z-index': 1990,
                            'width': window.top.innerWidth - widthFixed,
                            'height': window.top.innerHeight - heightFixed,
                        });
                        if ($(window.frameElement).parents('.cke_dialog').length > 0) {//在ck弹窗内
                            var $ck = $(window.frameElement).parents('.cke_dialog');
                            //ck弹窗的相对偏移量
                            $(window.frameElement).css({
                                'top': -$ck.offset().top,
                                'left': -$ck.offset().left,
                            });

                        }

                        //在jquery-ui的弹窗内(主要应用于管理后台
                        if ($(window.frameElement).parents('.ui-dialog').length > 0) {
                            var $uiDialog = $(window.frameElement).parents('.ui-dialog');
                            $(window.frameElement).parents('.ui-dialog-content').css('overflow', 'visible');
                            $uiDialog.css('overflow', 'visible');
                            //jquery-ui弹窗的相对偏移量
                            $(window.frameElement).css({
                                'top': -$uiDialog.offset().top - 40,
                                'left': -$uiDialog.offset().left,
                            });
                            widthFixed = 242;
                            heightFixed = 136;
                        }

                    }


                    $("body").append($aceContainer);//移到最外层，进行放大
                    $aceContainer.css({
                        'top': 0,
                        'z-index': 199000,
                        'left': -leftFixed,
                        'width': $(window).width() - widthFixed,
                        'height': $(window).height() - heightFixed,
                        'position': 'absolute'
                    });

                    $(".bootbox").hide();//要隐藏bootbox，否则无法输入
                }

                _this.$editor.resize();
                _this.$editor.focus();
            });


            $editorContainer.keydown(function (e) {
                if (e.keyCode == 192 && e.ctrlKey) {
                    $editorContainer.find('.ace_bigger').trigger('click');
                }
            });

            //组合快捷键切换窗口形态：ctrl+`
            this.$editor.commands.addCommand({
                name: 'saveCodeHisCommand',
                bindKey: {win: 'Ctrl-`'},
                exec: function (editor) {
                    $editorContainer.find('.ace_bigger').trigger('click');
                }
            });

        };

        this.init = function () {
            this.createZoomIcon();//创建
        };
    }).call(Zoom.prototype);

    exports.Zoom = Zoom;


    require("ace/config").defineOptions(Editor.prototype, "editor", {
        enableZoom: {
            set: function (val) {
                if (val) {
                    new Zoom(this).init();
                }
            },
            value: true
        }
    });


});
(function () {
    ace.require(["ace/ext/zoom_win"], function (m) {
        if (typeof module == "object" && typeof exports == "object" && module) {
            module.exports = m;
        }
    });
})();
            