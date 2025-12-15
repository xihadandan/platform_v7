define([ "constant", "commons", "server","appModal", "ListViewWidgetDevelopment" ],
    function(constant, commons, server,appModal, ListViewWidgetDevelopment) {
        var JDS = server.JDS;
        var AppSysLogListDevelopment = function() {
            ListViewWidgetDevelopment.apply(this, arguments);
        };
        commons.inherit(AppSysLogListDevelopment, ListViewWidgetDevelopment, {
            btn_check:function(e){
                var index = $(e.target).parents("tr").data("index");
                var rowObject = this.getData()[index];
                var self = this;
                var $div =  $("<div>", {"id": "infoDetailContainer", "style": "overflow:auto;max-height:400px;"}).append(
                    $("<div>").append(
                        $("<pre>", {
                            "class": "infoDetailPre infoDetailPreFirst",
                            "style": "border:none;background:transparent;"
                        })
                    )
                );
                $("body").append($div);
                appModal.dialog({
                    title:"日志详情--[ 提示：上下滚动加载更多日志 ]",
                    message:$("#infoDetailContainer"),
                    size : 'large',
                    buttons : {
                        confirm : {
                            label : "复制",
                            className : "btn-primary",
                            callback:function(){
                                var content = $("#infoDetailContainer pre").text() || "";
                                self.copyContent(content,".modal-dialog")
                                return false;
                            }
                        },
                        cancel : {
                            label : "取消",
                            className : "btn-default"
                        }
                    },

                    shown: function (e) {
                        $(".infoDetailPre").text(rowObject.message.trim());

                        var scrollAllow = true;
                        var preTime = new Date(rowObject['timestamp']).getTime(),
                            nextTime = preTime, preUuid = rowObject['uuid'], nextUuid = preUuid;

                        $("#infoDetailContainer")[0].onmousewheel = function (e) {
                            e = e || window.event;
                            if (e.wheelDelta && scrollAllow) {
                                var nScrollHight = $("#infoDetailContainer")[0].scrollHeight;
                                var nScrollTop = $("#infoDetailContainer")[0].scrollTop;
                                var height = $('#infoDetailContainer').height();

                                scrollAllow = false;//防止快速滚动
                                if (e.wheelDelta > 0) {
                                    if (nScrollTop == 0) {//往上滚动
                                        var dataList = self.loadMore(preTime, true);
                                        for (var i = 0, len = dataList.length, j = 0; i < len; i++) {

                                            if (dataList[i].uuid != preUuid) {
                                                var $pre = $("<pre>", {"class": "infoDetailPre"}).text(dataList[i].message.trim());
                                                $pre.insertBefore("#infoDetailContainer pre:eq(0)");
                                                if (++j == 10) {//只取10条最近的日志
                                                    preTime = parseInt(new Date(dataList[i]['timestamp']).getTime());
                                                    preUuid = dataList[i].uuid;
                                                    break;
                                                }
                                            }
                                            preTime = parseInt(new Date(dataList[i]['timestamp']).getTime());
                                            preUuid = dataList[i].uuid;
                                        }
                                    }
                                } else {
                                    //1.滚动条下拉到最底部的时候; 2.无滚动条;
                                    var downScrollLoad = (nScrollTop + height + 12 >= nScrollHight) || (nScrollHight == height + 12);
                                    if (downScrollLoad) {//往下滚动
                                        var dataList = self.loadMore(nextTime, false);
                                        for (var i = 0, len = dataList.length, j = 0; i < len; i++) {
                                            if (dataList[i].uuid != nextUuid) {
                                                var $pre = $("<pre>", {"class": "infoDetailPre"}).text(dataList[i].message.trim());
                                                $pre.insertAfter("#infoDetailContainer pre:last(0)");
                                                if (++j == 10) {//只取10条最近的日志
                                                    nextTime = parseInt(new Date(dataList[i]['timestamp']).getTime());
                                                    nextUuid = dataList[i].uuid;
                                                    break;
                                                }
                                            }
                                            nextTime = parseInt(new Date(dataList[i]['timestamp']).getTime());
                                            nextUuid = dataList[i].uuid;
                                        }
                                    }
                                }
                                window.setTimeout(function () {
                                    scrollAllow = true;
                                }, 500);
                            }
                        }
                    },
                    callback:function(){
                        $("#infoDetailContainer").remove()
                    }
                })
            },
            loadMore:function(baseTime, prev) {
                var data = [];
                var param = {
                    page: {
                        currentPage: 0,
                        pageSize: 100
                    }
                };
                /**
                 * 向上滚动拉取日志，则日志的选取时间是 <=endTime
                 * 向下滚动拉取日志，则日志的选取时间是 >=beginTime
                 */
                param[prev ? 'endTime' : 'beginTime'] = baseTime;
                if (param.endTime) {
                    param.beginTime = baseTime - 1000 * 60 * 60;//限定范围，减小检索范围
                    //向上滚动拉取的日志按时间降序，拉取到的数据会按顺序添加到日志弹窗
                    param.orders = [{
                        direction: 'DESC',
                        property: 'timestamp'
                    }];
                } else if (param.beginTime) {
                    param.endTime = baseTime + 1000 * 60 * 60;//限定范围，减小检索范围
                    //向下滚动拉取的日志按时间升序，拉取到的数据会按顺序添加到日志弹窗
                    param.orders = [{
                        direction: 'ASC',
                        property: 'timestamp'
                    }];
                }

                JDS.call({
                    service: "elasticSearchLogService.querySysLogs",
                    version: '',
                    data: [ param ],
                    async: false,
                    version:"",
                    success: function (result) {
                        if (result.success) {
                            data = result.data.dataList;
                        }
                    }
                });
                return data;
            },
            btn_copy: function(e){
                var content = this.getContent(e)
                this.copyContent(content,"body")
            },
            copyContent:function(content,$ele) {
                var $input = $("<textarea>", {
                    "id": "tempCopyContent",
                    "style": "opacity:0"
                });
                $input.val(content);
                $($ele).append($input);
                document.getElementById('tempCopyContent').select(); // 选择对象
                document.execCommand("Copy"); // 执行浏览器复制命令
                $("#tempCopyContent").remove();
                appModal.toast('复制成功');
            },
            getContent:function(e){
                var index = $(e.target).parents("tr").data("index");
                var data = this.getData();
                return data[index].message;
            }
        });

        return AppSysLogListDevelopment;
    });

