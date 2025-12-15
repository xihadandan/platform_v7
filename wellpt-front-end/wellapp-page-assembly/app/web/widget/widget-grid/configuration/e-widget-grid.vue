<template>
  <EditWrapper
    :widget="widget"
    :index="index"
    :widgetsOfParent="widgetsOfParent"
    :designer="designer"
    :parent="parent"
    :children="widget.configuration.cols"
  >
    <a-row
      v-if="isFlexType"
      :key="widget.id"
      :class="['widget-row', designer.terminalType, isDividingLine ? 'dividingLine' : '']"
      :type="isFlexType ? 'flex' : null"
      :style="{
        outline: selected ? 'unset' : '1px dashed #d5d5d5'
      }"
    >
      <template v-for="(col, cindex) in widget.configuration.cols">
        <a-col
          :class="['widget-col', colSelected(col) ? 'selected' : '']"
          :flex="flexValue(col)"
          :style="colFlexStyle(col)"
          :key="col.id"
          v-if="isFlexType"
          @click.native.stop="selectWidget(col)"
        >
          <span class="widget-title" v-show="colSelected(col)">{{ col.title }}</span>
          <div class="widget-operation-buttons" v-show="colSelected(col)">
            <template v-if="col.configuration.flex <= 130">
              <a-dropdown>
                <a-button icon="ellipsis" size="small" title="更多操作" />
                <a-menu slot="overlay" size="small" @click="e => handleMoreItemClick(e, widget.configuration.cols, cindex)">
                  <a-menu-item key="arrow-up" v-show="cindex != 0">向上移动</a-menu-item>
                  <a-menu-item key="arrow-down" v-show="cindex != widget.configuration.cols.length - 1">向下移动</a-menu-item>
                  <a-menu-item key="selectParent">选中父组件</a-menu-item>
                  <a-menu-item key="showJson">查看JSON</a-menu-item>
                  <a-menu-item key="del">删除</a-menu-item>
                </a-menu>
              </a-dropdown>
            </template>
            <template v-else>
              <a-button
                size="small"
                icon="arrow-up"
                title="向上移动"
                v-show="cindex != 0"
                @click.stop="colMoveUp(widget.configuration.cols, cindex)"
              ></a-button>
              <a-button
                size="small"
                icon="arrow-down"
                title="向下移动"
                v-show="cindex != widget.configuration.cols.length - 1"
                @click.stop="colMoveDown(widget.configuration.cols, cindex)"
              ></a-button>
              <a-button size="small" icon="vertical-align-top" title="选中父组件" @click.stop="selectWidget(widget)"></a-button>
              <a-button size="small" icon="profile" type="danger" title="查看JSON" @click.stop="showWidgetJsonDetail"></a-button>
              <a-button
                size="small"
                icon="delete"
                type="danger"
                @click.stop="deleteCol(widget.configuration.cols, cindex)"
                title="删除"
              ></a-button>
            </template>
          </div>
          <draggable
            :list="col.configuration.widgets"
            v-bind="{ group: draggableConfig.dragGroup, ghostClass: 'ghost', animation: 200 }"
            handle=".widget-drag-handler, .widget-edit-wrapper.dragable"
            :move="onDragMove"
            @add="e => onDragAdd(e, col.configuration.widgets)"
            @paste.native="onDraggablePaste"
            :class="[!col.configuration.widgets || col.configuration.widgets.length === 0 ? 'widget-edit-empty' : 'widget-edit-content']"
          >
            <transition-group name="fade" tag="div" class="grid-col-drop-panel">
              <template v-for="(wgt, i) in col.configuration.widgets">
                <WDesignItem
                  :widget="wgt"
                  :key="wgt.id"
                  :index="i"
                  :widgetsOfParent="col.configuration.widgets"
                  :designer="designer"
                  :dragGroup="dragGroup"
                  :parent="col"
                />
              </template>
            </transition-group>
          </draggable>
        </a-col>
      </template>
    </a-row>
    <template v-else v-for="(row, index) in defaultRowsData">
      <a-row
        :key="index"
        :class="['widget-row', designer.terminalType, isDividingLine ? 'dividingLine' : '']"
        :style="{
          outline: selected ? 'unset' : '1px dashed #d5d5d5'
        }"
      >
        <template v-for="(col, cindex) in row">
          <a-col
            :class="['widget-col', colSelected(col) ? 'selected' : '']"
            :span="rowDisplay ? 24 : col.configuration.span"
            :key="col.id"
            style=""
            @click.native.stop="selectWidget(col)"
          >
            <span class="widget-title" v-show="colSelected(col)">{{ col.title }}</span>
            <div class="widget-operation-buttons" v-show="colSelected(col)">
              <template v-if="rowDisplay || col.configuration.span <= 4">
                <a-dropdown>
                  <a-button icon="ellipsis" size="small" title="更多操作" />
                  <a-menu slot="overlay" size="small" @click="e => handleMoreItemClick(e, widget.configuration.cols, col.idx)">
                    <a-menu-item key="arrow-up" v-show="col.idx != 0">向上移动</a-menu-item>
                    <a-menu-item key="arrow-down" v-show="col.idx != widget.configuration.cols.length - 1">向下移动</a-menu-item>
                    <a-menu-item key="selectParent">选中父组件</a-menu-item>
                    <a-menu-item key="showJson">查看JSON</a-menu-item>
                    <a-menu-item key="del">删除</a-menu-item>
                  </a-menu>
                </a-dropdown>
              </template>
              <template v-else>
                <a-button
                  size="small"
                  icon="arrow-up"
                  title="向上移动"
                  v-show="col.idx != 0"
                  @click.stop="colMoveUp(widget.configuration.cols, col.idx)"
                ></a-button>
                <a-button
                  size="small"
                  icon="arrow-down"
                  title="向下移动"
                  v-show="col.idx != widget.configuration.cols.length - 1"
                  @click.stop="colMoveDown(widget.configuration.cols, col.idx)"
                ></a-button>
                <a-button size="small" icon="vertical-align-top" title="选中父组件" @click.stop="selectWidget(widget)"></a-button>
                <a-button size="small" icon="profile" type="danger" title="查看JSON" @click.stop="showWidgetJsonDetail"></a-button>
                <a-button
                  size="small"
                  icon="delete"
                  type="danger"
                  @click.stop="deleteCol(widget.configuration.cols, col.idx)"
                  title="删除"
                ></a-button>
              </template>
            </div>

            <draggable
              :list="col.configuration.widgets"
              v-bind="{ group: draggableConfig.dragGroup, ghostClass: 'ghost', animation: 200 }"
              @add="e => onDragAdd(e, col.configuration.widgets)"
              :move="onDragMove"
              handle=".widget-drag-handler, .widget-edit-wrapper.dragable"
              @paste.native="onDraggablePaste"
              :class="[!col.configuration.widgets || col.configuration.widgets.length === 0 ? 'widget-edit-empty' : 'widget-edit-content']"
            >
              <transition-group name="fade" tag="div" class="grid-col-drop-panel">
                <template v-for="(wgt, i) in col.configuration.widgets">
                  <WDesignItem
                    :widget="wgt"
                    :key="wgt.id"
                    :index="i"
                    :widgetsOfParent="col.configuration.widgets"
                    :designer="designer"
                    :dragGroup="dragGroup"
                    :parent="col"
                  />
                </template>
              </transition-group>
            </draggable>
          </a-col>
        </template>
      </a-row>
    </template>
  </EditWrapper>
