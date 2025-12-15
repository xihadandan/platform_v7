<template>
  <div
    :class="['widget-position-layout', selected ? 'selected' : '']"
    @click.stop="selectWidget(widget)"
    style="position: relative; outline: 1px dashed #000"
    @dragover.stop="onDragoverPositionLayout"
    @keydown="keydown"
    tabindex="0"
  >
    <div class="toolbar" @click.stop="() => {}" v-show="underSelected">
      <a-button size="small" icon="arrow-up" title="上移一层" @click.native.stop="addWidgetZIndex(1)" />
      <a-button size="small" icon="arrow-down" title="下移一层" @click.native.stop="addWidgetZIndex(-1)" />
      <a-button size="small" icon="vertical-align-top" title="置顶层" @click.native.stop="addWidgetZIndex('top')" />
      <a-button size="small" icon="vertical-align-bottom" title="置底层" @click.native.stop="addWidgetZIndex('bottom')" />
      <a-button size="small" icon="pic-center" title="居中" @click.native.stop="centerWidget" />
      <a-button size="small" icon="column-width" title="水平铺满" @click.native.stop="fillWidget('w')" />
      <a-button size="small" icon="column-height" title="垂直铺满" @click.native.stop="fillWidget('h')" />
      <a-dropdown>
        <a-button size="small" icon="switcher" title="切换组件" @click.native.stop="() => {}" />
        <a-menu slot="overlay">
          <a-menu-item
            key="0"
            v-for="(wgt, i) in widget.configuration.widgets"
            :key="'switch_' + wgt.id"
            @click="selectWidget(wgt, widget)"
          >
            {{ wgt.title }}
          </a-menu-item>
        </a-menu>
      </a-dropdown>
    </div>
    <!-- 拖拽区域 -->
    <draggable
      class="position-layout-draggable"
      :style="{
        width: '100%',
        height: '100%',
        position: 'absolute'
      }"
      :list="widgets"
      v-bind="{
        group: draggableConfig.dragGroup,
        ghostClass: 'position-ghost',
        dragoverBubble: true
      }"
      @add="e => onDragAddPositionLayout(e)"
      @onEnd="e => onDragEndPositionLayout(e)"
      handle=".widget-drag-handler"
    >
      <div
        :style="{
          width: '100%',
          height: '100%',
          position: 'absolute',
          zIndex: designer.dragging && !isSubContainerWidgetSelected ? 200 : -1
        }"
      ></div>
    </draggable>
    <vue-draggable-resizable
      @drop="onDropPositionLayout"
      :style="vStyle"
      :draggable="false"
      :resizable="true"
      :minHeight="30"
      ref="container"
      h="auto"
      :handles="['bm']"
      @resizing="(x, y, w, h) => onResizingContainer(x, y, w, h)"
      @click.stop="() => {}"
    >
      <transition-group name="fade" tag="div" style="width: 100%; height: 100%; position: relative" class="resize-drop-panel">
        <template v-for="(wgt, i) in widget.configuration.widgets">
          <vue-draggable-resizable
            v-if="widget.configuration.widgetPosition[wgt.id] != undefined"
            :grid="[1, 1]"
            :x="widget.configuration.widgetPosition[wgt.id].x"
            :y="widget.configuration.widgetPosition[wgt.id].y"
            :z="widget.configuration.widgetPosition[wgt.id].z"
            :h="widget.configuration.widgetPosition[wgt.id].h"
            :w="widget.configuration.widgetPosition[wgt.id].w"
            ref="resizeWrapper"
            :id="'resizewrapper_' + wgt.id"
            @resizing="(x, y, w, h) => onResizing(x, y, w, h, wgt)"
            @dragging="(x, y) => onDragging(x, y, wgt, i)"
            @dragstop="e => onDragstop(e, wgt)"
            :parent="true"
            :key="'dragitem_' + i"
            @dblclick.native.stop="e => dblclickResize(e, wgt)"
          >
            <div class="top-line" v-show="widgetPositionMatch[wgt.id] && widgetPositionMatch[wgt.id].top"></div>
            <div class="horizon-middle-line" v-show="widgetPositionMatch[wgt.id] && widgetPositionMatch[wgt.id].horizonMiddle"></div>
            <div class="bottom-line" v-show="widgetPositionMatch[wgt.id] && widgetPositionMatch[wgt.id].bottom"></div>
            <div class="left-line" v-show="widgetPositionMatch[wgt.id] && widgetPositionMatch[wgt.id].left"></div>
            <div class="vertical-middle-line" v-show="widgetPositionMatch[wgt.id] && widgetPositionMatch[wgt.id].verticalMiddle"></div>
            <div class="right-line" v-show="widgetPositionMatch[wgt.id] && widgetPositionMatch[wgt.id].right"></div>
            <!-- 间距计算 -->
            <template v-if="widgetDistance[wgt.id]">
              <div
                v-if="widgetDistance[wgt.id].top"
                class="top-distance"
                :style="{
                  top: '-' + widgetDistance[wgt.id].top + 'px'
                }"
              >
                <div
                  class="line-1"
                  :style="{
                    height: widgetDistance[wgt.id].top + 'px'
                  }"
                ></div>
                <div
                  class="line-2"
                  :style="{
                    height: widgetDistance[wgt.id].top + 'px'
                  }"
                ></div>
                <div
                  class="line-label"
                  :style="{
                    top: widgetDistance[wgt.id].top / 2 - 12 + 'px'
                  }"
                >
                  {{ widgetDistance[wgt.id].top }}px
                </div>
              </div>

              <div
                v-if="widgetDistance[wgt.id].left"
                class="left-distance"
                :style="{
                  left: '-' + widgetDistance[wgt.id].left + 'px',
                  width: widgetDistance[wgt.id].left + 'px'
                }"
              >
                <div class="line-1" :style="{ top: '50%', width: widgetDistance[wgt.id].left + 'px' }"></div>
                <div class="line-2" :style="{}"></div>
                <div class="line-label" :style="{ left: '50%' }">{{ widgetDistance[wgt.id].left }}px</div>
              </div>
            </template>

            <WDesignItem
              :widget="wgt"
              :index="i"
              :widgetsOfParent="widget.configuration.widgets"
              :designer="designer"
              :parent="widget"
              ref="item"
              @mounted="onItemMounted"
              style="width: auto"
            />
          </vue-draggable-resizable>
        </template>
      </transition-group>
    </vue-draggable-resizable>
    <div class="position-x-y" v-if="underSelected && !designer.dragging">
      ( {{ widget.configuration.widgetPosition[designer.selectedId].x }} ,
      {{ widget.configuration.widgetPosition[designer.selectedId].y }} )
    </div>
  </div>
