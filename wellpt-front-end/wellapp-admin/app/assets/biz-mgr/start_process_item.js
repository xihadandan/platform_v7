define(['constant', 'commons', 'server', 'appContext', 'appModal', 'AppPtMgrListViewWidgetDevelopment', 'AppPtMgrCommons'], function (
  constant,
  commons,
  server,
  appContext,
  appModal
) {
  var StringUtils = commons.StringUtils;
  var UUID = commons.UUID;
  var JDS = server.JDS;
  var startProcessItem = function (options) {
    new AppStartProcessItem(options);
  };

  var AppStartProcessItem = function (options) {
    var _self = this;
    _self.options = options;
    var params = options.params || {};
    var processDefId = params.processDefId;
    _self.processNodeId = params.processNodeId;
    _self.itemCode = params.itemCode;
    if (StringUtils.isBlank(processDefId)) {
      _self.startByChooseProcessDefinition();
    } else if (StringUtils.isNotBlank(_self.itemCode)) {
      _self.startByItemCode(processDefId, _self.itemCode);
    } else {
      _self.startByProcessDefId(processDefId);
    }
  };

  $.extend(AppStartProcessItem.prototype, {
    startByChooseProcessDefinition: function (options) {
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

    startByProcessDefId: function (processDefId) {
      var _self = this;
      JDS.restfulGet({
        url: ctx + `/proxy/api/biz/process/definition/getById/${processDefId}`,
        success: function (result) {
          var processDefinition = result.data;
          _self.showChooseProcessItemDialogByProcessDefinition(processDefinition);
        }
      });
    },

    startByItemCode: function (processDefId, itemCode) {
      var _self = this;
      var itemCode = _self.itemCode;
      if (StringUtils.isBlank(itemCode)) {
        return false;
      }

      var getItems = function (processNodes, items) {
        for (var i = 0; i < processNodes.length; i++) {
          var processNode = processNodes[i];
          if (processNode.items) {
            for (var j = 0; j < processNode.items.length; j++) {
              items.push(processNode.items[j]);
            }
          }
          getItems(processNode.nodes || [], items);
        }
      };

      var doStart = function (processNodes) {
        var items = [];
        getItems(processNodes, items);

        var startItem = null;
        for (var i = 0; i < items.length; i++) {
          if (items[i].itemCode == itemCode) {
            startItem = items[i];
            break;
          }
        }
        if (startItem == null) {
          appModal.error(`找不到的事项编码[${itemCode}]的业务流程配置信息！`);
        }

        _self.openStartItemWindow(processDefId, [startItem.id]);
      };

      JDS.restfulGet({
        url: ctx + `/proxy/api/biz/process/definition/listProcessNodeItemById/${processDefId}`,
        success: function (result) {
          doStart(result.data);
        }
      });
    },

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
              _self.openStartItemWindow(processDefId, processItemIds);
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

    openStartItemWindow: function (processDefId, processItemIds) {
      var _self = this;
      var rawParams = _self.options.params || {};
      var params = $.extend(true, {}, rawParams);
      var eventParams = appContext.resolveParams(params, _self.options);
      var urlParams = {};
      if (StringUtils.isNotBlank(eventParams.formUuid) && StringUtils.isNotBlank(eventParams.dataUuid)) {
        urlParams.formUuid = eventParams.formUuid;
        urlParams.dataUuid = eventParams.dataUuid;
      }
      // 附加参数
      for (var key in eventParams) {
        if (key.startsWith('ep_')) {
          urlParams[key] = eventParams[key];
        }
      }
      var url = `/biz/process/item/instance/new/${processDefId}?processItemIds=${processItemIds.join(';')}`;
      var winOptions = {};
      winOptions.url = url;
      winOptions.ui = _self.options.ui;
      winOptions.size = 'large';
      winOptions.urlParams = urlParams;
      appContext.openWindow(winOptions);
    },

    renderChooseProcessItemByProcessDefinition: function ($element, chooseItems, processDefinition) {
      var _self = this;
      JDS.restfulGet({
        url: ctx + `/proxy/api/biz/process/definition/listProcessNodeItemByUuid/${processDefinition.uuid}`,
        success: function (result) {
          var processNodes = _self.filterProcessNodeIfRequired(result.data);
          _self.processNodes = processNodes;
          _self.renderProcessNodes($element, processNodes, processDefinition);
          _self.renderItemState($element, processDefinition);
          _self.bindProcessItemEvents($element, chooseItems);
        }
      });
    },

    filterProcessNodeIfRequired: function (processNodes) {
      var _self = this;
      var processNodeId = _self.processNodeId;
      if (StringUtils.isNotBlank(processNodeId)) {
        for (var i = 0; i < processNodes.length; i++) {
          if (processNodes[i].id == processNodeId) {
            return [processNodes[i]];
          }
        }
      }
      return processNodes;
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

    renderItemState: function ($element, processDefinition) {
      var _self = this;
      var itemIds = _self.getItemIds();
      JDS.restfulPost({
        url: ctx + `/proxy/api/biz/process/item/instance/listItemStates?processDefId=${processDefinition.id}`,
        data: itemIds,
        success: function (result) {
          var stateMap = result.data || {};
          for (var key in stateMap) {
            var state = stateMap[key];
            if (state == '10' || state == '20') {
              $(`*[stateId="${key}"]`, $element).text('办理中');
            } else if (state == '30') {
              $(`*[stateId="${key}"]`, $element).text('已办理');
            }
          }
        }
      });
    },

    bindProcessItemEvents: function ($container, chooseItems) {
      var _self = this;
      // 全选/取消复选框事件
      $("input[name='checkAll']", $container).on('change', function () {
        if (this.checked) {
          $(this).closest('table').find('tbody .td-checkbox>input').attr('checked', 'checked').trigger('change');
        } else {
          $(this).closest('table').find('tbody .td-checkbox>input').removeAttr('checked').trigger('change');
        }
      });
      // 事项选择事件
      $("input[name='checkOne'], input[name='checkIncludeItem']", $container).on('change', function () {
        var processItemId = $(this).attr('processItemId');
        var itemType = $(this).attr('itemType');
        var itemName = $(this).closest('tr').find('.item-name').text();
        if (this.checked) {
          var item = _self.getItemById(processItemId);
          // 并联分发事项不发起自身
          if (item.itemType == '30' && item.dispenseItem) {
          } else {
            chooseItems[processItemId] = {
              processItemId,
              itemName
            };
          }
        } else {
          delete chooseItems[processItemId];
        }
        // 并联事项包含的事项
        if (itemType == '30') {
          $(this).closest('tr').find("input[name='checkIncludeItem']").attr('checked', this.checked).trigger('change');
        }
      });
    },

    getItemById: function (itemId) {
      var _self = this;
      var processNodes = _self.processNodes || [];
      var itemMap = _self.itemMap || {};
      if (itemMap[itemId]) {
        return itemMap[itemId];
      }

      var getItemMap = function (processNodes, itemMap) {
        for (var i = 0; i < processNodes.length; i++) {
          var processNode = processNodes[i];
          if (processNode.items) {
            for (var j = 0; j < processNode.items.length; j++) {
              var item = processNode.items[j];
              itemMap[item.id] = item;
              if (item.includeItemDtos) {
                $.each(item.includeItemDtos, function (i, itemDto) {
                  itemMap[itemDto.id] = itemDto;
                });
              }
            }
          }
          getItemMap(processNode.nodes || [], itemMap);
        }
      };
      getItemMap(processNodes, itemMap);

      _self.itemMap = itemMap;
      return itemMap[itemId];
    },

    getItemIds: function () {
      var _self = this;
      var itemIds = [];
      _self.getItemById('-1');
      var itemMap = _self.itemMap || {};
      for (var key in itemMap) {
        itemIds.push(key);
      }
      return itemIds;
    }
  });

  return startProcessItem;
});
