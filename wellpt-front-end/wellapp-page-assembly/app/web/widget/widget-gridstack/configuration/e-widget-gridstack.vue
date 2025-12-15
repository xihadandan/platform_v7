<template>
  <EditWrapper
    :widget="widget"
    :index="index"
    :widgetsOfParent="widgetsOfParent"
    :designer="designer"
    :parent="parent"
    :children="widget.configuration.gridItems"
  >
    <div class="grid-stack" :id="gridstackInstId">
      <template v-for="(item, t) in widget.configuration.gridItems">
        <div
          :key="'grid-item-' + item.id"
          :class="['grid-stack-item', designer.selectedId == item.id ? 'selected' : undefined]"
          :gs-w="item.configuration.itemPosition.w"
          :gs-x="item.configuration.itemPosition.x"
          :gs-y="item.configuration.itemPosition.y"
          :gs-h="item.configuration.itemPosition.h"
          :gs-id="item.id"
          :id="item.id"
          @click.stop="selectWidget(item, widget)"
        >
          <div class="grid-stack-item-widget-select-toolbar widget-operation-buttons" v-show="designer.selectedId == item.id">
            <a-button size="small" icon="vertical-align-top" title="选中父组件" @click.stop="selectWidget(widget)"></a-button>
            <a-button size="small" icon="profile" title="查看JSON" @click.stop="showWidgetJsonDetail"></a-button>
            <a-button size="small" icon="delete" type="danger" @click.stop="deleteGridstackItem(t)" title="删除"></a-button>
          </div>
          <div class="grid-stack-item-toolbar">
            <label style="font-size: 12px; font-weight: bolder; color: red">{{ item.configuration.itemPosition.h * cellHeight }}px</label>
            <Icon
              type="pticon iconfont icon-ptkj-tuodong"
              class="grid-stack-item-drag-handler"
              title="拖动位置"
              :style="{ cursor: 'move' }"
            />
          </div>
          <div
            class="grid-stack-item-content"
            :style="{
              borderRadius: formateBorderRadius(item.configuration.style.borderRadius)
            }"
          >
            <div>
              <span class="widget-title" v-show="designer.selectedId == item.id">{{ item.title }}</span>
              <draggable
                :list="item.configuration.widgets"
                v-bind="{ group: draggableConfig.dragGroup, ghostClass: 'ghost', animation: 200 }"
                @add="e => onDragAdd(e, item.configuration.widgets)"
                :move="onDragMove"
                handle=".widget-drag-handler, .widget-edit-wrapper.dragable"
                @paste.native="onDraggablePaste"
              >
                <transition-group
                  name="fade"
                  tag="div"
                  :style="{
                    minHeight: item.configuration.widgets.length == 0 ? '100px' : 'unset'
                  }"
                  class="grid-stack-item-drop"
                >
                  <template v-for="(wgt, i) in item.configuration.widgets">
                    <WDesignItem
                      :widget="wgt"
                      :key="wgt.id"
                      :index="i"
                      :widgetsOfParent="item.configuration.widgets"
                      :designer="designer"
                      :dragGroup="dragGroup"
                      :parent="item"
                      @mounted="resizeToContent(item.id)"
                    />
                  </template>
                </transition-group>
              </draggable>
            </div>
          </div>
        </div>
      </template>
    </div>

    <div style="text-align: center">
      <a-button icon="plus" type="link" @click="addItem">添加单元格</a-button>
    </div>
  </EditWrapper>
</template>
<style scope lang="less">
.widget-edit-container {
  .grid-stack-item-drop {
    > .design-row {
      &:last-child {
        & > .widget-edit-wrapper > .widget-edit-container > div > * {
          margin-bottom: 0px !important;
          &.widget-refrence {
            > *:last-child {
              margin-bottom: 0px !important;
            }
          }
        }
      }
    }
  }
  .grid-stack-item-content {
    overflow: hidden !important;
    outline: 1px dashed #d5d5d5;
  }
  .grid-stack-item {
    &[gs-x='0'] {
      > .grid-stack-item-content {
        inset: 0px 0px 12px 0px !important;
      }
    }
    &.selected {
      > .grid-stack-item-content {
        outline: 2px solid var(--w-primary-color);
      }
    }
    > .grid-stack-item-widget-select-toolbar {
      position: absolute;
      top: 0px;
      right: 0px;
      z-index: 5;
    }
    > .grid-stack-item-toolbar {
      position: absolute;
      bottom: 10px;
      right: 22px;
      font-size: 17px;
      display: none;
      cursor: pointer;
      z-index: 5;
    }
    &:hover {
      > .grid-stack-item-toolbar {
        display: block;
      }
    }
  }
}
</style>
<script type="text/babel">
import editWgtMixin from '@framework/vue/designer/editWidgetMixin';
import draggable from '@framework/vue/designer/draggable';
import { generateId } from '@framework/vue/utils/util';
import 'gridstack/dist/gridstack.min.css';
import 'gridstack/dist/gridstack-extra.min.css';
import { GridStack } from 'gridstack/dist/es5/gridstack';
import { CELL_HEIGHT } from '../constant';

