<template>
  <view v-if="!collapsable" class="w-section">
    <view v-if="!hideHeader" class="w-section-header" nvue>
      <slot name="header">
        <view v-if="extraIcon" class="w-section__head-icon">
          <uni-icons
            :type="extraIcon.type"
            :size="extraIcon.size || '16'"
            :color="extraIcon.color || '#000'"
          ></uni-icons>
        </view>
        <view v-else-if="type" class="w-section__head">
          <view :class="type" class="w-section__head-tag" />
        </view>
      </slot>
      <view class="w-section__content">
        <slot name="content">
          <text :class="{ distraction: !subTitle }" class="w-section__content-title">{{ title }}</text>
          <text v-if="subTitle" class="w-section__content-sub">{{ subTitle }}</text>
        </slot>
      </view>
      <view v-if="iconType" class="w-section-right">
        <uni-icons class="icon" :type="iconType" @tap="onIconTap($event, iconType)" />
      </view>
    </view>
    <view class="w-section-content" :style="{ padding: padding ? '10px' : '' }">
      <slot />
    </view>
  </view>
  <view v-else class="w-section">
    <uni-collapse ref="collapse">
      <uni-collapse-item :title="title">
        <template v-slot:title>
          <view class="w-section-header">
            <slot name="header">
              <view v-if="extraIcon" class="w-section__head-icon">
                <uni-icons
                  :type="extraIcon.type"
                  :size="extraIcon.size || '16'"
                  :color="extraIcon.color || '#000'"
                ></uni-icons>
              </view>
              <view v-else-if="type" class="w-section__head">
                <view :class="type" class="w-section__head-tag" />
              </view>
            </slot>
            <view class="w-section__content">
              <slot name="content">
                <text :class="{ distraction: !subTitle }" class="w-section__content-title">{{ title }}</text>
                <text v-if="subTitle" class="w-section__content-sub">{{ subTitle }}</text>
              </slot>
            </view>
          </view>
        </template>
        <slot />
      </uni-collapse-item>
    </uni-collapse>
  </view>
</template>

<script>
/**
 * Section 标题栏
 * @description 标题栏
 * @property {String} type = [line|circle] 标题装饰类型
 * 	@value line 竖线
 * 	@value circle 圆形
 * @property {String} title 主标题
 * @property {String} subTitle 副标题
 * @property {String} collapsable 是否可折叠
 * @property {String} extraIcon 标题图标
 */

export default {
  name: "UniSection",
  emits: ["click"],
  props: {
    type: {
      type: String,
      default: "",
    },
    title: {
      type: String,
      default: "",
    },
    subTitle: {
      type: String,
      default: "",
    },
    hideHeader: {
      type: Boolean,
      default: false,
    },
    iconType: {
      type: String,
      default: "",
    },
    padding: {
      type: Boolean,
      default: false,
    },
    collapsable: {
      type: Boolean,
      default: false,
    },
    extraIcon: {
      type: Object,
    },
  },
  data() {
    return {};
  },
  watch: {
    title(newVal) {
      if (uni.report && newVal !== "") {
        uni.report("title", newVal);
      }
    },
  },
  methods: {
    onClick() {
      this.$emit("click");
    },
    onIconTap(e, iconType) {
      this.$emit("iconTap", iconType);
    },
  },
};
</script>
<style lang="scss" scoped>
.w-section {
  background: $uni-bg-secondary-color;
  // overflow: hidden;
}
.w-section-header {
  position: relative;
  /* #ifndef APP-NVUE */
  display: flex;
  /* #endif */
  flex-direction: row;
  align-items: center;
  padding: 12px 10px;
  // height: 50px;
  font-weight: normal;
}
.w-section__head {
  flex-direction: row;
  justify-content: center;
  align-items: center;
  margin-right: 10px;
}

.w-section__head-icon {
  margin-right: 6px;
}

.line {
  height: 12px;
  background-color: $uni-primary;
  border-radius: 10px;
  width: 4px;
}

.circle {
  width: 8px;
  height: 8px;
  border-top-right-radius: 50px;
  border-top-left-radius: 50px;
  border-bottom-left-radius: 50px;
  border-bottom-right-radius: 50px;
  background-color: $uni-primary;
}

.w-section__content {
  /* #ifndef APP-NVUE */
  display: flex;
  /* #endif */
  flex-direction: column;
  flex: 1;
  color: #333;
}

.w-section__content-title {
  font-size: 14px;
  color: $uni-text-color;
}

.distraction {
  flex-direction: row;
  align-items: center;
}

.w-section__content-sub {
  font-size: 12px;
  color: #999;
  line-height: 16px;
  margin-top: 2px;
}

.w-section-right {
  .icon {
    color: $uni-icon-color !important;
  }
}
</style>
