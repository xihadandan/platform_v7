define(['jquery', 'server', 'commons', 'constant', 'appContext', 'appModal', 'DmsFileServices'], function (
  $,
  server,
  commons,
  constant,
  appContext,
  appModal,
  DmsFileServices
) {
  var Browser = commons.Browser;
  var StringUtils = commons.StringUtils;
  var SystemParams = server.SystemParams;

  // 文件在线预览查看器
  var DmsFileOnlineViewer = function (element, options) {
    this.element = element;
    this.$element = $(element);
    this.options = options;
    this.dmsFileServices = new DmsFileServices();
  };
  $.extend(DmsFileOnlineViewer.prototype, {
    viewOnline: function (fileInfo) {
      var _self = this;
      var oin = _self.getOfficeFileInfo(fileInfo);
      // 预览office文件
      if (oin.isOffice === true) {
        _self.viewOffice(fileInfo, oin);
      } else if (_self.isText(fileInfo) || _self.isImage(fileInfo) || _self.isZip(fileInfo) || _self.isH5Media(fileInfo)) {
        _self.viewOnDocumentPreviewServer(fileInfo);
      } else {
        // 获取查看文件的信息
        _self.dmsFileServices.getViewFileInfo(
          fileInfo,
          function (result) {
            var viewFileInfo = result.data;
            if (viewFileInfo && StringUtils.isNotBlank(viewFileInfo.viewUrl)) {
              appContext.getWindowManager().open({
                useUniqueName: true,
                url: viewFileInfo.viewUrl
              });
            } else {
              // 使用jquery.media插件预览
              _self.viewMedia(fileInfo);
            }
          },
          function () {
            // 使用jquery.media插件预览
            _self.viewMedia(fileInfo);
          }
        );
      }
    },
    viewOffice: function (fileInfo, officeInfo) {
      var _self = this;

      var officeUrl = '';
      var oin = officeInfo;
      // if (oin == null) {
      //   oin = _self.getOfficeFileInfo(fileInfo);
      // }
      var basePath = Browser.getBasePath();
      // var sysContext = "http://127.0.0.1:8080";
      var sysContext = SystemParams.getValue('sys.context.path');
      var WOPISrc = sysContext + '/wopi/files/' + fileInfo.dataUuid + '?access_token=' + fileInfo.dataUuid;
      require('filePreviewApi').preview(WOPISrc);
      // WOPISrc = encodeURIComponent(WOPISrc);
      // officeUrl = SystemParams.getValue("document.preview.path", "http://27.154.58.106:8113");
      // officeUrl = officeUrl + "/document/online/viewer?WOPISrc=";
      // officeUrl = officeUrl + WOPISrc;

      // var viewerUrl = basePath + '/dms/file/viewer/' + fileInfo.uuid + '?viewFileUrl=' + officeUrl;
      // appContext.getWindowManager().open({
      //   useUniqueName: true,
      //   url: viewerUrl
      // });
    },
    viewText: function (fileInfo) {
      var basePath = Browser.getBasePath();
      var url = basePath + '/dms/file/view/' + fileInfo.dataUuid;
      var viewerUrl = basePath + '/dms/file/viewer/' + fileInfo.uuid;
      appContext.getWindowManager().open({
        useUniqueName: true,
        url: viewerUrl
      });
    },
    viewImage: function (fileInfo) {
      var basePath = Browser.getBasePath();
      var url = basePath + '/dms/file/view/' + fileInfo.dataUuid;
      var viewerUrl = basePath + '/dms/file/viewer/' + fileInfo.uuid + '?viewFileUrl=' + url;
      appContext.getWindowManager().open({
        useUniqueName: true,
        url: viewerUrl
      });
    },
    viewMedia: function (fileInfo) {
      this.viewOffice(fileInfo, null);
    },
    viewOnDocumentPreviewServer: function (fileInfo) {
      this.viewOffice(fileInfo, null);
      // var basePath = Browser.getBasePath();
      // var documentServerPath = SystemParams.getValue('document.preview.path', 'http://192.168.0.136');
      // documentServerPath = documentServerPath == 'null' ? '' : documentServerPath;
      // var WOPISrc = basePath + '/wopi/files/' + fileInfo.dataUuid + '?access_token=' + fileInfo.dataUuid;
      // var serverFileUrl = documentServerPath + '/document/online/viewer?WOPISrc=' + WOPISrc;
      // var viewerUrl = basePath + '/dms/file/viewer/' + fileInfo.uuid + '?viewFileUrl=' + serverFileUrl;
      //
      // appContext.getWindowManager().open({
      //   useUniqueName: true,
      //   url: viewerUrl
      // });
    },
    viewWithUrl: function (viewFileUrl) {
      var _self = this;
      var $iframe = $(
        '<iframe src="' +
          viewFileUrl +
          '" marginwidth="0" marginheight="0" frameborder="0" width="100%" height="' +
          $(window).height() +
          '"></iframe>'
      );
      _self.options.container.html($iframe);
      $(window).on('resize', function () {
        $iframe.height($(window).height());
      });
    },
    viewByFileUuid: function (fileUuid) {
      var _self = this;
      _self.dmsFileServices.readFileContentAsString({
        fileUuid: fileUuid,
        success: function (result) {
          _self.options.container.html(result.data);
        }
      });
    },
    getOfficeFileInfo: function (fileInfo) {
      var _self = this;
      var oin = {};
      oin.isExcel = _self.isExcel(fileInfo);
      oin.isPowerPoint = _self.isPowerPoint(fileInfo);
      oin.isWord = _self.isWord(fileInfo);
      oin.isPdf = _self.isPdf(fileInfo);
      oin.isVsd = _self.isVsd(fileInfo);
      oin.isOffice = oin.isExcel || oin.isPowerPoint || oin.isWord || oin.isVsd || oin.isPdf;
      return oin;
    },
    isExcel: function (fileInfo) {
      return this.dmsFileServices.isExcel(fileInfo);
    },
    isPowerPoint: function (fileInfo) {
      return this.dmsFileServices.isPowerPoint(fileInfo);
    },
    isWord: function (fileInfo) {
      return this.dmsFileServices.isWord(fileInfo);
    },
    isVsd: function (fileInfo) {
      return this.dmsFileServices.isVsd(fileInfo);
    },
    isPdf: function (fileInfo) {
      return this.dmsFileServices.isPdf(fileInfo);
    },
    isText: function (fileInfo) {
      return this.dmsFileServices.isText(fileInfo);
    },
    isImage: function (fileInfo) {
      return this.dmsFileServices.isImage(fileInfo);
    },
    isZip: function (fileInfo) {
      return this.dmsFileServices.isZip(fileInfo);
    },
    isH5Media: function (fileInfo) {
      return this.dmsFileServices.isH5Media(fileInfo);
    }
  });
  return DmsFileOnlineViewer;
});
