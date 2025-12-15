var WTagGroupClass = function () {
  this.ctrlField = ''; //checkbox的属性
  this.optionDataSource = '1'; //备选项来源1:常量,2:字典
  this.dictCode = ''; //字典代码
  /*在optionDataSource属性为1的情况下,option的值则来源于该字段.
	该字段的值以map的形式保存key为值，value为备注*/
  this.optionSet = [];

  this.selectMode = '2'; //选择模式，单选1，多选2
  this.selectMinContent = ''; //多选 选中内容
  this.selectMaxContent = ''; //多选 取消选中内容
  this.tagColor = '1'; //标签颜色，单选
  this.tagShape = '1'; //标签形状，单选
  (this.tagFontColor = []), // 字体颜色
    (this.tagBgColor = []), // 背景颜色
    (this.tagBorderColor = []), // 边框颜色
    (this.tagEditable = '1'), // 是否可选， 单选
    (this.toJSON = toJSON);
};

WTagGroupClass.prototype = new MainFormFieldClass();
