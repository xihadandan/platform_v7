define(['jquery', 'commons', 'constant', 'server', 'appContext', 'appModal', 'DmsListViewViewAction', 'lodash'], function (
  $,
  commons,
  constant,
  server,
  appContext,
  appModal,
  DmsListViewViewAction,
  _
) {
  var JDS = server.JDS;
  var UUID = commons.UUID;
  var StringUtils = commons.StringUtils;
  var StringBuilder = commons.StringBuilder;
  var uploadUrl = '/repository/file/mongo/savefiles';
  var downloadUrl = '/repository/file/mongo/download?fileID=';
  var deleteUrl = '/repository/file/mongo/deleteFile?fileID=';
  var uploadOptions = {
    url: uploadUrl,
    dataType: 'json',
    autoUpload: true,
    maxFileSize: 50000000, // 50MB
    previewMaxWidth: 100,
    previewMaxHeight: 100,
    previewCrop: true
  };
  var dataImportBean = {
    title: '',
    importTemplate: '',
    importTemplate_fileNames: '',
    typeCode: '',
    typeName: '',
    beginRow: '',
    dynamicTable: '',
    fieldConfigs: {}
  };
  var DmsDataImportListViewAction = function () {
    DmsListViewViewAction.apply(this, arguments);
  };
  var bean = {};

  // 导入
  function showImportDialog(options) {
    var sb = new StringBuilder();
    var dataImports = options.ui.getConfiguration().dataImport;
    _.forEach(dataImports, function (value) {
      if (value.dataImportBtn == options.button.code) {
        bean = $.extend(dataImportBean, value.dataImportConfiguration);
        return true;
      }
    });
    if (_.isEmpty(bean)) {
      return false;
    }
    var templateEngine = appContext.getJavaScriptTemplateEngine();
    var fileUploadId = UUID.createUUID();
    var dlgId = 'dlg_' + fileUploadId;
    var fileValueId = fileUploadId + '_value';
    options.fileUploadId = fileUploadId;
    var message = templateEngine.renderById('dataImport', {
      ctx: ctx,
      dlgId: dlgId,
      fileUploadId: fileUploadId,
      importTemplate: bean.importTemplate,
      importTemplate_fileNames: bean.importTemplate_fileNames
    });
    uploadOptions.acceptFileTypes = null;
    if (bean.fileFormateLimit) {
      //限制文件格式的正则表达式
      uploadOptions.acceptFileTypes = new RegExp(bean.fileFormateLimit);
      uploadOptions.messages = { acceptFileTypes: bean.fileFormateLimitTip || '不允许的文件格式' };
      uploadOptions.processfail = function (e, data) {
        var currentFile = data.files[data.index];
        if (data.files.error && currentFile.error) {
          appModal.error(currentFile.error);
          appModal.hideMask();
        }
      };
    }
    var dialogOptions = {
      title: bean.title,
      size: 'middle',
      message: message,
      shown: function () {
        appContext.require(
          ['jquery-validate', 'jquery.fileupload', 'jquery.iframe-transport', 'jquery.fileupload-validate', 'jquery.fileupload-process'],
          function (validate) {
            $('#' + fileUploadId)
              .fileupload(uploadOptions)
              .off('fileuploadadd')
              .on('fileuploadadd', function (e, data) {
                appModal.showMask('文件传输中...');
              })
              .on('fileuploadprogressall', function (e, data) {
                debugger;
              })
              .on('progressall', function (e, data) {
                debugger;
              })
              .on('fileuploaddone', function (e, data) {
                var uploadedFile = data.result.data[0];
                var fileId = uploadedFile.fileID;
                var fileName = uploadedFile.filename;
                $('#' + fileValueId).val(fileId);
                var successTip = '<span>' + fileName + '已上传！</span>';
                $('.uploaded-file-info', '#' + dlgId).html(successTip);
                appModal.hideMask();
              })
              .on('fileuploadfail', function (e, data) {
                appModal.error('文件上传失败!');
                appModal.hideMask();
              });
          }
        );
      },
      buttons: {
        confirm: {
          label: '导入',
          className: 'btn-primary',
          callback: function () {
            var fileId = $('#' + fileValueId).val();
            if (StringUtils.isBlank(fileId)) {
              appModal.error('请先上传导入文件！');
              return false;
            }
            var resultEle = $('#' + dlgId + '_import_result');
            resultEle.empty();
            resultEle.siblings('select').remove();
            $('#' + dlgId + '_table_result_container').empty();
            $('#' + dlgId + '_table_result').remove();
            var isImportByForm = bean.importType != '2';
            appModal.showMask('导入文件处理中', null, 1000 * 60 * 3);
            JDS.call({
              service: 'dmsDataImportService.' + (isImportByForm ? 'importFormRepoFile' : 'importByListener'),
              data: [fileId, isImportByForm ? bean : bean.customImportListener],
              async: true,
              version: '',
              success: function (result) {
                appModal.hideMask();
                options.ui.refresh(); //刷新表格数据列表
                // 触发导入完成后事件
                options.ui.trigger('afterImportCompletion', result.data);
                if (result.data) {
                  resultEle.html(
                    '<a class="text-success dms-export-report">成功 ' +
                      result.data.success.length +
                      '</a>' +
                      '<span> / </span>' +
                      '<a class="text-danger dms-export-report">失败 ' +
                      result.data.fail.length +
                      '</a>'
                  );
                }
                if (resultEle.siblings('select').length == 0) {
                  var $select = $('<select>', { style: 'float:right;width:100px' });
                  $select.append(
                    $('<option>', { value: '全部' }).text('全部'),
                    $('<option>', { value: '成功' }).text('成功'),
                    $('<option>', { value: '失败' }).text('失败')
                  );
                  $select.insertAfter(resultEle);
                }
                var $table = $('#' + dlgId + '_table_result');
                if ($table.length == 0) {
                  $('#' + dlgId + '_table_result_container').append(
                    $('<table>', {
                      id: dlgId + '_table_result',
                      class: 'table table-bordered'
                    })
                  );
                  $table = $('#' + dlgId + '_table_result');
                }
                $table.data('idata', result.data);
                $table.bootstrapTable({
                  data: [].concat(result.data.fail).concat(result.data.success),
                  striped: true,
                  idField: 'rowIndex',
                  sidePagination: 'client',
                  pagination: true,
                  pageSize: 2,
                  pageList: [],
                  columns: [
                    {
                      field: 'rowIndex',
                      title: '行标',
                      width: 60
                    },
                    {
                      field: 'ok',
                      title: '结果',
                      width: 60,
                      formatter: function (value, row, index) {
                        return value ? '<span class="text-success">成功</span>' : '<span class="text-danger">失败</span>';
                      }
                    },
                    {
                      field: 'msg',
                      title: '导入提示'
                    }
                  ]
                });
              },
              error: function () {
                appModal.hideMask();
              }
            });
            return false;
          }
        },
        cancel: {
          label: '取消',
          className: 'btn-default',
          callback: function () {}
        }
      }
    };
    appModal.dialog(dialogOptions);

    $('#' + dlgId).on('change', 'select', function () {
      var $table = $('#' + dlgId + '_table_result');
      if ($(this).val() == '全部') {
        $table.bootstrapTable('load', $table.data('idata').fail.concat($table.data('idata').success));
      } else if ($(this).val() == '成功') {
        $('#' + dlgId + '_table_result').bootstrapTable('load', $('#' + dlgId + '_table_result').data('idata').success);
      } else {
        $('#' + dlgId + '_table_result').bootstrapTable('load', $('#' + dlgId + '_table_result').data('idata').fail);
      }
    });

    $('#' + dlgId).on('click', '.dms-export-report', function () {
      appModal.showMask('导出中', null, 1000 * 60 * 3);
      JDS.call({
        service: 'dmsDataImportService.generateImportDataReportExcel',
        data: [$('#' + fileValueId).val(), $(this).is('.text-success') ? 1 : 2, $('#' + dlgId + '_table_result').data('idata')],
        async: true,
        version: '',
        success: function (result) {
          appModal.hideMask();
          if (result.data) {
            var $iframe = $('<iframe>', {
              src: downloadUrl + result.data,
              style: 'display:none;'
            });
            $('body').append($iframe);
            window.setTimeout(function () {
              $iframe.remove();
            }, 1000);
          }
        },
        error: function () {
          appModal.hideMask();
          appModal.error('导出失败');
        }
      });
    });
  }

  commons.inherit(DmsDataImportListViewAction, DmsListViewViewAction, {
    btn_list_import_data: function (options) {
      var _self = this;
      showImportDialog(options);
      $("input[name='upload']").hide();
      $('#btn_' + options.fileUploadId)
        .off('click')
        .on('click', function (value) {
          $('#' + options.fileUploadId).trigger('click');
        });
    }
  });
  return DmsDataImportListViewAction;
});
