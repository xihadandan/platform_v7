<template>
  <div class="widget-build-container big-screen-build-container">
    <a-row class="widget-build-toolbar">
      <a-col :span="12">
        <a-popconfirm
          placement="bottomLeft"
          :arrowPointAtCenter="true"
          title="确认要清空吗?"
          ok-text="清空"
          cancel-text="取消"
          @confirm="clear"
        >
          <a-button type="link" size="small" icon="delete" :title="$t('PageDesigner.toolbar.clear')">
            <!-- 清空 -->
            {{ $t('PageDesigner.toolbar.clear', '清空') }}
          </a-button>
        </a-popconfirm>

        <slot name="preview-btn-slot">
          <a-button type="link" size="small" icon="eye" :title="$t('PageDesigner.toolbar.preview')" @click.stop="preview">
            <!-- 预览 -->
            {{ $t('PageDesigner.toolbar.preview', '预览') }}
          </a-button>
        </slot>
      </a-col>
      <a-col :span="12" :style="{ textAlign: 'right' }">
        <a-button v-if="designer.selectedId != undefined" type="link" size="small">
          坐标
          <a-input-number v-model="designer.selectedWidget.configuration.style.left" style="width: 80px; margin: 0px 5px" size="small" />
          <a-input-number v-model="designer.selectedWidget.configuration.style.top" style="width: 80px" size="small" />
        </a-button>

        <a-popover placement="bottom">
          <template slot="content">
            <div style="display: flex; flex-direction: column; align-items: center">
              <div>
                <a-input-number
                  v-model="zoom"
                  :min="1"
                  :max="100"
                  style="width: 58px; margin-bottom: 8px"
                  size="small"
                  @change="onChangeZoom"
                />
                %
              </div>
              <a-slider tooltipPlacement="right" vertical v-model="zoom" :min="1" :max="100" style="height: 120px" @change="onChangeZoom" />
            </div>
          </template>
          <a-button type="link" size="small">
            缩放
            <span style="display: inline-block; width: 42px">{{ zoom }}%</span>
          </a-button>
        </a-popover>
      </a-col>
    </a-row>
    <div class="widget-build-drop-container" style="position: relative">
      <!-- 用于放组件的配置抽屉展示区（不可删除） -->
    </div>
    <SketchRuler
      :key="'sketchRuler_' + rulerKey"
      :zoom="zoom"
      :screen-height="screenHeight"
      :screen-with="screenWith"
      ref="sketchRuler"
      @scroll="onScrollScreen"
    >
      <!-- <template slot="moveLineSlot">
        <div
          class="move-reference-line-h"
          :style="{
            // width: moveHorizontalLineLeft + 'px',
            width: screenWith + 'px',
            top: moveHorizontalLineTop + 'px'
          }"
        >
          <label :style="{ left: scrollLeft + 20 + 'px' }">{{ moveHorizontalLineLeft }}</label>
        </div>
        <div
          class="move-reference-line-v"
          :style="{
            // height: moveHorizontalLineTop + 'px',
            height: screenHeight + 'px',
            left: moveHorizontalLineLeft + 'px'
          }"
        >
          <label :style="{ top: scrollTop + 20 + 'px' }">{{ moveHorizontalLineTop }}</label>
        </div>
      </template> -->
      <template slot="default">
        <div
          :style="{
            width: '100%',
            height: '100%',
            position: 'absolute',
            ...vDesignStyle
          }"
          class="drop-zone bigscreen"
          @click.stop="clearSelected"
        >
          <draggable
            class="draggable-zone"
            :style="{
              width: '100%',
              height: '100%',
              position: 'absolute'
            }"
            :list="designer.widgets"
            v-bind="{
              group: draggableConfig.dragGroup,
              ghostClass: 'position-ghost',
              dragoverBubble: true
            }"
            @add="e => onDragAddToPanel(e)"
            @onEnd="e => onDragEndPanel(e)"
            handle=".widget-drag-handler"
            @dragover.native.stop="onDragover"
          ></draggable>

          <template v-for="(wgt, i) in designer.widgets">
            <wDesignMoveItem
              :widget="wgt"
              :key="wgt.id"
              :index="i"
              :widgetsOfParent="designer.widgets"
              :designer="designer"
              :dragGroup="draggableConfig.dragGroup"
            ></wDesignMoveItem>
          </template>
        </div>
      </template>
    </SketchRuler>

    <Moveable
      :key="rulerKey + 'moveable'"
      ref="designMoveable"
      :origin="false"
      :target="designMoveItemTarget"
      :draggable="true"
      :edgeDraggable="false"
      :isDisplaySnapDigit="true"
      :isDisplayInnerSnapDigit="true"
      :snappable="true"
      :bounds="{ left: 0, top: 0, right: 0, bottom: 0, position: 'css' }"
      :style="{ zIndex: 100 }"
      :snapGap="true"
      :snapDirections="{
        left: true,
        right: true,
        top: true,
        middle: true,
        center: true,
        bottom: true
      }"
      :elementSnapDirections="{
        left: true,
        right: true,
        top: true,
        bottom: true,
        center: true,
        middle: true
      }"
      snapContainer=".drop-zone"
      :elementGuidelines="elementGuidelines"
      root-container=".drop-zone"
      :resizable="true"
      @drag="onDragDesignMoveItem"
      @dragEnd="onDragDesignMoveItemEnd"
      @resize="onDesignMoveItemResize"
      :scrollable="true"
      :scrollOptions="{ container: '.screens', threshold: 50, throttleTime: 50 }"
      @scroll="onDragScroll"
      @dragGroup="onDragDesignMoveItemGroup"
      @resizeGroup="onDesignMoveItemResizeGroup"
    />
    <Selecto
      :key="rulerKey + 'selecto'"
      ref="selecto"
      dragContainer=".drop-zone"
      :selectable-targets="['.design-move-item']"
      :hit-rate="0"
      :select-by-click="true"
      :select-from-inside="false"
      :toggle-continue-select="['shift']"
      :ratio="0"
      @selectStart="onSelectStart"
      @selectEnd="onSelectEnd"
      @dragStart="onSelectDragStart"
    />

    <a-drawer
      title="组件定义JSON"
      placement="left"
      :closable="true"
      :mask="false"
      :visible="designer.widgetJsonDrawerVisible"
      :width="350"
      @close="designer.widgetJsonDrawerVisible = false"
    >
      <JsonViewer :value="jsonWidget" :expand-depth="3" copyable boxed sort>
        <div slot="copy">复制</div>
      </JsonViewer>
    </a-drawer>
  </div>
