define(['mui', 'constant', 'commons', 'server', 'mui-DyformField'], function ($, constant, commons, server, DyformField) {
  // 流水号
  var JDS = server.JDS;
  var wSerialNumber = function ($placeHolder, options) {
    DyformField.apply(this, arguments);
  };
  var getEditableSerialNumber = function (designatedId, designatedType, isOverride, formUuid, columnName, ctlName) {
    $.alert('可编辑流水号还未实现');
    return '';
  };
  commons.inherit(wSerialNumber, DyformField, {
    render: function () {
      var that = this;
      if (that.$editableElem != null) {
        // 创建可编辑框
        return;
      }
      that._superApply(arguments);
      var options = that.options.fieldDefinition;
      var ctlName = that.getName();
      var inputMode = options.inputMode;

      if (inputMode == dyFormInputMode.serialNumber) {
        that.$editableElem[0].classList.add('input-search'); // css in
        // wellnewoa.css
      }

      var formUuid = that.getFormUuid();
      var element = that.$editableElem[0];
      var val = element.value;
      if (dyFormInputMode.serialNumber == inputMode) {
        // 可编辑流水号
        var designatedId_ = options.designatedId;
        var designatedType_ = options.designatedType;
        var isOverride_ = options.isOverride;
        element.addEventListener('click', function () {
          var snValue = getEditableSerialNumber(
            designatedId_,
            designatedType_,
            parseInt(isOverride_),
            formUuid,
            options.columnName,
            ctlName
          );
          // element.val(snValue);
          that.setValue(snValue);
        });
        // 根据show类型展示
        // TODO that.displayByShowType();
      } else {
        that.setDisplayAsLabel();
      }
      if (that.getValue() == '' || that.getValue() == undefined) {
        // 设置默认值
        that.setValue(options.defaultValue);
      }
      // 绑定事件
      that._bindEvents();
    },
    /* 设值到标签中 */
    setValue2LabelElem: function () {
      var that = this;
      that._setUnEditSerialNumber();
      that._superApply(arguments);
    },

    _setUnEditSerialNumber: function () {
      var that = this;
      var options = that.options.fieldDefinition;
      // 判断是否应用于流程流水号，是则不产生流水号
      if (options.applyTo == applyToOptions.SERIAL_NO) {
        return;
      }

      var formUuid = that.getFormUuid();
      var val = that.getValue();
      var unEditDesignatedId = options.designatedId;
      var unEditIsSaveDb = options.isSaveDb;

      /** ************************************************************************不可编辑流水号对外调用接口********************************************************************************************************** */
      // 不可编辑流水号调用方法
      // serialNumberId： 流水号id
      // isOccupied: 判断该流水号是否强制保留进数据库,不保存则为临时流水号，true：保存；false：不保存）
      // formUuid: 该表单的uuid
      // field: 存放流水号的列名
      var getNotEditableSerialNumberForDytable = function (serialNumberId, isOccupied, formUuid, field) {
        var NotEditableSerialNumber;
        JDS.call({
          service: 'serialNumberService.getNotEditorSerialNumberForDytable',
          data: [serialNumberId, isOccupied, formUuid, field],
          async: false,
          success: function (result) {
            NotEditableSerialNumber = result.data;
            return result.data;
          }
        });
        return NotEditableSerialNumber;
      };
      /** *************************************************************************************************************************************************************************************************************** */
      if (val == '') {
        var notEditIsSaveDb = unEditIsSaveDb == '0' ? true : false;
        var snValue = getNotEditableSerialNumberForDytable(unEditDesignatedId, notEditIsSaveDb, formUuid, options.name);
        that.setValue(snValue);
      }
    },
    /**
     * 按照流水号ID生成流水
     * @param id
     */
    generateSerialNumber: function (id, isOccupied, renderParams) {
      var formUuid = this.dyform.formData.uuid;
      var field = this.definition.name;
      var serialNumber = '';
      var _this = this;
      if (id == null || id == undefined || id == '') {
        console.error('流水号ID为空');
        return '';
      }
      JDS.call({
        service: 'serialNumberService.generateSerialNumber',
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
          serialNumber = result.data;
        }
      });
      return serialNumber;
    },

    // modify by wujx 设置自动生成流水号
    _setSerialNumberAndOccupy: function () {
      // 判断是否应用于流程流水号，是则不产生流水号
      if (this.definition.applyTo == applyToOptions.SERIAL_NO) {
        return;
      }
      var options = this.options;
      var val = this.value;
      var unEditDesignatedId = options.fieldDefinition.designatedId;
      var unEditIsSaveDb = options.fieldDefinition.isSaveDb;

      if (val == '') {
        var notEditIsSaveDb = unEditIsSaveDb == '0' ? true : false;
        var snValue = this.generateSerialNumber(unEditDesignatedId, notEditIsSaveDb);
        this.setValue(snValue);
        // if (!this.validate()) {
        //   //验证流水是否重复等规则
        //   this.value = ''; //清空流水
        //   this._setSerialNumberAndOccupy();
        // }
      }
    },

    initControlOver: function ($dyform) {
      var _this = this;
      if (this.value == '') {
        if (dyFormInputMode.serialNumber != this.definition.inputMode) {
          //自动生成流水
          this._setSerialNumberAndOccupy();
          if (this.definition.showAsLabel == '2') {
            this.displayByShowType();
          }
        }
      }
    }
  });
  return wSerialNumber;
});
