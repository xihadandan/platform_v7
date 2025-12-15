define([ "ui_component","constant", "commons", "formBuilder", "appContext", "design_commons" ],
    function(ui_component,constant, commons,formBuilder, appContext, designCommons) {
        var UUID = commons.UUID;
        var StringUtils = commons.StringUtils;
        var StringBuilder = commons.StringBuilder;
        var component = $.ui.component.BaseComponent();


        function checkRequire (propertyNames, options, $container) {
            for (var i = 0; i < propertyNames.length; i++) {
                var propertyName = propertyNames[i];
                if (StringUtils.isBlank(options[propertyName])) {
                    var title = $("label[for='" + propertyName + "']", $container).text();
                    appModal.error(title.replace("*", "") + "不允许为空！");
                    return false;
                }
            }
            return true;
        }

        component.prototype.create = function() {

        };

        // 使用属性配置器
        component.prototype.usePropertyConfigurer = function() {
            return true;
        };

        // 返回属性配置器
        component.prototype.getPropertyConfigurer = function() {
            var collectClass = "w-configurer-option";
            var configurerPrototype = {};
            configurerPrototype.getTemplateUrl = function() {
                // 调用父类提交方法
                var wtype = this._superApply(arguments);
                return wtype;
            };
            configurerPrototype.onLoad = function($container, options) {
                var _self = this;
                var configuration = $.extend(true, {}, options.configuration);

                $("#widget_nav_tab_tabs ul a", $container).on("click", function(e) {
                    e.preventDefault();
                    $(this).tab("show");
                });
                //初始化基本信息
                this.initBaseInfo(configuration, $container);
            };

            configurerPrototype.onOk = function($container) {
                var _self = this;
                var opt = designCommons.collectConfigurerData($("#widget_nav_tab_base_info", $container),
                    collectClass);
                if (this.component.isReferenceWidget()) {
                    return;
                }

                var requeryFields = [];
                if (!checkRequire(requeryFields, opt, $container)) {
                    return false;
                }
                this.component.options.configuration = $.extend({}, opt);
            };

            configurerPrototype.initBaseInfo = function(configuration, $container) {
                var system = appContext.getCurrentUserAppData().getSystem();
                var productUuid = system.productUuid;
                $("#eventHanlderName",$container).AppEvent({
                    ztree: {params: [productUuid]},
                    okCallback: function ($el, data) {
                        if (data) {
                            $("#eventHanlderId", $container).val(data.id);
                            $("#eventHanlderType", $container).val(data.data.type);
                            $("#eventHanlderPath", $container).val(data.data.path);
                        }
                    },
                    clearCallback: function ($el) {
                        $("#eventHanlderId,#eventHanlderType,#eventHanlderPath", $container).val('');
                    }
                });
                designCommons.setElementValue(configuration, $container, 'tabs');
                // 二开JS模块
                $("#jsModule", $container).wSelect2({
                    serviceName: "appJavaScriptModuleMgr",
                    params: {
                        dependencyFilter: "NavTabWidgetDevelopment"
                    },
                    labelField: "jsModuleName",
                    valueField: "jsModule",
                    remoteSearch: false,
                    multiple: true
                });
                $('#jsModuleName',$container).val(configuration.jsModuleName);
                $('#jsModule',$container).val(configuration.jsModule);
                $('#nav_tab_name', $container).val(configuration.nav_tab_name);
                $('#nav_tab_height', $container).val(configuration.nav_tab_height);
                $('input[type="radio"][name="nav_tab_type"][value="'+ configuration.nav_tab_type +'"]').attr('checked',true);
                $('#customClass', $container).val(configuration.customClass);

                $('#defaultPage', $container).change(function () {
                    var $this = $(this);
                    if($this.attr('checked')) {
                        $('.defaultPage').show();
                    } else {
                        $('.defaultPage').hide();
                    }
                }).trigger('change')
            };
            
            var configurer = $.ui.component.BaseComponentConfigurer(configurerPrototype);
            return configurer;
        };



        component.prototype.getDefinitionJson = function() {
            var options = this.options;
            var id = this.getId();
            options.id = id;
            return options;
        };

        return component;
    });