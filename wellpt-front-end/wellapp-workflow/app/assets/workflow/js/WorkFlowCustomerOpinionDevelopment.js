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
  var currentUser = SpringSecurityUtils.getUserDetails();

  // 签署意见编辑器
  var WorkFlowCustomerOpinionDevelopment = function (workView, options) {
    OpinionEditor.call(this, workView, options);
  };
  commons.inherit(WorkFlowCustomerOpinionDevelopment, OpinionEditor, {
    // 初始化
    init: function () {
      var _self = this;
      _self.$editorPlaceholder = $(_self.editorPlaceholder);
      // 还原签署意见
      _self.restore();
      // 创建意见内容信息
      _self._createOpinionView();
    },
    // 显示
    show: function (action) {
      var _self = this;
      // 打开编辑框的动作
      _self.action = action;
      if (!$('.customer-opinion').is(':visible')) {
        $("button[name='B004011']", '.wf_operate').trigger('click');
      }
    },
    // 隐藏
    hide: function () {
      if ($("a[href='#wf_sign_opinion']", $('.tab-nav')).parent().hasClass('active')) {
        $("a[href='#wf_sign_opinion']", $('.tab-nav')).trigger('click');
      }
      $("a[href='#wf_sign_opinion']", $('.tab-nav')).parent().hide();
    },
    // 创建意见内容信息
    _createOpinionView: function () {
      var _self = this;
      this.hide(); // 隐藏左侧签署意见tab
      if (this.workView.isDraft() || this.workView.isTodo()) {
        // 草稿和待办的时候显示签署意见按钮
        $('.wf_operate').find("button[name='B004011']").show();
      }

      _self.getUsedAndCommonOpinion(); // 获取常用和最近使用意见
      console.log(_self.userOpinions); // 获取到的常用和最近使用意见

      this._bindEvents();
    },

    // 绑定事件
    _bindEvents: function () {
      var _self = this;
      var opinionHtml = getOpinionHtml(_self.userOpinions.opinions);
      //意见管理--全屏打开
      $("button[name='B004011']", '.wf_operate')
        .off()
        .on('click', function (e) {
          var $opinionDialog = appModal.dialog({
            className: 'default-bg full-screen-opinion customer-opinion',
            title: '签署意见',
            size: 'large',
            message: "<div id='sign_opinion_radio1'></div><textarea id='sign_opinion_text' cols='30' rows='30'></textarea>",
            shown: function () {
              if (_self.workView.isTodo()) {
                $('#sign_opinion_radio1', $opinionDialog).html(opinionHtml);
              }
              _self.$opinionDialog = $opinionDialog;

              $('#sign_opinion_text', $opinionDialog).val(_self.opinion.text);

              _self.collectOpinion($opinionDialog);

              $('#sign_opinion_text').live('keyup', function () {
                _self.collectOpinion($opinionDialog);
              });

              if (_self.opinion.value != '') {
                var $opinionBtn = $('#sign_opinion_radio1', $opinionDialog).find(
                  "button[name='btn_opinion'][value='" + _self.opinion.value + "']"
                );
                $opinionBtn.addClass('active');
                $opinionBtn.label = $opinionBtn.find('span').text();
              }

              $('#sign_opinion_radio1', $opinionDialog)
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
                    $('#sign_opinion_radio1', $opinionDialog)
                      .find("button[name='btn_opinion']")
                      .each(function (index, item) {
                        _self.btnOpinionChange($(item), false);
                      });
                    //选中当前项
                    _self.btnOpinionChange($(this), true);
                  }

                  $('#sign_opinion_text', $opinionDialog).val($('#sign_opinion_text').val());
                });
            },
            buttons: {
              ok: {
                label: '确定',
                className: 'well-btn w-btn-primary',
                callback: function () {
                  _self.collectOpinion($opinionDialog);
                }
              },
              cancel: {
                label: '取消',
                className: 'btn btn-default'
              }
            }
          });
        });
      //待办意见立场的dom
      function getOpinionHtml(opinions) {
        var html = '';
        $.each(opinions, function (oIndex, oItem) {
          html +=
            "<button name='btn_opinion' type='button' value='" +
            oItem.code +
            "' class='well-btn btn-primary-radio'><i class='iconfont'></i><span>" +
            oItem.content +
            '</span></button>';
        });
        return html;
      }
    },

    // 打开签署意见
    openToSignIfRequired: function (options) {
      var _self = this;
      var action = options.action;
      var methodName = 'isRequired' + StringUtils.capitalise(action) + 'Opinion';
      var isRequired = _self[methodName].call(_self);
      if (isRequired === false) {
        //&& !_self.isRequiredOpinionValue
        return false;
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
        _self.show(action);
        appModal.warning('请选择意见立场!');
        return true;
      }
      if (StringUtils.isBlank(text)) {
        _self.show(action);
        appModal.warning('请先签署意见!');
        return true;
      }
      return false;
    },

    // 收集签署的意见
    collectOpinion: function ($opinionDialog) {
      var _self = this;
      // 签署意见编辑器有初始化才收集返回的数据
      var text = _self.$opinionDialog ? _self.$opinionDialog.find('#sign_opinion_text').val() : '';
      if (StringUtils.isBlank(text)) {
        text = '';
      }
      _self.opinion.text = text;

      var workData = _self.workView.getWorkData();
      var records = workData.records;
      $.each(records, function (rIndex, rItem) {
        if (rItem.enableWysiwyg && rItem.includeOpinionTextVariable) {
          _self.dyformOpinionChange(rItem.field);
        }
      });

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
    dyformOpinionChange: function (field) {
      var _self = this;
      var $filedEL = $('[fieldname="' + field + '"]'); //表单显示位置
      if ($filedEL.length >= 1) {
        //该节点存在
        if ($filedEL.parents('td').first().find('.opinion_task_dyform_asyn').length < 1 && _self.opinion.text != '') {
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
        } else if (_self.opinion.text == '') {
          $filedEL.parents('td').first().find('.opinion_task_dyform_asyn').remove();
        }
        $('#opinion_task_dyform_asyn_' + field).html(_self.opinion.text || ' ');
      }
    }
  });

  return WorkFlowCustomerOpinionDevelopment;
});