export default {
  name: 'EWidgetGridstack',
  mixins: [editWgtMixin, draggable],
  data() {
    return {
      cellHeight: CELL_HEIGHT,
      gridstackInstId: `gs-inst-${generateId(6)}`
    };
  },

  beforeCreate() {},
  components: {},
  computed: {
    vColumn() {
      return this.widget.configuration.column;
    }
  },
  created() {},
  methods: {
    formateBorderRadius(borderRadius) {
      if (borderRadius != undefined) {
        if (typeof borderRadius == 'number') {
          return `${borderRadius}px`;
        }
        return borderRadius.join('px ') + 'px';
      }
    },
    resizeToContent(id) {
      if (this.grid) {
        let items = this.$el.querySelectorAll('.grid-stack-item');
        if (items && this.grid) {
          for (let item of items) {
            if ((id && item.getAttribute('id') == id) || id == undefined) {
              this.grid.resizeToContent(item, true);
            }
          }
        }

        let gridItems = this.grid.save(),
          map = {};
        for (let item of gridItems) {
          map[item.id] = item;
        }
        for (let gridItem of this.widget.configuration.gridItems) {
          if (map[gridItem.id]) {
            gridItem.configuration.itemPosition.x = map[gridItem.id].x;
            gridItem.configuration.itemPosition.y = map[gridItem.id].y;
            gridItem.configuration.itemPosition.w = map[gridItem.id].w || 1;
            gridItem.configuration.itemPosition.h = map[gridItem.id].h;
          }
        }
      }
    },
    deleteGridstackItem(index) {
      let item = this.widget.configuration.gridItems[index];
      this.grid.removeWidget(`#${item.id}`);
      this.$delete(this.designer.widgetTreeMap, item.id);
      this.$delete(this.designer.widgetIdMap, item.id);
      this.widget.configuration.gridItems.splice(index, 1);
      this.$nextTick(() => {
        this.designer.clearSelected();
        this.grid.save();
      });
    },
    addItem() {
      let id = generateId();

      this.widget.configuration.gridItems.push({
        title: '单元格',
        category: 'basicContainer',
        wtype: 'GridstackItem',
        id,
        configuration: {
          style: { borderRadius: 0 },
          itemPosition: {
            w: 1,
            h: 12
          },
          widgets: []
        }
      });
      this.$nextTick(() => {
        this.grid.makeWidget(`#${id}`);
        this.resizeToContent(id);
        let nodes = this.grid.save();
        let map = {};
        for (let n of nodes) {
          map[n.id] = n;
        }
        for (let item of this.widget.configuration.gridItems) {
          if (item.id == id) {
            item.configuration.itemPosition.x = map[item.id].x;
            item.configuration.itemPosition.y = map[item.id].y;
            item.configuration.itemPosition.w = map[item.id].w;
            item.configuration.itemPosition.h = map[item.id].h;
            break;
          }
        }
      });
    },
    refresh() {
      this.grid.destroy(false);
      this.initGrid();
    },
    initGrid() {
      this.$nextTick(() => {
        this.grid = GridStack.init(
          {
            column: this.vColumn,
            handleClass: 'grid-stack-item-drag-handler',
            cellHeight: this.cellHeight,
            cellHeightThrottle: 1,
            sizeToContent: false,
            margin: '0px 0px 12px 12px'
            // resizable: { handles: 'n,ne,e,se,s,sw,w,nw' }
          },
          `#${this.gridstackInstId}`
        );
        // this.grid.column(this.vColumn, 'compact');
        // this.$nextTick(() => {
        //   this.resizeToContent();
        // });

        this.grid.on('change', (e, items) => {
          console.log('change', items);
          let map = {};
          for (let i of this.widget.configuration.gridItems) {
            map[i.id] = i;
          }
          if (items) {
            for (let i of items) {
              map[i.id].configuration.itemPosition.x = i.x;
              map[i.id].configuration.itemPosition.y = i.y;
              map[i.id].configuration.itemPosition.w = i.w;
              map[i.id].configuration.itemPosition.h = i.h;
            }
          }
        });
      });
    }
  },

  mounted() {
    this.initGrid();
    this.pageContext.handleEvent(`widget:${this.widget.id}:gridItemPositionChange`, () => {
      this.refresh();
    });
  },

  watch: {
    index: {
      handler() {
        this.refresh();
      }
    },
    vColumn: {
      handler(v) {
        this.grid.column(v, 'list');
      }
    }
  }
};
</script>
