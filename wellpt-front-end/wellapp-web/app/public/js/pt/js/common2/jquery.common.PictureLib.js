(function (factory) {
  'use strict';
  if (typeof define === 'function' && define.amd) {
    // AMD. Register as an anonymous module.
    define(['jquery', 'server'], factory);
  } else {
    // Browser globals
    factory(jQuery);
  }
})(function ($, server) {
  'use strict';

  var JDS = null;
  if (server) {
    JDS = server.JDS;
  } else if (window.JDS) {
    JDS = window.JDS;
  }
  var containSelectTypes = function (selectTypes, treeNode) {
    if (selectTypes == null || selectTypes.length == 0) {
      return true;
    }
    if (treeNode.data == null) {
      return false;
    }
    var folderType = treeNode.data.folderType;
    for (var i = 0; i < selectTypes.length; i++) {
      if (selectTypes[i] == folderType) {
        return true;
      }
    }
    return false;
  };
  // 是否允许上传图片
  var isAllowUpload = function (options) {
    var selectTypes = options.selectTypes;
    if (selectTypes == null) {
      return true;
    }
    for (var i = 0; i < selectTypes.length; i++) {
      if (selectTypes[i] == 1) {
        return true;
      }
    }
    return false;
  };
  $.WCommonPictureLib = {
    version: '5.3',
    FILE_TYPE: {
      MONGO: 1,
      PROJECT: 2,
      ICON: 3
    },
    ICON_TYPES: [{
        id: 'icon-ptkj',
        name: '平台基础图标库',
        data: {
          folderType: 3
        }
      }
      // {
      //   id: 'glyphicons',
      //   name: 'Glyphicons字体图标库',
      //   data: { folderType: 3 }
      // },
      // {
      //   id: 'icon',
      //   name: 'iconfont图标库',
      //   data: { folderType: 3 }
      // },
      // {
      //   id: 'pt-mui-icon',
      //   name: '平台手机端图标库',
      //   data: { folderType: 3 }
      // }
    ],
    ICON_TYPES_DATA: {},
    defaultOptions: {
      selectTypes: [1, 2, 3], // 可选择的类型
      multiSelect: false,
      initPrevImg: '',
      uploadUrl: '',
      previewUrl: '',
      confirm: $.noop, // 确认按钮逻辑
      cancel: $.noop,
      mongoFolderID: 'pictureL-pict-pict-pict-pictureLibpi',
      mongoFilePreviewPath: '/repository/file/mongo/download?preview=true&fileID={fileID}',
      mongoFileSavePath: '/repository/file/mongo/saveFilesAndPushToFolder?folderID={folderID}',

      projRelativePath: '\\resources\\pt\\images'
      // 取消按钮逻辑
    },
    show: function (opts) {
      var options = $.extend({}, $.WCommonPictureLib.defaultOptions, opts);
      this._render(options);
    },
    hide: function () {},
    _render: function (options) {
      // 弹出框
      var $picLib = $.WCommonPictureLib._renderPicDialog(options);
      $picLib.addClass('wCommonPictureLib');
      $.WCommonPictureLib._renderPicUpload(options);
      $.WCommonPictureLib._renderPicLib(options);
    },
    // 渲染图片库窗口
    _renderPicDialog: function (options) {
      // 不允许上传图片，隐藏相应的tab
      var allowUpload = isAllowUpload(options);
      var uploadCls = allowUpload ? 'active' : 'hidden';
      var contentCls = allowUpload ? '' : 'active';
      var content =
        '<div id="wCommonPictureLib">' + //
        '<ul class="nav nav-tabs" role="tablist">' + //
        '		<li role="presentation" class="' +
        uploadCls +
        '">' +
        '			<a href="#picUpload" aria-controls="picUpload" role="tab" data-toggle="tab">图片上传</a>' + //
        '		</li>' + //
        '		<li role="presentation" class="' +
        contentCls +
        '">' +
        '			<a href="#picLib" aria-controls="picLib" role="tab" data-toggle="tab">图片库</a>' + //
        '		</li>' + //
        '</ul>' +
        '<div class="tab-content" style="height : 400px">' + //
        '		<div role="tabpanel" class="tab-pane ' +
        uploadCls +
        '" id="picUpload" style="height: 100%;">' + //
        '			<div class="col-xs-4">' + //
        '				<form class="form-horizontal">' + //
        '					<div class="form-group">' + //
        '						<label class="col-sm-4 control-label">图片上传</label>' + //
        '					</div>' + //
        '					<div class="form-group" style="display: none">' + //
        '						<label for="picUploadFrom" class="col-sm-4 control-label">资源来源</label>' + //
        '						<div class="col-sm-8">' + //
        '							<label class="radio-inline">' + //
        '								<input type="radio" name="picUploadFrom" id="picUploadFrom1" value="1" checked>本地' + //
        '							</label>' + //
        '							<label class="radio-inline" style="display: none">' + //
        '								<input type="radio" name="picUploadFrom" id="picUploadFrom2" value="2">网络' + //
        '							</label>' + //
        '						</div>' + //
        '					</div>' + //
        '					<div class="form-group" style="display:none">' + //
        '						<label for="picUploadUrl" class="col-sm-4 control-label">URL地址</label>' + //
        '						<div class="col-sm-8">' + //
        '							<input type="picUploadUrl" name="picUploadUrl" class="form-control" id="picUploadUrl">' + //
        '						</div>' + //
        '					</div>' + //
        '					<div class="form-group">' + //
        '						<label for="picUploadSelect" class="col-sm-4 control-label">图片</label>' + //
        '						<div class="col-sm-8">' + //
        '							<input id="picUploadFile" type="file" name="picUploadFile" style="display:none"/> ' + //
        '							<button id="picUploadFileSelectBtn" type="button" class="btn btn-primary">选择图片</button>' + //
        '						</div>' + //
        '					</div>' + //
        '				</form>' + //
        '			</div>' + //
        '			<div class="col-xs-8">' + //
        '				<form class="form-horizontal">' + //
        '					<div class="form-group">' + //
        '						<label class="control-label">图片预览</label>' + //
        '					</div>' + //
        '					<div class="form-group">' + //
        '						<div id="picUploadPreview"></div>' + //
        '					</div>' + //
        '				</form>' + //
        '			</div>' + //
        '		</div>' + //
        '		<div role="tabpanel" class="tab-pane ' +
        contentCls +
        '" id="picLib" style="height: 100%;">' + //
        '			<div class="col-xs-3" style="height: 100%;padding-left: 0px">' + //
        '				<div id="picLibFolder">' + //
        '					<ul id="picLibFolderTree" class="ztree"></ul>' + //
        '				</div>' + //
        '			</div>' + //
        '			<div class="col-xs-9" style="height: 100%; overflow: auto;">' + //
        '				<div id="picLibPreview">' + //
        '				</div>' + //
        '			</div>' + //
        '		</div>' + //
        '	</div>' + //
        '</div>';

      var dialogOpts = {
        title: '图片库',
        size: 'large',
        data: null,
        message: content,
        shown: function () {
          $('.modal-dialog').find('div').css({
            'box-sizing': 'border-box'
          });

          var mongoFileSavePath = options.mongoFileSavePath.replace('{folderID}', options.mongoFolderID);
          $('#picUploadFile').fileupload({
            url: getBackendUrl() + mongoFileSavePath, // 链接到服务器的地址
            iframe: false,
            dataType: 'json', // 服务器返回的数据格式
            // autoUpload: false,
            add: function (e, data) {
              $('.js-upload-img').on('click', function () {
                data.submit();
              });
            },
            done: function (e, data) {
              var result = data.result;
              var filesData = {};
              if (result.success) {
                var fileIDs = [];
                var filePaths = [];
                for (var i = 0; i < result.data.length; i++) {
                  var mongoFilePreviewPath = options.mongoFilePreviewPath.replace('{fileID}', result.data[i].fileID);
                  fileIDs.push(result.data[i].fileID);
                  filePaths.push(mongoFilePreviewPath);
                }
                filesData.fileType = $.WCommonPictureLib.FILE_TYPE.MONGO;
                filesData.fileIDs = fileIDs.join(',');
                filesData.filePaths = filePaths.join(',');
                return options.confirm.call(this, filesData);
              } else {
                $.WCommonAlert(result.msg);
              }
            },
            error: function (data, status, e) {
              $.WCommonAlert('上传失败');
            }
          });
        },
        buttons: {
          confirm: {
            label: '确定',
            className: 'btn-primary js-upload-img'
          },
          cancel: {
            label: '关闭',
            className: 'btn-default'
          }
        }
      };
      dialogOpts.buttons.cancel.callback = options.onEscape = function () {
        return options.cancel.call(this);
      };

      dialogOpts.buttons.confirm.callback = function () {
        var value = '';
        var data = {};
        if ($('#picUpload').hasClass('active')) {
          if (dialogOpts.data) {
            // 上传图片
            dialogOpts.data.submit();
          }

          // if ($.WCommonPictureLib._checkFile()) {
          // 上传图片
          // var mongoFileSavePath = options.mongoFileSavePath.replace('{folderID}', options.mongoFolderID);
          // var backendUrl = getBackendUrl()
          // var jwt='eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJVMDAwMDAwMDA1OSIsInN5c3RlbVVuaXRJZCI6IlMwMDAwMDAwMDcwIiwibG9naW5UeXBlIjoiMSIsImxvZ2luTmFtZSI6ImFkbV9wdCIsInVzZXJOYW1lIjoi57O757uf566h55CG5ZGYIiwidXNlcklkIjoiVTAwMDAwMDAwNTkiLCJleHAiOjE2MDUyNDI0ODl9.qUqy2ObnfMkMK2u4AslP3EhLDxVbw34SQrq7FgyhsnA'

          // $.ajaxFileUpload({
          //     async: false,
          //     url: backendUrl + mongoFileSavePath + '&jwt=' + jwt , // 链接到服务器的地址
          //     secureuri: false,
          //     fileElementId: 'picUploadFile',// 文件选择框的ID属性
          //     dataType: 'json', // 服务器返回的数据格式
          //     success: function (data) {
          //         var result = eval("(" + data + ")");
          //         if (result.success) {
          //             var fileIDs = [];
          //             var filePaths = [];
          //             for (var i = 0; i < result.data.length; i++) {
          //                 var mongoFilePreviewPath = options.mongoFilePreviewPath.replace('{fileID}',
          //                     result.data[i].fileID);
          //                 fileIDs.push(result.data[i].fileID);
          //                 filePaths.push(mongoFilePreviewPath);
          //             }
          //             data.fileType = $.WCommonPictureLib.FILE_TYPE.MONGO;
          //             data.fileIDs = fileIDs.join(",");
          //             data.filePaths = filePaths.join(",");
          //             return options.confirm.call(this, data);
          //         } else {
          //             $.WCommonAlert(result.msg);
          //         }
          //     },
          //     error: function (data, status, e) {
          //         $.WCommonAlert("上传失败");
          //     }
          // });
          // }
        } else if ($('#picLib').hasClass('active')) {
          var fileIDs = [];
          var filePaths = [];
          $("input[type='checkbox']:checked", $('#picLibPreview')).each(function (key, element) {
            var $element = $(this);
            fileIDs.push($element.attr('fileId'));
            filePaths.push($element.val());
          });
          if (fileIDs.length > 0) {
            var zTree = $.fn.zTree.getZTreeObj('picLibFolderTree');
            var selectNode = zTree.getSelectedNodes()[0];
            data.fileType = selectNode.data.folderType;
            data.fileIDs = fileIDs.join(',');
            data.filePaths = filePaths.join(',');
          }
          return options.confirm.call(this, data);
        }
      };

      var $picLib = $.WCommonDialog(dialogOpts);
      $('#wCommonPictureLib ul a').on('click', function (e) {
        e.preventDefault();
        $(this).tab('show');
      });

      if (options.initPrevImg) {
        // 添加预览图片
        var src = (options.initPrevImg || '') + '';
        if (options.initPrevImg.substr(0, 4) !== 'data') {
          // 非base64格式图片
          var _auth = getCookie('_auth');
          var token = getCookie(_auth);
          src += '&' + _auth + '=' + token;
        }

        $('#picUploadPreview').html('<img class="img-responsive img-rounded" src="' + src + '" style="background-color:#ddd" />');
      }

      return $picLib;
    },
    // 渲染图片上传
    _renderPicUpload: function (options) {
      var $self = $('#wCommonPictureLib');
      // 监听图片URL地址更改
      $('#picUploadFile', $self).on('change', function () {
        if ($.WCommonPictureLib._checkFile()) {
          var file = $('#picUploadFile')[0].files[0];
          // 添加预览图片
          $('#picUploadPreview').html(
            '<img class="img-responsive img-rounded" src="' +
            $.WCommonPictureLib._createObjectURL(file) +
            '" style="background-color:#ddd" />'
          );
        }
      });
      // 监听单击文件选择按钮
      $('#picUploadFileSelectBtn', $self).on('click', function () {
        // 弹出文件选择框
        $('#picUploadFile').click();
      });
    },
    // 渲染图片库
    _renderPicLib: function (options) {
      var $self = $('#wCommonPictureLib');

      var treeSetting = {
        view: {
          dblClickExpand: false,
          selectedMulti: false
        },
        data: {
          simpleData: {
            enable: true
          }
        },
        callback: {
          onClick: function (event, treeId, treeNode) {
            var zTree = $.fn.zTree.getZTreeObj(treeId);
            $.WCommonPictureLib._getFilesByFolderID(options, treeNode.id, treeNode.data.folderType, treeNode.data.__imgs);
            return false;
          }
        }
      };
      var data = [];
      var selectTypes = options.selectTypes;
      if (selectTypes == $.WCommonPictureLib.FILE_TYPE.ICON) {
        //平台图标库不需要后端注册
        data = [].concat($.WCommonPictureLib.ICON_TYPES);
        var WebApp = window.WebApp || {};
        if (WebApp && WebApp.containerDefaults && WebApp.containerDefaults.wtype == 'wMobilePage') {
          // data.splice(0, 1);
          // data.splice(1, 1);
        } else {
          data.splice(3, 1);
        }
      } else {
        $.ajax({
          url: '/web/resource/getProjectImagFolders',
          async: false,
          success: function (result) {
            if (result) {
              for (var i = 0; i < result.length; i++) {
                data.push(result[i]);
              }
            }
          }
        });
      }

      var zTree = $.fn.zTree.init($('#picLibFolderTree'), treeSetting, data);
      var nodes = zTree.getNodes();
      if (nodes.length > 0) {
        // 选中结点，加载数据
        $('#picLibFolderTree_1_span', $('#picLibFolderTree')).trigger('click');
      }
    },
    // 根据文件夹ID获取文件
    // 根据文件夹ID获取文件
    _getFilesByFolderID: function (options, folderID, folderType, imgs) {
      if (folderType == $.WCommonPictureLib.FILE_TYPE.ICON) {
        //图标样式直接解析相关文件
        $.WCommonPictureLib._renderFiles(options, this._explainCssFiles(folderID));
        return;
      }
      $.WCommonPictureLib._renderFiles(options, imgs);
    },

    _explainCssFiles: function (id) {
      if ($.WCommonPictureLib.ICON_TYPES_DATA[id]) {
        return $.WCommonPictureLib.ICON_TYPES_DATA[id];
      }
      var cssFilePice, iconPrefix, iconFlag;
      if (id == 'icon-ptkj') {
        cssFilePice = '/pt-iconfont/iconfont.css';
        iconPrefix = ['icon-ptkj-', 'icon-xmch-', 'icon-wsbs-', 'icon-szgy-', 'icon-oa-', 'icon-zwfw', 'icon-a-'];
        iconFlag = 'iconfont';
      } else if (id == 'glyphicons') {
        cssFilePice = '/css/bootstrap.min.css';
        iconPrefix = ['glyphicon-'];
        iconFlag = 'glyphicon';
      } else if (id == 'icon') {
        cssFilePice = '/iconfont/iconfont.css';
        iconPrefix = ['icon-'];
        iconFlag = 'iconfont';
      } else if (id == 'pt-mui-icon') {
        cssFilePice = '/mobile/mui/css/mui.min.css';
        iconPrefix = ['mui-icon-'];
        iconFlag = 'mui-icon';
      }
      var data = [];

      function _bool(arr, text) {
        var bool = false;
        $.each(arr, function (i, item) {
          if (text.indexOf(item) > -1) {
            bool = true;
            return false;
          }
        });
        return bool;
      }
      for (var i = 0, len = document.styleSheets.length; i < len; i++) {
        if (document.styleSheets[i].href && document.styleSheets[i].href.indexOf(cssFilePice) > -1) {
          var cssRules = document.styleSheets[i].cssRules;
          for (var j = 0, jlen = cssRules.length; j < jlen; j++) {
            if (document.styleSheets[i].cssRules[j].selectorText) {
              var texts = document.styleSheets[i].cssRules[j].selectorText.split(',');
              for (var t = 0, tlen = texts.length; t < tlen; t++) {
                var text = texts[t];
                if (text && (text.indexOf(iconPrefix) > -1 || _bool(iconPrefix, text)) && text.endsWith('::before')) {
                  var _id = text.replace('::before', '').replace('.', iconFlag + ' ');
                  data.push({
                    folderType: 3,
                    id: _id,
                    name: _id
                  });
                }
              }
            }
          }
          $.WCommonPictureLib.ICON_TYPES_DATA[id] = data;
          break;
        }
      }

      return data;
    },

    // 渲染文件列表
    _renderFiles: function (options, iconDatas) {
      $('#picLibPreview').empty();
      var picLibPrevHtml = '';
      var data2Htmlshow = function (data) {
        $('#picLibPreview').find('.row:gt(0)').remove();
        for (var i = 0; i < data.length; i++) {
          if (i % 3 == 0) {
            picLibPrevHtml += '<div class="row">';
          }
          var fileId = '';
          var path = '';
          var dataName = data[i].name;
          switch (data[i].folderType) {
            case $.WCommonPictureLib.FILE_TYPE.MONGO:
              path = options.mongoFilePreviewPath.replace('{fileID}', data[i].id);
              fileId = data[i].id;
              break;
            case $.WCommonPictureLib.FILE_TYPE.PROJECT:
              var id = data[i].id;
              path = staticPrefix + '/images' + id;
              fileId = path;
              dataName = id.substr(id.lastIndexOf('\\') + 1);
              break;
            case $.WCommonPictureLib.FILE_TYPE.ICON:
              path = data[i].id;
              fileId = data[i].id;
              break;
          }
          if ($.WCommonPictureLib.FILE_TYPE.ICON == data[i].folderType) {
            var isChecked = options.value == fileId ? 'checked' : '';
            picLibPrevHtml +=
              '<div class="col-xs-6 col-md-4">' + //
              '		<div class="thumbnail">' + //
              '			<span class="' +
              data[i].id +
              '" aria-hidden="true" style="font-size: 24px;"></span>' + //
              '			<div class="caption">' + //
              '					<input type="checkbox" value="' +
              path +
              '"' + //
              '						fileId="' +
              fileId +
              '" ' +
              isChecked +
              '>' +
              '				<label>' + //
              dataName + //
              '				</label>' + //
              '			</div>' + //
              '		</div>' + //
              '</div>';
          } else {
            picLibPrevHtml +=
              '<div class="col-xs-6 col-md-4">' + //
              '		<div class="thumbnail">' + //
              '			<img src="' +
              ctx +
              path + //
              '				" alt="" style="max-height: 150px; max-width: 100;background-color:#ddd" >' + //
              '			<div class="caption">' + //
              '					<input type="checkbox" value="' +
              path +
              '"' + //
              '						fileId="' +
              fileId +
              '">' +
              '				<label>' + //
              dataName + //
              '				</label>' + //
              '			</div>' + //
              '		</div>' + //
              '</div>';
          }
          if (i % 3 == 2) {
            picLibPrevHtml += '</div>';
          }
        }
        $('#picLibPreview').append(picLibPrevHtml);

        if (options && options.selectIconclass) {
          $("input[type='checkbox'][value='" + options.selectIconclass + "']").prop('checked', true);
        }
      };

      data2Htmlshow(iconDatas || []);

      $('#picLibPreview').on('click', '.thumbnail', function (key, element) {
        var $element = $(this);
        if (!options.multiSelect) {
          $("input[type='checkbox']", $('#picLibPreview')).attr('checked', false);
        }
        $("input[type='checkbox']", $element).attr('checked', true);
      });
    },
    // 获取数据的URL地址
    _createObjectURL: function (blob) {
      if (window.URL) {
        return window.URL.createObjectURL(blob);
      } else if (window.webkitURL) {
        return window.webkitURL.createObjectURL(blob);
      } else {
        return null;
      }
    },
    // 文件检测
    _checkFile: function () {
      // 获取文件
      var file = $('#picUploadFile')[0].files[0];
      // 文件为空判断
      if (file === null || file === undefined) {
        return false;
      }
      // 检测文件类型
      if (file.type.indexOf('image') === -1) {
        $.WCommonAlert('请选择图片文件！');
        return false;
      }
      // 计算文件大小
      var size = Math.floor(file.size / 1024);
      if (size > 5000) {
        $.WCommonAlert('上传文件不得超过5M!');
        return false;
      }
      return true;
    }
  };
});
