const _ = [
  {
    wtype: 'WidgetFormDatePicker',
    name: '日期时间',
    iconClass: 'pticon iconfont icon-a-icjichuzujianriqi',
    scope: ['dyform', 'mobileDyform'],
    category: 'basicComponent',
    configuration: {
      hidden: false,
      width: 'auto',
      inputMode: '30',
      dbDataType: '2',
      zeroShow: true, //默认补0
      datePattern: 'yyyy-MM-DD', //默认格式
      datePatternJson: {}, //格式json
      range: false, // 是否范围日期
      asEndDate: false,
      clearBtnShow: false,
      // minDateType: null,
      // maxDateType: null,
      // minDate: null,
      // maxDate: null,
      defaultDisplayState: 'edit',
      uneditableDisplayState: 'label',
      syncLabel2FormItem: true,
      style: {},
      defaultValueSetting: {
        valueType: 'no',
        fixedDateValue: null,
        systemValue: '',
        offset: '',
        offsetUnit: 'day',
        offsetType: 'weekday'
      },
      minValueSetting: {
        valueType: 'no',
        fixedDateValue: null,
        systemValue: '',
        offset: '',
        offsetUnit: 'day',
        offsetType: 'weekday',
        field: ''
      },
      maxValueSetting: {
        valueType: 'no',
        fixedDateValue: null,
        systemValue: '',
        offset: '',
        offsetUnit: 'day',
        offsetType: 'weekday',
        field: ''
      },
      defaultValue: '',
      hasDefaultValue: false,
      valueCreateMethod: '4',
      uniConfiguration: {
        bordered: false,
      } //移动端配置
    }
  },
  {
    wtype: 'WidgetFormDatePicker$Range',
    name: '日期时间范围',
    iconClass: 'pticon iconfont icon-ptkj-rilixuanzeriqi',
    scope: ['dyform', 'mobileDyform'],
    category: 'basicComponent',
    configuration: {
      hidden: false,
      width: 'auto',
      inputMode: '30',
      dbDataType: '2',
      zeroShow: true, //默认补0
      datePattern: 'yyyy-MM-DD', //默认格式
      datePatternJson: {}, //格式json
      range: false, // 是否范围日期
      asEndDate: false,
      clearBtnShow: false,
      // minDateType: null,
      // maxDateType: null,
      // minDate: null,
      // maxDate: null,
      defaultDisplayState: 'edit',
      uneditableDisplayState: 'label',
      syncLabel2FormItem: true,
      style: {},
      defaultValueSetting: {
        valueType: 'no',
        fixedDateValue: null,
        systemValue: '',
        offset: '',
        offsetUnit: 'day',
        offsetType: 'weekday'
      },
      minValueSetting: {
        valueType: 'no',
        fixedDateValue: null,
        systemValue: '',
        offset: '',
        offsetUnit: 'day',
        offsetType: 'weekday',
        field: ''
      },
      maxValueSetting: {
        valueType: 'no',
        fixedDateValue: null,
        systemValue: '',
        offset: '',
        offsetUnit: 'day',
        offsetType: 'weekday',
        field: ''
      },
      defaultValue: '',
      hasDefaultValue: false,
      valueCreateMethod: '4',
      relaFieldConfigures: [],
      range: true,
      endDefaultValueSetting: {
        valueType: 'no',
        fixedDateValue: null,
        systemValue: '',
        offset: '',
        offsetUnit: 'day',
        offsetType: 'weekday'
      },
      uniConfiguration: {
        bordered: false,
      } //移动端配置
    }
  }
];

export default _;
