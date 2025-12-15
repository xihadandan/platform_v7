define(['jquery', 'server', 'commons', 'constant', 'appContext', 'appModal', 'OpinionEditor'], function (
  $,
  server,
  commons,
  constant,
  appContext,
  appModal,
  OpinionEditor
) {
  var StringUtils = commons.StringUtils;
  var StringBuilder = commons.StringBuilder;
  var summernoteTolbar = [
    ['style', ['bold', 'italic', 'underline', 'clear']],
    ['font', ['strikethrough', 'superscript', 'subscript']],
    ['fontsize', ['fontsize']],
    ['color', ['color']],
    ['para', ['ul', 'ol', 'paragraph']],
    ['height', ['height']]
  ];
  var jds = server.JDS;
  var getCurrentUserOpinion2SignService = 'workV53Service.getCurrentUserOpinion2Sign';
  var currentUser = SpringSecurityUtils.getUserDetails();

  // 签署意见编辑器
  var WorkFlowSidebarTabOpinionEditor = function (workView, options) {
    OpinionEditor.call(this, workView, options);
  };
  commons.inherit(WorkFlowSidebarTabOpinionEditor, OpinionEditor, {
    // 初始化
    init: function () {
      var _self = this;
      if (_self.inited) {
        return;
      }
      // window.beginInit = true; // 避免多次渲染情况
      _self.$editorPlaceholder = $(_self.editorPlaceholder);
      // 还原签署意见
      _self.restore();
      // 创建意见内容信息
      _self._createOpinionView();
      _self.inited = true;
    },
    // 显示
    show: function (action) {
      var _self = this;
      // 打开编辑框的动作
      _self.action = action;
      if (!_self.isEditorVisible()) {
        $("a[href='#wf_sign_opinion']", $('.tab-nav')).trigger('click');
      }
      _self.focusEditor();
    },
    // 隐藏
    hide: function () {
      var _self = this;
      if (_self.isEditorVisible()) {
        $("a[href='#wf_sign_opinion']", $('.tab-nav')).trigger('click');
      }
    },
    // 创建意见内容信息
    _createOpinionView: function () {
      var _self = this;
      var workData = _self.workView.getWorkData();
      var flowDefUuid = workData.flowDefUuid;
      var taskId = workData.taskId;
      localStorage.removeItem('afterParseForm');
      $.ajax({
        url: ctx + '/proxy/api/workflow/work/getCurrentUserOpinion2Sign/' + flowDefUuid + '/' + taskId,
        type: 'GET',
        // async: false,
        // data: {
        //   flowDefUuid: flowDefUuid,
        //   taskId: taskId
        // },
        dataType: 'json',
        success: function (result) {
          var userOpinions = result.data;
          _self.userOpinions = userOpinions;
          _self.isRequiredOpinionValue =
            _self.userOpinions && _self.userOpinions.enableOpinionPosition && _self.userOpinions.requiredOpinionPosition;
          _self.fillCommonOpinionCategory(userOpinions);
          var templateEngine = appContext.getJavaScriptTemplateEngine();
          var text = templateEngine.renderById('pt-workflow-sidebar-tab-user-opinions', userOpinions);
          _self.$editorPlaceholder.html(text);
          _self.$editorPlaceholder.find('.list-group').each(function () {
            $(this).slimScroll({
              height: '100%',
              wheelStep: navigator.userAgent.indexOf('Firefox') > -1 ? 1 : 10
            });
          });
          _self.$editor = _self.$editorPlaceholder.find("textarea[name='sign_opinion_text']");
          if (StringUtils.isNotBlank(_self.opinion.value) && _self.$editorPlaceholder.find('.sign_opinion_radio').length > 0) {
            var $opinionBtn = _self.$editorPlaceholder
              .find('.sign_opinion_radio')
              .find("button[name='btn_opinion'][value='" + _self.opinion.value + "']");
            $opinionBtn.addClass('active');
            _self.opinion.label = $opinionBtn.find('span').text();
          }
          if (StringUtils.isNotBlank(_self.opinion.text)) {
            //记录初始签署意见值
            // _self.opinion.label = '';
            // _self.opinion.value = '';
            _self.workView.initSignValue = _self.opinion.text;
            _self.$editor.val(_self.opinion.text);
            _self.collectOpinion();
          }
          // 草稿或待办才显示确认提交按钮
          if (!(_self.workView.isDraft() || _self.workView.isTodo())) {
            _self.$editorPlaceholder.find("button[name='btn_ok']").hide();
          }
          if (!_self.workView.isTodo()) {
            _self.$editorPlaceholder.find('.sign_opinion_radio').hide();
          }
          _self.focusEditor();
          // 绑定事件
          _self._bindEvents();

          // 需要打开签署意见
          if (_self.requiredOpenToSign === true && _self.openToSignOptions) {
            _self.openToSignIfRequired(_self.openToSignOptions);
          }
        }
      });
    },
    // 填充常用意见
    fillCommonOpinionCategory: function (userOpinions) {
      var userOpinionCategories = userOpinions.userOpinionCategories || [];
      var commonOpinionCategory = null;
      $.each(userOpinionCategories, function (i, userOpinionCategory) {
        if (userOpinionCategory.id == '001') {
          commonOpinionCategory = userOpinionCategory;
        }
      });
      userOpinions.commonOpinions = commonOpinionCategory.opinions;
      userOpinions.commonOpinionCategory = commonOpinionCategory;
    },
    // 绑定事件
    _bindEvents: function () {
      var _self = this;
      var $signOpinionRadio = _self.$editorPlaceholder.find('.sign_opinion_radio:visible');
      if ($signOpinionRadio.length > 0) {
        var rHeight = $signOpinionRadio.height();
        _self.$editor.css({
          height: 160 - rHeight + 'px'
        });
      }
      // 意见框内容变更
      _self.$editor.on('keyup', function () {
        _self.collectOpinion();
      });
      _self.$editor.on('input', function () {
        _self.collectOpinion();
      });
      _self.$editor.on('blur', function () {
        _self.collectOpinion();
      });
      // 最近使用
      _self.$editorPlaceholder.find('.opinion-recent').on('click', '.list-group>.list-group-item', function () {
        var selectedOpinion = StringUtils.trim($(this).find('.opinion-recent-item-text').text());
        _self.$editor.val(StringUtils.trim(_self.$editor.val()) + StringUtils.trim(selectedOpinion));
        _self.collectOpinion();
      });
      // 公共意见
      _self.$editorPlaceholder.find('.opinion-common').on('click', '.list-group>.list-group-item', function () {
        var selectedOpinion = StringUtils.trim($(this).find('.opinion-common-item-text').text());
        _self.$editor.val(StringUtils.trim(_self.$editor.val()) + StringUtils.trim(selectedOpinion));
        _self.collectOpinion();
      });
      // 最近使用、公共意见签署并提交显示与隐藏
      $('.opinion-recent, .opinion-common', _self.$editorPlaceholder).on('hover', '.list-group-item', function (e) {
        if (e.type == 'mouseleave') {
          $(this).find('button').addClass('hide');
        } else {
          $(this).find('button').removeClass('hide');
        }
      });
      // 最近使用、公共意见签署并提交
      $('.opinion-recent, .opinion-common', _self.$editorPlaceholder).on('click', 'button', function (e) {
        e.stopPropagation();
        var text = StringUtils.trim($(this).parent().find('.item-text').text());
        _self.$editor.val(text);
        _self.sign({
          label: _self.opinion.label,
          value: _self.opinion.value,
          text: text
        });
        _self.workView.currentEvent = null;
        _self.collectOpinion();
        _self.workView.submit();
      });
      // 意见管理
      _self.$editorPlaceholder.find('span.wf-opinion-mgr').on('click', function (e) {
        appContext.require(['WorkFlowOpinionManage'], function (WorkFlowOpinionManage) {
          var opinionManage = new WorkFlowOpinionManage(_self, _self.userOpinions, _self.workView);
          opinionManage.open(function (commonOpinionCategory, recentlyOpinions) {
            if (commonOpinionCategory && commonOpinionCategory.opinions) {
              // 更新常用意见列表
              _self._updateCommonOpinions(commonOpinionCategory.opinions, 'opinion-common');
            }
            if (recentlyOpinions) {
              _self._updateCommonOpinions(recentlyOpinions, 'opinion-recent');
            }
          });
        });
        e.stopPropagation();
      });
      //意见管理--全屏打开
      _self.$editorPlaceholder.on('click', '.full-open', function (e) {
        var opinionVal = $('.sign-opinion-container').find('textarea').val();
        var radios = $('.sign-opinion-container').find('.sign_opinion_radio').html();
        var $opinionDialog = appModal.dialog({
          className: 'default-bg full-screen-opinion',
          title: '签署意见',
          size: 'large',
          ignoreMessage:
            "<span class='modal-ignore full-close'><span>点击这里可退出全屏编辑<span class='close-full-tips'>知道了</span></span></div>",
          ignore: true,
          tipsCode: 'closeFull1',
          ignoreMount: '.modal-content',
          ignoreType: '2',
          message:
            "<div id='sign_opinion_radio1'></div><textarea id='sign_opinion_text1' cols='30' rows='30'></textarea></div><span id='sign_opinion_text_count1' style='display:none;float: right;'>1000</span>",
          buttons: {},
          closeIcon: 'icon-ptkj-tuichuquanping',
          shown: function () {
            $('#sign_opinion_radio1').html(radios);
            $('#sign_opinion_text1').val(opinionVal);
            if (!_self.workView.isTodo()) {
              $('#sign_opinion_radio1', $opinionDialog).hide();
            }
            _self.collectOpinion();
            $('#sign_opinion_text1').live('keyup', function () {
              $('.sign-opinion-container').find("textarea[name='sign_opinion_text']").val($(this).val());
              _self.collectOpinion();
            });
            $('.icon-ptkj-tuichuquanping').attr('title', '退出全屏');
            // 意见立场
            $('#sign_opinion_radio1')
              .off()
              .on('click', "button[name='btn_opinion']", function (e) {
                var value = $(this).val();
                var text = $(this).find('span').text();
                var active = $(this).hasClass('active');
                if (active) {
                  _self.btnOpinionChange($(this), false);
                  _self.opinion.value = '';
                  _self.opinion.label = '';
                } else {
                  _self.opinion.value = value;
                  _self.opinion.label = text;
                  //清除所有项
                  $('#sign_opinion_radio1')
                    .find("button[name='btn_opinion']")
                    .each(function (index, item) {
                      _self.btnOpinionChange($(item), false);
                    });
                  //选中当前项
                  _self.btnOpinionChange($(this), true);
                  $('.sign-opinion-container').find("textarea[name='sign_opinion_text']").val($('#sign_opinion_text1').val());
                  $('#sign_opinion_text1').val($('#sign_opinion_text1').val());
                }
                //清除所有项(原页面)
                _self.$editorPlaceholder.find("button[name='btn_opinion']").each(function (index, item) {
                  var active1 = $(item).hasClass('active');
                  if (active1) {
                    _self.btnOpinionChange($(item), false);
                  } else {
                    var value1 = $(item).val();
                    if (value1 == value) {
                      //选中当前项(原页面)
                      _self.btnOpinionChange($(item), true);
                    } else {
                      _self.btnOpinionChange($(item), false);
                    }
                  }
                });
                _self.collectOpinion();
              });
          }
        });
      });

      // 确定
      _self.$editorPlaceholder.on('click', "button[name='btn_ok']", function (e) {
        _self.collectOpinion();
        if (_self.isRequiredSubmitOpinion() && StringUtils.isBlank(_self.opinion.text)) {
          appModal.warning('请先签署意见!', function () {
            _self.show(_self.action);
          });
        } else {
          // 执行提交/转办/会签等操作
          if (StringUtils.isNotBlank(_self.action) && $.isFunction(_self.workView[_self.action])) {
            _self.workView[_self.action]();
          } else {
            // _self.hide();
            _self.workView.currentEvent = null;
            _self.workView.submit();
          }
        }
      });
      // 意见立场
      _self.$editorPlaceholder.off('click', "button[name='btn_opinion']").on('click', "button[name='btn_opinion']", function (e) {
        var value = $(this).val();
        var text = $(this).find('span').text();
        var active = $(this).hasClass('active');
        if (active) {
          _self.opinion.value = '';
          _self.opinion.label = '';
          _self.btnOpinionChange($(this), false);
        } else {
          _self.opinion.value = value;
          _self.opinion.label = text;
          //清除所有项
          _self.$editorPlaceholder.find("button[name='btn_opinion']").each(function (index, item) {
            _self.btnOpinionChange($(item), false);
          });
          //选中当前项
          _self.btnOpinionChange($(this), true);
          $('.sign-opinion-container').find("textarea[name='sign_opinion_text']").val($('textarea[name="sign_opinion_text"]').val());
        }

        // var selectedOpinion = text;
        // _self.opinion.text += selectedOpinion;
        // _self.$editor.val(StringUtils.trim(_self.$editor.val()) + StringUtils.trim(selectedOpinion));
        // // 执行提交/转办/会签等操作
        // if (StringUtils.isNotBlank(_self.action) && $.isFunction(_self.workView[_self.action])) {
        //   _self.workView[_self.action]();
        // }
        _self.collectOpinion();
      });

      _self._getWorkFlowLink();
    },
    _getWorkFlowLink: function () {
      if (window._getWorkFlowLink === true) {
        return;
      }
      window._getWorkFlowLink = true;
      var _self = this;
      var workData = _self.workView.getWorkData();
      var taskInstUuid = workData.taskInstUuid;
      var flowDef = workData.flowDefId;

      $.ajax({
        dataType: 'json',
        type: 'get',
        contentType: 'application/json',
        url: contextPath + '/api/workflow/work/getTaskProcess',
        data: {
          taskInstUuid: taskInstUuid,
          flowDefId: flowDef
        },
        success: function (result) {
          var flowLink = result.data;
          var html = "<div class='work-flow-link clearfix'>";
          if (flowLink.previous) {
            html +=
              "<div class='work-flow-link-item'>" +
              "<div class='work-flow-link-content'>" +
              flowLink.previous.taskName +
              '</div>' +
              "<div class='work-flow-link-member mPrevious'>" +
              flowLink.previous.taskName +
              "<br><span class='doLabel'>办理人：</span><span class='doUser'>" +
              flowLink.previous.assignee.split(';').join('、') +
              '</span></div>' +
              '</div>';
          }
          if (flowLink.current) {
            html +=
              "<div class='work-flow-link-item work-flow-link-current'>" +
              "<div class='work-flow-link-content'>" +
              flowLink.current.taskName +
              '</div>';

            if (flowLink.current.taskName != '结束') {
              var curClassName = flowLink.previous ? 'mCurrent' : 'mPrevious';
              html += "<div class='work-flow-link-member " + curClassName + "'>" + flowLink.current.taskName + '<br>';
              if (flowLink.current.todoUserNames && flowLink.current.todoUserNames.length > 0) {
                html +=
                  "<div><span class='doLabel'>待办办理人：</span><span class='work-flow-link-members doUser'>" +
                  flowLink.current.todoUserNames.join('、') +
                  '</span></div>';
              }
              if (flowLink.current.doneUserNames && flowLink.current.doneUserNames.length > 0) {
                html +=
                  "<div><span class='doLabel'>已办办理人：</span><span class='work-flow-link-members doUser'>" +
                  flowLink.current.doneUserNames.join('、') +
                  '</span></div>';
              }
            }

            html += '</div>' + '</div>';
          }
          if (flowLink.next) {
            html += "<div class='work-flow-link-item'>" + "<div class='work-flow-link-content'>" + flowLink.next.taskName + '</div>';

            if (flowLink.next.taskName != '结束') {
              var nextClassName = flowLink.previous ? 'mNext' : 'mCurrent';
              html +=
                "<div class='work-flow-link-member " +
                nextClassName +
                "'>" +
                flowLink.next.taskName +
                '<br>' +
                "<span class='doLabel'>办理人：</span><span class='doUser'>" +
                flowLink.next.assignee.split(';').join('、') +
                '</span></div>';
            }
            html += '</div>';
          }
          html += '</div>';
          $('.wf-opinion', _self.$editorPlaceholder).prepend(html);
        }
      });
    },
    // 更新常用意见列表
    _updateCommonOpinions: function (opinions, $list) {
      var _self = this;
      var $commonListGroup = _self.$editorPlaceholder.find('.' + $list + ' .list-group');
      $commonListGroup.html('');
      var item = '';
      $.each(opinions, function (i, opinion) {
        item +=
          '<a class="list-group-item"><span class="item-text ' +
          $list +
          '-item-text" title="' +
          opinion.content +
          '">' +
          opinion.content +
          '</span><button type="button" class="sign-btn sign-with-submit pull-right hide">签署并提交</button></a>';
      });
      $commonListGroup.html(item);
    },
    // 打开签署意见
    openToSignIfRequired: function (options) {
      var _self = this;
      var action = options.action;
      var methodName = 'isRequired' + StringUtils.capitalise(action) + 'Opinion';
      var isRequired = _self[methodName].call(_self);
      if (_self.userOpinions == null) {
        _self.requiredOpenToSign = true;
        _self.openToSignOptions = options;
        if (!isRequired) {
          return false;
        } else {
          return true;
        }
      }

      if (isRequired === false) {
        return false;
      }
      // else if(!_self.isRequiredOpinionValue && !( action == 'transfer' || action == 'transfer' || action == 'remind')) {}
      if (_self.$editorPlaceholder.is(':visible')) {
        _self.collectOpinion();
      }
      var opinion = _self.opinion;
      var value = opinion.value;
      var text = opinion.text;
      if (
        _self.isRequiredOpinionValue &&
        StringUtils.isBlank(value) &&
        _self.workView.isTodo() &&
        (action == 'submit' || action == 'rollback' || action == 'directRollback')
      ) {
        appModal.warning('请先选择意见立场!');
        return true;
      }
      if (action != 'cancel' && StringUtils.isBlank(text)) {
        _self.show(action);
        appModal.warning('请先签署意见!', function () {
          _self.show(action);
        });
        return true;
      }
      return false;
    },
    // 判断签署意见编辑器是否可见
    isEditorVisible: function () {
      var _self = this;
      if (_self.$editor) {
        return _self.$editor.is(':visible');
      }
      return false;
    },
    // 编辑意见编辑器获取焦点
    focusEditor: function () {
      var _self = this;
      if (_self.$editor) {
        _self.$editor.focus();
      }
    },
    // 收集签署的意见
    collectOpinion: function () {
      var _self = this;
      // 签署意见编辑器有初始化才收集返回的数据
      // var html = _self._getEditorHtml();
      // var text = _extractSnapshotText(html);
      if (_self.$editor) {
        var text = _self.$editor.val();
        if (StringUtils.isBlank(text) && _self.opinion.text == '') {
          text = '';
        }
        if (text.length < 500) {
          $('#sign_opinion_text_count').hide();
          $('#sign_opinion_text_count1').hide();
        }
        if (text.length >= 500) {
          $('#sign_opinion_text_count')
            .text(text.length + '/1000')
            .show();
          $('#sign_opinion_text_count1')
            .text(text.length + '/1000')
            .show();
        }
        if (text.length > 1000) {
          appModal.warning('当前文本内容超过最大限制字符数量，已截断超出的字符！');
          text = text.substring(0, 1000);
          _self.$editor.val(text);
          $('#sign_opinion_text_count')
            .text(text.length + '/1000')
            .show();
          $('#sign_opinion_text_count1')
            .text(text.length + '/1000')
            .show();
          $('#sign_opinion_text1').val(text);
        }
        _self.opinion.text = text;
        _self.checkRecord();
      }
      return _self.opinion;
    },
    btnOpinionChange: function ($btn, isActive) {
      var _self = this;
      var color = $btn.attr('data-color'); //按钮颜色
      if (isActive) {
        //选中
        $($btn).addClass('active');
        if (color) {
          $($btn).css({
            background: _self.hexToRgba(color, 0.15),
            border: '1px solid ' + color
          });
          $($btn).find('i').css({
            color: color
          });
        }
      } else {
        //未选中
        $($btn).removeClass('active');
        if (color) {
          $($btn).removeAttr('style');
          $($btn).find('i').removeAttr('style');
        }
      }
    },
    //hex16进制颜色与rgb互转
    hexToRgba: function (hex, opacity) {
      return (
        'rgba(' +
        parseInt('0x' + hex.slice(1, 3)) +
        ',' +
        parseInt('0x' + hex.slice(3, 5)) +
        ',' +
        parseInt('0x' + hex.slice(5, 7)) +
        ',' +
        opacity +
        ')'
      );
    },
    //意见即时显示
    dyformOpinionChange: function (field, assembler) {
      var _self = this;
      var $filedEL = $('[fieldname="' + field + '"]'); //表单显示位置
      if ($filedEL.length >= 1) {
        //该节点存在
        if ($filedEL.parents('td').first().find('.opinion_task_dyform_asyn').length < 1 && _self.opinion.text != '') {
          if (assembler === 'descTaskFormOpinionAssembler') {
            //无兄弟节点，则创建一个
            $filedEL
              .before(
                '<div class="opinion_task_dyform_asyn"><span class="opinion_task_dyform_asyn_name">' +
                currentUser.userName +
                ' （我）</span><div id="opinion_task_dyform_asyn_' +
                field +
                '" class="opinion_task_dyform_asyn_field"></div></div>'
              );
          } else {
            // defaultTaskFormOpinionAssembler
            //无兄弟节点，则创建一个
            $filedEL
              .parents('td')
              .first()
              .append(
                '<div class="opinion_task_dyform_asyn"><span class="opinion_task_dyform_asyn_name">' +
                currentUser.userName +
                ' （我）</span><div id="opinion_task_dyform_asyn_' +
                field +
                '" class="opinion_task_dyform_asyn_field"></div></div>'
              );
          }
        } else if (_self.opinion.text == '') {
          $filedEL.parents('td').first().find('.opinion_task_dyform_asyn').remove();
        }
        $('#opinion_task_dyform_asyn_' + field).html(_self.opinion.text || ' ');
      }
    },
    checkRecord: function () {
      var _self = this;
      var afterParseForm = localStorage.getItem('afterParseForm');
      if (afterParseForm != 'true') {
        return false;
      }
      var workData = _self.workView.getWorkData();
      var records = workData.records;
      $.each(records, function (rIndex, rItem) {
        if (rItem.enableWysiwyg && rItem.includeOpinionTextVariable) {
          if (rItem.enablePreCondition && rItem.recordConditions) {
            var opinion = _self.opinion;
            var dyFormData = null;
            _self.workView.getDyformData(
              function (dyformData) {
                dyFormData = dyformData;
              },
              function () {
              }
            );
            var data = {
              dyFormData: dyFormData,
              flowInstUuid: workData.flowInstUuid,
              opinionLabel: opinion.label,
              opinionText: opinion.text,
              opinionValue: opinion.value,
              record: rItem,
              taskInstUuid: workData.taskInstUuid
            };
            $.ajax({
              type: 'post',
              url: contextPath + '/api/workflow/work/checkRecordPreCondition',
              dataType: 'json',
              data: JSON.stringify(data),
              contentType: 'application/json',
              success: function (res) {
                if (res.data) {
                  _self.dyformOpinionChange(rItem.field, rItem.assembler);
                }
              }
            });
          } else {
            _self.dyformOpinionChange(rItem.field, rItem.assembler);
          }
        }
      });
    }
  });

  return WorkFlowSidebarTabOpinionEditor;
});
