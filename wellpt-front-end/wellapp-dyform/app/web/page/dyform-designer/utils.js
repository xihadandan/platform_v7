import { generateId, deepClone } from '@framework/vue/utils/util';

export const convertFormField2DataModelColumns = function (fieldWidgets, formType) {
  let columnJson = [];
  for (let i = 0, len = fieldWidgets.length; i < len; i++) {
    let field = fieldWidgets[i];
    // 视图表单，只考虑增量的字段
    if (
      field.configuration.code &&
      field.configuration.isDatabaseField &&
      ((formType == 'V' && field.column == undefined && field.configuration.persistAsColumn) || formType === 'P')
    ) {
      let col = {
        uuid: field.id,
        title: field.configuration.name,
        notNull: false,
        dataType: 'varchar',
        length: field.configuration.length || 64
      };
      let dbDataType = field.configuration.dbDataType;
      if (field.wtype == 'WidgetFormInputNumber' || ['13', '131', '132', '14', '15', '12', '17'].indexOf(dbDataType) > -1) {
        col.dataType = 'number';
      }
      if (dbDataType == '17') {
        col.scale = field.configuration.scale || 0;
        col.length = field.configuration.precision;
      } else if (['15', '12'].includes(dbDataType)) {
        col.scale = field.configuration.decimalPlacesNumber;
      } else if (dbDataType == '16') {
        col.dataType = 'clob';
      } else if (dbDataType == '2') {
        col.dataType = 'timestamp';
        col.length = undefined;
        // 日期范围是两个字段
        if (field.wtype == 'WidgetFormDatePicker' && field.subtype == 'Range' && field.configuration.endDateField) {
          let endCol = {
            uuid: field.id + '_end',
            title: field.configuration.relaFieldConfigures[0].name || field.configuration.name + '(结束)',
            notNull: false,
            column: field.configuration.endDateField.toUpperCase(),
            dataType: 'timestamp',
            length: undefined
          };
          columnJson.push(endCol);
          col.relaColumns = [endCol]; // 设置关联字段，在数据模型设计表单时候使用
        }
      }
      col.column = field.configuration.code.toUpperCase();
      columnJson.push(deepClone(col));
      col.code = col.column;
      delete col.column;
      if (formType == 'V') {
        // 以数据模型为基础创建的展示表单，增加属性 column 作为字段组件渲染的判断
        field.column = col;
      }
    }
  }
  console.log('表单字段转数据模型字段json', columnJson);
  return columnJson;
};
// 下拉框菜单渲染父节点，解决下拉框与输入框分离的问题
export const getPopupContainerByPs = function () {
  return triggerNode => {
    if (triggerNode.closest('.ps')) {
      if (triggerNode.closest('.ps').clientHeight < 600) {
        // 页面高度小于60时，挂载到body下
        return document.body;
      } else {
        return triggerNode.closest('.ps');
      }
    }
    return triggerNode.parentNode;
  };
};
export const getDropdownClassName = function () {
  return 'ps__child--consume'; //阻止外层下拉滚动
};
