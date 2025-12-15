(function (factory) {
  // 配置requirejs
  requirejs.config({
    paths: {
      server: ctx + '/static/pt/js/server',
      imageViewer: ctx + '/static/mobile/mui/js/mui.imageViewer',
      filePreviewApi: ctx + '/static/filepreview/filePreviewApi',
      floatbutton: ctx + '/static/mobile/mui/js/common/mui.floatbutton'
    }
  });
  if (typeof define === 'function' && define.amd) {
    // AMD. Register as an anonymous module.
    define(['mui', 'server'], factory);
  } else {
    // Browser globals
    factory(mui);
  }
})(function ($, server) {
  var FileViewer = {};
  // Office 后缀
  var powerPointExts = (FileViewer.powerPointExts = ['odp', 'pot', 'potm', 'potx', 'pps', 'ppsm', 'ppsx', 'ppt', 'pptm', 'pptx']);
  var wordExts = (FileViewer.wordExts = ['doc', 'docm', 'docx', 'dot', 'dotm', 'dotx', 'odt']);
  var excelExts = (FileViewer.excelExts = ['ods', 'xls', 'xlsb', 'xlsm', 'xlsx']);
  var pdfExts = (FileViewer.pdfExts = ['pdf']);
  var txtExts = (FileViewer.txtExts = ['txt']);
  var excelUrl =
    server.SystemParams.getValue('document.preview.path', 'http://oa.well-soft.com:18082') + '/document/online/viewer?WOPISrc=';
  var powerPointUrl =
    server.SystemParams.getValue('document.preview.path', 'http://oa.well-soft.com:18082') + '/document/online/viewer?WOPISrc=';
  var wordViewerUrl =
    server.SystemParams.getValue('document.preview.path', 'http://oa.well-soft.com:18082') + '/document/online/viewer?WOPISrc=';
  var txtViewerUrl = ctx + '/repository/file/mongo/content2?fileID=';
  var requestServerUrl = server.SystemParams.getValue('sys.context.path', window.location.origin);
  var isMatchExts = (FileViewer.isMatchExts = function (ext, exts) {
    ext = ext || '';
    ext = ext.toLowerCase();
    for (var i = 0; i < exts.length; i++) {
      if (ext === exts[i]) {
        return true;
      }
    }
    return false;
  });
  FileViewer.preview = function (fileObj) {
    var fileUrl = null;
    if (typeof fileObj === 'string') {
      // url预览
      fileUrl = fileObj;
    } else if ($.isPlainObject(fileObj) && fileObj.fileId) {
      var fileName = fileObj.fileName,
        fileExt = fileObj.fileExt;
      if (!fileExt && fileName && fileName.lastIndexOf('.') > 0) {
        fileExt = fileObj.fileExt = fileName.substring(fileName.lastIndexOf('.') + 1);
      }
      var WOPISrc = requestServerUrl + '/wopi/files/' + fileObj.fileId + '?access_token=xxx&wdMobileHost=2&wdTtime=' + new Date().getTime();
      if (isMatchExts(fileExt, excelExts)) {
        // excel
        fileUrl = excelUrl + WOPISrc;
      } else if (isMatchExts(fileExt, powerPointExts)) {
        // powerpoint
        fileUrl = powerPointUrl + WOPISrc;
      } else if (isMatchExts(fileExt, wordExts)) {
        // word
        fileUrl = wordViewerUrl + WOPISrc;
      } else if (isMatchExts(fileExt, pdfExts)) {
        // pdf
        fileUrl = wordViewerUrl + WOPISrc + '&embed=1&PdfMode=1';
      } else if (isMatchExts(fileExt, txtExts)) {
        // txt
        fileUrl = txtViewerUrl + fileObj.fileId + '&preview=true';
      }
    }
    if (fileObj && fileObj.image && fileObj.fileUrl) {
      require(['imageViewer'], function () {
        // 需要时才加载
        var imageViewer = FileViewer.imageViewer;
        if (imageViewer == null || typeof imageViewer === 'undefined') {
          imageViewer = FileViewer.imageViewer = $.defaultImageViewer || $.imageViewer('img[id]');
        }
        var wrapperImg = $('img[id="' + fileObj.fileId + '"]')[0];
        if (wrapperImg == null || typeof wrapperImg === 'undefined') {
          var wrapperImg = document.createElement('img');
          wrapperImg.id = fileObj.fileId;
          wrapperImg.style.display = 'none';
          wrapperImg.classList.add('mui-image');
          wrapperImg.classList.add('filepriview-item');
          document.body.appendChild(wrapperImg);
        }
        wrapperImg.src = fileObj.fileUrl + '&preview=true';
        imageViewer.disposeImage(true);
        imageViewer.findAllImage(); // 重新搜索图片
        $.trigger(wrapperImg, 'tap');
        var stateId = $.ui.pushState(); //
        imageViewer.onClose = function () {
          FileViewer.imageViewer = imageViewer.onClose = null;
          $.ui.popState(stateId); //
        };
      });
    } else if (fileUrl || WOPISrc) {
      if (window.plus) {
        if (WOPISrc) {
          var filePreviewServer = SystemParams.getValue('app.filepreview.server');
          filePreviewServer = 'null' === filePreviewServer ? null : filePreviewServer;
          filePreviewServer = filePreviewServer || SystemParams.getValue('document.preview.path');
          // filePreviewServer = 'http://192.168.20.63:8022'
          var url = filePreviewServer + '/document/online/viewer?WOPISrc=';
          var winOpts = url + encodeURIComponent(WOPISrc);
          fileUrl = winOpts;
        }
        var _wvId = 'pdf';
        var _openw = null;
        if (!_openw) {
          // fileUrl = 'http://192.168.20.63:8022/document/online/viewer?WOPISrc=http://192.168.0.116:18097/wopi/files/58834df1f8d54c43a52039b0fcdc0791?access_token=1564653762894'
          fileUrl = fileUrl + '&platform=app';
          _openw = plus.webview.create(fileUrl, _wvId, {
            scrollIndicator: 'none',
            scalable: true,
            popGesture: 'close',
            // backButtonAutoControl: 'close',
            titleNView: {
              buttons: [
                {
                  type: 'close',
                  float: 'left',
                  onclick: function () {
                    // plus.webview.close(_openw);
                    _openw.close();
                  }
                }
              ],
              backgroundColor: '#FFFFFF',
              titleColor: '#333333',
              // titleText: '标题栏文字',
              splitLine: { color: '#CCCCCC' }
            }
          });
          if (window.plus && window.plus.os.name == 'Android') {
            var nwv = _openw.nativeInstanceObject();
            var setting = plus.android.invoke(nwv, 'getSettings');
            plus.android.invoke(setting, 'setSupportZoom', true);
            plus.android.invoke(setting, 'setBuiltInZoomControls', true);
            plus.android.invoke(setting, 'setDisplayZoomControls', false);
          }
        }
        _openw.addEventListener(
          'close',
          function () {
            _openw = null;
          },
          false
        );
        // _openw.loadURL('url');

        _openw.show('zoom-fade-out');
      } else {
        requirejs(['floatbutton', 'filePreviewApi'], function (floatButton, filePreviewApi) {
          var stateId = $.ui.pushState(); //
          var wrapperIframe = document.createElement('div');
          wrapperIframe.classList.add('filepreview');
          wrapperIframe.innerHTML =
            '<img src="/static/mobile/mui/images/close.png" class="filepreview-title-close"><iframe class="filepreview-body" src="" marginheight="0px" marginwidth="0px" width="100%" height="100%"></iframe>';
          document.body.appendChild(wrapperIframe);
          $(wrapperIframe).on('tap', '.filepreview-title-close', function (event) {
            FileViewer.close();
          });
          FileViewer.close = function () {
            FileViewer.close = null;
            $.ui.popState(stateId); //
            document.body.removeChild(wrapperIframe);
          };
          //
          if (WOPISrc) {
            filePreviewApi.preview(WOPISrc, {
              target: $('.filepreview-body', wrapperIframe)[0]
            });
          } else {
            $('.filepreview-body', wrapperIframe)[0].src = fileUrl;
          }
        });
      }
    } else {
      return $.toast('附件无法预览');
    }
  };
  FileViewer.dispose = function () {
    var dispose = false;
    if (FileViewer.close) {
      FileViewer.close(); // 单实例
      dispose = true;
    }
    if (FileViewer.imageViewer && FileViewer.imageViewer.close) {
      FileViewer.imageViewer.close();
      dispose = true;
    }
    $('img.filepriview-item').each(function () {
      var self = this;
      self.parentNode.removeChild(self);
    });
    return dispose;
  };
  $.FileViewer = FileViewer;
  return FileViewer;
});
