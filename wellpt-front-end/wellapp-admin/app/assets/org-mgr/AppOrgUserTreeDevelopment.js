define(["constant", "commons", "server", "appContext", "appModal", "HtmlWidgetDevelopment"],
  function (constant, commons, server, appContext, appModal, HtmlWidgetDevelopment) {
    var JDS = server.JDS;

    var AppOrgUserTreeDevelopment = function () {
      HtmlWidgetDevelopment.apply(this, arguments);
    };

    var eleIdPathStr = function (treeNode) {
      if (treeNode.getParentNode()) {
        return eleIdPathStr(treeNode.getParentNode()) + "/" + treeNode.id;
      } else {
        return treeNode.id;
      }
    };
    var orgVersionIdStr = function (trn) {
      if (trn.getParentNode()) {
        return orgVersionIdStr(trn.getParentNode());
      } else {
        return trn.id;
      }
    };
    commons.inherit(AppOrgUserTreeDevelopment, HtmlWidgetDevelopment, {
      init: function () {
        appModal.showMask("数据加载中...", $("#all_org_tree"));
        loadTree();
        $("#org_user_tree_wrap").slimScroll({
          height: "600px",
          wheelStep: navigator.userAgent.indexOf('Firefox') > -1 ? 1 : 10
        });

        $('input[name="allUserCheckbox"]').on('click', function () {
          if($(this).prop('checked')) {
            // 选中则查询全部用户
            var orgUserTable = $('#org_user_tree_wrap').parents('.ui-wBootgrid').find('.org-user-table').attr("id");

            $('#' + orgUserTable).wBootstrapTable('addParam', 'eleIdPath', '').wBootstrapTable('refresh');
          }
        });

        function loadTree() {
          var setting = {
            callback: {
              onClick: function (event, treeId, treeNode) {
                $('input[name="allUserCheckbox"]').prop('checked', false);
                if (treeNode == null) {
                  return;
                }
                var orgUserTable = $("#org_user_tree_wrap").parents(".ui-wBootgrid").find(".org-user-table").attr("id");
                var eleIdPath = eleIdPathStr(treeNode);

                $("#" + orgUserTable).wBootstrapTable("addParam", "eleIdPath", eleIdPath).wBootstrapTable("refresh")
                return true;
              },
              beforeAsync: function (treeId, treeNode) {
                if (!treeNode.isParent) {
                  return false;
                }
                if (!treeNode.isAjaxing) {
                  treeNode.times = 1;
                  var zTree = $.fn.zTree.getZTreeObj(treeId);
                  var otherParam = zTree.setting.async.otherParam;
                  var data = otherParam.data;
                  var orgVersionId = orgVersionIdStr(treeNode);
                  data[1].orgVersionId = orgVersionId;
                  data[1].treeNodeId = treeNode.id;
                  otherParam.data = data;
                  return true;
                } else {
                  appModal.info("数据加载中，请稍后展开节点...");
                  return false;
                }
              },
              onAsyncError: function (event, treeId, treeNode, XMLHttpRequest, textStatus, errorThrown) {
                appModal.info("获取数据出现异常。");
              }
            },
            async: {
              enable: true,
              url: ctx + "/json/data/services",
              otherParam: {
                methodName: 'children',
                serviceName: 'multiOrgTreeDialogService',
                validate: false,
                data: ["MyUnit", {
                  isNeedUser: "0",
                  isInMyUnit: true
                }]
              },
              contentType: "application/json",
              dataType: "json"
            }
          };

          $.ajax({
            url: ctx + '/api/org/tree/dialog/children',
            type: "POST",
            dataType: 'json',
            data: {
              type: 'MyUnit',
              params: {
                isNeedUser: '0',
                isInMyUnit: true
              }
            },
            success: function (result) {
              var treeNodes = result.data;
              for (let i = 0; i < treeNodes.length; i++) {
                treeNodes[i].isParent = 1;
              }
              var zTree = $.fn.zTree.init($("#all_org_tree"), setting, treeNodes);
              var nodes = zTree.getNodes();
              // 默认展开第一个节点
              if (nodes.length > 0) {
                var node = nodes[0];
                zTree.expandNode(node, true, false, false, true);
              }
            }
          });

        }
      }
    });
    return AppOrgUserTreeDevelopment;
  });

