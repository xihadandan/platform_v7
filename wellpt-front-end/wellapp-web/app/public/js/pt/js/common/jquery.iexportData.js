(function (factory) {
    "use strict";
    if (typeof define === 'function' && define.amd) {
        // AMD. Register as an anonymous module.
        define(['jquery', 'alerts'], factory);
    } else {
        // Browser globals
        factory(jQuery);
        // document.write("<script language=javascript src='" + ctx + "/resources/timers/jquery.timers.min.js'></script>");
        document.write("<script language=javascript src='" + ctx + "/resources/pt/js/common/jquery.alerts.js'></script>");
    }
}(function (jquery, alerts) {

    var treeObj

    function dblClickExpand(treeId, treeNode) {
        return treeNode.level > 0;
    }

    // zTree 的参数配置，深入使用请参考 API 文档（setting 配置详解）
    var treesetting = {
        edit: {
            enable: true,
            showRemoveBtn: false,
            showRenameBtn: false
        },
        data: {
            simpleData: {
                enable: true
            }
        },
        view: {
            dblClickExpand: dblClickExpand,
            fontCss: setFontCss
        },
        check: {
            enable: true,
            nocheckInherit: false,
            chkboxType: {
                "Y": "p",
                "N": "s"
            }
        },
        callback: {
            onCheck: function () {
                var treeObj = $.fn.zTree.getZTreeObj("exportZtree");
                if (treeObj != undefined && typeof treeObj != 'undefined') {
                    nodes = treeObj.getCheckedNodes(true);
                    $(".ui-dialog-buttonset button").each(function () {
                        if ($(this).text() == "导出") {
                            $(this).css("display", '');
                        }
                    });
                    for (var j = 0; j < nodes.length; j++) {
                        if (nodes[j].data.type == "errorData") {
                            $(".ui-dialog-buttonset button").each(function () {
                                if ($(this).text() == "导出") {
                                    $(this).css("display", 'none');
                                }
                            });
                        }
                    }
                }
            }
        }
    };

    function setFontCss(treeId, treeNode) {
        var data = treeNode.data;

        if (data != null) {
            if (data.type == "errorData") {
                $(".ui-dialog-buttonset button").each(function () {
                    if ($(this).text() == "导出") {
                        $(this).css("display", 'none');
                    }
                });
                return {
                    color: "red"
                };
            }
            return {
                color: data.color
            };
        }
    }

    $.iexportData = $.iexportData || {};
    $.iexportData["export"] = function (setting) {
        var json = new Object();
        var exporturl = ctx + "/common/iexport/service/export?uuid=" + setting.uuid + "&type=" + setting.type;
        var dialogHtml = $("<div>");
        dialogHtml.attr("id", "exportDialog");
        dialogHtml.attr("style", "display:none");
        dialogHtml.html("<ul id='exportZtree' class='ztree'></ul>")
        $("body").append(dialogHtml);
        json.autoOpen = true;
        json.title = '数据导出';
        json.width = 750;
        json.height = 450;
        json.beforeClose = function () {
            dialogHtml.remove();
        };
        json.buttons = {
        	"忽略异常" : function(){
        		dialogHtml.find("a[treenode_a]>span").filter(function(ide, element){
        			var text = $(element).text();
        			return text && text.indexOf("无法导出,")>=0
        		}).each(function(idx, element){
        			$(element).closest("a[treenode_a]").prev("span.checkbox_true_full").trigger("click")
        		});
        	},
            "导出": function () {
                var url = ctx + "/common/iexport/service/exportData";
                var hiddenFields = {};//用于存放要提交后url指定后台的隐藏域字段

                var nodes = treeObj.getCheckedNodes(true);
                var treeNodeIds = new Array();
                if (nodes.length > 0) {
                    for (var i = 0; i < nodes.length; i++) {
                        treeNodeIds.push(nodes[i].id);
                    }
                } else {
                    alert("请勾选需要导出的资源");
                    return false
                }

                hiddenFields["type"] = setting.type;
                hiddenFields["fileName"] = nodes[0].name;
                hiddenFields["treeNodeIds"] = treeNodeIds.join(";");
                hiddenFields["uuid"] = setting.uuid;

                openWindowByPost(url, hiddenFields, "_self");


                $("#exportDialog").dialog("close");

            },
            "取消": function () {
                $(this).dialog("close");
            }
        }
        $("#exportDialog").dialog(json);
        $.alerts.pageLock('show', '正在加载...');
        $.ajax({
            url: exporturl,
            type: "POST",
            async: true,
            dataType: 'json',
            success: function (result) {
                $.alerts.pageLock('hide');
                treesetting.check.enable = true;
                treeObj = $.fn.zTree.init($("#exportZtree"), treesetting, result);
                treeObj.expandAll(true);
                treeObj.checkAllNodes(true);
            },
            error: function (data) {
                $.alerts.pageLock('hide');
                alert("数据初始化失败，请检查是否存在脏数据！");
            }
        });
    };
    $.iexportData["import"] = function (setting) {
        var impurl = ctx + "/common/iexport/service/import";
        var impform = $("<form>");// 定义一个form表单
        impform.attr("style", "display:none");
        impform.attr("id", "impoerForm");
        impform.attr("target", "_black");
        impform.attr("enctype", "multipart/form-data");
        impform.attr("method", "post");
        impform
            .html("<input type='file' id ='iupload' name='iupload'/><input type='hidden' id ='fileId' name='fileId'/>")
        impform.append("<button id='btn_iupload' type='button' class='btn'>上传</button>")
        impform.append("<div>")
        impform.append("<ul id='importZtree' class='ztree'></ul>")
        impform.append("</div>")
        var json = new Object();
        json.title = '数据导入';
        json.width = 750;
        json.height = 450;
        json.beforeClose = function () {
            impform.remove();
        };
        json.buttons = {
            "导入": function () {
                var fileName = $("#iupload").val();
                var fileId = $("#fileId").val();
                if (isNoEmpty(fileName) && isEmpty(fileId)) {
                    alert("请先点击上传按钮，再导入");
                    return false;
                }
                if (isEmpty(fileId) && isEmpty(fileName)) {
                    alert("请先上传文件！");
                    return false;
                }
                var nodes = treeObj.getCheckedNodes(true);
                var treeNodeIds = new Array();
                if (nodes.length > 0) {
                    for (var i = 0; i < nodes.length; i++) {
                        treeNodeIds.push(nodes[i].id);
                    }
                } else {
                    alert("请勾选需要导入的资源");
                    return false
                }
                $.alerts.pageLock("show", "正在导入...");
                window.setTimeout(function(){
                    $.ajaxFileUpload({
                        url: impurl + "?filedId=" + fileId,// 链接到服务器的地址
                        secureuri: false,
                        data: {
                            "importIds": treeNodeIds.join(";")
                        },
                        async: true,
                        fileElementId: 'iupload',// 文件选择框的ID属性
                        dataType: 'text', // 服务器返回的数据格式(不设置会自动处理)
                        success: function (result) {
                            // QQ浏览器会多返回莫名奇妙的额外数据，所以套个text()方法来过滤脏数据
                            result = $("<div>" + result + "</div>").text();
                            $("#impoerForm").dialog("close");
                            $.alerts.pageLock("hide");
                            result = eval('[' + result + ']');
                            alert(result[0].msg);
                            if (result[0].status == 'success') {
                                impform.remove();
                                if (typeof (setting) != "undefined" && setting != undefined) {
                                    if (typeof (setting.callback) != "undefined" && setting.callback != undefined) {
                                        setting.callback();
                                    }
                                }
                            }
                        },
                        error: function (data) {
                            $.alerts.pageLock("hide");
                            alert("导入失败");
                        }
                    });
                },500);
            },
            "差异对比": function () {
                showDiffDialog("importZtree");
            }
        };
        $("body").append(impform);
        $("#impoerForm").dialog(json);
        $("#btn_iupload").on("click", function () {
            var fileName = $("#iupload").val();
            if (fileName == "" || fileName == null) {
                alert("请先选择要导入的文件！");
                return false;
            }
            if (fileName.indexOf(".") < 0) {
                return false;
            }
            var fileType = fileName.substring(fileName.lastIndexOf("."), fileName.length);
            if (fileType != ".def" && fileType != ".DEF" && fileType != ".defpf" && fileType != ".DEFPF") {
                alert("请先选择.def或者.defpf模板！");
                return false;
            }
            var uploadUrl = ctx + "/common/iexport/service/upload";
            $.alerts.pageLock("show", "正在上传...");
            $.ajaxFileUpload({
                url: uploadUrl,// 链接到服务器的地址
                secureuri: false,
                async: true,
                fileElementId: 'iupload',// 文件选择框的ID属性
                dataType: 'text', // 服务器返回的数据格式
                success: function (data, status) {
                    $.alerts.pageLock("hide");
                    // QQ浏览器会多返回莫名奇妙的额外数据，所以套个text()方法来过滤脏数据
                    data = $("<div>" + data + "</div>").text();
                    showImportTree(data);
                    $("#fileId").val(data);
                },
                error: function (data, status, e) {
                    $.alerts.pageLock("hide");
                    alert("上传失败");
                }
            });
        });

        function showImportTree(fileId) {
            JDS.call({
                service: "iexportService.getImportTree",
                data: [fileId],
                success: function (result) {
                    treesetting.check.enable = true;
                    var zTree = $.fn.zTree.init($("#importZtree"), treesetting, result.data);
                    treeObj = zTree;
                    zTree.expandAll(true);
                    zTree.checkAllNodes(true);
                }
            });
        }
    };
    $.iexportData["dependencie"] = function (setting) {
        var json = new Object();
        var exporturl = ctx + "/common/iexport/service/export?uuid=" + setting.uuid + "&type=" + setting.type;
        var dialogHtml = $("<div>");
        dialogHtml.attr("id", "dependencieDialog");
        dialogHtml.attr("style", "display:none");
        dialogHtml.html("<ul id='dependencieZtree' class='ztree'></ul>")
        $("body").append(dialogHtml);
        json.autoOpen = true;
        json.title = '依赖关系查看';
        json.width = 750;
        json.height = 450;
        json.beforeClose = function () {
            dialogHtml.remove();
        };
        json.buttons = {
            "取消": function () {
                $(this).dialog("close");
            }
        }
        $("#dependencieDialog").dialog(json);
        $.alerts.pageLock("show", "正在加载...");
        $.ajax({
            url: exporturl,
            type: "POST",
            async: true,
            dataType: 'json',
            success: function (result) {
                $.alerts.pageLock("hide");
                treesetting.check.enable = false;
                treeObj = $.fn.zTree.init($("#dependencieZtree"), treesetting, result);
                treeObj.expandAll(true);
                treeObj.checkAllNodes(true);
            },
            error: function (data) {
                $.alerts.pageLock("hide");
                alert("数据初始化失败，请检查是否存在脏数据！");
            }
        });
    }
    $.iexportData["viewImportLog"] = function (setting) {
        $("#dataImportLogDialog").remove();
        var dialogHtml = $("<div>");
        dialogHtml.attr("id", "dataImportLogDialog");
        dialogHtml.attr("style", "display:none");
        dialogHtml.html("<table id='data_import_log_list'></table><div id='data_import_log_pager'></div>")
        $("body").append(dialogHtml);
        var dataUuid = setting.uuid || "";
        var dataType = setting.type || "";
        dialogHtml.dialog({
            width: 800,
            height: 520,
            title: "定义导入日志",
            open: function () {
                $("#data_import_log_list").jqGrid(
                    $.extend($.common.jqGrid.settings, {
                            url: ctx + '/common/jqgrid/query?queryType=dataImportLog',
                            mtype: "POST",
                            datatype: "json",
                            postData: {
                                queryPrefix: "query",
                                queryOr: false,
                                query_EQS_dataUuid: dataUuid,
                                query_EQS_dataType: dataType
                            },
                            colNames: ["uuid", "登录名", "姓名", "部门", "操作时间", "登录IP"],
                            colModel: [{
                                name: "uuid",
                                index: "uuid",
                                hidden: true
                            }, {
                                name: "loginName",
                                index: "loginName",
                                width: "50"
                            }, {
                                name: "userName",
                                index: "userName",
                                width: "50"
                            }, {
                                name: "departmentJob",
                                index: "departmentJob",
                                width: "100"
                            }, {
                                name: "logTime",
                                index: "logTime",
                                width: "50"
                            }, {
                                name: "clientIp",
                                index: "clientIp",
                                width: "50"
                            }],
                            rowNum: 20,
                            rownumbers: true,
                            rowList: [10, 20, 50, 100, 200],
                            rowId: "uuid",
                            pager: "#data_import_log_pager",
                            sortname: "logTime",
                            viewrecords: true,
                            sortable: true,
                            sortorder: "asc",
                            multiselect: true,
                            autowidth: true,
                            height: 340,
                            scrollOffset: 0,
                            jsonReader: {
                                root: "dataList",
                                total: "totalPages",
                                page: "currentPage",
                                records: "totalRows",
                                repeatitems: false
                            },
                            // 行选择事件
                            onSelectRow: function (id) {
                            },
                            loadComplete: function (data) {
                            }
                        }
                    ));
            },
            close: function () {
                $(this).dialog('destory');
            },
            buttons: {
                "确认": function () {
                    $(this).dialog('close');
                }
            }
        });
    }

    // 显示对比差异弹出框
    function showDiffDialog(treeId) {
        var treeObj = $.fn.zTree.getZTreeObj(treeId);
        var filter = function (node) {
            return node.data.color === "red";
        }
        var nodes = treeObj.getNodesByFilter(filter, false);
        var dlgSelector = "#dlg_tmp_diff";
        $.common.html.createDiv(dlgSelector);
        var options = {
            title: "差异比较",
            modal: true,
            autoOpen: true,
            resizable: false,
            width: 750,
            height: 450,
            open: function () {
                if (nodes == null || nodes.length == 0) {
                    $(dlgSelector).html("没有差异数据");
                    return;
                }
                initDiffDialogContent(dlgSelector, nodes);
            },
            buttons: {
                "关闭": function (e) {
                    $(this).oDialog("close");
                }
            },
            close: function () {
                $(dlgSelector).html("");
            }
        };
        $(dlgSelector).oDialog(options);
    }

    function initDiffDialogContent(dlgSelector, nodes) {
        var fileId = $("#fileId").val();
        var content = '<div><div style="width: 200px; float:left;"><ul style="list-style: none;padding: 0 0 5px 0; cursor: pointer;overflow: auto;" class="diff-node-list"></ul></div>'
            + '<div style="width: 500px; float:right;"><table border="1" cellpadding="0" cellspacing="0" width="100%" class="table diff-table"><thead><tr><th>字段</th><th>已上传数据</th><th>系统数据</th></tr></thead><tbody class="diff-body"></tbody></table></div><div>';
        $(dlgSelector).html(content);
        for (var i = 0; i < nodes.length; i++) {
            var node = nodes[i];
            var $li = $('<li><span>' + node.name + '</span></li>');
            $(".diff-node-list").append($li);
            $li.data("node", node);
            $li.on("click", function () {
                var node = $(this).data("node");
                JDS.call({
                    service: "iexportService.getDifference",
                    data: [fileId, node.id, node.data.type],
                    success: function (result) {
                        var data = result.data;
                        if (data != null && data.dataDifferenceDetails != null) {
                            $(".diff-body", ".diff-table").html("");
                            $.each(data.dataDifferenceDetails, function () {
                                var detail = this;
                                var rowHtml = '<tr>';
                                if (detail.isDifference === true) {
                                    var rowHtml = '<tr style="background-color: red;">';
                                }
                                rowHtml += '<td>' + detail.fieldName + '</td><td>' + detail.controlValue + '</td><td>'
                                    + detail.testValue + '</td></tr>';
                                $(".diff-body", ".diff-table").append(rowHtml);
                            });
                        }
                    }
                });
            });
        }
    }
}));

(function ($) {


})(jQuery);