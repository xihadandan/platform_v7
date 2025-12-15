define(["constant", "commons", "server", "appContext", "appModal", "AppPtMgrListViewWidgetDevelopment",
    "AppPtMgrCommons"
], function(constant, commons, server, appContext, appModal,
    AppPtMgrListViewWidgetDevelopment, AppPtMgrCommons) {
    var StringUtils = commons.StringUtils;
    var JDS = server.JDS;

    // 平台管理_产品集成_模块_消息格式列表_视图组件二开
    var AppMessageQueueMonitorListViewWidgetDevelopment = function() {
        AppPtMgrListViewWidgetDevelopment.apply(this, arguments);
    };

    // 接口方法
    commons.inherit(AppMessageQueueMonitorListViewWidgetDevelopment, AppPtMgrListViewWidgetDevelopment, {
        // 准备创建回调
        prepare: function() {
            var _self = this;
            _self.addWidgetParamCondition();
        },
        addWidgetParamCondition: function() {
            var _self = this;
            var widget = _self.getWidget();
            // 获取传给组件的参数
            var params = _self.getWidgetParams();
            var otherConditions = [];
            // 归属产品集成信息UUID
            if (StringUtils.isNotBlank(params.appPiUuid)) {
                var condition = {
                    columnIndex: "parentAppPiUuid",
                    value: params.appPiUuid,
                    type: "eq"
                };
                otherConditions.push(condition);
            }
            widget.addOtherConditions(otherConditions);
        },
        init: function() {
            var _self = this;
            var queueTypeArr = [{
                id: 'messageQueue',
                text: '即时消息-待处理队列'
            }, {
                id: 'messageQueueHis',
                text: '即时消息-已处理队列'
            }, {
                id: 'scheduleMessageQueueEntity',
                text: '定时消息-待处理队列'
            }, {
                id: 'scheduleMessageQueueHisEntity',
                text: '定时消息-已处理队列'
            }];

            var $search = _self.getWidget().element.find('.keyword-search-wrap');
            var $typeSelect = $('<input>', {
                id: 'queueType',
                "class": 'query-fields',
                style: 'width: 150px'
            });
            var $typeSelectWrap = $('<div>', {
                "class": 'pull-left',
                style: 'width: 150px'
            });
            $typeSelectWrap.append($typeSelect);
            $search.before($typeSelectWrap);
            $typeSelect.wellSelect({
                valueField: 'queueType',
                searchable: false,
                style: { 'height': '34px' },
                data: queueTypeArr
            }).wellSelect('val', 'messageQueue');

            $typeSelect.on('change', function() {
                var $this = $(this);
                var type = $this.val();
                if (StringUtils.isNotBlank(type)) {
                    _self.widget.addParam('queryType', type);
                }
                _self.widget.refresh(true);
            });

            var $freshTypeSelect = $('<input>', {
                id: 'queryFields',
                class: 'query-fields',
                style: 'width: 140px'
            });
            var $typeSelectWrap = $('<div>', {
                class: 'pull-right',
                style: 'width: 140px'
            });
            $typeSelectWrap.append($freshTypeSelect);
            $search.before($typeSelectWrap);
            $freshTypeSelect.wellSelect({
                valueField: 'queryFields',
                searchable: false,
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
            }).wellSelect('val', '-1');

            var timer;
            $freshTypeSelect.on('change', function() {
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
        }
    });
    return AppMessageQueueMonitorListViewWidgetDevelopment;
});