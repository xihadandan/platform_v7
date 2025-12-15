(function (factory) {
  'use strict';
  if (typeof define === 'function' && define.amd) {
    // AMD. Register as an anonymous module.
    define(['jquery'], factory);
  } else {
    // Browser globals
    factory(jQuery);
  }
})(function ($) {
  var LayoutDocumentUtils = {};
  LayoutDocumentUtils.configList = [];
  LayoutDocumentUtils.getConfigList = function (asyncCallback) {
    if (LayoutDocumentUtils.configList.length === 0) {
      $.ajax({
        url: ctx + '/api/basicdata/layoutDocumentServiceConf/getEnableConfigList',
        type: 'get',
        async: false,
        success: function (result) {
          if (result && result.data && result.data.length && result.data.length > 0) {
            LayoutDocumentUtils.configList = result.data;
            if (asyncCallback != undefined) {
              asyncCallback(LayoutDocumentUtils.configList);
            }
          }
        }
      });
    }
    if (asyncCallback != undefined) {
      asyncCallback(LayoutDocumentUtils.configList);
    }
    return LayoutDocumentUtils.configList;
  };

  /**
   * 打开ofd文件
   */
  LayoutDocumentUtils.openFile = function (fileId, fileName) {
    LayoutDocumentUtils.getConfigList();
    if (LayoutDocumentUtils.configList.length > 0) {
      for (var i = 0; i < LayoutDocumentUtils.configList.length; i++) {
        var configListElement = LayoutDocumentUtils.configList[i];
        if (configListElement.serverUniqueCode === 'SUWELLOFD') {
          var url = location.origin + `/suWellOfdOnlineEditView?fileName=${fileName}&fileId=${fileId}`; // &fieldName=${_this.fieldName}
          var openMode = SystemParams.getValue('pt.layoutdocument.open.mode');
          if ('window' === openMode) {
            window.open(
              url,
              '_blank',
              ' left=0,top=0,width=' + screen.availWidth + ',height=' + (screen.availHeight - 50) + ',scrollbars,resizable=yes,toolbar=no'
            );
          } else {
            window.open(url);
          }
          return;
        }
        // else if (configListElement.serverUniqueCode === '') {
        //
        // }
        if (i === LayoutDocumentUtils.configList.length - 1) {
          appModal.error('没有可用的版式文档服务，请联系系统管理员。');
        }
      }

      // var configListElement = LayoutDocumentUtils.configList[0];
    } else {
      appModal.error('没有可用的版式文档服务，请联系系统管理员。');
    }
  };

  /**
   * 盖章ofd文件
   */
  LayoutDocumentUtils.sealFile = function (fileId, fileName) {
    LayoutDocumentUtils.getConfigList();
    if (LayoutDocumentUtils.configList.length > 0) {
      for (var i = 0; i < LayoutDocumentUtils.configList.length; i++) {
        var configListElement = LayoutDocumentUtils.configList[i];
        if (configListElement.serverUniqueCode === 'SUWELLOFD') {
          var url = location.origin + `/suWellOfdOnlineEditView?fileName=${fileName}&fileId=${fileId}&isEdit=true`; // &fieldName=${_this.fieldName}
          var openMode = SystemParams.getValue('pt.layoutdocument.open.mode');
          if ('window' === openMode) {
            window.open(
              url,
              '_blank',
              ' left=0,top=0,width=' + screen.availWidth + ',height=' + (screen.availHeight - 50) + ',scrollbars,resizable=yes,toolbar=no'
            );
          } else {
            window.open(url);
          }
          return;
        }
        // else if (configListElement.serverUniqueCode === '') {
        //
        // }
        if (i === LayoutDocumentUtils.configList.length - 1) {
          appModal.error('没有可用的版式文档服务，请联系系统管理员。');
        }
      }

      // var configListElement = LayoutDocumentUtils.configList[0];
    } else {
      appModal.error('没有可用的版式文档服务，请联系系统管理员。');
    }
  };

  LayoutDocumentUtils.checkFileType = function (fileName) {
    LayoutDocumentUtils.getConfigList();
    var fileExtensions = [];
    for (var i = 0; i < LayoutDocumentUtils.configList.length; i++) {
      var configListElement = LayoutDocumentUtils.configList[i];
      if (configListElement.fileExtensions) {
        fileExtensions = fileExtensions.concat(configListElement.fileExtensions.split(';'));
      }
    }

    if (fileExtensions.length > 0) {
      var regFileType = new RegExp('\\.(' + fileExtensions.join('|') + ')(\\?.*)?', 'i'); // re为/^\d+bl$/gim
      // var regFileType = /\.(png|jpe?g|gif|bmp|tif|tiff|zip|rar|7z|rtf|dot)(\?.*)?/i;
      return regFileType.test(fileName);
    } else {
      return false;
    }
  };

  LayoutDocumentUtils.dialogNoConfigError = function () {
    LayoutDocumentUtils.getConfigList();
    var configList = LayoutDocumentUtils.configList;
    if (configList.length === 0) {
      appModal.error('没有可用的版式文档服务，请联系系统管理员。');
      return false;
    }
    return true;
  };

  return LayoutDocumentUtils;
});
