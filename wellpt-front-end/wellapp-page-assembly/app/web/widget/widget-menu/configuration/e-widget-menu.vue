<template>
  <EditWrapper
    :widget="widget"
    :index="index"
    :widgetsOfParent="widgetsOfParent"
    :designer="designer"
    :parent="parent"
    :unselectable="layoutFixed && widget.main"
    :widgetDisplayAsReadonly="true"
  >
    <WidgetMenu :widget="widget" :index="index" :widgetsOfParent="widgetsOfParent" :designer="designer" :parent="parent"></WidgetMenu>
  </EditWrapper>
</template>
<script type="text/babel">
import editWgtMixin from '@framework/vue/designer/editWidgetMixin';
import { generateId } from '@framework/vue/utils/util';
export default {
  name: 'EWidgetMenu',
  mixins: [editWgtMixin],
  inject: ['layoutFixed'],
  data() {
    return {};
  },

  beforeCreate() {},
  components: {},
  computed: {
    isEmpty() {
      return (
        this.widget &&
        this.widget.configuration.menus.length == 0 &&
        (!this.widget.configuration.enableSysMenu ||
          (this.widget.configuration.enableSysMenu && this.widget.configuration.sysFunctionMenus.length == 0))
      );
    }
  },
  created() {},
  methods: {},
  mounted() {
    this.pageContext.emitEvent(`EWidgetMenu:Design:Mounted`, this.widget);
    if (this.layoutFixed && this.widget.main) {
      this.designer.unselectableWidgetIds.push(this.widget.id);
    }
  },
  watch: {
    // 'widget.configuration': {
    //   deep: true,
    //   handler(v) {
    //     this.refreshWidget();
    //   }
    // }
  }
};
</script>
