define(['jquery', 'commons', 'constant', 'server', 'appContext', 'DmsListViewActionBase', 'appModal'], function (
  $,
  commons,
  constant,
  server,
  appContext,
  DmsListViewActionBase,
  appModal
) {
  // 视图列表新增操作
  var DmsDyformPrevOrNextDocumentRefreshAction = function () {
    DmsListViewActionBase.apply(this, arguments);
  };
  commons.inherit(DmsDyformPrevOrNextDocumentRefreshAction, DmsListViewActionBase, {
    btn_dyform_prev_document: function (options) {
      this.__do(options);
    },

    btn_dyform_next_document: function (options) {
      this.__do(options);
    },

    __do: function (options) {
      var _self = this,
        saveAction = null;
      if (options.data.action.params && options.data.action.params.saveButton) {
        for (var k in options.data.actionMap) {
          if (options.data.actionMap[k].code == options.data.action.params.saveButton) {
            saveAction = options.data.actionMap[k];
            break;
          }
        }
      }
      var doFunction = function () {
        var requestid = commons.Browser.getQueryString('_requestCode');
        var item = window.sessionStorage.getItem(requestid + '_dataStoreParams');
        if (item) {
          item = JSON.parse(item);
          options.data.dataStoreParams = item;
          options.data.primaryField = item.idKey;
          options.data.index = item.index;
          options.success = function (result) {
            if (result.code == 0 && result.data) {
              var _item = JSON.parse(window.sessionStorage.getItem(requestid + '_dataStoreParams'));
              var dataStoreParams = result.data.dataStoreParams;
              dataStoreParams.idKey = _item.idKey;
              dataStoreParams.idValue = result.data.dataUuid;
              dataStoreParams.index = result.data.index;
              if (result.data.first) {
                dataStoreParams.first = true;
              }
              if (result.data.last) {
                dataStoreParams.last = true;
              }
              _item.url = _item.url.replace('idValue=' + _item.idValue, 'idValue=' + result.data.dataUuid);
              dataStoreParams.url = _item.url;
              window.sessionStorage.setItem(requestid + '_dataStoreParams', JSON.stringify(dataStoreParams));
              window.location.href = _item.url;
            }
          };
          options.ui.options.target = '';
          _self.dmsDataServices.performed(options);
        }
      };
      if (!saveAction) {
        doFunction();
        return;
      }
      DyformFacade.get$dyform().hasBeenModified(function (modified) {
        if (modified) {
          var _dialog = appModal.dialog({
            title: '提示',
            size: 'small',
            message: '<div style="text-align:center">文档已修改，是否保存?</div>',
            buttons: {
              confirm: {
                label: '保存',
                className: 'btn-primary',
                callback: function () {
                  // 调用保存按钮
                  saveAction.success = function (result) {
                    saveAction.stopDialogEvent = false;
                    if (result.code == 0) {
                      appModal.info(result.msg, function () {
                        doFunction();
                      });
                    } else {
                      appModal.error(result.msg || '保存失败');
                    }
                  };
                  saveAction.stopDialogEvent = true;
                  $("[btnid='" + saveAction.id + "']").trigger('click');
                }
              },
              notsave: {
                label: '不保存',
                callback: function () {
                  doFunction();
                }
              },
              cancel: {
                label: '取消',
                className: 'btn-default'
              }
            },
            callback: function () {},
            shown: function () {
              _dialog.find('.modal-body').attr('style', 'min-height:40px!important;text-align:center;');
              _dialog.find('.bootbox-body').attr('style', 'min-height:40px!important;text-align:center;');
            }
          });
        } else {
          doFunction();
        }
      });
      // TODO: 判断是否表单有编辑过，有则弹出提示保存或者不保存或者取消，调用二开方法触发调用保存逻辑
    }
  });

  return DmsDyformPrevOrNextDocumentRefreshAction;
});
