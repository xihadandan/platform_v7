<template>
  <div class="w-widget-tab">
    <!-- <w-section :title="widget.title" type="line"> -->
    <view class="uni-padding-wrap uni-common-mt">
      <uni-segmented-control
        class="w-widget-tab-control"
        :current="current"
        :values="items"
        :style-type="styleType"
        :active-color="activeColor"
        @clickItem="onItemClick"
      />
    </view>
    <view class="w-widget-tab-content">
      <view v-for="(tab, index) in tabs" :key="index">
        <view v-show="current === index">
          <view v-for="(widget, widgetIndex) in tab.configuration.widgets" :key="widgetIndex">
            <w-dyform-widget-tab v-if="widget.wtype == 'WidgetTab'" :widget="widget" :formScope="formScope">
              <!-- 区块动态插槽 -->
              <!-- #ifdef H5 || APP-PLUS -->
              <slot
                v-for="(blockWidgetCode, slotIndex) in blockWidgetCodes"
                :index="slotIndex"
                :name="'block_' + blockWidgetCode"
                :slot="'block_' + blockWidgetCode"
              ></slot>
              <!-- #endif -->
              <!-- #ifdef MP -->
              <slot
                v-for="(blockWidgetCode, slotIndex) in blockWidgetCodes"
                :index="slotIndex"
                name="{{'block_' + blockWidgetCode}}"
                slot="{{'block_' + blockWidgetCode}}"
              ></slot>
              <!-- #endif -->
            </w-dyform-widget-tab>
            <w-dyform-widget-card
              v-else-if="widget.wtype == 'WidgetCard' || widget.wtype == 'WidgetUniPanel'"
              :widget="widget"
              :formScope="formScope"
            >
              <!-- 区块动态插槽 -->
              <!-- #ifdef H5 || APP-PLUS -->
              <slot
                v-for="(blockWidgetCode, slotIndex) in blockWidgetCodes"
                :index="slotIndex"
                :name="'block_' + blockWidgetCode"
                :slot="'block_' + blockWidgetCode"
              ></slot>
              <!-- #endif -->
              <!-- #ifdef MP -->
              <slot
                v-for="(blockWidgetCode, slotIndex) in blockWidgetCodes"
                :index="slotIndex"
                name="{{'block_' + blockWidgetCode}}"
                slot="{{'block_' + blockWidgetCode}}"
              ></slot>
              <!-- #endif -->
            </w-dyform-widget-card>
            <w-dyform-widget-field v-else :widget="widget" :formScope="formScope" />
          </view>
        </view>
      </view>
    </view>
    <!-- </w-section> -->
  </div>
</template>
<script>
const _ = require("lodash");
export default {
  name: "wDyformWidgetTab",
  props: {
    widget: {
      type: Object,
      required: true,
    },
    formScope: {
      type: Object,
      required: true,
    },
  },
  data() {
    return {
      current: 0,
      tabs: [],
      items: [],
      activeColor: "#007aff",
      styleType: "text",
    };
  },
  created() {
    let _self = this;
    let configuration = _self.widget.configuration || {};
    let defaultActiveKey = configuration.defaultActiveKey;
    let tabs = _self.widget.configuration.tabs || [];
    let tabNames = [];
    let currentTabIndex = 0;
    _.each(tabs, function (tab, index) {
      if (tab.id == defaultActiveKey) {
        currentTabIndex = index;
      }
      tabNames.push(tab.title);
    });
    _self.tabs = tabs;
    _self.items = tabNames;
    _self.current = currentTabIndex;
  },
  methods: {
    onItemClick: function (e) {
      if (this.current !== e.currentIndex) {
        this.current = e.currentIndex;
      }
    },
  },
  computed: {
    blockWidgetCodes() {
      return this.formScope.getBlockWidgetCodes();
    },
  },
};
</script>

<style scoped>
.w-widget-tab-content {
  margin-top: 10px;
  background-color: #fff;
}

.w-widget-tab-control >>> .segmented-control__item {
  justify-content: flex-start;
}
</style>
