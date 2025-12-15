define(['constant', 'commons', 'server', 'appContext', 'appModal', 'HtmlWidgetDevelopment'], function (
  constant,
  commons,
  server,
  appContext,
  appModal,
  HtmlWidgetDevelopment
) {
  var JDS = server.JDS;

  var AppDyformCommonFieldSetDevelopment = function () {
    HtmlWidgetDevelopment.apply(this, arguments);
  };
  // 平台管理_公共资源_表单字段编辑组件二开
  commons.inherit(AppDyformCommonFieldSetDevelopment, HtmlWidgetDevelopment, {
    init: function () {
      var _self = this;
      var bean = {
        uuid: null,
        creator: null,
        createTime: null,
        modifier: null,
        modifyTime: null,
        scope: null,
        moduleId: null,
        moduleName: null,
        displayName: null,
        name: null,
        definitionJson: null,
        controlType: null,
        controlTypeName: null,
        categoryUuid: null,
        categoryName: null,
        notes: null
      };
      _self.initSelect();
      var uuid = GetRequestParam().uuid || Browser.getQueryString('uuid');
      if (uuid) {
        getModuleById(uuid);
      }

      var $container = _self.getWidgetElement();
      function getModuleById(uuid) {
        $.ajax({
          type: 'POST',
          url: ctx + '/pt/dyform/field/get/' + uuid,
          contentType: 'application/json',
          dataType: 'json',
          success: function (result) {
            bean = result.data.bean;
            $('#dyform_common_field_basic_info').json2form(bean);
            $('#dyform_common_field_categoryUuid').trigger('change');
            $('#dyform_common_field_controlType').trigger('change').wSelect2('disable');
            $('#dyform_common_field_moduleId').val(bean.moduleId).trigger('change');
            // 显示
            var list = result.data.list;

            $('#associationForm', $container).empty();
            $.each(list, function (i, item) {
              var $wrap = $('<div>', {
                class: 'well-tag',
                data: {
                  uuid: item.formUuid
                },
                text: item.displayName
              });
              $wrap.append('<i class="iconfont icon-ptkj-chakanbiaodanxiangqing"></i>');
              $('#associationForm', $container).append($wrap);
            });
          },
          error: function (result) {}
        });

        // 跳转表单详情
        $('#associationForm').on('click', '.well-tag', function () {
          var $this = $(this);
          var url = ctx + '/pt/dyform/definition/form-designer?uuid=' + $this.data('uuid');
          appContext.getNavTabWidget().createTab('dyformField', '表单字段-关联表单详情', 'iframe', url);
        });
      }

      var pluginBase = '/static/dyform/definition/ckeditor/plugins';
      CKEDITOR.plugins.basePath = pluginBase + '/'; // 自定义ckeditor的插件路径
      var editor = {};
      var $fieldContainer = $('#field-container');
      window.formDefinition =
        window.formDefinition ||
        $.extend(
          {
            fields: {},
            subforms: {}
          },
          {}
        );
      $.extend(editor, {
        focusedDom: $fieldContainer
      });

      function fieldExists(uuid, moduleId, categoryUuid, fieldName, fieldValue) {
        var fieldExists = true;
        JDS.call({
          service: 'formCommonFieldDefinitionService.fieldExists',
          data: [uuid, moduleId, categoryUuid, fieldName, fieldValue],
          async: false,
          success: function (result) {
            if (result.success) {
              fieldExists = result.data;
            }
          }
        });
        if (fieldExists === true) {
          alert('字段名重复，请重新设置');
          return false;
        }
      }

      $('#dyform_common_field_btn_prev')
        .off('click')
        .on('click', function (event) {
          $('#dyform_common_field_property').hide();
          $('#dyform_common_field_basic_info').show();
        });
      $('#dyform_common_field_btn_next')
        .off('click')
        .on('click', function (event) {
          $('#dyform_common_field_property').show();
          $('#dyform_common_field_basic_info').hide();
          $('#dyform_common_field_basic_info').form2json(bean);

          if (bean.moduleId === '') {
            alert('请选择所属模块');
            return;
          } else if (bean.categoryUuid === '') {
            alert('请选择所属分类');
            return;
          } else if (bean.controlType === '') {
            alert('请选择控件类型');
            return;
          }
          if (!$.fn.placeholder) {
            $.fn.placeholder = { input: true, textarea: true };
          }
          $.extend(formDefinition, formDefinitionMethod);
          if (bean.name && bean.definitionJson) {
            editor.focusedDom = $fieldContainer;
            var field = $.parseJSON(bean.definitionJson);
            if (bean.controlType === CkPlugin.SUBFORM) {
              formDefinition.subforms[bean.name] = field;
              formDefinition.subforms[field.formUuid] = field;
              $fieldContainer.attr('formUuid', field.formUuid);
            } else {
              $fieldContainer.attr('name', bean.name);
              formDefinition.fields[bean.name] = field;
            }
          } else {
            if (bean.controlType === CkPlugin.SUBFORM) {
              formDefinition.subforms = {};
            } else {
              formDefinition.fields = {};
            }
          }
          $.ajax({
            type: 'get',
            dataType: 'text',
            url: pluginBase + '/' + bean.controlType + '/plugin.js?t=' + new Date().getTime(),
            success: function (data) {
              var oAddPlugin = window.addPlugin;
              try {
                $fieldContainer.html('<div id="container_' + bean.controlType + '"></div');
                window.addPlugin = function (pluginName, pluginToolHint, dialogTitle, controlConfig) {
                  var dialog = getDialog(pluginName, pluginToolHint, dialogTitle, controlConfig)(editor);
                  dialog.onShow({
                    callback: function (data) {
                      // 设置字段不可编辑
                      if (bean.name && bean.definitionJson) {
                        $fieldContainer.find('input#name').attr('readonly', 'readonly');
                        $fieldContainer.find('select#dbDataType').attr('disabled', 'disabled');
                      }
                    }
                  });
                  $('#dyform_common_field_btn_save')
                    .off('click')
                    .on('click', function (event) {
                      formDefinition.fields = {};
                      formDefinition.subforms = {};
                      if (bean.name && bean.definitionJson) {
                        $fieldContainer.find('select#dbDataType').removeAttr('disabled');
                      }
                      if (dialog.onOk() === false) {
                        if (bean.name && bean.definitionJson) {
                          $fieldContainer.find('select#dbDataType').attr('disabled', 'disabled');
                        }
                        return;
                      }
                      if (pluginName === CkPlugin.SUBFORM) {
                        for (var fieldName in formDefinition.subforms) {
                          var field = formDefinition.subforms[fieldName];
                          bean.name = field.name;
                          bean.displayName = field.displayName;
                          bean.definitionJson = JSON.stringify(field);
                        }
                      } else {
                        for (var fieldName in formDefinition.fields) {
                          var field = formDefinition.fields[fieldName];
                          bean.name = field.name;
                          bean.displayName = field.displayName;
                          bean.definitionJson = JSON.stringify(field);
                        }
                      }
                      if (fieldExists(bean.uuid, bean.moduleId, bean.categoryUuid, 'displayName', bean.displayName) === false) {
                        if (bean.name && bean.definitionJson) {
                          $fieldContainer.find('select#dbDataType').attr('disabled', 'disabled');
                        }
                        return;
                      }
                      $.ajax({
                        type: 'POST',
                        url: ctx + '/pt/dyform/field/save',
                        data: JSON.stringify(bean),
                        contentType: 'application/json',
                        dataType: 'text',
                        success: function (result) {
                          // 保存成功刷新列表
                          appModal.success('保存成功', function () {
                            appContext.getNavTabWidget().closeTab();
                          });
                        },
                        error: function (result) {}
                      });
                    });
                };
                $.globalEval(data);
              } finally {
                window.addPlugin = oAddPlugin;
              }
            }
          });
        });
    },
    initSelect: function () {
      var _self = this;
      var plugins = $.map(getControlPlugins(), function (item) {
        return {
          id: item.code,
          text: item.name
        };
      });
      var $container = _self.getWidgetElement();

      // 所属业务模块
      $('#dyform_common_field_moduleId')
        .wSelect2({
          labelField: 'dyform_common_field_moduleName',
          valueField: 'dyform_common_field_moduleId',
          remoteSearch: false,
          serviceName: 'appModuleMgr',
          queryMethod: 'loadSelectData',
          params: {
            //excludeIds:$("#moduleId").val(),
            systemUnitId: server.SpringSecurityUtils.getCurrentUserUnitId()
          }
        })
        .on('change', function () {
          $('#dyform_common_field_categoryUuid').wellSelect('_ajax');
        });

      // 所属分类
      $('#dyform_common_field_categoryUuid').wSelect2({
        params: function () {
          return {
            params: {
              moduleId: $('#dyform_common_field_moduleId').val()
            }
          };
        },
        multiple: false,
        enableAdd: true,
        enableDel: true,
        enableUpdate: true,
        noMatchValueClear: true,
        serviceName: 'formCommonFieldCategoryService',
        queryMethod: 'loadSelectData',
        valueField: 'dyform_common_field_categoryUuid',
        labelField: 'dyform_common_field_categoryName',
        defaultBlank: true,
        width: '100%',
        height: 250,
        custom_more_style: 2,
        custom_more: true,
        customEvent: {
          moveUp: true,
          moveDown: true
        },
        sortCallBack: function (type, $currElement, $afterElement, cb) {
          if ($currElement.data('value') && $afterElement.data('value')) {
            JDS.call({
              service: 'formCommonFieldCategoryService.moveFieldCategoryAfterOther',
              data: [$currElement.data('value'), $afterElement.data('value')],
              success: function (result) {
                cb && cb();
              }
            });
          }
        },
        onSelect2delSuccess: function (data) {
          return appModal.success('删除成功');
        },
        onSelect2del: function (event, callback) {
          var isInRef = true;
          JDS.call({
            service: 'formCommonFieldCategoryService.isInRef',
            data: [event.val],
            async: false,
            success: function (result) {
              if (result.success) {
                isInRef = result.data;
              }
            }
          });
          if (isInRef === true) {
            appModal.alert('无法删除，该分类已关联字段信息');
            return false;
          }
        },
        onSelect2add: function (event, callback) {
          if ($.trim(event.val).length > 15) {
            appModal.alert('分类名称不能超过15个汉字');
            return false;
          }
          if (event && event.object) {
            event.object.moduleId = $('#dyform_common_field_moduleId').val();
          }
        }
      });

      //控件类型
      $('#dyform_common_field_controlType', $container).wSelect2({
        valueField: 'dyform_common_field_controlType',
        labelField: 'dyform_common_field_controlTypeName',
        data: plugins
      });
    },
    refresh: function () {
      this.init();
    }
  });
  return AppDyformCommonFieldSetDevelopment;
});
