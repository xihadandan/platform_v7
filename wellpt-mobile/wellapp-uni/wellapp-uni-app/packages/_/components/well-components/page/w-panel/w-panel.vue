<template>
  <view class="w-panel" :class="customClassCom" v-if="isEmptyUniJsModule">
    <uni-nav-bar
      v-if="customNavBar"
      class="custom-uni-nav-bar"
      :statusBar="true"
      :fixed="true"
      :left-icon="configuration.hideBack ? '' : 'left'"
      :title="navBarTitle"
      @clickLeft="goBack"
    >
      <block v-if="configuration.showNavMenu" slot="right">
        <slot name="menu" :menuItems="menuItems">
          <view v-for="(item, index) in menuItems" :index="index" :key="index">
            <uni-icons :type="item.iconType" size="22" @click="menuItemClick($event, item)" />
          </view>
        </slot>
      </block>
    </uni-nav-bar>
    <view class="w-panel-view" v-for="item in items" :key="item.id">
      <w-panel v-if="item.wtype == 'wMobilePanel' || item.wtype == 'WidgetUniPanel'" :widget="item"></w-panel>
      <w-swiper
        v-if="item.wtype == 'wMobileSwiper' || item.wtype == 'WidgetUniSwiper'"
        :widget="item"
        :parent="widget"
      ></w-swiper>
      <w-grid-view
        v-if="item.wtype == 'wMobileGridView' || item.wtype == 'WidgetUniGridView'"
        :widget="item"
        :parent="widget"
      ></w-grid-view>
      <w-nav v-if="item.wtype == 'wMobileNav' || item.wtype == 'WidgetUniNav'" :widget="item" :parent="widget"></w-nav>
      <w-list-view
        v-if="item.wtype == 'wMobileListView' || item.wtype == 'WidgetUniListView'"
        :widget="item"
        :parent="widget"
      ></w-list-view>
      <w-tabs
        v-if="item.wtype == 'wMobileTabs' || item.wtype == 'WidgetUniTab'"
        :widget="item"
        :parent="widget"
      ></w-tabs>
      <w-html
        v-if="item.wtype == 'wMobileHtml' || item.wtype == 'WidgetUniHtml'"
        :widget="item"
        :parent="widget"
      ></w-html>
      <w-tiles
        v-if="item.wtype == 'wMobileTiles' || item.wtype == 'WidgetUniTiles'"
        :widget="item"
        :parent="widget"
      ></w-tiles>
      <w-data-management-viewer
        v-if="item.wtype == 'wMobileDataManagementViewer' || item.wtype == 'WidgetUniDataManagementViewer'"
        :widget="item"
        :parent="widget"
      ></w-data-management-viewer>
    </view>
  </view>
  <w-widget-development v-else :widget="widget" :parent="parent"></w-widget-development>
</template>
<script>
import mixin from "../page-widget-mixin";
import { mapState } from "vuex";
export default {
  mixins: [mixin],
  computed: mapState(["customNavBar", "navBarTitle"]),
  data() {
    return {
      configuration: this.widget.configuration || {},
      menuItems: (this.widget.configuration && this.widget.configuration.menuItems) || [],
      items: this.widget.items || (this.widget.configuration && this.widget.configuration.widgets) || [],
    };
  },
  created: function () {
    // this.widget.testProp = "112233: " + this.widget.id;
    // console.log("11113333");
    // console.log(this.widget);
  },
  methods: {
    goBack() {
      uni.navigateBack({
        delta: 1,
      });
    },
    menuItemClick: function (e, item) {
      console.log(JSON.stringify(item));
      if (item.pageUrl) {
        uni.navigateTo({
          url: item.pageUrl,
        });
      }
    },
  },
};
</script>

<style scoped>
.w-panel {
}
.w-panel-view {
}
</style>