</template>
<style lang="less">
.widget-position-layout {
  .position-x-y {
    position: absolute;
    z-index: 100;
    background: #e5e5e5;
    bottom: 2px;
    right: 2px;
    line-height: 25px;
    padding: 0px 10px;
    font-weight: 500;
    box-shadow: 3px 3px 7px 2px #6b6767;
  }
  &.selected {
    outline: 2px solid var(--w-primary-color) !important;
    // box-shadow: 4px 4px 5px #409eff !important;
  }
  > .toolbar {
    position: absolute;
    top: 0px;
    left: 0px;
    z-index: 200;
  }
  > .resizable {
    .vdr {
      touch-action: none;
      position: absolute;
      box-sizing: border-box;
      outline: 1px dashed black;
    }
    > .handle {
      box-sizing: border-box;
      position: absolute;
      width: 10px;
      height: 10px;
      background: #eee;
      border: 1px solid #333;
      z-index: 101;
      border-radius: 5px;

      &.handle-bm {
        bottom: 0px;
        left: 50%;
        margin-left: -5px;
        cursor: s-resize;
      }
    }
  }
  .position-layout-draggable > .position-ghost {
    box-sizing: border-box;
    color: #ffffff00;
    // background: #000;
    // outline: 2px solid #409eff;
    box-shadow: inset 0px 0px 0px 10px #e86969;
    height: e('calc(100%)');
    width: 100%;
    z-index: 100;
    position: absolute;
    padding: 0;
    // display: none;
    // box-shadow: 2px 3px 5px #000000;
  }
  .resize-drop-panel {
    .vdr {
      touch-action: none;
      position: absolute;
      box-sizing: border-box;
      outline: 1px dashed black;
      > .z-index-operas {
        position: absolute;
        top: 0px;
        left: 0px;
        z-index: 200;
        opacity: 0;
      }
      &:hover {
        > .z-index-operas {
          opacity: 1;
        }
      }
    }
    .handle {
      box-sizing: border-box;
      position: absolute;
      width: 10px;
      height: 10px;
      background: #eee;
      border: 1px solid #333;
      z-index: 100;
      border-radius: 5px;
    }
    .handle-tl {
      top: -5px;
      left: -5px;
      cursor: nw-resize;
    }
    .handle-tm {
      top: -5px;
      left: 50%;
      margin-left: -5px;
      cursor: n-resize;
    }
    .handle-tr {
      top: -5px;
      right: -5px;
      cursor: ne-resize;
    }
    .handle-ml {
      top: 50%;
      margin-top: -5px;
      left: -5px;
      cursor: w-resize;
    }
    .handle-mr {
      top: 50%;
      margin-top: -5px;
      right: -5px;
      cursor: e-resize;
    }
    .handle-bl {
      bottom: -5px;
      left: -5px;
      cursor: sw-resize;
    }
    .handle-bm {
      bottom: -5px;
      left: 50%;
      margin-left: -5px;
      cursor: s-resize;
    }
    .handle-br {
      bottom: -5px;
      right: -5px;
      cursor: se-resize;
    }
    --w-drag-match-line-color: red;
    .top-line {
      width: 2000px;
      position: absolute;
      border-top: 1px dashed var(--w-drag-match-line-color);
      left: -1000px;
    }
    .horizon-middle-line {
      width: 2000px;
      position: absolute;
      border-top: 1px dashed var(--w-drag-match-line-color);
      left: -1000px;
      top: 50%;
    }
    .bottom-line {
      width: 2000px;
      position: absolute;
      border-top: 1px dashed var(--w-drag-match-line-color);
      left: -1000px;
      bottom: 0px;
    }
    .right-line {
      height: 2000px;
      position: absolute;
      border-left: 1px dashed var(--w-drag-match-line-color);
      right: 0px;
      top: -1000px;
    }
    .vertical-middle-line {
      height: 2000px;
      position: absolute;
      border-left: 1px dashed var(--w-drag-match-line-color);
      right: 50%;
      top: -1000px;
    }
    .left-line {
      height: 2000px;
      position: absolute;
      border-left: 1px dashed var(--w-drag-match-line-color);
      left: 0px;
      top: -1000px;
    }

    .top-distance {
      position: absolute;
      width: 100%;

      > .line-1 {
        position: absolute;
        border-top: 1px dashed var(--w-drag-match-line-color);
        width: 100%;
        z-index: 2;
      }
      > .line-2 {
        position: absolute;
        height: 100%;
        left: 50%;
        border-left: 1px dashed var(--w-drag-match-line-color);
      }
      > .line-label {
        position: absolute;
        background-color: #fff;
        left: 50%;
        transform: translateX(-50%);
        color: red;
        font-size: 12px;
        z-index: 100;
        width: fit-content;
      }
    }

    .left-distance {
      position: absolute;
      width: 100%;
      height: 100%;
      > .line-1 {
        position: absolute;
        border-top: 1px dashed var(--w-drag-match-line-color);
        width: 100%;
        top: 50%;
        z-index: 2;
      }
      > .line-2 {
        position: absolute;
        height: 100%;
        border-left: 1px dashed var(--w-drag-match-line-color);
      }
      > .line-label {
        position: absolute;
        background-color: #fff;
        right: -50%;
        top: 50%;
        transform: translate(-50%, -50%);
        color: red;
        font-size: 12px;
        z-index: 100;
        width: fit-content;
      }
    }
  }
}
</style>
<script type="text/babel">
import draggable from '@framework/vue/designer/draggable';
import VueDraggableResizable from 'vue-draggable-resizable';
import { min } from 'lodash';

