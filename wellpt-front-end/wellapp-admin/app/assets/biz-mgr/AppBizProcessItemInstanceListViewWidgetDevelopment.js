define(['constant', 'commons', 'server', 'appContext', 'appModal', 'AppPtMgrListViewWidgetDevelopment', 'AppPtMgrCommons'], function (
  constant,
  commons,
  server,
  appContext,
  appModal,
  AppPtMgrListViewWidgetDevelopment,
  AppPtMgrCommons
) {
  var StringUtils = commons.StringUtils;
  var UUID = commons.UUID;
  var JDS = server.JDS;

  // 平台管理_业务流程管理_业务事项办件列表_视图组件二开
  var AppBizProcessItemInstanceListViewWidgetDevelopment = function () {
    AppPtMgrListViewWidgetDevelopment.apply(this, arguments);
  };

  // 接口方法
  commons.inherit(AppBizProcessItemInstanceListViewWidgetDevelopment, AppPtMgrListViewWidgetDevelopment, {
    // 组件准备
    prepare: function () {},

    beforeRender: function (options, configuration) {},

    getSelectRowData: function (event) {
      var _self = this;
      var $toolbarDiv = $(event.target).closest('div');
      var rowData = [];
      if ($toolbarDiv.is('.div_lineEnd_toolbar')) {
        //行级点击操作
        var index = $toolbarDiv.attr('index');
        var allData = _self.getData();
        rowData = [allData[index]];
      } else {
        if (_self.getSelectionIndexes().length == 0) {
          return [];
        }
        rowData = _self.getSelections();
      }
      return rowData;
    },

    // 选择业务流程发起事项
    btn_start_by_process_definition_bak: function (event, ui) {
      var _self = this;
      var listViewWidget = null;
      var dlgId = UUID.createUUID();
      var title = '选择业务流程';
      var message = "<div id='" + dlgId + "'></div>";
      var dlgOptions = {
        title: title,
        message: message,
        size: 'large',
        shown: function () {
          appContext.renderWidget({
            renderTo: '#' + dlgId,
            widgetDefId: 'wPanel_CA032A6FA2C0000147A720001ED61490',
            forceRenderIfConflict: true,
            callback: function () {
              $('#' + dlgId + ' > .ui-wBootstrapTable').css({
                overflow: 'auto'
              });
            },
            onPrepare: {
              wBootstrapTable_CA032A7973100001B8291A8FBB3019B5: function () {
                listViewWidget = this;
              }
            }
          });
        },
        buttons: {
          confirm: {
            label: '确定',
            className: 'btn-primary',
            callback: function () {
              var selection = listViewWidget.getSelections();
              if (selection == null || selection.length == 0) {
                appModal.error('请选择业务流程！');
                return false;
              }
              _self.showChooseProcessItemDialogByProcessDefinition(selection[0]);
              return true;
            }
          },
          cancel: {
            label: '取消',
            className: 'btn-default',
            callback: function () {
              return true;
            }
          }
        }
      };
      appModal.dialog(dlgOptions);
    },

    // 根据业务流程显示业务事项选择弹出框
    showChooseProcessItemDialogByProcessDefinition: function (processDefinition) {
      var _self = this;
      var dlgId = UUID.createUUID();
      var title = '选择业务事项';
      var message = "<div id='" + dlgId + "'></div>";
      var chooseItems = {};
      var dlgOptions = {
        title: title,
        message: message,
        size: 'large',
        shown: function () {
          appContext.renderWidget({
            renderTo: '#' + dlgId,
            widgetDefId: 'wPanel_CA032C0790600001F7E61BFA1C00D8C0',
            forceRenderIfConflict: true,
            callback: function () {},
            onPrepare: {
              wHtml_CA032C087F2000012A69179218301B10: function () {
                // 根据业务流程定义选择业务事项
                _self.renderChooseProcessItemByProcessDefinition(this.element, chooseItems, processDefinition);
              }
            }
          });
        },
        buttons: {
          confirm: {
            label: '确定',
            className: 'btn-primary',
            callback: function () {
              if ($.isEmptyObject(chooseItems)) {
                appModal.error('请选择办理事项！');
                return false;
              }

              var processDefId = processDefinition.id;
              var processItemIds = [];
              for (var key in chooseItems) {
                processItemIds.push(key);
              }
              // 发起业务事项
              var url = `/biz/process/item/instance/new/${processDefId}?processItemIds=${processItemIds.join(';')}`;
              var options = {};
              options.url = url;
              options.ui = _self.getWidget();
              options.size = 'large';
              appContext.openWindow(options);
              return true;
            }
          },
          cancel: {
            label: '取消',
            className: 'btn-default',
            callback: function () {
              return true;
            }
          }
        }
      };
      appModal.dialog(dlgOptions);
    },

    renderChooseProcessItemByProcessDefinition: function ($element, chooseItems, processDefinition) {
      var _self = this;
      JDS.restfulGet({
        url: ctx + `/proxy/api/biz/process/definition/listProcessNodeItemByUuid/${processDefinition.uuid}`,
        success: function (result) {
          var processNodes = result.data;
          _self.renderProcessNodes($element, processNodes, processDefinition);
          _self.bindProcessItemEvents($element, chooseItems);
        }
      });
    },

    renderProcessNodes: function ($element, processNodes, processDefinition) {
      var _self = this;
      var templateEngine = appContext.getJavaScriptTemplateEngine();
      var html = templateEngine.renderById('biz-choose-process-item-by-process-definition', {
        processDefinition,
        processNodes
      });
      $element.html(html);

      // 子过程结点
      for (var i = 0; i < processNodes.length; i++) {
        var processNode = processNodes[i];
        if (processNode.nodes && processNode.nodes.length > 0) {
          _self.renderProcessNodes($('.children-nodes-' + processNode.id, $element), processNode.nodes);
        }
      }
    },

    bindProcessItemEvents: function ($container, chooseItems) {
      // 全选/取消复选框事件
      $("input[name='checkAll']", $container).on('change', function () {
        if (this.checked) {
          $(this).closest('table').find('tbody .td-checkbox>input').attr('checked', 'checked').trigger('change');
        } else {
          $(this).closest('table').find('tbody .td-checkbox>input').removeAttr('checked').trigger('change');
        }
      });
      // 事项选择事件
      $("input[name='checkOne']", $container).on('change', function () {
        var processItemId = $(this).attr('processItemId');
        var itemName = $(this).closest('tr').find('.item-name').text();
        if (this.checked) {
          chooseItems[processItemId] = {
            processItemId,
            itemName
          };
        } else {
          delete chooseItems[processItemId];
        }
      });
    },

    // 行点击查看详情
    onClickRow: function (rowNum, rowData, $element, field) {
      var _self = this;
      // 查看业务事项
      var url = `/biz/process/item/instance/view?itemInstUuid=${rowData.uuid}`;
      var options = {};
      options.url = url;
      options.ui = _self.getWidget();
      options.size = 'large';
      appContext.openWindow(options);
    }
  });
  return AppBizProcessItemInstanceListViewWidgetDevelopment;
});
