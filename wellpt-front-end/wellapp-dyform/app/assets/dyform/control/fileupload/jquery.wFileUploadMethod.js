/**
 * 从表操作的一些方法 增删行、更新行操作等
 */
(function ($) {
  $.wFileUploadMethod = {
    addMustMark: function () {
      $.wControlInterface.addMustMark.apply(this, arguments);
    },
    initInputEvents: function () {
      var _this = this;
      if (this.options.columnProperty.events && this.options.columnProperty.events.inputEvents) {
        var inputEvents = this.options.columnProperty.events.inputEvents;
        for (var i in inputEvents) {
          if (inputEvents.hasOwnProperty(i)) {
            (function (i, inputEvents, _this) {
              _this.$element.bind(i, function (e) {
                appContext.eval(inputEvents[i], _this, {
                  $this: _this, //当前控件对象
                  columnProperty: _this.options.columnProperty, //控件属性
                  $form: DyformFacade.get$dyform(), //当前表单
                  event: e
                });
              });
            })(i, inputEvents, _this);
          }
        }
      }
    },
    /**
     * add by wujx 20160614
     */
    displayByShowType: function () {
      $.wControlInterface.displayByShowType.apply(this);
    },

    getValue: function (fnSuccess, fnError) {
      var files = WellFileUpload.files[this.getFielctlID()];
      if (files) {
        var values = [];
        $.each(files, function (i, file) {
          var value = {};
          $.extend(value, file);
          delete value.$fileItemElement; // 删除页面相关元素信息
          if (value.fileID) {
            values.push(value);
          }
        });
        fnSuccess.call(this, values);
      } else {
        fnSuccess.call(this, []);
      }
    },

    getFielctlID: function () {
      // var id = this.$element.attr('id');//字段名称
      // return
      // WellFileUpload.getCtlID4Dytable(this.options.mainTableName, id,
      // 0);
      return this.getCtlName();
    },

    getAllOptions: function () {
      return this.options;
    },

    getRule: function () {
      return $.ControlUtil.getCheckRules(this.options);
    },

    getMessage: function () {
      return $.ControlUtil.getCheckMsg(this.options);
    },

    isValueMap: function () {
      return false;
    },

    // add by wujx 2016061011
    loadFilesFormDb: function (dataUuid, columnName) {
      var files = WellFileUpload.loadFilesFormDb(dataUuid, columnName);
      return files;
    },

    setValue: function (value) {
      var _this = this;
      if (typeof value == 'undefined' || value == null) {
        var columnName = this.options.columnProperty.columnName;
        var files = WellFileUpload.loadFilesFormDb(this.getDataUuid(), columnName);
        this.fileuploadobj.addFiles(files, true);
      } else {
        // modify by wujx 20160615 begin
        this.fileuploadobj.addFiles(value, this.options.isAppend);
        // modify by wujx 20160615 begin
      }

      this.getValue(function (value) {
        _this.invoke('afterSetValue', value);
      });
    },
    setTextFile2SWF: function (enable) {
      WellFileUpload.file2swf = enable;
    },

    /**
     * add by wujx 20160615
     *
     * @param isAppend
     *            值是否追加
     */
    setIsAppend: function (isAppend) {
      if (isAppend === false || isAppend === true) {
        this.options.isAppend = isAppend;
      }
    },

    isRequired: function () {
      return $.wControlInterface.isRequired.call(this, this.options);
    },

    // 判定控件是否需要验证,true需要验证,false不需要验证
    isValidateEnable: function () {
      var enable = typeof this.options.enableValidate == 'undefined' ? true : this.options.enableValidate;
      return enable;
    },

    isValidationNeeded: function () {
      return this.isValidateOnHidden() || !this.isHide();
    },

    isValidateOnHidden: function () {
      return !!this.options.columnProperty.validateOnHidden;
    },

    isHide: function () {
      // 控件本身隐藏
      if (this.options.isHide) {
        return true;
      }

      // 控件父级隐藏
      var $layout = this.get$Layout();
      while ($layout) {
        if ($layout.isHide()) {
          return true;
        }
        $layout = $layout.get$Layout();
      }
      return false;
    },

    getNamespace: function () {
      return this.options.columnProperty.ns;
    },

    get$Layout: function () {
      if (this.options.$layout) {
        return this.options.$layout;
      }

      var ns = this.getNamespace() || this.getFormUuid();
      var fieldName = this.getFieldName();
      var layoutNode = window.reverseFieldTree[ns][fieldName].parent;
      while (layoutNode && !layoutNode.isLayout) {
        layoutNode = layoutNode.parent;
      }

      if (layoutNode) {
        this.options.$layout = $.ContainerManager.getLayout(layoutNode.symbol);
      } else {
        this.options.$layout = null;
      }
      return this.options.$layout;
    },

    isVisible: function () {
      return !this.options.isHide;
    },

    // 设置hide属性
    setVisible: function (isvisible) {
      $.ControlUtil.setVisible(this.$element, isvisible);
      this.options.isHide = !isvisible;
      this.statusChange(3);
    },

    /**
     * 是否显示为标签
     */
    isShowAsLabel: function () {
      return this.options.isShowAsLabel;
    },

    setAcceptType: function (type) {
      if (type) {
        this.fileuploadobj.setAccept(type);
      }
    },

    /**
     * 点击文件名触发按钮事件
     * arg1:有编辑权限的按钮操作参数{code:"",readonly:"",type:""}
     * arg2:无编辑权限的按钮操作参数{code:"",readonly:"",type:""}
     */
    clickFileNameEvent: function (arg1, arg2) {
      if (arguments.length > 0) {
        this.fileuploadobj.clickFileNameButtonEvent(arg1, arg2);
      } else {
        console.log('二开未传入参数');
      }
    },

    // 参数示例，对象：{field:"name",title:"名称",width:"100"}
    // 参数示例，数组：[{field:"name",title:"名称",width:"100"}]
    addHistoryColumns: function (columns) {
      if (columns) {
        this.fileuploadobj.addHistoryColumns(columns);
      }
    },

    getRule: function () {
      return $.wControlInterface.getRule.apply(this);
      // return
      // JSON.stringify($.ControlUtil.getCheckRuleAndMsg(this.options)["rule"]);
    },

    getMessage: function () {
      return $.wControlInterface.getMessage.apply(this);
      // return
      // JSON.stringify($.ControlUtil.getCheckRuleAndMsg(this.options)["msg"]);
    },

    getFieldName: function () {
      return $.wControlInterface.getFieldName.apply(this);
    },

    /**
     * 验证控件的值
     */
    validate: function () {
      var result = this.invoke('beforeValidate'); // 若返回false,表示不进行验证,
      // true表示要验证
      var _this = this;
      var ctlName = this.$element.attr('name');

      if (typeof result != 'undefined' && !result) {
        // 不进行验证
        return true;
      }

      if (!this.isValidateEnable()) {
        // 不需要验证
        return true;
      }

      // if(!this.isVisible())return true;//隐藏不参与验证 ,直接验证通过

      // if(this.isShowAsLabel() && this.getPos() !=
      // dyControlPos.subForm){//显示为文本直接验证通过
      // return true;
      // }

      if (!this.isValidateEnable()) {
        // 不需要验证
        return true;
      }

      if (this.isOcxAttach()) {
        // ocx附件
        if (this.isLocalFilesOpen && this.isLocalFilesOpen()) {
          // ocx附件还打开着,提示用户关闭
          return false;
        }
      }

      var validator = this.getValidator(this.$placeHolder);
      //validator.getValue = function(){//校验器对附件控件在检验时，只要确认是否已有做上传文件即可
      //	return WellFileUpload.files[_this.getFielctlID()];
      //}

      var rule = this.getRule();
      var message = this.getMessage();
      if (typeof rule == 'undefined') {
        return true;
      }

      var ruleObj = {};
      var messageObj = {};
      // /console.log(ctlName + ":" + rule);

      ruleObj = eval('(' + rule + ')');
      messageObj = eval('(' + message + ')');

      // var fieldName = this.$element.attr("name");
      // console.log(fieldName + "================");

      // this.setCustomValidateRule( ruleObj, messageObj,
      // this.getDataUuid(), fieldName, this.getFormDefinition().name);
      ruleObj.validFiles = true; // 校验附件控件,额外添加validFiles校验
      validator.settings.rules[ctlName] = ruleObj;
      validator.settings.messages[ctlName] = messageObj;

      var valid = true;

      valid = validator.control(this);

      this.invoke('afterValidate');

      return valid;
    },

    validateElem: function () {
      var value = WellFileUpload.files[this.getFielctlID()];
      var result = true; //文件数据是否正确
      if (value && $.isArray(value)) {
        $.each(value, function (index, file) {
          if (!file.fileID) {
            // 没有fileId的数据 -> 文件上传中或上传失败
            result = false;
          }
        });
      }
      if (value != null && value.length > 0 && result) {
        // 去掉验证信息
        Theme.validationRules.unhighlight(this.$element);
      } else {
        $.wControlInterface.validateElem.apply(this);
        // this.validate();
      }
    },
    enableSignature: function (enable) {
      if (enable) {
        this.fileuploadobj.signature = signature.enable;
      } else {
        this.fileuploadobj.signature = signature.disable;
      }
    },

    setDataUuid: function (datauuid) {
      this.options.columnProperty.dataUuid = datauuid;
    },

    getDataUuid: function () {
      return this.options.columnProperty.dataUuid;
    },
    getFormUuid: function () {
      return this.getFormDefinition().uuid;
    },
    setPos: function (pos) {
      this.options.columnProperty.pos = pos;
    },

    getPos: function (pos) {
      return this.options.columnProperty.pos;
    },

    getFormDefinition: function () {
      return this.options.columnProperty.formDefinition;
    },

    // 设置可编辑
    setEditable: function (isEnable) {
      var self = this;
      if (isEnable === false) {
        self.options.readOnly = true;
        self.fileuploadobj.readOnly = true;
        self.setAllowDelete(false);
        self.setAllowUpload(false);
      } else {
        self.options.readOnly = false;
        self.fileuploadobj.readOnly = false;
        self.setAllowDelete(self.options.allowDelete);
        self.setAllowUpload(self.options.allowUpload);
      }

      // this.fileuploadobj.allowDownload = true;
      // this.fileuploadobj.allowDelete = true;
      // this.fileuploadobj.allowUpload = true;

      // this.fileuploadobj.enableUploadFunction();
      // this.fileuploadobj.enableDownLoadFunction();
      // this.fileuploadobj.enableDeleteFunction();
      self.statusChange(2);
      // this.$element.siblings("div[id^='_fileupload']").children(".upload_div").show();
      // this.$element.siblings("div[id^='_fileupload']").find(".delete_Div").show();
    },

    // add by wujx 20161101
    isEditable: function () {
      return this.fileuploadobj.allowUpload;
    },

    setDisplayAsCtl: function () {
      this.setEditable();
    },

    // 设置编辑属性 add by wujx 20160620
    setEnable: function (isenable) {
      if (isenable) {
        this.setEditable();
      } else {
        this.setReadOnly();
      }
    },

    // 设置只读
    setReadOnly: function () {
      this.options.readOnly = true;
      this.fileuploadobj.readOnly = true;

      // this.fileuploadobj.allowDownload = true;
      // this.fileuploadobj.allowDelete = false;
      // this.fileuploadobj.allowUpload = false;

      this.fileuploadobj.disableUploadFunction();
      this.fileuploadobj.disableDeleteFunction();
      // this.fileuploadobj.enableDownLoadFunction();
      this.fileuploadobj.resetFilesSecDevFileBtn();
      this.statusChange(1);
      // this.$element.siblings("div[id^='_fileupload']").children(".upload_div").hide();
      // this.$element.siblings("div[id^='_fileupload']").find(".delete_Div").hide();
    },

    isReadOnly: function () {
      return this.options.readOnly === true;
    },

    // add by wujx 20161207
    setRequired: function (isrequire) {
      $.wControlInterface.setRequired.apply(this, [isrequire]);
    },

    /**
     * 获得控件名
     *
     * @returns
     */
    getCtlName: function () {
      var id = this.$element.attr('id'); // 字段名称
      // return
      // WellFileUpload.getCtlID4Dytable(this.options.mainTableName, id,
      // 0);
      return id;
    },

    // 获取字段的显示名称
    getDisplayName: function () {
      return $.wControlInterface.getDisplayName.apply(this);
      // return this.options.columnProperty.displayName;
    },

    getCtlElement: function () {
      return this.$element;
    },

    // bind函数，桥接
    /*
     * bind:function(eventname,event){ this.$element.bind(eventname,event);
     * return this; },
     */
    // bind函数，桥接
    bind: function (eventname, event, custom) {
      // if(this.$editableElem != null)
      // this.$editableElem.bind(eventname,event);
      /*   */

      if ('uploadOkCallback' == eventname) {
        this.fileuploadobj.uploadOkCallback = event;
      } else if ('deleteOkCallback' == eventname) {
        this.fileuploadobj.deleteOkCallback = event;
      } else if ('beforeFileUploadCallback' == eventname) {
        this.fileuploadobj.beforeFileUploadCallback = event;
      } else {
        if (custom) {
          this.options[eventname] = event;
        } else {
          $('.' + this.editableClass + "[name='" + this.getCtlName() + "']").live(eventname, event);
        }
      }

      return this;
    },

    /* 在设置之后角发该事件 */
    invoke: function (method) {
      /*
       * if(this.options[method]){
       * this.options[method].apply(this,$.makeArray(arguments).slice(1)); }
       */
      $.wControlInterface.invoke.apply(this, $.makeArray(arguments));
      // this.options[method].apply(this,$.makeArray(arguments).slice(1));
    },

    // 运行 运算公式
    culateByFormula: function () {
      $.ControlManager.culateByFormula(this);
    },

    get$InputElem: function () {
      // return $.wControlInterface.get$InputElem.apply(this);
      return this.getCurrentForm().find("input[type='file'][name='" + this.getCtlName() + "']");
    },

    /*
     * validate:function(){ return true; },
     */

    // unbind函数，桥接
    unbind: function (eventname) {
      this.$element.unbind(eventname);
      return this;
    },
    // 一些其他method ---------------------

    /**
     * 添加url超连接事件
     */
    addUrlClickEvent: function (urlClickEvent) {},

    // 显示为lablel
    setDisplayAsLabel: function () {
      this.setReadOnly();
    },

    getCurrentForm: function () {
      return this.options.$currentForm;
    },
    getDyformDataOptions: function () {
      return this.getCurrentForm().dyformDataOptions();
    },
    getValidator: function (element) {
      var $form = this.getCurrentForm();

      var validator = $form.data('wValidator');
      if (typeof validator == 'undefined' || validator == null) {
        validator = $form.wValidate();
        $form.data('wValidator', validator);
      } else {
      }

      return validator;
    },

    /**
     * 设置需要分组验证的标识
     */
    setValidateGroup: function (group) {
      var element = this.$element;
      element.attr(group.groupUsed, group.groupName);
    },

    getInputMode: function () {
      return this.options.commonProperty.inputMode;
    },

    isOcxAttach: function () {
      return formDefinitionMethod.isOcxAttach(this.getInputMode());
    },

    isAttachCtl: function () {
      if (
        this.getInputMode() == dyFormInputMode.accessory3 ||
        this.getInputMode() == dyFormInputMode.accessory1 ||
        this.getInputMode() == dyFormInputMode.accessoryImg
      ) {
        return true;
      } else {
        return false;
      }
    },
    clear: function (isHint) {
      // this.setValue([]);
      if (this.fileuploadobj.clear) {
        this.fileuploadobj.clear(isHint);
      }

      if (this.fileuploadobj.clean) {
        this.fileuploadobj.clean(isHint);
      }
    },
    clean: function (isHint) {
      this.clear(isHint);
    },

    open: function (fileId) {
      if (this.fileuploadobj.open) {
        this.fileuploadobj.open(fileId);
      }
    },
    /**
     * 设置是否可以下载
     */
    setAllowDownload: function (allowDownload) {
      if (allowDownload) {
        if (this.fileuploadobj.enableDownLoadFunction) {
          this.fileuploadobj.enableDownLoadFunction();
        }
      } else {
        if (this.fileuploadobj.disableDownLoadFunction) {
          this.fileuploadobj.disableDownLoadFunction();
        }
      }
      this.fileuploadobj.allowDownload = true == allowDownload;
    },

    /**
     * 设置是否可以删除
     */
    setAllowDelete: function (allowDelete) {
      if (allowDelete) {
        this.fileuploadobj.enableDeleteFunction();
      } else {
        this.fileuploadobj.disableDeleteFunction();
      }
      this.fileuploadobj.allowDelete = true == allowDelete;
    },

    /**
     * 设置是否可以上传
     */
    setAllowUpload: function (allowUpload) {
      if (allowUpload) {
        this.fileuploadobj.enableUploadFunction();
      } else {
        this.fileuploadobj.disableUploadFunction();
      }
      this.fileuploadobj.allowUpload = true == allowUpload;
    },

    /**
     * 设置是否允许文件名重复
     */
    setAllowAllowFileNameRepeat: function (flag) {
      this.fileuploadobj.initAllowFileNameRepeat(flag);
    },

    initReadOnly: function () {
      var showType = this.options.columnProperty.showType;
      if (showType) {
        if (showType == '5') {
          this.options.readOnly = true;
        } else {
          this.options.readOnly = false;
        }
      } else {
        this.options.readOnly = !this.options.allowUpload;
      }
      return this.options.readOnly;
    },
    /**
     * add by wujx 20160615 根据文件ID获取文件
     *
     * @param fileId
     *            文件ID
     * @returns
     */
    getFileById: function (fileId) {
      return this.fileuploadobj.getFile(fileId);
    },
    /**
     * add by wujx 20160615 根据文件，删除文件
     *
     * @param file
     *            文件
     * @returns
     */
    deleteFile: function (file) {
      this.fileuploadobj.deleteFileItem(file);
    },
    /**
     * add by wujx 20160615 根据文件ID，删除文件
     *
     * @param fileId
     *            文件ID
     * @returns
     */
    deleteFileByID: function (fileId) {
      var file = this.getFileById(fileId);
      this.fileuploadobj.deleteFileItem(file);
    },
    isGetValueByAsyn: function () {
      return true;
    },
    toggle: function () {
      $.wControlInterface.toggle.apply(this, arguments);
    },
    statusChange: function () {
      $.wControlInterface.statusChange.apply(this, arguments);
    },
    resize: function () {
      if (this.fileuploadobj.resize) {
        this.fileuploadobj.resize();
      }
    },
    bindEvent: function () {
      var self = this;
      var $attachContainer = self.$element;
      $attachContainer.on('file-events.logs', function (event, data) {
        self.addLog(data.operation, data.bValue, data.aValue);
      });
    },
    addLog: function (operation, bValue, aValue) {
      var self = this;
      var createTime;
      if (typeof JDS === 'undefined' || false == $.isFunction(JDS.getServerDate)) {
        createTime = new Date();
      } else {
        createTime = JDS.getServerDate();
      }
      var parentDataId = self.getCurrentForm().getDataUuid();
      var formUuid = self.getFormUuid() || '';
      var dataUuid = self.getDataUuid() || parentDataId;
      var entity = {
        dataDefType: 'DYFORM',
        dataDefId: formUuid,
        dataDefName: null,
        dataId: dataUuid,
        dataName: null,
        parentDataId: parentDataId,
        attrId: self.getFieldName(),
        attrName: self.getDisplayName(),
        attrType: 'file',
        createTime: createTime.format('yyyy-MM-dd HH:mm:ss'),
        operation: operation,
        beforeValue: JSON.stringify(bValue),
        afterValue: JSON.stringify(aValue)
      };
      var dyformDataOptions = self.getDyformDataOptions();
      var logs = dyformDataOptions.getOptions('logs') || [];
      // 还没保存，删除相关日志
      //    	if((operation === "file-delete" || operation === "file-delete-batch") && logs.length > 0 && entity.beforeValue){
      //    		function getFileID(a,b){
      //    			if(a && a.origUuid){
      //    				return a.origUuid;
      //    			}else if(b && b.origUuid){
      //    				return b.origUuid;
      //    			}
      //    			return (a || b).fileID;
      //    		}
      //    		var fileID1 = getFileID(JSON.parse(entity.afterValue), JSON.parse(entity.beforeValue));
      //    		var addarr = ["file-upload", "file-new", "file-imp", "file-add"];
      //    		for(var i=0;i<logs.length;i++){
      //    			var log = logs[i];
      //    			var fileID2 = getFileID(JSON.parse(log.afterValue), JSON.parse(log.beforeValue));
      //    			if(fileID1 === fileID2 && $.inArray(log.operation, addarr) > -1) {
      //    				logs.splice(i, 1);
      //    				for(var j=logs.length-1;j>-1;j--){
      //    					var fileID2 = getFileID(JSON.parse(logs[j].afterValue), JSON.parse(logs[j].beforeValue));
      //    					if(fileID1 === fileID2){
      //    						logs.splice(j, 1);
      //    					}
      //    				}
      //    				// 未保存的附件，不关联删除
      //    				dyformDataOptions.putOptions("logs", logs);
      //    				return;
      //    			}
      //    		}
      //    	}
      logs.push(entity);
      dyformDataOptions.putOptions('logs', logs);
    },
    getLogs: function () {
      return this.logs;
    }
  };

  $.extend($.wFileUploadMethod, $.wAccessory);
})(jQuery);
