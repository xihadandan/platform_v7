<template>
  <view class="w-tab-layout tabs">
    <scroll-view class="scroll-h" :scroll-x="true" :show-scrollbar="false" :scroll-into-view="scrollIntoView">
      <view
        v-for="(tab, index) in tabs"
        :key="index"
        class="uni-tab-item"
        :id="tab.id"
        :data-current="index"
        @click="onTabTap"
      >
        <!-- #ifdef H5 || APP-PLUS -->
        <slot :name="'tab-title-' + tab.id" :item="tab">
          <uni-icons
            v-if="(iconField && tab[iconField]) || tab.icon"
            class="uni-tab-item-icon"
            :class="tabIndex == index ? 'uni-tab-item-icon-active' : ''"
            :type="tab[iconField] || tab.icon"
          ></uni-icons>
          <text class="uni-tab-item-title" :class="tabIndex == index ? 'uni-tab-item-title-active' : ''">
            {{ nameField ? tab[nameField] : tab.name }}
          </text>
        </slot>
        <!-- #endif -->
        <!-- #ifdef MP -->
        <slot name="{{'tab-title-' + tab.id}}" :item="tab">
          <uni-icons
            v-if="(iconField && tab[iconField]) || tab.icon"
            class="uni-tab-item-icon"
            :class="tabIndex == index ? 'uni-tab-item-icon-active' : ''"
            :type="tab[iconField] || tab.icon"
          ></uni-icons>
          <text class="uni-tab-item-title" :class="tabIndex == index ? 'uni-tab-item-title-active' : ''">
            {{ nameField ? tab[nameField] : tab.name }}
          </text>
        </slot>
        <!-- #endif -->
      </view>
    </scroll-view>
    <view class="line-h"></view>
    <swiper :current="tabIndex" class="swiper-box" :style="contentStyle" :duration="300" @change="onTabChange">
      <swiper-item class="swiper-item" v-for="(tab, index1) in tabs" :key="index1">
        <!-- #ifdef H5 || APP-PLUS -->
        <slot :name="'tab-content-' + tab.id" :item="tab">
          <view> {{ nameField ? tab[nameField] : tab.name }}</view>
        </slot>
        <!-- #endif -->
        <!-- #ifdef MP -->
        <slot name="{{'tab-content-' + tab.id}}" :item="tab">
          <view> {{ nameField ? tab[nameField] : tab.name }}</view>
        </slot>
        <!-- #endif -->
      </swiper-item>
    </swiper>
  </view>
</template>

<script>
export default {
  props: {
    tabs: Array,
    iconField: String,
    nameField: String,
    contentStyle: {
      type: Object,
      default() {
        return {};
      },
    },
  },
  data() {
    return {
      tabIndex: 0,
      scrollIntoView: "",
    };
  },
  methods: {
    onTabTap(e) {
      let index = e.target.dataset.current || e.currentTarget.dataset.current;
      this.switchTab(index);
    },
    onTabChange(e) {
      let index = e.target.current || e.detail.current;
      this.switchTab(index);
    },
    switchTab: function (index) {
      this.tabIndex = index;
      this.scrollIntoView = this.tabs[index].id;
      this.$emit("change", index);
    },
  },
};
</script>

<style lang="scss" scoped>
.w-tab-layout {
  width: 100%;
}
.tabs {
  flex: 1;
  flex-direction: column;
  overflow: hidden;
  background-color: $uni-bg-color;
  /* #ifndef APP-PLUS */
  height: 100vh;
  /* #endif */

  .scroll-h {
    width: 750rpx;
    /* #ifdef H5 */
    width: 100%;
    /* #endif */
    height: 80rpx;
    white-space: nowrap;
  }

  .line-h {
    height: 1rpx;
    background-color: #cccccc;
  }

  .uni-tab-item {
    /* #ifndef APP-PLUS */
    display: inline-block;
    /* #endif */
    flex-wrap: nowrap;
    padding-left: 34rpx;
    padding-right: 34rpx;
  }

  .uni-tab-item-icon {
    margin-right: 4px;
  }

  .uni-tab-item-icon-active {
    color: $uni-icon-color !important;
  }

  .uni-tab-item-title {
    color: #555;
    font-size: 30rpx;
    height: 80rpx;
    line-height: 80rpx;
    flex-wrap: nowrap;
    /* #ifndef APP-PLUS */
    white-space: nowrap;
    /* #endif */
  }

  .uni-tab-item-title-active {
    color: $uni-color-primary; //#007aff;
  }

  .swiper-box {
    flex: 1;
  }

  .swiper-item {
    flex: 1;
    flex-direction: row;
  }

  .scroll-v {
    flex: 1;
    /* #ifndef MP-ALIPAY */
    flex-direction: column;
    /* #endif */
    width: 750rpx;
    width: 100%;
  }
}
</style>
