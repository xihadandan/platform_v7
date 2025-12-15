define(['jquery', 'commons', 'constant', 'server', 'ViewDevelopmentBase', 'appModal', 'lodash', 'minicolors'], function (
  $,
  commons,
  constant,
  server,
  ViewDevelopmentBase,
  appModal,
  _,
  minicolors
) {
  var AppEmailTagDevelopment = function () {
    ViewDevelopmentBase.apply(this, arguments);
  };

  commons.inherit(AppEmailTagDevelopment, ViewDevelopmentBase, {
    init: function () {
      var _self = this;
      _self.loadCssLink();
    },

    afterRender: function (options, configuration) {
      var _self = this;
      var $tableElement = _self.widget.$tableElement;
      //绑定表格行上颜色选择器
      $tableElement.on('post-body.bs.table', function (event, data) {
        $('.rowTagColor').each(function () {
          _self.loadMiniColorsPlugin($(this), {
            tagUuid: $(this).attr('uuid'),
            tagColor: $(this).val()
          });
        });
      });
    },

    loadCssLink: function () {
      // $("head").append(
      //     $("<link>", {
      //         "href": staticPrefix + "/js/minicolors/css/jquery.minicolors.css",
      //         "rel": "stylesheet"
      //     }),
      //     $("<link>", {
      //         "href": staticPrefix + "/js/pt/css/webmail/wm_webmail_v2.css",
      //         "rel": "stylesheet"
      //     })
      // );
    },

    /**
     * 新建标签
     */
    addTag: function () {
      var _self = this;
      _self.popDialog();
    },

    /**
     * 弹窗展示
     */
    popDialog: function (tag, dialogOpt, successCallback) {
      var _self = this;
      var options = {
        title: dialogOpt && dialogOpt.title ? dialogOpt.title : tag ? '重命名标签' : '新建标签',
        message: _self.tagDialogHtml(tag),
        buttons: {
          confirm: {
            label: '确定',
            className: 'btn-primary',
            callback: function (result) {
              var commitResult = false;
              var inputTagName = $.trim($('#tagName').val());
              var inputTagColor = $.trim($('#tagColor').val());
              if (inputTagName.length > 20) {
                appModal.alert('标签名称不要超过20个中文字符');
                return false;
              }
              if (!tag && inputTagName.length == 0) {
                //新建时候需要判断标签名是否为空
                appModal.alert('请输入标签名称');
                return false;
              }
              if (tag && inputTagName.length == 0) {
                inputTagName = tag.tagName;
              }

              var dataParam = {
                uuid: tag ? tag.tagUuid : null,
                tagName: inputTagName,
                tagColor: inputTagColor
              };
              server.JDS.call({
                service: 'wmMailTagFacadeService.' + (tag ? 'updateTag' : 'addMailTag'),
                data: dataParam,
                success: function (result) {
                  commitResult = true;
                  if ($.isFunction(successCallback)) {
                    successCallback(result.data);
                  } else {
                    appModal.success(tag ? '重命名成功' : '新建成功');
                    _self.refresh();
                    _self.refreshLeftSideMenus({ expand: true });
                  }
                },
                error: function (jqXHR) {
                  appModal.alert((tag ? '重命名标签' : '新建标签') + '失败');

                  //var response=JSON.parse(jqXHR.responseText);
                },
                async: false
              });
              return commitResult;
            }
          },

          cancel: {
            label: '取消',
            className: 'btn-default',
            callback: function (result) {}
          }
        },
        size: 'small',
        shown: function ($Dialog) {
          $Dialog.find('.bootbox-body').css('overflow-y', 'visible');
        }
      };
      appModal.dialog(options);
      _self.loadMiniColorsPlugin($('#tagColor'), tag);
    },

    loadMiniColorsPlugin: function ($target, tag) {
      var _self = this;
      var swatches = '#90caf9|#ef9a9a|#a5d6a7|#fff59d|#ffcc80|#bcaaa4|#eeeeee|#f44336|#2196f3|#4caf50|#ffeb3b|#ff9800|#795548|#9e9e9e';
      var defaultValue = tag ? tag.tagColor : '#90caf9';
      $target.attr('last_color', tag ? tag.tagColor : '');
      $target.minicolors({
        control: 'hue',
        defaultValue: defaultValue,
        format: 'hex',
        position: 'bottom left',
        letterCase: 'lowercase',
        swatches: swatches.split('|'),
        change: function (value, opacity) {},
        hide: function () {
          if (tag && $('.minicolors-focus').length == 1 && $target.is('.rowTagColor')) {
            //表格行颜色修改
            if ($target.attr('last_color') == $target.val()) {
              //颜色未修改
              return true;
            }
            //修改颜色
            var commitSuccess = false;
            server.JDS.call({
              service: 'wmMailTagFacadeService.updateTag',
              data: [
                {
                  uuid: tag ? tag.tagUuid : null,
                  tagColor: $target.val()
                }
              ],
              success: function (result) {
                appModal.success('颜色修改成功');
                commitSuccess = true;
              },
              error: function (jqXHR) {
                //var response=JSON.parse(jqXHR.responseText);
              },
              async: false
            });
            if (commitSuccess) {
              $target.attr('last_color', $target.val());
              _self.refresh();
              _self.refreshLeftSideMenus({ expand: true });
            }
          }
        },
        theme: 'bootstrap'
      });
    },

    /**
     * 刷新左侧导航栏关于标签的导航项
     */
    refreshLeftSideMenus: function () {
      var $tagMenu = $("ul[loadintf='com.wellsoft.pt.webmail.support.WmMailTagTreeDataProvider']");
      if ($tagMenu) {
        $tagMenu.trigger('reloadMenuItem', arguments);
      }
      //$(".ui-wLeftSidebar[loadintf='com.wellsoft.pt.webmail.support.WmMailTagTreeDataProvider']").data('uiWLeftSidebar').refresh(true);
    },

    /**
     * 重命名文件夹
     */
    renameTag: function (event, options, rowData) {
      this.popDialog({
        tagName: rowData.TAG_NAME,
        tagUuid: rowData.UUID,
        tagColor: rowData.TAG_COLOR
      });
    },

    /**
     * 删除文件夹
     */
    deleteTag: function (event, options, rowData) {
      var _self = this;
      var deleteTipHtml = function () {
        var $table = $('<table>', {
          class: 'table js-deleteConfirmTable'
        });
        var $labelTr = $('<tr>').append($('<td>').html('是否删除标签[' + rowData.TAG_NAME + ']'));
        var $labelTr2 = $('<tr>').append($('<td>', { class: 'js-deleteConfirmTip' }).html('相关邮件也将移除此标签(邮件不会被删除)'));
        $table.append($labelTr, $labelTr2);
        return $table[0].outerHTML;
      };
      appModal.confirm(deleteTipHtml(), function (result) {
        if (result) {
          server.JDS.call({
            service: 'wmMailTagFacadeService.deleteTag',
            data: [rowData.UUID],
            success: function (result) {
              appModal.success('删除成功');
              _self.refresh();
              _self.refreshLeftSideMenus({ expand: true });
            },
            error: function (jqXHR) {}
          });
        }
      });
    },

    tagDialogHtml: function (tag) {
      var $table = $('<table>', {
        class: 'table table-hover table-striped'
      });
      var $labelTr = $('<tr>').append($('<td>').html('请输入标签名称'));
      var $inputTr = $('<tr>');
      $inputTr.append(
        $('<td>').append(
          $('<div>', { class: 'tagColorDiv' }).append(
            $('<input>', {
              type: 'hidden',
              id: 'tagColor',
              style: ''
            })
          ),
          $('<input>', {
            type: 'text',
            id: 'tagName',
            maxlength: 20,
            class: 'tagColorInput',
            value: tag && tag.tagName ? tag.tagName : ''
          })
        )
      );
      $table.append($labelTr, $inputTr);
      return $table[0].outerHTML;
    }
  });

  return AppEmailTagDevelopment;
});
