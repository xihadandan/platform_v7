<template>
  <view class="tabs-content">
    <template v-for="(tab, i) in visibleItem">
      <view v-show="activeIndex === i">
        <template v-if="tab.configuration.eventHandler.pageId">
          <w-app-page :pageId="tab.configuration.eventHandler.pageId" />
        </template>
        <template v-else>
          <template v-for="(wgt, w) in tab.configuration.widgets">
            <widget :widget="wgt" :parent="widget" :key="'tab_' + wgt.id"></widget>
          </template>
        </template>
      </view>
    </template>
  </view>
</template>

<script>
export default {
  name: "tabs-content",
  props: {
    visibleItem: {
      type: Array,
      default: () => [],
    },
    activeIndex: {
      type: Number,
      default: 0,
    },
  },
  methods: {},
};
</script>
