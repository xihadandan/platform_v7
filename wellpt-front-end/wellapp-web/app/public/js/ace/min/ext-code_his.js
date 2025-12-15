/**
 * 全屏代码编辑框
 */
ace.define("ace/ext/code_his", ["require", "exports", "module", "ace/editor", "ace/config"], function (require, exports, module) {

    var dom = require("ace/lib/dom");
    var lang = require("ace/lib/lang");
    var Editor = require("ace/editor").Editor;

    var expandClass = "fa fa-caret-left";
    var collapseClass = "fa fa-caret-right";

    var CodeHis = function (editor) {
        this.$editor = editor;
        this.$editorContainer = $(editor.container);

        if (typeof define === "function" && define.amd) {
            this.layer = requirejs('layer');
            this.JDS = requirejs('server').JDS;
            this.appModal = requirejs('appModal');
            this.layer.ready(function () {//会自动加载css
            });
        } else {
            this.JDS = window.JDS ? window.JDS : window.parent.JDS;
            this.appModal = window.appModal ? window.appModal : window.parent.appModal;
            this.layer = window.frameElement ? window.parent.layer : layer;//iframe的情况下取父层的弹窗
        }

        this._$changeCnt = 0;
        this.focused = false;
    };

    (function () {

        this.conf = {};

        this.createCodeHisBar = function () {
            var _this = this;

            //监听文档变更的事件
            this.$editor.on('change', function (obj) {
                if ((_this._$changeCnt++) == 0 && !_this.focused) {//首次变更，应为设初始值
                    _this._$originalValue = _this.$editor.getSession().doc.getValue();
                }
            });

            this.$editor.on('focus', function () {
                _this.foucsed = true;
            });

            var $editorContainer = this.$editorContainer;
            $editorContainer.on('mouseover', function () {
                if ($(this).find('.ace_code_his_container').length == 0) {
                    var $span = $("<span>", {
                        "class": "ace_code_his_expand " + expandClass,
                        "style": "position: absolute; z-index: 100002; cursor: pointer; right: 1px;top:40%;font-size:20px;",
                        "title": "展开历史版本",
                        "aria-hidden": true
                    });

                    var $div = $("<div>", {
                        "class": "ace_code_his_container",
                        "style": "height: 100%;width: 260px;border: 1px solid #787870;position: absolute;z-index: 100001;overflow-y:auto;" +
                        "right:-262px;background-color: white;border-top-left-radius: 5px;border-bottom-left-radius: 5px;"
                    });

                    var $table = $("<table>", {
                        "style": "position:absolute;top:1px;margin-left:2px;width:98%;cursor:pointer;overflow:hidden;",
                        "class": "table table-condensed table-hover",
                    });
                    $table.append($("<thead>").append($("<tr>").append($("<th>").text('修改时间'), $("<th>").text('修改人'))));
                    $(this).append($span, $div.append($table));
                }
                $editorContainer.find('.ace_code_his_expand').show();

                $(".ace_code_his_container").scroll(function () {
                    var nScrollHight = $(this)[0].scrollHeight;
                    var nScrollTop = $(this)[0].scrollTop;
                    if (nScrollTop + $(this).height() >= nScrollHight) {
                        if ($(this).find('table .load_more').length == 0) {
                            $(this).find('table').append($("<tr>", {
                                "class": "load_more",
                                "style": "text-align:center;"
                            }).append(
                                $("<td>", {"colspan": "2"}).text('加载更多...')
                            ));
                        }
                    }


                });
            });

            $editorContainer.on('mouseout', function () {
                if ($editorContainer.find('.ace_code_his_expand').hasClass(expandClass)) {
                    $editorContainer.find('.ace_code_his_expand').hide();
                }

            });

            $editorContainer.on('click', ".ace_code_his_expand", function () {
                var $icon = $editorContainer.find(".ace_code_his_expand");
                if ($(this).hasClass(expandClass)) {
                    $editorContainer.find('.ace_code_his_container').animate({"right": "0px"}, 500);
                    $icon.removeClass(expandClass).addClass(collapseClass);
                    $icon.animate({"right": 252}, 500);
                    $icon.attr('title', '折叠历史版本');
                    if ($(this).attr('loaded') == undefined || $(this).attr('loaded') == 'false') {
                        $(this).attr('loaded', true);
                        $editorContainer.find('.ace_code_his_container table').data('currentPage', 1);
                        _this.$editorContainer.find('.ace_code_his_container table tbody').empty();
                        _this.loadCodeHisRecords();
                    }
                } else {
                    $editorContainer.find('.ace_code_his_container').animate({"right": "-262px"}, 500);
                    $icon.removeClass(collapseClass).addClass(expandClass);
                    $icon.animate({"right": 1}, 500);
                    $icon.attr('title', '展开历史版本');
                }
            });

            $editorContainer.on('mouseenter', "table tr:gt(0)", function () {
                $(this).find('.code_tr_operation').animate({"right": 0}, 400);
            });
            $editorContainer.on('mouseleave', "table tr:gt(0)", function () {
                $(this).find('.code_tr_operation').animate({"right": -60}, 400);
            });

            $editorContainer.on('click', "table .see_code", function () {
                _this.popCodeDialog($(this).parents('tr').attr('uuid'));

            });
            $editorContainer.on('click', "table .apply_code", function () {
                _this.setHisCode2Editor($(this).parents('tr').attr('uuid'));
            });

            $editorContainer.on('click', "table .load_more", function () {
                var $table = _this.$editorContainer.find('.ace_code_his_container table');
                $table.data('currentPage', $table.data('currentPage') + 1);
                _this.loadCodeHisRecords();

            });

            //保存代码快捷键
            this.$editor.commands.addCommand({
                name: 'saveCodeHisCommand',
                bindKey: {win: 'Ctrl-S'},
                exec: function (editor) {
                    _this.saveCodeHis(true, function () {
                        _this.layer.msg('保存代码', {zIndex: 999999999});
                        $editorContainer.find('.ace_code_his_expand').attr('loaded', false);
                    });
                }
            });



        };

        this.setHisCode2Editor = function (uuid) {
            var _this = this;
            this.JDS.call({
                service: "appDefCodeHisFacadeService.getScriptContent",
                version: '',
                data: [
                    uuid
                ],
                async: false,
                success: function (result) {
                    if (result.success) {
                        _this.layer.msg('更新代码');
                        _this.$editor.setValue(result.data);
                    }
                }
            });

        }

        this.popCodeDialog = function (uuid) {
            var _this = this;
            this.JDS.call({
                service: "appDefCodeHisFacadeService.getScriptContent",
                version: '',
                data: [
                    uuid
                ],
                async: false,
                success: function (result) {
                    if (result.success) {

                        var scriptCode = result.data == null ? '' : result.data;
                        _this.layer.open({
                            title: '代码',
                            type: 1,
                            closeBtn: 0,
                            area: ['900px', '610px'], //宽高
                            content: "<pre style='min-height: 480px; margin-left:10px;margin-right: 10px;background-color: #f5f5f5;'>" + scriptCode + "</pre>",
                            btn: ['应用代码', '关闭'],
                            yes: function (index, layero) {
                                _this.$editor.setValue(result.data);
                                _this.layer.close(index);
                                return true;
                            }
                        });

                    }
                }
            });
        }


        this.loadCodeHisRecords = function () {
            var _this = this;
            var $table = this.$editorContainer.find('.ace_code_his_container table');
            var currentPage = $table.data('currentPage');
            this.JDS.call({
                service: "appDefCodeHisFacadeService.listByPage",
                version: '',
                data: [
                    _this.conf.relaBusizUuid,
                    _this.conf.codeType,
                    {currentPage: currentPage, pageSize: 100}
                ],
                async: false,
                success: function (result) {
                    if (result.success && result.data.length > 0) {
                        _this.renderData2Table(result.data);
                        $table.find('.load_more').remove();
                    } else {
                        $table.find('.load_more').find('td').text('无更多记录...');
                        //_this.$editorContainer.off('click', "table .load_more");
                    }
                }
            });
        };

        this.renderData2Table = function (data) {
            var $table = this.$editorContainer.find('table');
            for (var i = 0, len = data.length; i < len; i++) {
                $table.append(
                    $("<tr>", {"uuid": data[i].uuid}).append(
                        $("<td>").text(data[i].createTime),
                        $("<td>").append(
                            $("<span>").text(data[i].author),
                            $("<div>", {
                                "class": "code_tr_operation",
                                "style": "display: inline-block;position: absolute;width: 50px;" +
                                "overflow: hidden;white-space: nowrap;" +
                                "background-color: #337ab7;color:white;border-radius: 3px;padding: 2px;" +
                                "right: -60px;margin-top:-3px;box-shadow: 2px 2px 3px #888888"
                            }).append(
                                $("<i>", {
                                    "class": "fa fa-eye see_code",
                                    "style": "margin-left:5px;",
                                    "title": "查看代码"
                                })/*.text("查看")*/,
                                $("<i>", {
                                    "class": "fa fa-check-square apply_code",
                                    "style": "margin-left:10px;",
                                    "title": "使用代码"
                                })/*.text("应用")*/
                            )
                        )
                    )
                );
            }
        };

        this.saveCodeHis = function (forceSave, callback) {
            //debugger;
            if (forceSave || (this._$changeCnt > 0 && this._$originalValue != this.$editor.getSession().doc.getValue())) {
                var _this = this;
                this.JDS.call({
                    service: "appDefCodeHisFacadeService.addCodeHis",
                    version: '',
                    data: [
                        {
                            relaBusizUuid: _this.conf.relaBusizUuid,
                            script: _this.$editor.getValue(),
                            scriptType: _this.conf.codeType
                        }
                    ],
                    async: false,
                    success: function (result) {
                        if (result.success) {
                            if ($.isFunction(callback)) {
                                _this._$originalValue = _this.$editor.getSession().doc.getValue();
                                callback();
                            }
                        }
                    }
                });
            }
        };

        this.updateCodeHisConf = function (conf) {
            this.conf = $.extend({}, this.conf, conf);
        };

        this.init = function (val) {
            var _this = this;
            this.conf = val;
            this.createCodeHisBar();
            //定义保存代码历史的方法
            this.$editor._$saveCodeHis = function () {
                _this.saveCodeHis();
            };


            if (window.frameElement) {
                window.parent.$("body", window.parent.document).on('ace_$saveCodeHis', function () {
                    _this.saveCodeHis();
                });
            } else {
                //注册保存事件
                $("body").on('ace_$saveCodeHis', function () {
                    _this.saveCodeHis();
                });
            }


        };
    }).call(CodeHis.prototype);

    exports.CodeHis = CodeHis;

    var config = require("ace/config");

    config.defineOptions(Editor.prototype, "editor", {
        enableCodeHis: {
            set: function (v) {
                var val = $.extend(true, {}, v);
                if (val && val.enable) {
                    if (val.codeType == null) {//如果没有传codeType，则默认是editor容器的id
                        val.codeType = this.container.id;
                    }
                    if (val.relaBusizUuid == null) {
                        val.relaBusizUuid = $("#pi_uuid").val();//cms模块内
                    }
                    if (val.relaBusizUuid == null && val.codeType == null) {
                        console.warn('前端代码版本化记录未开启，原因：relaBusizUuid/codeType为空');
                        return;
                    }
                    console.log('初始化组件的代码历史记录功能：', val);
                    new CodeHis(this).init(val);
                }
            },
            value: {
                enable: false,
                relaBusizUuid: null,//关联的业务uuid
                codeType: null,//定义代码类型，如果没有传codeType，则默认是editor容器的id
            }
        }
    });
    


});
(function () {
    ace.require(["ace/ext/code_his"], function (m) {
        if (typeof module == "object" && typeof exports == "object" && module) {
            module.exports = m;
        }
    });
})();
            