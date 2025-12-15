<template>
  <view class="w-anchor" :class="customClassCom">
    <uni-w-fab-button :width="90" zIndex="98" disabled v-if="false">
      <view class="movableContent anchor-link">
        <view
          class="anchor-item"
          v-for="(anchor, i) in widget.configuration.anchors"
          :key="'anchorlink_' + i"
          v-if="!anchor.hidden"
          @tap="toAnchor(anchor)"
        >
          {{ anchor.label }}
        </view>
      </view>
    </uni-w-fab-button>
    <template v-for="(wgt, w) in widget.configuration.widgets">
     <widget :widget="wgt" :parent="widget" :key="'anchor_' + wgt.id"></widget>
    </template>
  </view>
</template>

<script>
import mixin from "../page-widget-mixin";
import { isEmpty, each as forEach, findIndex } from "lodash";
import { utils } from "wellapp-uni-framework";

export default {
  mixins: [mixin],
  name: "w-tabs",
  data() {
    return {};
  },
  methods: {
    toAnchor(anchor) {
      console.log(anchor);
      uni.$emit("anchorWidgetInContent", {
        id: anchor.href,
      });
    },
  },
};
</script>
<style lang="scss" scoped>
.w-anchor {
  .anchor-link {
    min-width: 120px;
  }
}
</style>
