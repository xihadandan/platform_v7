<template>
  <a-skeleton />
</template>
<script type="text/babel">
import editWgtMixin from '@framework/vue/designer/editWidgetMixin';

export default {
  name: 'EWidgetCloneTemplatePlaceholder',
  mixins: [editWgtMixin],
  data() {
    return { idMap: {} };
  },
  beforeDestroy() {},
  beforeCreate() {},
  components: {},
  computed: {},
  created() {},
  methods: {
    reCreateWidgetId() {
      // for(let item of this.widget.configuration.items){
      //   this.idMap[item.id] = generateId();
      //   item.id = this.idMap[item.id];// 新值
      //   // 遍历配置项，凡是有用到id都修改掉
      // }
    }
  },
  mounted() {
    if (this.widgetsOfParent != undefined) {
      this.widgetsOfParent.splice(this.index, 1, ...this.widget.configuration.items);
      this.designer.clearSelected();
      this.$delete(this.designer.widgetIdMap, this.widget.id);
      this.$delete(this.designer.widgetTreeMap, this.widget.id);
    }
  }
};
</script>
