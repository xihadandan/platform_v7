<template>
  <EditWrapper
    :widget="widget"
    :index="index"
    :widgetsOfParent="widgetsOfParent"
    :designer="designer"
    :parent="parent"
    :widgetDisplayAsReadonly="true"
  >
    <template v-if="designer.terminalType == 'pc'">
      <WidgetTable :widget="widget" :widgetsOfParent="widgetsOfParent" :index="index">
        <template slot="designBeforeTableHeaderSlot">
          <div
            v-if="
              widget.configuration != undefined &&
              widget.configuration.enableBeforeTableHeaderWidget &&
              widget.configuration.beforeTableHeaderWidgets != undefined &&
              widget.configuration.beforeTableHeaderWidgets
            "
            style="position: sticky; z-index: 3"
            :class="
              widget.configuration.enableBeforeTableHeaderWidget &&
              (widget.configuration.beforeTableHeaderWidgets == undefined || widget.configuration.beforeTableHeaderWidgets.length == 0)
                ? 'zone-draggable'
                : undefined
            "
          >
            <draggable
              :list="widget.configuration.beforeTableHeaderWidgets"
              v-bind="{ group: draggableConfig.dragGroup, ghostClass: 'ghost', animation: 200 }"
              handle=".widget-drag-handler"
              :style="{ width: '100%', 'min-height': '30px', outline: '1px dotted #e8e8e8' }"
              :move="onDragMove"
              @add="e => onDragAdd(e, widget.configuration.beforeTableHeaderWidgets)"
            >
              <template v-for="(wgt, index) in widget.configuration.beforeTableHeaderWidgets">
                <WDesignItem
                  :widget="wgt"
                  :key="wgt.id"
                  :index="index"
                  :widgetsOfParent="widget.configuration.beforeTableHeaderWidgets"
                  :designer="designer"
                  :parent="widget"
                  :ref="'item_label_' + index"
                  :dragGroup="draggableConfig.dragGroup"
                />
              </template>
            </draggable>
          </div>
        </template>
      </WidgetTable>
    </template>
    <template v-else>
      <widgetUniListView :widget="widget" :widgetsOfParent="widgetsOfParent" :index="index"></widgetUniListView>
    </template>
  </EditWrapper>
</template>
<style lang="less">
.widget-table {
  .zone-draggable {
    position: relative;
    &::before {
      content: '该区域可拖拽组件';
      position: absolute;
      left: 50%;
      transform: translateX(-50%);
      font-size: 12px;
      color: #cecece;
      bottom: 5px;
    }
  }
}
</style>
<script type="text/babel">
import draggable from '@framework/vue/designer/draggable';
import { generateId, deepClone } from '@framework/vue/utils/util';
import WidgetTable from '../widget-table.vue';
import editWgtMixin from '@framework/vue/designer/editWidgetMixin';
import widgetUniListView from '../../mobile/widget-uni-list-view/widget-uni-list-view.vue';

export default {
  mixins: [editWgtMixin, draggable],
  name: 'EWidgetTable',
  props: {},
  components: { WidgetTable, widgetUniListView },
  computed: {
    // 相关方法暴露为事件，提供外部调用
    defaultEvents() {
      return [{ id: 'refetch', title: '重新加载数据' }];
    }
  },
  data() {
    return {};
  },
  beforeCreate() {},
  created() {},
  beforeMount() {},
  mounted() {},
  methods: {}
};
</script>
