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

  // 平台应用_数据导入导出管理_数据导出记录详情页_HTML二开
  var AppExportLogWidgetDevelopment = function () {
    HtmlWidgetDevelopment.apply(this, arguments);
  };

  // 接口方法
  commons.inherit(AppExportLogWidgetDevelopment, HtmlWidgetDevelopment, {
    // 组件初始化
    init: function (options) {
      _self = this;
      $container = this.widget.element;
      $container.parents('.container-fluid').css({
        background: '#fff'
      });
      $('.header-title-text').html('数据导出日志');
      $('.import-title_1').html('导出结果');
      var pageUuid = Browser.getQueryString('uuid');
      // var pageUuid = this.widget.options.containerDefinition.params.uuid;
      _self.getBaseInfo(pageUuid); //获取基础数据
      _self.bindEvent(); //获取导出结果
    },
    //获取基础数据
    getBaseInfo: function (uuid) {
      var data = {
        url: '/api/dataIO/taskRecordInfo/export/' + uuid,
        params: {},
        type: 'GET'
      };
      appModal.showMask();
      appCommonJS
        .ajaxFunction(data)
        .then(function (res) {
          if (res.code == 0) {
            var ress = res.data;
            $('.header-title-time').html(ress.exportTime.substring(0, ress.exportTime.length - 3));
            _self.initBaseInfo(ress); //渲染基础数据
            _self.initLogInfo(ress.processLog); //渲染日志信息
            _self.getResultInfo(ress.dataRecordDetailBos); //处理导出结果
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
      _.each(appCommonJS.dataExportParams, function (item, index) {
        var $itemdiv = $('<div>', {
          class: 'data-import-info-item'
        });
        var $label = $('<div>', {
          class: 'item-label'
        }).html(item.text);
        var value = data[item.param];
        if (item.param == 'systemUnitNames') {
          value = value.replace(/,/g, '，');
        }
        var $value = $('<div>', {
          class: 'item-value'
        }).html(value);
        $itemdiv.append($label).append($value);
        $infoBody.append($itemdiv);
      });
    },
    //处理结果信息
    getResultInfo: function (data) {
      var $result = $('.data-import-result');
      $result.empty();
      _.each(data, function (item) {
        var $item = _self.initResultInfo(item); //渲染导出结果
        $result.append($item);
      });
    },
    //渲染结果数据
    initResultInfo: function (data) {
      var $item = $('<div>', {
        class: 'data-import-result-item'
      });
      var _count = data.count || data.count === 0 ? (data.taskStatus == 1 ? data.count : '-') : '-'; //完成时显示数量
      var html = '<span class="result-text">' + data.name + '</span>';
      html += _self.initStatus(data.taskStatus);
      html += '<span class="result-data">' + _count + '</span>';
      var $header = $('<div>', {
        class: 'data-import-result-header'
      }).html(html);
      var $detail = $('<div>', {
        class: 'data-import-result-detail'
      });
      _.each(data.childDetails, function (item) {
        var count = item.count || item.count === 0 ? (data.taskStatus == 1 ? item.count : '-') : '-'; //完成时显示数量
        var $detail_item = $('<div>', {
          class: 'data-import-result-node'
        }).html(item.name + '<span style="color:#488cee;">' + count + '</span>');
        $detail.append($detail_item);
      });
      var $remark = $('<div>', {
        class: 'data-import-result-remark'
      });
      if (data.taskStatus == 1) {
        //"完成"
        var finishTime = data.finishTime ? '<span class="mr-lg">完成时间：' + data.finishTime + '</span>' : '';
        var usedTime = data.usedTime ? '<span>用时：' + data.usedTime + '</span>' : '';
        $remark.html(finishTime + usedTime);
      } else if (data.taskStatus == 2) {
        //"导出中"
        var usedTime = data.usedTime ? '<span>已用时：' + data.usedTime + '</span>' : '';
        $remark.html(usedTime);
      } else if (data.taskStatus == 3) {
        //"异常终止"
        var usedTime = data.errorMessage || '';
        $remark.html(usedTime);
      } else if (data.taskStatus == 0) {
        //"取消"
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
      //   }).html(time + error + item.status + '<span class="data-import-log-type">' + item.taskName + '</span>导出');
      //   //{时间} {异常信息} {动作}{数据类型名称}导出
      //   $logBody.append($itemdiv);
      // });
    },
    initStatus: function (status) {
      if (status || status === 0) {
        $('.import-status').empty();
        var item = _.find(appCommonJS.statusJson, {
          id: status
        });
        var name = status == 2 ? '导出中' : item.text;
        return '<span class="data-import ml10"><span class="' + item.class + '">' + name + '</span></span>';
      }
      return '';
    },
    bindEvent: function () {
      $container.on('click', '.btn_close', function () {
        window.close();
      });
    },
    refresh: function () {
      this.init();
    }
  });
  return AppExportLogWidgetDevelopment;
});
