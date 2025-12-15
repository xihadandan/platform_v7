define([ "jquery", "server", "commons", "constant", "appContext", "appModal", "OpinionEditor" ], function($, server,
        commons, constant, appContext, appModal, OpinionEditor) {
    var StringUtils = commons.StringUtils;
    var StringBuilder = commons.StringBuilder;
    var summernoteTolbar = [ [ 'style', [ 'bold', 'italic', 'underline', 'clear' ] ],
            [ 'font', [ 'strikethrough', 'superscript', 'subscript' ] ], [ 'fontsize', [ 'fontsize' ] ],
            [ 'color', [ 'color' ] ], [ 'para', [ 'ul', 'ol', 'paragraph' ] ], [ 'height', [ 'height' ] ] ];
    var jds = server.JDS;
    var getCurrentUserOpinion2SignService = "workV53Service.getCurrentUserOpinion2Sign";

    // 签署意见编辑器
    var WorkFlowOpinionEditor = function(workView, options) {
        OpinionEditor.call(this, workView, options);
    };
    commons.inherit(WorkFlowOpinionEditor, OpinionEditor, {
        // 初始化
        init : function() {
            var _self = this;
            _self.$editorPlaceholder = $(this.editorPlaceholder);
            // _self.$editor
            // =_self.$editorPlaceholder.find(_self.editorSelector);
            _self.$editor = _self.$editorPlaceholder.find(_self.textareaSlector);
            _self.$snapshotPlaceholder = $(this.snapshotPlaceholder);
            _self.$snapshotInput = _self.$snapshotPlaceholder.find(_self.snapshotInputSelector);

            // 还原签署意见
            _self.restore();

            var isDraft = _self.workView.isDraft();
            var isTodo = _self.workView.isTodo();
            var isSupervise = _self.workView.isSupervise();
            var isMonitor = _self.workView.isMonitor();
            // 待办、督办、监控工作的时候才可签署意见
            // if (isDraft || isTodo || isSupervise || isMonitor) {
            if (_self.options.signOpinion) {
                _self.$snapshotInput.val(_extractSnapshotText(_self.opinion.text));
                _self._createOpinionView();
                _self._bindEvents();
                _self.show();
            } else {
                // 隐藏
                _self.hide();
            }
        },
        // 显示
        show : function() {
            this.$editorPlaceholder.hide();
            this.$snapshotPlaceholder.show();
            this.$editorPlaceholder.trigger("footer.resize");
        },
        // 隐藏
        hide : function() {
            this.$editorPlaceholder.hide();
            this.$snapshotPlaceholder.hide();
            this.$editorPlaceholder.trigger("footer.resize");
        },
        // 创建意见内容信息
        _createOpinionView : function() {
            var _self = this;
            var workData = _self.workView.getWorkData();
            var flowDefUuid = workData.flowDefUuid;
            var taskId = workData.taskId;
            $.get({
                url : ctx + "/api/workflow/work/getCurrentUserOpinion2Sign",
                data : {flowDefUuid: flowDefUuid, taskId: taskId},
                success : function(result) {
                    var options = result.data;
                    var templateEngine = appContext.getJavaScriptTemplateEngine();
                    var text = templateEngine.renderById("pt-workflow-user-opinions", options);
                    _self.$editorPlaceholder.find(".wf-opinion-tabs").html(text);
                    _self.$editorPlaceholder.find(".list-group").each(function() {
                        $(this).slimScroll({
                            "height" : "100%",
                            wheelStep: navigator.userAgent.indexOf('Firefox') > -1 ? 1 : 10
                        });
                    });
                    _self._createSnapshopOpinionView(options);
                }
            });
        },
        // 创建快照的意见内容信息
        _createSnapshopOpinionView : function(options) {
            var _self = this;
            var opinionValues = options.opinions;
            if (opinionValues && opinionValues.length > 0) {
                _self.isRequiredOpinionValue = true;
                var $recentSnapshop = $(".wf-opinion-recent-snapshop");
                var $optionValue = $("<div class='wf-opinion-value'></div>");
                var sb = new StringBuilder();
                sb.append("<div class='row'>")
                sb.append("<label class='col-sm-3 control-label'>意见立场</label>");
                sb.append("<div class='col-sm-9'>");
                sb.append("<select name='opinion-value' class='form-control' placeHolder='请选择意见立场！'>");
                sb.append("<option value=''></option>");
                $.each(opinionValues, function() {
                    var option = "<option value='" + this.code + "'>" + this.content + "</option>";
                    sb.append(option);
                });
                sb.append("</select>");
                sb.append("</div>");
                sb.append("</div>")
                $optionValue.insertAfter($recentSnapshop);
                $optionValue.html(sb.toString());
                $recentSnapshop.hide();
            } else if (options && options.recents) {
                var $recentSnapshop = $(".wf-opinion-recent-snapshop");
                var isOnlyOne = options.recents.length == 1;
                $.each(options.recents, function(i, option) {
                    option.content = option.content.replace(/<.*?>/ig, "");

                    var item = '<a class="list-group-item" title="' + option.content + '">' + option.content + '</a>';
                    $recentSnapshop.find(".list-group").append(item);
                });
                if (isOnlyOne) {
                    $recentSnapshop.addClass("only-one-item")
                } else {
                    $recentSnapshop.slimScroll({
                        "height": "100%",
                        wheelStep: navigator.userAgent.indexOf('Firefox') > -1 ? 1 : 10
                    });
                }
            } else {
                _self.$snapshotPlaceholder.find(".col-sm-9").addClass("col-sm-12").removeClass("col-sm-9");
                _self.$snapshotPlaceholder.find(".col-sm-3").remove();
            }
        },
        // 绑定事件
        _bindEvents : function() {
            var _self = this;
            _self.$snapshotInput.on("click", function() {
                _self._showEditor();
                // _self._setEditorHtml(_self.opinion.text);
                _self.$editor.val(_self.opinion.text);
                _self.$editor.focus();
            });

            // _self.$editor.on("summernote.focusin", function(event) {
            // _self._showEditor();
            // });
            _self.$editor.on("focus", function(event) {
                _self._showEditor();
            });

            // 文本变化，收集更新意见内容
            // _self.$editor.on("summernote.change", function(event) {
            // _self.collectOpinion();
            // })
            _self.$editor.on("change", function(event) {
                _self.collectOpinion();
            });

            // 点击意见处理
            var StringBuilder = commons.StringBuilder;
            var sb = new StringBuilder();
            _self.$editorPlaceholder.on("click", ".list-group>.list-group-item", function() {
                var bower = commons.Browser;
                var selectedOpinion = $(this).html();
                // if (bower.isIE8OrLower()) {
                // $(".note-editable").append(selectedOpinion);
                // return;
                // } else {
                // _self._appendEditorHtml(selectedOpinion);
                // }
                sb.append(selectedOpinion)
                _self.$editor.val(_self.$editor.val() + selectedOpinion);
            });

            // 点击快照意见处理
            var snapshotSelector = ".wf-opinion-recent-snapshop>.list-group>.list-group-item";
            _self.$snapshotPlaceholder.on("click", snapshotSelector, function() {
                var selectedOpinion = $(this).html();
                _self.opinion.text += selectedOpinion;
                _self.$snapshotInput.val(_self.opinion.text);
            });

            // 点击意见立场处理
            var opinionValueSelector = ".wf-opinion-value select";
            _self.$snapshotPlaceholder.on("change", opinionValueSelector, function() {
                var value = $(this).val();
                var text = $(this).find("option:selected").text();
                _self.opinion.value = value;
                _self.opinion.label = text;
                var selectedOpinion = text;
                if (StringUtils.isBlank(_self.opinion.text)) {
                    _self.opinion.text += selectedOpinion;
                    _self.$snapshotInput.val(_self.opinion.text);
                }
            });

            // 意见管理
            _self.$editorPlaceholder.on("click", "ul>li.wf-opinion-mgr", function() {
                appContext.require([ "WorkFlowOpinionManage" ], function(WorkFlowOpinionManage) {
                    var opinionManage = new WorkFlowOpinionManage(_self);
                    opinionManage.open();
                });
            });

            // 固定意见
            _self.$editorPlaceholder.on("click", "input.wf-fixed-opinion", function() {
                _self.fixedOpinion = this.checked;
            });
            var toastOption = {
                loader : false,
                hideAfter : 1500,
                bgColor : "##9f9f9f",
                textAlign : "center",
                allowToastClose : false,
                position : "bottom-center"
            }
            _self.$editorPlaceholder.on("click", "li.wf-opinion-fixed", function(event) {
                var $target = $("li.wf-opinion-fixed", _self.$editorPlaceholder);
                if ($target.is(".fixed")) {
                    $target.removeClass("fixed");
                    _self.fixedOpinion = false;
                    appModal.toast($.extend(toastOption, {
                        text : "已取消固定",
                    }));
                } else {
                    $target.addClass("fixed");
                    _self.fixedOpinion = true;
                    appModal.toast($.extend(toastOption, {
                        text : "已固定",
                    }));
                }
            });

            // 点击表单收起编辑器
            $(document).on("click", function(event) {
                if (_self.fixedOpinion === true) {
                    return;
                }
                if (!_self.$editorPlaceholder.is(":visible")) {
                    return;
                }
                if (_self.$editorPlaceholder.parent().find(event.target).length == 0) {
                    _self._hideEditor();
                    /*
                     * var html = _self._getEditorHtml(); var text =
                     * _extractSnapshotText(html);
                     */
                    var text = _self.$editor.val();
                    _self.$snapshotInput.val(text);
                    // 更新签署意见值
                    _self.opinion.text = text;
                }
            });
        },
        _showEditor : function() {
            var _self = this;

            var $fillTop = $(".widget-footer-fill-top");
            $fillTop.addClass("animated fadeInUp");
            $fillTop.show();

            _self.$snapshotPlaceholder.hide();
            _self.$editorPlaceholder.addClass("animated fadeInUp");
            _self.$editorPlaceholder.show();
            _self.$editorPlaceholder.trigger("footer.resize");
        },
        _hideEditor : function() {
            var _self = this;

            var $fillTop = $(".widget-footer-fill-top");
            $fillTop.removeClass("fadeInUp");
            $fillTop.addClass("fadeOut");

            _self.$editorPlaceholder.removeClass("fadeInUp");
            _self.$editorPlaceholder.addClass("fadeOut");
            setTimeout(function() {
                $fillTop.removeClass("fadeOut");
                $fillTop.hide();

                _self.$editorPlaceholder.removeClass("fadeOut");
                _self.$editorPlaceholder.hide();
                _self.$snapshotPlaceholder.show();
                _self.$editorPlaceholder.trigger("footer.resize");
            }, 500);
        },
        // 设置编辑器HTML
        _setEditorHtml : function(html) {
            var _self = this;
            if (_self.editorCreated === false) {
                _self.$editor.summernote({
                    callbacks : {
                        onPaste : function(e) {
                            // console.log('Called event paste');
                        }
                    },
                    lang : "zh-CN",
                    height : 204,// 163
                    toolbar : [],// summernoteTolbar
                    focus : true
                });
                _self.editorCreated = true;
                _self.$editorPlaceholder.find(".note-statusbar").hide();
            } else {
                _self.$editor.summernote("focus", true);
            }
            _self.$editor.summernote("code", html);
        },
        // 附加编辑器HTML
        _appendEditorHtml : function(html) {
            var _self = this;
            var currentHtml = _self.$editor.summernote("code");
            _self.$editor.summernote("insertHtml", html);
        },
        // 获取编辑器HTML
        _getEditorHtml : function() {
            var _self = this;
            if (_self.editorCreated === false) {
                return "";
            }
            return _self.$editor.summernote("code");
        },
        // 打开签署意见
        openToSignIfRequired : function(options) {
            var _self = this;
            var action = options.action;
            var methodName = "isRequired" + StringUtils.capitalise(action) + "Opinion";
            var isRequired = _self[methodName].call(_self);
            if (isRequired === false) {
                return false;
            }
            if (_self.$editorPlaceholder.is(":visible")) {
                _self.collectOpinion();
            }
            var opinion = _self.opinion;
            var value = opinion.value;
            var text = opinion.text;
            if (_self.isRequiredOpinionValue && StringUtils.isBlank(value)) {
                appModal.info("请选择意见立场!");
                return true;
            }
            if (StringUtils.isBlank(text)) {
                _self._showEditor();
                _self.$editor.val("");
                // _self._setEditorHtml("");
                appModal.warning("请先签署意见!");
                return true;
            }
            return false;
        },
        // 收集签署的意见
        collectOpinion : function() {
            var _self = this;
            // 签署意见编辑器有初始化才收集返回的数据
            // var html = _self._getEditorHtml();
            // var text = _extractSnapshotText(html);
            var text = _self.$editor.val();
            if (StringUtils.isBlank(text)) {
                text = "";
            }
            _self.opinion.text = text;
            return _self.opinion;
        },
        // 是否需要选择意见立场
        isRequiredOpinionPosition : function() {
            var _self = this;
            if (_self.workView.isTodo() === false) {
                return false;
            }
            var workData = _self.workView.getWorkData();
            if ($("#mini_wf_opinion").workflowMiniOpinion("isRequiredOpinionPosition") === true) {
                appModal.alert("请先选择意见立场!");
                return true;
            }
            return false;
        }
    });

    // 清除HTML标签
    function _extractSnapshotText(html) {
        if (StringUtils.isBlank(html)) {
            return html;
        }
        var nobsphtml = html.replace(/&nbsp;/g, "");
        return $("<span/>").html(nobsphtml).text();
        // 提取第一行快照文本
        // var strings = html.split("</p>");
        // for (var i = 0; i < strings.length; i++) {
        // var tmp1 = strings[i];
        // tmp1 = tmp1.replace(/<\/?.+?>/g, "");
        // if (StringUtils.isNotBlank(tmp1)) {
        // return tmp1.replace(/&nbsp;/g, "");
        // }
        // }
        //
        // var tmp2 = html.replace(/<\/?.+?>/g, "");
        // if (StringUtils.isBlank(tmp2)) {
        // return tmp2;
        // }
        // return tmp2.replace(/&nbsp;/g, "");
    }

    return WorkFlowOpinionEditor;
});