export default {
  name: 'DragResizePanel',
  mixins: [draggable],
  props: {
    widget: Object,
    designer: Object
  },
  provide() {
    return {
      draggable: false
    };
  },
  components: { VueDraggableResizable },
  computed: {
    selected() {
      return !!this.designer && this.widget.id === this.designer.selectedId;
    },
    vStyle() {
      let height = this.widget.configuration.style.height;
      return {
        width: '100%',
        height: typeof height == 'number' ? `${height}px` : height,
        overflow: 'hidden',
        position: 'relative'
        // background:
        //   'linear-gradient(-90deg, rgba(0, 0, 0, 0.1) 1px, transparent 1px) 10px 10px / 10px 10px, linear-gradient(rgba(0, 0, 0, 0.1) 1px, transparent 1px) 0% 0% / 10px 10px',
        // backgroundPosition: '10px 10px'
      };
    },
    underSelected() {
      for (let w of this.widget.configuration.widgets) {
        if (this.designer.selectedId == w.id) {
          return true;
        }
      }
      return false;
    },
    isSubContainerWidgetSelected() {
      let current = this.designer.widgetTreeMap[this.designer.selectedId],
        parentKey = null;
      if (current && (current.category == 'basicContainer' || current.category == 'advanceContainer')) {
        parentKey = current.parentKey;
        while (parentKey && parentKey != this.widget.id) {
          current = this.designer.widgetTreeMap[parentKey];
          if (current && current.parentKey == this.widget.id) {
            return true;
          } else {
            parentKey = current.parentKey;
          }
        }

        return parentKey == this.widget.id;
      }

      return false;
    }
  },
  data() {
    let widgets = JSON.parse(JSON.stringify(this.widget.configuration ? this.widget.configuration.widgets : [])),
      widgetPositionMatch = {},
      widgetDistance = {};
    for (let w of widgets) {
      widgetPositionMatch[w.id] = {
        top: false,
        right: false,
        bottom: false,
        left: false,
        verticalMiddle: false,
        horizonMiddle: false
      };
      widgetDistance[w.id] = {
        top: undefined,
        left: undefined,
        bottom: undefined,
        right: undefined
      };
    }
    return {
      widgetDistance,
      widgetPositionMatch,
      dragover: {
        x: 0,
        y: 0
      },
      widgets
    };
  },
  beforeCreate() {},
  created() {},
  beforeMount() {},
  mounted() {},
  methods: {
    dblclickResize(e, wgt) {},
    selectWidget(widget, parent) {
      this.designer.setSelected(widget || this.widget, parent || this.parent);
    },
    // 判断是否是子容器
    isSubWidgetContainerTypeSelected() {
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
    onItemMounted({ e }) {
      let widget = e.widget,
        _this = this,
        widgetPosition = this.widget.configuration.widgetPosition[widget.id];

      setTimeout(() => {
        // console.log(`${widget.title} 实际比例: ${e.$el.clientWidth} * ${e.$el.clientHeight}`);
        if (widgetPosition.w == 'auto') {
          widgetPosition.w = e.$el.clientWidth;
        }
        if (widgetPosition.h == 'auto') {
          widgetPosition.h = e.$el.clientHeight;
        }
        _this.calculatePositionPercent(widgetPosition, widget.id);
      }, 800);
    },
    onDropPositionLayout(e) {},
    onDragoverPositionLayout(e) {
      console.log('dragover e=>', e);
      this.dragover.x = e.offsetX;
      this.dragover.y = e.offsetY;
    },
    onDragEndPositionLayout(e) {},
    onDragAddPositionLayout(e) {
      let item = e.item._underlying_vm_,
        id = e.item._underlying_vm_.id;
      let maxZ = 0;
      if (this.widget.configuration.widgetPosition) {
        for (let i in this.widget.configuration.widgetPosition) {
          maxZ = Math.max(this.widget.configuration.widgetPosition[i].z, maxZ);
        }
      }
      this.widget.configuration.widgets.push(item);

      let positionStyle = {
        x: this.dragover.x,
        y: this.dragover.y,
        w: item.category == 'basicContainer' || item.category == 'advanceContainer' ? 600 : 'auto',
        wFixed: true,
        h: 'auto',
        z: maxZ + 1
      };
      //  宽度、定位按百分比换算
      let clientWidth = this.$el.clientWidth,
        clientHeight = this.$el.clientHeight;

      positionStyle.left = parseFloat((positionStyle.x / clientWidth) * 100 + '').toFixed(2) + '%';
      positionStyle.top = parseFloat((positionStyle.y / clientHeight) * 100 + '').toFixed(2) + '%';

      this.$set(this.widget.configuration.widgetPosition, id, positionStyle);
      this.$set(this.widgetPositionMatch, id, {
        top: false,
        right: false,
        bottom: false,
        left: false,
        verticalMiddle: false,
        horizonMiddle: false
      });

      this.designer.clearSelected();
      let _this = this;
      setTimeout(() => {
        _this.designer.setSelected(item);
        _this.calculatePositionPercent(_this.widget.configuration.widgetPosition[id], id);
      }, 500);
    },

    onResizingContainer(x, y, width, height) {
      if (this.$refs.resizeWrapper) {
        for (let i of this.$refs.resizeWrapper) {
          i.checkParentSize();
        }
      }
      this.widget.configuration.style.height = height;
    },

    onResizing: function (x, y, width, height, wgt) {
      this.$set(wgt.configuration.style, 'height', height);
      // 判断是否铺满布局区

      this.widget.configuration.widgetPosition[wgt.id].x = x;
      this.widget.configuration.widgetPosition[wgt.id].y = y;
      this.widget.configuration.widgetPosition[wgt.id].w = width;
      this.widget.configuration.widgetPosition[wgt.id].h = height;
      this.calculatePositionPercent(this.widget.configuration.widgetPosition[wgt.id], wgt.id);
    },
    onDragstop(e, wgt) {
      this.clearWidgetMatchLine();
      this.calculatePositionPercent(this.widget.configuration.widgetPosition[wgt.id], wgt.id);
    },
    keydown(e) {
      if (this.matchLineTimeout) {
        clearTimeout(this.matchLineTimeout);
      }
      if (this.designer.selectedId) {
        let code = e.code,
          direction = null,
          step = 0;
        if (code == 'ArrowRight' || code == 'ArrowLeft') {
          direction = 'x';
        } else if (code == 'ArrowUp' || code == 'ArrowDown') {
          direction = 'y';
        }
        if (code == 'ArrowRight' || code == 'ArrowDown') {
          step = 1;
        } else {
          step = -1;
        }
        if (direction) {
          let el = document.querySelector(`#resizewrapper_${this.designer.selectedId}`);
          if (el && el.__vue__) {
            let v = el.__vue__;
            v[direction] = v[direction] + step;
            this.widget.configuration.widgetPosition[this.designer.selectedId][direction] = v[direction];
            this.calculatePositionPercent(this.widget.configuration.widgetPosition[this.designer.selectedId], this.designer.selectedId);
          }
          this.widgetMatchLine(this.designer.selectedId);
          let _this = this;
          this.matchLineTimeout = setTimeout(() => {
            _this.clearWidgetMatchLine();
            _this.matchLineTimeout = undefined;
          }, 500);
        }
      }
    },

    widgetMatchLine(widgetId) {
      let { x, y, w, h } = this.widget.configuration.widgetPosition[widgetId];
      let distanced = {
        top: false
      };
      let top = [],
        left = [];
      for (let id in this.widget.configuration.widgetPosition) {
        let pos = this.widget.configuration.widgetPosition[id];
        if (id != widgetId) {
          this.widgetPositionMatch[id].horizonMiddle =
            y == pos.y + pos.h / 2 || y + h / 2 == pos.y + pos.h / 2 || y + h == pos.y + pos.h / 2;
          this.widgetPositionMatch[id].verticalMiddle =
            x == pos.x + pos.w / 2 || x + w / 2 == pos.x + pos.w / 2 || x + w == pos.x + pos.w / 2;
          this.widgetPositionMatch[id].top = y == pos.y || pos.y == y + h; // 顶边匹配
          this.widgetPositionMatch[id].bottom = y + h == pos.y + pos.h || pos.y + h == y; // 底边匹配
          this.widgetPositionMatch[id].right = x + w == pos.x + pos.w || x == pos.x + w; // 右边匹配
          this.widgetPositionMatch[id].left = x == pos.x || x + w == pos.x; // 左边匹配

          //  计算顶边间距
          // let dis = y - (pos.y + pos.h);
          // if (dis > 0) {
          //   distanced.top = true;
          //   top.push(dis);
          // }
          if ((x >= pos.x && x <= pos.x + pos.w) || (x + w >= pos.x && x + w <= pos.x + pos.w)) {
            if (y > pos.y + pos.h) {
              distanced.top = true;
              top.push(y - (pos.y + pos.h));
            }
          }

          // 计算左侧间距
          if ((y >= pos.y && y <= pos.y + pos.h) || (y + h >= pos.y && y + h <= pos.y + pos.h)) {
            if (x > pos.x + pos.w) {
              distanced.left = true;
              left.push(x - (pos.x + pos.w));
            }
          }
        }
      }
      if (this.widgetDistance[widgetId] == undefined) {
        this.$set(this.widgetDistance, widgetId, {
          top: false,
          left: false,
          bottom: false,
          right: false
        });
      }
      if (!distanced.top) {
        this.widgetDistance[widgetId].top = false;
      } else {
        this.widgetDistance[widgetId].top = min(top);
      }
      if (!distanced.left) {
        this.widgetDistance[widgetId].left = false;
      } else {
        this.widgetDistance[widgetId].left = min(left);
      }
    },

    clearWidgetMatchLine() {
      for (let k in this.widgetPositionMatch) {
        for (let direct of ['top', 'right', 'bottom', 'left', 'horizonMiddle', 'verticalMiddle']) {
          this.widgetPositionMatch[k][direct] = false;
        }
      }

      for (let k in this.widgetDistance) {
        for (let direct of ['top', 'right', 'bottom', 'left']) {
          this.widgetDistance[k][direct] = false;
        }
      }
    },

    onDragging: function (x, y, wgt) {
      this.widget.configuration.widgetPosition[wgt.id].x = x;
      this.widget.configuration.widgetPosition[wgt.id].y = y;
      this.designer.setSelected(wgt);
      this.widgetMatchLine(wgt.id);
    },

    calculatePositionPercent(position, id) {
      let clientWidth = this.$el.clientWidth,
        clientHeight = this.$el.clientHeight;
      position.left = position.x + 'px'; // parseFloat((position.x / clientWidth) * 100 + '').toFixed(2) + '%';
      position.top = position.y + 'px'; // parseFloat((position.y / clientHeight) * 100 + '').toFixed(2) + '%';
      position.widthPercent = parseFloat((position.w / clientWidth) * 100 + '').toFixed(2) + '%';
      let { x, y } = position;
      // 判断 x \ y 位置决定position方向

      // 拖拽边界判断
      let resizeWrapper = document.querySelector(`#resizewrapper_${id}`).__vue__;
      delete position.right;
      delete position.bottom;
      if (resizeWrapper.left > 0 && (resizeWrapper.right == 0 || x + position.w / 2 > clientWidth / 2)) {
        // 取右间距
        position.right = resizeWrapper.right + 'px';
        delete position.left;
      }
      if (resizeWrapper.top > 0 && (resizeWrapper.bottom == 0 || y + position.h / 2 > clientHeight / 2)) {
        position.bottom = resizeWrapper.bottom + 'px';
        delete position.top;
      }

      delete position.transform;

      if (typeof position.w === 'number') {
        position.widthPercent = parseFloat((position.w / clientWidth) * 100 + '').toFixed(2) + '%';
      }
      if (typeof position.h == 'number') {
        position.heightPercent = parseFloat((position.h / clientHeight) * 100 + '').toFixed(2) + '%';
      }
    },

    addWidgetZIndex(step) {
      let wgt = this.designer.selectedWidget;
      if (wgt) {
        if (step == 1 || step == -1) {
          let z = this.widget.configuration.widgetPosition[wgt.id].z;
          z = z + step;
          if (z <= 0) {
            z = 1;
          }
          this.widget.configuration.widgetPosition[wgt.id].z = z;
        } else if (step == 'bottom') {
          // 置底
          this.widget.configuration.widgetPosition[wgt.id].z = 1;
          for (let id in this.widget.configuration.widgetPosition) {
            let wgtPos = this.widget.configuration.widgetPosition[id];
            if (id == wgt.id) {
              wgtPos.z = 1;
            } else {
              if (wgtPos.z == 1) {
                wgtPos.z++;
              }
            }
          }
        } else if (step == 'top') {
          // 置顶
          let maxZ = this.widget.configuration.widgetPosition[wgt.id].z;
          for (let i in this.widget.configuration.widgetPosition) {
            maxZ = Math.max(this.widget.configuration.widgetPosition[i].z, maxZ);
          }
          this.widget.configuration.widgetPosition[wgt.id].z = maxZ + 1;
        }
      }
    },
    centerWidget() {
      let wgt = this.designer.selectedWidget;
      if (wgt) {
        let resizeWrapper = document.querySelector(`#resizewrapper_${wgt.id}`).__vue__;
        let w = resizeWrapper.w,
          h = resizeWrapper.h,
          clientWidth = this.$el.clientWidth,
          clientHeight = this.$el.clientHeight;
        let x = clientWidth / 2 - w / 2,
          y = clientHeight / 2 - h / 2;
        this.widget.configuration.widgetPosition[wgt.id].x = x;
        this.widget.configuration.widgetPosition[wgt.id].y = y;
        // 居中计算实际的 left \ top
        this.widget.configuration.widgetPosition[wgt.id].left = '50%';
        this.widget.configuration.widgetPosition[wgt.id].top = '50%';
        this.widget.configuration.widgetPosition[wgt.id].transform = 'translate(-50%,-50%)';
      }
    },

    fillWidget(type) {
      let wgt = this.designer.selectedWidget;
      if (wgt) {
        let clientWidth = this.$el.clientWidth,
          clientHeight = this.$el.clientHeight;

        if (type == 'w') {
          this.widget.configuration.widgetPosition[wgt.id].w = clientWidth;
          this.widget.configuration.widgetPosition[wgt.id].x = 0;
          this.widget.configuration.widgetPosition[wgt.id].wFixed = false;
        }
        if (type == 'h') {
          this.widget.configuration.widgetPosition[wgt.id].h = clientHeight;
          this.widget.configuration.widgetPosition[wgt.id].y = 0;
          wgt.configuration.style.height = clientHeight;
        }

        this.calculatePositionPercent(this.widget.configuration.widgetPosition[wgt.id], wgt.id);
      }
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
    }
  },

  watch: {
    'widget.configuration.widgets': {
      handler(v) {
        let ids = [];
        for (let i of v) {
          ids.push(i.id);
        }
        for (let k in this.widget.configuration.widgetPosition) {
          if (!ids.includes(k)) {
            delete this.widget.configuration.widgetPosition[k];
          }
        }
      }
    },
    'designer.selectedId': {
      handler(v) {
        if (this.$refs.item) {
          for (let i of this.$refs.item) {
            if (i.widget) {
              if (i.widget.id == v) {
                i.$parent.active = true;
                i.$parent.zIndex = 100;
              } else if (this.widget.configuration.widgetPosition[i.widget.id]) {
                i.$parent.active = false;
                i.$parent.zIndex = this.widget.configuration.widgetPosition[i.widget.id].z;
              }
            }
          }
        }
      }
    }
  }
};
</script>
