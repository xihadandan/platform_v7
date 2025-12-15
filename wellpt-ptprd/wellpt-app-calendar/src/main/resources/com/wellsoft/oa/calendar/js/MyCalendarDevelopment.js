define(["commons", "constant", "server", "CalendarWidgetDevelopment", "appModal", "moment", "formBuilder", "layer"], function (commons, constant, server,
                                                                                                                               CalendarWidgetDevelopment, appModal, moment, formBuilder, layer) {
    var currUser = server.SpringSecurityUtils.getUserDetails();
    var StringUtils = commons.StringUtils;
    var MyCalendarDevelopment = function (wWidget) {
        CalendarWidgetDevelopment.apply(this, arguments);
    };
    // 接口方法
    commons.inherit(MyCalendarDevelopment, CalendarWidgetDevelopment, {
        // 重复的类型
        repeatTypeOptions: {
            'day': '每天',
            'week': '每周',
            'month': '每月',
            'year': '每年',
        },
        // 每天重复的选项
        repeatDayOptions: {
            'work': '工作日',
            'sat': '星期六',
            'sun': '星期日',
        },
        // 每周重复的选项
        repeatWeekOptions: {
            'all': '全部',
            'w1': '周一',
            'w2': '周二',
            'w3': '周三',
            'w4': '周四',
            'w5': '周五',
            'w6': '周六',
            'w7': '周日',
        },

        // 提醒的间隔时间
        intervalOptions: new Array(1, 2, 3, 4, 5, 10, 15, 20, 30, 45, 60),
        // 提醒的间隔时间单位
        unitOptions: {
            'min': '分钟',
            'hour': '小时',
            'day': '天',
            'week': '周',
            'month': '月'
        },

        beforeRender: function (options, configuration) {
            //设置初始化条件，是看我的全部日程
            var userId = currUser.userId;
            var filterCriterion = {
                columnIndex: "calendarCreator",
                value: userId,
                type: "eq"
            };
            //首页需要
            // 20200918 1、首页日程面板展示用户作为参与人的日程 #3913 1、我的日程中展示用户作为参与人的日程 #3913
            //this.addOtherConditions([filterCriterion]);
//			this.addOtherConditions([filterCriterion]); 先关闭有需要打开
        },
        init: function () {
            console.log("MyCalendarDevelopment.init");
            var _self = this;
            //定义独立的新增日程按钮的点击事件
            $("#btn_new_event").click(function (e) {
                _self.dayClick(moment(), e, null);
                return false;
            });
            _self.formatHomePageTime();
            $(".well-calendar-body-date").on("click", function () {
                _self.formatHomePageTime();
            });
        },
        formatHomePageTime: function () {
            setTimeout(function () {
                $.each($(".time"), function (i, item) {
                    var time = $(this).html();// 时分秒
                    if (StringUtils.isNotBlank(time)) {
                        $(this).html(time.substr(0, 5));// 时分
                    }
                });
            }, 2);//2是个幸运的数字
        },
        dayClick: function (date, jsEvent, view) {
            //关注入口进来,不触发新建事项
            if ($("button.js-add-attention-user").length > 0) {
                return false;
            } else {
                //格式化周期开始和结束时间时候把秒去掉
                var repeatPeriodStartTime = date.format("YYYY-MM-DD HH:mm");
                var repeatPeriodEndTime = date.add(30, 'minutes').format("YYYY-MM-DD HH:mm");
                //如果是从月视图进来的的，则默认是当前时间
                if ((view && view.type == 'month') || date._ambigTime) {
                    date._isUTC = false
                    var d = new Date(date._d.getFullYear(), date._d.getMonth(), date._d.getDate(), 8, 0);
                    date._d = d;
                    var repeatPeriodStartTime = date.format("YYYY-MM-DD HH:mm");
                    var repeatPeriodEndTime = date.add(30, 'minutes').format("YYYY-MM-DD HH:mm");
                }
                var $dialog = this.getWidget().openEventDialog({
                    date: date,
                    jsEvent: jsEvent,
                    view: view,
                    event: {
                        repeatPeriodStartTime: repeatPeriodStartTime,
                        repeatPeriodEndTime: repeatPeriodEndTime,
                        noticeTypes: "ON_LINE;SMS",
                        noticeObjs: "1;3",
                    }
                });
                return $dialog;
            }
        },
        subEventAfterRender: function (event, element, view) {
        },
        //事件渲染完后触发，用来做展示已安排方面的处理
        eventAfterRender: function (event, element, view) {
            var _self = this;
            console.log("eventAfterRender===========");
            //所有 a 标签 top 0px
            //$(element).css("top","0%")
            if (view.name != "agendaDay") {
                $(element).removeClass("fc-short");
                // $(element).css("top",'');
                $(element).find(".fc-time").css("display", "block");
                $(element).find(".fc-title").css("display", "block");
                $(element).find(".fc-title").css("overflow", "hidden");
                $(element).find(".fc-title").css("text-overflow", "ellipsis");
                $(element).find(".fc-title").css("white-space", "nowrap");
            }
            if (event.isCanView == true) {
                //自己的日程不需要显示名称
                if (currUser.userId == event.calendarCreator) {
                    $(element).find(".fc-title").attr("title", event.title);
                } else {
                    $(element).find(".fc-title").html("[" + event.calendarCreatorName + "]" + event.title);
                    $(element).find(".fc-title").attr("title", "[" + event.calendarCreatorName + "]" + event.title);
                }
            } else {
                $(element).find(".fc-title").html("[" + event.calendarCreatorName + "]已安排");
                $(element).find(".fc-title").attr("title", "[" + event.calendarCreatorName + "]已安排");
            }
            _self.subEventAfterRender(event, element, view);
        },

        eventClick: function (event, jsEvent, view) {
            //格式化周期开始和结束时间
            if (event.repeatPeriodStartTime) {
                event.repeatPeriodStartTime = commons.DateUtils.format(new Date(
                    event.repeatPeriodStartTime), "yyyy-MM-dd HH:mm");
            }
            if (event.repeatPeriodEndTime) {
                event.repeatPeriodEndTime = commons.DateUtils.format(new Date(
                    event.repeatPeriodEndTime), "yyyy-MM-dd HH:mm");
            }
            if (typeof event.noticeObjs == "string") {
                event.noticeObjs = event.noticeObjs ? event.noticeObjs.split(";") : [];
            } else {
                event.noticeObjs = event.noticeObjs ? event.noticeObjs : [];
            }

            if (typeof event.noticeTypes == "string") {
                event.noticeTypes = event.noticeTypes ? event.noticeTypes.split(";") : [];
            } else {
                event.noticeTypes = event.noticeTypes ? event.noticeTypes : [];
            }
            if (event.isCanView) {
                var $dialog = this._super.apply(this, [event, jsEvent, view]);
            } else {
                var $dialog = appModal.dialog({
                    title: "日程详情",
                    size: "large",// large
                    message: $("<div id='div_calendar_simple_event'></div>"),// $itemForm,
                    shown: function (_$dialog) {
                        // 启用自定义表单
                        var $div = _$dialog.find("#div_calendar_simple_event");
                        var viewFieldOptions = {
                            container: $div,
                            inputClass: 'w-viewform-option',
                            rowClass: "js-item-row",
                            labelColSpan: "3",
                            controlColSpan: "9",
                            multiLine: false, // 一行一列/一行两列
                            contentItems: []
                        };
                        var titleOpts = {
                            label: "标题",
                            type: 'label',
                            value: event.title,
                            text: "[" + event.calendarCreatorName + "]已安排",
                        };
                        var startTime = moment(event.startTime).format("YYYY-MM-DD HH:mm");
                        var endTime = moment(event.endTime).format("YYYY-MM-DD HH:mm");
                        var timeOpts = {
                            label: "时间",
                            type: 'label',
                            value: startTime + "--" + endTime,
                            text: startTime + "--" + endTime,
                        };
                        viewFieldOptions.contentItems.push(titleOpts, timeOpts);
                        formBuilder.buildContentAsLabel(viewFieldOptions);
                    },
                    buttons: {
                        '关闭': function () {
                        }
                    }
                });
            }


        },

        //添加其他的验证器
        addOtherValidateRules: function (validateOptions, configuration) {
            //无效
            //validateOptions.focusCleanup=true;
            //validateOptions.onkeyup=null;

            validateOptions.rules['itemform_repeatType'] = {
                checkRepeatConf: true
            };
            validateOptions.rules['itemform_belongObjId'] = {
                checkBelongObjId: true
            };
            //检查结束时间不能大于开始时间
            //validateOptions.rules['itemform_repeatPeriodEndTime']['checkRepeatPeriodEndTime'] = true;
            validateOptions.rules['itemform_repeatPeriodEndTime'] = {checkRepeatPeriodEndTime: true};

            //检查公开范围的部分用户
            validateOptions.rules['itemform_partUsers'] = {
                checkPartUsers: true
            };
            //附件上传大小限制
            validateOptions.rules['itemform_fileUuids'] = {
                checkFileCapacity: true

            };
            $.validator.addMethod("checkRepeatConf", function (value, element) {
                //有设置重复进行检查
                if ($("#itemform_isRepeat").attr("checked")) {
                    if ("day" == value) {
                        return $("#js-repeat-day-options :checked").length > 0;
                    } else if ("week" == value) {
                        return $("#js-repeat-week-options :checked").length > 0;
                    } else if ("month" == value) {

                    } else if ("year" == value) {
                        var conf = $("#js-repeat-year-options").find("input[name='itemform_repeatYear']").val();
                        return commons.StringUtils.isNotBlank(conf);
                    }
                }
                return true;
            }, "请选择重复的配置项。");

            $.validator.addMethod("checkBelongObjId", function (value, element) {
                //有设置重复进行检查
                var data = $("#itemform_belongObjId").select2("data");
                return data ? true : false;
            }, "请选择日程归属。");

            $.validator.addMethod("checkRepeatPeriodEndTime", function (value, element) {
                //有设置重复进行检查
                var start = moment($("#itemform_repeatPeriodStartTime").val());
                var end = moment(value);
                return start.isBefore(end);
            }, "结束时间不能早于开始时间。");

            $.validator.addMethod("checkPartUsers", function (value, element) {
                //公开范围设置=部分用户可见，则需检查部分用户的配置不能为空
                var publicRange = $("#itemform_publicRange").val();
                if ("3" == publicRange) {
                    return commons.StringUtils.isNotBlank(value);
                }
                return true;
            }, "部分用户不能为空。");
            $.validator.addMethod("checkFileCapacity", function (value, element) {
                //有设置重复进行检查

                var fileCanSave = true;
                if (StringUtils.isNotBlank(value)) {
                    server.JDS.call({
                        service: "calendarFacade.getFileSizesByIds",
                        data: [value],
                        async: false,
                        success: function (result) {
                            if (result.success == true) {
                                var fileM = result.data;

                                for (var index = 0; index < fileM.length; index++) {
                                    if (fileM[index] > 50) {
                                        //超过50M
                                        fileCanSave = false;
                                        return false;
                                    }
                                }
                            }
                        },
                    });
                }
                return fileCanSave;
            }, "单个附件大小不能超过50M");

        },

        //对请求回来的event数据进行加工处理
        dataSourceProcessing: function (dataSource) {
            var _self = this;
            if (dataSource && dataSource.length > 0) {
                $.each(dataSource, function (index, event) {
                    if (event.isFinish == 0) {
                        event.backgroundColor = "green";
                    }
                })
                //过滤掉不可见的 23656 日程设置参与人可见，但是登陆任意一用户打开日程管理都可以查看 20200922不可见的日程在后台就不应该返回
                /*var canViewCollect=dataSource.filter(function (item) {
                    if(item.isCanView == true){
                         return item;
                    }else{
                        item.title="我是要不可见的";
                    }
                  return item.isCanView == true;
                });
                 _self.widget._dataSource  = canViewCollect;*/
            }
        },

        afterEventDialogRender: function ($dialog, options, configuration) {
            var _self = this;
            console.log("MyCalendarDevelopment.afterEventDialogRender");
            var $row = this.create3checkRow($dialog);
            // 3checkbox插入在结束时间下面
            //
            $dialog.find("#itemform_repeatPeriodEndTime").parents(".form-group").after($row);
            //$dialog.find("#itemform_repeatPeriodEndTime").parents(".js-item-row").after($row);

            // 插入提醒的配置行
            var $remindRow = this.createRemindConfRow($dialog);
            $dialog.find("#itemform_repeatPeriodEndTime").parents(".form-group").after($remindRow);
            //$dialog.find("#itemform_repeatPeriodEndTime").parents(".js-item-row").next().after($remindRow);

            //插入重复 的配置行
            var $repeatRow = this.createRepeatConfRow($dialog);
            $dialog.find("#itemform_repeatPeriodEndTime").parents(".form-group").after($repeatRow);
            //$dialog.find("#itemform_repeatPeriodEndTime").parents(".js-item-row").next().after($repeatRow);


            //定义联动事件
            _self._initSelectAndCheckboxEvent();

            //根据options.event中的值，初始化值
            //是否重复
            if (1 == options.event.isRepeat) {
                $("#itemform_isRepeat").attr("checked", "checked").trigger("change");
                var conf = JSON.parse(options.event.repeatConf);
                $("#itemform_repeatType").val(conf.repeatType).trigger('change');
                if ("day" == conf.repeatType) { //按天重复
                    var values = conf.repeatValue ? conf.repeatValue.split(";") : [];
                    $.each(values, function (i) {
                        $("#itemform_repeatDay_" + values[i]).attr("checked", "checked").trigger("change");
                    });
                } else if ("week" == conf.repeatType) { //按周重复
                    var values = conf.repeatValue ? conf.repeatValue.split(";") : [];
                    $.each(values, function (i) {
                        $("#itemform_repeatWeek_" + values[i]).attr("checked", "checked").trigger("change");
                    });
                } else if ("month" == conf.repeatType) { //按月重复
                    $("#js-repeat-month-options").val(conf.repeatValue).trigger("change");
                } else if ("year" == conf.repeatType) { //按年重复
                    $("input[name='itemform_repeatYear']").val(conf.repeatValue);
                }
            } else {
                $("#itemform_isRepeat").removeAttr("checked").trigger("change");
            }
            //是否提醒
            if (1 == options.event.isRemind) {
                $("#itemform_isRemind").attr("checked", "checked").trigger("change");
                var conf = JSON.parse(options.event.remindConf);
                $("#itemform_interval").val(conf.interval);
                $("#itemform_intervalUnit").val(conf.intervalUnit);
            } else {
                $("#itemform_isRemind").removeAttr("checked").trigger("change");
            }
            //是否全天
            if (1 == options.event.isAll) {
                $("#itemform_isAll").attr("checked", "checked").trigger("change");
            } else {
                $("#itemform_isAll").removeAttr("checked").trigger("change");
            }


            //公开范围 的配置
            $("#itemform_publicRange").val(options.event.publicRange);
            $($.parseHTML("#itemform_publicRange")).trigger("change");

            //因为重复 配置的校验器比较特殊，所以需要设置对应的那些checkbox的change事件来检查是否提示错误
            $("#js-row-repeat-conf :checkbox").change(function () {
                $("#js-row-repeat-conf #itemform_repeatType").trigger("blur");
            });
            $("#js-row-repeat-conf :text").blur(function () {
                $("#js-row-repeat-conf #itemform_repeatType").trigger("blur");
            });


            //新增的时候
            if (StringUtils.isBlank(options.event.uuid)) {
                var selectCalendarUuid = null;
                var $selectMenu = $(".ui-wBootstrapTabs .metismenuul .nav-menu-active");
                if ($selectMenu.length > 0) {
                    var menuitem = $selectMenu.data("menuitem");
                    if (menuitem && menuitem.type.indexOf("Calendar") > -1) {
                        selectCalendarUuid = menuitem.calendar.uuid;
                    }
                } else {
                    // 没有当前选中的，则找默认日历本
                    $selectMenu = $(".ui-wBootstrapTabs .metismenuul a[title='默认日历本']").parents("li");
                    selectCalendarUuid = $selectMenu.attr("menuid");
                }
                if (selectCalendarUuid) {
                    //$dialog.find("#itemform_belongObjId").select2("val", selectCalendarUuid );
                    $dialog.find("#itemform_belongObjId").wellSelect("val", selectCalendarUuid);//6.1新换下拉框控件
                } else {
                    //如果为空就取默认日历本
                    $selectMenu = $(".ui-wBootstrapTabs .metismenuul a[title='默认日历本']").parents("li");
                    selectCalendarUuid = $selectMenu.attr("menuid");
                    $dialog.find("#itemform_belongObjId").wellSelect("val", selectCalendarUuid);//6.1新换下拉框控件
                }
                var showBlock = $("#form_calendar_event div.js-item-row:lt(5)");
                var hideBlock = $("#form_calendar_event div.js-item-row:gt(4)");
                $($("#form_calendar_event div.js-item-row")[$("#form_calendar_event div.js-item-row").length - 1]).after("<div id='btn' style=' text-align: center;width: 100%;cursor:pointer;color: #0099FF;'>查看更多<span class='iconfont icon-ptkj-xianmiaoshuangjiantou-xia'></span></div>");
                hideBlock.css("height", "0px").css("overflow", "hidden");
                var iSpread = false;
                /*点击事件*/
                $("#form_calendar_event").on("click", "#btn", function (e) {
                    btn.disabled = 'disabled'
                    if (!iSpread) {
                        hideBlock.attr("style", "");
                        this.innerHTML = '收起';
                        $(this).append("<span class='iconfont icon-ptkj-xianmiaoshuangjiantou-shang'></span>");
                    } else {
                        this.innerHTML = '更多信息';
                        hideBlock.css("height", "0px");
                        hideBlock.css("overflow", "hidden");
                        $(this).append("<span class='iconfont icon-ptkj-xianmiaoshuangjiantou-xia'></span>");
                    }
                    iSpread = !iSpread
                });

                // 只有新增的时候 才 初始提醒方式
                if (options.event.noticeTypes) {
                    if (typeof options.event.noticeTypes == "string") {
                        options.event.noticeTypes = options.event.noticeTypes ? options.event.noticeTypes.split(";") : [];
                    } else {
                        options.event.noticeTypes = options.event.noticeTypes ? options.event.noticeTypes : [];
                    }
                    $.each(options.event.noticeTypes, function (i, dom) {
                        $("label[for='itemform_noticeTypes']").parents(".row").find("input[value='" + dom + "']").attr("checked", "checked");
                    });
                }
                if (options.event.noticeObjs) {
                    if (typeof options.event.noticeObjs == "string") {
                        options.event.noticeObjs = options.event.noticeObjs ? options.event.noticeObjs.split(";") : [];
                    } else {
                        options.event.noticeObjs = options.event.noticeObjs ? options.event.noticeObjs : [];
                    }
                    $.each(options.event.noticeObjs, function (i, dom) {
                        $("label[for='itemform_noticeObjs']").parents(".row").find("input[value='" + dom + "']").attr("checked", "checked");
                    });
                }

            }
            $("#itemform_title").keyup(function () {
                var title = $("#itemform_title").val();
                if (typeof title == "string") {
                    if (title.length > 100) {
                        $("#itemform_title").val(title.substr(0, 100));
                    }
                }
            });
            $("#itemform_address").attr("autocomplete", "on");
            $("#form_calendar_event").attr("autocomplete", "on");
            $("#itemform_belongObjId").off('change').on('change', function (value) {

                var belongObjId = $("#itemform_belongObjId").val();

                if (!_.isEmpty(belongObjId)) {
                    $("div[for='itemform_belongObjId']").hide();
                }
            });
            //$(".control-label-itemform_title").append('<font color="red" size="2">*</font>');

            //检查部分用户是否显示
            var $partUserRow = $dialog.find("#itemform_partUsers").parents(".formbuilder");
            //公开范围是部分用户
            if (options.event.publicRange == "3") {
                $partUserRow.show();
            } else {
                $partUserRow.hide();
            }
            //给开始时间和结束时间绑定触发事件
            var isFirstValid = false;
            $("#itemform_repeatPeriodEndTime,#itemform_repeatPeriodStartTime").on("blur", function () {
                if (!isFirstValid) {
                    isFirstValid = true;
                    setInterval(function () {
                        if ($("#form_calendar_event").validate() != undefined) {
                            $("#form_calendar_event").validate().element($("#itemform_repeatPeriodEndTime"));
                        }
                    }, 500);
                }
            });

        },

        _initSelectAndCheckboxEvent: function () {

            //定义重复checkbox的事件
            $("#itemform_isRepeat").change(function () {
                var checked = $(this).attr("checked");
                if (checked) {
                    $("#js-row-repeat-conf").show();
                } else {
                    $("#js-row-repeat-conf").hide();
                }
            });
            //定义提醒checkbox的事件
            $("#itemform_isRemind").change(function () {
                var checked = $(this).attr("checked");
                if (checked) {
                    $("#js-row-remind-conf").show();
                } else {
                    $("#js-row-remind-conf").hide();
                }
            });
            //定义全天的checkbox的事件
            $("#itemform_isAll").change(function () {
                var checked = $(this).attr("checked");
                if (checked) {
                    var start = $("#itemform_repeatPeriodStartTime").val();
                    var end = $("#itemform_repeatPeriodEndTime").val();
                    $("#itemform_repeatPeriodStartTime").val(moment(start).format("YYYY-MM-DD 00:00"));
                    $("#itemform_repeatPeriodEndTime").val(moment(end).format("YYYY-MM-DD 23:59"));
                }
            });

            //定义重复类型的事件
            $("#itemform_repeatType").change(function () {
                $('#js-repeat-day-options').hide();
                $('#js-repeat-week-options').hide();
                $('#js-repeat-month-options').hide();
                $('#js-repeat-year-options').hide();
                var v = $(this).val();
                if ("day" == v) {
                    $('#js-repeat-day-options').show();
                } else if ("week" == v) {
                    $('#js-repeat-week-options').show();
                } else if ("month" == v) {
                    $('#js-repeat-month-options').show();
                } else if ("year" == v) {
                    $('#js-repeat-year-options').show();
                }
            }).val("day").trigger("change");

            //周重复的全部选择
            $("#itemform_repeatWeek_all").change(function () {
                var checked = $(this).attr("checked");
                if (checked) {
                    $("#js-repeat-week-options :checkbox:gt(0)").attr("checked", "checked");
                } else {
                    $("#js-repeat-week-options :checkbox:gt(0)").removeAttr("checked");
                }
            });


            $("#itemform_publicRange").change(function () {
                if ("3" == $(this).val()) {
                    //加* 后续会隐藏无需取消
                    $("label[for='itemform_partUsers']").empty().append("部分用户<span style='color:red'>*</span>")
                    //$("#itemform_partUsers").parents(".js-item-row").show();
                    $("#itemform_partUsers").parents(".formbuilder").show();
                } else {
                    //$("#itemform_partUsers").parents(".js-item-row").hide();
                    $("#itemform_partUsers").parents(".formbuilder").hide();
                }
            });


        },

        createRowHtml: function ($dialog) {

            ///var $row = $dialog.find("#div_calendar_item_form div.js-item-row").first();
            var $row = $dialog.find("#div_calendar_item_form div.js-item-row div").first();//6.1和6.0结构都不一样 再取一级div
            var $clone = $row.clone();
            $clone.find("label").removeClass('control-label-itemform_title');
            $clone.find("div.controls").empty();
            return $clone;
        },

        // 创建全天，重复，提醒3个checkbox
        create3checkRow: function ($dialog) {

            var _self = this;
            var $row = this.createRowHtml($dialog);
            $row.attr("id", "js-row-3-checkbox");
            $row.find("label").html("");
            var $div = $row.find("div.controls");
            var options = {
                "isAll": "全天",
                "isRepeat": "重复",
                "isRemind": "提醒",
            }
            $.each(options, function (key, value) {
                var id = "itemform_" + key;
                var checkbox = "<label class='checkbox-inline label-formbuilder'>" +
                    "<input class='w-itemform-option' style='display: inline-block;' value='1' height='34px' id=" + id + " name=" + id + " type='checkbox'/>"
                    + "" + value + "</label>";
                $div.append(checkbox);
            });

            return $row;
        },

        // 创建重复的配置行
        createRepeatConfRow: function ($dialog) {

            var _self = this;
            var $row = this.createRowHtml($dialog);
            $row.attr("id", "js-row-repeat-conf");
            $row.find("label").html("重复时间");
            var $div = $row.find("div.controls");
            var $groupDiv = $("<div>", {
                'class': "form-contorl clearfix"
            });
            $div.append($groupDiv);
            // 重复类型
            var $repeatTypeSelect = $("<select>", {
                'class': 'col-xs-3 w-itemform-option',
                'name': 'itemform_repeatType',
                'id': 'itemform_repeatType',
                'height': '34px',
            });
            _self._addSelectOptions(_self.repeatTypeOptions, $repeatTypeSelect, false);

            // 每天重复配置项
            $repeatDay = _self.createRepeatDayConf($dialog);

            // 每周重复配置项
            $repeatWeek = _self.createRepeatWeekConf($dialog);

            // 每月重复配置项
            $repeatMonth = _self.createRepeatMonthConf($dialog);

            // 每年重复配置项
            $repeatYear = _self.createRepeatYearConf($dialog);

            $groupDiv.append($repeatTypeSelect);
            $groupDiv.append($repeatDay);
            $groupDiv.append($repeatWeek);
            $groupDiv.append($repeatMonth);
            $groupDiv.append($repeatYear);

            return $row;
        },

        //每天重复对应的配置项
        createRepeatDayConf: function ($dialog) {

            var _self = this;
            var $div = $("<div>", {
                'class': 'col-xs-9',
                'id': 'js-repeat-day-options'
            });

            _self._addCheckboxs(_self.repeatDayOptions, $div, "repeatDay");
            return $div;
        },

        _addCheckboxs: function (options, $div, idPrev) {

            $.each(options, function (key, value) {
                var id = "itemform_" + idPrev + "_" + key;
                var name = "itemform_" + idPrev;
                var label = "<label class='checkbox-inline label-formbuilder'>"
                    + "<input class='w-itemform-option' style='display: inline-block;margin-right:2px' height='34px' value=" + key + " id=" + id + " name=" + name + " type='checkbox'>" + value
                    + "</label>";

                $div.append(label);
            });
        },


        //每天重复对应的配置项
        createRepeatWeekConf: function ($dialog) {

            var _self = this;
            var $div = $("<div>", {
                'class': 'col-xs-9 ',
                'id': 'js-repeat-week-options'
            });
            _self._addCheckboxs(_self.repeatWeekOptions, $div, "repeatWeek");
            return $div;
        },

        //每天重复对应的配置项
        createRepeatMonthConf: function ($dialog) {

            var _self = this;
            var $div = $("<select>", {
                'class': 'col-xs-9  w-itemform-option',
                'id': 'js-repeat-month-options',
                'name': 'itemform_repeatMonth',
                'height': '39px',
            });
            for (var i = 1; i <= 31; i++) {
                var option = "<option value='" + i + "'>" + i + "日</option>";
                $div.append(option);
            }
            return $div;
        },

        //每年重复对应的配置项
        createRepeatYearConf: function ($dialog) {

            var _self = this;
            var $div = $("<div>", {
                'class': 'col-xs-9 input-group date',
                'id': 'js-repeat-year-options',
                'name': "itemform_repeatYear",
            });
            $div.append(
                "<input type='text' class='form-control w-itemform-option' height='34px' name='itemform_repeatYear'>"
            ).append(
                "<span class='input-group-addon'><i class='glyphicon glyphicon-calendar'></i></span>"
            );
            $div.find("input[name='itemform_repeatYear']").datetimepicker({
                showClose: true,
                showClear: true,
                showTodayButton: true,
                format: "MM-DD",// 日期格式
                locale: 'zh-cn'// 本地化
            });
            return $div;
        },


        // 创建提醒的配置
        createRemindConfRow: function ($dialog) {

            var _self = this;
            var $row = this.createRowHtml($dialog);
            $row.attr("id", "js-row-remind-conf");
            $row.find("label").html("提前通知");
            var $div = $row.find("div.controls");
            var $groupDiv = $("<div>", {
                'class': "form-contorl"
            });
            $div.append($groupDiv);

            // 间隔时间
            var $intervalSelect = $("<select>", {
                'class': 'col-xs-4 w-itemform-option',
                'name': 'itemform_interval',
                'id': 'itemform_interval',
                'height': '34px',
            });
            _self._addSelectOptions(_self.intervalOptions, $intervalSelect, true);

            // 间隔时间单位
            var $unitSelect = $("<select>", {
                'class': 'col-xs-4 w-itemform-option ',
                'name': 'itemform_intervalUnit',
                'id': 'itemform_intervalUnit',
                'height': '34px',
            });

            _self._addSelectOptions(_self.unitOptions, $unitSelect, false);

            $groupDiv.append($intervalSelect).append($unitSelect);
            return $row;
        },

        _addSelectOptions: function (options, $select, isArray) {

            $.each(options, function (key, value) {
                key = isArray ? value : key;
                var option = "<option value='" + key + "'>" + value + "</option>";
                $select.append(option);
            });
        },

        //收集事项表单数据
        afterCollectEventData: function (options, configuration, newFormData) {
            //格式化周期开始和结束时间
            if (newFormData.repeatPeriodStartTime) {
                //收集的时候把秒补全
                newFormData['repeatPeriodStartTime'] = commons.DateUtils.format(new Date(
                    newFormData['repeatPeriodStartTime']), "yyyy-MM-dd HH:mm:ss");
            }
            if (newFormData.repeatPeriodEndTime) {
                newFormData['repeatPeriodEndTime'] = commons.DateUtils.format(new Date(
                    newFormData['repeatPeriodEndTime']), "yyyy-MM-dd HH:mm:ss");
            }
            //事项开始,和结束时间是必须的，所以用 options中的值临时代替下，在服务端要根据重复的设置情况重新计算
            newFormData.startTime = options.event.startTime;
            newFormData.endTime = options.event.endTime;
            newFormData.isAll = newFormData.isAll ? "1" : "0";
            newFormData.isRepeat = newFormData.isRepeat ? "1" : "0";
            newFormData.isRemind = newFormData.isRemind ? "1" : "0";
            newFormData.isFinish = options.event.isFinish ? options.event.isFinish : "0";
            newFormData.noticeTypes = newFormData.noticeTypes ? newFormData.noticeTypes.join(";") : "";
            newFormData.noticeObjs = newFormData.noticeObjs ? newFormData.noticeObjs.join(";") : "";
            newFormData.repeatMarkId = options.event.repeatMarkId;

            //计算重复的配置项
            if ("1" == newFormData.isRepeat) {
                var repeatConf = {
                    repeatType: newFormData.repeatType,
                }
                //按天重复
                if ("day" == newFormData.repeatType) {
                    if (newFormData.repeatDay == null || newFormData.repeatDay.length == 0) {
                        newFormData.repeatDay = [];
                    }
                    repeatConf.repeatValue = newFormData.repeatDay.join(";");
                } else if ("week" == newFormData.repeatType) {
                    if (newFormData.repeatWeek == null || newFormData.repeatWeek.length == 0) {
                        newFormData.repeatWeek = [];
                    }
                    repeatConf.repeatValue = newFormData.repeatWeek.join(";");
                } else if ("month" == newFormData.repeatType) {
                    repeatConf.repeatValue = newFormData.repeatMonth;
                } else if ("year" == newFormData.repeatType) {
                    repeatConf.repeatValue = newFormData.repeatYear;
                }
                newFormData.repeatConfVo = JSON.stringify(repeatConf);
            }

            //计算提醒的配置项
            if ("1" == newFormData.isRemind) {
                var remindConf = {
                    interval: newFormData.interval,
                    intervalUnit: newFormData.intervalUnit
                }
                newFormData.remindConfVo = JSON.stringify(remindConf);
            }
        },

        //重构删除按钮事件
        deleteEventClick: function (event, configuration, options) {

            var _self = this;
            appModal.dialog({
                title: '确认',
                message: "确认删除?",
                size: "middle",
                buttons: {
                    deleteAll: {
                        label: "删除全部",
                        className: "btn-danger",
                        callback: function (result) {
                            server.JDS.call({
                                service: "calendarFacade.deleteEventsByRepeatMarkId",
                                data: [event.repeatMarkId],
                                success: function (result) {
                                    if (result && result.success) {
                                        _self.getWidget().refresh();
                                    }
                                },
                            });
                        }
                    },
                    deleteOne: {
                        label: "删除",
                        className: "btn-primary",
                        callback: function (result) {
                            _self.getWidget()._deleteEvent(event.uuid, function () {
                                _self.getWidget().refresh();
                            });
                        }
                    },
                    cancel: {
                        label: "取消",
                        className: "btn-default",
                    }

                },
            });
        },

        //监听左导航的点击事件
        onLeftSidebarItemClick: function (menuItemEvent, options, configuration) {
            var menuItem = menuItemEvent.detail.selectedItem.menuItem;
            //var menuValue = menuItemEvent.detail.selectedItem.value;
            if (menuItem) {
                var type = menuItem.type;
                //我关注的标识
                if ("我的关注" == menuItem.name) {
                    this.getWidget().ajaxParams['from'] = 'attention';
                    //type=type==undefined?"user":type;//不知为何user没有默认进去
                } else {
                    this.getWidget().ajaxParams['from'] = "";
                }
                if (!type) {
                    return;
                }
                // 清空其他的搜索添加
                this.clearOtherConditions();
                this.getWidget().ajaxParams = {};
                if ("myAll" == type) {
                    //我的日程-全部,
                    var filterCriterion = {
                        columnIndex: "calendarCreator",
                        value: currUser.userId,
                        type: "eq"
                    };
                    this.addOtherConditions([filterCriterion]);

                } else if ("otherAll" == type) {
                    //他人日程全部
                    var otherCalendarIds = menuItem.calendarUuids.split(";");
                    var filterCriterion = {
                        columnIndex: "belongObjId",
                        value: otherCalendarIds,
                        type: "in"
                    };
                    this.addOtherConditions([filterCriterion]);
                } else if ("user" == type) {
                    //我关注的个人
                    var userId = menuItem.user.id;
                    var filterCriterion = {
                        columnIndex: "calendarCreator",
                        value: userId,
                        type: "eq"
                    };
                    this.addOtherConditions([filterCriterion]);
                    this.getWidget().ajaxParams['from'] = 'attention';
                } else if ("group" == type) {
                    //我关注的群组
                    var userIds = menuItem.group.groupMembers.split(";");
                    var filterCriterion = {
                        columnIndex: "calendarCreator",
                        value: userIds,
                        type: "in"
                    };
                    this.addOtherConditions([filterCriterion]);
                    this.getWidget().ajaxParams['from'] = 'attention';
                } else if ("myCalendar" == type || "otherCalendar" == type) {
                    //我的日程和他人日程中的具体日历本
                    var calendarId = menuItem.calendar.uuid;
                    var filterCriterion = {
                        columnIndex: "belongObjId",
                        value: calendarId,
                        type: "eq"
                    };
                    this.addOtherConditions([filterCriterion]);
                }
                //刷新页面

                this.getWidget().refresh();
            }

        },

        createViewRowHtml: function ($dialog) {

            //var $row = $dialog.find("#div_calendar_view_form div.js-item-row").first();
            var $row = $dialog.find("#div_calendar_view_form div.js-item-row div").first();//6.1和6.0结构都不一样 再取一级div
            var $clone = $row.clone();
            $clone.find("label").removeClass('control-label-viewform_title');
            $clone.find("div.controls").empty();
            return $clone;
        },

        //查看事项完成状态
        createViewFinishStatusRow: function ($dialog, options) {

            var _self = this;
            var $row = this.createViewRowHtml($dialog);
            $row.find("label").html("当前状态");
            var $div = $row.find("div.controls");
            var $span = $("<span>", {
                'class': "w-viewform-option",
                'height': '34px',
            }).html(options.event.isFinish == 0 ? "未完成" : "已完成");
            $div.append($span);
            return $row;
        },

        //查看事项的创建者
        createViewEventCreatorRow: function ($dialog, options) {

            var user = server.SpringSecurityUtils.getUserDetails(options.event.creator);
            var _self = this;
            var $row = this.createViewRowHtml($dialog);
            $row.find("label").html("日程创建者");
            var $div = $row.find("div.controls");
            var $span = $("<span>", {
                'class': "w-viewform-option",
                'height': '34px',
            }).html(user.userName);
            $div.append($span);
            return $row;
        },
        createViewEventOwnerRow: function ($dialog, options) {

            var user = server.SpringSecurityUtils.getUserDetails(options.event.creator);
            var _self = this;
            var $row = this.createViewRowHtml($dialog);
            $row.find("label").html("日程所属");
            var $div = $row.find("div.controls");
            var $span = $("<span>", {
                'class': "w-viewform-option",
                'height': '34px',
            }).html(options.event.calendarCreatorName);
            $div.append($span);
            return $row;
        },

        //查看事项的创建者
        createViewRemindRow: function ($dialog, options) {

            var _self = this;
            var $row = this.createViewRowHtml($dialog);
            $row.find("label").html("提前通知");
            var $div = $row.find("div.controls");
            var $span = $("<span>", {
                'class': "w-viewform-option",
                'height': '34px',
            });
            var conf = JSON.parse(options.event.remindConf);
            $span.html(conf.interval + " " + _self.unitOptions[conf.intervalUnit]);
            $div.append($span);
            return $row;
        },

        //查看事项的创建者
        createViewRepeatRow: function ($dialog, options) {

            var _self = this;
            var $row = this.createViewRowHtml($dialog);
            $row.find("label").html("重复");
            var $div = $row.find("div.controls");
            var $span = $("<span>", {
                'class': "w-viewform-option",
                'height': '34px',
            });
            var conf = JSON.parse(options.event.repeatConf);
            var html = _self.repeatTypeOptions[conf.repeatType];
            var texts = [];
            if ("day" == conf.repeatType) { //按天重复
                var values = conf.repeatValue ? conf.repeatValue.split(";") : [];
                $.each(values, function (i) {
                    texts.push(_self.repeatDayOptions[values[i]]);
                });
            } else if ("week" == conf.repeatType) { //按周重复
                var values = conf.repeatValue ? conf.repeatValue.split(";") : [];
                $.each(values, function (i) {
                    texts.push(_self.repeatWeekOptions[values[i]]);
                });
            } else if ("month" == conf.repeatType) { //按月重复
                texts.push([conf.repeatValue + "日"]);
            } else if ("year" == conf.repeatType) { //按年重复
                texts.push([moment(conf.repeatValue, "MM-DD").format("MM月DD日")]);
            }

            $span.html(html + ":" + texts.join(";"));
            $div.append($span);
            return $row;
        },

        //查看事项弹窗渲染完后事件

        afterViewEventDialogRender: function ($dialog, options, configuration) {

            var _self = this;
            //插入提醒配置
            if (options.event.isRemind == 1) {
                var $remindRow = this.createViewRemindRow($dialog, options);
                //$dialog.find("#viewform_repeatPeriodEndTime").parents(".js-item-row").after($remindRow);
                $dialog.find("#viewform_repeatPeriodEndTime").parents(".form-group").after($remindRow);

            }

            //插入重复配置
            if (options.event.isRepeat == 1) {
                var $repeatRow = this.createViewRepeatRow($dialog, options);
                //$dialog.find("#viewform_repeatPeriodEndTime").parents(".js-item-row").after($repeatRow);
                $dialog.find("#viewform_repeatPeriodEndTime").parents(".form-group").after($remindRow);
            }


            //插入日程所属
            var $eventCreatorRow = this.createViewEventOwnerRow($dialog, options);
            //$dialog.find(".js-item-row").last().after($eventCreatorRow);//6.1布局问题
            $dialog.find(".js-item-row>.form-group").last().after($eventCreatorRow);

            //插入日程创建者
            var $eventCreatorRow = this.createViewEventCreatorRow($dialog, options);
            //$dialog.find(".js-item-row").last().after($eventCreatorRow);
            $dialog.find(".js-item-row>.form-group").last().after($eventCreatorRow);

            //插入日程状态
            var $finishRow = this.createViewFinishStatusRow($dialog, options);
            //$dialog.find(".js-item-row").last().after($finishRow);
            $dialog.find(".js-item-row>.form-group").last().after($finishRow);


            //检查部分用户是否显示
            //var $partUserRow = $dialog.find("#viewform_partUsers").parents('.js-item-row');

            var $partUserRow = $dialog.find("#viewform_partUsers").parents(".formbuilder");
            //公开范围是部分用户
            if (options.event.publicRange == "3") {
                $partUserRow.show();
            } else {
                $partUserRow.hide();
            }
            //格式化周期开始和结束时间
            if (options.event.repeatPeriodStartTime) {
                $dialog.find("#viewform_" + "repeatPeriodStartTime").html(options.event.repeatPeriodStartTime);
            }
            if (options.event.repeatPeriodEndTime) {
                $dialog.find("#viewform_" + "repeatPeriodEndTime").html(options.event.repeatPeriodEndTime);
            }

            //检查操作权限，自己的日历本不需要检查
            if (options.event.calendarCreator == currUser.userId) {
                //插入完成按钮
                _self.addFinishBtn($dialog, options);
            } else {
                //检查删除，编辑，变更状态权限，
                server.JDS.call({
                    service: "calendarFacade.queryCalendarPrivilege",
                    data: [options.event.belongObjId],
                    success: function (result) {
                        //有编辑权限，出现按钮
                        if (result.data.isEdit) {
                            $dialog.find("button.js-btn-edit").show();
                        } else {
                            $dialog.find("button.js-btn-edit").hide();
                        }
                        ;
                        if (result.data.isDel) {
                            $dialog.find("button.js-btn-del").show();
                        } else {
                            $dialog.find("button.js-btn-del").hide();
                        }
                        ;
                        if (result.data.isStatus) {
                            //插入完成按钮
                            _self.addFinishBtn($dialog, options);
//
//							$dialog.find("button.js-btn-status").show();
                        } else {
//							$dialog.find("button.js-btn-status").hide();
                        }
                    }
                })
            }
            ;
        },

        addFinishBtn: function ($dialog, options) {

            //插入完成按钮
            var _self = this;
            var $finishBtn = $("<button class='btn btn-default js-btn-status' type='button'></button>");
            //未完成状态，则需要出现完成按钮
            $finishBtn.html(options.event.isFinish == 1 ? "未完成" : "完成");
            $dialog.find(".modal-footer").find("button").first().before($finishBtn);
            $finishBtn.click(function () {
                var isFinish = options.event.isFinish == 1 ? 0 : 1;
                server.JDS.call({
                    service: "calendarFacade.updateEventStatus",
                    data: [options.event.uuid, isFinish],
                    success: function (result) {
                        if (result) {
                            options.event.isFinish = isFinish;
                            options.event.backgroundColor = isFinish ? "" : "green";
                            _self.getWidget()._updateEvent(options.event);
                        }
                    },
                });
            });
        },

        //获取分组的数据，用来映射资源视图对应的分组字段的ID
        getGroupDataMap: function (options, configuration) {
            var _self = this;
            server.JDS.call({
                service: "calendarFacade.getGroupDataMap",
                async: false,
                success: function (result) {
                    if (result) {
                        _self.getWidget().groupDataMap = result.data;
                    }
                },
            });
        },
        getAfterCollectEventDataCB: function (options, configuration, newFormData) {
            var $comfirmDialog = "#defaultComfirmDialog";
            var _self = this;
            var readySave = true;
            if (!_self.widget.validateEventData(newFormData)) {
                return false;
            }
            if (newFormData.repeatPeriodStartTime && newFormData.repeatPeriodEndTime) {
                //判断开始时间和结束时间 和uuid 与 自己的其它未完成的日程有没有冲突
                //时间段冲突 未结束且开始时间和结束时间重叠
                server.JDS.call({
                    service: "calendarFacade.judgeConflictZone",
                    data: [newFormData.repeatPeriodStartTime, newFormData.repeatPeriodEndTime, options.event.uuid],
                    async: false,
                    success: function (result) {
                        if (result && result.success) {
                            if (result.data && !options.event.readySave) {//冲突
                                var alertDefaults = {
                                    tipsCode: "666",
                                    title: "提示框",// 标题
                                    message: result.data, // 消息内容，不能为空
                                    container: "#defaultComfirmDialog", // 容器
                                    show: function () {
                                        readySave = false;
                                    },
                                    callback: function (callue) {
                                        if (callue) {
                                            readySave = true;
                                            //appModal.info("操作成功！",function(){
                                            //要把弹出框给关掉
                                            //appModal.hide("#defaultComfirmDialog");
                                            appModal.hide();
                                            //给options一些表示
                                            options.event.readySave = true;
                                            //再触发一下保存
                                            _self.widget._formOkClick(options);
                                            return true;
                                            //});
                                        }

                                    }
                                };
                                var $comfirmDialog = appModal.confirm(alertDefaults);
                            } else {
                                readySave = true;
                            }
                        }
                    },
                    error: function (result) {
                        appModal.error(result);
                    }
                });
            }
            return readySave;
        }
    });
    return MyCalendarDevelopment;
});