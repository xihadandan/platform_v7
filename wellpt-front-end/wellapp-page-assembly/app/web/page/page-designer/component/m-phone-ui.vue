<template>
  <div
    class="phone-ui"
    :style="{
      width: vWidth
    }"
  >
    <div>
      <div class="status-bar-right">
        <slot name="status-bar-right"></slot>
      </div>

      <PerfectScrollbar class="panel-scroll" :options="{ suppressScrollX: true, ...scrollOptions }">
        <slot></slot>
      </PerfectScrollbar>
      <svg
        version="1.1"
        id="图层_1"
        xmlns="http://www.w3.org/2000/svg"
        xmlns:xlink="http://www.w3.org/1999/xlink"
        x="0px"
        y="0px"
        viewBox="0 0 400 809"
        style="enable-background: new 0 0 400 809"
        xml:space="preserve"
      >
        <rect id="可编辑" x="22" y="19" :style="fill" width="357" height="771" class="rect" />

        <defs>
          <rect id="clipRoundedRect" x="19" y="19" width="363" height="771" rx="15" />
          <clipPath id="clip">
            <use xlink:href="#clipRoundedRect" />
          </clipPath>
        </defs>

        <image
          clip-path="url(#clip)"
          v-if="pageStyle && pageStyle.enableBackground && pageStyle.backgroundImage"
          x="19"
          y="19"
          width="363"
          height="771"
          id="Layer_1"
          preserveAspectRatio="xMidYMid slice"
          :xlink:href="pageStyle && pageStyle.backgroundImage"
          style="overflow: visible"
        ></image>
        <image style="overflow: visible" width="400" height="809" id="Layer_0" xlink:href="/static/images/phone.png"></image>
      </svg>
    </div>
  </div>
</template>
<style lang="less">
/* 当屏幕宽度在600px到1200px之间时应用的样式 */
@media (min-width: 600px) and (max-width: 1300px) {
  .phone-ui {
    .status-bar-right {
      right: 29px;
      top: 20px;
    }
  }
}

/* 当屏幕宽度大于1200px时应用的样式 */
@media (min-width: 1300px) {
  .phone-ui {
    .status-bar-right {
      right: 40px;
      top: 22px;
    }
  }
}

.phone-ui {
  display: flex;
  align-items: center;
  justify-content: center;
  // height: e('calc(100vh - 116px)');
  // width: 100%;
  position: relative;
  left: 50%;
  transform: translate(-50%, 0px);
  .status-bar-right {
    position: absolute;
  }
  > div {
    position: relative;
    width: 100%;
    > .panel-scroll {
      position: absolute;
      background-color: transparent;
      margin-top: 45px;
      width: 100%;
      height: e('calc(100% - 102px)') !important;
      padding-left: 21px;
      padding-right: 21px;
      > .ps__rail-y:hover {
        background-color: transparent;
        > .ps__thumb-y {
          background: transparent;
        }
      }
    }
  }
  // .widget-drop-panel:empty::after {
  //   content: '从左侧拖拽组件或布局开始设计';
  //   position: absolute;
  //   top: 50%;
  //   left: 50%;
  //   text-wrap: nowrap;
  //   transform: translate(-50%, -50%);
  //   display: block;
  //   color: var(--w-text-color-light);
  //   font-size: var(--w-font-size-base);
  // }
}
</style>
<script type="text/babel">
import { addWindowResizeHandler } from '@framework/vue/utils/util';

export default {
  name: 'MPhoneUi',
  inject: ['pageStyle'],
  props: {
    width: {
      type: [Number, String],
      default: 365
    },
    scrollOptions: {
      type: Object,
      default: function () {
        return {};
      }
    }
  },
  components: {},
  computed: {
    fill() {
      if (this.pageStyle && this.pageStyle.enableBackground && this.pageStyle.backgroundColor) {
        return 'fill:' + this.pageStyle.backgroundColor;
      }
      return 'fill:#fff';
    }
  },
  data() {
    let w = typeof this.width == 'number' ? `${this.width}px` : this.width;
    return {
      vWidth: w,
      initWidth: w
    };
  },
  beforeCreate() {},
  created() {},
  beforeMount() {},
  mounted() {
    let resize = () => {
      this.$nextTick(() => {
        if (window.innerWidth <= 1300) {
          this.vWidth = '300px';
        } else {
          this.vWidth = this.initWidth;
        }
        this.$emit('onResize');
      });
    };
    addWindowResizeHandler(() => {
      resize();
    });
    resize();
  },
  methods: {},
  watch: {}
};
</script>
