define([
  'appContext',
  'constant',
  'design_commons',
  'commons',
  'server',
  'wSelect2',
  'comboTree',
  'formBuilder',
  'HtmlWidgetDevelopment'
], function (appContext, constant, designCommons, commons, server, wSelect2, comboTree, formBuilder, HtmlWidgetDevelopment) {
  var StringUtils = commons.StringUtils;
  var Browser = commons.Browser;
  // 页面组件二开基础
  var AppMsgFormatWidgetDevelopment = function () {
    HtmlWidgetDevelopment.apply(this, arguments);
  };
  // 接口方法
  commons.inherit(AppMsgFormatWidgetDevelopment, HtmlWidgetDevelopment, {
    bean: {
      uuid: null,
      name: null,
      id: null,
      code: null,
      classifyName: '',
      classifyUuid: '',
      category: null,
      moduleId: null,
      type: null,
      systemUnitId: null,
      sendWays: null,
      sendTime: null,
      scheduleTime: null,
      mappingRule: null,
      messageEvent: null,
      messageEventText: null,
      messageInteface: null,
      messageIntefaceText: null,
      isOnlinePopup: null,
      showViewpoint: null,
      viewpointY: null,
      viewpointN: null,
      viewpointNone: null,
      askForSchedule: null,
      foregroundEvent: null,
      foregroundEventText: null,
      backgroundEvent: null,
      backgroundEventText: null,
      onlineSubject: null,
      onlineBody: null,
      relatedTitle: null,
      relatedUrl: null,
      onlineAttach: null,
      smsBody: null,
      emailSubject: null,
      emailBody: null,
      emailAttach: null,
      scheduleTitle: null,
      scheduleDates: null,
      scheduleDatee: null,
      scheduleAddress: null,
      reminderTime: null,
      repeatType: null,
      scheduleBody: null,
      srcTitle: null,
      srcAddress: null,
      webServiceUrl: null,
      usernameKey: null,
      usernameValue: null,
      passwordKey: null,
      passwordValue: null,
      tenantidKey: null,
      tenantidValue: null,
      children: [],
      changedChildren: [],
      deletedChildren: [],
      callbackJson: '',
      reminderType: 1,
      popupPosition: null,
      popupSize: 0,
      popupWidth: '',
      popupHeight: '',
      displayMask: 0,
      autoTimeCloseWin: null,
      dtMessageType: null,
      dtJumpType: null,
      dtTitle: null,
      dtBody: null,
      dtUri: null,
      dtUriTitle: null,
      dtBtnOrientation: null,
      dtBtnJsonList: []
    },

    // 组件初始化
    init: function () {
      var _this = this;

      _this.initStyle();
      _this.initCategorySelector();
      _this.initPopupPositionSelector();
      _this.initModuleSelector();
      _this.initMessageLinkTable();
      _this.initMessageEventsTable();

      // 初始化表单数值
      $('#msg_format_form').json2form(_this.bean);
      var moduleId = localStorage.getItem('moduleId') ? localStorage.getItem('moduleId') : '';
      var src = parent.$('.modal').find('iframe').attr('src');
      var id = decodeURIComponent(src.indexOf('&id=') > -1 ? src.split('&id=')[1] : '');

      if (id != '') {
        // 编辑模式
        _this.fillExistData(id);

        if (moduleId != '') {
          $('#moduleId').val(moduleId);
        }
      } else {
        // 新增模式
        $.common.idGenerator.generate('#id', 'MSG_');

        var remindWaysElements = [
          '#sendWay_online',
          '#type_system',
          '#sendTime_in_time',
          ' #countBadge',
          '#popupDefault',
          '#msgTypeActionCard',
          '#jumpTypeSingle',
          '#arrangementVertial'
        ];
        $(remindWaysElements.join(',')).attr('checked', true);
        $('#popupPosition').val(1).trigger('change');
        $('#autoTimeCloseWin').val(1);
        $('#showTimer').data('value', 1).addClass('active');

        _this.initMessageInterfaceTextComboTree();
        _this.initMessageEventTextComboTree();

        if (moduleId) {
          $('#moduleId').wSelect2('val', moduleId).wSelect2('readonly', true);
        }

        this.initMsgEventTableData();
      }

      this.bindEvents();
    },

    // 样式覆盖
    initStyle: function () {
      $('#msg_format_form').parents('body').css({
        background: '#fff'
      });

      $('#msg_format_form').parents('.container-fluid').css({
        background: '#fff'
      });

      $('.single-jump .fixed-table-body').css('overflow-x', 'hidden');
    },

    // 编辑时，填充数据
    fillExistData: function (id) {
      var _this = this;

      JDS.call({
        service: 'messageTemplateService.getBeanById',
        data: [id],
        async: true,
        version: '',
        success: function (result) {
          if (result.success) {
            _this.bean = result.data;
            var bean = _this.bean;

            $('#msg_format_form').json2form(bean);
            $('#id').prop('readonly', 'readonly');
            $('#moduleId').prop('readonly', bean.moduleId != null);
            $('#moduleId').trigger('change');
            $("input[name='type'][value='" + bean.type + "']").attr('checked', true);
            $("input[name='sendTime'][value='" + bean.sendTime + "']")
              .attr('checked', true)
              .trigger('change');

            if (bean.sendTime == 'SCHEDULE_TIME') {
              $('.tr_time').show();
            }

            $("input[name='reminderType'][value='" + bean.reminderType + "']")
              .attr('checked', true)
              .trigger('change');

            $("input[name='popupSize'][value=" + bean.popupSize + ']')
              .attr('checked', true)
              .trigger('change');

            $('#classifyName').val(bean.classifyName).trigger('change');
            $('#popupPosition').val(bean.popupPosition).trigger('change');
            $('#showMask').data('value', bean.displayMask).trigger('change');
            $('#showTimer').data('value', bean.autoTimeCloseWin).trigger('change');
            _this.initMessageInterfaceTextComboTree();
            _this.initMessageEventTextComboTree();
            $('#messageIntefaceText').trigger('change');
            $('#messageEventText').trigger('change');
            if (bean.displayMask == 1) {
              $('#showMask').addClass('active');
            } else {
              $('#showMask').removeClass('active');
            }
            if (bean.autoTimeCloseWin == 0) {
              $('#showTimer').removeClass('active');
            } else {
              $('#showTimer').addClass('active');
            }
            if ($('#sendWay_inteface').prop('checked') == true) {
              $('.tr_interface').show();
            }
            $("input[name='dtJumpType'][value=" + bean.dtJumpType + ']')
              .attr('checked', true)
              .trigger('change');
            $("input[name='dtBtnOrientation'][value=" + bean.dtBtnOrientation + ']')
              .attr('checked', true)
              .trigger('change');
            if (bean.dtBtnJsonList && bean.dtBtnJsonList != '' && bean.dtBtnJsonList != null) {
              var dtBtnJsonList = JSON.parse(bean.dtBtnJsonList);
              for (var i = 0; i < dtBtnJsonList.length; i++) {
                $('#msgLinkTable').bootstrapTable('insertRow', { index: i, row: dtBtnJsonList[i] });
              }
            }

            _this.initMsgEventTableData();
          }
        }
      });
    },

    // 初始化回调事件表格数据
    initMsgEventTableData: function () {
      var callbackJson = this.bean.callbackJson ? JSON.parse(this.bean.callbackJson) : {};
      var eventsObj = this.adjustMsgEventsJSON(callbackJson).events;

      for (var i = 0; i < eventsObj.length; i++) {
        var event = eventsObj[i];
        $('#msgEventTable').bootstrapTable('insertRow', { index: i, row: event });
      }

      this.setMsgEventsTableEditableStatus();
    },

    // 所属模块下拉框
    initModuleSelector: function () {
      $('#moduleId').wSelect2({
        valueField: 'moduleId',
        remoteSearch: false,
        serviceName: 'appModuleMgr',
        queryMethod: 'loadSelectData',
        params: {
          systemUnitId: window.SpringSecurityUtils.getCurrentUserUnitId()
        }
      });
    },

    // 接口实现
    initMessageInterfaceTextComboTree: function () {
      $('#messageIntefaceText').comboTree({
        labelField: 'messageInterfaceText',
        valueField: 'messageInteface',
        width: 220,
        height: 220,
        autoInitValue: false,
        autoCheckByValue: true,
        treeSetting: {
          async: {
            otherParam: {
              serviceName: 'messageService',
              methodName: 'getIntefaceSourceList',
              data: [-1, 1]
            }
          }
        }
      });
    },

    // 发送消息触发事件
    initMessageEventTextComboTree: function () {
      $('#messageEventText').comboTree({
        labelField: 'messageEventText',
        valueField: 'messageEvent',
        width: 220,
        height: 220,
        autoInitValue: false,
        autoCheckByValue: true,
        treeSetting: {
          async: {
            otherParam: {
              serviceName: 'messageEventService',
              methodName: 'getEventClientSourceList',
              data: [-1, 1]
            }
          }
        }
      });
    },

    // 分类下拉框
    initCategorySelector: function () {
      JDS.call({
        service: 'messageClassifyService.queryList',
        data: ['', ''],
        version: '',
        success: function (result) {
          if (result.success) {
            var categoryList = [];
            for (var i = 0; i < result.data.length; i++) {
              if (result.data[i].isEnable == 1) {
                categoryList.push({ id: result.data[i].uuid, text: result.data[i].name });
              }
            }

            $('#classifyName').wSelect2({
              valueField: 'classifyUuid',
              labelField: 'classifyName',
              remoteSearch: false,
              data: categoryList
            });
          }
        }
      });
    },

    // 弹窗位置
    initPopupPositionSelector: function () {
      $('#popupPosition')
        .wSelect2({
          data: [
            { id: 1, text: '浏览器右下角' },
            { id: 2, text: '浏览器中间' }
          ],
          valueField: 'popupPosition',
          remoteSearch: false
        })
        .change(function () {
          if ($(this).val() == 2 && $('#displayMask').val() != 1) {
            $('#showMask').trigger('click');
          }
        });
    },

    // 附加链接表格
    initMessageLinkTable: function () {
      var linkBean = { title: '', url: '' };

      formBuilder.bootstrapTable.initTableTopButtonToolbar('msgLinkTable', 'msg_link', '', linkBean);

      $('#msgLinkTable')
        .bootstrapTable('destroy')
        .bootstrapTable({
          data: [],
          idField: 'uuid',
          striped: true,
          width: 500,
          toolbar: $('#div_msg_link_toolbar'),
          columns: [
            {
              field: 'checked',
              checkbox: true,
              formatter: function (value) {
                return !!value;
              }
            },
            {
              field: 'title',
              title: '源标题',
              width: '50%',
              editable: {
                type: 'text',
                mode: 'inline',
                showbuttons: false,
                onblur: 'submit',
                validate: function (value) {
                  if (StringUtils.isBlank(value)) {
                    return '请输入源标题!';
                  }
                }
              }
            },
            {
              field: 'url',
              title: '源地址',
              width: '50%',
              editable: {
                type: 'text',
                mode: 'inline',
                showbuttons: false,
                onblur: 'submit',
                savenochange: true,
                validate: function (value) {
                  if (StringUtils.isBlank(value)) {
                    return '请输入源地址!';
                  }
                }
              }
            }
          ]
        });
    },

    // 回调事件表格
    initMessageEventsTable: function () {
      var _this = this;
      var bootstrapTable = {};

      // 1、目标位置
      var targetItems = [
        { text: '', id: '' },
        { text: '新窗口', id: '_blank' },
        { text: '当前窗口', id: '_self' },
        { text: '对话框', id: '_dialog' }
      ];

      var renderContentViewByPosition = function ($container, value) {
        var $containerDiv = $container.find('.div_content_option');
        if ($containerDiv[0]) {
          $containerDiv.empty();
        } else {
          $containerDiv = $("<div class='div_content_option'></div>");
          $container.append($containerDiv);
        }
      };

      bootstrapTable.targePosition = {};

      bootstrapTable.targePosition.value2input = function (value) {
        var $input = this.$input;
        $input.closest('form').removeClass('form-inline');
        $input.css('width', '400');
        $input.empty();
        value = value || {};
        formBuilder.buildSelect2({
          container: $input,
          label: '目标位置',
          name: 'position',
          value: value.position,
          display: 'positionName',
          displayValue: value.positionName,
          inputClass: 'w-custom-collect',
          labelColSpan: '3',
          controlColSpan: '9',
          select2: {
            data: [
              {
                text: '新窗口',
                id: '_blank'
              },
              {
                text: '当前窗口',
                id: '_self'
              },
              {
                text: '对话框',
                id: '_dialog'
              }
            ],
            labelField: 'widgetPositionTypeName',
            valueField: 'widgetPositionType',
            defaultBlank: true,
            remoteSearch: false
          },
          events: {
            change: function () {
              var position = $(this).val();
              renderContentViewByPosition($input, {
                position: position
              });
            }
          }
        });
        renderContentViewByPosition($input, value);
      };

      bootstrapTable.targePosition.value2display = function (value) {
        for (var i = 0; i < targetItems.length; i++) {
          var target = targetItems[i];
          var position = target.id;
          var positionName = target.text;
          if (position === value.position) {
            return positionName;
          }
        }
        return value.positionName;
      };

      bootstrapTable.targePosition.inputCompleted = function (value) {
        if (value) {
          value.refreshIfExists = Boolean(value.refreshIfExists);
        }
      };

      var buttonRowBean = {
        checked: false,
        uuid: '',
        code: '',
        text: '',
        group: '',
        cssStr: 'btn-bg-color',
        cssClass: 'btn-default'
      };
      // 定义添加，删除，上移，下移4按钮事件
      formBuilder.bootstrapTable.initTableTopButtonToolbar('msgEventTable', 'msg_event', '', buttonRowBean);

      $('#msgEventTable')
        .bootstrapTable('destroy')
        .bootstrapTable({
          data: [],
          idField: 'uuid',
          striped: true,
          width: 500,
          toolbar: $('#div_msg_event_toolbar'),
          columns: [
            {
              field: 'checked',
              checkbox: true,
              formatter: function (value) {
                return !!value;
              }
            },
            { field: 'uuid', title: 'UUID', visible: false },
            {
              field: 'type',
              title: '类型',
              visible: true,
              formatter: function (value, row, index) {
                var selectHtml = '<select class="msgEventTypeSelector" data-index="' + index + '">';
                selectHtml += '<option value="preset" ' + (value === 'preset' ? 'selected' : '') + '>内置事件</option>';
                selectHtml += '<option value="customize" ' + (value !== 'preset' ? 'selected' : '') + '>自定义事件</option>';
                selectHtml += '</select>';

                return selectHtml;
              }
            },
            {
              field: 'text',
              title: '名称',
              formatter: function (value, row, index) {
                var html = '';

                if (row.type === 'preset') {
                  // 默认按钮
                  html += '<select class="msgEventPresetNamesSelector" data-index="' + index + '">';
                  html += '<option value="" ' + (value !== '' ? 'selected' : '') + '></option>';
                  html += '<option value="reply" ' + (value === '回复' ? 'selected' : '') + '>回复</option>';
                  html += '<option value="forward" ' + (value === '转发' ? 'selected' : '') + '>转发</option>';
                  html += '<option value="delete" ' + (value === '删除' ? 'selected' : '') + '>删除</option>';
                  html += '</select>';
                } else {
                  // 自定义按钮
                  html += '<input class="form-control msgEventCustomNameInput" value="' + value + '"  data-index="' + index + '" />';
                }

                return html;
              }
            },
            {
              field: 'code',
              title: '编码',
              width: 80,
              editable: {
                type: 'text',
                mode: 'inline',
                showbuttons: false,
                onblur: 'submit',
                savenochange: true,
                validate: function (value) {
                  if (StringUtils.isBlank(value)) {
                    return '请输入编码!';
                  }
                }
              }
            },
            {
              field: 'displayLocation',
              title: '展示位置',
              visible: true,
              width: '280px',
              editable: {
                type: 'checklist',
                mode: 'inline',
                savenochange: true,
                showbuttons: false,
                viewseparator: ',',
                onblur: 'submit',
                display: function (value, sourceData) {
                  var textArr = [];
                  for (var i = 0; i < value.length; i++) {
                    var selectedItem = sourceData.find(function (item) {
                      return item.value === value[i];
                    });

                    selectedItem && textArr.push(selectedItem.text);
                  }

                  return $(this).text(textArr.length ? textArr.join(', ') : 'Empty');
                },
                source: [
                  { value: 'message-modal', text: '弹窗展示' },
                  { value: 'message-list', text: '消息列表' },
                  { value: 'message-detail', text: '消息详情' }
                ]
              }
            },
            {
              field: 'group',
              title: '组别',
              width: 100,
              editable: {
                type: 'text',
                mode: 'inline',
                showbuttons: false,
                onblur: 'submit'
              }
            },
            {
              field: 'btnLib',
              title: '按钮库',
              width: 100,
              editable: {
                onblur: 'save',
                type: 'wCustomForm',
                placement: 'bottom',
                savenochange: true,
                value2input: designCommons.bootstrapTable.btnLib.value2input,
                input2value: designCommons.bootstrapTable.btnLib.input2value,
                value2display: designCommons.bootstrapTable.btnLib.value2display,
                value2html: designCommons.bootstrapTable.btnLib.value2html
              }
            },
            {
              field: 'target',
              title: '目标位置',
              width: 100,
              editable: {
                onblur: 'cancel',
                type: 'wCustomForm',
                placement: 'bottom',
                savenochange: true,
                value2input: bootstrapTable.targePosition.value2input,
                value2display: bootstrapTable.targePosition.value2display,
                inputCompleted: bootstrapTable.targePosition.inputCompleted
              }
            },
            {
              field: 'eventManger',
              title: '事件管理',
              editable: {
                mode: 'modal',
                onblur: 'ignore',
                type: 'wCustomForm',
                placement: 'left',
                savenochange: true,
                value2input: designCommons.bootstrapTable.eventManager.value2input,
                input2value: designCommons.bootstrapTable.eventManager.input2value,
                value2display: designCommons.bootstrapTable.eventManager.value2display
              }
            }
          ],
          onResetView: function () {
            _this.setMsgEventsTableEditableStatus();
          },
          onPostBody: function () {
            setTimeout(function () {
              _this.setMsgEventsTableEditableStatus();
            }, 100);
          }
        });
    },

    adjustMsgEventsJSON: function (eventsObj) {
      var _this = this;
      if (typeof eventsObj === 'object' && eventsObj.adjusted) {
        return eventsObj;
      }

      var newEventsObj = {
        /** 用于识别对象是否调整为新的数据格式 */
        adjusted: true,

        /** 回调事件数组 */
        events: []
      };

      var oldEventsObj = eventsObj;

      // 预置转发/删除事件
      var forwardEvent = $.extend({}, _this.presetEventsInfo.forward);
      var deleteEvent = $.extend({}, _this.presetEventsInfo.delete);
      newEventsObj.events.push(forwardEvent, deleteEvent);

      // 原配置事件
      $.each(oldEventsObj, function (idx, event) {
        event.type = 'customize';
        event.displayLocation = ['message-modal', 'message-list', 'message-detail'];
        newEventsObj.events.push(event);
      });

      return newEventsObj;
    },

    // 保存数据
    save: function () {
      var bean = this.bean;
      $('#msg_format_form').form2json(bean);

      if ($('#name').val() == '') {
        appModal.error('名称不能为空！');
        return false;
      }

      if ($('#id').val() == '') {
        appModal.error('id不能为空！');
        return false;
      }

      if ($('#sendWay_dingtalk').prop('checked')) {
        if ($('#dtTitle').val() == '') {
          appModal.error('钉钉消息标题不能为空！');
          return false;
        }
        if ($('#dtBody').val() == '') {
          appModal.error('钉钉消息内容不能为空！');
          return false;
        }
      }

      bean.systemUnitId = SpringSecurityUtils.getCurrentUserUnitId();
      var events = $('#msgEventTable').bootstrapTable('getData');

      for (var i = 0; i < events.length; i++) {
        var event = events[i];

        if (!event.text) {
          appModal.error('回调事件名称不能为空！');
          return false;
        }

        if (!event.code) {
          appModal.error('回调事件编码不能为空！');
          return false;
        }
      }

      bean.callbackJson = JSON.stringify({
        adjusted: true,
        events: events
      });

      if (bean.dtJumpType == 'multi') {
        bean.dtUri = '';
        bean.dtUriTitle = '';
        bean.dtBtnJsonList =
          $('#msgLinkTable').bootstrapTable('getData').length > 0 ? JSON.stringify($('#msgLinkTable').bootstrapTable('getData')) : null;
      } else {
        bean.dtBtnOrientation = null;
        bean.dtBtnJsonList = null;
      }

      JDS.call({
        service: 'messageTemplateService.saveBean',
        data: [bean],
        version: '',
        success: function (result) {
          if (result.success) {
            var tableId = parent.$('.msg-content-list').find('.ui-wBootstrapTable').attr('id');
            parent.appContext.getWidgetById(tableId).refresh();
            parent.$('.bootbox.modal').modal('hide');
            parent.appModal.success({
              message: '保存成功！'
            });
          }
        }
      });
    },

    /**
     * 设置回调事件 表格编码、按钮库、目标位置、事件管理 四列是否可编辑
     * - 预设按钮不可编辑
     * - 自定义按钮可编辑
     *
     * * 无参数时，按表格数据重新设置所有行
     * * 传入1个参数时，按表格数据设置
     * * 传入2个参数时，按传入参数设置
     *
     * @param {number=} idx 表格行id
     * @param {boolean=} status 编辑状态
     */
    setMsgEventsTableEditableStatus: function (idx, status) {
      var _this = this;
      var $msgEventTable = $('#msgEventTable');

      if (arguments.length === 0) {
        var data = $msgEventTable.bootstrapTable('getData');
        for (var i = 0; i < data.length; i++) {
          _this.setMsgEventsTableEditableStatus(i, data[i].type !== 'preset');
        }
      } else if (arguments.length === 1) {
        var data = $msgEventTable.bootstrapTable('getData');
        _this.setMsgEventsTableEditableStatus(i, data[i].type !== 'preset');
      } else {
        var $tr = $('#msgEventTable').find('tr[data-index=' + idx + ']');
        $tr.find('[data-field=code] .editable').editable(status ? 'enable' : 'disable');
        $tr.find('[data-field=eventManger] .editable').editable(status ? 'enable' : 'disable');
        $tr.find('[data-field=target] .editable').editable(status ? 'enable' : 'disable');
      }
    },

    bindEvents: function () {
      var _this = this;
      var $msgEventTable = $('#msgEventTable');

      // 回调事件表格 - 类型 change事件
      // - 更新表格数据
      // - 更改名称列
      // - *预设事件* 的编码、按钮库、目标位置、事件管理不能更改
      $msgEventTable.on('change', '.msgEventTypeSelector', function () {
        var $this = $(this);
        var type = $this.val();
        var idx = $this.attr('data-index');

        var row = { type: type, text: '', code: '', displayLocation: '', group: '', btnLib: {}, target: '', eventManger: {} };
        $msgEventTable.bootstrapTable('updateRow', { index: idx, row: row, reinit: true });

        _this.setMsgEventsTableEditableStatus(idx, type === 'customize');
      });

      // 回调事件表格 - 名称(*预设*) change事件
      // - 更新表格数据
      // - 更改其它预设值
      $msgEventTable.on('change', '.msgEventPresetNamesSelector', function () {
        var $this = $(this);
        var name = $this.val();
        var idx = $this.attr('data-index');

        var row = $.extend({}, _this.presetEventsInfo[name]);
        $msgEventTable.bootstrapTable('updateRow', { index: idx, row: row, reinit: true });

        _this.setMsgEventsTableEditableStatus(idx, false);
      });

      // 回调事件表格 - 名称(*自定义*) change事件
      // - 更新表格数据
      $msgEventTable.on('change', '.msgEventCustomNameInput', function () {
        var $this = $(this);
        var name = $this.val();
        var idx = $this.attr('data-index');

        $msgEventTable.bootstrapTable('updateCell', { index: idx, field: 'text', value: name, reinit: false });
      });

      // 接口实现字段显示/隐藏
      $('#sendWay_inteface').on('change', function () {
        if ($(this).prop('checked') == true) {
          $('.tr_interface').show();
        } else {
          $('.tr_interface').hide();
        }
      });

      // 定时时间字段显示/隐藏
      $("input[name='sendTime']").on('change', function () {
        var val = $("input[name='sendTime']:checked").val();
        if (val == 'SCHEDULE_TIME') {
          $('.tr_time').show();
        } else {
          $('.tr_time').hide();
        }
      });

      // 消息提醒方式
      $("input[name='reminderType']").on('change', function () {
        var val = $("input[name='reminderType']:checked").val();
        if (val == 2) {
          $('.show-dialog').show();
        } else {
          $('.show-dialog').hide();
        }
      });

      // 弹窗大小
      $("input[name='popupSize']").on('change', function () {
        var val = $("input[name='popupSize']:checked").val();
        if (val == 2) {
          $('.popup-customer-size').css({ display: 'inline-block' });
        } else {
          $('.popup-customer-size').hide();
        }
      });

      // 显示遮罩
      $('#showMask').on('click', function () {
        if ($('#popupPosition').val() == 2 && $('#displayMask').val() == 1) {
          return;
        }
        if ($(this).hasClass('active')) {
          $(this).removeClass('active').data('value', 0);
        } else {
          $(this).addClass('active').data('value', 1);
        }
        $('#displayMask').val($(this).data('value'));
      });

      // 弹窗计时关闭
      $('#showTimer').on('click', function () {
        if ($(this).hasClass('active')) {
          $(this).removeClass('active').data('value', 0);
        } else {
          $(this).addClass('active').data('value', 1);
        }
        $('#autoTimeCloseWin').val($(this).data('value'));
      });

      // 跳转方式
      $("input[name='dtJumpType']").on('change', function () {
        var val = $("input[name='dtJumpType']:checked").val();
        if (val == 'single') {
          $('.overall-jump').show();
          $('.single-jump').hide();
        } else if (val == 'multi') {
          $('.single-jump').show();
          $('.overall-jump').hide();
          if (_this.bean.dtBtnOrientation == null) {
            $("input[name='dtBtnOrientation'][value='0']").attr('checked', true).trigger('change');
          }
        }
      });

      // 保存按钮
      $('#msg_format_save').on('click', function () {
        _this.save();
      });
    },

    refresh: function () {
      this.init();
    },

    // 预设按钮信息
    presetEventsInfo: {
      forward: {
        type: 'preset',
        text: '转发',
        group: '',
        code: 'btnForwardMsg',
        eventManger: {},
        uuid: 'C78EA36C02400001A8DA914010C01D4B',
        position: ['5-1'],
        displayLocation: ['message-detail', 'message-list'],
        btnLib: {
          btnColor: 'w-btn-primary',
          type: 'primary',
          btnInfo: {
            class: 'w-line-btn',
            type: 'line',
            type_name: '线框按钮',
            status: [
              { class: '', text: '普通状态' },
              { class: 'hover', text: '鼠标移入状态' },
              { class: 'active', text: '点击状态' },
              { class: 'w-disable-btn', text: '禁用状态' }
            ]
          },
          btnSize: '',
          iconInfo: {
            fileIDs: 'iconfont icon-oa-zhuanban',
            filePaths: 'iconfont icon-oa-zhuanban',
            fileType: 3
          }
        }
      },
      reply: {
        type: 'preset',
        text: '回复',
        group: '',
        code: 'btnReplyMsg',
        eventManger: {},
        displayLocation: [],
        position: ['5-1'],
        btnLib: {
          btnColor: 'w-btn-primary',
          type: 'primary',
          btnInfo: {
            class: 'w-line-btn',
            type: 'line',
            type_name: '线框按钮',
            status: [
              { class: '', text: '普通状态' },
              { class: 'hover', text: '鼠标移入状态' },
              { class: 'active', text: '点击状态' },
              { class: 'w-disable-btn', text: '禁用状态' }
            ]
          },
          iconInfo: {
            fileIDs: 'iconfont icon-ptkj-wentifankui',
            filePaths: 'iconfont icon-ptkj-wentifankui',
            fileType: 3
          }
        }
      },
      delete: {
        type: 'preset',
        text: '删除',
        group: '',
        code: 'btnDelMsg',
        displayLocation: ['message-list'],
        eventManger: {},
        uuid: 'C78EA36D0BC000018859CF707CF5DF60',
        position: ['5-1'],
        btnLib: {
          btnColor: 'w-btn-primary',
          type: 'primary',
          btnInfo: {
            class: 'w-line-btn',
            type: 'line',
            type_name: '线框按钮',
            status: [
              { class: '', text: '普通状态' },
              { class: 'hover', text: '鼠标移入状态' },
              { class: 'active', text: '点击状态' },
              { class: 'w-disable-btn', text: '禁用状态' }
            ]
          },
          btnSize: '',
          iconInfo: {
            fileIDs: 'iconfont icon-ptkj-shanchu',
            filePaths: 'iconfont icon-ptkj-shanchu',
            fileType: 3
          }
        }
      }
    }
  });

  return AppMsgFormatWidgetDevelopment;
});
