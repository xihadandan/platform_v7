<template>
  <EditWrapper
    :widget="widget"
    :showWidgetName="false"
    :index="index"
    :widgetsOfParent="widgetsOfParent"
    :designer="designer"
    :parent="parent"
    :title="widget.configuration.title"
    :children="widget.configuration.items"
    class="e-widget-form-layout"
  >
    <template slot="extra-buttons">
      <a-button size="small" icon="plus-square" title="快速添加一行" @click.stop="quickAddRowItems"></a-button>
    </template>

    <div :class="['widget-form-layout']" @click.stop="evt => selectWidget(widget, parent)" style="overflow: hidden; position: relative">
      <div
        v-if="designer.terminalType == 'pc' && resizeContainerWidth != 0 && widget.configuration.layout != 'vertical'"
        class="resize-bar"
        :style="{
          position: 'absolute',
          zIndex: 2,
          width: resizeContainerWidth + 'px',
          height:
            widget.configuration.titleHidden !== true && (widget.configuration.titleIcon || widget.configuration.title) ? '50px' : '0px'
        }"
      >
        <vue-draggable-resizable
          :x="dragX"
          :parent="true"
          :w="7"
          :h="20"
          :resizable="false"
          axis="x"
          :grid="[1, 1]"
          :onDragStart="onDragResizeStart"
          :onDrag="onDragCallback"
          @dragging="onDraggingResize"
          @dragstop="onDragstopResize"
        >
          <div>
            <div class="drag-x-text">{{ dragX }}px</div>
            <div class="drag-line"></div>
          </div>
        </vue-draggable-resizable>
      </div>

      <div
        class="layout-header"
        v-if="widget.configuration.titleHidden !== true"
        v-show="widget.configuration.titleIcon || widget.configuration.title"
      >
        <label class="title">
          <span v-if="widget.configuration.titleIcon" style="margin-right: var(--w-margin-3xs)">
            <Icon :type="widget.configuration.titleIcon" :size="24" color="var(--w-primary-color)" />
          </span>
          {{ widget.configuration.title }}
        </label>
      </div>
      <template v-if="designer.terminalType == 'pc'">
        <table :class="[!widget.configuration.bordered ? 'no-border' : '']">
          <colgroup>
            <col
              v-for="(col, i) in vColgroup"
              :style="{
                width: widget.configuration.columnWidthAvg
                  ? undefined /** 不需要设置百分比 */
                  : col.width != undefined
                  ? col.width + col.widthUnit
                  : undefined
              }"
              :key="'col_' + i"
            />
          </colgroup>
          <tbody>
            <template v-for="(row, r) in vGrid">
              <tr :key="'row_' + r">
                <template v-for="(item, i) in row">
                  <td
                    v-if="item.id != undefined"
                    :key="'td_' + r + '_' + i"
                    :style="{}"
                    :colspan="item.configuration.span"
                    :rowspan="item.configuration.rowspan"
                    :class="[multiSelectIds.includes(item.id) ? 'border-animate' : '']"
                  >
                    <a-row
                      :class="[
                        designer.selectedId == item.id && !multiSelectIds.includes(item.id) ? 'selected' : '',
                        widget.configuration.layout,
                        widget.configuration.colLabelContentMergeShow && widget.configuration.layout !== 'vertical'
                          ? 'label-content-merged'
                          : ''
                      ]"
                      @click.native.stop="e => selectCell(item, e, r, i)"
                      type="flex"
                      :style="{}"
                    >
                      <a-col
                        :flex="
                          widget.configuration.layout !== 'vertical' && !widget.configuration.colLabelContentMergeShow
                            ? labelColumnWidth
                            : undefined
                        "
                        :style="{
                          width:
                            widget.configuration.layout !== 'vertical' && !widget.configuration.colLabelContentMergeShow
                              ? labelColumnWidth
                              : undefined
                        }"
                        :class="['label-col', widget.configuration.labelAlign == 'right' ? 'right' : 'left']"
                        v-if="!item.configuration.labelHidden"
                      >
                        <draggable
                          :list="item.configuration.labelWidgets"
                          v-bind="{ group: dragGroup, ghostClass: 'ghost', animation: 200 }"
                          handle=".widget-drag-handler"
                          class="edit-form-layout-label"
                          :style="{ width: '100%', 'min-height': '14px', 'min-width': '30px' }"
                          :move="onDragMove"
                          @add="e => onDragAdd(e, item.configuration.labelWidgets)"
                          @paste.native="onDraggablePaste"
                        >
                          <a-row type="flex">
                            <a-col>
                              <label
                                v-show="item.configuration.label"
                                :class="[requiredItem(item) ? 'required' : '', widget.configuration.colon ? 'colon' : '']"
                              >
                                {{ item.configuration.label }}
                              </label>
                            </a-col>
                            <a-col flex="auto" style="padding-left: 4px">
                              <template v-for="(wgt, index) in item.configuration.labelWidgets">
                                <WDesignItem
                                  :widget="wgt"
                                  :key="wgt.id"
                                  :index="index"
                                  :widgetsOfParent="item.configuration.labelWidgets"
                                  :designer="designer"
                                  :parent="item"
                                  :ref="'item_label_' + index"
                                  :dragGroup="dragGroup"
                                />
                              </template>
                            </a-col>
                          </a-row>
                        </draggable>
                      </a-col>
                      <a-col flex="auto" class="content-col">
                        <draggable
                          :list="item.configuration.widgets"
                          v-bind="{ group: dragGroup, ghostClass: 'ghost', animation: 200 }"
                          handle=".widget-drag-handler"
                          @end="e => onDragEnd(e, afterOnEnd)"
                          @start="onDragWidgetStart"
                          @add="e => onDragAdd(e, item.configuration.widgets, item)"
                          @update="onDragUpdate"
                          :move="onDragMove"
                          @paste.native="onDraggablePaste"
                          style="height: 100%"
                        >
                          <transition-group
                            name="fade"
                            tag="div"
                            class="form-layout-grid-col-drop-panel"
                            style="height: 100%"
                            :style="
                              item.configuration.rowspan != undefined && item.configuration.rowspan > 1
                                ? {
                                    height: '100%',
                                    minHeight: '14px',
                                    display: 'flex',
                                    alignItems: 'center',
                                    'flex-direction': 'column',
                                    'justify-content': 'center'
                                  }
                                : { height: '100%', minHeight: '14px' }
                            "
                            @click.native.stop="selectWidget(item)"
                          >
                            <template v-for="(wgt, index) in item.configuration.widgets">
                              <WDesignItem
                                :widget="wgt"
                                :key="wgt.id"
                                :index="index"
                                :widgetsOfParent="item.configuration.widgets"
                                :designer="designer"
                                :parent="item"
                                :ref="'item' + index"
                                :dragGroup="dragGroup"
                              />
                            </template>
                          </transition-group>
                        </draggable>
                      </a-col>
                    </a-row>

                    <div class="widget-operation-buttons" v-show="designer.selectedId == item.id && !multiSelectIds.includes(item.id)">
                      <a-button
                        size="small"
                        icon="arrow-up"
                        title="向上移动"
                        v-show="!(r == 0 && i == 0)"
                        @click.stop="itemMoveUp(widget.configuration.items, item)"
                      ></a-button>
                      <a-button
                        size="small"
                        icon="arrow-down"
                        title="向下移动"
                        v-show="!(r == vGrid.length - 1 && i == row.length - 1)"
                        @click.stop="itemMoveDown(widget.configuration.items, item)"
                      ></a-button>
                      <a-button size="small" icon="vertical-align-top" title="选中父组件" @click.stop="selectWidget(widget)"></a-button>
                      <a-button size="small" icon="profile" title="查看JSON" @click.stop="showWidgetJsonDetail"></a-button>
                      <a-button
                        size="small"
                        icon="delete"
                        type="danger"
                        @click.stop="deleteItem(widget.configuration.items, item.id, r, i)"
                        title="删除"
                      ></a-button>
                      <a-popconfirm
                        placement="bottomRight"
                        :trigger="['click']"
                        overlayClassName="e-widget-form-layout-more-operation"
                        :overlayStyle="{ width: '195px' }"
                        :ref="'itemQuickInertConfirm_' + item.id"
                      >
                        <template slot="title">
                          <a-row class="inertBar" type="flex">
                            <a-col flex="140px">
                              插入
                              <a-input-number style="width: 60px" :min="1" :max="1000" size="small" :defaultValue="1" />
                              行
                            </a-col>
                            <a-col flex="auto">
                              <a-button icon="check" size="small" type="primary" @click="e => insert(item, 'row', e, row)" />
                            </a-col>
                          </a-row>
                          <a-row class="inertBar" type="flex">
                            <a-col flex="140px">
                              插入
                              <a-input-number style="width: 60px" :min="1" :max="1000" size="small" :defaultValue="1" />
                              单元格
                            </a-col>
                            <a-col flex="auto">
                              <a-button icon="check" size="small" type="primary" @click="e => insert(item, 'col', e)" />
                            </a-col>
                          </a-row>
                        </template>
                        <a-button icon="ellipsis" size="small" title="更多操作" @click.stop="() => {}" />
                      </a-popconfirm>
                    </div>
                  </td>

                  <!-- 合并占位列如果前一个存在跨行合并，则需要占位一个td -->
                  <td
                    :key="'td_' + r + '_' + i"
                    v-if="
                      row[i - 1] != undefined &&
                      row[i - 1].configuration != undefined &&
                      row[i - 1].configuration.rowspan != undefined &&
                      row[i - 1].configuration.rowspan > 1 &&
                      item.merged
                    "
                    :style="{}"
                  >
                    <div></div>
                  </td>
                </template>
              </tr>
            </template>
          </tbody>
        </table>
      </template>
      <template v-else>
        <div :class="['mobile-layout-content', widget.configuration.uniConfiguration.layout]">
          <a-row
            :class="['mobile-layout-row', designer.selectedId == item.id ? 'selected' : '']"
            @click.native.stop="e => selectWidget(item, widget)"
            type="flex"
            v-for="(item, i) in widget.configuration.items"
            :key="'row_' + i"
          >
            <a-col class="label-col" :flex="widget.configuration.uniConfiguration.layout == 'vertical' ? '36px' : '80px'">
              <label v-show="item.configuration.label" :class="[requiredItem(item) ? 'required' : '']">
                {{ item.configuration.label }}
              </label>
            </a-col>
            <a-col flex="auto" class="content-col">
              <draggable
                :list="item.configuration.widgets"
                v-bind="{ group: dragGroup, ghostClass: 'ghost', animation: 200 }"
                handle=".widget-drag-handler"
                @end="e => onDragEnd(e, afterOnEnd)"
                @start="onDragWidgetStart"
                @add="e => onDragAdd(e, item.configuration.widgets, item)"
                @update="onDragUpdate"
                :move="onDragMove"
                @paste.native="onDraggablePaste"
                style="height: 100%"
              >
                <transition-group
                  name="fade"
                  tag="div"
                  class="form-layout-grid-col-drop-panel"
                  style="height: 100%"
                  :style="{ height: '100%', minHeight: '20px' }"
                  @click.native.stop="selectWidget(item)"
                >
                  <template v-for="(wgt, index) in item.configuration.widgets">
                    <WDesignItem
                      :widget="wgt"
                      :key="wgt.id"
                      :index="index"
                      :widgetsOfParent="item.configuration.widgets"
                      :designer="designer"
                      :parent="item"
                      :ref="'item' + index"
                      :dragGroup="dragGroup"
                    />
                  </template>
                </transition-group>
              </draggable>
            </a-col>

            <div class="widget-operation-buttons" v-show="designer.selectedId == item.id" style="right: 5px !important">
              <a-button
                size="small"
                icon="arrow-up"
                title="向上移动"
                v-show="!(r == 0 && i == 0)"
                @click.stop="itemMoveUp(widget.configuration.items, item)"
              ></a-button>
              <a-button
                size="small"
                icon="arrow-down"
                title="向下移动"
                v-show="!(r == vGrid.length - 1 && i == row.length - 1)"
                @click.stop="itemMoveDown(widget.configuration.items, item)"
              ></a-button>
              <a-button size="small" icon="vertical-align-top" title="选中父组件" @click.stop="selectWidget(widget)"></a-button>
              <a-button size="small" icon="profile" title="查看JSON" @click.stop="showWidgetJsonDetail"></a-button>
              <a-button
                size="small"
                icon="delete"
                type="danger"
                @click.stop="deleteItem(widget.configuration.items, item.id)"
                title="删除"
              ></a-button>
              <a-popconfirm
                placement="bottomRight"
                :trigger="['click']"
                overlayClassName="e-widget-form-layout-more-operation"
                :overlayStyle="{ width: '195px' }"
                :ref="'itemQuickInertConfirm_' + item.id"
              >
                <template slot="title">
                  <a-row class="inertBar" type="flex">
                    <a-col flex="140px">
                      插入
                      <a-input-number style="width: 60px" :min="1" :max="1000" size="small" :defaultValue="1" />
                      行
                    </a-col>
                    <a-col flex="auto">
                      <a-button icon="check" size="small" type="primary" @click="e => insert(item, 'row', e, row)" />
                    </a-col>
                  </a-row>
                  <a-row class="inertBar" type="flex">
                    <a-col flex="140px">
                      插入
                      <a-input-number style="width: 60px" :min="1" :max="1000" size="small" :defaultValue="1" />
                      单元格
                    </a-col>
                    <a-col flex="auto">
                      <a-button icon="check" size="small" type="primary" @click="e => insert(item, 'col', e)" />
                    </a-col>
                  </a-row>
                </template>
                <a-button icon="ellipsis" size="small" title="更多操作" @click.stop="() => {}" />
              </a-popconfirm>
            </div>
          </a-row>
        </div>
      </template>
    </div>
  </EditWrapper>
