<template>
  <view class="content">
    <movable-area class="movableArea" :style="{ zIndex: zIndex }">
      <movable-view
        class="movableView"
        :position="position"
        :x="x"
        :y="y"
        :direction="direction"
        :damping="damping"
        :disabled="disabled"
        @change="onChange"
        @tap="onTap"
        @touchend="onTouchend"
        :style="{ visibility: viewVisible }"
      >
        <slot>
          <!-- 插槽样式必须有class="movableContent" -->
          <uniWFab
            ref="uniWFabRef"
            v-if="fabOptions"
            class="movableContent"
            :pattern="fabOptions.pattern"
            :content="fabOptions.content"
            :horizontal="horizontal"
            :vertical="vertical"
            :direction="fabOptions.direction"
            @trigger="fabOptions.trigger"
          ></uniWFab>
        </slot>
      </movable-view>
    </movable-area>
  </view>
</template>

<script>
import uniWFab from "./uni-w-fab.vue";
export default {
  name: "uni-w-fab-button",
  components: { uniWFab },
  data() {
    return {
      x: 0,
      y: 0,
      x1: 0,
      x2: 0,
      y1: 0,
      y2: 0,
      move: {
        x: 0,
        y: 0,
      },
      moveView: {
        width: this.width || 55,
        height: this.height || 55,
      },
      horizontal: "",
      vertical: "",
      viewVisible: "hidden",
    };
  },
  props: {
    damping: {
      //阻尼系数
      type: Number,
      default: 10,
    },
    direction: {
      type: String,
      default: "all",
    },
    position: {
      type: Number,
      default: 4, //1左上2右上3左下4右下
    },
    gap: {
      type: Number,
      default: 10, // 距离屏幕前后左右间距
    },
    offset: {
      //偏移量，position为1/2时，距离顶部高度；position为3/4时，距离底部高度
      type: Number,
      default: 50,
    },
    fabOptions: Object, //fab按钮参数
    width: Number, //按钮宽高获取不到时，需要填写，默认值55
    height: Number,
    zIndex: Number | String,
    disabled: Boolean,
  },
  computed: {},
  mounted() {
    this.getMovableViewInfo(() => {
      setTimeout(() => {
        if (this.position == 1 || this.position == 2) this.y = parseInt(this.y1 + this.offset);
        if (this.position == 3 || this.position == 4) this.y = parseInt(this.y2 - this.offset);
        if (this.position == 1 || this.position == 3) this.x = parseInt(this.x1);
        if (this.position == 2 || this.position == 4) this.x = parseInt(this.x2);
        this.move.x = this.x;
        this.move.y = this.y;
        this.updateFab();
        setTimeout(() => {
          this.viewVisible = "visible";
        }, 500);
      }, 10);
    });
  },
  methods: {
    onChange(e) {
      if (e.detail.source === "touch") {
        this.move.x = e.detail.x;
        this.move.y = e.detail.y;
      }
      this.$emit("change", e);
    },
    onTap(e) {
      this.$emit("onTap", e);
    },
    onTouchend() {
      this.x = this.move.x;
      this.y = this.move.y;
      setTimeout(() => {
        if (this.move.x < this.x2 / 2) {
          this.x = this.x1;
        } else {
          this.x = this.x2;
        }
        if (this.move.y > this.y2) {
          this.y = this.y2;
        } else if (this.move.y < this.y1) {
          this.y = this.y1;
        }
        // console.log("yuan" + this.x, this.y);
        this.updateFab();
        this.$emit("onTouchend", this);
      }, 100);
    },
    getMovableViewInfo(callback) {
      const query = uni.createSelectorQuery().in(this);
      query
        .select(".movableContent")
        .boundingClientRect((data) => {
          if (data && data.width > 20) {
            this.moveView = {
              width: data.width,
              height: data.height,
            };
          }
          this.getSystemInfo(callback);
        })
        .exec();
    },
    getSystemInfo(callback) {
      uni.getSystemInfo({
        success: (res) => {
          this.x1 = this.gap;
          this.x2 = parseInt(res.windowWidth) - this.gap - this.moveView.width;
          this.y1 = this.gap;
          this.y2 = parseInt(res.windowHeight) - this.gap - this.moveView.height;
          if (typeof callback == "function") {
            callback();
          }
        },
      });
    },
    updateFab() {
      if (this.fabOptions && this.$refs.uniWFabRef) {
        if (!this.fabOptions.direction || this.fabOptions.direction == "horizontal") {
          // 水平菜单
          if (this.x > this.x2 / 2) {
            // 屏幕左半部分
            this.horizontal = "right";
          } else {
            this.horizontal = "left";
          }
        } else if (this.fabOptions.direction == "vertical") {
          // 垂直菜单
          if (this.y > this.y2 / 2) {
            // 屏幕下半部分
            this.vertical = "bottom";
          } else {
            this.vertical = "top";
          }
        }
        this.$refs.uniWFabRef.update();
      }
    },
  },
};
</script>

<style scoped>
/* .content {
  position: relative;
  height: 100vh;
} */

.movableArea {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
  z-index: 999;
}

.movableView {
  pointer-events: auto;
}
</style>
