import VueWidgetDevelopment from './VueWidgetDevelopment';

class WidgetRefTableToFieldDevelopment extends VueWidgetDevelopment {

  getTable() {
    return this.$widget.refs.widgetTable;
  }

  get ROOT_CLASS() {
    return 'WidgetRefTableToFieldDevelopment'
  }
}

export default WidgetRefTableToFieldDevelopment;
