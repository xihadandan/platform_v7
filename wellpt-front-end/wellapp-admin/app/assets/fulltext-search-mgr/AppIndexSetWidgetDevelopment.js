define(['constant', 'commons', 'server', 'appContext', 'appModal', 'HtmlWidgetDevelopment'], function (
  constant,
  commons,
  server,
  appContext,
  appModal,
  HtmlWidgetDevelopment
) {
  var StringUtils = commons.StringUtils;
  var StringBuilder = commons.StringBuilder;
  var JDS = server.JDS;

  //  平台应用_公共资源_表单应用配置_编辑二开
  var AppIndexSetWidgetDevelopment = function () {
    HtmlWidgetDevelopment.apply(this, arguments);
  };
  // 接口方法
  commons.inherit(AppIndexSetWidgetDevelopment, HtmlWidgetDevelopment, {
    // 初始化回调
    init: function () {
      var _self = this;
      this.$element = this.widget.element;
      var bean = {
        name: null,
        titleExps: null,
        contentExps: null,
        creatorExps: null,
        createTimeField: null,
        modifyTimeField: null,
        url: null,
        type: 'workflow',
        uuid: null
      };

      var fieldData = [
        {
          text: '开始时间字段',
          id: 'startTime'
        },
        {
          text: '创建时间字段',
          id: 'createTime'
        },
        {
          text: '修改时间字段',
          id: 'modifyTime'
        },
        {
          text: '结束时间字段',
          id: 'endTime'
        },
        {
          text: '日期保留字段1',
          id: 'reservedDate1'
        },
        {
          text: '日期保留字段2',
          id: 'reservedDate2'
        }
      ];
      $('#createTimeField', this.$element).wellSelect({
        data: fieldData,
        valueField: 'createTimeField',
        searchable: false
      });
      $('#modifyTimeField', this.$element).wellSelect({
        data: fieldData,
        valueField: 'modifyTimeField',
        searchable: false
      });

      JDS.call({
        service: 'indexDocTemplateService.getByType',
        data: ['workflow'],
        version: '',
        success: function (res) {
          if (res.data) {
            bean = res.data;
            $('#index_set_form', this.$element).json2form(bean);
            $('#titleExps', this.$element).text(bean.titleExps);
            $('#contentExps', this.$element).text(bean.contentExps);
            $('#creatorExps', this.$element).text(bean.creatorExps);
            $('#createTimeField', this.$element).wellSelect('val', bean.createTimeField).trigger('change');
            $('#modifyTimeField', this.$element).wellSelect('val', bean.modifyTimeField).trigger('change');
          }
        }
      });

      $(this.$element)
        .off()
        .on('click', '.indexSetBtn', function () {
          var $self = $(this);
          var _html = get_title_exp();
          var $dialog = appModal.dialog({
            title: '索引设置',
            message: _html,
            size: 'large',
            height: 600,
            shown: function () {
              $('textarea', $dialog).val($self.prev().text());
              set_expressionSelect($dialog, 'defaultField', true, [
                '流程名称',
                '流程ID',
                '流程编号',
                '发起人姓名',
                '发起人所在部门名称',
                '发起人所在部门名称全路径',
                '年',
                '简年',
                '月',
                '日',
                '时',
                '分',
                '秒'
              ]);
              $('.choose-item', $dialog)
                .off()
                .on('click', function (e) {
                  var $this = $(this);
                  e.stopPropagation();
                  $this.toggleClass('well-select-visible');
                  $('.choose-item', $dialog).each(function () {
                    var _$this = $(this);
                    if (!_$this.is($this)) {
                      _$this.removeClass('well-select-visible');
                    }
                  });
                });
              $(document)
                .off()
                .on('click', function (e) {
                  if ($('.choose-item', $dialog)[0] === $(e.target).parents('.well-select')[0]) return;
                  $('.choose-item', $dialog).removeClass('well-select-visible');
                });

              $('.well-select-dropdown', $dialog)
                .off()
                .on('click', function (e) {
                  e.stopPropagation();
                });
              $('.well-select-item', $dialog)
                .off()
                .on('click', function (e) {
                  var $titleControl = $('#titleControl', $dialog)[0];
                  var value = $titleControl.value;
                  var start = $titleControl.selectionStart;
                  var value1 = value.substr(0, start);
                  var value2 = value.substr(start);
                  var finalValue = value1 + '${' + $(this).attr('data-value') + '}' + value2;
                  $('textarea', $dialog).val(finalValue);
                  $('.choose-item', $dialog).removeClass('well-select-visible');
                });

              $('.well-select-input', $dialog)
                .off()
                .on('input propertychange', function () {
                  var $that = $(this);
                  var keyword = $.trim($that.val()).toUpperCase();
                  var $wellSelect = $that.closest('.well-select');
                  var $wellSelectItem = $wellSelect.find('.well-select-item');
                  var $wellSelectNotFound = $wellSelect.find('.well-select-not-found');
                  var showNum = 0;
                  $wellSelectItem.each(function () {
                    var $this = $(this);
                    if ($this.data('value').toUpperCase().indexOf(keyword) > -1 || $this.data('name').toUpperCase().indexOf(keyword) > -1) {
                      $this.show();
                      showNum++;
                    } else {
                      $this.hide();
                    }
                  });
                  if (showNum) {
                    $wellSelectNotFound.hide();
                  } else {
                    $wellSelectNotFound.show();
                  }
                });
            },
            buttons: {
              ok: {
                label: '保存',
                className: 'well-btn w-btn-primary',
                callback: function () {
                  $self.prev().text($('textarea', $dialog).val());
                }
              },
              cancel: {
                label: '取消',
                className: 'btn-default'
              }
            }
          });
        });

      $('#indexSetSave', this.$element)
        .off()
        .on('click', function () {
          if ($('#name', this.$element).val() == '') {
            appModal.error('数据类型名称不能为空！');
            return false;
          }
          if ($('#titleExps', this.$element).text() == '') {
            appModal.error('索引标题不能为空！');
            return false;
          }
          if ($('#contentExps', this.$element).text() == '') {
            appModal.error('索引摘要不能为空！');
            return false;
          }
          if ($('#creatorExps', this.$element).text() == '') {
            appModal.error('创建人不能为空！');
            return false;
          }
          if ($('#createTimeField', this.$element).val() == '') {
            appModal.error('创建时间不能为空！');
            return false;
          }
          if ($('#modifyTimeField', this.$element).val() == '') {
            appModal.error('修改时间不能为空！');
            return false;
          }
          if ($('#url', this.$element).val() == '') {
            appModal.error('文档链接不能为空！');
            return false;
          }
          $('#index_set_form', this.$element).form2json(bean);
          bean.titleExps = $('#titleExps', this.$element).text();
          bean.contentExps = $('#contentExps', this.$element).text();
          bean.creatorExps = $('#creatorExps', this.$element).text();
          JDS.call({
            service: 'indexDocTemplateService.save',
            version: '',
            data: bean,
            success: function (res) {
              if (res.success) {
                appModal.success('保存成功！');
                _self.init();
              }
            }
          });
        });

      function get_title_exp() {
        return (
          '<div class="title_expression_wrap">' +
          '<div class="tip">' +
          '<span>' +
          '<i class="iconfont icon-ptkj-xinxiwenxintishi"></i>在下方编辑索引表达式，可插入流程内置变量文本。</span>' +
          '</div>' +
          '<div class="content">' +
          '<div class="choose-btns clear">' +
          '<div id="defaultField" class="choose-item well-select"><span>插入流程内置变量</span><i class="iconfont icon-ptkj-xianmiaojiantou-xia well-select-arrow"></i></div>' +
          '</div>' +
          '<textarea id="titleControl" class="form-control" rows="10"></textarea>' +
          '</div>' +
          '<div class="bootom-tip">样例：${流程名称}_${发起人姓名}(${发起年}${发起月}${发起日})</div>' +
          '</div>'
        );
      }

      function set_expressionSelect($dialog, id, showSearch, data) {
        $dialog
          .find('#' + id)
          .append(
            '<div class="well-select-dropdown" x-placement="bottom-start"' +
              '    style="position: absolute; left: 0; top: 34px; width: 300px;">' +
              '    <div class="well-select-search" style="display: ' +
              (showSearch ? 'block' : 'none') +
              ';"><input class="well-select-input" placeholder="搜索">' +
              '        <div class="search-icon"><i class="iconfont icon-ptkj-sousuochaxun"></i></div>' +
              '    </div>' +
              '    <ul class="well-select-not-found" style="display: none;">' +
              '        <li>无匹配数据</li>' +
              '    </ul>' +
              '    <ul class="well-select-dropdown-list"></ul>' +
              '</div>'
          );

        $.each(data, function (i, item) {
          $dialog
            .find('#defaultField .well-select-dropdown-list')
            .append('<li class="well-select-item" data-name="' + item + '" data-value="' + item + '"><span>' + item + '</span></li>');
        });
      }
    }
  });
  return AppIndexSetWidgetDevelopment;
});
