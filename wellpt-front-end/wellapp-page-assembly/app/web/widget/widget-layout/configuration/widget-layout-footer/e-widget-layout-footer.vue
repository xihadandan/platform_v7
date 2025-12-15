<template>
  <a-layout-footer
    :class="['widget-layout-footer', selected ? 'selected' : '']"
    @click.native.stop="selectWidget(widget)"
    v-if="widget.configuration.visible"
  >
    <span class="widget-title" v-show="selected">{{ widget.title }}</span>
    <div class="widget-operation-buttons" v-show="selected">
      <a-button size="small" icon="vertical-align-top" v-show="hasParent" title="选中父组件" @click.stop="selectWidget(parent)"></a-button>
      <a-button size="small" icon="profile" type="danger" title="查看JSON" @click.stop="showWidgetJsonDetail"></a-button>
    </div>
    <draggable
      :list="widget.configuration.widgets"
      v-bind="{ group: dragGroup, ghostClass: 'ghost', animation: 200 }"
      @end="evt => onGridDragEnd(evt, widget.configuration.widgets)"
      @add="evt => onGridDragAdd(evt, widget.configuration.widgets)"
      @update="onGridDragUpdate"
      @paste.native="onDraggablePaste"
    >
      <transition-group name="fade" tag="div" class="grid-col-drop-panel">
        <template v-for="(wgt, i) in widget.configuration.widgets">
          <WDesignItem
            :widget="wgt"
            :key="wgt.id"
            :index="i"
            :widgetsOfParent="widget.configuration.widgets"
            :designer="designer"
            :parent="widget"
            :dragGroup="dragGroup"
          />
        </template>
      </transition-group>
    </draggable>
  </a-layout-footer>
</template>
<style scoped>
.widget-edit-wrapper .widget-layout-footer {
  padding: 2px;
  outline: 1px dotted #d5d5d5;
}
.widget-edit-wrapper .grid-col-drop-panel {
  min-height: 30px;
}
.widget-layout-footer.selected {
  position: relative;
  outline: 2px solid #409eff;
}
.widget-layout-footer.selected .widget-title {
  top: -2px;
  line-height: normal;
  left: -3px;
}

.widget-layout-footer.selected .widget-operation-buttons {
  line-height: 0px;
  top: 0px;
}
</style>
<script type="text/babel">
import editWgtMixin from '@framework/vue/designer/editWidgetMixin';
import draggable from '@framework/vue/designer/draggable';

export default {
  name: 'EWidgetLayoutFooter',
  mixins: [editWgtMixin, draggable],
  inject: ['layoutFixed'],
  data() {
    return {};
  },

  beforeCreate() {},
  components: {},
  computed: {},
  created() {},
  methods: {
    selectWidget(wgt) {
      if (this.layoutFixed) {
        return;
      }
      this.designer.setSelected(wgt);
    },
    getWidgetDefinitionElements(widget) {
      // 由布局组件提供
      return [];
    },
    onGridDragEnd(evt, subWidgets) {
      //
    },

    onGridDragEnd(evt, subWidgets) {
      //
    },

    onGridDragAdd(evt, subWidgets) {
      const newIndex = evt.newIndex;
      if (!!subWidgets[newIndex]) {
        this.designer.setSelected(subWidgets[newIndex]);
      }

      // this.designer.emitEvent('field-selected', this.widget);
    },

    onGridDragUpdate() {}
  },
  mounted() {
    if (this.layoutFixed && this.parent.main) {
      this.designer.unselectableWidgetIds.push(this.widget.id);
    }
  }
};
</script>
