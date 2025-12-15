define(["constant", "commons", "server", "appModal", "AppPtMgrDetailsWidgetDevelopment", "AppPtMgrCommons", 'dataStoreBase', 'formBuilder','echarts'],
    function (constant, commons, server, appModal, AppPtMgrDetailsWidgetDevelopment, AppPtMgrCommons, DataStore, formBuilder,echarts) {
        var validator;
        var listView;
        // 平台管理_产品集成_消息格式配置详情_HTML组件二开
        var AppMessageQueueProcessingStatisticsWidgetDevelopment = function () {
            AppPtMgrDetailsWidgetDevelopment.apply(this, arguments);
        };

        // 接口方法
        commons.inherit(AppMessageQueueProcessingStatisticsWidgetDevelopment, AppPtMgrDetailsWidgetDevelopment, {
            init: function () {
                // 基于准备好的dom，初始化echarts实例
                var myChart = echarts.init(document.getElementById('chart'));

                // 指定图表的配置项和数据
                var option = {
                    title: {
                        text: '消息队列处理统计',
                        x: 'center'
                    },
                    tooltip: {
                        trigger: 'item',
                        formatter: "{a} <br/>{b} : {c} ({d}%)"
                    },
                    legend: {
                        orient: 'vertical',
                        left: 'left',
                        data: ['待处理即时消息队列', '已处理即时消息队列', '待处理定时消息队列', '已处理定时消息队列']
                    },
                    series: [
                        {
                            name: '数据来源',
                            type: 'pie',
                            radius: '55%',
                            center: ['50%', '60%'],
                            label: {
                                formatter: '{b} : {c}',
                            },
                            data: [
                                // {value: 335, name: '待处理即时消息队列', code: 'messageQueue'},
                                // {value: 310, name: '已处理即时消息队列', code: 'messageQueueHis'},
                                // {value: 234, name: '待处理定时消息队列', code: 'scheduleMessageQueueEntity'},
                                // {value: 135, name: '已处理定时消息队列', code: 'scheduleMessageQueueHisEntity'}
                            ],
                            itemStyle: {
                                emphasis: {
                                    shadowBlur: 10,
                                    shadowOffsetX: 0,
                                    shadowColor: 'rgba(0, 0, 0, 0.5)'
                                }
                            }
                        }
                    ]
                };

                // 使用刚指定的配置项和数据显示图表。
                myChart.setOption(option);

                var callChartData = function () {
                    $.get(ctx + "/message/queue/count", {}, function (res) {
                        myChart.setOption({
                            series: [
                                {
                                    data: res.data
                                }
                            ]
                        })
                    });
                };
                callChartData();


                myChart.on('click', function (params) {
                    $("#queueType").find('option[value=' + params.data.code + ']').prop('selected', true);
                    $("#queueType").trigger('change');

                });
            }

        });
        return AppMessageQueueProcessingStatisticsWidgetDevelopment;
    });