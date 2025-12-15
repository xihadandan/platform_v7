// location.origin polyfill
null == window.location.origin &&
  (window.location.origin =
    window.location.protocol + '//' + window.location.hostname + (window.location.port ? ':' + window.location.port : ''));
function openWindows(url, target) {
  var iframe = target;
  if (typeof target === 'string') {
    iframe = document.getElementById(target);
  }
  if (target && 'IFRAME' === target.tagName.toUpperCase()) {
    target.src = url;
  } else {
    var openMode = SystemParams.getValue('pt.preview.open.mode');
    if ('window' == openMode) {
      window.open(
        url,
        '_blank',
        ' left=0,top=0,width=' + (screen.availWidth - 20) + ',height=' + (screen.availHeight - 80) + ',scrollbars,resizable=yes,toolbar=no'
      );
    } else {
      window.open(url, '_blank');
    }
  }
}
var pdfReg = new RegExp('(pdf)$', 'i');
var ofdReg = new RegExp('(ofd)$', 'i');
var wmvReg = new RegExp('(wmv)$', 'i');
var txtReg = new RegExp('(txt)$', 'i');
var tifReg = new RegExp('(tif|tiff)$', 'i');
var zipReg = new RegExp('(zip|rar|7z)$', 'i');
var imgReg = new RegExp('(gif|jpg|jpeg|png|bmp|tif|tiff)$', 'i');
var excelReg = new RegExp('(ods|xls|xlsb|xlsm|xlsx)$', 'i');
var wordReg = new RegExp('(doc|docm|docx|dot|dotm|dotx|odt)$', 'i');
var pptReg = new RegExp('(odp|pot|potm|potx|pps|ppsm|ppsx|ppt|pptm|pptx)$', 'i');
/**
 *
 */
(function (root, factory) {
  'use strict';
  if (null == window.SystemParams && require.defined('server')) {
    window.SystemParams = require('server').SystemParams;
  }

  if (typeof define === 'function' && define.amd) {
    define(['jquery'], function ($) {
      // 注入filePreviewServer
      return factory.call(this, $);
    });
  } else if (typeof exports === 'object') {
    module.exports = factory(require('jquery'));
  } else {
    root.filePreviewApi = factory(root.jQuery);
  }
})(
  this,

  function FilePreviewConstructor($) {
    return {
      inited: false,
      Previewer: null,
      _init: function () {
        if (this.inited) {
          return;
        }
        this.inited = true;
        //获取参数
        var filePreviewType = SystemParams.getValue('app.filepreview.type');
        filePreviewType = 'null' === filePreviewType ? null : filePreviewType;
        var filePreviewServer = SystemParams.getValue('app.filepreview.server');
        filePreviewServer = 'null' === filePreviewServer ? null : filePreviewServer;
        // filePreviewServer = 'http://192.168.20.63:8022'
        this.Previewer = {
          isAvailable: function () {
            return true;
          },
          preview: function (WOPISrc, options) {
            // 默认 filePreviewType = wellFilePreview 的预览方式
            options = options || {};
            filePreviewServer = filePreviewServer || SystemParams.getValue('document.preview.path');
            var url = filePreviewServer + '/document/online/viewer?WOPISrc=';
            var winOpts = url + encodeURIComponent(WOPISrc);
            openWindows(winOpts, options.target);
          }
        };
        if ('msOfficeOnline' === filePreviewType) {
          this.Previewer.preview = function (WOPISrc, options) {
            options = options || {};
            // 查询文件信息
            $.ajax({
              type: 'GET',
              url: WOPISrc,
              data: $.extend(
                {
                  ignoreSha256: 'true'
                },
                options.wopiOptions
              ),
              dataType: 'json',
              success: function (fileInfo) {
                // console.log(fileInfo);
                var url = filePreviewServer;
                var fileName = fileInfo.BaseFileName;
                if (wordReg.test(fileName)) {
                  url += '/wv/wordviewerframe.aspx?WOPISrc=';
                } else if (excelReg.test(fileName)) {
                  url += '/x/_layouts/xlviewerinternal.aspx?WOPISrc=';
                } else if (pptReg.test(fileName)) {
                  url += '/p/PowerPointFrame.aspx?WOPISrc=';
                } else {
                  return window.appModal && appModal.info('附件类型不支持预览');
                }
                var winOpts = url + encodeURIComponent(WOPISrc);
                openWindows(winOpts, options.target);
              },
              error: function () {
                window.appModal && appModal.error('获取附件信息错误');
              }
            });
          };
        } else if ('yozodcs' === filePreviewType) {
          // factory = yozodcs;
          this.Previewer.preview = function (WOPISrc, options) {
            options = options || {};
            // 查询文件信息
            $.ajax({
              type: 'GET',
              url: WOPISrc,
              data: $.extend(
                {
                  ignoreSha256: 'true'
                },
                options.wopiOptions
              ),
              dataType: 'json',
              success: function (fileInfo) {
                // console.log(fileInfo);
                /**
0-----文档格式到高清html的转换。先对与1，高清显示更多的细节，比如word中的图片。
1-----文档格式到html的转换
14----pdf文档格式到html的转换
17----tif文件转成html
19----压缩文件到html的转换(模版)
20----PDF文件到html的转换(模版)
21----OFD文件到html的转换
23----图片到html的转换
45----视频到mp4的转换
61----文档格式到高清html canvas的转换。针对office文件，兼容性更好
             */
                var convertType = 0; //
                var fileName = fileInfo.BaseFileName;
                if (pdfReg.test(fileName)) {
                  convertType = 20;
                } else if (ofdReg.test(fileName)) {
                  convertType = 21;
                } /*else if (tifReg.test(fileName)) {
              convertType = 17;
            }*/ else if (imgReg.test(fileName)) {
                  convertType = 23;
                } else if (wmvReg.test(fileName)) {
                  convertType = 45;
                } else if (zipReg.test(fileName)) {
                  convertType = 19;
                }
                (function doConvert(convertType, password) {
                  window.appModal && appModal.showMask('附件转换中...', null, 60 * 60 * 1000);

                  $.ajax({
                    type: 'GET',
                    url: getBackendUrl() + '/wopi/files/getYozoPreviewUrl',
                    data: {
                      fileName: fileName,
                      fileUrl: WOPISrc.replace(fileInfo.SHA256, fileInfo.SHA256 + '/contents'),
                      convertType: convertType
                    },
                    dataType: 'text',
                    success: function (result) {
                      result = eval('(' + result + ')');
                      if (0 == result.errorcode) {
                        var vToken = result.data;
                        openWindows(vToken.viewUrl, options.target);
                      } else if (4 == result.errorcode) {
                        var password = prompt('请输入密码');
                        if ($.trim(password).length) {
                          doConvert(convertType, password);
                        }
                      } else {
                        window.appModal && appModal.error(result.message);
                      }
                    },
                    error: function (err) {
                      window.appModal && appModal.error('附件转换失败');
                    },
                    complete: function () {
                      window.appModal && appModal.hideMask();
                    }
                  });
                })(convertType);
              },
              error: function () {
                window.appModal && appModal.error('获取附件信息错误');
              }
            });
          };
        }
      },
      isAvailable: function isAvailable() {
        this._init();
        return true;
      },
      preview: function preview(WOPISrc, options) {
        this._init();
        this.Previewer.preview(WOPISrc, options);
      }
    };
  }
);
