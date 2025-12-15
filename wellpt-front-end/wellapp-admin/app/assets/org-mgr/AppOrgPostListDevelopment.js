define(['constant', 'commons', 'server', 'appContext', 'appModal', 'ListViewWidgetDevelopment'], function (
  constant,
  commons,
  server,
  appContext,
  appModal,
  ListViewWidgetDevelopment
) {
  var AppOrgPostListDevelopment = function () {
    ListViewWidgetDevelopment.apply(this, arguments);
  };
  // 接口方法
  commons.inherit(AppOrgPostListDevelopment, ListViewWidgetDevelopment, {
    btn_export: function () {
      var url = getBackendUrl() + '/multi/org/duty/export';
      var _auth = getCookie('_auth');
      if (_auth) {
        url += '?' + _auth + '=' + getCookie(_auth);
      }
      openWindowByPost(url, null, '_blank');
    },
    btn_import: function () {
      var self = this;
      var msg = getMessage();
      appModal.dialog({
        title: '导入职务',
        size: 'large',
        message: msg,
        buttons: {
          confirm: {
            label: '确定',
            className: 'btn-primary',
            callback: function () {
              var file = $('#uploadfile').val();
              if (file == '') {
                appModal.error('请选择xls文件');
                return false;
              }
              if (file.indexOf('.') < 0) {
                return false;
              }
              var fileType = file.substring(file.lastIndexOf('.'), file.length);
              if (fileType == '.xls' || fileType == '.xlsx') {
                var url = getBackendUrl() + '/multi/org/duty/import';
                var _auth = getCookie('_auth');
                if (_auth) {
                  url += '?' + _auth + '=' + getCookie(_auth);
                }
                $.ajaxFileUpload({
                  url: url, // 链接到服务器的地址
                  secureuri: false,
                  fileElementId: 'uploadfile', // 文件选择框的ID属性
                  dataType: 'text', // 服务器返回的数据格式
                  success: function (data, status) {
                    // QQ浏览器会多返回莫名奇妙的额外数据，所以套个text()方法来过滤脏数据
                    data = $('<div>' + data + '</div>').text();
                    if (data.indexOf('成功') > 0) {
                      appModal.alert(data);
                      self.refresh();
                    } else {
                      appModal.alert(data);
                    }
                  },
                  error: function (data) {}
                });
              } else {
                appModal.error('请选择xls文件');
                return false;
              }
            }
          },
          cancel: {
            label: '取消',
            className: 'btn-default'
          }
        }
      });
      function getMessage() {
        var message = '';
        message +=
          '<form id="import_form" class="form-widget" name="import_form" tenctype="multipart/form-data" method="post">' +
          '<div class="well-form">' +
          '<div class="form-group">' +
          '<label for="sapCode" class="well-form-label control-label">选择XLS文件</label>' +
          '<div class="well-form-control">' +
          '<input id="uploadfile" name="upload" type="file">' +
          '<div class="content"><a href="' +
          ctx +
          '/static/resfacade/share/职务数据导入模板_6.0.xls">职务数据导入模板</a></div>' +
          '</div>' +
          '</div>' +
          '</div>';
        ('</form>');
        return message;
      }
    },
    btn_def_export: function () {
      this.onExport('duty');
    },
    btn_def_import: function () {
      this.onImport();
    }
  });
  return AppOrgPostListDevelopment;
});
