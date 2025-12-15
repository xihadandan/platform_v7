/**
 * 代码编辑框的一些内置变量定义
 */
ace.define("ace/ext/code_var_snippets", ["require", "exports", "module", "ace/editor", "ace/config", "ace/snippets"], function (require, exports, module) {

    var dom = require("ace/lib/dom");
    var lang = require("ace/lib/lang");
    var config = require("ace/config");

    var Editor = require("ace/editor").Editor;
    var infoClass = "fa fa-code";

    var VarSnippets = function (editor) {
        this.$editor = editor;
        this.$editorContainer = $(editor.container);
        this.snippetManager = require("ace/snippets").snippetManager;
        this.snippets = {};

        if (typeof define === "function" && define.amd) {
            this.JDS = requirejs('server').JDS;
            this.layer = requirejs('layer');
            this.layer.ready(function () {//会自动加载css
            });
        } else {
            this.JDS = window.JDS ? window.JDS : window.parent.JDS;
            this.layer = window.frameElement ? window.parent.layer : layer;//iframe的情况下取父层的弹窗
        }

    };

    (function () {

        /**
         * 代码块的内置变量提示
         * @param value
         * @return {string}
         */
        this.getVarSnippetsTemplate = function (value) {
            if (value == 'wComponentDataStoreCondition') {
                return [{
                    content: ":currentUserId",
                    name: ":currentUserId : 内置变量-当前用户ID变量解析。</br>语法：<code>:currentUserId</code>",
                    tabTrigger: ":currentUserId"
                }, {
                    content: ":currentUserName",
                    name: ":currentUserName : 内置变量-当前用户名称变量解析。</br>语法：<code>:currentUserName</code>",
                    tabTrigger: ":currentUserName"
                }, {
                    content: ":currentUserUnitId",
                    name: ":currentUserUnitId : 内置变量-当前登录用户所属的单位ID变量解析。</br>语法：<code>:currentUserUnitId</code>",
                    tabTrigger: ":currentUserUnitId"
                }, {
                    content: ":currentUserUnitName",
                    name: ":currentUserUnitName : 内置变量-当前登录用户所属的单位名称变量解析。</br>语法：<code>:currentUserUnitName</code>",
                    tabTrigger: ":currentUserUnitName"
                }, {
                    content: ":currentUserDepartmentId",
                    name: ":currentUserDepartmentId : 内置变量-当前登录用户所属的部门ID变量解析。</br>语法：<code>:currentUserDepartmentId</code>",
                    tabTrigger: ":currentUserDepartmentId"
                }, {
                    content: ":currentUserDepartmentName",
                    name: ":currentUserDepartmentName : 内置变量-当前登录用户所属的部门名称变量解析。</br>语法：<code>:currentUserDepartmentName</code>",
                    tabTrigger: ":currentUserDepartmentName"
                }, {
                    content: ":sysdate",
                    name: ":sysdate : 内置变量-当前服务端系统时间。</br>语法：<code>:sysdate</code>",
                    tabTrigger: ":sysdate"
                }, {
                    content: ":currentUserMainBusinessUnitId",
                    name: ":currentUserMainBusinessUnitId : 内置变量-当前登录用户主业务单位ID变量解析。</br>语法：<code>:currentUserOtherBusinessUnitIds</code>",
                    tabTrigger: ":currentUserMainBusinessUnitId"
                }, {
                    content: ":currentUserOtherBusinessUnitIds",
                    name: ":currentUserOtherBusinessUnitIds : 内置变量-当前登录用户其他业务单位ID集合变量解析。</br>语法：<code>:currentUserOtherBusinessUnitIds</code>",
                    tabTrigger: ":currentUserOtherBusinessUnitIds"
                }, {
                    content: "location",
                    name: "location : location包含有关当前URL的信息解析。</br>语法：<code>&{location.参数}</code>",
                    tabTrigger: "location"
                }, {
                    content: "&{location.query.${1}}",
                    name: "location.query : location包含当前URL的查询参数。</br>语法：<code>&{location.query.参数}</code>",
                    tabTrigger: "location.query"
                }];
            }

            if (value == 'wBootstrapTable.render') {//表格组件渲染代码块支持的内置变量
                return "# \n\
# 表格组件渲染前内置变量 \n\
snippet configuration : 内置变量-表格配置数据\n\
	configuration${1}\n\
snippet $this : 内置变量-表格组件对象\n\
	\\$this${1}";
            }

            if (value == 'wBootstrapTable.afterRenderCode') {//表格组件渲染后代码块支持的内置变量
                return "# \n\
# 表格组件渲染前内置变量 \n\
snippet $this : 内置变量-表格组件对象\n\
	\\$this${1}\n\
snippet configuration : 内置变量-表格配置数\n\
	configuration${1}\n\
	";
            }


            if (value == 'wBootstrapTable.beforeRenderButton') {//表格组件按钮渲染前代码块支持的内置变量
                return "# \n\
# 表格组件渲染前内置变量 \n\
snippet buttons : 内置变量-按钮组配置数据\n\
	buttons${1}\n\
snippet $this : 内置变量-表格组件对象\n\
	\\$this${1}";
            }

            if (value == 'wBootstrapTable.clickTr') {//表格组件行点击代码块支持的内置变量
                return "# \n\
# 表格组件渲染前内置变量 \n\
snippet row : 内置变量-当前触发的表格行对应的行数据\n\
	row${1}\n\
snippet $this : 内置变量-当前触发的表格行\n\
	\\$this${1}\n\
snippet event : 内置变量-当前dom事件\n\
	event${1}";
            }

            if (value == 'wBootstrapTable.mouseOutTr') {//表格组件移除行代码块支持的内置变量
                return "# \n\
# 表格组件渲染前内置变量 \n\
snippet $this : 内置变量-当前触发的表格行\n\
	\\$this${1}\n\
snippet event : 内置变量-当前dom事件\n\
	event${1}";
            }

            if (value == 'wBootstrapTable.beforeExport') {//表格组件导出前的代码块支持的内置变量
                return "# \n\
# 表格组件渲染前内置变量 \n\
snippet param : 内置变量-导出的参数\n\
	param${1}\n\
snippet param.extras : 内置变量-导出的额外参数设置\n\
	param.extras={\n\
	    ${1:key1}:${2:val1},\n\
	    ${3:key2}:${4:val2}\n\
	}\n\
	    ";
            }

            if (value == 'wBootstrapTable.defineButtonEvent') {//事件处理的按钮代码块支持的内置变量
                return "# \n\
# 事件处理按钮内置变量 \n\
snippet $this : 内置变量-当前点击的按钮\n\
	\\$this${1}\n\
	";
            }

            if (value == 'wLeftSidebar.afterRenderCode') {//左导航组件渲染前代码块支持的内置变量
                return "# \n\
# 左导航组件渲染后内置变量 \n\
snippet $this : 内置变量-导航组件对象\n\
	\\$this${1}\n\
snippet options : 内置变量-导航组件配置\n\
	options${1}";
            }

            if (value == 'wLeftSidebar.beforeRenderCode') {//左导航组件渲染后代码块支持的内置变量
                return "# \n\
# 左导航组件渲染前内置变量 \n\
snippet $this : 内置变量-导航组件对象\n\
	\\$this${1}\n\
snippet configuration : 内置变量-导航选项配置数据\n\
	configuration${1}\n\
	";
            }

            if (value == 'wDataManagementViewer.afterDealEventCode') {
                return "# \n\
# 数据管理查看器的按钮事件内置变量 \n\
snippet $this : 内置变量-数据管理查看器组件对象\n\
	\\$this${1}\n\
snippet result : 内置变量-服务处理结果\n\
	result${1}\n\
	";
            }

            if (value == 'dyform.controlEvent') { //表单控件事件代码块支持的内置变量
                return "# \n\
# 表单控件事件内置变量 \n\
snippet $this : 内置变量-控件对象\n\
	\\$this${1}\n\
snippet columnProperty : 内置变量-字段属性\n\
	columnProperty${1}\n\
snippet $form : 内置变量-表单对象\n\
	\\$form${1}\n\
snippet event : 内置变量-事件event对象\n\
	event${1}";
            }

            if (value == 'dyform.controlValidateEvent') {//表单控件的校验规则事件代码块支持的内置变量
                return "# \n\
# 表单控件校验事件内置变量 \n\
snippet data : 内置变量-当前控件值数据\n\
	data${1}\n\
	";
            }

            if (value == 'dyform.afterSave_event') {//表单保存后事件代码块支持的内置变量
                return "# \n\
# 表单保存后事件内置变量 \n\
snippet dyformData : 内置变量-保存的表单数据\n\
	dyformData${1}\n\
snippet result : 内置变量-保存后的返回结果\n\
	result${1}\n\
	";
            }


            if (value == 'dyform.beforeSave_event') {//表单保存前事件代码块支持的内置变量
                return "# \n\
# 表单保存前事件内置变量 \n\
snippet dyformData : 内置变量-保存的表单数据\n\
	dyformData${1}\n\
	";
            }


            if (value == 'dyform.beforeInit_event') {//表单初始化前事件代码块支持的内置变量
                return "# \n\
# 表单初始化前事件内置变量 \n\
snippet formDefinition : 内置变量-表单定义数据\n\
	formDefinition${1}\n\
	";
            }

            if (value == 'dyform.afterInit_event') {//表单初始化后事件代码块支持的内置变量
                return "# \n\
# 表单初始化前事件内置变量 \n\
snippet $dyform : 内置变量-表单对象\n\
	\\$dyform${1}\n\
	";
            }

            if (value == 'wBootstrapTable.importColGroovyUse') {//表格导入功能列的groovy验规则
                return "# \n\
# 校验规则内置变量 \n\
snippet _fieldValue : 内置变量-导入列的值\n\
	_fieldValue${1}\n\
snippet _formData : 内置变量-每一行导入列的数据\n\
	_formData${1}";
            }

            if (value == 'bot.fieldCalValue') { //单据转换字段值的计算
                return "# \n\
# 单据转换字段值计算的内置变量 \n\
snippet sourceValue : 内置变量-源字段值\n\
	sourceValue${1}\n\
snippet targetValue : 内置变量-目标字段值\n\
	targetValue${1}\n\
snippet data : 所有源数据如果源数据是表单，则表示表单数据， 可以通过data.表单ID.表单字段的方式取值，例如：data.uf_oa_book.book_name；如果源数据是JSON报文对象，则表示JSON对象，可以通过 data.报文字段的方式取值，例如JSON报文为{'name':'test','code':100}，取值name通过data.name\n\
	data${1}\n\
snippet targetFormData : 表单单据数据，表示目标单据数据，可以通过targetFormData.表单ID.表单字段的方式取值，例如：targetFormData.uf_oa_book.book_name\n\
    targetFormData${1}\n\
snippet currentUserName : 当前用户的名称\n\
	currentUserName${1}\n\
snippet currentLoginName : 当前用户的登录名\n\
	currentLoginName${1}\n\
snippet currentUserId : 当前用户ID\n\
	currentUserId${1}\n\
snippet currentUserUnitId : 当前用户单位ID\n\
	currentUserUnitId${1}\n\
snippet currentUserUnitName : 当前用户单位名称\n\
	currentUserUnitName${1}\n\
snippet currentUserDepartmentId : 当前用户的部门ID\n\
	currentUserDepartmentId${1}\n\
snippet currentUserDepartmentName : 当前用户的部门名称\n\
	currentUserDepartmentName${1}\n\
snippet sysdate : 当前系统时间\n\
	sysdate${1}";
            }

            if (value == 'wf.workflowDefineGroovy') { //流程定义的groovy的内置变量
                return "# \n\
# 流程定义的groovy的内置变量 \n\
snippet applicationContext : 内置变量-spring应用上下文\n\
	applicationContext${1}\n\
snippet currentUser : 内置变量-当前用户信息\n\
	currentUser${1}\n\
snippet event : 内置变量-流程相关事件信息\n\
	event${1}\n\
snippet flowInstUuid : 内置变量-流程实例UUID\n\
	flowInstUuid${1}\n\
snippet taskInstUuid : 内置变量-环节实例UUID\n\
	taskInstUuid${1}\n\
snippet taskData : 内置变量-环节数据\n\
	taskData${1}\n\
snippet formUuid : 内置变量-表单定义UUID\n\
	formUuid${1}\n\
snippet dataUuid : 内置变量-表单数据UUID\n\
	dataUuid${1}\n\
snippet dyFormData : 内置变量-表单数据\n\
	dyFormData${1}\n\
snippet actionType : 内置变量-办理意见\n\
	actionType${1}\n\
snippet resultMessage : 内置变量-事件脚本执行结果，调用resultMessage.isSuccess()方法返回true，通过resultMessage.setSuccess(true/false)设置脚本执行是否成功\n\
	resultMessage${1}\n\
	";
            }


        };

        this.commonCodeBizSnippetKey = function (value) {
            return value + "_$common_code_biz";
        };

        /**
         * 代码块常用的业务逻辑提示封装
         * @param value
         * @return {string}
         */
        this.getCommonCodeBizTemplate = function (value) {

            if (value == 'wBootstrapTable.afterRenderCode') {//表格组件渲染后代码块常用代码逻辑提示
                return "# \n\
# 左导航组件渲染前内置变量 \n\
snippet _@listenTreeToggle : 监听树形列表状态切换完成的事件\n\
	\\$this.\\$tableElement.on('post-tree-body.bs.table',function(e,isTree){\n\
	    if(isTree){\n\
	        ${1://TODO:树形状态下业务处理}\n\
	    }else{\n\
	        ${2://TODO:非树形状态下的业务处理}\n\
	    }\n\
	});\n\
	";
            }

        };


        this.varSnippetAdapter = function (data) {
            var _this = this;
            var _snippetManager = this.snippetManager;
            var showSnippetsTabs = ['内置变量', 'API', '常用代码逻辑'];
            var scope =  ['javascript', 'groovy','sql'];
            var value = data;
            if (typeof (data) !== 'string') {
                if (data.showSnippetsTabs) {
                    showSnippetsTabs = value.showSnippetsTabs;
                }
                value = data.value;
                scope = data.scope;
            }

            this.$editorContainer.on('mouseover', function (e) {
                if ($(e.currentTarget).is('.ace_autocomplete')) {
                    return true;
                }
                _this.$editorContainer.find(".ace_snippets_info").show();
            });

            this.$editorContainer.on('mouseout', function () {
                _this.$editorContainer.find(".ace_snippets_info").hide();
            });
            if (showSnippetsTabs && showSnippetsTabs.length) {
                this.$editorContainer.on('click', '.ace_snippets_info', function () {
                    if (!_this.snippets[value]) {
                        _this.loadSnippets(value);
                    }
                    var tabs = [];
                    for (var s = 0; s < showSnippetsTabs.length; s++) {
                        if (showSnippetsTabs[s] === '内置变量') {
                            tabs.push({
                                title: '内置变量',
                                content: (function () {
                                    var $table = $("<table>", {"class": "table table-condensed table-hover"});
                                    for (var key in _snippetManager.snippetNameMap[value]) {
                                        var parts = key.split(" : ");
                                        if (parts[0].indexOf("_@") == 0) {//常用业务逻辑代码块，非内置变量
                                            continue;
                                        }
                                        $table.append($("<tr>").append(
                                            $("<td>").text(parts[0]),
                                            $("<td>").html(parts[1].replace("内置变量-", ""))
                                        ));
                                    }
                                    return '<div style="max-height:300px;overflow-y:auto;">' + $table[0].outerHTML + '</div>';
                                })()
                            });
                        } else if (showSnippetsTabs[s] === 'API') {
                            tabs.push({
                                title: 'API',
                                content: (function () {
                                    var $table = $("<table>", {"class": "table table-condensed table-hover"});
                                    for (var key in _snippetManager.snippetNameMap[_this.apiSnippetKey()]) {
                                        var parts = key.split(" : ");
                                        if (parts[0].indexOf("_@") == 0) {//常用业务逻辑代码块，非API
                                            continue;
                                        }
                                        $table.append($("<tr>").append(
                                            $("<td>").text(parts[0]),
                                            $("<td>").html(parts[1])
                                        ));
                                    }
                                    return '<div style="max-height:300px;overflow-y:auto;">' + $table[0].outerHTML + '</div>';
                                })()
                            });
                        } else if (showSnippetsTabs[s] === '常用代码逻辑') {
                            tabs.push({
                                title: '常用代码逻辑',
                                content: (function () {
                                    var $table = $("<table>", {"class": "table table-condensed table-hover"});
                                    //常用逻辑分两部分，一个指定代码块的常用逻辑，一个api的常用逻辑封装
                                    var valueType = [value, _this.apiSnippetKey(), _this.commonCodeBizSnippetKey(value)];
                                    for (var i = 0; i < valueType.length; i++) {
                                        for (var key in _snippetManager.snippetNameMap[valueType[i]]) {
                                            var parts = key.split(" : ");
                                            if (parts[0].indexOf("_@") == 0) {//常用业务逻辑代码块
                                                $table.append($("<tr>").append(
                                                    $("<td>").text(parts[0]),
                                                    $("<td>").html(parts[1])
                                                ));
                                            }

                                        }
                                    }


                                    return '<div style="max-height:300px;overflow-y:auto;">' + $table[0].outerHTML + '</div>';
                                })()
                            });
                        }
                    }
                    if (_this.$editor.session.$modeId == 'ace/mode/groovy') {
                        tabs.push({
                            title: '运行域导入类',
                            content: (function () {
                                if (!_this.runtimeImportClass) {
                                    _this.JDS.call({
                                        service: "groovyHelper.queryGroovyImportClass",
                                        version: '',
                                        data: [],
                                        async: false,
                                        success: function (result) {
                                            if (result.success) {
                                                _this.runtimeImportClass = result.data;
                                            }
                                        }
                                    });
                                }

                                var $table = $("<table>", {"class": "table table-condensed table-hover"});
                                for (var i = 0; i < _this.runtimeImportClass.length; i++) {
                                    $table.append($("<tr>").append(
                                        $("<td>").text(_this.runtimeImportClass[i]))
                                    );
                                }
                                return '<div style="max-height:300px;overflow-y:auto;">' + $table[0].outerHTML + '</div>';
                            })()
                        });
                    }

                    _this.layer.tab({
                        area: ['600px', '400px'],
                        btn: ['关闭'],
                        tab: tabs,
                        success: function (layero, index) {
                            $(layero).find('.layui-layer-content').css('overflow', 'inherit');
                            $(layero).find('ul').css('list-style-type', 'none');
                        }
                    });


                });
            }

            _this.$editor.on('focus', function () {

                //添加代码内置变量片段
                if (!_this.snippets[value]) {
                    _this.loadSnippets(value);
                }

                /**
                 * 重置代码提示的包含域，只显示与当前代码块类型的提示
                 * @type {Array}
                 */
                var baseScope = scope.concat(value);
                for(var k in _snippetManager.snippetMap){
                    if(baseScope.indexOf(k)==-1){
                        delete _snippetManager.snippetMap[k];
                    }
                }
                for (var i = 0; i < baseScope.length; i++) {
                    if (_snippetManager.snippetMap[baseScope[i]]) {
                        _snippetManager.snippetMap[baseScope[i]].includeScopes = [];
                        _snippetManager.snippetMap[baseScope[i]].includeScopes.push(_this.apiSnippetKey());
                        _snippetManager.snippetMap[baseScope[i]].includeScopes.push(value);
                        _snippetManager.snippetMap[baseScope[i]].includeScopes.push(_this.commonCodeBizSnippetKey(value));
                    }
                }


            });

        };

        this.apiSnippetKey = function () {
            if (this.$editor.session.$modeId == 'ace/mode/groovy') {
                return 'wellsoft_groovy';
            }
            //判断可用API
            if (window.appPageDesigner) {
                //cms
                return 'cms_api_snippets';
            }
            if (window.dyform_constant || window.parent.dyform_constant) {
                //dyform
                return 'dyform_api_snippets';
            }


        }


        this.loadSnippets = function (value) {
            var valueTypes = [value, value];//第一次加载内置变量，第二次加载常用逻辑
            for (var i = 0; i < valueTypes.length; i++) {
                var snippetText = (i == 0 ? this.getVarSnippetsTemplate(valueTypes[i]) : this.getCommonCodeBizTemplate(valueTypes[i]));
                if (snippetText) {
                    var snippets = this.snippetManager.parseSnippetFile(snippetText);
                    var key = (i == 0 ? valueTypes[i] : this.commonCodeBizSnippetKey(valueTypes[i]));
                    this.snippetManager.register(snippets || [], key);
                    this.snippets[key] = snippets;
                }
            }


        };

        this.createSnippetInfoIcon = function () {
            var $span = $("<span>", {
                "class": "ace_snippets_info " + infoClass,
                "style": "position: absolute; z-index: 100001; cursor: pointer; right: 35px;top:5px;display:none;font-weight:bolder;",
                "title": "相关代码说明",
                "aria-hidden": true
            });
            this.$editorContainer.append($span);
        };

        this.createCodeTypeIcon = function () {
            var iconClass = '';
            if (this.$editor.session.$modeId == 'ace/mode/groovy') {
                iconClass = 'wellsoftIcon-Groovy';
            }
            if (this.$editor.session.$modeId == 'ace/mode/javascript') {
                iconClass = 'wellsoftIcon-javascript';
            }
            if (this.$editor.session.$modeId == 'ace/mode/ftl') {
                iconClass = 'wellsoftIcon-freeMarker';
            }
            if (!iconClass) {
                return;
            }
            var $span = $("<span>", {
                "class": iconClass,
                "style": "position: absolute; z-index: 100001; cursor: pointer; right: 10px;bottom:-10px;font-weight:bolder;font-size:40px;",
                "title": "可编写代码",
                "aria-hidden": true
            });
            this.$editorContainer.append($span);
        };

        this.init = function (v) {
            this.createCodeTypeIcon();
            this.createSnippetInfoIcon();
            this.varSnippetAdapter(v);
        };
    }).call(VarSnippets.prototype);

    exports.VarSnippets = VarSnippets;


    config.defineOptions(Editor.prototype, "editor", {
        enableVarSnippets: {
            set: function (val) {
                if (val) {
                    new VarSnippets(this).init(val);
                }
            },
            value: null
        }
    });


});
(function () {
    ace.require(["ace/ext/code_var_snippets"], function (m) {
        if (typeof module == "object" && typeof exports == "object" && module) {
            module.exports = m;
        }
    });
})();
            