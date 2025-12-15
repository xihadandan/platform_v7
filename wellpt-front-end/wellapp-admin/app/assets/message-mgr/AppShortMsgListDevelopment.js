define(["constant", "commons", "server", "ListViewWidgetDevelopment"],
    function(constant, commons, server, ListViewWidgetDevelopment) {
        // 视图组件二开基础
        var AppShortMsgListDevelopment = function() {
            ListViewWidgetDevelopment.apply(this, arguments);
        };
        // 接口方法
        commons.inherit(AppShortMsgListDevelopment, ListViewWidgetDevelopment, {
            init: function() {
                var _self = this;
                var $search = _self.getWidget().element.find('.keyword-search-wrap');
                var $typeSelect = $('<input>', {
                    id: 'queryFields',
                    class: 'query-fields',
                    style: 'width: 140px'
                });
                var $typeSelectWrap = $('<div>', {
                    class: 'pull-right',
                    style: 'width: 140px'
                });
                $typeSelectWrap.append($typeSelect);
                $search.before($typeSelectWrap);
                $typeSelect.wSelect2({
                    valueField: 'queryFields',
                    searchable: false,
                    defaultBlank: false, //显示空值
                    showEmpty: false, //显示空值
                    style: { 'height': '34px' },
                    data: [{
                        id: '-1',
                        text: '不自动刷新'
                    }, {
                        id: '5',
                        text: '每5秒刷新'
                    }, {
                        id: '10',
                        text: '每10秒刷新'
                    }, {
                        id: '20',
                        text: '每20秒刷新'
                    }, {
                        id: '30',
                        text: '每30秒刷新'
                    }, {
                        id: '60',
                        text: '每1分钟刷新'
                    }]
                }).wSelect2('val', '-1');

                var timer;
                $typeSelect.on('change', function() {
                    var $this = $(this);
                    var _val = $this.val();
                    clearInterval(timer);
                    if (_val === '-1') {
                        return;
                    }

                    timer = setInterval(function() {
                        _self.widget.refresh(true);
                    }, _val * 1000);
                    _self.widget.refresh(true);
                })
            },
            btn_detail: function(event, ui, rowData) {
                appModal.dialog({
                    title: "短信内容",
                    size: 'small',
                    type: 'default',
                    message: rowData.body,
                    buttons: {}
                })
            }
        });

        return AppShortMsgListDevelopment;
    });