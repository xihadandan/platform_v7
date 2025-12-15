define([ "jquery", "server", "commons", "constant", "appContext", "appModal" ], function($, server, commons, constant,
        appContext, appModal) {
    var StringUtils = commons.StringUtils;
    var StringBuilder = commons.StringBuilder;
    var UrlUtils = commons.UrlUtils;
    var UUID = commons.UUID;
    // 流程快照数据查看器
    var WorkFlowSnapshotViewer = function(workView) {
        this.workView = workView;
    };
    var tplSb = new StringBuilder();
    tplSb.append('<div class="btn-group">');
    tplSb.append('<button name="btn_compare_snapshots" class="btn btn-primary">比较选择的快照</button>');
    tplSb.append('<button name="btn_view_snapshot_data" class="btn btn-primary">查看快照数据</button>');
    tplSb.append('</div>');
    tplSb.append('<table class="table">');
    tplSb.append('  <thead>');
    tplSb.append('      <tr>');
    tplSb.append('          <th></th>');
    tplSb.append('          <th style="min-width: 3.5em;">快照ID</th>');
    tplSb.append('          <th>创建人</th>');
    tplSb.append('          <th>创建时间</th>');
    tplSb.append('      </tr>');
    tplSb.append('  </thead>');
    tplSb.append('  <tbody>');
    tplSb.append('  </tbody>');
    tplSb.append('</table>');
    var trTpl = "<td><input snid='{0}' name='ck-snid' type='checkbox'></td><td>{0}</td><td>{1}</td><td>{2}</td>";
    commons.inherit(WorkFlowSnapshotViewer, $.noop, {
        show : function() {
            var _self = this;
            var dlgId = UUID.createUUID();
            var dlgSelector = "#" + dlgId;
            var title = "查看流程数据快照";
            var message = "<div id='" + dlgId + "'>" + tplSb.toString() + "</div>";
            var dlgOptions = {
                title : title,
                message : message,
                className : "dlg-wf-snapshot",
                size : "large",
                shown : function() {
                    var snapshots = _self.getFlowDataSnapshots();
                    $(dlgSelector).find("tbody").append(_self.getSnapshotHtmlBody(snapshots));
                    _self.bindEvents($(dlgSelector));
                }
            };
            appModal.dialog(dlgOptions);
        },
        getFlowDataSnapshots : function() {
            var _self = this;
            var snapshots = [];
            var workData = _self.workView.getWorkData();
            $.get({
	            url : ctx + "/api/workflow/work/listFlowDataSnapshotWithoutDyformDataByFlowInstUuid",
                data : {flowInstUuid: workData.flowInstUuid},
                async : false,
                success : function(result) {
                    snapshots = result.data;
                }
            });
            return snapshots;
        },
        getSnapshotHtmlBody : function(snapshots) {
            var trSb = new StringBuilder();
            $.each(snapshots, function(i, snapshot) {
                var id = snapshot.id;
                var userName = snapshot.createUserName;
                var createTime = snapshot.createTime;
                trSb.append("<tr>");
                trSb.appendFormat(trTpl, id, userName, createTime);
                trSb.append("<tr/>");
            });
            return trSb.toString();
        },
        // 绑定事件
        bindEvents : function($container) {
            // 比较选择的快照
            $("button[name='btn_compare_snapshots']", $container).on("click", function() {
                var $checkbox = $($container).find("input[name='ck-snid']:checked");
                if ($checkbox.length != 2) {
                    appModal.error("请选择两条记录！");
                    return;
                }
                var snId1 = $($checkbox[0]).attr("snid");
                var snId2 = $($checkbox[1]).attr("snid");
                var workViewUrl = window.location.href;
                workViewUrl = UrlUtils.appendUrlParams(workViewUrl, {
                    "snAction" : "1",
                    "snId1" : encodeURIComponent(snId1),
                    "snId2" : encodeURIComponent(snId2),
                    "ep_workViewFragment" : "WorkViewSnapshotFragment"
                });
                appContext.getWindowManager().open(workViewUrl);
            });
            // 查看快照数据
            $("button[name='btn_view_snapshot_data']", $container).on("click", function() {
                var $checkbox = $($container).find("input[name='ck-snid']:checked");
                if ($checkbox.length != 1) {
                    appModal.error("请选择一条记录！");
                    return;
                }
                var workViewUrl = window.location.href;
                workViewUrl = UrlUtils.appendUrlParams(workViewUrl, {
                    "snAction" : "2",
                    "snId" : encodeURIComponent($checkbox.attr("snid")),
                    "ep_workViewFragment" : "WorkViewSnapshotFragment"
                });
                appContext.getWindowManager().open(workViewUrl);
            });
        }
    });
    return WorkFlowSnapshotViewer;
});