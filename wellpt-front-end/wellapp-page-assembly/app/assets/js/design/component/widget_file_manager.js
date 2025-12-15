define(['ui_component', 'constant', 'commons', 'server', 'formBuilder', 'appContext', 'design_commons', 'comboTree'], function (
  ui_component,
  constant,
  commons,
  server,
  formBuilder,
  appContext,
  designCommons
) {
  var UUID = commons.UUID;
  var StringUtils = commons.StringUtils;
  var StringBuilder = commons.StringBuilder;
  var collectClass = 'w-configurer-option';
  var component = $.ui.component.BaseComponent();
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
    var configuration = options.configuration || {};
    var children = _self.getChildren();
    var id = _self.getId();
    var html = new StringBuilder();
    html.appendFormat('<div id="{0}" class="ui-wFileManager">', id);
    html.appendFormat('<div class="row file-manager">');
    if (configuration.showFolderNav) {
      html.appendFormat('<div class="scroll-wrapper"><div class="file-manager-nav"></div></div>');
      html.appendFormat('<div class="file-manager-view">');
    } else {
      html.appendFormat('<div class="col-xs-12 file-manager-view">');
    }
    if (children != null) {
      $.each(children, function (i) {
        var child = this;
        html.appendFormat(child.toHtml.call(child));
      });
    }
    html.appendFormat('</div>');
    html.appendFormat('</div>');
    html.appendFormat('</div>');
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
  var loadDyformFields = function () {
    if (!nameSource) {
      var source = [];
      var formUuid = $('#formUuid').val();
      server.JDS.call({
        service: 'dataManagementViewerComponentService.getDyformFieldDefinitionsByUuid',
        data: [formUuid],
        async: false,
        success: function (result) {
          nameSource = $.map(result.data, function (data) {
            return {
              value: data.displayName,
              text: data.displayName
            };
          });
        }
      });
    }
    return nameSource;
  };
  var loadOperator = function () {
    var operatorSource = [];
    server.JDS.call({
      service: 'viewComponentService.getQueryOperators',
      async: false,
      success: function (result) {
        if (result.msg == 'success') {
          operatorSource = result.data;
        }
      }
    });
    return operatorSource;
  };
  var fileActionsArray = [
    {
      id: 'createFolder',
      name: '新建文件夹'
    },
    {
      id: 'createFile',
      name: '上传文件'
    },
    {
      id: 'createDocument',
      name: '新建文档'
    },
    {
      id: 'share',
      name: '分享'
    },
    {
      id: 'download',
      name: '下载'
    },
    {
      id: 'delete',
      name: '删除'
    },
    {
      id: 'rename',
      name: '重命名'
    },
    {
      id: 'copy',
      name: '复制到'
    },
    {
      id: 'move',
      name: '移动到'
    },
    {
      id: 'viewAttributes',
      name: '查看属性'
    }
  ];
  var isExistsButton = function (btnCode, buttonData) {
    for (var i = 0; i < buttonData.length; i++) {
      if (buttonData[i].code === btnCode) {
        return true;
      }
    }
  };
  // 添加存在为草稿按钮
  var addSaveAsDraftButton = function (buttonData) {
    buttonData.push({
      uuid: UUID.createUUID(),
      text: '保存为草稿',
      code: 'btn_file_manager_dyform_save_as_draft',
      associatedFileAction: 'createDocument',
      associatedDocEditModel: '1',
      cssClass: 'btn-primary',
      hidden: '0',
      eventHandler: {
        id: 'c67a9a8c688aa003aa070bbaf4bc947a',
        name: '功能: 数据管理_操作功能_文件管理_表单数据_保存为草稿',
        path: '/pt-mgr/pt-base/pt-base-dms/btn_file_manager_dyform_save_as_draft',
        type: 4
      }
    });
  };
  // 添加存在为正搞按钮
  var addSaveAsNormalButton = function (buttonData) {
    buttonData.push({
      uuid: UUID.createUUID(),
      text: '保存为正稿',
      code: 'btn_file_manager_dyform_save_as_normal',
      associatedFileAction: 'createDocument',
      associatedDocEditModel: '1',
      cssClass: 'btn-primary',
      hidden: '0',
      eventHandler: {
        id: 'ca90328bd3f1cd84ee804973bd20bfe3',
        name: '功能: 数据管理_操作功能_文件管理_表单数据_保存为正稿',
        path: '/pt-mgr/pt-base/pt-base-dms/btn_file_manager_dyform_save_as_normal',
        type: 4
      }
    });
  };
  // 添加套打按钮
  var addAsPrintButton = function (buttonData) {
    buttonData.push({
      uuid: UUID.createUUID(),
      text: '套打',
      code: 'btn_file_manager_dyform_as_print',
      associatedFileAction: 'download',
      associatedDocEditModel: '0',
      cssClass: 'btn-primary',
      hidden: '0',
      eventHandler: {
        id: '70c687fc0880a03968cc51bb21ca8d97',
        name: '功能: 数据管理_操作功能_文件管理_表单数据_套打',
        path: '/pt-mgr/pt-base/pt-base-dms/btn_file_manager_dyform_as_print',
        type: 4
      }
    });
  };

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
      $('#widget_file_manager_tabs ul a', $container).on('click', function (e) {
        e.preventDefault();
        $(this).tab('show');
        var panelId = $(this).attr('href');
        $(panelId + ' .definition_info').bootstrapTable('resetView');
      });
      var _self = this.component;
      var configuration = $.extend(true, {}, options.configuration);
      this.initConfiguration(configuration, $container);
    };
    configurer.prototype.onOk = function ($container) {
      var _self = this;
      var component = _self.component;
      component.options.configuration = _self.collectConfiguration($container);
      return validateConfiguration(component.options.configuration);
    };

    configurer.prototype.initConfiguration = function (configuration, $container) {
      // 基本信息
      this.initBaseInfo(configuration, $container);
      // 数据源
      this.initDataSourceInfo(configuration, $container);
      // 文档信息
      this.initDocInfo(configuration, $('#widget_file_manager_tabs_doc_info', $container));
      // 视图按钮
      this.initButtonInfo(configuration, $('#widget_file_manager_tabs_button_info', $container));
      // 可视化
      this.initVisualizationInfo(configuration, $container);
    };
    configurer.prototype.initBaseInfo = function (configuration, $container) {
      // 设置值
      designCommons.setElementValue(configuration, $container, 'query');

      // 显示导航夹变更——显示根目录
      $('#showFolderNav', $container)
        .on('change', function () {
          if ($(this).attr('checked') == 'checked') {
            $('.show-root-folder', $container).show();
          } else {
            $('#showRootFolder', $container).attr('checked', false).trigger('change');
            $('.show-root-folder', $container).hide();
          }
        })
        .trigger('change');

      // 显示根目录变更——默认展开子夹、根目录显示名称
      $('#showRootFolder', $container)
        .on('change', function () {
          if ($(this).attr('checked') == 'checked') {
            $('.expand-root-folder', $container).show();
            $('.root-folder-display-name', $container).show();
          } else {
            $('.expand-root-folder', $container).hide();
            $('.root-folder-display-name', $container).hide();
            $('#expandRootFolder', $container).attr('checked', false);
          }
        })
        .trigger('change');

      $('#belongToFolderName').val(configuration.belongToFolderName);
      $('#belongToFolderUuid').val(configuration.belongToFolderUuid);
      // 分类
      // $("#categoryCode", $container).wSelect2({
      // 	serviceName : "dataDictionaryService",
      // 	params : {
      // 		type : "MODULE_CATEGORY"
      // 	},
      // 	labelField : "categoryName",
      // 	valueField : "categoryCode",
      // 	remoteSearch : false
      // });
      var belongToFolderSetting = {
        view: {
          showLine: false
        },
        check: {
          enable: true,
          chkStyle: 'radio'
        },
        async: {
          otherParam: {
            serviceName: 'fileManagerComponentService',
            methodName: 'getFolderTree',
            args: ''
          }
        }
      };
      $('#belongToFolderUuid').comboTree({
        labelField: 'belongToFolderName',
        valueField: 'belongToFolderUuid',
        treeSetting: belongToFolderSetting,
        autoInitValue: true,
        width: '100%',
        height: 260,
        mutiSelect: false,
        selectParent: true
      });
      // 产品集成信息-树设置
      // $("#belongToFolderUuid").wCommonComboTree({
      // 	service : "fileManagerComponentService.getFolderTree",
      // 	// serviceParams : "-1",
      // 	width : "100%",
      //    // labelField: 'belongToFolderName',
      // 	// valueField: 'belongToFolderUuid',
      // 	multiSelect : false, // 是否多选
      // 	parentSelect : true, // 父节点选择有效，默认无效
      // 	onAfterSetValue : function(event, self, value) {
      // 		if (self.options && self.options.valueNodes && self.options.valueNodes.length == 1) {
      // 			$("#belongToFolderName").val(self.options.valueNodes[0].name);
      // 			$("#belongToFolderUuid").val(self.options.valueNodes[0].id)
      // 		} else {
      // 			$("#belongToFolderName").val("");
      //            $("#belongToFolderUuid").val("");
      // 		}
      // 	}
      // });

      // 二开JS模块
      $('#jsModule', $container).wSelect2({
        serviceName: 'appJavaScriptModuleMgr',
        params: {
          dependencyFilter: 'FileManagerWidgetDevelopment'
        },
        labelField: 'jsModuleName',
        valueField: 'jsModule',
        remoteSearch: false,
        multiple: true
      });
    };
    configurer.prototype.initDataSourceInfo = function (configuration, $container) {
      var store = configuration.store || {};
      // 设置值
      designCommons.setElementValue(store, $container, 'query');

      // 类型
      $('#dataType', $container).wSelect2({
        serviceName: 'dataManagementViewerComponentService',
        queryMethod: 'getDataTypeSelectData',
        valueField: 'dataType',
        remoteSearch: false
      });
      $('.data-type', $container).hide();
      $('#dataType', $container)
        .on('change', function () {
          $('.data-type', $container).hide();
          var val = $(this).val();
          $('.data-type-' + val, $container).show();
        })
        .trigger('change');

      // 绑定表单选择的列信息
      var bindDyformColumns = function () {
        // 启用置顶
        var $stickDefinition = $('#div_stick_definition', $container);
        $('#stick', $container)
          .on('change', function () {
            if ($(this).is(':checked')) {
              $stickDefinition.show();
            } else {
              $stickDefinition.hide();
              clearInputValue($stickDefinition);
            }
          })
          .trigger('change');
        $('#stickStatusField', $container).wSelect2({
          serviceName: 'dataManagementViewerComponentService',
          queryMethod: 'getColumnsOfDyFormSelectData',
          labelField: 'stickStatusFieldName',
          valueField: 'stickStatusField',
          defaultBlank: true,
          remoteSearch: false,
          width: '100%',
          height: 250,
          params: {
            formUuid: $('#formUuid').val()
          }
        });
        $('#stickTimeField', $container).wSelect2({
          serviceName: 'dataManagementViewerComponentService',
          queryMethod: 'getColumnsOfDyFormSelectData',
          labelField: 'stickTimeFieldName',
          valueField: 'stickTimeField',
          defaultBlank: true,
          remoteSearch: false,
          width: '100%',
          height: 250,
          params: {
            formUuid: $('#formUuid').val()
          }
        });

        // 记录阅读记录
        var $readRecordDefinition = $('#div_readRecord_definition', $container);
        $('#readRecord', $container)
          .on('change', function () {
            if ($(this).is(':checked')) {
              $readRecordDefinition.show();
            } else {
              $readRecordDefinition.hide();
              clearInputValue($readRecordDefinition);
            }
          })
          .trigger('change');
        $('#readRecordField', $container).wSelect2({
          serviceName: 'dataManagementViewerComponentService',
          queryMethod: 'getColumnsOfDyFormSelectData',
          labelField: 'readRecordFieldName',
          valueField: 'readRecordField',
          defaultBlank: true,
          remoteSearch: false,
          width: '100%',
          height: 250,
          params: {
            formUuid: $('#formUuid').val()
          }
        });
      };

      // 动态表单
      $('#formName', $container).wSelect2({
        serviceName: 'dataManagementViewerComponentService',
        queryMethod: 'getDataTypeOfDyFormSelectData',
        selectionMethod: 'getDataTypeOfDyFormSelectDataByIds',
        labelField: 'formName',
        valueField: 'formUuid',
        defaultBlank: true,
        width: '100%',
        height: 250
      });
      $('#formName', $container).on('change', function () {
        $('#stickStatusField', $container).val('').trigger('change');
        $('#stickTimeField', $container).val('').trigger('change');
        $('#readRecordField', $container).val('').trigger('change');
        // 重新绑定表单选择的列信息
        bindDyformColumns();
      });

      // 数据源
      $('#dataStoreId', $container).wSelect2({
        serviceName: 'viewComponentService',
        queryMethod: 'loadSelectData',
        labelField: 'dataStoreName',
        valueField: 'dataStoreId',
        remoteSearch: false
      });

      // 绑定表单选择的列信息
      bindDyformColumns();
    };
    configurer.prototype.initDocInfo = function (configuration, $container) {
      var document = configuration.document || {};
      document.documentJsModule = document.jsModule;
      document.documentJsModuleName = document.jsModuleName;
      delete document.jsModule;
      delete document.jsModuleName;
      // 设置值
      designCommons.setElementValue(document, $container, 'query');

      var buttonData = document.buttons ? document.buttons : [];
      var piUuid = this.component.pageDesigner.getPiUuid();
      var system = appContext.getCurrentUserAppData().getSystem();
      var productUuid = system.productUuid;
      if (system != null && system.piUuid != null) {
        piUuid = system.piUuid;
      }

      // 操作处理拦截器
      $('#interceptors', $container).wSelect2({
        serviceName: 'dataManagementViewerComponentService',
        queryMethod: 'getInterceptorSelectData',
        labelField: 'interceptorNames',
        valueField: 'interceptors',
        remoteSearch: false,
        multiple: true
      });

      // 操作处理拦截器
      $('#documentJsModule', $container).wSelect2({
        serviceName: 'appJavaScriptModuleMgr',
        params: {
          dependencyFilter: 'DmsFileManagerDyformDocumentView'
        },
        labelField: 'documentJsModuleName',
        valueField: 'documentJsModule',
        defaultBlank: true,
        remoteSearch: false
      });

      // 按钮定义
      var $buttonInfoTable = $('#table_doc_button_info', $container);
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
          associatedFileAction: 'createDocument',
          associatedDocEditModel: '1',
          group: '',
          cssClass: 'btn-default'
        }
      });
      // 按钮定义删除一行事件
      formBuilder.bootstrapTable.addDeleteRowButtonClickEvent({
        tableElement: $buttonInfoTable,
        button: $('#btn_delete_button', $container)
      });

      // 默认添加保存、发布的按钮
      // btn_file_manager_dyform_save_as_draft
      if (!isExistsButton('btn_file_manager_dyform_save_as_draft', buttonData)) {
        addSaveAsDraftButton(buttonData);
      }
      // btn_file_manager_dyform_save_as_normal
      if (!isExistsButton('btn_file_manager_dyform_save_as_normal', buttonData)) {
        addSaveAsNormalButton(buttonData);
      }
      // btn_file_manager_dyform_as_print
      if (!isExistsButton('btn_file_manager_dyform_as_print', buttonData)) {
        addAsPrintButton(buttonData);
      }

      // 兼容旧的事件处理配置
      for (var i = 0, len = buttonData.length; i < len; i++) {
        if (!buttonData[i].eventManger) {
          buttonData[i].eventManger = {
            eventHandler: buttonData[i].eventHandler
          };
          if (buttonData[i].eventParams) {
            $.extend(buttonData[i].eventManger, buttonData[i].eventParams);
          }
        }
      }

      var associatedFileActions = $.map(fileActionsArray, function (data) {
        return {
          value: data.id,
          text: data.name
        };
      });

      $buttonInfoTable.bootstrapTable('destroy').bootstrapTable({
        data: buttonData,
        idField: 'uuid',
        showColumns: true,
        striped: true,
        width: 500,
        onEditableHidden: onEditHidden,
        toolbar: $('#div_doc_button_info_toolbar', $container),
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
            // }, {
            // 	field : "cssClass",
            // 	title : "样式",
            // 	width : 80,
            // 	editable : {
            // 		type : "select",
            // 		mode : "inline",
            // 		onblur : "submit",
            // 		showbuttons : false,
            // 		source : function() {
            // 			return $.map(constant.WIDGET_COLOR, function(val) {
            // 				return {
            // 					value : "btn-" + val.value,
            // 					text : val.name
            // 				}
            // 			});
            // 		}
            // 	}
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
          {
            field: 'associatedFileAction',
            title: '与文件库权限关联',
            width: 80,
            editable: {
              type: 'select',
              mode: 'inline',
              onblur: 'submit',
              showbuttons: false,
              source: [
                {
                  value: '',
                  text: ''
                }
              ].concat(associatedFileActions)
            }
          },
          {
            field: 'associatedDocEditModel',
            title: '关联单据状态',
            width: 80,
            visible: true,
            editable: {
              type: 'select',
              mode: 'inline',
              onblur: 'submit',
              showbuttons: false,
              source: [
                {
                  value: '2',
                  text: '全部'
                },
                {
                  value: '1',
                  text: '编辑模式'
                },
                {
                  value: '0',
                  text: '显示为文本'
                }
              ]
            }
          },
          {
            field: 'hidden',
            title: '隐藏',
            width: 80,
            editable: {
              type: 'select',
              mode: 'inline',
              showbuttons: false,
              source: [
                {
                  value: '1',
                  text: '是'
                },
                {
                  value: '0',
                  text: '否'
                }
              ]
            }
          },
          {
            field: 'eventManger',
            title: '事件管理',
            width: 100,
            editable: {
              mode: 'modal',
              onblur: 'ignore',
              type: 'wCustomForm',
              placement: 'left',
              savenochange: true,
              renderParams: {
                defineJs: false
              },
              value2input: designCommons.bootstrapTable.eventManager.value2input,
              input2value: designCommons.bootstrapTable.eventManager.input2value,
              validate: designCommons.bootstrapTable.eventManager.validate,
              value2display: designCommons.bootstrapTable.eventManager.value2display
            }
          }
        ]
      });
    };
    configurer.prototype.initButtonInfo = function (configuration, $container) {
      var _self = this;
      var view = configuration.view || {};
      // 权限按钮重定义
      var fileActions = view.fileActions ? view.fileActions : [];
      if (fileActions.length == 0) {
        $.each(fileActionsArray, function () {
          fileActions.push({
            uuid: UUID.createUUID(),
            name: this.name,
            code: this.id,
            associatedFileAction: this.id,
            group: '',
            cssClass: 'btn-primary',
            confirmMsg: ''
          });
        });
      } else {
        // 初始化旧数据
        $.each(fileActions, function (i, data) {
          if (StringUtils.isBlank(data.uuid)) {
            data.uuid = UUID.createUUID();
          }
          if (StringUtils.isNotBlank(data.id) && StringUtils.isBlank(data.code) && StringUtils.isBlank(data.associatedFileAction)) {
            data.code = data.id;
            data.associatedFileAction = data.id;
          }
        });
      }
      var associatedFileActions = $.map(fileActionsArray, function (data) {
        return {
          value: data.id,
          text: data.name
        };
      });
      var $fileActionsTable = $('#table_file_actions', $container);
      // 定义添加，删除，上移，下移4按钮事件
      var fileActionBean = {
        name: '',
        id: '',
        code: '',
        group: '',
        cssClass: '',
        confirmMsg: ''
      };
      formBuilder.bootstrapTable.initTableTopButtonToolbar('table_file_actions', 'button', $container, fileActionBean);
      $fileActionsTable.bootstrapTable('destroy').bootstrapTable({
        data: fileActions,
        idField: 'uuid',
        showColumns: false,
        striped: true,
        width: 500,
        onEditableHidden: onEditHidden,
        toolbar: $('#div_file_actions_toolbar', $container),
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
            field: 'name',
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
              validate: function (value) {
                if (StringUtils.isBlank(value)) {
                  return '请输入编码!';
                }
              }
            }
          },
          {
            field: 'id',
            title: '操作',
            visible: false,
            width: 80
          },
          {
            field: 'associatedFileAction',
            title: '关联文件库权限',
            width: 80,
            editable: {
              type: 'select',
              mode: 'inline',
              onblur: 'submit',
              showbuttons: false,
              source: associatedFileActions
            }
          },
          {
            field: 'group',
            title: '组别',
            width: 80,
            visible: true,
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
            field: 'hidden',
            title: '隐藏',
            width: 80,
            editable: {
              type: 'select',
              mode: 'inline',
              showbuttons: false,
              source: [
                {
                  value: '1',
                  text: '是'
                },
                {
                  value: '0',
                  text: '否'
                }
              ]
            }
          },
          {
            field: 'confirmMsg',
            title: '确认信息',
            width: 80,
            visible: false,
            editable: {
              type: 'text',
              mode: 'inline',
              showbuttons: false,
              onblur: 'submit'
            }
          },
          {
            field: 'target',
            title: '目标位置',
            width: 100,
            editable: {
              onblur: 'cancel',
              type: 'wCustomForm',
              placement: 'left',
              savenochange: true,
              value2input: designCommons.bootstrapTable.targePosition.value2input,
              value2display: designCommons.bootstrapTable.targePosition.value2display,
              inputCompleted: designCommons.bootstrapTable.targePosition.inputCompleted
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
              renderParams: {
                defineJs: false
              },
              value2input: designCommons.bootstrapTable.eventManager.value2input,
              input2value: designCommons.bootstrapTable.eventManager.input2value,
              validate: designCommons.bootstrapTable.eventManager.validate,
              value2display: designCommons.bootstrapTable.eventManager.value2display
            }
          }
        ]
      });
    };
    configurer.prototype.initVisualizationInfo = function (configuration, $container) {
      var _self = this;
      var view = configuration.view || {};
      // 设置值
      designCommons.setElementValue(view, $container, 'query');
      var appPageUuid = _self.component.pageDesigner.getPageUuid();
      // 数据展示
      $('#dataViewType', $container).wSelect2({
        serviceName: 'fileManagerComponentService',
        queryMethod: 'getDataViewSelectData',
        labelField: 'dataViewName',
        valueField: 'dataViewType',
        remoteSearch: false
      });
      // 数据展示
      $('#defaultDataViewType', $container).wSelect2({
        serviceName: 'fileManagerComponentService',
        queryMethod: 'getDefaultDataViewSelectData',
        labelField: 'defaultDataViewName',
        valueField: 'defaultDataViewType',
        remoteSearch: false
      });
      $('#dataViewType', $container)
        .on('change', function () {
          var value = $(this).val();
          $('.dataView').hide();
          if (StringUtils.isNotBlank(value)) {
            $('.' + value).show();
          }
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
      // 标签树组件
      $('#tagTreeId', $container).wSelect2({
        serviceName: 'appWidgetDefinitionMgr',
        queryMethod: 'loadSelectData',
        labelField: 'tagTreeName',
        valueField: 'tagTreeId',
        params: {
          wtype: 'wTagTree',
          appPageUuid: appPageUuid,
          uniqueKey: 'id'
        },
        remoteSearch: false
      });
      // 组织树组件
      $('#unitTreeId', $container).wSelect2({
        serviceName: 'appWidgetDefinitionMgr',
        queryMethod: 'loadSelectData',
        labelField: 'unitTreeName',
        valueField: 'unitTreeId',
        params: {
          wtype: 'wUnitTree',
          appPageUuid: appPageUuid,
          uniqueKey: 'id'
        },
        remoteSearch: false
      });
      // 左导航组件
      $('#leftSidebarTreeId', $container).wSelect2({
        serviceName: 'appWidgetDefinitionMgr',
        queryMethod: 'loadSelectData',
        labelField: 'leftSidebarTreeName',
        valueField: 'leftSidebarTreeId',
        params: {
          wtype: 'wLeftSidebar',
          appPageUuid: appPageUuid,
          uniqueKey: 'id'
        },
        remoteSearch: false
      });
    };

    configurer.prototype.collectConfiguration = function ($container) {
      var configuration = {};
      // 基本信息
      this.collectBaseInfo(configuration, $container);
      // 数据源
      this.collectDataSourceInfo(configuration, $container);
      // 文档信息
      this.collectDocInfo(configuration, $container);
      // 视图按钮
      this.collectButtonInfo(configuration, $container);
      // 可视化
      this.collectVisualizationInfo(configuration, $container);
      return $.extend({}, configuration);
    };
    configurer.prototype.collectBaseInfo = function (configuration, $container) {
      var $form = $('#widget_file_manager_tabs_base_info', $container);
      var opt = designCommons.collectConfigurerData($form, collectClass);
      opt.showFolderNav = Boolean(opt.showFolderNav);
      opt.showRootFolder = Boolean(opt.showRootFolder);
      opt.expandRootFolder = Boolean(opt.expandRootFolder);
      opt.showBreadcrumbNav = Boolean(opt.showBreadcrumbNav);
      $.extend(configuration, opt);
    };
    configurer.prototype.collectDataSourceInfo = function (configuration, $container) {
      var $form = $('#widget_file_manager_tabs_data_source_info', $container);
      var opt = designCommons.collectConfigurerData($form, collectClass);
      opt.stick = Boolean(opt.stick);
      opt.readRecord = Boolean(opt.readRecord);
      configuration.store = configuration.store || {};
      $.extend(configuration.store, opt);
    };
    configurer.prototype.collectDocInfo = function (configuration, $container) {
      var $form = $('#widget_file_manager_tabs_doc_info', $container);
      var opt = designCommons.collectConfigurerData($form, collectClass);
      opt.enableVersioning = Boolean(opt.enableVersioning);
      var $tableButtonInfo = $('#table_doc_button_info', $container);
      var buttons = $tableButtonInfo.bootstrapTable('getData');
      opt.buttons = $.map(buttons, clearChecked);
      for (var i = 0; i < opt.buttons.length; i++) {
        // 事件管理的各参数值提到上一层，兼容老代码
        if (!$.isEmptyObject(opt.buttons[i].eventManger)) {
          for (var ek in opt.buttons[i].eventManger) {
            opt.buttons[i][ek] = opt.buttons[i].eventManger[ek];
          }
        }
      }
      opt.jsModule = opt.documentJsModule;
      opt.jsModuleName = opt.documentJsModuleName;
      delete opt.documentJsModule;
      delete opt.documentJsModuleName;
      configuration.document = configuration.document || {};
      $.extend(configuration.document, opt);
    };
    configurer.prototype.collectButtonInfo = function (configuration, $container) {
      var opt = {};
      opt.fileActions = $container.find('#table_file_actions').bootstrapTable('getData');
      configuration.view = configuration.view || {};
      $.extend(configuration.view, opt);
    };
    configurer.prototype.collectVisualizationInfo = function (configuration, $container) {
      var $form = $('#widget_file_manager_tabs_visualization_info', $container);
      var opt = designCommons.collectConfigurerData($form, collectClass);
      configuration.view = configuration.view || {};
      $.extend(configuration.view, opt);
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
