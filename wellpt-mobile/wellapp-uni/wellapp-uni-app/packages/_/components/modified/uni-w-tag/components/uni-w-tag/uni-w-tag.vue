<template>
  <view class="uni-w-tag" v-if="text" :class="classes" :style="customStyle" @click="onClick">
    <slot>
      {{ text }}
    </slot>
  </view>
</template>

<script>
/**
 * Tag 标签
 * @description 用于展示1个或多个文字标签，可点击切换选中、不选中的状态
 * @tutorial https://ext.dcloud.net.cn/plugin?id=35
 * @property {String} text 标签内容
 * @property {String} size = [default|small|mini] 大小尺寸
 * 	@value default 正常
 * 	@value small 小尺寸
 * @property {String} type = [default|primary|success｜warning｜error]  颜色类型
 * 	@value default 灰色
 * 	@value primary 蓝色
 * 	@value success 绿色
 * 	@value warning 黄色
 * 	@value error 红色
 * @property {Boolean} disabled = [true|false] 是否为禁用状态
 * @property {Boolean} inverted = [true|false] 是否无需背景颜色（空心标签）
 * @property {Boolean} circle = [true|false] 是否为圆角
 * @event {Function} click 点击 Tag 触发事件
 */

export default {
  name: "UniTag",
  props: {
    type: {
      // 标签类型default、primary、success、warning、error、sub
      type: String,
      default: "default",
      validator: function validator(val) {
        return ["default", "primary", "success", "warning", "error", "sub"].includes(val);
      },
    },
    bordered: {
      type: [Boolean, String],
      default: false,
    },
    size: {
      // 标签大小 normal, small
      type: String,
      default: "normal",
    },
    // 标签内容
    text: {
      type: String,
      default: "",
    },
    disabled: {
      // 是否为禁用状态
      type: [Boolean, String],
      default: false,
    },
    inverted: {
      // 是否为空心
      type: [Boolean, String],
      default: false,
    },
    circle: {
      // 是否为圆角样式
      type: [Boolean, String],
      default: false,
    },
    mark: {
      type: [Boolean, String],
      default: false,
      validator: function validator(val) {
        return typeof val === "boolean" || ["top", "bottom", "left", "right"].includes(val);
      },
    },
    customStyle: {
      type: [Object, String],
      default: "",
    },
  },
  computed: {
    classes() {
      const { type, bordered, disabled, inverted, circle, mark, size, isTrue } = this;
      const classArr = [
        bordered ? "bordered" : "",
        type,
        isTrue(disabled) ? "disabled" : "",
        isTrue(inverted) ? "" : "bg-color",
        isTrue(circle) ? "circle" : "",
        isTrue(mark) ? "left-mark" : mark ? mark + "-mark" : "",
        size === "small" ? "small" : "",
      ];
      // 返回类的字符串，兼容字节小程序
      return classArr.join(" ");
    },
  },
  methods: {
    isTrue(value) {
      return value === true || value === "true";
    },
    onClick() {
      if (this.isTrue(this.disabled)) return;
      this.$emit("click");
    },
  },
};
</script>

