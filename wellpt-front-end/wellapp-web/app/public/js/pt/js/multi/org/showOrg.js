$(function () {

  // 初始化按钮和tabs控件
  $("input[type=submit], a, button").button();
  // 页面布局
  Layout.layout();

  var setting = {};
  var t = $("#type").val();
  var params = JSON.parse($("#params").html());
  $.ajax({
    url: ctx + '/api/org/tree/dialog/queryUnitTreeDialogDataByType',
    type: "POST",
    dataType: 'json',
    data: {
      type: t,
      params: params
    },
    success: function (result) {
      var treeNodes = result.data;
      var zTree = $.fn.zTree.init($("#org_tree"), setting, treeNodes);
      var nodes = zTree.getNodes();
      // 默认展开第一个节点
      if (nodes.length > 0) {
        var node = nodes[0];
        zTree.expandNode(node, true, false, false, true);
      }
    }
  });

  //全收缩,收缩到根节点
  $("#fold_all").click(function () {
    var zTree = $.fn.zTree.getZTreeObj("org_tree");
    var rootNode = zTree.getNodes()[0];
    for (var i = 0; i < rootNode.children.length; i++) {
      var childNode = rootNode.children[i];
      zTree.expandNode(childNode, false);
    }
  });
  //全展开
  $("#unfold_all").click(function () {
    $.fn.zTree.getZTreeObj("org_tree").expandAll(true);
  });

});






