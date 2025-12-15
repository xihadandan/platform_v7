import VueWidgetDevelopment from './VueWidgetDevelopment';

class WidgetDataManagerViewDevelopment extends VueWidgetDevelopment {
  constructor($widget) {
    super($widget);
  }

  getTable() {
    return this.$widget.$refs.table
  }

  refetchTable(options) {
    this.getTable().refetch(options);
  }

  get ROOT_CLASS() {
    return 'WidgetDataManagerViewDevelopment';
  }
}

export default WidgetDataManagerViewDevelopment;
