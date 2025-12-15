define(['mui', 'commons', 'server', 'appContext', 'appModal', 'WorkView', 'WorkViewProxy', 'formBuilder'], function (
  $,
  commons,
  server,
  appContext,
  appModal,
  WorkView,
  workView,
  formBuilder
) {
  var actionBackTemplate = '<a class="mui-action-back mui-icon mui-icon-left-nav mui-pull-left"></a>';
  var StringUtils = commons.StringUtils;
  var SystemParams = server.SystemParams;
  // 返回分类数据，按流程分类折叠
  function getCagegoryDataWithCollapses(data, text) {
    text.append('<div class="mui-card">');
    text.append('<ul class="mui-table-view mui-table-view-chevron">');
    $.each(data, function (i) {
      if (this.children == null || this.children.length == 0) {
        return;
      }
      getCagegoryCollapseData(this, text);
    });
    text.append('</ul>');
    text.append('</div>');
  }
  function getCagegoryCollapseData(treeNode, text) {
    var len = treeNode.children.length;
    if (!treeNode.iconSkin) {
      treeNode.iconSkin = 'iconfont icon-ptkj-fenlei2';
    }
    if (!treeNode.iconStyle) {
      treeNode.iconStyle = 'color:#64B3EA;';
    }

    text.append(
      '<li class="mui-table-view-cell mui-collapse"><a class="mui-navigate-right" href="#"><span class="' +
        treeNode.iconSkin +
        '" style="' +
        treeNode.iconStyle +
        'margin-right: 6px;"></span>' +
        treeNode.name +
        '<span class="cate-content-count">(' +
        len +
        ')</span></a>'
    );
    text.append('<ul class="mui-table-view mui-table-view-chevron flow-list-view">');
    $.each(treeNode.children, function (i) {
      var li = '<li class="mui-table-view-cell" flowId="' + this.id + '"><a class="" href="#">' + this.name + '</a></li>';
      text.append(li);
    });
    text.append('</ul>');
    text.append('</li>');
  }

  // 绑定事件
  function bindEvent(wrapper, ui) {
    $('.flow-list-view').on('tap', '.mui-table-view-cell', function (event) {
      var flowDefId = this.getAttribute('flowId');
      newWorkById(flowDefId);
    });
  }

  // 发起工作
  function newWorkById(flowDefId, title) {
    var options = {};
    options.title = title;
    options.flowDefId = flowDefId;
    var wrapper = document.createElement('div');
    wrapper.id = 'wf_work_view';
    var pageContainer = appContext.getPageContainer();
    var renderPlaceholder = pageContainer.getRenderPlaceholder();
    renderPlaceholder[0].appendChild(wrapper);
    formBuilder.buildPanel({
      title: title || '',
      container: '#wf_work_view'
    });
    $.ui.loadContent('#wf_work_view');
    $('#wf_work_view').workView(options);
  }

  var startNewWork = function (options) {
    // 从传入参数发起工作
    if (options && options.action && options.action === 'newWorkById' && options.flowDefId) {
      newWorkById(options.flowDefId, options.title);
      return;
    }
    var params = options.params || {};
    // 流程分类
    var categoryCode = params.categoryCode;
    var categoryCodes = StringUtils.isBlank(categoryCode) ? [] : categoryCode.split(';');
    // 应用类型
    var flowApplyId = params.flowApplyId;
    var flowApplyIds = StringUtils.isBlank(flowApplyId) ? [] : flowApplyId.split(';');
    // 流程ID
    var flowDefIdsString = params.flowDefIds;
    var flowDefIds = StringUtils.isBlank(flowDefIdsString) ? [] : flowDefIdsString.split(';');

    var filterByCategoryCode = function (dataList) {
      return $.grep(dataList, function (category, idx) {
        if (categoryCodes.length <= 0 || $.inArray(category.id, categoryCodes) > -1) {
          category.children = $.grep(category.children, function (flow, idx) {
            return flowApplyIds.length <= 0 || $.inArray(flow.type, flowApplyIds) > -1;
          });
          category.children = $.grep(category.children, function (flow, idx) {
            return flowDefIds.length <= 0 || $.inArray(flow.id, flowDefIds) > -1;
          });
          return true;
        }
      });
    };

    var filterH5DisableFlow = function (dataList) {
      try {
        var h5DisableFlows = SystemParams.getValue('h5.flow.disable');
        var h5DisableFlowArr = [];
        if (h5DisableFlows) {
          h5DisableFlowArr = h5DisableFlows.split(';');
        }
        return $.grep(dataList, function (category, idx) {
          if (h5DisableFlowArr.length > 0) {
            category.children = $.grep(category.children, function (flow, idx) {
              return h5DisableFlowArr.indexOf(flow.id) > -1;
            });
          }
          return category.children.length !== 0;
        });
      } catch (e) {
        return dataList;
      }
    };

    // 列表显示发起工作
    var JDS = server.JDS;
    var StringBuilder = commons.StringBuilder;
    var content = new StringBuilder();
    JDS.call({
      service: 'workflowNewWorkService.getMobileFlowDefinitions',
      async: false,
      success: function (result) {
        var data = filterByCategoryCode(result.data);
        // data = filterH5DisableFlow(data);
        getCagegoryDataWithCollapses(data, content);
      }
    });

    setTimeout(() => {
      // 创建发起工作面板
      var wrapper = document.createElement('div');
      wrapper.id = 'mobile_new_work';
      var pageContainer = appContext.getPageContainer();
      var renderPlaceholder = pageContainer.getRenderPlaceholder();
      renderPlaceholder[0].appendChild(wrapper);
      formBuilder.buildPanel({
        title: '发起工作',
        content: content.toString(),
        container: '#mobile_new_work'
      });

      bindEvent(wrapper, options.ui);
      $.ui.loadContent('#mobile_new_work');
    }, 0);
  };
  return startNewWork;
});
