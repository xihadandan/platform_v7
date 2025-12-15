(function (root, factory) {
  "use strict";
  if (typeof define === "function" && define.amd) {
    // AMD. Register as an anonymous module.
    define(["jquery", "commons", "server", "appContext", "appModal", "formBuilder"], factory);
  } else if (typeof exports === "object") {
    // Node. Does not work with strict CommonJS, but
    // only CommonJS-like environments that support module.exports,
    // like Node.
    module.exports = factory(require("jquery"));
  } else {
    // Browser globals (root is window)
    root.appModal = factory(root.jQuery, root.commons);
  }

}(this, function init($, commons, server, appContext, appModal, formBuilder) {
  "use strict";
  var UUID = commons.UUID;
  var StringBuilder = commons.StringBuilder;
  var StringUtils = commons.StringUtils;
  var FileDownloadUtils = server.FileDownloadUtils;
  var SystemParams = server.SystemParams;
  var backendUrl = getBackendUrl();
  var importUrl = backendUrl + "/common/iexport/service/import";
  var exporturl = backendUrl + "/common/iexport/service/export";
  var exportDataUrl = backendUrl + "/common/iexport/service/exportData";
  // our public object; augmented after our private API
  var iexports = {};

  // 设置弹出框样式
  function setDialogContainerStyle($container, options) {
    var cssOptions = $.extend({
      "height": "380px",
      "overflow-x": "hidden",
      "overflow-y": "auto"
    }, options || {});
    $container.css(cssOptions);
  }

  // 定义导入
  iexports["import"] = function (options) {
    var dlgId = UUID.createUUID();
    var title = "定义导入";
    var sb = new StringBuilder();
    sb.appendFormat("<div id='{0}'>", dlgId);
    sb.appendFormat("<div id='fileUploadDiv'></div>");
    sb.appendFormat("<div><div>文字颜色标识的说明<i id='importTip' class='iconfont icon-ptkj-tishishuoming'></i></div><ul id='importZtree' class='ztree'></ul></div>");
    sb.appendFormat("</div>");
    var message = sb.toString();
    var $dialog;
    var dlgOptions = {
      title: title,
      message: message,
      size: "large",
      shown: function () {
        $('#importTip', $dialog).popover({
          html: true,
          placement: "bottom",
          container: "body",
          trigger: "hover",
          template: '<div class="popover" role="tooltip" style="z-index: 999999999999999;"><div class="arrow"></div><h3 class="popover-title"></h3><div class="popover-content"></div></div>',
          content: function () {
            return '<span style="color:#000;font-weight: bold">不导入绿色数据！存在灰色数据时，无法导入！</span><br>' +
              '<span style="color:green">绿色：当前系统存在该资源，且资源在数据库中的版本号一致</span><br>' +
              '<span style="color:orange">橙色：当前系统存在该资源，但资源在数据库中的版本号不一致</span><br>' +
              '<span style="color:black">黑色：当前系统不存在该资源，且导入数据中包含资源的定义</span><br>' +
              '<span style="color:red">红色：当前系统不存在该资源，且导入数据中不包含资源的定义</span>';
          }
        });
        var $container = $("#" + dlgId);
        setDialogContainerStyle($container);
        formBuilder.buildFileUpload({
          url: importUrl,
          container: $("#fileUploadDiv", $container),
          labelColSpan: "0",
          name: "iexportFileUuid",
          controlOption: {
            addFileText: "选择定义文件",
          },
          singleFile: true,
          ext: ['defpf'],
          events: {
            "fileuploaddone": function (e, data) {
              $(".file-info-list", $container).hide();
              var uploadedFiles = data.result.data;
              if ($.isArray(uploadedFiles) && uploadedFiles.length > 0) {
                showImportTree(uploadedFiles[0].fileID, $container);
              }
            }
          }
        })
      },
      buttons: {
        confirm: {
          label: "导入",
          className: "btn-primary btn-import",
          callback: function () {
            var $container = $("#" + dlgId);
            var fileId = $("input[name='iexportFileUuid']", $container).val();
            if (isEmpty(fileId)) {
              appModal.error("请先上传文件！");
              return false;
            }
            var treeObj = $.fn.zTree.getZTreeObj("importZtree");
            var nodes = treeObj.getCheckedNodes(true);
            var treeNodeIds = new Array();
            if (nodes.length > 0) {
              for (var i = 0; i < nodes.length; i++) {
                treeNodeIds.push(nodes[i].id);
              }
            } else {
              appModal.error("请勾选需要导入的数据！");
              return false
            }
            appModal.showMask("正在导入中", null, 1000 * 60 * 10);
            JDS.call({
              service: "iexportService.importData",
              data: [fileId, false, treeNodeIds.join(";")],
              async: true,
              mask: true,
              success: function (result) {
                $dialog.modal('hide');
                appModal.hideMask();
                appModal.success("导入成功！");
                if ($.isFunction(options.callback)) {
                  options.callback.call(this);
                }
              }
            });
            return false;
          }
        },
        showDiff: {
          label: "差异对比",
          className: "btn-primary",
          callback: function () {
            var $container = $("#" + dlgId);
            var fileId = $("input[name='iexportFileUuid']", $container).val();
            if (isEmpty(fileId)) {
              appModal.error("请先上传文件！");
              return false;
            }
            showDiffDialog("importZtree", fileId);
            return false;
          }
        },
        cancel: {
          label: "取消",
          className: "btn-default"
        }
      }
    };
    $dialog = appModal.dialog(dlgOptions);
  }

  function showImportTree(fileId, $container) {
    appModal.showMask("产品集成信息解析中", null, 1000 * 60 * 10);
    JDS.call({
      service: "iexportService.getImportTree",
      data: [fileId],
      mask: true,
      success: function (result) {
        var treeSettings = getImportTreeSettings();
        var zTree = $.fn.zTree.init($("#importZtree", $container), treeSettings, result.data);
        zTree.expandAll(true);
        zTree.checkAllNodes(true);
        appModal.hideMask();
      }
    });
  }

  function showDiffDialog(treeId, fileId) {
    var ztree = $.fn.zTree.getZTreeObj(treeId);
    var filter = function (node) {
      return node.data.color === "red";
    }
    var nodes = ztree.getNodesByFilter(filter, false);
    var dlgId = UUID.createUUID();
    var contentSelector = "#dlg_tmp_diff";
    var title = "差异比较";
    var message = "<div id='" + dlgId + "'><div id='dlg_tmp_diff'></div></div>";
    var dlgOptions = {
      title: title,
      message: message,
      size: "large",
      shown: function () {
        var $container = $("#" + dlgId);
        setDialogContainerStyle($container, {
          "overflow-x": "auto"
        });
        if (nodes == null || nodes.length == 0) {
          $(contentSelector, $container).html("没有差异数据！");
          return;
        }
        initDiffDialogContent(contentSelector, nodes, fileId);
      },
      buttons: {
        confirm: {
          label: "确定",
          className: "btn-primary",
          callback: function () {
            return true;
          }
        }
      }
    };
    appModal.dialog(dlgOptions);
  }

  function initDiffDialogContent(contentSelector, nodes, fileId) {
    var sb = new StringBuilder();
    sb.append('<div class="row">');
    sb.append('<div class="col-xs-2">');
    sb.append('<ul style="list-style: none;padding: 0 0 5px 0; cursor: pointer;" class="diff-node-list"></ul>');
    sb.append('</div>');
    sb.append('<div class="col-xs-10">');
    sb.append('<table border="1" cellpadding="0" cellspacing="0" width="100%" class="table diff-table">');
    sb.append('<thead><tr><th>字段</th><th>已上传数据</th><th>系统数据</th></tr></thead><tbody class="diff-body"></tbody>');
    sb.append('</table>');
    sb.append('</div>');
    sb.append('</div>');
    var content = sb.toString();
    $(contentSelector).html(content);
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
            showDataDifferenceDetails(data);
          }
        });
      });
    }
  }

  function showDataDifferenceDetails(data) {
    if (data != null && data.dataDifferenceDetails != null) {
      $(".diff-body", ".diff-table").html("");
      $.each(data.dataDifferenceDetails, function () {
        var detail = this;
        var rowHtml = '<tr>';
        if (detail.isDifference === true) {
          var rowHtml = '<tr style="background-color: red;">';
        }
        rowHtml += '<td>' + detail.fieldName + '</td><td>' + detail.controlValue + '</td><td>' +
          detail.testValue + '</td></tr>';
        $(".diff-body", ".diff-table").append(rowHtml);
      });
    }
  }

  // 定义导出
  iexports["export"] = function (options) {
    var dlgId = UUID.createUUID();
    var title = "定义导出";
    var message = "<div id='" + dlgId + "'><ul id='exportZtree' class='ztree'></ul></div>";
    var dlgOptions = {
      title: title,
      message: message,
      size: "large",
      shown: function () {
        var $container = $("#" + dlgId);
        setDialogContainerStyle($container);
        appModal.showMask("产品集成信息解析中", null, 1000 * 60 * 10);
        JDS.call({
          service: "iexportService.getExportTree",
          data: [options.uuid, options.type],
          version: "",
          mask: true,
          success: function (result) {
            var data = result.data;
            var treeSettings = getExportTreeSettings();
            var ztree = $.fn.zTree.init($("#exportZtree", $container), treeSettings, data);
            ztree.expandAll(true);
            ztree.checkAllNodes(true);
            appModal.hideMask();
          }
        });
      },
      buttons: {
        confirm: {
          label: "确定",
          className: "btn-primary btn-export",
          callback: function () {
            // 用于存放要提交后url指定后台的隐藏域字段
            var hiddenFields = {};
            var treeObj = $.fn.zTree.getZTreeObj("exportZtree");
            var nodes = treeObj.getCheckedNodes(true);
            var treeNodeIds = new Array();
            if (nodes.length > 0) {
              for (var i = 0; i < nodes.length; i++) {
                treeNodeIds.push(nodes[i].id);
              }
            } else {
              appModal.error("请勾选需要导出的数据！");
              return false
            }
            hiddenFields["type"] = options.type;
            hiddenFields["fileName"] = nodes[0].name;
            hiddenFields["treeNodeIds"] = treeNodeIds.join(";");
            hiddenFields["uuid"] = options.uuid;
            FileDownloadUtils.downloadTools(exportDataUrl, hiddenFields);
            return true;
          }
        },
        ignore: {
          label: "忽略错误",
          className: "btn-default",
          callback: function () {
            $("#" + dlgId).find("a[treenode_a]>span").filter(function (ide, element) {
              var text = $(element).text();
              return text && text.indexOf("无法导出,") >= 0
            }).each(function (idx, element) {
              $(element).closest("a[treenode_a]").prev("span.checkbox_true_full").trigger("click")
            });
            return false;
          }
        },
        cancel: {
          label: "取消",
          className: "btn-default"
        }
      }
    };
    appModal.dialog(dlgOptions);
  };

  // 定义导入日志
  iexports["viewImportLog"] = function (options) {
    var defaultListViewId = "wBootstrapTable_C87E172A603000013FEBD60011A27900";
    var importLogListViewId = SystemParams.getValue("cd.import.log.listViewId", defaultListViewId);
    var dlgId = UUID.createUUID();
    var title = "定义导入日志";
    var message = "<div id='" + dlgId + "'></div>";
    var dlgOptions = {
      title: title,
      message: message,
      size: "large",
      shown: function () {
        var $container = $("#" + dlgId);
        setDialogContainerStyle($container);
        appContext.renderWidget({
          renderTo: "#" + dlgId,
          widgetDefId: defaultListViewId,
          forceRenderIfConflict: true,
          callback: function () { },
          onPrepare: function () { }
        });
      },
      buttons: {
        confirm: {
          label: "确定",
          className: "btn-primary",
          callback: function () {
            return true;
          }
        }
      }
    };
    appModal.dialog(dlgOptions);
  }

  // 查看依赖
  iexports["viewDependences"] = iexports["dependencie"] = function (options) {
    var dlgId = UUID.createUUID();
    var title = "查看依赖关系";
    var message = "<div id='" + dlgId + "'><ul id='dependenceZtree' class='ztree'></ul></div>";
    var dlgOptions = {
      title: title,
      message: message,
      size: "large",
      shown: function () {
        var $container = $("#" + dlgId);
        setDialogContainerStyle($container);
        JDS.call({
          service: "iexportService.getExportTree",
          data: [options.uuid, options.type],
          version: "",
          mask: true,
          success: function (result) {
            var data = result.data;
            var treeSettings = getExportTreeSettings();
            treeSettings.check.enable = false;
            var ztree = $.fn.zTree.init($("#dependenceZtree", $container), treeSettings, data);
            ztree.expandAll(true);
            ztree.checkAllNodes(true);
          }
        });
      },
      buttons: {
        confirm: {
          label: "确定",
          className: "btn-primary"
        }
      }
    };
    appModal.dialog(dlgOptions);
  }

  // 双击展开结点
  function dblClickExpand(treeId, treeNode) {
    return treeNode.level > 0;
  }

  // 获取导入树设置
  function getImportTreeSettings() {
    var treeSettings = getExportTreeSettings();
    return treeSettings;
  }

  // 获取导出树设置
  function getExportTreeSettings() {
    function setFontCss(treeId, treeNode) {
      var data = treeNode.data;
      if (data != null) {
        // 有错误数据，标记为红色并隐藏导出按钮
        if (data.type == "errorData") {
          $(".btn-export", ".modal-dialog").hide();
          return {
            color: "red"
          };
        }
        return {
          color: data.color
        };
      }
    }

    var treeSettings = {
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
            var nodes = treeObj.getCheckedNodes(true);
            $(".btn-export", ".modal-dialog").show();
            console.log($(".btn-export", ".modal-dialog"));
            for (var j = 0; j < nodes.length; j++) {
              if (nodes[j].data.type == "errorData") {
                $(".btn-export", ".modal-dialog").hide();
                return;
              }
            }
          }
        }
      }
    };
    return treeSettings;
  }

  $.iexportData = iexports;
  return $.iexportData;
}));