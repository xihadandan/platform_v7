<template>
  <div class="sketch-ruler-wrapper">
    <a-icon
      :title="isShowReferLine ? '隐藏参考线' : '显示参考线'"
      @click="isShowReferLine = !isShowReferLine"
      :type="isShowReferLine ? 'eye' : 'eye-invisible'"
      :style="{ position: 'absolute', zIndex: 4, color: '#fff', top: '2px', left: '1px', fontSize: '12px' }"
    />
    <VueSketchRuler
      :key="'sketch-ruler-' + scale"
      :lang="lang"
      :thick="thick"
      :scale="scale"
      :width="width"
      :height="height"
      :startX="startX"
      :startY="startY"
      :shadow="shadow"
      :horLineArr="horLineArr"
      :verLineArr="lines.v"
      :is-show-refer-line="isShowReferLine"
      :palette="palette"
      @handleLine="handleLine"
      @onCornerClick="handleCornerClick"
    ></VueSketchRuler>

    <div ref="screensRef" class="screens" @wheel="handleWheel" @scroll="handleScroll">
      <div ref="containerRef" class="screen-container" :style="vContainerRefStyle">
        <slot name="moveLineSlot" />
        <div class="canvas" :style="canvasStyle">
          <slot></slot>
        </div>
      </div>
    </div>

    <div class="minimap">
      <div class="move-cube" />
      <Moveable
        :origin="false"
        :target="['.move-cube']"
        ref="cubeMoveable"
        :draggable="true"
        @drag="onDragMinimap"
        @dragEnd="onDragMinimapEnd"
        :snappable="true"
        :bounds="{ left: 0, top: 0, right: 0, bottom: 0, position: 'css' }"
        :snapGap="true"
        :snapDirections="{
          left: true,
          right: true,
          top: true,
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
        snapContainer=".minimap"
      />
    </div>
  </div>
</template>
<style lang="less"></style>
<script type="text/babel">
import '../../../../assets/css/sketch-ruler.less';
import { throttle } from 'lodash';
import Moveable from 'vue-moveable';
export default {
  name: 'SketChRuler',
  props: {
    zoom: {
      type: Number,
      default: 100
    },
    screenWith: {
      type: Number,
      default: 960
    },
    screenHeight: {
      type: Number,
      default: 720
    }
  },
  components: { VueSketchRuler: () => import('vue-sketch-ruler'), Moveable },
  computed: {
    shadow() {
      return {
        x: 0,
        y: 0,
        width: this.screenWith,
        height: this.screenHeight
      };
    },
    canvasStyle() {
      let style = {
        width: this.screenWith + 'px',
        height: this.screenHeight + 'px',
        transform: `scale(${this.scale})`,
        transformOrigin: '0 0 0'
      };

      return style;
    },
    vContainerRefStyle() {
      return {
        ...this.containerRefStyle
        // transform: `scale(${this.scale})`,
        // transformOrigin: '0 0 0'
      };
    },
    scale() {
      return this.zoom / 100;
    },
    horLineArr() {
      return [...this.lines.h];
    }
  },
  data() {
    return {
      startX: 0,
      startY: 0,
      lines: {
        h: [],
        v: [],
        mh: [],
        mv: []
      },

      thick: 17,
      width: 500,
      height: 400,
      lang: 'zh-CN', // 中英文
      isShowRuler: true, // 显示标尺
      isShowReferLine: true, // 显示参考线
      palette: {
        bgColor: 'rgb(21, 26, 38)',
        longfgColor: '#fff',
        shortfgColor: '#fff',
        fontColor: '#fff',
        lineColor: 'var(--w-primary-color)',
        borderColor: '#DADADC',
        cornerActiveColor: 'rgb(235, 86, 72, 0.6)'
      },
      miniMapDragArea: null,
      minimapContainer: null,
      containerRefStyle: {
        width: this.screenWith + 50 + 'px',
        height: this.screenHeight + 50 + 'px'
      },
      moveX: 0,
      moveY: 0,
      dragging: false
    };
  },
  beforeCreate() {},
  created() {},
  beforeMount() {},
  mounted() {
    this.minimapContainer = this.$el.querySelector('.selectWin');
    this.minimapRect = this.$el.querySelector('.minimap').getBoundingClientRect();
    this.screenContainerRect = this.$el.querySelector('.screen-container').getBoundingClientRect();
    this.screenElement = this.$el.querySelector('.screens');
    this.moveCubeRect = this.$el.querySelector('.move-cube').getBoundingClientRect();
    this.canvasRect = this.$el.querySelector('.canvas').getBoundingClientRect();
    this.$nextTick(() => {
      this.initSize();
      this.handleScroll();
    });
    // this.initMinimapMoveable();
  },
  methods: {
    handleLine(lines) {
      this.lines = lines;
    },
    throttleScroll() {
      throttle(() => {
        this.handleScroll();
      }, 100)();
    },
    handleScroll(e) {
      if (e && !this.dragging) {
        let scrollTop = e.target.scrollTop,
          scrollLeft = e.target.scrollLeft;
         let screenRect = this.screenElement.getBoundingClientRect();
        this.$el.querySelector('.move-cube').style.transform = `translate(${
          (this.minimapRect.width - this.moveCubeRect.width) * (scrollLeft / (this.screenContainerRect.width - screenRect.width))
        }px, ${
          (this.minimapRect.height - this.moveCubeRect.height) * (scrollTop / (this.screenContainerRect.height - screenRect.height))
        }px)`;
        this.$refs.cubeMoveable.updateRect();
      }

      this.updateStartXY();
      this.$emit('scroll');
    },
    updateStartXY() {
      // 标尺开始的刻度
      let screenRect = this.screenElement.getBoundingClientRect();
      const startX = (screenRect.left + this.thick - this.canvasRect.left) / this.scale;
      const startY = (screenRect.top + this.thick - this.canvasRect.top) / this.scale;

      this.startX = startX >> 0;
      this.startY = startY >> 0;
    },
    // 控制缩放值
    handleWheel(e) {
      if (e.ctrlKey || e.metaKey) {
        e.preventDefault();
        const nextScale = parseFloat(Math.max(0.2, this.scale - e.deltaY / 500).toFixed(2));
        this.scale = nextScale;
      }
      this.$nextTick(() => {
        this.handleScroll();
      });
    },
    initSize() {
      const wrapperRect = document.querySelector('.sketch-ruler-wrapper').getBoundingClientRect();
      const borderWidth = 1;
      this.width = wrapperRect.width - this.thick - borderWidth;
      this.height = wrapperRect.height - this.thick - borderWidth;
    },
    onDragStart(e) {},
    onDragMinimapEnd() {
      this.dragging = false;
    },
    onDragMinimap(e) {
      this.dragging = true;

      e.target.style.transform = e.transform;
      let screenRect = this.screenElement.getBoundingClientRect();

      // 移动比例
      const percentageX = e.translate[0] / (this.minimapRect.width - this.moveCubeRect.width);
      const percentageY = e.translate[1] / (this.minimapRect.height - this.moveCubeRect.height);
      // 进度条需要滚动的距离
      const scrollTopLength = percentageY * (this.screenContainerRect.height - screenRect.height);
      const scrollLeftLength = percentageX * (this.screenContainerRect.width - screenRect.width);
      this.screenElement.scrollLeft = scrollLeftLength;
      this.screenElement.scrollTop = scrollTopLength;
    }
  },

  watch: {
    shadow: {
      handler() {
        this.throttleScroll();
      }
    },
    scale: {
      handler() {
        this.throttleScroll();
      }
    }
  }
};
</script>
