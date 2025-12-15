define(['constant', 'commons', 'server', 'appContext', 'appModal', 'HtmlWidgetDevelopment'], function (
  constant,
  commons,
  server,
  appContext,
  appModal,
  HtmlWidgetDevelopment
) {
  var AppOpinionClassifyTreeDevelopment = function () {
    HtmlWidgetDevelopment.apply(this, arguments);
  };
  commons.inherit(AppOpinionClassifyTreeDevelopment, HtmlWidgetDevelopment, {
    init: function () {
      loadTree();
      function loadTree() {
        var setting = {
          callback: {
            beforeClick: function (treeId, treeNode) {
              if (treeNode == null) {
                return;
              }
              var ids = getIds(treeNode, []);
              var opinionClassifyTable = $('#common_opinion_classify').parents('.ui-wBootgrid').find('.opinion_classify_table').attr('id');
              $('#' + opinionClassifyTable).attr('data-businessFlag', treeNode.id);
              $('#' + opinionClassifyTable).attr('data-businessFlagName', treeNode.name);

              $('#' + opinionClassifyTable)
                .wBootstrapTable('addParam', 'businessFlag', ids)
                .wBootstrapTable('refresh');
              return true;
            }
          }
        };
        $.common.ztree.initOpinionTree('common_opinion_classify_tree', setting, null);
      }
      setTimeout(function () {
        $('a.level0').trigger('click');
      }, 300);

      $('#common_opinion_classify_tree').slimScroll({
        height: '600px'
      });

      function getIds(obj, ids) {
        ids.push(obj.id);
        if (obj.children.length > 0) {
          $.each(obj.children, function (index, item) {
            getIds(item, ids);
          });
        }
        return ids;
      }

      $('#common_opinion_classify')
        .on('blur', '#classifyKeyword', function () {
          queryNode($(this).val());
        })
        .on('keyup', '#classifyKeyword', function (e) {
          if (e.keyCode == '13') {
            queryNode($(this).val());
          }
        });

      function queryNode(val) {
        if (val != '') {
          var $zTree1 = $.fn.zTree.getZTreeObj('common_opinion_classify_tree');
          var node = $zTree1.getNodesByParamFuzzy('name', val);
          if (node.length) {
            $zTree1.selectNode(node[0]);
          }
        }
      }
    },
    refresh: function () {
      var _self = this;
      _self.init();
    }
  });
  return AppOpinionClassifyTreeDevelopment;
});
