<template>
  <view :class="['w-nav-bar', backgroundColor ? 'custom-color' : '']">
    <uni-w-nav-bar
      :leftWidth="72"
      :rightWidth="72"
      :backgroundColor="backgroundColor"
      :color="backgroundColor ? '#fff' : ''"
    >
      <template v-slot:left>
        <view class="_left-container" v-if="widget.configuration.enabledLeftContainer">
          <template v-if="widget.configuration.enabledBack">
            <view class="_left-back" @tap="handleBack">
              <w-icon :iconConfig="widget.configuration.backButtonIcon" :size="20" />
              <view class="back-button-name" v-if="widget.configuration.enabledBackButton">
                {{ widget.configuration.backButtonName }}
              </view>
            </view>
          </template>
          <template v-if="widget.configuration.enabledLogo">
            <view class="_logo-image" v-if="showLogoImage">
              <image class="image" mode="aspectFit" :src="logoImageUrl" />
            </view>
            <w-icon v-else :iconConfig="widget.configuration.logoIcon" :size="28" />
          </template>
        </view>
      </template>
      <template v-slot>
        <view class="_middle-container" v-if="widget.configuration.enabledMiddleContainer">
          <view class="middle-title" v-if="widget.configuration.enabledMiddleTitle">
            <w-icon
              :iconConfig="widget.configuration.addonBeforeIcon"
              v-show="widget.configuration.addonBeforeIcon"
              :size="20"
            />
            <view class="middle-title-text">{{ widget.configuration.middleTitle }}</view>
            <w-icon
              :iconConfig="widget.configuration.addonAfterIcon"
              v-show="widget.configuration.addonAfterIcon"
              :size="20"
            />
          </view>
          <!-- 标签页 -->
          <view class="middle-tabs-nav" v-if="widget.configuration.enabledTabs">
            <tabs-nav :widget="widgetTabs" @change="onChangeTab" @tabs="onTabs" />
          </view>
        </view>
      </template>
      <template v-slot:right>
        <view class="_right-container" v-if="widget.configuration.enabledRightContainer">
          <view class="right-button" v-if="showRightButton">
            <w-button :button="widget.configuration.rightButtonConfig" :parentWidget="getSelf" />
            <template v-if="showRightDrawer">
              <uni-w-button
                v-for="(button, i) in drawerButtons"
                :key="i"
                :type="button.style.type"
                :block="button.style.block"
                :size="button.style.size || size"
                :ghost="button.style.ghost"
                :shape="button.style.shape"
                :icon="button.style.icon"
                :text="button.title"
                :textHidden="button.style.textHidden"
                @click="(e) => onTrigger(e, 'click', button, true)"
              />
            </template>
          </view>
          <!-- 下拉框 -->
          <template v-if="widget.configuration.enabledRightSelect">
            <nav-bar-select :widget="widget.configuration.widgetSelect" :navBarWidget="widget" ref="widgetSelect" />
          </template>
        </view>
      </template>
    </uni-w-nav-bar>
    <!-- 抽屉 -->
    <template v-if="currentButton && currentButton.drawerWidget">
      <uni-popup
        :key="currentButton.drawerWidget.id"
        :type="currentButton.drawerWidget.configuration.direction"
        ref="drawerPopup"
        background-color="#ffffff"
      >
        <!-- TestRenderCustomContent -->
        <w-template :widget="currentButton.drawerWidget" />
      </uni-popup>
    </template>
    <!-- 标签页 -->
    <template v-if="widget.configuration.enabledTabs">
      <tabs-content :visibleItem="visibleTabs" :activeIndex="activeTabIndex" />
    </template>
  </view>
</template>

<script>
import { storage } from "wellapp-uni-framework";
import mixin from "../page-widget-mixin";
import TabsNav from "../w-tabs/tabs-nav.vue";
import TabsContent from "../w-tabs/tabs-content.vue";
import NavBarSelect from "./nav-bar-select.vue";

