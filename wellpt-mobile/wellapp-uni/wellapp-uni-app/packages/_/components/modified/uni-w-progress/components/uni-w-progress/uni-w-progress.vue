<template>
  <view :style="colorStyle">
    <LineProgress
      v-if="type == 'line'"
      :percent="innserPercentage"
      :showPercent="showPercent"
      :round="round"
      :activeColor="activeColorCom"
      :inactiveColor="inactiveColorCom"
      :height="height"
      :striped="striped || status == 'active'"
      :stripedActive="stripedActive || status == 'active'"
      :textStyle="progressTextStyle"
    >
      <slot v-if="$slots.default || $slots.$default" />
      <block v-else>
        <view v-if="status == 'success' || status == 'exception'">
          <w-icon
            v-if="status == 'success'"
            icon="iconfont icon-ptkj-mianxingchenggongtishi"
            :color="progressTextStyle.color"
            :size="progressTextStyle.fontSize"
          ></w-icon>
          <w-icon
            v-else-if="status == 'exception'"
            icon="iconfont icon-ptkj-mianxingshibaitishi"
            :color="progressTextStyle.color"
            :size="progressTextStyle.fontSize"
          ></w-icon>
        </view>
        <text v-else>{{ innserPercentage + "%" }}</text>
      </block>
    </LineProgress>
    <CircleProgress
      v-else
      :percent="innserPercentage"
      :showPercent="showPercent"
      :round="round"
      :width="width"
      :borderWidth="height"
      :activeColor="activeColorCom"
      :inactiveColor="inactiveColorCom"
      :textStyle="progressTextStyle"
      :gapDegree="gapDegree"
      :gapPosition="gapPosition"
    >
      <view :style="progressTextStyle" class="u-progress-text" v-if="showPercent">
        <slot v-if="$slots.default || $slots.$default" />
        <block v-else>
          <view v-if="status == 'success' || status == 'exception'">
            <w-icon
              v-if="status == 'success'"
              icon="iconfont icon-ptkj-mianxingchenggongtishi"
              :color="progressTextStyle.color"
              :size="progressTextStyle.fontSize"
            ></w-icon>
            <w-icon
              v-else-if="status == 'exception'"
              icon="iconfont icon-ptkj-mianxingshibaitishi"
              :color="progressTextStyle.color"
              :size="progressTextStyle.fontSize"
            ></w-icon>
          </view>
          <text v-else>{{ innserPercentage + "%" }}</text>
        </block>
      </view>
    </CircleProgress>
  </view>
</template>

<script>
import LineProgress from "./uni-w-line-progress.vue";
import CircleProgress from "./uni-w-circle-progress.vue";
import { utils } from "wellapp-uni-framework";
export default {
  name: "uni-w-progress",
  components: { LineProgress, CircleProgress },
  props: {
    // 两端是否显示半圆形
    round: {
      type: Boolean,
      default: true,
    },
    // 线性、圆形
    type: {
      type: String,
      default: "",
    },
    // 激活部分的颜色
    activeColor: {
      type: String,
      default: "#488cee",
    },
    inactiveColor: {
      type: String,
      default: "#f5f5f5",
    },
    // 进度百分比，数值
    percent: {
      type: [Number, String],
      default: 0,
    },
    // 是否在进度条内部显示百分比的值
    showPercent: {
      type: Boolean,
      default: true,
    },
    // 进度条的高度
    height: {
      type: [Number, String],
      default: 12,
    },
    // 圆形进度条宽度
    width: {
      type: [Number, String],
      default: 200,
    },
    // 是否显示条纹
    striped: {
      type: Boolean,
      default: false,
    },
    // 条纹是否显示活动状态
    stripedActive: {
      type: Boolean,
      default: false,
    },
    status: {
      type: String,
      default: "default",
    },
    success: {
      type: Object,
      default: () => ({}),
    },
    exception: {
      type: Object,
      default: () => ({}),
    },
    textStyle: {
      type: Object,
      default: () => ({
        color: "#333",
        fontSize: 14,
        fontWeight: "normal",
      }),
    },
    gapDegree: {
      type: Number,
      default: 0,
    },
    gapPosition: {
      type: String,
      default: "bottom",
    },
  },
  data() {
    return {
      successDefault: {
        strokeColor: "#19be6b",
        bgColor: "#ececec",
        textColor: "#19be6b",
      },
      exceptionDefault: {
        strokeColor: "#f56c6c",
        bgColor: "#ececec",
        textColor: "#f56c6c",
      },
      colorStyle: {},
    };
  },
  computed: {
    progressTextStyle() {
      let style = utils.deepClone(this.textStyle);
      style.color = this.getColorVal(this.status, "textColor", this.textStyle.color, "#333", "--w-progress-text-color");
      return style;
    },
    activeColorCom() {
      return this.getColorVal(this.status, "strokeColor", this.activeColor, "#488cee", "--w-progress-active-color");
    },
    inactiveColorCom() {
      return this.getColorVal(this.status, "bgColor", this.inactiveColor, "#f5f5f5", "--w-progress-inactive-color");
    },
    innserPercentage() {
      // 控制范围在0-100之间
      return this.range(0, 100, this.percent);
    },
  },
  methods: {
    /**
     * @description 如果value小于min，取min；如果value大于max，取max
     * @param {number} min
     * @param {number} max
     * @param {number} value
     */
    range(min = 0, max = 0, value = 0) {
      return Math.max(min, Math.min(max, Number(value) || 0));
    },
    getColorValue(color) {
      if (color) {
        return color.startsWith("#") ? color : `var(${color})`;
      }
      return "";
    },
    getColorVal(status, param, color, defaultColor, styleParam) {
      this.colorStyle[styleParam] = "";
      if (this.status == "success" || this.status == "exception") {
        color =
          this[this.status].defaultColor == "default"
            ? this[this.status + "Default"][param]
            : this[this.status][param] || this[this.status + "Default"][param];
        if (!color.startsWith("#")) {
          this.colorStyle[styleParam] = this.getColorValue(color);
          color = this[this.status + "Default"][param];
        }
      } else {
        color = color.startsWith("#") ? color : defaultColor;
        if (!color.startsWith("#")) {
          this.colorStyle[styleParam] = this.getColorValue(color);
          color = defaultColor;
        }
      }
      return color;
    },
  },
};
</script>

<style lang="scss" scoped></style>