</template>
<style lang="less">
.e-widget-form-layout {
  .widget-form-layout {
    background-color: #fff;
    > table {
      &.no-border {
        > tbody {
          outline: 1px dotted #e8e8e8 !important;
          > tr {
            // border-top: 1px dotted #e8e8e8 !important;
            // border-right: 1px dotted #e8e8e8 !important;
            &:first-child {
              border-top: 1px dotted #e8e8e8 !important;
            }
            &:last-child {
              border-bottom: 1px dotted #e8e8e8 !important;
            }
            > td {
              border-left: 1px dotted #e8e8e8 !important;
              border-right: 1px dotted #e8e8e8 !important;
              border-bottom: 1px dotted #e8e8e8 !important;

              .label-col {
                border-right: 1px dotted #e8e8e8 !important;
              }
            }
          }
        }
      }
      td {
        &:has(> .selected) {
          padding-right: 2px;
          padding-left: 2px;
          // padding-bottom: 1px;
        }
        > div.selected {
          outline: 2px solid var(--w-primary-color);
        }
        > .widget-operation-buttons {
          position: absolute;
          z-index: 91; //内部组件design-item-draggable-right：z-index:90
          top: 0;
          right: 2px;
          line-height: normal;
        }

        .form-layout-grid-col-drop-panel {
          display: flex;
          align-items: center;
          justify-content: flex-start;
          flex-wrap: wrap;

          > * {
            width: 100%;
          }
        }
        .content-col {
          .widget-row > .widget-col div[w-id] {
            padding: unset !important;
            .ant-form-item-label {
              line-height: 32px; // 大部分组件默认高度
            }
          }
          .widget-row {
            margin-bottom: 0px !important;
          }
        }
      }
    }
    > .mobile-layout-content {
      padding: 0 5px;
      .mobile-layout-row {
        margin-bottom: 22px;
        outline: 2px dotted #e8e8e8;
        .label-col {
          display: flex;
          align-items: baseline;
          padding-right: var(--w-padding-2xs);
          padding-left: var(--w-padding-xs);
        }
        .content-col {
          border-left: 1px dotted #e8e8e8;
          min-height: 30px;
        }
        &.selected {
          outline: 2px solid var(--w-primary-color);
        }
        .required {
          position: relative;
          &:before {
            position: absolute;
            margin-right: 4px;
            color: #f5222d;
            font-size: 14px;
            font-family: SimSun, sans-serif;
            line-height: 1;
            content: '*';
            left: -9px;
            top: 50%;
            margin-top: -7px;
          }
        }
      }
      &.vertical {
        .mobile-layout-row {
          flex-direction: column;
          .label-col {
            align-items: center;
          }
        }
      }
    }

    .form-item.selected {
      box-shadow: inset 0px 0px 0px 2px var(--w-primary-color);
      > div {
        > .form-item-label {
          box-shadow: inset 0px 0px 0px 2px var(--w-primary-color);
        }
        > .form-item-content {
          .widget-operation-buttons {
            right: 2px;
            top: -8px;
          }
        }
      }
    }
    &:hover {
      > .resize-bar {
        display: block;
      }
    }

    > .resize-bar {
      display: none;
      position: absolute;
      z-index: 2;
      width: 933px;
      height: 20px;
      > div {
        background: #f9f9f9;
        cursor: col-resize;
        width: 7px;
        height: 20px;
        border-radius: 4px;
        outline: 1px solid #c2bdbd;
        margin-left: -4px;
        .drag-x-text {
          position: absolute;
          left: 17px;
          font-size: 12px;
          color: red;
        }
        .drag-line {
          position: absolute;
          height: 2000px;
          border-right: 4px dashed red;
          top: 24px;
          left: 2px;
          display: none;
        }
        &:hover {
          .drag-line {
            display: block;
          }
        }
      }
    }

    /*边框虚线滚动动画特效*/
    .border-animate {
      padding: 2px;
      background: linear-gradient(90deg, gray 60%, transparent 60%) repeat-x left top/10px 1px,
        linear-gradient(0deg, gray 60%, transparent 60%) repeat-y right top/1px 10px,
        linear-gradient(90deg, gray 60%, transparent 60%) repeat-x right bottom/10px 1px,
        linear-gradient(0deg, gray 60%, transparent 60%) repeat-y left bottom/1px 10px;

      animation: border-animate 0.382s infinite linear;
    }

    @keyframes border-animate {
      0% {
        background-position: left top, right top, right bottom, left bottom;
      }
      100% {
        background-position: left 10px top, right top 10px, right 10px bottom, left bottom 10px;
      }
    }
  }
}