</template>
<style scope lang="less">
.widget-row {
  flex-flow: row nowrap;

  &.dividingLine {
    margin-bottom: 0;
    display: flex;
    > .widget-col {
      &:first-child {
        border-right: 1px solid var(--w-border-color-light);
        padding-right: 0px !important;
        border-left: 0px;
      }
      &:last-child {
        padding-left: 0px !important;
      }
    }
  }
}

.widget-edit-wrapper .widget-col {
  .dashed-border {
    outline: 1px dashed var(--w-border-color-base);
    > div {
      min-height: 35px;
    }
  }
  .ant-empty-image svg {
    width: 40px;
  }
}
.widget-edit-wrapper .grid-col-drop-panel {
  /* min-height: 30px; */
  /* padding-bottom: 5px; */
}
.widget-col > .widget-operation-buttons {
  top: -1px;
  right: -1px;
}
.widget-col .widget-title {
  top: -1px;
  left: -1px;
}
.widget-col .grid-col-drop-panel > .design-row {
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
.widget-edit-wrapper .widget-col.selected {
  margin-bottom: 0px !important;
  outline: 2px solid var(--w-primary-color) !important;
  z-index: 1;
}
</style>
<script type="text/babel">
import editWgtMixin from '@framework/vue/designer/editWidgetMixin';
import draggable from '@framework/vue/designer/draggable';
import { each, filter, findIndex } from 'lodash';
import colsEqualHeightMixin from '../colsEqualHeight.mixin';

import { generateId } from '@framework/vue/utils/util';
export default {
  name: 'EWidgetGrid',
  mixins: [editWgtMixin, draggable, colsEqualHeightMixin],
  data() {
    return {
      colDragContainerHeight: '35px'
    };
  },

  beforeCreate() {},
  components: {},
  computed: {
    isFlexType() {
      if (this.rowDisplay) {
        return false;
      } else if (this.widget.configuration.rowType === 'flex') {
        return true;
      } else if (this.designer.terminalType == 'mobile' && this.widget.configuration.heightType == 'colsEqualHeight') {
        return true;
      }
      return false;
    },
    // 移动端,行展示
    rowDisplay() {
      return this.designer.terminalType == 'mobile' && this.widget.configuration.uniConfiguration.rowDisplay;
    },
    // 默认布局，列宽超过24则另取一行
    defaultRowsData() {
      let rows = {},
        index = 0,
        span = 0;
      if (!this.isFlexType) {
        each(this.widget.configuration.cols, (col, idx) => {
          col.idx = idx;
          if (col.configuration.span) {
            span += col.configuration.span;
          }
          if (span <= 24) {
            if (!rows[index]) {
              rows[index] = [];
            }
            rows[index].push(col);
          } else {
            span = col.configuration.span;
            index++;
            rows[index] = [];
            rows[index].push(col);
          }
        });
      } else if (this.rowDisplay) {
        rows = [this.widget.configuration.cols];
      }
      return rows;
    },
    // 判断是否有分隔线
    isDividingLine() {
      if (this.widget.configuration.dividingLine) {
        if (!this.isFlexType) {
          let allSpan = 0;
          each(this.widget.configuration.cols, (col, idx) => {
            if (col.configuration.span || col.configuration.span === 0) {
              allSpan += col.configuration.span;
            } else {
              allSpan += 24;
            }
          });
          if (allSpan > 24) {
            return false;
          }
        }
        return true;
      }
      return false;
    }
  },
  created() {},
  methods: {
    handleMoreItemClick(e, cols, cindex) {
      if (e.key === 'arrow-up') {
        this.colMoveUp(cols, cindex);
      } else if (e.key === 'arrow-down') {
        this.colMoveDown(cols, cindex);
      } else if (e.key === 'selectParent') {
        this.selectWidget(this.widget);
      } else if (e.key === 'showJson') {
        this.showWidgetJsonDetail();
      } else if (e.key === 'del') {
        this.deleteCol(cols, cindex);
      }
    },
    colMoveUp(widgetsOfParent, cindex) {
      this.designer.widgetMoveUp(widgetsOfParent, cindex);
    },

    colMoveDown(widgetsOfParent, cindex) {
      this.designer.widgetMoveDown(widgetsOfParent, cindex);
    },
    colSelected(col) {
      return col.id === this.designer.selectedId;
    },
    deleteCol(cols, cindex) {
      let nextSelected = null;
      if (cols.length === 1) {
        nextSelected = this.widget;
      } else if (cols.length === 1 + cindex) {
        nextSelected = cols[cindex - 1];
      } else {
        nextSelected = cols[cindex + 1];
      }

      this.$delete(this.designer.widgetTreeMap, cols[cindex].id);
      this.$delete(this.designer.widgetIdMap, cols[cindex].id);

      this.$nextTick(() => {
        cols.splice(cindex, 1);
        //if (!!nextSelected) {
        this.designer.setSelected(nextSelected);
        //}
      });
    },
    flexValue(wgt) {
      if (this.isFlexType) {
        if (this.designer.terminalType === 'mobile') {
          if (!this.widget.configuration.uniConfiguration.rowDisplay) {
            if (wgt.configuration.uniflex || wgt.configuration.uniflex === 0) {
              return wgt.configuration.uniflex + 'px';
            }
          }
        } else if (wgt.configuration.flex) {
          return wgt.configuration.flex + 'px';
        }
        return 'auto';
      }
    },
    colFlexStyle(wgt) {
      let style = {};
      if (this.isFlexType) {
        // if (!wgt.configuration.flex) {
        // flex：auto设置width：0，使该列宽度固定
        style.width = '0';
        // }
      }
      return style;
    }
  },
  mounted() {}
};
</script>
