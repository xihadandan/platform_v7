define(['server', 'commons', 'constant', 'jquery-ui', 'bootbox', 'appContext', 'appModal', 'slimScroll', 'muuri'], function (
  server,
  commons,
  constant,
  ui,
  bootbox,
  appContext,
  appModal,
  slimScroll,
  Muuri
) {
  var JDS = server.JDS;
  var StringUtils = commons.StringUtils;
  // 本地数据数组
  var allDataArr = null;

  // 返回分类数据
  function getCagegoryData(dataArr) {
    /*
     * if (!treeNode.children || treeNode.children.length === 0) { return
     * ""; }
     */
    var text =
      '<div class="event-contents"><div class="nav-container"><div class="dropdown-icon"><i class="fa fa-angle-down" aria-hidden="true"></i></div><ul class="nav nav-tabs" id="sortable">';
    var templateEngine = appContext.getJavaScriptTemplateEngine();
    // 页签tab导航
    $.each(dataArr, function (i, n) {
      var cssClass = n.cssClass || '';
      var tabId = 'event-title' + n.id;
      var li = '';
      if (i == 0) {
        li =
          '<li class="active ' +
          cssClass +
          '"><a href="#' +
          tabId +
          '" tabId="' +
          tabId +
          '" role="tab" title="' +
          n.name +
          '" data-toggle="tab">' +
          n.name +
          '</a></li>';
      } else {
        li =
          '<li class="' +
          cssClass +
          '" draggable="true"><a href="#' +
          tabId +
          '" tabId="' +
          tabId +
          '" role="tab" data-toggle="tab" title="' +
          n.name +
          '">' +
          n.name +
          '</a></li>';
      }
      text += templateEngine.render(li, this);
    });
    text += '</ul></div><div class="tab-content list-group">';
    // 页签tab内容区
    $.each(dataArr, function (i, n) {
      if (i == 0) {
        var div = '<div role="tabpanel" class="tab-pane active" id="event-title' + n.id + '">';
      } else {
        var div = '<div role="tabpanel" class="tab-pane" id="event-title' + n.id + '">';
      }
      $.each(n.children, function (j) {
        var li = '<a class="list-group-item" flowId="${id}">${name}</a>';
        div += templateEngine.render(li, this);
      });
      div += '</div>';
      text += div;
    });
    text += '</div></div>';
    return text;
  }

  // 数据结构转换
  function newData(data) {
    // 最近使用
    var recentUse = null;
    var allEvents = new Object();
    allEvents.name = '全部';
    allEvents.id = 0;
    allEvents.cssClass = 'unsortable all-flow';
    var allEventArr = [];
    var returnData = [];
    $.each(data, function (i, n) {
      if (n.id == 'recent_use') {
        recentUse = n;
        recentUse.cssClass = 'unsortable';
        return;
      }
      if (n.children && n.children.length > 0) {
        returnData.push(n);
      }
      $.each(n.children, function (j, k) {
        allEventArr.push(k);
      });
    });
    allDataArr = allEventArr;
    allEvents.children = allEventArr;
    returnData.unshift(allEvents);
    if (recentUse != null && recentUse.children && recentUse.children.length > 0) {
      returnData.unshift(recentUse);
    }
    return returnData;
  }

  // 页签导航事件
  function tabNavEvent() {
    var liArr = $('.nav-container .nav-tabs li');
    if (liArr.length > 6) {
      $('.nav-container .dropdown-icon').show();
    }
    $('.nav-container .dropdown-icon').on('click', function () {
      if ($(this).find('i').hasClass('fa-angle-down')) {
        $('.nav-container').css('height', 'auto');
        $('.nav-container .nav').css('height', 'auto');
        var navNewHeight = $('.nav-container .nav').height();
        var containerHeight = $(this).parent().parent().height();
        $(this)
          .parent()
          .next('.list-group')
          .css('height', containerHeight - navNewHeight + 'px');
        $('.nav-container .dropdown-icon').find('i').removeClass('fa-angle-down').addClass('fa-angle-up');
      } else {
        $('.nav-container').css('height', '42px');
        $('.nav-container .nav').css('height', '42px');
        var containerHeight = $(this).parent().parent().height();
        $(this)
          .parent()
          .next('.list-group')
          .css('height', containerHeight - 42 + 'px');
        $('.nav-container .dropdown-icon').find('i').removeClass('fa-angle-up').addClass('fa-angle-down');
      }
    });
    dragEvent();
  }

  // 发起事件搜索部分
  function searchToHtml() {
    var searchBox =
      '<div class="search-bar clearfix">' +
      '<div class="input-group pull-right">' +
      '<input type="search" class="form-control" aria-label="...">' +
      '<div class="input-group-btn">' +
      '<button class="btn btn-primary"><i class="fa fa-search"></i></button>' +
      '</div></div></div>';
    return searchBox;
  }

  // 发起搜索事件
  function searchEvent(data) {
    $('.search-bar button').on('click', function () {
      var keyWord = $(this).parent().prev('input').val().trim();
      $('.nav-container').find('.all-flow a').tab('show');
      searchFlow(keyWord, data);
      return;
    });
    $('.search-bar input').on('change', function () {
      var keyWord = $(this).val().trim();
      $('.nav-container').find('.all-flow a').tab('show');
      searchFlow(keyWord, data);
    });
  }
  function searchFlow(keyWord, data) {
    var searchData = [];
    $.each(data, function (i, n) {
      var flag = n.name.indexOf(keyWord);
      if (flag != -1) {
        searchData.push(n);
      }
    });
    var tabId = $('.nav-container').find('.all-flow a').attr('tabId');
    var searchHtml = '';
    var templateEngine = appContext.getJavaScriptTemplateEngine();
    if (searchData.length == 0) {
      searchHtml = '没有匹配到您搜索的关键字！';
      $('.list-group')
        .find('#' + tabId)
        .html(searchHtml);
      return;
    }
    $.each(searchData, function (i) {
      var li = '<a class="list-group-item" flowId="${id}">${name}</a>';
      searchHtml += templateEngine.render(li, this);
    });
    $('.list-group')
      .find('#' + tabId)
      .html(searchHtml);
  }

  // 拖拽事件
  function dragEvent() {
    $('#sortable').sortable({
      revert: true,
      items: 'li:not(.unsortable)'
    });
    $('#sortable').on('sortdeactivate', function (event, ui) {
      // 获取新页签顺序数组
      var newNavArr = [];
      $(this)
        .find('li')
        .each(function (i, n) {
          if (i != 0) {
            var newObj = new Object();
            newObj.name = $(n).find('a').html();
            newObj.id = $(n).find('a').data('id');
            newNavArr.push(newObj);
          }
        });
    });
  }

  // 绑定事件
  function bindEvent($container, opt) {
    $('.list-group', $container).on('click', 'a.list-group-item', function () {
      var flowDefId = $(this).attr('flowId');
      $('.bootbox-close-button', $container).trigger('click');
      var url = ctx + '/workflow/work/new/' + flowDefId;
      // 回调处理
      if ($.isFunction(opt.callback)) {
        var callbackContext = opt.callbackContext || this;
        opt.callback.call(callbackContext, {
          flowDefId: flowDefId,
          newWorkUrl: url
        });
      } else {
        // 默认发起流程
        var options = {};
        options.url = url;
        options.ui = opt.ui;
        options.size = 'large';
        appContext.openWindow(options);
        // 记录最近使用
        JDS.call({
          service: 'recentUseFacadeService.use',
          data: [flowDefId, 'WORKFLOW']
        });
      }
    });

    $('.wf-item')
      .off()
      .on('click', function () {
        var flowDefId = $(this).attr('data-flowId');
        $('.bootbox-close-button', $container).trigger('click');
        var url = ctx + '/workflow/work/new/' + flowDefId;
        // 回调处理
        if ($.isFunction(opt.callback)) {
          var callbackContext = opt.callbackContext || this;
          opt.callback.call(callbackContext, {
            flowDefId: flowDefId,
            newWorkUrl: url
          });
        } else {
          // 默认发起流程
          var options = {};
          options.url = url;
          options.ui = opt.ui;
          options.size = 'large';
          options.useUniqueName = false;
          appContext.openWindow(options);
          // 记录最近使用
          JDS.call({
            service: 'recentUseFacadeService.use',
            data: [flowDefId, 'WORKFLOW']
          });
        }
      });

    $('.search-wrap input', $container)
      .off()
      .on('keyup', function (e) {
        var $this = $(this);
        var keyword = $.trim($this.val());
        if (keyword) {
          $this.siblings('.clear-icon').show();
        } else {
          $this.siblings('.clear-icon').hide();
        }
        if (e.keyCode === 13) {
          $('.search-icon', $container).trigger('click');
        }
      });

    $('.search-icon', $container)
      .off()
      .on('click', function () {
        var $this = $(this);
        var keyword = $.trim($this.prev().val());
        $('.wf-all-tab a', $container).trigger('click');
        $('.wf-category-wrap').css('opacity', 0);
        searchWf(keyword);
        if (!keyword) {
          $('.new-work-flow-content .well-no-data-wrap').hide();
          $('.grid-item', $container).find('.drag-icon').show();
          $('.not-match', $container).show();
          $('.match-dom', $container).each(function () {
            var _$this = $(this);
            _$this.html(_$this.attr('data-text')).removeClass('match-dom');
          });
        } else {
          $('.grid-item', $container).find('.drag-icon').hide();
        }

        setTimeout(function () {
          var items = grid.getItems();
          var empty = true;
          $.each(items, function (i, item) {
            if (!item._visibility._isHidden) {
              empty = false;
              return false;
            }
          });
          //无匹配数据
          if (empty) {
            grid.refreshItems().layout();
            setTimeout(function () {
              $('.wf-category-wrap').css('opacity', 1);
            }, 400);
            $('.new-work-flow-content .well-no-data-wrap').show();
          } else {
            $('.new-work-flow-content .well-no-data-wrap').hide();
            grid.refreshItems().layout();
            setTimeout(function () {
              $('.wf-category-wrap').css('opacity', 1);
            }, 200);
          }
        }, 200);
      });

    $('.clear-icon', $container)
      .off()
      .on('click', function () {
        var $this = $(this);
        $this.hide().siblings('input').val('');
        $this.prev().trigger('click');
      });
  }

  function renderAllCategory(data, $dialog) {
    var categoryData = $.map(data, function (item) {
      if (item && item.id !== 'recent_use' && item.id !== 0) return item;
    });
    $.each(categoryData, function (i, category) {
      var $category = $('<div class="wf-category grid-item" data-categoryId="' + category.id + '"></div>');
      $category.data('categoryData', category);
      var $categoryTit = $(
        '<div class="category-tit" title="' +
        category.name +
        '">' +
        '<div class="tit-icon">' +
        '<i class="' +
        (category.iconSkin || 'iconfont icon-ptkj-fenlei2' + (category.iconStyle || ' text-primary')) +
        '" style="' +
        (category.iconStyle || ' ') +
        '"></i>' +
        '</div>' +
        '<div class="category-name" data-text="' +
        category.name +
        '">' +
        category.name +
        '</div>' +
        '<div class="drag-icon" title="拖动排序"><i class="iconfont icon-ptkj-tuodong"></i></div>' +
        '</div>'
      );
      var $categoryList = $('<ul class="category-list clear"></ul>');
      if (category.children.length) {
        $categoryTit.append('<div class="category-total">（' + category.children.length + '）</div>');
        $.each(category.children, function (i, item) {
          $categoryList.append(
            '<li class="wf-item" title="' +
            item.name +
            '" data-text="' +
            item.name +
            '" data-flowId="' +
            item.id +
            '">' +
            item.name +
            '</li>'
          );
        });
      }
      $category.append($categoryTit);
      $category.append($categoryList);
      $dialog.find('.wf-category-wrap').append($category);
    });

    var grid = new Muuri('.grid', {
      showDuration: 400,
      showEasing: 'ease',
      hideDuration: 400,
      hideEasing: 'ease',
      layoutDuration: 400,
      layoutOnInit: true,
      layoutOnResize: false,
      layoutEasing: 'cubic-bezier(0.625, 0.225, 0.100, 0.890)',
      sortData: {},
      dragEnabled: true,
      dragHandle: '.drag-icon',
      dragRelease: {
        duration: 400,
        easing: 'cubic-bezier(0.625, 0.225, 0.100, 0.890)',
        useDragContainer: true
      },
      dragPlaceholder: {
        enabled: true,
        createElement(item) {
          return item.getElement().cloneNode(true);
        }
      },
      dragAutoScroll: {
        targets: [window],
        sortDuringScroll: false,
        syncAfterScroll: false
      }
    });

    grid.on('dragEnd', function (item, event) {
      var categorys = grid.getItems();
      var categorysData = $.map(categorys, function (item) {
        return $(item._element).data('categoryData').id;
      });
      // 保存用户的流程分类排序配置信息
      JDS.call({
        async: false,
        service: 'appUserWidgetDefFacadeService.saveCurrentUserWidgetDefinition',
        data: ['newWorkDefinition', JSON.stringify(categorysData)],
        version: '',
        success: function (result) { }
      });
    });

    window.grid = grid;

    if (data[0].id !== 'recent_use') {
      $('.wf-history-tab', $dialog).hide();
      $('.wf-all-tab', $dialog).trigger('click').find('a').tab('show');
      grid.refreshItems().layout();
    }
  }

  function newWorkFlowContent(data) {
    var searchWrap =
      '<div class="search-wrap fr">' +
      '<input class="form-control" placeholder="搜索">' +
      '<div class="search-icon">' +
      '<i class="iconfont icon-ptkj-sousuochaxun"></i>' +
      '</div>' +
      '<div class="clear-icon">' +
      '<i class="iconfont icon-ptkj-dacha-xiao"></i>' +
      '</div>' +
      '</div>';
    var historyLi = '';
    if (data[0].id === 'recent_use') {
      $.each(data[0].children, function (i, item) {
        historyLi +=
          '<li class="wf-item" title="' +
          item.name +
          '" data-text="' +
          item.name +
          '" data-flowId="' +
          item.id +
          '">' +
          item.name +
          '</li>';
      });
    }
    var historyList = '<ul class="wf-history-list clear">' + historyLi + '</ul>';
    var tabWrap =
      '<ul class="nav nav-tabs wf-data-tab">' +
      '<li class="tab-noborder wf-history-tab active">' +
      '<a href="#history" data-toggle="tab">最近使用</a>' +
      '</li>' +
      '<li class="tab-noborder wf-all-tab">' +
      '<a href="#all" data-toggle="tab">全部</a>' +
      '</li>' +
      '</ul>' +
      '<div class="tab-content">' +
      '<div class="tab-pane subtab active" name="history" title="最近使用" id="history">' +
      historyList +
      '</div>' +
      '<div class="tab-pane subtab" name="all" title="全部" id="all">' +
      '<div class="wf-category-wrap wf-category-list grid"></div>' +
      '<div class="well-no-data-wrap"><div class="well-no-data"></div><span>无匹配数据！</span></div>' +
      '</div></div>';
    var changeMode = '<div class="wf-change-mode" data-type="list" title="列表视图"><i class="iconfont icon-wsbs-liebiaoshitu"></i></div>';
    var content = '<div class="wf-data-content">' + changeMode + tabWrap + '</div>';
    return '<div class="new-work-flow-content"><div class="new-wf-header clear">' + searchWrap + '</div>' + content + '</div>';
  }

  function resetSearchResult() {
    $('.new-work-flow-content .grid-item').find('.drag-icon').show();
    $('.new-work-flow-content .not-match').show();
    $('.new-work-flow-content .match-dom').each(function () {
      var _$this = $(this);
      _$this.html(_$this.attr('data-text')).removeClass('match-dom');
    });
  }

  function searchWf(keyword) {
    resetSearchResult();
    grid.filter(function (item) {
      var element = item.getElement();
      var $element = $(element);
      var categoryData = $element.data().categoryData;
      var categoeyMatch = false;
      if (categoryData.name.indexOf(keyword) > -1) {
        var $categoryName = $element.find('.category-name');
        replaceMatchKeywordText($categoryName, keyword);
        categoeyMatch = true;
      }
      var childrenMatch = false;
      $.each(categoryData.children, function (i, item) {
        var $wfItem = $element.find('.wf-item[data-flowid="' + item.id + '"]');
        if (item.name.indexOf(keyword) > -1) {
          replaceMatchKeywordText($wfItem, keyword);
          childrenMatch = true;
        } else {
          if (!categoeyMatch) {
            $wfItem.addClass('not-match').hide();
          }
        }
      });
      if (!keyword || categoeyMatch || childrenMatch) {
        return true;
      }
    });
  }

  function replaceMatchKeywordText($ele, keyword) {
    var html = $ele.attr('data-text');
    var re = new RegExp(keyword, 'g');
    $ele.addClass('match-dom').html(html.replace(re, '<span class="matchText">' + keyword + '</span>'));
  }

  var startNewWork = function (options) {
    var $container = $(options.ui.element);
    var content = '';
    var params = options.params || {};
    // 流程分类
    var categoryCode = params.categoryCode;
    var categoryCodes = StringUtils.isBlank(categoryCode) ? [] : categoryCode.split(';');
    for (var i = 0; i < categoryCodes.length; i++) {
      categoryCodes[i] = 'FLOW_CATEGORY_' + categoryCodes[i];
    }
    // 应用类型
    var flowApplyId = params.flowApplyId;
    var flowApplyIds = StringUtils.isBlank(flowApplyId) ? [] : flowApplyId.split(';');
    // 流程ID
    var flowDefIdsString = params.flowDefIds;
    var flowDefIds = StringUtils.isBlank(flowDefIdsString) ? [] : flowDefIdsString.split(';');

    var filterByCategoryCode = function (dataList) {
      return $.grep(dataList, function (category, idx) {
        if (categoryCodes.length <= 0 || $.inArray(category.data, categoryCodes) > -1) {
          category.children = $.grep(category.children, function (flow, idx) {
            return flowApplyIds.length <= 0 || $.inArray(flow.type, flowApplyIds) > -1;
          });
          category.children = $.grep(category.children, function (flow, idx) {
            return flowDefIds.length <= 0 || $.inArray(flow.id, flowDefIds) > -1;
          });
          return true;
        }
        return false;
      });
    };

    var customDefinitions = null;
    var customDefinitionsID = 'newWorkDefinition';
    JDS.call({
      async: false,
      service: 'appUserWidgetDefFacadeService.getCurrentUserWidgetDefintion',
      data: [customDefinitionsID],
      version: '',
      success: function (result) {
        if (result.data) {
          customDefinitions = JSON.parse(result.data);
          var tmpMap = {};
          var tmpArray = [];
          // 去重
          for (var i = 0; i < customDefinitions.length; i++) {
            if (tmpMap[customDefinitions[i]] == null) {
              tmpMap[customDefinitions[i]] = customDefinitions[i];
              tmpArray.push(customDefinitions[i]);
            }
          }
          customDefinitions = tmpArray;
        }
      }
    });

    var formatData;
    JDS.call({
      service: 'workflowNewWorkService.getFlowDefinitions',
      async: false,
      success: function (result) {
        var data = filterByCategoryCode(result.data);
        formatData = newData(data);
        if (customDefinitions && data.length > 0 && data[0].id == 'recent_use') {
          var newFormatData = [];
          var newCategory = [];
          newFormatData.push(formatData[0]);
          newFormatData.push(formatData[1]);
          //按自定义的顺序组建新的流程分类
          $.each(customDefinitions, function (i, item) {
            $.each(formatData, function (n, category) {
              if (item == category.id) {
                newFormatData.push(category);
                return false;
              }
            });
          });
          //新增的流程类别
          $.each(formatData, function (n, category) {
            if (customDefinitions.indexOf(category.id.toString()) < 0) {
              newCategory.push(category);
            }
          });
          formatData = newFormatData.concat(newCategory);
        }
        // content += searchToHtml();
        // content += getCagegoryData(data);
        content += newWorkFlowContent(formatData);
      }
    });

    // 空流程处理
    if (StringUtils.isBlank(content)) {
      content = '<div>没有您可以发起的流程！</div>';
    }

    var target = options.target;
    var targetWidgetId = options.targetWidgetId;
    var title = params.title || '发起流程';
    if (constant.TARGET_POSITION.TARGET_WIDGET != target || commons.StringUtils.isBlank(options.targetWidgetId)) {
      var $dialog = top.appModal.dialog({
        title: title,
        size: 'large',
        message: content,
        buttons: {
          cancel: {
            label: '取消',
            className: 'btn btn-default'
          }
        },
        shown: function () {
          window.setTimeout(function () {
            tabNavEvent();
            searchEvent(allDataArr);
            renderAllCategory(formatData, $dialog);
            bindEvent($dialog, options);
          }, 100);

          $('.wf-change-mode')
            .off()
            .on('click', function () {
              var $this = $(this);
              if ($this.attr('data-type') === 'list') {
                $this
                  .attr('data-type', 'block')
                  .attr('title', '卡片视图')
                  .find('i')
                  .removeClass('icon-wsbs-liebiaoshitu')
                  .addClass('icon-wsbs-fangkuaishitu');
                $('.wf-category-wrap').removeClass('wf-category-list').addClass('wf-category-block');
              } else {
                $this
                  .attr('data-type', 'list')
                  .attr('title', '列表视图')
                  .find('i')
                  .addClass('icon-wsbs-liebiaoshitu')
                  .removeClass('icon-wsbs-fangkuaishitu');
                $('.wf-category-wrap').addClass('wf-category-list').removeClass('wf-category-block');
              }
              $('.wf-category-wrap').css('opacity', 0);
              grid.refreshItems().layout();
              setTimeout(function () {
                $('.wf-category-wrap').css('opacity', 1);
              }, 100);
            });
          $('.tab-noborder')
            .off()
            .on('click', function () {
              if ($(this).hasClass('wf-all-tab')) {
                $('.wf-change-mode').show();
              } else {
                $('.wf-change-mode').hide();
              }
              setTimeout(function () {
                grid.refreshItems().layout();
                bindEvent($dialog, options);
              }, 0);
            });
        }
      });

      $('.tab-content', $dialog).slimScroll({
        height: '500px',
        wheelStep: navigator.userAgent.indexOf('Firefox') > -1 ? 1 : 10
      });
      $('.bootbox-body', $dialog).css('overflow-y', 'visible');
      bindEvent($dialog, options);
    } else {
      var $box = appContext.getWidgetRenderPlaceholder(targetWidgetId);
      if ($box.css('position') == 'static') {
        $remove_position = true;
        $box.addClass('position-relative');
      }
      var overlay = '<div class="box-widget"><div class="row">' + content + '</div></div>';
      $box.html(overlay);
      $('.box-widget', $box).slimScroll({
        height: '500px',
        wheelStep: navigator.userAgent.indexOf('Firefox') > -1 ? 1 : 10
      });

      window.setTimeout(function () {
        renderAllCategory(formatData, $box);

        bindEvent($box, options);
      }, 100);

      $('.wf-change-mode')
        .off()
        .on('click', function () {
          var $this = $(this);
          if ($this.attr('data-type') === 'list') {
            $this
              .attr('data-type', 'block')
              .attr('title', '卡片视图')
              .find('i')
              .removeClass('icon-wsbs-liebiaoshitu')
              .addClass('icon-wsbs-fangkuaishitu');
            $('.wf-category-wrap').removeClass('wf-category-list').addClass('wf-category-block');
          } else {
            $this
              .attr('data-type', 'list')
              .attr('title', '列表视图')
              .find('i')
              .addClass('icon-wsbs-liebiaoshitu')
              .removeClass('icon-wsbs-fangkuaishitu');
            $('.wf-category-wrap').addClass('wf-category-list').removeClass('wf-category-block');
          }
          $('.wf-category-wrap').css('opacity', 0);
          grid.refreshItems().layout();
          setTimeout(function () {
            $('.wf-category-wrap').css('opacity', 1);
          }, 100);
        });

      $('.tab-noborder')
        .off()
        .on('click', function () {
          if ($(this).hasClass('wf-all-tab')) {
            $('.wf-change-mode').show();
          } else {
            $('.wf-change-mode').hide();
          }
          setTimeout(function () {
            grid.refreshItems().layout();
            bindEvent($dialog, options);
          }, 0);
        });
    }
  };
  return startNewWork;
});
