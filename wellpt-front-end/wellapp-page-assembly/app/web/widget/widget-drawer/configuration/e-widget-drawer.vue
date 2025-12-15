<template>
  <a-drawer
    :class="['pt-drawer edit-widget-drawer-wrap', selected ? 'selected' : '']"
    :wrap-style="{ position: 'absolute' }"
    :bodyStyle="{ padding: vPadding }"
    :title="widget.configuration.title"
    placement="right"
    :closable="widget.configuration.closable === true"
    :visible="visible"
    :mask="widget.configuration.mask === true"
    :width="widget.configuration.style.width"
    :zIndex="10"
    :get-container="false"
    @click.native.stop="evt => selectDrawer(evt)"
  >
    <div>
      <div v-show="selected && visible" class="widget-operation-buttons">
        <a-button size="small" icon="profile" title="查看JSON" @click.stop="showWidgetJsonDetail"></a-button>
        <a-button size="small" icon="eye-invisible" title="隐藏" @click.stop="noDrawerCancel"></a-button>
        <a-popconfirm title="确认要删除吗?" ok-text="删除" cancel-text="取消" @confirm="deleteSelf">
          <a-button size="small" icon="delete" type="danger" title="删除"></a-button>
        </a-popconfirm>
      </div>
      <draggable
        :list="widget.configuration.widgets"
        v-bind="{ group: draggableConfig.dragGroup, ghostClass: 'ghost', animation: 200 }"
        handle=".widget-drag-handler"
        @add="e => onDragAdd(e, widget.configuration.widgets)"
        @paste.native="onDraggablePaste"
        :move="onDragMove"
        :class="[!widget.configuration.widgets || widget.configuration.widgets.length === 0 ? 'widget-edit-empty' : 'widget-edit-content']"
      >
        <transition-group name="fade" tag="div" class="widget-drawer-drop-panel" style="min-height: 500px">
          <template v-for="(wgt, i) in widget.configuration.widgets">
            <WDesignItem
              :widget="wgt"
              :key="wgt.id"
              :index="i"
              :widgetsOfParent="widget.configuration.widgets"
              :designer="designer"
              :dragGroup="dragGroup"
              :parent="widget"
            />
          </template>
        </transition-group>
      </draggable>
      <div
        v-show="!widget.configuration.footerHidden"
        class="ant-drawer-footer"
        :style="{
          position: 'absolute',
          bottom: 0,
          width: '100%',
          borderTop: '1px solid #e8e8e8',
          padding: '10px 16px',
          textAlign: 'right',
          left: 0,
          zIndex: 1,
          background: '#fff',
          borderRadius: '0 0 4px 4px'
        }"
      >
        <WidgetTableButtons :button="widget.configuration.footerButton" :key="vButtonKey" />
      </div>
    </div>
  </a-drawer>
</template>
<style lang="less">
.edit-widget-drawer-wrap.selected div.ant-drawer-content-wrapper {
  border: 2px solid var(--w-primary-color) !important;
}
.widget-drawer-drop-panel {
  min-height: 500px;
}

.edit-widget-drawer-wrap.selected .widget-operation-buttons {
  position: absolute;
  left: 0px;
  z-index: 1001;
  top: 1px;
  button {
    border-radius: 0px;
    float: left;
  }
}
</style>

<script type="text/babel">
import editWgtMixin from '@framework/vue/designer/editWidgetMixin';
import draggable from '@framework/vue/designer/draggable';
export default {
  name: 'EWidgetDrawer',
  mixins: [editWgtMixin, draggable],
  inject: ['vProvideStyle'],
  data() {
    return {
      visible: false,
      buttonAlign: 'right',
      vButtonKey: this.widget.id + '_footerButton',
      panelStyle: {
        'min-height': '200px'
      }
    };
  },

  beforeCreate() {},
  components: {},
  computed: {
    defaultEvents() {
      return [
        { id: 'showDrawer', title: '打开抽屉浮层' },
        { id: 'closeDrawer', title: '关闭抽屉浮层' },
        { id: 'updateDrawerTitle', title: '更新抽屉浮层标题' }
      ];
    },
    bodyHeight() {
      return '';
    }
  },
  created() {},
  methods: {
    getContainer() {
      return false; //document.querySelector('.widget-drop-panel');
    },
    isSubWidgetSelected() {
      let current = this.designer.widgetTreeMap[this.designer.selectedId],
        parentKey = null;
      if (current) {
        parentKey = current.parentKey;
        while (parentKey && parentKey != this.widget.id) {
          current = this.designer.widgetTreeMap[parentKey];
          if (current && current.parentKey == this.widget.id) {
            return true;
          } else {
            parentKey = current.parentKey;
          }
        }
      }

      return parentKey == this.widget.id;
    },
    noDrawerCancel() {
      this.visible = false;
      this.designer.clearSelected();
    },
    selectDrawer() {
      this.selectWidget();
    }
  },
  mounted() {
    this.visible = this.widget.id == this.designer.selectedId || this.isSubWidgetSelected();
    this.mounted = true;
  },
  watch: {
    'widget.configuration.footerButton': {
      deep: true,
      handler(v) {
        this.vButtonKey += new Date().getTime();
      }
    },
    'designer.selectedId': {
      handler(v) {
        if (this.mounted && (v == this.widget.id || this.isSubWidgetSelected())) {
          this.visible = true;
        }
      }
    }
  }
};
</script>
