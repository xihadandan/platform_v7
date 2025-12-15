(function ($) {
  var FileUpload = function (element, options) {
    this.$element = $(element);
    this.options = $.extend({}, $.fn.fileUpload.defaults, options);
    this.init();
  };
  FileUpload.prototype = {
    constructor: FileUpload,
    init: function () {
      var $element = this.$element;
      var options = this.options;
      var $this = this;
      if (options.folderId != null) {
        this.nodeName = options.folderId;
      } else {
        this.nodeName = new UUID().id.toLowerCase();
      }

      this.files = [];
      var fileDivId = new UUID().id.toLowerCase();
      var html =
        '<div id="upload_div" class="upload_div">' +
        '<span class="btn btn-success fileinput-button2">' +
        '<span class="file_icon"></span>' +
        '<span class="add_icon">添加附件</span>' +
        '<input id="' +
        fileDivId +
        '" name="files" type="file" class="fileupload_css" />' +
        '</span></div>' +
        '<div class="files files_' +
        fileDivId +
        '">' +
        '<div class="fileList">' +
        '<ul class="attach-list"></ul>' +
        '</div></div>';
      $element.append(html);
      options.formData.attach = this.nodeName;
      $('#' + fileDivId)
        .fileupload(options)
        .on('fileuploadadd', function (e, data) {
          pageLock('show');
        })
        .on('fileuploadprogressall', function (e, data) {})
        .on('fileuploaddone', function (e, data) {
          pageLock('hide');
          onFileuploadDone.call($this, data.result.data);
        })
        .on('fileuploadfail', function (e, data) {});

      // 初始化文件列表
      if (options.folderId != null) {
        JDS.call({
          service: 'jcrFileService.getFiles2',
          data: ['DY_TABLE_FORM', options.folderId],
          success: function (result) {
            onFileuploadDone(result.data);
          }
        });
      }

      var $files = this.files;
      function onFileuploadDone(data) {
        var files = data;
        $.each(files, function (i, file) {
          if (file.id == null || $.trim(file.id) == '') {
            file.id = new UUID().id.toLowerCase();
          }
          $files.push(file);
          var html =
            '<div><div class="files_div"><span class="file_icon"></span><p class="filename">' +
            file.filename +
            '</p>' +
            ' <div class="bar bar-div" ></div></div>' +
            "<div class='delete_Div'><input type='button' id = '" +
            file.id +
            "'  class='delete_input' filename='" +
            file.filename +
            "' value='删除' /></div></div></div>";
          $('.files_' + fileDivId + ' .attach-list').append(html);
          $('input[id=' + file.id + ']').click($.proxy($this._onDeleteFile, $this, file.id));

          // 不允许删除处理
          if (options.allowDelete === false) {
            $('.delete_Div > .delete_input').hide();
          }
        });
      }

      // 不允许添加附件处理
      if (options.allowUpload === false) {
        $('#upload_div', $element).hide();
      }
    },
    getFolderId: function () {
      return this.nodeName;
    },
    _onDeleteFile: function (fileId) {
      var newFiles = [];
      var deleteFile = null;
      for (var i = 0; i < this.files.length; i++) {
        var tmp = this.files[i];
        if (fileId != tmp['id']) {
          newFiles.push(tmp);
        } else {
          deleteFile = tmp;
        }
      }
      this.files = newFiles;
      $('input[id=' + fileId + ']')
        .parent()
        .parent()
        .remove();
    }
  };
  $.fn.fileUpload = function (option) {
    var method = false;
    var args = null;
    if (arguments.length == 2) {
      method = true;
      args = arguments[1];
    }
    var element = this[0];
    var $this = $(element),
      data = $this.data('fileUpload'),
      options = $.extend({}, $this.data(), typeof option == 'object' && option);
    if (!data) {
      $this.data('fileUpload', (data = new FileUpload(element, options)));
    }
    if (typeof option == 'string') {
      if (method == true && args != null) {
        return data[option](args);
      } else {
        return data[option]();
      }
    }
    return $this;
  };

  $.fn.fileUpload.defaults = {
    allowUpload: true, // 允许上传
    allowDownload: true, // 允许下载
    allowDelete: true, // 允许删除
    dataType: 'json',
    formData: {
      moduleName: 'DY_TABLE_FORM',
      signUploadFile: false
    },
    autoUpload: true,
    maxFileSize: 5000000, // 50 MB
    previewMaxWidth: 100,
    previewMaxHeight: 100,
    previewCrop: true
  };
})(jQuery);