<style lang="scss" scoped>
.uni-w-tag {
  display: inline-block;
  --w-tag-border-color: transparent;
  --w-tag-border-width: var(--w-border-width-base);
  --w-tag-border-style: var(--w-border-style-base);
  --w-tag-text-color: var(--w-text-color-dark);
  --w-tag-text-size: var(--w-font-size-base);
  --w-tag-text-weight: var(--w-font-weight-regular);
  --w-tag-background: var(--w-fill-color-light);
  --w-tag-background-hover: var(--w-fill-color-dark);
  --w-tag-lr-padding: var(--w-padding-2xs);
  --w-tag-height: var(--w-height-2xs);
  --w-tag-border-color-bordered: var(--w-gray-color-3);
  --w-tag-border-radius: var(--w-border-radius-2);
  --w-tag-background-color: var(--w-fill-color-dark);
  --w-tag-border-radius-single: calc(var(--w-tag-height) / 2);
  --w-tag-margin: 0;
  --w-tag-margin-right: var(--w-margin-2xs);
  --w-tag-line-height: calc(var(--w-tag-height) + var(--w-tag-border-width));
  --w-tag-max-width: 120px;

  height: var(--w-tag-height);
  line-height: var(--w-tag-line-height);
  border-radius: var(--w-tag-border-radius);
  font-size: var(--w-tag-text-size);
  font-weight: var(--w-tag-text-weight);
  padding: 0 var(--w-tag-lr-padding);
  color: var(--w-tag-text-color);
  background: var(--w-tag-background);
  border: var(--w-tag-border-width) var(--w-tag-border-style) var(--w-tag-border-color);
  vertical-align: middle;
  width: auto;
  max-width: var(--w-tag-max-width);
  margin: var(--w-tag-margin);
  overflow: hidden;
  white-space: nowrap;
  text-overflow: ellipsis;

  &:not(:last-child) {
    margin-right: var(--w-tag-margin-right);
  }

  &.bordered {
    --w-tag-border-color: var(--w-tag-border-color-bordered);
  }

  &.small {
    --w-tag-height: var(--w-height-3xs);
    --w-tag-text-size: var(--w-font-size-sm);
  }

  &.circle {
    --w-tag-border-radius: var(--w-tag-border-radius-single);
  }

  &.left-mark {
    --w-tag-border-radius: 0 var(--w-tag-border-radius-single) var(--w-tag-border-radius-single) 0;
  }
  &.right-mark {
    --w-tag-border-radius: var(--w-tag-border-radius-single) 0 0 var(--w-tag-border-radius-single);
  }
  &.top-mark {
    --w-tag-border-radius: 0 0 var(--w-tag-border-radius-single) var(--w-tag-border-radius-single);
  }
  &.bottom-mark {
    --w-tag-border-radius: var(--w-tag-border-radius-single) var(--w-tag-border-radius-single) 0 0;
  }

  &.bg-color {
    --w-tag-text-color: #ffffff;
    --w-tag-background: var(--w-tag-background-color); //实心背景
  }

  &.default.bg-color {
    --w-tag-text-color: var(--w-text-color-dark);
  }

  &.disabled {
    --w-tag-text-color: var(--text-color-disable);
    --w-tag-background: var(--w-gray-color-1);
    --w-tag-background-color: var(--w-gray-color-4);
    --w-tag-border-color-bordered: var(--w-gray-color-3);
  }

  &.primary {
    --w-tag-text-color: var(--w-primary-color);
    --w-tag-background: var(--w-primary-color-1);
    --w-tag-background-color: var(--w-primary-color);
    --w-tag-border-color-bordered: var(--w-primary-color-3);
  }

  &.danger {
    --w-tag-text-color: var(--w-danger-color);
    --w-tag-background: var(--w-danger-color-1);
    --w-tag-background-color: var(--w-danger-color);
    --w-tag-border-color-bordered: var(--w-danger-color-3);
  }

  &.warning {
    --w-tag-text-color: var(--w-warning-color);
    --w-tag-background: var(--w-warning-color-1);
    --w-tag-background-color: var(--w-warning-color);
    --w-tag-border-color-bordered: var(--w-warning-color-3);
  }

  &.success {
    --w-tag-text-color: var(--w-success-color);
    --w-tag-background: var(--w-success-color-1);
    --w-tag-background-color: var(--w-success-color);
    --w-tag-border-color-bordered: var(--w-success-color-3);
  }
  &.sub {
    --w-tag-text-color: var(--w-sub-color);
    --w-tag-background: var(--w-sub-color-1);
    --w-tag-background-color: var(--w-sub-color);
    --w-tag-border-color-bordered: var(--w-sub-color-3);
  }
}
</style>
