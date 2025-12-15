(function ($) {
  var columnProperty = {
    // 控件字段属性
    applyTo: null, // 应用于
    columnName: null, // 字段定义 fieldname
    displayName: null, // 描述名称 descname
    dbDataType: '', // 字段类型 datatype type
    indexed: null, // 是否索引
    showed: null, // 是否界面表格显示
    sorted: null, // 是否排序
    sysType: null, // 系统定义类型，包括三种（0：系统默认，1：管理员常量定义，2：表单添加后自定义）
    length: null, // 长度
    showType: '1', // 显示类型 1,2,3,4 datashow
    defaultValue: null, // 默认值
    valueCreateMethod: '1', // 默认值创建方式 1用户输入
    onlyreadUrl: null // 只读状态下设置跳转的url
  };

  // 控件公共属性
  var commonProperty = {
    inputMode: null, // 输入样式 控件类型 inputDataType
    fieldCheckRules: null,
    fontSize: null, // 字段的大小
    fontColor: null, // 字段的颜色
    ctlWidth: null, // 宽度
    ctlHight: null, // 高度
    textAlign: null // 对齐方式
  };

  /*
   * SERIALNUMBER CLASS DEFINITION ======================
   */
  var SerialNumber = function ($placeHolder, options) {
    this.options = $.extend({}, $.fn['wserialNumber'].defaults, options);
    this.value = '';
    this.$editableElem = null;
    this.$labelElem = null;
    this.$placeHolder = $placeHolder;
  };

  SerialNumber.prototype = {
    constructor: SerialNumber
  };

  $.SerialNumber = {
    // add by wujx 20160622
    /* 设值到标签中 */
    setValue2LabelElem: function () {
      $.wTextCommonMethod.setValue2LabelElem.apply(this);
    },

    createEditableElem: function () {
      if (this.$editableElem != null) {
        // 创建可编辑框
        return;
      }
      var options = this.options;
      var ctlName = this.getCtlName();
      var editableElem = document.createElement('input');
      editableElem.setAttribute('class', this.editableClass);
      editableElem.setAttribute('name', ctlName);
      editableElem.setAttribute('type', 'text');
      editableElem.setAttribute('maxlength', options.columnProperty.length);
      editableElem.setAttribute('placeholder', options.columnProperty.placeholder || '点击编辑流水号');
      if (options.commonProperty.inputMode == '7') {
        editableElem.setAttribute('readonly', 'true');
        editableElem.classList.add('modal-input');
      }

      $(editableElem).css(this.getTextInputCss());

      this.$placeHolder.after($(editableElem));
      this.$editableElem = this.$placeHolder.next('.' + this.editableClass);
      var _this = this;

      var inputMode = this.options.commonProperty.inputMode;

      if (inputMode == dyFormInputMode.serialNumber) {
        this.$editableElem.addClass('input-search'); // css in
        // wellnewoa.css
      }

      var formUuid = this.options.formUuid;
      var element = this.$editableElem;
      var val = this.$editableElem.val();
      var options = this.options;
      this.displayByShowType();
      if (dyFormInputMode.serialNumber == inputMode) {
        // 可编辑流水号
        this.$editableElem.on('click', function () {
          var designatedId_ = options.designatedId;
          var designatedType_ = options.designatedType;
          var isOverride_ = options.isOverride;
          var fieldParams = _this.getFieldParams();
          if (options.isCreateWhenSave == '1') {
            isOverride_ = '3';
          }
          if (options.columnProperty.showType == '3') {
            console.warn('流水号只读');
            return false;
          }
          getEditableSerialNumber(
            designatedId_,
            designatedType_,
            parseInt(isOverride_),
            formUuid,
            options.columnProperty.columnName,
            ctlName,
            {
              dialogTitle: fieldParams.dialogTitle,
              serialNumberTypeLabel: fieldParams.serialNumberTypeLabel,
              serialNumberSelLabel: fieldParams.serialNumberSelLabel,
              serialNumberLabel: fieldParams.serialNumberLabel,
              serialNumberFill: fieldParams.serialNumberFill,
              automaticNumberSupplement: fieldParams.automaticNumberSupplement
            },
            _this.$editableElem.data('initSelectedValue'),
            function (sn, selectedValue) {
              _this.setValue(sn);
              var serialnumber = selectedValue;
              serialnumber.value = sn;
              var dyformDataOptions = _this.getFieldDataOptions();
              dyformDataOptions.putOptions('serialNumberConfirm', JSON.stringify(serialnumber));

              _this.$editableElem.data('initSelectedValue', selectedValue);
            },
            options.columnProperty.length,
            options.serialNumberTips
          );
        });
      } else {
        //流失号为空则自动生成
        //this._setSerialNumberAndOccupy();
        //this.displayByShowType();
      }
      if ((this.getValue() == '' || this.getValue() == undefined) && options.columnProperty.defaultValue != '') {
        // 设置默认值
        this.setValue(options.columnProperty.defaultValue);
      }

      if (options.columnProperty.showType == '1') {
        this.$editableElem.on('blur', function () {
          _this.setValue(_this.$editableElem.val());
        });
      }
    },

    setDesignatedId: function (designatedId) {
      this.options.designatedId = designatedId;
    },

    setDesignatedType: function (designatedType) {
      this.options.designatedType = designatedType;
    },

    /**
     * 变更是否允许补号
     * @param allow
     */
    toggleFillSerialNumber: function (allow) {
      this.getFieldParams().serialNumberFill = allow == undefined ? false : allow;
    },

    /**
     * 按照流水号ID生成流水
     * @param id
     */
    generateSerialNumber: function (id, isOccupied, renderParams) {
      var formUuid = this.options.formUuid;
      var field = this.options.columnProperty.columnName;
      var maintain;
      var _this = this;
      if (id == null || id == undefined || id == '') {
        console.error('流水号ID为空');
        return '';
      }
      JDS.call({
        service: 'serialNumberService.generateSerialNumberMaintain',
        data: [
          {
            serialNumberId: id,
            occupied: isOccupied,
            formUuid: formUuid,
            formField: field,
            renderParams: renderParams
          }
        ],
        version: '',
        async: false,
        success: function (result) {
          maintain = result.data;
        },
        error: function (jqXHR) {
          var result = jqXHR.responseJSON;
          if (result && typeof result.data === 'string') {
            if (result.data && typeof result.msg === 'string' && result.msg.indexOf('wellsoft') > -1) {
              appModal.alert(result.data);
            } else {
              appModal.alert(result.msg);
            }
          }
        }
      });
      return maintain;
    },

    // modify by wujx 设置自动生成流水号
    _setSerialNumberAndOccupy: function () {
      // 判断是否应用于流程流水号，是则不产生流水号
      if (this.options.columnProperty.applyTo == applyToOptions.SERIAL_NO) {
        return;
      }
      var options = this.options;
      var val = this.value;
      var unEditDesignatedId = options.designatedId;
      var unEditIsSaveDb = options.isSaveDb;

      if (val == '') {
        var notEditIsSaveDb = unEditIsSaveDb == '0' ? true : false;
        var maintain = this.generateSerialNumber(unEditDesignatedId, notEditIsSaveDb);
        //判断是否有使用人权限
        if (maintain) {
          var snValue = maintain.headPart + maintain.pointer + maintain.lastPart;
          this.setValue(snValue);
          var serialNumberConfirm = {
            sn: options.designated, // 流水号名称
            snm: snValue, // 流水号维护记录
            snid: options.designatedId, // 流水号ID
            headPart: maintain.headPart,
            lastPart: maintain.lastPart,
            value: snValue,
            uuid: maintain.uuid,
            occupied: maintain.occupied
          };
          var dyformDataOptions = this.getFieldDataOptions();
          dyformDataOptions.putOptions('serialNumberConfirm', JSON.stringify(serialNumberConfirm));
        } else {
          //设置为不可点击
          this.setEnable(false);
          this.$editableElem.attr('placeholder', '您无流水号的数据权限，请联系管理员');
        }
        if (!this.validate() && notEditIsSaveDb) {
          //验证流水是否重复等规则
          // 占用才重新请求，不然每次请求都相同
          this.value = ''; //清空流水
          this._setSerialNumberAndOccupy();
        }
      }
    },

    initControlOver: function ($dyform, fieldname) {
      var _this = this;
      if (_this.value == '') {
        if (dyFormInputMode.serialNumber != this.options.commonProperty.inputMode) {
          setTimeout(function () {
            //自动生成流水
            _this._setSerialNumberAndOccupy();
            if (_this.options.columnProperty.showAsLabel == '2') {
              _this.displayByShowType();
            }
          }, 0);
        }
      }
    }
  };

  /*
   * SERIALNUMBER PLUGIN DEFINITION =========================
   */
  $.fn.wserialNumber = function (option) {
    var method = false;
    var args = null;
    if (arguments.length == 2) {
      method = true;
      args = arguments[1];
    }

    if (typeof option == 'string') {
      if (option === 'getObject') {
        // 通过getObject来获取实例
        var $this = $(this),
          data = $this.data('wserialNumber');
        if (data) {
          return data; // 返回实例对象
        } else {
          throw new Error('This object is not available');
        }
      }
    }

    var $this = $(this),
      data = $this.data('wserialNumber'),
      options = typeof option == 'object' && option;
    if (!data) {
      data = new SerialNumber($(this), options);
      $.extend(data, $.wControlInterface);
      $.extend(data, $.wTextCommonMethod);
      $.extend(data, $.SerialNumber);
      data.init();
      $this.data('wserialNumber', data);
    }
    if (typeof option == 'string') {
      if (method == true && args != null) {
        return data[option](args);
      } else {
        return data[option]();
      }
    } else {
      return data;
    }
  };

  $.fn.wserialNumber.Constructor = SerialNumber;

  $.fn.wserialNumber.defaults = {
    columnProperty: columnProperty, // 字段属性
    commonProperty: commonProperty, // 公共属性

    // 控件私有属性
    isHide: false, // 是否隐藏
    disabled: false,
    readOnly: false,
    designatedId: '',
    isShowAsLabel: false,
    designatedType: '',
    isOverride: '',
    isSaveDb: '',
    formUuid: '',
    serialNumberTips: '' // 流水号重复提示
  };
})(jQuery);
