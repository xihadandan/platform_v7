/**
 * 控件公共接口方法
 */
(function ($) {
  var arrEntities1 = {
    '<': '&lt;',
    '>': '&gt;',
    '&': '&amp;',
    '"': '&quot;',
    "'": '&apos;'
  };
  var optionSetCache = {};
  var optionSetCacheRequest = {};
  $.wControlInterface = {
    labelClass: 'labelclass',
    editableClass: 'editableClass',

    init: function () {
      // 初始化私有参数
      this.inited = true;
      this.initSelf();
    },
    initSelf: function () {
      /*
       * if(this.isInSubform()){ return; }
       */
      var options = this.options;
      this.initControlEvents();

      this.invoke('beforeInit', options);

      if (!this.isNaked()) {
        // 非裸体控件
        this.displayByShowType();

        this.addMustMark();
      } else {
        var showType = this.options.columnProperty.showType;
        if (showType == dyshowType.showAsLabel) {
          this.options.isShowAsLabel = true;
        }
      }

      // if (options.columnProperty.data) {// 在初始化时也有数据
      if (typeof options.columnProperty.data != 'undefined') {
        this.setDefaultValue(this.options.columnProperty.data);
      } else {
        // 没数据则用默认值
        var defaultValue = options.columnProperty.defaultValue;
        if (
          defaultValue &&
          defaultValue.indexOf(':') < 0 &&
          defaultValue.charAt(0) === '{' &&
          defaultValue.charAt(defaultValue.length - 1) === '}'
        ) {
          defaultValue = '';
        }
        this.setDefaultValue(defaultValue);
      }

      this.options.initComplete = true;

      this.invoke('afterInit', true);
    },
    getSeparator: function () {
      var self = this;
      return self.options.valSeparator || ';';
    },
    initControlEvents: function () {
      var _this = this;
      if (_this.options.columnProperty.events && _this.options.columnProperty.events.controlEvents) {
        var controlEvents = this.options.columnProperty.events.controlEvents;
        for (var i in controlEvents) {
          if (controlEvents.hasOwnProperty(i)) {
            (function (i, controlEvents, _this) {
              _this.bind(
                i,
                function (e) {
                  appContext.eval(controlEvents[i], _this, {
                    $this: _this, //当前控件对象
                    columnProperty: _this.options.columnProperty, //控件属性
                    $form: DyformFacade.get$dyform(), //当前表单
                    event: e
                  });
                },
                true
              );
            })(i, controlEvents, _this);
          }
        }
      }
    },

    // 是否为裸体控件(没有对应的labelelement和editableelement)
    isNaked: function () {
      var naked = this.getOptions().naked;
      return naked == undefined ? false : naked;
    },

    initControlOver: function ($dyform, fieldname) {},

    /**
     * 清空控件
     */
    clear: function () {
      this.clean();
    },

    clean: function () {
      this.setValue('', true);
      this.removeValidate();
    },

    getOptions: function () {
      return this.options;
    },

    lazySetDictOptions: function (cb, async) {
      var self = this;
      var fieldName = self.options.columnProperty.columnName;
      var formUuid = self.getFormUuid();
      // 是否需要等待备选项加载
      if (self.waitOptionSetIfRequired(cb)) {
        return;
      }
      $.ajax({
        url: ctx + '/pt/dyform/definition/getFieldDictionaryOptionSet',
        dataType: 'json',
        type: 'post',
        data: {
          fieldName: fieldName,
          formUuid: formUuid,
          paramsObj: JSON.stringify({ dictCode: '' })
        },
        async: async,
        success: function (optionSet) {
          var cacheKey = self.getOptionSetCacheKey();
          if (cacheKey != null) {
            optionSetCache[cacheKey] = self.copyOptionSet(optionSet);
          }
          cb(optionSet);
          // 回调等待加载的备选项回调函数
          self.callbackWaitOptionSet(cacheKey);
        }
      });
    },
    waitOptionSetIfRequired: function (cb) {
      var self = this;
      var cacheKey = self.getOptionSetCacheKey();
      if (cacheKey == null) {
        return false;
      }
      if (optionSetCache[cacheKey]) {
        cb(self.copyOptionSet(optionSetCache[cacheKey]));
        return true;
      } else if (optionSetCacheRequest[cacheKey]) {
        var reqCb = function (optionSet) {
          cb(optionSet);
        };
        optionSetCacheRequest[cacheKey].push(reqCb);
        return true;
      } else {
        optionSetCacheRequest[cacheKey] = [];
        return false;
      }
    },
    callbackWaitOptionSet: function (cacheKey) {
      var self = this;
      if (cacheKey == null) {
        return;
      }
      var reqCbArray = optionSetCacheRequest[cacheKey] || [];
      while (reqCbArray.length > 0) {
        reqCbArray.shift()(self.copyOptionSet(optionSetCache[cacheKey]));
      }
      delete optionSetCacheRequest[cacheKey];
      // 清空缓存
      var tryClearCache = function () {
        setTimeout(function () {
          var reqCbArray = optionSetCacheRequest[cacheKey] || [];
          if (reqCbArray.length == 0) {
            delete optionSetCache[cacheKey];
          } else {
            tryClearCache();
          }
        }, 10 * 1000);
      };
      // bug#59287
      // tryClearCache();
    },
    getOptionSetCacheKey: function () {
      var self = this;
      var options = self.options;
      var self = this;
      var options = self.options;
      if (options.dataSourceFieldName == null || options.dataSourceFieldName == '') {
        return null;
      }
      var dataSourceRequest = {
        dataSourceId: options.dataSourceId,
        dataSourceFieldNam: options.dataSourceFieldName,
        dataSourceDisplayName: options.dataSourceDisplayName,
        dataSourceGroup: options.dataSourceGroup,
        defaultCondition: options.defaultCondition,
        dictCode: options.dictCode
      };
      var cacheKey = JSON.stringify(dataSourceRequest);
      return cacheKey;
    },
    copyOptionSet: function (optionSet) {
      if ($.isArray(optionSet)) {
        var copyArray = [];
        $.each(optionSet, function (i, item) {
          copyArray.push($.extend(true, {}, item));
        });
        return copyArray;
      } else {
        return $.extend(true, {}, optionSet);
      }
    },

    /**
     * 获取动态常量备选项
     */
    getConstantOptionSet: function (dataOptsLists, value, optionSet) {
      var self = this;
      var newOptionSet = optionSet;
      if ($.isArray(value)) {
        $.each(value, function (index, item) {
          newOptionSet = self.getConstantOptionSet(dataOptsLists, item, newOptionSet);
        });
      } else {
        for (var i = 0; i < dataOptsLists.length; i++) {
          var dataOptsList = dataOptsLists[i];
          var currOptionSet = '';
          if (dataOptsList.operate == 'eq' && value == dataOptsList.content) {
            currOptionSet = dataOptsList.value;
          } else if (dataOptsList.operate == 'ne' && value != dataOptsList.content) {
            currOptionSet = dataOptsList.value;
          } else if (dataOptsList.operate == 'lt' && Number(value) < Number(dataOptsList.content)) {
            currOptionSet = dataOptsList.value;
          } else if (dataOptsList.operate == 'gt' && Number(value) > Number(dataOptsList.content)) {
            currOptionSet = dataOptsList.value;
          } else if (dataOptsList.operate == 'le' && Number(value) <= Number(dataOptsList.content)) {
            currOptionSet = dataOptsList.value;
          } else if (dataOptsList.operate == 'ge' && Number(value) >= Number(dataOptsList.content)) {
            currOptionSet = dataOptsList.value;
          } else if (dataOptsList.operate == 'like' && typeof value === 'string' && value.indexOf(dataOptsList.content) > -1) {
            currOptionSet = dataOptsList.value;
          } else if (dataOptsList.operate == 'nlike' && typeof value === 'string' && value.indexOf(dataOptsList.content) == -1) {
            currOptionSet = dataOptsList.value;
          }
          if (currOptionSet != '') {
            newOptionSet = newOptionSet.concat(self.obj2Array(currOptionSet));
          }
        }
      }

      return newOptionSet;
    },
    obj2Array: function (currOptionSet) {
      var arr = [];
      var optionSet = eval('(' + currOptionSet + ')');
      if (!$.isArray(optionSet)) {
        for (var i in optionSet) {
          arr.push({
            value: i,
            name: optionSet[i]
          });
        }
      } else {
        arr = optionSet;
      }
      return arr;
    },
    /**
     * 数组对象去重(备选项使用)
     */
    arrayRepeat: function (list) {
      var newList = [];
      if (list.length > 0) {
        var valueList = [];
        for (var i = 0; i < list.length; i++) {
          var arr = list[i];
          if (valueList.indexOf(arr.value) > -1) {
            continue;
          }
          valueList.push(arr.value);
          newList.push(arr);
        }
      }

      return newList;
    },

    /**
     * 获取动态数据字典备选项
     */
    getDictOptionSet: function (dictType, dictCode, optionSet) {
      var newOptionSet = optionSet;
      JDS.call({
        service: 'dataDictionaryService.getDataDictionariesByTypeCode',
        data: [dictType, dictCode],
        version: '',
        async: false,
        success: function (result) {
          var data = result.data;
          if (data.length > 0) {
            newOptionSet = newOptionSet.concat(data);
          }
        }
      });
      return newOptionSet;
    },
    getNewOptionSet: function (dictType, optionSet, value) {
      var dictCode = '';
      if (dictType != '') {
        dictCode = value;
      } else {
        dictType = value;
      }
      JDS.call({
        service: 'dataDictionaryService.getDataDictionariesByTypeCode',
        data: [dictType, dictCode],
        version: '',
        async: false,
        success: function (result) {
          var data = result.data;
          if (data.length > 0) {
            optionSet = data;
          }
        }
      });
      return optionSet;
    },

    /**
     * 设置文本控件的css样式.
     *
     * @param elment
     * @param options
     */
    setTextInputCss: function () {
      // 宽、高
      this.$element.css(this.getTextInputCss());
    },

    getTextInputCss: function () {
      var width = this.options.commonProperty.ctlWidth;
      if (width && width.indexOf('%') < 0) {
        width = width + 'px';
      }
      return {
        width: width,
        height: this.options.commonProperty.ctlHight ? this.options.commonProperty.ctlHight + 'px' : '',
        'text-align': this.options.commonProperty.textAlign,
        'font-size': this.options.commonProperty.fontSize + 'px',
        color: this.options.commonProperty.fontColor
      };
    },
    /**
     *
     * @param isRender
     *            是否对控件再进行界面渲染,false表示不渲染,true或者没定义表示要渲染
     */
    setValueByMap: function (valuemap, isRender) {
      var that = this,
        realValue = [];
      if ($.trim(valuemap).length > 0) {
        if (typeof valuemap === 'string') {
          valuemap = eval('(' + valuemap + ')');
        }
        for (var key in valuemap) {
          realValue.push(key);
        }
      }
      var valSeparator = that.getSeparator();
      that.setValue(realValue.join(valSeparator), isRender);
    },

    isInitCompleted: function () {
      return this.options.initComplete;
    },

    /**
     * 设置默认值
     *
     * @param defaultValue
     */
    setDefaultValue: function (defaultValue) {
      // 加空判断
      if (defaultValue == null || defaultValue == '') {
        return;
      }
      var self = this;
      self.setValue(defaultValue);
    },

    /**
     * 根据showtype类型展示
     */
    displayByShowType: function () {
      var showType = this.options.columnProperty.showType;
      var readStyle = this.options.columnProperty.readStyle;
      if (showType == dyshowType.showAsLabel) {
        this.setDisplayAsLabel();
      } else if (showType == dyshowType.readonly) {
        // this.setDisplayAsCtl();
        this.setReadOnly(true);
      } else if (showType == dyshowType.disabled) {
        this.setEnable(false);
      } else if (showType == dyshowType.hide) {
        // this.setDisplayAsCtl();
        this.setVisible(false);
      } else {
        // this.setDisplayAsCtl();

        this.setEnable(true);
      }
    },

    // 设值
    setValue: function (value) {
      var self = this;
      if ($.isArray(value)) {
        var valSeparator = self.getSeparator();
        value = value.join(valSeparator);
      } else if (self.isValueMap()) {
        if ($.isPlainObject(value) || (typeof value === 'string' && value.indexOf('{') === 0)) {
          self.setValueByMap(value);
        }
      }
      self.value = value;
      if (self.isValueMap()) {
        self.setValue2LabelElem && self.setValue2LabelElem();
        self.setValue2EditableElem && self.setValue2EditableElem();
      } else {
        self.get$InputElem().val(value);
      }
      if (self.culateByFormula) {
        self.culateByFormula(); // 根据运算公式计算
      }
      if (self.setValueLock) {
        return self.setValueLock;
      }
      self.setValueLock = true;
      try {
        if (self.isValueMap()) {
          self.setToRealDisplayColumn();
        }
        self.invoke('afterSetValue', self.value, self.lazySetValue);
        delete self.lazySetValue;
      } finally {
        self.setValueLock = false;
      }
    },

    // 设置必输
    setRequired: function (isrequire) {
      $.ControlUtil.setRequired(isrequire, this.options);
      if (isrequire) {
        this.addMustMark();
      } else {
        this.addMustMark(false);
      }
    },

    isRequired: function () {
      return $.ControlUtil.isRequired(this.options);
    },

    // 设置可编辑
    setEditable: function (isEnable) {
      var self = this;
      self.options.isShowAsLabel = isEnable === false;
      if (isEnable === false && self.options.columnProperty.readStyle == dyshowType.readonly) {
        //不可编辑情况下以只读形态进行展示情况下
        self.options.isShowAsLabel = false;
        self.options.columnProperty.showType = dyshowType.readonly;
      }
      self.render();
      if (isEnable === false) {
        self.setEnable(false);
      } else {
        self.setEnable(true);
      }
    },

    /**
     * 返回是否可编辑(由readOnly和disabled判断)
     *
     * @returns {Boolean}
     */
    isEditable: function () {
      if (this.options.columnProperty.showType == dyshowType.edit) {
        return true;
      } else {
        return false;
      }
    },
    /**
     * 隐藏行
     * @param isvisible
     */
    setRowVisible: function (isvisible) {
      this.options.isRowHide = !isvisible;
      if (isvisible) {
        // 设置为显示出来
        this.$placeHolder.closest('tr').show();
      } else {
        this.$placeHolder.closest('tr').hide();
      }
    },
    /**
     * 隐藏列
     * @param isvisible
     */
    setCellVisible: function (isvisible) {
      this.options.isRowHide = !isvisible;
      var filedIndex = this.$placeHolder.parent().index();
      var labelIndex = this.$placeHolder.parent().prev().index();
      var tableObj = this.$placeHolder.parents('table');
      if (isvisible) {
        // 设置为显示出来
        $(tableObj[0])
          .find('tr')
          .find('td:eq(' + filedIndex + ')')
          .show();
        $(tableObj[0])
          .find('tr')
          .find('td:eq(' + labelIndex + ')')
          .show();
      } else {
        $(tableObj[0])
          .find('tr')
          .find('td:eq(' + filedIndex + ')')
          .hide();
        $(tableObj[0])
          .find('tr')
          .find('td:eq(' + labelIndex + ')')
          .hide();
      }
    },

    // 设置hide属性
    setVisible: function (isvisible) {
      this.options.isHide = !isvisible;
      if (isvisible) {
        // 设置为显示出来
        this.render();
        // add by zhongwd 2081012 begin
        //this.showLabelElem();
        //this.showEditableElem();

        this.$placeHolder.parent().prev().show();
        if (this.isInSubform && false == this.isInSubform()) {
          this.$placeHolder.parent().show();
          this.$placeHolder.parents('tr').find('td[colspan]').removeAttr('colspan');
          var colspan = 0;
          var hideenFields = this.$placeHolder.parents('tr').find('>td.label-td:visible').size();
          if (hideenFields >= 1) {
            colspan = 1 + hideenFields * 2;
          }
          if (this.$placeHolder.parents('tr').siblings().size() > 1) {
            this.$placeHolder.parents('tr').find('>td:visible').last().attr('colspan', colspan);
          }
        }

        if (this.isShowAsLabel()) {
          //this.hideEditableElem();
          this.statusChange(1);
        } else {
          this.statusChange(2);
        }

        // add by zhongwd 2081012 end
      } else {
        // 设置为隐藏起来
        this.hideLabelElem();
        this.hideEditableElem();
        this.$placeHolder.parent().prev().hide();

        // add by zhongwd 2081012 begin
        if (this.isInSubform && false == this.isInSubform()) {
          this.$placeHolder.parent().hide();
          var colspan = 3;
          var hideenFields = this.$placeHolder.parents('tr').find('>td.label-td:hidden').size();
          if (hideenFields >= 1) {
            colspan = 1 + hideenFields * 2;
          }
          if (this.$placeHolder.parents('tr').siblings().size() > 1) {
            this.$placeHolder.parents('tr').find('>td:visible').last().attr('colspan', colspan);
          }
        }
        // add by zhongwd 2081012 end
        // add by wujx 20161101 begin
        this.removeValidate();
        // add by wujx 20161101 end
        this.statusChange(3);
      }
    },

    // 显示为lablel,不适用于放在从表中的控件
    setDisplayAsLabel: function () {
      var self = this;
      var options = self.options;
      var readStyle = options.columnProperty.readStyle;
      if (readStyle === dyshowType.readonly) {
        self.setEnable(false);
        return self.setReadOnly(true);
      }
      if (this.get$LabelElem().size() == 0) {
        // 创建标签元素
        /*
         * var labelElem = document.createElement("span");
         * labelElem.setAttribute("class", this.labelClass);
         * labelElem.setAttribute("name", this.getCtlName()); $(
         * labelElem).css(this.getTextInputCss());
         * this.$placeHolder.after($( labelElem)); this.$labelElem =
         * this.$placeHolder.next("." + this.labelClass);
         */
        // 添加url点击事件
        this.markPlaceHolderAsLabelElem();
      }

      this.get$LabelElem().show();

      options.isShowAsLabel = true;
      this.hideEditableElem && this.hideEditableElem();
      this.setValue2LabelElem && this.setValue2LabelElem();
      if (this.setCtlField) {
        this.setCtlField();
      }
      // add by wujx 20161014 begin
      if (!this.isInSubform()) {
        this.addUrlClickEvent(urlClickEvent);
      }
      // add by wujx 20161014 end
      // add by wujx 20161101 begin
      this.removeValidate();
      // add by wujx 20161101 end
      this.statusChange(1);
    },

    markPlaceHolderAsLabelElem: function () {
      this.$labelElem = this.$placeHolder;
      this.$labelElem.addClass(this.labelClass);
      //控件为富文本框是为labelElem增加默认文本左对齐防止被父级元素样式影响(富文本框设置左对齐时未添加样式默认是左对齐)
      if (this.options.commonProperty.inputMode === '2') {
        this.$labelElem.css('text-align', 'left');
      }
    },

    // 显示为可编辑框
    setDisplayAsCtl: function () {
      var options = this.options;
      options.isShowAsLabel = false;
      this.showEditableElem();
      this.hideLabelElem();
      if (this.setValue2EditableElem) {
        this.setValue2EditableElem();
      }
      this.invoke('show');
    },

    showEditableElem: function () {
      var self = this;
      if (self.$editableElem == null) {
        self.createEditableElem();
        self.initInputEvents();
      }
      if (self.$editableElem.css('display') === 'none') {
        self.$editableElem.css({
          resize: 'vertical'
        });
        self.$editableElem.show();
      }
    },

    initInputEvents: function () {
      var _this = this;
      if (this.options.columnProperty.events && this.options.columnProperty.events.inputEvents) {
        var inputEvents = this.options.columnProperty.events.inputEvents;
        for (var i in inputEvents) {
          if (inputEvents.hasOwnProperty(i)) {
            (function (i, inputEvents, _this) {
              _this.bind(i, function (e) {
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

    hideEditableElem: function () {
      if (this.$editableElem == null) {
        if (this.isNaked()) {
          this.markPlaceHolderAsLabelElem();
        } else {
          return;
        }
      }
      if (this.$editableElem) {
        this.$editableElem.hide();
        this.$editableElem.siblings('.well-select').hide();
        this.$editableElem.siblings('.well-textarea-max-tip').hide();
        this.$editableElem.siblings('.Validform_checktip').hide();
      }
    },
    hideLabelElem: function () {
      if (this.get$LabelElem().size() == 0) {
        return;
      }
      this.$labelElem.hide();
    },
    showLabelElem: function () {
      if (this.get$LabelElem().size() == 0) {
        return;
      }
      this.$labelElem.show();
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

    getFormDefinition: function () {
      return this.options.columnProperty.formDefinition;
    },

    /**
     * add by wujx 20160923 获取从表配置信息
     */
    getSubformConfig: function () {
      return this.options.columnProperty.subformConfig;
    },

    setPos: function (pos) {
      this.options.columnProperty.pos = pos;
    },

    getPos: function (pos) {
      return this.options.columnProperty.pos;
    },

    // get..........................................................//

    // 返回控件真实值
    getValue: function () {
      var self = this;
      var value = self.value;
      if (value == null || $.trim(value).length == 0) {
        return '';
      } else if ($.isArray(value) && self.isValueMap()) {
        var valSeparator = self.getSeparator();
        value = value.join(valSeparator);
      }
      return value;
    },

    getValueMap: function () {
      var self = this;
      var value = self.value;
      $.dyform.warn('getValueMap is Deprecated');
      if ($.trim(value).length == 0) {
        return JSON.cStringify({});
      }
      var valueObject = {};
      var valSeparator = self.getSeparator();
      var values = value.split(valSeparator);
      var displayValues = getDisplayLabel(values);
      for (var i = 0; i < values.length; i++) {
        valueObject[values[i]] = displayValues[i];
      }
      return value;
    },
    getDisplayValue: function () {
      var self = this;
      var value = self.value;
      if ($.trim(value).length == 0 || self.isValueMap() === false) {
        return '';
      }
      var displayValue = self.getDisplayLabel(value);
      if ($.isArray(displayValue)) {
        return displayValue.join(';');
      } else {
        return displayValue;
      }
    },
    getDisplayLabel: function (realValue) {
      var self = this,
        separator;
      if (typeof realValue === 'string' && realValue.indexOf((separator = self.getSeparator())) > 0) {
        realValue = realValue.split(separator);
      } else if (false === $.isArray(realValue)) {
        realValue = [realValue];
      }
      var displayValue = null;
      var showDisplay = false; //用于options无对应数据但有配置显示值字段时直接取显示值字段的值
      // getOptionSet树形控件和组织选择没有getOptionSet,需要重写getDisplayLabel
      if ($.isFunction(self.getOptionSet)) {
        displayValue = [];
        var optionSet = self.getOptionSet(self.options) || {};
        for (var i = 0; i < realValue.length; i++) {
          var value,
            key = realValue[i];
          if ($.isArray(optionSet)) {
            for (var j = 0; j < optionSet.length; j++) {
              if (key == optionSet[j].value || key == optionSet[j].id) {
                value = optionSet[j].name || optionSet[j].text;
                break;
              }
            }
          } else {
            value = optionSet[key];
          }
          // other_input
          if (value === undefined) {
            showDisplay = true;
          }
          // 延时加载时的备选项显示值设置为空
          if (self.options && self.options.lazyLoading && ($.isEmptyObject(optionSet) || ($.isArray(optionSet) && optionSet.length == 0))) {
            displayValue.push('');
          } else {
            displayValue.push(value === undefined ? key : value);
          }
        }
      } else {
        displayValue = realValue;
      }

      var _display = self.options.columnProperty.realDisplay.display;
      if (_display && (self.options.optionSet.length === 0 || showDisplay)) {
        if (self.__displayValue != undefined) {
          displayValue = self.__displayValue;
        } else {
          var formData = self.dyform$Context().data().options.formData;
          var formDatas = formData.formDatas[formData.formUuid];
          if (formDatas && formDatas[0] && formDatas[0][_display]) {
            displayValue = formDatas[0][_display];
          }
        }
      }
      return typeof displayValue === 'string' ? displayValue : displayValue.join(self.options.separator);
    },
    isValueMap: function () {
      return false;
    },

    isValueAsJsonCtl: function () {
      return this.isValueMap();
    },

    isEnable: function () {
      // return !this.options.disabled;
      if (this.options.columnProperty.showType == dyshowType.disabled) {
        return false;
      }
      return true;
    },

    isVisible: function () {
      if (this.isInSubform()) {
        return !this.options.isHide;
      }
      if (!this.options.isHide) {
        //控件本身不隐藏， 父级元素被隐藏，则这时也返回隐藏
        if (this.get$InputElem().size() > 0) {
          if (this.get$InputElem().siblings('.well-select').size() > 0) {
            var realControl = this.get$InputElem().siblings('.well-select');
            if (realControl.css('display') != 'none') {
              return (outerVisible = this.checkOuterWrapIsVisible());
            }
          } else if (this.get$InputElem().siblings('.org-select-container').size() > 0) {
            var realControl = this.get$InputElem().siblings('.org-select-container.editableClass');
            if (realControl.css('display') != 'none') {
              return (outerVisible = this.checkOuterWrapIsVisible());
            }
          } else if (this.get$InputElem().siblings('.cke').size() > 0) {
            var realControl = this.get$InputElem().siblings('.cke');
            if (realControl.css('display') != 'none') {
              return (outerVisible = this.checkOuterWrapIsVisible());
            }
          } else if (this.options.commonProperty.inputMode == '18' || this.options.commonProperty.inputMode == '17') {
            this.get$InputElem().each(function () {
              if ($(this).is(':visible')) {
                return true;
              }
            });
          } else if (!this.get$InputElem().is(':visible')) {
            // tab隐藏、tab下控件不隐藏返回可见
            if (this.get$InputElem().css('display') != 'none') {
              // 区块隐藏，返回隐藏
              var $table = this.get$InputElem().closest('table');
              if ($table.find("td[blockhide='true']").length > 0) {
                return false;
              }
              // tab隐藏控件不隐藏返回可见
              var $tabPanel = this.get$InputElem().closest('.tab-pane');
              if ($tabPanel.length > 0 && $tabPanel.css('display') == 'none' && $tabPanel.parent().is(':visible')) {
                // 是否验证隐藏tab下的字段
                var dyformOptions = this.dyform$Context().data().options;
                if (dyformOptions.validateInHiddenTab === false) {
                  return false;
                }
                return true;
              }
            }
            return false;
          }
        }

        if (this.get$LabelElem().size() > 0) {
          if (!this.get$LabelElem().is(':visible')) {
            return false;
          }
        }
        return true;
      } else {
        //控件本身隐藏
        return false;
      }
    },

    //检查外部容器是否隐藏
    checkOuterWrapIsVisible: function () {
      // 区块隐藏，返回隐藏
      var $table = this.get$InputElem().closest('table');
      if ($table.find("td[blockhide='true']").length > 0) {
        return false;
      }
      // tab隐藏控件不隐藏返回可见
      var $tabPanel = this.get$InputElem().closest('.tab-pane');
      if ($tabPanel.length > 0 && $tabPanel.css('display') == 'none' && $tabPanel.parent().is(':visible')) {
        // 是否验证隐藏tab下的字段
        var dyformOptions = this.dyform$Context().data().options;
        if (dyformOptions.validateInHiddenTab === false) {
          return false;
        }
        return true;
      }
      return true;
    },

    // 设置disabled属性
    setEnable: function (isenable) {
      if (this.isShowAsLabel()) {
        return;
      }
      this.render();
      if (isenable) {
        this.options.columnProperty.showType = dyshowType.edit;
        this.get$InputElem().removeAttr('disabled');
        this.statusChange(2);
      } else {
        this.options.columnProperty.showType = dyshowType.disabled;
        this.get$InputElem().attr('disabled', 'disabled');
        // add by wujx 20161101 begin
        this.removeValidate();
        this.statusChange(1);
        // add by wujx 20161101 end
      }
    },

    setReadOnly: function (isReadOnly) {
      //if (this.isShowAsLabel() && !this.isInSubform()) {
      //return;
      //}
      this.render(true);
      if (isReadOnly) {
        this.options.columnProperty.showType = dyshowType.readonly;
        this.get$InputElem().attr('readonly', 'readonly');
        // add by wujx 20161101 begin
        this.removeValidate();
        this.statusChange(1);
        // add by wujx 20161101 end
      } else {
        this.options.columnProperty.showType = dyshowType.edit;
        this.get$InputElem().removeAttr('readonly');
        this.statusChange(2);
      }
      this.render();
    },

    isReadOnly: function () {
      if (this.options.columnProperty.showType == dyshowType.readonly) {
        return true;
      }
      return false;
    },

    isShowAsLabel: function () {
      return this.options.isShowAsLabel;
    },

    getAllOptions: function () {
      return this.options;
    },

    getRule: function () {
      return JSON.stringify($.ControlUtil.getCheckRuleAndMsg(this.options)['rule']);
    },

    getMessage: function () {
      return JSON.stringify($.ControlUtil.getCheckRuleAndMsg(this.options)['msg']);
    },

    /**
     * 获得控件名
     *
     * @returns
     */
    getCtlName: function () {
      return this.options.columnProperty.controlName;
    },
    getFieldName: function () {
      return this.options.columnProperty.columnName;
    },
    getNamespace: function () {
      return this.options.columnProperty.ns;
    },
    // bind函数，桥接
    bind: function (eventname, event, custom) {
      // if(this.$editableElem != null)
      // this.$editableElem.bind(eventname,event);
      if (custom) {
        this.options[eventname] = event;
      } else {
        $('.' + this.editableClass + "[name='" + this.getCtlName() + "']").on(eventname, event);
      }
      return this;
    },

    bind2: function (eventname, event, custom) {
      var self = this,
        fn = self.options[eventname];
      if (fn && custom) {
        self.options[eventname] = function () {
          fn.apply(this, arguments);
          event && event.apply(this, arguments);
        };
        return self;
      }
      return self.bind.apply(self, arguments);
    },

    // unbind函数，桥接
    unbind: function (eventname) {
      $('.' + this.editableClass + "[name='" + this.getCtlName() + "']").off(eventname);
      return this;
    },

    /**
     * 获得控件元素 指的是动态表单中生成的(_input\_textarea\_select+fieldname)的控件元素
     * 如checkbox，radio元素组则是在此元素下面附加的元素.属性值仍然是放在(_input\_textarea\_select+fieldname)里面.
     *
     * @returns
     */
    getCtlElement: function () {
      return this.get$InputElem();
    },

    /**
     * 将值渲染到页面元素上
     */
    render: function (isRender) {
      if (this.isShowAsLabel()) {
        this.setDisplayAsLabel();
      } else {
        if (typeof isRender == 'undefined' || isRender == null || isRender === true) {
          this.setDisplayAsCtl();
        }
      }
    },
    isRender: function () {
      if ((this.isNaked() && !this.isInitCompleted()) || this.isHide()) {
        // 隐藏或者裸控件时，就不进行渲染
        return false;
      } else {
        return true;
      }
    },

    get$Layout: function () {
      if (this.options.$layout) {
        return this.options.$layout;
      }

      // 控件在从表内,外层布局为从表
      if (this.isInSubform()) {
        var subformConfig = this.getSubformConfig();
        return $.ControlManager.getSubFormControl(subformConfig.formUuid, this.getNamespace());
      }

      // 控件在普通布局内
      var ns = this.getNamespace() || this.getFormUuid();
      var fieldName = this.getFieldName();
      var field = window.reverseFieldTree[ns][fieldName];
      var layoutNode = null == field ? null : field.parent;
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

    isValidationNeeded: function () {
      return this.isValidateOnHidden() || !this.isHide();
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

    isValidateOnHidden: function () {
      return !!this.options.columnProperty.validateOnHidden;
    },

    /**
     * 获取输入框元素
     */
    get$InputElem: function () {
      if (this.$editableElem == null) {
        return $([]); // 还没生成输入框时，先返回一个jquery对象
      } else {
        return this.$editableElem;
      }
    },

    get$LabelElem: function () {
      if (this.$labelElem == null) {
        return $([]); // 还没生成文本元素时时，先返回一个jquery对象
      } else {
        return this.$labelElem;
      }
    },

    /**
     * 添加url超连接事件
     */
    addUrlClickEvent: function (urlClickEvent) {
      var onlyreadUrl = this.options.columnProperty.onlyreadUrl;
      if (!(onlyreadUrl == '' || onlyreadUrl == undefined)) {
        if (this.isShowAsLabel()) {
          var elment = this.get$LabelElem();
          elment.css('cursor', 'pointer');
          elment.addClass('text-primary');
          elment.unbind('click', urlClickEvent);
          elment.bind(
            'click',
            {
              url: onlyreadUrl,
              dyform: this.dyform$Context()
            },
            urlClickEvent
          );
        }
      }
    },

    getCurrentForm: function () {
      return this.options.$currentForm;
    },
    getDyformDataOptions: function () {
      return this.getCurrentForm().dyformDataOptions();
    },
    getFieldDataOptions: function () {
      var self = this;
      var fieldName = self.getFieldName();
      var formUuid = self.getFormUuid() || '';
      var dataUuid = self.getDataUuid() || self.getCurrentForm().getDataUuid();
      var dyformDataOptions = self.getCurrentForm().dyformDataOptions();
      return {
        getOptions: function (key) {
          key = formUuid + '.' + dataUuid + '.' + fieldName + '.' + key;
          return dyformDataOptions.getOptions(key);
        },
        putOptions: function (key, value) {
          key = formUuid + '.' + dataUuid + '.' + fieldName + '.' + key;
          dyformDataOptions.putOptions(key, value);
        }
      };
    },
    /**
     * 设置需要分组验证的标识
     */
    setValidateGroup: function (group) {
      var element = this.$placeHolder;
      for (var i in group) {
        element.attr(i, group[i]);
      }

      this.options.validateGroup = group;
    },
    getValidateGroup: function () {
      return this.options.validateGroup;
    },

    getValidator: function (element) {
      var $form = this.getCurrentForm();

      var validator = $form.data('wValidator');
      if (typeof validator == 'undefined' || validator == null) {
        validator = $form.wValidate();
        $form.data('wValidator', validator);
      }

      return validator;
    },
    /* 判断控件是不是在class为field的tr里面 */
    isControlInFieldTr: function () {
      if (this.$placeHolder.parents("tr[class='field']").size() == 0) {
        return false;
      } else {
        return true;
      }
    },
    isFieldTrHide: function () {
      if (this.$placeHolder.parents("tr[class='field']:hidden").size() == 0) {
        return false;
      } else {
        return true;
      }
    },

    // 设置该控件是否需要验证, true为需要验证,false为不需要验证
    enableValidate: function (enable) {
      enable = typeof enable == 'undefined' ? true : enable;
      this.options.enableValidate = enable;
    },

    // 判定控件是否需要验证,true需要验证,false不需要验证
    isValidateEnable: function () {
      var enable = typeof this.options.enableValidate == 'undefined' ? true : this.options.enableValidate;
      return enable;
    },
    // 获取字段的显示名称
    getDisplayName: function () {
      return this.options.columnProperty.displayName;
    },
    isOcxAttach: function () {
      return formDefinitionMethod.isOcxAttach(this.getInputMode());
    },

    initValidate: function (formatAndAdd) {
      // true表示要验证
      var ctlName = this.getCtlName();
      var result = this.invoke('beforeValidate'); // 若返回false,表示不进行验证,
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

      var validator = this.getValidator(this.$placeHolder);

      var rule = this.getRule();
      var message = this.getMessage();
      if (typeof rule == 'undefined') {
        return true;
      }
      // modify by wujx 20160927 没有表单定义的校验或用户自定义校验，则返回 begin
      var customValidates = this.options.customValidates;
      if (typeof rule == 'undefined' && (customValidates == undefined || customValidates.length == 0)) {
        return true;
      }
      // modify by wujx 20160927 没有表单定义的校验或用户自定义校验，则返回 end

      var ruleObj = {};
      var messageObj = {};
      // /console.log(ctlName + ":" + rule);

      ruleObj = eval('(' + rule + ')');
      messageObj = eval('(' + message + ')');

      var fieldName = this.getFieldName();

      this.setCustomValidateRule(ruleObj, messageObj, this.getDataUuid(), fieldName, this.getFormDefinition().tableName);
      validator.settings.rules[ctlName] = ruleObj;
      validator.settings.messages[ctlName] = messageObj;

      // add by wujx 20160705 增加自定义校验 begin
      var _this = this;

      if (customValidates != undefined && customValidates.length > 0) {
        for (var key in customValidates) {
          var validRuleFn = customValidates[key]['validRuleFn'];
          if (!validRuleFn) {
            //没有按规范定义自定义校验规则
            console.error('没有遵守自定义校验规则的参数规范:' + customValidates);
            continue;
          }
          var validJson = validRuleFn.call(this, _this.getValue(), _this);
          if (!(validJson instanceof Object)) {
            console.error('自定义校验规则' + validRuleFn + '中，返回值不是合法的Object对象类型，当前校验失效！');
            continue;
          }
          if (validJson.valid == undefined) {
            console.error('自定义校验规则' + validRuleFn + '中，返回值对象缺少valid（是否校验通过）属性，当前校验失效！');
            continue;
          }
          if (!validJson.valid && validJson.tipMsg == undefined) {
            console.error('自定义校验规则' + validRuleFn + '中，返回值对象缺少tipMsg（提示消息）属性，当前校验失效！');
            continue;
          }
          var methodMark = 'custValidMethod' + new UUID() + ctlName;
          // moidfy by wujx 20160809
          // 必须采用模块化，将外部validRuleFn作为参数传进来，
          // 如果直接传进来，会因为作用域，导致获取到的validRuleFn一直是for循环后的最后一个值
          var validRuleFnReal = (function (fn, ctlName, methodMark) {
            return function () {
              var validJson2 = fn.call(this, _this.getValue(), _this);
              validator.settings.messages[ctlName][methodMark] = validJson2.tipMsg;
              return validJson2.valid;
            };
          })(validRuleFn, ctlName, methodMark);

          validJson.methodMark = methodMark;
          validator.settings.rules[ctlName] = validator.settings.rules[ctlName] || {};
          validator.settings.messages[ctlName] = validator.settings.messages[ctlName] || {};
          validator.settings.rules[ctlName][methodMark] = true;
          validator.settings.messages[ctlName][methodMark] = validJson.tipMsg;
          jQuery.wValidator.addMethod(methodMark, validRuleFnReal);
        }
      }
      var $element = this.get$InputElem();
      if (
        (formatAndAdd && $element && $element.not(':hidden').length) ||
        ($element.parents('.dyform-subtable').length && $element.parents('td').css('display') !== 'none')
      ) {
        var rules = validator.settings.rules[ctlName];
        for (var method in rules) {
          var rule = {
            method: method,
            parameters: rules[method]
          };
          try {
            validator.formatAndAdd(this, rule);
          } catch (e) {
            if (validator.settings.debug && window.console) {
              console.log('Exception occurred when checking element ' + ctlName + ", check the '" + rule.method + "' method.", e);
            }
          }
        }
      }
    },
    /**
     * 验证控件的值
     */
    validate: function () {
      var columnName = this.options.columnProperty.columnName;

      if (this.initValidate(false)) {
        return true;
      }

      var validator = this.getValidator(this.$placeHolder);
      // add by wujx 20160705 增加自定义校验 end

      var valid = true;

      valid = validator.control(this);

      this.invoke('afterValidate');

      return valid;
    },
    objectLength: function (obj) {
      var count = 0;
      for (var i in obj) {
        count++;
      }
      return count;
    },

    /**
     * 增加自定义校验规则（用于二开） add by wujx 20160705
     *
     * @param validRuleFn
     *            自定义校验规则函数，返回false表示校验失败，否则校验成功
     */
    addCustomValidate: function (validRuleFn) {
      if (typeof validRuleFn != 'function') {
        console.error('自定义校验规则' + validRuleFn + '中，第一个参数不是合法的FUNCTION类型！');
        return;
      }

      var _this = this,
        validRuleFn2 = validRuleFn.toString();
      // 自定义校验内容存放到控件校验区
      if (!_this.options.customValidates) {
        _this.options.customValidates = new Array();
      }
      for (var i = 0; i < _this.options.customValidates.length; i++) {
        if (_this.options.customValidates[i].validRuleFn.toString() == validRuleFn2) {
          return false;
        }
      }
      _this.options.customValidates.push({
        validRuleFn: validRuleFn
      });
    },

    /**
     * 设定自定义的校验规则
     *
     * @param ruleObj
     * @param messageObj
     * @param rowId
     * @param fieldName
     * @param control
     */
    setCustomValidateRule: function (ruleObj, messageObj, dataUuid, fieldName, tblName) {
      var _this = this;
      var validateGroup = this.options.validateGroup;
      var newRuleObj = {};
      var newMessageObj = {};
      for (var i in ruleObj) {
        // var ruleItem = ruleObj[i];
        if (i == 'unique') {
          // 唯一性验证
          var data = {
            uuid: dataUuid,
            tblName: tblName, // 表单名称
            fieldName: fieldName,
            unitUnique: this.options.commonProperty.unitUnique == 'true' ? true : false,
            fieldValue: function () {
              return _this.getValue() + '';
            },
            control: _this,
            form: _this.dyform$Context()
          };

          if (typeof validateGroup != 'undefined' && validateGroup != null) {
            data[validateGroupType.unique] = validateGroup[validateGroupType.unique];
          }

          newRuleObj['isUnique'] = {
            url: ctx + '/pt/dyform/data/validate/exists',
            type: 'POST',
            async: false,
            data: data
          };
          newMessageObj['isUnique'] = messageObj[i];
          delete ruleObj[i]; // 删除unique
        }
      }
      $.extend(ruleObj, newRuleObj);
      $.extend(messageObj, newMessageObj);
    },

    getControl: function (fieldName) {
      return $.ControlManager.getCtl(fieldName);
    },
    // 设值到真实值显示值字段
    setToRealDisplayColumn: function () {
      var self = this;
      if (!self.options.columnProperty.realDisplay) {
        return;
      }
      var value = self.getValue();
      var displayValue = self.getDisplayValue();
      var real = self.options.columnProperty.realDisplay.real;
      var display = self.options.columnProperty.realDisplay.display;

      if (typeof real === 'string' && real.length > 0) {
        // $.dyform.warn("realDisplay.real is Deprecated");// 配置过期，删掉控件重新配置
        var control = $.ControlManager.getCtl(self.getContronId(real));
        if (typeof control == 'undefined' || control == null) {
          return;
        }

        setControlValue(control, value, real);
      }

      if (typeof display === 'string' && display.length > 0) {
        var control = $.ControlManager.getCtl(self.getContronId(display));
        if (typeof control == 'undefined' || control == null) {
          return;
        }

        if (typeof displayValue === 'object') {
          displayValue = displayValue.join(self.getSeparator());
        }

        setControlValue(control, displayValue, display);
      }

      function setControlValue(control, val, fieldName) {
        control.setValue(val);

        var formDefinition = control.getFormDefinition();
        if (!formDefinition.mappingFields) {
          formDefinition.mappingFields = [fieldName];
          return;
        }

        if ($.inArray(fieldName, formDefinition.mappingFields) === -1) {
          formDefinition.mappingFields.push(fieldName);
        }
      }
    },

    isInSubform: function () {
      if (this.getPos() == dyControlPos.subForm) {
        // 在从表中
        return true;
      } else {
        return false;
      }
    },

    getContronId: function (fieldName) {
      var dataUuid = this.options.columnProperty.dataUuid;
      if (this.getPos() == dyControlPos.subForm) {
        // 在从表中
        return this.getCellId(dataUuid, fieldName);
      }
      return this.getCellId(null, fieldName);
    },
    getCellId: function (rowId, fieldName) {
      var ns = rowId || this.getNamespace();
      return $.trim(ns).length > 0 ? $.dyform.getCellId(ns, fieldName) : fieldName;
    },

    /**
     * 必输字段的备注前加*或去掉*，
     * 1、增加isAdd参数，true为添加*，false为去除*（默认true）modifybywujx20160708
     *
     */
    addMustMark: function (isAdd) {
      if (this.getPos() == dyControlPos.subForm) {
        // 从表不用加
        return;
      }
      if (!this.isRequired() && isAdd == undefined) {
        // 非必输
        return;
      }
      var $parentTd = this.$placeHolder.parent('td');

      window.setTimeout(function () {
        if ($parentTd.size() == 0) {
          return;
        }

        var $prevTd = $parentTd.prev('td');
        if ($prevTd.size() == 0) {
          return;
        }
        var requiredAttr = $prevTd.attr('required');
        if (typeof requiredAttr != 'undefined') {
          return;
        }
        var asteriskRemark = '*';
        var mustRemark = '<font color="red" size="2">*</font>';
        var mustRemarkSelector = "font[color='red']";
        if (($prevTd.is('.Label') && $prevTd.html().length > 0) || $prevTd.find('label').size() > 0) {
          if ($prevTd.is('.Label')) {
            if (isAdd === false) {
              $prevTd.find(mustRemarkSelector).remove();
            } else {
              // var html = $prevTd.html() + mustRemark;
              $prevTd.html().indexOf(asteriskRemark) <= 0 && $prevTd.prepend(mustRemark);
            }
          } else {
            var label = $prevTd.find('label').get($prevTd.find('label').size() - 1);
            var $label = $(label);
            if (isAdd === false) {
              $label.find(mustRemarkSelector).remove();
            } else {
              $label.html().indexOf(asteriskRemark) <= 0 && $label.prepend(/*$label.html() + */ mustRemark);
            }
          }

          // 单元格带 `label` 标签时必填设置错误
          // if (isAdd === false) {
          //   $prevTd.removeAttr("required");
          // } else {
          //   $prevTd.attr('required', 'required');
          // }
        } else if ($.trim($prevTd.html()).length > 0) {
          if (isAdd === false) {
            $prevTd.find(mustRemarkSelector).remove();
          } else if ($prevTd.find(mustRemarkSelector).length <= 0) {
            // var html = $prevTd.html() + mustRemark;
            $prevTd.prepend(mustRemark);
          }
        }
      }, 100);
    },
    /**
     * 判断是不是radio控件
     */
    isRadioCtl: function () {
      if (this.getInputMode() == dyFormInputMode.radio) {
        return true;
      } else {
        return false;
      }
    },
    getInputMode: function () {
      if (typeof this.options.commonProperty.inputMode == 'undefined') {
        throw new Error(this.getCtlName() + ' inputMode is undefined');
      }
      return this.options.commonProperty.inputMode;
    },
    /**
     * 判断是不是checkbox控件
     */
    isCheckBoxCtl: function () {
      if (this.getInputMode() == dyFormInputMode.checkbox) {
        return true;
      } else {
        return false;
      }
    },
    /**
     * 树形下拉框
     *
     * @returns {Boolean}
     */
    isTreeSelectCtl: function () {
      if (this.options.commonProperty.inputMode == dyFormInputMode.treeSelect) {
        return true;
      } else {
        return false;
      }
    },

    isSelectCtl: function () {
      if (this.options.commonProperty.inputMode == dyFormInputMode.selectMutilFase) {
        return true;
      } else {
        return false;
      }
    },
    isNumberCtl: function () {
      if (this.options.commonProperty.inputMode == dyFormInputMode.number) {
        return true;
      } else {
        return false;
      }
    },
    isCkeditor: function () {
      if (this.options.commonProperty.inputMode == dyFormInputMode.ckedit) {
        return true;
      } else {
        return false;
      }
    },

    // 是否为附件控件
    isFileupload: function () {
      return $.wControlInterface.isAttachInputMode(this.options.commonProperty.inputMode);
    },
    isAttachInputMode: function (inputMode) {
      if (inputMode == dyFormInputMode.accessory1 || inputMode == dyFormInputMode.accessory3 || inputMode == dyFormInputMode.accessoryImg) {
        return true;
      } else {
        return false;
      }
    },
    culateByFormula: function () {
      /*
       * if(typeof this.options.ctlFormulas == "undefined"){ return; }
       *
       * var formulas = this.options.ctlFormulas; for(var i = 0; i <
       * formulas.length; i ++){ formulas[i].call(this.dyform$Context(),
       * this); }
       */
      // alert(this.dyform$Context().cache);
      // var allformulas = this.cache.get.call(this, cacheType.formula);
      $.ControlManager.culateByFormula(this);
    },
    /* 在设置之后触发该事件 */
    invoke: function (method) {
      var self = this;
      if ($('.widget-main').length > 0) {
        // 表单控件设值后重新设置滚动条
        setTimeout(function () {
          $('.widget-main').getNiceScroll().resize();
        }, 500);
      }
      // add by wujx 20161014 begin 触发校验
      if (method === 'afterSetValue' || method == 'afterOrgInfo') {
        var $inputElem = this.get$InputElem();
        if ($inputElem && $inputElem.length > 0 && !this.isShowAsLabel() && (this.isEnable ===undefined || this.isEnable())) {
          $inputElem.trigger('reValidate', this);
        }
      }
      // add by wujx 20161014 end
      var handle = self.options[method];
      if (method === 'afterSetValue') {
        self['_timeoutIdForSetValue'] && clearTimeout(self['_timeoutIdForSetValue']);
        var args = $.makeArray(arguments).slice(1);
        return (self['_timeoutIdForSetValue'] = setTimeout(function () {
          $(self).trigger('afterSetValue', args);
          $.isFunction(handle) && handle.apply(self, args);
          self['_timeoutIdForSetValue'] = null;
        }, 0));
      } else {
        $(self).trigger(method, $.makeArray(arguments).slice(1));
        if ($.isFunction(handle)) {
          return handle.apply(self, $.makeArray(arguments).slice(1));
        }
      }
    },
    dyform$Context: function () {
      // console.log("---" + this.$placeHolder.size());
      if (this.$dyform$Context) {
        return this.$dyform$Context;
      }
      return (this.$dyform$Context = this.$placeHolder.closest('.dyform'));
    },

    /**
     * 获取被选中的文本
     */
    getSelection: function () {
      if (!this.$editableElem[0]) {
        // 编辑框还没展示出来
        return null;
      }

      if (document.selection) {
        // IE
        return document.selection.createRange().text;
      } else {
        return this.$editableElem[0].value.substring(this.$editableElem[0].selectionStart, this.$editableElem[0].selectionEnd);
      }
    },
    /**
     * 是否是进行选中操作
     */
    isSelection: function () {
      var selectionVal = this.getSelection();
      if (selectionVal == null || $.trim(selectionVal).length == 0) {
        return false;
      } else {
        return true;
      }
    },

    // 验证输入框
    validateElem: function () {
      var editableElem = this.get$InputElem();
      if (editableElem != null && editableElem.size() > 0) {
        // editableElem.focus();
        var validator = this.getValidator(this.$placeHolder);
        // 重写jquery.validate.js validationTargetFor
        // 通过name获取单选框错误
        // added by lingh 20150603
        validator.validationTargetFor = function (element) {
          return element;
        };
        //                validator.element(editableElem);
        validator.element(this);
      }
    },

    // 移除校验 add by wujx 20160927
    removeValidate: function () {
      /**
       * var validator = this.getValidator(this.$placeHolder); var ctlName =
       * this.getCtlName(); delete validator.settings.rules[ctlName];
       * delete validator.settings.messages[ctlName]; delete
       * validator.invalid[ctlName]; if (typeof bubble != "undefined" &&
       * bubble != null) { bubble.removeErrorItem(ctlName); }
       */
      var editableElem = this.get$InputElem();
      if (editableElem != null && editableElem.size() > 0) {
        if (typeof Theme != 'undefined') {
          var validator = this.getValidator(this.$placeHolder);
          var ctlName = this.getCtlName();
          delete validator.invalid[ctlName];
          if (validator.unhighlight && validator.success) {
            validator.unhighlight(this);
            validator.success(null, this);
          }
        }
      }
    },

    /**
     * 判断是否为[{value:"1",name:"笔记本"},{}]格式 add by wujx 20160803
     *
     * @param optionSet
     * @returns {Boolean}
     */
    isValueNameFormater: function (optionSet) {
      if (optionSet instanceof Array) {
        var optionSetArray = optionSet;
        for (var i = 0; i < optionSetArray.length; i++) {
          var optionSet = optionSetArray[i];
          if (!(optionSet.hasOwnProperty('name') && optionSet.hasOwnProperty('value'))) {
            return false;
          }
        }
        return true;
      } else {
        if (optionSet.hasOwnProperty('name') && optionSet.hasOwnProperty('value')) {
          return true;
        } else {
          return false;
        }
      }
    },

    /**
     * 判断取值时，是否需要异步, 如:图标附件及正文附件需要异步上传附件，这时就需要异步取值
     *
     * @returns {Boolean}
     */
    isGetValueByAsyn: function () {
      return false;
    },

    /**
     * 判断值是否为空
     */
    isValueAsBlank: function () {
      var value = '';
      var isAttachCtl = this.isAttachCtl && this.isAttachCtl();
      if (isAttachCtl) {
        // 对附件控件在检验时，只要确认是否已有做上传文件即可
        value = WellFileUpload.files[this.getFielctlID()];
      } else {
        value = this.getValue();
      }

      var isBlank = true;
      if (value == null || value == undefined) {
        isBlank = true;
      } else if (isAttachCtl && value instanceof Array && value.length > 0) {
        isBlank = false;
      } else {
        if ($.trim(value).length > 0) {
          isBlank = false;
        }
      }
      return isBlank;
    },

    // 返回表单归属的系统单位ID
    getSystemUnitId: function () {
      return this.getFormDefinition().systemUnitId;
    },

    getFieldParams: function () {
      var fieldName = this.getFieldName();
      return this.getFormDefinition().getField(fieldName);
    },
    html2Escape: function (sHtml) {
      return typeof sHtml === 'string'
        ? sHtml.replace(/[<>&"']/g, function (c) {
            return arrEntities1[c];
          })
        : sHtml;
    },
    //控件状态变化监听接口
    toggle: function (labelCallback /*编辑状态发生变化 */, controlCallback /*显示状态发生变化*/, hideCallback) {
      if (labelCallback) {
        this.labelCallbacks = this.labelCallbacks || [];
        this.labelCallbacks.push(labelCallback);
      }

      if (controlCallback) {
        this.controlCallbacks = this.controlCallbacks || [];
        this.controlCallbacks.push(controlCallback);
      }

      if (hideCallback) {
        this.hideCallbacks = this.hideCallbacks || [];
        this.hideCallbacks.push(hideCallback);
      }
    },
    //状态变化
    statusChange: function (status) {
      //  1： 显示为label时的回调, 2: 显示control时的回调, 3: 隐藏时的回调
      var self = this;
      if (status == 1) {
        for (var id in this.labelCallbacks) {
          var labelCallback = this.labelCallbacks[id];
          labelCallback(self);
        }
      } else if (status == 2) {
        for (var id in this.controlCallbacks) {
          var controlCallback = this.controlCallbacks[id];
          controlCallback(self);
        }
      } else if (status == 3) {
        for (var id in this.hideCallbacks) {
          var hideCallback = this.hideCallbacks[id];
          hideCallback(self);
        }
      }
    },
    addLog: function (operation, bValue, aValue) {
      var self = this;
      var inputMode = self.getInputMode(),
        attrType = 'string',
        createTime;
      if (inputMode === dyFormInputMode.ckedit || inputMode === dyFormInputMode.textArea) {
        attrType = 'clob';
      }
      if (typeof JDS === 'undefined' || false == $.isFunction(JDS.getServerDate)) {
        createTime = new Date();
      } else {
        createTime = JDS.getServerDate();
      }
      var parentDataId = self.getCurrentForm().getDataUuid();
      var formUuid = self.getFormUuid() || '';
      var dataUuid = self.getDataUuid() || parentDataId;
      var entity = {
        dataDefType: 'dyformdata',
        dataDefId: formUuid,
        dataDefName: null,
        dataId: dataUuid,
        dataName: null,
        parentDataId: parentDataId,
        attrId: self.getFieldName(),
        attrName: self.getDisplayName(),
        attrType: attrType,
        createTime: createTime.format('yyyy-MM-dd HH:mm:ss'),
        operation: operation,
        beforeValue: bValue,
        afterValue: aValue
      };
      // var logs = self.logs || (self.logs = []);
      var dyformDataOptions = self.getDyformDataOptions();
      var logs = dyformDataOptions.getOptions('logs') || [];
      logs.push(entity);
      dyformDataOptions.putOptions('logs', logs);
    },
    getLogs: function () {
      return this.logs;
    },
    proxiedBeforeInvoke: function (method, beforeFn) {
      return (function (object) {
        var _method = object[method];
        var _proxied = function () {
          beforeFn && beforeFn.apply(object, arguments);
          var retVal = _method.apply(object, arguments);
          return retVal;
        };
        object[method] = _proxied;
      })(this);
    },
    proxiedAfterInvoke: function (method, afterFn) {
      return (function (object) {
        var _method = object[method];
        var _proxied = function () {
          var retVal = _method.apply(object, arguments);
          afterFn && afterFn.apply(object, arguments);
          return retVal;
        };
        object[method] = _proxied;
      })(this);
    }
  };

  //为控件添加配件相关管理接口
  $.extend(
    $.wControlInterface,
    ($.wAccessory = {
      addAccessory: function (opitons) {
        var self = this;
        var defaultOptions = {
          id: null,
          init: function (control) {},
          onDisplayAsLabel: $.noop,
          onDisplayAsControl: $.noop,
          onDisplayAsNone: $.noop
        };
        var opitons = $.extend(defaultOptions, opitons);
        if (!opitons.id) {
          opitons.id = 'accessory' + $.guid++;
        }

        var accessory = new BaseAccessory(opitons, self);
        accessory.init();

        this.toggle(
          function () {
            //label
            if (accessory.onDisplayAsLabel && accessory.onDisplayAsLabel != $.noop) {
              accessory.onDisplayAsLabel.call(accessory, self);
            }
          },
          function () {
            //control
            if (accessory.onDisplayAsControl && accessory.onDisplayAsControl != $.noop) {
              accessory.onDisplayAsControl.call(accessory, self);
            }
          },
          function () {
            //hide
            if (accessory.onDisplayAsNone && accessory.onDisplayAsNone != $.noop) {
              accessory.onDisplayAsNone.call(accessory, self);
            }
          }
        );

        self.accessories = self.accessories || {};
        self.accessories[opitons.id] = accessory;
        return accessory;
      },
      getAccessories: function () {
        var self = this;
        return self.accessories;
      },
      getAccessory: function (id) {
        var self = this;
        return self.accessories[id];
      },
      removeAccessory: function (id) {
        var self = this;
        var accessories = self.getAccessories();
        if (null == accessories) {
          return;
        }
        var accessory = accessories[id];
        accessory.remove(); // 移除
        delete accessories[id];
      },
      removeAllAccessories: function () {
        var self = this;
        var accessories = self.getAccessories();
        if (null == accessories) {
          return;
        }
        for (var id in accessories) {
          self.removeAccessory(id);
        }
      },
      createAccessoryContainer: function (position, accessoryId) {
        var $accessoryContainer = null;
        var $elem = null;
        if (this.isVisible() && this.isEditable()) {
          //可编辑状态
          if (this.getInputMode() === '31') {
            //数字输入框
            $elem = this.get$WrapElem();
          } else {
            $elem = this.get$InputElem();
          }
        }
        if (this.isVisible() && !this.isEditable()) {
          //可编辑状态
          $elem = this.get$LabelElem();
        }

        if ($elem) {
          var containerId = 'accessory_container_' + accessoryId;
          var containerHtml = "<span id='" + containerId + "'></span>";
          var $accessoryContainer = $(containerHtml);
          if (position == -1) {
            $elem.before($accessoryContainer);
          } else {
            $elem.after($accessoryContainer);
          }
        }
        return $accessoryContainer;
      }
    })
  );

  var BaseAccessory = function (options, control) {
    var self = this;
    self.options = options;
    self.id = self.options.id;
    self.init = self.options.init;
    self.control = control;
    self.onDisplayAsLabel = self.options.onDisplayAsLabel;
    self.onDisplayAsControl = self.options.onDisplayAsControl;
    self.onDisplayAsNone = self.options.onDisplayAsNone;
    return self;
  };

  $.extend(BaseAccessory.prototype, {
    getControl: function () {
      return this.control;
    },
    getId: function () {
      return this.id;
    },
    remove: function () {
      var self = this;
      var control = self.getControl();
      if (null == control) {
        return;
      }
      delete self.onDisplayAsLabel; //防止toggle通知
      delete self.onDisplayAsControl; //防止toggle通知
      delete self.onDisplayAsNone; //防止toggle通知
      self.removeContainer();
    },

    /**
     * 1: 表示附加到后面
     * -1:表示附件到前面
     */
    createContainer: function (position) {
      var control = this.getControl();
      this.removeContainer();
      this.$container = control.createAccessoryContainer(position, this.id);
      return this.$container;
    },
    removeContainer: function () {
      //删除配件容器
      if (this.$container) {
        this.$container.remove();
      }
    },

    after: function (html) {
      var $container = this.createContainer(1);
      this.$container.html(html);
    },
    before: function (html) {
      var $container = this.createContainer(-1);
      this.$container.html(html);
    },
    empty: function () {
      this.$container.empty();
    },
    hide: function () {
      this.$container.hide();
    },
    show: function () {
      this.$container.show();
    },
    $: function (selector) {
      return $(selector, this.$container);
    }
  });
  return BaseAccessory;
})(jQuery);
