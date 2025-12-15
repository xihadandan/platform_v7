define(["jquery", "commons", "constant", "server", "ViewDevelopmentBase", "appModal", "minicolors"],
    function ($, commons, constant, server, ViewDevelopmentBase, appModal, minicolors) {
        var DmsDataLabelDevelopment = function () {
            ViewDevelopmentBase.apply(this, arguments);
        };

        commons.inherit(DmsDataLabelDevelopment, ViewDevelopmentBase, {

            init: function () {
                var _self = this;
            },

            afterRender: function (options, configuration) {
                var _self = this;
                var $tableElement = _self.widget.$tableElement;
                //绑定表格行上颜色选择器
                $tableElement.on('post-body.bs.table', function (event, data) {
                    $(".rowLabelColor").each(function () {
                        _self.loadMiniColorsPlugin($(this), {
                            'labelUuid': $(this).attr('uuid'),
                            'labelColor': $(this).val()
                        });
                    });
                });

            },


            loadMiniColorsPlugin: function ($target, label) {
                var _self = this;
                var swatches = "#90caf9|#ef9a9a|#a5d6a7|#fff59d|#ffcc80|#bcaaa4|#eeeeee|#f44336|#2196f3|#4caf50|#ffeb3b|#ff9800|#795548|#9e9e9e";
                var defaultValue = label ? label.labelColor : '#90caf9';
                $target.attr('last_color', label ? label.labelColor : '');
                $target.minicolors({
                    control: 'hue',
                    defaultValue: defaultValue,
                    format: 'hex',
                    position: 'bottom left',
                    letterCase: 'lowercase',
                    swatches: swatches.split("|"),
                    change: function (value, opacity) {
                    },
                    hide: function () {
                        if (label && $(".minicolors-focus").length == 1 && $target.is('.rowLabelColor')) {//表格行颜色修改
                            if ($target.attr('last_color') == $target.val()) {//颜色未修改
                                return true;
                            }
                            server.JDS.call({
                                service: 'dmsDataLabelService.saveDmsDataLabel',
                                version: '',
                                data: [{
                                    'uuid': (label ? label.labelUuid : null),
                                    'labelColor': $target.val()
                                }],
                                success: function (result) {
                                    appModal.success('颜色修改成功');
                                    $target.attr('last_color', $target.val());
                                    _self.refresh();
                                    _self.refreshLeftSideMenus();
                                },
                                error: function (jqXHR) {
                                    //var response=JSON.parse(jqXHR.responseText);
                                }
                            });

                        }
                    },
                    theme: 'bootstrap'
                });
            },

            /**
             * 刷新左侧导航栏关于标签的导航项
             */
            refreshLeftSideMenus: function () {
                var $tagMenu = $("ul[loadintf='com.wellsoft.pt.dms.support.DmsDataLabelTreeDataProvider']");
                if ($tagMenu.length > 0) {
                    $tagMenu.trigger('reloadMenuItem', arguments);
                }
            },


        });

        return DmsDataLabelDevelopment;
    });