define(['mui', 'constant', 'commons', 'server', 'mui-DyformField', 'mui-DyformConstant', 'appContext', 'mui-fileupload'], function (
  $,
  constant,
  commons,
  server,
  DyformField,
  DyformConstant,
  appContext,
  muiFileupload
) {
  var StringUtils = commons.StringUtils;
  var StringBuilder = commons.StringBuilder;
  var ftl =
    '<div class="mui-input-row mui-attach-row">\
	<input type="file" id="{1}" name="{1}" class="dyform-field-editable">\
		<ul class="mui-table-view attach-container">\
		<li class="mui-table-view-cell mui-collapse">\
		<a class="mui-navigate-right" href="#">\
			<label class="attach-label">\
			<label class="mui-ellipsis" style="width: auto">\
			<i class="iconfont icon-ptkj-fujian text-primary" style="font-size: 14px;"></i>\
			{0}<font class="required-marker" color="red">*</font></label>\
			</label>\
			<label style="float: right;width: auto;margin-right: 40px;padding: 11px 0;text-align: right"><span class="attach-total dyform-field-label">0</span>&nbsp;&nbsp;个附件</label>\
		</a>\
			<div class="mui-collapse-content">\
				<div class="mui-button-row mui-clearfix attach-button-row">\
					<input type="file" name="{1}-clone" class="mui-btn mui-btn-blue attach-picker"/>\
					<buttom type="button" class="mui-btn mui-btn-blue btn-fileshowtype">图标/</buttom>\
					<!--<buttom type="button" class="mui-btn mui-btn-blue btn-filedownall">下载</buttom>-->\
					<buttom type="button" class="mui-btn mui-btn-blue btn-filedelall">清空</buttom>\
				</div>\
				<ul class="mui-table-view attach-table-view">\
				</ul>\
			</div></li>\
	</ul><span class="dyform-field-error mui-hidden"></span></div>';
  // 附件上传
  var wFileUpload = function ($placeHolder, options) {
    DyformField.apply(this, arguments);
  };
  commons.inherit(wFileUpload, DyformField, {
    // 初始化，构造函数执行后自动调用
    init: function () {
      var self = this;
      self._superApply(arguments);
      if (!self.value && self.options['loadFiles']) {
        self.value = muiFileupload.loadFilesFormDb(self.getDataUuid(), self.getName());
      }
      self.orininalValue = $.extend([], self.value);
    },
    // 渲染
    render: function () {
      var _self = this,
        $upload;
      var wrapper = document.createElement('div');
      wrapper.id = 'div_' + _self.getId();
      var sb = new StringBuilder();
      var displayName = _self.definition.tagName ? _self.definition.tagName : _self.definition.displayName;
      sb.appendFormat(ftl, displayName, _self.definition.name);
      wrapper.innerHTML = sb.toString();

      if (_self.$element == null) {
        _self.$element = $(wrapper);
      } else {
        _self.$element[0].innerHTML = wrapper.innerHTML;
      }

      _self.$placeHolder[0].appendChild(wrapper);
      _self.$editableElem = $(wrapper.querySelector('.dyform-field-editable'));
      _self.$labelElem = $(wrapper.querySelector('.dyform-field-label'));
      _self.$errorElem = $(wrapper.querySelector('.dyform-field-error'));
      _self.$requiredMarkerElem = $(wrapper.querySelector('.required-marker'));
      var options = _self.options.fieldDefinition;
      options.multiple = options.mutiselect;
      var dyFormInputMode = DyformConstant.dyFormInputMode;
      if (options.inputMode == dyFormInputMode.accessoryImg) {
        options.multiple = false; // 保持PC端一致
        options.filedelall = false;
        options.filedownall = false;
        options.type = 'image';
      } else if (options.inputMode == dyFormInputMode.accessory1) {
        options.type = 'icon';
      }
      options.files = _self.value;
      options.autoUpload = true;
      options.itemFormat = function (file) {
        var self = this;
        var item =
          '<li class="mui-table-view-cell">\
											<div class="attach-handler"><h4 class="mui-ellipsis">' +
          (file && file.fileName) +
          '</h4><h5 >' +
          file.createTime +
          '<span class="filesize">' +
          self.formatSize(file.fileSize) +
          '</span></h5></div><a class="mui-btn btn-filedel mui-icon mui-icon-info"></a></li>';
        return item;
      };

      var todoAction = function (file, cmd) {
        $.alert('[' + cmd + ']还未实现');
        return true;
      };
      /*
			options.action = {
				"存到云盘" : todoAction,
				"通过邮件发送" : todoAction,
				"发送给联系人" : todoAction
			};
			*/
      $upload = _self.$upload = new muiFileupload(_self.$editableElem[0], options);
      $upload.on('afterSetValue', function (files, append, itemFormat) {
        // debugger
        _self.afterSetValue(files, append, itemFormat);
      });

      // 绑定事件
      _self._bindEvents(arguments);
    },
    // 设置字段显示为文本
    setDisplayAsLabel: function () {
      var self = this;
      self.$upload.setAllowUpload(false);
      self.$upload.setAllowDelete(false);
      self.$upload.setAllowDownload(true);
    },
    // 设置字段值
    getValue: function () {
      return this.$upload && this.$upload.files;
    },
    // 设置字段值
    setValue: function (value) {
      return this.$upload.setItems(value, false);
    },
    isRequired: function () {
      var self = this;
      self.$upload['required'];
      return self._superApply(arguments);
    },
    setRequired: function (required) {
      var self = this;
      self._superApply(arguments);
      return self.$upload.setRequired(required);
    },
    isEditable: function () {
      return this.$upload['editable'];
    },
    setEditable: function (editable) {
      var self = this;
      return self.$upload.setEditable(editable);
    },
    isReadonly: function () {
      return this.$upload['readonly'];
    },
    setReadonly: function (readonly) {
      return this.$upload.setReadonly(readonly);
    },
    isSignature: function () {
      return this.$upload['signature'];
    },
    setSignature: function (signature) {
      return this.$upload.setSignture(signature);
    },
    isHidden: function () {
      return this.$upload['hidden'];
    },
    setHidden: function (hidden) {
      return this._superApply(arguments);
      // return this.$upload.setHidden();
    }
  });
  return wFileUpload;
});
