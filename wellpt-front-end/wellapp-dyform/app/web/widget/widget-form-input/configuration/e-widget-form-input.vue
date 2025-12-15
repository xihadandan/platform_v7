<template>
  <EditWrapper
    :showWidgetName="false"
    :widget="widget"
    :index="index"
    :widgetsOfParent="widgetsOfParent"
    :designer="designer"
    :parent="parent"
  >
    <WidgetFormInput :widget="widget" :widgetsOfParent="widgetsOfParent" :index="index" :parent="parent" class="design-mode">
      <template slot="designAddonAfterSlot">
        <div
          :class="
            widget.configuration.addonAfterSlot &&
            (widget.configuration.addonAfterSlotWidgets == undefined || widget.configuration.addonAfterSlotWidgets.length == 0)
              ? 'zone-draggable'
              : undefined
          "
          v-if="widget.configuration != undefined && widget.configuration.addonAfterSlot"
        >
          <draggable
            :list="widget.configuration.addonAfterSlotWidgets"
            v-bind="{ group: draggableConfig.dragGroup, ghostClass: 'ghost', animation: 200 }"
            handle=".widget-drag-handler"
            :style="{ width: '100%', 'min-height': '30px', outline: '1px dotted #e8e8e8' }"
            :move="onDragMove"
            @add="e => onDragAdd(e, widget.configuration.addonAfterSlotWidgets)"
          >
            <template v-for="(wgt, index) in widget.configuration.addonAfterSlotWidgets">
              <WDesignItem
                :widget="wgt"
                :key="wgt.id"
                :index="index"
                :widgetsOfParent="widget.configuration.addonAfterSlotWidgets"
                :designer="designer"
                :parent="widget"
                :ref="'item_label_' + index"
                :dragGroup="draggableConfig.dragGroup"
              />
            </template>
          </draggable>
        </div>
      </template>
    </WidgetFormInput>
  </EditWrapper>
</template>
<style lang="less">
.widget-form-input.design-mode {
  .outside-addonAfter {
    min-width: 100px;
  }
  .ant-input {
    pointer-events: none;
  }
  .zone-draggable {
    // position: relative;
    // &::before {
    //   content: '该区域可拖拽组件';
    //   position: absolute;
    //   left: 50%;
    //   transform: translateX(-50%);
    //   font-size: 11px;
    //   color: #cecece;
    //   bottom: 1px;
    //   line-height: normal;
    // }
  }
}
</style>
<script type="text/babel">
import draggable from '@framework/vue/designer/draggable';
import { generateId, deepClone } from '@framework/vue/utils/util';
import WidgetFormInput from '../widget-form-input.vue';
import editWgtMixin from '@framework/vue/designer/editWidgetMixin';
import defaultFieldEventMixins from '@dyform/app/web/widget/mixin/defaultFieldEventMixins.js';

export default {
  mixins: [editWgtMixin, draggable, defaultFieldEventMixins],
  name: 'EWidgetFormInput',
  props: {},
  components: { WidgetFormInput },
  computed: {},
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
