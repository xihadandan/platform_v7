/*******************************************************************************
 * 如何描述该JS
 * 
 * @author zhulh
 * @date 2016-07-04
 */
$(function() {
    // 表单选择器
    var form_selector = "#wf_task_delegation_form";
    // WfTaskDelegation的VO类
    var bean = {
        uuid: null, // UUID
        recVer: null, // 版本号
        flowInstUuid: null, // FLOW_INST_UUID
        taskInstUuid: null, // TASK_INST_UUID
        dutyAgentUuid: null, // DUTY_AGENT_UUID
        consignorName: null, // CONSIGNOR_NAME
        consignor: null, // CONSIGNOR
        trusteeName: null, // TRUSTEE_NAME
        trustee: null, // TRUSTEE
        taskIdentityUuid: null, // TASK_IDENTITY_UUID
        dueToTakeBackWork: null, // DUE_TO_TAKE_BACK_WORK
        completionState: null, // COMPLETION_STATE
        fromTime: null, // FROM_TIME
        toTime: null
            // TO_TIME
    };
    $("#list").jqGrid(
        $.extend($.common.jqGrid.settings, {
            url: ctx + '/workflow/work/task/delegation/list',
            colNames: ["uuid", "dutyAgentUuid", "委托人", "consignor", "受托人", "trustee", "taskIdentityUuid", "委托流程",
                "委托环节", "到期回收", "状态", "开始时间", "结束时间"
            ],
            colModel: [{
                name: 'uuid',
                index: 'uuid',
                width: 180,
                hidden: true,
                key: true
            }, {
                name: 'dutyAgentUuid',
                index: 'dutyAgentUuid',
                hidden: true,
                width: 180
            }, {
                name: 'consignorName',
                index: 'consignorName',
                width: 80
            }, {
                name: 'consignor',
                index: 'consignor',
                hidden: true,
                width: 180
            }, {
                name: 'trusteeName',
                index: 'trusteeName',
                width: 80
            }, {
                name: 'trustee',
                index: 'trustee',
                hidden: true,
                width: 180
            }, {
                name: 'taskIdentityUuid',
                index: 'taskIdentityUuid',
                hidden: true,
                width: 180
            }, {
                name: 'flowTitle',
                index: 'flowTitle',
                width: 380
            }, {
                name: 'taskName',
                index: 'taskName',
                width: 180
            }, {
                name: 'dueToTakeBackWork',
                index: 'dueToTakeBackWork',
                width: 80,
                formatter: function(cellvalue, options, rowObject) {
                    if (cellvalue == "true") {
                        return '是';
                    }
                    return "否";
                }
            }, {
                name: 'completionState',
                index: 'completionState', // 完成状态 0运行中、1等待回收中、2已结束
                width: 80,
                formatter: function(cellvalue, options, rowObject) {
                    if (cellvalue == "0") {
                        return '委托中';
                    } else if (cellvalue == "1") {
                        return '等待回收中';
                    } else if (cellvalue == "2") {
                        return '已完成';
                    }
                    return "未知";
                }
            }, {
                name: 'fromTime',
                index: 'fromTime',
                width: 180
            }, {
                name: 'toTime',
                index: 'toTime',
                width: 180
            }], // 行选择事件
            sortname: "",
            onSelectRow: function(id) {
                var rowData = $(this).getRowData(id);
                getWfTaskDelegation(rowData.uuid);
            }
        }));

    // 根据UUID获取组织选择项
    function getWfTaskDelegation(uuid) {
        var wfTaskDelegation = {};
        wfTaskDelegation.uuid = uuid;
        JDS.call({
            service: "wfTaskDelegationMgr.getBean",
            data: wfTaskDelegation.uuid,
            success: function(result) {
                bean = result.data;
                $("#btn_del").show();
                $(form_selector).json2form(bean);
            }
        });
    }

    // 新增操作
    $("#btn_add").click(function() {
        $(form_selector).clearForm(true);
        $("#btn_del").hide();
    });

    // 保存用户信息
    $("#btn_save").click(function() {
        $(form_selector).form2json(bean);
        JDS.call({
            service: "wfTaskDelegationMgr.saveBean",
            data: bean,
            success: function(result) {
                alert("保存成功！");
                // 删除成功刷新列表
                $("#list").trigger("reloadGrid");
            }
        });
    });

    // 删除操作
    $("#btn_del").click(function() {
        if (bean.uuid == "" || bean.uuid == null) {
            alert("请选择记录！");
            return true;
        }
        var name = bean.name;
        if (confirm("确定要删除选择项[" + name + "]吗？")) {
            JDS.call({
                service: "wfTaskDelegationMgr.remove",
                data: bean.uuid,
                success: function(result) {
                    alert("删除成功!");
                    $(form_selector).clearForm(true);
                    // 删除成功刷新列表
                    $("#list").trigger("reloadGrid");
                }
            });
        }
    });
    // 批量删除操作
    $("#btn_del_all").click(function() {
        var rowids = $("#list").jqGrid('getGridParam', 'selarrrow');
        if (rowids.length == 0) {
            alert("请选择记录!");
            return true;
        }
        if (confirm("确定要删除所选记录吗?")) {
            JDS.call({
                service: "wfTaskDelegationMgr.removeAll",
                data: [rowids],
                success: function(result) {
                    alert("删除成功!");
                    $(form_selector).clearForm(true);
                    // 删除成功刷新列表
                    $("#list").trigger("reloadGrid");
                }
            });
        }
    });

    // 列表查询
    $("#query_wf_task_delegation").keypress(function(e) {
        if (e.keyCode == 13) {
            $("#btn_query").trigger("click");
        }
    });

    // JQuery UI按钮
    $("input[type=submit], a, button", $(".btn-group")).button();
    // JQuery UI页签
    $(".tabs").tabs();
    // $("li>a[href!='#tabs-0']", ".tabs").parent().hide();

    $("#btn_query").click(function(e) {
        var queryValue = $("#query_wf_task_delegation").val();
        var postData = {
            "queryPrefix": "query",
            "queryOr": true,
            "query_LIKES_consignorName_OR_trusteeName_OR_flowTitle_OR_taskName": queryValue
        };
        // if (queryValue == "是") {
        // postData["query_EQB_show"] = true;
        // } else if (queryValue == "否") {
        // postData["query_EQB_show"] = false;
        // }
        $("#list").jqGrid("setGridParam", {
            postData: null
        });
        $("#list").jqGrid("setGridParam", {
            postData: postData,
            page: 1
        }).trigger("reloadGrid");
    });

    // JQuery layout布局变化时，更新jqGrid高度与宽度
    function resizeJqGrid(position, pane, paneState) {
        if (grid = $('.ui-jqgrid-btable:visible')) {
            grid.each(function(index) {
                var paneWidth = pane.width();
                var paneHeight = pane.height() - 25;
                if (Browser.isIE()) { // 检测是否是IE浏览器
                    $(this).setGridWidth(paneWidth - 22);
                    $(this).setGridHeight(paneHeight - 84);
                } else if (Browser.isChrome()) { // 检测是否是chrome浏览器
                    $(this).setGridWidth(paneWidth - 30);
                    $(this).setGridHeight(paneHeight - 114);
                } else if (Browser.isMozila()) { // 检测是否是Firefox浏览器
                    $(this).setGridWidth(paneWidth - 22);
                    $(this).setGridHeight(paneHeight - 84);
                } else {
                    $(this).setGridWidth(paneWidth);
                    $(this).setGridHeight(pane.height());
                }
            });
        }
    }
    // JQuery layout布局
    $('#container').layout({
        center: {
            closable: false,
            resizable: false,
            slidable: false,
            onresize: resizeJqGrid,
            minSize: 500,
            triggerEventsOnLoad: true
        }
    });
});