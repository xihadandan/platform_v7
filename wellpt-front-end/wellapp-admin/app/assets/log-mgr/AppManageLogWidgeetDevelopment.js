define([
  'constant',
  'commons',
  'server',
  'appContext',
  'mergely',
  'appModal',
  'HtmlWidgetDevelopment',
  'css!' + ctx + '/static/js/codemirror-5.62.0/lib/codemirror.css',
  'css!' + ctx + '/static/js/mergely/mergely'
], function (constant, commons, server, appContext, mergely, appModal, HtmlWidgetDevelopment) {
  var AppManageLogWidgeetDevelopment = function () {
    HtmlWidgetDevelopment.apply(this, arguments);
  };
  commons.inherit(AppManageLogWidgeetDevelopment, HtmlWidgetDevelopment, {
    init: function () {
      var $element = this.widget.element;
      var uuid = GetRequestParam().uuid || this.getWidgetParams().uuid;
      parent.$('iframe').parent().css({
        paddingBottom: '52%'
      });
      $element.parents('.container-fluid').css({
        background: '#fff'
      });
      appModal.showMask('正在加载中，请稍后...');

      $.ajax({
        type: 'get',
        url: ctx + '/api/log/manage/operation/getLogManageOperation?logManageOperationUuid=' + uuid,
        dataType: 'json',
        success: function (res) {
          appModal.hideMask();
          if (res.code == 0) {
            var data = res.data;
            if (data.dataTypeId == 1) {
              // 流程分类
              var html = '';
              var colorBefore = '';
              var colorAfter = '';
              $.each(data.logManageDetailsEntity, function (index, item) {
                if (item.dataShowType == 'icon') {
                  html +=
                    '<div class="log-row log-icon">' +
                    '<label>' +
                    item.attrName +
                    '：</label>' +
                    '从 <span class="icon-before ' +
                    (item.beforeValue || 'iconfont icon-ptkj-fenlei2') +
                    '"></span> 改为 <span class="icon-after ' +
                    (item.afterValue || 'iconfont icon-ptkj-fenlei2') +
                    '"></span>' +
                    '</div>';
                } else if (item.dataShowType == 'text') {
                  html +=
                    '<div class="log-row">' +
                    '<label>' +
                    item.attrName +
                    '：</label>' +
                    '从' +
                    (item.beforeValue != null ? '【' + item.beforeValue + '】' : '无') +
                    '改为【' +
                    (item.afterValue || '无') +
                    '】' +
                    '</div>';
                } else if (item.dataShowType == 'icon-color') {
                  colorBefore = item.beforeValue;
                  colorAfter = item.afterValue;
                }
              });

              $('.log-row-wrapper', $element).append(html);
              if (colorBefore != '' || colorAfter != '') {
                $('.log-row-wrapper', $element)
                  .find('.icon-before')
                  .css({
                    background: colorBefore || '#64B3EA'
                  });
                $('.log-row-wrapper', $element)
                  .find('.icon-after')
                  .css({
                    background: colorAfter || '#64B3EA'
                  });
              }
            } else if (data.dataTypeId == 2) {
              // 流程定义
              var height = 460;
              var operation = ['add', 'delete', 'defExport'];

              if (operation.indexOf(data.operationId) == -1 && !(data.operationId == 'defImport' && data.beforeDataName == null)) {
                if ((data.beforeDataName != null || data.afterDataName != null) && data.afterDataName != data.beforeDataName) {
                  var html = '<div class="log-row">' + data.dataNameInfo + ' </div>';
                  $('.log-row-wrapper', $element).append(html);
                  height = height - 50;
                }
              }

              $('.log-diff-wrapper', $element).show();
              if (data.operationId == 'delete') {
                $('.log-diff-wrapper', $element).addClass('type-del');
              }
              var beforeData = trimBlank(data.beforeMessageValue || '');
              var afterData = trimBlank(data.afterMessageValue || '');
              if (beforeData == '') {
                $('.log-diff-wrapper', $element).addClass('type-add');
              }
              if (operation.indexOf(data.operationId) > -1 || (data.operationId == 'defImport' && data.beforeDataName == null)) {
                $('.type', $element).hide();
                height = height - 0 + 40;
              }

              $('#mergely', $element).mergely({
                license: 'lgpl',
                cmsettings: {
                  readOnly: true
                },
                bgcolor: '#fff',
                height: height,
                _debug: '',
                ignorews: true,
                line_numbers: true,
                lhs: function (setValue) {
                  setValue(formateXml(beforeData));
                },
                rhs: function (setValue) {
                  setValue(formateXml(afterData));
                },
                loaded: function () {
                  $('#mergely').mergely('scrollTo', 'lhs', 0);
                  $('#mergely').mergely('scrollTo', 'rhs', 0);

                  $element.find('.log-diff-wrapper').not('.type-add,.type-del').find('pre.ch.e').prev().find('.CodeMirror-linenumber').css({
                    background: '#6395F9'
                  });
                  $element.find('.log-diff-wrapper').not('.type-add,.type-del').find('pre.ch.d').prev().find('.CodeMirror-linenumber').css({
                    background: '#F6AAA0'
                  });
                  $element.find('.log-diff-wrapper').not('.type-add,.type-del').find('pre.ch.a').prev().find('.CodeMirror-linenumber').css({
                    background: '#44D89F'
                  });

                  if (operation.indexOf(data.operationId) == -1) {
                    // var $add = $(".CodeMirror-line.mergely.ch.a.rhs ",$element);
                    // var $add1 = $(".mergely.rhs.a.CodeMirror-linebackground ",$element);
                    //
                    //
                    // $.each($add,function(index,item){
                    //     var len = $("#mergely-editor-lhs",$element).find(".CodeMirror-scroll").find(".CodeMirror-code > div").length;
                    //     var lIndex = $(item).parent().index();
                    //     if(lIndex == 0){
                    //         $("#mergely-editor-lhs",$element).find(".CodeMirror-scroll").find(".CodeMirror-code").prepend("<div class='line-add'></div>");
                    //     }else if(lIndex>=len){
                    //         $("#mergely-editor-lhs",$element).find(".CodeMirror-scroll").find(".CodeMirror-code").append("<div class='line-add'></div>");
                    //     }else{
                    //         $("#mergely-editor-lhs",$element).find(".CodeMirror-scroll").find(".CodeMirror-code>div:eq("+lIndex+")").before("<div class='line-add'></div>");
                    //     }
                    // })
                    // $.each($add1,function(index,item){
                    //     var len = $("#mergely-editor-lhs",$element).find(".CodeMirror-scroll").find(".CodeMirror-code > div").length;
                    //     var lIndex = $(item).parent().index();
                    //     if(lIndex == 0){
                    //         $("#mergely-editor-lhs",$element).find(".CodeMirror-scroll").find(".CodeMirror-code").prepend("<div class='line-add'></div>");
                    //     }else if(lIndex>=len){
                    //         $("#mergely-editor-lhs",$element).find(".CodeMirror-scroll").find(".CodeMirror-code").append("<div class='line-add'></div>");
                    //     }else{
                    //         $("#mergely-editor-lhs",$element).find(".CodeMirror-scroll").find(".CodeMirror-code>div:eq("+lIndex+")").before("<div class='line-add'></div>");
                    //     }
                    // })
                    // var summary = $('#mergely',$element).mergely('summary');
                    //
                    // var addCount = $(".mergely.rhs.a.CodeMirror-linebackground").length;
                    // var delCount = $("pre.d",$element).length;
                    //
                    // $(".addLine",$element).html("新增" + ($(".CodeMirror-line.mergely.ch.a.rhs",$element).length - 0 + addCount) + "行");
                    // $(".editLine",$element).html("编辑" + $("#mergely-editor-rhs",$element).find(".CodeMirror-code").find("[class=' CodeMirror-line mergely ch e rhs ']").length + "行");
                    // $(".delLine",$element).html("删除" + (summary.d - 0 + delCount) + "行");
                  }
                }
              });
            }
          } else {
            appModal.error(res.msg);
          }
        }
      });

      function trimBlank(str) {
        return str.replace(/(\n[\s\t]*\r*\n)/g, '\n').replace(/^[\n\r\n\t]*|[\n\r\n\t]*$/g, '');
      }

      function formateXml(xmlStr) {
        var text = xmlStr;
        //使用replace去空格
        text =
          '\n' +
          text
            .replace(/(<\w+)(\s.*?>)/g, function ($0, name, props) {
              return name + ' ' + props.replace(/\s+(\w+=)/g, ' $1');
            })
            .replace(/>\s*?</g, '>\n<');
        //处理注释
        text = text
          .replace(/\n/g, '\r')
          .replace(/<!--(.+?)-->/g, function ($0, text) {
            var ret = '<!--' + escape(text) + '-->';
            return ret;
          })
          .replace(/\r/g, '\n');
        //调整格式  以压栈方式递归调整缩进
        var rgx = /\n(<(([^\?]).+?)(?:\s|\s*?>|\s*?(\/)>)(?:.*?(?:(?:(\/)>)|(?:<(\/)\2>)))?)/gm;
        var nodeStack = [];
        var output = text.replace(rgx, function ($0, all, name, isBegin, isCloseFull1, isCloseFull2, isFull1, isFull2) {
          var isClosed = isCloseFull1 == '/' || isCloseFull2 == '/' || isFull1 == '/' || isFull2 == '/';
          var prefix = '';
          if (isBegin == '!') {
            //!开头
            prefix = setPrefix(nodeStack.length);
          } else {
            if (isBegin != '/') {
              ///开头
              prefix = setPrefix(nodeStack.length);
              if (!isClosed) {
                //非关闭标签
                nodeStack.push(name);
              }
            } else {
              nodeStack.pop(); //弹栈
              prefix = setPrefix(nodeStack.length);
            }
          }
          var ret = '\n' + prefix + all;
          return ret;
        });
        var prefixSpace = -1;
        var outputText = output.substring(1);
        //还原注释内容
        outputText = outputText.replace(/\n/g, '\r').replace(/(\s*)<!--(.+?)-->/g, function ($0, prefix, text) {
          if (prefix.charAt(0) == '\r') prefix = prefix.substring(1);
          text = unescape(text).replace(/\r/g, '\n');
          var ret = '\n' + prefix + '<!--' + text.replace(/^\s*/gm, prefix) + '-->';
          return ret;
        });
        outputText = outputText.replace(/\s+$/g, '').replace(/\r/g, '\r\n');
        return outputText;
      }

      function setPrefix(prefixIndex) {
        var result = '';
        var span = '    '; //缩进长度
        var output = [];
        for (var i = 0; i < prefixIndex; ++i) {
          output.push(span);
        }
        result = output.join('');
        return result;
      }
    },
    refresh: function () {
      this.init();
    }
  });
  return AppManageLogWidgeetDevelopment;
});
