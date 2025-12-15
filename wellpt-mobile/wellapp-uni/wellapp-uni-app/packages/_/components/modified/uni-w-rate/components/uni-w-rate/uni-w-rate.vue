<template>
  <view class="uni-w-rate" :id="elId" ref="uni-w-rate">
    <view class="uni-w-rate__content" @touchmove.stop="touchMove" @touchend.stop="touchEnd">
      <view
        class="uni-w-rate__content__item"
        :style="{
          lineHeight: size + 'px',
        }"
        v-for="(item, index) in Number(count)"
        :key="index"
        :class="[elClass]"
      >
        <uni-tooltip
          v-if="tooltips[index]"
          placement="top"
          :content="tooltips[index]"
          :class="hoverIndex == index ? tooltipsClass : ''"
        >
          <view
            class="uni-w-rate__content__item__icon-wrap"
            ref="uni-w-rate__content__item__icon-wrap"
            @tap.stop="clickHandler($event, index + 1)"
          >
            <view
              v-if="$slots.character"
              :style="{
                'padding-left': addUnit(gutter / 2),
                'padding-right': addUnit(gutter / 2),
                color: Math.floor(activeIndex) > index ? activeColorCom : inactiveColorCom,
                fontSize: size + 'px',
              }"
            >
              <slot name="character"></slot>
            </view>
            <w-icon
              v-else
              :icon="Math.floor(activeIndex) > index ? activeIcon : inactiveIcon"
              :color="Math.floor(activeIndex) > index ? activeColorCom : inactiveColorCom"
              :iconStyle="{
                'padding-left': addUnit(gutter / 2),
                'padding-right': addUnit(gutter / 2),
              }"
              :size="size"
            ></w-icon>
          </view>
          <view
            v-if="allowHalf"
            @tap.stop="clickHandler($event, index + 1)"
            class="uni-w-rate__content__item__icon-wrap uni-w-rate__content__item__icon-wrap--half"
            ref="uni-w-rate__content__item__icon-wrap"
          >
            <view
              v-if="$slots.character"
              :style="{
                'padding-left': addUnit(gutter / 2),
                'padding-right': addUnit(gutter / 2),
                color: Math.ceil(activeIndex) > index ? activeColorCom : inactiveColorCom,
                fontSize: size + 'px',
              }"
            >
              <slot name="character"></slot>
            </view>
            <w-icon
              v-else
              :icon="Math.ceil(activeIndex) > index ? activeIcon : inactiveIcon"
              :color="Math.ceil(activeIndex) > index ? activeColorCom : inactiveColorCom"
              :iconStyle="{
                'padding-left': addUnit(gutter / 2),
                'padding-right': addUnit(gutter / 2),
              }"
              :size="size"
            ></w-icon>
          </view>
        </uni-tooltip>
        <template v-else>
          <view
            class="uni-w-rate__content__item__icon-wrap"
            ref="uni-w-rate__content__item__icon-wrap"
            @tap.stop="clickHandler($event, index + 1)"
          >
            <view
              v-if="$slots.character"
              :style="{
                'padding-left': addUnit(gutter / 2),
                'padding-right': addUnit(gutter / 2),
                color: Math.floor(activeIndex) > index ? activeColorCom : inactiveColorCom,
                fontSize: size + 'px',
              }"
            >
              <slot name="character"></slot>
            </view>
            <w-icon
              v-else
              :icon="Math.floor(activeIndex) > index ? activeIcon : inactiveIcon"
              :color="Math.floor(activeIndex) > index ? activeColorCom : inactiveColorCom"
              :iconStyle="{
                'padding-left': addUnit(gutter / 2),
                'padding-right': addUnit(gutter / 2),
              }"
              :size="size"
            ></w-icon>
          </view>
          <view
            v-if="allowHalf"
            @tap.stop="clickHandler($event, index + 1)"
            class="uni-w-rate__content__item__icon-wrap uni-w-rate__content__item__icon-wrap--half"
            ref="uni-w-rate__content__item__icon-wrap"
          >
            <view
              v-if="$slots.character"
              :style="{
                'padding-left': addUnit(gutter / 2),
                'padding-right': addUnit(gutter / 2),
                color: Math.ceil(activeIndex) > index ? activeColorCom : inactiveColorCom,
                fontSize: size + 'px',
              }"
            >
              <slot name="character"></slot>
            </view>
            <w-icon
              v-else
              :icon="Math.ceil(activeIndex) > index ? activeIcon : inactiveIcon"
              :color="Math.ceil(activeIndex) > index ? activeColorCom : inactiveColorCom"
              :iconStyle="{
                'padding-left': addUnit(gutter / 2),
                'padding-right': addUnit(gutter / 2),
              }"
              :size="size"
            ></w-icon>
          </view>
        </template>
      </view>
    </view>
  </view>
