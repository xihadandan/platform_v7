/**
 * 控件管理类，主要负责控件的动态创建以及控件的实例对象获取。
 */
(function ($) {
  // $.ControlManager || 不允许去掉，否则在表单中使用视图时，会导致存放在$.ControlManager的表单控件丢失 by
  // wujx 20161019
  $.ControlManager = $.ControlManager || {
    /**
     * 控件创建,控件的id保存于占位符的name中
     *
     * @param $placeHolder
     *            该控件的占位符
     * @param column
     *            控件配置
     * @param formDefinition
     */
    createCtl: function (params) {
      var $placeHolder = params.$placeHolder,
        column = params.fieldDefinition,
        formDefinition = params.formDefinition,
        $currentForm = params.$currentForm;
      var subformConfig = params.subformConfig;
      if (typeof $currentForm == 'undefined') {
        $currentForm = element.closest('.dyform');
      }

      if (typeof $placeHolder == 'undefined' || $placeHolder == null || $placeHolder.size() == 0) {
        console.error('placeholder not defined');
        return null;
      }

      var ctlName = $placeHolder.attr('name');
      if (typeof ctlName == 'undefined' || ctlName.length == 0) {
        console.error('placeholer must have a name property ,used to save the control id');
        return null;
      }

      // $placeHolder.hide();
      var ctlType = '',
        ns;

      // var
      // appendhtml=getHtmlStrByInputDataType(inputMode,fieldcode,'_');
      // $("input[name='"+fieldcode+"']").after(appendhtml.html);
      // initDyControl(ctlName,appendhtml.formfieldcode,column,formDefinition);
      // // (单选框、复选框、开关按钮)
      var defaultReadStyle = $.inArray(column.inputMode, ['17', '18', '128']) > -1 ? '3' : '2';
      var columnProperty = {
        // 控件字段属性
        applyTo: column.applyTo, // 应用于
        controlName: ctlName, // 控件名称，由外面指定
        columnName: column.name, // 字段定义 fieldname
        displayName: column.displayName, // 描述名称 descname
        dbDataType: column.dbDataType, // 字段类型 datatype type
        indexed: column.indexed, // 是否索引
        showed: column.showed, // 是否界面表格显示
        sorted: column.sorted, // 是否排序
        sysType: column.sysType, // 系统定义类型，包括三种（0：系统默认，1：管理员常量定义，2：表单添加后自定义）
        length: column.length, // 长度
        isHide: column.isHide, // 是否隐藏
        showType: column.showType === '4' ? '3' : column.showType, // 显示类型 1,2,3,4 datashow (*禁用选项被取消，改为只读*)
        readStyle: column.readStyle || defaultReadStyle,
        validateOnHidden: column.validateOnHidden === true || column.validateOnHidden === 'true',
        defaultValue: column.defaultValue, // 默认值
        valueCreateMethod: column.valueCreateMethod, // 默认值创建方式
        formatNumber: column.formatNumber, // 默认值创建方式
        // 1用户输入
        onlyreadUrl: column.onlyreadUrl, // 只读状态下设置跳转的url
        realDisplay: column.realDisplay,
        formDefinition: formDefinition,
        subformConfig: subformConfig, // 所属从表配置
        pos: column.pos,
        data: column.data,
        dataUuid: column.dataUuid,
        relativeMethod: column.relativeMethod,
        allowInput: column.allowInput,
        decimal: column.decimal,
        precision: column.precision,
        scale: column.scale,
        events: column.events,
        uninit: !!params.uninit
      };
      if ($.trim((ns = $placeHolder.attr('data-ns'))).length > 0) {
        columnProperty.ns = ns; // 控件命名空间
      }
      // 控件公共属性
      var commonProperty = {
        inputMode: column.inputMode, // 输入样式 控件类型
        // inputDataType
        fieldCheckRules: column.fieldCheckRules,
        unitUnique: column.unitUnique,
        fontSize: column.fontSize, // 字段的大小
        fontColor: column.fontColor, // 字段的颜色
        ctlWidth: column.ctlWidth, // 宽度
        ctlHight: column.ctlHight, // 高度
        textAlign: column.textAlign,
        customizedRegularText: column.customizedRegularText, //自定义正则表达式
        validateEventManage: column.validateEventManage, //事件管理
        noNullValidateReminder: column.noNullValidateReminder, //非空提示
        uniqueValidateReminder: column.uniqueValidateReminder, //唯一校验提示
        regularValidateReminder: column.regularValidateReminder, //正则校验提示
        eventValidateReminder: column.eventValidateReminder //正则校验提示

        // 对齐方式
      };
      var inputMode = commonProperty.inputMode;

      var control = null;
      // radio
      if (inputMode == dyFormInputMode.radio) {
        control = $placeHolder.wradio({
          columnProperty: columnProperty,
          commonProperty: commonProperty,
          optionDataSource: column.optionDataSource, // 备选项来源1:常量,2:字段
          optionSet: column.optionSet,
          dictCode: column.dictCode,
          radioStatus: column.radioStatus,
          selectDirection: column.selectDirection,
          lazyLoading: column.lazyLoading,
          dataSourceId: column.dataSourceId,
          dataSourceFieldName: column.dataSourceFieldName,
          dataSourceDisplayName: column.dataSourceDisplayName,
          dataSourceGroup: column.dataSourceGroup,
          defaultCondition: column.defaultCondition,
          radioShape: column.radioShape,
          radioCancel: column.radioCancel,
          selectAlign: column.selectAlign,
          $currentForm: $currentForm,
          naked: column.naked,
          optionDataAutoSet: column.optionDataAutoSet,
          relateField: column.relateField,
          dataOptsList: column.dataOptsList
        });
        ctlType = 'wradio';
        // checkbox
      } else if (inputMode == dyFormInputMode.checkbox) {
        control = $placeHolder.wcheckBox({
          columnProperty: columnProperty,
          commonProperty: commonProperty,
          ctrlField: column.ctrlField,
          optionDataSource: column.optionDataSource, // 备选项来源1:常量,2:字段
          optionSet: column.optionSet,
          dictCode: column.dictCode,
          lazyLoading: column.lazyLoading,
          dataSourceId: column.dataSourceId,
          dataSourceFieldName: column.dataSourceFieldName,
          dataSourceDisplayName: column.dataSourceDisplayName,
          dataSourceGroup: column.dataSourceGroup,
          defaultCondition: column.defaultCondition,
          selectMode: column.selectMode, // 选择模式，单选1，多选2
          singleCheckContent: column.singleCheckContent, // 单选// 选中内容
          singleUnCheckContent: column.singleUnCheckContent, // 单选// 取消选中内容
          selectDirection: column.selectDirection,
          checkboxAll: column.checkboxAll,
          checkboxMin: column.checkboxMin,
          checkboxMax: column.checkboxMax,
          selectAlign: column.selectAlign,
          $currentForm: $currentForm,
          naked: column.naked,
          optionDataAutoSet: column.optionDataAutoSet,
          relateField: column.relateField,
          dataOptsList: column.dataOptsList
        });
        ctlType = 'wcheckBox';
      } else if (inputMode == dyFormInputMode.number) {
        // numbertext
        columnProperty.minNum = column.minNum;
        columnProperty.maxNum = column.maxNum;
        control = $placeHolder.wnumberInput({
          columnProperty: columnProperty,
          commonProperty: commonProperty,
          numberRange: column.numberRange,
          decimal: column.decimal,
          negative: column.negative,
          operator: column.operator,
          $currentForm: $currentForm,
          naked: column.naked
        });

        ctlType = 'wnumberInput';
      } else if (inputMode == dyFormInputMode.text) {
        columnProperty.placeholder = column.placeholder;
        columnProperty.isPasswdInput = column.isPasswdInput;
        columnProperty.showPasswordEye = column.showPasswordEye;
        //单行文本额外元素数据
        columnProperty.addonValue = column.addonValue;
        columnProperty.addonFrontValue = column.addonFrontValue;
        columnProperty.addonEndValue = column.addonEndValue;
        columnProperty.addonIcon = column.addonIcon;
        columnProperty.chooseAddonIcon = column.chooseAddonIcon;
        columnProperty.checkOnBlur = column.checkType === '2';

        // text
        control = $placeHolder.wtextInput({
          columnProperty: columnProperty,
          commonProperty: commonProperty,
          $currentForm: $currentForm,
          naked: column.naked,
          toLinkFieldData: column.toLinkFieldData
        });
        ctlType = 'wtextInput';
      } else if (inputMode == dyFormInputMode.orgSelect2) {
        columnProperty.viewStyle = column.viewStyle;
        columnProperty.mappingValues = column.mappingValues;
        var useRelativeRole = 'useRelativeRole' in column ? column.useRelativeRole : !!column.relativeRoleNames;
        control = $placeHolder.wunit2({
          columnProperty: columnProperty,
          commonProperty: commonProperty,
          mutiSelect: column.mutiSelect,
          showUnitType: column.showUnitType, // 是否显示分类
          filterCondition: column.filterCondition, // 过滤条件
          $currentForm: $currentForm,
          naked: column.naked,
          useRelativeRole: useRelativeRole,
          typeList: useRelativeRole ? ['MyUnit', 'PublicGroup'] : column.typeList,
          defaultTypeList: column.defaultTypeList,
          selectTypeList: column.selectTypeList,
          selectTypeObj: column.selectTypeObj,
          orgStyle: column.orgStyle,
          defaultType: column.defaultType,
          nameDisplayMethod: column.nameDisplayMethod
        });
        ctlType = 'wunit2';
      } else if (inputMode == dyFormInputMode.serialNumber || inputMode == dyFormInputMode.unEditSerialNumber) {
        // serialNumber
        columnProperty.placeholder = column.placeholder;
        control = $placeHolder.wserialNumber({
          columnProperty: columnProperty,
          commonProperty: commonProperty,
          designatedId: column.designatedId,
          designatedType: column.designatedType,
          isOverride: column.isOverride,
          isSaveDb: column.isSaveDb,
          isCreateWhenSave: column.isCreateWhenSave,
          serialNumberTips: column.serialNumberTips, // 流水号重复提示
          formUuid: formDefinition.uuid,
          $currentForm: $currentForm,
          naked: column.naked
        });
        ctlType = 'wserialNumber';
      } else if (inputMode == dyFormInputMode.date) {
        columnProperty.dpStyle = column.dpStyle;
        columnProperty.stepStyle = column.stepStyle;
        columnProperty.timeStep = column.timeStep;
        columnProperty.minDate = column.minDate;
        columnProperty.maxDate = column.maxDate;
        columnProperty.setTimeStatus = column.setTimeStatus;
        columnProperty.relevanceEndTime = column.relevanceEndTime;
        columnProperty.defaultTime = column.defaultTime;
        control = $placeHolder.wdatePicker({
          columnProperty: columnProperty,
          commonProperty: commonProperty,
          contentFormat: column.contentFormat,
          layDateFormat: column.layDateFormat,
          $currentForm: $currentForm,
          naked: column.naked
        });
        ctlType = 'wdatePicker';
      } else if (inputMode == dyFormInputMode.textArea) {
        columnProperty.isHideNumTip = column.isHideNumTip;
        columnProperty.allowResize = column.allowResize;
        columnProperty.htmlCodec = column.htmlCodec;
        columnProperty.placeholder = column.placeholder;
        control = $placeHolder.wtextArea({
          columnProperty: columnProperty,
          commonProperty: commonProperty,
          $currentForm: $currentForm,
          naked: column.naked
        });
        ctlType = 'wtextArea';
        // 富文本
      } else if (inputMode == dyFormInputMode.ckedit) {
        columnProperty.showMode = column.showMode;
        columnProperty.htmlCodec = column.htmlCodec;
        control = $placeHolder.wckeditor({
          columnProperty: columnProperty,
          commonProperty: commonProperty,
          indent: true,
          breakBeforeOpen: false,
          breakAfterOpen: false,
          breakBeforeClose: false,
          breakAfterClose: false,
          $currentForm: $currentForm,
          naked: column.naked
        });
        ctlType = 'wckeditor';
        // combobox
      } else if (inputMode == dyFormInputMode.selectMutilFase) {
        columnProperty.selectMultiple = column.selectMultiple;
        columnProperty.selectCheckAll = column.selectCheckAll;
        control = $placeHolder.wcomboBox({
          columnProperty: columnProperty,
          commonProperty: commonProperty,
          optionDataSource: column.optionDataSource, // 备选项来源1:常量,2:字段
          optionSet: column.optionSet,
          lazyLoading: column.lazyLoading,
          dictCode: column.dictCode,
          $currentForm: $currentForm,
          naked: column.naked,
          searchable: typeof column.searchable == 'undefined' ? true : column.searchable
        });
        ctlType = 'wcomboBox';
      } else if (inputMode == dyFormInputMode.comboSelect) {
        control = $placeHolder.wcomboSelect({
          columnProperty: columnProperty,
          commonProperty: commonProperty,
          optionDataSource: column.optionDataSource, // 备选项来源1:常量,2:字段
          optionSet: column.optionSet,
          dictCode: column.dictCode,
          lazyLoading: column.lazyLoading,
          dataSourceId: column.dataSourceId,
          dataSourceFieldName: column.dataSourceFieldName,
          dataSourceDisplayName: column.dataSourceDisplayName,
          dataSourceGroup: column.dataSourceGroup,
          defaultCondition: column.defaultCondition,
          $currentForm: $currentForm,
          naked: column.naked,
          searchable: typeof column.searchable == 'undefined' ? true : column.searchable,
          multiSelect: column.multiSelect == true ? true : false,
          optionDataAutoSet: column.optionDataAutoSet,
          relateField: column.relateField,
          dataOptsList: column.dataOptsList,
          clearAll: column.clearAll
        });
        ctlType = 'wcomboSelect';
      } else if (inputMode == dyFormInputMode.select) {
        control = $placeHolder.wselect({
          columnProperty: columnProperty,
          commonProperty: commonProperty,
          optionDataSource: column.optionDataSource, // 备选项来源1:常量,2:字段
          optionSet: column.optionSet,
          dictCode: column.dictCode,
          dictUuid: column.dictUuid,
          $currentForm: $currentForm,
          lazyLoading: column.lazyLoading,
          naked: column.naked,
          searchable: column.searchable,
          selectMultiple: column.selectMultiple,
          selectCheckAll: column.selectCheckAll,
          maxSelectionLength: column.maxSelectionLength,
          showRightBtn: column.showRightBtn,
          dicCodeAddBtn: column.dicCodeAddBtn,
          dicCodeMoveUp: column.dicCodeMoveUp,
          dicCodeMoveDown: column.dicCodeMoveDown,
          dicCodeDelBtn: column.dicCodeDelBtn,
          selectType: column.selectType,
          dataSourceId: column.dataSourceId,
          dataSourceFieldName: column.dataSourceFieldName,
          dataSourceDisplayName: column.dataSourceDisplayName,
          dataSourceGroup: column.dataSourceGroup,
          defaultCondition: column.defaultCondition,
          reloadOption: column.reloadOption,
          optionDataAutoSet: column.optionDataAutoSet,
          relateField: column.relateField,
          dataOptsList: column.dataOptsList,
          clearAll: column.clearAll
        });
        ctlType = 'wselect';
      } else if (inputMode == dyFormInputMode.treeSelect) {
        control = $placeHolder.wcomboTree({
          columnProperty: columnProperty,
          commonProperty: commonProperty,
          serviceName: column.serviceName,
          newServiceName: column.newServiceName,
          treeWidth: column.treeWidth,
          treeHeight: column.treeHeight,
          mutiSelect: column.mutiSelect,
          realDisplay: column.realDisplay,
          selectParent: column.selectParent,
          showCheckAll: column.showCheckAll,
          expandAndCollapse: column.expandAndCollapse,
          $currentForm: $currentForm,
          naked: column.naked,
          showRightBtn: column.showRightBtn,
          dicCodeAddBtn: column.dicCodeAddBtn,
          dicCodeMoveUp: column.dicCodeMoveUp,
          dicCodeMoveDown: column.dicCodeMoveDown,
          dicCodeDelBtn: column.dicCodeDelBtn,
          optionDataAutoSet: column.optionDataAutoSet,
          relateField: column.relateField,
          allPath: column.allPath,
          clearAll: column.clearAll,
          dictValueColumn: column.dictValueColumn
        });
        ctlType = 'wcomboTree';
        // 视图展示
      } else if (inputMode == dyFormInputMode.viewdisplay) {
        control = $placeHolder.wviewDisplay({
          columnProperty: columnProperty,
          commonProperty: commonProperty,
          relationDataText: column.relationDataText,
          relationDataValue: column.relationDataValue,
          relationDataSql: column.relationDataSql,
          $currentForm: $currentForm,
          naked: column.naked
        });
        ctlType = 'wviewDisplay';
      } else if (inputMode == dyFormInputMode.accessory3) {
        control = $placeHolder.wfileUpload({
          columnProperty: columnProperty,
          commonProperty: commonProperty,
          allowUpload: column.allowUpload, // 允许上传
          allowDownload: column.allowDownload, // 允许下载
          allowDelete: column.allowDelete, // 允许删除
          mutiselect: column.mutiselect, // 是否多选
          allowFileNameRepeat: column.allowFileNameRepeat, // 是否允许文件名重复
          enableSignature: formDefinition.enableSignature == signature.enable,
          $currentForm: $currentForm,
          fileExt: column.fileExt,
          fileMaxSize: column.fileMaxSize,
          fileMaxNum: column.fileMaxNum,
          downloadAllType: column.downloadAllType,
          btnShowType: column.btnShowType,
          naked: column.naked,
          isShowFileFormatIcon: column.isShowFileFormatIcon,
          isShowFileSourceIcon: column.isShowFileSourceIcon,
          fileSourceIdStr: column.fileSourceIdStr,
          secDevBtnIdStr: column.secDevBtnIdStr,
          flowSecDevBtnIdStr: column.flowSecDevBtnIdStr
        });
        ctlType = 'wfileUpload';
      } else if (inputMode == dyFormInputMode.accessoryImg) {
        control = $placeHolder.wfileUpload4Image({
          columnProperty: columnProperty,
          commonProperty: commonProperty,
          allowUpload: column.allowUpload, // 允许上传
          allowDownload: column.allowDownload, // 允许下载
          allowDelete: column.allowDelete, // 允许删除
          allowPreview: column.allowPreview, // 允许预览
          mutiselect: column.mutiselect, // 是否多选
          fileExt: column.fileExt,
          fileTips: column.fileTips,
          fileMaxSize: column.fileMaxSize,
          fileMaxNum: column.fileMaxNum,
          allowFileNameRepeat: column.allowFileNameRepeat, // 是否允许文件名重复
          pixelCheck: column.pixelCheck, // 是否允许文件名重复
          imgWidth: column.imgWidth, // 是否允许文件名重复
          imgHeight: column.imgHeight, // 是否允许文件名重复
          enableSignature: formDefinition.enableSignature == signature.enable,
          $currentForm: $currentForm,
          naked: column.naked,
          flowSecDevBtnIdStr: column.flowSecDevBtnIdStr,
          operationMode: column.operationMode || '1',
          selectedCategories: column.selectedCategories || []
        });
        ctlType = 'wfileUpload4Image';
      } else if (inputMode == dyFormInputMode.accessory1) {
        if (column.keepOpLog == keepOpLogType.yes) {
          // 正方形控件
          control = $placeHolder.wfileUpload4Body({
            columnProperty: columnProperty,
            commonProperty: commonProperty,
            allowUpload: column.allowUpload, // 允许上传
            allowDownload: column.allowDownload, // 允许下载
            allowDelete: column.allowDelete, // 允许删除
            mutiselect: column.mutiselect, // 是否多选
            operateBtns: column.operateBtns, // 操作权限设置
            allowFileNameRepeat: column.allowFileNameRepeat, // 是否允许文件名重复
            enableSignature: formDefinition.enableSignature == signature.enable,
            $currentForm: $currentForm,
            naked: column.naked,
            fileExt: column.fileExt,
            fileMaxSize: column.fileMaxSize,
            fileMaxNum: column.fileMaxNum,
            defaultView: column.defaultView,
            createHistory: column.createHistory,
            flowSecDevBtnIdStr: column.flowSecDevBtnIdStr,
            readOnly: column.readOnly || columnProperty.showType == dyshowType.readonly || columnProperty.showType == dyshowType.showAsLabel
          });
        } else {
          control = $placeHolder.wfileUpload4Icon({
            columnProperty: columnProperty,
            commonProperty: commonProperty,
            allowUpload: column.allowUpload, // 允许上传
            allowDownload: column.allowDownload, // 允许下载
            allowDelete: column.allowDelete, // 允许删除
            mutiselect: column.mutiselect, // 是否多选
            operateBtns: column.operateBtns, // 操作权限设置
            allowFileNameRepeat: column.allowFileNameRepeat, // 是否允许文件名重复
            enableSignature: formDefinition.enableSignature == signature.enable,
            $currentForm: $currentForm,
            naked: column.naked,
            fileExt: column.fileExt,
            fileMaxSize: column.fileMaxSize,
            fileMaxNum: column.fileMaxNum,
            downloadAllType: column.downloadAllType,
            defaultView: column.defaultView,
            createHistory: column.createHistory,
            flowSecDevBtnIdStr: column.flowSecDevBtnIdStr,
            readOnly: column.readOnly || columnProperty.showType == dyshowType.readonly || columnProperty.showType == dyshowType.showAsLabel
          });
        }

        ctlType = 'wfileUpload4Icon';
        // 弹出对话框
      } else if (inputMode == dyFormInputMode.dialog) {
        control = $placeHolder.wdialog({
          columnProperty: columnProperty,
          commonProperty: commonProperty,
          relationDataIdTwo: column.relationDataIdTwo,
          relationDataTextTwo: column.relationDataTextTwo,
          relationDataValueTwo: column.relationDataValueTwo,
          relationDataSourceTwo: column.relationDataSourceTwo,
          relationDsDefaultCondition: column.relationDsDefaultCondition,
          countNumHref: column.countNumHref,
          countNumHrefText: column.countNumHrefText,
          relationDataTwoSql: column.relationDataTwoSql,
          relationDataDefiantion: column.relationDataDefiantion,
          relationDataShowMethod: column.relationDataShowType,
          relationDataShowType: column.relationDataShowType,
          selectType: column.selectType,
          dialogTitle: column.dialogTitle,
          dialogSize: column.dialogSize,
          destType: column.destType,
          destSubform: column.destSubform,
          pageSize: column.pageSize,
          isRelevantWorkFlow: column.isRelevantWorkFlow,
          workflow: column.workflow,
          $currentForm: $currentForm,
          naked: column.naked,
          searchMultiple: 'true' == column.searchMultiple,
          countNum2Input: column.countNum2Input,
          allowValNotInDs: column.allowValNotInDs,
          queryDefaultCondition: column.queryDefaultCondition
        });
        ctlType = 'wdialog';
      } else if (inputMode == dyFormInputMode.timeEmploy) {
        control = $placeHolder.wtimeEmploy({
          columnProperty: columnProperty,
          commonProperty: commonProperty,
          resCode: column.resCode,
          $currentForm: $currentForm,
          naked: column.naked
        });
        ctlType = 'wtimeEmploy';
      } else if (inputMode == dyFormInputMode.embedded) {
        control = $placeHolder.wembedded({
          columnProperty: columnProperty,
          commonProperty: commonProperty,
          $currentForm: $currentForm,
          naked: column.naked
        });
        ctlType = 'wembedded';
      } else if (inputMode == dyFormInputMode.job) {
        control = $placeHolder.wjobSelect({
          columnProperty: columnProperty,
          commonProperty: commonProperty,
          realDisplay: column.realDisplay,
          optionSet: column.optionSet,
          $currentForm: $currentForm,
          naked: column.naked
        });
        ctlType = 'wjobSelect';
      } else if (inputMode == dyFormInputMode.chained) {
        control = $placeHolder.wchained({
          columnProperty: columnProperty,
          commonProperty: commonProperty,
          optionDataSource: column.optionDataSource, // 备选项来源1:常量,2:字段
          optionSet: column.optionSet,
          lazyLoading: column.lazyLoading,
          dictCode: column.dictCode,
          $currentForm: $currentForm,
          naked: column.naked,
          optionDataAutoSet: column.optionDataAutoSet,
          relateField: column.relateField
        });
        ctlType = 'wchained';
      } else if (inputMode == dyFormInputMode.taggroup) {
        control = $placeHolder.wtagGroup({
          columnProperty: columnProperty,
          commonProperty: commonProperty,
          ctrlField: column.ctrlField,
          optionDataSource: column.optionDataSource, // 备选项来源1:常量,2:字段
          dictCode: column.dictCode,
          optionSet: column.optionSet,
          lazyLoading: column.lazyLoading,
          dataSourceId: column.dataSourceId,
          dataSourceFieldName: column.dataSourceFieldName,
          dataSourceDisplayName: column.dataSourceDisplayName,
          dataSourceGroup: column.dataSourceGroup,
          defaultCondition: column.defaultCondition,
          selectMode: column.selectMode, //选择模式，单选1，多选2
          selectMinContent: column.selectMinContent, //多选 选中内容
          selectMaxContent: column.selectMaxContent, //多选 取消选中内容
          tagColor: column.tagColor, //标签颜色，单选
          tagShape: column.tagShape, //标签形状，单选
          tagFontColor: column.tagFontColor, // 字体颜色
          tagBgColor: column.tagBgColor, // 背景颜色
          tagBorderColor: column.tagBorderColor, // 边框颜色
          tagEditable: column.tagEditable, // 是否可选， 单选
          $currentForm: $currentForm,
          naked: column.naked,
          optionDataAutoSet: column.optionDataAutoSet,
          relateField: column.relateField,
          dataOptsList: column.dataOptsList
        });
        ctlType = 'wtagGroup';
      } else if (inputMode == dyFormInputMode.colors) {
        control = $placeHolder.wcolor({
          columnProperty: columnProperty,
          commonProperty: commonProperty,
          ctrlField: column.ctrlField,
          selectMode: column.selectMode, //选择模式，单选1，多选2
          relatedField: column.relatedField,
          $currentForm: $currentForm,
          naked: column.naked
        });
        ctlType = 'wcolor';
      } else if (inputMode == dyFormInputMode.switchs) {
        control = $placeHolder.wswitchs({
          columnProperty: columnProperty,
          commonProperty: commonProperty,
          ctrlField: column.ctrlField,
          openText: column.openText, //选择模式，单选1，多选2
          closeText: column.closeText, //选择模式，单选1，多选2
          switchsVal: column.switchsVal, // 默认值
          $currentForm: $currentForm,
          naked: column.naked
        });
        ctlType = 'wswitchs';
      } else if (inputMode == dyFormInputMode.progress) {
        control = $placeHolder.wprogress({
          columnProperty: columnProperty,
          commonProperty: commonProperty,
          ctrlField: column.ctrlField,
          defaultProgress: column.defaultProgress, //默认值
          progressMin: column.progressMin, //最小值
          progressMax: column.progressMax, //最大值
          progressColor: column.progressColor, //最大值
          progressUnit: column.progressUnit, // 单位
          // editProgress: column.editProgress,
          progressHeight: column.progressHeight,
          $currentForm: $currentForm,
          naked: column.naked
        });
        ctlType = 'wprogress';
      } else if (inputMode == dyFormInputMode.placeholder) {
        control = $placeHolder.wplaceholder({
          columnProperty: columnProperty,
          commonProperty: commonProperty,
          ctrlField: column.ctrlField
        });
        ctlType = 'wplaceholder';
      } else {
        console.error(ctlName + '--> unknown inputMode[' + inputMode + ']');
        return null;
      }

      // 注册公式
      if (column.formula) $.ControlManager.registFormula(column.formula);

      $.ControlManager[ctlName] = {
        $placeHolder: $placeHolder,
        ctlType: ctlType,
        control: control
      };
      return control;
    },

    /**
     *  可编辑表格值转html输出
     * @param column
     */
    bootstrapTableEditableCtrlValue2Html: function (column, element) {
      var inputMode = column.inputMode;
      debugger;
      if (inputMode == dyFormInputMode.select) {
      }
    },

    /**
     * 创建视图列表控件
     * @param $tableElement
     * @param parentElement
     * @param formDefinition
     * @param tableViewDefinition
     */
    createTableViewControl: function ($tableElement, parentElement, formDefinition, tableViewDefinition, formData) {
      $tableElement.wTableView({
        widgetId: tableViewDefinition.relaTableViewWidgetId,
        columns: tableViewDefinition.columns,
        defaultCondition: tableViewDefinition.defaultCondition,
        formData: formData,
        showType: tableViewDefinition.showType
      });
    },

    createFileLibraryControl: function ($tableElement, parentElement, formDefinition, tableViewDefinition, formData) {
      $tableElement.wFormFileLibrary({
        widgetId: tableViewDefinition.relaFileLibraryWidgetId,
        columns: tableViewDefinition.columns,
        defaultCondition: tableViewDefinition.defaultCondition,
        formData: formData,
        showType: tableViewDefinition.showType
      });
    },

    /**
     * createSubFormControl 创建从表控件
     */
    createSubFormControl: function ($tableelement, parentelement, formDefinition, subformDefinition, subformData) {
      var id = $tableelement.attr('id');
      var formUuid = $tableelement.attr('formUuid');
      var subformConfig = formDefinition.getSubform(formUuid); // 从表配置
      if (typeof subformConfig == 'undefined' || subformConfig == null) {
        throw new Error(" cann't find subform[" + formUuid + '] in form definition json package]');
      }
      // //从表字段定义
      if (typeof subformDefinition == 'undefined' || subformDefinition == null) {
        subformDefinition = loadFormDefinition(formUuid);
        if (typeof subformDefinition == 'undefined' || subformDefinition == null) {
          throw new Error(" cann't find formUuid[" + formUuid + ' in db]');
        }
      }
      parentelement.cache.put.call(parentelement, cacheType.formDefinition, subformDefinition);

      var groupColumnShow = true;
      if (subformConfig.isGroupShowTitle == dySubFormGroupShow.show) {
        $tableelement.attr('isGroupShowTitle', dySubFormGroupShow.show);
        groupColumnShow = false;
      }
      var ns = $tableelement.attr('data-ns');
      var nsAttr = $.trim(ns).length ? 'data-ns="' + ns + '"' : '';
      if (subformConfig.isGroupShowTitle == dySubFormGroupShow.show) {
        $tableelement.wsubForm4Group({
          $paranentelement: parentelement,
          formDefinition: formDefinition,
          subformDefinition: subformDefinition,
          readOnly: false,
          mainformdataUuid: parentelement.getDataUuid(),
          formUuid: formUuid,
          groupField: subformConfig.groupShowTitle,
          groupOrder: 'asc',
          groupColumnShow: groupColumnShow,
          autoWidth: subformConfig.autoWidth,
          $parent: $tableelement.parent()
        });
      } else {
        $tableelement.wsubForm({
          $paranentelement: parentelement,
          formDefinition: formDefinition,
          subformDefinition: subformDefinition,
          readOnly: false,
          mainformdataUuid: parentelement.getDataUuid(),
          formUuid: formUuid,
          multiselect: subformConfig.multiselect,
          autoWidth: subformConfig.autoWidth,
          fixedHeader: subformConfig.fixedHeader,
          showType: subformConfig.showType,
          $parent: $tableelement.parent(),
          data: subformData
        });
      }
    },

    /**
     * 获得从表控件(根据isGroupShowTitle属性区分.)
     *
     * @param formUuid
     * @returns
     */
    getSubFormControl: function (formUuid, ns) {
      var ctl = {};
      var isGroupShowTitle = '';
      if ($($.dyform.nss(ns, '#' + formUuid)).size() > 0) {
        isGroupShowTitle = $($.dyform.nss(ns, '#' + formUuid)).attr('isGroupShowTitle');
      } else {
        return undefined;
      }
      if (isGroupShowTitle == dySubFormGroupShow.show) {
        ctl = $($.dyform.nss(ns, '#' + formUuid)).wsubForm4Group('getObject');
      } else {
        ctl = $($.dyform.nss(ns, '#' + formUuid)).wsubForm('getObject');
      }
      return ctl;
    },

    /**
     * 根据name返回控件对象。
     *
     * @param name
     * @returns
     */
    getCtl: function (ctlName) {
      var ctlMapInfo = $.ControlManager[ctlName];
      if (typeof ctlMapInfo == 'undefined' || ctlMapInfo == null) {
        return null;
      }
      return ctlMapInfo.control;
    },

    getControlFormulas: function () {
      if (typeof controlFormulas == 'undefined') {
        controlFormulas = {};
      }
      return controlFormulas;
    },

    // 注册公式
    registFormula: function (formula) {
      var formulas = $.ControlManager.getControlFormulas();
      if (typeof formulas == 'undefined' || formulas == null) {
        formulas = {};
      }

      if (typeof formula == 'undefined' || formula == null || $.trim(formula).length == 0) {
        return;
      }
      if (typeof formula == 'string') {
        try {
          formula = eval('(' + formula + ')');
        } catch (e) {
          console.log(formula + '公式注册失败');
          return;
        }
      }

      var triggerElements = formula.triggerElements; // 触发公式的元素
      var formulaFun = formula.formula; // 公式

      // 非法公式
      if (typeof triggerElements == 'undefined' || typeof formulaFun == 'undefined') {
        if (typeof formula == 'string') {
          console.error('非法的公式:' + formula);
        } else {
          console.error('存在非法的公式');
        }

        return;
      }

      for (var i = 0; i < triggerElements.length; i++) {
        var triggerElementFieldName = triggerElements[i];
        var fieldFormulas = [];
        for (var fn in formulas) {
          if (fn == triggerElementFieldName) {
            fieldFormulas = formulas[fn];
          }
        }
        fieldFormulas.push(formulaFun);
        formulas[triggerElementFieldName] = fieldFormulas;
      }
    },
    // 运算公式
    culateByFormula: function (triggerControl) {
      var fieldName = triggerControl.getCtlName();
      var allformulas = $.ControlManager.getControlFormulas();
      if (triggerControl.getPos() == dyControlPos.subForm) {
        fieldName = triggerControl.options.columnProperty.columnName;
        fieldName = (triggerControl.getFormDefinition().outerId || triggerControl.getFormDefinition().id) + ':' + fieldName;
      }

      var formulas = allformulas[fieldName]; // 获取公式
      if (typeof formulas == 'undefined') {
        return;
      }

      // 运行公式
      for (var i = 0; i < formulas.length; i++) {
        try {
          formulas[i].call(triggerControl);
        } catch (e) {
          console.error(e);
        }
      }
      // control.registFormula(allformulas[name]);
    }
  };

  /**
   * 控件初始化
   */
  function initDyControl(ctlName, name, column, formDefinition) {
    var columnProperty = {
      // 控件字段属性
      applyTo: column.applyTo, // 应用于
      controlName: ctlName, // 控件名称，由外面指定
      columnName: column.name, // 字段定义 fieldname
      displayName: column.displayName, // 描述名称 descname
      dbDataType: column.dbDataType, // 字段类型 datatype type
      indexed: column.indexed, // 是否索引
      showed: column.showed, // 是否界面表格显示
      sorted: column.sorted, // 是否排序
      sysType: column.sysType, // 系统定义类型，包括三种（0：系统默认，1：管理员常量定义，2：表单添加后自定义）
      length: column.length, // 长度
      isHide: column.isHide, // 是否隐藏
      showType: column.showType, // 显示类型 1,2,3,4 datashow
      defaultValue: column.defaultValue, // 默认值
      valueCreateMethod: column.valueCreateMethod, // 默认值创建方式 1用户输入
      onlyreadUrl: column.onlyreadUrl, // 只读状态下设置跳转的url
      realDisplay: column.realDisplay,
      formDefinition: formDefinition,
      precision: column.precision,
      scale: column.scale
    };
    // 控件公共属性
    var commonProperty = {
      inputMode: column.inputMode, // 输入样式 控件类型 inputDataType
      fieldCheckRules: column.fieldCheckRules,
      unitUnique: column.unitUnique,
      fontSize: column.fontSize, // 字段的大小
      fontColor: column.fontColor, // 字段的颜色
      ctlWidth: column.ctlWidth, // 宽度
      ctlHight: column.ctlHight, // 高度
      textAlign: column.textAlign,
      customizedRegularText: column.customizedRegularText, //自定义正则表达式
      validateEventManage: column.validateEventManage, //事件管理
      noNullValidateReminder: column.noNullValidateReminder, //非空提示
      uniqueValidateReminder: column.uniqueValidateReminder, //唯一校验提示
      regularValidateReminder: column.regularValidateReminder, //正则校验提示
      eventValidateReminder: column.eventValidateReminder //正则校验提示
      // 对齐方式
    };
    var inputMode = commonProperty.inputMode;
    // radio
    if (inputMode == dyFormInputMode.radio) {
      $("input[name='" + name + "']").wradio({
        columnProperty: columnProperty,
        commonProperty: commonProperty,
        optionDataSource: column.optionDataSource, // 备选项来源1:常量,2:字段
        optionSet: column.optionSet,
        dictCode: column.dictCode,
        radioStatus: column.radioStatus,
        selectDirection: column.selectDirection,
        lazyLoading: column.lazyLoading,
        radioShape: column.radioShape,
        radioCancel: column.radioCancel,
        selectAlign: column.selectAlign
      });
      // checkbox
    } else if (inputMode == dyFormInputMode.checkbox) {
      $("input[name='" + name + "']").wcheckBox({
        columnProperty: columnProperty,
        commonProperty: commonProperty,
        ctrlField: column.ctrlField,
        optionDataSource: column.optionDataSource, // 备选项来源1:常量,2:字段
        optionSet: column.optionSet,
        dictCode: column.dictCode,
        lazyLoading: column.lazyLoading,
        selectMode: column.selectMode, // 选择模式，单选1，多选2
        singleCheckContent: column.singleCheckContent, // 单选 选中内容
        singleUnCheckContent: column.singleUnCheckContent, // 单选 取消选中内容
        selectDirection: column.selectDirection,
        checkboxAll: column.checkboxAll,
        checkboxMin: column.checkboxMin,
        checkboxMax: column.checkboxMax,
        selectAlign: column.selectAlign
      });
    } else if (inputMode == dyFormInputMode.number) {
      // numbertext
      $("input[name='" + name + "']").wnumberInput({
        columnProperty: columnProperty,
        commonProperty: commonProperty,
        numberRange: column.numberRange,
        decimal: column.decimal,
        negative: column.negative,
        operator: column.operator
      });
      // text
    } else if (inputMode == dyFormInputMode.text) {
      $("input[name='" + name + "']").wtextInput({
        columnProperty: columnProperty,
        commonProperty: commonProperty
      });
    } else if (inputMode == dyFormInputMode.orgSelect2) {
      columnProperty.viewStyle = column.viewStyle;
      columnProperty.mappingValues = column.mappingValues;
      $("input[name='" + name + "']").wunit2({
        columnProperty: columnProperty,
        commonProperty: commonProperty,
        mutiSelect: column.mutiSelect,
        typeList: column.typeList,
        orgStyle: column.orgStyle,
        selectTypeList: column.selectTypeList,
        defaultType: column.defaultType
      });
    }
    // serialNumber
    else if (inputMode == dyFormInputMode.serialNumber || inputMode == dyFormInputMode.unEditSerialNumber) {
      $("input[name='" + name + "']").wserialNumber({
        columnProperty: columnProperty,
        commonProperty: commonProperty,
        designatedId: column.designatedId,
        designatedType: column.designatedType,
        serialNumberTips: column.serialNumberTips, // 流水号重复提示
        isOverride: column.isOverride,
        isSaveDb: column.isSaveDb,
        isCreateWhenSave: column.isCreateWhenSave,
        formUuid: formDefinition.uuid
      });
    } else if (inputMode == dyFormInputMode.date) {
      columnProperty.dpStyle = column.dpStyle;
      columnProperty.maxDate = column.maxDate;
      columnProperty.minDate = column.minDate;
      columnProperty.stepStyle = column.stepStyle;
      columnProperty.timeStep = column.timeStep;
      $("input[name='" + name + "']").wdatePicker({
        columnProperty: columnProperty,
        commonProperty: commonProperty,
        contentFormat: column.contentFormat
      });
    } else if (inputMode == dyFormInputMode.textArea) {
      columnProperty.htmlCodec = column.htmlCodec;
      $('#' + name).wtextArea({
        columnProperty: columnProperty,
        commonProperty: commonProperty
      });
      // 富文本
    } else if (inputMode == dyFormInputMode.ckedit) {
      columnProperty.htmlCodec = column.htmlCodec;
      $('#' + name).wckeditor({
        columnProperty: columnProperty,
        commonProperty: commonProperty,
        indent: true,
        breakBeforeOpen: false,
        breakAfterOpen: false,
        breakBeforeClose: false,
        breakAfterClose: false
      });
      // combobox
    } else if (inputMode == dyFormInputMode.selectMutilFase) {
      $("select[name='" + name + "']").wcomboBox({
        columnProperty: columnProperty,
        commonProperty: commonProperty,
        optionDataSource: column.optionDataSource, // 备选项来源1:常量,2:字段
        optionSet: column.optionSet,
        lazyLoading: column.lazyLoading,
        dictCode: column.dictCode
      });
    } else if (inputMode == dyFormInputMode.select) {
      $("select[name='" + name + "']").wselect({
        columnProperty: columnProperty,
        commonProperty: commonProperty,
        lazyLoading: column.lazyLoading,
        optionDataSource: column.optionDataSource, // 备选项来源1:常量,2:字段
        optionSet: column.optionSet,
        dictCode: column.dictCode
      });
    } else if (inputMode == dyFormInputMode.treeSelect) {
      $("input[name='" + name + "']").wcomboTree({
        columnProperty: columnProperty,
        commonProperty: commonProperty,
        serviceName: column.serviceName,
        treeWidth: column.treeWidth,
        treeHeight: column.treeHeight,
        mutiSelect: column.mutiSelect,
        realDisplay: column.realDisplay
      });
      // 视图展示
    } else if (inputMode == dyFormInputMode.viewdisplay) {
      $("input[name='" + name + "']").wviewDisplay({
        columnProperty: columnProperty,
        commonProperty: commonProperty,
        relationDataText: column.relationDataText,
        relationDataValue: column.relationDataValue,
        relationDataSql: column.relationDataSql
      });
    } else if (inputMode == dyFormInputMode.accessory3) {
      $("input[name='" + name + "']").wfileUpload({
        columnProperty: columnProperty,
        commonProperty: commonProperty,
        allowUpload: column.allowUpload, // 允许上传
        allowDownload: column.allowDownload, // 允许下载
        allowDelete: column.allowDelete, // 允许删除
        mutiselect: column.mutiselect, // 是否多选
        allowFileNameRepeat: column.allowFileNameRepeat, // 是否允许文件名重复
        enableSignature: columnProperty.formDefinition.enableSignature == signature.enable
      });
    } else if (inputMode == dyFormInputMode.accessoryImg) {
      $("input[name='" + name + "']").wfileUpload4Image({
        columnProperty: columnProperty,
        commonProperty: commonProperty,
        allowUpload: column.allowUpload, // 允许上传
        allowDownload: column.allowDownload, // 允许下载
        allowDelete: column.allowDelete, // 允许删除
        allowPreview: column.allowPreview, // 允许预览
        mutiselect: column.mutiselect, // 是否多选
        allowFileNameRepeat: column.allowFileNameRepeat, // 是否允许文件名重复
        pixelCheck: column.pixelCheck, // 是否允许文件名重复
        imgWidth: column.imgWidth, // 是否允许文件名重复
        imgHeight: column.imgHeight, // 是否允许文件名重复
        enableSignature: columnProperty.formDefinition.enableSignature
      });
    } else if (inputMode == dyFormInputMode.accessory1) {
      $("input[name='" + name + "']").wfileUpload4Icon({
        columnProperty: columnProperty,
        commonProperty: commonProperty,
        allowUpload: column.allowUpload, // 允许上传
        allowDownload: column.allowDownload, // 允许下载
        allowDelete: column.allowDelete, // 允许删除
        mutiselect: column.mutiselect, // 是否多选
        allowFileNameRepeat: column.allowFileNameRepeat, // 是否允许文件名重复
        enableSignature: columnProperty.formDefinition.enableSignature,
        fileExt: column.fileExt,
        fileMaxSize: column.fileMaxSize
      });
      // 弹出对话框
    } else if (inputMode == dyFormInputMode.dialog) {
      $("input[name='" + name + "']").wdialog({
        columnProperty: columnProperty,
        commonProperty: commonProperty,
        relationDataIdTwo: column.relationDataIdTwo,
        relationDataTextTwo: column.relationDataTextTwo,
        relationDataValueTwo: column.relationDataValueTwo,
        relationDataTwoSql: column.relationDataTwoSql,
        relationDataDefiantion: column.relationDataDefiantion,
        relationDataShowMethod: column.relationDataShowType,
        relationDataShowType: column.relationDataShowType,
        queryDefaultCondition: column.queryDefaultCondition,
        searchMultiple: 'true' == column.searchMultiple
      });
    } else if (inputMode == dyFormInputMode.taggroup) {
      $("input[name='" + name + "']").wtagGroup({
        columnProperty: columnProperty,
        commonProperty: commonProperty,
        ctrlField: column.ctrlField,
        optionDataSource: column.optionDataSource, // 备选项来源1:常量,2:字段
        dictCode: column.dictCode,
        optionSet: column.optionSet,
        selectMode: column.selectMode, //选择模式，单选1，多选2
        selectMinContent: column.selectMinContent, //多选 选中内容
        selectMaxContent: column.selectMaxContent, //多选 取消选中内容
        tagColor: column.tagColor, //标签颜色，单选
        tagShape: column.tagShape, //标签形状，单选
        tagFontColor: column.tagFontColor, // 字体颜色
        tagBgColor: column.tagBgColor, // 背景颜色
        tagBorderColor: column.tagBorderColor, // 边框颜色
        tagEditable: column.tagEditable, // 是否可选， 单选
        lazyLoading: column.lazyLoading,
        $currentForm: $currentForm,
        naked: column.naked
      });
    } else if (inputMode == dyFormInputMode.colors) {
      $("input[name='" + name + "']").wcolor({
        columnProperty: columnProperty,
        commonProperty: commonProperty,
        ctrlField: column.ctrlField,
        selectMode: column.selectMode, //选择模式，单选1，多选2
        relatedField: column.relatedField,
        $currentForm: $currentForm,
        naked: column.naked
      });
    } else if (inputMode == dyFormInputMode.switchs) {
      $("input[name='" + name + "']").wswitchs({
        columnProperty: columnProperty,
        commonProperty: commonProperty,
        ctrlField: column.ctrlField,
        openText: column.openText, //选择模式，单选1，多选2
        closeText: column.closeText, //选择模式，单选1，多选2
        switchsVal: column.switchsVal, // 默认值
        $currentForm: $currentForm,
        naked: column.naked
      });
    } else if (inputMode == dyFormInputMode.progress) {
      $("input[name='" + name + "']").wprogress({
        columnProperty: columnProperty,
        commonProperty: commonProperty,
        ctrlField: column.ctrlField,
        defaultProgress: column.defaultProgress, //默认值
        progressMin: column.progressMin, //最小值
        progressMax: column.progressMax, //最大值
        progressColor: column.progressColor, //最大值
        progressUnit: column.progressUnit, // 单位
        editProgress: column.editProgress,
        progressHeight: column.progressHeight,
        $currentForm: $currentForm,
        naked: column.naked
      });
    }
  }

  /**
   * 通过输入类型获得拼装的html
   */
  function getHtmlStrByInputDataType(inputMode, fieldcode, elmenttype) {
    var appendhtml = {};
    if (inputMode == dyFormInputMode.textArea || inputMode == dyFormInputMode.ckedit) {
      formfieldcode = elmenttype + 'textarea' + fieldcode;
      appendhtml.html = '<textarea type="text" id="' + formfieldcode + '" name="' + formfieldcode + '"></textarea>';
      appendhtml.formfieldcode = formfieldcode;
    } else if (inputMode == dyFormInputMode.selectMutilFase) {
      formfieldcode = elmenttype + 'select' + fieldcode;
      appendhtml.formfieldcode = formfieldcode;
      appendhtml.html = '<select name="' + formfieldcode + '" type="text" > </select>';
    } else {
      formfieldcode = elmenttype + 'input' + fieldcode;
      appendhtml.formfieldcode = formfieldcode;
      appendhtml.html = '<input name="' + formfieldcode + '"  type="text" />';
    }
    return appendhtml;
  }
})(jQuery);
