define(['constant', 'commons', 'server', 'appModal', 'HtmlWidgetDevelopment', 'AppDataImportExportCommon'], function (
  constant,
  commons,
  server,
  appModal,
  HtmlWidgetDevelopment,
  appCommonJS
) {
  var Browser = commons.Browser;
  var $container = undefined;
  var _self = undefined;
  var dataType_first_id = 'org_data'; //数据类别，组织数据项对应的id值
  var dataType_user_id = 'user'; //数据类别，用户项对应的id值
  var dataType_zzjg_id = 'version'; //数据类别，组织架构项对应的id值

  // 平台应用_数据导入导出管理_数据导入记录详情页_HTML二开
  var AppImportLogWidgetDevelopment = function () {
    HtmlWidgetDevelopment.apply(this, arguments);
  };

  // 接口方法
  commons.inherit(AppImportLogWidgetDevelopment, HtmlWidgetDevelopment, {
    // 组件初始化
    init: function (options) {
      _self = this;
      $container = this.widget.element;
      $container.parents('.container-fluid').css({
        background: '#fff'
      });
      $('.header-title-text').html('数据导入日志');
      var pageUuid = Browser.getQueryString('uuid');
      // var pageUuid = this.widget.options.containerDefinition.params.uuid;
      _self.getBaseInfo(pageUuid); //获取基础数据
      _self.bindEvent(); //获取导入结果
    },
    //获取基础数据
    getBaseInfo: function (uuid) {
      var data = {
        url: '/api/dataIO/taskRecordInfo/import/' + uuid,
        params: {},
        type: 'GET'
      };
      appModal.showMask();
      appCommonJS
        .ajaxFunction(data)
        .then(function (res) {
          if (res.code == 0) {
            var ress = res.data;
            $('.header-title-time').html(ress.importTime.substring(0, ress.importTime.length - 3));
            _self.initStatus(ress.importStatus); //状态
            _self.initBaseInfo(ress); //渲染基础数据
            _self.initLogInfo(ress.processLog); //渲染日志信息
            _self.getResultInfo(ress.dataRecordDetailBos, res.data); //处理导入结果
            appModal.hideMask();
          } else {
            appModal.error(res.msg || '操作失败');
          }
        })
        .catch(function (err) {
          // console.error(err.msg);
        });
    },
    //渲染基础数据
    initBaseInfo: function (data) {
      var $infoBody = $('.data-import-info', $container);
      $infoBody.empty();
      var dataJson = JSON.parse(data.dataTypeJson);
      _.each(appCommonJS.dataImportParams, function (item, index) {
        var ispass = true;
        if (dataJson[dataType_first_id]) {
          if (dataJson[dataType_first_id][dataType_user_id] == 0) {
            //未选用户
            if (item.param == 'settingPwd') {
              ispass = false;
            } else if (item.param == 'versionName') {
              if (dataJson[dataType_first_id][dataType_zzjg_id] == 0) {
                //未选组织
                ispass = false;
              }
            }
          } else if (dataJson[dataType_first_id][dataType_zzjg_id] == 0) {
            //未选组织
            if (item.param == 'versionName') {
              ispass = false;
            }
          }
        } else {
          if (item.param == 'versionName' || item.param == 'settingPwd') {
            //仅在组织结构选择用户或组织时才显示
            ispass = false;
          }
        }
        if (ispass) {
          var $itemdiv = $('<div>', {
            class: 'data-import-info-item'
          });
          var $label = $('<div>', {
            class: 'item-label'
          }).html(item.text);
          var $value = $('<div>', {
            class: 'item-value'
          }).html(data[item.param]);
          if (item.param == 'repeatStrategy') {
            //数据重复时
            var value = data[item.param] == 1 ? '替换重复数据' : '跳过重复数据';
            $value = $('<div>', {
              class: 'item-value'
            }).html(value);
          } else if (item.param == 'errorStrategy') {
            //数据异常时
            var value = data[item.param] == 1 ? '终止数据导入' : '跳过异常数据';
            $value = $('<div>', {
              class: 'item-value'
            }).html(value);
          } else if (item.param == 'versionName') {
          }
          $itemdiv.append($label).append($value);
          $infoBody.append($itemdiv);
        }
      });
    },
    //处理结果信息
    getResultInfo: function (data, res) {
      var $result = $('.data-import-result');
      $result.empty();
      var colors = {
        导入数据: '#488cee',
        重复数据: '#999',
        异常数据: '#E33033'
      };
      _.each(data, function (item) {
        item.color = colors[item.name];
        var $item = _self.initResultInfo(item, res); //渲染导入结果
        $result.append($item);
      });
    },
    //渲染结果数据
    initResultInfo: function (data, res) {
      var $item = $('<div>', {
        class: 'data-import-result-item'
      });
      var _count = data.count == null ? '-' : data.count;
      var $header = $('<div>', {
        class: 'data-import-result-header'
      }).html(
        '<span class="result-text">' + data.name + '</span><span class="result-data" style="color:' + data.color + '">' + _count + '</span>'
      );
      var $detail = $('<div>', {
        class: 'data-import-result-detail'
      });
      _.each(data.childDetails, function (item) {
        if (item.name == '组织架构') {
          item.name = '组织架构节点';
        }
        var count = item.count == null ? '-' : item.count;
        var $detail_item = $('<div>', {
          class: 'data-import-result-node'
        }).html(item.name + '<span style="color:' + data.color + '">' + count + '</span>');
        $detail.append($detail_item);
      });
      var $remark = $('<div>', {
        class: 'data-import-result-remark'
      });
      if (data.name != '导入数据') {
        var $tips = $(
          '<span>' +
            (data.name == '重复数据'
              ? res['repeatStrategy'] == '1'
                ? '重复数据已替换原数据'
                : '重复数据已跳过'
              : res['errorStrategy'] == '1'
              ? '异常数据已终止导入'
              : '异常数据已跳过') +
            '</span>'
        );
        var $btn = $(
          '<span class="pull-right"><button type="button" class="well-btn well-btn-sm w-btn-primary w-noLine-btn" data-uuid="' +
            data.taskUuid +
            '" data-name="' +
            data.name +
            '"><i class="iconfont icon-ptkj-yulan"></i>查看</button></span>'
        );
        $remark.html(data.errorMessage).append($tips, $btn);
      } else {
        var finishTime = data.finishTime ? '<span class="mr-lg">完成时间：' + data.finishTime + '</span>' : '';
        var usedTime = data.usedTime ? '<span>用时：' + data.usedTime + '</span>' : '';
        $remark.html(finishTime + usedTime);
      }
      $item.append($header).append($detail).append($remark);
      return $item;
    },
    //渲染日志数据
    initLogInfo: function (data) {
      var $logBody = $('.data-import-log', $container);
      $logBody.append(data);
      // $logBody.empty();
      // _.each(data, function (item, index) {
      //   var time = item.time ? item.time + '  ' : ''; //时间
      //   var error = item.error ? item.error + ',' : ''; //异常信息
      //   var $itemdiv = $('<div>', {
      //     class: 'data-import-log-item'
      //   }).html(time + error + item.status + '<span class="data-import-log-type">' + item.taskName + '</span>导入');
      //   //{时间} {异常信息} {动作}{数据类型名称}导入
      //   $logBody.append($itemdiv);
      // });
    },
    initStatus: function (status) {
      if (status || status === 0) {
        $('.import-status').empty();
        var item = _.find(appCommonJS.statusJson, {
          id: status
        });
        var name = status == 2 ? '导入中' : item.text;
        $('.import-status').append('<span class="data-import"><span class="' + item.class + '">' + name + '</span></span>');
      }
    },
    bindEvent: function () {
      $container
        .on('click', '.btn_close', function () {
          window.close();
        })
        .on('click', '.data-import-result-item button', function () {
          var name = $(this).data('name');
          var uuid = $(this).data('uuid');
          var params = {
            uuid: uuid
          };
          var pageId = '';
          if (name == '重复数据') {
            params.repeat = 1;
            pageId = 'wPage_C6EF9980202611ECBFF1030B2674B732';
          } else if (name == '异常数据') {
            params.error = true;
            pageId = 'wPage_E0765EE0204C11ECB5CF19E29E215BD5';
          }
          _self.resultDialog({
            name: name,
            params: params,
            pageId: pageId
          });
        });
    },
    resultDialog: function (arg) {
      var message = "<div id='newdialogdiv' class='modal_content_div'></div>";
      var dialogOptions = {
        title: arg.name,
        size: 'large',
        message: message,
        className: 'custom-modal-dialog',
        // height: "600px",
        onEscape: function () {},
        // 显示弹出框后事件
        shown: function () {
          $('.bootbox-body').css({
            'max-height': 'initial'
          });
          appContext.renderWidget({
            target: '_dialog',
            targetWidgetId: '',
            renderTo: '#newdialogdiv',
            widgetDefId: arg.pageId,
            params: arg.params,
            callback: function () {
              $('.bootbox-body').css('overflow', 'hidden');
              $('.modal-body', parent.document).css('padding', '0');
            },
            refresh: true
          });
        },
        buttons: {
          cancel: {
            label: '关闭',
            className: 'btn-default',
            callback: function () {}
          }
        }
      };
      appModal.dialog(dialogOptions);
    },
    refresh: function () {
      this.init();
    }
  });
  return AppImportLogWidgetDevelopment;
});
