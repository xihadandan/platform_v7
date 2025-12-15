define(['constant', 'commons', 'server', 'appContext', 'appModal', 'HtmlWidgetDevelopment'], function (
  constant,
  commons,
  server,
  appContext,
  appModal,
  HtmlWidgetDevelopment
) {
  var AppOpinionBaseTreeDevelopment = function () {
    HtmlWidgetDevelopment.apply(this, arguments);
  };
  commons.inherit(AppOpinionBaseTreeDevelopment, HtmlWidgetDevelopment, {
    init: function () {
      loadTree();
      function loadTree() {
        var setting = {
          callback: {
            onNodeCreated: function () {
              if (arguments[2].type == '-1') {
                $('#' + arguments[2].tId)
                  .find('span.button.ico_docu')
                  .css({
                    'background-color': 'rgba(72,140,238,.3)'
                  });
              }
            },
            beforeClick: function (treeId, treeNode) {
              if (treeNode == null) {
                return;
              }
              if (treeNode.type == '-1') {
                var opinionBaseTable = $('#common_opinion_base').parents('.ui-wBootgrid').find('.opinion_base_table').attr('id');

                $('#' + opinionBaseTable)
                  .wBootstrapTable('addParam', 'opinionCategoryUuids', treeNode.id)
                  .wBootstrapTable('refresh');
                $('#' + opinionBaseTable).attr('data-opinionCategoryName', treeNode.name);
              }

              return true;
            }
          }
        };
        $.common.ztree.initOpinionTree('common_opinion_base_tree', setting, true);
      }

      $('#common_opinion_base_tree').slimScroll({
        height: '600px'
      });

      $('#common_opinion_base')
        .on('blur', '#baseKeyword', function () {
          queryNode($(this).val());
        })
        .on('keyup', '#baseKeyword', function (e) {
          if (e.keyCode == 13) {
            queryNode($(this).val());
          }
        });

      function queryNode(val) {
        if (val != '') {
          var $zTree1 = $.fn.zTree.getZTreeObj('common_opinion_base_tree');
          var node = $zTree1.getNodesByParamFuzzy('name', $('#baseKeyword').val());
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
  return AppOpinionBaseTreeDevelopment;
});
