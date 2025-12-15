export default {
  wtype: 'WidgetFormNumericalBar',
  name: '数值条',
  iconClass: 'pticon iconfont icon-a-icjichuzujianjindutiao',
  scope: ['dyform', 'mobileDyform'],
  category: 'basicComponent',
  configuration: {
    code: '',
    type: 'progress',
    defaultDisplayState: 'edit',
    uneditableDisplayState: 'readonly',
    dataType: 'number',
    dbDataType: '17', //默认：Number
    scale: 2, //小数位数
    precision: 8, //精度位（有效数）
    length: 10,
    // 进度条
    progress: {
      type: 'line',
      showInput: true,
      defaultBgColor: 'default', //背景颜色，默认
      bgColor: '',
      defaultColor: 'default', //线段颜色，默认（使用主题色）
      strokeColor: '',
      strokeWidth: 10,
      round: true, //strokeLinecap默认圆角，false为square
      width: 132, // 画布宽度
      gapDegree: undefined, // 缺口角度
      gapPosition: 'bottom', // 缺口位置
      showInfo: true, // 显示百分比
      defaultTextStyle: 'default', //进度数值样式，默认
      textColor: '',
      fontSize: 14,
      fontWeight: 'normal',
      statusShowType: 'define', // 状态显示
      status: 'normal',
      success: {
        defaultColor: 'default', //线段颜色，默认
        defaultBgColor: 'default', //背景颜色，默认
        strokeColor: '',
        bgColor: '',
        textColor: ''
      },
      exception: {
        defaultBgColor: 'default', //背景颜色，默认
        defaultColor: 'default', //线段颜色，默认
        textColor: '',
        strokeColor: '',
        bgColor: '',
      },
      isUseFormat: false,
      format: '',
      isTooltips: false,
      tooltip: ''
    },
    // 评分
    rate: {
      allowClear: true,
      allowHalf: false, //半选
      characterType: 'icon', //字符类型
      characterString: '',
      characterIcon: '',
      count: 5,
      defaultStyle: 'default',
      selectedColor: '',
      bgColor: '',
      fontSize: 20,
      fontWeight: 'normal',
      isTooltips: false,
      tooltips: []
    }
  }
};