.form-layout-grid-col-drop-panel {
  > .design-row .ant-form-item {
    margin-bottom: 0px !important;
  }
}
.e-widget-form-layout-more-operation {
  .inertBar {
    margin: 5px 0px;
    .ant-btn {
      opacity: 0;
    }
  }
  .inertBar:hover {
    .ant-btn {
      opacity: 1;
    }
  }

  .ant-popover-message > i {
    display: none;
  }
  .ant-popover-buttons {
    display: none;
  }
  .ant-popover-inner-content,
  .ant-popover-message.ant-popover-message-title {
    padding: 0px;
  }
}
</style>
<script type="text/babel">
import formEditWidgetMixin from '../../mixin/formEditWidgetMixin';
import draggable from '@framework/vue/designer/draggable';
import { generateId, deepClone } from '@framework/vue/utils/util';
import VueDraggableResizable from 'vue-draggable-resizable';

import '../css/index.less';
export default {
  name: 'EWidgetFormLayout',
  mixins: [formEditWidgetMixin, draggable],

  data() {
    return {
      resizeContainerWidth: 0,
      minDragX: 80,
      dragResizing: false,
      dragX: this.widget.configuration.labelColumnWidth || 120,
      itemDragContainerHeight: '35px',
      minRowHeight: 60,
      multiSelectIds: [],
      itemRequiredField: {},
      dragFormItemIndex: -1
    };
  },

  beforeCreate() {},
  components: { VueDraggableResizable },
  computed: {
    labelColumnWidth() {
      return (this.widget.configuration.labelColumnWidth || 120) + 'px';
    },
    vColumn() {
      return this.designer.terminalType == 'mobile' ? 1 : this.widget.configuration.column;
    },
    vGrid() {
      let column = this.vColumn,
        items = this.widget.configuration.items,
        grid = [];

      // 预先生成行/列二维数组
      let index = 0,
        itemLength = items.length;
      grid = Array.from({ length: items.length }, () => {
        return Array.from({ length: column }, () => {
          return {};
        });
      });

      // console.log('绘制grid,', grid);
      for (let i = 0; i < grid.length; i++) {
        let row = grid[i];
        for (let j = 0; j < row.length; j++) {
          if (index < itemLength) {
            if (row[j].merged) {
              // 被合并占用，不能放置控件
              continue;
            }
            let item = items[index++];

            let span = item.configuration.span || 1;
            // 修正占位: 如果占位数超过被合并预占的单元格，则需要减小占位数
            if (span > 1 && row[j + (span - 1)] && row[j + (span - 1)].merged) {
              span -= 1;
              item.configuration.span = span;
            }
            if (span > 1 && span > column - j) {
              // 要修正 span ，否则会多出一列
              span = column - j;
              item.configuration.span = column - j;
            }
            row[j] = item;

            // 行合并
            let rowspan = item.configuration.rowspan || 1;
            if (rowspan > 1) {
              // 向下合并行
              for (let r = 1; r < rowspan; r++) {
                if (grid[i + r] != undefined) {
                  grid[i + r][j] = { merged: true };
                  if (span > 1) {
                    // 往右合并
                    for (let s = 1; s < span; s++) {
                      if (grid[i + r][j + s] != undefined) {
                        grid[i + r][j + s] = { merged: true };
                      }
                    }
                  }
                } else {
                  break;
                }
              }
            }

            if (span > 1) {
              j = j + (span - 1); // 位移，表示合并占用格子
            }
          }
        }
        if (index < itemLength && i == grid.length - 1) {
          // 还有控件未放置，补充
          grid.push(
            Array.from({ length: column }, () => {
              return {};
            })
          );
        }
      }

      for (let i = 0; i < grid.length; i++) {
        let emptyRow = true;
        for (let j = 0, jlen = grid[i].length; j < jlen; j++) {
          if (grid[i][j] && grid[i][j].id) {
            emptyRow = false;
            break;
          }
        }
        if (emptyRow) {
          grid.splice(i--, 1);
        }
      }

      //  console.log('生成表单布局网格: ', grid);
      return grid;
    },
    vColgroup() {
      let colgroup = this.widget.configuration.colgroup || [];
      if (colgroup.length == 0) {
        for (let i = 0, len = this.widget.configuration.column; i < len; i++) {
          colgroup.push({
            width: undefined,
            widthUnit: undefined
          });
        }
      }
      return colgroup;
    },

    defaultEvents() {
      return [
        { id: 'showFormLayout', title: '显示表单布局' },
        { id: 'hideFormLayout', title: '隐藏表单布局' }
      ];
    }
  },
  created() {
    if (this.designer.draggingWidgetId == this.widget.id) {
      // 当前拖拽进来的布局组件的标题列，需要与同级的同宽
      for (let w of this.widgetsOfParent) {
        if (w.wtype == 'WidgetFormLayout' && w.id != this.widget.id && w.configuration.layoutType !== 'vertical') {
          this.widget.configuration.labelColumnWidth = w.configuration.labelColumnWidth;
          break;
        }
      }
    }
    // 修正无span 情况
    for (let item of this.widget.configuration.items) {
      if (item.configuration.span == undefined) {
        item.configuration.span = 1;
      }
    }

    if (this.widget.configuration.cloneWidget) {
      this.widget.configuration.code = new Date().getTime();
      delete this.widget.configuration.cloneWidget;
    }

    if (!this.widget.configuration.hasOwnProperty('uniConfiguration')) {
      this.$set(this.widget.configuration, 'uniConfiguration', {
        layout: 'horizontal'
      });
    }
  },
  methods: {
    // 判断是否为表单基础组件
    isDyformElement(item) {
      return (
        (item.category == 'basicComponent' && (!item.scope || (item.scope && item.scope.indexOf('dyform') > -1))) ||
        item.category == 'basic'
      );
    },
    requiredItem(rowItem) {
      // 通过必填项性运算，来同步获取直接字段组件的同步名称
      let setRequiredItem = item => {
        if (item.configuration && item.configuration.widgets) {
          let named = false;
          for (let i = 0, len = item.configuration.widgets.length; i < len; i++) {
            let itemWidget = deepClone(item.configuration.widgets[i]);
            if (this.isDyformElement(itemWidget)) {
              if (itemWidget.configuration) {
                if (itemWidget.configuration.syncLabel2FormItem == true && itemWidget.configuration.name && !named) {
                  // 名称同步
                  this.$set(item.configuration, 'label', itemWidget.configuration.name);
                  named = true;
                }
                let { required, code } = itemWidget.configuration;
                if (required != undefined && code != undefined) {
                  if (required) {
                    if (!this.itemRequiredField[rowItem.id].includes(code)) {
                      this.itemRequiredField[rowItem.id].push(code);
                    }
                  } else {
                    let i = this.itemRequiredField[rowItem.id].indexOf(code);
                    if (i != -1) {
                      this.itemRequiredField[rowItem.id].splice(i, 1);
                    }
                  }
                  // 需要判断必填字段编码是否都存在
                  let fields = this.designer.SimpleFieldInfos;
                  if (fields) {
                    let codes = [];
                    for (let j = 0, len = fields.length; j < len; j++) {
                      codes.push(fields[j].code);
                    }
                    for (let j = 0; j < this.itemRequiredField[rowItem.id].length; j++) {
                      if (!codes.includes(this.itemRequiredField[rowItem.id][j])) {
                        this.itemRequiredField[rowItem.id].splice(j--, 1);
                      }
                    }
                  }
                  rowItem.configuration.required = this.itemRequiredField[rowItem.id].length > 0;
                }
              }
            } else if (itemWidget.configuration) {
              if (itemWidget.configuration.cols || itemWidget.configuration.widgets) {
                if (itemWidget.configuration.cols && itemWidget.configuration.cols.length > 0) {
                  // 栅格
                  itemWidget.configuration.widgets = itemWidget.configuration.cols;
                }
                if (itemWidget.configuration.widgets.length) {
                  setRequiredItem(itemWidget);
                }
              }
            }
          }
        }
      };
      if (this.itemRequiredField[rowItem.id] == undefined) {
        this.itemRequiredField[rowItem.id] = [];
      } else {
        this.itemRequiredField[rowItem.id].splice(0, this.itemRequiredField[rowItem.id].length);
      }
      setRequiredItem(rowItem);
      return this.itemRequiredField[rowItem.id].length > 0;
    },
    onDragResizeStart() {
      this.dragX = this.widget.configuration.labelColumnWidth || 120;
    },
    onDragWidgetStart(evt) {
      // 拖动组件时，如果组件名称与单元格标题同步，记录拖动组件原先的位置
      this.designer.dragging = true;
      this.designer.draggedWidget = evt.item._underlying_vm_;
      let fieldWidget = evt.item._underlying_vm_;
      this.dragFormItemIndex = -1;
      let parent = this.designer.widgetConfigurations[fieldWidget.id].parent;
      if (parent && parent.wtype == 'WidgetFormItem' && fieldWidget.configuration.syncLabel2FormItem) {
        this.dragFormItemIndex = this.widget.configuration.items.findIndex(item => item.id == parent.id);
      }
    },
    afterOnEnd(evt, item) {
      if (this.dragFormItemIndex > -1) {
        // 拖动组件时，如果组件名称与单元格标题同步，通过记录的原位置，删除组件标题
        this.widget.configuration.items[this.dragFormItemIndex].configuration.label = '';
        delete this.widget.configuration.items[this.dragFormItemIndex].configuration.i18n;
        this.dragFormItemIndex = -1;
      }
    },
    onDragCallback(x) {
      return x >= this.minDragX;
    },
    onDraggingResize(x) {
      this.dragX = x;
      this.dragResizing = true;
    },

    onDragstopResize(x) {
      if (x <= this.minDragX) {
        this.$message.info(`最小宽度限制: ${this.minDragX}px`);
      }
      this.dragResizing = false;
      this.$set(this.widget.configuration, 'labelColumnWidth', this.dragX);
      this.updateSiblingWidgetFormLayoutLabelColWidth(this.dragX);
    },
    updateSiblingWidgetFormLayoutLabelColWidth(width) {
      for (let w of this.widgetsOfParent) {
        if (w.wtype == 'WidgetFormLayout' && w.id != this.widget.id) {
          // 同级标题列宽度调整为一致
          this.$set(w.configuration, 'labelColumnWidth', width);
        }
      }
    },
    insert(item, type, e, row) {
      // 隐藏插入选项框
      this.$refs['itemQuickInertConfirm_' + item.id][0].setVisible(false);
      this.$nextTick(() => {
        let num = parseInt(e.target.parentElement.previousElementSibling.querySelector('input').value),
          newItem = this.defaultEmptyItem();
        let items = [],
          index = undefined,
          currentColIndex = 0;
        if (type == 'row') {
          num = this.designer.terminalType == 'mobile' ? 1 : num * this.widget.configuration.column;
          // 找到该行第一个元素作为插入位
          if (row != undefined) {
            for (let i = 0, len = row.length; i < len; i++) {
              if (row[i].id != undefined) {
                item = row[i];
                break;
              }
            }
          }
        }

        if (type != 'row') {
          // 插入单元格的情况下，要判断当前插入的占位数，需要对齐表格
        }
        for (let i = 0, len = num; i < len; i++) {
          let _it = deepClone(newItem);
          _it.id = `form-item-${generateId()}`;
          _it.configuration.span = 1; // type == 'row' ? 1 : item.configuration.span;
          items.push(_it);
        }

        index = this.getItemIndexOf(item.id);

        if (type == 'row') {
          this.widget.configuration.items.splice(index, 0, ...items);
        } else {
          this.widget.configuration.items.splice(index, 0, ...items);
        }

        this.designer.toTree(this.widget, this.widget.configuration.items, this.parent, this.index);
      });
    },

    showWidgetJsonDetail() {
      this.designer.widgetJsonDrawerVisible = true;
    },
    getItemIndexOf(itemId) {
      for (let i = 0, len = this.widget.configuration.items.length; i < len; i++) {
        if (itemId == this.widget.configuration.items[i].id) {
          return i;
        }
      }
      return -1;
    },
    itemMoveUp(widgetsOfParent, item) {
      let id = item.id;
      let span = item.configuration.span;
      let index = this.getItemIndexOf(id);
      // if (span == this.widget.configuration.column) {
      //   // 单元格占据整行，则整行上移
      //   if (index >= 3) {
      //     let tempWidget = widgetsOfParent[index];
      //     widgetsOfParent.splice(index, 1);
      //     index -= span;
      //     widgetsOfParent.splice(index, 0, tempWidget);
      //     for (let i = 0, len = widgetsOfParent.length; i < len; i++) {
      //       this.widgetTreeMap[widgetsOfParent[i].id].index = i;
      //     }
      //     this.designer.toTree(this.widget, this.widget.configuration.items, this.parent, this.index);
      //     return;
      //   }
      // }
      this.designer.widgetMoveUp(widgetsOfParent, index);
      this.designer.toTree(this.widget, this.widget.configuration.items, this.parent, this.index);
    },

    itemMoveDown(widgetsOfParent, item) {
      let id = item.id;
      let span = item.configuration.span;
      let index = this.getItemIndexOf(id);
      // if (span == this.widget.configuration.column) {
      //   // 整行下移
      //   if (index <= widgetsOfParent.length - 1) {
      //     let tempWidget = widgetsOfParent[index];
      //     widgetsOfParent.splice(index, 1);
      //     index += span;
      //     widgetsOfParent.splice(index, 0, tempWidget);
      //     for (let i = 0, len = widgetsOfParent.length; i < len; i++) {
      //       this.widgetTreeMap[widgetsOfParent[i].id].index = i;
      //     }
      //     this.designer.toTree(this.widget, this.widget.configuration.items, this.parent, this.index);
      //     return;
      //   }
      // }
      this.designer.widgetMoveDown(widgetsOfParent, index);
      this.designer.toTree(this.widget, this.widget.configuration.items, this.parent, this.index);
    },
    addItem() {
      let newItem = this.defaultEmptyItem();
      this.widget.configuration.items.push(newItem);
    },
    defaultEmptyItem() {
      return {
        wtype: 'WidgetFormItem',
        title: '单元格',
        id: `form-item-${generateId()}`,
        configuration: {
          label: '',
          span: 1,
          hidden: false,
          widgets: [],
          labelWidgets: [],
          required: false
        }
      };
    },

    quickAddRowItems() {
      let newItem = this.defaultEmptyItem();
      if (this.designer.terminalType == 'mobile') {
        this.widget.configuration.items.push(newItem);
        return;
      }
      // 判断最后一行的列span是否满足，不满足则修改，避免增加一行时候存在列错行情况
      let lastRow = this.vGrid[this.vGrid.length - 1],
        column = this.widget.configuration.column,
        merged = false;
      if (lastRow != undefined) {
        let _span = 0,
          lastCol = null;
        for (let i = 0; i < column; i++) {
          if (lastRow[i] && lastRow[i].configuration) {
            if (lastRow[i].configuration.span != undefined) {
              _span += lastRow[i].configuration.span;
              lastCol = lastRow[i];
            }
            if (lastRow[i].configuration.rowspan != undefined) {
              merged = true;
            }
          }
          if (lastRow[i].merged) {
            _span += 1;
          }
        }
        if (_span < column) {
          lastCol.configuration.span += column - _span;
        }
      }
      for (let i = 0; i < column + (merged ? -1 : 0); i++) {
        let _it = deepClone(newItem);
        _it.id = `form-item-${generateId()}`;
        this.widget.configuration.items.push(_it);
      }
      this.designer.toTree(this.widget, this.widget.configuration.items, this.parent, this.index);
    },
    deleteItem(items, id, r, i) {
      let nextSelected = null,
        index = this.getItemIndexOf(id);
      // if (items.length === 1) {
      //   nextSelected = this.widget;
      // } else if (items.length === 1 + index) {
      //   nextSelected = items[index - 1];
      // } else {
      //   nextSelected = items[index + 1];
      // }
      if (this.designer.terminalType == 'pc') {
        let currentRowItems = this.vGrid[r];
        if (currentRowItems.length > 1) {
          let filterItems = currentRowItems.filter(it => {
            return it != undefined && it.wtype === 'WidgetFormItem';
          });
          if (filterItems.length > 1) {
            filterItems[i == 0 ? i + 1 : i - 1].configuration.span += items[index].configuration.span;
          }
        }
      }
      items.splice(index, 1);

      //}

      this.designer.deleteWidget(id);

      this.designer.toTree(this.widget, this.widget.configuration.items, this.parent, this.index);

      // 选中前或后一个
      if (items.length > 0) {
        this.$nextTick(() => {
          if (index == 0) {
            this.selectWidget(items[0], this.widget);
          } else {
            this.selectWidget(items[index - 1], this.widget);
          }
        });
      }
    },

    selectCell(item, e, r, i) {
      // TODO: 多选进行合并操作
      // if (e.ctrlKey && this.designer.selectedWidget && this.designer.selectedWidget.wtype == 'WidgetFormItem') {
      //   // 不选中
      //   let i = this.multiSelectIds.indexOf(this.designer.selectedId);
      //   if (i == -1) {
      //     this.multiSelectIds.push(this.designer.selectedId);
      //     let row = this.vGrid[r];
      //     // 判断是否是连续区域
      //   }
      //   i = this.multiSelectIds.indexOf(item.id);
      //   if (i == -1) {
      //     this.multiSelectIds.push(item.id);
      //   }
      //   console.log('当前区域多选: ', this.multiSelectIds);
      //   // 判断是否是连续区域
      // } else {
      //   this.multiSelectIds.splice(0, this.multiSelectIds.length);
      //   this.selectWidget(item, this.widget);
      // }

      this.selectWidget(item, this.widget);
    }
  },

  watch: {
    'designer.selectedId': {
      handler(v) {
        // if (this.designer.selectedWidget && this.designer.selectedWidget.wtype !== 'WidgetFormItem') {
        //   this.multiSelectIds.splice(0, this.multiSelectIds.length);
        // }
      }
    },
    'widget.configuration.labelColumnWidth': {
      handler(v) {
        if (v != this.dragX) {
          this.dragX = v;
          this.updateSiblingWidgetFormLayoutLabelColWidth(this.dragX);
        }
      }
    }
  },
  beforeMount() {
    if (this.widget.configuration.columnWidthAvg == undefined) {
      this.$set(this.widget.configuration, 'columnWidthAvg', true);
    }
    if (this.widget.configuration.colgroup == undefined) {
      let colgroup = [];
      for (let i = 0; i < this.widget.configuration.column; i++) {
        colgroup.push({ width: undefined, widthUnit: 'px' });
      }
      this.$set(this.widget.configuration, 'colgroup', colgroup);
    }
  },
  mounted() {
    this.resizeContainerWidth = this.$el.querySelector('.widget-form-layout').clientWidth;
  },

  beforeDestroy() {}
};
</script>
