<template>
  <EditWrapper
    :widget="widget"
    :index="index"
    :widgetsOfParent="widgetsOfParent"
    :designer="designer"
    :parent="parent"
    :children="vChildren"
    :unselectable="layoutFixed && widget.main"
    :class="[
      !widget.configuration.footer.configuration.visible ? 'no-footer' : '',
      !widget.configuration.header.configuration.visible ? 'no-header' : ''
    ]"
  >
    <WidgetLayout :widget="widget" :index="index" :widgetsOfParent="widgetsOfParent" :designer="designer" :parent="parent" />
  </EditWrapper>
</template>

<script type="text/babel">
import editWgtMixin from '@framework/vue/designer/editWidgetMixin';
import { generateId } from '@framework/vue/utils/util';
export default {
  name: 'EWidgetLayout',
  mixins: [editWgtMixin],
  inject: ['layoutFixed'],
  data() {
    return {};
  },

  beforeCreate() {},
  components: {},
  computed: {
    // 相关方法暴露为事件，提供外部调用
    defaultEvents() {
      return [{ id: 'restoreLayoutContent', title: '重置布局内容区' }];
    },
    vChildren() {
      let children = [];
      children.push(this.widget.configuration.header);
      children.push(this.widget.configuration.content);
      if (this.widget.configuration.layoutType != 'topMiddleBottom') {
        children.push(this.widget.configuration.sider);
      }
      children.push(this.widget.configuration.footer);
      return children;
    }
  },
  created() {
    // 初始化配置
  },

  methods: {},
  mounted() {
    this.pageContext.emitEvent(`EWidgetLayout:Design:Mounted`, this.widget);
    if (this.layoutFixed && this.widget.main) {
      this.designer.unselectableWidgetIds.push(this.widget.id);
    }
  },
  beforeDestroy() {}
};
</script>
