/**
 * 表单提供给外部外开的接口, 这里的接口面向的人员定位是: 主要提供给实施人员使用，所以直接暴露[DyformFacade.函数名(参数1,
 * 参数2,...)]出去给实施人员使用 请勿再有其他规范
 */
define(["jquery", "server", "commons", "constant", "appContext", "appModal", "dyform_excel", "api-commons"], function ($, server, commons, constant,
  appContext, appModal, dyformExcel, commonApi) {
  // //////////////////////////////表单接口////////////////////////////////////
  /**
   * 表单接口
   */
  var DyformFacade = {
    // 设置全局命名空间，用于操作多表单业务
    setNamespace: function (namespace) {
      DyformFacade.namespace = namespace;
    },
    // 移除全局命名空间，业务操作完后调用，返回到默认表单
    removeNamespace: function () {
      delete DyformFacade.namespace;
    },
    /**
     * 获取表单jquery对象
     *
     * @param {String}
     *            formId
     */
    get$dyform: function (formId) {
      var $dyform = $.dyform["$dyform"][formId];
      if ($dyform == undefined) {
        if (DyformFacade.namespace) {
          return $.dyform["$dyform"][DyformFacade.namespace];
        }
        // 则默认取其中一个
        for (var i in $.dyform["$dyform"]) {
          $dyform = $.dyform["$dyform"][i];
          break;
        }
      }
      return $dyform;
    }
  }

  // ////////////////////////////通用控件接口///////////////////////////////
  var ControlCommonFacade = {

    /**
     * 主表控件是否存在
     *
     * @param {String}
     *            fileName
     * @param {String}
     *            formId
     */
    isControlExist: function (fileName, dataUuid, formId) {
      var $dyform = DyformFacade.get$dyform(formId);
      return $dyform.isControl(fileName, dataUuid);
    },

    /**
     * 获取控件类型, 返回的值参照dyform_constant.js下的dyFormInputMode
     *
     * @param {String}
     *            fileName
     * @param {String}
     *            formId
     *
     */
    getControlType: function (fieldname, formId) {
      var $dyform = DyformFacade.get$dyform(formId);
      return $dyform.getControl(fieldname).getInputMode();
    },

    /**
     * 获取主表控件或者从表控件 返回获取到的主表控件及从表控件
     *
     * @param {String}
     *            fileName
     * @param {String}
     *            dataUuid
     * @param {String}
     *            formId
     */
    getControl: function (fieldnames, dataUuid, formId) {
      if (Object.prototype.toString.call(fieldnames) === '[object String]') {
        fieldnames = fieldnames.split(",");
      }
      if (typeof fieldnames !== "object") {
        console.error("参数类型错误，第一个参数应为数组类型");
        return false;
      }
      var $dyform = DyformFacade.get$dyform(formId);
      var controlArr = new Array();
      if (StringUtils.isNotBlank(dataUuid)) {
        for (var i = 0; i < fieldnames.length; i++) {
          if (!ControlCommonFacade.isControlExist(fieldnames[i], dataUuid)) {
            console.error("字段名为:[" + fieldnames[i] + "]的附件不存在");
            return false;
          }
          controlArr.push($dyform.getControl(fieldnames[i], dataUuid));// 获取从表控件
        }
        //                return fieldnames.length == 1 ? controlArr[0] : controlArr;
        return controlArr;
      } else {
        for (var i = 0; i < fieldnames.length; i++) {
          if (!ControlCommonFacade.isControlExist(fieldnames[i])) {
            console.error("字段名为:[" + fieldnames[i] + "]的附件不存在");
            return false;
          }
          controlArr.push($dyform.getControl(fieldnames[i])); // 获取主表控件
        }
        //                return fieldnames.length == 1 ? controlArr[0] : controlArr;
        return controlArr;
      }
    },

    /**
     * 判断控件是否为空
     *
     * @param {String}
     *            fileName
     * @param {String}
     *            dataUuid
     * @param {String}
     *            formId
     */
    isControlEmpty: function (fieldname, dataUuid, formId) {
      if (StringUtils.isBlank(fieldname)) {
        console.error("字段名为必填参数，请传入相应控件字段名");
        return;
      }
      var $dyform = DyformFacade.get$dyform(formId);
      var control;
      if (StringUtils.isNotBlank(dataUuid)) {
        control = $dyform.getControl(fieldname, dataUuid);
      } else {
        control = $dyform.getControl(fieldname);
      }
      if (control.getValue() == "") {
        return true;
      } else {
        return false;
      }

    },
    /**
     * 设置控件值
     *
     * @param {String}
     *            fieldname 字段编码
     * @param {Object}
     *            val 控件值
     * @param {String}
     *            dataUuid 数据UUID(如果控件在主表中，则这个参数可以不传,
     *            如果控件在从表中，则这个参数的值为行uuid)
     * @param {String}
     *            formId 主表单据ID
     */
    setControlValue: function (fieldname, val, dataUuid, formId) {
      var $dyform = DyformFacade.get$dyform(formId);
      if (StringUtils.isNotBlank(dataUuid)) {
        control = $dyform.getControl(fieldname, dataUuid);
      } else {
        control = $dyform.getControl(fieldname);
      }
      if (control == null || control == undefined) {
        console.error("fieldname=[" + fieldname + "], dataUuid=[" + dataUuid + "], formId=[" + formId
          + "]的控件不存在");
        return false;
      }

      control.setValue(val);
      return true;
    },

    /**
     * 获取控件值
     *
     * @param {String}
     *            fieldname 字段编码
     * @param {Object}
     *            val 控件值
     * @param {String}
     *            dataUuid 数据UUID(如果控件在主表中，则这个参数可以不传,
     *            如果控件在从表中，则这个参数的值为行uuid)
     * @param {String}
     *            formId 主表单据ID
     * @param {function}
     *            fnSuccesscallback 取值成功回调
     * @param {function}
     *            fnFailCallback 取值失败回调
     */
    getControlValue: function (fieldname, dataUuid, formId, fnSuccesscallback, fnFailCallback) {
      var $dyform = DyformFacade.get$dyform(formId);
      if (StringUtils.isNotBlank(dataUuid)) {
        control = $dyform.getControl(fieldname, dataUuid);
      } else {
        control = $dyform.getControl(fieldname);
      }
      if (control == null || control == undefined) {
        console.error("fieldname=[" + fieldname + "], dataUuid=[" + dataUuid + "], formId=[" + formId
          + "]的控件不存在");
        return false;
      }

      return control.getValue(fnSuccesscallback, fnFailCallback);
    },

    /**
     * 判断控件是否已初始化完成
     *
     * @param {String}
     *            fileName
     * @param {String}
     *            dataUuid
     * @param {String}
     *            formId
     */

    isInitCompleted: function (fieldName, dataUuid, formId) {
      var controlArr = this.getControl([fieldName], dataUuid, formId);
      if (controlArr && controlArr.length > 0) {
        if (controlArr[0].isInitCompleted) {
          return controlArr[0].isInitCompleted();
        } else {
          console.error("字段:[" + fieldName + "]对应的控件不存在方法isInitCompleted");
        }
      } else {
        console.error("字段:[" + fieldName + "]不存在");
      }
    },

    /**
     * 隐藏指定控件
     *
     * @param {String}
     *            fileName
     * @param {String}
     *            dataUuid
     * @param {String}
     *            formId
     */

    setControlHide: function (fieldnames, dataUuid, formId) {
      var controlArr = this.getControl(fieldnames, dataUuid, formId);
      controlArr.forEach(function (control) {
        control.setVisible(false);
      })
    },
    /**
     * 隐藏主表指定控件的行
     *
     * @param {String}
     *            fieldNames
     * @param {String}
     *            formId
     */

    setControlRowHide: function (fieldNames, formId) {
      var $dyform = DyformFacade.get$dyform(formId);
      fieldNames.forEach(function (fieldName) {
        $dyform.setControlRowHide(fieldName);
      })
    },
    /**
     * 显示主表指定控件的行
     *
     * @param {String}
     *            fileNames
     * @param {String}
     *            formId
     */

    setControlRowShow: function (fieldNames, formId) {
      var $dyform = DyformFacade.get$dyform(formId);
      fieldNames.forEach(function (fieldName) {
        $dyform.setControlRowShow(fieldName);
      })
    },
    /**
     * 隐藏主表指定控件的列
     *
     * @param {String}
     *            fileNames
     * @param {String}
     *            formId
     */

    setControlCellHide: function (fieldNames, formId) {
      var $dyform = DyformFacade.get$dyform(formId);
      fieldNames.forEach(function (fieldName) {
        $dyform.setControlCellHide(fieldName);
      })
    },
    /**
     * 显示主表指定控件的列
     *
     * @param {String}
     *            fileNames
     * @param {String}
     *            formId
     */


    setControlCellShow: function (fieldNames, formId) {
      var $dyform = DyformFacade.get$dyform(formId);
      fieldNames.forEach(function (fieldName) {
        $dyform.setControlCellShow(fieldName);
      })
    },
    /**
     * 隐藏指定控件
     *
     * @param {String}
     *            fileName
     * @param {String}
     *            dataUuid
     * @param {String}
     *            formId
     */

    setControlHideByFieldName: function (fieldNames, dataUuid, formId) {
      var $dyform = DyformFacade.get$dyform(formId);
      fieldNames.forEach(function (fieldName) {
        $dyform.setFieldAsHiddenByFieldName(fieldName, dataUuid);
      })
    },
    /**
     * 显示指定控件
     *
     * @param {String}
     *            fileName
     * @param {String}
     *            dataUuid
     * @param {String}
     *            formId
     */
    setControlShowByFieldName: function (fieldNames, dataUuid, formId) {
      var $dyform = DyformFacade.get$dyform(formId);
      fieldNames.forEach(function (fieldName) {
        $dyform.setFieldAsShowByFieldName(fieldName, dataUuid);
      })
    },

    /**
     * 展示指定控件
     *
     * @param {String}
     *            fileName
     * @param {String}
     *            dataUuid
     * @param {String}
     *            formId
     */
    setControlShow: function (fieldnames, dataUuid, formId) {
      var controlArr = this.getControl(fieldnames, dataUuid, formId);
      controlArr.forEach(function (control) {
        control.setVisible(true);
      })
    },
    /**
     * 设置主表控件为可编辑
     *
     * @param {Array}
     *            fieldNames
     * @param {String}
     *            formId
     */
    setControlEditable: function (fieldnames, dataUuid, formId) {
      var controlArr = this.getControl(fieldnames, dataUuid, formId);
      controlArr.forEach(function (control) {
        var isEnable = control.isEnable();
        control.setEditable(isEnable);
      })
    },
    /**
     * 设置主表控件为文本
     *
     * @param {Array}
     *            fieldNames
     * @param {String}
     *            formId
     */
    setControlLabel: function (fieldnames, dataUuid, formId) {
      var controlArr = this.getControl(fieldnames, dataUuid, formId);
      controlArr.forEach(function (control) {
        control.setDisplayAsLabel();
      })
    },

    /**
     * 设置指定控件必填
     */
    setControlRequire: function (fieldnames, dataUuid, formId) {
      var controlArr = this.getControl(fieldnames, dataUuid, formId);
      controlArr.forEach(function (control) {
        control.setRequired(true);
      })
    },

    /**
     * 设置指定控件不用必填
     *
     * @param {String}
     *            fileName
     * @param {String}
     *            dataUuid
     * @param {String}
     *            formId
     */
    setControlNorequire: function (fieldnames, dataUuid, formId) {
      var controlArr = this.getControl(fieldnames, dataUuid, formId);
      controlArr.forEach(function (control) {
        control.setRequired(false);
      })
    },

    /**
     * 设置从表为不可编辑状态
     *
     * @param {String}
     *            fileName
     * @param {String}
     *            formId
     */
    setSubFormDisable: function (subFormId, formId) {
      console.warn("setSubFormDisable Deprecated")
      ControlCommonFacade.setSubFormReadOnly(subFormId, formId);
    },
    setSubFormReadOnly: function (subFormId, formId) {
      var $dyform = DyformFacade.get$dyform(formId);
      var subformUuid = $dyform.getFormUuid(subFormId);
      $dyform.getSubformControl(subformUuid).setReadOnly();
    },

    /**
     * 设置从表为编辑状态
     *
     * @param {String}
     *            fileName
     * @param {String}
     *            formId
     */
    setSubFormEditable: function (subFormId, formId) {
      var $dyform = DyformFacade.get$dyform(formId);
      var subformUuid = $dyform.getFormUuid(subFormId);
      $dyform.getSubformControl(subformUuid).setEditable();
    },

    /**
     * 设置从表显示
     */
    setSubFormShow: function (subFormId, formId) {
      var $dyform = DyformFacade.get$dyform(formId);
      var subformUuid = $dyform.getFormUuid(subFormId);
      $dyform.getSubformControl(subformUuid).show();
    },
    /**
     * 设置从表隐藏
     */
    setSubFormHide: function (subFormId, formId) {
      var $dyform = DyformFacade.get$dyform(formId);
      var subformUuid = $dyform.getFormUuid(subFormId);
      $dyform.getSubformControl(subformUuid).hide();
    },
    getTableView: function (widgetId) {
      return $("#" + widgetId).data('uiWBootstrapTable');
    },

    /**
     * 设置区块的隐藏
     *
     * @param {String}
     *            fileName
     * @param {String}
     *            formId
     */
    setBlockHide: function (blockCode, formId) {
      var $dyform = DyformFacade.get$dyform(formId);
      $dyform.hideBlock(blockCode);
    },

    /**
     * 设置区块展示
     *
     * @param {String}
     *            fileName
     * @param {String}
     *            formId
     */

    setBlockShow: function (blockCode, formId) {
      var $dyform = DyformFacade.get$dyform(formId);
      $dyform.showBlock(blockCode);
    },

    /**
     * 对日期控件设置最小值
     * minDate格式为格式为“yyyy-MM-dd”
     */
    setMinDate: function (fieldName, minDate, dataUuid) {
      var $dyform = DyformFacade.get$dyform();
      if (StringUtils.isNotBlank(dataUuid)) {
        control = $dyform.getControl(fieldName, dataUuid);
      } else {
        control = $dyform.getControl(fieldName);
      }
      if (control == null || control == undefined) {
        console.error("fieldname=[" + fieldName + "], dataUuid=[" + dataUuid + "]的控件不存在");
        return false;
      }
      control.setMinDate(minDate);
    },
    /**
     * 对日期控件设置最大值
     * maxDate格式为格式为“yyyy-MM-dd”
     */
    setMaxDate: function (fieldName, maxDate, dataUuid) {
      var $dyform = DyformFacade.get$dyform();
      if (StringUtils.isNotBlank(dataUuid)) {
        control = $dyform.getControl(fieldName, dataUuid);
      } else {
        control = $dyform.getControl(fieldName);
      }
      if (control == null || control == undefined) {
        console.error("fieldname=[" + fieldName + "], dataUuid=[" + dataUuid + "]的控件不存在");
        return false;
      }
      control.setMaxDate(maxDate);
    },
    /**
     * 设置下拉框选项内容
     *
     * @param {String}
     *            fileName
     * @param {String}
     *            dataUuid
     * @param {String}
     *            formId
     */

    setSelectOption: function (fieldnames, options, dataUuid, formId) {
      var controlArr = this.getControl(fieldnames, dataUuid, formId);
      controlArr.forEach(function (control) {
        control.setOptionSet(options);
        control.reRender();
        control.initAsWellSelect();
      })
    },
    /**
     * 判断过滤条件是否唯一
     * true 为 唯一，false 为不唯一
     * @param uuid 表单uuid
     * @param formUuid 表单formUuid
     * @param fieldKeyValues 表单唯一性判断字段组合（字段名，字段值）
     * @param isFiterCondition 过滤范围（D：部门，C:当前用户 ，O：组织单位，A：全部）
     * @return Boolean
     */
    isUniqueForFields: function (uuid, formId, fieldKeyValues, isFiterCondition) {
      var isUnique;
      var JDS = server.JDS;
      var formUuid = this.getFormUuid(formId);
      JDS.call({
        service: "dyFormFacade.isUniqueForFields",
        async: false,
        data: [uuid, formUuid, fieldKeyValues, isFiterCondition],
        success: function (result) {
          isUnique = result.data;
        },
        error: function (data) {
          appModal.error("组合字段唯一性校验出错!");
          return;
        }
      });
      return isUnique;
    },
    /**
     * 判断当前用户是否有指定类型的功能权限
     */
    isGranted: function (path, functionType) {
      var valResult;
      $.ajax({
        type: "POST",
        url: ctx + "/security/user/details/isGranted",
        data: {
          "path": path,
          "functionType": functionType
        },
        async: false,
        success: function (data) {
          valResult = data;
        },
        error: function (data) {
          appModal.error("验证指定类型功能权限失败!");
          return;
        }
      });
      return valResult;
    },
    /**
     * 判断当前用户是否有指定角色的功能权限
     */
    hasRole: function (roleId) {
      if (server.SpringSecurityUtils.hasRole(roleId)) {
        return true;
      } else {
        return false;
      }
    },
    /**
     * 传递日期和天数，返回指定日期多少天前后的日期
     */
    getDateByDateDaysParam: function (date, days, param) {
      return commons.DateUtils.getDateByDateDaysParam(date, days, param);
    },
    /**
     * 传递日期和周数，返回指定日期多少周前后的日期
     */
    getDateByDateWeeksParam: function (date, weeks, param) {
      return commons.DateUtils.getDateByDateWeeksParam(date, weeks, param);
    },
    /**
     * 传递日期和天数，返回指定日期多少个月前后的日期
     */
    getDateByDateMonthsParam: function (date, months, param) {
      return commons.DateUtils.getDateByDateMonthsParam(date, months, param);
    },
    /**
     * 传递日期和年数，返回指定日期多少年前后的日期
     */
    getDateByDateYearsParam: function (date, years, param) {
      return commons.DateUtils.getDateByDateYearsParam(date, years, param);
    },
    /**
     * 传递日期和格式，返回指定格式日期
     */
    getDateStrByDateAndFormat: function (date, format) {
      return commons.DateUtils.getDateStrByDateAndFormat(date, format);
    },
    /**
     * 传递日期字符串和格式，返回指定日期
     */
    getSpecificDateByDateFormat: function (date, format) {
      return commons.DateUtils.getSpecificDateByDateFormat(date, format);
    },
    /**
     * 传递日期，得到星期几
     */

    getWeekDayByDate: function (date) {
      var newDate = new Date(date);
      return "星期" + "日一二三四五六".charAt(newDate.getDay());
    },
    /**
     * 传递日期和工作日天数，返回指定日期多少个工作日前后的日期
     */
    getSpecificDateByDatewkhrParam: function (date, workHour, param) {
      return commons.DateUtils.getSpecificDateByDatewkhrParam(date, workHour, param);
    },
    /**
     * 获取表单字段的附件ID
     *
     * @param {String}
     *            fieldName
     * @param {String}
     *            formId
     */
    getFileIds: function (fieldName, formId) {
      var $dyform = DyformFacade.get$dyform();
      var logicFiles = [];
      if (formId) {
        var formInfo = {
          id: formId,
          fnSuccessCallback: function (rowDatas) {
            if (rowDatas == null || rowDatas.length <= 0) {
              return;
            }
            for (var i = 0; i < rowDatas.length; i++) {
              var values = rowDatas[i][fieldName];
              if ($.isArray(values) && values.length > 0) {
                logicFiles = logicFiles.concat(values);
              }
            }
          },
        }
        $dyform.getAllRowData(formInfo, null);
      } else {
        var values = $dyform.getFieldValueByFieldName(fieldName);
        if ($.isArray(values) && values.length > 0) {
          logicFiles = logicFiles.concat(values);
        }
      }
      return logicFiles;
    },
    /**
     * 下载附件ID
     *
     * @param {String}
     *            fileIds
     * @param {String}
     *            fileName
     */
    downFileIds: function (fileIds, fileName) {
      server.FileDownloadUtils.downloadMongoFile({
        fileId: fileIds,
        fileName: fileName,
        batchCompression: true
      })
      //            $.ajax({
      //                type: "POST",
      //                data: {
      //                    fileIDs: fileIds || [],
      //                    fileName: fileName,
      //                },
      //                // dataType : "json",
      //                // contentType : "application/json",
      //                url: ctx + "/repository/file/mongo/downAllFiles",
      //            })
    },
    /**
     * 往附件列表中增加图标
     *
     */
    addFileIcon: function (fieldName, fileId, iconUrl, dataUuid, formId) {
      var $dyform = DyformFacade.get$dyform();
      if (StringUtils.isNotBlank(dataUuid)) {
        control = $dyform.getControl(fieldName, dataUuid);
      } else {
        control = $dyform.getControl(fieldName);
      }
      if (control == null || control == undefined) {
        console.error("fieldname=[" + fieldName + "], dataUuid=[" + dataUuid + "], formId=[" + formId
          + "]的控件不存在");
        return false;
      }
      control.addFilesIcon(fileId, iconUrl);
    },
    /**
     * 隐藏从表控件
     *
     * @param {String} formUuid
     *
     * 从表formUuid
     *
     */
    hideSubFormByFormUuid: function (formUuid, mainFormId) {
      if (_.isEmpty(formUuid)) {
        console.log("formUuid is null");
        return;
      }
      var $dyform = DyformFacade.get$dyform(mainFormId);
      if (_.isEmpty($dyform)) {
        $dyform = DyformFacade.get$dyform();
      }
      $dyform.hideSubFormByFormUuid(formUuid);
    },
    /**
     * 隐藏从表 1
     *
     * @param formId
     * @param mainFormId
     */
    hideSubForm: function (formId, mainFormId) {
      if (_.isEmpty(formId)) {
        console.log("formId is null");
        return;
      }
      var $dyform = DyformFacade.get$dyform(mainFormId);
      if (_.isEmpty($dyform)) {
        $dyform = DyformFacade.get$dyform();
      }
      $dyform.hideSubForm(formId);

    },
    /**
     * 获取从表的所有行数据
     *
     * @param formInfo
     *            {id:""}
     */
    getSubFormAllRowData: function (formInfo, mainFormId) {
      if (_.isEmpty(formInfo)) {
        console.log("formInfo is null");
        return;
      }
      var $dyform = DyformFacade.get$dyform(mainFormId);
      return $dyform.getAllRowData(formInfo);
    },
    /**
     * 设置从表整列只读
     *
     * @param formId
     * @param mappingName
     */
    setSubFormColumnReadOnly: function (formId, fieldName, mainFormId) {
      if (_.isEmpty(formId)) {
        console.log("formId is null");
        return;
      }
      if (_.isEmpty(fieldName)) {
        console.log("mappingName is null");
        return;
      }
      var $dyform = DyformFacade.get$dyform(mainFormId);

      $dyform.setColumnReadOnly(formId, fieldName);

    },
    /**
     *
     * 设置整个从表只读
     *
     * @param formUuid
     */
    setSubFormReadOnlyByFormUuid: function (formUuid, mainFormId) {
      if (_.isEmpty(formUuid)) {
        console.log("formUuid is null");
        return;
      }
      var $dyform = DyformFacade.get$dyform(mainFormId);
      $dyform.setSubformReadOnlyByFormUuid(formUuid);
    },
    /**
     * 隐藏列(根据字段名)
     *
     * @param formId
     * @param mappingName
     * @param mainFormId
     */
    hideSubFormColumnByFieldName: function (formId, fieldName, mainFormId) {
      if (_.isEmpty(formId)) {
        console.log("formId is null");
        return;
      }
      if (_.isEmpty(fieldName)) {
        console.log("fieldName is null");
        return;
      }
      var $dyform = DyformFacade.get$dyform(mainFormId);
      $dyform.hideColumnByFieldName(formId, fieldName);
    },
    /**
     * 显示列
     *
     * @param formId
     * @param fieldName
     */
    showSubFormColumnByFieldName: function (formId, fieldName, mainFormId) {
      if (_.isEmpty(formId)) {
        console.log("formId is null");
        return;
      }
      if (_.isEmpty(fieldName)) {
        console.log("fieldName is null");
        return;
      }
      var $dyform = DyformFacade.get$dyform(mainFormId);
      $dyform.showColumnByFieldName(formId, fieldName);

    },
    /**
     * 通过从表的行ID获取对应的从表formUuid
     *
     * @param rowId
     * @return null
     */
    getSubFormRecordByRowId: function (rowId) {
      //            var $dyform = DyformFacade.get$dyform(mainFormId);
      //            var formDatas = $dyform.getFormDatas();
      //            for (var i in formDatas) {
      //                var records = formDatas[i];
      //                for (var index = 0; index < records.length; index++) {
      //                    if (records[index] == rowId) {
      //                        return records[index];
      //                    }
      //                }
      //            }
      //            return null;
    },
    /**
     * 通过从表的formUuid获取对应的从表行 RowIDs
     *
     * @param rowId
     * @return Array
     */
    getSubFormRowIds: function (formUuid, mainFormId) {
      if (_.isEmpty(formUuid)) {
        console.log("formUuid is null");
        return;
      }
      var $dyform = DyformFacade.get$dyform(mainFormId);
      return $dyform.getRowIds(formUuid);
    },
    /**
     * 添加行 by formId 1
     * isCopy 是否复制 Boolean值
     */
    addRowData: function (mainFormId, formUuid, data, dysubform, isCopy) {
      var cacheCtl = {};
      for (var i in $.ControlManager) {
        cacheCtl[i] = $.ControlManager[i];
      }

      if (_.isEmpty(mainFormId)) {
        console.log("mainFormId is null");
        return;
      }
      if (_.isEmpty(formUuid)) {
        console.log("formUuid is null");
        return;
      }
      data = data || {};
      if (_.isEmpty(data.id) || isCopy) {
        $.extend(data, {
          newRow: true,
          id: commons.UUID.createUUID()
        });
      }
      var $dyform = DyformFacade.get$dyform(mainFormId);
      if (isCopy) {
        $dyform.copyRowDataByFormUuid(formUuid, data);
      } else if (dysubform && dysubform.getSubformConfig().editMode == '2') {
        console.log('editSubformRowDataInDialog formUuid:' + formUuid + ' data:' + data);
        $dyform.editSubformRowDataInDialog(formUuid, dysubform.getSubformConfig(), data)
      } else {
        console.log('addRowDataByFormUuid formUuid:' + formUuid + ' data:' + data);
        $dyform.addRowDataByFormUuid(formUuid, data);
      }

      //触发从表控件验证
      for (var ctlName in $.ControlManager) {
        if (cacheCtl[ctlName]) {
          continue;
        }
        var ctrlObj = $.ControlManager[ctlName];
        var control = ctrlObj && ctrlObj.control;
        if (control && control.initValidate) {
          control.initValidate(true);
        }
      }
    },
    /**
     * 获取uuid by formId 1
     */
    getFormUuid: function (formId) {
      var $dyform = DyformFacade.get$dyform(formId);
      console.log('getFormUuid formId:' + formId);
      return $dyform.getFormUuid();
    },
    /**
     * 修改行 by formUuid
     */
    updateRowData: function (mainFormId, formUuid, data) {
      if (_.isEmpty(mainFormId)) {
        console.log("mainFormId is null");
        return;
      }
      if (_.isEmpty(formUuid)) {
        console.log("formUuid is null");
        return;
      }
      var $dyform = DyformFacade.get$dyform(mainFormId);
      console.log('updateRowData formUuid:' + formUuid + ' data:' + data);
      $dyform.updateRowDataByFormUuid(formUuid, data);
    },
    /**
     * 刪除行 1
     */
    deleteRowData: function (mainFormId, formUuid, selectRowId) {
      if (_.isEmpty(mainFormId)) {
        console.log("mainFormId is null");
        return;
      }
      if (_.isEmpty(formUuid)) {
        console.log("formUuid is null");
        return;
      }
      if (_.isEmpty(selectRowId)) {
        appModal.error('请先选择记录')
        console.log("selectRowId is null");
        return;
      }
      var $dyform = DyformFacade.get$dyform(mainFormId);
      if (_.isArray(selectRowId)) {
        _.forEach(selectRowId, function (rowId) {
          // console.log('delete row ' + rowId);
          $dyform.deleteRowDataByFormUuid(formUuid, rowId);
        });
      } else {
        $dyform.deleteRowDataByFormUuid(formUuid, selectRowId);
      }

    },
    /**
     * 获取选中的行Data
     */
    getSelectedRowData: function (mainFormId, formUuid, fnSuccess, fnError) {
      if (_.isEmpty(mainFormId)) {
        console.log("mainFormId is null");
        return;
      }
      if (_.isEmpty(formUuid)) {
        console.log("formUuid is null");
        return;
      }
      var $dyform = DyformFacade.get$dyform(mainFormId);
      return $dyform.getSelectedRowDataByFormUuid(formUuid, fnSuccess, fnError);
    },
    /**
     * 获取选中的行 RowId
     */
    getSelectedRowId: function (mainFormId, formUuid) {
      if (_.isEmpty(mainFormId)) {
        console.log("mainFormId is null");
        return;
      }
      if (_.isEmpty(formUuid)) {
        console.log("formUuid is null");
        return;
      }
      var $dyform = DyformFacade.get$dyform(mainFormId);
      return $dyform.getSelectedRowId(formUuid);
    },
    /**
     * 上移 1
     */
    upSubFormRowData: function (mainFormId, formUuid, selectRowId) {
      if (_.isEmpty(mainFormId)) {
        console.log("mainFormId is null");
        return;
      }
      if (_.isEmpty(formUuid)) {
        console.log("formUuid is null");
        return;
      }
      if (_.isEmpty(selectRowId)) {
        appModal.info("请先选中记录");
        console.log("selectRowId is null");
        return;
      }
      var $dyform = DyformFacade.get$dyform(mainFormId);
      $dyform.upSubformRowData(formUuid, selectRowId);
    },
    /**
     * 下移 1
     */
    downSubFormRowData: function (mainFormId, formUuid, selectRowId) {
      if (_.isEmpty(mainFormId)) {
        console.log("mainFormId is null");
        return;
      }
      if (_.isEmpty(formUuid)) {
        console.log("formUuid is null");
        return;
      }
      if (_.isEmpty(selectRowId)) {
        appModal.info("请先选中记录");
        console.log("selectRowId is null");
        return;
      }
      var $dyform = DyformFacade.get$dyform(mainFormId);
      $dyform.downSubformRowData(formUuid, selectRowId);
    },
    /**
     * 清空丛表的所有空行
     */
    clearSubFormBlankRow: function (mainFormId, formUuid) {
      if (_.isEmpty(mainFormId)) {
        console.log("mainFormId is null");
        return;
      }
      if (_.isEmpty(formUuid)) {
        console.log("formUuid is null");
        return;
      }
      var $dyform = DyformFacade.get$dyform(mainFormId);
      console.log(" clearSubformBlankRow " + formUuid);
      $dyform.clearSubformBlankRow(formUuid);
    },
    /**
     * 从表导入 1
     */
    subFormExcelImp4MainForm: function (formEl) {
      if (_.isEmpty(formEl)) {
        console.log("formEl is null");
        return;
      }
      window.excelImp4MainForm(formEl);
    },
    /**
     * 从表导出 1
     */
    subFormExcelExp4MainFormEx: function (formEl, formId) {
      if (_.isEmpty(formEl)) {
        console.log("formEl is null");
        return;
      }
      if (_.isEmpty(formId)) {
        console.log("formId is null");
        return;
      }
      window.excelExp4MainFormEx(formEl, formId);
    },
    /**
     * 添加子行
     */
    addSubformRowDataInJqGrid: function (mainFormId, formUuid, selectRowId) {
      if (_.isEmpty(mainFormId)) {
        console.log("mainFormId is null");
        return;
      }
      if (_.isEmpty(formUuid)) {
        console.log("formUuid is null");
        return;
      }
      if (_.isEmpty(selectRowId)) {
        console.log("selectRowId is null");
        return;
      }
      var $dyform = DyformFacade.get$dyform(mainFormId);
      $dyform.addSubformRowDataInJqGrid(formUuid, selectRowId);
    }


  };

  $.extend(true, DyformFacade, ControlCommonFacade);// 这句是为了遵循统一规范,统一通过DyformFacade对外暴露

  // ////////////////////////////文本控件接口///////////////////////////////
  var TextControlFacade = {}
  $.extend(true, DyformFacade, TextControlFacade);// 这句是为了遵循统一规范,统一通过DyformFacade对外暴露

  // ////////////////////////////附件控件接口///////////////////////////////
  var FileUploadFacade = {}
  $.extend(true, DyformFacade, FileUploadFacade);// 这句是为了遵循统一规范,统一通过DyformFacade对外暴露

  $.extend(true, DyformFacade, new commonApi());

  window.DyformFacade = DyformFacade;// 统一规范: 通过DyformFacade对外暴露二开接口,
  // 不得再有其他限定名规范

  return DyformFacade;
});
