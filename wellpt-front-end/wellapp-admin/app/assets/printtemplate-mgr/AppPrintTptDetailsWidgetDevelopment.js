define([
  'constant',
  'commons',
  'server',
  'appModal',
  'AppPtMgrDetailsWidgetDevelopment',
  'AppPtMgrCommons',
  'formBuilder',
  'printtemplateUtils',
  'ckeditor',
  'ztree'
], function (
  constant,
  commons,
  server,
  appModal,
  AppPtMgrDetailsWidgetDevelopment,
  AppPtMgrCommons,
  formBuilder,
  Printtemplate,
  ckeditor,
  ztree
) {
  var validator;
  var listView;
  // 平台管理_产品集成_打印模板详情_HTML组件二开
  var AppPrintTptDetailsWidgetDevelopment = function () {
    AppPtMgrDetailsWidgetDevelopment.apply(this, arguments);
  };

  // 接口方法
  commons.inherit(AppPrintTptDetailsWidgetDevelopment, AppPtMgrDetailsWidgetDevelopment, {
    // 组件初始化
    init: function () {
      var _self = this;
      // 验证器
      validator = server.Validation.validate({
        beanName: 'printTemplate',
        container: this.widget.element,
        wrapperForm: true
      });

      _self._formInputRender();
      _self._initPrintTemplateDesignTable();
      // 绑定事件
      _self._bindEvents();
    },

    _formInputRender: function () {
      var _self = this;
      var $container = this.widget.element;

      $("input[name='printInterval']", $container).on('change', function () {
        $('.multi_line', $container).hide();
        if ($("input[name='printInterval']:checked", $container).val() == 'multi_line') {
          $('.multi_line', $container).show();
        }
      });

      $('#isSaveSource', $container).on('change', function () {
        $('.isSaveSource', $container).hide();
        if ($(this).prop('checked')) {
          $('.isSaveSource', $container).show();
        }
      });

      $('#moduleId', $container).val(_self._moduleId());
      $('#moduleId', $container).prop('readonly', $('#moduleId', $container).val() != '');
      //模块选择
      $('#moduleId', $container).wSelect2({
        valueField: 'moduleId',
        remoteSearch: false,
        serviceName: 'appModuleMgr',
        queryMethod: 'loadSelectData'
      });
      // 分类
      $('#category', $container).comboTree({
        valueField: 'category',
        labelField: 'categoryName',
        treeSetting: {
          check: {
            enable: false
          },
          async: {
            otherParam: {
              serviceName: 'printTemplateCategoryService',
              methodName: 'getPrintTemplateCategoryTree',
              data: ['']
            }
          },
          callback: {
            onAsyncSuccess: function (event, treeId, treeNode, msg) {
              var zTree = $.fn.zTree.getZTreeObj('category_ztree');
              zTree.expandAll(true);
            }
          }
        },
        width: 220,
        height: 220,
        multiple: false,
        selectParent: true,
        autoInitValue: false,
        autoCheckByValue: true,
        refreshDataInEveryTime: true
      });

      $("input[name='templateType']", $container).on('change', function () {
        var ext = '';
        var val = $("input[name='templateType']:checked", $container).val();
        if (val == 'wordType') {
          ext = ['doc'];
        } else if (val == 'wordPoiType') {
          ext = ['docx'];
        } else if (val == 'htmlType') {
          ext = ['html'];
        } else {
          ext = ['xml'];
        }
        _self._initUploadPrintTemplateFile('', ext);
      });

      _self._initUploadPrintTemplateFile();
    },

    _initUploadPrintTemplateFile: function (value, ext) {
      var $container = this.widget.element;
      $('#printTemplateFileUpload', $container).empty();
      //上传模板文件
      formBuilder.buildFileUpload({
        container: $('#printTemplateFileUpload', $container),
        labelColSpan: '2',
        label: '上传模板文件',
        name: 'fileUuid',
        controlOption: {
          addFileText: '选择模板文件'
        },
        value: value,
        singleFile: true,
        ext: ext
      });
    },

    _moduleId: function () {
      return this.getWidgetParams().moduleId;
    },
    _bean: function () {
      return {
        uuid: null,
        type: null,
        name: null,
        id: null,
        code: null,
        templateType: null,
        printInterval: null,
        rowNumber: null,
        fileUuid: null,
        isSaveTrace: null,
        isReadOnly: null,
        isSaveSource: null,
        fileNameFormat: null,
        isSavePrintRecord: null,
        printContents: null,
        printContentsDel: [],
        version: null,
        keyWords: null,
        moduleId: this._moduleId(),
        category: null,
        categoryName: null
      };
    },

    _printTptDesignTableDataRefresh: function (rows) {
      $('#table_print_template_info', $(this.widget.element)).bootstrapTable('load', rows);
    },

    _desginLangOptions: function () {
      var options = [];
      for (var k in Printtemplate.langs) {
        options.push({
          value: k,
          text: Printtemplate.langs[k]
        });
      }
      return options;
    },

    _initPrintTemplateDesignTable: function () {
      var _self = this;
      var widget = _self.getWidget();
      var $container = $(widget.element);
      var $printTptTable = $('#table_print_template_info', $container);
      var langOptions = _self._desginLangOptions();

      formBuilder.bootstrapTable.initTableTopButtonToolbar(
        'table_print_template_info',
        'print_template',
        $container,
        {
          lang: 'zh-CN', //默认中文
          remark: '',
          operation: '',
          uuid: ''
        },
        'uuid'
      );

      $printTptTable.bootstrapTable('destroy').bootstrapTable({
        data: [],
        idField: 'uuid',
        pagination: false,
        striped: false,
        showColumns: false,
        toolbar: $('#div_print_template_toolbar', $container),
        onEditableHidden: function (field, row, $el, reason) {
          $el.closest('table').bootstrapTable('resetView');
        },
        width: 500,
        columns: [
          {
            field: 'checked',
            formatter: function (value, row, index) {
              if (value) {
                return true;
              }
              return false;
            },
            checkbox: true
          },
          {
            field: 'GUUID',
            title: 'GUUID',
            visible: false
          },
          {
            field: 'uuid',
            title: 'UUID',
            visible: false
          },
          {
            field: 'fileUuid',
            title: 'fileUuid',
            visible: false
          },
          {
            field: 'formUuid',
            title: 'formUuid',
            visible: false
          },
          {
            field: 'printContent',
            title: 'printContent',
            visible: false
          },
          {
            field: 'lang',
            title: '语言',
            editable: {
              type: 'select',
              mode: 'inline',
              showbuttons: false,
              onblur: 'submit',
              emptytext: '请选择',
              source: function () {
                return langOptions;
              }
            }
          },
          {
            field: 'remark',
            title: '备注',
            editable: {
              type: 'text',
              mode: 'inline',
              showbuttons: false,
              onblur: 'submit',
              savenochange: true
            }
          },
          {
            field: 'operation',
            title: '操作',
            formatter: function (value, row, index) {
              var $btn = $('<button>', {
                class: 'btn btn-sm printtemplate_operation',
                type: 'button',
                'data-index': index
              }).text('设计');
              return $btn[0].outerHTML;
            }
          }
        ]
      });

      $printTptTable.on('click', '.printtemplate_operation', function () {
        _self._designDialog($(this).attr('data-index'));
        return false;
      });
    },

    _designDialog: function (index) {
      var _self = this;
      var $dialog;
      var $container = this.widget.element;
      var name = $('#name', $container).val();
      var templateType = $('input[name=templateType]:checked', $container).val();
      if (!templateType) {
        appModal.info('请在基本信息栏位内选择模板类型');
        return false;
      }
      var rowData = $('#table_print_template_info').bootstrapTable('getData')[index];

      /*if (!rowData || !rowData.uuid) {
                    appModal.info('请先保存模板配置');
                    return false;
                }*/

      var dialogOpts = {
        title: '模板设计',
        message: '<div class="container-fluid design-dialog-container" style="max-height: 600px;overflow-y: auto;"></div>',
        buttons: {
          confirm: {
            label: '确定',
            className: 'btn-primary',
            callback: function (result) {
              var commitResult = false;
              if (templateType === 'htmlType') {
                var printContent = printContentEditor.getData();
                if (console && console.log) {
                  // beforeReplace
                  console.log('-----------------------beforeReplace---------------------------');
                  console.log(printContent);
                }
                printContent = printContent.replace(/<!--#list-->/gi, '</#list>');
                printContent = printContent.replace(/&lt;#list&gt;/gi, '</#list>');
                printContent = printContent.replace(/(\&lt;#list) ([\S]+) as ([\S]+) (\&gt;)/gi, function () {
                  return '<#list ' + arguments[2] + ' as ' + arguments[3] + '>';
                });
                printContent = printContent.replace(/(<!--#list) ([\S]+) as ([\S]+) (-->)/gi, function () {
                  return '<#list ' + arguments[2] + ' as ' + arguments[3] + '>';
                });
                if (console && console.log) {
                  // afterReplace
                  console.log('-----------------------afterReplace---------------------------');
                  console.log(printContent);
                }
                printContent = encodeURI(printContent);
                var $formCtl = $('#formId', $dialog);
                var formUuid = $formCtl.select2('data').id || $formCtl.val();
                $('#table_print_template_info', $container).bootstrapTable('updateRow', {
                  index: index,
                  row: {
                    fileUuid: null,
                    formUuid: formUuid,
                    printContent: printContent
                  }
                });
                commitResult = true;
              } else {
                if ($("input[name='contentFileUuid']", $dialog).val()) {
                  $('#table_print_template_info', $container).bootstrapTable('updateRow', {
                    index: index,
                    row: {
                      fileUuid: $("input[name='contentFileUuid']", $dialog).val(),
                      formUuid: null,
                      printContent: null
                    }
                  });
                }

                commitResult = true;
              }
              return commitResult;
            }
          },
          cancel: {
            label: '取消',
            className: 'btn-default',
            callback: function (result) {}
          }
        },
        shown: function () {
          if (templateType == 'htmlType') {
            _self._initPrintContentCkEditor(rowData, templateType, name, $dialog);
          } else {
            _self._initPrintTemplateDownloadHtml(rowData, $dialog);
          }
        },
        size: 'large'
      };
      $dialog = appModal.dialog(dialogOpts);
    },

    _initPrintTemplateDownloadHtml: function (rowData, $dialog) {
      var $dialogContainer = $('.design-dialog-container', $dialog);

      var $uploadDiv = $('<div>', { id: 'contentUploadFileDiv' });
      $dialogContainer.append($uploadDiv);

      //上传模板文件
      formBuilder.buildFileUpload({
        container: $('#contentUploadFileDiv', $dialog),
        labelColSpan: '2',
        label: '上传模板文件',
        name: 'contentFileUuid',
        controlOption: {
          addFileText: '选择模板文件'
        },
        value: rowData.fileUuid,
        singleFile: true
      });
    },

    _initFormTree: function (formId, $dialog) {
      var _self = this;
      var checkedPformId = null;
      var printContentEditor = $dialog.data('ck');
      var zNodes = [
        {
          id: 'root_zNdoes_field',
          pId: 0,
          name: '字段信息',
          open: true,
          isParent: true,
          nocheck: true
        },
        {
          id: 'root_zNdoes_subform',
          pId: 0,
          name: '从表信息',
          open: true,
          chkDisabled: true,
          isParent: true,
          nocheck: true
        }
      ];
      var pFormDefinition = null,
        fields = {},
        subforms = {};
      if (formId) {
        pFormDefinition = _self.loadFormDefinition(formId);
        fields = pFormDefinition.fields || {};
        for (var i in fields) {
          if (fields.hasOwnProperty(i)) {
            zNodes.push({
              id: i,
              formId: pFormDefinition.id || pFormDefinition.outerId,
              formUuid: pFormDefinition.uuid,
              pId: 'root_zNdoes_field',
              name: fields[i].displayName,
              open: true
            });
          }
        }

        subforms = pFormDefinition.subforms || {};
        for (var i in subforms) {
          if (subforms.hasOwnProperty(i)) {
            zNodes.push({
              id: i,
              data: subforms[i],
              formId: subforms[i].outerId || subforms[i].name,
              formUuid: subforms[i].formUuid,
              pId: 'root_zNdoes_subform',
              name: subforms[i].displayName || subforms[i].name,
              open: true
            });
          }
        }
      }

      var treeObj = $.fn.zTree.init(
        $('#formTree', $dialog),
        {
          check: {
            enable: true
          },
          data: {
            simpleData: {
              enable: true
            }
          },
          callback: {
            onDblClick: function (event, treeId, treeNode) {
              if (!treeNode || treeNode.isParent) {
                return;
              }
              if (treeNode.getParentNode().id == 'root_zNdoes_field') {
                // 选中的节点为字段
                printContentEditor.insertText('${dytable.' + treeNode.formId + '.' + treeNode.id + '}');
              } else if (treeNode.getParentNode().id == 'root_zNdoes_subform') {
                // 选中的节点为从表
                // return alert("不支持从表配置");
                printContentEditor.treeNode = treeNode;
                printContentEditor.formId = treeNode.formId;
                printContentEditor.formUuid = treeNode.formUuid;
                // ckeditor中弹出从表配置框，字段顺序，字段显示(表达式)，序号，从表变量，标题
                printContentEditor.execCommand('subtemplate'); // 弹出属性窗口
              }
            }
          }
        },
        zNodes
      );

      // 设置树的选中状态: 被引用则处于选中状态
      function checkedPform() {
        if (!pFormDefinition || !pFormDefinition.outerId) {
          return;
        }
        var htmlData = printContentEditor.getData() || '';
        for (var i in fields) {
          if (fields.hasOwnProperty(i)) {
            var checked = false;
            var treeNode = treeObj.getNodeByParam('id', i, null);
            if (htmlData.indexOf('${dytable.' + pFormDefinition.outerId + '.' + i + '}') > -1) {
              treeObj.setChkDisabled(treeNode, false);
              treeObj.checkNode(treeNode, true, false, false);
              treeObj.setChkDisabled(treeNode, true);
            } else {
              treeObj.setChkDisabled(treeNode, false);
              treeObj.checkNode(treeNode, false, false, false);
              treeObj.setChkDisabled(treeNode, true);
            }
          }
        }

        for (var i in subforms) {
          if (subforms.hasOwnProperty(i)) {
            var checked = false;
            var treeNode = treeObj.getNodeByParam('id', i, null);
            if (htmlData.indexOf('form-uuid="' + i + '"') > -1) {
              treeObj.setChkDisabled(treeNode, false);
              treeObj.checkNode(treeNode, true, false, false);
              treeObj.setChkDisabled(treeNode, true);
            } else {
              treeObj.setChkDisabled(treeNode, false);
              treeObj.checkNode(treeNode, false, false, false);
              treeObj.setChkDisabled(treeNode, true);
            }
          }
        }
        checkedPformId = window.setTimeout(checkedPform, 1000);
      }

      checkedPformId && clearTimeout(checkedPformId);
      checkedPform();
    },

    loadFormDefinition: function (uuid) {
      if (uuid == '' || uuid == null || typeof uuid == 'undefined' || uuid == 'undefined') {
        return null;
      }
      var definitionObj = null;
      var time1 = new Date().getTime();

      $.ajax({
        url: ctx + '/pt/dyform/definition/getFormDefinition',
        cache: false,
        async: false, // 同步完成
        type: 'POST',
        data: {
          formUuid: uuid,
          justDataAndDef: false
        },
        dataType: 'json',
        success: function (data) {
          definitionObj = data;
        },
        error: function (data) {
          // 加载定义失败
          if (data.status == 404) {
            appModal.info('表单不存在:formUuid=' + uuid);
            definitionObj = null;
          } else {
            appModal.info('表单定义加载失败,请重试');
            // window.close();//加载失败时关闭当前窗口,用户需重新点击
            definitionObj = null;
            throw new Error(data);
          }
        }
      });

      var time2 = new Date().getTime();
      console.log('加载定义所用时间:' + (time2 - time1) / 1000.0 + 's');
      return definitionObj;
    },

    _initPrintContentCkEditor: function (rowData, templateType, name, $dialog) {
      var _self = this;
      var $dialogContainer = $('.design-dialog-container', $dialog);
      $dialogContainer.append(
        $('<div>', { class: 'form-group' }).append(
          $('<div>', { class: 'col-sm-3 form-select-div' }).append(
            $('<input>', { id: 'formId', class: 'form-control', type: 'text' }),
            $('<ul>', { id: 'formTree', class: 'ztree' }),
            $('<span>', { class: 'help-block' }).text('注意：1、编辑器中选中位置，双击字段名称. 2、编辑器右击选择“删除表格”即可删除从表')
          ),
          $('<div>', { class: 'col-sm-9' }).append($('<textarea>', { id: 'printContent', name: 'printContent' }))
        )
      );
      var $formCtl = $('#formId', $dialog);

      $formCtl
        .on('change', function (event) {
          var formId = $formCtl.val();
          _self._initFormTree(formId, $dialog);
        })
        .wSelect2({
          remoteSearch: false,
          pageSize: 1000000,
          serviceName: 'formDefinitionService'
        });

      // 清除编辑器
      var name = 'printContent';
      // var printContentEditor = CKEDITOR.instances[name];
      _self._subtemplate();
      var printContentEditor = CKEDITOR.replace(name, {
        allowedContent: true,
        extraPlugins: 'subtemplate',
        enterMode: CKEDITOR.ENTER_P,
        height: '180px',
        width: '100%',
        // 工具栏
        toolbar: [
          ['Bold', 'Italic', 'Underline'],
          ['Cut', 'Copy', 'Paste'],
          ['NumberedList', 'BulletedList', '-'],
          ['JustifyLeft', 'JustifyCenter', 'JustifyRight', 'JustifyBlock'],
          ['Link', 'Unlink'],
          ['Format', 'Font', 'FontSize'],
          ['TextColor', 'BGColor'],
          ['Image', 'Table'],
          ['Source', 'Maximize']
        ],
        on: {
          instanceReady: function (ev) {
            $dialog.data('ck', printContentEditor);
          },
          change: function () {},
          // FIXME:编辑框放大效果
          /*afterCommandExec: function (event) {
                            var command = event.data && event.data.command;
                            if (command && command.name === "maximize") {
                                var $modalContent = $dialog.find('.modal-content');
                                var ckcontainer = event.editor.container.$;
                                if (command.state === 2) { // 最小化
                                    $modalContent.css({
                                        width: Number($modalContent.data('width')),
                                        height: Number($modalContent.data('height'))
                                    });
                                } else if (command.state === 1) { // 最大化
                                    if (!$modalContent.data('width')) {
                                        $modalContent.data('width', $modalContent.width());
                                        $modalContent.data('height', $modalContent.height());
                                    }
                                    var $ckeInner = $("div.cke_inner", ckcontainer);
                                    var treePanelWidth = $(".form-select-div",$dialogContainer).width();
                                    $ckeInner.css({
                                        left : 200,
                                        width : $ckeInner.width() -200
                                    });
                                    $modalContent.css({
                                        width: Number($("body").width()),
                                        height: Number($("body").height())
                                    });
                                }
                            }
                        },*/
          key: function () {}
        }
      });

      var printContent = rowData['printContent'];
      if (printContent == null || typeof printContent === 'undefined' || printContent.length <= 0) {
        $.ajax({
          type: 'GET',
          url: ctx + '/html/print/template/manager/print_content_html.html',
          dataType: 'html',
          async: false,
          success: function (html) {
            printContent = html;
          }
        });
      } else {
        printContent = decodeURI(printContent);
      }
      printContentEditor.setData(printContent);
      var formUuid = rowData['formUuid'];
      $formCtl.val(formUuid).trigger('change');
    },

    _uneditableForm: function (uneditable) {
      uneditable = uneditable == undefined ? true : Boolean(uneditable);
      AppPtMgrCommons.uneditableForm(
        {
          container: this.widget.element
        },
        uneditable
      );

      //表格不可编辑
      $('#div_print_template_toolbar', this.widget.element).show();
      if (uneditable) {
        $('#div_print_template_toolbar', this.widget.element).hide();
      }

      $('#table_print_template_info').each(function () {
        $(this).bootstrapTable('refreshOptions', {
          editable: !uneditable
        });
      });

      //保存按钮不可用
      $('#btn_save_printtemplate,#btn_save_printtemplate_new_version', this.widget.element).prop('disabled', uneditable);

      $('#moduleId', this.widget.element).prop('readonly', $('#moduleId').val() != '');
    },

    _bindEvents: function () {
      var _self = this;
      var widget = _self.getWidget();
      var $container = $(widget.element);
      var pageContainer = _self.getPageContainer();

      // 新增
      pageContainer.off('AppPrintTemplateListView.editRow');
      pageContainer.on('AppPrintTemplateListView.editRow', function (e) {
        // 清空表单
        AppPtMgrCommons.clearForm({
          container: $container,
          includeHidden: true
        });

        _self._uneditableForm(false);
        _self.printContentsUuid = [];
        $('#moduleId', $container).val(_self._moduleId());
        $('#moduleId', $container).trigger('change');

        _self._initUploadPrintTemplateFile();

        if (!e.detail.rowData) {
          //新增
          // 生成ID
          AppPtMgrCommons.idGenerator.generate($container.find('#id'), 'PRT_');
          $('#code', $container).val($('#id', $container).val().replace('PRT_', ''));
          // ID可编辑
          $('#id', $container).prop('readonly', false);
          // 分类清空
          $('#display_category', $container).find('.well-select-selected-value').val('').hide();
          _self._printTptDesignTableDataRefresh([]);
          $("input[name='printInterval']", $container).trigger('change');
          $('#isSaveSource', $container).trigger('change');
        } else {
          //编辑
          server.JDS.call({
            service: 'printTemplateService.getBeanByUuid',
            data: [e.detail.rowData.uuid],
            version: '',
            async: false,
            success: function (result) {
              if (result.success) {
                var bean = _self._bean();
                $.extend(bean, result.data);
                bean = result.data;

                _self._initUploadPrintTemplateFile(bean.fileUuid);
                AppPtMgrCommons.json2form({
                  json: bean,
                  container: $container
                });
                bean.categoryName
                  ? $('#category', $container).next('#display_category').find('.well-select-selected-value').html(bean.categoryName).show()
                  : $('#category', $container).next('#display_category').find('.well-select-selected-value').html('').hide();
                // ID只读
                $('#id', $container).prop('readonly', true);
                validator.form();
                $("input[name='printInterval']", $container).trigger('change');
                $('#isSaveSource', $container).trigger('change');
                $('#moduleId', $container).trigger('change');
                $('#moduleId', $container).prop('readonly', bean.moduleId != null);

                for (var i = 0, len = bean.printContents.length; i < len; i++) {
                  var printContent = bean.printContents[i];
                  _self.printContentsUuid.push(printContent.uuid);
                  if (typeof printContent === 'string' && printContent.length >= 0) {
                    bean.printContents[i].printContent = encodeURI(printContent);
                  } else {
                    bean.printContents[i].printContent = '';
                  }
                }
                _self._printTptDesignTableDataRefresh(bean.printContents);

                if (e.detail.rowData.isRef == 1) {
                  //被引用，不允许编辑
                  _self._uneditableForm();
                }
              }
            }
          });
        }

        // 显示第一个tab内容
        $('.nav-tabs>li>a:first', $container).tab('show');

        listView = e.detail.ui;
      });

      $('#btn_save_printtemplate', $container).on('click', function () {
        _self._save();
        return false;
      });

      $('#btn_save_printtemplate_new_version', $container).on('click', function () {
        _self._save(true);
        return false;
      });
    },

    _save: function (newVersion) {
      var _self = this;
      var $container = $(this.widget.element);
      var bean = _self._bean();
      AppPtMgrCommons.form2json({
        json: bean,
        container: $container
      });

      var printContents = $('#table_print_template_info', $container).bootstrapTable('getData');
      var delUuid = _self.printContentsUuid;

      $.each(printContents, function (index, item) {
        var i = delUuid.indexOf(item.uuid);
        if (i > -1) {
          delUuid.splice(i, 1);
        } else {
          item.uuid = null;
        }
      });

      bean.printContents = printContents;
      bean.printContentsDel = delUuid;

      if (!validator.form()) {
        return false;
      }
      if (bean.name.indexOf('/') > -1 || bean.name.indexOf('\\') > -1) {
        appModal.error('打印模板名称不要有/或\\');
        return false;
      }

      server.JDS.call({
        service: 'printTemplateService.saveBean',
        data: [bean, newVersion],
        version: '',
        success: function (result) {
          if (result.success) {
            appModal.success('保存成功！', function () {
              // 保存成功刷新列表
              listView.trigger('AppPrintTemplateListView.refresh');

              // 清空表单
              AppPtMgrCommons.clearForm({
                container: $container,
                includeHidden: true
              });
              $('#moduleId', $container).val(_self._moduleId());
              $('#moduleId', $container).trigger('change');
              $('#category', $container).next('#display_category').find('.well-select-selected-value').text('').hide();
              $('#category', $container).val('');
              $('#categoryName', $container).val('');
              _self._uneditableForm(false);
              _self._initUploadPrintTemplateFile();
              _self._printTptDesignTableDataRefresh([]);
            });
          }
        }
      });
    },

    _subtemplate: function () {
      // ckeditor插件
      var pluginName = 'subtemplate';
      CKEDITOR.plugins.add(pluginName, {
        requires: ['dialog'],
        init: function (editor) {
          // 定义"设置从表"对话框
          var dialogContent = $('#subtemplate-dialog').html();
          $('#subtemplate-dialog').remove();
          CKEDITOR.dialog.add(pluginName, function (editor) {
            return {
              title: '设置从表',
              minWidth: 480,
              minHeight: 320,
              contents: [
                {
                  id: 'subformProperty',
                  expand: true,
                  padding: 0,
                  elements: [
                    {
                      type: 'html',
                      style: 'width: 100%;',
                      id: 'subtemplate-dialog-element',
                      html: "<div id='subtemplate-dialog'>" + dialogContent + '</div>'
                    }
                  ]
                }
              ],
              onOk: function () {
                var $dialog = $('#subtemplate-dialog');
                var trs = $dialog.find('#sub-form-table tr:gt(0)');
                var formId = $dialog.find('#sub-form-id').val();
                var tableWidth = $dialog.find('#sub-width').val();
                var tableTitle = $dialog.find('#sub-title').val();
                var formUuid = $dialog.find('#sub-form-uuid').val();
                var tableHTML =
                  '<table align="center" style="border-bottom:1px solid #000;" form-id="' +
                  formId +
                  '" form-uuid="' +
                  formUuid +
                  '" width="' +
                  tableWidth +
                  '">';
                if (tableTitle && tableTitle.length > 0) {
                  tableHTML += '<tr class="title"><td colspan="' + trs.length + '">' + tableTitle + '</td></tr>';
                }
                var th = '<tr class="header">',
                  tb = '<tr class="field">';
                for (var i = 0; i < trs.length; i++) {
                  var align = $('td.align>input:checked', trs[i]).val();
                  var title = $('input[name=title]', trs[i]).val();
                  var width = $('input[name=width]', trs[i]).val();
                  var property = $('input[name=property]', trs[i]).val();
                  th += '<td align="center" width="' + width + '">' + title + '</td>';
                  tb += '<td align="' + align + '">' + property + '</td>';
                }
                th += '</tr>';
                tb += '</tr>';
                tableHTML += th;
                tableHTML += '<tr style="display:none;"><td><#list dytable.' + formId + ' as subform></td></tr>';
                tableHTML += tb;
                tableHTML += '<tr style="display:none;"><td></#list></td></tr>';
                tableHTML += '</table>';
                var element = CKEDITOR.dom.element.createFromHtml(tableHTML);
                if (editor.focusedDom != null) {
                  element.insertBefore(editor.focusedDom);
                  editor.focusedDom.remove();
                } else {
                  editor.insertElement(element);
                }
                editor.formId = null;
                editor.formUuid = null;
                editor.treeNode = null;
                editor.focusedDom = null;
              },
              onCancel: function () {
                // 退出窗口时清空属性窗口的缓存
                editor.formId = null;
                editor.formUuid = null;
                editor.treeNode = null;
                editor.focusedDom = null;
              },
              onShow: function () {
                var formId = editor.formId;
                var formUuid = editor.formUuid;
                if (!formId || !formUuid) {
                  return;
                }
                var focusedDom = editor.focusedDom; // ckeditor双击
                var subform = editor.treeNode && editor.treeNode.data; // 树双击
                var $dialog = $('#subtemplate-dialog');
                $dialog.find('#sub-form-table tr:gt(0)').remove();
                // 初始化事件
                var addRow = function (title, property, width, align) {
                  var tr = '<tr>';
                  tr += '<td class="title"><input name="title" value="' + (title || '') + '"/></td>';
                  tr += '<td class="property"><input name="property" value="' + (property || '') + '"/></td>';
                  tr += '<td class="width"><input name="width" value="' + (width || '') + '"/></td>';
                  // 对齐方式
                  var alignName = 'align' + $.guid++;
                  tr += '<td class="align">';
                  tr += '<label for="align-left">左</label><input type="radio" id="align-left" name="' + alignName + '" value="left" >';
                  tr +=
                    '<label for="align-center">中</label><input type="radio" id="align-center" name="' +
                    alignName +
                    '" value="center" checked="checked">';
                  tr += '<label for="align-right">右</label><input type="radio" id="align-right" name="' + alignName + '" value="right" >';
                  tr += '</td>';
                  tr += '</tr>';
                  var $selected = $dialog.find('#sub-form-table tr.selected');
                  if ($selected && $selected.length) {
                    $selected.after(tr);
                  } else {
                    $dialog.find('#sub-form-table').append(tr);
                  }
                  if (align) {
                    $dialog.find('#sub-form-table input[name=' + alignName + '][value=' + align + ']').attr('checked', 'checked');
                  }
                };
                if (!editor.eventInited) {
                  $dialog.on('click', 'button[id=addRow]', function (event) {
                    var $field = $dialog.find('#sub-form-fields>option:selected');
                    var fieldName = $field.val();
                    var fieldTitle = $field.text();
                    if (!fieldName && !fieldTitle) {
                      return alert('请选择字段');
                    } else if (fieldName === 'index' || fieldTitle === '序号') {
                      return addRow(fieldTitle, '${subform_index+1}');
                    }
                    addRow(fieldTitle, "${subform['" + fieldName + "']}");
                  });
                  $dialog.on('click', 'button[id=deleteRow]', function (event) {
                    $dialog.find('#sub-form-table tr.selected').remove();
                  });
                  $dialog.on('click', 'button[id=upRow]', function (event) {
                    var $selected = $dialog.find('#sub-form-table tr.selected');
                    if ($selected && $selected.length && $selected.prev('tr:not(.th)').length) {
                      $selected.after($selected.prev());
                    }
                  });
                  $dialog.on('click', 'button[id=downRow]', function (event) {
                    var $selected = $dialog.find('#sub-form-table tr.selected');
                    if ($selected && $selected.length && $selected.next('tr').length) {
                      $selected.before($selected.next('tr'));
                    }
                  });
                  $dialog.on('focus', '#sub-form-table input[name]', function (event) {
                    $dialog.find('#sub-form-table tr.selected').removeClass('selected');
                    $(event.target).closest('tr').addClass('selected');
                  });
                  editor.eventInited = true;
                }
                // 回填信息
                var tableTitle, tableWidth;
                if (focusedDom && focusedDom.is('table')) {
                  var $title = focusedDom.find('tr.title');
                  if ($title && $title.count() === 1) {
                    tableTitle = $title.getItem(0).getText();
                  }
                  tableWidth = focusedDom.getAttribute('width');
                  var ths = editor.focusedDom.find('tr.header>td');
                  var tbs = editor.focusedDom.find('tr.field>td');
                  if (ths.count() && ths.count() === tbs.count()) {
                    for (var i = 0; i < ths.count(); i++) {
                      var th = ths.getItem(i);
                      var tb = tbs.getItem(i);
                      var property = tb.getText();
                      var displayName = th.getText();
                      var width = th.getAttribute('width');
                      var align = tb.getAttribute('align');
                      addRow(displayName, property, width, align);
                    }
                  }
                } else if (subform && subform.fields) {
                  tableTitle = subform.displayName || subform.name;
                  var fields = [];
                  addRow('序号', '${subform_index+1}');
                  for (var fieldName in subform.fields) {
                    var field = subform.fields[fieldName];
                    if (field.hidden !== '1' || field.order == null) {
                      // 1显示，2隐藏
                      continue;
                    }
                    fields.push(field);
                  }
                  fields.sort(function (fa, fb) {
                    return fa.order - fb.order;
                  });
                  for (var i = 0; i < fields.length; i++) {
                    var field = fields[i];
                    addRow(field.displayName, "${subform['" + field.name + "']}");
                  }
                }
                $dialog.find('#sub-form-id').val(formId);
                $dialog.find('#sub-width').val(tableWidth);
                $dialog.find('#sub-title').val(tableTitle);
                $dialog.find('#sub-form-uuid').val(formUuid);
                // 加载字段
                var formDefinition = loadFormDefinition(formUuid);
                if (formDefinition && formDefinition.fields) {
                  var option = '<option value="index">序号</option>';
                  for (var fieleName in formDefinition.fields) {
                    var field = formDefinition.fields[fieleName];
                    option += '<option value="' + fieleName + '">' + field.displayName + '</option>';
                  }
                  $dialog.find('#sub-form-fields').append(option);
                }
              }
            };
          });
          // 定义命令，用于打开"设置从表"对话框
          editor.addCommand(pluginName, new CKEDITOR.dialogCommand(pluginName));
          // 定义双击事件
          editor.on('doubleclick', function (evt) {
            var focusedDomElement = evt.data.element; // element是CKEDITOR.dom.node类的对象
            var parentDomElement = focusedDomElement.getParents();
            if (parentDomElement != null && parentDomElement.length > 0) {
              for (var i = 0; i < parentDomElement.length; i++) {
                var _focusedDomElement = parentDomElement[i];
                var formId = _focusedDomElement.getAttribute('form-id');
                var formUuid = _focusedDomElement.getAttribute('form-uuid');
                if (_focusedDomElement.is('table') && formId && formUuid) {
                  editor.formId = formId;
                  editor.formUuid = formUuid;
                  editor.focusedDom = _focusedDomElement;
                  evt.data.dialog = pluginName;
                  break;
                }
              }
            }
          });
        }
      });
    }
  });
  return AppPrintTptDetailsWidgetDevelopment;
});