</template>
<style lang="less">
.big-screen-build-container {
  .position-ghost {
    box-sizing: border-box;
    color: #ffffff00;
    // background: #000;
    // outline: 2px solid #409eff;
    box-shadow: inset 0px 0px 0px 10px var(--w-primary-color);
    height: e('calc(100%)');
    width: 100%;
    z-index: 100;
    position: absolute;
    padding: 0;
    // display: none;
    // box-shadow: 2px 3px 5px #000000;
  }

  .moveable-resizable {
    border-radius: unset !important;
    background-color: unset !important;
    width: 20px !important;
    height: 20px !important;
    border-color: #73d13d !important ;
  }

  .moveable-n,
  .moveable-s {
    height: 1px !important;
    top: 6px !important;
    border-top: 2px solid #73d13d !important;
    border-bottom: unset !important;
    border-left: unset !important;
    border-right: unset !important;
  }

  .moveable-e,
  .moveable-w {
    border-left: 2px solid #73d13d !important;
    border-right: unset !important;
    left: 6px !important;
    border-top: unset !important;
    border-bottom: unset !important;
  }

  .moveable-nw {
    border-bottom: unset !important;
    border-right: unset !important;
    top: 6px !important;
    left: 6px !important;
  }

  .moveable-ne {
    border-left: unset !important;
    border-bottom: unset !important;
    top: 6px !important;
    left: -12px !important;
  }

  .moveable-se {
    border-top: unset !important;
    border-left: unset !important;
    top: -12px !important;
    left: -12px !important;
  }

  .moveable-sw {
    border-top: unset !important;
    border-right: unset !important;
    top: -12px !important;
    left: 6px !important;
  }

  #mb-ruler {
    .lines {
      .action {
        color: #fff;
      }
    }
  }
}
</style>
<script type="text/babel">
import 'vue-json-viewer/style.css';
import SketchRuler from './sketch-ruler.vue';
import Moveable from 'vue-moveable';
import Selecto from 'vue-selecto';
import draggable from '@framework/vue/designer/draggable';
import wDesignMoveItem from './w-design-move-item.vue';
import md5 from '@framework/vue/utils/md5';
import { queryString, generateId } from '@framework/vue/utils/util';
import JsonViewer from 'vue-json-viewer/ssr';
export default {
  name: 'WidgetBigscreenBuildPanel',
  inject: ['designer', 'draggableConfig'],
  mixins: [draggable],
  props: {
    width: {
      type: Number,
      default: 500
    },
    height: {
      type: Number,
      default: 400
    },
    screenWith: {
      type: Number,
      default: 2560
    },
    screenHeight: {
      type: Number,
      default: 1440
    },
    pageStyle: Object
  },
  components: { SketchRuler, wDesignMoveItem, Moveable, Selecto, JsonViewer },
  computed: {
    rulerKey() {
      let key = '';
      key += this.screenWith + '_' + this.screenHeight;
      return key;
    },
    // moveableKey() {
    //   let element = [];
    //   for (let i = 0, len = this.widgets.length; i < len; i++) {
    //     element.push(this.widgets[i].id);
    //   }
    //   return md5(element.join(','));
    // },
    elementGuidelines() {
      let element = [];
      for (let i = 0, len = this.designer.widgets.length; i < len; i++) {
        element.push('#design-move-item-' + this.designer.widgets[i].id);
      }
      return element;
    },
    scale() {
      return this.zoom / 100;
    },
    rulerShadow() {
      return {
        x: 0,
        y: 0,
        width: this.sketchRuler.width,
        height: this.sketchRuler.height
      };
    },

    canvasStyle() {
      return {
        width: this.width + 'px',
        height: this.height + 'px',
        transform: `scale(${this.scale})`,
        transformOrigin: '0 0 0'
      };
    },
    vDesignStyle() {
      let style = {};
      if (this.pageStyle) {
        let {
          enableBackground,
          backgroundColor,
          backgroundImage,
          backgroundImageInput,
          bgImageUseInput,
          backgroundPosition,
          backgroundRepeat
        } = this.pageStyle;

        if (backgroundColor) {
          style.backgroundColor = backgroundColor;
        }
        let bgImgStyle = bgImageUseInput ? backgroundImageInput : backgroundImage;
        if (bgImgStyle) {
          let isUrl =
            bgImgStyle.startsWith('data:') ||
            bgImgStyle.startsWith('http') ||
            bgImgStyle.startsWith('/') ||
            bgImgStyle.startsWith('../') ||
            bgImgStyle.startsWith('./');
          style.backgroundImage = isUrl ? `url("${bgImgStyle}")` : bgImgStyle;
        }
        if (backgroundPosition) {
          style.backgroundPosition = backgroundPosition;
        }
        if (backgroundRepeat) {
          style.backgroundRepeat = backgroundRepeat;
        }
      }
      return style;
    },
    jsonWidget() {
      return this.designer.selectedWidget;
    },
    widgetIds() {
      return Object.keys(this.designer.widgetIdMap);
    }
  },
  data() {
    return {
      zoom: 100,
      sketchRulerRender: false,
      sketchRuler: {
        width: this.width,
        height: this.height,
        startX: 0,
        startY: 0,
        thick: 20,
        lines: {
          h: [],
          v: []
        },
        showReferLine: true,
        cornerActive: true,
        palette: {
          bgColor: 'rgba(225,225,225, 0)',
          longfgColor: '#BABBBC',
          shortfgColor: '#C8CDD0',
          fontColor: '#000',
          font: '20px',
          shadowColor: 'transparent',
          lineColor: '#0089d0',
          borderColor: '#transparent',
          cornerActiveColor: 'rgb(235, 86, 72, 0.6)'
        }
      },
      innerHeight: 0,
      screenContainerStyle: {
        width: `${this.width}px`,
        height: `${this.height}px`
      },
      dragover: {
        x: 0,
        y: 0
      },
      designMoveItemTarget: [],
      designMoveItemWidgetId: undefined,
      moveHorizontalLineLeft: 0,
      moveHorizontalLineTop: 0,
      dragging: false,
      scrollLeft: 0,
      scrollTop: 0,
      defaultPageVars: null
    };
  },
  beforeCreate() {},
  created() {},
  beforeMount() {},
  mounted() {
    this.designer.handleEvent('widget:deleted', () => {
      this.$nextTick(() => {
        this.updateDesignMoveableRect();
      });
    });
    this.$nextTick(() => {
      let sketchRuler = document.querySelector('.sketch-ruler-wrapper');
      let rect = sketchRuler.getBoundingClientRect();
      // 根据大屏分辨率自动缩放到当前自适应的zoom
      let scaleX = rect.width / this.screenWith,
        scaleY = rect.height / this.screenHeight;
      this.zoom = parseInt(Math.min(scaleX, scaleY).toFixed(2) * 100);
      console.log(this.zoom);
    });

    document.onkeyup = e => {
      if (this.designer.selectedId && e.code == 'Delete') {
        let designItem = document.querySelector(`#design-move-item-${this.designer.selectedId}`);
        if (designItem) {
          designItem.__vue__.removeWidget();
        }
      }
    };
  },
  methods: {
    clear() {
      this.designer.clear();
    },
    undo() {
      this.designer.undo();
    },
    redo() {
      this.designer.redo();
    },
    updateDesignMoveableRect() {
      if (this.$refs.designMoveable) {
        this.$refs.designMoveable.updateRect();
      }
    },
    onScrollScreen() {
      this.updateDesignMoveableRect();
    },
    preview() {
      let urlParams = queryString(location.search.substr(1)),
        _temp = urlParams._temp || generateId();
      this.designer.tempLocalStorageKey = _temp;

      window.localStorage.setItem(
        `${_temp}`,
        JSON.stringify({
          id: this.pageId,
          widgets: this.designer.widgets,
          pageVars: JSON.parse(this.defaultPageVars),
          pageJsModule: this.designer.pageJsModule,
          pageStyle: this.vDesignStyle
        })
      );
      window.localStorage.setItem(`${_temp}_widgetMap`, JSON.stringify(this.designer.widgetIdMap));
      this.previewWindow = window.open('/page-designer/preview/' + _temp, _temp);
      if (!urlParams._temp) {
        history.pushState({}, '大屏设计', `${location.pathname}${location.search}${location.search ? '&' : '?'}_temp=${_temp}`);
      }
    },
    onSelectStart(e) {},
    onSelectEnd(e) {
      let designMoveable = this.$refs.designMoveable;
      if (e.isDragStartEnd) {
        e.inputEvent.preventDefault();
        designMoveable.waitToChangeTarget().then(() => {
          designMoveable.dragStart(e.inputEvent);
        });
      }
      this.designMoveItemTarget = e.selected;
    },

    onSelectDragStart(e) {
      const target = e.inputEvent.target;
      if (this.$refs.designMoveable.isMoveableElement(target) || this.designMoveItemTarget.some(t => t === target || t.contains(target))) {
        e.stop();
      }
    },
    onDrop(e) {},
    onDragover(e) {
      this.dragover.x = e.offsetX;
      this.dragover.y = e.offsetY;
    },
    onDragEndPanel(e) {},
    onDragAddToPanel(e) {
      let item = e.item._underlying_vm_,
        id = e.item._underlying_vm_.id;
      if (item.configuration.style == undefined) {
        this.$set(item.configuration, 'style', {
          position: 'absolute',
          left: this.dragover.x,
          top: this.dragover.y
        });
      } else {
        this.$set(item.configuration.style, 'left', this.dragover.x);
        this.$set(item.configuration.style, 'top', this.dragover.y);
        this.$set(item.configuration.style, 'position', 'absolute');
      }
    },
    onDragScroll(e) {
      e.scrollContainer.scrollBy(15 * e.direction[0], 15 * e.direction[1]);
    },
    onDragDesignMoveItemEnd() {
      this.dragging = false;
      this.designer.draggingWidgetId = undefined;
    },
    onDragDesignMoveItemGroup(e) {
      e.events.forEach(ev => {
        let x = ev.translate[0],
          y = ev.translate[1];
        ev.target.__vue__.widget.configuration.style.left = x;
        ev.target.__vue__.widget.configuration.style.top = y;
        ev.target.style.transform = ev.transform;
      });
    },
    onDesignMoveItemResizeGroup(e) {
      e.events.forEach(ev => {
        let widgetStyle = ev.target.__vue__.widget.configuration.style;
        widgetStyle.width = ev.width;
        widgetStyle.height = ev.height;
        widgetStyle.left = ev.drag.translate[0];
        widgetStyle.top = ev.drag.translate[1];
        ev.target.style.transform = ev.drag.transform;
      });
    },
    onDragDesignMoveItem(e) {
      this.dragging = true;
      this.designer.draggingWidgetId = e.target.__vue__.widget.id;
      e.target.style.transform = e.transform;
      let x = e.translate[0],
        y = e.translate[1];
      let widgetStyle = e.target.__vue__.widget.configuration.style;

      widgetStyle.left = parseFloat(x.toFixed(1));
      widgetStyle.top = parseFloat(y.toFixed(1));
      // this.moveHorizontalLineLeft = x + 30;
      // this.moveHorizontalLineTop = y + 30;

      this.scrollLeft = this.$el.querySelector('.screens').scrollLeft;
      this.scrollTop = this.$el.querySelector('.screens').scrollTop;
    },
    onDesignMoveItemResize(e) {
      let widgetStyle = e.target.__vue__.widget.configuration.style;
      e.target.style.width = `${e.width}px`;
      e.target.style.height = `${e.height}px`;
      widgetStyle.left = e.drag.translate[0];
      widgetStyle.top = e.drag.translate[1];
      widgetStyle.width = e.width;
      widgetStyle.height = e.height;
      e.target.style.transform = e.drag.transform;
    },
    clearSelected() {
      this.designer.clearSelected();
    },
    onChangeZoom() {
      this.$nextTick(() => {
        this.updateDesignMoveableRect();
      });
    }
  },

  watch: {}
};
</script>
