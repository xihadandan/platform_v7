<template>
  <view class="w-tabs">
    <u-tabs
      :list="tabs"
      @change="onChangeTab"
      keyName="title"
      v-if="widget.configuration.uniConfiguration.tabStyleType == 'default'"
    ></u-tabs>
    <u-subsection
      :list="tabs"
      keyName="title"
      :current="current"
      :mode="widget.configuration.uniConfiguration.subsectionMode"
      @change="onChangeTab"
      v-if="widget.configuration.uniConfiguration.tabStyleType == 'subsection'"
    ></u-subsection>

    <view class="tab-content">
      <template v-for="(tab, i) in tabs">
        <view v-show="current === i">
          <template v-if="tab.configuration.eventHandler.pageId">
            <w-app-page :pageId="tab.configuration.eventHandler.pageId" />
          </template>
          <template v-else>
            <template v-for="(wgt, w) in tab.configuration.widgets">
              <w-widget :widget="wgt" :parent="widget" :key="wgt.id"></w-widget>
            </template>
          </template>
        </view>
      </template>
    </view>
  </view>
</template>

<script>
import mixin from "../page-widget-mixin";
import { isEmpty, each as forEach } from "lodash";
export default {
  mixins: [mixin],
  name: "w-tabs",
  data() {
    let tabStyleType = "button";
    if (this.widget.configuration.uniConfiguration && this.widget.configuration.uniConfiguration.tabStyleType) {
      tabStyleType = this.widget.configuration.uniConfiguration.tabStyleType;
    }
    return {
      current: 0,
      tabs: [],
      tabStyleType,
      controlValues: [],
      badgeNums: [],
      scrollIntoView: "",
    };
  },
  components: {
    "w-widget": () => import("../w-widget/w-widget.vue"),
  },
  created() {
    const _self = this;
    let configuration = this.widget.configuration;
    let tabs = configuration.tabs || [];
    for (let i = 0, len = tabs.length; i < len; i++) {
      let tab = tabs[i];
      _self.getBadgeCount(tab.configuration, function (item, data) {
        _self.$set(tab, "badge", { value: data });
      });
    }
    _self.tabs = tabs;
  },
  mounted: function () {
    // 更新窗口高度
    // uni.getSystemInfo({
    //   success: (result) => {
    //     var windowHeight = result.windowHeight;
    //     const query = uni.createSelectorQuery().in(this);
    //     query
    //       .select(".line-h")
    //       .boundingClientRect((data) => {
    //         if (data) {
    //           this.swiperStyle.height = windowHeight - data.bottom + "px";
    //         } else {
    //           this.swiperStyle.height = windowHeight + "px";
    //         }
    //       })
    //       .exec();
    //   },
    // });
  },
  methods: {
    onChangeTab(e) {
      let index = typeof e == "number" ? e : e.index;
      if (this.current != index) {
        this.current = index;
      }
    },
  },
};
</script>
