(function (global, factory) {
    if (typeof define === "function" && define.amd) {
        // AMD. Register as an anonymous module.
        define(["api"], function (api) {
            factory(api);
        });
    } else {
        // Browser globals
        factory(global.api);
    }
})(window, function init(api) {
    printHistory = [];
    // 1、根据文件ID和用户ID获取预览权限
    api.ajax({
        url: "/wopi/files/getViewPermission/" + api.getFileId(),
        type: "GET",
        dataType: "json",
        data: {
            userId: api.getUserId(),
        },
        success: function (result) {
            var permission = result;
            api.enablePrint(permission.print);
            api.enableOpenFile(permission.openFile);
            api.enableDownload(permission.download);
            api.enableViewBookmark(permission.viewBookmark);
        },
        error: function (jqXHR) {
        }
    });
    // 2、根据文件ID获取文件打印记录
    api.ajax({
        url: "/wopi/files/getPrintHistory/" + api.getFileId(),
        type: "GET",
        dataType: "json",
        success: function (result) {
            printHistory = result;
            if (printHistory && printHistory.length) {
                var $modal = $("<div class=\"modal fade\" id=\"printHistoryModal\" tabindex=\"-1\" role=\"dialog\" aria-labelledby=\"printHistoryModalLabel\" aria-hidden=\"true\">\n" +
                    "    <div class=\"modal-dialog\">\n" +
                    "        <div class=\"modal-content\">\n" +
                    "            <div class=\"modal-header\">\n" +
                    "                <button type=\"button\" class=\"close\" data-dismiss=\"modal\" aria-hidden=\"true\">&times;</button>\n" +
                    "                <h4 class=\"modal-title\" id=\"printHistoryModalLabel\">打印记录</h4>\n" +
                    "            </div>\n" +
                    "            <div class=\"modal-body\"></div>\n" +
                    "            <div class=\"modal-footer\">\n" +
                    "                <button type=\"button\" class=\"btn btn-default\" data-dismiss=\"modal\">取消</button>\n" +
                    "            </div>\n" +
                    "        </div><!-- /.modal-content -->\n" +
                    "    </div><!-- /.modal -->\n" +
                    "</div>\n" +
                    "");
                var $template = $("<button id=\"showHistory\" class=\"btn toolbarButton glyphicon glyphicon-header hiddenLargeView\" title=\"打印记录\" tabindex=\"32\" data-l10n-id=\"show_history\">\n" +
                    "     <span data-l10n-id=\"show_history_label\">打印记录</span>\n" +
                    "</button>");
                api.appendButton($template);
                $(document.body).append($modal);
                $modal.modal({
                    show: false
                });
                $template.click(function (event) {
                    var html = '<table class="table table-bordered"><thead><tr><th>操作人</th><th>打印次数</th><th>打印时间</th></tr></thead><tbody>';
                    for (var i = 0; i < printHistory.length; i++) {
                        html += '<tr><td>' + printHistory[i].username + '</td><td>' + printHistory[i].printcount + '</td><td>' + printHistory[i].printdate + '</td></tr>'
                    }
                    html += '</tbody></table>';
                    $modal.find(".modal-content>.modal-body").html(html);
                    $modal.modal("show");
                });
            }
        },
        error: function (jqXHR) {
        }
    });
    // 3、根据文件ID获取文件详情
    api.ajax({
        url: "/wopi/files/" + api.getFileId() + "?access_token=0",
        type: "GET",
        dataType: "json",
        success: function (result) {
            // console.log("result:", result);
        },
        error: function (jqXHR) {
        }
    });
    // 4、打印时，记录打印原因
    var onbeforeprint = api.onbeforeprint;
    api.onbeforeprint = function () {
        var reason = "";
        if (printHistory && printHistory.length > 1) {
            reason = prompt("请输入打印原因", "");
            if ($.trim(reason).length <= 0) {
                return false;
            }
        }
        if (false === onbeforeprint.apply(this, arguments)) {
            return false;
        }
        ;
        api.ajax({
            url: "/wopi/files/savePrintReason/" + api.getFileId(),
            type: "POST",
            dataType: "json",
            data: {
                userId: api.getUserId(),
                reason: reason,
            },
            success: function (result) {
                console.log("result:", result);
            },
            error: function (jqXHR) {
            }
        });
    }
});