export default {
  mixins: [mixin],
  data() {
    let activeTabIndex = 0,
      widgetTabs;
    if (this.widget.configuration.enabledTabs) {
      widgetTabs = this.widget.configuration.widgetTabs;
    }

    return {
      drawerButtons: [],
      generalButtons: [],
      currentButton: undefined,
      widgetTabs,
      activeTabIndex,
      visibleTabs: [], // 实际显示的tabs
    };
  },
  components: {
    TabsNav,
    TabsContent,
    NavBarSelect,
  },
  computed: {
    // 显示logo图片
    showLogoImage() {
      let show = false;
      if (this.widget.configuration.logoIcon.indexOf("repository/file/mongo/") !== -1) {
        show = true;
      }
      return show;
    },
    logoImageUrl() {
      let url;
      if (this.showLogoImage) {
        url = storage.fillAccessResourceUrl(this.widget.configuration.logoIcon);
      }
      return url;
    },
    // 显示右侧按钮
    showRightButton() {
      let show = false;
      if (this.widget.configuration.rightButtonConfig && this.widget.configuration.rightButtonConfig.enable) {
        show = true;
      }
      return show;
    },
    // 显示右侧抽屉按钮
    showRightDrawer() {
      let show = false;
      if (this.showRightButton) {
        let drawerButtons = [],
          generalButtons = [];

        this.widget.configuration.rightButtonConfig.buttons.map((item) => {
          if (item.eventHandler && item.eventHandler.actionType === "showDrawer") {
            if (item.defaultVisible) {
              drawerButtons.push(item);
            }
          } else {
            generalButtons.push(item);
          }
        });
        this.generalButtons = generalButtons;
        this.widget.configuration.rightButtonConfig.buttons = generalButtons;
        if (drawerButtons.length) {
          show = true;
          this.drawerButtons = drawerButtons;
        }
      }
      return show;
    },
    backgroundColor() {
      let color = "",
        backgroundColor = this.widget.configuration.backgroundColor;
      if (backgroundColor) {
        color = this.getColorValue(backgroundColor);
      }
      return color;
    },
  },
  methods: {
    onChangeTab(index) {
      this.activeTabIndex = index;
    },
    onTabs(tabs) {
      this.visibleTabs = tabs;
    },
    onTrigger(evt, trigger, button) {
      this.currentButton = button;
      if (button.drawerWidget) {
        this.$nextTick(() => {
          this.$refs.drawerPopup.open();
        });
      }
    },
    handleBack() {
      uni.navigateBack({
        delta: 1,
      });
    },
    getColorValue(color) {
      if (color) {
        return color.startsWith("#") ? color : `var(${color})`;
      }
      return "";
    },
    getSelf() {
      return this;
    },
  },
};
</script>

<style lang="scss">
.w-nav-bar {
  &.custom-color {
    ::v-deep .uni-w-tabs-item {
      color: #fff !important;
      &.uni-w-tabs-item-active {
        color: var(--w-primary-color) !important;
      }
    }
  }
  ._left-container {
    display: flex;
    align-items: center;
    > ._left-back {
      display: flex;
      align-items: center;
      > .w-icon {
        width: 20px;
      }
    }
    .back-button-name {
      line-height: 20px;
      font-size: 14px;
      margin-right: 2px;
    }
    ._logo-image {
      width: 20px;
      height: 20px;
      > .image {
        width: 100%;
        height: 100%;
      }
    }
  }
  ._middle-container {
    width: 100%;
    display: flex;
    justify-content: center;
    > .middle-title {
      display: flex;
      align-items: center;
      justify-content: center;
      height: 100%;
      .middle-title-text {
        padding: 0 4px;
        font-size: 16px;
      }
    }
    .middle-tabs-nav {
      // flex: 1;
      ::v-deep .uni-w-tabs {
        background: transparent !important;
      }
    }
  }
  ._right-container {
    display: flex;
    align-items: center;
    .right-button {
      display: flex;
      align-items: center;
    }
  }

  ::v-deep .uni-w-tabs-item__text {
    display: block;
    white-space: nowrap !important;
  }
  ::v-deep .uni-navbar__header-btns {
    overflow: visible;
  }
}
</style>
