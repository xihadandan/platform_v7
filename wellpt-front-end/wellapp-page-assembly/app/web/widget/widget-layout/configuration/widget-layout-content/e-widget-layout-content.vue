<template>
  <a-layout-content
    :class="['widget-layout-content', selected ? 'selected' : '']"
    @click.native.stop="selectWidget(widget)"
    :style="{
      height: 'calc(100vh - ' + vContentHeight + ')',
      ...vContentStyle
    }"
  >
    <span class="widget-title" v-show="selected">{{ widget.title }}</span>
    <div class="widget-operation-buttons" v-show="selected">
      <a-button size="small" icon="vertical-align-top" v-show="hasParent" title="选中父组件" @click.stop="selectWidget(parent)"></a-button>
      <a-button size="small" icon="profile" type="danger" title="查看JSON" @click.stop="showWidgetJsonDetail"></a-button>
    </div>
    <PerfectScrollbar
      :style="{
        height: 'calc(100vh - ' + vContentHeight + ' - 10px)',
        position: 'relative',
        'overflow-x': 'hidden',
        width: 'calc(100% - 1px)'
      }"
      :options="{ suppressScrollX: true }"
      ref="scroll"
    >
      <a-tabs
        class="layout-content-tabs"
        v-if="widget.configuration.contentAsTabs"
        :activeKey="widget.id + '_tab_0'"
        type="editable-card"
        :hideAdd="true"
        ref="tabs"
      >
        <a-tab-pane
          :key="widget.id + '_tab_0'"
          :tab="widget.configuration.defaultTabTitle || '首页'"
          :closable="widget.configuration.defaultTabClosable !== false"
        >
          <draggable
            :list="widget.configuration.widgets"
            v-bind="{ group: dragGroup, ghostClass: 'ghost', animation: 200 }"
            @end="evt => onGridDragEnd(evt, widget.configuration.widgets)"
            @add="evt => onGridDragAdd(evt, widget.configuration.widgets)"
            @update="onGridDragUpdate"
            @paste.native="onDraggablePaste"
          >
            <transition-group
              name="fade"
              tag="div"
              class="widget-layout-content-drop-panel"
              :style="{
                height: 'auto',
                minHeight: '300px'
              }"
            >
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
        </a-tab-pane>
      </a-tabs>
      <template v-else>
        <draggable
          :list="widget.configuration.widgets"
          v-bind="{ group: dragGroup, ghostClass: 'ghost', animation: 200 }"
          @end="evt => onGridDragEnd(evt, widget.configuration.widgets)"
          @add="evt => onGridDragAdd(evt, widget.configuration.widgets)"
          @update="onGridDragUpdate"
          @paste.native="onDraggablePaste"
        >
          <transition-group
            name="fade"
            tag="div"
            class="widget-layout-content-drop-panel"
            :style="{
              height: 'auto',
              minHeight: '300px'
            }"
          >
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
      </template>
    </PerfectScrollbar>
  </a-layout-content>
</template>
<style lang="less" scoped>
.widget-edit-wrapper .widget-layout-content .ps {
  width: e('calc(100vh + 30px)');
  padding: 2px;
  position: absolute;
}

.widget-edit-wrapper .widget-layout-content {
  // padding: 2px;
  // outline: 1px dotted #d5d5d5;
}
.widget-layout-content.selected {
  position: relative;
  outline: 2px solid var(--w-primary-color);
  // box-shadow: 4px 4px 5px var(--w-primary-color);
}
.widget-layout-content.selected .widget-title {
  top: -2px;
  line-height: normal;
  left: -3px;
}

.widget-layout-content.selected .widget-operation-buttons {
  line-height: 0px;
  top: 0px;
}
</style>
<style lang="less">
.widget-edit-wrapper {
  .widget-layout-content {
    .layout-content-tabs {
      // background: var(--w-widget-page-layout-bg-color);

      // > .ant-tabs-content {
      //   padding: var(--w-padding-xs) var(--w-padding-md);
      // }

      > .ant-tabs-card-bar {
        margin: 0px;
        background: var(--w-widget-block-bg-color);
        border-bottom: 0;
        color: var(--w-text-color-dark);

        .ant-tabs-nav-container {
          line-height: 48px;
          height: 48px;
        }

        .ant-tabs-tab {
          border-radius: 8px 8px 0 0;
          border: 0;
          background-color: var(--w-widget-block-bg-color);
          line-height: 48px;
          height: 48px;
          margin: 0;
          padding: 0 var(--w-padding-md);
          position: relative;
          font-size: var(--w-font-size-base);

          &:hover {
            color: var(--w-primary-color);
          }

          &::after {
            content: '';
            position: absolute;
            width: 1px;
            height: var(--w-font-size-base);
            right: 0;
            top: 50%;
            margin-top: e('calc(0px - var(--w-font-size-base) / 2)');
            background-color: var(--w-border-color-light);
          }

          &-active {
            background-color: var(--w-widget-page-layout-bg-color);
            color: var(--w-primary-color);
            font-weight: bold;
          }

          .ant-tabs-close-x {
            color: var(--w-text-color-light);
            padding-top: 2px;
            border-radius: var(--w-border-radius-2);
            height: 16px;

            &:hover {
              color: var(--w-text-color-base);
              background-color: var(--w-gray-color-5);
            }
          }
        }
      }
    }
    .widget-layout-content-drop-panel {
      > .design-row {
        > .widget-edit-wrapper {
          > .widget-edit-container {
            > div {
              > .widget-table,
              > .widget-button,
              > div > .widget-tree,
              > .widget-data-manager-view > .widget-table {
                padding: var(--w-padding-xs) var(--w-padding-md);
              }
            }
          }
        }
      }
    }
  }
}
</style>
<script type="text/babel">
import editWgtMixin from '@framework/vue/designer/editWidgetMixin';
import draggable from '@framework/vue/designer/draggable';

export default {
  name: 'EWidgetLayoutContent',
  mixins: [editWgtMixin, draggable],
  inject: ['layoutFixed'],
  data() {
    return {
      // scrollerHeight: '300px',
      // layoutContentHeight: 300,
    };
  },

  beforeCreate() {},
  components: {},
  computed: {
    vContentStyle() {
      let style = {};
      if (this.widget.configuration.contentAsTabs) {
        style.margin = '0';
      }
      return style;
    },
    vContentWidth() {
      let layoutType = this.parent.configuration.layoutType;
      if (layoutType.toLowerCase().indexOf('sider') != -1 && this.parent.configuration.sider.configuration.visible) {
        return 225 + 'px';
      }
      return 25 + 'px';
    },
    vContentHeight() {
      let num = 270;
      if (!this.parent.configuration.header.configuration.visible) {
        num -= 70;
      }
      if (!this.parent.configuration.footer.configuration.visible) {
        num -= 40;
      }
      return num + 'px';
    }
    // selected() {
    //   return !!this.designer && this.parent.id === this.designer.selectedId;
    // }
  },
  created() {},
  methods: {
    selectWidget(wgt) {
      if (this.layoutFixed && this.parent.main) {
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

    onGridDragUpdate() {},
    updateHeight() {
      //TODO
    }
  },
  mounted() {
    // this.layoutContentHeight = window.innerHeight - 260 ;
    // addWindowResizeHandler(() => {
    //   this.$nextTick(() => {
    //     this.layoutContentHeight = window.innerHeight - 260  ;
    //   });
    // });

    if (this.layoutFixed && this.parent.main) {
      this.designer.unselectableWidgetIds.push(this.widget.id);
    }
  }
};
</script>