</template>

<script>
/**
 * rate 评分
 * @description 该组件一般用于满意度调查，星型评分的场景
 * @tutorial https://www.uviewui.com/components/rate.html
 * @property {String Number} count 最多可选的星星数量（默认5）
 * @property {String Number} current 默认选中的星星数量（默认0）
 * @property {Boolean} disabled 是否禁止用户操作（默认false）
 * @property {String Number} size 星星的大小，单位rpx（默认32）
 * @property {String} inactive-color 未选中星星的颜色（默认#b2b2b2）
 * @property {String} active-color 选中的星星颜色（默认#FA3534）
 * @property {String} active-icon 选中时的图标名，只能为uView的内置图标（默认star-fill）
 * @property {String} inactive-icon 未选中时的图标名，只能为uView的内置图标（默认star）
 * @property {String} gutter 星星之间的距离（默认10）
 * @property {String Number} min-count 最少选中星星的个数（默认0）
 * @property {Boolean} allow-half 是否允许半星选择（默认false）
 * @event {Function} change 选中的星星发生变化时触发
 * @example <uni-w-rate :count="count" :current="2"></uni-w-rate>
 */

import { utils } from "wellapp-uni-framework";
import test from "uview-ui/libs/function/test.js";
export default {
  name: "UniWRate",
  props: {
    // 用于v-model双向绑定选中的星星数量
    // 1.4.5版新增
    value: {
      type: [Number, String],
      default: -1,
    },
    // 要显示的星星数量
    count: {
      type: [Number, String],
      default: 5,
    },
    // 当前需要默认选中的星星(选中的个数)
    // 1.4.5后通过value双向绑定，不再建议使用此参数
    current: {
      type: [Number, String],
      default: 0,
    },
    // 是否不可选中
    disabled: {
      type: Boolean,
      default: false,
    },
    // 星星的大小，单位rpx
    size: {
      type: [Number, String],
      default: 32,
    },
    // 未选中时的颜色
    inactiveColor: {
      type: String,
      default: "#b2b2b2",
    },
    // 选中的颜色
    activeColor: {
      type: String,
      default: "#fadb14",
    },
    // 星星之间的间距，单位rpx
    gutter: {
      type: [Number, String],
      default: 10,
    },
    // 最少能选择的星星个数
    minCount: {
      type: [Number, String],
      default: 0,
    },
    // 是否允许半星
    allowHalf: {
      type: Boolean,
      default: true,
    },
    // 选中时的图标(星星)
    activeIcon: {
      type: String,
      default: "star-filled",
    },
    // 未选中时的图标(星星)
    inactiveIcon: {
      type: String,
      default: "star-filled",
    },
    disabledColor: {
      type: String,
      default: "#c8c9cc",
    },
    // 自定义扩展前缀，方便用户扩展自己的图标库
    customPrefix: {
      type: String,
      default: "uicon",
    },
    colors: {
      type: Array,
      default() {
        return [];
      },
    },
    icons: {
      type: Array,
      default() {
        return [];
      },
    },
    allowClear: {
      type: Boolean,
      default: false,
    },
    tooltips: {
      type: Array,
      default: () => [],
    },
  },
  data() {
    let id = "uniWRate_" + utils.generateId();
    let activeIndex = Number(this.value != -1 ? this.value : this.current);
    return {
      // 生成一个唯一id，否则一个页面多个评分组件，会造成冲突
      elId: id,
      elClass: id,
      starBoxLeft: 0, // 评分盒子左边到屏幕左边的距离，用于滑动选择时计算距离
      // 当前激活的星星的index，如果存在value，优先使用value，因为它可以双向绑定(1.4.5新增)
      activeIndex,
      starWidth: 0, // 每个星星的宽度
      starWidthArr: [], //每个星星最右边到组件盒子最左边的距离
      // 标识是否正在滑动，由于iOS事件上touch比click先触发，导致快速滑动结束后，接着触发click，导致事件混乱而出错
      moving: false,
      hoverIndex: -1,
      tooltipsClass: "hover",
    };
  },
  watch: {
    current(val) {
      this.activeIndex = val;
    },
    value(val) {
      this.activeIndex = val;
    },
  },
  computed: {
    decimal() {
      if (this.disabled) {
        return (this.activeIndex * 100) % 100;
      } else if (this.allowHalf) {
        return 50;
      }
    },
    activeColorCom() {
      return this.activeColor.startsWith("#") ? this.activeColor : "#fadb14";
    },
    inactiveColorCom() {
      return this.inactiveColor.startsWith("#") ? this.inactiveColor : "#b2b2b2";
    },
  },
  methods: {
    // 从wellapp-uni-app\src\uni_modules\uview-ui\libs\mixin\mixin.js复制
    $uGetRect(selector, all) {
      return new Promise((resolve) => {
        uni
          .createSelectorQuery()
          .in(this)
          [all ? "selectAll" : "select"](selector)
          .boundingClientRect((rect) => {
            if (all && Array.isArray(rect) && rect.length) {
              resolve(rect);
            }
            if (!all && rect) {
              resolve(rect);
            }
          })
          .exec();
      });
    },
    // 获取评分组件盒子的布局信息
    getElRectById() {
      // uView封装的获取节点的方法，详见文档
      // #ifndef APP-NVUE
      this.$uGetRect("#" + this.elId).then((res) => {
        this.starBoxLeft = res.left;
      });
      // #endif
      // #ifdef APP-NVUE
      dom.getComponentRect(this.$refs["uni-w-rate"], (res) => {
        this.rateBoxLeft = res.size.left;
      });
      // #endif
    },
    // 获取单个星星的尺寸
    getElRectByClass() {
      // uView封装的获取节点的方法，详见文档
      // #ifndef APP-NVUE
      this.$uGetRect("." + this.elClass).then((res) => {
        this.starWidth = res.width + this.gutter;
        // 把每个星星右边到组件盒子左边的距离放入数组中
        for (let i = 0; i < this.count; i++) {
          this.starWidthArr[i] = (i + 1) * this.starWidth;
        }
      });
      // #endif
      // #ifdef APP-NVUE
      dom.getComponentRect(this.$refs["uni-w-rate__content__item__icon-wrap"][0], (res) => {
        this.rateWidth = res.size.width;
      });
      // #endif
    },
    // 手指滑动
    touchMove(e) {
      if (this.disabled) {
        return;
      }
      if (!e.changedTouches[0]) {
        return;
      }
      this.preventEvent(e);
      const x = e.changedTouches[0].pageX;
      this.getActiveIndex(x);
    },
    // 停止滑动
    touchEnd(e) {
      if (this.disabled) {
        return;
      }
      if (!e.changedTouches[0]) {
        return;
      }
      // 如果非滑动，返回
      if (!this.moving) {
        return;
      }
      this.preventEvent(e);
      const x = e.changedTouches[0].pageX;
      this.getActiveIndex(x);
    },
    // 通过点击，直接选中
    clickHandler(e, index) {
      if (this.disabled || this.moving) {
        return;
      }
      if (!e.changedTouches[0]) {
        return;
      }
      this.preventEvent(e);
      let x = 0;
      // 点击时，在nvue上，无法获得点击的坐标，所以无法实现点击半星选择
      // #ifndef APP-NVUE
      x = e.changedTouches[0].pageX;
      // #endif
      // #ifdef APP-NVUE
      // nvue下，无法通过点击获得坐标信息，这里通过元素的位置尺寸值模拟坐标
      x = index * this.starWidth + this.starBoxLeft;
      // #endif
      this.getActiveIndex(x, true);
    },
    // 发出事件
    emitEvent() {
      // 发出change事件
      this.$emit("change", this.activeIndex);
      // 同时修改双向绑定的value的值
      this.$emit("input", this.activeIndex);
    },
    // 获取当前激活的评分图标
    getActiveIndex(x, isClick = false) {
      if (this.disabled || this.readonly) {
        return;
      }
      if (this.activeIndex && this.allowClear && isClick) {
        this.activeIndex = 0;
        this.emitEvent();
        return;
      }
      // 判断当前操作的点的x坐标值，是否在允许的边界范围内
      const allstarWidth = this.starWidth * this.count + this.starBoxLeft;
      // 如果小于第一个图标的左边界，设置为最小值，如果大于所有图标的宽度，则设置为最大值
      x = uni.$u.range(this.starBoxLeft, allstarWidth, x) - this.starBoxLeft;
      // 滑动点相对于评分盒子左边的距离
      const distance = x + this.gutter;
      // 滑动的距离，相当于多少颗星星
      let index;
      // 判断是否允许半星
      if (this.allowHalf) {
        index = Math.floor(distance / this.starWidth);
        // 取余，判断小数的区间范围
        const decimal = distance % this.starWidth;
        if (decimal <= this.starWidth / 2 && decimal > 0) {
          index += 0.5;
        } else if (decimal > this.starWidth / 2) {
          index++;
        }
      } else {
        index = Math.floor(distance / this.starWidth);
        // 取余，判断小数的区间范围
        const decimal = distance % this.starWidth;
        // 非半星时，只有超过了图标的一半距离，才认为是选择了这颗星
        if (isClick) {
          if (decimal > 0) index++;
        } else {
          if (decimal > this.starWidth / 2) index++;
        }
      }
      this.activeIndex = Math.min(index, this.count);
      // 对最少颗星星的限制
      if (this.activeIndex < this.minCount) {
        this.activeIndex = this.minCount;
      }

      // 设置延时为了让click事件在touchmove之前触发
      setTimeout(() => {
        this.moving = true;
      }, 10);
      // 一定时间后，取消标识为移动中状态，是为了让click事件无效
      setTimeout(() => {
        this.moving = false;
      }, 10);

      this.emitEvent();
    },
    showDecimalIcon(index) {
      return this.disabled && parseInt(this.activeIndex) === index;
    },
    // 阻止事件冒泡
    preventEvent(e) {
      e && typeof e.stopPropagation === "function" && e.stopPropagation();
    },
    /**
     * @description 添加单位，如果有rpx，upx，%，px等单位结尾或者值为auto，直接返回，否则加上px单位结尾
     * @param {string|number} value 需要添加单位的值
     * @param {string} unit 添加的单位名 比如px
     */
    addUnit(value = "auto", unit = uni?.$u?.config?.unit ?? "px") {
      value = String(value);
      // 用uView内置验证规则中的number判断是否为数值
      return test.number(value) ? `${value}${unit}` : value;
    },
    // 颜色非#，使用默认颜色
    getColorVal(param, defaultColor) {
      let color = this[param];
      if (!color.startsWith("#")) {
        this.colorStyle[styleParam] = this.getColorValue(color);
        color = this[this.status + "Default"][param];
      }
    },
  },
  mounted() {
    this.getElRectById();
    this.getElRectByClass();
  },
};
</script>

<style scoped lang="scss">
$uni-w-rate-margin: 0 !default;
$uni-w-rate-padding: 0 !default;
$uni-w-rate-item-icon-wrap-half-top: 0 !default;
$uni-w-rate-item-icon-wrap-half-left: 0 !default;

.uni-w-rate {
  /* #ifndef APP-NVUE */
  display: flex;
  /* #endif */
  flex-direction: row;
  align-items: center;
  margin: $uni-w-rate-margin;
  padding: $uni-w-rate-padding;
  /* #ifndef APP-NVUE */
  touch-action: none;
  /* #endif */

  &__content {
    /* #ifndef APP-NVUE */
    display: flex;
    /* #endif */
    flex-direction: row;

    &__item {
      position: relative;

      &__icon-wrap {
        display: flex;
        word-break: keep-all;
        &--half {
          position: absolute;
          overflow: hidden;
          top: $uni-w-rate-item-icon-wrap-half-top;
          left: $uni-w-rate-item-icon-wrap-half-left;
          width: 50%;
          height: 100%;
        }
      }
    }
  }
  .w-icon {
    /* #ifndef APP-NVUE */
    box-sizing: border-box;
    /* #endif */
  }
}
</style>
