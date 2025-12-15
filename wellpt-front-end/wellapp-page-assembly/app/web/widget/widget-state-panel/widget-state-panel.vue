<template>
  <div class="widget-state-panel">
    <template v-for="(panel, i) in widget.configuration.statePanels">
      <div :key="'statepanel_' + panel.id" v-if="panel.id === activeState">
        <template v-for="(wgt, index) in panel.configuration.widgets">
          <component
            :key="wgt.id"
            :is="resolveWidgetType(wgt)"
            :widget="wgt"
            :parent="widget"
            :index="index"
            :widgetsOfParent="panel.configuration.widgets"
            :designer="designer"
            v-bind="wgt.props"
          ></component>
        </template>
      </div>
    </template>
  </div>
</template>

<script type="text/babel">
import widgetMixin from '@framework/vue/mixin/widgetMixin';

export default {
  name: 'WidgetStatePanel',
  mixins: [widgetMixin],
  props: {},

  data() {
    let activeState = this.widget.configuration.defaultActiveState;
    return {
      state: undefined,
      activeState
    };
  },
  watch: {},
  beforeCreate() {},
  components: {},
  computed: {
    // 相关方法暴露为事件，提供外部调用
    defaultEvents() {
      let events = [{ id: 'setState', title: '设置状态', eventParams: [{ paramKey: 'state', required: true, remark: '状态编码' }] }];
      // if (this.widget.configuration.statePanels) {
      //   for (let s of this.widget.configuration.statePanels) {
      //     events.push({
      //       id: `setStateTo${s.id}`,
      //       title: `设置状态为${s.title}`
      //     });
      //   }
      // }

      return events;
    }
  },
  created() {
    // 初始化方法
    // if (this.widget.configuration.statePanels) {
    //   for (let s of this.widget.configuration.statePanels) {
    //     this[`setStateTo${s.id}`] = () => {
    //       this.activeState = s.id;
    //     };
    //   }
    // }
  },
  methods: {
    setState(e) {
      let state = undefined;
      if (typeof e == 'string') {
        state = e;
      } else if (e.eventParams != undefined) {
        state = e.eventParams.state;
      }
      for (let panel of this.widget.configuration.statePanels) {
        if (panel.id === state || panel.title === state) {
          this.activeState = panel.id;
          break;
        }
      }
    }
  },
  beforeMount() {},
  mounted() {},
  beforeDestroy() {}
};
</script>
<style scoped lang="less">
.widget-state-panel {
  > div {
    > .widget-table,
    > .widget-button,
    > div > .widget-tree,
    > .widget-data-manager-view > .ant-layout,
    > .widget-data-manager-view > .widget-table {
      // 页面直接套表格，表格增加内边距
      padding: var(--w-padding-xs) var(--w-padding-md);
    }
  }
}
</style>
