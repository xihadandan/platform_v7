<template>
  <EditWrapper :widget="widget" :index="index" :widgetsOfParent="widgetsOfParent" :designer="designer" :parent="parent">
    <template slot="extra-buttons">
      <a-select
        :options="vStateOptions"
        v-model="activeStateIndex"
        size="small"
        class="widget-state-panel-select"
        style="width: 150px"
        :dropdownStyle="{
          'min-width': '80px'
        }"
      />
    </template>
    <template v-for="(t, i) in widget.configuration.statePanels">
      <div :key="'statepanel_' + i" v-show="activeStateIndex == i">
        <!-- 组件加载 -->
        <draggable
          :list="t.configuration.widgets"
          v-bind="{ group: draggableConfig.dragGroup, ghostClass: 'ghost', animation: 200 }"
          handle=".widget-drag-handler"
          @add="e => onDragAdd(e, t.configuration.widgets)"
          @paste.native="onDraggablePaste"
          :class="[!t.configuration.widgets || t.configuration.widgets.length === 0 ? 'widget-edit-empty' : 'widget-edit-content']"
        >
          <transition-group name="fade" tag="div" :style="{ minHeight: '400px' }">
            <template v-for="(wgt, i) in t.configuration.widgets">
              <WDesignItem
                :widget="wgt"
                :key="wgt.id"
                :index="i"
                :widgetsOfParent="t.configuration.widgets"
                :designer="designer"
                :parent="widget"
                :dragGroup="dragGroup"
              />
            </template>
          </transition-group>
        </draggable>
      </div>
    </template>
  </EditWrapper>
</template>
<style lang="less">
.widget-state-panel-select {
  .ant-select-arrow {
    font-size: 12px;
    color: rgba(0, 0, 0, 0.65);
    right: 4px;
    margin-top: 1px;
  }
  &:hover {
    .ant-select-arrow {
      font-size: 12px;
      border-color: var(--w-button-primary-background-focus);
      color: var(--w-button-primary-background-focus);
    }
  }
  & > .widget-edit-wrapper > .widget-edit-container > div > * {
    margin-left: 0px !important;
    margin-right: 0px !important;

    &.widget-table,
    &.widget-button,
    & > .widget-tree,
    &.widget-data-manager-view > .widget-table {
      padding: var(--w-padding-xs) var(--w-padding-md);
    }
  }
}
</style>
<script type="text/babel">
import editWgtMixin from '@framework/vue/designer/editWidgetMixin';
import draggable from '@framework/vue/designer/draggable';
export default {
  name: 'EWidgetStatePanel',
  mixins: [editWgtMixin, draggable],
  data() {
    return {
      activeStateIndex: 0,
      autoRefresh: false
    };
  },

  beforeCreate() {},
  components: {},
  computed: {
    // 相关方法暴露为事件，提供外部调用
    defaultEvents() {
      let events = [
        {
          id: 'setState',
          title: '设置状态',
          eventParams: [
            {
              paramKey: 'state',
              required: true,
              remark: '选择状态面板',
              valueSource: {
                inputType: 'select', // multi-select , checkbox , radio, input
                options: (() => {
                  let scope = [];
                  if (this.widget.configuration.statePanels) {
                    for (let i = 0, len = this.widget.configuration.statePanels.length; i < len; i++) {
                      scope.push({
                        value: this.widget.configuration.statePanels[i].id,
                        label: this.widget.configuration.statePanels[i].title
                      });
                    }
                  }
                  return scope;
                })()
              }
            }
          ]
        }
      ];
      // if (this.widget.configuration.statePanels) {
      //   for (let s of this.widget.configuration.statePanels) {
      //     events.push({
      //       id: `setStateTo${s.id}`,
      //       title: `设置状态为${s.title}`
      //     });
      //   }
      // }

      return events;
    },
    vStateOptions() {
      let opt = [];
      if (this.widget.configuration && this.widget.configuration.statePanels != undefined) {
        for (let i = 0, len = this.widget.configuration.statePanels.length; i < len; i++) {
          opt.push({ value: i, label: this.widget.configuration.statePanels[i].title });
        }
      }
      return opt;
    }
  },
  created() {},
  methods: {}
};
</script>
