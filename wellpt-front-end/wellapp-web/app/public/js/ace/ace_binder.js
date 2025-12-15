(function ($) {


    var AceBinder = function () {
    };

    var defaultOptions = {
        iframeId: null,//用iframe嵌套，为了ace编辑器的样式不被公用的样式影响
        id: "aceCodeEditor",
        minHeight: '300',
        mode: 'javascript',
        theme: 'chrome',
        readOnly: false,
        valueChange: $.noop,//值变化函数事件
        container: 'body',
        varSnippets: null,//内置变量snippets
        codeHis: {             //代码历史记录
            enable: false,
            codeType: null,
            relaBusizUuid: null
        },
        value: '',//默认值
    }

    AceBinder.prototype.init = function (options) {
        this.options = $.extend({}, defaultOptions, options);
        var _this = this;
        if ($(this.options.container).attr('ace-mode')) {
            this.options.mode = $(this.options.container).attr('ace-mode');
        }
        if ($(this.options.container).attr('ace-theme')) {
            this.options.theme = $(this.options.container).attr('ace-theme');
        }
        if (this.options.iframeId) {
            var codeHis = JSON.stringify(this.options.codeHis);
            //url里面存在 json 字符串，对 url 进行编码 by qilin.liu 2018-12-30
            var editorUrl = encodeURI(staticPrefix + "/js/ace/ace_editor.html?id=" + this.options.id
                + "&mode=" + this.options.mode + "&theme=" + this.options.theme
                + "&codeHis=" + codeHis + "&varSnippets=" + this.options.varSnippets+"&readOnly="+this.options.readOnly);
            var $iframe = $("<iframe>", {
                "name": this.options.iframeId,
                "id": this.options.iframeId,
                "src": editorUrl,
                "style": "width: 100%;height: 300px; border: 1px solid #a9a9a9;"
            });

            if (!window.aceEvent) {
                window.aceEvent = {};
            }
            window.aceEvent[_this.options.id] = {
                'valueChange': function (v) {
                    _this.options.valueChange(v);
                },
                'defaultValue': function () {
                    return _this.options.value != '' ? _this.options.value : '';
                }
            }


            $(this.options.container).append($iframe);

        } else {
            var $pre = $("<pre>", {
                "id": _this.options.id,
                "style": "min-height:300px;margin:0px;"
            });
            $(_this.options.container).append($pre);

            var $script = $("<script>", {
                "type": "text/javascript",
                "src": ctx + "/resources/ace/min/ace.js"
            });
            var $script2 = $("<script>", {
                "type": "text/javascript",
                "src": ctx + "/resources/ace/min/ext-language_tools.js"
            });
            var $script3 = $("<script>", {
                "type": "text/javascript",
                "src": ctx + "/resources/ace/min/ext-zoom_win.js"
            });
            $("head").append($script, $script2, $script3);

            //代码编辑框初始化
            var aceCodeEditor = ace.edit(_this.options.id);
            ace.config.set('basePath', ctx + "/resources/ace/min");
            aceCodeEditor.setTheme("ace/theme/" + _this.options.theme);
            aceCodeEditor.session.setMode("ace/mode/" + _this.options.mode);

            //启用提示菜单
            ace.require("ace/ext/language_tools");
            ace.require("ace/ext/zoom_win");
            aceCodeEditor.setOptions({
                enableBasicAutocompletion: true,
                enableSnippets: true,
                enableVarSnippets: _this.options.varSnippets,
                enableLiveAutocompletion: true,
                showPrintMargin: false,
                enableCodeHis: _this.options.codeHis,
                readOnly:_this.options.readOnly
            });
            if (_this.options.value) {
                aceCodeEditor.setValue(_this.options.value);
            }
            $pre.data('aceCodeEditor', aceCodeEditor);

            $("#" + _this.options.id).on('keyup', 'textarea', function () {
                _this.options.valueChange(aceCodeEditor.getValue());
            });

        }
        return this;
    }

    AceBinder.prototype.getContainer = function () {
        return $(this.options.container);
    }

    AceBinder.prototype.getValue = function () {
        if (this.options.iframeId) {
            return window[this.options.iframeId].aceCodeEditor.getValue();
        } else {
            $("#" + this.options.id, $(this.options.container)).data('aceCodeEditor').getValue();
        }
    }

    AceBinder.prototype.setValue = function (v) {
        if (this.options.iframeId) {
            this.options.value=v;
            var _iframe = window[this.options.iframeId];
            if (_iframe && _iframe.aceCodeEditor) {
                _iframe.aceCodeEditor.setValue(v);
            }
        } else {
            $("#" + this.options.id, $(this.options.container)).data('aceCodeEditor').setValue(v);
        }
    }

    $.fn.aceBinder = function (options) {
        return new AceBinder().init(options);
    }

})(jQuery);