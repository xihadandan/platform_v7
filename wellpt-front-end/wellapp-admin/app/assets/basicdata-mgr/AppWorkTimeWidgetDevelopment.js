define([ "constant", "commons", "server", "appContext","appModal","layDate", "HtmlWidgetDevelopment" ],
    function(constant, commons, server,appContext,appModal,layDate, HtmlWidgetDevelopment) {
        var JDS = server.JDS;

        var AppWorkTimeWidgetDevelopment = function() {
            HtmlWidgetDevelopment.apply(this, arguments);
        };
        // 接口方法
        commons.inherit(AppWorkTimeWidgetDevelopment, HtmlWidgetDevelopment, {
            init: function() {
                var self = this;
                var bean = {
                    "uuid" : null,
                    "type" : null,
                    "name" : null,
                    "code" : null,
                    "isWorkday" : null,
                    "fromDate" : null,
                    "toDate" : null,
                    "fromTime1" : null,
                    "toTime1" : null,
                    "fromTime2" : null,
                    "toTime2" : null,
                    "remark" : null,
                    "changedFixedHolidays" : [],
                    "changedSpecialHolidays" : [],
                    "changedMakeups" : [],
                    "deletedFixedHolidays" : [],
                    "deletedSpecialHolidays" : [],
                    "deletedMakeups" : []
                };

                getWorkDate();

                $(".work_time_list").each(function(){
                    var id = $(this).attr("id");
                    var title = "";
                    var url = "";
                    if(id=="fixed_holidays_list"){
                        title="(月-日)";
                        url = "/basicdata/workhour/fixedHolidaysList.action";
                    }else if(id == "special_holidays_list"){
                        url = "/basicdata/workhour/specialHolidaysList.action";
                    }else{
                        url = "/basicdata/workhour/makeUpList.action";
                    }
                    getData(id,title,url)

                })

                $(".work_time_hour").each(function(){
                    var id = $(this).attr("id")
                    layDate.render({
                        elem: '#'+id,
                        type: 'time',
                        trigger:"click",
                        format:"HH:mm",
                        ready: formatminutes
                    });
                })

                $(".btn-add-rows").click(function(){
                    var id = getId($(this))
                    var datas = {
                        uuid:null,
                        name:null,
                        fromDate:null,
                        toDate:null,
                        remark:null
                    }
                    addTableRow($("#"+id),datas)
                })

                $(".btn-del-rows").click(function(){
                    var id = getId($(this))
                    var delField = "";
                    if(id == "fixed_holidays_list"){
                        delField = "deletedFixedHolidays"
                    }else if(id == "special_holidays_list"){
                        delField = "deletedSpecialHolidays"
                    }else{
                        delField = "deletedMakeups"
                    }
                    delTableRow($("#"+id),"name",delField)
                })

                $("#work_time_btn_save").click(function() {
                    $("#work_hour_form").form2json(bean);
                    if (!collectFixedAndSpecialAndMakeup(bean)) {
                        return;
                    }

                    JDS.call({
                        service : "workHourService.saveBean",
                        data : [ bean ],
                        version:"",
                        success : function(result) {
                            appModal.success("保存成功！");
                        }
                    });

                    // 工作日
                    var fromTime1 = $("#fromTime1").val();
                    var toTime1 = $("#toTime1").val();
                    var fromTime2 = $("#fromTime2").val();
                    var toTime2 = $("#toTime2").val();
                    var notCheckedArray = $(".workday",self.widget.element).find("input:checkbox").not("input:checked");// 获取未选中的checkbox
                    var notCheckedValArray = new Array(); // 存放未选中的checkbox的value值
                    var checkedArray = $(".workday",self.widget.element).find("input:checked");// 获取选中的checkbox
                    var checkedValArray = new Array(); // 存放选中的checkbox的value值

                    for (var i = 0; i < checkedArray.length; i++) {
                        checkedValArray[i] = checkedArray[i].value;
                    }
                    for (var i = 0; i < notCheckedArray.length; i++) {
                        notCheckedValArray[i] = notCheckedArray[i].value;
                    }
                    jQuery.ajax({
                        type : "GET",
                        url : ctx + '/basicdata/workhour/save.action',
                        data : "fromTime1=" + fromTime1 + "&toTime1=" + toTime1 + "&fromTime2=" + fromTime2 + "&toTime2=" + toTime2 + "&checkedValArray=" + checkedValArray + "&notCheckedValArray=" + notCheckedValArray,
                        dataType : "text",
                        success : function(msg) {

                        },
                        error : function(result) {

                        }
                    });
                });

                function  formatminutes() {
                    var showtime = $($(".laydate-time-list li ol")[1]).find("li");
                    for (var i = 0; i < showtime.length; i++) {
                        var t00 = showtime[i].innerText;
                        if (t00 != "00" && t00 != "20" && t00 != "30" && t00 != "40" && t00 != "50") {
                            showtime[i].remove()
                        }
                    }
                    $($(".laydate-time-list li ol")[2]).find("li").remove();
                }

                function getWorkDate(){
                    JDS.call({
                        service:'workHourService.getWorkDayList',
                        data:[server.SpringSecurityUtils.getCurrentUserUnitId()],
                        version:"",
                        success : function(result) {
                            var data = result.data;
                            for(var i = 0; i < data.length; i++ ){
                                var code = data[i].code

                                if(code == $("#workday_name_"+code).val() && data[i].isWorkday){
                                    $("input[value='"+code+"']").attr("checked",true);
                                }

                                if(i == 0){
                                    $("#fromTime1").val(data[i].fromTime1)
                                    $("#toTime1").val(data[i].toTime1)
                                    $("#fromTime2").val(data[i].fromTime2)
                                    $("#toTime2").val(data[i].toTime2)
                                }
                            }
                        }
                    });
                }

                function getData(id,title,url){
                    var data = []
                    var time = (new Date()).getTime()
                    $.ajax({
                        url: ctx + url,
                        dataType: "json",
                        method: "POST",
                        data:"_search=false&nd="+time+"&rows=20&page=1&sidx=name&sord=asc",
                        success:function (res) {
                            data = res.dataList
                            initTable($("#"+id),title,data)
                        }
                    })
                }

                function getId(ele) {
                    var id = ele.attr("id")
                    var newId = id.substring(0,id.length-7) + "list";
                    return newId;
                }

                function initTable(ele,title,data){
                    ele.bootstrapTable({
                        data:data,
                        idField: "uuid",
                        striped: false,
                        showColumns: false,
                        uniqueId: 'uuid',
                        width: 500,
                        sortable:true,
                        sortName:"name",
                        sortOrder:"asc",
                        pageNumber:"20",
                        undefinedText:'',
                        columns: [
                            {
                                checkbox:true
                            },{
                                field: "uuid",
                                title: "uuid",
                                visible: false
                            },{
                                field: "name",
                                title: "名称",
                                editable: {
                                    type: "text",
                                    showbuttons: false,
                                    onblur: "submit",
                                    mode: "inline"
                                }
                            },{
                                field: "fromDate",
                                title: "开始日期"+ title,
                                editable: {
                                    type: "text",
                                    showbuttons: false,
                                    onblur: "ignore",
                                    mode: "inline",
                                    title:"开始日期"
                                }
                            },{
                                field: "toDate",
                                title: "结束日期"+title,
                                editable: {
                                    type: "text",
                                    showbuttons: false,
                                    onblur: "ignore",
                                    mode: "inline",
                                    title:"结束日期"
                                }
                            },{
                                field: "remark",
                                title: "备注",
                                editable: {
                                    type: "text",
                                    showbuttons: false,
                                    onblur: "submit",
                                    mode: "inline"
                                }
                            }
                        ],
                        onClickCell:function(field,value,row,$element){
                            if(field == "toDate" || field == "fromDate"){
                                var format = ele.attr("id") == "fixed_holidays_list" ? "MM-dd" : "yyyy-MM-dd"
                                layDate.render({
                                    elem: $('.input-sm',$element)[0],
                                    trigger:"click",
                                    format:format,
                                    done: function(value){
                                        var data = ele.bootstrapTable("getData");
                                        $.each(data, function (index, rowData) {
                                            if (row == rowData) {
                                                rowData[field] = value;
                                                ele.bootstrapTable("updateRow", index, rowData);
                                            }
                                        });
                                    }
                                });
                            }
                        }
                    })
                }

                function addTableRow(ele,data){       // 插入行数据
                    ele.bootstrapTable("insertRow", {index: 0,row:data});
                }

                function delTableRow(ele,fields,delField){        // 删除行数据
                    var rowids = ele.bootstrapTable("getSelections");
                    if (rowids.length == 0) {
                        appModal.error("请选择记录!");
                        return;
                    }
                    appModal.confirm("确定删除记录？",function(result){
                        if(result){
                            var fieldsVal = []
                            for(var i = 0; i < rowids.length; i++){
                                fieldsVal.push(rowids[i][fields])
                                if (rowids[i].uuid != null && $.trim(rowids[i].uuid) != "") {
                                    bean[delField].push(rowids[i]);
                                }
                            }
                            ele.bootstrapTable("remove",{
                                field: fields,
                                values: fieldsVal
                            })
                            if(ele.attr("id")=="ip_sms_verify_list"){
                                var datas = $("#ip_sms_verify_list").bootstrapTable("getData")
                                if(datas.length<=0){
                                    $("#sms_valid_period").hide();
                                }
                            }
                        }
                    })
                }

                function getDateValue(ele,field,text,bean) {
                    var changes1 = ele.bootstrapTable("getData");
                    for (var i = 0; i < changes1.length; i++) {
                        if((changes1[i].fromDate != "" && changes1[i].fromDate != null) && (changes1[i].toDate != "" && changes1[i].toDate != null)){
                            var form = changes1[i].fromDate.replace("-", "");
                            var to = changes1[i].toDate.replace("-", "");
                            if (form > to) {
                                appModal.success(text+"【" + changes1[i].name + "】开始日期不能大于结束日期！");
                                return false;
                            }
                        }
                    }
                    bean[field] = changes1;
                }

                function collectFixedAndSpecialAndMakeup(bean) {
                    getDateValue($("#fixed_holidays_list"),"changedFixedHolidays","固定节假日",bean)
                    getDateValue($("#special_holidays_list"),"changedSpecialHolidays","特殊节假日",bean)
                    getDateValue($("#make_up_list"),"changedMakeups","补班日期",bean)
                    return true;
                }
            }
        })
        return AppWorkTimeWidgetDevelopment;
    })

