define(["jquery", "commons", "constant", "server", "CalendarWidgetDevelopment", "appModal", "moment", "formBuilder"], function ($, commons, constant, server,
                                                                                                                                CalendarWidgetDevelopment, appModal, moment, formBuilder, layer) {
    var currUser = server.SpringSecurityUtils.getUserDetails();
    var StringUtils = commons.StringUtils;
    var MyCalendarDevelopment = function (wWidget) {
        CalendarWidgetDevelopment.apply(this, arguments);
    };
    if (appContext.isMobileApp()) {
        commons.inherit(MyCalendarDevelopment, CalendarWidgetDevelopment, {
            //重构删除按钮事件
            deleteEventClick: function (event, configuration, options, callback) {
                var _self = this;
                appModal.confirm("确认删除?", function (result) {
                    if (result) {
                        _self.getWidget()._deleteEvent(event.uuid, function () {
                            if ($.isFunction(callback) && callback.apply(this, arguments) === false) {
                                return false;
                            }
                            _self.getWidget().refresh();
                        });
                    }
                })
            },
            afterEventDialogRender: function (panel, options, configuration) {
                var self = this;
                options.event = options.event || {};
                var options = self.getWidget().options;
                var repeatDayOptions = options.repeatDayOptions
                var repeatWeekOptions = options.repeatWeekOptions;
                var contentSelector = ".form-content";
                var addressSelector = ".form-group-seperate";
                var content = $(contentSelector, panel)[0];
                var seperate = $(addressSelector, panel)[0];

                // 控制部分用户（partUsers）显示隐藏
                var publicRange = options.event.publicRange || "";
                var partUsersEle = $("input[type=hidden][name=partUsers]", panel)[0];
                var publicRangeEle = $("input[type=hidden][name=publicRange]", panel)[0];
                if (partUsersEle && publicRangeEle) {
                    partUsersEle = $(partUsersEle).closest("a.mui-navigate-right");
                    if (false == StringUtils.contains(publicRange, "3")) {
                        partUsersEle.style.display = "none";
                    }
                    publicRangeEle.addEventListener("change", function (event) {
                        partUsersEle.style.display = StringUtils.contains(this.value, "3") ? "" : "none";
                    });
                }

                // 重复
                options.event.repeatConf = options.event.repeatConf || {};
                if (typeof options.event.repeatConf === "string") {
                    options.event.repeatConf = $.parseJSON(options.event.repeatConf);
                }
                var repeatType = options.event.repeatConf["repeatType"];
                var repeatValue = options.event.repeatConf["repeatValue"];
                var repeatDayDom = formBuilder.buildOutlineCheckbox({
                    label: "每天",
                    name: "repeatDay",
                    value: repeatType === "day" ? repeatValue : "",
                    // container : content,
                    items: [{
                        id: "work",
                        text: "工作日"
                    }, {
                        id: "sat",
                        text: "星期六"
                    }, {
                        id: "sun",
                        text: "星期日"
                    }],
                });
                var repeatWeekDom = formBuilder.buildOutlineCheckbox({
                    label: "每周",
                    name: "repeatWeek",
                    // container : content,
                    value: repeatType === "week" ? repeatValue : "",
                    items: [{
                        id: "all",
                        text: "全部"
                    }, {
                        id: "w1",
                        text: "周一"
                    }, {
                        id: "w2",
                        text: "周二"
                    }, {
                        id: "w3",
                        text: "周三"
                    }, {
                        id: "w4",
                        text: "周四"
                    }, {
                        id: "w5",
                        text: "周五"
                    }, {
                        id: "w6",
                        text: "周六"
                    }, {
                        id: "w7",
                        text: "周日"
                    }],
                });
                var repeatMonthOptions = {
                    label: "每月",
                    name: "repeatMonth",
                    // container : content,
                    value: repeatType === "month" ? repeatValue : "",
                    items: [],
                }
                for (var i = 1; i <= 31; i++) {
                    repeatMonthOptions.items.push({
                        id: i,
                        text: i + "日"
                    });
                }
                var repeatMonthDom = formBuilder.buildOutlinRadio(repeatMonthOptions);
                var repeatYearDom = formBuilder.buildDatetimepicker({
                    label: "每年",
                    name: "repeatYear",
                    callback: function (value, selectItem) {
                        return selectItem.m.value + '-' + selectItem.d.value;
                    },
                    // container : content,
                    value: repeatType === "year" ? repeatValue : "",
                    controlOption: {
                        format: 'YYYY-MM-DD',
                    },
                });

                var repeatEditor = function (options, event) {
                    var aEditor = this;
                    var repeatEditorPanel = formBuilder.selectEditor.apply(this, arguments);
                    $(repeatEditorPanel).on("change", "input[name=" + options.name + "]", function (event) {
                        var elem = this;
                        repeatYearDom.style.display = "none";
                        repeatMonthDom.style.display = "none";
                        repeatWeekDom.style.display = "none";
                        repeatDayDom.style.display = "none";
                        switch (elem.value) {
                            case "day" : {
                                repeatDayDom.style.display = "";
                                console.log("d");
                                break;
                            }
                            case "week" : {
                                repeatWeekDom.style.display = "";
                                console.log("w");
                                break;
                            }
                            case "month" : {
                                repeatMonthDom.style.display = "";
                                console.log("M");
                                break;
                            }
                            case "year" : {
                                repeatYearDom.style.display = "";
                                console.log("y");
                                break;
                            }
                            default : {
                                console.log("default");
                            }
                        }
                    });
                };

                var repeatDom = formBuilder.buildOutlinRadio({
                    editor: repeatEditor,
                    label: "重复",
                    name: "repeat",
                    value: repeatType,
                    // container : content,
                    items: [{
                        id: "",
                        text: "不重复"
                    }, {
                        id: "day",
                        text: "每天"
                    }, {
                        id: "week",
                        text: "每周"
                    }, {
                        id: "month",
                        text: "每月"
                    }, {
                        id: "year",
                        text: "每年"
                    }]
                });
                repeatYearDom.style.display = "none";
                repeatMonthDom.style.display = "none";
                repeatWeekDom.style.display = "none";
                repeatDayDom.style.display = "none";
                if (repeatType === "day") {
                    repeatDayDom.style.display = "";
                } else if (repeatType === "week") {
                    repeatWeekDom.style.display = "";
                } else if (repeatType === "month") {
                    repeatMonthDom.style.display = "";
                } else if (repeatType === "year") {
                    repeatYearDom.style.display = "";
                }
                seperate.parentNode.insertBefore(repeatYearDom, seperate.nextSibling);
                seperate.parentNode.insertBefore(repeatMonthDom, seperate.nextSibling);
                seperate.parentNode.insertBefore(repeatWeekDom, seperate.nextSibling);
                seperate.parentNode.insertBefore(repeatDayDom, seperate.nextSibling);
                seperate.parentNode.insertBefore(repeatDom, seperate.nextSibling);

                // 提醒
                options.event.remindConf = options.event.remindConf || {};
                if (typeof options.event.remindConf === "string") {
                    options.event.remindConf = $.parseJSON(options.event.remindConf);
                }
                var interval = options.event.remindConf["interval"];
                var intervalUnit = options.event.remindConf["intervalUnit"];
                var intervalUnitDom = formBuilder.buildOutlinRadio({
                    label: "间隔单位",
                    name: "intervalUnit",
                    value: intervalUnit,
                    // container : content,
                    items: [{
                        id: "min",
                        text: "分钟"
                    }, {
                        id: "hour",
                        text: "小时"
                    }, {
                        id: "day",
                        text: "天"
                    }, {
                        id: "week",
                        text: "周"
                    }, {
                        id: "month",
                        text: "月"
                    }],
                });
                var remindEditor = function (options, event) {
                    var aEditor = this;
                    var remindEditorPanel = formBuilder.selectEditor.apply(this, arguments);
                    $(remindEditorPanel).on("change", "input[name=" + options.name + "]", function (event) {
                        var elem = this;
                        intervalUnitDom.style.display = StringUtils.isBlank(elem.value) ? "none" : "";
                    });
                };
                var remindDom = formBuilder.buildOutlinRadio({
                    editor: remindEditor,
                    label: "提醒",
                    name: "remind",
                    value: interval,
                    // container : content,
                    items: [{
                        id: "",
                        text: "不提醒"
                    }, {
                        id: "1",
                        text: "1"
                    }, {
                        id: "2",
                        text: "2"
                    }, {
                        id: "3",
                        text: "3"
                    }, {
                        id: "4",
                        text: "4"
                    }, {
                        id: "5",
                        text: "5"
                    }, {
                        id: "10",
                        text: "10"
                    }, {
                        id: "15",
                        text: "15"
                    }, {
                        id: "20",
                        text: "20"
                    }, {
                        id: "30",
                        text: "30"
                    }],
                });
                intervalUnitDom.style.display = StringUtils.isBlank(interval) ? "none" : "";
                seperate.parentNode.insertBefore(intervalUnitDom, seperate.nextSibling);
                seperate.parentNode.insertBefore(remindDom, seperate.nextSibling);
            },
            //收集事项表单数据
            afterCollectEventData: function (options, configuration, newFormData) {
                //格式化周期开始和结束时间
                if (newFormData.repeatPeriodStartTime) {
                    newFormData['repeatPeriodStartTime'] = commons.DateUtils.format(new Date(
                        newFormData['repeatPeriodStartTime']), "yyyy-MM-dd HH:mm:ss");
                }
                if (newFormData.repeatPeriodEndTime) {
                    newFormData['repeatPeriodEndTime'] = commons.DateUtils.format(new Date(
                        newFormData['repeatPeriodEndTime']), "yyyy-MM-dd HH:mm:ss");
                }
                //事项开始,和结束时间是必须的，所以用 options中的值临时代替下，在服务端要根据重复的设置情况重新计算
                newFormData.startTime = newFormData.startTime || options.event.startTime;
                newFormData.endTime = newFormData.endTime || options.event.endTime;
                newFormData.isAll = newFormData.isAll === "true" || newFormData.isAll === true ? "1" : "0";
                newFormData.isRepeat = newFormData.isRepeat ? "1" : "0";
                newFormData.isRemind = newFormData.isRemind ? "1" : "0";
                newFormData.isFinish = options.event.isFinish ? options.event.isFinish : "0";
                newFormData.noticeTypes = typeof (newFormData.noticeTypes) == "object" ? newFormData.noticeTypes.join(";") : newFormData.noticeTypes;
                newFormData.noticeObjs = typeof (newFormData.noticeObjs) == "object" ? newFormData.noticeObjs.join(";") : newFormData.noticeObjs;
                newFormData.repeatMarkId = options.event.repeatMarkId;

                //计算重复的配置项
                if ("1" == newFormData.isRepeat) {
                    var repeatConf = {
                        repeatType: newFormData.repeat,
                    }
                    //按天重复
                    if ("day" == repeatConf.repeatType) {
                        if (newFormData.repeatDay == null || newFormData.repeatDay.length == 0) {
                            newFormData.repeatDay = [];
                        }
                        repeatConf.repeatValue = typeof (newFormData.repeatDay) == "object" ? newFormData.repeatDay.join(";") : newFormData.repeatDay;

                        typeof (newFormData.repeatDay) == "object"
                    } else if ("week" == repeatConf.repeatType) {
                        if (newFormData.repeatWeek == null || newFormData.repeatWeek.length == 0) {
                            newFormData.repeatWeek = [];
                        }
                        repeatConf.repeatValue = typeof (newFormData.repeatWeek) == "object" ? newFormData.repeatWeek.join(";") : newFormData.repeatWeek;
                    } else if ("month" == repeatConf.repeatType) {
                        repeatConf.repeatValue = newFormData.repeatMonth;
                    } else if ("year" == repeatConf.repeatType) {
                        if (newFormData.repeatYear && newFormData.repeatYear.length === 10) {
                            newFormData.repeatYear = newFormData.repeatYear.substr(5);
                        }
                        repeatConf.repeatValue = newFormData.repeatYear;
                    }
                    newFormData.repeatConfVo = JSON.stringify(repeatConf);
                }

                //计算提醒的配置项
                if ("1" == newFormData.isRemind) {
                    var remindConf = {
                        interval: newFormData.remind,
                        intervalUnit: newFormData.intervalUnit
                    }
                    newFormData.remindConfVo = JSON.stringify(remindConf);
                }
            },
            otherValidateRules: function (data) {
                var self = this;
                var errorMsg = null;
                if (data.title.length > 200) {
                    errorMsg = "\"标题\"输入的内容不能超过100字";
                }
                if (data.address && data.address.length > 200) {
                    errorMsg = "\"地址\"输入的内容不能超过100字";
                }
                if (data.eventContent && data.eventContent.length > 200) {
                    errorMsg = "\"补充说明\"输入的内容不能超过100字";
                } else if (data.publicRange === "3" && StringUtils.isBlank(data.partUsers)) {
                    errorMsg = "部分用户不能为空";
                } else if ((data.repeat === "day" && data.repeatDay.length <= 0)
                    || (data.repeat === "week" && data.repeatWeek.length <= 0)
                    || (data.repeat === "year" && data.repeatYear.length <= 0)) {
                    errorMsg = "请选择重复的配置项";
                }
                if (StringUtils.isNotBlank(errorMsg)) {
                    $.toast(errorMsg);
                    return false;
                }
            },
        })
    } else {

    }
    return MyCalendarDevelopment;
});