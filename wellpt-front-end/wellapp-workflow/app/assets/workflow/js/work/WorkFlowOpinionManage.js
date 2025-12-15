define(['jquery', 'server', 'commons', 'constant', 'appContext', 'appModal'], function (
  $,
  server,
  commons,
  constant,
  appContext,
  appModal
) {
  var StringUtils = commons.StringUtils;
  var StringBuilder = commons.StringBuilder;
  var UUID = commons.UUID;
  var jds = server.JDS;
  var getCurrentUserOpinion2SignService = 'workV53Service.getCurrentUserOpinion2Sign';
  // 保存常用意见服务
  var saveFlowOpinionCategoryBeansService = 'flowOpinionService.saveFlowOpinionCategoryBeans';
  // 意见管理——增删改查
  var WorkFlowOpinionManage = function (opinionEditor, userOptions, workView) {
    this.opinionEditor = opinionEditor;
    this.userOptions = userOptions;
    this.workView = workView;
  };
  $.extend(WorkFlowOpinionManage.prototype, {
    // 打开意见管理
    open: function (callback) {
      var _self = this;
      // 常用意见
      var commonOpinions = _self.userOptions.commonOpinionCategory.opinions || [];
      var publicOpinions = _self.userOptions.publicOpinionCategory.opinions || [];
      var recentOpinions = _self.userOptions.recents;
      var dlgId = UUID.createUUID();
      var title = '意见管理';
      var dlgOptions = {
        title: title,
        dlgId: dlgId,
        commonOpinions: commonOpinions,
        publicOpinions: publicOpinions,
        recentOpinions: recentOpinions,
        templateId: 'pt-workflow-user-opinion-mgr',
        size: 'large',
        shown: function () {
          _self.getTipsStatus();
          _self.shown($('#' + dlgId));
          _self.bindEvents($('#' + dlgId));
        },
        buttons: {
          confirm: {
            label: '确定',
            className: 'well-btn w-btn-primary',
            callback: function () {
              var saveResult = false;
              _self.commonOpinionCategory = _self.collectCommonOpinionCategory($('#' + dlgId));
              jds.restfulPost({
                url: ctx + '/api/workflow/opinion/saveFlowOpinionCategories',
                data: { opinionCategories: [_self.commonOpinionCategory], deletedCategoryUuids: [] },
                async: false,
                success: function (result) {
                  saveResult = true;
                }
              });
              return saveResult;
            }
          },
          cancel: {
            label: '取消',
            className: 'btn-default'
          }
        },
        callback: function () {
          if ($.isFunction(callback)) {
            callback.call(this, _self.commonOpinionCategory, _self.userOptions.recents);
          }
        }
      };
      appModal.dialog(dlgOptions);
    },
    shown: function ($container) {
      var _self = this;
      $('.list-group', $container).slimScroll({
        height: '100%',
        wheelStep: navigator.userAgent.indexOf('Firefox') > -1 ? 1 : 10
      });
      $('#searchOpinion').on('blur', function () {
        var val = $(this).val();
        var listHtml = '';
        var items = _self.searchOpinion(val);
        if (items.length == 0) {
          listHtml += "<div class='search-no-opinion'>未搜索到相关数据</div>";
        } else {
          for (var i = 0; i < items.length; i++) {
            listHtml += '<a class="list-group-item" opinionUuid="' + items[i].uuid + '">' + items[i].content + '</a>';
          }
        }
        $('#opinion_list').find('.list-group').html(listHtml);
      });
      _self.syncOpinionListState($container);
    },
    syncOpinionListState: function ($container) {
      var _self = this;
      // 公共意见已选择的添加禁用样式
      var $publicItems = $('.public-opinion-list .list-group-item', $container);
      $.each($publicItems, function (i) {
        var $item = $(this);
        var opinionUuid = $item.attr('opinionUuid');
        var publicOpinion = _self.getPublicOpinion(opinionUuid);
        if (publicOpinion != null) {
          var opinionCode = publicOpinion.code;
          var $userItem = $(".list-group-item[opinioncode='" + opinionCode + "']", '.user-opinion-list');
          if ($userItem.length > 0) {
            $item.addClass('disabled');
          } else {
            $item.removeClass('disabled');
          }
        } else {
          $item.removeClass('disabled');
        }
      });
    },
    bindEvents: function ($container) {
      var _self = this;
      // 提示信息关闭
      $('.info-close', $container).on('click', function () {
        $(this).parent().hide();
        if ($(this).parents('.alert-info').attr('id') == 'recent_list_tip') {
          _self.closeTips();
        }
      });

      // 添加办理意见
      $("button[name='btn_opinion_add']", $container).on('click', function () {
        var $opinionText = $("textarea[name='opinion-text']", $container);
        var opinionText = $opinionText.val();
        if (StringUtils.isBlank(opinionText)) {
          $('.opinion-empty-alert', $container).removeClass('hide');
          $('.opinion-exceed-max-length-alert', $container).addClass('hide');
        } else if (opinionText.length > 1333) {
          $('.opinion-empty-alert', $container).addClass('hide');
          $('.opinion-exceed-max-length-alert', $container).removeClass('hide');
        } else {
          $('.opinion-empty-alert', $container).addClass('hide');
          $('.opinion-exceed-max-length-alert', $container).addClass('hide');
          var $itemIcons = _self._getItemIcons();
          var itemDom =
            "<a class='list-group-item initStyle' opinionCode='" +
            opinionText +
            "'><span class='group-item-text' title='" +
            opinionText +
            "'>" +
            opinionText +
            '</span></a>';
          var $item = $(itemDom).append($itemIcons);
          $('.user-opinion-list', $container).find('.list-group').append($item);
          _self._itemBlur($container);
          $opinionText.val('');
        }
      });

      // 办理意见鼠标经过显示删除按钮
      $('.user-opinion-list,.recent_opinion_list', $container).on('hover', '.list-group-item', function (e) {
        if (e.type == 'mouseleave') {
          $(this).find('i').parent().addClass('hide');
        } else {
          $(this).find('i').parent().removeClass('hide');
        }
      });
      // 上移常用意见
      $('.user-opinion-list', $container).on('click', 'i.icon-ptkj-shangyi', function () {
        var $item = $(this).parents('.list-group-item');
        $item.after($item.prev());
      });
      // 下移常用意见
      $('.user-opinion-list', $container).on('click', 'i.icon-ptkj-xiayi', function () {
        var $item = $(this).parents('.list-group-item');
        $item.before($item.next());
      });
      // 删除常用意见
      $('.user-opinion-list', $container).on('click', 'i.icon-ptkj-dacha', function () {
        $(this).parents('.list-group-item').remove();
        _self.syncOpinionListState($container);
      });
      // 编辑常用意见
      $('.user-opinion-list', $container).on('click', 'i.icon-ptkj-bianji', function () {
        $(this).parent().prev().attr('contenteditable', true).focus().addClass('isEditable');
        $(this).parents('.list-group-item').removeClass('initStyle').addClass('editing');
      });
      // 删除最近使用意见
      $('.recent_opinion_list', $container).on('click', 'i.icon-ptkj-dacha', function () {
        var $this = $(this);
        var userId = $.cookie('cookie.current.userId');
        var content = $(this).parent().prev('.group-item-text').html();
        $.ajax({
          type: 'delete',
          data: JSON.stringify({ content: content, userId: userId }),
          dataType: 'json',
          contentType: 'application/json',
          url: ctx + '/api/workflow/opinion/deleteRecentOpinion',
          success: function (result) {
            $this.parents('.list-group-item').remove();
            _self.getRecentOpinion($container);
            appModal.success('删除成功！');
          },
          error: function () {
            appModal.error('删除失败！');
            sendResult = false;
          }
        });
      });

      // 从公共意见库选择
      // $("button[name='btn_choose_public_opinion']", $container).on("click", function() {
      //     var $publicOptionList = $(".public-opinion-list", $container);
      //     if ($publicOptionList.is(":visible")) {
      //         $publicOptionList.addClass("hide");
      //     } else {
      //         $publicOptionList.removeClass("hide");
      //     }
      // });
      _self._itemBlur($container);

      // 公共意见库进入个人意见
      $('.public-opinion-list', $container).on('click', '.list-group-item', function () {
        var opinionUuid = $(this).attr('opinionUuid');
        var publicOpinion = _self.getPublicOpinion(opinionUuid);
        var opinionCode = publicOpinion.code;
        if ($("a[opinionCode='" + opinionCode + "']", $('.user-opinion-list', $container)).length > 0) {
          // 存在删除
          $("a[opinionCode='" + opinionCode + "']", $('.user-opinion-list', $container)).remove();
          $(this).removeClass('disabled');
        } else {
          $(this).addClass('disabled');
          // 不存在添加
          var itemDom =
            "<a class='list-group-item initStyle' opinionCode='" +
            opinionCode +
            "'><span class='group-item-text' title='" +
            publicOpinion.content +
            "'>" +
            publicOpinion.content +
            '</span></a>';
          var $itemIcons = _self._getItemIcons();
          var $item = $(itemDom).append($itemIcons);
          $('.user-opinion-list', $container).find('.list-group').append($item);
          _self._itemBlur($container);
        }
      });
    },
    getTipsStatus: function () {
      var _self = this;
      $.ajax({
        url: ctx + '/api/org/facade/getCurrentUserProperty',
        type: 'get',
        data: {
          propName: 'workflow.sign.process.opinionTip.state'
        },
        success: function (result) {
          if (result.data == 'close') {
            $('#recent_list_tip').hide();
          }
        }
      });
    },
    // 关闭提示信息
    closeTips: function () {
      JDS.call({
        service: 'orgApiFacade.saveUserProperty',
        data: [SpringSecurityUtils.getCurrentUserId(), 'workflow.sign.process.opinionTip.state', 'close'],
        mask: false,
        success: function (result) {}
      });
    },
    getRecentOpinion: function ($container) {
      var _self = this;
      var workData = _self.workView.getWorkData();
      var flowDefUuid = workData.flowDefUuid;
      var taskId = workData.taskId;
      $.get({
        url: ctx + '/api/workflow/work/getCurrentUserOpinion2Sign',
        data: { flowDefUuid: flowDefUuid, taskId: taskId },
        success: function (result) {
          var userOpinions = result.data;
          _self.userOptions.recents = userOpinions.recents;
          var html = '';
          $.each(userOpinions.recents, function (index, item) {
            html +=
              '<a class="list-group-item initStyle" opinionUuid="' +
              item.uuid +
              '" opinionCode="' +
              item.code +
              '" >' +
              '<span class="group-item-text" title="' +
              item.content +
              '">' +
              item.content +
              '</span>' +
              '<span class="hide">' +
              '<i class="iconfont icon-ptkj-dacha pull-right" title="删除"></i>' +
              '</span>' +
              '</a>';
          });
          $('.recent_opinion_list .list-group', $container).html(html);
        }
      });
    },
    _itemBlur: function ($container) {
      $('.group-item-text', $container).on('blur', function () {
        $(this).attr('contenteditable', false).removeClass('isEditable').attr('title', $(this).text());
        $(this).parent().addClass('initStyle').removeClass('editing');
      });
    },
    _getItemIcons: function () {
      var sb = new StringBuilder();
      sb.append("<span class='hide'>");
      sb.append('<i title="删除" class="iconfont icon-ptkj-dacha pull-right"></i>');
      sb.append('<i title="下移" class="iconfont icon-ptkj-xiayi pull-right"></i>');
      sb.append('<i title="上移" class="iconfont icon-ptkj-shangyi pull-right"></i>');
      sb.append('<i title="编辑" class="iconfont icon-ptkj-bianji pull-right"></i>');
      sb.append('</span>');
      return $(sb.toString());
    },
    collectCommonOpinionCategory: function ($container) {
      var _self = this;
      // 常用意见
      var commonOpinionCategory = _self.userOptions.commonOpinionCategory || [];
      var opinions = [];
      $('.user-opinion-list', $container)
        .find('.list-group-item')
        .each(function () {
          opinions.push({
            code: $(this).attr('opinionCode'),
            content: $(this).text()
          });
        });
      commonOpinionCategory.opinions = opinions;
      _self.userOptions.commonOpinionCategory = commonOpinionCategory;
      return commonOpinionCategory;
    },
    getPublicOpinion: function (opinionUuid) {
      var _self = this;
      var publicOpinions = _self.userOptions.publicOpinionCategory.opinions || [];
      for (var i = 0; i < publicOpinions.length; i++) {
        if (publicOpinions[i].uuid == opinionUuid) {
          return publicOpinions[i];
        }
      }
      return {};
    },
    searchOpinion: function (val) {
      var publicOpinions = this.userOptions.publicOpinionCategory.opinions || [];
      var items = [];
      if (val == '') {
        return publicOpinions;
      } else {
        for (var i = 0; i < publicOpinions.length; i++) {
          if (publicOpinions[i].content.indexOf(val) > -1) {
            items.push(publicOpinions[i]);
          }
        }
        return items;
      }
    }
  });
  return WorkFlowOpinionManage;
});
