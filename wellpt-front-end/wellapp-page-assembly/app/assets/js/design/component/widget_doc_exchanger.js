define(['ui_component', 'constant', 'commons', 'server', 'formBuilder', 'appContext', 'design_commons'], function (
  ui_component,
  constant,
  commons,
  server,
  formBuilder,
  appContext,
  designCommons
) {
  var StringUtils = commons.StringUtils;
  var StringBuilder = commons.StringBuilder;
  var collectClass = 'w-configurer-option';
  var component = $.ui.component.BaseComponent();
  var UUID = commons.UUID;
  console.log('widget_doc_exchanger.js ');
  component.prototype.create = function () {
    var _self = this;
    var options = _self.options;
    var $element = $(_self.element);
    var $dmsContainer = $element.find('.ui-sortable');
    _self.pageDesigner.sortable(_self, $dmsContainer, $dmsContainer);

    // 初始化容器结点
    if (options.items != null) {
      $.each(options.items, function (index, item) {
        var $draggable = _self.pageDesigner.createDraggableByDefinitionJson(item);
        _self.pageDesigner.drop(_self, $dmsContainer, $draggable, item);
      });
    }
  };

  // 返回定义的HTML
  component.prototype.toHtml = function () {
    var _self = this;
    var options = _self.options;
    var children = _self.getChildren();
    var id = _self.getId();
    var html = '<div id="' + id + '" class="ui-wDocExchanger">';
    if (children != null) {
      $.each(children, function (i) {
        var child = this;
        var childHtml = child.toHtml.call(child);
        html += childHtml;
      });
    }
    html += '</div>';
    return html;
  };

  // 使用属性配置器
  component.prototype.usePropertyConfigurer = function () {
    return true;
  };
  var onEditHidden = function (field, row, $el, reason) {
    $el.closest('table').bootstrapTable('resetView');
  };
  var clearChecked = function (row) {
    row.checked = false;
    return row;
  };
  var checkedFormat = function (value, row, index) {
    if (value) {
      return true;
    }
    return false;
  };
  var clearInputValue = function ($container) {
    $container.find('.w-configurer-option').each(function () {
      var $element = $(this);
      var type = $element.attr('type');
      if (type == 'text' || type == 'hidden') {
        $element.val('');
      } else if (type == 'checkbox' || type == 'radio') {
        $element.prop('checked', false);
      }
      $element.trigger('change');
    });
  };
  var nameSource = null;

  // 验证配置信息的合法性
  var validateConfiguration = function (configuration) {
    // 单据管理按钮名称、编号不能为空
    if (configuration.document && configuration.document.buttons) {
      var buttons = configuration.document.buttons;
      for (var i = 0; i < buttons.length; i++) {
        var button = buttons[i];
        if (StringUtils.isBlank(button.text)) {
          appModal.error('按钮的名称不允许为空！');
          return false;
        }
        if (StringUtils.isBlank(button.code)) {
          appModal.error('按钮的编码不允许为空！');
          return false;
        }
      }
    }
    return true;
  };
  // 返回属性配置器
  component.prototype.getPropertyConfigurer = function () {
    var configurer = $.ui.component.BaseComponentConfigurer();
    configurer.prototype.onLoad = function ($container, options) {
      // 初始化页签项
      $('#widget_data_management_viewer_tabs ul a', $container).on('click', function (e) {
        e.preventDefault();
        $(this).tab('show');
      });
      var _self = this.component;
      var configuration = $.extend(true, {}, options.configuration);
      this.initConfiguration(configuration, $container);
    };
    configurer.prototype.onOk = function ($container) {
      var _self = this;
      var component = _self.component;
      component.options.configuration = _self.collectConfiguration($container);
      if (component.options.configuration.store.configUuid == '') {
        appModal.error('交换业务不能为空！');
        return false;
      }
      if (!component.options.configuration.isValidate) {
        return component.options.configuration.isValidate;
      }
      return validateConfiguration(component.options.configuration);
    };

    configurer.prototype.initConfiguration = function (configuration, $container) {
      // 基本信息
      this.initBaseInfo(configuration, $container);
      // 文档定义
      this.initDocDefine(configuration, $container);
      // 权限操作
      this.initActionInfo(configuration, $container);
      // 可视化
      this.initVisualizationInfo(configuration, $container);

      //数据标记
      this.initDataMarkDefine(configuration, $container);
    };
    configurer.prototype.initBaseInfo = function (configuration, $container) {
      // 设置值
      designCommons.setElementValue(configuration, $container, 'query');

      // 分类
      // $("#categoryCode", $container).wSelect2({
      //     serviceName: "dataDictionaryService",
      //     params: {
      //         type: "MODULE_CATEGORY"
      //     },
      //     labelField: "categoryName",
      //     valueField: "categoryCode",
      //     remoteSearch: false
      // });
    };
    configurer.prototype.initDocDefine = function (configuration, $container) {
      var store = configuration.store || {};
      // 设置值
      designCommons.setElementValue(store, $container, 'query');
      var self = this;

      getQueryList(self, $container);

      $('.exchangeBusinessManagement', $container)
        .off()
        .on('click', function () {
          var configUuid = $('#configUuid', $container).val();
          var html1 = getBusinessHtml();
          var $businessDialog = appModal.dialog({
            message: html1,
            title: '交换业务管理',
            size: 'large',
            height: 620,
            width: 980,
            zIndex: 10001,
            shown: function () {
              $('.bootbox-body', $businessDialog).css({
                overflow: 'hidden'
              });
              var bean = {
                uuid: '',
                dmsDocExchangeDyformUuid: '',
                receiveDyformUuid: '',
                name: '', //业务名称
                descriptor: '', //业务描述
                exchangeType: '', //交换类型
                dyformUuid: '', //动态表单
                dyformName: '', //动态表单
                docEncryptionLevel: '', //文档密级
                defaultEncryptionLevel: '',// 文档密级默认值
                docUrgeLevel: '', //文档紧急程度
                defaultUrgeLevel: '', // 文档紧急程度默认值
                businessCategoryUuid: '', //业务通讯录
                sendRoleUuid: '', //发件角色
                recipientRoleUuid: '', //收件角色
                approve: '', //发件审批
                flowUuid: '', //发件审批流程-流程uuid
                isNeedSign: '', //启用签收功能
                docSign: '', //按文档设置签收
                defaultSign: '', //默认需要签收
                signTimeLimit: '', //启用签收时限
                signEvent: '', //签收事件监听
                isNeedFeedback: '', //启用反馈功能
                docFeedback: '', //按文档设置反馈
                defaultFeedback: '', //默认需要反馈
                feedbackTimeLimit: '', //启用反馈时限
                feedbackEvent: '', //反馈事件监听
                autoFinish: '', //自动办结设置
                isForward: '', //转发设置
                processView: '', //办理过程
                refuseToView: '', //收件单位可拒绝查看
                notifyTypes: [], //发件可选提醒方式
                defaultNotifyTypes: [], //默认提醒方式
                notifyMsgUuid: '', //发件消息格式
                signBeforeNum: '', //签收
                signBeforeUnit: '',
                signBeforeMsgUuid: '',
                signAfterNum: '',
                signAfterUnit: '',
                signAfterMsgUuid: '',
                signAfterFrequency: '',
                feedbackBeforeNum: '',
                feedbackBeforeUnit: '',
                feedbackBeforeMsgUuid: '',
                feedbackAfterNum: '',
                feedbackAfterUnit: '',
                feedbackAfterMsgUuid: '',
                feedbackAfterFrequency: ''
              };
              self.savedBean = {};
              var isEdit = configUuid == '' ? false : true;

              $('.business-box-right ul a', $businessDialog).on('click', function (e) {
                e.preventDefault();
                $(this).tab('show');
              });

              dragEvent($businessDialog);
              // 获取左侧列表数据
              renderLeftHtml(self.configData, $businessDialog, bean, configUuid, self);

              $("input[name='exchangeType']", $businessDialog)
                .off()
                .on('change', function () {
                  var val = $(this).val();
                  $('.value-form', $businessDialog)[val == '0' ? 'show' : 'hide']();
                  listenFormChange('交换类型', 'exchangeType', self, $businessDialog, val, '1');
                  if (val == '0') {
                    $('#editDocDesigner', $businessDialog).removeClass('w-disable-btn').removeAttr('disabled');
                  } else {
                    $('#editDocDesigner', $businessDialog).addClass('w-disable-btn').attr('disabled', true);
                  }
                });

              // 动态表单
              $('#dyformName', $businessDialog)
                .wSelect2({
                  serviceName: 'dataManagementViewerComponentService',
                  queryMethod: 'getDataTypeOfDyFormSelectData',
                  selectionMethod: 'getDataTypeOfDyFormSelectDataByIds',
                  labelField: 'dyformName',
                  valueField: 'dyformUuid',
                  defaultBlank: true,
                  width: '100%',
                  height: 250,
                  remoteSearch: false
                })
                .off()
                .on('change', function () {
                  var val = $('#dyformUuid', $businessDialog).val();
                  listenFormChange('动态表单', 'dyformUuid', self, $businessDialog, val, '1');
                })
                .trigger('change');

              var fields = ['approve', 'isNeedSign', 'isNeedFeedback', 'processView'];
              var events = [];
              $.each(fields, function (index, item) {
                $("input[name='" + item + "']", $businessDialog)
                  .off()
                  .on('change', function () {
                    var isChecked = $(this).prop('checked');
                    $('.value-' + item, $businessDialog)[isChecked ? 'show' : 'hide']();
                    if (item == 'isNeedSign' || item == 'isNeedFeedback') {
                      var title = item == 'isNeedSign' ? '是否需要签收' : '是否需要反馈';
                      listenFormChange(title, item, self, $businessDialog, isChecked ? 1 : 0, '2');
                    }

                    $('.tab-content', $businessDialog).getNiceScroll().resize();
                  });

                if (item == 'isNeedSign' || item == 'isNeedFeedback') {
                  //事件监听
                  var newItem1 = item == 'isNeedSign' ? 'sign' : 'feedback';
                  events.push(newItem1 + 'Event');

                  // 按文档签收或反馈设置
                  var newItem2 = item == 'isNeedSign' ? 'Sign' : 'Feedback';
                  $('#doc' + newItem2, $businessDialog)
                    .off()
                    .on('change', function () {
                      var isChecked = $(this).prop('checked');
                      var docVal = isChecked ? 1 : 0;
                      var text = newItem2 == 'Sign' ? '签收' : '反馈';
                      $('#default' + newItem2, $businessDialog)
                        .hide()
                        .next()
                        [isChecked ? 'show' : 'hide']();

                      listenFormChange('按文档设置' + text, 'doc' + newItem2, self, $businessDialog, docVal, '2');
                    });
                }
              });

              $('#signTimeLimit', $businessDialog)
                .off()
                .on('change', function () {
                  var val = $(this).prop('checked') ? 1 : 0;
                  listenFormChange('签收时限', 'signTimeLimit', self, $businessDialog, val, '2');
                });

              $('#feedbackTimeLimit', $businessDialog)
                .off()
                .on('change', function () {
                  var val = $(this).prop('checked') ? 1 : 0;
                  listenFormChange('反馈时限', 'feedbackTimeLimit', self, $businessDialog, val, '2');
                });

              $.ajax({
                url: ctx + '/api/dms/doc/exc/config/queryEvents',
                type: 'get',
                success: function (res) {
                  var eventData = res.data;
                  $.each(events, function (eIndex, eItem) {
                    $('#' + eItem, $businessDialog).wellSelect({
                      valueField: eItem,
                      data: eventData,
                      chooseAll: true,
                      multiple: true
                    });
                  });
                }
              });

              // 发件角色
              $('#sendRoleUuid', $businessDialog).wellSelect({
                data: [],
                valueField: 'sendRoleUuid',
                remoteSearch: false,
                defaultBlank: false
              });

              // 收件角色
              $('#recipientRoleUuid', $businessDialog).wellSelect({
                data: [],
                valueField: 'recipientRoleUuid',
                remoteSearch: false,
                defaultBlank: false
              });

              // 业务通讯录
              $.ajax({
                url: ctx + '/api/dms/doc/exc/config/queryBusinessCategorys',
                type: 'get',
                async: false,
                success: function (res) {
                  var cateDate = res.data;
                  $('#businessCategoryUuid', $businessDialog)
                    .wellSelect({
                      data: cateDate,
                      valueField: 'businessCategoryUuid',
                      remoteSearch: false
                    })
                    .off()
                    .on('change', function () {
                      var val = $('#businessCategoryUuid', $businessDialog).val();
                      $('.value-contact', $businessDialog)[val == '' ? 'hide' : 'show']();
                      $('#sendRoleUuid', $businessDialog).val('');
                      $('#recipientRoleUuid', $businessDialog).val('');
                      getRoles(val, $businessDialog);
                      $('#businessForm', $businessDialog).getNiceScroll().resize();
                      $('.tab-content', $businessDialog).getNiceScroll().resize();
                    });
                }
              });

              // 流程审批
              $('#flowUuid', $businessDialog).wSelect2({
                serviceName: 'docExchangerComponentService',
                queryMethod: 'getWorkFlowSelectData',
                valueField: 'flowUuid',
                remoteSearch: false,
                defaultBlank: false
              });

              $('#docEncryptionLevel', $businessDialog)
                .off()
                .on('change', function () {
                  var val = $(this).prop('checked') ? 1 : 0;
                  listenFormChange('文档密级', 'docEncryptionLevel', self, $businessDialog, val, '2');
                });
              $('#docUrgeLevel', $businessDialog)
                .off()
                .on('change', function () {
                  var val = $(this).prop('checked') ? 1 : 0;
                  listenFormChange('文档缓急', 'docUrgeLevel', self, $businessDialog, val, '2');
                });

              // 消息格式
              var msgType = ['notifyMsgUuid', 'signBeforeMsgUuid', 'signAfterMsgUuid', 'feedbackBeforeMsgUuid', 'feedbackAfterMsgUuid'];
              $.ajax({
                type: 'get',
                url: ctx + '/api/workflow/definition/getMessageTemplates',
                success: function (result) {
                  var templates = [];
                  $.each(result.data, function (index, items) {
                    templates.push({
                      id: items.id,
                      text: items.name
                    });
                  });

                  $.each(msgType, function (index, item) {
                    $('#' + item, $businessDialog).wellSelect({
                      data: templates,
                      valueField: item,
                      remoteSearch: false,
                      defaultBlank: false,
                      showEmpty: false
                    });
                  });

                  setMsg($businessDialog);
                }
              });

              // 消息格式
              var unitData = [
                {
                  text: '天',
                  id: 86400
                },
                {
                  text: '小时',
                  id: 3600
                },
                {
                  text: '分钟',
                  id: 60
                },
                {
                  text: '工作日',
                  id: 3
                },
                {
                  text: '工作小时',
                  id: 2
                },
                {
                  text: '工作分钟',
                  id: 1
                }
              ];
              var units = ['signBeforeUnit', 'signAfterUnit', 'feedbackBeforeUnit', 'feedbackAfterUnit'];
              $.each(units, function (index, item) {
                $('#' + item, $businessDialog).wSelect2({
                  data: unitData,
                  valueField: item,
                  remoteSearch: false,
                  searchable: false,
                  defaultBlank: false
                });
              });

              // 文档密级  0 非密 1 秘密 2 机密
              var encryptLevel = [
                {
                  text: '非密',
                  id: 1
                },
                {
                  text: '秘密',
                  id: 2
                },
                {
                  text: '机密',
                  id: 3
                },
                {
                  text: '绝密',
                  id: 4
                }
              ];
              $('#defaultEncryptionLevel', $businessDialog).wSelect2({
                data: encryptLevel,
                valueField: 'defaultEncryptionLevel',
                remoteSearch: false,
                searchable: false,
                defaultBlank: true
              });
              $('#docEncryptionLevel', $businessDialog).off()
                .on('change', function () {
                  if ($(this).prop('checked')) {
                    // 启用文档密级
                    $('#docEncryptionLevelDiv', $businessDialog).show();
                  } else {
                    $('#docEncryptionLevelDiv', $businessDialog).hide();
                  }
                });

              // 文档缓急程度 defaultUrgeLevel
              var docUrgeLevel = [
                {
                  text: '一般',
                  id: 0
                },
                {
                  text: '急件',
                  id: 1
                },
                {
                  text: '特急',
                  id: 2
                }
              ];
              $('#defaultUrgeLevel', $businessDialog).wSelect2({
                data: docUrgeLevel,
                valueField: 'defaultUrgeLevel',
                remoteSearch: false,
                searchable: false,
                defaultBlank: true
              });
              $('#docUrgeLevel', $businessDialog).off()
                .on('change', function () {
                  if ($(this).prop('checked')) {
                    // 启用文档缓急程度
                    $('#docUrgeLevelDiv', $businessDialog).show();
                  } else {
                    $('#docUrgeLevelDiv', $businessDialog).hide();
                  }
                });

              $("input[name='notifyTypes']", $businessDialog)
                .off()
                .on('change', function () {
                  var notifyTypesVal = $(this).val();
                  var notifyTypesVal1 = [];
                  if ($(this).prop('checked')) {
                    $('.checkbox' + notifyTypesVal, $businessDialog).show();
                  } else {
                    $('.checkbox' + notifyTypesVal, $businessDialog).hide();
                    $('.checkbox' + notifyTypesVal, $businessDialog)
                      .find('input')
                      .prop('checked', false);
                  }

                  $.each($("input[name='notifyTypes']", $businessDialog), function (fIndex, fItem) {
                    if ($(fItem).prop('checked')) {
                      notifyTypesVal1.push($(fItem).val());
                    }
                  });
                  $('.tab-content', $businessDialog).getNiceScroll().resize();

                  listenFormChange('发件提醒方式', 'notifyTypes', self, $businessDialog, notifyTypesVal1, '2');
                });

              $businessDialog.off('click', '.business-left-item').on('click', '.business-left-item', function () {
                $(this).addClass('active').siblings().removeClass('active');
                var uuid = $(this).data('uuid');
                self.savedBean = {};
                getOne(uuid, $businessDialog, bean, self);
                $('.business-table-item', $businessDialog).find('.error').hide();
              });

              $('.tab-content', $businessDialog).niceScroll({
                height: '400',
                oneaxismousemode: false,
                cursorcolor: '#ccc',
                cursorwidth: '10px'
              });

              // 删除交换业务
              $businessDialog.off('click', '.itemDel').on('click', '.itemDel', function (e) {
                e.stopPropagation();
                var $this = $(this);
                var uuid = $(this).parent().data('uuid');
                $.ajax({
                  url: ctx + '/api/dms/doc/exc/config/del',
                  type: 'post',
                  dataType: 'json',
                  // contentType: 'application/json',
                  data: {
                    uuid: uuid
                  },
                  success: function (res) {
                    if (res.code == 0) {
                      appModal.success('删除成功！');
                      $this.parent().remove();
                      $('.business-left-list', $businessDialog).getNiceScroll().resize();
                    } else {
                      appModal.error(res.msg);
                    }
                  }
                });
              });

              // 添加交换业务
              $('#addBusinessBtn', $businessDialog)
                .off()
                .on('click', function () {
                  $('#businessForm', $businessDialog).form2json(bean);
                  // if (Object.keys(self.savedBean).length == 0) {
                  //   self.savedBean = _.cloneDeep(bean);
                  // }
                  var hasNewData = _.isEqual(self.savedBean, bean);
                  if (!hasNewData) {
                    appModal.error('存在未保存的数据，请先保存！');
                    return false;
                  } else {
                    $('.business-left-item', $businessDialog).removeClass('active');
                    $("span[class^='checkbox']").hide();

                    $('#businessForm', $businessDialog).clearForm(true);

                    $.common.json.clearJson(bean);

                    self.savedBean = {};

                    var fields = [
                      'businessCategoryUuid',
                      'sendRoleUuid',
                      'recipientRoleUuid',
                      'flowUuid',
                      'signEvent',
                      'feedbackEvent',
                      'notifyMsgUuid',
                      'signBeforeMsgUuid',
                      'signAfterMsgUuid',
                      'signBeforeUnit',
                      'signAfterUnit',
                      'feedbackBeforeMsgUuid',
                      'feedbackAfterMsgUuid',
                      'feedbackBeforeUnit',
                      'feedbackAfterUnit',
                      'docEncryptionLevel',
                      'docUrgeLevel'
                    ];
                    $.each(fields, function (index, item) {
                      $('#' + item)
                        .wellSelect('val', '')
                        .trigger('change');
                    });

                    $('#dyformName', $businessDialog).wellSelect('val', '').trigger('change');
                    $('#dyformName', $businessDialog).parents('.business-table-item').hide();

                    var checkboxs = ['approve', 'isNeedSign', 'docSign', 'isNeedFeedback', 'docFeedback', 'processView'];
                    $.each(checkboxs, function (index, item) {
                      $('#' + item, $businessDialog).trigger('change');
                    });

                    $('.business-table-item', $businessDialog).find('.error').hide();

                    // 设置默认值
                    setDefaultValue(bean, $businessDialog, true);

                    setMsg($businessDialog);
                  }
                });

              //编辑发件文档展示单据
              $('#editDocDesigner', $businessDialog)
                .off()
                .on('click', function () {
                  $('#businessForm', $businessDialog).form2json(bean);
                  openDocDyform($businessDialog, self.savedBean, bean, 'editDesigner');
                });
              //编辑收件文档展示单据
              $('#receiveEditDocDesigner', $businessDialog)
                .off()
                .on('click', function () {
                  $('#businessForm', $businessDialog).form2json(bean);
                  openDocDyform($businessDialog, self.savedBean, bean, 'receiveEditDesigner');
                });

              // 保存交换业务
              $('#saveBusinessBtn', $businessDialog)
                .off()
                .on('click', function () {
                  saveConfigs($businessDialog, bean, self);
                });

              if (!isEdit) {
                $('#businessForm', $businessDialog).form2json(bean);

                self.savedBean = _.cloneDeep(bean);
              }

              // 重置滚动条
              $businessDialog.on('dragstop', function () {
                $('.business-left-list', $businessDialog).getNiceScroll().resize();
                $('#businessForm', $businessDialog).getNiceScroll().resize();
                $('.tab-content', $businessDialog).getNiceScroll().resize();
              });

              // 设置默认值
              configUuid == '' ? setDefaultValue(bean, $businessDialog) : null;
            },
            buttons: {
              close: {
                label: '关闭',
                className: 'btn btn-default'
              }
            },
            callback: function () {
              getQueryList(self, $container);
            }
          });
        });
    };

    function openDocDyform(businessDialog, savedBean, bean, editDesigner) {
      var hasNewData = _.isEqual(savedBean, bean);
      if (!hasNewData || bean.uuid == '') {
        appModal.dialog({
          message: '需要先保存交换业务设置，是否保存？',
          zIndex: 10005,
          className: 'alignCenter',
          buttons: {
            save: {
              label: '保存',
              className: 'well-btn w-btn-primary',
              callback: function () {
                saveConfigs(businessDialog, bean, self, editDesigner);
              }
            },
            cancel: {
              label: '取消',
              className: 'btn btn-default'
            }
          }
        });
      } else {
        var docUuid = bean.dmsDocExchangeDyformUuid;
        if (editDesigner == 'receiveEditDesigner') {
          docUuid = bean.receiveDyformUuid;
        }
        var url =
          ctx +
          '/web/app/page/preview/ceda5214e21a8c7dde5fee5fb99543d2?pageUuid=6e541898-2951-4a4f-9eea-f9ab2fc02c01&uuid=' +
          bean.uuid +
          '&formUuid=' +
          bean.dyformUuid +
          '&docUuid=' +
          docUuid +
          '&editType=' +
          editDesigner;
        window.open(url, url);
      }
    }

    function listenFormChange(title, field, self, $container, val, type) {
      if (self.savedBean[field] == null || val == self.savedBean[field] || self.savedBean.dmsDocExchangeDyformUuid == '') {
        return false;
      }
      var message = type == '1' ? '修改' + title + '将重置文档展示单据，是否继续？' : '需要同时设置' + title + '在页面上的显示，是否继续？';
      var $fieldsDialog = appModal.dialog({
        message: message,
        className: 'alignCenter',
        shown: function () {
        },
        buttons: {
          ok: {
            className: 'well-btn w-btn-primary',
            label: type == '1' ? '重置并继续' : '去设置',
            callback: function () {
              saveConfigs($container, self.savedBean, self, 'editDesigner');
            }
          },
          cancel: {
            className: 'btn btn-default',
            label: type == '1' ? '取消' : '稍后设置',
            callback: function () {
              if (field == 'exchangeType' || field == 'dyformUuid') {
                $('#' + field, $container)
                  .val(self.savedBean[field])
                  .trigger('change');
              } else {
                saveConfigs($container, self.savedBean, self);
              }
            }
          }
        }
      });
    }

    function getQueryList(self, $container) {
      $.ajax({
        url: ctx + '/api/dms/doc/exc/config/queryList',
        type: 'get',
        success: function (res) {
          self.configData = res.data;
          var configData = [];
          $.each(res.data, function (index, item) {
            configData.push({
              text: item.name,
              id: item.uuid
            });
          });

          $('#configUuid', $container).wellSelect({
            data: configData,
            valueField: 'configUuid',
            remoteSearch: false
          });
        }
      });
    }

    function saveConfigs($businessDialog, bean, self, editDesigner) {
      $('#businessForm', $businessDialog).form2json(bean);
      var curBean = _.cloneDeep(bean);
      var validate = true;
      if (bean.name == '') {
        $('#name', $businessDialog).parents('.business-table-value').find('.error').show();
        changeTab(validate, 'business_tabs_base_info', $businessDialog);
        validate = false;
      } else {
        $('#name', $businessDialog).parents('.business-table-value').find('.error').hide();
      }

      var exchangeType = $("input[name='exchangeType']:checked", $businessDialog).val();
      if (exchangeType == '' || exchangeType == undefined) {
        changeTab(validate, 'business_tabs_doc_define', $businessDialog);
        $("input[name='exchangeType']", $businessDialog).parents('.business-table-value').find('.error').show();
        validate = false;
      } else {
        $("input[name='exchangeType']", $businessDialog).parents('.business-table-value').find('.error').hide();
        if (
          exchangeType == '0' &&
          ($('#dyformUuid', $businessDialog).val() == '' || $('#dyformUuid', $businessDialog).val() == undefined)
        ) {
          changeTab(validate, 'business_tabs_doc_define', $businessDialog);
          $('#dyformUuid', $businessDialog).parents('.business-table-value').find('.error').show();
          validate = false;
        } else {
          $('#dyformUuid', $businessDialog).parents('.business-table-value').find('.error').hide();
        }
      }

      var contactUuid = $('#businessCategoryUuid', $businessDialog).val();
      if (contactUuid != '' && $('#sendRoleUuid', $businessDialog).val() == '') {
        changeTab(validate, 'business_tabs_transceiver_set', $businessDialog);
        $('#sendRoleUuid', $businessDialog).parents('.business-table-value').find('.error').show();
        validate = false;
      } else {
        $('#sendRoleUuid', $businessDialog).parents('.business-table-value').find('.error').hide();
      }

      if (contactUuid != '' && $('#recipientRoleUuid', $businessDialog).val() == '') {
        changeTab(validate, 'business_tabs_transceiver_set', $businessDialog);
        $('#recipientRoleUuid', $businessDialog).parents('.business-table-value').find('.error').show();
        validate = false;
      } else {
        $('#recipientRoleUuid', $businessDialog).parents('.business-table-value').find('.error').hide();
      }

      var auditFlow = $('#approve', $businessDialog).prop('checked');
      if (auditFlow && $('#flowUuid', $businessDialog).val() == '') {
        changeTab(validate, 'business_tabs_transceiver_set', $businessDialog);
        $('#flowUuid', $businessDialog).parents('.business-table-value').find('.error').show();
        validate = false;
      } else {
        $('#flowUuid', $businessDialog).parents('.business-table-value').find('.error').hide();
      }

      // var sendMsgType = $('#notifyMsgUuid', $businessDialog).val();
      // if (sendMsgType == '' && bean.notifyTypes.length == 0) {
      //   changeTab(validate, 'business_tabs_remind_set', $businessDialog);
      //   $('#notifyMsgUuid', $businessDialog).parents('.business-table-value').find('.error').show();
      //   validate = false;
      // } else {
      //   $('#notifyMsgUuid', $businessDialog).parents('.business-table-value').find('.error').hide();
      // }

      // 逾期提醒次数
      var feedbackAfterFrequencyVal = $('#feedbackAfterFrequency', $businessDialog).val();
      if (!checkNumber(feedbackAfterFrequencyVal, '反馈提醒-逾期提醒次数')) {
        return false;
      }

      // 逾期时间
      var feedbackAfterNumVal = $('#feedbackAfterNum', $businessDialog).val();
      if (!checkNumber(feedbackAfterNumVal, '反馈提醒-逾期时间')) {
        return false;
      }

      // 到期时间
      var feedbackBeforeNumVal = $('#feedbackBeforeNum', $businessDialog).val();
      if (!checkNumber(feedbackBeforeNumVal, '反馈提醒-到期时间')) {
        return false;
      }

      // 签收提醒=逾期提醒次数
      var signAfterFrequencyVal = $('#signAfterFrequency', $businessDialog).val();
      if (!checkNumber(signAfterFrequencyVal, '签收提醒-逾期提醒次数')) {
        return false;
      }

      // 签收提醒-逾期时间
      var signAfterNumVal = $('#signAfterNum', $businessDialog).val();
      if (!checkNumber(signAfterNumVal, '签收提醒-逾期时间')) {
        return false;
      }

      // 签收提醒-到期时间
      var signBeforeNumVal = $('#signBeforeNum', $businessDialog).val();
      if (!checkNumber(signBeforeNumVal, '签收提醒-到期时间')) {
        return false;
      }

      if (!validate) {
        return false;
      }

      bean.approve = $("input[name='approve']", $businessDialog).prop('checked') ? 1 : 0;
      bean.autoFinish = $("input[name='autoFinish']", $businessDialog).prop('checked') ? 1 : 0;
      bean.defaultFeedback = $("input[name='defaultFeedback']", $businessDialog).prop('checked') ? 1 : 0;
      bean.defaultSign = $("input[name='defaultSign']", $businessDialog).prop('checked') ? 1 : 0;
      bean.docEncryptionLevel = $("input[name='docEncryptionLevel']", $businessDialog).prop('checked') ? 1 : 0;
      bean.docFeedback = $("input[name='docFeedback']", $businessDialog).prop('checked') ? 1 : 0;
      bean.docSign = $("input[name='docSign']", $businessDialog).prop('checked') ? 1 : 0;
      bean.docUrgeLevel = $("input[name='docUrgeLevel']", $businessDialog).prop('checked') ? 1 : 0;
      bean.exchangeType = $("input[name='exchangeType']:checked", $businessDialog).val();
      bean.feedbackTimeLimit = $("input[name='feedbackTimeLimit']", $businessDialog).prop('checked') ? 1 : 0;
      bean.isForward = $("input[name='isForward']", $businessDialog).prop('checked') ? 1 : 0;
      bean.isNeedFeedback = $("input[name='isNeedFeedback']", $businessDialog).prop('checked') ? 1 : 0;
      bean.isNeedSign = $("input[name='isNeedSign']", $businessDialog).prop('checked') ? 1 : 0;
      bean.processView = $("input[name='processView']", $businessDialog).prop('checked') ? 1 : 0;
      bean.refuseToView = $("input[name='refuseToView']", $businessDialog).prop('checked') ? 1 : 0;
      bean.signTimeLimit = $("input[name='signTimeLimit']", $businessDialog).prop('checked') ? 1 : 0;
      bean.notifyTypes = bean.notifyTypes.join(';');
      bean.defaultNotifyTypes = bean.defaultNotifyTypes.join(';');
      bean.defaultEncryptionLevel = $("input[name='defaultEncryptionLevel']", $businessDialog).val();
      bean.defaultUrgeLevel = $("input[name='defaultUrgeLevel']", $businessDialog).val();

      self.savedBean = _.cloneDeep(curBean);
      $.ajax({
        url: ctx + '/api/dms/doc/exc/config/saveOrUpdate',
        type: 'post',
        data: JSON.stringify(bean),
        contentType: 'application/json',
        success: function (res) {
          if (res.code == 0) {
            appModal.success('保存成功！');
            $('#uuid', $businessDialog).val(res.data);
            self.savedBean.uuid = res.data;
            if (bean.uuid == '') {
              bean.uuid = res.data;
              rerenderLeftHtml($businessDialog, bean, self);
            }
            if (editDesigner) {
              var docUuid = bean.dmsDocExchangeDyformUuid;
              if (editDesigner == 'receiveEditDesigner') {
                docUuid = bean.receiveDyformUuid;
              }
              var url =
                ctx +
                '/web/app/page/preview/ceda5214e21a8c7dde5fee5fb99543d2?pageUuid=6e541898-2951-4a4f-9eea-f9ab2fc02c01&uuid=' +
                bean.uuid +
                '&formUuid=' +
                bean.dyformUuid +
                '&docUuid=' +
                docUuid +
                '&editType=' +
                editDesigner;
              window.open(url, url);
            }
          }
        }
      });
    }

    // 正整数校验规则
    function checkNumber(num, checkLabel) {
      var reg = /^[1-9]+[0-9]*$/;
      var returnHelper,
        errorMsg = '';
      if (!num && num !== 0) {
        returnHelper = true;
        errorMsg = '';
      } else {
        if (isNaN(num)) {
          returnHelper = false;
          errorMsg = '只能输入数字类型！';
        } else {
          if (reg.test(num)) {
            returnHelper = true;
            errorMsg = '';
          } else {
            returnHelper = false;
            errorMsg = checkLabel + '只能输入正整数！';
          }
        }
      }
      if (returnHelper == false) {
        appModal.error(errorMsg);
      }
      return returnHelper;
    }

    function getRoles(val, $businessDialog) {
      $.ajax({
        url: ctx + '/api/dms/doc/exc/config/queryBusinessRoles',
        type: 'get',
        data: {
          categoryUuid: val
        },
        success: function (res) {
          var rolesData = res.data;
          $('#sendRoleUuid', $businessDialog).wellSelect({
            data: rolesData
          });
          $('#recipientRoleUuid', $businessDialog).wellSelect({
            data: rolesData
          });
        }
      });
    }

    function changeTab(isValidate, dom, $businessDialog) {
      if (isValidate) {
        $("a[href='#" + dom + "']", $businessDialog).trigger('click');
      }
    }

    function getOne(uuid, $businessDialog, bean, self) {
      $.ajax({
        url: ctx + '/api/dms/doc/exc/config/getOne',
        type: 'get',
        dataType: 'json',
        data: {
          uuid: uuid
        },
        success: function (res) {
          bean = res.data;

          $('#businessForm', $businessDialog).json2form(bean);
          $("input[name='exchangeType'][value='" + bean.exchangeType + "']", $businessDialog)
            .prop('checked', true)
            .trigger('change');

          $('#dyformUuid', $businessDialog).val(bean.dyformUuid);
          $('#dyformName', $businessDialog).wellSelect('val', bean.dyformUuid).trigger('change');

          bean.defaultEncryptionLevel = bean.defaultEncryptionLevel ? bean.defaultEncryptionLevel.toString() : '';
          bean.defaultUrgeLevel = bean.defaultUrgeLevel ? bean.defaultUrgeLevel.toString() : '';
          bean.signBeforeUnit = bean.signBeforeUnit ? bean.signBeforeUnit.toString() : '';
          bean.signAfterUnit = bean.signAfterUnit ? bean.signAfterUnit.toString() : '';
          bean.feedbackBeforeUnit = bean.feedbackBeforeUnit ? bean.feedbackBeforeUnit.toString() : '';
          bean.feedbackAfterUnit = bean.feedbackAfterUnit ? bean.feedbackAfterUnit.toString() : '';

          setDefaultValue(bean, $businessDialog, false);

          var checkboxs = ['approve', 'isNeedSign', 'docSign', 'isNeedFeedback', 'docFeedback', 'processView', 'docEncryptionLevel', 'docUrgeLevel'];
          $.each(checkboxs, function (index, item) {
            $('#' + item, $businessDialog).trigger('change');
          });
          var fields = [
            'businessCategoryUuid',
            'sendRoleUuid',
            'recipientRoleUuid',
            'flowUuid',
            'signEvent',
            'feedbackEvent',
            'notifyMsgUuid',
            'signBeforeMsgUuid',
            'signAfterMsgUuid',
            'signBeforeUnit',
            'signAfterUnit',
            'feedbackBeforeMsgUuid',
            'feedbackAfterMsgUuid',
            'feedbackBeforeUnit',
            'feedbackAfterUnit',
            'defaultEncryptionLevel',
            'defaultUrgeLevel'
          ];
          $.each(fields, function (index, item) {
            $('#' + item, $businessDialog)
              .wellSelect('val', bean[item])
              .trigger('change');
          });

          var notifyTypes = bean.notifyTypes == null ? [] : bean.notifyTypes.split(';');
          var defaultNotifyTypes = bean.defaultNotifyTypes == null ? [] : bean.defaultNotifyTypes.split(';');
          $("input[name='notifyTypes']").val(notifyTypes).trigger('change');
          $("input[name='defaultNotifyTypes']").val(defaultNotifyTypes).trigger('change');

          /*var docEncryptionLevel = bean.docEncryptionLevel ? bean.docEncryptionLevel.toString() : '';
          var docUrgeLevel = bean.docUrgeLevel ? bean.docUrgeLevel.toString() : '';
          if ('' == docEncryptionLevel) {
            $("#docEncryptionLevelDiv").hide();
          }*/

          $('#businessForm', $businessDialog).form2json(bean);
          self.savedBean = _.cloneDeep(bean);
        }
      });
    }

    function setMsg($container) {
      var arr3 = [
        {
          name: 'notifyMsgUuid',
          value: 'DOC_EXCHANGER_RECEIVE_NOTIFY'
        },
        {
          name: 'signBeforeMsgUuid',
          value: 'DOC_EXCHANGER_SIGN_NOTIFY'
        },
        {
          name: 'signAfterMsgUuid',
          value: 'DOC_EXCHANGER_SIGN_OVERDUE_NOTIFY'
        },
        {
          name: 'feedbackBeforeMsgUuid',
          value: 'DOC_EXCHANGER_FEEDBACK_NOTIFY'
        },
        {
          name: 'feedbackAfterMsgUuid',
          value: 'DOC_EXCHANGER_FEEDBACK_OVERDUE_NOTIFY'
        }
      ];

      $.each(arr3, function (mIndex, mItem) {
        $('#' + mItem.name, $container)
          .wellSelect('val', mItem.value)
          .trigger('change');
      });
    }

    // 设置默认值
    function setDefaultValue(bean, $container, isClear) {
      var arr1 = [
        {
          name: 'feedbackAfterFrequency',
          value: '1'
        },
        {
          name: 'feedbackAfterNum',
          value: '1'
        },
        {
          name: 'feedbackBeforeNum',
          value: '1'
        },
        {
          name: 'signAfterNum',
          value: '1'
        },
        {
          name: 'signAfterFrequency',
          value: '1'
        },
        {
          name: 'signBeforeNum',
          value: '1'
        }
      ];
      var arr2 = [
        {
          name: 'feedbackAfterUnit',
          value: '3'
        },
        {
          name: 'feedbackBeforeUnit',
          value: '3'
        },
        {
          name: 'signAfterUnit',
          value: '3'
        },
        {
          name: 'signBeforeUnit',
          value: '3'
        },
        {
          name: 'defaultEncryptionLevel',
          value: '1'
        },
        {
          name: 'defaultUrgeLevel',
          value: '0'
        }
      ];

      if (isClear) {
        for (var i = 0; i < arr1.length; i++) {
          bean[arr1[i].name] = arr1[i].value;
          $('#' + arr1[i].name, $container).val(bean[arr1[i].name]);
        }

        for (var j = 0; j < arr2.length; j++) {
          bean[arr2[j].name] = arr2[j].value;
          $('#' + arr2[j].name, $container)
            .wellSelect('val', bean[arr2[j].name])
            .trigger('change');
        }
      } else {
        for (var i = 0; i < arr1.length; i++) {
          bean[arr1[i].name] = bean[arr1[i].name] ? bean[arr1[i].name] : arr1[i].value;
          $('#' + arr1[i].name, $container).val(bean[arr1[i].name]);
        }

        for (var j = 0; j < arr2.length; j++) {
          bean[arr2[j].name] = bean[arr2[j].name] ? bean[arr2[j].name] : arr2[j].value;
          $('#' + arr2[j].name, $container)
            .val(bean[arr2[j].name])
            .trigger('change');
        }
      }
    }

    function dragEvent($businessDialog) {
      $('.business-left-list', $businessDialog).sortable({
        revert: true,
        cursor: 'move',
        handle: '.icon-ptkj-tuodong',
        items: '.business-left-item',
        axis: 'y',
        update: function () {
          var datas = [];
          var items = $('.business-left-list', $businessDialog).find('.business-left-item');
          $.each(items, function (index, item) {
            datas.push({
              uuid: $(item).data('uuid'),
              sequence: index - 0 + 1
            });
          });
          $.ajax({
            type: 'post',
            url: ctx + '/api/dms/doc/exc/config/sequence',
            data: JSON.stringify(datas),
            contentType: 'application/json',
            success: function (res) {
              console.log(res);
            }
          });
        }
      });
    }

    function rerenderLeftHtml($businessDialog, bean, self) {
      $.ajax({
        url: ctx + '/api/dms/doc/exc/config/queryList',
        type: 'get',
        success: function (res) {
          if (res.code == 0) {
            renderLeftHtml(res.data, $businessDialog, bean, '', self);
          }
        }
      });
    }

    function renderLeftHtml(configData, $businessDialog, bean, configUuid, self) {
      var html = '';
      $.each(configData, function (index, item) {
        var isActive = '';
        if (configUuid != '' && configUuid == item.uuid) {
          isActive = 'active';
          getOne(configUuid, $businessDialog, bean, self);
        }
        html +=
          '<div class="business-left-item ' +
          isActive +
          '" data-uuid="' +
          item.uuid +
          '">' +
          '<span class="business-left-name"><i class="iconfont icon-ptkj-tuodong"></i><span>' +
          item.name +
          '</span></span>' +
          '<i class="iconfont icon-ptkj-shanchu itemDel"></i>' +
          '</div>';
      });

      $('.business-left-list', $businessDialog).html(html);
      $('.business-left-list', $businessDialog).niceScroll({
        height: '440',
        oneaxismousemode: false,
        cursorcolor: '#ccc',
        cursorwidth: '10px'
      });
    }

    function getBusinessHtml() {
      var html = '';
      html +=
        '<div class="business-box">' +
        '<div class="business-box-left">' +
        '<div class="business-left-list"></div>' +
        '<div class="business-box-btn"><button id="addBusinessBtn" type="button" class="well-btn w-btn-primary">添加</button></div>' +
        '</div>' +
        '<div class="business-box-right">' +
        '<form class="business-right-body" id="businessForm">' +
        '<input type="hidden" id="uuid" name="uuid"/>' +
        '<ul class="nav nav-tabs" role="tablist">' +
        '<li role="presentation" class="active">' +
        '<a href="#business_tabs_base_info">基础信息</a>' +
        '</li>' +
        '<li role="presentation">' +
        '<a href="#business_tabs_doc_define">文档定义</a>' +
        '</li>' +
        '<li role="presentation">' +
        '<a href="#business_tabs_transceiver_set">收发设置</a>' +
        '</li>' +
        '<li role="presentation">' +
        '<a href="#business_tabs_visualization_info">文档布局</a>' +
        '</li>' +
        '<li role="presentation">' +
        '<a href="#business_tabs_remind_set">提醒设置</a>' +
        '</li>' +
        '</ul>' +
        '<div class="tab-content">' +
        '<div class="tab-pane active" role="tabpanel" id="business_tabs_base_info">' +
        '<div class="business-form-table">' +
        '<div class="business-table-item">' +
        '<div class="business-table-label">业务名称 <font style="color:#f00;">*</font></div>' +
        '<div class="business-table-value">' +
        '<input type="text" name="name" id="name"/>' +
        '<div class="error" style="display:none">业务名称不能为空!</div>' +
        '</div>' +
        '</div>' +
        '<div class="business-table-item">' +
        '<div class="business-table-label">业务描述</div>' +
        '<div class="business-table-value"><textarea name="descriptor" id="descriptor" placeholder="请输入内容" style="width:100%;"></textarea></div>' +
        '</div>' +
        '</div>' +
        '</div>' +
        '<div class="tab-pane" role="tabpanel" id="business_tabs_doc_define">' +
        '<div class="business-form-table">' +
        '<div class="business-table-item">' +
        '<div class="business-table-label">交换类型 <font style="color:#f00;">*</font></div>' +
        '<div class="business-table-value">' +
        '<input type="radio" name="exchangeType" id="exchangeType1" value="0"/><label for="exchangeType1">动态表单</label>' +
        // '<input type="radio" name="exchangeType" id="exchangeType2" value="1"/><label for="exchangeType2">文件</label>' +
        '<div class="error" style="display:none">交换类型不能为空!</div>' +
        '</div>' +
        '</div>' +
        '<div class="business-table-item value-form" style="display:none;">' +
        '<div class="business-table-label">动态表单 <font style="color:#f00;">*</font></div>' +
        '<div class="business-table-value">' +
        '<div>' +
        '<input type="text" name="dyformName" id="dyformName"/>' +
        '<input type="hidden" name="dyformUuid" id="dyformUuid"/>' +
        '</div>' +
        '<div class="error" style="display:none">动态表单不能为空!</div>' +
        '</div>' +
        '</div>' +
        '<div class="business-table-item">' +
        '<div class="business-table-label">文档密级</div>' +
        '<div class="business-table-value value-flex">' +
        '<div class="w140 ml5"><input type="checkbox" name="docEncryptionLevel" id="docEncryptionLevel"/><label for="docEncryptionLevel">启用文档密级</label></div>' +
        '<div class="w300 ml5 mr5" id="docEncryptionLevelDiv" style="display: none;">' +
        '默认值 ' +
        '<div class="w100 ml5 mr5" style=" display: inline-block; "><input type="hidden" name="defaultEncryptionLevel" id="defaultEncryptionLevel"/></div>' +
        '</div>' +
        '</div>' +
        '</div>' +
        '<div class="business-table-item">' +
        '<div class="business-table-label">文档缓急程度</div>' +
        '<div class="business-table-value value-flex">' +
        '<div class="w140 ml5"><input type="checkbox" name="docUrgeLevel" id="docUrgeLevel"/><label for="docUrgeLevel">启用文档缓急程度</label></div>' +
        '<div class="w300 ml5 mr5" id="docUrgeLevelDiv" style="display: none;">' +
        '默认值 ' +
        '<div class="w100 ml5 mr5" style=" display: inline-block;"><input type="hidden" name="defaultUrgeLevel" id="defaultUrgeLevel"/></div>' +
        '</div>' +
        '</div>' +
        '</div>' +
        '</div>' +
        '</div>' +
        '<div class="tab-pane" role="tabpanel" id="business_tabs_transceiver_set">' +
        '<div class="business-form-table">' +
        '<div class="business-table-item">' +
        '<div class="business-table-label">业务通讯录</div>' +
        '<div class="business-table-value">' +
        '<input type="hidden" name="businessCategoryUuid" id="businessCategoryUuid"/>' +
        '</div>' +
        '</div>' +
        '<div class="business-table-item value-contact" style="display:none;">' +
        '<div class="business-table-label">发件角色 <font style="color:#f00;">*</font></div>' +
        '<div class="business-table-value">' +
        '<div>' +
        '<input type="text" name="sendRoleUuid" id="sendRoleUuid"/>' +
        '</div>' +
        '<div class="error" style="display:none">发件角色不能为空!</div>' +
        '</div>' +
        '</div>' +
        '<div class="business-table-item value-contact" style="display:none;">' +
        '<div class="business-table-label">收件角色 <font style="color:#f00;">*</font></div>' +
        '<div class="business-table-value">' +
        '<div>' +
        '<input type="text" name="recipientRoleUuid" id="recipientRoleUuid"/>' +
        '</div>' +
        '<div class="error" style="display:none">收件角色不能为空!</div>' +
        '</div>' +
        '</div>' +
        '<div class="business-table-item">' +
        '<div class="business-table-label">发件审批</div>' +
        '<div class="business-table-value"><input type="checkbox" name="approve" id="approve"/><label for="approve">启用发件审批流程</label></div>' +
        '</div>' +
        '<div class="business-table-item value-approve" style="display:none;">' +
        '<div class="business-table-label">发件审批流程 <font style="color:#f00;">*</font></div>' +
        '<div class="business-table-value">' +
        '<div>' +
        '<input type="hidden" name="flowUuid" id="flowUuid"/>' +
        '</div>' +
        '<div class="error" style="display:none">发件审批流程不能为空!</div>' +
        '</div>' +
        '</div>' +
        '<div class="business-table-item">' +
        '<div class="business-table-label">签收设置</div>' +
        '<div class="business-table-value">' +
        '<input type="checkbox" name="isNeedSign" id="isNeedSign"/><label for="isNeedSign">启用签收功能</label>' +
        '<div class="business-value-hide value-isNeedSign" style="display:none;">' +
        '<div><input type="checkbox" name="docSign" id="docSign"/><label for="docSign">按文档设置签收</label><input type="checkbox" name="defaultSign" id="defaultSign" style="display:none;"/><label for="defaultSign" style="display:none;">默认需要签收</label></div>' +
        '<div><input type="checkbox" name="signTimeLimit" id="signTimeLimit"/><label for="signTimeLimit">启用签收时限</label></div>' +
        '<div><div>事件监听</div>' +
        '<input type="hidden" name="signEvent" id="signEvent">' +
        '</div>' +
        '</div>' +
        '</div>' +
        '</div>' +
        '<div class="business-table-item">' +
        '<div class="business-table-label">反馈设置</div>' +
        '<div class="business-table-value">' +
        '<input type="checkbox" name="isNeedFeedback" id="isNeedFeedback"/><label for="isNeedFeedback">启用反馈功能</label>' +
        '<div class="business-value-hide value-isNeedFeedback" style="display:none;">' +
        '<div><input type="checkbox" name="docFeedback" id="docFeedback"/><label for="docFeedback">按文档设置反馈</label><input type="checkbox" name="defaultFeedback" id="defaultFeedback" style="display:none;"/><label for="defaultFeedback" style="display:none;">默认需要反馈</label></div>' +
        '<div><input type="checkbox" name="feedbackTimeLimit" id="feedbackTimeLimit"/><label for="feedbackTimeLimit">启用反馈时限</label></div>' +
        '<div><div>事件监听</div>' +
        '<input type="hidden" name="feedbackEvent" id="feedbackEvent">' +
        '</div>' +
        '</div>' +
        '</div>' +
        '</div>' +
        '<div class="business-table-item">' +
        '<div class="business-table-label">自动办结设置</div>' +
        '<div class="business-table-value">' +
        '<input type="checkbox" name="autoFinish" id="autoFinish"/><label for="autoFinish">发件被全部签收或反馈完成后自动办结</label>' +
        '</div>' +
        '</div>' +
        '<div class="business-table-item">' +
        '<div class="business-table-label">转发设置</div>' +
        '<div class="business-table-value">' +
        '<input type="checkbox" name="isForward" id="isForward"/><label for="isForward">收件单位可转发收件</label>' +
        '</div>' +
        '</div>' +
        '<div class="business-table-item">' +
        '<div class="business-table-label">办理过程<br>查看设置</div>' +
        '<div class="business-table-value">' +
        '<input type="checkbox" name="processView" id="processView"/><label for="processView">发件单位查看收件单位办理过程相关文档</label>' +
        '<div class="business-value-hide value-processView" style="display:none;"><input type="checkbox" name="refuseToView" id="refuseToView"/><label for="refuseToView">收件单位可拒绝查看</label></div>' +
        '</div>' +
        '</div>' +
        '</div>' +
        '</div>' +
        '<div class="tab-pane" role="tabpanel" id="business_tabs_visualization_info">' +
        '<div class="edit-doc-designer"><input type="hidden" id="dmsDocExchangeDyformUuid" name="dmsDocExchangeDyformUuid"><button id="editDocDesigner" type="button" class="well-btn w-btn-primary w-line-btn"><i class="iconfont icon-ptkj-bianji"></i>编辑发件文档展示单据</button></div>' +
        '<div class="edit-doc-designer"><input type="hidden" id="receiveDyformUuid" name="receiveDyformUuid"><button id="receiveEditDocDesigner" type="button" class="well-btn w-btn-primary w-line-btn"><i class="iconfont icon-ptkj-bianji"></i>编辑收件文档展示单据</button></div>' +
        '</div>' +
        '<div class="tab-pane" role="tabpanel" id="business_tabs_remind_set">' +
        '<div class="business-title">发件提醒' +
        '<div class="read-tooltip">' +
        '<i class="iconfont icon-ptkj-tishishuoming"></i>' +
        '<div class="read-tooltip-content">发件成功后将提醒收件方<br>发件可选提醒方式：发件时提供给用户选择的提醒方式<br>默认提醒方式：发件时该提醒方式默认选中<br>消息格式：指定发件提醒的信息格式</div>' +
        '</div>' +
        '</div>' +
        '<div class="business-form-table">' +
        '<div class="business-table-item">' +
        '<div class="business-table-label">发件可选<br>提醒方式</div>' +
        '<div class="business-table-value">' +
        '<input type="checkbox" name="notifyTypes" id="onlineMsg" value="IM"/><label for="onlineMsg">在线消息</label>' +
        '<input type="checkbox" name="notifyTypes" id="sms" value="SMS"/><label for="sms">短信</label>' +
        '<input type="checkbox" name="notifyTypes" id="email" value="MAIL"/><label for="email">邮件</label>' +
        '</div>' +
        '</div>' +
        '<div class="business-table-item">' +
        '<div class="business-table-label">默认提醒方式</div>' +
        '<div class="business-table-value">' +
        '<span class="checkboxIM" style="display:none;"><input type="checkbox" name="defaultNotifyTypes" id="defaultNotifyMethod1" value="IM"/><label for="defaultNotifyMethod1">在线消息</label></span>' +
        '<span class="checkboxSMS" style="display:none;"><input type="checkbox" name="defaultNotifyTypes" id="defaultNotifyMethod2" value="SMS"/><label for="defaultNotifyMethod2">短信</label></span>' +
        '<span class="checkboxMAIL" style="display:none;"><input type="checkbox" name="defaultNotifyTypes" id="defaultNotifyMethod3" value="MAIL"/><label for="defaultNotifyMethod3">邮件</label></span>' +
        '</div>' +
        '</div>' +
        '<div class="business-table-item">' +
        '<div class="business-table-label">消息格式</div>' +
        '<div class="business-table-value">' +
        '<div>' +
        '<input type="text" name="notifyMsgUuid" id="notifyMsgUuid"/>' +
        '</div>' +
        // '<div class="error" style="display:none">消息格式不能为空!</div>' +
        '</div>' +
        '</div>' +
        '</div>' +
        '<div class="business-title">签收提醒' +
        '<div class="read-tooltip">' +
        '<i class="iconfont icon-ptkj-tishishuoming"></i>' +
        '<div class="read-tooltip-content">对有签收时限要求的文档，设置签收提醒</div>' +
        '</div>' +
        '</div>' +
        '<div class="business-form-table">' +
        '<div class="business-table-item">' +
        '<div class="business-table-label">到期前</div>' +
        '<div class="business-table-value value-flex">' +
        '提前' +
        '<div class="w75 ml5"><input type="text" name="signBeforeNum" id="signBeforeNum"/></div>' +
        '<div class="w100 ml5 mr5"><input type="text" name="signBeforeUnit" id="signBeforeUnit"/></div>' +
        '提醒收件人' +
        '</div>' +
        '</div>' +
        '<div class="business-table-item">' +
        '<div class="business-table-label">消息格式</div>' +
        '<div class="business-table-value"><input type="text" name="signBeforeMsgUuid" id="signBeforeMsgUuid"/></div>' +
        '</div>' +
        '<div class="business-table-item">' +
        '<div class="business-table-label">逾期后</div>' +
        '<div class="business-table-value value-flex">' +
        '每隔' +
        '<div class="w75 ml5"><input type="text" name="signAfterNum" id="signAfterNum"/></div>' +
        '<div class="w100 ml5 mr5"><input type="text" name="signAfterUnit" id="signAfterUnit"/></div>' +
        '提醒收件人，共' +
        '<div class="w75 ml5 mr5"><input type="text" name="signAfterFrequency" id="signAfterFrequency"/></div>' +
        '次。' +
        '</div>' +
        '</div>' +
        '<div class="business-table-item">' +
        '<div class="business-table-label">消息格式</div>' +
        '<div class="business-table-value"><input type="text" name="signAfterMsgUuid" id="signAfterMsgUuid"/></div>' +
        '</div>' +
        '</div>' +
        '<div class="business-title">反馈提醒' +
        '<div class="read-tooltip">' +
        '<i class="iconfont icon-ptkj-tishishuoming"></i>' +
        '<div class="read-tooltip-content">对有反馈时限要求的文档，设置反馈提醒</div>' +
        '</div>' +
        '</div>' +
        '<div class="business-form-table">' +
        '<div class="business-table-item">' +
        '<div class="business-table-label">到期前</div>' +
        '<div class="business-table-value value-flex">' +
        '提前' +
        '<div class="w75 ml5"><input type="text" name="feedbackBeforeNum" id="feedbackBeforeNum"/></div>' +
        '<div class="w100 ml5 mr5"><input type="text" name="feedbackBeforeUnit" id="feedbackBeforeUnit"/></div>' +
        '提醒收件人' +
        '</div>' +
        '</div>' +
        '<div class="business-table-item">' +
        '<div class="business-table-label">消息格式</div>' +
        '<div class="business-table-value"><input type="text" name="feedbackBeforeMsgUuid" id="feedbackBeforeMsgUuid"/></div>' +
        '</div>' +
        '<div class="business-table-item">' +
        '<div class="business-table-label">逾期后</div>' +
        '<div class="business-table-value value-flex">' +
        '每隔' +
        '<div class="w75 ml5"><input type="text" name="feedbackAfterNum" id="feedbackAfterNum"/></div>' +
        '<div class="w100 ml5 mr5"><input type="text" name="feedbackAfterUnit" id="feedbackAfterUnit"/></div>' +
        '提醒收件人，共' +
        '<div class="w75 ml5 mr5"><input type="text" name="feedbackAfterFrequency" id="feedbackAfterFrequency"/></div>' +
        '次。' +
        '</div>' +
        '</div>' +
        '<div class="business-table-item">' +
        '<div class="business-table-label">消息格式</div>' +
        '<div class="business-table-value"><input type="text" name="feedbackAfterMsgUuid" id="feedbackAfterMsgUuid"/></div>' +
        '</div>' +
        '</div>' +
        '</div>' +
        '</div>' +
        '</form>' +
        '<div class="business-box-btn"><button id="saveBusinessBtn" type="button" class="well-btn w-btn-primary">保存</button></div>' +
        '</div>' +
        '</div>';
      return html;
    }

    configurer.prototype.initActionInfo = function (configuration, $container) {
      var document = configuration.document || {};
      // 设置值
      designCommons.setElementValue(document, $container, 'query');

      var buttonData = document.buttons ? document.buttons : [];
      var piUuid = this.component.pageDesigner.getPiUuid();
      var system = appContext.getCurrentUserAppData().getSystem();
      var productUuid = system.productUuid;
      if (system != null && system.piUuid != null) {
        piUuid = system.piUuid;
      }

      // js模块二开
      $('#jsModule', $container).wSelect2({
        serviceName: 'appJavaScriptModuleMgr',
        params: {
          dependencyFilter: 'DmsDocExchangeDocumentView'
        },
        labelField: 'jsModuleName',
        valueField: 'jsModule',
        defaultBlank: true,
        remoteSearch: false
      });

      // 按钮定义
      var $buttonInfoTable = $('#table_button_info', $container);
      // 按钮定义上移事件
      formBuilder.bootstrapTable.addRowUpButtonClickEvent({
        tableElement: $buttonInfoTable,
        button: $('#btn_row_up_button', $container)
      });
      // 按钮定义下移事件
      formBuilder.bootstrapTable.addRowDownButtonClickEvent({
        tableElement: $buttonInfoTable,
        button: $('#btn_row_down_button', $container)
      });
      // 按钮定义添加一行事件
      formBuilder.bootstrapTable.addAddRowButtonClickEvent({
        tableElement: $buttonInfoTable,
        button: $('#btn_add_button', $container),
        bean: {
          checked: false,
          uuid: '',
          code: '',
          text: '',
          group: '',
          cssClass: 'btn-default'
        }
      });
      // 按钮定义删除一行事件
      formBuilder.bootstrapTable.addDeleteRowButtonClickEvent({
        tableElement: $buttonInfoTable,
        button: $('#btn_delete_button', $container)
      });
      //新增的情况下，把文档交换器支持的按钮都列出来供选择
      if ($.isEmptyObject(configuration)) {
        buttonData = this.allSupportDocExchangeEventAction();
      }

      for (var i = 0; i < buttonData.length; i++) {
        var btn = buttonData[i];
        if (typeof btn.btnLib === 'undefined') {
          btn.btnLib = _createdBtnLibObj(btn.code);
        }
        if (typeof btn.uuid === 'undefined') {
          btn.uuid = UUID.createUUID();
        }
      }

      $buttonInfoTable.bootstrapTable('destroy').bootstrapTable({
        data: buttonData,
        idField: 'uuid',
        showColumns: true,
        striped: true,
        width: 500,
        onEditableHidden: onEditHidden,
        toolbar: $('#div_button_info_toolbar', $container),
        columns: [
          {
            field: 'checked',
            checkbox: true,
            formatter: checkedFormat
          },
          {
            field: 'uuid',
            title: 'UUID',
            visible: false
          },
          {
            field: 'text',
            title: '名称',
            width: 100,
            editable: {
              type: 'text',
              mode: 'inline',
              showbuttons: false,
              onblur: 'submit',
              validate: function (value) {
                if (StringUtils.isBlank(value)) {
                  return '请输入名称!';
                }
              }
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
            field: 'group',
            title: '组别',
            width: 80,
            visible: false,
            editable: {
              type: 'text',
              mode: 'inline',
              showbuttons: false,
              onblur: 'submit'
            }
          },
          // {
          //   field: 'cssClass',
          //   title: '样式',
          //   width: 80,
          //   editable: {
          //     type: 'select',
          //     mode: 'inline',
          //     onblur: 'submit',
          //     showbuttons: false,
          //     source: function () {
          //       return $.map(constant.WIDGET_COLOR, function (val) {
          //         return {
          //           value: 'btn-' + val.value,
          //           text: val.name
          //         };
          //       });
          //     }
          //   }
          // },
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
            field: 'eventHandler',
            title: '操作处理',
            width: 200,
            editable: {
              onblur: 'ignore',
              type: 'wCustomForm',
              placement: 'left',
              savenochange: true,
              value2input: designCommons.bootstrapTable.eventHandler.value2input,
              input2value: designCommons.bootstrapTable.eventHandler.input2value,
              validate: designCommons.bootstrapTable.eventHandler.validate,
              value2display: designCommons.bootstrapTable.eventHandler.value2display
            }
          }
        ]
      });
    };

    configurer.prototype.allSupportDocExchangeEventAction = function () {
      return [
        {
          uuid: UUID.createUUID(),
          text: '发送',
          code: 'send',
          cssClass: 'btn-default',
          eventHandler: {
            id: '77e673a85dd49f7918abdb7c7679a354',
            name: '功能: 数据管理_操作功能_文档交换_发送',
            type: 4,
            path: '/pt-mgr/pt-dms-mgr/document_exchange/btn_send_doc_exchanger'
          }
        },
        {
          uuid: UUID.createUUID(),
          text: '保存',
          code: 'save',
          cssClass: 'btn-default',
          eventHandler: {
            id: 'aacb2eb01c6d2328e64e2719673fefd0',
            name: '功能: 数据管理_操作功能_文档交换_保存',
            type: 4,
            path: '/pt-mgr/pt-dms-mgr/document_exchange/btn_save_doc_exchanger'
          }
        },
        {
          uuid: UUID.createUUID(),
          text: '催办',
          code: 'urge_doc',
          cssClass: 'btn-default',
          eventHandler: {
            id: '254068eaa5cd94ab236f764fb1712636',
            name: '功能: 数据管理_操作功能_文档交换_催办',
            type: 4,
            path: '/pt-mgr/pt-dms-mgr/document_exchange/btn_urge_doc_exchanger'
          }
        },
        {
          uuid: UUID.createUUID(),
          text: '办结',
          code: 'finish',
          cssClass: 'btn-default',
          eventHandler: {
            id: '609cd9fd731af53ce0e8694444dd146e',
            name: '功能: 数据管理_操作功能_文档交换_办结',
            type: 4,
            path: '/pt-mgr/pt-dms-mgr/document_exchange/btn_finish_doc_exchanger'
          }
        },
        {
          uuid: UUID.createUUID(),
          text: '撤回',
          code: 'revoke',
          cssClass: 'btn-default',
          eventHandler: {
            id: 'e2a547cf89e13498d4184135eaeb822c',
            name: '功能: 数据管理_操作功能_文档交换_撤回',
            type: 4,
            path: '/pt-mgr/pt-dms-mgr/document_exchange/btn_revoke_doc_exchanger'
          }
        },
        {
          uuid: UUID.createUUID(),
          text: '补充发送',
          code: 'extra_send',
          cssClass: 'btn-default',
          eventHandler: {
            id: 'dd3a2279f8a6592eefb741ad643e4cea',
            name: '功能: 数据管理_操作功能_文档交换_补充发送',
            type: 4,
            path: '/pt-mgr/pt-dms-mgr/document_exchange/btn_extra_send_doc_exchanger'
          }
        },
        {
          uuid: UUID.createUUID(),
          text: '转发',
          code: 'forward',
          cssClass: 'btn-default',
          eventHandler: {
            id: 'd2cb23d06781991f5a2e43cadb27d586',
            name: '功能: 数据管理_操作功能_文档交换_转发',
            type: 4,
            path: '/pt-mgr/pt-dms-mgr/document_exchange/btn_forward_doc_exchanger'
          }
        },
        {
          uuid: UUID.createUUID(),
          text: '退回',
          code: 'back',
          cssClass: 'btn-default',
          eventHandler: {
            id: 'e27e2c7704c31be9c3833c0b6f911822',
            name: '功能: 数据管理_操作功能_文档交换_退回',
            type: 4,
            path: '/pt-mgr/pt-dms-mgr/document_exchange/btn_return_doc_exchanger'
          }
        },
        {
          uuid: UUID.createUUID(),
          text: '反馈意见',
          code: 'feedback',
          cssClass: 'btn-default',
          eventHandler: {
            id: '524ae43f5bc4f7fc2635d57837018bbc',
            name: '功能: 数据管理_操作功能_文档交换_反馈意见',
            type: 4,
            path: '/pt-mgr/pt-dms-mgr/document_exchange/btn_feedback_doc_exchanger'
          }
        },
        {
          uuid: UUID.createUUID(),
          text: '保存并验证',
          code: 'saveAndValidate',
          cssClass: 'btn-default',
          eventHandler: {
            id: '8439b211ff88b58a0bb4cfc692721e7f',
            name: '功能: 数据管理_操作功能_文档交换_保存并验证',
            type: 4,
            path: '/pt-mgr/pt-dms-mgr/document_exchange/btn_save_and_validate_doc_exchanger'
          }
        }
      ];
    };

    configurer.prototype.initVisualizationInfo = function (configuration, $container) {
      var _self = this;
      var view = configuration.view || {};
      // 设置值
      designCommons.setElementValue(view, $container, 'query');
      var appPageUuid = _self.component.pageDesigner.getPageUuid();

      $('#dataViewName', $container)
        .on('change', function () {
          var isChecked = $(this).prop('checked');
          $('.dataView')[isChecked ? 'show' : 'hide']();
        })
        .trigger('change');
      // 视图组件
      $('#listViewId', $container).wSelect2({
        serviceName: 'appWidgetDefinitionMgr',
        labelField: 'listViewName',
        valueField: 'listViewId',
        params: {
          wtype: 'wBootstrapTable',
          appPageUuid: appPageUuid,
          uniqueKey: 'id',
          includeWidgetRef: 'true'
        },
        remoteSearch: false
      });

      $('#listViewId', $container)
        .on('change', function () {
          var v = $(this).val();
          //获取表格中定义的数据仓库的数据字段
          if (v) {
            var tableConfiguration = JSON.parse(appContext.getWidgetDefinition(v).definitionJson).configuration;
            var columnDatas = [];
            for (var i = 0, len = tableConfiguration.columns.length; i < len; i++) {
              columnDatas.push({
                id: tableConfiguration.columns[i].name,
                text: tableConfiguration.columns[i].header
              });
            }
            $('#docExchangeUuidColumnTitle', $container).wSelect2({
              data: columnDatas,
              labelField: 'docExchangeUuidColumnTitle',
              valueField: 'docExchangeUuidColumn',
              remoteSearch: false
            });

            $('.bootstrapTableDataViewColumns').show();
          } else {
            $('.bootstrapTableDataViewColumns').hide();
          }
        })
        .trigger('change');
    };

    configurer.prototype.getListViewTableColumnData = function () {
      var colDatas = [];
      if ($('#listViewId').val() && appPageDesigner[$('#listViewId').val()]) {
        var columnTableData = appPageDesigner[$('#listViewId').val()].options.configuration.columns;
        var j = 1;
        for (var i = 0; i < columnTableData.length; i++) {
          if (columnTableData[i].hidden == 0) {
            colDatas.push({
              value: j + '',
              id: j + '',
              text: '[第' + j++ + '列] ' + columnTableData[i].header
            });
          }
        }
      }
      return colDatas;
    };

    function _getButtonPresettingByCode(code) {
      var presettingMap = {
        send: {
          type: 'primary',
          iconfont: 'icon-ptkj-tijiaofabufasong'
        },
        save: {
          type: 'primary',
          iconfont: 'icon-ptkj-baocun'
        },
        urge_doc: {
          type: 'line',
          iconfont: 'icon-ptkj-cuibanjian'
        },
        finish: {
          type: 'line',
          iconfont: 'icon-xmch-yibangongzuo'
        },
        revoke: {
          type: 'line',
          iconfont: 'icon-oa-chehui'
        },
        extra_send: {
          type: 'line',
          iconfont: 'icon-ptkj-buchongfasong'
        },
        forward: {
          type: 'line',
          iconfont: 'icon-oa-zhuanban'
        },
        back: {
          type: 'line',
          iconfont: 'icon-oa-zhijietuihui'
        },
        feedback: {
          type: 'line',
          iconfont: 'icon-ptkj-wentifankui'
        },
        saveAndValidate: {
          type: 'line',
          iconfont: 'icon-ptkj-baocunbingyanzheng'
        }
      };

      return presettingMap[code];
    }

    function _createdBtnLibObj(code) {
      var presetting = _getButtonPresettingByCode(code);
      var type = presetting ? presetting.type : 'line';
      var iconfont = presetting ? presetting.iconfont : '';

      return {
        btnSize: '',
        btnColor: 'w-btn-primary',
        iconInfo: {
          fileType: 3,
          fileIDs: 'iconfont ' + iconfont
        },
        btnInfo: {
          type: 'line',
          class: type === 'line' ? 'w-line-btn' : '',
          status: [
            {
              class: '',
              text: '普通状态'
            },
            {
              class: 'hover',
              text: '鼠标移入状态'
            },
            {
              class: 'active',
              text: '点击状态'
            },
            {
              class: 'w-disable-btn',
              text: '禁用状态'
            }
          ]
        }
      };
    }

    configurer.prototype.initDataMarkDefine = function (configuration, $container) {
      var markDataDefine = configuration.markDataDefine;
      var _self = this;
      designCommons.setElementValue(markDataDefine, $container);
      $('.enableDataMark', $container)
        .on('change', function () {
          if ($(this).is(':checked')) {
            $('.' + $(this).attr('id'))
              .filter(':not(.mark_data_type)')
              .show();
          } else {
            $('.' + $(this).attr('id')).hide();
            clearInputValue($('.' + $(this).attr('id')));
            $('.' + $(this).attr('id')).css('display', 'none');
            $('.' + $(this).attr('id'))
              .find('.iconDelete')
              .trigger('click');
            $('.' + $(this).attr('id'))
              .find('.markDataType')
              .trigger('change');
          }
        })
        .trigger('change');
      $('#labelModuleId')
        .on('change', function () {
          var moduleId = appContext.getCurrentUserAppData().appData.module ? appContext.getCurrentUserAppData().appData.module.id : null;
          if (!moduleId && appContext.getCurrentUserAppData().appData.appPath) {
            moduleId = appContext.getCurrentUserAppData().appData.appPath.split('/')[2];
          }
          $('#labelModuleId').val(moduleId);
        })
        .trigger('change');

      //可视化视图变更，触发标签展示列、按钮的变更
      $('#listViewId', $container).on('change', function () {
        //展示列
        $('#columnNameForShowLabel', $container).wSelect2({
          data: _self.getListViewTableColumnData(),
          labelField: 'columnNameForShowLabel',
          valueField: 'columnIndexForShowLabel',
          remoteSearch: false
        });

        $('.renderMarkBtn').each(function () {
          var btnLabelField = $(this).attr('id');
          var btnValueField = $(this).next().attr('id');
          $(this).wSelect2({
            data: _self.getListViewTableButtonData(),
            labelField: btnLabelField,
            valueField: btnValueField,
            remoteSearch: false
          });
        });
      });
      $('#listViewId', $container).trigger('change');

      //标签关联实体类
      $('#lableRelaEntity', $container).wSelect2({
        serviceName: 'dmsDataLabelFacadeService',
        queryMethod: 'loadDataLabelRelaEntityData',
        labelField: 'lableRelaEntity',
        valueField: 'lableRelaEntityName',
        remoteSearch: false
      });

      _self.initStatusMarkDefine(configuration, $container);
    };

    //初始化状态标记
    configurer.prototype.initStatusMarkDefine = function (configuration, $container) {
      var _self = this;
      // 状态标记表格
      var $statusMarkTableInfo = $('#table_status_mark_info', $container);

      var statusMarkRowBean = {
        checked: false,
        uuid: '',
        text: '',
        markDataType: '', //标记数据类型
        markStyle: '', //标记样式
        unmarkStyle: '', //取消标记样式
        markButton: '', //标记事件按钮
        unmarkButton: '', //取消标记事件按钮
        markBtnTrigger: '', //其他触发
        unmarkBtnTrigger: ''
      };
      //定义添加，删除，上移，下移4按钮事件
      formBuilder.bootstrapTable.initTableTopButtonToolbar('table_status_mark_info', 'status_mark', $container, statusMarkRowBean);
      var statusMarks =
        configuration.markDataDefine && configuration.markDataDefine.statusMarks ? configuration.markDataDefine.statusMarks : [];
      $statusMarkTableInfo.bootstrapTable('destroy').bootstrapTable({
        data: statusMarks,
        idField: 'uuid',
        showColumns: true,
        striped: true,
        width: 500,
        onEditableHidden: onEditHidden,
        toolbar: $('#div_status_mark_toolbar', $container),
        columns: [
          {
            field: 'checked',
            checkbox: true,
            formatter: checkedFormat
          },
          {
            field: 'uuid',
            title: 'UUID',
            visible: false
          },
          {
            field: 'text',
            title: '名称',
            width: 50,
            editable: {
              type: 'text',
              mode: 'inline',
              showbuttons: false,
              onblur: 'submit',
              validate: function (value) {
                if (StringUtils.isBlank(value)) {
                  return '请输入名称!';
                }
              }
            }
          },
          {
            field: 'markDataType',
            title: '标记数据类型',
            width: 100,
            editable: {
              onblur: 'cancel',
              type: 'wCustomForm',
              placement: 'right',
              renderParams: {
                superEntityClass: 'com.wellsoft.pt.dms.entity.DataMarkEntity'
              },
              savenochange: true,
              value2input: designCommons.bootstrapTable.markTableOrEntity.value2input,
              input2value: designCommons.bootstrapTable.markTableOrEntity.input2value,
              value2display: designCommons.bootstrapTable.markTableOrEntity.value2display,
              value2html: designCommons.bootstrapTable.markTableOrEntity.value2html
            }
          },
          {
            field: 'markStyle',
            title: '标记样式',
            width: 100,
            editable: {
              onblur: 'ignore',
              type: 'wCustomForm',
              placement: 'right',
              savenochange: true,
              iconSelectTypes: [3],
              value2input: designCommons.bootstrapTable.tableRowStyle.value2input,
              input2value: designCommons.bootstrapTable.tableRowStyle.input2value,
              value2display: designCommons.bootstrapTable.tableRowStyle.value2display,
              value2html: designCommons.bootstrapTable.tableRowStyle.value2html
            }
          },
          {
            field: 'unmarkStyle',
            title: '取消标记样式',
            width: 100,
            editable: {
              onblur: 'ignore',
              type: 'wCustomForm',
              placement: 'bottom',
              savenochange: true,
              iconSelectTypes: [3],
              value2input: designCommons.bootstrapTable.tableRowStyle.value2input,
              input2value: designCommons.bootstrapTable.tableRowStyle.input2value,
              value2display: designCommons.bootstrapTable.tableRowStyle.value2display,
              value2html: designCommons.bootstrapTable.tableRowStyle.value2html
            }
          },
          {
            field: 'markButton',
            title: '标记事件处理',
            width: 50,
            editable: {
              type: 'select',
              mode: 'inline',
              onblur: 'submit',
              showbuttons: false,
              source: _self.getListViewTableButtonData()
            }
          },
          {
            field: 'unmarkButton',
            title: '取消标记事件处理',
            width: 50,
            editable: {
              type: 'select',
              mode: 'inline',
              onblur: 'submit',
              showbuttons: false,
              source: _self.getListViewTableButtonData()
            }
          },
          {
            field: 'markBtnTrigger',
            title: '关联标记事件处理',
            width: 100,
            editable: {
              onblur: 'cancel',
              type: 'wCustomForm',
              placement: 'left',
              savenochange: true,
              renderParams: {
                source: _self.getListViewTableButtonData()
              },
              value2input: designCommons.bootstrapTable.multiSelectButtons.value2input,
              input2value: designCommons.bootstrapTable.multiSelectButtons.input2value,
              value2display: designCommons.bootstrapTable.multiSelectButtons.value2display,
              value2html: designCommons.bootstrapTable.multiSelectButtons.value2html
            }
          },
          {
            field: 'unmarkBtnTrigger',
            title: '关联取消标记事件处理',
            width: 100,
            editable: {
              onblur: 'cancel',
              type: 'wCustomForm',
              placement: 'left',
              savenochange: true,
              renderParams: {
                source: _self.getListViewTableButtonData()
              },
              value2input: designCommons.bootstrapTable.multiSelectButtons.value2input,
              input2value: designCommons.bootstrapTable.multiSelectButtons.input2value,
              value2display: designCommons.bootstrapTable.multiSelectButtons.value2display,
              value2html: designCommons.bootstrapTable.multiSelectButtons.value2html
            }
          }
        ]
      });
    };

    configurer.prototype.getListViewTableButtonData = function () {
      var btnDatas = [];
      if ($('#listViewId').val() && appPageDesigner[$('#listViewId').val()]) {
        var buttonColumnData = appPageDesigner[$('#listViewId').val()].options.configuration.buttons;
        for (var i = 0; i < buttonColumnData.length; i++) {
          btnDatas.push({
            value: buttonColumnData[i].code,
            id: buttonColumnData[i].code,
            text: buttonColumnData[i].text
          });
        }
      }
      return btnDatas;
    };

    configurer.prototype.collectConfiguration = function ($container) {
      var configuration = {};
      // 基本信息
      this.collectBaseInfo(configuration, $container);
      // 文档定义
      this.collectDocDefine(configuration, $container);
      // 权限操作
      this.collectActionInfo(configuration, $container);
      //可视化
      this.collectVisualizationInfo(configuration, $container);

      this.collectMarkDefineInfo(configuration, $container);

      return $.extend({}, configuration);
    };
    configurer.prototype.collectBaseInfo = function (configuration, $container) {
      var $form = $('#widget_doc_exchanger_tabs_base_info', $container);
      var opt = designCommons.collectConfigurerData($form, collectClass);
      $.extend(configuration, opt);
    };
    configurer.prototype.collectDocDefine = function (configuration, $container) {
      var $form = $('#widget_doc_exchanger_tabs_doc_define', $container);
      var opt = designCommons.collectConfigurerData($form, collectClass);
      opt.auditFlow = Boolean(opt.auditFlow);
      if (!opt.auditFlow) {
        opt.flowUuid = '';
        opt.flowName = '';
      }
      opt.encrypt = Boolean(opt.encrypt);
      opt.urge = Boolean(opt.urge);
      opt.sign = Boolean(opt.sign);
      opt.feedback = Boolean(opt.feedback);
      configuration.store = configuration.store || {};
      if (!(opt.notifyTypes instanceof Array) && opt.notifyTypes) {
        opt.notifyTypes = [opt.notifyTypes];
      }
      if (!opt.notifyTypes) {
        delete opt.notifyTypes;
      }
      $.extend(configuration.store, opt);
    };
    configurer.prototype.collectActionInfo = function (configuration, $container) {
      var $form = $('#widget_doc_exchanger_tabs_action_info', $container);
      var opt = designCommons.collectConfigurerData($form, collectClass);
      opt.enableVersioning = Boolean(opt.enableVersioning);
      var $tableButtonInfo = $('#table_button_info', $container);
      var buttons = $tableButtonInfo.bootstrapTable('getData');
      opt.buttons = $.map(buttons, clearChecked);
      configuration.document = configuration.document || {};
      $.extend(configuration.document, opt);
    };
    configurer.prototype.collectVisualizationInfo = function (configuration, $container) {
      var $form = $('#widget_doc_exchanger_tabs_visualization_info', $container);
      var opt = designCommons.collectConfigurerData($form, collectClass);
      opt.dataViewName = $('#dataViewName').prop('checked') ? 'bootstrapTableDataView' : '';
      opt.dataViewType = $('#dataViewName').prop('checked') ? 'bootstrapTableDataView' : '';
      configuration.view = configuration.view || {};
      $.extend(configuration.view, opt);
    };

    configurer.prototype.collectMarkDefineInfo = function (configuration, $container) {
      var markDataDefine = designCommons.collectConfigurerData(
        $('#widget_data_management_viewer_tabs_label_info', $container),
        collectClass
      );
      markDataDefine.enableLabel = Boolean(markDataDefine.enableLabel);

      configuration.isValidate = true;

      if (markDataDefine.enableLabel && markDataDefine.columnIndexForShowLabel == '') {
        configuration.isValidate = false;
        appModal.error('展示列不允许为空！');
        return false;
      }

      if (markDataDefine.enableLabel && markDataDefine.labelBtnCode == '') {
        configuration.isValidate = false;
        appModal.error('标签按钮不允许为空！');
        return false;
      }

      if (markDataDefine.enableLabel && markDataDefine.lableRelaEntityName == '') {
        configuration.isValidate = false;
        appModal.error('标签数据关系实体不允许为空！');
        return false;
      }
      configuration.markDataDefine = configuration.markDataDefine || {};

      // 状态标记配置定义
      var $tableStatusMarkInfo = $('#table_status_mark_info', $container),
        statusMarks = [];

      if ($tableStatusMarkInfo.find('tr').length > 0) {
        statusMarks = $.map($tableStatusMarkInfo.bootstrapTable('getData'), clearChecked);
        for (var i = 0; i < statusMarks.length; i++) {
          var mk = statusMarks[i];
          if (StringUtils.isBlank(mk.text)) {
            configuration.isValidate = false;
            appModal.error('名称不允许为空！');
            return false;
          }
          if (StringUtils.isBlank(mk.markDataType)) {
            appModal.error('标记数据类型不允许为空！');
            configuration.isValidate = false;
            return false;
          }
        }
      }
      markDataDefine.statusMarks = statusMarks;
      $.extend(configuration.markDataDefine, markDataDefine);
    };

    return configurer;
  };

  // 返回组件定义
  component.prototype.getDefinitionJson = function () {
    var _self = this;
    var definitionJson = _self.options;
    definitionJson.id = _self.getId();
    definitionJson.items = [];
    var children = _self.getChildren();
    $.each(children, function (i) {
      var child = this;
      definitionJson.items.push(child.getDefinitionJson());
    });
    return definitionJson;
  };

  return component;
});
