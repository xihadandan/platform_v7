define('DmsDyformPrintAction', ['jquery', 'commons', 'constant', 'server', 'appContext', 'appModal', 'DmsDyformActionBase'], function (
  $,
  commons,
  constant,
  server,
  appContext,
  appModal,
  DmsDyformActionBase
) {
  var JDS = server.JDS;
  var FileDownloadUtils = server.FileDownloadUtils;
  var StringUtils = commons.StringUtils;
  // 表单单据套打操作
  var DmsDyformPrintAction = function () {
    DmsDyformActionBase.apply(this, arguments);
  };
  // 表单单据套打操作
  commons.inherit(DmsDyformPrintAction, DmsDyformActionBase, {
    btn_file_manager_dyform_as_print: function (options) {
      var _self = this;
      var uuid = options.data.folderUuid; //文件夹Uuid
      _self.getFilePrintTemplates(uuid);
    },
    btn_dyform_print: function (options) {
      var _self = this;
      var params = options.params || {};
      var printTemplateId = params.printTemplateId;
      if (StringUtils.isBlank(printTemplateId)) {
        appModal.error('没有配置套打模板！');
      } else {
        var printTemplateIds = printTemplateId.split(';');
        // 只有一个模板，直接套打
        if (printTemplateIds.length == 1) {
          _self.doPrint(printTemplateIds[0], options);
        } else {
          // 弹框选择套打模板进行套打
          _self.getPrintTemplates(printTemplateIds, options, _self.showChoosePrintTemplateDialog);
        }
      }
    },
    // 获取套打模板
    getPrintTemplates: function (printTemplateIds, options, callback) {
      var _self = this;
      JDS.call({
        service: 'printTemplateFacadeService.getPrintTemplatesByIds',
        data: [printTemplateIds],
        version: '',
        success: function (result) {
          callback.call(_self, result.data, options);
        },
        error: function () {
          appModal.error('无法获取配置的套打模板信息！');
        }
      });
    },
    // 弹框选择套打模板进行套打
    showChoosePrintTemplateDialog: function (templates, options) {
      var _self = this;
      // 多个套打模板，弹框选择打印
      var message = "<div class='choose-print-template'></div>";
      var title = '选择套打模板';
      if (options.params && options.params.title) {
        title = options.params.title;
      }
      var dlgOptions = {
        title: title,
        size: 'middle',
        message: message,
        shown: function () {
          var data = $.map(templates, function (object, index) {
            return {
              id: object.id,
              text: object.name
            };
          });
          var formBuilder = require('formBuilder');
          var $container = $('.choose-print-template');
          var chooseType = 'radio';
          formBuilder.buildRadio({
            container: $container,
            items: data,
            name: 'choosePrintTemplate',
            display: 'choosePrintTemplateName',
            labelColSpan: '0',
            controlColSpan: '12'
          });
        },
        buttons: {
          confirm: {
            label: '确定',
            className: 'btn-primary',
            callback: function () {
              var $container = $('.choose-print-template');
              var $checked = $('input[name=choosePrintTemplate]:checked', $container);
              var templateId = $checked.val();
              if (StringUtils.isBlank(templateId)) {
                appModal.error('请选择套打模板！');
                return false;
              }
              _self.doPrint(templateId, options);
              return true;
            }
          },
          cancel: {
            label: '取消',
            className: 'btn-default'
          }
        }
      };
      appModal.dialog(dlgOptions);
    },
    // 套打处理
    doPrint: function (templateId, options) {
      var _self = this;
      if (!options) {
        options = _self.options;
      }
      var urlParams = options.urlParams || {};
      urlParams.templateId = templateId;
      urlParams.printResultFileName = options.rawParams.printResultFileName;
      options.urlParams = urlParams;
      options.success = function (result) {
        FileDownloadUtils.downloadMongoFile({
          fileId: result.data.fileID
        });
      };
      _self.dmsDataServices.performed(options);
    },
    // 根据文件夹Uuid获取对应的套打模板（树）
    getFilePrintTemplates: function (uuid) {
      var _self = this;
      $.ajax({
        url: ctx + '/api/dms/printtemplate/getPrintTemplateTreeByFolderUuids',
        async: false,
        type: 'POST',
        data: {
          folderUuids: [uuid]
        },
        dataType: 'json',
        success: function (data) {
          _self.getFilePrintTemplatesSuccess(data);
        },
        error: function (err) {
          appModal.error('获取套打模板报错！');
        }
      });
    },
    // 获取文件夹模板成功数据处理
    getFilePrintTemplatesSuccess: function (result) {
      var _self = this;
      var templates = result.data;
      var ifOpendialog = true;
      templateTreeList = templates;
      if (templates.length === 0) {
        appModal.info('流程没有配置套打模板！');
      } else if (templates.length === 1) {
        var cateTepList = _self.getCateTemplate(templates, []) || [];
        if (cateTepList.length == 1) {
          ifOpendialog = false;
        }
        if (!ifOpendialog) {
          _self.printHandle(cateTepList[0].data.id, cateTepList[0].data.uuid);
        } else {
          _self.setFilePrintTemplatesDialogOption(templates);
        }
      } else if (ifOpendialog) {
        _self.setFilePrintTemplatesDialogOption(templates);
      }
    },
    // 文件夹模板弹框及其相关操作
    setFilePrintTemplatesDialogOption: function (templates) {
      var _self = this;
      // 多个套打模板，弹框选择打印
      var message =
        "<div class='form-widget choose-print-template-box'>" +
        "<div class='input-box'>" +
        "<input id='fullFlowName' type='text' value='' placeholder='搜索' />" +
        "<i id='searchFlowCategory' class='iconfont icon-ptkj-sousuochaxun'></i>" +
        "<i id='deleteFlowCategory' class='iconfont icon-ptkj-dacha-xiao'></i>" +
        '</div>' +
        "<div class='choose-print-template msg-category-content print-category-content' id='choose_print_template' style='padding-top:0;'><div class='msg-category-list' style='height:306px;margin-top:0;'><ul id='flow_category_tree'>";
      var lis = '';
      lis += _self.buildHtml(templates, -1);
      message +=
        lis +
        '</ul></div>' +
        "<div class='form_operate'><button type='button' id='ID_LExpandAll' class='fl well-btn w-btn-line' style='background-color:transparent;'><i class='icon iconfont icon-ptkj-zhankai'></i>展开</button><button type='button' id='ID_LCollapseAll' class='fl well-btn w-btn-line' style='background-color:transparent;' value='折叠'><i class='icon iconfont icon-ptkj-zhedie'></i>折叠</button></div>" +
        '</div>' +
        '<div class="well-no-data-wrap" style="height:340px;"><div class="well-search-no-match"></div></div>' +
        '</div>';
      var options = {
        title: '选择套打模板',
        size: 'middle',
        message: message,
        shown: function () {
          /* 绑定事件 */
          var $container = $('.choose-print-template-box');
          // 类型下模板收缩/展开
          $('#choose_print_template', $container)
            .off()
            .on('click', '.hasList', function () {
              var liCode = $(this).closest('li').data('uuid');
              if ($(this).hasClass('icon-ptkj-shixinjiantou-you')) {
                $(this).removeClass('icon-ptkj-shixinjiantou-you').addClass('icon-ptkj-shixinjiantou-xia');
                $('.children-' + liCode, $container).show();
              } else {
                $(this).removeClass('icon-ptkj-shixinjiantou-xia').addClass('icon-ptkj-shixinjiantou-you');
                $('.children-' + liCode, $container).hide();
              }
              // 每次点击展开都resize一下滚动条
              $('.msg-category-list', $container).getNiceScroll().resize();
            });
          // 全部展开
          $('#ID_LExpandAll', $container)
            .off('click')
            .on('click', function () {
              $('.hasList', $container).each(function () {
                var liCode = $(this).closest('li').data('uuid');
                $(this).removeClass('icon-ptkj-shixinjiantou-you').addClass('icon-ptkj-shixinjiantou-xia');
                $('.children-' + liCode, $container).show();
              });
              $('.msg-category-list', $container).getNiceScroll().resize();
            });
          // 全部折叠
          $('#ID_LCollapseAll', $container)
            .off('click')
            .on('click', function () {
              $('.hasList', $container).each(function () {
                var liCode = $(this).closest('li').data('uuid');
                $(this).removeClass('icon-ptkj-shixinjiantou-xia').addClass('icon-ptkj-shixinjiantou-you');
                $('.children-' + liCode, $container).hide();
              });
            });
          // 滚动条
          setTimeout(function () {
            $('.msg-category-list', $container).niceScroll({
              height: '304',
              oneaxismousemode: false,
              cursorcolor: '#ccc',
              cursorwidth: '8px'
            });
          }, 200);
          // 查询模板
          $('#searchFlowCategory', $container)
            .off()
            .on('click', function () {
              var val = $('#fullFlowName', $container).val();
              _self.getList(val, $container);
            });
          // 输入按enter键查询
          $('#fullFlowName', $container)
            .off()
            .on('keyup', function () {
              if ($(this).val() != '') {
                $('#deleteFlowCategory', $container).show();
              } else {
                $('#deleteFlowCategory', $container).hide();
              }
            })
            .on('keypress', function (e) {
              if (e.keyCode == 13) {
                _self.getList($(this).val(), $container);
              }
            });
          // 去掉查询
          $('#deleteFlowCategory', $container)
            .off()
            .on('click', function () {
              $('#fullFlowName', $container).val('');
              $(this).hide();
              _self.getList('', $container);
            });

          // 没有模板，隐藏 展开/折叠
          if ($('.hasList', '#choose_print_template').length == 0) {
            $('.form_operate', $container).hide();
          }
          if ($('#flow_category_tree li', $container).length > 0) {
            // 一屏放得下 隐藏搜索框
            setTimeout(function () {
              var lisHeight = 0;
              $('#flow_category_tree li', $container).each(function () {
                lisHeight += $(this).outerHeight();
              });
              console.log(lisHeight);
              if (lisHeight >= 304) {
                $('.input-box', '.choose-print-template-box').show();
              } else {
                $('.input-box', '.choose-print-template-box').hide();
              }
            }, 100);
          }

          // 默认只展开第一级分类模板
          var hasListArray = $('.hasList', '#choose_print_template');
          // console.log(hasListArray);
          hasListArray.each(function (index, item) {
            if (index > 0) {
              $(item).trigger('click');
            }
          });

          /* 绑定事件结束 */
        },
        buttons: {
          confirm: {
            label: '确定',
            className: 'btn-primary',
            callback: function () {
              var $container = $('.choose-print-template');
              var $checked = $('input[name=choosePrintTemplate]:checked', $container);
              var templateId = $checked.val(),
                templateUuid = $checked.parent().data('uuid'),
                templateLang;
              if (StringUtils.isBlank(templateId)) {
                appModal.error('请选择套打模板！');
                return false;
              }

              _self.printHandle(templateId, templateUuid);
              return true;
            }
          },
          cancel: {
            label: '取消',
            className: 'btn-default'
          }
        }
      };
      appModal.dialog(options);
      // 拖拽弹框后，更新弹框内滚动条位置
      $('.choose-print-template-box')
        .parents('.modal-dialog')
        .draggable({
          stop: function () {
            $('.msg-category-list', '.choose-print-template-box').getNiceScroll().resize();
          }
        });
    },
    getCateTemplate: function (list, ttList) {
      var _self = this;
      for (var i = 0; i < list.length; i++) {
        if (list[i].type === 'PrintTemplate') {
          ttList.push(list[i]);
        }
        if (list[i].children.length > 0) {
          _self.getCateTemplate(list[i].children, ttList);
        }
      }
      console.log(ttList);
      return ttList;
    },
    getList: function (val, $container) {
      var _self = this;
      val = val.trim() ? val : '';

      $('#flow_category_tree', $container).detach();
      $('.form_operate', $container).hide();

      var templateTreeListCopy = JSON.parse(JSON.stringify(templateTreeList));
      var templateTreeListTemp = {
        children: templateTreeListCopy
      };
      _self.recTemplateTreeFilterName(val, templateTreeListTemp);
      $('.msg-category-list', $container).append(
        '<ul id="flow_category_tree">' + _self.buildHtml(templateTreeListTemp.children, -1) + '</ul>'
      );

      if ($('.hasList', '#choose_print_template').length == 0) {
        $('.form_operate', $container).hide();
      } else {
        $('.form_operate', $container).show();
      }
      if ($('li', '#flow_category_tree', '#choose_print_template').length > 0) {
        $('#choose_print_template', $container).show();
        $('.well-no-data-wrap', $container).hide();
      } else {
        $('#choose_print_template', $container).hide();
        $('.well-no-data-wrap', $container).show();
      }
    },
    recTemplateTreeFilterName: function (searchValue, templateTreeFilterName) {
      var _self = this;
      var result = false;
      var initName = templateTreeFilterName.name ? templateTreeFilterName.name.toUpperCase() : '';
      var upSearchValue = searchValue.toUpperCase();
      if (initName && initName.indexOf(upSearchValue) > -1) {
        return true;
      }
      if (templateTreeFilterName.children && templateTreeFilterName.children.length > 0) {
        var rmTreeNodeList = [];
        for (var i = 0; i < templateTreeFilterName.children.length; i++) {
          var child = templateTreeFilterName.children[i];
          if (_self.recTemplateTreeFilterName(searchValue, child)) {
            result = true;
          } else {
            rmTreeNodeList.push(child);
          }
        }
        for (var i = 0; i < rmTreeNodeList.length; i++) {
          var rmTreeNodeListElement = rmTreeNodeList[i];
          var indexOf = templateTreeFilterName.children.indexOf(rmTreeNodeListElement);
          if (indexOf > -1) {
            templateTreeFilterName.children.splice(indexOf, 1);
          }
        }
      }
      return result;
    },
    buildHtml: function (serviceData, count, code) {
      var _self = this;
      var html = '';
      count++;
      var value = $('#fullFlowName', '.choose-print-template-box').val() || '';
      for (var i = 0; i < serviceData.length; i++) {
        var data = serviceData[i].data;
        var icon = data.icon ? data.icon : 'iconfont icon-ptkj-fenlei2';
        var background = data.iconColor ? data.iconColor : '#64B3EA';
        var remark = data.remark || '';
        var title = '';
        var className = 'msg-category-item-' + count;
        var $li = '';
        if (count == 0) {
          code = serviceData[i].id;
          if (serviceData[i].type == 'PrintTemplate') {
            $li =
              "<li class='msg-category-item " +
              className +
              "' data-code='" +
              data.code +
              "' data-uuid='" +
              data.uuid +
              "' title='" +
              title +
              "'>" +
              "<span class='slide_icon'></span>" +
              "<input type='radio' name='choosePrintTemplate' id='" +
              data.id +
              "' value='" +
              data.id +
              "' />" +
              "<label for='" +
              data.id +
              "' style='margin-right:0;'></label>" +
              _self.setKeyRed(serviceData[i].name, value);
          } else {
            $li =
              "<li class='msg-category-item " +
              className +
              "' data-code='" +
              data.code +
              "' data-uuid='" +
              serviceData[i].id +
              "' title='" +
              title +
              "'>" +
              "<span class='slide_icon'><i class='iconfont icon-folders hasList icon-ptkj-shixinjiantou-xia'></i></span>" +
              "<span class='button ico_close'" +
              '></span>' +
              _self.setKeyRed(serviceData[i].name, value);
          }
        } else {
          if (serviceData[i].type == 'PrintTemplateCategory') {
            $li =
              "<li class='msg-category-item " +
              className +
              ' children-' +
              code +
              "' data-code='" +
              data.code +
              "' data-uuid='" +
              serviceData[i].id +
              "' title='" +
              title +
              "'>" +
              "<span class='button ico_close'" +
              '></span>' +
              _self.setKeyRed(serviceData[i].name, value);
          } else {
            $li =
              "<li class='msg-category-item " +
              className +
              ' children-' +
              code +
              "' data-code='" +
              data.code +
              "' data-uuid='" +
              data.uuid +
              "' title='" +
              title +
              "'>" +
              "<input type='radio' name='choosePrintTemplate' id='" +
              data.id +
              "' value='" +
              data.id +
              "' />" +
              "<label for='" +
              data.id +
              "' style='margin-right:0;'></label>" +
              _self.setKeyRed(serviceData[i].name, value);
          }
        }

        html += $li;
        if (serviceData[i].children && serviceData[i].children.length > 0) {
          html += _self.buildHtml(serviceData[i].children, count, code);
        }
      }
      return html;
    },
    setKeyRed: function (name, value) {
      var _self = this;
      var html = '';
      var position, subEnd;
      var initName = name ? name.toUpperCase() : '';
      var upSearchValue = value.toUpperCase();
      if (value === '') {
        html = "<span class='msg-category-text'>" + name + '</span>' + '</li>';
      } else {
        if (initName.indexOf(upSearchValue) == -1) {
          html = "<span class='msg-category-text'>" + name + '</span>' + '</li>';
        } else {
          position = initName.indexOf(upSearchValue);
          subEnd = position + value.length;
          html =
            "<span class='msg-category-text'>" +
            name.substring(0, position) +
            "<span style='color:red;margin-right:0;line-height:22px;width:auto;'>" +
            name.substring(position, subEnd) +
            '</span>' +
            name.substring(subEnd, name.length) +
            '</span>' +
            '</li>';
        }
      }
      return html;
    },
    // 文件夹套打
    printHandle: function (templateId, templateUuid) {
      // alert(templateId, templateUuid)
      var _self = this;
      //参数有List<String>时，要用 values.join(",")
      JDS.restfulPost({
        url: ctx + '/proxy/api/dms/printtemplate/print',
        data: {
          formUuid: _self.ui.formUuid,
          dataUuid: _self.ui.dataUuid,
          printTemplateUuId: templateUuid
        },
        mask: true,
        success: function (result) {
          if (result.code == 0) {
            FileDownloadUtils.downloadMongoFile({
              fileId: result.data.fileID
            });
          } else {
            appModal.error(res.msg);
          }
        }
      });
    }
  });
  return DmsDyformPrintAction;
});
